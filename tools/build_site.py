#!/usr/bin/env python3
"""
build_site - Code Viewer as a plain website, for browsers that cannot reach the laptop: the
Learning Hub's public copy serves it at tools/java-practice/ (6 Nov 2026, Ravi: "easily accessed
from any device without blocking by any company policy").

    python -I tools/build_site.py OUT_DIR

OUT_DIR gets:
    index.html          codeview.html with its libraries pointed at vendor/, a few website patches
                        (below), and site_static.js loaded first
    static.js           tools/site_static.js: answers the page's /api/... requests in the browser
    data/tree.json      the file list (what /api/tree gives)
    data/files.json     each file's text and hash, and for Java files how to run them on a PC
    data/practice.json  each practice file's skeleton, hidden notes and hints (langsrc, as /api/practice)
    vendor/             Monaco, marked and Mermaid from Code Viewer's offline copy (tools/offline.py)

What works there: reading, Practice, Hint, Compare, Visual, Next, Done / Revise (kept in that
browser), the file filter and search in code, and UI pages (.html), which run in the browser.
What does not: running Java, Python or JavaScript (Run shows how to run it on a PC instead), the
ticks, Save, new files, Push and Java suggestions.

Only files git tracks are published, and of those only what Code Viewer itself lists
(SKIP_IN_REPO keeps tools/, _personal/ and 08-Reference/ out). Every page asks search engines not
to index it. The build stops if a file looks like it holds a secret.
"""
import hashlib, json, pathlib, re, shutil, subprocess, sys, time

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parent
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
     "out.innerHTML = c && isCode(c) && c.kind !== 'html' && window.CV_STATIC ? `<div class=\"hint\">This website cannot run "
     "${KIND_NAME[c.kind]}. Copy the code (the copy button above) to run it on your PC; ▶ Run shows how.</div>`\n"
     "      : c && isCode(c) ? `<div class=\"hint\">Press"),
    # the list's right-click menu is all laptop actions (open in Explorer, rename, delete...)
    ("function showRowMenu(e) {\n  if (e.shiftKey) return;",
     "function showRowMenu(e) {\n  if (e.shiftKey || window.CV_STATIC) return;"),
    # one folder only: no "Open another folder..."
    ("  sel.title = cur ? cur.path : '';", "  sel.title = cur ? cur.path : '';\n  sel.disabled = !!window.CV_STATIC;"),
    # say what this copy is at the top of the help
    ("<h3>Shortcuts</h3>",
     "<h3>Website copy</h3>\n  <p class=\"site-note\">This is Code Viewer's website copy. Reading, Practice, Hint, Compare, Visual, "
     "Next, Done / Revise, the file filter and Search in code work here, and UI pages (.html) run with their tests. "
     "Your progress, drafts and attempts are kept in this browser only. Java, Python and JavaScript cannot run here: "
     "▶ Run shows how to run the file on a PC (copy it into an IntelliJ scratch file, or <code>java File.java</code>). "
     "Save, new files, Push, the ticks and Java suggestions need Code Viewer on the laptop.</p>\n  <h3>Shortcuts</h3>"),
]
# laptop-only controls, hidden
CSS = """<style>
  #newBtn, #pushBtn, #autostartRow, #rootPanel, #browseBtn,
  #menu [data-act="reveal"], #menu [data-act="delete"], #menu [data-act="assistPractice"] { display: none !important; }
  #rootSel:disabled { opacity: 1; cursor: default; }
  .status.away { color: var(--muted); }
  .help .site-note { margin: 0 0 12px; max-width: 72ch; }
  /* a phone: the file list or the file, one at a time (static.js hides the list once a file opens) */
  @media (max-width: 760px) {
    body:not(.side-collapsed) { grid-template-columns: 100vw 0 0 !important; }
    body:not(.side-collapsed) .vsplit, body:not(.side-collapsed) main { display: none; }
    #jdk { display: none; }
    main .bar { overflow-x: auto; }
  }
</style>
"""


def git_files():
    out = subprocess.run(["git", "ls-files", "-z"], cwd=REPO, capture_output=True, check=True).stdout
    return [p for p in out.decode("utf-8").split("\0") if p]


def listed(rel):
    """True when Code Viewer's file list shows this repo path (codeview.walk's rules)."""
    parts = rel.split("/")
    skip = codeview.SKIP_ALWAYS | codeview.SKIP_IN_REPO
    if any(d in skip or d.startswith(".") for d in parts[:-1]):
        return False
    p = REPO / rel
    return (pathlib.PurePosixPath(rel).suffix.lower() in codeview.TEXT_EXT and p.is_file()
            and p.stat().st_size <= codeview.MAX_VIEW)


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


def build(out):
    rels = sorted(r for r in git_files() if listed(r))
    files, practice, tree, problems = {}, {}, {"name": REPO.name, "dirs": {}, "files": []}, []
    for rel in rels:
        data = (REPO / rel).read_bytes()
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
    version = "site-" + hashlib.sha1(page.encode() + CSS.encode() + (HERE / "site_static.js").read_bytes()).hexdigest()[:10]
    page = page.replace("__CV_VERSION__", version)
    for key, url in urls.items():
        page = page.replace(key, url)
    site = json.dumps({"version": version, "root": REPO.name, "built": built})
    boot = (f"<script>window.CV_SITE = {site};</script>\n<script src=\"static.js\"></script>\n")
    for old, new in (('<meta charset="utf-8">', '<meta charset="utf-8">\n' + NOINDEX),
                     ("</head>", CSS + "</head>"),
                     ("<!-- marked must load before", boot + "<!-- marked must load before"),
                     ("<title>Code Viewer</title>", "<title>Code Viewer · Java practice</title>")):
        if page.count(old) != 1:
            sys.exit(f"codeview.html: expected one {old!r}")
        page = page.replace(old, new)
    if "__CV_" in page:
        sys.exit("codeview.html: a __CV_ placeholder was left unfilled")
    (out / "index.html").write_text(page, encoding="utf-8", newline="\n")
    shutil.copyfile(HERE / "site_static.js", out / "static.js")

    size = sum(p.stat().st_size for p in out.rglob("*") if p.is_file())
    print(f"{out}: {len(files)} files ({len(problems)} practice files, {sum(1 for p in practice.values() if 'error' in p)} "
          f"without a skeleton), {size / 1024 / 1024:.1f} MB, version {version}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    build(pathlib.Path(sys.argv[1]).resolve())
