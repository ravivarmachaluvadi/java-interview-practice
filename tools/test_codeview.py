"""
Tests for codeview's server side: tray quit, the log, progress, practice hints and the
offline copy of the page's libraries.

    python tools/test_codeview.py

Everything runs against temporary files: the env vars below are set before codeview is
imported, so the real tools/codeview-state.json and codeview.log are never touched.
"""
import base64, hashlib, io, json, os, pathlib, re, shutil, sys, tarfile, tempfile, threading, unittest
import urllib.error, urllib.request
from collections import Counter

TMP = pathlib.Path(tempfile.mkdtemp(prefix="codeview_test_"))
os.environ["CODEVIEW_STATE"] = str(TMP / "state.json")
os.environ["CODEVIEW_LOG"] = str(TMP / "codeview.log")
os.environ["CODEVIEW_VENDOR"] = str(TMP / "vendor")
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

import check_headers  # noqa: E402
import codeview  # noqa: E402
import javasrc  # noqa: E402
import offline  # noqa: E402

SAMPLE = """/*
 * =====================================================================
 *  Two Sum                                LeetCode 1 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Find two numbers that add up to target.
 *
 * APPROACH  (hash map)
 *   1. Walk once.
 *      indented detail
 *
 * KEY INSIGHT
 *   Look up the complement.
 *
 * COMPLEXITY
 *   Time O(n).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sorted input?
 */
class TwoSum {
    static int[] solve(int[] nums, int target) {
        return new int[]{0, 1};
    }

    public static void main(String[] args) {
        System.out.println(java.util.Arrays.toString(solve(new int[]{2, 7}, 9)) + "   expected [0, 1]");
    }
}
"""


class FakeIcon:
    stopped = False

    def stop(self):
        self.stopped = True


class FakeServer:
    shut = False

    def __init__(self, steps=None):
        self.steps = steps if steps is not None else []

    def shutdown(self):
        self.shut = True
        self.steps.append("shutdown")

    def server_close(self):
        self.steps.append("close")


def log_text():
    for h in codeview.log.handlers:
        h.flush()
    p = pathlib.Path(os.environ["CODEVIEW_LOG"])
    return p.read_text(encoding="utf-8") if p.exists() else ""


def menu_item(menu, text):
    return next(i for i in menu.items if i.text.startswith(text))


class QuitTest(unittest.TestCase):
    """3 Oct: one stray click on the tray's Quit stopped the page. 5 Oct: the Yes/No box
    that guarded it opened out of sight, and the tray stayed frozen until it was answered."""

    def setUp(self):
        codeview.setup_log()
        codeview._stop_logged = False      # one stop per process; each test is its own run
        import pystray
        self.menu = codeview.tray_menu(pystray, FakeServer(), "http://127.0.0.1:1/")

    def test_quit_is_a_submenu_so_one_click_cannot_stop_it(self):
        quit_item = menu_item(self.menu, "Quit Code Viewer")
        self.assertIsNotNone(quit_item.submenu)
        self.assertEqual(len(quit_item.submenu.items), 1)

    def test_second_click_stops_and_logs_why(self):
        icon, server = FakeIcon(), FakeServer()
        import pystray
        menu = codeview.tray_menu(pystray, server, "http://127.0.0.1:1/")
        menu_item(menu, "Quit Code Viewer").submenu.items[0](icon)
        self.assertTrue(icon.stopped)
        self.assertTrue(server.shut)
        self.assertIn("stopped: Quit from the tray menu", log_text())

    def test_no_popup_box_is_left(self):
        self.assertFalse(hasattr(codeview, "confirm_quit"))


class RestartTest(unittest.TestCase):
    """5 Oct: a fix in the server code needed Quit and a start by hand to take effect."""

    def setUp(self):
        codeview.setup_log()
        codeview._stop_logged = False

    def test_frees_the_port_before_starting_the_new_copy(self):
        steps, icon = [], FakeIcon()
        server = FakeServer(steps)
        codeview.restart_from_tray(icon, server, argv=["codeview.py", "--tray", "--port", "9000"],
                                   spawn=lambda args: steps.append(("spawn", args)))
        self.assertEqual([s if isinstance(s, str) else s[0] for s in steps], ["shutdown", "close", "spawn"])
        self.assertTrue(icon.stopped)
        args = steps[2][1]
        self.assertEqual(args[0], sys.executable)
        self.assertTrue(args[1].endswith("codeview.py"))
        self.assertEqual(args[2:], ["--tray", "--port", "9000", "--no-open"])   # the tab is already open
        self.assertIn("stopped: Restart from the tray menu", log_text())

    def test_no_open_is_not_added_twice(self):
        steps = []
        codeview.restart_from_tray(FakeIcon(), FakeServer(steps), argv=["codeview.py", "--tray", "--no-open"],
                                   spawn=lambda args: steps.append(args))
        self.assertEqual(steps[-1].count("--no-open"), 1)

    def test_tray_menu_offers_it(self):
        import pystray
        menu = codeview.tray_menu(pystray, FakeServer(), "http://127.0.0.1:1/")
        self.assertIsNone(menu_item(menu, "Restart Code Viewer").submenu)


class TemplateTest(unittest.TestCase):
    """5 Oct: a blank New-file template (DecodeWays.java) passed check_headers.py."""

    def test_header_check_flags_an_unfilled_new_file(self):
        found = check_headers.placeholders(codeview.java_template("C16_MyProblem.java"))
        self.assertIn("LeetCode ? | ?", found)
        self.assertIn("O(?)", found)
        self.assertIn("...", found)

    def test_filled_headers_have_none(self):
        self.assertEqual(check_headers.placeholders(SAMPLE), [])


class DeleteTest(unittest.TestCase):
    """5 Oct: a stray New file (+) left a blank DecodeWays.java with no way to remove it
    from the page. Delete moves one file to the Recycle Bin; nothing else."""

    def setUp(self):
        self.base = TMP / "delete_root"
        (self.base / "sub").mkdir(parents=True, exist_ok=True)
        (self.base / "sub" / "A01_X.java").write_text("class X {}", encoding="utf-8")
        (self.base / "notes.bin").write_bytes(b"\0")
        self.rid = codeview.STATE.add_root(str(self.base))["id"]
        self.trashed = []

    def tearDown(self):
        codeview.STATE.remove_root(self.rid)
        shutil.rmtree(self.base, ignore_errors=True)

    def trash(self, p):
        self.trashed.append(p)
        p.unlink()

    def test_file_goes_to_the_bin(self):
        res = codeview.delete_file(self.rid, "sub/A01_X.java", trash=self.trash)
        self.assertEqual(res, {"ok": True})
        self.assertEqual(self.trashed, [(self.base / "sub" / "A01_X.java").resolve()])
        self.assertFalse((self.base / "sub" / "A01_X.java").exists())

    def test_refuses_anything_but_one_viewable_file(self):
        outside = TMP / "outside.java"
        outside.write_text("class O {}", encoding="utf-8")
        for rel in ("sub", "", ".", "../outside.java", "sub/missing.java", "notes.bin"):
            with self.subTest(rel=rel), self.assertRaises(ValueError):
                codeview.delete_file(self.rid, rel, trash=self.trash)
        self.assertEqual(self.trashed, [])
        self.assertTrue(outside.exists())


class BrowserTest(unittest.TestCase):
    """4 Oct: the tray opened Edge, the Windows default, but every attempt was saved in
    Chrome's storage, so reopening from the tray looked like the work was gone. The tray's
    Open-in menu picks the browser and profile, and every way of opening the page uses it."""

    URL = "http://127.0.0.1:8025/"

    def setUp(self):
        d = pathlib.Path(tempfile.mkdtemp(dir=TMP))
        self.exe = d / "pf" / "Google/Chrome/Application/chrome.exe"
        self.exe.parent.mkdir(parents=True)
        self.exe.write_bytes(b"")
        data = d / "local" / "Google/Chrome/User Data"
        data.mkdir(parents=True)
        (data / "Local State").write_text(json.dumps({"profile": {"info_cache": {
            "Profile 2": {"name": "Work"}, "Default": {"name": "Ravi"}}}}), encoding="utf-8")
        self.env = {"PROGRAMFILES": str(d / "pf"), "LOCALAPPDATA": str(d / "local")}
        codeview.STATE.data = {"roots": [], "progress": {}}
        self.launched, self.fallback = [], []

    def open(self, launch=None):
        codeview.open_page(self.URL, found=codeview.browsers(self.env),
                           launch=launch or self.launched.append, fallback=self.fallback.append)

    def test_each_profile_of_an_installed_browser_is_offered(self):
        found = codeview.browsers(self.env)
        self.assertEqual([(b["id"], b["label"]) for b in found],
                         [("chrome/Default", "Chrome (Ravi)"), ("chrome/Profile 2", "Chrome (Work)")])

    def test_a_browser_that_is_not_installed_is_not_offered(self):
        self.assertFalse([b for b in codeview.browsers(self.env) if b["id"].startswith("edge/")])

    def test_unreadable_profile_list_still_offers_the_default_profile(self):
        (pathlib.Path(self.env["LOCALAPPDATA"]) / "Google/Chrome/User Data/Local State").write_text("{")
        self.assertEqual([b["id"] for b in codeview.browsers(self.env)], ["chrome/Default"])

    def test_chosen_profile_opens_the_page(self):
        codeview.STATE.set_browser("chrome/Default")
        self.open()
        self.assertEqual(self.launched, [[str(self.exe), "--profile-directory=Default", self.URL]])
        self.assertEqual(self.fallback, [])

    def test_no_choice_uses_the_default_browser(self):
        self.open()
        self.assertEqual((self.launched, self.fallback), ([], [self.URL]))

    def test_choice_that_is_no_longer_installed_uses_the_default_browser(self):
        codeview.STATE.set_browser("edge/Default")
        self.open()
        self.assertEqual((self.launched, self.fallback), ([], [self.URL]))

    def test_browser_that_will_not_start_falls_back_and_logs(self):
        codeview.setup_log()
        codeview.STATE.set_browser("chrome/Default")

        def broken(_cmd):
            raise OSError("gone")
        self.open(launch=broken)
        self.assertEqual(self.fallback, [self.URL])
        self.assertIn("could not start Chrome (Ravi)", log_text())

    def test_choice_survives_a_restart(self):
        codeview.STATE.set_browser("chrome/Profile 2")
        self.assertEqual(codeview.State().data["browser"], "chrome/Profile 2")
        codeview.STATE.set_browser(None)
        self.assertNotIn("browser", codeview.State().data)


class LogTest(unittest.TestCase):
    """3 Oct: it vanished with no record of why. The log keeps start and stop lines,
    so a run that started but never logged a stop was ended from outside."""

    def test_unclosed_previous_run_is_reported(self):
        text = ("2026-10-03 12:00:00 pid 100 started  port 8025\n"
                "2026-10-03 12:05:00 pid 100 stopped: Quit from the tray menu\n"
                "2026-10-03 13:00:00 pid 200 started  port 8025\n"
                "2026-10-03 13:01:00 pid 200 request failed\n")
        note = codeview.unclosed_run(text)
        self.assertEqual(note, ("200", "2026-10-03 13:00:00"))

    def test_closed_previous_run_is_not_reported(self):
        text = ("2026-10-03 13:00:00 pid 200 started  port 8025\n"
                "2026-10-03 13:30:00 pid 200 stopped: Ctrl+C\n")
        self.assertIsNone(codeview.unclosed_run(text))

    def test_empty_log_is_not_reported(self):
        self.assertIsNone(codeview.unclosed_run(""))

    def test_parent_name_is_found(self):
        self.assertTrue(codeview.process_name(os.getpid()).lower().startswith("python"))

    @unittest.skipUnless(os.name == "nt", "Windows process names")
    def test_name_of_a_process_this_user_cannot_open(self):
        """3 Oct: a restart through Task Scheduler logged 'parent ?', because a normal
        user may not open svchost. Process 4 (System) is closed to us the same way."""
        self.assertEqual(codeview.process_name(4), "System")


class ProgressTest(unittest.TestCase):
    def setUp(self):
        codeview.STATE.data = {"roots": [], "progress": {}}
        self.rid = codeview.STATE.roots()[0]["id"]

    def test_solve_records_time_and_hints(self):
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=300, hints=1)
        self.assertEqual((e["s"], e["secs"], e["best"], e["hints"]), ("done", 300, 300, 1))
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=200, hints=0)
        self.assertEqual((e["secs"], e["best"], e["hints"]), (200, 200, 0))
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=400, hints=0)
        self.assertEqual((e["secs"], e["best"]), (400, 200))

    def test_passing_again_counts_a_review(self):
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=60)
        self.assertNotIn("r", e)
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=50)
        self.assertEqual(e["r"], 1)
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=40)
        self.assertEqual(e["r"], 2)

    def test_revise_resets_reviews(self):
        codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True)
        codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True)
        e = codeview.STATE.set_progress(self.rid, "a/B01_X.java", "revise")
        self.assertNotIn("r", e)

    def test_bad_numbers_are_refused(self):
        with self.assertRaises(ValueError):
            codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs="abc")
        with self.assertRaises(ValueError):
            codeview.STATE.set_progress(self.rid, "a/B01_X.java", "done", True, secs=-5)


class RestoreTest(unittest.TestCase):
    """The page's Undo after a peek: marking revise drops the review count and the done
    date, and marking done again cannot bring them back, so the old entry is put back."""

    def setUp(self):
        codeview.STATE.data = {"roots": [], "progress": {}}
        self.rid = codeview.STATE.roots()[0]["id"]

    def test_undo_brings_back_reviews_and_date(self):
        before = {"s": "done", "t": "2026-09-20", "r": 2, "best": 300, "secs": 320, "hints": 0,
                  "practiced": "2026-09-20", "pass": True}
        codeview.STATE.data["progress"][codeview.STATE.root(self.rid)["path"]] = {"a/B01_X.java": dict(before)}
        codeview.STATE.set_progress(self.rid, "a/B01_X.java", "revise")
        e = codeview.STATE.restore_progress(self.rid, "a/B01_X.java", before)
        self.assertEqual(e, before)
        self.assertEqual(codeview.STATE.progress(self.rid)["a/B01_X.java"], before)

    def test_restoring_nothing_removes_the_entry(self):
        codeview.STATE.set_progress(self.rid, "a/B01_X.java", "revise")
        self.assertEqual(codeview.STATE.restore_progress(self.rid, "a/B01_X.java", {}), {})
        self.assertNotIn("a/B01_X.java", codeview.STATE.progress(self.rid))

    def test_bad_entries_are_refused(self):
        for bad in ([], {"s": "maybe"}, {"t": "yesterday"}, {"r": -1}, {"pass": "yes"}, {"colour": "red"}):
            with self.subTest(bad=bad), self.assertRaises(ValueError):
                codeview.STATE.restore_progress(self.rid, "a/B01_X.java", bad)


class HintsTest(unittest.TestCase):
    def test_gentlest_first(self):
        hints = javasrc.practice_hints(SAMPLE)
        self.assertEqual([h["title"] for h in hints],
                         ["KEY INSIGHT", "APPROACH (hash map)", "COMPLEXITY", "INTERVIEW FOLLOW-UPS"])
        self.assertEqual(hints[0]["text"], "Look up the complement.")
        self.assertEqual(hints[1]["text"], "1. Walk once.\n   indented detail")

    def test_pattern_sections_are_hidden_and_come_back_as_hints(self):
        """The 00-Patterns files add sections that name the pattern; practice mode must
        hide them, and the hint for which pattern it is comes before its template."""
        src = SAMPLE.replace(" * APPROACH  (hash map)", " * RECOGNIZE WHEN\n *   Pair with a sum.\n *\n"
                             " * TEMPLATE\n *   seen.put(x, i)\n *\n * APPROACH  (hash map)").replace(
            " * INTERVIEW FOLLOW-UPS", " * VARIATIONS\n *   LC 454 four lists\n *\n"
            " * PITFALLS\n *   Same index twice.\n *\n * DEEP DIVE\n *   A02_TwoSum\n *\n"
            " * INTERVIEW FOLLOW-UPS")
        skeleton, _ = javasrc.practice_skeleton(src)
        for leaked in ("Pair with a sum", "seen.put", "LC 454", "Same index twice", "A02_TwoSum"):
            self.assertNotIn(leaked, skeleton)
        titles = [h["title"] for h in javasrc.practice_hints(src)]
        self.assertEqual(titles[:4], ["RECOGNIZE WHEN", "TEMPLATE", "KEY INSIGHT", "APPROACH (hash map)"])
        self.assertEqual(sorted(titles[5:]), ["DEEP DIVE", "INTERVIEW FOLLOW-UPS", "PITFALLS", "VARIATIONS"])

    def test_skeleton_mentions_the_hint_button(self):
        skeleton, _ = javasrc.practice_skeleton(SAMPLE)
        self.assertIn("Hint", skeleton)
        self.assertNotIn("Look up the complement", skeleton)

    def test_hints_hold_exactly_what_practice_hides_in_every_repo_file(self):
        """Falsifier: a header line that practice mode hides but no hint shows would be
        lost, and a hint line that practice mode shows would be a duplicate."""
        files = [p for p in (HERE.parent / "AAScratches").rglob("*.java")]
        self.assertGreater(len(files), 500)          # the check is not running on nothing
        header_line = lambda l: " ".join(re.sub(r"^\s*\*", "", l).split())
        checked = with_hints = 0
        for p in files:
            src = p.read_text(encoding="utf-8", errors="replace")
            start = src.find("/*")
            end = src.find("*/", start + 2) if start != -1 else -1
            if start == -1 or end == -1 or start > 400:
                continue
            after_text = javasrc.hide_hints(src)
            before = Counter(header_line(l) for l in src[start:end].split("\n"))
            after = Counter(header_line(l) for l in after_text[start:after_text.find("*/", start + 2)].split("\n"))
            removed = before - after
            removed.pop("", None)
            shown = Counter()
            for h in javasrc.practice_hints(src):
                shown[h["title"]] += 1
                shown.update(" ".join(l.split()) for l in h["text"].split("\n") if l.strip())
            self.assertEqual(removed, shown, p.name)
            checked += 1
            with_hints += bool(shown)
        self.assertGreater(checked, 500)
        self.assertGreater(with_hints, 500)


class SkeletonTest(unittest.TestCase):
    """Which method bodies practice mode hides. Each case is a real file that went wrong (5 Oct)."""

    @staticmethod
    def hidden(body, cls="Solver"):
        src = f"class {cls} {{\n{body}\n    public static void main(String[] args) {{ }}\n}}\n"
        return javasrc.practice_skeleton(src)[1]

    def test_main_class_named_like_a_data_holder_is_still_hidden(self):
        """B11_FindCorruptPair: 'Pair' at the end of the name made the whole file look like a
        Pair data class, so Practice said 'Nothing to hide'."""
        for cls in ("FindCorruptPair", "InsertInterval", "BestMeetingPoint", "MinimumTimeToVisitCell"):
            with self.subTest(cls=cls):
                self.assertEqual(self.hidden("    static int solve(int[] a) { return a[0]; }", cls), ["solve"])

    def test_real_data_holder_keeps_its_methods(self):
        src = ("class Pair {\n    int a, b;\n    int sum() { return a + b; }\n}\n"
               "class Solver {\n    static int solve(Pair p) { return p.sum(); }\n"
               "    public static void main(String[] args) { }\n}\n")
        self.assertEqual(javasrc.practice_skeleton(src)[1], ["solve"])

    def test_check_or_print_named_answer_is_hidden(self):
        """checkInclusion (LC 567), checkBST, printSpiral: answers whose names start like a helper."""
        for sig in ("static boolean checkInclusion(String a, String b) { return a.isEmpty(); }",
                    "boolean checkBST(Object node, long lo, long hi) { return node == null; }",
                    "static java.util.List<Integer> printSpiral(int[][] m) { return null; }"):
            with self.subTest(sig=sig):
                self.assertEqual(len(self.hidden("    " + sig)), 1)

    def test_check_and_print_helpers_stay_visible(self):
        body = ('    private static void print(String label, Object actual, Object expected) {\n'
                '        System.out.println(label + ": " + actual + "   expected " + expected);\n    }\n'
                '    private static void checkCase(int[] a, int want) {\n'
                '        if (a.length != want) throw new AssertionError("expected " + want);\n    }\n'
                '    static void printList(int[] a) {\n        System.out.println(java.util.Arrays.toString(a));\n    }\n')
        self.assertEqual(self.hidden(body), [])

    def test_local_variable_named_expected_does_not_keep_an_answer(self):
        """C04_StackSortable: `int expected = 1` is part of the answer, not a test helper."""
        body = ("    static boolean isStackSortable(int[] a) {\n        int expected = 1;\n"
                "        for (int v : a) if (v == expected) expected++;\n"
                "        return expected == a.length + 1;\n    }\n")
        self.assertEqual(self.hidden(body), ["isStackSortable"])

    def test_every_dsa_file_has_something_to_practise(self):
        """Falsifier for 'Nothing to hide': a new file, or a change to the rules above, that
        leaves a DSA file with nothing hidden fails here instead of in the page."""
        all_in_main = {"InfosysGrumpyOwner.java"}   # an interview answer typed straight into main()
        files = sorted((HERE.parent / "AAScratches" / "01-DSA").rglob("*.java"))
        self.assertGreater(len(files), 500)          # the check is not running on nothing
        empty = {p.name for p in files
                 if not javasrc.practice_skeleton(p.read_text(encoding="utf-8", errors="replace"))[1]}
        self.assertEqual(empty, all_in_main)


def fake_tgz(members):
    buf = io.BytesIO()
    with tarfile.open(fileobj=buf, mode="w:gz") as t:
        for name, data in members.items():
            info = tarfile.TarInfo(name)
            info.size = len(data)
            t.addfile(info, io.BytesIO(data))
    raw = buf.getvalue()
    return raw, "sha512-" + base64.b64encode(hashlib.sha512(raw).digest()).decode()


class OfflineTest(unittest.TestCase):
    def setUp(self):
        self.lib = offline.Lib("fake-editor", "1.2.3", "min/", "https://cdn.example/fake/min")

    def test_cdn_until_installed_then_local(self):
        self.assertEqual(offline.url_for(self.lib), "https://cdn.example/fake/min")
        raw, integrity = fake_tgz({"package/min/vs/loader.js": b"// loader",
                                   "package/README.md": b"not needed"})
        offline.install(self.lib, raw, integrity)
        self.assertEqual(offline.url_for(self.lib), "/vendor/fake-editor@1.2.3/min")
        self.assertEqual(offline.path_for("fake-editor@1.2.3/min/vs/loader.js").read_bytes(), b"// loader")
        self.assertFalse((offline.lib_dir(self.lib) / "README.md").exists())

    def test_wrong_checksum_is_refused(self):
        lib = offline.Lib("other", "1.0.0", "min/", "https://cdn.example/other")
        raw, _ = fake_tgz({"package/min/a.js": b"x"})
        with self.assertRaises(ValueError):
            offline.install(lib, raw, "sha512-" + base64.b64encode(b"0" * 64).decode())
        self.assertFalse(offline.ready(lib))

    def test_paths_outside_the_cache_are_refused(self):
        raw, integrity = fake_tgz({"package/min/vs/a.js": b"a",
                                   "package/min/../../../escaped.js": b"evil"})
        lib = offline.Lib("trav", "1.0.0", "min/", "https://cdn.example/trav")
        offline.install(lib, raw, integrity)
        self.assertFalse((TMP / "escaped.js").exists())
        self.assertFalse(any(p.name == "escaped.js" for p in TMP.rglob("*")))
        for bad in ("../state.json", "trav@1.0.0/../../state.json", "not-installed@1/x.js", ""):
            with self.assertRaises(ValueError, msg=bad):
                offline.path_for(bad)


class HttpTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = codeview.make_server(0)
        cls.port = cls.server.server_address[1]
        threading.Thread(target=cls.server.serve_forever, daemon=True).start()

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()

    def get(self, path):
        return urllib.request.urlopen(f"http://127.0.0.1:{self.port}{path}", timeout=10)

    def test_page_has_no_unfilled_placeholders(self):
        html = self.get("/").read().decode("utf-8")
        self.assertNotIn("__CV_", html)

    def test_vendor_traversal_is_refused(self):
        for path in ("/vendor/..%2Fstate.json", "/vendor/../codeview.py", "/vendor/nothing@1/x.js"):
            with self.assertRaises(urllib.error.HTTPError, msg=path) as cm:
                self.get(path)
            self.assertIn(cm.exception.code, (400, 404), path)

    def test_practice_returns_hints(self):
        rel = "AAScratches/_codeview_test/B01_TwoSum.java"
        p = codeview.REPO / rel
        p.parent.mkdir(parents=True, exist_ok=True)
        try:
            p.write_text(SAMPLE, encoding="utf-8")
            rid = codeview.STATE.roots()[0]["id"]
            d = json.load(self.get(f"/api/practice?root={rid}&path={rel}"))
            self.assertEqual(d["hints"][0]["title"], "KEY INSIGHT")
            self.assertEqual(d["hidden"], ["solve"])
        finally:
            p.unlink()
            p.parent.rmdir()

    def post(self, path, body, page_header=True):
        req = urllib.request.Request(f"http://127.0.0.1:{self.port}{path}", json.dumps(body).encode(),
                                     {"Content-Type": "application/json", **({"X-CodeView": "1"} if page_header else {})})
        return urllib.request.urlopen(req, timeout=10)

    def test_delete_is_wired_and_guarded(self):
        """A missing file gets the delete route's own refusal (400, not 404), and a request
        without the page's header is refused before anything is touched."""
        rid = codeview.STATE.roots()[0]["id"]
        body = {"root": rid, "path": "AAScratches/no_such_file.java"}
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/delete", body)
        self.assertEqual(cm.exception.code, 400)
        self.assertIn("not a file", json.load(cm.exception)["error"])
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/delete", body, page_header=False)
        self.assertEqual(cm.exception.code, 403)


def tearDownModule():
    for h in list(codeview.log.handlers):      # Windows will not delete an open log file
        h.close()
        codeview.log.removeHandler(h)
    shutil.rmtree(TMP, ignore_errors=True)


if __name__ == "__main__":
    unittest.main(verbosity=2)
