#!/usr/bin/env python3
"""
test_site - Code Viewer's website copy (tools/build_site.py) in headless Chrome: build it, serve
it on this PC, and do what a visitor does.

    python -I tools/test_site.py                 build into a temp folder and check it here
    python -I tools/test_site.py --live          check the public site instead (no build; the
                                                 "new build" group needs a local copy and is skipped)
    python -I tools/test_site.py --only js,offline
    python -I tools/test_site.py --keep          keep the temp folder (screenshots in shots/)

Groups: site (list, filters, search, Run's how-to, Practice, Done, notes, UI ticks, phone),
edit (folder link, Edit / Save / Reset, Try, Compare, Copy), js (every .js file against Node,
edge programs, the time limit, auto-done), offline (saved after one visit, installable, works
with the network cut) and update (a new build replaces the saved copy at once).

Needs Python Playwright (pip install playwright) and the installed Chrome (channel "chrome":
Playwright's own browser is not installed here), and Node for the JavaScript comparison. Exit 0
when every check passes. Written 6 Nov 2026 from the checks run while building the site.
"""
import argparse, functools, http.server, json, os, pathlib, re, shutil, socket, subprocess, sys, tempfile, threading
import urllib.request

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parent
sys.path.insert(0, str(HERE))
sys.stdout.reconfigure(encoding="utf-8")
LIVE = "https://ravivarmachaluvadi.github.io/learning-hub/tools/java-practice/"
R = "java-interview-practice"
EDITOR = "(() => monaco.editor.getEditors().find(e => e.getDomNode().closest('#editor')))()"

RESULTS = []


def check(group, name, ok, detail=""):
    RESULTS.append((group, name, bool(ok)))
    print(("PASS " if ok else "FAIL ") + f"[{group}] {name}" + (f"  [{detail}]" if detail else ""), flush=True)


class Site:
    """Where the site is (base URL) and its data (files.json, practice.json)."""

    def __init__(self, base, data_dir=None):
        self.base = base
        self.data_dir = data_dir
        self.files = self.json("files.json")
        self.order = self.files["order"]

    def json(self, name):
        if self.data_dir:
            return json.loads((self.data_dir / name).read_text(encoding="utf-8"))
        with urllib.request.urlopen(self.base + "data/" + name, timeout=60) as r:
            return json.load(r)

    def text(self, path):
        return self.files["files"][path]["text"]

    def url(self, path=""):
        return self.base + ("#/" + R + "/" + path if path else "")


def serve(folder):
    """A plain static server for folder on a free port -> (base URL of folder/site/, server)."""
    with socket.socket() as s:
        s.bind(("127.0.0.1", 0))
        port = s.getsockname()[1]
    http.server.SimpleHTTPRequestHandler.log_message = lambda *a: None
    srv = http.server.ThreadingHTTPServer(("127.0.0.1", port),
                                          functools.partial(http.server.SimpleHTTPRequestHandler, directory=str(folder)))
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    return f"http://127.0.0.1:{port}/site/", srv


def wait_list(pg):
    pg.wait_for_function("document.querySelector('#count') && /Done/.test(document.querySelector('#count').textContent)",
                         timeout=60000)


def open_file(pg, site, path, settle=1500):
    pg.goto(site.url(path))
    pg.wait_for_function(f"document.querySelector('#crumbs').textContent.includes({json.dumps(path.split('/')[-1][:12])})"
                         " || document.querySelector('#crumbs').textContent.includes(" + json.dumps(re.sub(r'^[A-D]\d\d_', '', path.split('/')[-1])[:12]) + ")",
                         timeout=30000)
    pg.wait_for_timeout(settle)


def page_errors(pg):
    errors = []
    pg.on("pageerror", lambda e: errors.append(str(e)))
    return errors


def no_errors(group, errors):
    real = [e for e in errors if "Canceled" not in e]       # Monaco's own, known and harmless
    check(group, "no page errors", not real, " | ".join(real)[:300])


# ---------------------------------------------------------------- site

def group_site(b, site, shots):
    G = "site"
    ctx = b.new_context(viewport={"width": 1400, "height": 860})
    pg = ctx.new_page()
    errors = page_errors(pg)
    f = site.files["files"]
    java_first = next(p for p in site.order if p.endswith(".java") and (f[p].get("run") or {}).get("first") and "/01-DSA/" in p)
    java_later = next((p for p in site.order if p.endswith(".java") and (f[p].get("run") or {}).get("main")
                       and not f[p]["run"]["first"]), None)
    md_mermaid = next(p for p in site.order if p.endswith(".md") and "```mermaid" in f[p]["text"])
    ui = next(p for p in site.order if p.endswith(".html") and 'type="test"' in f[p]["text"])

    pg.goto(site.url(), wait_until="load")
    wait_list(pg)
    check(G, "the file list loads with the progress footer", True, pg.inner_text("#count"))
    check(G, "the header says Website copy", pg.inner_text("#jdk") == "Website copy")
    pg.wait_for_selector("#stageMsg", state="hidden", timeout=60000)
    pg.wait_for_function("document.querySelector('#crumbs').textContent.includes('README')", timeout=30000)
    check(G, "README opens first", True)

    open_file(pg, site, java_first)
    check(G, "a Java file's output pane says to copy it to run it", "cannot run Java" in pg.inner_text("#output"))
    pg.click("#runBtn")
    pg.wait_for_function("document.querySelector('#status').textContent.includes('Runs on your PC')", timeout=10000)
    name = java_first.split("/")[-1]
    check(G, "Run on Java shows the java command and IntelliJ", f"java {name}" in pg.inner_text("#output")
          and "IntelliJ" in pg.inner_text("#output"), java_first)
    pg.screenshot(path=str(shots / "site-java-run.png"))
    if java_later:
        open_file(pg, site, java_later)
        pg.click("#runBtn")
        pg.wait_for_function("document.querySelector('#status').textContent.includes('Runs on your PC')", timeout=10000)
        check(G, "main() not in the first class: javac + java -cp", "javac -d out" in pg.inner_text("#output")
              and f[java_later]["run"]["main"] in pg.inner_text("#output"), java_later)
        open_file(pg, site, java_first)

    pg.click("#practiceBtn")
    pg.wait_for_timeout(1500)
    check(G, "Practice shows Hint, Compare and the timer",
          all(pg.evaluate("['hintBtn','compareBtn','timerBtn'].map(id => !document.getElementById(id).hidden)")))
    skel = site.json("practice.json")[java_first]["text"]
    check(G, "the Practice editor holds the skeleton", pg.evaluate(EDITOR + ".getModel().getValue()").strip() == skel.strip())
    pg.click("#hintBtn")
    pg.wait_for_timeout(500)
    check(G, "the Hint card opens", pg.is_visible("#hintCard"))
    pg.click("#hintClose")
    pg.click("#practiceBtn")
    pg.wait_for_timeout(800)

    pg.click("#progBtn")
    pg.wait_for_timeout(600)
    prog = pg.evaluate("fetch('/api/progress').then(r => r.json())")
    check(G, "Mark done is kept in this browser", (prog.get(java_first) or {}).get("s") == "done")
    check(G, "the footer counts it", "Done 1/" in pg.inner_text("#count"), pg.inner_text("#count"))

    pg.select_option("#showSel", "C")
    pg.wait_for_timeout(500)
    names = [n for n in pg.eval_on_selector_all("#tree .row.file", "rs => rs.map(r => r.dataset.path.split('/').pop())") if n and n != "scratch"]
    check(G, "Show: C medium lists only C files", names and all(n.startswith("C") for n in names), f"{len(names)} files")
    pg.click("#filter")
    pg.keyboard.type("tree")
    pg.wait_for_timeout(400)
    hits = pg.eval_on_selector_all("#results .row.file", "rs => rs.map(r => r.dataset.path.split('/').pop())")
    check(G, "the name search keeps to the Show choice", hits and all(n.startswith("C") for n in hits), f"{len(hits)} hits")
    pg.fill("#filter", "")
    pg.keyboard.press("Escape")
    pg.select_option("#showSel", "must")
    pg.wait_for_timeout(400)
    check(G, "Show: must-know lists the starred files", pg.eval_on_selector_all("#tree .row.file .star", "s => s.length") > 50)
    pg.select_option("#showSel", "all")
    pg.click("#filter")
    pg.keyboard.type("two sum")
    pg.wait_for_timeout(400)
    check(G, "the name search finds 'two sum'", any("TwoSum" in h for h in pg.eval_on_selector_all("#results .row.file", "rs => rs.map(r => r.dataset.path)")))
    pg.fill("#filter", "")
    pg.keyboard.press("Escape")

    pg.click(".tabs button[data-tab=search]")
    pg.fill("#sq", "PriorityQueue")
    pg.wait_for_function("/match/.test(document.querySelector('#sres').textContent)", timeout=20000)
    check(G, "Search in code finds PriorityQueue", "PriorityQueue" in pg.inner_text("#sres"), pg.inner_text("#sres").split("\n")[0][:60])
    pg.click(".tabs button[data-tab=files]")

    pg.goto(site.url(md_mermaid))
    pg.wait_for_selector("#md .mmd svg", timeout=30000)
    check(G, "a note renders and draws its diagrams", True, md_mermaid)

    open_file(pg, site, ui, 2500)
    pg.click("#runBtn")
    pg.wait_for_function("!/Running/.test(document.querySelector('#status').textContent) && document.querySelector('#status').textContent", timeout=20000)
    pg.wait_for_timeout(500)
    check(G, "a UI page's tests run here, with ticks", pg.inner_text("#status").startswith("✓")
          and "match expected" in (pg.inner_text("#verdict") if pg.is_visible("#verdict") else ""), pg.inner_text("#status"))
    no_errors(G, errors)
    ctx.close()

    # a phone
    ctx = b.new_context(viewport={"width": 390, "height": 844}, is_mobile=True, has_touch=True)
    ph = ctx.new_page()
    ph.goto(site.url())
    ph.wait_for_selector("#stageMsg", state="hidden", timeout=60000)
    ph.wait_for_timeout(1000)
    if ph.evaluate("document.body.classList.contains('side-collapsed')"):
        ph.click("#sideBtn")
        ph.wait_for_timeout(400)
    w = ph.evaluate("[innerWidth, document.querySelector('aside.side').getBoundingClientRect().width]")
    check(G, "phone: phone width, the list fills it", w[0] == 390 and abs(w[1] - 390) < 2, str(w))
    for d in ("AAScratches/01-DSA", "AAScratches/01-DSA/00-Patterns", "AAScratches/01-DSA/00-Patterns/01-Arrays-Prefix-Sums"):
        if not ph.evaluate(f"document.querySelector('#tree .row.dir[data-dir=\"{d}\"]').classList.contains('open')"):
            ph.tap(f'#tree .row.dir[data-dir="{d}"]')
            ph.wait_for_timeout(250)
    first = next(p for p in site.order if p.startswith("AAScratches/01-DSA/00-Patterns/01-Arrays-Prefix-Sums/"))
    ph.tap(f'#tree .row.file[data-path="{first}"]')
    ph.wait_for_timeout(1500)
    ew = ph.evaluate("[document.body.classList.contains('side-collapsed'), document.querySelector('#editor').getBoundingClientRect().width, innerWidth]")
    check(G, "phone: opening a file hides the list", ew[0] and ew[1] > 300 and ew[2] == 390, str(ew))
    boxes = ph.evaluate("['#runBtn', '#copyBtn'].map(s => { const r = document.querySelector(s).getBoundingClientRect(); return [r.left, r.right, r.width]; })")
    check(G, "phone: Run and Copy are on screen", all(x[2] > 0 and x[0] >= 0 and x[1] <= 390 for x in boxes), str(boxes))
    ph.click("#tryBtn")
    ph.wait_for_timeout(500)
    check(G, "Try's message does not promise a run", "Copy takes it to your PC" in ph.inner_text("#toast"))
    ph.screenshot(path=str(shots / "site-phone.png"))
    ctx.close()


# ---------------------------------------------------------------- edit

def group_edit(b, site, shots):
    G = "edit"
    ctx = b.new_context(viewport={"width": 1400, "height": 860})
    origin = re.match(r"https?://[^/]+", site.base).group(0)
    ctx.grant_permissions(["clipboard-read", "clipboard-write"], origin=origin)
    pg = ctx.new_page()
    errors = page_errors(pg)
    F = next(p for p in site.order if p.endswith("P001_PrefixSumRangeQuery.java"))
    pg.goto(site.url("AAScratches/"))
    pg.wait_for_selector("#stageMsg", state="hidden", timeout=60000)
    pg.wait_for_timeout(2500)
    check(G, "a folder link shows the folder", "01-DSA" in (pg.inner_text("#md") if pg.is_visible("#md") else ""))

    open_file(pg, site, F, 2500)
    pg.click("#editBtn")
    pg.wait_for_timeout(500)
    pg.evaluate(EDITOR + ".focus()")
    pg.keyboard.press("Control+End")
    pg.keyboard.type("\n// my edit")
    pg.wait_for_timeout(400)
    pg.keyboard.press("Control+s")
    pg.wait_for_timeout(500)
    check(G, "Save says it needs the laptop", "laptop" in pg.inner_text("#toast"))
    pg.reload()
    pg.wait_for_selector("#stageMsg", state="hidden", timeout=60000)
    pg.wait_for_timeout(2500)
    check(G, "the draft survives a reload", "// my edit" in pg.evaluate(EDITOR + ".getModel().getValue()"))
    if pg.is_visible("#resetBtn"):
        pg.click("#resetBtn")
        pg.wait_for_timeout(500)
    check(G, "Reset puts the file back", "// my edit" not in pg.evaluate(EDITOR + ".getModel().getValue()"))

    pg.click("#tryBtn")
    pg.wait_for_timeout(1000)
    check(G, "Try opens a throwaway copy", "Discard" in pg.inner_text("#tryBtn"))
    pg.evaluate(EDITOR + ".focus()")
    pg.keyboard.press("Control+End")
    pg.keyboard.type("\n// try edit")
    pg.click("#copyBtn")
    pg.wait_for_timeout(400)
    check(G, "Copy copies the changed code", "// try edit" in pg.evaluate("navigator.clipboard.readText()"))
    pg.click("#tryBtn")
    pg.wait_for_timeout(600)

    pg.click("#practiceBtn")
    pg.wait_for_timeout(1200)
    pg.evaluate(EDITOR + ".focus()")
    pg.keyboard.press("Control+End")
    pg.keyboard.type("\n// my attempt")
    pg.click("#compareBtn")
    pg.wait_for_timeout(1200)
    check(G, "Compare shows the solution next to the attempt", pg.is_visible("#diffWrap"))
    pg.click("#compareBtn")
    pg.wait_for_timeout(500)
    pg.reload()
    pg.wait_for_selector("#stageMsg", state="hidden", timeout=60000)
    pg.wait_for_timeout(2500)
    check(G, "a practice attempt survives a reload", "// my attempt" in pg.evaluate(EDITOR + ".getModel().getValue()"))
    no_errors(G, errors)
    ctx.close()


# ---------------------------------------------------------------- js

EDGE = {
    "objects": "console.log([1, 'a', null, undefined, {x: 1, 'y-z': [2, 3]}], new Map([['k', 1]]), new Set([1, 2]), -0, 10n, true);"
               "console.log({}); console.log([]); console.log([ , 1]); console.log(function f() {}, () => 1, class A {});"
               "console.log({a: {b: {c: {d: 1}}}}); console.log('it\\'s', ['it\\'s']); class P { constructor() { this.q = 1; } } console.log(new P());",
    "timers": "let n = 0; const id = setInterval(() => { console.log('tick', ++n); if (n === 3) clearInterval(id); }, 5);"
              "setTimeout(() => console.log('later'), 250); Promise.resolve().then(() => console.log('micro'));",
    "throws": "function main() {\n  console.log('before');\n  throw new TypeError('boom');\n}\nmain();",
    "rejects": "async function go() { await null; throw new Error('async boom'); }\ngo();\nconsole.log('sync');",
    "string thrown": "throw 'just a string';",
}


def node(code):
    with tempfile.TemporaryDirectory() as d:
        p = pathlib.Path(d) / "edge.js"
        p.write_text(code, encoding="utf-8")
        r = subprocess.run(["node", str(p)], capture_output=True, text=True, encoding="utf-8", cwd=d)
        return r.returncode, r.stdout


def group_js(b, site, shots):
    G = "js"
    import codeview
    if not shutil.which("node"):
        check(G, "Node is installed (needed to compare)", False)
        return
    rid = codeview.STATE.roots()[0]["id"]
    pg = b.new_context().new_page()
    errors = page_errors(pg)
    pg.goto(site.base, wait_until="load")
    wait_list(pg)

    def run(code, path, timeout=10):
        return pg.evaluate("([code, path, timeout]) => fetch('/api/run', {method: 'POST', headers: {'Content-Type': "
                           "'application/json'}, body: JSON.stringify({code, path, lang: 'js', timeout, stdin: ''})}).then(r => r.json())",
                           [code, path, timeout])
    js_files = [p for p in site.order if p.endswith(".js")]
    check(G, "there are JavaScript files to compare", len(js_files) >= 10, f"{len(js_files)}")
    for rel in js_files:
        code = site.text(rel)
        want = codeview.run_code(code, rid, rel, "", 15, None)["output"].replace("\r\n", "\n")
        got = run(code, rel)
        check(G, f"{rel.split('/')[-1]} prints what Node prints", got["output"] == want and got["ok"],
              "" if got["output"] == want else f"browser {got['output'][:120]!r} | node {want[:120]!r}")
        check(G, f"{rel.split('/')[-1]} ticks as outcheck.py does", got["verdicts"] == codeview.check_lines(got["output"])[0])
    for name, code in EDGE.items():
        rc, nout = node(code)
        got = run(code, f"AAScratches/edge/{name.replace(' ', '_')}.js")
        if rc == 0:
            check(G, f"edge '{name}': same as Node", got["output"] == nout and got["exitCode"] == 0, f"{got['output']!r} vs {nout!r}")
        else:
            check(G, f"edge '{name}': fails like Node", got["exitCode"] == 1 and got["output"].startswith(nout), got["output"][:120])
    got = run("const fs = require('fs');\nconsole.log(fs);", "AAScratches/edge/node_only.js")
    check(G, "require() says it needs Node", got["exitCode"] == 1 and "part of Node.js" in got["output"])
    got = run("while (true) {}", "AAScratches/edge/loop.js", 2)
    check(G, "an endless loop stops at the time limit", got["timedOut"] and not got["ok"])
    check(G, "the page still responds after it", pg.evaluate("1 + 1") == 2)

    rel = js_files[0]
    open_file(pg, site, rel, 2500)
    pg.click("#runBtn")
    pg.wait_for_function("/match expected|differ/.test(document.querySelector('#verdict').textContent)", timeout=20000)
    check(G, "Run on a .js file shows ticks", "match expected" in pg.inner_text("#verdict"), pg.inner_text("#verdict"))
    pg.click("#practiceBtn")
    pg.wait_for_timeout(1500)
    pg.evaluate("t => " + EDITOR + ".getModel().setValue(t)", site.text(rel))
    pg.click("#runBtn")
    pg.wait_for_function("/match expected|differ/.test(document.querySelector('#verdict').textContent)", timeout=20000)
    pg.wait_for_timeout(800)
    prog = pg.evaluate("fetch('/api/progress').then(r => r.json())")
    check(G, "a solved .js practice marks itself done", (prog.get(rel) or {}).get("s") == "done", pg.inner_text("#toast")[:80])
    no_errors(G, errors)
    pg.context.close()


# ---------------------------------------------------------------- offline

def group_offline(b_type, site, shots, work):
    G = "offline"
    f = site.files["files"]
    java = next(p for p in site.order if p.endswith(".java") and "/01-DSA/" in p)
    js = next(p for p in site.order if p.endswith(".js"))
    md = next(p for p in site.order if p.endswith(".md") and "/01-DSA/" in p and "```mermaid" not in f[p]["text"])
    profile = pathlib.Path(tempfile.mkdtemp(prefix="profile_", dir=work))
    ctx = b_type.launch_persistent_context(str(profile), channel="chrome", headless=True, viewport={"width": 1300, "height": 820})
    pg = ctx.pages[0] if ctx.pages else ctx.new_page()
    errors = page_errors(pg)
    pg.goto(site.base, wait_until="load")
    wait_list(pg)
    pg.wait_for_function("navigator.serviceWorker.controller !== null", timeout=120000)
    saved = pg.evaluate("async () => { const o = {}; for (const k of await caches.keys()) o[k] = (await (await caches.open(k)).keys()).length; return o; }")
    check(G, "the service worker took over and saved the site",
          any(k.startswith("java-practice-site-") for k in saved) and saved.get("java-practice-vendor", 0) > 10, json.dumps(saved))
    man = pg.evaluate("fetch('manifest.webmanifest').then(r => r.json())")
    check(G, "the manifest is there", man.get("short_name") == "Java Practice" and len(man.get("icons", [])) == 2)
    inst = ctx.new_cdp_session(pg).send("Page.getInstallabilityErrors")
    check(G, "Chrome finds nothing stopping an install", not inst.get("installabilityErrors"), json.dumps(inst)[:200])
    for p in (java, js):
        pg.goto(site.url(p))
        pg.wait_for_timeout(1500)
    ctx.set_offline(True)
    pg.reload(wait_until="load")
    wait_list(pg)
    check(G, "offline: the file list loads", True)
    pg.wait_for_selector("#stageMsg", state="hidden", timeout=60000)
    open_file(pg, site, java)
    check(G, "offline: a Java file opens", pg.evaluate(EDITOR + ".getModel().getValue()") == f[java]["text"])
    check(G, "offline: Java is coloured", pg.evaluate("document.querySelectorAll('#editor .view-line span span[class^=\"mtk\"]').length") > 20)
    pg.click("#practiceBtn")
    pg.wait_for_timeout(1200)
    check(G, "offline: Practice works", pg.is_visible("#hintBtn"))
    pg.click("#practiceBtn")
    open_file(pg, site, js)
    pg.click("#runBtn")
    pg.wait_for_function("/match expected|differ/.test(document.querySelector('#verdict').textContent)", timeout=20000)
    check(G, "offline: JavaScript runs, with ticks", "match expected" in pg.inner_text("#verdict"))
    pg.goto(site.url(md))
    pg.wait_for_timeout(1500)
    check(G, "offline: a note renders", pg.is_visible("#md") and len(pg.inner_text("#md")) > 200)
    pg.click(".tabs button[data-tab=search]")
    pg.fill("#sq", "HashMap")
    pg.wait_for_timeout(1500)
    check(G, "offline: Search in code", "match" in pg.inner_text("#sres"))
    no_errors(G, errors)
    pg.screenshot(path=str(shots / "offline.png"))
    ctx.close()


# ---------------------------------------------------------------- sync (a fake Google Drive)

class FakeDrive:
    """Google Drive's REST calls that drive-sync.js makes, answered in memory, so two browsers
    (two "devices") can sync through it without a Google account."""

    def __init__(self):
        self.items, self.n = {}, 0

    def stamp(self):
        self.n += 1
        return f"2026-11-07T00:00:{self.n // 1000:02d}.{self.n % 1000:03d}Z"

    def content(self, name="java-practice.json"):
        f = next((x for x in self.items.values() if x["name"] == name and not x.get("trashed")), None)
        return json.loads(f["content"]) if f and f.get("content") else None

    def handle(self, route, request):
        from urllib.parse import urlparse, parse_qs
        u = urlparse(request.url)
        q, path, m = parse_qs(u.query), u.path, request.method
        cors = {"Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "Authorization, Content-Type",
                "Access-Control-Allow-Methods": "GET, POST, PATCH"}
        if m == "OPTIONS":
            return route.fulfill(status=204, headers=cors)
        ok = lambda obj: route.fulfill(status=200, content_type="application/json", headers=cors, body=json.dumps(obj))
        if request.headers.get("authorization") != "Bearer fake-token":
            return route.fulfill(status=401, headers=cors, body="no")
        fid = re.match(r".*/files/([^/?]+)$", path)
        if path == "/drive/v3/files" and m == "GET":
            query = q.get("q", [""])[0]
            name = re.search(r"name='([^']*)'", query).group(1)
            parent = re.search(r"'([^']+)' in parents", query)
            folder = "mimeType='application/vnd.google-apps.folder'" in query
            hits = [{"id": i, "name": x["name"], "modifiedTime": x["modified"]} for i, x in self.items.items()
                    if x["name"] == name and not x.get("trashed") and x["folder"] == folder
                    and (not parent or parent.group(1) in x["parents"])]
            return ok({"files": hits})
        if path == "/drive/v3/files" and m == "POST":
            body = json.loads(request.post_data)
            i = f"folder{len(self.items)}"
            self.items[i] = {"name": body["name"], "folder": True, "parents": body.get("parents") or [], "modified": self.stamp()}
            return ok({"id": i})
        if path == "/upload/drive/v3/files" and m == "POST":
            boundary = request.headers["content-type"].split("boundary=", 1)[1]
            parts = [p for p in request.post_data.split("--" + boundary) if p.strip() and p.strip() != "--"]
            meta, data = (json.loads(p.split("\r\n\r\n", 1)[1].strip()) for p in parts[:2])
            i = f"file{len(self.items)}"
            self.items[i] = {"name": meta["name"], "folder": False, "parents": meta["parents"], "content": json.dumps(data),
                             "modified": self.stamp()}
            return ok({"id": i, "modifiedTime": self.items[i]["modified"]})
        if fid and fid.group(1) in self.items:
            x = self.items[fid.group(1)]
            if "/upload/" in path and m == "PATCH":
                x["content"], x["modified"] = request.post_data, self.stamp()
                return ok({"id": fid.group(1), "modifiedTime": x["modified"]})
            if m == "PATCH":
                x["trashed"] = True
                return ok({"id": fid.group(1)})
            if q.get("alt") == ["media"]:
                return route.fulfill(status=200, content_type="application/json", headers=cors, body=x["content"])
            return ok({"id": fid.group(1), "modifiedTime": x["modified"], "trashed": bool(x.get("trashed"))})
        return route.fulfill(status=404, headers=cors, body="not found")


TOKEN = "localStorage.setItem('drivesync.token', JSON.stringify({token: 'fake-token', exp: Date.now() + 3600e3, email: 'test@example.com'}));"


def group_sync(b, site, shots):
    G = "sync"
    drive = FakeDrive()
    f = site.files["files"]
    x = next(p for p in site.order if p.endswith(".java") and "/01-DSA/" in p)
    y = next(p for p in site.order if p.endswith(".java") and "/02-Java-Core/" in p)

    def device(token=True, init=""):
        ctx = b.new_context(viewport={"width": 1300, "height": 820})
        ctx.route(re.compile(r"https://www\.googleapis\.com/.*"), drive.handle)
        if token:
            ctx.add_init_script(TOKEN)
        if init:
            ctx.add_init_script(init)
        pg = ctx.new_page()
        pg.goto(site.url(), wait_until="load")
        wait_list(pg)
        return ctx, pg

    def settled(pg):
        pg.wait_for_function("window.cvSync && !window.cvSync.dirty && !window.cvSync.busy && window.cvSync.status.state === 'saved'",
                             timeout=30000)

    def mark(pg, path, status):
        pg.evaluate("([path, status]) => fetch('/api/progress', {method: 'POST', headers: {'Content-Type': 'application/json'},"
                    " body: JSON.stringify({root: 'java-interview-practice', path, status})}).then(r => r.json())", [path, status])

    has = lambda path, key="s": ((drive.content() or {}).get("items") or {}).get(path, {}).get(key)
    a_ctx, a = device()
    settled(a)
    check(G, "device A connects to Drive", a.evaluate("window.cvSync.status.text").startswith("Saved in Drive"), a.evaluate("window.cvSync.status.text"))
    mark(a, x, "done")
    settled(a)
    check(G, "a mark on device A is saved in Drive", has(x) == "done", json.dumps(drive.content())[:200])

    b_ctx, b2 = device()
    b2.wait_for_function("/Done 1\\//.test(document.querySelector('#count').textContent)", timeout=30000)
    check(G, "device B shows device A's mark", True, b2.inner_text("#count"))
    open_file(b2, site, x)
    check(G, "…on the file's own button too", "Done" in b2.inner_text("#progBtn") or "done" in b2.inner_text("#progBtn").lower(), b2.inner_text("#progBtn"))
    mark(b2, x, "revise")
    settled(b2)
    check(G, "device B's change is saved in Drive", has(x) == "revise")
    a.evaluate("window.cvSync.pull(true)")
    a.wait_for_function("/to revise <b>1<\\/b>/.test(document.querySelector('#count').innerHTML)", timeout=30000)
    check(G, "device A picks up device B's change", True, a.inner_text("#count"))
    mark(a, x, "none")
    settled(a)
    entry = ((drive.content() or {}).get("items") or {}).get(x, {})
    check(G, "clearing a mark syncs as cleared (as the laptop keeps it: a date, no mark)", "s" not in entry and "t" in entry, json.dumps(entry))
    b2.evaluate("window.cvSync.pull(true)")
    b2.wait_for_function("/Done 0\\//.test(document.querySelector('#count').textContent) && !/to revise <b>1/.test(document.querySelector('#count').innerHTML)", timeout=30000)
    check(G, "device B drops the cleared mark", True, b2.inner_text("#count"))
    a.evaluate("p => fetch('/api/progress', {method: 'POST', headers: {'Content-Type': 'application/json'},"
               " body: JSON.stringify({root: 'java-interview-practice', path: p, restore: {}})}).then(r => r.json())", x)
    settled(a)
    entry = ((drive.content() or {}).get("items") or {}).get(x, {})
    check(G, "an emptied entry (Undo of a first mark) is a tombstone, so no device brings it back",
          entry.get("deleted") is True, json.dumps(entry))

    seed = ("if (!localStorage.getItem('cvsite:progress')) localStorage.setItem('cvsite:progress', JSON.stringify("
            + json.dumps({y: {"s": "done", "t": "2026-11-01"}}) + "));")
    c_ctx, c = device(init=seed)
    settled(c)
    check(G, "marks made before sync existed are uploaded once", has(y) == "done", json.dumps(drive.content())[:200])

    d_ctx, d = device(token=False)
    check(G, "signed out: the Continue with Google button shows", "Continue with Google" in d.inner_text("#cvSyncWrap"), d.inner_text("#cvSyncWrap"))
    mark(d, x, "done")
    d.reload()
    wait_list(d)
    check(G, "signed out: marks still work in this browser", "Done 1/" in d.inner_text("#count"), d.inner_text("#count"))
    d.screenshot(path=str(shots / "sync-signed-out.png"))
    a.screenshot(path=str(shots / "sync-signed-in.png"))
    for ctx in (a_ctx, b_ctx, c_ctx, d_ctx):
        ctx.close()


# ---------------------------------------------------------------- update (local only)

def group_update(b_type, site, shots, work):
    G = "update"
    root = site.data_dir.parent
    files_path, sw_path = root / "data" / "files.json", root / "sw.js"
    orig_files, orig_sw = files_path.read_bytes(), sw_path.read_bytes()
    profile = pathlib.Path(tempfile.mkdtemp(prefix="profile_", dir=work))
    try:
        ctx = b_type.launch_persistent_context(str(profile), channel="chrome", headless=True)
        pg = ctx.pages[0] if ctx.pages else ctx.new_page()
        pg.goto(site.base, wait_until="load")
        pg.wait_for_function("navigator.serviceWorker.controller !== null", timeout=120000)
        old = pg.evaluate("caches.keys()")
        data = json.loads(orig_files)                      # "publish" a new build: README and sw.js change
        data["files"]["README.md"]["text"] = "# NEW BUILD MARKER\n" + data["files"]["README.md"]["text"]
        files_path.write_text(json.dumps(data, ensure_ascii=False), encoding="utf-8")
        sw_path.write_text(re.sub(r'const VERSION = "([^"]+)"', r'const VERSION = "\1-next"', orig_sw.decode()), encoding="utf-8")
        pg.reload(wait_until="load")
        wait_list(pg)
        pg.goto(site.url("README.md"))
        pg.wait_for_timeout(2000)
        check(G, "online: a new build's content shows at once", "NEW BUILD MARKER" in pg.inner_text("#md"))
        pg.wait_for_function("caches.keys().then(k => k.some(x => x.endsWith('-next')))", timeout=120000)
        pg.wait_for_timeout(2000)
        new = pg.evaluate("caches.keys()")
        olds = {k for k in old if k.startswith("java-practice-site")}
        check(G, "the new saved copy replaced the old one", not (olds & set(new)) and any(k.endswith("-next") for k in new), f"{old} -> {new}")
        ctx.set_offline(True)
        pg.reload(wait_until="load")
        wait_list(pg)
        pg.goto(site.url("README.md"))
        pg.wait_for_timeout(2000)
        check(G, "offline afterwards: the saved copy is the new one", "NEW BUILD MARKER" in pg.inner_text("#md"))
        ctx.close()
    finally:
        files_path.write_bytes(orig_files)
        sw_path.write_bytes(orig_sw)


GROUPS = ("site", "edit", "js", "sync", "offline", "update")


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--live", action="store_true", help="check the public site")
    ap.add_argument("--only", default=",".join(GROUPS), help="comma-separated groups: " + ", ".join(GROUPS))
    ap.add_argument("--keep", action="store_true", help="keep the temp folder")
    args = ap.parse_args()
    only = [g.strip() for g in args.only.split(",") if g.strip()]
    from playwright.sync_api import sync_playwright
    work = pathlib.Path(tempfile.mkdtemp(prefix="codeview_site_test_"))
    shots = work / "shots"
    shots.mkdir()
    srv = None
    try:
        if args.live:
            site = Site(LIVE)
            if "update" in only:
                only.remove("update")
                print("(the 'update' group changes the site's files, so it runs only on a local build)")
        else:
            import build_site
            print(build_site.build(work / "site"))
            base, srv = serve(work)
            site = Site(base, work / "site" / "data")
        with sync_playwright() as pw:
            b = pw.chromium.launch(channel="chrome", headless=True)
            for g in only:
                print(f"--- {g}")
                try:
                    if g in ("offline", "update"):
                        (group_offline if g == "offline" else group_update)(pw.chromium, site, shots, work)
                    else:
                        {"site": group_site, "edit": group_edit, "js": group_js, "sync": group_sync}[g](b, site, shots)
                except Exception as e:                 # noqa: BLE001 - one broken group must not hide the others
                    check(g, "the group ran to the end", False, f"{type(e).__name__}: {str(e)[:300]}")
            b.close()
    finally:
        if srv:
            srv.shutdown()
        if args.keep:
            print("kept:", work)
        else:
            shutil.rmtree(work, ignore_errors=True)
    bad = [r for r in RESULTS if not r[2]]
    print(f"\n{len(RESULTS) - len(bad)}/{len(RESULTS)} passed" + ("" if not bad else " - FAILED: " + "; ".join(f"[{g}] {n}" for g, n, _ in bad)))
    sys.exit(1 if bad else 0)


if __name__ == "__main__":
    main()
