#!/usr/bin/env python3
"""
build_site - Code Viewer as a plain website, for browsers that cannot reach the laptop: the
Learning Hub's public copy serves it at tools/java-practice/ (6 Nov 2026, Ravi: "easily accessed
from any device without blocking by any company policy").

    python -I tools/build_site.py OUT_DIR
    python -I tools/build_site.py --publish     build into ../learning-hub/tools/java-practice, then commit
                                                and push only that folder there (Code Viewer's Push runs
                                                this after each push of this repo); --json prints the result

OUT_DIR gets:
    index.html          codeview.html with its libraries pointed at vendor/, a few website patches
                        (below), and site_static.js loaded first
    static.js           tools/site_static.js: answers the page's /api/... requests in the browser
    data/tree.json      the file list (what /api/tree gives)
    data/files.json     each file's text and hash, and for Java files how to run them on a PC
    data/practice.json  each practice file's skeleton, hidden notes and hints (langsrc, as /api/practice)
    vendor/             Monaco, marked and Mermaid from Code Viewer's offline copy (tools/offline.py)

What works there: reading, Practice, Hint, Compare, Visual, Next, Done / Revise (kept in that
browser), the file filter and search in code. JavaScript runs in a Web Worker and UI pages (.html)
in their frame, both ticked by tools/outcheck.js (outcheck.py ported). What does not: running Java
or Python (Run shows how to run it on a PC instead), Save, new files, Push and Java suggestions.

Only committed files are published, as HEAD has them (an unsaved or uncommitted edit never reaches
the website), and of those only what Code Viewer itself lists (SKIP_IN_REPO keeps tools/,
_personal/ and 08-Reference/ out). Every page asks search engines not to index it. The build stops
if a file looks like it holds a secret.
"""
import hashlib, io, json, pathlib, re, shutil, subprocess, sys, tarfile, tempfile, time

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parent
HUB = REPO.parent / "learning-hub"           # the public site's repo, next to this one
SITE_DIR = "tools/java-practice"             # where the site lives in it
sys.path.insert(0, str(HERE))
import codeview  # noqa: E402  the file rules (TEXT_EXT, SKIP_*), so the site lists what Code Viewer lists
import javasrc  # noqa: E402  which class declares main()
import langsrc  # noqa: E402  practice skeletons and hints
import offline  # noqa: E402  where the offline library copies are

NOINDEX = '<meta name="robots" content="noindex">'
IO_CALL = re.compile(r"\bIO\s*\.\s*(?:println|print|readln)\s*\(")
MODULE_IMPORT = re.compile(r"^\s*import\s+module\s", re.M)
# a secret or personal detail must never reach a public page; a hit stops the build
SECRETS = re.compile(r"AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9]{30,}|sk-[A-Za-z0-9_-]{20,}|xox[abp]-[A-Za-z0-9-]{10,}"
                     r"|-----BEGIN [A-Z ]*PRIVATE KEY-----|AIza[0-9A-Za-z_-]{35}")

# Text changes to codeview.html for the website. Each `old` must appear exactly once, so a
# change to the page that moves one stops the build instead of quietly dropping the patch.
PATCHES = [
    # the header pill says what this is, instead of a JDK version
    ("$('#jdk').textContent = 'JDK ' + S.info.jdk;",
     "$('#jdk').textContent = S.info.pill || 'JDK ' + S.info.jdk;\n  if (S.info.pillTitle) $('#jdk').title = S.info.pillTitle;"),
    # Run on Java / Python / JavaScript answers with how to run it elsewhere (site_static.js)
    ("const runStatus = res => res.phase === 'compile' && !res.ok",
     "const runStatus = res => res.phase === 'elsewhere' ? ['Runs on your PC: how, below', 'away']\n  : res.phase === 'compile' && !res.ok"),
    # the output pane's first hint
    ("out.innerHTML = c && isCode(c) ? `<div class=\"hint\">Press",
     "out.innerHTML = c && isCode(c) && (c.kind === 'java' || c.kind === 'py') && window.CV_STATIC ? `<div class=\"hint\">This website cannot run "
     "${KIND_NAME[c.kind]}. Copy the code (the copy button above) to run it on your PC; ▶ Run shows how.</div>`\n"
     "      : c && isCode(c) ? `<div class=\"hint\">Press"),
    # the list's right-click menu is all laptop actions (open in Explorer, rename, delete...)
    ("function showRowMenu(e) {\n  if (e.shiftKey) return;",
     "function showRowMenu(e) {\n  if (e.shiftKey || window.CV_STATIC) return;"),
    # one folder only: no "Open another folder..."
    ("  sel.title = cur ? cur.path : '';", "  sel.title = cur ? cur.path : '';\n  sel.disabled = !!window.CV_STATIC;"),
    # say what this copy is at the top of the help
    # Try cannot run here
    ("'Try: a copy to change and run freely. The file is never touched; Discard when you are done.'",
     "'Try: a copy to change freely; Copy takes it to your PC to run. The file is never touched; Discard when you are done.'"),
    ("<h3>Shortcuts</h3>",
     "<h3>Website copy</h3>\n  <p class=\"site-note\">This is Code Viewer's website copy. Reading, Practice, Hint, Compare, Visual, "
     "Next, Done / Revise, the file filter and Search in code work here. JavaScript files run in this browser and UI "
     "pages (.html) run with their tests, both with ✓ / ✗ on each expected line, so a solved practice marks itself done. "
     "Your progress, drafts and attempts are kept in this browser only. Java and Python cannot run here: "
     "▶ Run shows how to run the file on a PC (copy it into an IntelliJ scratch file, or <code>java File.java</code>). "
     "Save, new files, Push and Java suggestions need Code Viewer on the laptop.</p>\n  <h3>Shortcuts</h3>"),
]
# laptop-only controls, hidden
CSS = """<style>
  #newBtn, #pushBtn, #autostartRow, #rootPanel, #browseBtn,
  #menu [data-act="reveal"], #menu [data-act="delete"], #menu [data-act="assistPractice"] { display: none !important; }
  #rootSel:disabled { opacity: 1; cursor: default; }
  .status.away { color: var(--muted); }
  .help .site-note { margin: 0 0 12px; max-width: 72ch; }
  /* a phone: the file list or the file, one at a time (static.js hides the list once a file opens).
     The file's bar wraps: the name on the first line, then Run and Copy first, then the rest. */
  @media (max-width: 760px) {
    body:not(.side-collapsed) { grid-template-columns: 100vw 0 0 !important; }
    body:not(.side-collapsed) .vsplit, body:not(.side-collapsed) main { display: none; }
    #jdk, #timeoutSel, main .bar .spacer { display: none; }
    main { grid-template-rows: auto auto minmax(0, 1fr) 4px var(--out-h, 230px); }
    main.out-collapsed { grid-template-rows: auto auto minmax(0, 1fr) 4px 36px; }
    main .bar { flex-wrap: wrap; row-gap: 6px; padding: 6px 8px; }
    main .bar .crumbs { flex: 1 1 0; }
    main .bar .tools { flex: 1 1 100%; flex-wrap: wrap; row-gap: 6px; }
    #runBtn { order: -2; }
    #copyBtn { order: -1; }
  }
</style>
"""


def committed_files():
    """{path: bytes} of every file in HEAD, so the site shows what is committed: never an
    unsaved draft or a file changed but not yet committed (6 Nov)."""
    tar = subprocess.run(["git", "archive", "--format=tar", "HEAD"], cwd=REPO, capture_output=True, check=True).stdout
    with tarfile.open(fileobj=io.BytesIO(tar)) as t:
        return {m.name: t.extractfile(m).read() for m in t.getmembers() if m.isfile()}


def listed(rel, size):
    """True when Code Viewer's file list shows this repo path (codeview.walk's rules)."""
    parts = rel.split("/")
    skip = codeview.SKIP_ALWAYS | codeview.SKIP_IN_REPO
    if any(d in skip or d.startswith(".") for d in parts[:-1]):
        return False
    return pathlib.PurePosixPath(rel).suffix.lower() in codeview.TEXT_EXT and size <= codeview.MAX_VIEW


def run_info(text, name):
    """How a Java file runs on a PC: main()'s class, whether it is the file's first class (then
    `java File.java` starts it), its package, and whether it needs JDK 25."""
    pkg, mains, _public, compact = javasrc.analyse(text)
    s = javasrc.strip_noise(text)
    depth = javasrc.depths(s)
    tops = [m.group(3) for m in javasrc.TYPE_DECL.finditer(s) if depth[m.start(3)] == 0]
    main = javasrc.pick_main(mains, name) if mains else None
    return {"main": main, "first": bool(main) and tops[:1] == [main], "pkg": pkg,
            "jdk25": bool(compact or IO_CALL.search(s) or MODULE_IMPORT.search(s))}


# Offline after one visit (tools/site_sw.js): the editor files saved up front. The JavaScript/HTML/
# CSS language services (6.5 MB), the diagram library (3.2 MB) and other languages are saved when
# first used instead, so a first visit on a phone does not download all 26 MB.
VENDOR_UP_FRONT = re.compile(r"^vendor/(marked@[^/]+/.*|monaco-editor@[^/]+/min/vs/(loader\.js|editor/.*|base/.*"
                             r"|basic-languages/(java|python|javascript|typescript|html|css|markdown|xml|sql|yaml"
                             r"|ini|shell|kotlin)/.*))$")
MANIFEST = {"name": "Java Practice · Code Viewer", "short_name": "Java Practice", "start_url": "./", "scope": "./",
            "display": "standalone", "background_color": "#1e1f22", "theme_color": "#3574f0",
            "description": "The java-interview-practice problems and notes in Code Viewer, offline too.",
            "icons": [{"src": f"icon-{n}.png", "sizes": f"{n}x{n}", "type": "image/png"} for n in (192, 512)]}
HEAD_LINKS = ('<link rel="manifest" href="manifest.webmanifest">\n<meta name="theme-color" content="#3574f0">\n'
              '<link rel="apple-touch-icon" href="icon-192.png">\n')


def write_offline(out, version):
    """sw.js (site_sw.js with this build's file lists), the manifest and its icons."""
    for n in (192, 512):
        codeview.draw_icon(n).save(out / f"icon-{n}.png")
    (out / "manifest.webmanifest").write_text(json.dumps(MANIFEST, ensure_ascii=False, indent=1), encoding="utf-8", newline="\n")
    core = ["./", "static.js", "outcheck.js", "manifest.webmanifest", "icon-192.png", "icon-512.png",
            "data/tree.json", "data/files.json", "data/practice.json"]
    vendor = sorted(r for r in (p.relative_to(out).as_posix() for p in (out / "vendor").rglob("*") if p.is_file())
                    if VENDOR_UP_FRONT.match(r))
    sw = (HERE / "site_sw.js").read_text(encoding="utf-8")
    for key, value in (("'__VERSION__'", json.dumps(version)), ("__CORE__", json.dumps(core)), ("__VENDOR__", json.dumps(vendor))):
        if sw.count(key) != 1:
            sys.exit(f"site_sw.js: expected one {key}")
        sw = sw.replace(key, value)
    (out / "sw.js").write_text(sw, encoding="utf-8", newline="\n")


def retry(fn, *args):
    """OneDrive holds a file or folder for a moment while it syncs it, and Windows then says
    "Access is denied" (6 Nov: the first --publish stopped half-way through deleting the old
    site). Try again for a few seconds before giving up."""
    for wait in (0.3, 0.7, 1.5, 3, 0):
        try:
            return fn(*args)
        except PermissionError:
            if not wait:
                raise
            time.sleep(wait)


def sync(stage, out):
    """Make out the same as stage, touching only what differs: changed and new files are
    copied, files no longer built are deleted, and folders are never removed wholesale."""
    want = {p.relative_to(stage) for p in stage.rglob("*") if p.is_file()}
    for rel in sorted(want):
        src, dest = stage / rel, out / rel
        data = src.read_bytes()
        if dest.is_file() and dest.stat().st_size == len(data) and dest.read_bytes() == data:
            continue
        dest.parent.mkdir(parents=True, exist_ok=True)
        retry(dest.write_bytes, data)
    if out.exists():
        for p in sorted(out.rglob("*"), key=lambda p: -len(p.parts)):
            if p.is_file() and p.relative_to(out) not in want:
                retry(p.unlink)
            elif p.is_dir() and not any(p.iterdir()):
                try:
                    p.rmdir()
                except OSError:
                    pass                                   # an empty folder left behind is harmless


def build(out):
    """Build the site into out: first into a temporary folder outside OneDrive, then copied
    over what is there. -> a one-line summary."""
    stage = pathlib.Path(tempfile.mkdtemp(prefix="java-practice-site-"))
    try:
        summary = build_into(stage / "site")
        sync(stage / "site", out)
    finally:
        shutil.rmtree(stage, ignore_errors=True)
    size = sum(p.stat().st_size for p in out.rglob("*") if p.is_file())
    return summary.replace("{out}", str(out)).replace("{size}", f"{size / 1024 / 1024:.1f} MB")


def build_into(out):
    head = committed_files()
    files, practice, tree, problems = {}, {}, {"name": REPO.name, "dirs": {}, "files": []}, []
    for rel in sorted(r for r, data in head.items() if listed(r, len(data))):
        data = head[rel]
        text = data.decode("utf-8", "replace")
        hit = SECRETS.search(text)
        if hit:
            sys.exit(f"{rel}: looks like a secret ({hit.group(0)[:12]}...); not publishing. Remove it or exclude the file.")
        name = rel.rsplit("/", 1)[-1]
        entry = {"text": text, "hash": codeview.digest(data)}
        lang = langsrc.lang_of(name)
        f = {"name": name, "path": rel}
        if lang:
            problems.append(rel)
            if "MUST-KNOW" in data[:1500].decode("utf-8", "replace"):      # codeview.is_must
                f["must"] = True
            try:
                skeleton, hidden = langsrc.practice_skeleton(text, lang)
                practice[rel] = {"text": skeleton, "hidden": hidden, "hints": langsrc.practice_hints(text, lang)}
            except Exception as e:                       # noqa: BLE001 - the page shows it, as the server would
                practice[rel] = {"error": f"Practice could not hide this file's solution: {e}"}
            if lang == "java":
                entry["run"] = run_info(text, name)
        files[rel] = entry
        node = tree
        for part in rel.split("/")[:-1]:
            node = node["dirs"].setdefault(part, {"name": part, "dirs": {}, "files": []})
        node["files"].append(f)

    # codeview.build_tree's order: files by lower-case name, then folders by lower-case name
    order = []

    def finish(n, prefix):
        n["files"].sort(key=lambda f: f["name"].lower())
        order.extend(f["path"] for f in n["files"])
        n["dirs"] = [finish(d, prefix + d["name"] + "/") for _, d in sorted(n["dirs"].items(), key=lambda kv: kv[0].lower())]
        return n
    finish(tree, "")

    if out.exists():
        shutil.rmtree(out)
    (out / "data").mkdir(parents=True)
    dump = lambda name, obj: (out / "data" / name).write_text(json.dumps(obj, ensure_ascii=False, separators=(",", ":")),
                                                               encoding="utf-8", newline="\n")
    dump("tree.json", tree)
    dump("files.json", {"order": order, "files": files})
    dump("practice.json", practice)

    # the libraries, from Code Viewer's offline copy (it downloads them, sha512-checked, on start)
    urls = {}
    for lib, key in ((offline.MONACO, "__CV_MONACO__"), (offline.MARKED, "__CV_MARKED__"), (offline.MERMAID, "__CV_MERMAID__")):
        if not offline.ready(lib):
            sys.exit(f"No offline copy of {lib.npm} {lib.version} in {offline.cache_dir()}: start Code Viewer once with internet.")
        src = offline.lib_dir(lib) / lib.member.rstrip("/")
        dest = out / "vendor" / f"{lib.npm}@{lib.version}" / lib.member.rstrip("/")
        if src.is_dir():
            shutil.copytree(src, dest)
        else:
            dest.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(src, dest)
        urls[key] = f"vendor/{lib.npm}@{lib.version}/{lib.member.rstrip('/')}"
    (out / "vendor" / "README.txt").write_text(
        "Unmodified copies of open-source libraries, all under the MIT licence, taken from the npm registry\n"
        "(checked against its sha512) by java-interview-practice/tools/offline.py:\n\n"
        + "".join(f"  {lib.npm} {lib.version}  (npm package {lib.npm}, the {lib.member} part)\n" for lib in offline.LIBS),
        encoding="utf-8", newline="\n")

    # the page
    page = codeview.PAGE.read_text(encoding="utf-8")
    for old, new in PATCHES:
        n = page.count(old)
        if n != 1:
            sys.exit(f"codeview.html: expected one {old[:60]!r}, found {n}; update PATCHES in build_site.py")
        page = page.replace(old, new)
    built = time.strftime("%d %b %Y")
    version = "site-" + hashlib.sha1(page.encode() + CSS.encode() + (HERE / "site_static.js").read_bytes()
                                     + (HERE / "outcheck.js").read_bytes() + (HERE / "site_sw.js").read_bytes()
                                     + json.dumps(files, sort_keys=True).encode()).hexdigest()[:10]
    page = page.replace("__CV_VERSION__", version)
    for key, url in urls.items():
        page = page.replace(key, url)
    site = json.dumps({"version": version, "root": REPO.name, "built": built})
    boot = (f"<script>window.CV_SITE = {site};</script>\n<script src=\"outcheck.js\"></script>\n"
            f"<script src=\"static.js\"></script>\n")
    for old, new in (('<meta charset="utf-8">', '<meta charset="utf-8">\n' + NOINDEX),
                     ("</head>", CSS + HEAD_LINKS + "</head>"),
                     ("<!-- marked must load before", boot + "<!-- marked must load before"),
                     ("<title>Code Viewer</title>", "<title>Code Viewer · Java practice</title>")):
        if page.count(old) != 1:
            sys.exit(f"codeview.html: expected one {old!r}")
        page = page.replace(old, new)
    if "__CV_" in page:
        sys.exit("codeview.html: a __CV_ placeholder was left unfilled")
    (out / "index.html").write_text(page, encoding="utf-8", newline="\n")
    shutil.copyfile(HERE / "site_static.js", out / "static.js")
    shutil.copyfile(HERE / "outcheck.js", out / "outcheck.js")
    write_offline(out, version)

    return (f"{{out}}: {len(files)} files ({len(problems)} practice files, {sum(1 for p in practice.values() if 'error' in p)} "
            f"without a skeleton), {{size}}, version {version}")


def publish():
    """Build into the Learning Hub repo and push only that folder there: the website updates a
    minute later (GitHub Pages). Anything else changed in learning-hub is left alone.
    -> {ok, changed, commit, note} or {ok: False, error}."""
    if not (HUB / ".git").exists():
        return {"ok": False, "error": f"The learning-hub repo is not next to this one ({HUB}), so the website was not updated."}

    def git(*args):
        return subprocess.run(["git", *args], cwd=HUB, capture_output=True, text=True, encoding="utf-8", timeout=120)

    def last(r):
        lines = (r.stderr or r.stdout).strip().splitlines()
        return lines[-1] if lines else f"git exited with {r.returncode}"
    summary = build(HUB / SITE_DIR)
    git("add", "-A", "--", SITE_DIR)
    if not git("status", "--porcelain", "--", SITE_DIR).stdout.strip():
        return {"ok": True, "changed": False, "note": "The website already shows this.", "summary": summary}
    src = subprocess.run(["git", "rev-parse", "--short", "HEAD"], cwd=REPO, capture_output=True, text=True).stdout.strip()
    r = git("commit", "-q", "-m", f"Java practice: update from java-interview-practice {src}", "--", SITE_DIR)
    if r.returncode:
        return {"ok": False, "error": "Could not commit the website in learning-hub: " + last(r)}
    commit = git("rev-parse", "--short", "HEAD").stdout.strip()
    r = git("push")
    if r.returncode:
        return {"ok": False, "commit": commit, "error": f"The website was committed in learning-hub ({commit}) but not "
                f"pushed: {last(r)}. Run git push in learning-hub."}
    return {"ok": True, "changed": True, "commit": commit, "summary": summary,
            "note": f"The website updates in about a minute (learning-hub {commit})."}


if __name__ == "__main__":
    args = sys.argv[1:]
    if args and args[0] == "--publish":
        try:
            res = publish()
        except (SystemExit, Exception) as e:              # noqa: BLE001 - the caller gets the reason, not a traceback
            res = {"ok": False, "error": f"The website build stopped: {e}"}
        print(json.dumps(res) if "--json" in args else (res.get("note") or res.get("error")))
        sys.exit(0 if res["ok"] else 1)
    if len(args) != 1:
        sys.exit(__doc__)
    print(build(pathlib.Path(args[0]).resolve()))
