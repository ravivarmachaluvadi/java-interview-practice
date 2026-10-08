#!/usr/bin/env python3
"""
codeview - read, practise and run the practice files (or any folder) in a browser.

    tools/codeview                       start on http://127.0.0.1:8025 and open a tab
    tools/codeview --port 9000           use another port
    tools/codeview --no-open             do not open a browser tab
    tools/codeview --tray                no console window; a tray icon to reopen, restart
                                         or quit, and to pick the browser it opens (Open in)
    tools/codeview --install-shortcuts   "Code Viewer" on the Desktop and Start menu (tray version)
    tools/codeview --autostart on|off    start the tray version when Windows starts
    tools/codeview --open PATH           open the page on this file or folder (any folder:
                                         one outside the list is added to it)
    tools/codeview --context-menu on|off "Open in Code Viewer" in File Explorer's right-click
                                         menu, for files, folders and a folder's empty space
                                         (Windows 11: under "Show more options", or Shift+right-click)

Starting it again while it is already running just opens a browser tab.

THE PAGE
  Files      every file as a tree; Ctrl+P filters by name, "Show" narrows to must-know,
             not-done or to-revise; new files appear by themselves (the tree refreshes)
  Search     Ctrl+Shift+F searches inside every file
  Run        Ctrl+Enter runs what is in the editor and ticks each "expected" line: Java
             compiled first, Python (.py) with the python.exe beside this server's Python,
             JavaScript (.js, .mjs) with Node.js. Practice, Try, Compare and Next work the
             same for all three
  UI pages   a .html shows beside its code, live, in a locked frame (no access to this page
             or the server); Ctrl+Enter runs its <script type="test"> block there and ticks
             its checks. Practice hides the page's function bodies, not its markup or CSS
  Assist     Java autocomplete like IntelliJ's while editing (Edit, Try, Scratch; Practice
             only if switched on in the menu): members after a dot with real parameter
             names, JDK classes with their import, live and postfix templates, parameter
             hints, Javadoc on hover, and javac's errors underlined as you type. One
             background JVM (tools/CvAssist.java) answers; it stops after 20 idle minutes
  Edit/Save  Edit (Ctrl+E) changes a draft kept in the browser; Save (Ctrl+S) writes it
             to the file. New file (+) creates one from a template; ⋯ → Delete this file
             moves one to the Recycle Bin (Windows)
  Try        Alt+T opens a throwaway copy of the file to change and run; the file is
             never touched and Save is off. The copy stays in the browser until Discard copy,
             one at a time (Try on another file deletes it); Compare (Alt+C) puts it next to
             the file with every change marked
  Visual     Alt+V plays the solution step by step on a picture, on the file's EXAMPLE
             inputs or your own (Trapping Rain Water and Largest Rectangle for now; not in
             practice mode)
  Practice   hides the solution bodies and the APPROACH notes; when every expected line
             matches, the file is marked done. While practising: a timer (limit in the
             menu), Hint (Alt+H) shows the hidden notes one at a time, gentlest first, and
             Compare (Alt+C) puts your attempt next to the original solution
  Scratch    a pad kept in the browser for any code, in Java, Python, JavaScript or HTML (one
             text each); its timer starts stopped (click ⏱ Start) and ⋯ → Restart the timer
             zeroes it for the next problem
  Progress   mark each file Done or Revise; counts per folder and overall. A done file
             comes back for review after 3, 7, 21 and 60 days (◷ in the list); Next (Alt+J)
             opens due reviews first, then to-revise, then must-know not done
  Folders    the folder menu opens any other folder, not just this repo. A folder opened
             from Explorer's right-click shows a listing: its subfolders and files with
             their done / revise / must-know marks
  Right-click a file or folder in the list: Open in browser (a folder as the browser's own
             "Index of" page, a file as itself), its folder in the browser, a new Code Viewer
             tab, Show in File Explorer, Copy path. Shift+right-click keeps the browser's menu
  Full screen  the corners button (Alt+Enter) hides the browser's tabs, address and bookmarks
             bars and the page header, for small screens; hold Esc or Alt+Enter to leave
  Offline    the editor, markdown and diagram libraries are downloaded once (tools/offline.py), so
             the page works without internet after the first start with it
  Stopped?   an open tab says so at once, with how to start it again

Runs Java the same way as runjava: compile to a temp folder, then run whichever class
declares main(), so the filename never matters. Newest installed JDK, not JAVA_HOME.
Python and Node run a copy in a temp folder too; a .py's own folder is on PYTHONPATH (so
`import helper` works) and python -B leaves no __pycache__ there.

STATE: progress, the folder list and the tray's browser choice live in
tools/codeview-state.json (git-ignored, backed up by OneDrive). Drafts, practice attempts,
their timers and opened hints live in the browser, and each browser (and each Chrome
profile) keeps its own. So the tray's "Open in" menu pins the one to open; until it is
set, the Windows default browser opens.

LOG: tools/codeview.log (git-ignored) has a line when it starts (with what started it),
when it stops and why, and any error. A start with no stop before the next start means
something outside ended it; the next start says so. Tray Quit is a submenu, so stopping
takes a second click; tray Restart starts a fresh copy, which a change to the server code
(codeview.py, javasrc.py, ...) needs before it takes effect.

TESTS: python tools/test_codeview.py

SECURITY: this executes code and writes files, so it listens on 127.0.0.1 only, and
every request must carry this server's own Host; anything that changes something also
needs the page's X-CodeView header and a same-origin Origin. A web page you happen to
visit cannot make it run or write anything.
"""
import argparse, atexit, hashlib, itertools, json, logging, logging.handlers, os, pathlib, queue, re, shutil
import subprocess, sys, tempfile, threading, time, urllib.error, urllib.request, webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, quote, unquote, urlparse

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parent
sys.path.insert(0, str(HERE))
import javasrc  # noqa: E402  main()/package detection and practice skeletons
import langsrc  # noqa: E402  practice skeletons for Python, JavaScript and UI (.html) files
import offline  # noqa: E402  local copy of the page's editor + markdown libraries
import outcheck  # noqa: E402  ticks and crosses for "actual   expected X" output lines
from runjava import JAVA, JAVAC  # noqa: E402  same newest-JDK pick as the CLI runner

PAGE = HERE / "codeview.html"
ICON = HERE / "codeview.ico"
STATE_FILE = pathlib.Path(os.environ.get("CODEVIEW_STATE") or HERE / "codeview-state.json")
LOG_FILE = pathlib.Path(os.environ.get("CODEVIEW_LOG") or HERE / "codeview.log")
# Test servers (test_ui.py, test_codeview.py) set this so a Push click can never reach GitHub
# from this repo; their pushes go to throwaway repos.
NO_PUSH = bool(os.environ.get("CODEVIEW_NO_PUSH"))
# A page loaded before an update keeps running its old JavaScript (seen 3 Oct: Run still
# showed the old crosses). The page carries the version it was served with and compares
# it with /api/info, so it can offer a reload. Server code counts as of this start-up.
SERVER_CODE = hashlib.sha1(b"".join((HERE / n).read_bytes() for n in
                                    ("codeview.py", "outcheck.py", "javasrc.py", "runjava.py",
                                     "offline.py", "CvAssist.java", "gen_readmes.py",
                                     "check_headers.py", "langsrc.py"))).hexdigest()


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


def find_python():
    """python.exe beside this interpreter: the tray runs under pythonw.exe, whose print() goes
    nowhere. Elsewhere this interpreter itself."""
    if not IS_WIN:
        return sys.executable
    p = pathlib.Path(sys.executable).with_name("python.exe")
    return str(p) if p.exists() else shutil.which("python")


def find_node():
    """node on PATH, else its usual Windows home (a tray started at login may have an older PATH)."""
    found = shutil.which("node")
    if found or not IS_WIN:
        return found
    p = pathlib.Path(os.environ.get("ProgramFiles", r"C:\Program Files")) / "nodejs" / "node.exe"
    return str(p) if p.exists() else None


# 8 Oct: .py files run with this Python, .js with Node (None: that language cannot run here)
PYTHON, NODE = find_python(), find_node()
# Tray icon, shortcuts and start-at-login are Windows extras; everything else is portable.
STARTUP_LNK = pathlib.Path(os.environ.get("APPDATA", "")) / \
    "Microsoft/Windows/Start Menu/Programs/Startup/Code Viewer.lnk" if IS_WIN else None


def autostart_on():
    """True/False on Windows; None where start-at-login is not supported."""
    return STARTUP_LNK.exists() if IS_WIN else None


# ---------------------------------------------------------------- log
# On 3 Oct it vanished twice and nothing said why. The log has one line when it starts,
# one when it stops and why, and any error. A start with no matching stop means
# something outside ended it, and the next start says so.

log = logging.getLogger("codeview")
_stop_logged = False


def setup_log():
    if any(isinstance(h, logging.handlers.RotatingFileHandler) for h in log.handlers):
        return
    try:
        h = logging.handlers.RotatingFileHandler(LOG_FILE, maxBytes=200_000, backupCount=1, encoding="utf-8")
    except OSError:
        return                                   # no log is better than no viewer
    h.setFormatter(logging.Formatter("%(asctime)s pid %(process)d %(message)s", "%Y-%m-%d %H:%M:%S"))
    log.addHandler(h)
    log.setLevel(logging.INFO)
    log.propagate = False


def unclosed_run(text):
    """(pid, started at) of the last run in the log if it never logged a stop, else None."""
    last = None
    for line in text.splitlines():
        m = re.match(r"(\S+ \S+) pid (\d+) (started|stopped)\b", line)
        if m and m.group(3) == "started":
            last = (m.group(2), m.group(1))
        elif m and last and m.group(2) == last[0]:
            last = None
    return last


def process_name(pid):
    """The executable name of a process, or '' when it has gone or cannot be read.

    Windows: read from the system's process list, not by opening the process, because a
    normal user may not open svchost - and Task Scheduler (svchost) is exactly the parent
    worth naming (3 Oct: the first try logged 'parent ?')."""
    if IS_WIN:
        import ctypes
        from ctypes import wintypes

        class Entry(ctypes.Structure):                     # PROCESSENTRY32W
            _fields_ = [("dwSize", wintypes.DWORD), ("cntUsage", wintypes.DWORD),
                        ("th32ProcessID", wintypes.DWORD), ("th32DefaultHeapID", ctypes.c_size_t),
                        ("th32ModuleID", wintypes.DWORD), ("cntThreads", wintypes.DWORD),
                        ("th32ParentProcessID", wintypes.DWORD), ("pcPriClassBase", ctypes.c_long),
                        ("dwFlags", wintypes.DWORD), ("szExeFile", ctypes.c_wchar * 260)]
        k32 = ctypes.WinDLL("kernel32", use_last_error=True)
        k32.CreateToolhelp32Snapshot.restype = wintypes.HANDLE
        k32.CreateToolhelp32Snapshot.argtypes = (wintypes.DWORD, wintypes.DWORD)
        k32.Process32FirstW.argtypes = k32.Process32NextW.argtypes = (wintypes.HANDLE, ctypes.POINTER(Entry))
        k32.CloseHandle.argtypes = (wintypes.HANDLE,)
        snap = k32.CreateToolhelp32Snapshot(0x2, 0)        # TH32CS_SNAPPROCESS
        if not snap or snap == wintypes.HANDLE(-1).value:
            return ""
        try:
            e = Entry()
            e.dwSize = ctypes.sizeof(Entry)
            more = k32.Process32FirstW(snap, ctypes.byref(e))
            while more:
                if e.th32ProcessID == pid:
                    return e.szExeFile
                more = k32.Process32NextW(snap, ctypes.byref(e))
            return ""
        finally:
            k32.CloseHandle(snap)
    try:
        return pathlib.Path(f"/proc/{pid}/comm").read_text().strip()
    except OSError:
        return ""


def boot_time():
    """When this PC last booted (seconds since the epoch), or None."""
    try:
        if IS_WIN:
            import ctypes
            k32 = ctypes.windll.kernel32
            k32.GetTickCount64.restype = ctypes.c_ulonglong
            return time.time() - k32.GetTickCount64() / 1000
        with open("/proc/stat") as fh:
            for line in fh:
                if line.startswith("btime"):
                    return float(line.split()[1])
    except (OSError, AttributeError, ValueError):
        pass
    return None


def log_start(port, mode):
    try:
        prev = unclosed_run(LOG_FILE.read_text(encoding="utf-8", errors="replace"))
    except OSError:
        prev = None
    boot = boot_time()
    booted = time.strftime("%Y-%m-%d %H:%M", time.localtime(boot)) if boot else "unknown"
    if prev:
        log.info(f"note: the previous run (pid {prev[0]}, started {prev[1]}) never logged a stop, so "
                 f"something outside ended it: another program, sign-out or shutdown. "
                 f"This PC last booted {booted}.")
    ppid = os.getppid()
    log.info(f"started  {mode}  port {port}  parent {process_name(ppid) or '?'} (pid {ppid})  "
             f"python {sys.version.split()[0]}  booted {booted}")
    atexit.register(log_stop, "exited")


def log_stop(reason):
    global _stop_logged
    if not _stop_logged:
        _stop_logged = True
        log.info(f"stopped: {reason}")


def log_crashes():
    """Uncaught errors go to the log: under pythonw there is no console to print them."""
    def main_thread(kind, err, tb):
        log.error("crashed", exc_info=(kind, err, tb))
        log_stop("crashed (the error is above)")

    def other_thread(a):
        name = a.thread.name if a.thread else "?"
        log.error(f"thread {name} crashed", exc_info=(a.exc_type, a.exc_value, a.exc_traceback))
    sys.excepthook, threading.excepthook = main_thread, other_thread


# ---------------------------------------------------------------- state: folders + progress

class State:
    """tools/codeview-state.json: {"roots": [paths], "progress": {root path: {file: entry}},
    "browser": browsers() id (absent: the default browser)}."""

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

    def set_browser(self, bid):
        """The tray's Open-in choice: a browsers() id, or None for the default browser."""
        with self.lock:
            if bid:
                self.data["browser"] = bid
            else:
                self.data.pop("browser", None)
            self.save()

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

    def set_progress(self, rid, rel, status, practice_pass=None, secs=None, hints=None):
        """Entry keys: s done/revise, t date marked, r reviews passed since first done,
        practiced/pass last practice run, secs/best/hints the last and best solve."""
        key = self.root(rid)["path"]
        secs, hints = whole(secs, "secs", 7 * 86400), whole(hints, "hints", 100)
        with self.lock:
            files = self.data["progress"].setdefault(key, {})
            entry = files.get(rel, {})
            if status == "done" and entry.get("s") == "done" and practice_pass:
                entry["r"] = entry.get("r", 0) + 1      # solved again: one more review passed
            elif status in ("revise", "none") or (status == "done" and entry.get("s") != "done"):
                entry.pop("r", None)
            if practice_pass and secs is not None:
                entry["secs"] = secs
                entry["best"] = min(secs, entry.get("best", secs))
            if practice_pass and hints is not None:
                entry["hints"] = hints
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

    def move_progress(self, rid, old, new):
        """Rename (8 Oct): old's entry, or every entry under folder old, moves to new."""
        key = self.root(rid)["path"]
        with self.lock:
            files = self.data["progress"].get(key) or {}
            moved = [k for k in files if k == old or k.startswith(old + "/")]
            for k in moved:
                files[new + k[len(old):]] = files.pop(k)
            if moved:
                self.save()

    def restore_progress(self, rid, rel, entry):
        """Put an entry back exactly as /api/progress returned it: the page's Undo after a
        peek, since marking revise drops r and t and marking done again cannot restore them."""
        if not isinstance(entry, dict):
            raise ValueError("restore needs an entry")
        clean = {}
        for k, v in entry.items():
            if k == "s" and v in ("done", "revise"):
                clean[k] = v
            elif k in ("t", "practiced") and isinstance(v, str) and re.fullmatch(r"\d{4}-\d\d-\d\d", v):
                clean[k] = v
            elif k in ("r", "hints"):
                clean[k] = whole(v, k, 100)
            elif k in ("secs", "best"):
                clean[k] = whole(v, k, 7 * 86400)
            elif k == "pass" and isinstance(v, bool):
                clean[k] = v
            else:
                raise ValueError(f"cannot restore {k}={v!r}")
        key = self.root(rid)["path"]
        with self.lock:
            files = self.data["progress"].setdefault(key, {})
            if clean:
                files[rel] = clean
            else:
                files.pop(rel, None)
            self.save()
        return clean


def whole(v, what, most):
    """None, or a whole number 0..most sent by the page; ValueError for anything else."""
    if v is None:
        return None
    try:
        n = int(v)
    except (TypeError, ValueError):
        raise ValueError(f"{what} must be a whole number") from None
    if not 0 <= n <= most:
        raise ValueError(f"{what} must be between 0 and {most}")
    return n


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


def shows(base, p):
    """True when folder base's tree has p in it: inside base, and no folder on the way is
    one walk() skips (skip_dirs, or a name starting with a dot)."""
    if p != base and base not in p.parents:
        return False
    parts = p.relative_to(base).parts
    skip = skip_dirs(base)
    return not any(d in skip or d.startswith(".") for d in (parts if p.is_dir() else parts[:-1]))


def locate(path):
    """Any file or folder -> {root, path, added}: where the page shows it. Explorer's
    right-click "Open in Code Viewer" uses it (7 Oct). This repo first, because progress
    is kept per folder in the list; then the deepest listed folder that shows it; else
    its folder joins the list. path is "" for the folder itself."""
    text = str(path).strip().strip('"')
    if not text:
        raise ValueError("no path given")
    p = pathlib.Path(os.path.realpath(os.path.expanduser(text)))
    if not p.exists():
        raise ValueError(f"not found: {text}")
    roots = STATE.roots()
    for r in roots[:1] + sorted(roots[1:], key=lambda r: -len(pathlib.Path(r["path"]).parts)):
        base = pathlib.Path(r["path"])
        if shows(base, p):
            return {"root": r["id"], "path": "" if p == base else p.relative_to(base).as_posix(), "added": False}
    r = STATE.add_root(str(p if p.is_dir() else p.parent))
    return {"root": r["id"], "path": "" if p.is_dir() else p.name, "added": True}


def walk(base, empty=None):
    """Yield text files under base, skipping build/VCS folders, at most MAX_FILES. empty: a list
    that gets every folder with nothing in it at all - a folder just made with + (8 Oct)."""
    skip, count = skip_dirs(base), 0
    for dirpath, dirnames, filenames in os.walk(base):
        if empty is not None and not dirnames and not filenames and pathlib.Path(dirpath) != pathlib.Path(base):
            empty.append(pathlib.Path(dirpath))
        dirnames[:] = sorted(d for d in dirnames if d not in skip and not d.startswith("."))
        for f in sorted(filenames, key=str.lower):
            if pathlib.Path(f).suffix.lower() in TEXT_EXT:
                yield pathlib.Path(dirpath) / f
                count += 1
                if count >= MAX_FILES:
                    return


def build_tree(rid):
    base = root_path(rid)
    root, empty = {"name": base.name, "dirs": {}, "files": []}, []

    def folder(parts):
        node = root
        for part in parts:
            node = node["dirs"].setdefault(part, {"name": part, "dirs": {}, "files": []})
        return node
    for p in walk(base, empty):
        rel = p.relative_to(base)
        f = {"name": p.name, "path": rel.as_posix()}
        if langsrc.lang_of(p.name) and "MUST-KNOW" in read_head(p):
            f["must"] = True
        folder(rel.parts[:-1])["files"].append(f)
    for d in empty:                      # shown, so a new folder can be opened and filled
        folder(d.relative_to(base).parts)

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


# ---- the method a new problem file starts with (6 Oct: Ravi asked for it to be made from the
# signature, typed or pasted from LeetCode, with a main() that already calls it)

JAVA_KEYWORDS = set("""abstract assert boolean break byte case catch char class const continue default do double
    else enum extends final finally float for goto if implements import instanceof int interface long native new
    package private protected public return short static strictfp super switch synchronized this throw throws
    transient try void volatile while true false null var record yield""".split())
PRIMITIVES = {"int", "long", "short", "byte", "char", "boolean", "double", "float"}
TYPE = r"[A-Za-z_$][\w$.]*(?:\s*<[^()]*?>)?(?:\s*\[\s*\])*"
SIGNATURE = re.compile(r"(?:\b(?:public|private|protected|static|final|synchronized)\s+)*"
                       rf"(?P<ret>{TYPE})\s+(?P<name>[A-Za-z_$][\w$]*)\s*\((?P<params>[^()]*)\)")
# typical value, edge value, three values for an array or collection
SAMPLES = {"int": ("2", "0", "1, 2, 3"), "long": ("2L", "0L", "1L, 2L, 3L"), "short": ("(short) 2", "(short) 0", "1, 2, 3"),
           "byte": ("(byte) 2", "(byte) 0", "1, 2, 3"), "double": ("1.5", "0.0", "1.5, 2.5, 3.5"),
           "float": ("1.5f", "0f", "1.5f, 2.5f, 3.5f"), "boolean": ("true", "false", "true, false, true"),
           "char": ("'a'", "'a'", "'a', 'b', 'c'"), "String": ('"abc"', '""', '"a", "b", "c"'),
           "Integer": ("2", "0", "1, 2, 3"), "Long": ("2L", "0L", "1L, 2L, 3L"), "Double": ("1.5", "0.0", "1.5, 2.5, 3.5"),
           "Boolean": ("true", "false", "true, false, true"), "Character": ("'a'", "'a'", "'a', 'b', 'c'")}
EMPTY = {"int": "0", "long": "0", "short": "0", "byte": "0", "double": "0", "float": "0", "boolean": "false",
         "char": "' '", "String": '""', "Integer": "0", "Long": "0L", "Double": "0.0", "Boolean": "false",
         "Character": "' '"}
NEW_OF = {"List": "ArrayList", "Collection": "ArrayList", "Iterable": "ArrayList", "ArrayList": "ArrayList",
          "LinkedList": "LinkedList", "Set": "HashSet", "HashSet": "HashSet", "TreeSet": "TreeSet",
          "Map": "HashMap", "HashMap": "HashMap", "TreeMap": "TreeMap", "Queue": "ArrayDeque",
          "Deque": "ArrayDeque", "ArrayDeque": "ArrayDeque", "PriorityQueue": "PriorityQueue"}
UTIL = ("ArrayDeque", "ArrayList", "Arrays", "Collection", "Deque", "HashMap", "HashSet", "LinkedList", "List",
        "Map", "PriorityQueue", "Queue", "Set", "TreeMap", "TreeSet")
NODES = {
    "TreeNode": """class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {                  // level order, as LeetCode prints a tree
        List<String> out = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            out.add(node == null ? "null" : String.valueOf(node.val));
            if (node != null) {
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        while (out.get(out.size() - 1).equals("null")) {
            out.remove(out.size() - 1);
        }
        return out.toString();
    }
}
""",
    "ListNode": """class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    @Override
    public String toString() {                  // the values, as LeetCode prints a list
        StringBuilder sb = new StringBuilder("[");
        for (ListNode node = this; node != null; node = node.next) {
            sb.append(node.val).append(node.next == null ? "" : ", ");
        }
        return sb.append("]").toString();
    }
}
"""}


def norm_type(t):
    """'Map<String,List<Integer> >' -> 'Map<String, List<Integer>>'; 'int [ ]' -> 'int[]'."""
    t = re.sub(r"\s*([<>\[\]])\s*", r"\1", t.strip())
    return re.sub(r"\s*,\s*", ", ", t)


def split_top(s):
    """Split on the commas outside <...>: 'Map<String, Integer> m, int k' -> 2 parts."""
    out, depth, cur = [], 0, ""
    for ch in s:
        depth += (ch == "<") - (ch == ">")
        if ch == "," and depth == 0:
            out.append(cur)
            cur = ""
        else:
            cur += ch
    return [x.strip() for x in out + [cur] if x.strip()]


def parse_signature(text):
    """'public int[] twoSum(int[] nums, int target) {' -> ('int[]', 'twoSum', [('int[]', 'nums'),
    ('int', 'target')]). LeetCode's whole starter code (class Solution { ... }) works too."""
    text = re.sub(r"\bclass\s+[\w$]+\s*\{", " ", text or "")
    m = SIGNATURE.search(text)
    if not m or m["name"] in JAVA_KEYWORDS or (m["ret"] in JAVA_KEYWORDS and m["ret"] not in PRIMITIVES | {"void"}):
        raise ValueError("Write the method as Java, such as int[] twoSum(int[] nums, int target)")
    params = []
    for p in split_top(m["params"]):
        p = re.sub(r"@[\w.]+\s*|\bfinal\s+", "", p).replace("...", "[] ")
        pm = re.fullmatch(rf"({TYPE})\s+([A-Za-z_$][\w$]*)((?:\s*\[\s*\])*)", p.strip())
        if not pm or pm[2] in JAVA_KEYWORDS or (pm[1] in JAVA_KEYWORDS and pm[1] not in PRIMITIVES):
            raise ValueError(f"Cannot read the parameter '{p.strip()}': write it as type and name, such as int[] nums")
        params.append((norm_type(pm[1] + pm[3]), pm[2]))
    name = m["name"]
    if name in ("check", "show", "main"):
        raise ValueError(f"main() already uses {name}: give the method another name")
    return norm_type(m["ret"]), name, params


def generic(t):
    g = re.fullmatch(r"([\w.]+)<(.*)>", t)
    return (g[1].split(".")[-1], split_top(g[2])) if g else (None, [])


def literal(t, edge=False):
    """A value of type t for main() to pass in: typical, or the edge case (empty, zero)."""
    raw, args = generic(t)
    if t.endswith("[]"):
        base = t[:-2]
        if "<" in base:
            return "null"                                       # Java has no generic array literal
        if edge:
            return f"new {base}[]{{}}"
        inner = SAMPLES[base][2] if base in SAMPLES else ", ".join([literal(base)] * 2)
        return f"new {base}[]{{{inner}}}"
    if t in SAMPLES:
        return SAMPLES[t][1 if edge else 0]
    if t == "TreeNode":
        return "null" if edge else "new TreeNode(1, new TreeNode(2), new TreeNode(3))"
    if t == "ListNode":
        return "null" if edge else "new ListNode(1, new ListNode(2, new ListNode(3)))"
    if raw in NEW_OF and args:
        if raw in ("Map", "HashMap", "TreeMap"):
            items = "" if edge or len(args) < 2 else f"{literal(args[0])}, {literal(args[1])}"
            made = f"Map.of({items})"
        else:
            el = args[0]
            many = SAMPLES[el][2] if el in SAMPLES else literal(el)       # one of anything else: Set.of refuses repeats
            made = ("Set" if "Set" in raw else "List") + f".of({'' if edge else many})"
        exact = raw in ("List", "Collection", "Iterable", "Set", "Map")
        return made if exact else f"new {NEW_OF[raw]}<>({made})"
    return "null"


def empty_value(t):
    """What the stub returns until it is written: 0, "", an empty array or collection."""
    if t in EMPTY:
        return EMPTY[t]
    if t.endswith("[]"):
        first = t.index("[")
        return "null" if "<" in t else f"new {t[:first]}[0]{t[first + 2:]}"
    raw, _ = generic(t)
    return f"new {NEW_OF[raw]}<>()" if raw in NEW_OF else "null"


def stub_code(cls, ret, name, params):
    """The class with `static ret name(params)` returning an empty value, check() and a main() that
    calls it on a typical and an edge input; a void method's changed argument is printed."""
    printed, cases = ret, []
    for no, edge in ((1, False), (2, True)):
        args, label = [literal(t, edge) for t, _ in params], f"case {no}" + (" edge" if edge else "")
        if ret != "void":
            line = f'        check("{label}", {name}({", ".join(args)}), "?");'
            if len(line) > 100:                # the repo's line limit: name each input first
                line = "".join(f"        {t} {n}{no} = {a};\n" for (t, n), a in zip(params, args)) \
                    + f'        check("{label}", {name}({", ".join(f"{n}{no}" for _, n in params)}), "?");'
            cases += [line, ""] if "\n" in line else [line]
        elif params:
            i = next((k for k, (t, _) in enumerate(params) if t.endswith("]") or "<" in t or t in NODES), 0)
            var, printed = f"{params[i][1]}{no}", params[i][0]
            cases += [f"        {printed} {var} = {args[i]};", f"        {name}({', '.join(args[:i] + [var] + args[i + 1:])});",
                      f'        check("{label}", {var}, "?");', ""]
        else:
            cases.append(f'        {name}();\n        check("{label}", "ran", "ran");')
            break
    arrays = printed.endswith("]")
    show = "show(actual)" if arrays else "actual"
    head = f"    static {ret} {name}("
    decl = head + ", ".join(f"{t} {n}" for t, n in params) + ") {"
    if len(decl) > 100:                        # wrapped as IntelliJ does: each parameter under the first
        decl = head + (",\n" + " " * len(head)).join(f"{t} {n}" for t, n in params) + ") {"
    code = [f"class {cls} {{", "", decl,
            "        // your code here" if ret == "void" else f"        return {empty_value(ret)};",
            "    }", "",
            "    private static void check(String label, Object actual, Object expected) {",
            f'        System.out.println(label + ": " + {show} + "   expected " + expected);', "    }", ""]
    if arrays:
        code += ["    private static String show(Object o) {          // an array prints its values, not [I@1b6d3586",
                 *[f"        if (o instanceof {p}[] a) return Arrays.toString(a);" for p in
                   ("int", "long", "double", "char", "boolean")],
                 "        if (o instanceof Object[] a) return Arrays.deepToString(a);",
                 "        return String.valueOf(o);", "    }", ""]
    code += ["    public static void main(String[] args) {",
             *(['        // LeetCode\'s examples go here: replace the inputs, and each "?" with the answer']
               if params else []),
             *(cases[:-1] if cases and cases[-1] == "" else cases), "    }", "}", ""]
    body = "\n".join(code)
    nodes = "".join(NODES[n] + "\n" for n in NODES if re.search(rf"\b{n}\b", body))
    used = sorted(u for u in UTIL if re.search(rf"\b{u}\b", nodes + body))
    return "".join(f"import java.util.{u};\n" for u in used) + ("\n" if used else "") + nodes + body


def java_template(name, title=None, meta="LeetCode ? | ?", must=False, method=None):
    """A new practice file: the header every DSA file carries (tools/check_headers.py), with
    `...` and `O(?)` left for you to fill, and a main() that prints actual vs expected.
    method: parse_signature()'s (return type, name, params); then the class holds that method."""
    cls = re.sub(r"^[A-D]\d\d_", "", pathlib.Path(name).stem)
    if not re.fullmatch(r"[A-Za-z_$][\w$]*", cls):
        cls = "Main"
    title = title or re.sub(r"(?<=[a-z0-9])(?=[A-Z])", " ", cls)
    # two spaces at least: check_headers splits the title from the source on 2+ spaces
    line = f" *  {title}{' ' * max(2, 41 - len(title))}{meta}{'   MUST-KNOW' if must else ''}"
    if len(line) > 100:
        raise ValueError("The name is too long for the header's first line (100 characters); shorten it")
    return f"""/*
 * =====================================================================
{line}
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
 *   Time  O(?)
 *   Space O(?)
 *
 * INTERVIEW FOLLOW-UPS
 *   ...
 *
 * RUN
 *   main() runs the cases below and prints actual vs expected.
 */
""" + (stub_code(cls, *method) if method else f"""class {cls} {{

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
""")


# 8 Oct: the same header and blanks for Python, JavaScript and UI files. JavaScript keeps Java's
# " * " lines; a Python docstring and an HTML comment have none (tools/javasrc.py STYLES).
HEADER_SECTIONS = (("PROBLEM", "..."), ("EXAMPLE", "..."), ("APPROACH", "..."), ("KEY INSIGHT", "..."),
                   ("COMPLEXITY", "Time  O(?)\nSpace O(?)"), ("INTERVIEW FOLLOW-UPS", "..."))


def header_for(name, title, meta, must, style, run):
    """-> (title, the header comment) of a new practice file. style: 'star' (/* * */), 'py'
    (a docstring) or 'html' (<!-- -->)."""
    title = title or re.sub(r"(?<=[a-z0-9])(?=[A-Z])", " ", re.sub(r"^[A-D]\d\d_", "", pathlib.Path(name).stem))
    lead = " *  " if style == "star" else " "
    # as java_template: the source starts in column 45, two spaces at least after the title
    line = f"{lead}{title}{' ' * max(2, 45 - len(lead) - len(title))}{meta}{'   MUST-KNOW' if must else ''}"
    if len(line) > 100:
        raise ValueError("The name is too long for the header's first line (100 characters); shorten it")
    rule = "=" * 69
    rows = [rule, line, rule, ""]
    for head, body in HEADER_SECTIONS + (("RUN", run),):
        rows += [head] + ["  " + b for b in body.split("\n")] + [""]
    rows.pop()
    if style == "star":
        rows = [r if r is line else (" * " + r).rstrip() for r in rows]
        return title, "/*\n" + "\n".join(rows) + "\n */\n"
    opens, closes = ('"""', '"""') if style == "py" else ("<!--", "-->")
    return title, opens + "\n" + "\n".join(rows) + "\n" + closes + "\n"


PY_BODY = '''

def solve(nums):
    return 0


def check(label, actual, expected):
    print(f"{label}: {actual}   expected {expected}")


def main():
    check("case 1 typical", solve([1, 2, 3]), 6)
    check("case 2 empty  ", solve([]), 0)


if __name__ == "__main__":
    main()
'''

JS_BODY = '''
function solve(nums) {
  return 0;
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  check('case 1 typical', solve([1, 2, 3]), 6);
  check('case 2 empty  ', solve([]), 0);
}

main();
'''

# A small working screen to change, and the tests Ctrl+Enter runs. Browsers skip a
# <script type="test">, so the page alone (the preview, or the file opened in Chrome) never runs it.
HTML_BODY = '''<html lang="en">
<head>
  <meta charset="utf-8">
  <title>__TITLE__</title>
  <style>
    body { font: 16px system-ui, sans-serif; margin: 24px; }
    button { font: inherit; padding: 4px 12px; }
  </style>
</head>
<body>
  <p>Count: <span id="count">0</span></p>
  <button id="add">Add one</button>

  <script>
    let count = 0;

    function render() {
      document.querySelector('#count').textContent = count;
    }

    function addOne() {
      count += 1;
      render();
    }

    document.querySelector('#add').addEventListener('click', addOne);
    render();
  </script>

  <!-- Tests: Ctrl+Enter in Code Viewer runs this block once the page has loaded.
       await works here, and every check() line is ticked or crossed. -->
  <script type="test">
    const $ = (sel) => document.querySelector(sel);
    const click = (sel) => $(sel).click();
    const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
    function type(sel, text) {
      const el = $(sel);
      el.value = text;
      el.dispatchEvent(new Event('input', { bubbles: true }));
    }
    function check(label, actual, expected) {
      console.log(`${label}: ${actual}   expected ${expected}`);
    }

    check('case 1 starts at', $('#count').textContent, '0');
    click('#add');
    click('#add');
    check('case 2 two clicks', $('#count').textContent, '2');
  </script>
</body>
</html>
'''


def python_template(name, title=None, meta="LeetCode ? | ?", must=False):
    _, head = header_for(name, title, meta, must, "py", "main() runs the cases below and prints actual vs expected.")
    return head + PY_BODY


def js_template(name, title=None, meta="LeetCode ? | ?", must=False):
    _, head = header_for(name, title, meta, must, "star", "main() runs the cases below and prints actual vs expected.")
    return head + JS_BODY


def html_template(name, title=None, meta="Machine coding | ?", must=False):
    title, head = header_for(name, title, meta, must, "html",
                             "The preview beside the code shows the page as you type.\n"
                             "Ctrl+Enter runs the test block at the bottom and ticks each check.")
    return "<!DOCTYPE html>\n" + head + HTML_BODY.replace("__TITLE__", title.replace("&", "&amp;").replace("<", "&lt;"))


TEMPLATES = {".py": python_template, ".js": js_template, ".mjs": js_template, ".html": html_template}
NEW_LANGS = {"java": ".java", "py": ".py", "js": ".js", "html": ".html"}


WIN_BAD = re.compile(r'[<>:"|?*\x00-\x1f]')
WIN_DEVICES = {"CON", "PRN", "AUX", "NUL", *(f"COM{i}" for i in range(1, 10)), *(f"LPT{i}" for i in range(1, 10))}


def check_path(rid, rel, folder=False):
    """A path typed in the page -> 'a/b/c', or a ValueError in words (8 Oct). Before this,
    'a:b.md' said "outside the folder", a folder named build said "excluded folder", and
    CON.md was made - a name that breaks a git checkout on Windows. folder: the last name is
    a folder too, so the list's hidden folder names (build, target, ...) are refused for it."""
    rel = rel.strip().replace("\\", "/").strip("/")
    if not rel:
        raise ValueError("Type a name")
    parts = rel.split("/")
    skip = skip_dirs(root_path(rid))
    for i, part in enumerate(parts):
        if part in ("", ".", ".."):
            raise ValueError(f"'{part or '//'}' can't be part of a name: give names inside this folder")
        bad = WIN_BAD.search(part)
        if bad:
            raise ValueError(f'Windows does not allow {bad[0] if bad[0] > " " else "control characters"} in a name ("{part}")')
        if part[-1] in ". ":
            raise ValueError(f'"{part}" ends with a dot or a space, which Windows drops; remove it')
        if part.split(".")[0].rstrip().upper() in WIN_DEVICES:
            raise ValueError(f"{part.split('.')[0]} is a name Windows keeps for itself (like CON, NUL, COM1); pick another")
        if part.startswith("."):
            raise ValueError(f'"{part}" starts with a dot: the list hides those, so it would never show')
        if (folder or i < len(parts) - 1) and part in skip:
            raise ValueError(f"The list hides folders named {part} (also {', '.join(sorted(skip - {part})[:4])}, ...); pick another name")
    return "/".join(parts)


def tier_clash(p, keep=None):
    """B11_X.java when its folder already has a B11_ file (other than keep, a file being renamed):
    file names give the practice order, so a number is used once."""
    tier = re.match(r"([A-D])\d\d_", p.name)
    if tier and p.parent.is_dir():
        taken = next((q.name for q in sorted(p.parent.iterdir()) if q.name.startswith(p.name[:4]) and q != keep), None)
        if taken:
            raise ValueError(f"{p.name[:3]} is already {taken} in this folder; "
                             f"the next free {tier[1]} number is {next_tier(p.parent, tier[1])}")


def new_file(rid, rel, title=None, meta="LeetCode ? | ?", must=False, method=None):
    if rel.rstrip().endswith(("/", "\\")):
        raise ValueError(f"{rel.strip()} ends with /, so it is a folder: make it with New folder")
    rel = check_path(rid, rel)
    p = resolve(rid, rel)
    if not p.suffix:
        raise ValueError(f"Add a file type to {p.name}, such as {p.name}.md")
    if p.suffix.lower() not in TEXT_EXT:
        raise ValueError(f"'{p.suffix}' is not a text file type this viewer shows")
    if p.exists():
        raise ValueError(f"{rel} already exists")
    tier_clash(p)
    suffix = p.suffix.lower()
    text = (java_template(p.name, title, meta, must, method) if suffix == ".java"
            else TEMPLATES[suffix](p.name, title, meta, must) if suffix in TEMPLATES
            else f"# {p.stem}\n\n" if suffix == ".md" else "")
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_bytes(text.encode("utf-8"))                # LF like the repo's files (write_text gives CRLF on Windows)
    res = {"path": p.relative_to(root_path(rid)).as_posix()}
    blank = re.search(r"^((?: \*)?[ \t]+)\.\.\.$", text, re.M)   # the page selects the first ... to type over
    if blank:
        res["select"] = {"line": text.count("\n", 0, blank.start()) + 1, "col": len(blank[1]) + 1, "len": 3}
    elif p.suffix == ".md":                            # the caret under the heading; the page opens it as source
        res["select"] = {"line": 3, "col": 1, "len": 0}
    return res


def new_folder(rid, rel):
    """+ → Folder, or right-click → New folder here (8 Oct). build_tree lists it while empty."""
    rel = check_path(rid, rel, folder=True)
    p = resolve(rid, rel)
    if p.exists():
        raise ValueError(f"{rel} already exists")
    p.mkdir(parents=True)
    return {"path": p.relative_to(root_path(rid)).as_posix()}


def rename_path(rid, rel, name):
    """Right-click → Rename (8 Oct): a file or folder the list shows gets a new name in the same
    folder, and its progress entries move with it. The page moves its own drafts and attempts."""
    rel = rel.strip().replace("\\", "/").strip("/")
    if not rel:
        raise ValueError("The folder itself can't be renamed here")
    base, p = root_path(rid), resolve(rid, rel)
    if not p.exists():
        raise ValueError(f"{rel} was not found (renamed or deleted meanwhile?)")
    is_dir = p.is_dir()
    if not shows(base, p) or (not is_dir and p.suffix.lower() not in TEXT_EXT):
        raise ValueError(f"{rel} is not a file or folder this viewer shows")
    name = name.strip()
    if "/" in name or "\\" in name:
        raise ValueError("Type one name only: Rename keeps it in the same folder")
    check_path(rid, name, folder=is_dir)
    target = p.with_name(name)
    if name == p.name:
        raise ValueError(f"{p.name} already has that name")
    if target.exists() and not os.path.samefile(target, p):     # same file: only the case changes
        raise ValueError(f"{target.relative_to(base).as_posix()} already exists")
    if not is_dir:
        if target.suffix.lower() not in TEXT_EXT:
            raise ValueError(f"{name} would not show in the list (it shows .java, .md and other text files)")
        tier_clash(target, keep=p)
    try:
        os.rename(p, target)
    except PermissionError:
        raise ValueError(f"Windows would not rename {p.name}: is it open in another program?") from None
    new = target.relative_to(base).as_posix()
    STATE.move_progress(rid, rel, new)
    return {"path": new, "dir": is_dir}


def next_tier(folder, letter):
    """B12 when the folder's last B file is B11_...: file names give the practice order."""
    nums = [int(m[1]) for q in (folder.iterdir() if folder.is_dir() else ())
            if (m := re.match(rf"{letter}(\d\d)_", q.name))]
    n = max(nums, default=0) + 1
    if n > 99:
        raise ValueError(f"{folder.name} already goes up to {letter}99; use another level or folder")
    return f"{letter}{n:02d}"


DIFFICULTIES = ("Easy", "Medium", "Hard")


def class_name(name):
    """'LRU cache' -> LRUCache, "kadane's algorithm" -> KadanesAlgorithm."""
    words = re.findall(r"[A-Za-z0-9]+", re.sub(r"['‘’]", "", name))
    cls = "".join(w[0].upper() + w[1:] for w in words)
    if not cls:
        raise ValueError("Give the problem a name, such as Two Sum")
    if cls[0].isdigit():
        raise ValueError("A Java class name cannot start with a digit: write it in words, such as Three Sum")
    return cls


def new_problem(rid, folder, level, name, source, difficulty, must=False, method="", lang="java"):
    """The New problem form (6 Oct): 01-Arrays + B + "Two Sum" -> 01-Arrays/B12_TwoSum.java,
    numbered after the folder's last B file, with the header's first line filled in. method: a
    signature the class starts with; with no name, the name comes from it (twoSum -> Two Sum).
    lang (8 Oct): java, py, js or html - the same form makes a Python, JavaScript or UI file."""
    if lang not in NEW_LANGS:
        raise ValueError("The language is Java, Python, JavaScript or UI (HTML)")
    if method and method.strip() and lang != "java":
        raise ValueError("The method field is for Java problems")
    sig = parse_signature(method) if method and method.strip() else None
    if sig and not name.strip():
        name = re.sub(r"(?<=[a-z0-9])(?=[A-Z])", " ", sig[1])
        name = name[0].upper() + name[1:]
    folder = folder.strip().replace("\\", "/").strip("/")
    level = (level or "").strip().upper()
    if level not in ("", "A", "B", "C", "D"):
        raise ValueError("The level is A, B, C, D or none")
    if difficulty not in DIFFICULTIES:
        raise ValueError("The difficulty is Easy, Medium or Hard")
    name = " ".join(name.replace("‘", "'").replace("’", "'").split())
    cls = class_name(name)
    title = re.sub(r"(?<=[a-z0-9])(?=[A-Z])", " ", name) if re.fullmatch(r"\w+", name) else name
    source = " ".join(source.split())
    source = f"LeetCode {source}" if source.isdigit() else source or ("Machine coding" if lang == "html" else "LeetCode ?")
    where = resolve(rid, folder) if folder else root_path(rid)
    if where.exists() and not where.is_dir():
        raise ValueError(f"{folder} is a file, not a folder")
    prefix = next_tier(where, level) + "_" if level else ""
    ext = NEW_LANGS[lang]
    return new_file(rid, f"{folder}/{prefix}{cls}{ext}" if folder else f"{prefix}{cls}{ext}",
                    title, f"{source} | {difficulty}", must, sig)


def to_recycle_bin(p):
    """Windows Recycle Bin, so a wrong click can be undone from there."""
    if not IS_WIN:
        raise ValueError("Delete is only set up on Windows; delete it in your file manager")
    import ctypes
    from ctypes import wintypes

    class SHFILEOPSTRUCTW(ctypes.Structure):
        _fields_ = [("hwnd", wintypes.HWND), ("wFunc", wintypes.UINT), ("pFrom", wintypes.LPCWSTR),
                    ("pTo", wintypes.LPCWSTR), ("fFlags", ctypes.c_ushort), ("fAnyOperationsAborted", wintypes.BOOL),
                    ("hNameMappings", ctypes.c_void_p), ("lpszProgressTitle", wintypes.LPCWSTR)]
    # FO_DELETE; ALLOWUNDO (to the bin) | NOCONFIRMATION | SILENT | NOERRORUI: the page already asked
    op = SHFILEOPSTRUCTW(wFunc=3, pFrom=str(p) + "\0", fFlags=0x40 | 0x10 | 0x4 | 0x400)
    err = ctypes.windll.shell32.SHFileOperationW(ctypes.byref(op))
    if err or op.fAnyOperationsAborted or p.exists():
        raise OSError(f"could not move {p.name} to the Recycle Bin (error {err})")


def delete_file(rid, rel, trash=to_recycle_bin):
    """Move one file the tree shows to the Recycle Bin (5 Oct: a stray New file had no way
    out but File Explorer). Never a folder, never a permanent delete."""
    p = resolve(rid, rel)
    if not p.is_file() or p.suffix.lower() not in TEXT_EXT:
        raise ValueError(f"{rel or 'that'} is not a file this viewer shows")
    trash(p)
    return {"ok": True}


def delete_folder(rid, rel, trash=to_recycle_bin):
    """Right-click a folder → Move to Recycle Bin (8 Oct), so a mistyped folder needs no File
    Explorer. Only a folder the list shows, never the opened folder itself."""
    rel = rel.strip().replace("\\", "/").strip("/")
    if not rel:
        raise ValueError("The folder itself can't be deleted here")
    p = resolve(rid, rel)
    if not p.is_dir() or not shows(root_path(rid), p):
        raise ValueError(f"{rel} is not a folder this viewer shows")
    trash(p)
    return {"ok": True}


# ---------------------------------------------------------------- git: the Push panel
# 6 Oct: Ravi asked for one button that commits his changes and pushes them. The panel lists
# what changed; Push commits only the ticked files, refreshes the README lists, and pushes,
# rebasing onto anything pushed from elsewhere first. A clash stops before anything is
# pushed and keeps the commit on this PC.

GIT_LOCK = threading.Lock()
REJECTED = re.compile(r"\[rejected\]|non-fast-forward|fetch first|failed to push some refs")


def git(top, *args, timeout=60, check=True, stdin=None):
    """git in folder `top`. It never waits on a console prompt: the tray has no console."""
    feed = {"input": stdin} if stdin is not None else {"stdin": subprocess.DEVNULL}
    try:
        r = subprocess.run(["git", "-c", "core.quotepath=off", "--literal-pathspecs", "-C", str(top), *args],
                           capture_output=True, text=True, encoding="utf-8", errors="replace", timeout=timeout,
                           env=dict(os.environ, GIT_TERMINAL_PROMPT="0"), creationflags=NO_WINDOW, **feed)
    except FileNotFoundError:
        raise ValueError("git is not installed, or not on PATH")
    if check and r.returncode:
        raise ValueError(f"git {args[0]} failed: " + last_line(r))
    return r


def last_line(r):
    lines = [x.strip() for x in (r.stderr or r.stdout or "").splitlines() if x.strip()]
    return re.sub(r"^(fatal|error): ", "", lines[-1]) if lines else f"exit code {r.returncode}"


def git_top(base):
    r = git(base, "rev-parse", "--show-toplevel", check=False)
    return pathlib.Path(r.stdout.strip()).resolve() if r.returncode == 0 and r.stdout.strip() else None


def readme_root(top):
    """Where tools/gen_readmes.py builds README lists: only in this repo."""
    return top / "AAScratches" if top == REPO else None


def refresh_readmes(top, write=True, extra=()):
    """Rebuild the README lists from the file headers, as `python tools/gen_readmes.py` does.
    -> the README paths (relative to top) whose text changes. extra: repo paths about to be
    added, counted as tracked (the panel's preview)."""
    root = readme_root(top)
    if root is None:
        return []
    import gen_readmes
    pre = root.relative_to(top).as_posix() + "/"
    extra = {p[len(pre):] for p in extra if p.startswith(pre)}
    changed = []
    for t in gen_readmes.TITLES:
        if not (root / t).is_dir():
            continue
        p = root / t / "README.md"
        new = gen_readmes.render(t, root, extra)[0].encode("utf-8")
        if (p.read_bytes() if p.exists() else b"") != new:
            if write:
                p.write_bytes(new)
            changed.append(p.relative_to(top).as_posix())
    return changed


def status_word(xy):
    if "U" in xy or xy in ("AA", "DD"):
        return "conflict"
    if xy == "??" or "A" in xy or "C" in xy:
        return "new"
    if "R" in xy:
        return "renamed"
    return "deleted" if "D" in xy else "changed"


def repo_status(top):
    """{branch, upstream, ahead, behind, remote, files: [{path, status, from?}]}; paths from top."""
    out = git(top, "status", "--porcelain=v1", "-z", "--branch", "--untracked-files=all").stdout
    head, *rest = out.split("\0")
    files, i = [], 0
    while i < len(rest):
        e = rest[i]
        i += 1
        if len(e) < 4:
            continue
        f = {"path": e[3:], "status": status_word(e[:2])}
        if e[0] in "RC":                                   # the old name comes next
            f["from"] = rest[i]
            i += 1
        files.append(f)
    branch, upstream, ahead, behind = None, None, 0, 0
    h = head[3:]
    if h.startswith("No commits yet on "):
        branch = h[len("No commits yet on "):]
    elif not h.startswith("HEAD (no branch)"):
        m = re.match(r"(.+?)(?:\.\.\.(\S+))?(?: \[(.*)\])?$", h)
        branch, upstream, info = m[1], m[2], m[3] or ""
        ahead = int((re.search(r"ahead (\d+)", info) or [0, 0])[1])
        behind = int((re.search(r"behind (\d+)", info) or [0, 0])[1])
        if "gone" in info:
            upstream = None
    remotes = git(top, "remote").stdout.split()
    return {"branch": branch, "upstream": upstream, "ahead": ahead, "behind": behind,
            "remote": "origin" if "origin" in remotes else (remotes[0] if remotes else None), "files": files}


def git_status(rid, readmes=False):
    """What the Push panel lists. prefix: the folder's path inside the repo, so the page can
    open a listed file. readmes: the README lists a push of everything listed would update."""
    base = root_path(rid).resolve()
    top = git_top(base)
    if top is None:
        return {"repo": False}
    st = repo_status(top)
    st.update(repo=True, top=top.name,
              prefix=base.relative_to(top).as_posix() + "/" if top in base.parents else "")
    if readmes:
        st["readmes"] = refresh_readmes(top, write=False, extra=[
            f["path"] for f in st["files"] if f["status"] in ("new", "renamed") and f["path"].endswith(".java")])
    return st


def git_push(rid, paths, message):
    base = root_path(rid)
    if NO_PUSH and (base == REPO or REPO in base.parents):
        raise ValueError("Push is switched off for this repo on a test server (CODEVIEW_NO_PUSH)")
    if not GIT_LOCK.acquire(blocking=False):
        raise ValueError("A push is already running")
    try:
        res = push_now(base, list(paths), (message or "").strip())
    finally:
        GIT_LOCK.release()
    log.info(f"push {len(paths)} file(s) in {base.name}: " + (f"ok {res['commit']}" if res["ok"] else
                                                               "FAILED " + res["error"]))
    return res


def push_now(base, paths, message):
    top = git_top(base)
    if top is None:
        raise ValueError("This folder is not in a git repository, so there is nothing to push")
    st = repo_status(top)
    known = {f["path"]: f for f in st["files"]}
    if any(f["status"] == "conflict" for f in st["files"]):
        raise ValueError("git is in the middle of a merge here; finish it in a terminal first")
    stale = [p for p in paths if p not in known]
    if stale:
        raise ValueError(f"{stale[0]} has no change to commit any more; close the panel and open it again")
    if not paths and not st["ahead"]:
        raise ValueError("Nothing to push: no file is ticked and every commit is already pushed")
    if paths and not message:
        raise ValueError("Write a commit message first")
    if not st["branch"]:
        raise ValueError("No branch is checked out (detached HEAD); push from a terminal")
    committed, readmes = None, []
    if paths:
        git(top, "add", "-A", "--pathspec-from-file=-", "--pathspec-file-nul", stdin="\0".join(paths))
        specs = paths + [known[p]["from"] for p in paths if known[p].get("from")]   # a rename's old name
        readmes = [p for p in refresh_readmes(top) if p not in specs]
        if readmes:
            git(top, "add", "--pathspec-from-file=-", "--pathspec-file-nul", stdin="\0".join(readmes))
        # with paths, commit takes exactly those: anything else already staged stays staged
        git(top, "commit", "-q", "-m", message, "--pathspec-from-file=-", "--pathspec-file-nul",
            stdin="\0".join(specs + readmes))
        committed = git(top, "rev-parse", "--short", "HEAD").stdout.strip()

    def fail(why):
        kept = f" Your commit {committed} is saved on this PC, so nothing is lost." if committed else \
            " Your commits are saved on this PC, so nothing is lost."
        return {"ok": False, "committed": committed, "error": why + kept}

    if not st["remote"]:
        return {"ok": True, "commit": committed, "pushed": False, "readmes": readmes,
                "note": "Committed on this PC. This repository has no remote to push to."}
    push = ["push"] if st["upstream"] else ["push", "-u", st["remote"], st["branch"]]
    rebased = False
    try:
        r = git(top, *push, timeout=120, check=False)
        if r.returncode and REJECTED.search(r.stderr):
            pull = ["pull", "--rebase", "--autostash"] + ([] if st["upstream"] else [st["remote"], st["branch"]])
            p = git(top, *pull, timeout=120, check=False)
            if p.returncode:
                git(top, "rebase", "--abort", check=False)
                if re.search(r"CONFLICT|could not apply", p.stdout + p.stderr):
                    return fail("GitHub has newer commits that change the same lines as yours, so nothing was "
                                "pushed. Open a terminal in the repo and run git pull to merge them.")
                return fail("Could not bring in GitHub's newer commits: " + last_line(p) + ".")
            rebased = True
            r = git(top, *push, timeout=120, check=False)
    except subprocess.TimeoutExpired:
        return fail("Pushing took over 2 minutes and was stopped. If a GitHub sign-in window is open, finish "
                    "it, then press Push again.")
    if r.returncode:
        err = r.stderr
        if re.search(r"Authentication failed|could not read Username|terminal prompts disabled|403", err):
            return fail("GitHub did not accept the sign-in. Run git push once in a terminal in the repo to "
                        "sign in again; after that this button works.")
        if re.search(r"Could not resolve host|unable to access|timed out", err):
            return fail("Could not reach GitHub (no internet?). Press Push again when you are online.")
        return fail("The push failed: " + last_line(r) + ".")
    return {"ok": True, "commit": git(top, "rev-parse", "--short", "HEAD").stdout.strip(), "pushed": True,
            "rebased": rebased, "readmes": readmes, "files": len(paths), "branch": st["branch"],
            "remote": st["remote"]}


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


# Attempts live in one browser profile's storage. On 4 Oct the tray opened Edge (the Windows
# default) while every attempt was in Chrome, so the work looked lost. Chromium browsers
# take --profile-directory, which also lands the tab in the right profile's window.
BROWSERS = (("Chrome", "Google/Chrome/Application/chrome.exe", "Google/Chrome/User Data"),
            ("Edge", "Microsoft/Edge/Application/msedge.exe", "Microsoft/Edge/User Data"))


def browsers(env=os.environ):
    """[{id, label, exe, profile}]: every profile of each installed Chrome and Edge, named
    as the browser's profile menu names it. Empty off Windows."""
    out = []
    for name, exe_rel, data_rel in BROWSERS:
        exe = next((p for p in (pathlib.Path(env[k]) / exe_rel
                                for k in ("PROGRAMFILES", "PROGRAMFILES(X86)", "LOCALAPPDATA") if env.get(k))
                    if p.is_file()), None)
        if not exe:
            continue
        try:
            state = json.loads((pathlib.Path(env.get("LOCALAPPDATA", "")) / data_rel / "Local State")
                               .read_text(encoding="utf-8"))
            profiles = {d: (i.get("name") or d) for d, i in state["profile"]["info_cache"].items()}
        except (OSError, ValueError, KeyError, TypeError, AttributeError):
            profiles = {"Default": "Default"}
        for d in sorted(profiles, key=lambda d: (d != "Default", d)):
            out.append({"id": f"{name.lower()}/{d}", "label": f"{name} ({profiles[d]})",
                        "exe": str(exe), "profile": d})
    return out


def open_page(url, found=None, launch=subprocess.Popen, fallback=webbrowser.open):
    """Open the page in the browser picked in the tray's Open-in menu, else the default one."""
    bid = STATE.data.get("browser")
    b = next((b for b in (browsers() if found is None else found) if b["id"] == bid), None) if bid else None
    if b:
        try:
            launch([b["exe"], f"--profile-directory={b['profile']}", url])
            return
        except OSError:
            log.exception(f"could not start {b['label']}; opened the default browser instead")
    fallback(url)


def open_local(rid, rel, family, found=None, launch=subprocess.Popen, fallback=webbrowser.open):
    """Right-click in the page's file list → "Open in browser" (7 Oct): the file, or a folder's
    "Index of" page, as a file:/// tab. A page served from http may not open file:/// itself,
    so this starts the browser the page runs in (family "chrome" or "edge"): the tray's Open-in
    choice with its profile when it is that browser, else that browser as is (it opens the tab
    in the profile used last), else whichever Chrome or Edge is installed. Not the Windows
    default: that is Edge here while the work is in Chrome, and for a file:/// folder address
    webbrowser would open File Explorer."""
    p = resolve(rid, rel) if rel else root_path(rid)
    if not p.exists():
        raise ValueError(f"not found: {rel or p}")
    url = p.as_uri() + ("/" if p.is_dir() else "")
    found = browsers() if found is None else found
    kind = lambda b: b["id"].split("/")[0]                    # noqa: E731
    chosen = next((b for b in found if b["id"] == STATE.data.get("browser")), None)
    if chosen and (not family or kind(chosen) == family):
        cmd = [chosen["exe"], f"--profile-directory={chosen['profile']}", url]
    else:
        b = next((b for b in found if kind(b) == family), None) or (found[0] if found else None)
        if not b:
            if IS_WIN:
                raise ValueError("Opening this in a browser needs Chrome or Edge.")
            fallback(url)
            return {"ok": True, "url": url}
        cmd = [b["exe"], url]
    launch(cmd)
    log.info(f"opened {url} in {pathlib.Path(cmd[0]).name}")
    return {"ok": True, "url": url}


def page_url(port, where=None):
    """The page's address, opened on {root, path} from locate() when given (path "" is the
    folder itself, which the page shows as a listing)."""
    url = f"http://127.0.0.1:{port}/"
    return url + f"#/{quote(where['root'], safe='')}/{quote(where['path'], safe='/')}" if where else url


def locate_running(port, path):
    """Ask the copy already running on this port where it shows path. It must be that copy
    that answers: it may add a folder to its list, and it keeps the list in memory."""
    req = urllib.request.Request(f"http://127.0.0.1:{port}/api/locate", json.dumps({"path": str(path)}).encode(),
                                 {"Content-Type": "application/json", "X-CodeView": "1"})
    try:
        with urllib.request.urlopen(req, timeout=15) as r:
            return json.load(r)
    except urllib.error.HTTPError as e:
        if e.code == 404:
            raise ValueError("The Code Viewer that is running is an older copy. "
                             "Tray icon → Restart Code Viewer once, then try again.") from None
        try:
            msg = json.load(e)["error"]
        except (ValueError, KeyError, TypeError):
            msg = f"Code Viewer answered {e.code}"
        raise ValueError(msg) from None


def open_in_running(port, path):
    """Second launch with --open: the running copy finds the place, this one opens the tab."""
    url = page_url(port, locate_running(port, path))
    open_page(url)
    return url


def tell(msg):
    """Say something to whoever started this: a printed line in a console, a message box
    under pythonw (Explorer's right-click starts it with no console)."""
    log.info(msg)
    if sys.stdout is not None or not IS_WIN:
        print(msg, flush=True)
        return
    import ctypes
    ctypes.windll.user32.MessageBoxW(None, msg, "Code Viewer", 0x30 | 0x10000 | 0x40000)  # warning, foreground, topmost


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

def compile_target(code, rid, rel_path):
    """-> (folder for -sourcepath or None, file name, javasrc.analyse(code)): how javac sees
    this code. Run and the autocomplete helper share it, so helper classes in the same
    folder resolve the same way in both."""
    src_dir, fname = None, "Scratch.java"
    if rel_path:
        p = resolve(rid, rel_path)
        src_dir, fname = p.parent, p.name
    info = javasrc.analyse(code)
    pkg, public = info[0], info[2]
    if public:                         # javac insists a public type lives in <Name>.java
        fname = public + ".java"
    if pkg and src_dir:                # sourcepath must be the package root, not its folder
        for _ in pkg.split("."):
            src_dir = src_dir.parent
    return src_dir, fname, info


def run_code(code, rid, rel_path, stdin, timeout, want_main, lang=None):
    """Compile and run code. A file's type comes from rel_path; the Scratch pad and the notes'
    blocks have no path and send lang (java, py, js or mjs)."""
    lang = (langsrc.lang_of(rel_path) if rel_path else lang) or "java"
    if lang == "py":
        return run_python(code, rid, rel_path, stdin, timeout)
    if lang in ("js", "mjs"):
        return run_node(code, rid, rel_path, stdin, timeout, lang)
    if lang != "java":
        return {"phase": "compile", "ok": False, "output": f"Run does not know {lang} files."}
    src_dir, fname, (pkg, mains, public, compact) = compile_target(code, rid, rel_path)
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

        t0 = time.monotonic()
        # 5 Oct: compile in the autocomplete helper's warm javac (~570 ms -> tens of ms); its
        # errors read exactly like the javac command's. If it cannot answer, the command does.
        c = ASSIST.ask("compile", "", 0, src_dir, str(src), extra=str(out))
        if "ok" in c:
            ok, output = c["ok"], tidy(c["output"], work)
        else:
            cmd = [JAVAC, "-J-Dstderr.encoding=UTF-8", "-J-Dstdout.encoding=UTF-8",
                   "-nowarn", "-encoding", "UTF-8", "-d", str(out)]
            if src_dir:
                cmd += ["-sourcepath", str(src_dir)]
            cmd.append(str(src))
            try:
                c = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8",
                                   errors="replace", timeout=120, stdin=subprocess.DEVNULL,
                                   creationflags=NO_WINDOW)
            except subprocess.TimeoutExpired:
                return {"phase": "compile", "ok": False, "output": "javac took longer than 120 s."}
            ok, output = c.returncode == 0, tidy(c.stderr + c.stdout, work)
        compile_ms = int((time.monotonic() - t0) * 1000)
        if not ok:
            return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms, "output": output}
        if not mains:
            return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms,
                    "output": "Compiled, but no class declares main() - nothing to run."}

        main = want_main if want_main in mains else javasrc.pick_main(mains, fname)
        cmd = [JAVA, "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
               "-Dfile.encoding=UTF-8", "-XX:TieredStopAtLevel=1", "-cp", str(out), prefix + main]
        rc, timed_out, output, truncated, run_ms = run_process(cmd, work, stdin, timeout)
        verdicts, checks = check_lines(output)
        return {"phase": "run", "ok": rc == 0 and not timed_out, "exitCode": rc,
                "timedOut": timed_out, "truncated": truncated, "file": fname,
                "ran": main, "mains": mains, "compileMs": compile_ms, "runMs": run_ms,
                "output": output, "verdicts": verdicts, "checks": checks}
    finally:
        shutil.rmtree(work, ignore_errors=True)


def run_process(cmd, work, stdin, timeout, env=None):
    """Run one program in the work folder, stdin from a file, for at most timeout seconds ->
    (exit code, timed out, its output (stdout and stderr together, tidied), cut at OUTPUT_CAP,
    milliseconds). Java, Python and Node runs all end here."""
    (work / "stdin.txt").write_text(stdin or "", encoding="utf-8")
    t0 = time.monotonic()
    with open(work / "stdin.txt", "rb") as fin, open(work / "output.txt", "wb") as fout:
        proc = subprocess.Popen(cmd, stdin=fin, stdout=fout, stderr=subprocess.STDOUT, cwd=work, env=env,
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
    return rc, timed_out, tidy(text, work), size > OUTPUT_CAP, run_ms


def run_result(rc, timed_out, output, truncated, run_ms, fname, compile_ms, runner):
    verdicts, checks = check_lines(output)
    return {"phase": "run", "ok": rc == 0 and not timed_out, "exitCode": rc, "timedOut": timed_out,
            "truncated": truncated, "file": fname, "runner": runner, "compileMs": compile_ms,
            "runMs": run_ms, "output": output, "verdicts": verdicts, "checks": checks}


def run_folder():
    """A temp folder by its long name: Node prints the long form of an 8.3 path such as
    RAVIVA~1, and tidy() must find the same text to take out."""
    return pathlib.Path(os.path.realpath(tempfile.mkdtemp(prefix="codeview_")))


def python_syntax(code, fname):
    """-> Python's own report of the first syntax error ("" when there is none). Compiles only;
    nothing runs. Warnings such as an invalid escape are left to the run."""
    import traceback, warnings
    try:
        with warnings.catch_warnings():
            warnings.simplefilter("ignore")
            compile(code, fname, "exec", dont_inherit=True)
    except SyntaxError as e:                 # IndentationError and TabError too
        return "".join(traceback.format_exception_only(e))
    except ValueError as e:                  # a NUL byte in the code
        return f"{fname}: {e}\n"
    return ""


def syntax_errors(lang, code):
    """POST /api/syntax: the editor's live red underline for Python (JavaScript gets its own
    from the editor). -> [{line, column, endColumn, message}], at most one."""
    if lang != "py":
        raise ValueError("Live syntax checks are for Python")
    import warnings
    try:
        with warnings.catch_warnings():
            warnings.simplefilter("ignore")
            compile(code, "<editor>", "exec", dont_inherit=True)
    except SyntaxError as e:
        col = e.offset or 1
        return [{"line": e.lineno or 1, "column": col,
                 "endColumn": e.end_offset if e.end_offset and e.end_offset > col and e.end_lineno == e.lineno else col + 1,
                 "message": f"{type(e).__name__}: {e.msg}"}]
    except ValueError as e:
        return [{"line": 1, "column": 1, "endColumn": 2, "message": str(e)}]
    return []


def run_python(code, rid, rel_path, stdin, timeout):
    """A .py file: Python's own syntax check, then python -X utf8 -u -B on a copy in a temp
    folder. PYTHONPATH is the file's real folder, so `import helper` finds helper.py beside it;
    -B means no __pycache__ appears there."""
    fname = pathlib.Path(rel_path).name if rel_path else "Scratch.py"
    folder = resolve(rid, rel_path).parent if rel_path else None
    if not PYTHON:
        return {"phase": "compile", "ok": False, "file": fname,
                "output": "No python.exe was found beside the Python that runs Code Viewer, so .py files cannot run."}
    t0 = time.monotonic()
    error = python_syntax(code, fname)
    compile_ms = int((time.monotonic() - t0) * 1000)
    if error:
        return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms, "output": error}
    work = run_folder()
    try:
        src = work / "src" / fname
        src.parent.mkdir()
        src.write_bytes(code.encode("utf-8"))
        env = dict(os.environ, PYTHONIOENCODING="utf-8", PYTHONUTF8="1", PYTHONDONTWRITEBYTECODE="1")
        if folder:
            env["PYTHONPATH"] = os.pathsep.join(p for p in (str(folder), os.environ.get("PYTHONPATH")) if p)
        res = run_process([PYTHON, "-X", "utf8", "-u", "-B", str(src)], work, stdin, timeout, env)
        return run_result(*res, fname, compile_ms, "Python " + platform_python())
    finally:
        shutil.rmtree(work, ignore_errors=True)


# Node's own frames ("at Module._compile (node:internal/...)") say nothing about the program
NODE_INTERNAL = re.compile(r"^\s+at (?:.* \()?node:internal/.*\n?", re.M)


def run_node(code, rid, rel_path, stdin, timeout, lang="js"):
    """A .js or .mjs file: node --check (syntax only), then node on a copy in a temp folder.
    Node 24 runs a .js that uses import/export as a module by itself."""
    fname = pathlib.Path(rel_path).name if rel_path else "Scratch.mjs" if lang == "mjs" else "Scratch.js"
    if not NODE:
        return {"phase": "compile", "ok": False, "file": fname,
                "output": "Node.js is not installed, so .js files cannot run. Install it from https://nodejs.org,"
                          " then restart Code Viewer (tray → Restart)."}
    work = run_folder()
    try:
        src = work / "src" / fname
        src.parent.mkdir()
        src.write_bytes(code.encode("utf-8"))
        t0 = time.monotonic()
        try:
            c = subprocess.run([NODE, "--check", str(src)], capture_output=True, text=True, encoding="utf-8",
                               errors="replace", timeout=60, stdin=subprocess.DEVNULL, cwd=work, creationflags=NO_WINDOW)
        except subprocess.TimeoutExpired:
            return {"phase": "compile", "ok": False, "file": fname, "output": "node --check took longer than 60 s."}
        compile_ms = int((time.monotonic() - t0) * 1000)
        if c.returncode != 0:
            return {"phase": "compile", "ok": False, "file": fname, "compileMs": compile_ms,
                    "output": NODE_INTERNAL.sub("", tidy(c.stderr + c.stdout, work))}
        rc, timed_out, output, truncated, run_ms = run_process([NODE, str(src)], work, stdin, timeout)
        return run_result(rc, timed_out, NODE_INTERNAL.sub("", output), truncated, run_ms, fname, compile_ms,
                          "Node " + (node_version() or ""))
    finally:
        shutil.rmtree(work, ignore_errors=True)


def platform_python():
    return sys.version.split()[0]


def langs():
    """What this server can run, for /api/info: Java, UI files (they run in the page), and
    Python and JavaScript when their interpreters are here."""
    return ["java", "html"] + (["py"] if PYTHON else []) + (["js"] if NODE else [])


_node_version = []


def node_version():
    """'v24.19.0', asked once; None without Node."""
    if not NODE:
        return None
    if not _node_version:
        try:
            v = subprocess.run([NODE, "--version"], capture_output=True, text=True, timeout=20,
                               stdin=subprocess.DEVNULL, creationflags=NO_WINDOW).stdout.strip()
        except (OSError, subprocess.TimeoutExpired):
            v = ""
        _node_version.append(v)
    return _node_version[0] or None


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
    """Show `A01_X.java:12: error` instead of the temp folder path (Node's modules print it as
    a file:/// URL)."""
    src = work / "src"
    for form in ("file:///" + src.as_posix() + "/", str(src) + "\\", str(src) + "/", str(work) + "\\"):
        text = text.replace(form, "")
    return text


# ---------------------------------------------------------------- autocomplete helper
# The editor's dropdown, parameter hints, hover docs and live error underlines come from
# tools/CvAssist.java: one JVM that keeps javac warm, so each answer takes ~15-30 ms instead
# of the ~1 s a fresh javac needs. It is compiled once into the cache folder (outside the
# repo), started on the first request, and stopped after ASSIST_IDLE seconds unused (it
# holds ~210 MB). It also exits by itself when this server goes, because its stdin closes.

ASSIST_SRC = HERE / "CvAssist.java"
ASSIST_IDLE = 20 * 60
# Measured 5 Oct on C06_DailyTemperatures (30 warm runs each): -Xmx384m alone held 305 MB;
# stopping the JIT at its first tier holds 208 MB, with the same ~20 ms per suggestion.
ASSIST_JVM = os.environ.get("CODEVIEW_ASSIST_JVM", "-Xmx256m -XX:+UseSerialGC -XX:TieredStopAtLevel=1").split()
ASSIST_OPS = {"complete", "signature", "hover", "doc", "check", "warm", "imports", "definition", "rename", "extract",
              "format", "create"}
ASSIST_WAIT = {"check": 10, "warm": 60, "compile": 120}   # seconds; the rest 4. A cold start adds 40.
LATEST_ONLY = {"complete", "signature", "hover", "check"}   # a newer request makes a queued one pointless
ASSIST_BUILDS = lambda: offline.cache_dir().parent / "assist"   # noqa: E731  one folder per helper build


def assist_build():
    """Name of the build of this CvAssist.java by this JDK (a new one after every change)."""
    return hashlib.sha1(ASSIST_SRC.read_bytes() + str(JAVAC).encode()).hexdigest()[:12]


# ---------------------------------------------------------------- start-up clean-up
# 5 Oct, Ravi asked what piles up. Two things did, with nothing removing them: a helper build
# per CvAssist.java change (~100 KB each) and the temp folder of a Run whose server was killed
# mid-way. Every start removes both once they are an hour old; a Run lasts 3 minutes at most.

SWEEP_AGE = 3600


def sweep_leftovers(builds=None, temp=None):
    """-> (folders removed, bytes freed). Old helper builds except the current one, and
    codeview_* Run folders. Anything in use or locked is left for the next start."""
    builds = pathlib.Path(builds or ASSIST_BUILDS())
    temp = pathlib.Path(temp or tempfile.gettempdir())
    current = assist_build()
    found = [p for p in builds.iterdir() if p.is_dir() and p.name != current] if builds.is_dir() else []
    found += [p for p in temp.glob("codeview_*") if p.is_dir()] if temp.is_dir() else []
    removed = freed = 0
    for p in found:
        try:
            if time.time() - p.stat().st_mtime < SWEEP_AGE:
                continue
            size = sum(f.stat().st_size for f in p.rglob("*") if f.is_file())
            shutil.rmtree(p)
            removed, freed = removed + 1, freed + size
        except OSError:
            continue
    if removed:
        log.info("clean-up: removed %d leftover folder(s), %.1f MB", removed, freed / 1e6)
    return removed, freed


class Assist:
    def __init__(self):
        self.lock = threading.Lock()
        self.proc = None
        self.lines = None
        self.used = 0.0
        self.latest = {}
        self.tickets = itertools.count()
        self.watching = False

    def classes(self):
        """Folder with CvAssist.class, compiled once per version of the source and the JDK."""
        digest = assist_build()
        out = ASSIST_BUILDS() / digest
        if (out / "CvAssist.class").is_file():
            os.utime(out)                          # "in use": the start-up sweep leaves it for an hour
            return out
        out.parent.mkdir(parents=True, exist_ok=True)
        tmp = pathlib.Path(tempfile.mkdtemp(prefix=digest + ".", dir=out.parent))
        try:
            r = subprocess.run([JAVAC, "-J-Dstderr.encoding=UTF-8", "-nowarn", "-encoding", "UTF-8",
                                "-d", str(tmp), str(ASSIST_SRC)], capture_output=True, text=True,
                               encoding="utf-8", errors="replace", timeout=180, stdin=subprocess.DEVNULL,
                               creationflags=NO_WINDOW)
            if r.returncode:
                raise RuntimeError("could not compile CvAssist.java: " + (r.stderr + r.stdout)[-600:])
            try:
                tmp.replace(out)
            except OSError:
                if not (out / "CvAssist.class").is_file():    # not another server compiling it at once
                    raise
        finally:
            shutil.rmtree(tmp, ignore_errors=True)
        return out

    def start(self):
        cp = self.classes()
        self.proc = subprocess.Popen([JAVA, *ASSIST_JVM, "-Dfile.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                                      "-Dstdout.encoding=UTF-8", "-cp", str(cp), "CvAssist"],
                                     stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                     stderr=subprocess.DEVNULL, creationflags=NO_WINDOW)
        lines = self.lines = queue.Queue()

        def pump(out):
            try:
                for line in out:
                    lines.put(line)
            except (OSError, ValueError):          # closed by stop()
                pass
            lines.put(None)                        # it exited
        threading.Thread(target=pump, args=(self.proc.stdout,), daemon=True).start()
        if not self.watching:
            self.watching = True
            threading.Thread(target=self.idle_watch, daemon=True).start()
        log.info("autocomplete helper started")

    def idle_watch(self):
        while True:
            time.sleep(60)
            with self.lock:
                if self.proc and time.monotonic() - self.used > ASSIST_IDLE:
                    log.info("autocomplete helper stopped after %d idle minutes", ASSIST_IDLE // 60)
                    self.stop()

    def stop(self):
        p, self.proc = self.proc, None
        if not p:
            return
        try:
            p.stdin.close()
            p.wait(timeout=2)
        except (OSError, ValueError, subprocess.TimeoutExpired):
            p.kill()
            p.wait()
        try:
            p.stdout.close()
        except (OSError, ValueError):
            pass

    def ask(self, op, code="", offset=0, src_dir=None, fname="Scratch.java", flags="", extra=""):
        """One answer from the helper as a dict; {"error": ...} if it failed (it restarts on the
        next request), {"stale": True} if a newer request of the same kind was already waiting."""
        ticket = next(self.tickets)
        self.latest[op] = ticket
        with self.lock:
            if op in LATEST_ONLY and self.latest.get(op) != ticket:
                return {"stale": True}
            cold = self.proc is None or self.proc.poll() is not None
            try:
                if cold:
                    self.stop()                    # a helper that died still holds its pipes
                    self.start()
                body = (code or "").encode("utf-8")
                fields = (op, str(int(offset)), str(src_dir or ""), fname, flags, extra, str(len(body)))
                header = "\t".join(re.sub(r"[\t\r\n]", " ", f) for f in fields)
                self.proc.stdin.write(header.encode("utf-8") + b"\n" + body)
                self.proc.stdin.flush()
                line = self.lines.get(timeout=ASSIST_WAIT.get(op, 4) + (40 if cold else 0))
            except queue.Empty:
                log.warning("autocomplete helper took too long on %s; restarting it", op)
                self.stop()
                return {"error": "The autocomplete helper took too long; it restarts on the next request."}
            except (OSError, RuntimeError, subprocess.SubprocessError) as e:
                log.warning("autocomplete helper failed: %s", e)
                self.stop()
                return {"error": f"Autocomplete is unavailable: {e}"}
            self.used = time.monotonic()
            if line is None:
                self.stop()
                return {"error": "The autocomplete helper stopped; it restarts on the next request."}
            try:
                return json.loads(line)
            except ValueError:
                return {"error": "The autocomplete helper answered something unreadable."}


ASSIST = Assist()
atexit.register(ASSIST.stop)


def assist_request(req):
    op = req.get("op", "")
    if op not in ASSIST_OPS:
        raise ValueError(f"unknown autocomplete request: {op}")
    if op in ("doc", "warm"):
        return ASSIST.ask(op, extra=str(req.get("key") or ""))
    code, rid = req.get("code") or "", req.get("root", "")
    src_dir, fname, info = compile_target(code, rid, req.get("path") or None)
    res = ASSIST.ask(op, code, int(req.get("offset") or 0), src_dir, fname, "c" if info[3] else "",
                     str(req.get("key") or ""))
    if "file" in res:                  # a definition in another file: the page opens it by its path
        try:
            res["path"] = pathlib.Path(res.pop("file")).resolve().relative_to(root_path(rid).resolve()).as_posix()
        except ValueError:             # outside the open folder: nothing to open
            res.pop("line", None)
    return res


def jdk_version():
    try:
        r = subprocess.run([JAVA, "-version"], capture_output=True, text=True, timeout=20,
                           stdin=subprocess.DEVNULL, creationflags=NO_WINDOW)
        m = re.search(r'version "([^"]+)"', r.stderr)
        return m.group(1) if m else "unknown"
    except (OSError, subprocess.TimeoutExpired):
        return "not found"


# ---------------------------------------------------------------- HTTP

VENDOR_TYPES = {".js": "text/javascript; charset=utf-8", ".css": "text/css; charset=utf-8",
                ".ttf": "font/ttf", ".json": "application/json", ".svg": "image/svg+xml"}


class Server(ThreadingHTTPServer):
    def handle_error(self, request, client_address):
        if not isinstance(sys.exc_info()[1], ConnectionError):   # a closed tab is not an error
            log.exception("request failed")


def make_server(port):
    server = Server(("127.0.0.1", port), Handler)
    port = server.server_address[1]
    server.allowed_hosts = {f"127.0.0.1:{port}", f"localhost:{port}"}
    server.jdk = jdk_version()
    return server


class Handler(BaseHTTPRequestHandler):
    server_version = "codeview"

    def log_message(self, fmt, *args):
        pass

    def send(self, code, body, ctype="text/plain; charset=utf-8", cache="no-store"):
        if isinstance(body, str):
            body = body.encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", ctype)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", cache)
        self.send_header("X-Content-Type-Options", "nosniff")
        self.end_headers()
        self.wfile.write(body)

    def send_json(self, obj, code=200):
        self.send(code, json.dumps(obj), "application/json; charset=utf-8")

    def host_ok(self):
        return self.headers.get("Host", "") in self.server.allowed_hosts

    def drain(self):
        """Read and drop a refused request's body first. Windows resets a socket closed with
        unread data, so the sender got "connection aborted" instead of the 403 (5 Oct: 21 of
        300 refusals; the delete test failed about 1 run in 7)."""
        try:
            n = int(self.headers.get("Content-Length") or 0)
        except ValueError:
            return
        if 0 < n <= 4_000_000:
            self.rfile.read(n)

    def do_GET(self):
        if not self.host_ok():
            return self.send(403, "Forbidden host")
        u = urlparse(self.path)
        q = {k: v[0] for k, v in parse_qs(u.query).items()}
        rid = q.get("root", "")
        try:
            if u.path == "/":
                html = PAGE.read_bytes()
                body = (html.replace(b"__CV_VERSION__", page_version(html).encode())
                        .replace(b"__CV_MONACO__", offline.url_for(offline.MONACO).encode())
                        .replace(b"__CV_MARKED__", offline.url_for(offline.MARKED).encode())
                        .replace(b"__CV_MERMAID__", offline.url_for(offline.MERMAID).encode()))
                return self.send(200, body, "text/html; charset=utf-8")
            if u.path.startswith("/vendor/"):
                p = offline.path_for(unquote(u.path[len("/vendor/"):]))
                # versioned paths never change, so the browser may keep them
                return self.send(200, p.read_bytes(), VENDOR_TYPES.get(p.suffix, "application/octet-stream"),
                                 "public, max-age=31536000, immutable")
            if u.path == "/api/info":
                return self.send_json({"jdk": self.server.jdk, "repo": REPO.name, "version": page_version(PAGE.read_bytes()),
                                       "autostart": autostart_on(), "platform": sys.platform, "git": True, "stub": True,
                                       "files": True, "langs": langs(), "python": PYTHON and platform_python(),
                                       "node": node_version()})
            if u.path == "/api/roots":
                return self.send_json(STATE.roots())
            if u.path == "/api/tree":
                return self.send_json(build_tree(rid))
            if u.path == "/api/file":
                return self.send_json(read_file(rid, q.get("path", "")))
            if u.path == "/api/practice":
                rel = q.get("path", "")
                lang = langsrc.lang_of(rel)
                if not lang:
                    raise ValueError("Practice works on .java, .py, .js and .html files")
                text = read_file(rid, rel)["text"]
                skeleton, hidden = langsrc.practice_skeleton(text, lang)
                return self.send_json({"text": skeleton, "hidden": hidden, "hints": langsrc.practice_hints(text, lang)})
            if u.path == "/api/progress":
                return self.send_json(STATE.progress(rid))
            if u.path == "/api/git":
                return self.send_json(git_status(rid, q.get("readmes") == "1"))
            if u.path == "/api/search":
                return self.send_json(search(rid, q.get("q", ""), q.get("regex") == "1", q.get("case") == "1"))
        except (ValueError, OSError, subprocess.TimeoutExpired) as e:
            return self.send_json({"error": str(e)}, 400)
        self.send(404, "Not found")

    def do_POST(self):
        origin = self.headers.get("Origin")
        if (not self.host_ok() or self.headers.get("X-CodeView") != "1"
                or (origin and origin.split("//", 1)[-1] not in self.server.allowed_hosts)):
            self.drain()
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
                               timeout, req.get("main"), req.get("lang"))
                status = "ok" if res.get("ok") else ("timeout" if res.get("timedOut") else "failed")
                print(f"  run {req.get('path') or 'scratch'}  ->  {res.get('phase')} {status}", flush=True)
                return self.send_json(res)
            if path == "/api/assist":
                return self.send_json(assist_request(req))
            if path == "/api/check":            # a UI file's test output, run in the page: its ticks
                verdicts, checks = check_lines(str(req.get("output", "")))
                return self.send_json({"verdicts": verdicts, "checks": checks})
            if path == "/api/syntax":
                return self.send_json({"errors": syntax_errors(req.get("lang"), str(req.get("code", "")))})
            if path == "/api/save":
                res = save_file(rid, req["path"], req.get("code", ""), req.get("base"), req.get("force"))
                return self.send_json(res, 409 if res.get("conflict") else 200)
            if path == "/api/new" and "name" in req:
                return self.send_json(new_problem(rid, req.get("folder", ""), req.get("level", ""), req["name"],
                                                  req.get("source", ""), req.get("difficulty", ""),
                                                  bool(req.get("must")), req.get("method", ""), req.get("lang") or "java"))
            if path == "/api/new":
                return self.send_json(new_file(rid, req.get("path", "")))
            if path == "/api/git/push":
                return self.send_json(git_push(rid, req.get("paths") or [], req.get("message", "")))
            if path == "/api/delete" and req.get("folder"):
                return self.send_json(delete_folder(rid, req.get("path", "")))
            if path == "/api/delete":
                return self.send_json(delete_file(rid, req.get("path", "")))
            if path == "/api/mkdir":
                return self.send_json(new_folder(rid, req.get("path", "")))
            if path == "/api/rename":
                return self.send_json(rename_path(rid, req.get("path", ""), req.get("name", "")))
            if path == "/api/progress" and "restore" in req:
                return self.send_json(STATE.restore_progress(rid, req["path"], req["restore"]))
            if path == "/api/progress":
                return self.send_json(STATE.set_progress(rid, req["path"], req.get("status"), req.get("pass"),
                                                         req.get("secs"), req.get("hints")))
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
            if path == "/api/open-local":       # the file list's right-click → Open in browser
                return self.send_json(open_local(rid, req.get("path", ""), req.get("browser", "")))
            if path == "/api/locate":           # Explorer's right-click; a POST since it may add a folder
                return self.send_json(locate(req.get("path", "")))
            if path == "/api/autostart":
                set_autostart(bool(req.get("enabled")))
                return self.send_json({"autostart": autostart_on()})
        except (ValueError, KeyError, OSError, subprocess.CalledProcessError, subprocess.TimeoutExpired) as e:
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


def pythonw():
    """The console-less Python next to this one: shortcuts and the right-click menu use it."""
    pyw = pathlib.Path(sys.executable).with_name("pythonw.exe")
    if not pyw.exists():
        raise ValueError(f"pythonw.exe not found next to {sys.executable}")
    return pyw


def make_shortcut(lnk, args):
    """A .lnk that runs this script under pythonw (no console) with the given arguments."""
    ensure_icon()
    pyw = pythonw()
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


# Explorer's right-click "Open in Code Viewer" (7 Oct): one verb for every file, one for a
# folder, one for the empty space inside a folder (%V is that folder). Per user, so no admin.
# Windows 11 puts verbs like these under "Show more options"; Shift+right-click shows them.
MENU_BASE = r"Software\Classes"
MENU_KEYS = (("*", "%1"), ("Directory", "%1"), (r"Directory\Background", "%V"))


def context_menu_on(base=MENU_BASE):
    """True/False on Windows; None where there is no such menu."""
    if not IS_WIN:
        return None
    import winreg
    try:
        winreg.CloseKey(winreg.OpenKey(winreg.HKEY_CURRENT_USER, rf"{base}\*\shell\CodeViewer\command"))
        return True
    except OSError:
        return False


def set_context_menu(enabled, base=MENU_BASE):
    if not IS_WIN:
        raise ValueError("The right-click menu is only set up on Windows")
    import winreg
    hk = winreg.HKEY_CURRENT_USER
    for cls, arg in MENU_KEYS:
        key = rf"{base}\{cls}\shell\CodeViewer"
        if not enabled:
            for k in (key + r"\command", key):
                try:
                    winreg.DeleteKey(hk, k)
                except FileNotFoundError:
                    pass
            continue
        with winreg.CreateKeyEx(hk, key, 0, winreg.KEY_SET_VALUE) as k:
            winreg.SetValueEx(k, "", 0, winreg.REG_SZ, "Open in Code Viewer")
            winreg.SetValueEx(k, "Icon", 0, winreg.REG_SZ, str(ensure_icon()))
            winreg.SetValueEx(k, "MultiSelectModel", 0, winreg.REG_SZ, "Single")   # 10 files: no 10 tabs
        with winreg.CreateKeyEx(hk, key + r"\command", 0, winreg.KEY_SET_VALUE) as k:
            winreg.SetValueEx(k, "", 0, winreg.REG_SZ, f'"{pythonw()}" "{HERE / "codeview.py"}" --tray --open "{arg}"')


def quit_from_tray(icon, server):
    log_stop("Quit from the tray menu")
    icon.stop()
    server.shutdown()


def start_detached(args):
    """A new copy that outlives this one, with no console window."""
    flags = (0x8 | 0x200) if IS_WIN else 0          # DETACHED_PROCESS | CREATE_NEW_PROCESS_GROUP
    subprocess.Popen(args, creationflags=flags, close_fds=True, start_new_session=not IS_WIN)


def restart_from_tray(icon, server, argv=None, spawn=start_detached):
    """Stop, then start a fresh copy with the same options, so a change to the server code
    takes effect (5 Oct: a fix needed Quit and a start by hand). The port is freed first,
    so the new copy gets the same one; the open tab then offers to reload."""
    log_stop("Restart from the tray menu")
    server.shutdown()
    server.server_close()
    args, rest = [], iter((argv or sys.argv)[1:])
    for x in rest:                      # a tray started by a right-click: don't open that path again
        if x == "--open":
            next(rest, None)
        elif not x.startswith("--open="):
            args.append(x)
    if "--no-open" not in args:
        args.append("--no-open")
    spawn([sys.executable, str(pathlib.Path(__file__).resolve())] + args)
    icon.stop()


def tray_menu(pystray, server, url):
    """Quit is a submenu: the second click is the confirmation. It used to be a Yes/No box,
    which on 5 Oct opened out of sight and froze the tray until it was answered."""
    def toggle_autostart(icon, _item):
        set_autostart(not STARTUP_LNK.exists())

    def toggle_context_menu(icon, _item):
        set_context_menu(not context_menu_on())

    def pick_browser(bid):
        def act(icon, _item):
            STATE.set_browser(bid)
            icon.update_menu()          # the Windows menu is built once; rebuild it for the tick
        return act

    def open_in_items():
        found = browsers()
        ids = {b["id"] for b in found}

        def item(label, bid):
            return pystray.MenuItem(label, pick_browser(bid), radio=True, checked=lambda _i: (
                STATE.data.get("browser") if STATE.data.get("browser") in ids else None) == bid)
        return [item("Default browser", None)] + [item(b["label"], b["id"]) for b in found]

    return pystray.Menu(
        pystray.MenuItem("Open Code Viewer", lambda *_: open_page(url), default=True),
        pystray.MenuItem("Open in", pystray.Menu(open_in_items), visible=IS_WIN),
        pystray.MenuItem("Open repo folder", lambda *_: open_path(REPO)),
        pystray.MenuItem("Start with Windows", toggle_autostart, checked=lambda _i: bool(autostart_on()),
                         visible=IS_WIN),
        pystray.MenuItem("Right-click: Open in Code Viewer", toggle_context_menu,
                         checked=lambda _i: bool(context_menu_on()), visible=IS_WIN),
        pystray.Menu.SEPARATOR,
        pystray.MenuItem("Restart Code Viewer", lambda icon, _item: restart_from_tray(icon, server)),
        pystray.MenuItem("Quit Code Viewer", pystray.Menu(
            pystray.MenuItem("Yes, stop it (the page stops working)",
                             lambda icon, _item: quit_from_tray(icon, server)))))


def run_tray(server, url, open_browser, open_url=None):
    try:
        import pystray
        from PIL import Image
    except ImportError:
        log_stop("the tray icon needs pystray and Pillow")
        sys.exit("The tray icon needs pystray and Pillow:  pip install pystray pillow")
    tray = {}

    def serve():
        try:
            server.serve_forever()
        except Exception:                                  # noqa: BLE001 - logged, then the icon goes too
            log.exception("the web server failed")
            log_stop("the web server failed (the error is above)")
            if "icon" in tray:
                tray["icon"].stop()
    threading.Thread(target=serve, daemon=True).start()

    def setup(icon):
        icon.visible = True
        if open_browser:
            open_page(open_url or url)

    image = Image.open(ICON) if ICON.exists() else draw_icon()
    tray["icon"] = pystray.Icon("codeview", image, f"Code Viewer - {url}", tray_menu(pystray, server, url))
    tray["icon"].run(setup=setup)


def main():
    ap = argparse.ArgumentParser(description="Read, practise and run code in a browser.")
    ap.add_argument("--port", type=int, default=8025)
    ap.add_argument("--no-open", action="store_true", help="do not open a browser tab")
    ap.add_argument("--tray", action="store_true", help="run with a tray icon and no console")
    ap.add_argument("--install-shortcuts", action="store_true",
                    help="create Desktop and Start-menu shortcuts that start the tray version")
    ap.add_argument("--autostart", choices=["on", "off"],
                    help="start the tray version when Windows starts (or stop doing so)")
    ap.add_argument("--open", metavar="PATH",
                    help="open the page on this file or folder (Explorer's right-click menu uses it)")
    ap.add_argument("--context-menu", choices=["on", "off"],
                    help='"Open in Code Viewer" in File Explorer\'s right-click menu (or remove it)')
    a = ap.parse_args()

    if a.install_shortcuts:
        return install_shortcuts()
    if a.autostart:
        if not IS_WIN:
            sys.exit("--autostart is Windows-only. On macOS or Linux run tools/codeview from a terminal.")
        set_autostart(a.autostart == "on")
        print(f"  start with Windows: {'on' if autostart_on() else 'off'}  ({STARTUP_LNK})")
        return
    if a.context_menu:
        if not IS_WIN:
            sys.exit("--context-menu is Windows-only.")
        set_context_menu(a.context_menu == "on")
        print(f"  right-click \"Open in Code Viewer\": {'on' if context_menu_on() else 'off'}"
              f"  (HKEY_CURRENT_USER\\{MENU_BASE}\\*\\shell\\CodeViewer and the Directory keys)")
        return
    setup_log()
    if already_running(a.port):
        url = f"http://127.0.0.1:{a.port}/"
        if a.open:
            try:
                url = open_in_running(a.port, a.open)
            except (ValueError, OSError) as e:
                return tell(f"Could not open {a.open} in Code Viewer:\n{e}")
            return log.info(f"opened {a.open} -> {url}")
        log.info(f"second launch: already running at {url}" + ("" if a.no_open else ", opened a tab"))
        print(f"codeview is already running at {url} - opening it.")
        if not a.no_open:
            open_page(url)
        return

    server = None
    for port in range(a.port, a.port + 10):
        try:
            server = make_server(port)
            break
        except OSError:
            continue
    if server is None:
        log.info(f"could not start: ports {a.port}-{a.port + 9} are all busy")
        sys.exit(f"Ports {a.port}-{a.port + 9} are all busy.")
    port = server.server_address[1]
    url = f"http://127.0.0.1:{port}/"
    log_crashes()
    log_start(port, "tray" if a.tray else "console")
    threading.Thread(target=offline.ensure, args=(log.info,), name="offline-copy", daemon=True).start()
    threading.Thread(target=sweep_leftovers, name="clean-up", daemon=True).start()
    start_url = url
    if a.open:
        try:
            start_url = page_url(port, locate(a.open))
            log.info(f"opened {a.open} -> {start_url}")
        except ValueError as e:
            tell(f"Could not open {a.open} in Code Viewer:\n{e}")
    if a.tray:
        return run_tray(server, url, not a.no_open or bool(a.open), start_url)

    print(f"codeview  {url}")
    print(f"  repo {REPO}")
    print(f"  JDK  {server.jdk}  ({JAVA})")
    print("  Ctrl+C to stop", flush=True)
    if not a.no_open or a.open:
        open_page(start_url)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        log_stop("Ctrl+C")
        print("stopped")


if __name__ == "__main__":
    main()
