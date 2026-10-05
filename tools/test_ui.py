"""
Browser checks for Code Viewer (tools/codeview.html), driven through a real headless Chrome:
autocomplete, Run, F12, rename, Ctrl+Alt+V, the formatter, the header timer, the caret, Save.

    python tools/test_ui.py              every suite in tools/ui_tests/
    python tools/test_ui.py timer caret  only the suites whose names contain these words

Needs Google Chrome and Node.js 22 or newer. It starts its own Code Viewer on 127.0.0.1:8026
with a throwaway state file - the tray's server on 8025 and your progress are never used - and
stops it at the end. All suites take about 5 minutes.

The suites read repo files (C06 and a few others) but never change them: whatever saves works
in a scratch folder, and a suite fails if `git status` or `git diff` changed while it ran.
How a suite is written: tools/ui_tests/harness.mjs.
"""
import os, pathlib, re, shutil, socket, subprocess, sys, tempfile, time, urllib.request

HERE = pathlib.Path(__file__).resolve().parent
SUITES = HERE / "ui_tests"
PORT = 8026
BASE = f"http://127.0.0.1:{PORT}"


def fail(msg):
    print(msg)
    sys.exit(2)


def need_tools():
    try:
        v = subprocess.run(["node", "--version"], capture_output=True, text=True).stdout.strip()
    except OSError:
        fail("Node.js is not installed (needed to drive Chrome): https://nodejs.org")
    if int(re.sub(r"\D.*", "", v.lstrip("v")) or 0) < 22:
        fail(f"Node.js {v} is too old: the suites need 22 or newer (for its built-in WebSocket).")
    with socket.socket() as s:
        if s.connect_ex(("127.0.0.1", PORT)) == 0:
            fail(f"Something already listens on port {PORT} (an earlier test server?). Stop it first.")


def page_parses():
    """The page's main script must parse; a typo there would only show as every suite timing out."""
    html = (HERE / "codeview.html").read_text(encoding="utf-8")
    js = max(re.findall(r"<script>(.*?)</script>", html, re.S), key=len)
    with tempfile.TemporaryDirectory() as d:
        f = pathlib.Path(d) / "page.js"
        f.write_text(js, encoding="utf-8")
        r = subprocess.run(["node", "--check", str(f)], capture_output=True, text=True)
    if r.returncode:
        fail("codeview.html: its script does not parse\n" + r.stderr[-2000:])


def start_server(tmp):
    # CODEVIEW_NO_PUSH: the push suite clicks Push; this server refuses it for the real repo
    env = dict(os.environ, CODEVIEW_STATE=str(tmp / "state.json"), CODEVIEW_LOG=str(tmp / "codeview.log"),
               CODEVIEW_NO_PUSH="1")
    server = subprocess.Popen([sys.executable, str(HERE / "codeview.py"), "--port", str(PORT), "--no-open"],
                              env=env, stdin=subprocess.DEVNULL, stdout=subprocess.DEVNULL, stderr=subprocess.STDOUT)
    for _ in range(150):
        if server.poll() is not None:
            break
        try:
            with urllib.request.urlopen(BASE + "/api/roots", timeout=2):
                return server
        except OSError:
            time.sleep(0.2)
    stop_server(server)
    log = tmp / "codeview.log"
    fail("The test server did not start.\n" + (log.read_text(encoding="utf-8", errors="replace")[-2000:] if log.exists() else ""))


def stop_server(server):
    """the server and its javac helper with it"""
    if os.name == "nt":
        subprocess.run(["taskkill", "/PID", str(server.pid), "/T", "/F"], capture_output=True)
    else:
        server.terminate()
    try:
        server.wait(timeout=10)
    except subprocess.TimeoutExpired:
        server.kill()


def run_suite(path):
    """streams the suite's lines; returns (passed, total, seconds)"""
    t0, passed, total = time.time(), 0, 0
    p = subprocess.Popen(["node", str(path)], env=dict(os.environ, CV_BASE=BASE), stdout=subprocess.PIPE,
                         stderr=subprocess.STDOUT, text=True, encoding="utf-8", errors="replace")
    for line in p.stdout:
        print("  " + line.rstrip())
        m = re.match(r"(\d+) of (\d+) passed", line)
        if m:
            passed, total = int(m[1]), int(m[2])
    code = p.wait()
    if code and passed == total:          # crashed before its summary line
        total += 1
    return passed, total, time.time() - t0


def main(words):
    # a suite's lines hold ⇡, →, ★; a cp1252 console (Windows, piped) would stop the run on them
    sys.stdout.reconfigure(errors="replace")
    suites = sorted(p for p in SUITES.glob("*.mjs") if p.name != "harness.mjs"
                    and (not words or any(w.lower() in p.stem.lower() for w in words)))
    if not suites:
        fail(f"No suite matches {' '.join(words)}. There are: "
             + ", ".join(p.stem for p in SUITES.glob("*.mjs") if p.name != "harness.mjs"))
    need_tools()
    page_parses()
    tmp = pathlib.Path(tempfile.mkdtemp(prefix="cvuitest_"))
    server = start_server(tmp)
    rows, t0 = [], time.time()
    try:
        for path in suites:
            print(f"\n== {path.stem}")
            rows.append((path.stem, *run_suite(path)))
    finally:
        stop_server(server)
        shutil.rmtree(tmp, ignore_errors=True)
    print(f"\n{'suite':<24}{'passed':>9}{'time':>8}")
    for name, passed, total, secs in rows:
        print(f"{name:<24}{passed:>4} / {total:<3}{secs:>6.0f} s" + ("" if passed == total else "   <- FAILED"))
    ok, checks = sum(r[1] for r in rows), sum(r[2] for r in rows)
    mins = (time.time() - t0) / 60
    print(f"\n{'all ' if ok == checks else ''}{ok} of {checks} checks passed in {mins:.1f} min")
    return 0 if ok == checks else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
