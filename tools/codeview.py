#!/usr/bin/env python3
"""
codeview - read, practise and run the practice files (or any folder) in a browser.

    tools/codeview                       start on http://127.0.0.1:8025 and open a tab
    tools/codeview --port 9000           use another port
    tools/codeview --no-open             do not open a browser tab
    tools/codeview --tray                no console window; a tray icon to reopen or quit
    tools/codeview --install-shortcuts   "Code Viewer" on the Desktop and Start menu (tray version)
    tools/codeview --autostart on|off    start the tray version when Windows starts

Starting it again while it is already running just opens a browser tab.

THE PAGE
  Files      every file as a tree; Ctrl+P filters by name, "Show" narrows to must-know,
             not-done or to-revise; new files appear by themselves (the tree refreshes)
  Search     Ctrl+Shift+F searches inside every file
  Run        Ctrl+Enter compiles what is in the editor and ticks each "expected" line
  Edit/Save  Edit (Ctrl+E) changes a draft kept in the browser; Save (Ctrl+S) writes it
             to the file. New file (+) creates one from a template
  Practice   hides the solution bodies and the APPROACH notes; when every expected line
             matches, the file is marked done
  Progress   mark each file Done or Revise; counts per folder and overall
  Folders    the folder menu opens any other folder, not just this repo

Runs Java the same way as runjava: compile to a temp folder, then run whichever class
declares main(), so the filename never matters. Newest installed JDK, not JAVA_HOME.

STATE: progress and the folder list live in tools/codeview-state.json (git-ignored,
backed up by OneDrive). Drafts and practice attempts live in the browser.

SECURITY: this executes code and writes files, so it listens on 127.0.0.1 only, and
every request must carry this server's own Host; anything that changes something also
needs the page's X-CodeView header and a same-origin Origin. A web page you happen to
visit cannot make it run or write anything.
"""
import argparse, hashlib, json, os, pathlib, re, shutil, subprocess, sys, tempfile, threading, time
import urllib.request, webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parent
sys.path.insert(0, str(HERE))
import javasrc  # noqa: E402  main()/package detection and practice skeletons
import outcheck  # noqa: E402  ticks and crosses for "actual   expected X" output lines
from runjava import JAVA, JAVAC  # noqa: E402  same newest-JDK pick as the CLI runner

PAGE = HERE / "codeview.html"
ICON = HERE / "codeview.ico"
STATE_FILE = pathlib.Path(os.environ.get("CODEVIEW_STATE") or HERE / "codeview-state.json")
# A page loaded before an update keeps running its old JavaScript (seen 3 Oct: Run still
# showed the old crosses). The page carries the version it was served with and compares
# it with /api/info, so it can offer a reload. Server code counts as of this start-up.
SERVER_CODE = hashlib.sha1(b"".join((HERE / n).read_bytes() for n in
                                    ("codeview.py", "outcheck.py", "javasrc.py", "runjava.py"))).hexdigest()


def page_version(html):
    return hashlib.sha1(html + SERVER_CODE.encode()).hexdigest()[:12]
# Started from the tray (pythonw) there is no console, so every javac/java run would
# flash a new console window. This flag suppresses that; it is 0 off Windows.
NO_WINDOW = getattr(subprocess, "CREATE_NO_WINDOW", 0)

SKIP_ALWAYS = {".git", ".idea", ".vscode", "__pycache__", "node_modules", "target", "build",
               "dist", "out", ".gradle", "venv", ".venv", ".mypy_cache", ".pytest_cache"}
SKIP_IN_REPO = {"tools", "_personal", "08-Reference"}      # only for this repo
TEXT_EXT = {".java", ".kt", ".kts", ".scala", ".groovy", ".gradle", ".md", ".txt", ".sql",
            ".properties", ".xml", ".json", ".yml", ".yaml", ".toml", ".ini", ".cfg", ".csv",
            ".py", ".js", ".mjs", ".ts", ".tsx", ".jsx", ".go", ".rs", ".c", ".h", ".cpp",
            ".cs", ".sh", ".ps1", ".bat", ".cmd", ".html", ".css", ".ahk"}
MAX_FILES = 20000                # per folder tree; a whole drive would be pointless anyway
MAX_VIEW = 2 * 1024 * 1024       # bytes; bigger files are not opened
OUTPUT_CAP = 256 * 1024          # bytes of program output sent back to the page
MAX_TIMEOUT = 60                 # seconds; the page offers 5 / 10 / 30 / 60
IS_WIN, IS_MAC = os.name == "nt", sys.platform == "darwin"
# Tray icon, shortcuts and start-at-login are Windows extras; everything else is portable.
STARTUP_LNK = pathlib.Path(os.environ.get("APPDATA", "")) / \
    "Microsoft/Windows/Start Menu/Programs/Startup/Code Viewer.lnk" if IS_WIN else None


def autostart_on():
    """True/False on Windows; None where start-at-login is not supported."""
    return STARTUP_LNK.exists() if IS_WIN else None


# ---------------------------------------------------------------- state: folders + progress

class State:
    """tools/codeview-state.json: {"roots": [paths], "progress": {root path: {file: entry}}}."""

    def __init__(self):
        self.lock = threading.Lock()
        try:
            self.data = json.loads(STATE_FILE.read_text(encoding="utf-8"))
        except (OSError, ValueError):
            self.data = {}
        self.data.setdefault("roots", [])
        self.data.setdefault("progress", {})

    def save(self):
        tmp = STATE_FILE.with_suffix(".tmp")
        tmp.write_text(json.dumps(self.data, indent=1, sort_keys=True), encoding="utf-8")
        os.replace(tmp, STATE_FILE)

    def roots(self):
        """[{id, name, path, default}] - this repo first, then added folders."""
        out, seen = [], set()
        for i, p in enumerate([str(REPO)] + [r for r in self.data["roots"] if r != str(REPO)]):
            name = pathlib.Path(p).name or p
            rid = re.sub(r"[^A-Za-z0-9_-]+", "-", name).strip("-").lower() or "folder"
            base, n = rid, 2
            while rid in seen:
                rid, n = f"{base}-{n}", n + 1
            seen.add(rid)
            out.append({"id": rid, "name": name, "path": p, "default": i == 0})
        return out

    def root(self, rid):
        for r in self.roots():
            if r["id"] == rid:
                return r
        raise ValueError(f"unknown folder '{rid}'")

    def add_root(self, path):
        p = pathlib.Path(path).expanduser().resolve()
        if not p.is_dir():
            raise ValueError(f"not a folder: {path}")
        if p.parent == p:
            raise ValueError("a whole drive is too big to open - pick a project folder")
        with self.lock:
            if str(p) != str(REPO) and str(p) not in self.data["roots"]:
                self.data["roots"].append(str(p))
                self.save()
        return next(r for r in self.roots() if r["path"] == str(p))

    def remove_root(self, rid):
        r = self.root(rid)
        if r["default"]:
            raise ValueError("this repo cannot be removed")
        with self.lock:
            self.data["roots"] = [x for x in self.data["roots"] if x != r["path"]]
            self.save()

    def progress(self, rid):
        return self.data["progress"].get(self.root(rid)["path"], {})

    def set_progress(self, rid, rel, status, practice_pass=None):
        key = self.root(rid)["path"]
        with self.lock:
            files = self.data["progress"].setdefault(key, {})
            entry = files.get(rel, {})
            if status in ("done", "revise"):
                entry["s"] = status
                entry["t"] = time.strftime("%Y-%m-%d")
            elif status == "none":
                entry.pop("s", None)
            if practice_pass is not None:
                entry["practiced"] = time.strftime("%Y-%m-%d")
                entry["pass"] = bool(practice_pass)
            if entry:
                files[rel] = entry
            else:
                files.pop(rel, None)
            self.save()
            return entry


STATE = State()


# ---------------------------------------------------------------- files

def root_path(rid):
    return pathlib.Path(STATE.root(rid)["path"])


def skip_dirs(base):
    return SKIP_ALWAYS | (SKIP_IN_REPO if base == REPO else set())


def resolve(rid, rel):
    """Folder id + relative path -> absolute path, refusing anything outside that folder."""
    base = root_path(rid)
    p = (base / rel).resolve()
    if p != base and base not in p.parents:
        raise ValueError("outside the folder")
    if any(part in skip_dirs(base) for part in p.relative_to(base).parts):
        raise ValueError("excluded folder")
    return p


def walk(base):
    """Yield text files under base, skipping build/VCS folders, at most MAX_FILES."""
    skip, count = skip_dirs(base), 0
    for dirpath, dirnames, filenames in os.walk(base):
        dirnames[:] = sorted(d for d in dirnames if d not in skip and not d.startswith("."))
        for f in sorted(filenames, key=str.lower):
            if pathlib.Path(f).suffix.lower() in TEXT_EXT:
                yield pathlib.Path(dirpath) / f
                count += 1
                if count >= MAX_FILES:
                    return


def build_tree(rid):
    base = root_path(rid)
    root = {"name": base.name, "dirs": {}, "files": []}
    for p in walk(base):
        rel = p.relative_to(base)
        node = root
        for part in rel.parts[:-1]:
            node = node["dirs"].setdefault(part, {"name": part, "dirs": {}, "files": []})
        f = {"name": p.name, "path": rel.as_posix()}
        if p.suffix == ".java" and "MUST-KNOW" in read_head(p):
            f["must"] = True
        node["files"].append(f)

    def finish(n):
        n["dirs"] = [finish(d) for _, d in sorted(n["dirs"].items(), key=lambda kv: kv[0].lower())]
        return n
    return finish(root)


def read_head(p, n=1500):
    try:
        with open(p, "rb") as fh:
            return fh.read(n).decode("utf-8", "replace")
    except OSError:
        return ""


def digest(data):
    return hashlib.sha1(data).hexdigest()


def read_file(rid, rel):
    p = resolve(rid, rel)
    if p.stat().st_size > MAX_VIEW:
        raise ValueError(f"{p.name} is over {MAX_VIEW // 1024 // 1024} MB")
    data = p.read_bytes()
    return {"text": data.decode("utf-8", "replace"), "hash": digest(data)}


def save_file(rid, rel, text, base_hash, force):
    p = resolve(rid, rel)
    if p.exists() and not force and base_hash and digest(p.read_bytes()) != base_hash:
        return {"conflict": True}
    data = text.encode("utf-8")
    tmp = p.with_name(p.name + ".codeview-tmp")
    tmp.write_bytes(data)
    os.replace(tmp, p)
    return {"ok": True, "hash": digest(data)}


def java_template(name):
    cls = re.sub(r"^[A-D]\d\d_", "", pathlib.Path(name).stem)
    if not re.fullmatch(r"[A-Za-z_$][\w$]*", cls):
        cls = "Main"
    title = re.sub(r"(?<=[a-z0-9])(?=[A-Z])", " ", cls)
    return f"""/*
 * =====================================================================
 *  {title:<40} LeetCode ? | ?
 * =====================================================================
 *
 * PROBLEM
 *   ...
 *
 * EXAMPLE
 *   ...
 *
 * APPROACH
 *   ...
 *
 * KEY INSIGHT
 *   ...
 *
 * COMPLEXITY
 *   Time O(?), space O(?).
 *
 * RUN
 *   main() runs the cases below and prints actual vs expected.
 */
class {cls} {{

    static int solve(int[] nums) {{
        return 0;
    }}

    private static void check(String label, Object actual, Object expected) {{
        System.out.println(label + ": " + actual + "   expected " + expected);
    }}

    public static void main(String[] args) {{
        check("case 1 typical", solve(new int[]{{1, 2, 3}}), 6);
        check("case 2 empty  ", solve(new int[]{{}}), 0);
    }}
}}
"""


def new_file(rid, rel):
    rel = rel.strip().replace("\\", "/").strip("/")
    if not rel or ".." in rel.split("/"):
        raise ValueError("give a file name such as 01-Arrays/C16_MyProblem.java")
    p = resolve(rid, rel)
    if p.suffix.lower() not in TEXT_EXT:
        raise ValueError(f"'{p.suffix or 'no extension'}' is not a text file type this viewer shows")
    if p.exists():
        raise ValueError(f"{rel} already exists")
    p.parent.mkdir(parents=True, exist_ok=True)
    text = java_template(p.name) if p.suffix == ".java" else (f"# {p.stem}\n" if p.suffix == ".md" else "")
    p.write_text(text, encoding="utf-8")
    return {"path": p.relative_to(root_path(rid)).as_posix()}


def search(rid, q, regex, case):
    if not q:
        return {"files": [], "matches": 0}
    try:
        pat = re.compile(q if regex else re.escape(q), 0 if case else re.I)
    except re.error as e:
        raise ValueError(f"bad regular expression: {e}")
    base, files, total = root_path(rid), [], 0
    for p in walk(base):
        try:
            if p.stat().st_size > MAX_VIEW:
                continue
            text = p.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        hits = []
        for no, line in enumerate(text.splitlines(), 1):
            m = pat.search(line)
            if m and m.end() > m.start():
                cut = max(0, m.start() - 60)
                hits.append({"line": no, "col": m.start() + 1, "len": m.end() - m.start(),
                             "text": line[cut:cut + 220], "off": m.start() - cut})
                if len(hits) >= 50:
                    break
        if hits:
            files.append({"path": p.relative_to(base).as_posix(), "hits": hits})
            total += len(hits)
            if total >= 2000:
                break
    return {"files": files, "matches": total, "capped": total >= 2000}


def pick_folder():
    """Native folder chooser, shown on top of the browser."""
    if IS_MAC:
        # Tk may only open windows on the main thread on macOS, and this runs on a
        # request thread, so ask the system dialog through AppleScript instead.
        r = subprocess.run(["osascript", "-e", 'POSIX path of (choose folder with prompt '
                            '"Open a folder in Code Viewer")'], capture_output=True, text=True)
        return r.stdout.strip() or None
    import tkinter
    from tkinter import filedialog
    root = tkinter.Tk()
    root.withdraw()
    root.attributes("-topmost", True)
    try:
        path = filedialog.askdirectory(parent=root, title="Open a folder in Code Viewer", mustexist=True)
    finally:
        root.destroy()
    return path or None


def open_path(p):
    """Open a folder in the system file manager."""
    if IS_WIN:
        os.startfile(p)
    else:
        subprocess.Popen(["open" if IS_MAC else "xdg-open", str(p)])


def reveal(rid, rel):
    """Show the file selected in Explorer / Finder (Linux: open its folder)."""
    p = resolve(rid, rel) if rel else root_path(rid)
    if p.is_file() and IS_WIN:
        subprocess.Popen(["explorer", "/select,", str(p)])
    elif p.is_file() and IS_MAC:
        subprocess.Popen(["open", "-R", str(p)])
    else:
        open_path(p if p.is_dir() else p.parent)


# ---------------------------------------------------------------- compile + run

def run_code(code, rid, rel_path, stdin, timeout, want_main):
    src_dir, fname = None, "Scratch.java"
    if rel_path:
        p = resolve(rid, rel_path)
        src_dir, fname = p.parent, p.name
    pkg, mains, public, compact = javasrc.analyse(code)
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
                    "output": tidy(c.stderr + c.stdout, work)}
        if not mains:
            return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms,
                    "output": "Compiled, but no class declares main() - nothing to run."}

        main = want_main if want_main in mains else javasrc.pick_main(mains, fname)
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
        output = tidy(text, work)
        verdicts, checks = check_lines(output)
        return {"phase": "run", "ok": rc == 0 and not timed_out, "exitCode": rc,
                "timedOut": timed_out, "truncated": size > OUTPUT_CAP, "file": fname,
                "ran": main, "mains": mains, "compileMs": compile_ms, "runMs": run_ms,
                "output": output, "verdicts": verdicts, "checks": checks}
    finally:
        shutil.rmtree(work, ignore_errors=True)


def check_lines(output):
    """One verdict per output line (True / False / None), split exactly as the page splits.
    `unchecked` counts result-looking lines the checker would not call either way."""
    text = output.replace("\r\n", "\n").replace("\r", "\n")
    if text.endswith("\n"):
        text = text[:-1]
    verdicts, checks = [], {"pass": 0, "fail": 0, "unchecked": 0}
    for line in text.split("\n"):
        v = outcheck.judge(line) if "expected" in line.lower() else None
        verdicts.append(v)
        if v is True:
            checks["pass"] += 1
        elif v is False:
            checks["fail"] += 1
        elif "expected" in line.lower() and not outcheck.ERROR_LINE.search(line):
            checks["unchecked"] += 1
    return verdicts, checks


def tidy(text, work):
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
        q = {k: v[0] for k, v in parse_qs(u.query).items()}
        rid = q.get("root", "")
        try:
            if u.path == "/":
                html = PAGE.read_bytes()
                return self.send(200, html.replace(b"__CV_VERSION__", page_version(html).encode()),
                                 "text/html; charset=utf-8")
            if u.path == "/api/info":
                return self.send_json({"jdk": self.server.jdk, "repo": REPO.name, "version": page_version(PAGE.read_bytes()),
                                       "autostart": autostart_on(), "platform": sys.platform})
            if u.path == "/api/roots":
                return self.send_json(STATE.roots())
            if u.path == "/api/tree":
                return self.send_json(build_tree(rid))
            if u.path == "/api/file":
                return self.send_json(read_file(rid, q.get("path", "")))
            if u.path == "/api/practice":
                text = read_file(rid, q.get("path", ""))["text"]
                skeleton, hidden = javasrc.practice_skeleton(text)
                return self.send_json({"text": skeleton, "hidden": hidden})
            if u.path == "/api/progress":
                return self.send_json(STATE.progress(rid))
            if u.path == "/api/search":
                return self.send_json(search(rid, q.get("q", ""), q.get("regex") == "1", q.get("case") == "1"))
        except (ValueError, OSError) as e:
            return self.send_json({"error": str(e)}, 400)
        self.send(404, "Not found")

    def do_POST(self):
        origin = self.headers.get("Origin")
        if (not self.host_ok() or self.headers.get("X-CodeView") != "1"
                or (origin and origin.split("//", 1)[-1] not in self.server.allowed_hosts)):
            return self.send(403, "Forbidden")
        length = int(self.headers.get("Content-Length") or 0)
        if length > 4_000_000:
            return self.send(413, "Too large")
        path = urlparse(self.path).path
        try:
            req = json.loads(self.rfile.read(length) or b"{}")
            rid = req.get("root", "")
            if path == "/api/run":
                timeout = max(1, min(int(req.get("timeout") or 10), MAX_TIMEOUT))
                res = run_code(req.get("code", ""), rid, req.get("path") or None, req.get("stdin", ""),
                               timeout, req.get("main"))
                status = "ok" if res.get("ok") else ("timeout" if res.get("timedOut") else "failed")
                print(f"  run {req.get('path') or 'scratch'}  ->  {res.get('phase')} {status}", flush=True)
                return self.send_json(res)
            if path == "/api/save":
                res = save_file(rid, req["path"], req.get("code", ""), req.get("base"), req.get("force"))
                return self.send_json(res, 409 if res.get("conflict") else 200)
            if path == "/api/new":
                return self.send_json(new_file(rid, req.get("path", "")))
            if path == "/api/progress":
                return self.send_json(STATE.set_progress(rid, req["path"], req.get("status"), req.get("pass")))
            if path == "/api/roots":
                if req.get("action") == "remove":
                    STATE.remove_root(req.get("id", ""))
                    return self.send_json({"roots": STATE.roots()})
                added = STATE.add_root(req.get("path", ""))
                return self.send_json({"roots": STATE.roots(), "added": added})
            if path == "/api/pick-folder":
                return self.send_json({"path": pick_folder()})
            if path == "/api/reveal":
                reveal(rid, req.get("path", ""))
                return self.send_json({"ok": True})
            if path == "/api/autostart":
                set_autostart(bool(req.get("enabled")))
                return self.send_json({"autostart": autostart_on()})
        except (ValueError, KeyError, OSError, subprocess.CalledProcessError) as e:
            return self.send_json({"error": str(e)}, 400)
        self.send(404, "Not found")


# ---------------------------------------------------------------- tray, shortcuts, autostart

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


def make_shortcut(lnk, args):
    """A .lnk that runs this script under pythonw (no console) with the given arguments."""
    ensure_icon()
    pyw = pathlib.Path(sys.executable).with_name("pythonw.exe")
    if not pyw.exists():
        raise ValueError(f"pythonw.exe not found next to {sys.executable}")
    script = HERE / "codeview.py"
    ps = (
        "$s = (New-Object -ComObject WScript.Shell).CreateShortcut($env:CV_LNK)\n"
        "$s.TargetPath = $env:CV_TARGET\n"
        "$s.Arguments = $env:CV_ARGS\n"
        "$s.WorkingDirectory = $env:CV_DIR\n"
        "$s.IconLocation = $env:CV_ICON + ',0'\n"
        "$s.Description = 'Read, practise and run code in a browser'\n"
        "$s.Save()\n")
    env = dict(os.environ, CV_LNK=str(lnk), CV_TARGET=str(pyw), CV_ARGS=f'"{script}" {args}',
               CV_DIR=str(HERE), CV_ICON=str(ICON))
    pathlib.Path(lnk).parent.mkdir(parents=True, exist_ok=True)
    subprocess.run(["powershell", "-NoProfile", "-NonInteractive", "-Command", ps], check=True,
                   env=env, creationflags=NO_WINDOW, stdin=subprocess.DEVNULL, capture_output=True)


def install_shortcuts():
    """Desktop + Start-menu shortcuts that start the tray version."""
    if not IS_WIN:
        sys.exit("Shortcuts are Windows-only. On macOS or Linux run tools/codeview from a terminal.")
    ps ="[Environment]::GetFolderPath('Desktop'); [Environment]::GetFolderPath('Programs')"
    dirs = subprocess.run(["powershell", "-NoProfile", "-Command", ps], capture_output=True,
                          text=True, check=True).stdout.splitlines()
    for d in dirs:
        if d.strip():
            lnk = pathlib.Path(d.strip()) / "Code Viewer.lnk"
            make_shortcut(lnk, "--tray")
            print(f"  created {lnk}")


def set_autostart(enabled):
    if not IS_WIN:
        raise ValueError("Start-at-login is only set up on Windows")
    if enabled:
        make_shortcut(STARTUP_LNK, "--tray --no-open")
    elif STARTUP_LNK.exists():
        STARTUP_LNK.unlink()


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

    def toggle_autostart(icon, _item):
        set_autostart(not STARTUP_LNK.exists())

    def setup(icon):
        icon.visible = True
        if open_browser:
            webbrowser.open(url)

    menu = pystray.Menu(
        pystray.MenuItem("Open Code Viewer", lambda *_: webbrowser.open(url), default=True),
        pystray.MenuItem("Open repo folder", lambda *_: open_path(REPO)),
        pystray.MenuItem("Start with Windows", toggle_autostart, checked=lambda _i: bool(autostart_on()),
                         visible=IS_WIN),
        pystray.Menu.SEPARATOR,
        pystray.MenuItem("Quit Code Viewer", quit_))
    image = Image.open(ICON) if ICON.exists() else draw_icon()
    pystray.Icon("codeview", image, f"Code Viewer - {url}", menu).run(setup=setup)


def main():
    ap = argparse.ArgumentParser(description="Read, practise and run code in a browser.")
    ap.add_argument("--port", type=int, default=8025)
    ap.add_argument("--no-open", action="store_true", help="do not open a browser tab")
    ap.add_argument("--tray", action="store_true", help="run with a tray icon and no console")
    ap.add_argument("--install-shortcuts", action="store_true",
                    help="create Desktop and Start-menu shortcuts that start the tray version")
    ap.add_argument("--autostart", choices=["on", "off"],
                    help="start the tray version when Windows starts (or stop doing so)")
    a = ap.parse_args()

    if a.install_shortcuts:
        return install_shortcuts()
    if a.autostart:
        if not IS_WIN:
            sys.exit("--autostart is Windows-only. On macOS or Linux run tools/codeview from a terminal.")
        set_autostart(a.autostart == "on")
        print(f"  start with Windows: {'on' if autostart_on() else 'off'}  ({STARTUP_LNK})")
        return
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
