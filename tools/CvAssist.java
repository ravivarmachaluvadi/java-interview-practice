/*
 * =====================================================================
 *  CvAssist - IntelliJ-style help for Code Viewer's editor, from the JDK's own javac
 * =====================================================================
 *
 * WHAT IT ANSWERS (one request at a time, from tools/codeview.py)
 *   complete   what can go at the cursor: members after a dot, names in scope, JDK classes
 *              (with the import they need), constructors after `new`, and postfix
 *              templates such as `waiting.pop().var` -> `Integer pop = waiting.pop();`
 *   signature  the parameters of the call the cursor is inside, with their real names
 *   hover      what the name under the cursor is, with its Javadoc
 *   doc        the Javadoc of one dropdown item
 *   check      javac's errors for the file, without running it (the live red underlines)
 *   warm       loads the caches, so the first suggestion you see is quick
 *
 * HOW: TYPE-CHECK THE FILE, THEN ASK JAVAC WHAT IS THERE
 *   1. Put a marker at the cursor: `waiting.pe|` becomes `waiting.pe__CVMARK__()`. Half-typed
 *      code never compiles, but a call is a valid statement and a valid expression almost
 *      everywhere, so javac still builds a sensible tree around it (measured 5 Oct on 19
 *      cursor positions: no `;` needed, other errors in the file do not matter).
 *   2. javac parses and type-checks the text in memory (nothing is written, nothing runs;
 *      -XDshould-stop.at=FLOW stops it before code generation).
 *   3. Find the marker in the tree. Before a dot: ask the type of what is left of the dot
 *      (Trees.getTypeMirror) and list that type's members (Elements.getAllMembers), with
 *      generics filled in (Types.asMemberOf), so Stack<Integer>.pop() says Integer. A plain
 *      word: list what is in scope there (Trees.getScope) plus JDK class names.
 *   Parameter names and Javadoc are not in the JDK's class files; they come from the JDK's
 *   own sources, lib/src.zip, parsed (never compiled) the first time a class is needed.
 *
 * WHY IT IS QUICK
 *   One JVM stays running and one file manager is reused, so the JDK's class index stays
 *   loaded. A request is a fresh javac task over one file: about 15-30 ms once warm.
 *
 * PROTOCOL (stdin -> stdout)
 *   request    op \t offset \t sourcepath \t fileName \t flags \t extra \t byteCount \n
 *              then byteCount bytes of UTF-8 source. flags "c" = a JDK 25 compact source
 *              file, which imports all of java.base by itself. extra = a doc key.
 *   response   one line of JSON. Offsets are UTF-16 positions, the same as the editor's.
 *   It exits when stdin closes, so it never outlives the server.
 *
 * TESTS: python tools/test_codeview.py AssistTest
 */

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.IndexTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.SummaryTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.tree.*;
import com.sun.source.util.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.*;

public class CvAssist {
    static final String MARK = "__CVMARK__";
    static final List<String> OPTIONS = List.of("-proc:none", "-XDshould-stop.at=FLOW", "-nowarn",
                                                "-Xlint:none", "-encoding", "UTF-8");
    /* the packages JDK class names are offered from; others still work once typed in full */
    static final List<String> PACKAGES = List.of("java.lang", "java.util", "java.util.function",
            "java.util.stream", "java.util.concurrent", "java.util.concurrent.atomic",
            "java.util.concurrent.locks", "java.util.regex", "java.math", "java.io", "java.nio.file",
            "java.nio.charset", "java.time", "java.text");
    /* what DSA code reaches for first, so they top the class list */
    static final Set<String> COMMON = Set.of("ArrayList", "HashMap", "HashSet", "ArrayDeque",
            "PriorityQueue", "Arrays", "Collections", "StringBuilder", "Map", "List", "Set", "Deque",
            "Queue", "LinkedList", "TreeMap", "TreeSet", "Stack", "Integer", "String", "Math",
            "Character", "Long", "Comparator", "Iterator", "LinkedHashMap", "Objects", "Optional");
    static final List<String> KEYWORDS = List.of("abstract", "boolean", "break", "byte", "case",
            "catch", "char", "class", "continue", "default", "do", "double", "else", "enum", "extends",
            "false", "final", "finally", "float", "for", "if", "implements", "import", "instanceof",
            "int", "interface", "long", "new", "null", "private", "protected", "public", "record",
            "return", "short", "static", "super", "switch", "this", "throw", "throws", "true", "try",
            "var", "void", "while", "yield");
    static final Set<String> OBJECT_METHODS = Set.of("equals", "hashCode", "toString", "getClass",
            "wait", "notify", "notifyAll", "clone", "finalize");

    final JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager fm = jc.getStandardFileManager(null, Locale.ENGLISH, StandardCharsets.UTF_8);
    final ZipFile srcZip;
    final List<String> index = new ArrayList<>();                 // JDK classes, fully qualified
    final Map<String, Boolean> isPublic = new HashMap<>();
    final Map<String, List<String>> paramNames = new HashMap<>(); // source key -> names, kept forever
    final Set<String> namesRead = new HashSet<>();                // src.zip entries already read
    final Map<String, Sources> sources = new LinkedHashMap<>(32, .75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, Sources> e) { return size() > 16; }
    };
    final Map<String, String> mdCache = new HashMap<>();

    CvAssist() {
        ZipFile z = null;
        try {
            z = new ZipFile(Path.of(System.getProperty("java.home"), "lib", "src.zip").toFile());
        } catch (IOException e) { /* no sources installed: names fall back to arg0, no Javadoc */ }
        srcZip = z;
    }

    /* ------------------------------------------------------------------ protocol */

    record Req(String op, int offset, String sourcepath, String fileName, String flags, String extra, String text) {
        boolean compact() { return flags.contains("c"); }
    }

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out), false, StandardCharsets.UTF_8);
        System.setOut(System.err);                 // anything printed by accident stays off the protocol
        InputStream in = new BufferedInputStream(System.in);
        CvAssist a = new CvAssist();
        while (true) {
            String header = readLine(in);
            if (header == null) return;            // stdin closed: the server stopped
            String[] f = header.split("\t", -1);
            String resp;
            boolean fatal = false;
            try {
                byte[] body = in.readNBytes(Integer.parseInt(f[6].trim()));
                Req r = new Req(f[0], Integer.parseInt(f[1]), f[2], f[3], f[4], f[5], new String(body, StandardCharsets.UTF_8));
                resp = a.handle(r);
            } catch (Throwable e) {
                fatal = e instanceof Error;
                resp = json(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
            }
            out.print(resp);
            out.print('\n');
            out.flush();
            if (fatal) System.exit(3);             // out of memory and the like: the server starts a fresh one
        }
    }

    static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        int c;
        while ((c = in.read()) != -1 && c != '\n') b.write(c);
        return c == -1 && b.size() == 0 ? null : b.toString(StandardCharsets.UTF_8);
    }

    String handle(Req r) throws IOException {
        long t0 = System.nanoTime();
        Map<String, Object> res = switch (r.op) {
            case "complete" -> complete(r);
            case "signature" -> signature(r);
            case "hover" -> hover(r);
            case "doc" -> doc(r.extra);
            case "check" -> check(r);
            case "warm" -> warm();
            default -> new LinkedHashMap<>(Map.of("error", "unknown op " + r.op));
        };
        res.put("ms", (System.nanoTime() - t0) / 1_000_000);
        return json(res);
    }

    /* ------------------------------------------------------------------ javac */

    static final class Source extends SimpleJavaFileObject {
        final String text;
        Source(String name, String text) { super(uri(name), Kind.SOURCE); this.text = text; }
        @Override public CharSequence getCharContent(boolean ignore) { return text; }
        static URI uri(String name) {
            try { return new URI("mem", null, "/" + name, null); }
            catch (URISyntaxException e) { return URI.create("mem:///Unnamed.java"); }
        }
    }

    /** One file parsed and type-checked. */
    final class Unit {
        final JavacTask task;
        final Trees trees;
        final Elements el;
        final Types ty;
        final CompilationUnitTree cu;
        final Source file;
        final List<Diagnostic<? extends JavaFileObject>> diags = new ArrayList<>();

        Unit(String text, Req r) throws IOException {
            fm.setLocation(StandardLocation.SOURCE_PATH,
                           r.sourcepath.isEmpty() ? List.of() : List.of(new File(r.sourcepath)));
            fm.setLocation(StandardLocation.CLASS_PATH, List.of());
            file = new Source(r.fileName.isEmpty() ? "Scratch.java" : r.fileName, text);
            task = (JavacTask) jc.getTask(Writer.nullWriter(), fm, diags::add, OPTIONS, null, List.of(file));
            trees = Trees.instance(task);
            el = task.getElements();
            ty = task.getTypes();
            cu = task.parse().iterator().next();
            task.analyze();
        }

        long start(Tree t) { return trees.getSourcePositions().getStartPosition(cu, t); }
        long end(Tree t) { return trees.getSourcePositions().getEndPosition(cu, t); }

        Scope scope(TreePath p) {
            for (; p != null; p = p.getParentPath()) {
                try { return trees.getScope(p); } catch (RuntimeException e) { /* inside broken code: try the parent */ }
            }
            return null;
        }
    }

    /* ------------------------------------------------------------------ complete */

    /** Where the marker ended up: after a dot, a plain name, after `new`, an annotation,
        an import, or the name of something being declared. */
    record Spot(TreePath path, String kind) {}

    Map<String, Object> complete(Req r) throws IOException {
        String t = r.text;
        int cur = Math.max(0, Math.min(r.offset, t.length()));
        int from = cur;
        while (from > 0 && Character.isJavaIdentifierPart(t.charAt(from - 1))) from--;
        int after = cur;
        while (after < t.length() && Character.isJavaIdentifierPart(t.charAt(after))) after++;
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("from", from);
        res.put("to", cur);
        List<Map<String, Object>> items = new ArrayList<>();
        res.put("items", items);
        if (lex(t, cur).quoted) return res;               // inside a comment or a string
        String prefix = t.substring(from, cur);
        boolean callFollows = nextNonSpace(t, after) == '(';
        String head = t.substring(0, cur) + MARK, tail = t.substring(after);
        List<String> variants = callFollows ? List.of(head + tail) : List.of(head + "()" + tail, head + tail);
        for (String v : variants) {
            Unit u = new Unit(v, r);
            Spot s = findMark(u);
            if (s == null) continue;
            if (s.kind.equals("member") || s.kind.equals("import")) {
                if (!members(u, s, r, t, cur, callFollows, items)) continue;   // left of the dot unknown: try the next variant
            } else if (s.kind.equals("name")) {
                names(u, s, prefix, items);
            } else {
                scopeItems(u, s, r, prefix, callFollows, items, res);
            }
            importPlace(u, r, res);
            return res;
        }
        if (!prefix.isEmpty()) {                          // somewhere javac could not place: words only
            for (String k : KEYWORDS) if (k.startsWith(prefix)) items.add(keyword(k));
        }
        return res;
    }

    Spot findMark(Unit u) {
        Spot[] found = {null};
        new TreePathScanner<Void, Void>() {
            @Override public Void scan(Tree tree, Void v) { return found[0] != null ? null : super.scan(tree, v); }
            @Override public Void visitMemberSelect(MemberSelectTree n, Void v) {
                if (n.getIdentifier().toString().contains(MARK)) {
                    Tree parent = getCurrentPath().getParentPath().getLeaf();
                    found[0] = new Spot(getCurrentPath(), parent.getKind() == Tree.Kind.IMPORT ? "import" : "member");
                    return null;
                }
                return super.visitMemberSelect(n, v);
            }
            @Override public Void visitIdentifier(IdentifierTree n, Void v) {
                if (n.getName().toString().contains(MARK)) {
                    TreePath p = getCurrentPath();
                    Tree parent = p.getParentPath().getLeaf();
                    if (parent.getKind() == Tree.Kind.PARAMETERIZED_TYPE) parent = p.getParentPath().getParentPath().getLeaf();
                    String kind = parent instanceof NewClassTree ? "new"
                            : parent.getKind() == Tree.Kind.ANNOTATION ? "annotation" : "ident";
                    found[0] = new Spot(p, kind);
                }
                return null;
            }
            @Override public Void visitVariable(VariableTree n, Void v) {
                if (n.getName().toString().contains(MARK)) { found[0] = new Spot(getCurrentPath(), "name"); return null; }
                return super.visitVariable(n, v);
            }
            @Override public Void visitMethod(MethodTree n, Void v) {
                if (n.getName().toString().contains(MARK)) { found[0] = new Spot(getCurrentPath(), "name"); return null; }
                return super.visitMethod(n, v);
            }
            @Override public Void visitErroneous(ErroneousTree n, Void v) {
                for (Tree e : n.getErrorTrees()) scan(e, v);
                return null;
            }
        }.scan(u.cu, null);
        return found[0];
    }

    /** After a dot. Returns false when javac could not tell what is left of the dot. */
    boolean members(Unit u, Spot s, Req r, String t, int cur, boolean callFollows, List<Map<String, Object>> items) {
        MemberSelectTree sel = (MemberSelectTree) s.path.getLeaf();
        TreePath rp = new TreePath(s.path, sel.getExpression());
        Element re = u.trees.getElement(rp);
        TypeMirror rt = u.trees.getTypeMirror(rp);
        Scope scope = u.scope(s.path);
        boolean newCtx = s.path.getParentPath().getLeaf() instanceof NewClassTree;
        if (re instanceof PackageElement pe) {
            for (Element e : pe.getEnclosedElements()) {
                if (e instanceof TypeElement te && te.getModifiers().contains(Modifier.PUBLIC)) {
                    items.add(typeItem(u, te, newCtx, callFollows, null, "1", r));
                }
            }
            return true;
        }
        if (rt == null || rt.getKind() == TypeKind.ERROR || rt.getKind() == TypeKind.NONE && !(re instanceof TypeElement)) {
            return false;
        }
        if (re instanceof TypeElement te && isTypeName(sel.getExpression())) {      // Arrays. Integer. Map.Entry.
            DeclaredType dt = (DeclaredType) te.asType();
            for (Element m : u.el.getAllMembers(te)) {
                if (!m.getModifiers().contains(Modifier.STATIC) && !(m instanceof TypeElement)) continue;
                if (m.getKind() == ElementKind.CONSTRUCTOR || !accessible(u, scope, m, dt)) continue;
                items.add(memberItem(u, m, dt, te, callFollows, newCtx));
            }
            Map<String, Object> cls = keyword("class");
            cls.put("sort", "4class");
            items.add(cls);
            return true;
        }
        TypeMirror base = rt.getKind() == TypeKind.TYPEVAR ? ((TypeVariable) rt).getUpperBound() : rt;
        if (base.getKind() == TypeKind.ARRAY) {
            Map<String, Object> len = item("length", "field", "length");
            len.put("type", "int");
            len.put("sort", "1length");
            items.add(len);
            TypeElement obj = u.el.getTypeElement("java.lang.Object");
            for (Element m : u.el.getAllMembers(obj)) {
                if (m.getKind() == ElementKind.METHOD && !m.getModifiers().contains(Modifier.STATIC)
                        && accessible(u, scope, m, (DeclaredType) obj.asType())) {
                    items.add(memberItem(u, m, (DeclaredType) obj.asType(), obj, callFollows, false));
                }
            }
        } else if (base.getKind() == TypeKind.DECLARED) {
            DeclaredType dt = (DeclaredType) base;
            TypeElement te = (TypeElement) dt.asElement();
            for (Element m : u.el.getAllMembers(te)) {
                if (m.getModifiers().contains(Modifier.STATIC) || m.getKind() == ElementKind.CONSTRUCTOR
                        || m instanceof TypeElement || !accessible(u, scope, m, dt)) continue;
                items.add(memberItem(u, m, dt, te, callFollows, false));
            }
        }
        postfix(u, sel, rt, t, cur, items);
        return true;
    }

    static boolean isTypeName(ExpressionTree e) {
        return e instanceof IdentifierTree || e instanceof MemberSelectTree;
    }

    boolean accessible(Unit u, Scope scope, Element m, DeclaredType in) {
        if (scope == null) return m.getModifiers().contains(Modifier.PUBLIC);
        try { return u.trees.isAccessible(scope, m, in); } catch (RuntimeException e) { return false; }
    }

    /** A plain name: what is in scope here, then keywords, then class names. */
    void scopeItems(Unit u, Spot s, Req r, String prefix, boolean callFollows,
                    List<Map<String, Object>> items, Map<String, Object> res) {
        Scope scope = u.scope(s.path);
        boolean typesOnly = s.kind.equals("new") || s.kind.equals("annotation");
        char first = prefix.isEmpty() ? 0 : Character.toLowerCase(prefix.charAt(0));
        Set<String> seen = new HashSet<>();
        if (scope != null && !typesOnly) {
            int rank = 0;
            Set<Element> locals = new LinkedHashSet<>();
            for (Scope sc = scope; sc != null; sc = sc.getEnclosingScope()) {
                for (Element e : sc.getLocalElements()) {
                    switch (e.getKind()) {
                        case LOCAL_VARIABLE, PARAMETER, EXCEPTION_PARAMETER, RESOURCE_VARIABLE, BINDING_VARIABLE -> {
                            if (!e.getSimpleName().toString().contains(MARK)) locals.add(e);
                        }
                        default -> { }
                    }
                }
            }
            for (Element e : locals) {
                if (!seen.add(e.getSimpleName().toString())) continue;     // an inner declaration hides an outer one
                Map<String, Object> it = item(e.getSimpleName().toString(), e.getKind() == ElementKind.PARAMETER ? "param" : "local", e.getSimpleName().toString());
                it.put("type", simple(e.asType()));
                it.put("sort", "0" + String.format("%03d", rank++) + e.getSimpleName());
                items.add(it);
            }
            // instance members only where there is a `this`: not in a static method, and not
            // from an outer class once a static nested class is in between
            boolean instanceOk = !isStatic(s.path);
            for (TypeElement c = scope.getEnclosingClass(); c != null; c = outer(c)) {
                DeclaredType dt = (DeclaredType) c.asType();
                for (Element m : u.el.getAllMembers(c)) {
                    if (m.getKind() == ElementKind.CONSTRUCTOR || m.getSimpleName().toString().contains(MARK)) continue;
                    boolean st = m.getModifiers().contains(Modifier.STATIC) || m instanceof TypeElement;
                    if (!st && !instanceOk) continue;
                    if (!accessible(u, scope, m, dt)) continue;
                    String name = m.getSimpleName().toString();
                    if (first != 0 && Character.toLowerCase(name.charAt(0)) != first) continue;
                    if (m.getKind() != ElementKind.METHOD && !seen.add(name)) continue;
                    Map<String, Object> it = memberItem(u, m, dt, c, callFollows, false);
                    it.put("sort", (OBJECT_METHODS.contains(name) ? "5" : "1") + name);
                    items.add(it);
                }
                if (c.getModifiers().contains(Modifier.STATIC) || c.getNestingKind() == NestingKind.TOP_LEVEL) instanceOk = false;
            }
        }
        if (first == 0) {
            if (!typesOnly) for (String k : KEYWORDS) if (k.length() > 1) items.add(keyword(k));
            return;
        }
        if (!typesOnly) {
            for (String k : KEYWORDS) if (k.charAt(0) == first) items.add(keyword(k));
        }
        // types: this file's own, imported, java.lang, then the JDK index (with an import)
        boolean newCtx = s.kind.equals("new"), annotation = s.kind.equals("annotation");
        Set<String> imported = new HashSet<>(), starPkgs = new HashSet<>(Set.of("java.lang"));
        for (ImportTree it : u.cu.getImports()) {
            if (it.isStatic()) continue;
            String q = it.getQualifiedIdentifier().toString();
            if (q.endsWith(".*")) starPkgs.add(q.substring(0, q.length() - 2)); else imported.add(q);
        }
        String pkg = u.cu.getPackageName() == null ? "" : u.cu.getPackageName().toString();
        starPkgs.add(pkg);
        List<TypeElement> own = new ArrayList<>();
        for (Tree d : u.cu.getTypeDecls()) {
            Element e = u.trees.getElement(new TreePath(new TreePath(u.cu), d));
            if (e instanceof TypeElement te) own.add(te);
        }
        for (TypeElement te : own) {
            if (seen.add(te.getSimpleName().toString()) && matches(te.getSimpleName().toString(), first, annotation, te)) {
                items.add(typeItem(u, te, newCtx, callFollows, null, "2", r));
            }
        }
        try { buildIndex(); } catch (IOException e) { /* no JDK class list: names in scope only */ }
        int added = 0;
        boolean capped = false;
        for (String fqn : index) {
            String sn = fqn.substring(fqn.lastIndexOf('.') + 1);
            if (Character.toLowerCase(sn.charAt(0)) != first || seen.contains(sn)) continue;
            if (!isPublic(u, fqn)) continue;
            TypeElement te = u.el.getTypeElement(fqn);
            if (te == null || !matches(sn, first, annotation, te)) continue;
            if (++added > 300) { capped = true; break; }
            String p = fqn.substring(0, fqn.lastIndexOf('.'));
            boolean needs = !r.compact() && !starPkgs.contains(p) && !imported.contains(fqn);
            String rank = imported.contains(fqn) || p.equals("java.lang") ? "2" : COMMON.contains(sn) ? "3" : "6";
            items.add(typeItem(u, te, newCtx, callFollows, needs ? fqn : null, rank, r));
            seen.add(sn);
        }
        if (capped) res.put("incomplete", true);
    }

    static boolean matches(String name, char first, boolean annotation, TypeElement te) {
        return Character.toLowerCase(name.charAt(0)) == first && (!annotation || te.getKind() == ElementKind.ANNOTATION_TYPE);
    }

    static TypeElement outer(TypeElement c) {
        Element e = c.getEnclosingElement();
        while (e != null && !(e instanceof TypeElement)) e = e.getEnclosingElement();
        return (TypeElement) e;
    }

    /** True inside a static method, static field or static block: no instance members bare there. */
    static boolean isStatic(TreePath p) {
        for (; p != null; p = p.getParentPath()) {
            Tree t = p.getLeaf();
            if (t instanceof ClassTree) return false;
            if (t instanceof MethodTree m) return m.getModifiers().getFlags().contains(Modifier.STATIC);
            if (t instanceof BlockTree b && b.isStatic()) return true;
            if (t instanceof VariableTree v && p.getParentPath().getLeaf() instanceof ClassTree) {
                return v.getModifiers().getFlags().contains(Modifier.STATIC);
            }
        }
        return false;
    }

    boolean isPublic(Unit u, String fqn) {
        Boolean b = isPublic.get(fqn);
        if (b == null) {
            TypeElement te = u.el.getTypeElement(fqn);
            b = te != null && te.getModifiers().contains(Modifier.PUBLIC);
            isPublic.put(fqn, b);
        }
        return b;
    }

    /** Naming something being declared: suggest names from its type, as IntelliJ does. */
    void names(Unit u, Spot s, String prefix, List<Map<String, Object>> items) {
        if (!(s.path.getLeaf() instanceof VariableTree v) || v.getType() == null) return;
        TypeMirror tm = u.trees.getTypeMirror(new TreePath(s.path, v.getType()));
        if (tm == null) return;
        for (String n : nameFor(tm, null)) {
            if (n.startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
                Map<String, Object> it = item(n, "local", n);
                it.put("sort", "0" + n);
                items.add(it);
            }
        }
    }

    /* ------------------------------------------------------------------ items */

    static Map<String, Object> item(String label, String kind, String insert) {
        Map<String, Object> it = new LinkedHashMap<>();
        it.put("label", label);
        it.put("kind", kind);
        it.put("insert", insert);
        return it;
    }

    static Map<String, Object> keyword(String k) {
        Map<String, Object> it = item(k, "keyword", k);
        it.put("sort", "4" + k);
        return it;
    }

    Map<String, Object> memberItem(Unit u, Element m, DeclaredType in, TypeElement recv, boolean callFollows, boolean newCtx) {
        String name = m.getSimpleName().toString();
        if (m instanceof TypeElement te) return typeItem(u, te, newCtx, callFollows, null, "1", null);
        Element owner = m.getEnclosingElement();
        String rank = owner.equals(recv) ? "1" : owner instanceof TypeElement o && o.getQualifiedName().contentEquals("java.lang.Object") ? "5" : "2";
        Map<String, Object> it;
        if (m instanceof ExecutableElement ex) {
            ExecutableType et = memberType(u, in, ex);
            List<String> names = paramNamesOf(u, ex);
            String params = params(et == null ? null : et.getParameterTypes(), ex, names);
            String ret = simple(et == null ? ex.getReturnType() : et.getReturnType());
            boolean none = ex.getParameters().isEmpty();
            it = item(name, "method", callFollows ? name : none ? name + "()" : name + "($0)");
            if (!callFollows && !none) {
                it.put("snippet", true);
                it.put("params", true);
            }
            it.put("detail", "(" + params + ")");
            it.put("type", ret);
            it.put("sig", modifiers(ex) + ret + " " + name + "(" + params + ")  ·  " + qualified(owner));
        } else {
            TypeMirror ft = fieldType(u, in, m);
            boolean constant = m.getModifiers().contains(Modifier.STATIC) && m.getModifiers().contains(Modifier.FINAL);
            it = item(name, m.getKind() == ElementKind.ENUM_CONSTANT ? "enumConst" : constant ? "constant" : "field", name);
            it.put("type", simple(ft));
            it.put("sig", modifiers(m) + simple(ft) + " " + name + "  ·  " + qualified(owner));
        }
        it.put("sort", rank + name);
        if (u.el.isDeprecated(m)) it.put("deprecated", true);
        String key = docKey(u, m);
        if (key != null) it.put("key", key);
        return it;
    }

    Map<String, Object> typeItem(Unit u, TypeElement te, boolean newCtx, boolean callFollows, String importFqn, String rank, Req r) {
        String name = te.getSimpleName().toString();
        String kind = switch (te.getKind()) {
            case INTERFACE -> "interface";
            case ENUM -> "enum";
            case ANNOTATION_TYPE -> "annotation";
            case RECORD -> "record";
            default -> "class";
        };
        String insert = name;
        boolean snippet = false;
        if (newCtx && !callFollows) {
            insert = name + (te.getTypeParameters().isEmpty() ? "" : "<>") + "($0)";
            snippet = true;
        }
        Map<String, Object> it = item(name, kind, insert);
        if (snippet) { it.put("snippet", true); it.put("params", true); }
        Element pkg = u.el.getPackageOf(te);
        it.put("type", pkg == null ? "" : ((PackageElement) pkg).getQualifiedName().toString());
        it.put("sig", kind + " " + te.getQualifiedName() + typeParams(te));
        if (te.getKind() == ElementKind.INTERFACE && newCtx) rank = String.valueOf((char) (rank.charAt(0) + 1));
        it.put("sort", rank + name);
        if (importFqn != null) it.put("importFqn", importFqn);
        if (u.el.isDeprecated(te)) it.put("deprecated", true);
        String key = docKey(u, te);
        if (key != null) it.put("key", key);
        return it;
    }

    /** Turns importFqn into the text to insert, once the place for imports is known. */
    void importPlace(Unit u, Req r, Map<String, Object> res) {
        @SuppressWarnings("unchecked") List<Map<String, Object>> items = (List<Map<String, Object>>) res.get("items");
        if (items.stream().noneMatch(it -> it.containsKey("importFqn"))) return;
        // positions before the cursor are the same in the analysed text and the real one;
        // imports and the package line always come before any code the cursor can be in
        String before, afterText;
        long at;
        List<? extends ImportTree> imports = u.cu.getImports();
        if (!imports.isEmpty()) {
            at = u.end(imports.get(imports.size() - 1));
            before = "\n";
            afterText = "";
        } else if (u.cu.getPackage() != null) {
            at = u.end(u.cu.getPackage());
            before = "\n\n";
            afterText = "";
        } else {
            long first = u.cu.getTypeDecls().isEmpty() ? 0 : u.start(u.cu.getTypeDecls().get(0));
            String text = u.file.text;
            at = Math.max(0, text.lastIndexOf('\n', (int) Math.max(0, first - 1)) + 1);
            before = "";
            afterText = "\n\n";
        }
        res.put("importAt", at);
        for (Map<String, Object> it : items) {
            Object fqn = it.remove("importFqn");
            if (fqn != null) it.put("import", before + "import " + fqn + ";" + afterText);
        }
    }

    ExecutableType memberType(Unit u, DeclaredType in, ExecutableElement ex) {
        try { return (ExecutableType) u.ty.asMemberOf(in, ex); }
        catch (RuntimeException e) { return ex.asType() instanceof ExecutableType et ? et : null; }
    }

    TypeMirror fieldType(Unit u, DeclaredType in, Element f) {
        try { return u.ty.asMemberOf(in, f); } catch (RuntimeException e) { return f.asType(); }
    }

    static String params(List<? extends TypeMirror> types, ExecutableElement ex, List<String> names) {
        List<String> out = new ArrayList<>();
        List<? extends VariableElement> ps = ex.getParameters();
        for (int i = 0; i < ps.size(); i++) {
            TypeMirror tm = types != null && i < types.size() ? types.get(i) : ps.get(i).asType();
            String type = simple(tm);
            if (ex.isVarArgs() && i == ps.size() - 1 && type.endsWith("[]")) type = type.substring(0, type.length() - 2) + "...";
            out.add(type + " " + (names != null && i < names.size() ? names.get(i) : ps.get(i).getSimpleName()));
        }
        return String.join(", ", out);
    }

    static String modifiers(Element e) {
        StringBuilder b = new StringBuilder();
        for (Modifier m : e.getModifiers()) {
            if (m == Modifier.NATIVE) continue;
            b.append(m).append(' ');
        }
        return b.toString();
    }

    static String qualified(Element owner) {
        return owner instanceof TypeElement te ? te.getQualifiedName().toString() : String.valueOf(owner);
    }

    static String typeParams(TypeElement te) {
        if (te.getTypeParameters().isEmpty()) return "";
        StringJoiner j = new StringJoiner(", ", "<", ">");
        for (TypeParameterElement p : te.getTypeParameters()) j.add(p.getSimpleName());
        return j.toString();
    }

    /** A type as you would write it in this file: Map.Entry<String, List<Integer>>, not java.util... */
    static String simple(TypeMirror t) {
        if (t == null) return "";
        switch (t.getKind()) {
            case DECLARED: {
                DeclaredType d = (DeclaredType) t;
                StringBuilder b = new StringBuilder(nestedName((TypeElement) d.asElement()));
                if (!d.getTypeArguments().isEmpty()) {
                    StringJoiner j = new StringJoiner(", ", "<", ">");
                    for (TypeMirror a : d.getTypeArguments()) j.add(simple(a));
                    b.append(j);
                }
                return b.toString();
            }
            case ARRAY: return simple(((ArrayType) t).getComponentType()) + "[]";
            case TYPEVAR: {
                String s = t.toString();
                return s.startsWith("capture#") ? "?" : ((TypeVariable) t).asElement().getSimpleName().toString();
            }
            case WILDCARD: {
                WildcardType w = (WildcardType) t;
                return w.getExtendsBound() != null ? "? extends " + simple(w.getExtendsBound())
                        : w.getSuperBound() != null ? "? super " + simple(w.getSuperBound()) : "?";
            }
            case INTERSECTION: {
                StringJoiner j = new StringJoiner(" & ");
                for (TypeMirror b : ((IntersectionType) t).getBounds()) j.add(simple(b));
                return j.toString();
            }
            default: return t.toString();
        }
    }

    static String nestedName(TypeElement te) {
        String q = te.getQualifiedName().toString();
        Element e = te;
        while (e.getEnclosingElement() instanceof TypeElement o) e = o;
        String top = ((TypeElement) e).getQualifiedName().toString();
        int dot = top.lastIndexOf('.');
        return dot < 0 ? q : q.substring(dot + 1);
    }

    /* ------------------------------------------------------------------ postfix */

    /** expr.var, expr.for, ... : offered when what is left of the dot is a value on this line. */
    void postfix(Unit u, MemberSelectTree sel, TypeMirror rt, String t, int cur, List<Map<String, Object>> items) {
        long start = u.start(sel.getExpression());
        int dot = t.lastIndexOf('.', cur - 1);
        if (start < 0 || start >= dot || t.substring((int) start, cur).contains("\n")) return;
        if (rt.getKind() == TypeKind.VOID || rt.getKind() == TypeKind.NONE || rt.getKind() == TypeKind.PACKAGE) return;
        String recv = t.substring((int) start, dot).strip();
        String r = snip(recv);
        TypeKind k = rt.getKind();
        boolean bool = k == TypeKind.BOOLEAN || isBoxed(rt, "java.lang.Boolean");
        boolean ref = k == TypeKind.DECLARED || k == TypeKind.ARRAY || k == TypeKind.TYPEVAR;
        String type = simple(rt);
        boolean plain = !type.contains("?") && !type.contains("&");
        String name = nameFor(rt, sel.getExpression()).get(0);
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{"var", (plain ? snip(type) : "var") + " ${1:" + name + "} = " + r + ";$0", type + " name = expr;"});
        out.add(new String[]{"sout", "System.out.println(" + r + ");$0", "System.out.println(expr);"});
        out.add(new String[]{"return", "return " + r + ";$0", "return expr;"});
        if (bool) {
            out.add(new String[]{"if", "if (" + r + ") {\n\t$0\n}", "if (expr) { }"});
            out.add(new String[]{"not", (recv.matches("[\\w.()\\[\\]]+") ? "!" + r : "!(" + r + ")") + "$0", "!expr"});
            out.add(new String[]{"while", "while (" + r + ") {\n\t$0\n}", "while (expr) { }"});
        }
        if (ref) {
            out.add(new String[]{"nn", "if (" + r + " != null) {\n\t$0\n}", "if (expr != null) { }"});
            out.add(new String[]{"null", "if (" + r + " == null) {\n\t$0\n}", "if (expr == null) { }"});
        }
        TypeMirror elem = elementType(u, rt);
        if (elem != null) {
            String et = simple(elem);
            String en = nameFor(elem, null).get(0);
            out.add(new String[]{"for", "for (" + snip(et.contains("?") ? "var" : et) + " ${1:" + en + "} : " + r + ") {\n\t$0\n}", "for (T item : expr) { }"});
        }
        String limit = limitOf(u, rt, r);
        if (limit != null) {
            out.add(new String[]{"fori", "for (int ${1:i} = 0; ${1:i} < " + limit + "; ${1:i}++) {\n\t$0\n}", "for (int i = 0; i < n; i++) { }"});
            out.add(new String[]{"forr", "for (int ${1:i} = " + limit + " - 1; ${1:i} >= 0; ${1:i}--) {\n\t$0\n}", "for (int i = n - 1; i >= 0; i--) { }"});
        }
        // The template replaces only the word after the dot, like any member, and a separate
        // edit ("cut") deletes `expr.` before it. A range starting at `expr` made the editor
        // score these above the members (5 Oct: `waiting.` listed for/fori/nn before push).
        for (String[] p : out) {
            Map<String, Object> it = item(p[0], "postfix", p[1]);
            it.put("snippet", true);
            it.put("cut", List.of(start, dot + 1));
            it.put("type", p[2]);
            it.put("sort", "7" + p[0]);
            items.add(it);
        }
    }

    static boolean isBoxed(TypeMirror t, String fqn) {
        return t.getKind() == TypeKind.DECLARED
                && ((TypeElement) ((DeclaredType) t).asElement()).getQualifiedName().contentEquals(fqn);
    }

    /** What a for-each over this gives: the array's component, or Iterable's type argument. */
    TypeMirror elementType(Unit u, TypeMirror t) {
        if (t.getKind() == TypeKind.ARRAY) return ((ArrayType) t).getComponentType();
        if (t.getKind() != TypeKind.DECLARED) return null;
        TypeElement iterable = u.el.getTypeElement("java.lang.Iterable");
        Deque<TypeMirror> todo = new ArrayDeque<>(List.of(t));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            TypeMirror x = todo.poll();
            if (!(x instanceof DeclaredType d) || !seen.add(x.toString())) continue;
            if (d.asElement().equals(iterable)) {
                List<? extends TypeMirror> args = d.getTypeArguments();
                if (args.isEmpty()) return u.el.getTypeElement("java.lang.Object").asType();
                TypeMirror a = args.get(0);
                if (a instanceof WildcardType w) a = w.getExtendsBound() != null ? w.getExtendsBound() : u.el.getTypeElement("java.lang.Object").asType();
                return a;
            }
            todo.addAll(u.ty.directSupertypes(x));
        }
        return null;
    }

    /** How many: n, arr.length, list.size() or s.length(); null when it has no length. */
    String limitOf(Unit u, TypeMirror t, String r) {
        switch (t.getKind()) {
            case INT: case LONG: case SHORT: case BYTE: return r;
            case ARRAY: return r + ".length";
            case DECLARED: {
                if (isBoxed(t, "java.lang.Integer")) return r;
                TypeMirror coll = u.ty.erasure(u.el.getTypeElement("java.util.Collection").asType());
                if (u.ty.isAssignable(u.ty.erasure(t), coll)) return r + ".size()";
                TypeMirror cs = u.el.getTypeElement("java.lang.CharSequence").asType();
                if (u.ty.isAssignable(t, cs)) return r + ".length()";
                return null;
            }
            default: return null;
        }
    }

    /** A variable name for a value: pop() -> pop, getName() -> name, Stack<Integer> -> stack. */
    static List<String> nameFor(TypeMirror t, ExpressionTree from) {
        List<String> out = new ArrayList<>();
        if (from instanceof MethodInvocationTree mi) {
            String m = mi.getMethodSelect() instanceof MemberSelectTree ms ? ms.getIdentifier().toString()
                    : mi.getMethodSelect().toString();
            String n = m.replaceFirst("^(get|is|to)(?=[A-Z])", "");
            if (!n.isEmpty()) out.add(safe(Character.toLowerCase(n.charAt(0)) + n.substring(1)));
        }
        String base;
        switch (t.getKind()) {
            case INT, LONG, SHORT, BYTE -> base = "i";
            case CHAR -> base = "c";
            case BOOLEAN -> base = "b";
            case DOUBLE, FLOAT -> base = "d";
            case ARRAY -> {
                String inner = nameFor(((ArrayType) t).getComponentType(), null).get(0);
                base = inner.length() == 1 ? "arr" : inner + "s";
            }
            case DECLARED -> {
                String s = ((DeclaredType) t).asElement().getSimpleName().toString();
                base = Character.toLowerCase(s.charAt(0)) + s.substring(1);
            }
            default -> base = "value";
        }
        out.add(safe(base));
        return out;
    }

    static String safe(String n) {
        return KEYWORDS.contains(n) || n.equals("const") || n.equals("goto") ? n + "1" : n;
    }

    /** Monaco snippet text: $ } and \ in user code must not be read as placeholders. */
    static String snip(String s) {
        return s.replace("\\", "\\\\").replace("$", "\\$").replace("}", "\\}");
    }

    /* ------------------------------------------------------------------ signature */

    Map<String, Object> signature(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        String t = r.text;
        int cur = Math.max(0, Math.min(r.offset, t.length()));
        Lex lx = lex(t, cur);
        if (lx.quoted || lx.open.isEmpty() || lx.open.peek()[0] != '(') return res;
        int paren = lx.open.peek()[1], active = lx.open.peek()[2];
        int k = paren - 1;
        while (k >= 0 && Character.isWhitespace(t.charAt(k))) k--;
        int calleeEnd = k + 1;
        if (k >= 0 && t.charAt(k) == '>') {                 // new ArrayList<>(  or  Collections.<T>empty(
            int depth = 0;
            for (; k >= 0; k--) {
                char c = t.charAt(k);
                if (c == '>') depth++;
                else if (c == '<' && --depth == 0) { k--; break; }
            }
            while (k >= 0 && Character.isWhitespace(t.charAt(k))) k--;
        }
        int e = k + 1;
        while (k >= 0 && Character.isJavaIdentifierPart(t.charAt(k))) k--;
        String name = t.substring(k + 1, e);
        if (name.isEmpty() || KEYWORDS.contains(name) && !name.equals("this") && !name.equals("super")) return res;
        // the text as typed first; then with the call closed, which parses better
        for (String v : List.of(t, t.substring(0, cur) + ")" + t.substring(cur))) {
            Unit u = new Unit(v, r);
            List<ExecutableElement> cands = new ArrayList<>();
            DeclaredType[] in = {null};
            ExecutableElement chosen = callee(u, calleeEnd, name, cands, in);
            if (cands.isEmpty()) continue;
            List<Map<String, Object>> sigs = new ArrayList<>();
            int best = -1;
            for (int i = 0; i < cands.size(); i++) {
                ExecutableElement ex = cands.get(i);
                ExecutableType et = in[0] == null ? (ExecutableType) ex.asType() : memberType(u, in[0], ex);
                List<String> names = paramNamesOf(u, ex);
                String label0 = ex.getKind() == ElementKind.CONSTRUCTOR ? ex.getEnclosingElement().getSimpleName().toString() : ex.getSimpleName().toString();
                StringBuilder label = new StringBuilder(label0).append('(');
                List<List<Integer>> spans = new ArrayList<>();
                List<? extends VariableElement> ps = ex.getParameters();
                for (int p = 0; p < ps.size(); p++) {
                    if (p > 0) label.append(", ");
                    int s0 = label.length();
                    String type = simple(et == null ? ps.get(p).asType() : et.getParameterTypes().get(p));
                    if (ex.isVarArgs() && p == ps.size() - 1 && type.endsWith("[]")) type = type.substring(0, type.length() - 2) + "...";
                    label.append(type).append(' ').append(names != null && p < names.size() ? names.get(p) : ps.get(p).getSimpleName());
                    spans.add(List.of(s0, label.length()));
                }
                label.append(')');
                Map<String, Object> sig = new LinkedHashMap<>();
                sig.put("label", label.toString());
                sig.put("params", spans);
                Doc d = docOf(u, ex);
                if (d != null) {
                    sig.put("doc", d.summary);
                    sig.put("pdocs", d.paramDocs);
                }
                sigs.add(sig);
                if (ex.equals(chosen)) best = i;
                else if (best < 0 && (ps.size() > active || ex.isVarArgs())) best = i;
            }
            res.put("sigs", sigs);
            res.put("active", Math.max(0, best));
            res.put("param", active);
            return res;
        }
        return res;
    }

    /** The method or constructor whose name ends at calleeEnd; fills every overload into cands. */
    ExecutableElement callee(Unit u, int calleeEnd, String name, List<ExecutableElement> cands, DeclaredType[] in) {
        TreePath[] found = {null};
        new TreePathScanner<Void, Void>() {
            @Override public Void scan(Tree tree, Void v) { return found[0] != null ? null : super.scan(tree, v); }
            @Override public Void visitMethodInvocation(MethodInvocationTree n, Void v) {
                if (u.end(n.getMethodSelect()) == calleeEnd) { found[0] = getCurrentPath(); return null; }
                return super.visitMethodInvocation(n, v);
            }
            @Override public Void visitNewClass(NewClassTree n, Void v) {
                if (u.end(n.getIdentifier()) == calleeEnd) { found[0] = getCurrentPath(); return null; }
                return super.visitNewClass(n, v);
            }
            @Override public Void visitErroneous(ErroneousTree n, Void v) {
                for (Tree e : n.getErrorTrees()) scan(e, v);
                return null;
            }
        }.scan(u.cu, null);
        if (found[0] == null) return null;
        TreePath p = found[0];
        Scope scope = u.scope(p);
        if (p.getLeaf() instanceof NewClassTree nc) {
            Element te = u.trees.getElement(new TreePath(p, nc.getIdentifier()));
            if (te instanceof TypeElement type) {
                for (ExecutableElement c : ElementFilter.constructorsIn(type.getEnclosedElements())) {
                    if (accessible(u, scope, c, (DeclaredType) type.asType())) cands.add(c);
                }
            }
            Element chosen = u.trees.getElement(p);
            return chosen instanceof ExecutableElement ex ? ex : null;
        }
        MethodInvocationTree mi = (MethodInvocationTree) p.getLeaf();
        TreePath ms = new TreePath(p, mi.getMethodSelect());
        Element chosen = u.trees.getElement(ms);
        if (mi.getMethodSelect() instanceof MemberSelectTree sel) {
            TreePath rp = new TreePath(ms, sel.getExpression());
            Element re = u.trees.getElement(rp);
            TypeMirror rt = u.trees.getTypeMirror(rp);
            if (rt != null && rt.getKind() == TypeKind.TYPEVAR) rt = ((TypeVariable) rt).getUpperBound();
            TypeElement te = re instanceof TypeElement t0 && isTypeName(sel.getExpression()) ? t0
                    : rt instanceof DeclaredType d ? (TypeElement) d.asElement() : null;
            if (te == null && rt != null && rt.getKind() == TypeKind.ARRAY) te = u.el.getTypeElement("java.lang.Object");
            if (te == null) return null;
            if (rt instanceof DeclaredType d) in[0] = d;
            for (ExecutableElement m : ElementFilter.methodsIn(u.el.getAllMembers(te))) {
                if (m.getSimpleName().contentEquals(name) && accessible(u, scope, m, (DeclaredType) te.asType())) cands.add(m);
            }
        } else if (scope != null) {
            for (TypeElement c = scope.getEnclosingClass(); c != null; c = outer(c)) {
                for (ExecutableElement m : ElementFilter.methodsIn(u.el.getAllMembers(c))) {
                    if (m.getSimpleName().contentEquals(name)) cands.add(m);
                }
                if (!cands.isEmpty()) break;
            }
        }
        return chosen instanceof ExecutableElement ex ? ex : null;
    }

    /* ------------------------------------------------------------------ hover */

    Map<String, Object> hover(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        String t = r.text;
        int cur = Math.max(0, Math.min(r.offset, t.length()));
        int s = cur, e = cur;
        while (s > 0 && Character.isJavaIdentifierPart(t.charAt(s - 1))) s--;
        while (e < t.length() && Character.isJavaIdentifierPart(t.charAt(e))) e++;
        if (s == e || lex(t, s).quoted) return res;
        String word = t.substring(s, e);
        Unit u = new Unit(t, r);
        int ws = s, we = e;
        TreePath[] hit = {null};
        new TreePathScanner<Void, Void>() {
            @Override public Void scan(Tree tree, Void v) { return hit[0] != null ? null : super.scan(tree, v); }
            @Override public Void visitIdentifier(IdentifierTree n, Void v) {
                if (u.start(n) == ws && n.getName().contentEquals(word)) hit[0] = getCurrentPath();
                return null;
            }
            @Override public Void visitMemberSelect(MemberSelectTree n, Void v) {
                if (u.end(n) == we && n.getIdentifier().contentEquals(word)) { hit[0] = getCurrentPath(); return null; }
                return super.visitMemberSelect(n, v);
            }
            @Override public Void visitVariable(VariableTree n, Void v) {
                if (n.getName().contentEquals(word) && nameAt(n.getType() == null ? u.start(n) : u.end(n.getType()), ws)) { hit[0] = getCurrentPath(); return null; }
                return super.visitVariable(n, v);
            }
            @Override public Void visitMethod(MethodTree n, Void v) {
                if (n.getName().contentEquals(word) && nameAt(n.getReturnType() == null ? u.start(n) : u.end(n.getReturnType()), ws)) { hit[0] = getCurrentPath(); return null; }
                return super.visitMethod(n, v);
            }
            @Override public Void visitClass(ClassTree n, Void v) {
                if (n.getSimpleName().contentEquals(word) && t.substring((int) Math.max(0, u.start(n)), ws).matches("(?s).*\\b(class|interface|enum|record)\\s+$")) { hit[0] = getCurrentPath(); return null; }
                return super.visitClass(n, v);
            }
            boolean nameAt(long after, int pos) {
                return after >= 0 && after <= pos && t.substring((int) after, pos).isBlank();
            }
        }.scan(u.cu, null);
        if (hit[0] == null) return res;
        Element el = u.trees.getElement(hit[0]);
        if (el == null) return res;
        StringBuilder md = new StringBuilder("```java\n").append(declaration(u, el)).append("\n```\n");
        Element owner = el.getEnclosingElement();
        if (owner instanceof TypeElement te && !(el instanceof TypeElement)) md.append("in `").append(te.getQualifiedName()).append("`\n\n");
        Doc d = docOf(u, el);
        if (d != null && !d.md.isEmpty()) md.append("\n").append(d.md);
        res.put("md", md.toString());
        return res;
    }

    String declaration(Unit u, Element e) {
        switch (e.getKind()) {
            case METHOD: case CONSTRUCTOR: {
                ExecutableElement ex = (ExecutableElement) e;
                String params = params(null, ex, paramNamesOf(u, ex));
                String name = e.getKind() == ElementKind.CONSTRUCTOR ? e.getEnclosingElement().getSimpleName().toString() : e.getSimpleName().toString();
                String ret = e.getKind() == ElementKind.CONSTRUCTOR ? "" : simple(ex.getReturnType()) + " ";
                return modifiers(e) + ret + name + "(" + params + ")";
            }
            case CLASS: case INTERFACE: case ENUM: case RECORD: case ANNOTATION_TYPE: {
                TypeElement te = (TypeElement) e;
                String kind = switch (e.getKind()) { case INTERFACE -> "interface"; case ENUM -> "enum"; case RECORD -> "record"; case ANNOTATION_TYPE -> "@interface"; default -> "class"; };
                return modifiers(e) + kind + " " + te.getQualifiedName() + typeParams(te);
            }
            case LOCAL_VARIABLE: case PARAMETER: case EXCEPTION_PARAMETER: case RESOURCE_VARIABLE: case BINDING_VARIABLE:
                return simple(e.asType()) + " " + e.getSimpleName();
            default:
                return modifiers(e) + simple(e.asType()) + " " + e.getSimpleName();
        }
    }

    /* ------------------------------------------------------------------ check */

    Map<String, Object> check(Req r) throws IOException {
        Unit u = new Unit(r.text, r);
        List<Map<String, Object>> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Diagnostic<? extends JavaFileObject> d : u.diags) {
            // only this file's: a broken helper class found through -sourcepath is not underlined here
            if (d.getKind() != Diagnostic.Kind.ERROR || d.getSource() == null
                    || !d.getSource().toUri().equals(u.file.toUri())) continue;
            long pos = d.getPosition(), start = d.getStartPosition(), end = d.getEndPosition();
            if (start < 0) start = pos;
            if (end < start) end = start;
            String msg = d.getMessage(Locale.ENGLISH);
            if (!seen.add(start + msg)) continue;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("start", start);
            m.put("end", end);
            m.put("msg", msg);
            out.add(m);
        }
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("diags", out);
        return res;
    }

    /* ------------------------------------------------------------------ docs */

    /** A member's documentation: parameter names, the whole Javadoc, its first sentence. */
    record Doc(List<String> params, String md, String summary, List<String> paramDocs) {}

    /** One JDK source file, parsed: where each member is, for its Javadoc. */
    record Sources(DocTrees docTrees, Map<String, TreePath> paths) {}

    /** The key a member has in the JDK sources: m|Map.Entry|getKey| (params as written, no generics). */
    static String sourceKey(Element e) {
        TypeElement owner = e instanceof TypeElement te ? te : (TypeElement) e.getEnclosingElement();
        String nested = nestedName(owner);
        return switch (e.getKind()) {
            case CLASS, INTERFACE, ENUM, RECORD, ANNOTATION_TYPE -> "t|" + nested;
            case FIELD, ENUM_CONSTANT -> "f|" + nested + "|" + e.getSimpleName();
            case METHOD, CONSTRUCTOR -> {
                StringJoiner j = new StringJoiner(",");
                for (VariableElement p : ((ExecutableElement) e).getParameters()) j.add(norm(p.asType().toString()));
                yield (e.getKind() == ElementKind.CONSTRUCTOR ? "c|" : "m|") + nested + "|"
                        + (e.getKind() == ElementKind.CONSTRUCTOR ? "<init>" : e.getSimpleName()) + "|" + j;
            }
            default -> null;
        };
    }

    /** Collection<? extends E> -> Collection, java.lang.Object... -> Object[]: comparable both ways. */
    static String norm(String s) {
        s = s.replaceAll("@[\\w.]+(\\([^)]*\\))?\\s*", "");
        StringBuilder b = new StringBuilder();
        int depth = 0;
        for (char c : s.toCharArray()) {
            if (c == '<') depth++;
            else if (c == '>') depth--;
            else if (depth == 0 && !Character.isWhitespace(c)) b.append(c);
        }
        String r = b.toString().replace("...", "[]");
        int dims = r.indexOf('[');
        String base = dims < 0 ? r : r.substring(0, dims), arr = dims < 0 ? "" : r.substring(dims);
        return base.substring(base.lastIndexOf('.') + 1) + arr;
    }

    /** The src.zip entry holding this element, or null for code that is not the JDK's. */
    String entryOf(Unit u, Element e) {
        if (srcZip == null) return null;
        Element top = e;
        while (top != null && !(top instanceof TypeElement && !(top.getEnclosingElement() instanceof TypeElement))) top = top.getEnclosingElement();
        if (!(top instanceof TypeElement te)) return null;
        ModuleElement mod = u.el.getModuleOf(te);
        if (mod == null || mod.isUnnamed()) return null;
        return mod.getQualifiedName() + "/" + te.getQualifiedName().toString().replace('.', '/') + ".java";
    }

    String docKey(Unit u, Element e) {
        String entry = entryOf(u, e);
        if (entry == null) return null;
        TypeElement owner = e instanceof TypeElement te ? te : (TypeElement) e.getEnclosingElement();
        String sk = sourceKey(e);
        return sk == null ? null : owner.getQualifiedName() + "|" + sk;
    }

    List<String> paramNamesOf(Unit u, ExecutableElement ex) {
        String entry = entryOf(u, ex);
        if (entry == null) {                    // your own code: javac knows the real names
            List<String> n = new ArrayList<>();
            for (VariableElement p : ex.getParameters()) n.add(p.getSimpleName().toString());
            return n;
        }
        if (!namesRead.contains(entry)) sourcesOf(entry);
        return paramNames.get(entry + "#" + sourceKey(ex));
    }

    Sources sourcesOf(String entry) {
        Sources s = sources.get(entry);
        if (s != null) return s;
        Map<String, TreePath> paths = new HashMap<>();
        DocTrees dt = null;
        try {
            ZipEntry ze = srcZip.getEntry(entry);
            if (ze != null) {
                String text;
                try (InputStream in = srcZip.getInputStream(ze)) { text = new String(in.readAllBytes(), StandardCharsets.UTF_8); }
                JavacTask pt = (JavacTask) jc.getTask(Writer.nullWriter(), fm, d -> { }, List.of("-proc:none"), null,
                                                      List.of(new Source(entry.substring(entry.lastIndexOf('/') + 1), text)));
                dt = DocTrees.instance(pt);
                CompilationUnitTree cu = pt.parse().iterator().next();
                new TreePathScanner<Void, String>() {
                    @Override public Void visitClass(ClassTree n, String outer) {
                        String nested = outer == null ? n.getSimpleName().toString() : outer + "." + n.getSimpleName();
                        paths.put("t|" + nested, getCurrentPath());
                        for (Tree m : n.getMembers()) scan(m, nested);
                        return null;
                    }
                    @Override public Void visitMethod(MethodTree n, String nested) {
                        boolean ctor = n.getName().contentEquals("<init>");
                        StringJoiner j = new StringJoiner(",");
                        List<String> names = new ArrayList<>();
                        for (VariableTree p : n.getParameters()) {
                            j.add(norm(p.getType().toString()));
                            names.add(p.getName().toString());
                        }
                        String key = (ctor ? "c|" : "m|") + nested + "|" + n.getName() + "|" + j;
                        paths.put(key, getCurrentPath());
                        paramNames.put(entry + "#" + key, names);
                        return null;
                    }
                    @Override public Void visitVariable(VariableTree n, String nested) {
                        paths.put("f|" + nested + "|" + n.getName(), getCurrentPath());
                        return null;
                    }
                }.scan(cu, null);
            }
        } catch (IOException | RuntimeException e) { /* unreadable: no names or docs for it */ }
        namesRead.add(entry);
        s = new Sources(dt, paths);
        sources.put(entry, s);
        return s;
    }

    Doc docOf(Unit u, Element e) {
        String entry = entryOf(u, e);
        if (entry == null) {                                   // your own code: its /** */ comment, if any
            try {
                DocCommentTree d = DocTrees.instance(u.task).getDocCommentTree(e);
                return d == null ? null : render(d, null);
            } catch (RuntimeException x) { return null; }
        }
        String key = sourceKey(e);
        Sources s = sourcesOf(entry);
        TreePath p = s.paths.get(key);
        Doc d = null;
        if (p != null && s.docTrees != null) {
            try { d = render(s.docTrees.getDocCommentTree(p), paramNames.get(entry + "#" + key)); } catch (RuntimeException x) { d = null; }
        }
        if ((d == null || d.md.isEmpty() || d.md.contains(INHERIT)) && e instanceof ExecutableElement ex) {
            Doc sup = inherited(u, ex);
            if (sup != null) return d == null || d.md.isEmpty() ? sup
                    : new Doc(d.params, d.md.replace(INHERIT, sup.md), d.summary.replace(INHERIT, sup.summary), d.paramDocs);
        }
        if (d != null && d.md.contains(INHERIT)) d = new Doc(d.params, d.md.replace(INHERIT, ""), d.summary.replace(INHERIT, ""), d.paramDocs);
        return d;
    }

    /** The doc of the method this one overrides, nearest first. */
    Doc inherited(Unit u, ExecutableElement ex) {
        TypeElement owner = (TypeElement) ex.getEnclosingElement();
        Deque<TypeMirror> todo = new ArrayDeque<>(u.ty.directSupertypes(owner.asType()));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            TypeMirror t = todo.poll();
            if (!(t instanceof DeclaredType d) || !seen.add(d.asElement().toString())) continue;
            TypeElement te = (TypeElement) d.asElement();
            for (ExecutableElement m : ElementFilter.methodsIn(te.getEnclosedElements())) {
                if (m.getSimpleName().equals(ex.getSimpleName()) && u.el.overrides(ex, m, owner)) {
                    Doc doc = docOf(u, m);
                    if (doc != null && !doc.md.isEmpty()) return doc;
                }
            }
            todo.addAll(u.ty.directSupertypes(t));
        }
        return null;
    }

    Map<String, Object> doc(String key) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        String cached = mdCache.get(key);
        if (cached != null) { res.put("md", cached); return res; }
        String[] k = key.split("\\|", -1);           // owner | kind | nested | name | params
        if (k.length < 3) return res;
        Unit u = new Unit("class CvDoc {}", new Req("doc", 0, "", "CvDoc.java", "", "", ""));
        TypeElement owner = u.el.getTypeElement(k[0]);
        if (owner == null) return res;
        String want = key.substring(k[0].length() + 1);
        Element target = null;
        if (want.startsWith("t|")) target = owner;
        else for (Element m : owner.getEnclosedElements()) {
            if (want.equals(sourceKey(m))) { target = m; break; }
        }
        if (target == null) return res;
        Doc d = docOf(u, target);
        String md = d == null ? "" : d.md;
        mdCache.put(key, md);
        res.put("md", md);
        return res;
    }

    static final String INHERIT = "\u0000inherit\u0000";

    /** Javadoc -> Markdown for the editor: {@code}, {@link}, <p>, <pre>, lists, @param/@return/@throws. */
    static Doc render(DocCommentTree d, List<String> names) {
        if (d == null) return new Doc(names, "", "", List.of());
        Renderer r = new Renderer();
        String body = r.text(d.getFullBody());
        String summary = r.text(d.getFirstSentence());
        List<String> ps = new ArrayList<>(), th = new ArrayList<>();
        Map<String, String> pdoc = new HashMap<>();
        String ret = null;
        for (DocTree b : d.getBlockTags()) {
            switch (b.getKind()) {
                case PARAM -> {
                    ParamTree p = (ParamTree) b;
                    if (p.isTypeParameter()) break;
                    String text = r.text(p.getDescription());
                    ps.add("`" + p.getName() + "` – " + text);
                    pdoc.put(p.getName().toString(), text);
                }
                case RETURN -> ret = r.text(((com.sun.source.doctree.ReturnTree) b).getDescription());
                case THROWS, EXCEPTION -> {
                    ThrowsTree x = (ThrowsTree) b;
                    th.add("`" + simpleRef(x.getExceptionName().getSignature()) + "` – " + r.text(x.getDescription()));
                }
                default -> { }
            }
        }
        StringBuilder md = new StringBuilder(body);
        if (!ps.isEmpty()) md.append("\n\n**Params:**  \n").append(String.join("  \n", ps));
        if (ret != null) md.append("\n\n**Returns:** ").append(ret);
        if (!th.isEmpty()) md.append("\n\n**Throws:**  \n").append(String.join("  \n", th));
        String out = md.toString().strip();
        if (out.length() > 6000) out = out.substring(0, 6000) + " …";
        List<String> pd = new ArrayList<>();
        if (names != null) for (String n : names) pd.add(pdoc.getOrDefault(n, ""));
        return new Doc(names, out, summary, pd);
    }

    static String simpleRef(String sig) {
        String s = sig.replace('#', '.');
        if (s.startsWith(".")) s = s.substring(1);
        int paren = s.indexOf('(');
        String head = paren < 0 ? s : s.substring(0, paren), tail = paren < 0 ? "" : s.substring(paren);
        String[] parts = head.split("\\.");
        int i = 0;
        while (i < parts.length - 1 && !parts[i].isEmpty() && Character.isLowerCase(parts[i].charAt(0))) i++;
        return String.join(".", Arrays.copyOfRange(parts, i, parts.length)) + tail.replaceAll("[\\w.]+\\.(\\w+)", "$1");
    }

    static final class Renderer {
        boolean pre;

        String text(List<? extends DocTree> l) {
            StringBuilder b = new StringBuilder();
            for (DocTree t : l) one(t, b);
            return b.toString().replaceAll("[ \\t]+\\n", "\n").replaceAll("\\n{3,}", "\n\n").strip();
        }

        void one(DocTree t, StringBuilder b) {
            switch (t.getKind()) {
                case TEXT -> {
                    String s = ((TextTree) t).getBody();
                    b.append(pre ? s : s.replaceAll("\\s*\\n\\s*", " "));
                }
                case CODE -> {
                    String s = ((com.sun.source.doctree.LiteralTree) t).getBody().getBody();
                    b.append(pre ? s : "`" + s.strip().replace('`', '\'') + "`");
                }
                case LITERAL -> b.append(((com.sun.source.doctree.LiteralTree) t).getBody().getBody());
                case LINK, LINK_PLAIN -> {
                    LinkTree l = (LinkTree) t;
                    String s = l.getLabel().isEmpty() ? simpleRef(l.getReference().getSignature()) : text(l.getLabel());
                    b.append(t.getKind() == DocTree.Kind.LINK && !pre ? "`" + s + "`" : s);
                }
                case START_ELEMENT -> start(((StartElementTree) t).getName().toString().toLowerCase(Locale.ROOT), b);
                case END_ELEMENT -> end(((EndElementTree) t).getName().toString().toLowerCase(Locale.ROOT), b);
                case ENTITY -> b.append(entity(((EntityTree) t).getName().toString()));
                case RETURN -> { b.append("Returns "); for (DocTree x : ((com.sun.source.doctree.ReturnTree) t).getDescription()) one(x, b); }
                case SUMMARY -> { for (DocTree x : ((SummaryTree) t).getSummary()) one(x, b); }
                case INHERIT_DOC -> b.append(INHERIT);
                case VALUE -> {
                    ValueTree v = (ValueTree) t;
                    if (v.getReference() != null) b.append('`').append(simpleRef(v.getReference().getSignature())).append('`');
                }
                case INDEX -> one(((IndexTree) t).getSearchTerm(), b);
                default -> b.append(t.toString());
            }
        }

        void start(String tag, StringBuilder b) {
            switch (tag) {
                case "p" -> b.append("\n\n");
                case "pre" -> { pre = true; b.append("\n\n```java\n"); }
                case "ul", "ol", "dl", "table", "tr" -> b.append('\n');
                case "li", "dt" -> b.append("\n- ");
                case "dd" -> b.append(": ");
                case "td", "th" -> b.append(" | ");
                case "b", "strong" -> b.append("**");
                case "i", "em", "cite", "var" -> b.append('*');
                case "code", "tt", "kbd", "samp" -> { if (!pre) b.append('`'); }
                case "br" -> b.append("  \n");
                case "h1", "h2", "h3", "h4", "h5", "h6" -> b.append("\n\n**");
                case "blockquote" -> b.append("\n\n> ");
                default -> { }
            }
        }

        void end(String tag, StringBuilder b) {
            switch (tag) {
                case "pre" -> { pre = false; b.append("\n```\n\n"); }
                case "b", "strong" -> b.append("**");
                case "i", "em", "cite", "var" -> b.append('*');
                case "code", "tt", "kbd", "samp" -> { if (!pre) b.append('`'); }
                case "h1", "h2", "h3", "h4", "h5", "h6" -> b.append("**\n\n");
                case "ul", "ol", "dl", "table" -> b.append("\n\n");
                default -> { }
            }
        }

        String entity(String name) {
            switch (name) {
                case "lt": return pre ? "<" : "\\<";
                case "gt": return ">";
                case "amp": return "&";
                case "quot": return "\"";
                case "apos": return "'";
                case "nbsp": return " ";
                case "hellip": return "…";
                case "mdash": return "—";
                case "ndash": return "–";
                default:
                    if (name.startsWith("#")) {
                        try {
                            int cp = name.startsWith("#x") || name.startsWith("#X") ? Integer.parseInt(name.substring(2), 16) : Integer.parseInt(name.substring(1));
                            return new String(Character.toChars(cp));
                        } catch (RuntimeException e) { return "&" + name + ";"; }
                    }
                    return "&" + name + ";";
            }
        }
    }

    /* ------------------------------------------------------------------ warm-up */

    void buildIndex() throws IOException {
        if (!index.isEmpty()) return;
        JavaFileManager.Location loc = fm.getLocationForModule(StandardLocation.SYSTEM_MODULES, "java.base");
        for (String pkg : PACKAGES) {
            for (JavaFileObject f : fm.list(loc, pkg, Set.of(JavaFileObject.Kind.CLASS), false)) {
                String bn = fm.inferBinaryName(loc, f);
                if (bn != null && !bn.contains("$") && !bn.endsWith("package-info") && !bn.endsWith("module-info")) index.add(bn);
            }
        }
        index.sort(Comparator.comparing((String s) -> s.substring(s.lastIndexOf('.') + 1)));
    }

    /** Loads the class index, the public flags and the sources of common classes, so the
        first real request is as quick as the rest. */
    Map<String, Object> warm() throws IOException {
        buildIndex();
        String sample = """
            import java.util.*;
            class W {
                void f(int[] a, String s) {
                    Stack<Integer> st = new Stack<>(); Map<String, List<Integer>> m = new HashMap<>();
                    Deque<Integer> d = new ArrayDeque<>(); PriorityQueue<int[]> pq = new PriorityQueue<>();
                    StringBuilder sb = new StringBuilder(); List<Integer> l = new ArrayList<>(); Set<Integer> hs = new HashSet<>();
                    @@
                }
            }
            """;
        for (String recv : List.of("st", "m", "d", "pq", "sb", "l", "hs", "s", "Arrays", "Math", "Integer", "Character", "Collections")) {
            String v = sample.replace("@@", recv + ".");
            complete(new Req("complete", v.indexOf(recv + ".") + recv.length() + 1, "", "W.java", "", "", v));
        }
        String v = sample.replace("@@", "A");
        Unit u = new Unit(v, new Req("complete", 0, "", "W.java", "", "", v));
        for (String fqn : index) isPublic(u, fqn);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("classes", index.size());
        res.put("sources", srcZip != null);
        return res;
    }

    /* ------------------------------------------------------------------ lexing */

    /** Is the position inside a comment or string, and which brackets are open before it
        (each: bracket, offset, commas seen at its level). */
    record Lex(boolean quoted, Deque<int[]> open) {}

    static Lex lex(String t, int end) {
        Deque<int[]> open = new ArrayDeque<>();
        int i = 0, n = t.length();
        while (i < end) {
            char c = t.charAt(i);
            char next = i + 1 < n ? t.charAt(i + 1) : 0;
            if (c == '/' && next == '/') {
                int nl = t.indexOf('\n', i);
                if (nl < 0 || nl >= end) return new Lex(true, open);
                i = nl + 1;
                continue;
            }
            if (c == '/' && next == '*') {
                int close = t.indexOf("*/", i + 2);
                if (close < 0 || close + 2 > end) return new Lex(true, open);
                i = close + 2;
                continue;
            }
            if (t.startsWith("\"\"\"", i)) {
                int j = i + 3;
                while (j < n && !(t.startsWith("\"\"\"", j) && t.charAt(j - 1) != '\\')) j++;
                if (j >= end) return new Lex(true, open);
                i = j + 3;
                continue;
            }
            if (c == '"' || c == '\'') {
                int j = i + 1;
                while (j < n && t.charAt(j) != c && t.charAt(j) != '\n') j += t.charAt(j) == '\\' ? 2 : 1;
                if (j >= end) return new Lex(true, open);
                i = j + 1;
                continue;
            }
            if (c == '(' || c == '[' || c == '{') open.push(new int[]{c, i, 0});
            else if ((c == ')' || c == ']' || c == '}') && !open.isEmpty()) open.pop();
            else if (c == ',' && !open.isEmpty()) open.peek()[2]++;
            i++;
        }
        return new Lex(false, open);
    }

    static int nextNonSpace(String t, int i) {
        while (i < t.length() && Character.isWhitespace(t.charAt(i))) i++;
        return i < t.length() ? t.charAt(i) : -1;
    }

    /* ------------------------------------------------------------------ JSON */

    static String json(Object o) {
        StringBuilder b = new StringBuilder();
        write(b, o);
        return b.toString();
    }

    static void write(StringBuilder b, Object o) {
        if (o == null) b.append("null");
        else if (o instanceof String s) str(b, s);
        else if (o instanceof Number || o instanceof Boolean) b.append(o);
        else if (o instanceof Map<?, ?> m) {
            b.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (!first) b.append(',');
                first = false;
                str(b, String.valueOf(e.getKey()));
                b.append(':');
                write(b, e.getValue());
            }
            b.append('}');
        } else if (o instanceof Collection<?> c) {
            b.append('[');
            boolean first = true;
            for (Object x : c) {
                if (!first) b.append(',');
                first = false;
                write(b, x);
            }
            b.append(']');
        } else str(b, o.toString());
    }

    static void str(StringBuilder b, String s) {
        b.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> {
                    if (c < 0x20 || c == ' ' || c == ' ') b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
                }
            }
        }
        b.append('"');
    }
}
