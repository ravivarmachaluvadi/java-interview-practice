#!/usr/bin/env python3
"""
codeview - read and run the practice files in a browser.

    tools/codeview               start on http://127.0.0.1:8025 and open a tab
    tools/codeview --port 9000   use another port
    tools/codeview --no-open     do not open a browser tab
    tools/codeview --tray        no console window; a tray icon to reopen or quit
    tools/codeview --install-shortcuts   "Code Viewer" on the Desktop and Start menu
                                         (they start the tray version)

Starting it again while it is already running just opens a browser tab.

Left: every file in the repo as a tree, with a filter box (Ctrl+P). Middle: the
file in the VS Code editor (Monaco) with Java colouring, read-only until you press
Edit. Run (Ctrl+Enter) compiles whatever is in the editor - the file as written or
your edited copy - and shows the output underneath, ticking every
"actual   expected X" line that matches.

Edits never touch the repo. The page keeps them in the browser (localStorage) as a
draft per file, so a reload keeps them; Reset throws a draft away. The Scratch pad
is a blank file for trying anything.

Runs the same way as runjava: compile to a temp folder, then run whichever class
declares main(), so the filename never matters. Newest installed JDK, not JAVA_HOME.

SECURITY: this executes code, so it listens on 127.0.0.1 only and refuses any
request whose Host or Origin is not this server - a web page you happen to visit
cannot make it run anything.
"""
import argparse, json, os, pathlib, re, shutil, subprocess, sys, tempfile, threading, time
import urllib.request, webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parent
sys.path.insert(0, str(HERE))
from runjava import JAVA, JAVAC  # noqa: E402  same newest-JDK pick as the CLI runner

PAGE = HERE / "codeview.html"
ICON = HERE / "codeview.ico"
# Started from the tray (pythonw) there is no console, so every javac/java run would
# flash a new console window. This flag suppresses that; it is 0 off Windows.
NO_WINDOW = getattr(subprocess, "CREATE_NO_WINDOW", 0)
SKIP_DIRS = {".git", ".idea", ".vscode", "tools", "__pycache__", "_personal",
             "08-Reference", "out", "target", "node_modules"}
TEXT_EXT = {".java", ".md", ".txt", ".sql", ".properties", ".xml", ".json", ".yml", ".yaml"}
OUTPUT_CAP = 256 * 1024          # bytes of program output sent back to the page
MAX_TIMEOUT = 60                 # seconds; the page offers 5 / 10 / 30 / 60


# ---------------------------------------------------------------- file tree

def build_tree():
    def walk(d):
        node = {"name": d.name, "dirs": [], "files": []}
        try:
            entries = sorted(d.iterdir(), key=lambda p: p.name.lower())
        except OSError:
            return node
        for p in entries:
            if p.is_dir():
                if p.name in SKIP_DIRS or p.name.startswith("."):
                    continue
                child = walk(p)
                if child["dirs"] or child["files"]:
                    node["dirs"].append(child)
            elif p.suffix.lower() in TEXT_EXT:
                f = {"name": p.name, "path": p.relative_to(REPO).as_posix()}
                if p.suffix == ".java" and "MUST-KNOW" in read_head(p):
                    f["must"] = True
                node["files"].append(f)
        return node
    return walk(REPO)


def read_head(p, n=1500):
    try:
        with open(p, "rb") as fh:
            return fh.read(n).decode("utf-8", "replace")
    except OSError:
        return ""


def resolve(rel):
    """Repo-relative path -> absolute, refusing anything outside the repo or in SKIP_DIRS."""
    p = (REPO / rel).resolve()
    if REPO not in p.parents:
        raise ValueError("outside the repo")
    if any(part in SKIP_DIRS for part in p.relative_to(REPO).parts):
        raise ValueError("excluded folder")
    return p


# ---------------------------------------------------------------- finding main()

TYPE_DECL = re.compile(
    r"^[ \t]*((?:(?:public|protected|private|final|abstract|sealed|non-sealed|static|strictfp)\s+)*)"
    r"(class|enum|record|interface)\s+(\w+)", re.M)
MAIN_DECL = re.compile(r"\bvoid\s+main\s*\(")
PACKAGE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.M)


def strip_noise(src):
    """Blank out comments, strings, chars and text blocks (same length, newlines kept),
    so braces and the word 'main' inside them do not confuse the scan below."""
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


def analyse(src):
    """-> (package, top-level classes declaring main(), public top-level type, is compact file).

    A JDK 25 compact source file has `void main()` with no class around it; javac
    names the class after the file. Instance `void main()` inside a class also counts."""
    s = strip_noise(src)
    pm = PACKAGE.search(s)
    pkg = pm.group(1) if pm else ""
    depth, d = [0] * (len(s) + 1), 0
    for i, ch in enumerate(s):
        depth[i] = d
        if ch == "{":
            d += 1
        elif ch == "}":
            d -= 1
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


# ---------------------------------------------------------------- compile + run

def run_code(code, rel_path, stdin, timeout, want_main):
    src_dir, fname = None, "Scratch.java"
    if rel_path:
        p = resolve(rel_path)
        src_dir, fname = p.parent, p.name
    pkg, mains, public, compact = analyse(code)
    if public:                         # javac insists a public type lives in <Name>.java
        fname = public + ".java"
    if pkg and src_dir:                # sourcepath must be the package root, not its folder
        for _ in pkg.split("."):
            src_dir = src_dir.parent
    if compact:
        mains = [pathlib.Path(fname).stem]
    prefix = pkg + "." if pkg else ""

    work = pathlib.Path(tempfile.mkdtemp(prefix="codeview_"))
    try:
        src = work / "src" / fname
        src.parent.mkdir()
        src.write_text(code, encoding="utf-8")
        out = work / "out"
        out.mkdir()

        cmd = [JAVAC, "-J-Dstderr.encoding=UTF-8", "-J-Dstdout.encoding=UTF-8",
               "-nowarn", "-encoding", "UTF-8", "-d", str(out)]
        if src_dir:
            cmd += ["-sourcepath", str(src_dir)]
        cmd.append(str(src))
        t0 = time.monotonic()
        try:
            c = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8",
                               errors="replace", timeout=120, stdin=subprocess.DEVNULL,
                               creationflags=NO_WINDOW)
        except subprocess.TimeoutExpired:
            return {"phase": "compile", "ok": False, "output": "javac took longer than 120 s."}
        compile_ms = int((time.monotonic() - t0) * 1000)
        if c.returncode != 0:
            return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms,
                    "output": tidy(c.stderr + c.stdout, work, fname)}
        if not mains:
            return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms,
                    "output": "Compiled, but no class declares main() - nothing to run."}

        main = want_main if want_main in mains else pick_main(mains, fname)
        (work / "stdin.txt").write_text(stdin or "", encoding="utf-8")
        cmd = [JAVA, "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
               "-Dfile.encoding=UTF-8", "-XX:TieredStopAtLevel=1", "-cp", str(out), prefix + main]
        t0 = time.monotonic()
        with open(work / "stdin.txt", "rb") as fin, open(work / "output.txt", "wb") as fout:
            proc = subprocess.Popen(cmd, stdin=fin, stdout=fout, stderr=subprocess.STDOUT, cwd=work,
                                    creationflags=NO_WINDOW)
            try:
                rc, timed_out = proc.wait(timeout=timeout), False
            except subprocess.TimeoutExpired:
                proc.kill()
                rc, timed_out = proc.wait(), True
        run_ms = int((time.monotonic() - t0) * 1000)
        size = (work / "output.txt").stat().st_size
        with open(work / "output.txt", "rb") as fh:
            text = fh.read(OUTPUT_CAP).decode("utf-8", "replace")
        return {"phase": "run", "ok": rc == 0 and not timed_out, "exitCode": rc,
                "timedOut": timed_out, "truncated": size > OUTPUT_CAP, "file": fname,
                "ran": main, "mains": mains, "compileMs": compile_ms, "runMs": run_ms,
                "output": tidy(text, work, fname)}
    finally:
        shutil.rmtree(work, ignore_errors=True)


def tidy(text, work, fname):
    """Show `A01_X.java:12: error` instead of the temp folder path."""
    for form in (str(work / "src") + "\\", str(work / "src") + "/", str(work) + "\\"):
        text = text.replace(form, "")
    return text


def jdk_version():
    try:
        r = subprocess.run([JAVA, "-version"], capture_output=True, text=True, timeout=20,
                           stdin=subprocess.DEVNULL, creationflags=NO_WINDOW)
        m = re.search(r'version "([^"]+)"', r.stderr)
        return m.group(1) if m else "unknown"
    except (OSError, subprocess.TimeoutExpired):
        return "not found"


# ---------------------------------------------------------------- HTTP

class Handler(BaseHTTPRequestHandler):
    server_version = "codeview"

    def log_message(self, fmt, *args):
        pass

    def send(self, code, body, ctype="text/plain; charset=utf-8"):
        if isinstance(body, str):
            body = body.encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", ctype)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.send_header("X-Content-Type-Options", "nosniff")
        self.end_headers()
        self.wfile.write(body)

    def send_json(self, obj, code=200):
        self.send(code, json.dumps(obj), "application/json; charset=utf-8")

    def host_ok(self):
        return self.headers.get("Host", "") in self.server.allowed_hosts

    def do_GET(self):
        if not self.host_ok():
            return self.send(403, "Forbidden host")
        u = urlparse(self.path)
        if u.path == "/":
            return self.send(200, PAGE.read_bytes(), "text/html; charset=utf-8")
        if u.path == "/api/info":
            return self.send_json({"jdk": self.server.jdk, "repo": REPO.name})
        if u.path == "/api/tree":
            return self.send_json(build_tree())
        if u.path == "/api/file":
            rel = parse_qs(u.query).get("path", [""])[0]
            try:
                p = resolve(rel)
                return self.send(200, p.read_text(encoding="utf-8", errors="replace"))
            except (ValueError, OSError) as e:
                return self.send(404, f"Cannot open {rel}: {e}")
        self.send(404, "Not found")

    def do_POST(self):
        origin = self.headers.get("Origin")
        if (not self.host_ok() or self.headers.get("X-CodeView") != "1"
                or (origin and origin.split("//", 1)[-1] not in self.server.allowed_hosts)):
            return self.send(403, "Forbidden")
        if urlparse(self.path).path != "/api/run":
            return self.send(404, "Not found")
        length = int(self.headers.get("Content-Length") or 0)
        if length > 2_000_000:
            return self.send(413, "Too large")
        try:
            req = json.loads(self.rfile.read(length) or b"{}")
            timeout = max(1, min(int(req.get("timeout") or 10), MAX_TIMEOUT))
            res = run_code(req.get("code", ""), req.get("path") or None, req.get("stdin", ""),
                           timeout, req.get("main"))
        except ValueError as e:
            return self.send_json({"phase": "compile", "ok": False, "output": str(e)}, 400)
        label = req.get("path") or "scratch"
        status = "ok" if res.get("ok") else ("timeout" if res.get("timedOut") else "failed")
        print(f"  run {label}  ->  {res.get('phase')} {status}", flush=True)
        self.send_json(res)


# ---------------------------------------------------------------- tray + shortcuts

def already_running(port):
    """True when a codeview is already answering on this port."""
    try:
        with urllib.request.urlopen(f"http://127.0.0.1:{port}/api/info", timeout=1) as r:
            return "jdk" in json.load(r)
    except (OSError, ValueError):
        return False


def draw_icon(size=256):
    """Blue rounded square with white < > chevrons, the same mark as the page favicon."""
    from PIL import Image, ImageDraw
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([0, 0, size - 1, size - 1], radius=int(size * 0.22), fill=(53, 116, 240, 255))
    w = max(2, int(size * 0.095))
    for pts in (((.40, .27), (.20, .50), (.40, .73)), ((.60, .27), (.80, .50), (.60, .73))):
        xy = [(x * size, y * size) for x, y in pts]
        d.line(xy, fill="white", width=w, joint="curve")
        for x, y in xy:                                   # round the line ends
            d.ellipse([x - w / 2, y - w / 2, x + w / 2, y + w / 2], fill="white")
    return img


def ensure_icon():
    if not ICON.exists():
        sizes = [(16, 16), (24, 24), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)]
        draw_icon().save(ICON, sizes=sizes)
    return ICON


def install_shortcuts():
    """Desktop + Start-menu shortcuts that start the tray version with no console."""
    ensure_icon()
    pyw = pathlib.Path(sys.executable).with_name("pythonw.exe")
    if not pyw.exists():
        sys.exit(f"pythonw.exe not found next to {sys.executable}")
    script = HERE / "codeview.py"
    ps = (
        "$w = New-Object -ComObject WScript.Shell\n"
        "foreach ($dir in @([Environment]::GetFolderPath('Desktop'), [Environment]::GetFolderPath('Programs'))) {\n"
        "  $path = Join-Path $dir 'Code Viewer.lnk'\n"
        "  $s = $w.CreateShortcut($path)\n"
        f"  $s.TargetPath = '{pyw}'\n"
        f"  $s.Arguments = '\"{script}\" --tray'\n"
        f"  $s.WorkingDirectory = '{HERE}'\n"
        f"  $s.IconLocation = '{ICON},0'\n"
        "  $s.Description = 'Read and run the Java practice files in a browser'\n"
        "  $s.Save()\n"
        "  Write-Output \"  created $path\"\n"
        "}\n")
    subprocess.run(["powershell", "-NoProfile", "-NonInteractive", "-Command", ps], check=True)


def run_tray(server, url, open_browser):
    try:
        import pystray
        from PIL import Image
    except ImportError:
        sys.exit("The tray icon needs pystray and Pillow:  pip install pystray pillow")
    threading.Thread(target=server.serve_forever, daemon=True).start()

    def quit_(icon, _item):
        icon.stop()
        server.shutdown()

    def setup(icon):
        icon.visible = True
        if open_browser:
            webbrowser.open(url)

    menu = pystray.Menu(
        pystray.MenuItem("Open Code Viewer", lambda *_: webbrowser.open(url), default=True),
        pystray.MenuItem("Open repo folder", lambda *_: os.startfile(REPO)),
        pystray.Menu.SEPARATOR,
        pystray.MenuItem("Quit Code Viewer", quit_))
    image = Image.open(ICON) if ICON.exists() else draw_icon()
    pystray.Icon("codeview", image, f"Code Viewer - {url}", menu).run(setup=setup)


def main():
    ap = argparse.ArgumentParser(description="Read and run the practice files in a browser.")
    ap.add_argument("--port", type=int, default=8025)
    ap.add_argument("--no-open", action="store_true", help="do not open a browser tab")
    ap.add_argument("--tray", action="store_true", help="run with a tray icon and no console")
    ap.add_argument("--install-shortcuts", action="store_true",
                    help="create Desktop and Start-menu shortcuts that start the tray version")
    a = ap.parse_args()

    if a.install_shortcuts:
        return install_shortcuts()
    if already_running(a.port):
        url = f"http://127.0.0.1:{a.port}/"
        print(f"codeview is already running at {url} - opening it.")
        if not a.no_open:
            webbrowser.open(url)
        return

    server = None
    for port in range(a.port, a.port + 10):
        try:
            server = ThreadingHTTPServer(("127.0.0.1", port), Handler)
            break
        except OSError:
            continue
    if server is None:
        sys.exit(f"Ports {a.port}-{a.port + 9} are all busy.")
    port = server.server_address[1]
    server.allowed_hosts = {f"127.0.0.1:{port}", f"localhost:{port}"}
    server.jdk = jdk_version()
    url = f"http://127.0.0.1:{port}/"
    if a.tray:
        return run_tray(server, url, not a.no_open)

    print(f"codeview  {url}")
    print(f"  repo {REPO}")
    print(f"  JDK  {server.jdk}  ({JAVA})")
    print("  Ctrl+C to stop", flush=True)
    if not a.no_open:
        webbrowser.open(url)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("stopped")


if __name__ == "__main__":
    main()
