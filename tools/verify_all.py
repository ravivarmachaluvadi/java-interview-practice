import pathlib
"""Compile and run every practice file, and check each printed 'expected' against the actual.

Convention the files follow:  System.out.println("case 1: " + actual + "   expected " + X)
so on each output line containing ' expected ', the text to the right should also appear
immediately to the left of it.

Variations the files also use, all accepted:
  a note after the value       expected 2   (1 + 1)    expected 3 (both)    expected 1   OK
  one value for several        expected 700 for both   expected 3 from all three
  several simple values        expected true true      expected 8, 8, true  (matched in order)
  two checks on one line       index 2   expected 2 | distance 2   expected 2
  alternatives                 expected 'hello' or 'leet'
Lines with nothing to compare are skipped: an empty left side (Tricky-MCQ answers), a
'---' heading, a range described in words (expected: usually < 100000), or an expected
empty string ("expected " + "" + "   OK").
"""
import os, re, subprocess, sys, tempfile, pathlib, json, time
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import runjava

ROOT = pathlib.Path(__file__).resolve().parent.parent / "AAScratches"
PACKAGED = ("orders-springboot-project", "WorkFlowExecutor")
EXP = re.compile(r"\bexpected\b[: ]*(.*)$", re.I)
# Where a note may start after the expected value.
NOTE = re.compile(r"\s{2,}|\s\(|\s-\s|\s(?:from|for)\s")
# An expectation described in words (thread races), not a value to compare.
QUALITATIVE = re.compile(r"(almost|usually|fewer|more than|less than|roughly|about|around|at least|at most)\b",
                         re.I)
SIMPLE_TOKEN = re.compile(r"-?[\w.']+$")


def norm(s):
    return re.sub(r"\s+", "", s)


def agrees(left, exp):
    """Does the expected value (possibly followed by a note) appear in the text before it?"""
    trimmed = left.rstrip().rstrip("(,;|-").rstrip()
    candidates = [exp]
    note = NOTE.search(exp)
    if note and note.start() > 0:
        candidates.append(exp[:note.start()])
    for c in candidates:
        c = norm(c.strip().rstrip("."))
        if c and (c in norm(left) or c in norm(trimmed)):
            return True
    # Alternatives: "expected 'hello' or 'leet'" -- any one of them will do.
    base = candidates[-1].strip()
    if re.search(r"\sor\s", base):
        return any(norm(alt) in norm(left) for alt in re.split(r"\s+or\s+", base) if alt.strip())
    # Several simple values for several results ("true true", "8, 8, true"): each must
    # appear as a whole word, in the same order, in the text before.
    tokens = [t for t in re.split(r"[,\s]+", candidates[-1].strip()) if t]
    if len(tokens) > 1 and all(SIMPLE_TOKEN.match(t) for t in tokens):
        pos = 0
        for t in tokens:
            found = re.search(r"(?<![\w.-])" + re.escape(t) + r"(?![\w.])", left[pos:])
            if not found:
                return False
            pos += found.end()
        return True
    return False


def check_expectations(out):
    """Return list of (line, expected) where the actual clearly does not match."""
    bad = []
    for line in out.splitlines():
        if len(re.findall(r"\bexpected\b", line, re.I)) > 1 and " | " in line:
            segments = line.split(" | ")                   # two checks on one line
        else:
            segments = [line]
        for seg in segments:
            m = EXP.search(seg)
            if not m:
                continue
            exp = m.group(1).strip().rstrip(".")
            left = seg[:m.start()]
            if not exp or len(exp) > 120 or not left.strip() or left.lstrip().startswith("---"):
                continue
            if QUALITATIVE.match(exp):
                continue
            if re.match(r":? {3,}", seg[m.start() + len("expected"):]):
                continue                                   # "expected " + "" + "   note"

            if not agrees(left, exp):
                bad.append((line.strip()[:160], exp[:80]))
    return bad


def main():
    out_path = pathlib.Path(sys.argv[1])
    files = sorted(p for p in ROOT.rglob("*.java") if not p.relative_to(ROOT).as_posix().startswith("_"))
    results = []
    t0 = time.time()
    for i, p in enumerate(files):
        rel = p.relative_to(ROOT).as_posix()
        rec = {"file": rel}
        if any(k in rel for k in PACKAGED):
            rec["status"] = "SKIP_PACKAGED"
            results.append(rec)
            continue
        text = p.read_text(encoding="utf-8", errors="replace")
        mains = runjava.classes_with_main(text)
        with tempfile.TemporaryDirectory(prefix="vf_") as outdir:
            c = subprocess.run([runjava.JAVAC, "-nowarn", "-encoding", "UTF-8", "-d", outdir,
                                "-sourcepath", str(p.parent), str(p)],
                               capture_output=True, text=True, errors="replace", timeout=180)
            if c.returncode != 0:
                rec["status"] = "COMPILE_FAIL"
                rec["err"] = (c.stderr or "")[:600]
            elif not mains:
                rec["status"] = "NO_MAIN"
            else:
                try:
                    r = subprocess.run([runjava.JAVA, "-cp", outdir, mains[0]],
                                       capture_output=True, text=True, errors="replace",
                                       stdin=subprocess.DEVNULL, timeout=20)
                    if r.returncode != 0:
                        rec["status"] = "RUN_FAIL"
                        rec["err"] = (r.stderr or "")[:600]
                    else:
                        mism = check_expectations(r.stdout)
                        rec["status"] = "MISMATCH" if mism else "OK"
                        rec["lines"] = len(r.stdout.splitlines())
                        rec["expected_count"] = len([l for l in r.stdout.splitlines() if re.search(r"\bexpected\b", l, re.I)])
                        if mism:
                            rec["mismatches"] = mism[:6]
                except subprocess.TimeoutExpired:
                    rec["status"] = "TIMEOUT"
        results.append(rec)
        if i % 40 == 0:
            print(f"{i}/{len(files)} {time.time()-t0:.0f}s", flush=True)
            out_path.write_text(json.dumps(results, indent=1), encoding="utf-8")
    out_path.write_text(json.dumps(results, indent=1), encoding="utf-8")
    from collections import Counter
    print("STATUS:", Counter(r["status"] for r in results))
    print("files asserting expectations:", sum(1 for r in results if r.get("expected_count")))
    print("total expectation lines:", sum(r.get("expected_count", 0) for r in results))


if __name__ == "__main__":
    main()
