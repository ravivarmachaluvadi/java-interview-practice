"""
langsrc - Practice mode for Python, JavaScript and UI (.html) files, beside javasrc's Java.

    lang_of(name)                 -> "java" | "py" | "js" | "html" | None
    practice_skeleton(src, lang)  -> (the file with each answer's body replaced by a TODO,
                                      [names of the hidden functions])
    practice_hints(src, lang)     -> the header notes practice mode hides, gentlest first

The keep rules are javasrc's, so the four languages behave alike: main(), constructors and
__init__, toString / __repr__, the print and check helpers that produce the "expected" lines,
and the methods of data holders such as ListNode stay; every other function or method body is
hidden. Only functions at the top of the file and the methods of classes are candidates; a
function inside a function goes with the outer one.

  Python      read with Python's own parser (ast), so the ranges are exact. A function's
              docstring stays (it states the contract), its body becomes a TODO and `pass`.
              The `if __name__ == "__main__":` block is never touched.
  JavaScript  read the javasrc way: comments, strings, template strings and regex literals
              blanked, then braces counted. `function f(...) {`, `const f = (...) => {`,
              `const f = function (...) {` and class methods. Arrow functions without braces
              (`x => x * 2`) stay visible. Not a full JavaScript parser.
  UI (.html)  the JavaScript rule on each page <script>; markup, CSS and the
              <script type="test"> block (the file's own tests) stay. The tests live in that
              block, so in the page's own script render(), show() and print*() are answers
              and are hidden too.
"""
import ast
import io
import re
import tokenize

import javasrc

EXT = {".java": "java", ".py": "py", ".js": "js", ".mjs": "js", ".html": "html"}
STYLE = {"java": "java", "js": "java", "py": "py", "html": "html"}       # header comment style
TODO = "TODO: your solution. (Practice mode - Solution shows the original.)"
LANGS = tuple(STYLE)


def lang_of(name):
    m = re.search(r"\.[^./\\]+$", name)
    return EXT.get(m.group(0).lower()) if m else None


def practice_skeleton(src, lang):
    if lang == "java":
        return javasrc.practice_skeleton(src)
    if lang not in STYLE:
        raise ValueError(f"Practice mode does not know {lang} files")
    text, hidden = {"py": python_skeleton, "js": js_skeleton, "html": html_skeleton}[lang](src)
    return javasrc.hide_hints(text, STYLE[lang]), hidden


def practice_hints(src, lang):
    if lang not in STYLE:
        raise ValueError(f"Practice mode does not know {lang} files")
    return javasrc.practice_hints(src, STYLE[lang])


def kept_by_comment(src, start):
    before = src[max(0, start - 160):start].lower()
    return "demo only" in before or "test helper" in before


# ---------------------------------------------------------------- Python

# Constructors and the printing / comparing dunders stay, as Java keeps constructors,
# toString, equals, hashCode and compareTo. __iter__, __len__, __getitem__ can be the answer.
PY_KEEP = {"main", "__init__", "__post_init__", "__repr__", "__str__", "__eq__", "__hash__", "__lt__"}
PY_PRINTS = re.compile(r"\bprint\s*\(|\bassert\b|\bAssertionError\b")


def python_skeleton(src):
    try:
        tree = ast.parse(src)
        tokens = list(tokenize.generate_tokens(io.StringIO(src).readline))
    except (SyntaxError, ValueError, tokenize.TokenError):
        return src, []                     # Run shows the error; there is nothing safe to cut
    lines = src.splitlines(keepends=True)
    nl = "\r\n" if "\r\n" in src else "\n"
    ends = [t.start[0] for t in tokens if t.type == tokenize.NEWLINE]     # where logical lines end
    cuts, hidden = [], []

    def offset(line):                       # index in src where a 1-based line starts
        return sum(len(x) for x in lines[:line - 1])

    def keep(fn, cls):
        name = fn.name
        if name in PY_KEEP or javasrc.KEEP_PREFIX.match(name):
            return True
        code = ast.get_source_segment(src, fn) or ""
        if javasrc.HARNESS_PREFIX.match(name) and PY_PRINTS.search(code):
            return True
        if cls and javasrc.HELPER_CLASS.search(cls):
            return True
        a = fn.args
        params = [x.arg for x in a.posonlyargs + a.args + a.kwonlyargs + [a.vararg, a.kwarg] if x]
        if any("expected" in p.lower() for p in params):
            return True
        if any(isinstance(n, ast.Constant) and isinstance(n.value, str) and "expected" in n.value.lower()
               for n in ast.walk(fn)):
            return True
        if any((getattr(d, "id", None) or getattr(d, "attr", None)) == "abstractmethod" for d in fn.decorator_list):
            return True
        first = fn.decorator_list[0].lineno if fn.decorator_list else fn.lineno
        return kept_by_comment(src, offset(first))

    def body_range(fn):
        header_end = next((n for n in ends if n >= fn.lineno), None)
        body = fn.body
        if header_end is None or body[0].lineno <= header_end:
            return None                     # `def f(): return 1` on one line
        doc = isinstance(body[0], ast.Expr) and isinstance(body[0].value, ast.Constant) \
            and isinstance(body[0].value.value, str)
        if doc and len(body) == 1:
            return None
        start = body[0].end_lineno + 1 if doc else header_end + 1
        end = body[-1].end_lineno
        j = end                             # comments at the end of the body go with it
        while j < len(lines):
            t = lines[j]
            if not t.strip():
                j += 1
                continue
            if t.lstrip().startswith("#") and len(t) - len(t.lstrip()) > fn.col_offset:
                end = j = j + 1
                continue
            break
        stmt = lines[(body[1] if doc else body[0]).lineno - 1]
        indent = stmt[:len(stmt) - len(stmt.lstrip())]
        return start, end, f"{indent}# {TODO}{nl}{indent}pass{nl}"

    def visit(body, cls):
        for node in body:
            if isinstance(node, ast.ClassDef):
                visit(node.body, node.name)
            elif isinstance(node, (ast.FunctionDef, ast.AsyncFunctionDef)) and not keep(node, cls):
                cut = body_range(node)
                if cut:
                    cuts.append(cut)
                    hidden.append(node.name)

    visit(tree.body, None)
    for start, end, text in sorted(cuts, reverse=True):
        lines[start - 1:end] = [text]
    return "".join(lines), hidden


# ---------------------------------------------------------------- JavaScript

# a `/` after one of these (or at the start) begins a regex literal, not a division
REGEX_AFTER = set("(,=:[!&|?{};+-*%<>~^")
REGEX_WORDS = {"return", "typeof", "case", "in", "of", "delete", "void", "throw", "new", "else", "do",
               "yield", "await", "instanceof"}
JS_KEEP = {"main", "constructor", "toString", "equals", "hashCode", "compareTo", "compare"}
JS_PRINTS = re.compile(r"\bconsole\s*\.\s*(?:log|info|warn|error|table)\b|\bassert\b")
JS_STRING = re.compile(r'"(?:[^"\\\n]|\\.)*"|\'(?:[^\'\\\n]|\\.)*\'|`(?:[^`\\]|\\.)*`', re.S)
NOT_MEMBER = {"if", "for", "while", "switch", "catch", "function", "return", "super", "with"}

TOP_FUNCTION = re.compile(r"^[ \t]*(?:export\s+(?:default\s+)?)?(?:async\s+)?function\b\s*\*?\s*([\w$]+)\s*\(", re.M)
# const f = (a) => {   const f = async x => {   const f = function (a) {
TOP_BINDING = re.compile(r"^[ \t]*(?:export\s+)?(?:const|let|var)\s+([\w$]+)\s*=\s*(?:async\s+)?"
                         r"(?:function\b\s*\*?\s*[\w$]*\s*(\()|(\()|[\w$]+\s*(=>))", re.M)
MEMBER = re.compile(r"^[ \t]*(?:static\s+)?(?:async\s+)?(?:\*\s*)?(?:(?:get|set)\s+)?(#?[\w$]+)\s*(\()", re.M)
FIELD = re.compile(r"^[ \t]*(?:static\s+)?(#?[\w$]+)\s*=\s*(?:async\s+)?(?:(\()|[\w$]+\s*(=>))", re.M)
CLASS = re.compile(r"\bclass\b(?:\s+([\w$]+))?[^;{}]*\{")


def js_noise(src):
    """javasrc.strip_noise for JavaScript: comments, strings, template strings (with their
    ${...} parts) and regex literals blanked, same length, newlines kept."""
    out, n = list(src), len(src)

    def blank(a, b):
        for k in range(a, b):
            if out[k] != "\n":
                out[k] = " "

    def string_end(i):
        q, j = src[i], i + 1
        while j < n and src[j] != q and src[j] != "\n":
            j += 2 if src[j] == "\\" else 1
        return min(j + 1, n)

    def template_end(i):
        j = i + 1
        while j < n:
            if src[j] == "\\":
                j += 2
            elif src[j] == "`":
                return j + 1
            elif src.startswith("${", j):
                j = expression_end(j + 2)
            else:
                j += 1
        return n

    def expression_end(j):
        depth = 1
        while j < n:
            c = src[j]
            if c in "\"'":
                j = string_end(j)
                continue
            if c == "`":
                j = template_end(j)
                continue
            if c == "{":
                depth += 1
            elif c == "}":
                depth -= 1
                if depth == 0:
                    return j + 1
            j += 1
        return n

    def regex_end(i):
        j, in_class = i + 1, False
        while j < n and src[j] != "\n":
            c = src[j]
            if c == "\\":
                j += 2
                continue
            if c == "[":
                in_class = True
            elif c == "]":
                in_class = False
            elif c == "/" and not in_class:
                j += 1
                while j < n and src[j].isalpha():
                    j += 1
                return j
            j += 1
        return None                          # no closing / on the line: a division after all

    def regex_allowed(i):
        k = i - 1
        while k >= 0 and out[k] in " \t\r\n":
            k -= 1
        if k < 0 or out[k] in REGEX_AFTER:
            return True
        if out[k].isalnum() or out[k] in "_$":
            w = k
            while w >= 0 and (out[w].isalnum() or out[w] in "_$"):
                w -= 1
            return "".join(out[w + 1:k + 1]) in REGEX_WORDS
        return False

    i = 0
    while i < n:
        if src.startswith("//", i):
            j = src.find("\n", i)
            j = n if j == -1 else j
        elif src.startswith("/*", i):
            j = src.find("*/", i + 2)
            j = n if j == -1 else j + 2
        elif src[i] in "\"'":
            j = string_end(i)
        elif src[i] == "`":
            j = template_end(i)
        elif src[i] == "/" and regex_allowed(i) and (j := regex_end(i)) is not None:
            pass
        else:
            i += 1
            continue
        blank(i, j)
        i = j
    return "".join(out)


def js_skeleton(src, page=False):
    """page: a UI file's own script. Its tests live in the separate test block, so there
    render(), show() and display() are the answer, not a test helper to keep."""
    s = js_noise(src)
    depth = javasrc.depths(s)
    pairs, stack = [], []                    # (open, close) of every brace pair
    for i, ch in enumerate(s):
        if ch == "{":
            stack.append(i)
        elif ch == "}" and stack:
            pairs.append((stack.pop(), i))
    classes = {}                             # a class body's '{' -> the class name
    for m in CLASS.finditer(s):
        classes[m.end() - 1] = m.group(1) or ""

    def owner(p):                            # the innermost '{' around position p, or None
        best = None
        for a, z in pairs:
            if a < p < z and (best is None or a > best):
                best = a
        return best

    def paren_end(i):                        # index just after the ')' matching s[i] == '('
        d = 0
        for j in range(i, len(s)):
            if s[j] == "(":
                d += 1
            elif s[j] == ")":
                d -= 1
                if d == 0:
                    return j + 1
        return None

    def body_open(after, arrow):             # the body's '{' after a parameter list, or None
        m = re.compile(r"\s*=>\s*\{" if arrow else r"\s*\{").match(s, after)
        return m.end() - 1 if m else None

    # paren: index of the parameters' '(' (None for `x => {`); arrow: an `=>` follows the ')';
    # after: for `x => {`, the index just after the `=>`
    candidates = []                          # (start, name, paren, arrow, after, class name)
    for m in TOP_FUNCTION.finditer(s):
        if depth[m.start(1)] == 0:
            candidates.append((m.start(), m.group(1), m.end() - 1, False, None, None))
    for m in TOP_BINDING.finditer(s):
        if depth[m.start(1)] == 0:
            if m.group(2):
                candidates.append((m.start(), m.group(1), m.start(2), False, None, None))
            elif m.group(3):
                candidates.append((m.start(), m.group(1), m.start(3), True, None, None))
            else:
                candidates.append((m.start(), m.group(1), None, False, m.end(4), None))
    for m in MEMBER.finditer(s):
        o = owner(m.start(1))
        if o in classes and m.group(1) not in NOT_MEMBER:
            candidates.append((m.start(), m.group(1), m.start(2), False, None, classes[o]))
    for m in FIELD.finditer(s):              # handle = (e) => {  inside a class
        o = owner(m.start(1))
        if o in classes:
            if m.group(2):
                candidates.append((m.start(), m.group(1), m.start(2), True, None, classes[o]))
            else:
                candidates.append((m.start(), m.group(1), None, False, m.end(3), classes[o]))

    cuts, hidden = [], []
    for start, name, paren, arrow, after, cls in sorted(candidates, key=lambda c: c[0]):
        if paren is not None:
            close = paren_end(paren)
            if close is None:
                continue
            b = body_open(close, arrow)
            params = src[paren:close]
        else:                                # x => {
            b = body_open(after, False)
            params = src[start:after]
        if b is None or any(a <= b < z for a, z, _ in cuts):
            continue
        d = depth[b]
        e = b + 1
        while e < len(s) and not (s[e] == "}" and depth[e] == d + 1):
            e += 1
        original = src[start:e]
        if (name in JS_KEEP or (not page and javasrc.KEEP_PREFIX.match(name))
                or (not page and javasrc.HARNESS_PREFIX.match(name) and JS_PRINTS.search(s, start, e))
                or (cls and javasrc.HELPER_CLASS.search(cls))
                or "expected" in params.lower()
                or any("expected" in t.lower() for t in JS_STRING.findall(original))
                or kept_by_comment(src, start)):
            continue
        indent = re.match(r"[ \t]*", src[start:]).group(0)
        inner = re.search(r"\n([ \t]*)\S", src[b:e])
        step = inner.group(1)[len(indent):] if inner and len(inner.group(1)) > len(indent) else "  "
        cuts.append((b + 1, e, f"\n{indent}{step}// {TODO}\n{indent}"))
        hidden.append(name)
    out = src
    for a, z, body in sorted(cuts, reverse=True):
        out = out[:a] + body + out[z:]
    return out, hidden


# ---------------------------------------------------------------- UI (.html)

SCRIPT = re.compile(r"(<script\b([^>]*)>)(.*?)(</script\s*>)", re.S | re.I)
SCRIPT_TYPE = re.compile(r"""\btype\s*=\s*["']?([^"'\s>]+)""", re.I)
JS_TYPES = ("", "module", "text/javascript", "application/javascript")


def page_scripts(src):
    """-> [(start, end)] of the page's own JavaScript: not <script type="test">, not src=."""
    out = []
    for m in SCRIPT.finditer(src):
        t = SCRIPT_TYPE.search(m.group(2))
        if (t.group(1).lower() if t else "") in JS_TYPES and not re.search(r"\bsrc\s*=", m.group(2), re.I):
            out.append((m.start(3), m.end(3)))
    return out


def html_skeleton(src):
    parts, hidden, pos = [], [], 0
    for a, z in page_scripts(src):
        text, names = js_skeleton(src[a:z], page=True)
        parts += [src[pos:a], text]
        hidden += names
        pos = z
    parts.append(src[pos:])
    return "".join(parts), hidden
