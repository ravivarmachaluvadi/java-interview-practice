"""
javasrc - just enough Java source analysis for runjava and codeview.

    analyse(src)            -> package, classes declaring main(), public type, compact-file flag
    pick_main(mains, name)  -> which of those classes to run for a file called `name`
    practice_skeleton(src)  -> the file with every solution method body replaced by a stub
    practice_hints(src)     -> the header notes practice mode hides, gentlest first

No parser: comments and literals are blanked out first, then braces are counted.
That is reliable on this repo's 580 files (see tools/codeview's practice check)
but is not a general Java parser.
"""
import pathlib
import re
import textwrap

TYPE_DECL = re.compile(
    r"^[ \t]*((?:(?:public|protected|private|final|abstract|sealed|non-sealed|static|strictfp)\s+)*)"
    r"(class|enum|record|interface)\s+(\w+)", re.M)
MAIN_DECL = re.compile(r"\bvoid\s+main\s*\(")
PACKAGE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.M)


def strip_noise(src):
    """Blank out comments, strings, chars and text blocks (same length, newlines kept),
    so braces and words inside them do not confuse brace counting."""
    out, i, n = list(src), 0, len(src)

    def blank(a, b):
        for k in range(a, b):
            if out[k] != "\n":
                out[k] = " "

    while i < n:
        if src.startswith("//", i):
            j = src.find("\n", i)
            j = n if j == -1 else j
        elif src.startswith("/*", i):
            j = src.find("*/", i + 2)
            j = n if j == -1 else j + 2
        elif src.startswith('"""', i):
            j = src.find('"""', i + 3)
            while j != -1 and src[j - 1] == "\\":
                j = src.find('"""', j + 1)
            j = n if j == -1 else j + 3
        elif src[i] in "\"'":
            q, j = src[i], i + 1
            while j < n and src[j] != q and src[j] != "\n":
                j += 2 if src[j] == "\\" else 1
            j = min(j + 1, n)
        else:
            i += 1
            continue
        blank(i, j)
        i = j
    return "".join(out)


def depths(s):
    """depth[i] = brace depth just before character i."""
    depth, d = [0] * (len(s) + 1), 0
    for i, ch in enumerate(s):
        depth[i] = d
        if ch == "{":
            d += 1
        elif ch == "}":
            d -= 1
    depth[len(s)] = d
    return depth


def analyse(src):
    """-> (package, top-level classes declaring main(), public top-level type, is compact file).

    A JDK 25 compact source file has `void main()` with no class around it; javac
    names the class after the file. Instance `void main()` inside a class also counts."""
    s = strip_noise(src)
    pm = PACKAGE.search(s)
    pkg = pm.group(1) if pm else ""
    depth = depths(s)
    mains, public = [], None
    for m in TYPE_DECL.finditer(s):
        if depth[m.start(3)] != 0:
            continue
        if "public" in m.group(1).split():
            public = public or m.group(3)
        start = s.find("{", m.end())
        if start == -1:
            continue
        end = start + 1
        while end < len(s) and not (s[end] == "}" and depth[end] == 1):
            end += 1
        if any(depth[mm.start()] == 1 for mm in MAIN_DECL.finditer(s, start, end)):
            mains.append(m.group(3))
    compact = not mains and any(depth[mm.start()] == 0 for mm in MAIN_DECL.finditer(s))
    return pkg, mains, public, compact


def pick_main(mains, fname):
    """Prefer the class the file is named after (A01_TwoSum.java -> TwoSum), else the first."""
    stem = re.sub(r"^[A-D]\d\d_", "", pathlib.Path(fname).stem)
    for m in mains:
        if m == stem:
            return m
    return mains[0]


# ---------------------------------------------------------------- practice mode

METHOD = re.compile(
    r"^[ \t]*(?:@\w+(?:\([^)]*\))?\s+)*"
    r"((?:(?:public|protected|private|static|final|synchronized|abstract|native|default|strictfp)\s+)*)"
    r"(?:<[^>{};]*>\s+)?"
    r"([\w$][\w$.<>\[\],? ]*?)\s+([\w$]+)\s*\(([^;{}]*?)\)\s*(?:throws\s+[\w$.,\s]+?)?\s*\{", re.M)
NOT_TYPES = {"new", "return", "else", "throw", "case", "do", "yield",
             "class", "interface", "enum", "record",
             # a modifier in the return-type slot means the regex backtracked onto a
             # constructor (`public Foo(int x) {`) - constructors are kept, not hidden
             "public", "protected", "private", "static", "final", "synchronized",
             "abstract", "native", "default", "strictfp"}
NOT_NAMES = {"if", "for", "while", "switch", "catch", "synchronized", "try", "else"}
KEEP_NAMES = {"main", "toString", "equals", "hashCode", "compareTo", "compare"}
# Test-harness helpers. Deliberately excludes build/serialize/parse: those are real problems.
KEEP_PREFIX = re.compile(r"^(print|show|check|run|test|verify|assert|expect|fmt|format|label|"
                         r"describe|render|dump|demo|display|report|log)", re.I)
# Data holders whose methods are plumbing, not the answer: TreeNode, ListNode, Pair, ...
HELPER_CLASS = re.compile(r"(Node|Pair|Edge|Point|Interval|Cell|Entry|Tuple)$")
# Header sections that give the answer away, and the ones that only state the task.
# Counted across the repo's headers on 3 Oct 2026; a section in neither list keeps
# whatever state the previous one set.
HIDDEN_SECTIONS = re.compile(r"^\s*\*\s*(APPROACH|KEY INSIGHT|COMPLEXITY|INTERVIEW FOLLOW-UPS|FOLLOW-UPS|"
                             r"SOLUTION|INTUITION|GOTCHAS|KEY DECISIONS|HOW IT WORKS|DESIGN|ANSWER|"
                             r"HOW TO REASON ABOUT IT|FIXES APPLIED|FIXED|ROLES IN THIS CODE|WHAT TO NOTICE)\b")
SHOWN_SECTIONS = re.compile(r"^\s*\*\s*(PROBLEM|EXAMPLES?|RUN|CONSTRAINTS|INPUT|OUTPUT|QUESTION|OPTIONS|"
                            r"INTENT|WHEN TO USE|WHAT YOU WILL SEE|ROLE IN THE PROJECT|PATTERN)\b")


def brace_owners(s):
    """{index of '{': (kind, name, parent_kind, parent_name)} where kind is
    'class' (a type body), 'anon' (anonymous class body) or 'block'."""
    owners, stack, seg_start = {}, [("file", "")], 0
    for i, ch in enumerate(s):
        if ch == "{":
            seg = s[seg_start:i]
            m = re.search(r"\b(?:class|interface|enum|record)\s+([\w$]+)[^;{}]*$", seg)
            if m:
                kind, name = "class", m.group(1)
            elif re.search(r"\bnew\s+[\w$.<>\[\], ?]+\([^;{}]*\)\s*$", seg):
                kind, name = "anon", ""
            else:
                kind, name = "block", ""
            owners[i] = (kind, name) + stack[-1]
            stack.append((kind, name))
            seg_start = i + 1
        elif ch == "}":
            if len(stack) > 1:
                stack.pop()
            seg_start = i + 1
        elif ch == ";":
            seg_start = i + 1
    return owners


def stub_for(ret):
    ret = ret.strip()
    if ret == "void":
        return ""
    if ret == "boolean":
        return "return false;"
    if ret in ("int", "long", "short", "byte", "char", "float", "double"):
        return "return 0;"
    return "return null;"


def practice_skeleton(src):
    """-> (skeleton text, [hidden method names]).

    Hides the body of every method that looks like the answer, keeping main(), the
    print/check helpers that produce 'expected' lines, constructors, and helper
    classes such as ListNode. Also hides the APPROACH / KEY INSIGHT / COMPLEXITY
    sections of the header comment."""
    s = strip_noise(src)
    depth = depths(s)
    owners = brace_owners(s)
    cuts, hidden = [], []
    for m in METHOD.finditer(s):
        ret, name = m.group(2).strip(), m.group(3)
        if ret in NOT_TYPES or name in NOT_NAMES or ret.split()[-1] in NOT_TYPES:
            continue
        b = m.end() - 1                      # the body's '{'
        kind, cname, pkind, pname = owners.get(b, ("block", "", "block", ""))
        if pkind != "class" or name == pname:   # direct members of a named type; not constructors
            continue
        d = depth[b]
        e = b + 1
        while e < len(s) and not (s[e] == "}" and depth[e] == d + 1):
            e += 1
        if any(a <= b < z for a, z, _ in cuts):
            continue
        original = src[m.start():e]
        if (name in KEEP_NAMES or KEEP_PREFIX.match(name) or HELPER_CLASS.search(pname)
                or "expected" in original.lower() or "abstract" in m.group(1)):
            continue
        before = src[max(0, m.start() - 160):m.start()].lower()
        if "demo only" in before or "test helper" in before:
            continue
        indent = re.match(r"[ \t]*", src[m.start():]).group(0)
        stub = stub_for(ret)
        body = (f"\n{indent}    // TODO: your solution. (Practice mode - Solution shows the original.)\n"
                + (f"{indent}    {stub}\n" if stub else "") + indent)
        cuts.append((b + 1, e, body))
        hidden.append(name)
    out = src
    for a, z, body in sorted(cuts, reverse=True):
        out = out[:a] + body + out[z:]
    return hide_hints(out), hidden


def header_lines(src):
    """-> (start, end, [(line, section title or None)]) for the first block comment, where
    the title names the hidden section the line belongs to; (None, None, []) if no header."""
    start = src.find("/*")
    end = src.find("*/", start + 2) if start != -1 else -1
    if start == -1 or end == -1 or start > 400:
        return None, None, []
    out, title = [], None
    for line in src[start:end].split("\n"):
        if HIDDEN_SECTIONS.match(line):
            title = " ".join(line.split("*", 1)[1].split())
            out.append((line, title))
            continue
        if SHOWN_SECTIONS.match(line):
            title = None
        out.append((line, title))
    return start, end, out


def hide_hints(src):
    """Drop the APPROACH / KEY INSIGHT / COMPLEXITY / FOLLOW-UPS sections of the first
    block comment, keeping PROBLEM, EXAMPLE and RUN."""
    start, end, lines = header_lines(src)
    if start is None:
        return src
    kept, noted = [], False
    for line, title in lines:
        if title is None:
            kept.append(line)
        elif not noted:
            kept.append(" *  The approach notes (APPROACH, KEY INSIGHT, COMPLEXITY, ...) are hidden in practice mode.")
            kept.append(" *  The Hint button shows them one at a time.")
            kept.append(" *")
            noted = True
    return src[:start] + "\n".join(kept) + src[end:]


# Hints come out gentlest first: the idea, then the method, then its cost. Sections in
# none of these groups (follow-ups, gotchas, fixes) come last, in file order.
HINT_ORDER = (("INTUITION", "KEY INSIGHT", "HOW TO REASON ABOUT IT", "WHAT TO NOTICE"),
              ("APPROACH", "HOW IT WORKS", "SOLUTION", "DESIGN", "KEY DECISIONS", "ANSWER", "ROLES IN THIS CODE"),
              ("COMPLEXITY",))


def practice_hints(src):
    """-> [{"title", "text"}]: exactly the header sections hide_hints drops, gentlest first."""
    sections = []
    for line, title in header_lines(src)[2]:
        if title is None:
            continue
        if HIDDEN_SECTIONS.match(line):
            sections.append({"title": title, "lines": []})
        else:
            sections[-1]["lines"].append(re.sub(r"^\s*\*", "", line).rstrip())

    def rank(s):
        key = HIDDEN_SECTIONS.match(" * " + s["title"]).group(1)
        return next((i for i, group in enumerate(HINT_ORDER) if key in group), len(HINT_ORDER))

    out = []
    for s in sorted(sections, key=rank):
        text = textwrap.dedent("\n".join(s["lines"])).strip("\n")
        out.append({"title": s["title"], "text": text})
    return out
