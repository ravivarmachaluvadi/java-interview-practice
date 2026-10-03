"""
outcheck - judge one line of program output that states what it expected.

    judge("case 1 typical : [3, 4, -1, 1] -> 2   expected 2")   -> True
    judge("case 1 typical : 0   expected 6")                     -> False
    judge("prints actual vs expected")                           -> None  (not a result line)

True: the actual value clearly matches. False: one actual value clearly differs.
None: not a result line, or too ambiguous to call. Unsure lines are left unmarked on
purpose - a wrong cross is what makes people stop trusting the marks.

Shapes, measured by running all 580 repo files on 3 Oct 2026 (3,207 such lines):
  label: actual   expected X
  label: input -> actual   expected X           arrows: ->  =>  and the unicode arrow
  label: actual   expected X   (a note)        also "X - a note", "X for both", "X from all three"
  label: a=1, b=1   expected 1                 several answers that must each equal X
  label: a 8, b 8, c true   expected 8, 8, true  several answers, one expected each
  label: actual (valid)   expected valid       the verdict in brackets after the actual
  label: x   expected x   [OK]  / match=true   the program printed its own verdict
  a 2   expected 2 | b 3   expected 3          two checks on one line
  f(7, 3) = 4   expected 4                     no label: only ever a tick, never a cross
"""
import re

ERROR_LINE = re.compile(r"^(Exception in thread|Caused by:|\s+at |\s+\.\.\. \d+ more|Error: )"
                        r"|^[\w.$]+(Exception|Error)(: |$)")
EXPECTED_WORD = re.compile(r"\bexpected\b", re.I)
ARROW = re.compile(r"\s*(?:(?<!<)->|=>|→)\s*")
ALL_MARKER = re.compile(r"\s*(?:\(?\b(?:for |from )?both\)?|\bfrom all \w+|\bfor all(?: \w+)?|\(all\))$", re.I)
# The program's own verdict at the very end: "[OK]", "   OK", "match=true", a tick or cross.
# Bare words must be upper case and set off by 2+ spaces, so a value "Fail" is not a verdict.
OWN_VERDICT = re.compile(r"(?:\b(?i:match)\s*=\s*((?i:true|false))|\[((?i:ok|pass|fail|mismatch))\]"
                         r"|\s{2,}(OK|PASS|FAIL|MISMATCH)|(✓|✗))\s*$")
# Expectations that are a description, not a value: never worth a cross.
QUALITATIVE = re.compile(r"\b(almost|usually|fewer|more than|less than|at least|at most|about|roughly|"
                         r"approximately|never|any|some|one of|either|or|not|varies|random)\b|[<>~≈]", re.I)
BOUNDARY = set(" :>=|,")


def norm(s):
    return re.sub(r"\s+", " ", s.strip())


def scan(s):
    """Yield (index, char, depth, in_quote) - depth counts (), [] and {} outside quotes."""
    depth, quote = 0, None
    for i, ch in enumerate(s):
        if quote:
            yield i, ch, depth, True
            if ch == quote:
                quote = None
            continue
        if ch in "\"'":
            quote = ch
            yield i, ch, depth, True
            continue
        if ch in ")]}":
            depth -= 1
        yield i, ch, depth, False
        if ch in "([{":
            depth += 1


def split_top(s, sep=","):
    """Split on `sep` outside brackets and quotes: 'a [1, 2], b 3' -> ['a [1, 2]', 'b 3']."""
    out, start = [], 0
    for i, ch, depth, q in scan(s):
        if ch == sep and depth == 0 and not q:
            out.append(s[start:i].strip())
            start = i + 1
    out.append(s[start:].strip())
    return [p for p in out if p]


def top_words(s):
    """Words outside brackets and quotes: '[India Gate, Red Fort]' counts as one."""
    words, inword = 0, False
    for _, ch, depth, q in scan(s):
        if depth == 0 and not q and ch.isspace():
            inword = False
        elif not inword:
            words, inword = words + 1, True
    return words


def value_of(seg):
    """'two-pointer 8' -> '8', 'set: true' -> 'true', 'PQ [0, 8]' -> '[0, 8]', '42' -> '42'."""
    last_sep, last_space = -1, -1
    for i, ch, depth, q in scan(seg):
        if depth == 0 and not q:
            if ch in "=:":
                last_sep = i
            elif ch == " ":
                last_space = i
    if last_sep >= 0:
        return seg[last_sep + 1:].strip()
    if last_space >= 0:
        return seg[last_space + 1:].strip()
    return seg.strip()


def segments(actual):
    """'binary true, staircase true' / 'recursive=true iterative=true' -> ['true', 'true'];
    a single value comes back as a one-element list."""
    parts = split_top(actual)
    if len(parts) == 1:
        named = re.findall(r"[A-Za-z][\w-]*\s*=\s*(\S+)", actual)
        if len(named) > 1 and re.fullmatch(r"(?:[A-Za-z][\w-]*\s*=\s*\S+\s*)+", actual):
            return named
        return parts
    return [value_of(p) for p in parts]


def ends_with(actual, want):
    if actual == want:
        return True
    if want and actual.endswith(want) and len(actual) > len(want):
        return actual[-len(want) - 1] in BOUNDARY
    return False


def strip_trailing_note(s):
    """'true (Thread.x() put it back)' -> 'true'; '[1, 5] - raw heap array' -> '[1, 5]'."""
    s = s.rstrip()
    if s.endswith(")"):
        depth = 0
        for i in range(len(s) - 1, -1, -1):
            depth += {")": 1, "(": -1}.get(s[i], 0)
            if depth == 0:
                if i > 0 and s[i - 1] == " ":
                    return s[:i].rstrip()
                break
    m = re.search(r"\s[-—]\s", s)
    return s[:m.start()].rstrip() if m else s


def first_top_level(hits, line):
    """The first 'expected' outside brackets - a note may say 'expected' again."""
    for m in hits:
        before = line[:m.start()]
        if before.count("(") <= before.count(")") and before.count("[") <= before.count("]"):
            return m
    return hits[0]


def judge(line):
    if ERROR_LINE.search(line) or re.match(r"^\s*(-{3}|={3})", line):
        return None
    hits = list(EXPECTED_WORD.finditer(line))
    if not hits:
        return None
    own = OWN_VERDICT.search(line)
    if own and own.start() >= hits[0].end():       # "expected    OK": empty value, then a verdict
        word = next(g for g in own.groups() if g).lower()
        return word in ("true", "ok", "pass", "✓")
    if len(hits) > 1 and " | " in line:
        parts = [p for p in line.split(" | ") if EXPECTED_WORD.search(p)]
        # later parts lose the line's label ("distance 9   expected 2"): read "name value" as "name: value"
        parts = parts[:1] + [p if ":" in p.split("expected")[0] else re.sub(r"^(\s*[A-Za-z][\w-]*)\s+", r"\1: ", p)
                             for p in parts[1:]]
        if len(parts) > 1:                             # "a 2   expected 2 | b 3   expected 3"
            results = [judge(p) for p in parts]
            if False in results:
                return False
            return True if all(results) else None
    return judge_one(line, first_top_level(hits, line))


def judge_one(line, cut):
    raw_left, raw_right = line[:cut.start()], line[cut.end():]
    raw_right = re.sub(r"^\s*[:=]?", "", raw_right)
    opened = raw_left.rstrip().endswith("(")
    left_full = norm(raw_left)
    left = left_full.rstrip(" ,;(|")
    right = norm(raw_right)
    if opened and right.endswith(")"):
        right = right[:-1].rstrip()
    right = right.rstrip(".").strip()

    if not left:
        return None                                    # "expected: ..." with no actual: prose
    if not right:
        return True if left.endswith(":") else None    # empty actual vs empty expected
    labelled = ":" in left or bool(ARROW.search(left)) or bool(re.search(r"\bactual\b", left))

    # candidate actuals: after the last arrow, after the first ':', after the word "actual"
    cands = []
    parts = ARROW.split(left)
    if len(parts) > 1:
        cands.append(parts[-1].strip())
    if ":" in left:
        cands += [left.split(":", 1)[1].strip(), left_full.split(":", 1)[1].strip()]
    m = re.search(r"\bactual\b\s*:?\s*(.*)$", left)
    if m:
        cands.append(m.group(1).strip())
    if not labelled:
        cands.append(left)
    for c in list(cands):
        m = re.search(r"\(([^()]+)\)$", c)             # '"aba" (valid)' -> also try 'valid'
        if m:
            cands += [m.group(1).strip(), c[:m.start()].strip()]

    # candidate expected values: as written, minus a trailing note, minus "for both"
    wants, w = [right], right
    for _ in range(4):
        w2 = ALL_MARKER.sub("", strip_trailing_note(w)).strip()
        if not w2 or w2 == w:
            break
        wants.append(w2)
        w = w2

    for want in wants:
        for c in cands:
            if not labelled and c.count("=") > 1:
                continue                               # "x = 1   y = 2": too loose to tick
            if ends_with(norm(c), want):
                return True
            segs = segments(c)
            if len(segs) > 1:
                exp = [value_of(e) for e in split_top(want)] if len(split_top(want)) > 1 else want.split()
                if len(exp) == len(segs) and all(norm(a) == norm(b) for a, b in zip(segs, exp)):
                    return True                         # "a true, b false   expected true, false"
                if all(norm(s) == want for s in segs):
                    return True                         # "set=true sort=true   expected true"

    # A cross only when the call is clear-cut.
    core = wants[-1]
    if not labelled or QUALITATIVE.search(core) or top_words(core) > 4:
        return None
    primary = cands[0]
    if len(primary) > 160:
        return None
    segs = segments(primary)
    if len(segs) > 1:
        return False if (len(split_top(core)) == 1 and len(core.split()) == 1) else None
    return False
