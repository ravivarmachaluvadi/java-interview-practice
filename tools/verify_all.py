import pathlib
"""Compile and run every practice file, and check each printed 'expected' against the actual.

Convention the files follow:  System.out.println("case 1: " + actual + "   expected " + X)
so on each output line containing ' expected ', the text to the right should also appear
immediately to the left of it.
"""
import os, re, subprocess, sys, tempfile, pathlib, json, time
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import runjava

ROOT = pathlib.Path(__file__).resolve().parent.parent / "AAScratches"
PACKAGED = ("orders-springboot-project", "WorkFlowExecutor")
EXP = re.compile(r"\bexpected\b[: ]*(.*)$", re.I)


def check_expectations(out):
    """Return list of (line, expected) where the actual clearly does not match."""
    bad = []
    for line in out.splitlines():
        m = EXP.search(line)
        if not m:
            continue
        exp = m.group(1).strip().rstrip(".")
        if not exp or len(exp) > 120:
            continue
        left = line[:m.start()].rstrip().rstrip("(,;|-").rstrip()
        # normalise whitespace for comparison
        norm = lambda s: re.sub(r"\s+", "", s)
        if norm(exp) and norm(exp) not in norm(left):
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
