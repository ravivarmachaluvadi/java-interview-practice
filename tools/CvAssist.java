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
    final Map<String, List<String>> implCache = new HashMap<>();      // List -> ArrayList, LinkedList, ...

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
            case "compile" -> compile(r);
            case "imports" -> imports(r);
            case "definition" -> definition(r);
            case "rename" -> rename(r);
            case "extract" -> extract(r);
            case "format" -> format(r);
            case "create" -> create(r);
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
        // the start of a statement: `re__CVMARK__();` alone on its line
        TreePath up = s.path.getParentPath();
        if (up != null && up.getLeaf() instanceof MethodInvocationTree mi && mi.getMethodSelect() == s.path.getLeaf()) up = up.getParentPath();
        boolean statementStart = up != null && up.getLeaf() instanceof ExpressionStatementTree;
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
        boolean newCtx = s.kind.equals("new"), annotation = s.kind.equals("annotation");
        Set<String> imported = new HashSet<>(), starPkgs = new HashSet<>(Set.of("java.lang"));
        for (ImportTree it : u.cu.getImports()) {
            if (it.isStatic()) continue;
            String q = it.getQualifiedIdentifier().toString();
            if (q.endsWith(".*")) starPkgs.add(q.substring(0, q.length() - 2)); else imported.add(q);
        }
        String pkg = u.cu.getPackageName() == null ? "" : u.cu.getPackageName().toString();
        starPkgs.add(pkg);
        if (newCtx) {
            // IntelliJ's ★ (5 Oct, Ravi asked): what the left side needs comes first
            TypeMirror exp = expectedType(u, s.path);
            if (exp != null) expectedItems(u, exp, callFollows, r, imported, starPkgs, seen, items);
            if (first != 0) {                         // `new int` can only be an array
                for (TypeKind k : List.of(TypeKind.INT, TypeKind.LONG, TypeKind.CHAR, TypeKind.BOOLEAN,
                                          TypeKind.DOUBLE, TypeKind.BYTE, TypeKind.SHORT, TypeKind.FLOAT)) {
                    TypeMirror arr = u.ty.getArrayType(u.ty.getPrimitiveType(k));
                    if (k.name().toLowerCase(Locale.ROOT).charAt(0) == first && seen.add(simple(arr))) items.add(arrayItem(arr, "1", false));
                }
            }
        }
        if (first == 0) {
            if (!typesOnly) for (String k : KEYWORDS) if (k.length() > 1) items.add(keyword(k, statementStart));
            return;
        }
        if (!typesOnly) {
            for (String k : KEYWORDS) if (k.charAt(0) == first) items.add(keyword(k, statementStart));
        }
        // types: this file's own, imported, java.lang, then the JDK index (with an import)
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

    /** What a `new` must produce: the declared type left of `=`, an assigned variable's type,
        or the method's return type after `return`. Null when nothing says. */
    TypeMirror expectedType(Unit u, TreePath ident) {
        TreePath nc = ident.getParentPath();
        if (nc.getLeaf() instanceof ParameterizedTypeTree) nc = nc.getParentPath();
        if (!(nc.getLeaf() instanceof NewClassTree)) return null;
        TreePath parent = nc.getParentPath();
        Tree pt = parent.getLeaf();
        TypeMirror t = null;
        if (pt instanceof VariableTree v && v.getInitializer() == nc.getLeaf() && v.getType() != null) {
            t = u.trees.getTypeMirror(new TreePath(parent, v.getType()));
        } else if (pt instanceof AssignmentTree a && a.getExpression() == nc.getLeaf()) {
            t = u.trees.getTypeMirror(new TreePath(parent, a.getVariable()));
        } else if (pt instanceof ReturnTree) {
            for (TreePath p = parent; p != null; p = p.getParentPath()) {
                if (p.getLeaf() instanceof LambdaExpressionTree) return null;
                if (p.getLeaf() instanceof MethodTree m) {
                    if (m.getReturnType() != null) t = u.trees.getTypeMirror(new TreePath(p, m.getReturnType()));
                    break;
                }
            }
        }
        return t == null || t.getKind() == TypeKind.ERROR || t.getKind() == TypeKind.VOID ? null : t;
    }

    /** The ★ items: the expected array type, the expected class, or for an interface or an
        abstract class (List, Deque, Map...) its usual implementations, common ones first. */
    void expectedItems(Unit u, TypeMirror exp, boolean callFollows, Req r, Set<String> imported, Set<String> starPkgs,
                       Set<String> seen, List<Map<String, Object>> items) {
        if (exp.getKind() == TypeKind.ARRAY) {
            Map<String, Object> it = arrayItem(exp, "00", true);
            seen.add((String) it.get("label"));
            items.add(it);
            return;
        }
        if (exp.getKind() != TypeKind.DECLARED) return;
        TypeElement want = (TypeElement) ((DeclaredType) exp).asElement();
        List<TypeElement> found = new ArrayList<>();
        if (concrete(want)) found.add(want);
        else for (String fqn : implementations(u, want)) {
            TypeElement te = u.el.getTypeElement(fqn);
            if (te != null) found.add(te);
        }
        for (TypeElement te : found) {
            String name = te.getSimpleName().toString(), fqn = te.getQualifiedName().toString();
            String p = u.el.getPackageOf(te).getQualifiedName().toString();
            boolean needs = !r.compact() && te.getNestingKind() == NestingKind.TOP_LEVEL && entryOf(u, te) != null
                    && !starPkgs.contains(p) && !imported.contains(fqn);
            Map<String, Object> it = typeItem(u, te, true, callFollows, needs ? fqn : null, "0", r);
            it.put("sort", "0" + (COMMON.contains(name) ? "0" : "1") + name);
            it.put("type", "★ " + it.get("type"));
            items.add(it);
            seen.add(name);
        }
    }

    /** JDK classes you can `new` for this interface or abstract class, common ones first, at
        most 8. Scanning the class list took ~130 ms, so a JDK type's answer is kept. */
    List<String> implementations(Unit u, TypeElement want) {
        String key = want.getQualifiedName().toString();
        boolean jdk = entryOf(u, want) != null;
        List<String> cached = jdk ? implCache.get(key) : null;
        if (cached != null) return cached;
        try { buildIndex(); } catch (IOException e) { return List.of(); }
        TypeMirror target = u.ty.erasure(want.asType());
        List<TypeElement> found = new ArrayList<>();
        for (String fqn : index) {
            if (!isPublic(u, fqn)) continue;
            TypeElement te = u.el.getTypeElement(fqn);
            if (te != null && concrete(te) && u.ty.isAssignable(u.ty.erasure(te.asType()), target)) found.add(te);
        }
        found.sort(Comparator.comparing((TypeElement te) -> !COMMON.contains(te.getSimpleName().toString()))
                             .thenComparing(te -> te.getSimpleName().toString()));
        List<String> out = new ArrayList<>();
        for (TypeElement te : found.subList(0, Math.min(8, found.size()))) out.add(te.getQualifiedName().toString());
        if (jdk) implCache.put(key, out);
        return out;
    }

    static boolean concrete(TypeElement te) {
        return te.getKind() == ElementKind.CLASS && !te.getModifiers().contains(Modifier.ABSTRACT);
    }

    /** int[] -> inserts int[|]; int[][] -> int[|][] */
    static Map<String, Object> arrayItem(TypeMirror t, String sort, boolean star) {
        int dims = 0;
        TypeMirror c = t;
        while (c.getKind() == TypeKind.ARRAY) { dims++; c = ((ArrayType) c).getComponentType(); }
        String label = simple(t), base = simple(c);
        Map<String, Object> it = item(label, c.getKind().isPrimitive() ? "keyword" : "class", base + "[$0]" + "[]".repeat(dims - 1));
        it.put("snippet", true);
        it.put("sort", sort + label);
        it.put("type", star ? "★ expected type" : "array");
        return it;
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

    static Map<String, Object> keyword(String k) { return keyword(k, false); }

    /* 5 Oct: `re` at the end of a method listed `record` first (every keyword ranked the same,
       so A-Z). Now, where a statement starts, `return` comes first, as in IntelliJ, then the
       words that start statements; words rarely typed go last. */
    static final Set<String> STATEMENT_WORDS = Set.of("if", "for", "while", "switch", "try", "throw", "break", "continue",
            "do", "final", "var", "new", "this", "super", "int", "long", "double", "boolean", "char", "byte", "short", "float");
    static final Set<String> RARE_WORDS = Set.of("record", "enum", "interface", "class", "abstract", "native", "strictfp",
            "transient", "volatile", "synchronized", "implements", "extends", "throws", "import", "default", "case",
            "assert", "yield", "instanceof");

    static Map<String, Object> keyword(String k, boolean statementStart) {
        Map<String, Object> it = item(k, "keyword", k);
        it.put("sort", statementStart && k.equals("return") ? "0"
                : statementStart && STATEMENT_WORDS.contains(k) ? "1" + k
                : RARE_WORDS.contains(k) ? "8" + k : "4" + k);
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

    /** The word around the cursor: {start, end}, or null outside a name, a comment or a string. */
    static int[] wordAt(String t, int offset) {
        int cur = Math.max(0, Math.min(offset, t.length()));
        int s = cur, e = cur;
        while (s > 0 && Character.isJavaIdentifierPart(t.charAt(s - 1))) s--;
        while (e < t.length() && Character.isJavaIdentifierPart(t.charAt(e))) e++;
        return s == e || lex(t, s).quoted ? null : new int[]{s, e};
    }

    /** Where the name written at [s, e) starts, for a tree that names something there: an
        identifier, a.name, Type::name, or a variable, method or class being declared. */
    long nameStart(Unit u, Tree n, String text) {
        switch (n.getKind()) {
            case IDENTIFIER: return u.start(n);
            case MEMBER_SELECT: return u.end(n) - ((MemberSelectTree) n).getIdentifier().length();
            case MEMBER_REFERENCE: return u.end(n) - ((MemberReferenceTree) n).getName().length();
            case VARIABLE: {
                VariableTree v = (VariableTree) n;
                return findName(text, v.getName().toString(), v.getType() == null ? u.start(n) : u.end(v.getType()));
            }
            case METHOD: {
                MethodTree m = (MethodTree) n;
                String name = m.getName().contentEquals("<init>") ? enclosingClassName(u, n) : m.getName().toString();
                return name == null ? -1 : findName(text, name, m.getReturnType() == null ? u.start(n) : u.end(m.getReturnType()));
            }
            default:
                if (n instanceof ClassTree c) {
                    int at = (int) Math.max(0, u.start(n));
                    java.util.regex.Matcher k = java.util.regex.Pattern.compile("\\b(class|interface|enum|record)\\s+").matcher(text);
                    return k.find(at) ? findName(text, c.getSimpleName().toString(), k.end()) : -1;
                }
                return -1;
        }
    }

    String enclosingClassName(Unit u, Tree method) {
        TreePath p = u.trees.getPath(u.cu, method);
        for (; p != null; p = p.getParentPath()) if (p.getLeaf() instanceof ClassTree c) return c.getSimpleName().toString();
        return null;
    }

    /** First whole-word `name` at or after `from`, or -1. */
    static long findName(String text, String name, long from) {
        if (from < 0) return -1;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?<![\\w$])" + java.util.regex.Pattern.quote(name) + "(?![\\w$])").matcher(text);
        return m.find((int) from) ? m.start() : -1;
    }

    /** The tree whose name is the word at [s, e). */
    TreePath pathAt(Unit u, String t, int s, int e) {
        String word = t.substring(s, e);
        TreePath[] hit = {null};
        new TreePathScanner<Void, Void>() {
            @Override public Void scan(Tree tree, Void v) {
                if (hit[0] != null || tree == null) return null;
                long a = u.start(tree), b = u.end(tree);
                if (a >= 0 && b >= 0 && (b < s || a > e)) return null;          // not around the word
                return super.scan(tree, v);
            }
            void at(Tree n, CharSequence name) {
                if (hit[0] == null && name.toString().equals(word) && nameStart(u, n, t) == s) hit[0] = getCurrentPath();
            }
            @Override public Void visitIdentifier(IdentifierTree n, Void v) { at(n, n.getName()); return null; }
            @Override public Void visitMemberSelect(MemberSelectTree n, Void v) { at(n, n.getIdentifier()); return super.visitMemberSelect(n, v); }
            @Override public Void visitMemberReference(MemberReferenceTree n, Void v) { at(n, n.getName()); return super.visitMemberReference(n, v); }
            @Override public Void visitVariable(VariableTree n, Void v) { at(n, n.getName()); return super.visitVariable(n, v); }
            @Override public Void visitMethod(MethodTree n, Void v) {
                at(n, n.getName().contentEquals("<init>") ? String.valueOf(enclosingClassName(u, n)) : n.getName());
                return super.visitMethod(n, v);
            }
            @Override public Void visitClass(ClassTree n, Void v) { at(n, n.getSimpleName()); return super.visitClass(n, v); }
        }.scan(u.cu, null);
        return hit[0];
    }

    Map<String, Object> hover(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        int[] w = wordAt(r.text, r.offset);
        if (w == null) return res;
        Unit u = new Unit(r.text, r);
        TreePath hit = pathAt(u, r.text, w[0], w[1]);
        if (hit == null) return res;
        Element el = u.trees.getElement(hit);
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

    /* ------------------------------------------------------------------ compile (Run) */

    /** Run's compile step, in this warm JVM. It runs the javac command's own code (Tool.run)
        with the very arguments codeview.py gives the command, so the class files and every
        character of the error text are the same; only the start-up is gone. The javac API
        words errors differently (java.lang.String, not String), so it is not used here.
        Request: fileName = the source file Run wrote, extra = the folder for the classes.
        5 Oct: javac was ~570 of a ~680 ms Run. */
    Map<String, Object> compile(Req r) {
        List<String> args = new ArrayList<>(List.of("-nowarn", "-encoding", "UTF-8", "-d", r.extra));
        if (!r.sourcepath.isEmpty()) args.addAll(List.of("-sourcepath", r.sourcepath));
        args.add(r.fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int rc = jc.run(null, out, out, args.toArray(new String[0]));
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", rc == 0);
        res.put("output", out.toString(StandardCharsets.UTF_8));
        return res;
    }

    /* ------------------------------------------------------------------ quick fix, definition, rename */

    /** Alt+Enter on "cannot find symbol": the JDK classes with that name and their import. */
    Map<String, Object> imports(Req r) throws IOException {
        Unit u = new Unit(r.text, r);
        List<Map<String, Object>> fixes = new ArrayList<>();
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("fixes", fixes);
        try { buildIndex(); } catch (IOException e) { return res; }
        for (String fqn : index) {
            if (fqn.substring(fqn.lastIndexOf('.') + 1).equals(r.extra) && isPublic(u, fqn)) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("fqn", fqn);
                f.put("importFqn", fqn);
                fixes.add(f);
            }
        }
        fixes.sort(Comparator.comparing((Map<String, Object> f) -> !String.valueOf(f.get("fqn")).startsWith("java.util.")));
        res.put("items", fixes);                      // importPlace fills in "import" on these
        importPlace(u, r, res);
        res.remove("items");
        return res;
    }

    /** Alt+Enter on a red call to a method not written yet: IntelliJ's Create method (6 Oct).
        Parameters take the arguments' types and names (a variable keeps its name, a literal is
        named by its type); the return type comes from where the call stands: `int n = f(..)` int,
        `return f(..)` the method's type, `if (f(..))` boolean, a plain `f(..);` void, else Object.
        Static in static code. It goes after the member that holds the call, or, for `a.f(..)` on
        a class of this file, at the end of that class. Result: insertAt + text (whole lines,
        starting with a newline), selLine/selCol/selLen of the body to type over, and imports. */
    Map<String, Object> create(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        int[] w = wordAt(r.text, r.offset);
        if (w == null) return res;
        Unit u = new Unit(r.text, r);
        TreePath hit = pathAt(u, r.text, w[0], w[1]);
        if (hit == null || !(hit.getParentPath().getLeaf() instanceof MethodInvocationTree call)
                || call.getMethodSelect() != hit.getLeaf()) return res;
        TreePath callPath = hit.getParentPath();
        Element known = u.trees.getElement(hit);
        if (known != null && known.getKind() == ElementKind.METHOD) return res;   // it exists: a different error
        // where it goes: after the member holding the call, in that member's class...
        TreePath member = null;
        for (TreePath p = hit; p.getParentPath() != null; p = p.getParentPath()) {
            if (p.getParentPath().getLeaf() instanceof ClassTree) { member = p; break; }
        }
        if (member == null) return res;
        ClassTree target = (ClassTree) member.getParentPath().getLeaf();
        Tree after = member.getLeaf();
        String mods = isStatic(hit) ? "private static " : "private ";
        // ...or, for a.f(..) / Type.f(..) on a class declared in this file, at the end of that class
        if (hit.getLeaf() instanceof MemberSelectTree ms
                && !(ms.getExpression() instanceof IdentifierTree q && q.getName().contentEquals("this"))) {
            TreePath qp = new TreePath(hit, ms.getExpression());
            Element qe = u.trees.getElement(qp);
            TypeMirror qt = u.trees.getTypeMirror(qp);
            boolean typeName = qe != null && (qe.getKind().isClass() || qe.getKind().isInterface());
            TypeElement te = typeName ? (TypeElement) qe
                    : qt != null && qt.getKind() == TypeKind.DECLARED ? (TypeElement) ((DeclaredType) qt).asElement() : null;
            TreePath tp = te == null ? null : u.trees.getPath(te);
            if (tp == null || tp.getCompilationUnit() != u.cu || !(tp.getLeaf() instanceof ClassTree ct)
                    || te.getKind() != ElementKind.CLASS) {
                res.put("reject", "Create method works on classes written in this file");
                return res;
            }
            target = ct;
            List<? extends Tree> ms2 = ct.getMembers();
            after = ms2.isEmpty() ? null : ms2.get(ms2.size() - 1);
            mods = typeName ? "static " : "";
        }
        String text = r.text;
        long at;
        String indent;
        if (after != null) {
            at = u.end(after);
            int ls = text.lastIndexOf('\n', (int) u.start(after) - 1) + 1;
            indent = text.substring(ls, (int) u.start(after)).replaceAll("\\S.*", "");
        } else {                                      // an empty class: just inside its closing brace
            at = text.lastIndexOf('}', (int) u.end(target) - 1);
            int ls = text.lastIndexOf('\n', (int) u.start(target) - 1) + 1;
            indent = text.substring(ls, (int) u.start(target)).replaceAll("\\S.*", "") + "    ";
        }
        int eol = text.indexOf('\n', (int) at);
        at = after == null ? at : (eol < 0 ? text.length() : eol);
        // the parameters
        List<String> params = new ArrayList<>();
        Set<String> used = new HashSet<>();
        List<TypeMirror> types = new ArrayList<>();
        for (ExpressionTree a : call.getArguments()) {
            TypeMirror t = u.trees.getTypeMirror(new TreePath(callPath, a));
            String type = typeText(t);
            if (type.equals("Object")) t = null;
            types.add(t);
            String n = argName(a, t), base = n;
            for (int k = 1; !used.add(n); k++) n = base + k;
            params.add(type + " " + n);
        }
        TypeMirror rt = resultType(u, callPath);
        String ret = rt == null ? "Object" : rt.getKind() == TypeKind.VOID ? "void" : typeText(rt);
        if (rt != null && rt.getKind() != TypeKind.VOID) types.add(rt);
        String body = ret.equals("void") ? "" : "return " + emptyValue(rt, ret) + ";";
        String name = w[0] < w[1] ? text.substring(w[0], w[1]) : "";
        String in = indent + "    ";
        String made = "\n\n" + indent + mods + ret + " " + name + "(" + String.join(", ", params) + ") {\n"
                + in + body + "\n" + indent + "}";
        if (after == null) made = made.substring(1) + "\n" + indent.substring(Math.min(4, indent.length()));
        res.put("name", name);
        res.put("insertAt", at);
        res.put("text", made);
        res.put("selLine", Arrays.asList(made.split("\n", -1)).indexOf(in + body));
        res.put("selCol", in.length() + 1);
        res.put("selLen", body.length());
        // imports for the types it names that this file does not see yet
        Set<String> imported = new HashSet<>(), star = new HashSet<>(List.of("java.lang"));
        for (ImportTree it : u.cu.getImports()) {
            String q = it.getQualifiedIdentifier().toString();
            if (it.isStatic()) continue;
            if (q.endsWith(".*")) star.add(q.substring(0, q.length() - 2)); else imported.add(q);
        }
        if (u.cu.getPackageName() != null) star.add(u.cu.getPackageName().toString());
        Set<String> need = new TreeSet<>();
        for (TypeMirror t : types) needImports(u, t, imported, star, need);
        if (!need.isEmpty()) {
            List<Map<String, Object>> items = new ArrayList<>();
            for (String fqn : need) items.add(new LinkedHashMap<>(Map.of("importFqn", fqn)));
            Map<String, Object> ir = new LinkedHashMap<>();
            ir.put("items", items);
            importPlace(u, r, ir);
            List<String> imps = new ArrayList<>();
            for (Map<String, Object> it : items) imps.add((String) it.get("import"));
            res.put("imports", imps);
            res.put("importAt", ir.get("importAt"));
        }
        return res;
    }

    /** A type as the new method's code writes it; Object for what javac could not work out. */
    static String typeText(TypeMirror t) {
        if (t == null) return "Object";
        switch (t.getKind()) {
            case ERROR, NULL, NONE, VOID, OTHER, EXECUTABLE, PACKAGE, MODULE: return "Object";
            default: {
                String s = simple(t);
                return s.isEmpty() || s.contains("?") || s.contains("capture#") ? "Object" : s;
            }
        }
    }

    /** IntelliJ's names for a new parameter: a variable keeps its name, a.b is b, getX() is x,
        anything else is named by its type (i, l, c, d, b, s, or the class name). */
    static String argName(ExpressionTree a, TypeMirror t) {
        if (a instanceof IdentifierTree id) return id.getName().toString();
        if (a instanceof MemberSelectTree ms && !ms.getIdentifier().contentEquals("class")) return safe(ms.getIdentifier().toString());
        if (a instanceof MethodInvocationTree && t != null) return nameFor(t, a).get(0);
        if (t == null) return "o";
        switch (t.getKind()) {
            case INT, SHORT, BYTE: return "i";
            case LONG: return "l";
            case CHAR: return "c";
            case BOOLEAN: return "b";
            case DOUBLE: return "d";
            case FLOAT: return "f";
            default:
                if (t.getKind() == TypeKind.DECLARED
                        && ((DeclaredType) t).asElement().getSimpleName().contentEquals("String")) return "s";
                return nameFor(t, null).get(0);
        }
    }

    /** What the new method returns until it is written. An array is empty rather than null, so
        `for (int x : f(..))` runs. */
    static String emptyValue(TypeMirror t, String text) {
        if (t == null) return "null";
        return switch (t.getKind()) {
            case BOOLEAN -> "false";
            case INT, LONG, SHORT, BYTE, CHAR, DOUBLE, FLOAT -> "0";
            case ARRAY -> {
                int b = text.indexOf('[');
                yield "new " + text.substring(0, b) + "[0]" + text.substring(b + 2);
            }
            default -> "null";
        };
    }

    /** The type the call's value must have, from the code around it; void for a plain statement,
        null when nothing says (the caller writes Object). */
    TypeMirror resultType(Unit u, TreePath expr) {
        TreePath p = expr.getParentPath();
        Tree child = expr.getLeaf();
        while (p.getLeaf() instanceof ParenthesizedTree) { child = p.getLeaf(); p = p.getParentPath(); }
        Tree t = p.getLeaf();
        TypeMirror bool = u.ty.getPrimitiveType(TypeKind.BOOLEAN);
        if (t instanceof ExpressionStatementTree) return u.ty.getNoType(TypeKind.VOID);
        if (t instanceof VariableTree v && v.getInitializer() == child) {
            return v.getType() == null ? null : known(u.trees.getTypeMirror(new TreePath(p, v.getType())));
        }
        if (t instanceof AssignmentTree a && a.getExpression() == child) return known(u.trees.getTypeMirror(new TreePath(p, a.getVariable())));
        if (t instanceof CompoundAssignmentTree a && a.getExpression() == child) return known(u.trees.getTypeMirror(new TreePath(p, a.getVariable())));
        if (t instanceof ReturnTree) {
            for (TreePath q = p; q != null; q = q.getParentPath()) {
                if (q.getLeaf() instanceof LambdaExpressionTree) return null;
                if (q.getLeaf() instanceof MethodTree m) {
                    return m.getReturnType() == null ? null : known(u.trees.getTypeMirror(new TreePath(q, m.getReturnType())));
                }
            }
            return null;
        }
        if (t instanceof IfTree || t instanceof WhileLoopTree || t instanceof DoWhileLoopTree
                || (t instanceof ForLoopTree f && f.getCondition() == child)
                || (t instanceof ConditionalExpressionTree c && c.getCondition() == child)
                || (t instanceof UnaryTree un && un.getKind() == Tree.Kind.LOGICAL_COMPLEMENT)
                || t instanceof AssertTree) return bool;
        if (t instanceof BinaryTree b) {
            Tree.Kind k = b.getKind();
            if (k == Tree.Kind.CONDITIONAL_AND || k == Tree.Kind.CONDITIONAL_OR) return bool;
            ExpressionTree other = b.getLeftOperand() == child ? b.getRightOperand() : b.getLeftOperand();
            TypeMirror ot = known(u.trees.getTypeMirror(new TreePath(p, other)));
            boolean compare = k == Tree.Kind.LESS_THAN || k == Tree.Kind.GREATER_THAN || k == Tree.Kind.LESS_THAN_EQUAL
                    || k == Tree.Kind.GREATER_THAN_EQUAL || k == Tree.Kind.EQUAL_TO || k == Tree.Kind.NOT_EQUAL_TO;
            if (compare) return ot;
            if (ot != null && ot.getKind() == TypeKind.DECLARED && k == Tree.Kind.PLUS) return ot;   // "..." + f(..)
            TypeMirror whole = resultType(u, p);                 // 1 + f(..) where a double is wanted: double
            if (whole != null && whole.getKind().isPrimitive()) return whole;
            return ot;
        }
        if (t instanceof EnhancedForLoopTree ef && ef.getExpression() == child) {
            TypeMirror v = known(u.trees.getTypeMirror(new TreePath(new TreePath(p, ef.getVariable()), ef.getVariable().getType())));
            return v == null ? null : u.ty.getArrayType(v);
        }
        if (t instanceof ArrayAccessTree aa && aa.getIndex() == child) return u.ty.getPrimitiveType(TypeKind.INT);
        if (t instanceof MethodInvocationTree outer) {                   // an argument: only an unambiguous method says
            Element e = u.trees.getElement(p);
            int i = outer.getArguments().indexOf(child);
            if (e instanceof ExecutableElement ex && i >= 0 && i < ex.getParameters().size() && !ex.isVarArgs()
                    && ElementFilter.methodsIn(ex.getEnclosingElement().getEnclosedElements()).stream()
                           .filter(m -> m.getSimpleName().equals(ex.getSimpleName())).count() == 1) {
                return known(ex.getParameters().get(i).asType());
            }
        }
        return null;
    }

    static TypeMirror known(TypeMirror t) {
        return t == null || typeText(t).equals("Object") && !(t.getKind() == TypeKind.DECLARED) ? null : t;
    }

    /** The imports a type needs (its type arguments too) that this file does not have. */
    static void needImports(Unit u, TypeMirror t, Set<String> imported, Set<String> star, Set<String> need) {
        if (t == null) return;
        if (t.getKind() == TypeKind.ARRAY) { needImports(u, ((ArrayType) t).getComponentType(), imported, star, need); return; }
        if (t.getKind() != TypeKind.DECLARED) return;
        DeclaredType d = (DeclaredType) t;
        TypeElement te = (TypeElement) d.asElement();
        for (TypeMirror a : d.getTypeArguments()) needImports(u, a, imported, star, need);
        if (te.getNestingKind() != NestingKind.TOP_LEVEL) return;
        String fqn = te.getQualifiedName().toString(), pkg = u.el.getPackageOf(te).getQualifiedName().toString();
        if (pkg.isEmpty() || star.contains(pkg) || imported.contains(fqn)) return;
        need.add(fqn);
    }

    /** Ctrl+click / F12: where the name under the cursor is declared. In this file: its offsets;
        in another file of the folder (a helper class): that file with line and column. */
    Map<String, Object> definition(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        int[] w = wordAt(r.text, r.offset);
        if (w == null) return res;
        Unit u = new Unit(r.text, r);
        TreePath hit = pathAt(u, r.text, w[0], w[1]);
        Element el = hit == null ? null : u.trees.getElement(hit);
        TreePath decl = el == null ? null : u.trees.getPath(el);
        if (decl == null) return res;                 // the JDK's, or unknown
        CompilationUnitTree cu = decl.getCompilationUnit();
        String text = cu == u.cu ? r.text : cu.getSourceFile().getCharContent(true).toString();
        long at;
        if (cu == u.cu) at = nameStart(u, decl.getLeaf(), text);
        else {                                        // positions in that file need its own tree
            SourcePositions sp = u.trees.getSourcePositions();
            Tree n = decl.getLeaf();
            String name = el.getKind() == ElementKind.CONSTRUCTOR ? el.getEnclosingElement().getSimpleName().toString() : el.getSimpleName().toString();
            long from = sp.getStartPosition(cu, n);
            if (n instanceof VariableTree v && v.getType() != null) from = sp.getEndPosition(cu, v.getType());
            if (n instanceof MethodTree m && m.getReturnType() != null) from = sp.getEndPosition(cu, m.getReturnType());
            if (n instanceof ClassTree) {
                java.util.regex.Matcher k = java.util.regex.Pattern.compile("\\b(class|interface|enum|record)\\s+").matcher(text);
                from = k.find((int) Math.max(0, from)) ? k.end() : from;
            }
            at = findName(text, name, from);
        }
        if (at < 0) return res;
        int len = (el.getKind() == ElementKind.CONSTRUCTOR ? el.getEnclosingElement() : el).getSimpleName().length();
        if (cu == u.cu) {
            res.put("start", at);
            res.put("end", at + len);
        } else {
            int ls = text.lastIndexOf('\n', (int) at - 1) + 1;
            res.put("file", new File(cu.getSourceFile().toUri()).getPath());
            res.put("line", text.substring(0, ls).chars().filter(c -> c == '\n').count() + 1);
            res.put("col", at - ls + 1);
            res.put("len", len);
        }
        return res;
    }

    /** Shift+F6: every place in this file that names the same thing as the word under the
        cursor. Only what this file declares can be renamed; a JDK name is refused. */
    Map<String, Object> rename(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        int[] w = wordAt(r.text, r.offset);
        if (w == null) { res.put("reject", "Put the cursor on a name to rename it"); return res; }
        Unit u = new Unit(r.text, r);
        TreePath hit = pathAt(u, r.text, w[0], w[1]);
        Element el = hit == null ? null : u.trees.getElement(hit);
        if (el != null && el.getKind() == ElementKind.CONSTRUCTOR) el = el.getEnclosingElement();
        TreePath decl = el == null ? null : u.trees.getPath(el);
        if (el == null || decl == null || decl.getCompilationUnit() != u.cu) {
            res.put("reject", el == null ? "This name is not known here" : "Only names declared in this file can be renamed");
            return res;
        }
        String name = el.getSimpleName().toString(), text = r.text;
        Element target = el;
        Set<Long> spots = new TreeSet<>();
        new TreePathScanner<Void, Void>() {
            void at(Tree n) {
                Element e = u.trees.getElement(getCurrentPath());
                if (e != null && e.getKind() == ElementKind.CONSTRUCTOR && target instanceof TypeElement) e = e.getEnclosingElement();
                if (!target.equals(e)) return;
                long s = nameStart(u, n, text);
                if (s >= 0 && text.startsWith(name, (int) s)) spots.add(s);
            }
            @Override public Void visitIdentifier(IdentifierTree n, Void v) { at(n); return null; }
            @Override public Void visitMemberSelect(MemberSelectTree n, Void v) { at(n); return super.visitMemberSelect(n, v); }
            @Override public Void visitMemberReference(MemberReferenceTree n, Void v) { at(n); return super.visitMemberReference(n, v); }
            @Override public Void visitVariable(VariableTree n, Void v) { at(n); return super.visitVariable(n, v); }
            @Override public Void visitMethod(MethodTree n, Void v) { at(n); return super.visitMethod(n, v); }
            @Override public Void visitClass(ClassTree n, Void v) { at(n); return super.visitClass(n, v); }
        }.scan(u.cu, null);
        res.put("spots", new ArrayList<>(spots));
        res.put("len", name.length());
        return res;
    }

    /* ------------------------------------------------------------------ introduce variable */

    /** Ctrl+Alt+V, IntelliJ's Introduce Variable (5 Oct, Ravi asked). With a selection: that
        expression. At a bare cursor: the expressions around it, innermost first, for the page
        to offer (a single one is used at once). Answers the declaration's type and a free name,
        where it goes for this occurrence and, if the same expression appears again, for all
        of them, and any import the type needs. Refuses what cannot be a variable, and an
        expression using a variable that would not exist where the declaration goes.
        Request: offset = selection start, extra = selection end. */
    Map<String, Object> extract(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        String t = r.text;
        int a = Math.max(0, Math.min(r.offset, t.length())), b = a;
        try { b = Math.max(0, Math.min(Integer.parseInt(r.extra.trim()), t.length())); } catch (NumberFormatException e) { /* a bare cursor */ }
        if (b < a) { int x = a; a = b; b = x; }
        while (a < b && Character.isWhitespace(t.charAt(a))) a++;
        while (b > a && Character.isWhitespace(t.charAt(b - 1))) b--;
        Unit u = new Unit(t, r);
        List<TreePath> around = expressionsAround(u, a, b);
        TreePath target = null;
        if (a < b) {
            for (TreePath p : around) if (u.start(p.getLeaf()) == a && u.end(p.getLeaf()) == b) { target = p; break; }
            if (target == null) return reject(res, "Select a whole expression, such as temperatures[i] or stack.peek()");
        } else {
            List<TreePath> cands = new ArrayList<>();
            for (TreePath p : around) {
                Tree leaf = p.getLeaf();
                if (!(leaf instanceof IdentifierTree) && !(leaf instanceof ParenthesizedTree) && whyNot(u, p) == null) cands.add(p);
            }
            if (cands.isEmpty()) return reject(res, "Put the cursor in an expression, or select one");
            if (cands.size() > 1) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (TreePath p : cands.subList(0, Math.min(6, cands.size()))) {
                    Map<String, Object> c = new LinkedHashMap<>();
                    c.put("start", u.start(p.getLeaf()));
                    c.put("end", u.end(p.getLeaf()));
                    c.put("text", t.substring((int) u.start(p.getLeaf()), (int) u.end(p.getLeaf())).replaceAll("\\s+", " "));
                    list.add(c);
                }
                res.put("candidates", list);
                return res;
            }
            target = cands.get(0);
        }
        String why = whyNot(u, target);
        if (why != null) return reject(res, why);
        TreePath anchor = (TreePath) anchorOf(target);
        String gone = invisible(u, target, anchor);
        if (gone != null) {
            return reject(res, "It uses `" + gone + "`, which does not exist yet where the new variable would go (above the"
                    + " statement). Put { } around the loop's or if's body first.");
        }
        long start = u.start(target.getLeaf()), end = u.end(target.getLeaf());
        TypeMirror tm = u.trees.getTypeMirror(target);
        String type = simple(tm);
        if (type.contains("?") || type.contains("&")) type = "var";
        TreePath body = bodyOf(target);
        res.put("type", type);
        res.put("name", freeName(u, target, tm, anchor, body));
        res.put("expr", t.substring((int) start, (int) end));
        res.put("start", start);
        res.put("end", end);
        Tree parent = target.getParentPath().getLeaf();
        if (parent instanceof ExpressionStatementTree es) {     // `list.add(x);` becomes `boolean add = list.add(x);`
            res.put("statement", List.of(u.start(es), u.end(es)));
        } else {
            res.put("one", place(u, t, anchor.getLeaf()));
            List<TreePath> occ = occurrences(u, target, body);
            if (occ.size() > 1) {
                TreePath common = commonAnchor(u, occ);
                if (common != null && invisible(u, target, common) == null) {
                    Map<String, Object> all = place(u, t, common.getLeaf());
                    List<List<Long>> spots = new ArrayList<>();
                    for (TreePath p : occ) spots.add(List.of(u.start(p.getLeaf()), u.end(p.getLeaf())));
                    all.put("spots", spots);
                    res.put("all", all);
                }
            }
        }
        res.put("imports", importsFor(u, r, tm, type));
        return res;
    }

    static Map<String, Object> reject(Map<String, Object> res, String why) {
        res.clear();
        res.put("reject", why);
        return res;
    }

    /** Every expression around [a, b], innermost first. */
    List<TreePath> expressionsAround(Unit u, int a, int b) {
        List<TreePath> out = new ArrayList<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void scan(Tree tree, Void v) {
                if (tree == null) return null;
                long s = u.start(tree), e = u.end(tree);
                if (s >= 0 && e >= 0 && (s > a || e < b)) return null;
                if (tree instanceof ExpressionTree && s >= 0 && e >= 0 && getCurrentPath() != null) out.add(new TreePath(getCurrentPath(), tree));
                return super.scan(tree, v);
            }
        }.scan(u.cu, null);
        out.sort(Comparator.comparingLong(p -> u.end(p.getLeaf()) - u.start(p.getLeaf())));
        return out;
    }

    /** Why this expression cannot become a variable, or null. */
    String whyNot(Unit u, TreePath p) {
        Tree leaf = p.getLeaf();
        Tree parent = p.getParentPath() == null ? null : p.getParentPath().getLeaf();
        if (parent instanceof MethodInvocationTree mi && mi.getMethodSelect() == leaf) return "That is a method's name; select the whole call, with its ( )";
        if (parent instanceof AssignmentTree as && as.getVariable() == leaf
                || parent instanceof CompoundAssignmentTree ca && ca.getVariable() == leaf
                || parent instanceof UnaryTree ut && incDec(ut)) {
            return "That is the left side of an assignment: a place to store into, not a value";
        }
        if (leaf instanceof AssignmentTree || leaf instanceof CompoundAssignmentTree || leaf instanceof UnaryTree ut2 && incDec(ut2)) {
            return "That is an assignment, not a value to keep";
        }
        if (parent instanceof CaseTree) return "A case label must stay a constant";
        Element el = u.trees.getElement(p);
        if ((leaf instanceof IdentifierTree || leaf instanceof MemberSelectTree) && (el instanceof TypeElement || el instanceof PackageElement)) {
            return "That is a class name, not a value";
        }
        TypeMirror tm = u.trees.getTypeMirror(p);
        if (tm == null || tm.getKind() == TypeKind.ERROR) return "javac cannot tell this expression's type; fix the red underline first";
        if (tm.getKind() == TypeKind.VOID) return "This call returns nothing (void), so there is no value to keep";
        if (tm.getKind() == TypeKind.NULL) return "A plain null has no type to declare";
        if (tm.getKind() == TypeKind.PACKAGE || tm.getKind() == TypeKind.EXECUTABLE || tm.getKind() == TypeKind.NONE) return "That is not a value";
        Object anchor = anchorOf(p);
        return anchor instanceof String s ? s : null;
    }

    static boolean incDec(UnaryTree u) {
        return switch (u.getKind()) { case PREFIX_INCREMENT, PREFIX_DECREMENT, POSTFIX_INCREMENT, POSTFIX_DECREMENT -> true; default -> false; };
    }

    /** The statement the declaration goes above (its path), or why there is none (a String). */
    static Object anchorOf(TreePath p) {
        TreePath child = p;
        for (TreePath q = p.getParentPath(); q != null; child = q, q = q.getParentPath()) {
            Tree leaf = q.getLeaf(), c = child.getLeaf();
            if (leaf instanceof BlockTree bt && bt.getStatements().contains(c)) return child;
            if (leaf instanceof CaseTree ct && ct.getStatements() != null && ct.getStatements().contains(c)) return child;
            if (leaf instanceof LambdaExpressionTree lt && lt.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION) {
                return "Inside a one-line lambda (x -> ...); give it a { } body first";
            }
            // the declaration would go above the loop and be computed once (5 Oct)
            if (leaf instanceof WhileLoopTree w && w.getCondition() == c || leaf instanceof DoWhileLoopTree d && d.getCondition() == c
                    || leaf instanceof ForLoopTree f && (f.getCondition() == c || f.getUpdate().contains(c))) {
                return "It is in the loop's condition, which runs every time round; a variable above the loop "
                        + "would be computed only once. Make it a variable inside the loop by hand.";
            }
            if (leaf instanceof ClassTree || leaf instanceof CompilationUnitTree) break;
        }
        return "Only inside a method's body";
    }

    /** The first local variable the expression uses that is not visible at the anchor, or null.
        Visible: declared before the anchor, in a block (or for, method...) that contains the
        anchor. Asked of the trees, not of Trees.getScope: at a statement inside a loop, getScope
        left out the method's own locals (5 Oct: `waiting` looked invisible inside the for). */
    String invisible(Unit u, TreePath expr, TreePath anchor) {
        long as = u.start(anchor.getLeaf()), ae = u.end(anchor.getLeaf());
        Set<Element> inside = new HashSet<>();
        String[] bad = {null};
        new TreePathScanner<Void, Void>() {
            @Override public Void visitVariable(VariableTree n, Void v) {      // a lambda's own parameters
                Element e = u.trees.getElement(getCurrentPath());
                if (e != null) inside.add(e);
                return super.visitVariable(n, v);
            }
            @Override public Void visitIdentifier(IdentifierTree n, Void v) {
                Element e = u.trees.getElement(getCurrentPath());
                if (bad[0] != null || e == null || !isLocal(e) || inside.contains(e)) return null;
                TreePath decl = u.trees.getPath(e);
                if (decl == null || decl.getParentPath() == null) return null;
                Tree holder = decl.getParentPath().getLeaf();
                boolean seen = u.start(decl.getLeaf()) < as && u.start(holder) <= as && ae <= u.end(holder);
                if (!seen) bad[0] = e.getSimpleName().toString();
                return null;
            }
        }.scan(expr, null);
        return bad[0];
    }

    static boolean isLocal(Element e) {
        return switch (e.getKind()) {
            case LOCAL_VARIABLE, PARAMETER, EXCEPTION_PARAMETER, RESOURCE_VARIABLE, BINDING_VARIABLE -> true;
            default -> false;
        };
    }

    /** The method (or lambda, or initializer) the expression is in. */
    static TreePath bodyOf(TreePath p) {
        TreePath last = p;
        for (TreePath q = p; q != null; last = q, q = q.getParentPath()) {
            Tree leaf = q.getLeaf();
            if (leaf instanceof MethodTree || leaf instanceof LambdaExpressionTree) return q;
            if (leaf instanceof ClassTree) return last;
        }
        return last;
    }

    /** Where the declaration goes: the start of the anchor's line, indented like it; or, when the
        line has other code before the statement, right before the statement on that line. */
    static Map<String, Object> place(Unit u, String t, Tree stmt) {
        int s = (int) u.start(stmt);
        int ls = t.lastIndexOf('\n', s - 1) + 1;
        String before = t.substring(ls, s);
        Map<String, Object> m = new LinkedHashMap<>();
        boolean own = before.isBlank();
        m.put("at", own ? ls : s);
        m.put("indent", own ? before : "");
        m.put("inline", !own);
        return m;
    }

    /** The same expression elsewhere in the method: same text (spaces aside), same variables. */
    List<TreePath> occurrences(Unit u, TreePath target, TreePath body) {
        String text = squash(u, target.getLeaf());
        List<Element> refs = refs(u, target);
        Tree.Kind kind = target.getLeaf().getKind();
        List<TreePath> out = new ArrayList<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void scan(Tree tree, Void v) {
                if (tree != null && tree.getKind() == kind && getCurrentPath() != null) {
                    TreePath p = new TreePath(getCurrentPath(), tree);
                    if (squash(u, tree).equals(text) && refs(u, p).equals(refs) && whyNot(u, p) == null
                            && !(p.getParentPath().getLeaf() instanceof ExpressionStatementTree)) out.add(p);
                }
                return super.scan(tree, v);
            }
        }.scan(body, null);
        return out;
    }

    String squash(Unit u, Tree tree) {
        long s = u.start(tree), e = u.end(tree);
        String all = u.file.text;
        return s < 0 || e < s ? "" : all.substring((int) s, (int) e).replaceAll("\\s+", "");
    }

    List<Element> refs(Unit u, TreePath p) {
        List<Element> out = new ArrayList<>();
        new TreePathScanner<Void, Void>() {
            @Override public Void visitIdentifier(IdentifierTree n, Void v) {
                out.add(u.trees.getElement(getCurrentPath()));
                return null;
            }
        }.scan(p, null);
        return out;
    }

    /** Above the first occurrence, in the innermost block that holds all of them. */
    TreePath commonAnchor(Unit u, List<TreePath> occ) {
        List<List<Tree[]>> chains = new ArrayList<>();
        for (TreePath p : occ) {
            List<Tree[]> chain = new ArrayList<>();
            TreePath child = p;
            for (TreePath q = p.getParentPath(); q != null; child = q, q = q.getParentPath()) {
                Tree leaf = q.getLeaf(), c = child.getLeaf();
                if (leaf instanceof BlockTree bt && bt.getStatements().contains(c)
                        || leaf instanceof CaseTree ct && ct.getStatements() != null && ct.getStatements().contains(c)) {
                    chain.add(new Tree[]{leaf, c});
                }
                if (leaf instanceof MethodTree || leaf instanceof ClassTree) break;
            }
            if (chain.isEmpty()) return null;
            chains.add(chain);
        }
        for (Tree[] link : chains.get(0)) {
            Tree best = null;
            boolean everywhere = true;
            for (List<Tree[]> chain : chains) {
                Tree stmt = null;
                for (Tree[] l : chain) if (l[0] == link[0]) { stmt = l[1]; break; }
                if (stmt == null) { everywhere = false; break; }
                if (best == null || u.start(stmt) < u.start(best)) best = stmt;
            }
            if (everywhere) return TreePath.getPath(u.cu, best);
        }
        return null;
    }

    /** A name like IntelliJ's: the call's name, the field's, the array's singular, else from the
        type; then 1, 2... until no local variable or parameter of the method has it. */
    String freeName(Unit u, TreePath target, TypeMirror tm, TreePath anchor, TreePath body) {
        ExpressionTree e = (ExpressionTree) target.getLeaf();
        while (e instanceof ParenthesizedTree pt) e = pt.getExpression();
        String base = null;
        if (e instanceof MethodInvocationTree) base = nameFor(tm, e).get(0);
        else if (e instanceof MemberSelectTree ms) base = ms.getIdentifier().toString();
        else if (e instanceof ArrayAccessTree aa) {
            ExpressionTree arr = aa.getExpression();
            String an = arr instanceof IdentifierTree id ? id.getName().toString()
                    : arr instanceof MemberSelectTree m2 ? m2.getIdentifier().toString() : null;
            if (an != null && an.matches(".*[^s]s$")) base = an.endsWith("ies") ? an.substring(0, an.length() - 3) + "y" : an.substring(0, an.length() - 1);
        }
        if (base == null || base.isEmpty() || !Character.isJavaIdentifierStart(base.charAt(0))) {
            List<String> byType = nameFor(tm, null);
            base = byType.get(byType.size() - 1);
        }
        base = safe(base);
        Set<String> taken = new HashSet<>();
        for (Scope s = u.scope(anchor); s != null; s = s.getEnclosingScope()) {
            for (Element x : s.getLocalElements()) taken.add(x.getSimpleName().toString());
        }
        new TreePathScanner<Void, Void>() {
            @Override public Void visitVariable(VariableTree n, Void v) {
                taken.add(n.getName().toString());
                return super.visitVariable(n, v);
            }
        }.scan(body, null);
        String name = base;
        for (int i = 1; taken.contains(name); i++) name = base + i;
        return name;
    }

    /** The import lines the declared type needs: JDK classes this file does not import yet. */
    List<Map<String, Object>> importsFor(Unit u, Req r, TypeMirror tm, String type) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (r.compact() || type.equals("var")) return out;
        Set<String> imported = new HashSet<>(), star = new HashSet<>(Set.of("java.lang"));
        for (ImportTree it : u.cu.getImports()) {
            if (it.isStatic()) continue;
            String q = it.getQualifiedIdentifier().toString();
            if (q.endsWith(".*")) star.add(q.substring(0, q.length() - 2)); else imported.add(q);
        }
        if (u.cu.getPackageName() != null) star.add(u.cu.getPackageName().toString());
        Set<String> need = new LinkedHashSet<>();
        Deque<TypeMirror> todo = new ArrayDeque<>(List.of(tm));
        while (!todo.isEmpty()) {
            TypeMirror x = todo.poll();
            if (x instanceof DeclaredType d) {
                Element top = d.asElement();
                while (top.getEnclosingElement() instanceof TypeElement o) top = o;
                TypeElement te = (TypeElement) top;
                String pkg = u.el.getPackageOf(te).getQualifiedName().toString();
                String fqn = te.getQualifiedName().toString();
                if (entryOf(u, te) != null && !star.contains(pkg) && !imported.contains(fqn)) need.add(fqn);
                todo.addAll(d.getTypeArguments());
            } else if (x instanceof ArrayType at) todo.add(at.getComponentType());
            else if (x instanceof WildcardType w) {
                if (w.getExtendsBound() != null) todo.add(w.getExtendsBound());
                if (w.getSuperBound() != null) todo.add(w.getSuperBound());
            }
        }
        if (need.isEmpty()) return out;
        Map<String, Object> tmp = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        for (String fqn : need) { Map<String, Object> it = new LinkedHashMap<>(); it.put("importFqn", fqn); items.add(it); }
        tmp.put("items", items);
        importPlace(u, r, tmp);
        for (Map<String, Object> it : items) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("text", it.get("import"));
            m.put("at", tmp.get("importAt"));
            out.add(m);
        }
        return out;
    }

    /* ------------------------------------------------------------------ format (Ctrl+Alt+L) */

    /* IntelliJ's default Java style (5 Oct, Ravi's choice "keep my style"): indentation 4,
       continuation 8, IntelliJ's spaces and blank lines; but comments are left as written,
       2+ spaces after a comma stay (his test-case columns), and lines are never split or
       joined. Checked against IntelliJ's own command-line formatter run with those settings
       (tools/test_codeview.py FormatTest has the rules it showed). */

    /** A token: w word, n number, s string/char/text block, l // comment, b block comment, o operator. */
    record Tok(char kind, int start, int end) {}

    /** Positions javac's tree gives meaning to: which < > are generics, which ) ends a cast... */
    static final class Marks {
        final Set<Integer> genOpen = new HashSet<>(), genOpenSpaced = new HashSet<>(), genClose = new HashSet<>();
        final Set<Integer> genCloseTight = new HashSet<>();      // a method call's own type arguments: no space after
        final Set<Integer> castClose = new HashSet<>(), spacedColon = new HashSet<>(), ternary = new HashSet<>();
        final Set<Integer> arrayBrace = new HashSet<>(), prefix = new HashSet<>(), postfix = new HashSet<>();
        final Set<Integer> switchBrace = new HashSet<>(), colonCase = new HashSet<>();
        final Set<Integer> alignParen = new HashSet<>();          // ( whose wrapped content lines up under its first item
        final Set<Integer> enumBrace = new HashSet<>();           // an enum's body: a comma ends a constant
        final Map<Integer, Long> braceOwner = new HashMap<>();   // a block's { -> where its statement starts
    }

    static final Set<String> CONTROL = Set.of("if", "for", "while", "switch", "catch", "synchronized", "try", "return",
            "throw", "case", "assert", "yield", "else", "do");
    static final Set<String> BINARY = Set.of("=", "==", "!=", "<", ">", "<=", ">=", "+", "-", "*", "/", "%", "&&", "||", "&",
            "|", "^", "<<", ">>", ">>>", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>=", "->");
    static final String[] OPS = {">>>=", "<<=", ">>=", ">>>", "...", "->", "::", "++", "--", "&&", "||", "==", "!=", "<=",
            ">=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<", ">>"};

    Map<String, Object> format(Req r) throws IOException {
        Map<String, Object> res = new LinkedHashMap<>();
        String t = r.text;
        fm.setLocation(StandardLocation.SOURCE_PATH, List.of());
        List<Diagnostic<? extends JavaFileObject>> diags = new ArrayList<>();
        Source file = new Source(r.fileName.isEmpty() ? "Scratch.java" : r.fileName, t);
        JavacTask task = (JavacTask) jc.getTask(Writer.nullWriter(), fm, diags::add, List.of("-proc:none"), null, List.of(file));
        CompilationUnitTree cu = task.parse().iterator().next();
        for (Diagnostic<? extends JavaFileObject> d : diags) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                return reject(res, "Fix the syntax error on line " + d.getLineNumber() + " first (the red underline), then format");
            }
        }
        SourcePositions sp = Trees.instance(task).getSourcePositions();
        Marks mk = marks(cu, sp, t);
        List<Tok> toks = lexJava(t, mk.genClose);
        res.put("text", layout(t, toks, mk, cu, sp));
        return res;
    }

    /* ---- tree marks */

    Marks marks(CompilationUnitTree cu, SourcePositions sp, String t) {
        Marks mk = new Marks();
        new TreeScanner<Void, Void>() {
            long s(Tree x) { return sp.getStartPosition(cu, x); }
            long e(Tree x) { return sp.getEndPosition(cu, x); }
            int after(long from, char c) { int i = t.indexOf(c, (int) Math.max(0, from)); return i; }
            int before(long from, char c) { return t.lastIndexOf(c, (int) from - 1); }
            void typeParams(List<? extends Tree> ps, boolean spaced) {
                if (ps.isEmpty()) return;
                int open = before(s(ps.get(0)), '<'), close = after(e(ps.get(ps.size() - 1)), '>');
                if (open >= 0) (spaced ? mk.genOpenSpaced : mk.genOpen).add(open);
                if (close >= 0) mk.genClose.add(close);
            }
            void typeArgs(List<? extends Tree> as, long fallbackFrom) {      // Collections.<Integer>emptyList()
                if (as.isEmpty()) return;
                int open = before(s(as.get(0)), '<'), close = after(e(as.get(as.size() - 1)), '>');
                if (open >= 0) mk.genOpen.add(open);
                if (close >= 0) { mk.genClose.add(close); mk.genCloseTight.add(close); }
            }
            @Override public Void visitParameterizedType(ParameterizedTypeTree n, Void v) {
                int open = after(e(n.getType()), '<');
                long end = e(n);
                if (open >= 0 && end > open) { mk.genOpen.add(open); mk.genClose.add((int) end - 1); }
                return super.visitParameterizedType(n, v);
            }
            @Override public Void visitClass(ClassTree n, Void v) {
                typeParams(n.getTypeParameters(), false);
                long bodyFrom = n.getTypeParameters().isEmpty() ? s(n) : e(n.getTypeParameters().get(n.getTypeParameters().size() - 1));
                for (Tree x : List.of(n.getExtendsClause() == null ? n : n.getExtendsClause())) bodyFrom = Math.max(bodyFrom, x == n ? bodyFrom : e(x));
                for (Tree x : n.getImplementsClause()) bodyFrom = Math.max(bodyFrom, e(x));
                int brace = after(bodyFrom, '{');
                if (brace >= 0 && brace < e(n)) mk.braceOwner.put(brace, s(n));
                if (brace >= 0 && n.getKind() == Tree.Kind.ENUM) mk.enumBrace.add(brace);
                return super.visitClass(n, v);
            }
            @Override public Void visitMethod(MethodTree n, Void v) {
                typeParams(n.getTypeParameters(), true);
                if (n.getBody() != null) mk.braceOwner.put((int) s(n.getBody()), s(n));
                // IntelliJ aligns wrapped parameters under the first one
                if (!n.getParameters().isEmpty()) { int p = before(s(n.getParameters().get(0)), '('); if (p >= 0) mk.alignParen.add(p); }
                return super.visitMethod(n, v);
            }
            @Override public Void visitMethodInvocation(MethodInvocationTree n, Void v) {
                typeArgs(n.getTypeArguments(), s(n));
                return super.visitMethodInvocation(n, v);
            }
            @Override public Void visitTypeCast(TypeCastTree n, Void v) {
                int close = after(e(n.getType()), ')');
                if (close >= 0) mk.castClose.add(close);
                return super.visitTypeCast(n, v);
            }
            @Override public Void visitConditionalExpression(ConditionalExpressionTree n, Void v) {
                int q = after(e(n.getCondition()), '?'), c = after(e(n.getTrueExpression()), ':');
                if (q >= 0) mk.ternary.add(q);
                if (c >= 0) { mk.ternary.add(c); mk.spacedColon.add(c); }
                return super.visitConditionalExpression(n, v);
            }
            @Override public Void visitEnhancedForLoop(EnhancedForLoopTree n, Void v) {
                int c = after(e(n.getVariable()), ':');
                if (c >= 0) mk.spacedColon.add(c);
                own(n.getStatement(), s(n));
                return super.visitEnhancedForLoop(n, v);
            }
            @Override public Void visitAssert(AssertTree n, Void v) {
                if (n.getDetail() != null) { int c = after(e(n.getCondition()), ':'); if (c >= 0) mk.spacedColon.add(c); }
                return super.visitAssert(n, v);
            }
            @Override public Void visitNewArray(NewArrayTree n, Void v) {
                if (n.getInitializers() != null) {
                    long from = n.getDimensions().isEmpty() ? s(n) : e(n.getDimensions().get(n.getDimensions().size() - 1));
                    int open = n.getType() == null ? (int) s(n) : after(from, '{');
                    if (open >= 0) mk.arrayBrace.add(open);
                    mk.arrayBrace.add((int) e(n) - 1);
                    if (open >= 0) mk.braceOwner.put(open, s(n));
                }
                return super.visitNewArray(n, v);
            }
            @Override public Void visitLiteral(LiteralTree n, Void v) {    // javac reads -1 as one literal
                long st = s(n);
                if (st >= 0 && st < t.length() && t.charAt((int) st) == '-') mk.prefix.add((int) st);
                return super.visitLiteral(n, v);
            }
            @Override public Void visitUnary(UnaryTree n, Void v) {
                switch (n.getKind()) {
                    case POSTFIX_INCREMENT, POSTFIX_DECREMENT -> mk.postfix.add((int) e(n) - 2);
                    default -> mk.prefix.add((int) s(n));
                }
                return super.visitUnary(n, v);
            }
            @Override public Void visitSwitch(SwitchTree n, Void v) {
                int b = after(e(n.getExpression()), '{');
                if (b >= 0) { mk.switchBrace.add(b); mk.braceOwner.put(b, s(n)); }
                cases(n.getCases());
                return super.visitSwitch(n, v);
            }
            @Override public Void visitSwitchExpression(SwitchExpressionTree n, Void v) {
                int b = after(e(n.getExpression()), '{');
                if (b >= 0) { mk.switchBrace.add(b); mk.braceOwner.put(b, s(n)); }
                cases(n.getCases());
                return super.visitSwitchExpression(n, v);
            }
            void cases(List<? extends CaseTree> cs) {
                for (CaseTree c : cs) {
                    if (c.getCaseKind() == CaseTree.CaseKind.STATEMENT) mk.colonCase.add((int) s(c));
                    if (c.getBody() instanceof BlockTree b) mk.braceOwner.put((int) s(b), s(c));
                }
            }
            /* a block's content is indented from where its statement starts (if, for, lambda...) */
            void own(Tree body, long start) { if (body instanceof BlockTree b) mk.braceOwner.put((int) s(b), start); }
            @Override public Void visitIf(IfTree n, Void v) { own(n.getThenStatement(), s(n)); return super.visitIf(n, v); }
            @Override public Void visitForLoop(ForLoopTree n, Void v) {
                own(n.getStatement(), s(n));
                int p = after(s(n), '(');                                // IntelliJ aligns a wrapped for header
                if (p >= 0) mk.alignParen.add(p);
                return super.visitForLoop(n, v);
            }
            @Override public Void visitWhileLoop(WhileLoopTree n, Void v) { own(n.getStatement(), s(n)); return super.visitWhileLoop(n, v); }
            @Override public Void visitDoWhileLoop(DoWhileLoopTree n, Void v) { own(n.getStatement(), s(n)); return super.visitDoWhileLoop(n, v); }
            @Override public Void visitTry(TryTree n, Void v) {
                own(n.getBlock(), s(n));
                if (!n.getResources().isEmpty()) { int p = before(s(n.getResources().get(0)), '('); if (p >= 0) mk.alignParen.add(p); }
                return super.visitTry(n, v);
            }
            @Override public Void visitCatch(CatchTree n, Void v) { own(n.getBlock(), s(n)); return super.visitCatch(n, v); }
            @Override public Void visitSynchronized(SynchronizedTree n, Void v) { own(n.getBlock(), s(n)); return super.visitSynchronized(n, v); }
            @Override public Void visitLambdaExpression(LambdaExpressionTree n, Void v) { own(n.getBody(), s(n)); return super.visitLambdaExpression(n, v); }
        }.scan(cu, null);
        return mk;
    }

    /* ---- lexer */

    static List<Tok> lexJava(String t, Set<Integer> genClose) {
        List<Tok> out = new ArrayList<>();
        int i = 0, n = t.length();
        while (i < n) {
            char c = t.charAt(i);
            if (c == ' ' || c == '\t' || c == '\f' || c == '\r' || c == '\n') { i++; continue; }
            int j;
            if (c == '/' && i + 1 < n && t.charAt(i + 1) == '/') {
                j = i;
                while (j < n && t.charAt(j) != '\n' && t.charAt(j) != '\r') j++;
                out.add(new Tok('l', i, j));
            } else if (c == '/' && i + 1 < n && t.charAt(i + 1) == '*') {
                int close = t.indexOf("*/", i + 2);
                j = close < 0 ? n : close + 2;
                out.add(new Tok('b', i, j));
            } else if (t.startsWith("\"\"\"", i)) {
                j = i + 3;
                while (j < n && !(t.startsWith("\"\"\"", j) && t.charAt(j - 1) != '\\')) j++;
                j = Math.min(n, j + 3);
                out.add(new Tok('s', i, j));
            } else if (c == '"' || c == '\'') {
                j = i + 1;
                while (j < n && t.charAt(j) != c && t.charAt(j) != '\n') j += t.charAt(j) == '\\' ? 2 : 1;
                j = Math.min(n, j + 1);
                out.add(new Tok('s', i, j));
            } else if (Character.isDigit(c) || c == '.' && i + 1 < n && Character.isDigit(t.charAt(i + 1))) {
                j = i;
                while (j < n) {
                    char d = t.charAt(j);
                    if (Character.isLetterOrDigit(d) || d == '_' || d == '.' && !t.startsWith("..", j)) j++;
                    else if ((d == '+' || d == '-') && j > i && "eEpP".indexOf(t.charAt(j - 1)) >= 0 && !t.startsWith("0x", i) && !t.startsWith("0X", i)) j++;
                    else break;
                }
                out.add(new Tok('n', i, j));
            } else if (Character.isJavaIdentifierStart(c)) {
                j = i + 1;
                while (j < n && Character.isJavaIdentifierPart(t.charAt(j))) j++;
                out.add(new Tok('w', i, j));
            } else {
                j = i + 1;
                if (!(c == '>' && genClose.contains(i))) {
                    for (String op : OPS) if (t.startsWith(op, i)) { j = i + op.length(); break; }
                    // a generic close inside >> or >>> is its own token
                    for (int k = i + 1; k < j; k++) if (t.charAt(k) == '>' && genClose.contains(k)) { j = k; break; }
                }
                out.add(new Tok('o', i, j));
            }
            i = j;
        }
        return out;
    }

    /* ---- layout */

    /** One open bracket while laying out lines. */
    static final class Open {
        final String ch;
        final int indent;          // where its content lines start
        final int close;           // where a line starting with its closer goes
        final boolean array, swtch;
        boolean caseSeen, aligned, enumBody;
        int argFrom = -1;          // a wrapped argument's first line's indent: its next line goes 8 past it
        int start;                 // the bracket's position in the text
        Ctx saved;                 // a block: the statement state outside it
        Open(String ch, int indent, int close, boolean array, boolean swtch) {
            this.ch = ch; this.indent = indent; this.close = close; this.array = array; this.swtch = swtch;
        }
    }

    String layout(String t, List<Tok> toks, Marks mk, CompilationUnitTree cu, SourcePositions sp) {
        String eol = t.contains("\r\n") ? "\r\n" : "\n";
        List<Integer> starts = new ArrayList<>(List.of(0));
        for (int i = 0; i < t.length(); i++) if (t.charAt(i) == '\n') starts.add(i + 1);
        int nl = starts.size();
        String[] lines = new String[nl];
        for (int i = 0; i < nl; i++) {
            int a = starts.get(i), b = i + 1 < nl ? starts.get(i + 1) - 1 : t.length();
            String s = t.substring(a, b);
            lines[i] = s.endsWith("\r") ? s.substring(0, s.length() - 1) : s;
        }
        // tokens by line; a token reaching past its line (block comment, text block) covers the next ones
        List<List<Tok>> byLine = new ArrayList<>();
        for (int i = 0; i < nl; i++) byLine.add(new ArrayList<>());
        int[] coveredBy = new int[nl];
        Arrays.fill(coveredBy, -1);
        for (int k = 0; k < toks.size(); k++) {
            Tok tk = toks.get(k);
            int line = lineOf(starts, tk.start);
            byLine.get(line).add(tk);
            int last = lineOf(starts, Math.max(tk.start, tk.end - 1));
            for (int x = line + 1; x <= last; x++) coveredBy[x] = k;
        }
        // A bracket "wraps" when one of its own items starts a later line. IntelliJ indents a
        // bracket's content 8 past the nearest enclosing bracket that wraps (else past the line):
        // print("x", find(6,\n...\n"y") puts find's next line 8 deeper than print's items.
        // A call whose ) is followed by .next() on a later line is part of a wrapped chain:
        // IntelliJ puts its arguments 8 past the chain's lines, and its ) on the chain's level.
        Set<Integer> wrapped = new HashSet<>(), chainParen = new HashSet<>();
        {
            Deque<Tok> open = new ArrayDeque<>();
            int prevLine = -1;
            for (int k = 0; k < toks.size(); k++) {
                Tok tk = toks.get(k);
                int line = lineOf(starts, tk.start);
                String s = tk.kind == 'o' ? text(t, tk) : "";
                if (line != prevLine && !open.isEmpty() && coveredBy[line] < 0) {
                    Tok owner = open.peek();
                    if (lineOf(starts, owner.start) < line) wrapped.add(owner.start);
                }
                prevLine = line;
                if (s.equals("(") || s.equals("[") || s.equals("{")) open.push(tk);
                else if ((s.equals(")") || s.equals("]") || s.equals("}")) && !open.isEmpty()) {
                    Tok o = open.pop();
                    if (s.equals(")") && k + 1 < toks.size() && text(t, toks.get(k + 1)).equals(".")
                            && lineOf(starts, toks.get(k + 1).start) > line && lineOf(starts, o.start) < line) chainParen.add(o.start);
                }
            }
        }
        String[] out = new String[nl];
        int[] shift = new int[toks.size()];
        Deque<Open> stack = new ArrayDeque<>();
        Ctx ctx = new Ctx();
        Map<Integer, Integer> lineIndent = new HashMap<>();
        for (int i = 0; i < nl; i++) {
            boolean verbatim = false;
            if (coveredBy[i] >= 0) {                                   // inside a block comment or text block
                Tok tk = toks.get(coveredBy[i]);
                out[i] = tk.kind == 'b' ? shifted(lines[i], shift[coveredBy[i]]) : lines[i];
                if (byLine.get(i).isEmpty()) continue;
                // code after it on its last line (the ; after a text block): left as written, but
                // it still ends the statement
                out[i] = lines[i].stripTrailing();
                verbatim = true;
            }
            List<Tok> lt = byLine.get(i);
            if (lt.isEmpty()) { out[i] = ""; continue; }
            Tok first = lt.get(0);
            String ft = text(t, first);
            boolean commentOnly = lt.stream().allMatch(x -> x.kind == 'l' || x.kind == 'b');
            int oldIndent = first.start - starts.get(i);
            int indent;
            Open top = stack.peek();
            boolean closer = ft.equals("}") || ft.equals(")") || ft.equals("]");
            boolean caseLine = ft.equals("case") || ft.equals("default") && lt.size() > 1
                    && (text(t, lt.get(1)).equals(":") || text(t, lt.get(1)).equals("->"));
            boolean colonCase = mk.colonCase.contains(first.start);          // only `case x:` indents what follows
            if (closer && top != null) {
                indent = top.close;
            } else if (top != null && !top.ch.equals("{")) {
                indent = !top.aligned && top.argFrom >= 0 ? Math.max(top.indent, top.argFrom + 8) : top.indent;
            } else if (top != null && top.array) {
                indent = top.indent;
            } else {
                int base = top == null ? 0 : top.indent;
                if (top != null && top.swtch && top.caseSeen && !caseLine) base += 4;
                if (ctx.pendingBody >= 0 && !ctx.cont) indent = ctx.pendingBody;
                else if (ctx.cont) indent = (ctx.afterAssign || ctx.exprFrom < 0 ? ctx.stmtIndent : ctx.exprFrom) + 8;
                else indent = base;
            }
            if (top != null && top.swtch && caseLine && !closer) indent = top.indent;
            if (commentOnly && oldIndent == 0) indent = 0;              // IntelliJ keeps first-column comments
            indent = Math.max(0, indent);
            Map<Integer, Integer> cols = new HashMap<>();
            if (verbatim) {
                indent = lines[i].length() - lines[i].stripLeading().length();
            } else {
                if (first.kind == 'b') shift[toks.indexOf(first)] = indent - oldIndent;
                out[i] = " ".repeat(indent) + joinLine(t, lt, mk, lines[i], starts.get(i), cols, indent);
            }
            lineIndent.put(i, indent);
            if (commentOnly) continue;
            // the state after this line
            boolean newStmt = !ctx.cont && (top == null || top.ch.equals("{") && !top.array);
            if (newStmt) { ctx.stmtIndent = indent; ctx.exprFrom = -1; ctx.afterAssign = false; }
            else if (ctx.cont && ctx.afterAssign && (top == null || top.ch.equals("{") && !top.array)) {
                ctx.exprFrom = indent;                                  // the value after `=` starts here
                ctx.afterAssign = false;
            }
            for (int k = 0; k < lt.size(); k++) {
                Tok tk = lt.get(k);
                if (tk.kind != 'o') continue;
                String s = text(t, tk);
                switch (s) {
                    case "(", "[" -> {
                        // content: 8 past the nearest enclosing bracket that wraps, else past this
                        // line; a method's parameters (and a for or try header) line up under the first
                        boolean aligned = mk.alignParen.contains(tk.start) && k + 1 < lt.size() && lt.get(k + 1).kind != 'l'
                                && cols.containsKey(lt.get(k + 1).start);
                        boolean chain = !aligned && chainParen.contains(tk.start);
                        int at = aligned ? cols.get(lt.get(k + 1).start) : wrapBase(stack, wrapped, indent) + (chain ? 16 : 8);
                        Open o = new Open(s, at, chain ? indent + 8 : indent, false, false);
                        o.aligned = aligned;
                        o.start = tk.start;
                        stack.push(o);
                    }
                    case "{" -> {
                        boolean arr = mk.arrayBrace.contains(tk.start);
                        Long owner = mk.braceOwner.get(tk.start);
                        int ownerIndent = owner == null ? indent : lineIndent.getOrDefault(lineOf(starts, owner.intValue()), indent);
                        Open o = arr ? new Open("{", wrapBase(stack, wrapped, indent) + 8, indent, true, false)
                                     : new Open("{", ownerIndent + 4, ownerIndent, false, mk.switchBrace.contains(tk.start));
                        if (!arr) { o.saved = ctx; ctx = new Ctx(); }       // a block has statements of its own
                        o.start = tk.start;
                        o.enumBody = mk.enumBrace.contains(tk.start);
                        stack.push(o);
                    }
                    case "}", ")", "]" -> {
                        if (stack.isEmpty()) break;
                        Open o = stack.pop();
                        if (o.saved != null) ctx = o.saved;                // back to the statement around the block
                    }
                    default -> { }
                }
            }
            Open now = stack.peek();
            if (now != null && now.swtch && colonCase) now.caseSeen = true;
            Tok last = null;
            for (int k = lt.size() - 1; k >= 0; k--) if (lt.get(k).kind != 'l' && lt.get(k).kind != 'b') { last = lt.get(k); break; }
            String lastText = last == null ? "" : text(t, last);
            boolean annotationLine = ft.equals("@") && !lastText.equals(";") && !lastText.equals("{") && (now == null || now.ch.equals("{"));
            boolean label = lt.size() == 2 && first.kind == 'w' && lastText.equals(":");
            boolean ended = lastText.equals(";") || lastText.equals("{") || lastText.equals("}") || caseLine && lastText.equals(":")
                    || annotationLine || label || lastText.equals("->") && caseLine
                    || lastText.equals(",") && now != null && now.enumBody;           // MONDAY(1) {...},
            boolean insideParen = now != null && !now.ch.equals("{");
            if (insideParen) {
                // inside ( ): a line not ending at a comma leaves its argument unfinished
                if (lastText.equals(",") || lastText.equals("(") || lastText.equals("[")) now.argFrom = -1;
                else if (now.argFrom < 0) now.argFrom = indent;
                continue;
            }
            if (now != null && now.array) continue;
            if (ended) {
                ctx.cont = false;
                ctx.pendingBody = -1;
            } else if (headerDone(t, lt)) {
                ctx.pendingBody = ctx.stmtIndent + 4;    // a body on the next line gets +4, not continuation +8
                ctx.cont = false;
            } else {
                ctx.cont = true;
                ctx.afterAssign = ASSIGN.contains(lastText);
            }
        }
        return blankLines(out, cu, sp, t, starts, eol);
    }

    /** What a new bracket's content goes 8 past: the nearest enclosing bracket that wraps, else this line. */
    static int wrapBase(Deque<Open> stack, Set<Integer> wrapped, int lineIndent) {
        for (Open o : stack) {                                   // innermost first
            if (o.ch.equals("{") && !o.array) break;             // a block starts afresh
            if (wrapped.contains(o.start)) return Math.max(lineIndent, o.indent);
        }
        return lineIndent;
    }

    static final Set<String> ASSIGN = Set.of("=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>=");

    /** Where the statement being laid out stands. A block keeps the one around it to go back to. */
    static final class Ctx {
        boolean cont;              // the statement goes on to the next line
        boolean afterAssign;       // ... and that line holds the value after `=`
        int stmtIndent;            // its first line's indent
        int exprFrom = -1;         // the line where that value started (its wrapped lines go 8 past it)
        int pendingBody = -1;      // indent for an if / for / while body without braces
    }

    /* "if (...)" / "for (...)" / "while (...)" / "else" / "do" ending its line without { or ; */
    static boolean headerDone(String t, List<Tok> lt) {
        List<String> words = new ArrayList<>();
        for (Tok tk : lt) if (tk.kind != 'l' && tk.kind != 'b') words.add(text(t, tk));
        if (words.isEmpty()) return false;
        int k = words.get(0).equals("}") ? 1 : 0;
        if (k >= words.size()) return false;
        String w = words.get(k), last = words.get(words.size() - 1);
        if ((w.equals("else") || w.equals("do") || w.equals("try") || w.equals("finally")) && words.size() == k + 1) return true;
        if (w.equals("else") && k + 1 < words.size() && words.get(k + 1).equals("if")) w = "if";
        return (w.equals("if") || w.equals("for") || w.equals("while")) && last.equals(")");
    }

    static String shifted(String line, int by) {
        if (by == 0) return line.stripTrailing();
        int lead = 0;
        while (lead < line.length() && line.charAt(lead) == ' ') lead++;
        int now = Math.max(0, lead + by);
        return (" ".repeat(now) + line.substring(lead)).stripTrailing();
    }

    static int lineOf(List<Integer> starts, int pos) {
        int lo = 0, hi = starts.size() - 1;
        while (lo < hi) {
            int mid = (lo + hi + 1) / 2;
            if (starts.get(mid) <= pos) lo = mid; else hi = mid - 1;
        }
        return lo;
    }

    static String text(String t, Tok tk) { return t.substring(tk.start, tk.end); }

    /** One line's tokens with IntelliJ's spaces between them; cols gets each token's column. */
    String joinLine(String t, List<Tok> lt, Marks mk, String line, int lineStart, Map<Integer, Integer> cols, int indent) {
        StringBuilder b = new StringBuilder();
        for (int k = 0; k < lt.size(); k++) {
            Tok tk = lt.get(k);
            String s = tk.end > lineStart + line.length() ? line.substring(tk.start - lineStart) : text(t, tk);
            if (k > 0) {
                Tok p = lt.get(k - 1);
                b.append(gap(t, p, tk, mk, t.substring(Math.min(p.end, tk.start), tk.start)));
            }
            cols.put(tk.start, indent + b.length());
            b.append(s);
        }
        return b.toString().stripTrailing();
    }

    static String gap(String t, Tok p, Tok n, Marks mk, String orig) {
        String pt = text(t, p), nt = text(t, n);
        char pk = p.kind, nk = n.kind;
        boolean pWord = pk == 'w' || pk == 'n' || pk == 's', nWord = nk == 'w' || nk == 'n' || nk == 's';
        if (nk == 'l' || nk == 'b') return orig.isEmpty() ? " " : orig;        // comments keep their place
        if (pk == 'b') return orig.isEmpty() ? "" : " ";
        if (pt.equals(",")) return orig.length() >= 2 && orig.isBlank() ? orig : " ";   // his aligned columns
        if (mk.genOpen.contains(p.start) || mk.genOpenSpaced.contains(p.start)) return "";
        if (mk.genOpenSpaced.contains(n.start)) return " ";
        if (mk.genOpen.contains(n.start)) return "";
        if (mk.genClose.contains(n.start)) return "";
        if (mk.genClose.contains(p.start)) {
            if (mk.genCloseTight.contains(p.start)) return "";
            if (nt.equals("{") && !mk.arrayBrace.contains(n.start)) return " ";      // implements Iterator<Integer> {
            return nk == 'w' || nt.equals("@") ? " " : "";
        }
        if (pt.equals(".") || nt.equals(".") || pt.equals("::") || nt.equals("::")) return "";
        if (pt.equals("(") || pt.equals("[")) return "";
        if (pt.equals(";") && nt.equals(")")) return " ";                         // for (x = head; x != null; )
        if (nt.equals(")") || nt.equals("]") || nt.equals(",") || nt.equals(";")) return "";
        if (pt.equals(";")) return " ";
        if (nt.equals("{") && mk.arrayBrace.contains(n.start)) return pt.equals("]") || pt.equals("{") ? "" : " ";
        if (pt.equals("{") && mk.arrayBrace.contains(p.start)) return "";
        if (nt.equals("}") && mk.arrayBrace.contains(n.start)) return "";
        if (pt.equals("@")) return "";
        if (mk.prefix.contains(p.start) && pk == 'o') return "";
        if (mk.postfix.contains(n.start)) return "";
        if (pt.equals("!") || pt.equals("~")) return "";
        if (nt.equals("...")) return "";
        if (pt.equals("...")) return " ";
        if (nt.equals(":")) return mk.spacedColon.contains(n.start) ? " " : "";
        if (pt.equals(":")) return " ";
        if (nt.equals("?")) return mk.ternary.contains(n.start) ? " " : "";
        if (pt.equals("?")) return mk.ternary.contains(p.start) || nk == 'w' ? " " : "";
        if (nt.equals("(")) {
            if (mk.castClose.contains(p.start)) return " ";                  // (double) (a - b)
            if (pk == 'w') return CONTROL.contains(pt) ? " " : "";
            if (pt.equals(")") || pt.equals("]")) return "";
            return BINARY.contains(pt) ? " " : "";
        }
        if (pt.equals(")")) {
            if (mk.castClose.contains(p.start)) return " ";
            if (nWord || nt.equals("{") || BINARY.contains(nt) || nt.equals("@")) return " ";
            return "";
        }
        if (pt.equals("{") && nt.equals("}")) return "";
        if (nt.equals("{") || pt.equals("{") || nt.equals("}")) return " ";
        if (pt.equals("}")) return nk == 'w' ? " " : "";
        if (nt.equals("[")) return "";
        if (pt.equals("]")) return nWord || BINARY.contains(nt) || nt.equals("@") ? " " : "";
        if (BINARY.contains(pt) || BINARY.contains(nt)) return " ";
        if (pWord && nWord) return " ";
        if (pt.equals("++") || pt.equals("--")) return nWord ? " " : "";
        return orig.isEmpty() ? "" : " ";
    }

    /* ---- blank lines: at most 2 in a row; 1 before and after imports, after package, and
       around methods and classes (IntelliJ's minimums), counting comments as part of what follows */

    String blankLines(String[] out, CompilationUnitTree cu, SourcePositions sp, String t, List<Integer> starts, String eol) {
        int n = out.length;
        boolean[] blankBefore = new boolean[n];
        java.util.function.IntPredicate blank = i -> i >= 0 && i < n && out[i].isBlank();
        List<? extends ImportTree> imports = cu.getImports();
        if (cu.getPackage() != null) {
            int pl = lineOf(starts, (int) sp.getEndPosition(cu, cu.getPackage()));
            if (pl + 1 < n && !blank.test(pl + 1)) blankBefore[pl + 1] = true;
        }
        if (!imports.isEmpty()) {
            int fi = lineOf(starts, (int) sp.getStartPosition(cu, imports.get(0)));
            int li = lineOf(starts, (int) sp.getEndPosition(cu, imports.get(imports.size() - 1)));
            if (fi > 0 && !blank.test(fi - 1)) blankBefore[fi] = true;
            if (li + 1 < n && !blank.test(li + 1)) blankBefore[li + 1] = true;
            // IntelliJ's import groups: everything else, then java/javax, then static; a blank between
            for (int k = 1; k < imports.size(); k++) {
                int a = lineOf(starts, (int) sp.getStartPosition(cu, imports.get(k - 1)));
                int b = lineOf(starts, (int) sp.getStartPosition(cu, imports.get(k)));
                if (b == a + 1 && importGroup(imports.get(k - 1)) != importGroup(imports.get(k))) blankBefore[b] = true;
            }
        }
        new TreeScanner<Void, Void>() {
            void members(List<? extends Tree> ms) {
                Tree prev = null;
                for (Tree m : ms) {
                    if (sp.getStartPosition(cu, m) < 0 || sp.getEndPosition(cu, m) < 0) continue;   // javac's default constructor
                    if (prev != null && (big(prev) || big(m))) {
                        int pe = lineOf(starts, (int) sp.getEndPosition(cu, prev) - 1);
                        int vs = lineOf(starts, (int) sp.getStartPosition(cu, m));
                        // its /** */ and annotations belong to it, and so do // lines right above
                        // it; but a // line above a /** */ does not (IntelliJ puts a blank between)
                        boolean doc = false;
                        while (vs - 1 > pe) {
                            String s = out[vs - 1].strip();
                            if (s.isEmpty()) break;
                            if (s.startsWith("/*") || s.startsWith("*") || s.endsWith("*/") || s.startsWith("@")) { doc |= !s.startsWith("@"); vs--; }
                            else if (s.startsWith("//") && !doc) vs--;
                            else break;
                        }
                        if (vs > pe && !blank.test(vs - 1)) blankBefore[vs] = true;
                        if (vs - 1 > pe && !blank.test(pe + 1)) blankBefore[pe + 1] = true;   // the // lines are apart from both
                    }
                    prev = m;
                }
            }
            boolean big(Tree m) { return m instanceof MethodTree || m instanceof ClassTree; }
            @Override public Void visitCompilationUnit(CompilationUnitTree c, Void v) { members(c.getTypeDecls()); return super.visitCompilationUnit(c, v); }
            @Override public Void visitClass(ClassTree c, Void v) { members(c.getMembers()); return super.visitClass(c, v); }
        }.scan(cu, null);
        StringBuilder b = new StringBuilder();
        int run = 0;
        boolean started = false;
        for (int i = 0; i < n; i++) {
            if (!started && out[i].isBlank() && i < n - 1) continue;      // no blank lines at the top of a file
            started = true;
            if (blankBefore[i]) { if (run == 0 && b.length() > 0) b.append(eol); run = 1; }
            if (out[i].isBlank()) {
                if (++run > 2 || i == n - 1 && n > 1) { if (i == n - 1) b.append(""); continue; }
                b.append(i == n - 1 ? "" : eol);
                continue;
            }
            run = 0;
            b.append(out[i]);
            if (i < n - 1) b.append(eol);
        }
        return b.toString();
    }

    static int importGroup(ImportTree it) {
        String q = it.getQualifiedIdentifier().toString();
        return it.isStatic() ? 2 : q.startsWith("java.") || q.startsWith("javax.") ? 1 : 0;
    }

    static boolean isComment(String line) {
        String s = line.strip();
        return s.startsWith("//") || s.startsWith("/*") || s.startsWith("*") || s.startsWith("@");
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
        for (String i : List.of("java.util.List", "java.util.Map", "java.util.Set", "java.util.Deque", "java.util.Queue", "java.util.Collection")) {
            TypeElement te = u.el.getTypeElement(i);
            if (te != null) implementations(u, te);
        }
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
