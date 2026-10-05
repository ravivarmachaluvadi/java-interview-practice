"""
Tests for codeview's server side: tray quit, the log, progress, practice hints and the
offline copy of the page's libraries.

    python tools/test_codeview.py

Everything runs against temporary files: the env vars below are set before codeview is
imported, so the real tools/codeview-state.json and codeview.log are never touched.
"""
import base64, hashlib, io, json, os, pathlib, re, shutil, subprocess, sys, tarfile, tempfile, threading, unittest
import urllib.error, urllib.request
from unittest import mock
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

    def post(self, path, body, page_header=True, timeout=10):
        req = urllib.request.Request(f"http://127.0.0.1:{self.port}{path}", json.dumps(body).encode(),
                                     {"Content-Type": "application/json", **({"X-CodeView": "1"} if page_header else {})})
        return urllib.request.urlopen(req, timeout=timeout)

    @unittest.skipUnless(pathlib.Path(codeview.JAVAC).exists(), "no JDK")
    def test_assist_is_wired_and_guarded(self):
        code = "import java.util.*;\nclass S {\n    void f() {\n        List<Integer> nums = new ArrayList<>();\n        nums.\n    }\n}\n"
        res = json.load(self.post("/api/assist", {"op": "complete", "root": "", "path": None, "code": code,
                                                  "offset": code.index("nums.\n") + 5}, timeout=90))
        self.assertIn("add", {it["label"] for it in res["items"]})
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/assist", {"op": "shell"})
        self.assertEqual(cm.exception.code, 400)
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/assist", {"op": "complete", "code": code}, page_header=False)
        self.assertEqual(cm.exception.code, 403)

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

    def test_refusal_is_always_a_clean_403(self):
        """Answering before reading the body made Windows abort about 7% of refusals
        (WinError 10053), so the test above failed about 1 run in 7. 100 in a row now."""
        body = {"root": "x", "path": "AAScratches/no_such_file.java"}
        for i in range(100):
            with self.assertRaises(urllib.error.HTTPError, msg=f"request {i}") as cm:
                self.post("/api/delete", body, page_header=False)
            self.assertEqual(cm.exception.code, 403)


@unittest.skipUnless(pathlib.Path(codeview.JAVAC).exists(), "no JDK")
class RunLeavesFileTest(unittest.TestCase):
    """The page's Try copy relies on this: running changed code for a real file compiles it
    in a temp folder, so the file and its folder are never written."""

    def test_changed_code_runs_and_the_file_stays_as_it_was(self):
        rel = "AAScratches/_codeview_test/B02_Echo.java"
        p = codeview.REPO / rel
        p.parent.mkdir(parents=True, exist_ok=True)
        src = b'class B02_Echo {\n    public static void main(String[] a) { System.out.println("a"); }\n}\n'
        try:
            p.write_bytes(src)
            before = p.stat().st_mtime_ns
            rid = codeview.STATE.roots()[0]["id"]
            res = codeview.run_code(src.decode().replace('"a"', '"b"'), rid, rel, "", 10, None)
            self.assertTrue(res["ok"], res.get("output"))
            self.assertEqual(res["output"].strip(), "b")
            self.assertEqual(p.read_bytes(), src)
            self.assertEqual(p.stat().st_mtime_ns, before)
            self.assertEqual([x.name for x in p.parent.iterdir()], ["B02_Echo.java"])   # no .class left beside it
        finally:
            for x in p.parent.iterdir():
                x.unlink()
            p.parent.rmdir()


CUR = "‸"          # where the cursor is in a fixture; taken out before the code is sent
DAILY = """import java.util.Arrays;
import java.util.Stack;

class DailyTemperatures {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Stack<Integer> waiting = new Stack<>();
        for (int today = 0; today < n; today++) {
            while (!waiting.isEmpty() && temperatures[today] > temperatures[waiting.peek()]) {
                int colderDay = waiting.pop();
                result[colderDay] = today - colderDay;
                @@
            }
            waiting.push(today);
        }
        return result;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual));
    }

    public static void main(String[] args) {
        DailyTemperatures solution = new DailyTemperatures();
        %%
        print("case 1", solution.dailyTemperatures(new int[]{73, 74}), new int[]{1, 0});
    }
}
"""


@unittest.skipUnless(pathlib.Path(codeview.JAVAC).exists(), "no JDK")
class AssistTest(unittest.TestCase):
    """The editor's autocomplete, parameter hints, docs and live errors (tools/CvAssist.java)."""

    def ask(self, op, src, fname="DailyTemperatures.java", flags="", extra="", src_dir=None):
        i = src.index(CUR) if CUR in src else 0
        res = codeview.ASSIST.ask(op, src.replace(CUR, ""), i, src_dir, fname, flags, extra)
        self.assertNotIn("error", res, res)
        return res

    def complete(self, method="", main="", src=None, **kw):
        return self.ask("complete", src or DAILY.replace("@@", method).replace("%%", main), **kw)

    def item(self, res, label):
        found = [it for it in res["items"] if it["label"] == label]
        self.assertTrue(found, f"{label} not in {sorted({it['label'] for it in res['items']})}")
        return found[0]

    def labels(self, res):
        return {it["label"] for it in res["items"]}

    def test_members_of_a_local_with_real_parameter_names(self):
        """The line after the cursor is another statement and there is no `;` yet: javac
        must still see `waiting` as a Stack<Integer>, not a declaration of a new variable."""
        res = self.complete(method=f"waiting.{CUR}")
        self.assertLessEqual({"peek", "pop", "push", "isEmpty", "search"}, self.labels(res))
        self.assertEqual(self.item(res, "push")["detail"], "(Integer item)")   # name from the JDK's src.zip
        self.assertEqual(self.item(res, "pop")["type"], "Integer")             # E filled in from Stack<Integer>

    def test_replace_range_is_the_typed_prefix(self):
        src = DAILY.replace("@@", f"waiting.pe{CUR}").replace("%%", "")
        res = self.ask("complete", src)
        cur = src.index(CUR)
        self.assertEqual((res["from"], res["to"]), (cur - 2, cur))
        self.assertIn("peek", self.labels(res))

    def test_inside_a_condition(self):
        src = DAILY.replace("while (!waiting.isEmpty()", f"while (!waiting.isE{CUR}").replace("@@", "").replace("%%", "")
        self.assertIn("isEmpty", self.labels(self.ask("complete", src)))

    def test_class_name_gives_statics_only(self):
        arrays = self.labels(self.complete(method=f"Arrays.{CUR}"))
        self.assertLessEqual({"sort", "toString", "fill", "asList"}, arrays)
        self.assertFalse({"getClass", "wait", "notify"} & arrays)
        ints = self.labels(self.complete(method=f"int q = Integer.MAX{CUR}"))
        self.assertIn("MAX_VALUE", ints)
        self.assertNotIn("intValue", ints)

    def test_array_has_length(self):
        self.assertIn("length", self.labels(self.complete(method=f"temperatures.{CUR}")))

    def test_generic_chain(self):
        src = ("import java.util.*;\nclass M {\n    void f() {\n        Map<String, List<Integer>> m = new HashMap<>();\n"
               f"        m.get(\"a\").{CUR}\n    }}\n")
        res = self.ask("complete", src, fname="M.java")
        self.assertEqual(self.item(res, "add")["detail"], "(Integer e)")

    def test_locals_first_and_no_instance_methods_in_static_main(self):
        res = self.complete(method=f"col{CUR}")
        ranked = sorted((it for it in res["items"] if it["label"].lower().startswith("col")), key=lambda it: it["sort"])
        self.assertEqual(ranked[0]["label"], "colderDay")
        in_main = self.labels(self.complete(main=f"d{CUR}"))
        self.assertNotIn("dailyTemperatures", in_main)
        self.assertIn("print", self.labels(self.complete(main=f"pri{CUR}")))

    def test_class_name_brings_its_import(self):
        src = DAILY.replace("@@", f"ArrayDe{CUR}").replace("%%", "")
        res = self.ask("complete", src)
        dq = self.item(res, "ArrayDeque")
        self.assertEqual(dq["import"], "\nimport java.util.ArrayDeque;")
        self.assertEqual(res["importAt"], src.index("import java.util.Stack;") + len("import java.util.Stack;"))
        self.assertNotIn("import", self.item(self.complete(method=f"Stac{CUR}"), "Stack"))
        self.assertNotIn("import", self.item(self.complete(method=f"Strin{CUR}"), "String"))

    def test_import_goes_between_header_and_class_when_there_are_none(self):
        src = f"/*\n * header\n */\n\nclass D {{\n    void f() {{\n        ArrayDe{CUR}\n    }}\n}}\n"
        res = self.ask("complete", src, fname="D.java")
        self.assertEqual(self.item(res, "ArrayDeque")["import"], "import java.util.ArrayDeque;\n\n")
        self.assertEqual(res["importAt"], src.index("class D"))

    def test_compact_file_needs_no_import(self):
        src = f"void main() {{\n    ArrayDe{CUR}\n}}\n"
        res = self.ask("complete", src, fname="Scratch.java", flags="c")
        self.assertNotIn("import", self.item(res, "ArrayDeque"))

    def test_new_inserts_diamond_and_brackets(self):
        res = self.complete(method=f"java.util.Deque<Integer> d = new ArrayDe{CUR}")
        self.assertTrue(self.item(res, "ArrayDeque")["insert"].startswith("ArrayDeque<>("))

    def first_after_new(self, src, fname="E.java"):
        """The top item after `new`, as the editor sorts them, and the whole list."""
        res = self.ask("complete", src, fname=fname)
        return min(res["items"], key=lambda it: it["sort"]), res

    def test_new_offers_the_expected_array_type_first(self):
        """IntelliJ's ★: `int[] arr = new i` offers int[] first, caret inside the brackets."""
        top, _ = self.first_after_new(self.wrap(f"int[] arr = new i{CUR}"))
        self.assertEqual((top["label"], top["insert"]), ("int[]", "int[$0]"))
        self.assertIn("★", top["type"])
        top, _ = self.first_after_new(self.wrap(f"int[][] grid = new {CUR}"))      # nothing typed yet
        self.assertEqual((top["label"], top["insert"]), ("int[][]", "int[$0][]"))
        top, _ = self.first_after_new(self.wrap(f"String[] parts = new S{CUR}"))
        self.assertEqual((top["label"], top["insert"]), ("String[]", "String[$0]"))

    def test_new_offers_implementations_of_an_expected_interface(self):
        top, res = self.first_after_new(self.wrap(f"List<Integer> list = new {CUR}", imports="import java.util.List;\n"))
        self.assertEqual(top["label"], "ArrayList")
        self.assertTrue(top["insert"].startswith("ArrayList<>("))
        self.assertEqual(top["import"], "\nimport java.util.ArrayList;")
        self.assertIn("★", self.item(res, "LinkedList")["type"])
        top, _ = self.first_after_new(self.wrap(f"Deque<Integer> dq = new A{CUR}", imports="import java.util.*;\n"))
        self.assertEqual(top["label"], "ArrayDeque")

    def test_new_after_return_and_assignment(self):
        src = f"class E {{\n    int[] f(int n) {{\n        return new {CUR}\n    }}\n}}\n"
        self.assertEqual(self.first_after_new(src)[0]["label"], "int[]")
        top, _ = self.first_after_new(self.wrap(f"long[] memo;\n        memo = new {CUR}"))
        self.assertEqual(top["label"], "long[]")

    def test_new_without_an_expected_type_still_offers_primitive_arrays(self):
        _, res = self.first_after_new(self.wrap(f"Object o = new i{CUR}"))
        self.assertEqual(self.item(res, "int[]")["insert"], "int[$0]")

    @staticmethod
    def wrap(body, imports=""):
        return f"{imports}class E {{\n    void f() {{\n        {body}\n    }}\n}}\n"

    def test_postfix_var(self):
        src = DAILY.replace("@@", f"waiting.pop().va{CUR}").replace("%%", "")
        res = self.ask("complete", src)
        var = self.item(res, "var")
        start = src.index("waiting.pop().va")
        self.assertEqual(var["cut"], [start, start + len("waiting.pop().")])    # deleted; the word after the dot is replaced
        self.assertNotIn("from", var)                                           # so it ranks like a member, not above them
        self.assertIn("Integer ${1:pop} = waiting.pop();", var["insert"])

    def test_nothing_inside_a_comment(self):
        self.assertEqual(self.complete(method=f"// waiting.{CUR}")["items"], [])

    def test_signature_with_names_and_active_parameter(self):
        res = self.ask("signature", DAILY.replace("@@", f"waiting.push({CUR}").replace("%%", ""))
        self.assertEqual(res["sigs"][res["active"]]["label"], "push(Integer item)")
        self.assertEqual(res["param"], 0)
        res = self.ask("signature", DAILY.replace("@@", f"int big = Math.max(1, {CUR}").replace("%%", ""))
        self.assertIn("max(int a, int b)", [s["label"] for s in res["sigs"]])
        self.assertEqual(res["param"], 1)

    def test_doc_comes_from_the_jdk_sources(self):
        key = self.item(self.complete(method=f"waiting.{CUR}"), "peek")["key"]
        md = self.ask("doc", "", extra=key)["md"]
        self.assertIn("Looks at the object at the top of this stack", md)

    def test_hover_shows_signature_and_doc(self):
        src = DAILY.replace("waiting.peek()", f"waiting.pe{CUR}ek()").replace("@@", "").replace("%%", "")
        md = self.ask("hover", src)["md"]
        self.assertIn("peek()", md)
        self.assertIn("Looks at the object at the top of this stack", md)

    def test_check_reports_a_type_error_on_its_line(self):
        src = DAILY.replace("@@", 'int x = "a";').replace("%%", "")
        diags = self.ask("check", src)["diags"]
        self.assertEqual(len(diags), 1, diags)
        self.assertIn("incompatible types", diags[0]["msg"])
        line = src.index('int x = "a";')
        self.assertTrue(line <= diags[0]["start"] < line + len('int x = "a";'))

    def test_check_flags_only_what_javac_rejects(self):
        """Falsifier for false red underlines: across every .java file under AAScratches,
        compiled exactly as Run compiles it (same file name, same -sourcepath), a file the
        live check flags must also fail a real javac. 5 Oct: 682 of 692 clean; the other 10
        fail javac too (2 trick-question answers that are meant not to compile, 8 LLD and
        Spring files that need Lombok, Guava, JUnit and the like)."""
        root = codeview.REPO / "AAScratches"
        files = sorted(root.rglob("*.java"))
        self.assertGreater(len(files), 500)          # the check is not running on nothing
        rid = codeview.STATE.roots()[0]["id"]
        wrong, clean = {}, 0
        for p in files:
            code = p.read_text(encoding="utf-8", errors="replace")
            src_dir, fname, info = codeview.compile_target(code, rid, p.relative_to(codeview.REPO).as_posix())
            diags = self.ask("check", code, fname=fname, flags="c" if info[3] else "", src_dir=src_dir)["diags"]
            if not diags:
                clean += 1
                continue
            work = pathlib.Path(tempfile.mkdtemp(dir=TMP))
            (work / fname).write_bytes(code.encode("utf-8"))
            cmd = [codeview.JAVAC, "-nowarn", "-encoding", "UTF-8", "-d", str(work / "out")]
            javac = subprocess.run(cmd + (["-sourcepath", str(src_dir)] if src_dir else []) + [str(work / fname)],
                                   capture_output=True, stdin=subprocess.DEVNULL)
            if javac.returncode == 0:
                wrong[p.relative_to(root).as_posix()] = diags[0]["msg"]
        self.assertEqual(wrong, {})
        self.assertGreater(clean, 600)

    # --- Run compiles in the warm helper (5 Oct: javac was ~85% of a 0.7 s Run)
    def test_run_compiles_in_the_warm_helper(self):
        codeview.ASSIST.ask("warm")
        code = DAILY.replace("@@", "").replace("%%", "")
        with mock.patch.object(codeview.subprocess, "run", side_effect=AssertionError("a javac process was started")):
            res = codeview.run_code(code, "", None, "", 10, None)
        self.assertTrue(res["ok"], res.get("output"))
        self.assertIn("case 1: [1, 0]", res["output"])

    def test_compile_errors_read_exactly_like_javac(self):
        """The page underlines errors by parsing javac's text (file:line: error, source line,
        caret line), so the helper must print exactly what the javac command prints."""
        code = DAILY.replace("@@", 'int x = "é→";\n                undefinedCall(colderDay);').replace("%%", "")
        res = codeview.run_code(code, "", None, "", 10, None)
        with mock.patch.object(codeview.ASSIST, "ask", return_value={"error": "off"}):
            cli = codeview.run_code(code, "", None, "", 10, None)
        self.assertEqual((res["phase"], res["ok"]), ("compile", False))
        self.assertIn("2 errors", cli["output"])
        self.assertEqual(res["output"].replace("\r\n", "\n"), cli["output"].replace("\r\n", "\n"))

    def test_run_falls_back_to_the_javac_command(self):
        code = DAILY.replace("@@", "").replace("%%", "")
        with mock.patch.object(codeview.ASSIST, "ask", return_value={"error": "helper down"}):
            res = codeview.run_code(code, "", None, "", 10, None)
        self.assertTrue(res["ok"], res.get("output"))

    def sibling_folder(self, name):
        """A folder holding Helper.java and B03_UsesHelper.java, removed after the test."""
        folder = codeview.REPO / "AAScratches" / name
        folder.mkdir(parents=True, exist_ok=True)
        self.addCleanup(lambda: shutil.rmtree(folder, ignore_errors=True))
        (folder / "Helper.java").write_bytes(b"class Helper {\n    static int twice(int x) {\n        return 2 * x;\n    }\n}\n")
        user = b"class B03_UsesHelper {\n    public static void main(String[] a) {\n        System.out.println(Helper.twice(21));\n    }\n}\n"
        (folder / "B03_UsesHelper.java").write_bytes(user)
        return folder, f"AAScratches/{name}/B03_UsesHelper.java", user.decode()

    def test_run_finds_a_helper_class_in_the_same_folder(self):
        folder, rel, code = self.sibling_folder("_codeview_test_run")
        rid = codeview.STATE.roots()[0]["id"]
        res = codeview.run_code(code, rid, rel, "", 10, None)
        self.assertTrue(res["ok"], res.get("output"))
        self.assertEqual(res["output"].strip(), "42")
        self.assertEqual(sorted(p.name for p in folder.iterdir()), ["B03_UsesHelper.java", "Helper.java"])

    # --- Alt+Enter: add the missing import
    def test_quick_fix_offers_the_import(self):
        src = "class Q {\n    void f() {\n        ArrayDeque<Integer> d = new ArrayDeque<>();\n    }\n}\n"
        res = self.ask("imports", src, fname="Q.java", extra="ArrayDeque")
        self.assertEqual([f["fqn"] for f in res["fixes"]], ["java.util.ArrayDeque"])
        self.assertEqual(res["fixes"][0]["import"], "import java.util.ArrayDeque;\n\n")
        self.assertEqual(res["importAt"], 0)

    # --- go to definition
    def test_definition_of_a_local_a_method_and_nothing_for_the_jdk(self):
        src = DAILY.replace("result[colderDay] = today", f"result[col{CUR}derDay] = today").replace("@@", "").replace("%%", "")
        res = self.ask("definition", src)
        decl = src.replace(CUR, "").index("int colderDay") + len("int ")
        self.assertEqual((res["start"], res["end"]), (decl, decl + len("colderDay")))
        src = DAILY.replace("solution.dailyTemperatures(", f"solution.daily{CUR}Temperatures(").replace("@@", "").replace("%%", "")
        decl = src.replace(CUR, "").index("public int[] dailyTemperatures") + len("public int[] ")
        self.assertEqual(self.ask("definition", src)["start"], decl)
        src = DAILY.replace("waiting.peek()", f"waiting.pe{CUR}ek()").replace("@@", "").replace("%%", "")
        res = self.ask("definition", src)
        self.assertNotIn("start", res)
        self.assertNotIn("file", res)

    def test_definition_in_another_file_of_the_folder(self):
        folder, rel, code = self.sibling_folder("_codeview_test_def")
        rid = codeview.STATE.roots()[0]["id"]
        res = codeview.assist_request({"op": "definition", "root": rid, "path": rel, "code": code,
                                       "offset": code.index("twice") + 2})
        self.assertEqual(res["path"], "AAScratches/_codeview_test_def/Helper.java")
        self.assertEqual((res["line"], res["col"], res["len"]), (2, 16, 5))

    # --- Shift+F6 rename
    def test_rename_finds_every_use_in_the_file(self):
        src = DAILY.replace("int colderDay", f"int col{CUR}derDay").replace("@@", "").replace("%%", "")
        res = self.ask("rename", src)
        clean = src.replace(CUR, "")
        self.assertEqual(sorted(res["spots"]), [m.start() for m in re.finditer(r"\bcolderDay\b", clean)])
        self.assertEqual(len(res["spots"]), 3)
        self.assertEqual(res["len"], len("colderDay"))

    def test_rename_a_class_and_a_method(self):
        src = DAILY.replace("class DailyTemperatures {", f"class Daily{CUR}Temperatures {{").replace("@@", "").replace("%%", "")
        clean = src.replace(CUR, "")
        self.assertEqual(sorted(self.ask("rename", src)["spots"]),
                         [m.start() for m in re.finditer(r"\bDailyTemperatures\b", clean)])
        src = DAILY.replace("int[] dailyTemperatures(", f"int[] daily{CUR}Temperatures(").replace("@@", "").replace("%%", "")
        self.assertEqual(len(self.ask("rename", src)["spots"]), 2)

    def test_rename_refuses_jdk_names(self):
        src = DAILY.replace("waiting.peek()", f"waiting.pe{CUR}ek()").replace("@@", "").replace("%%", "")
        self.assertIn("reject", self.ask("rename", src))

    def test_restarts_after_the_helper_dies(self):
        self.complete(method=f"waiting.{CUR}")
        codeview.ASSIST.proc.kill()
        codeview.ASSIST.proc.wait()
        self.assertIn("push", self.labels(self.complete(method=f"waiting.{CUR}")))


def tearDownModule():
    codeview.ASSIST.stop()
    for h in list(codeview.log.handlers):      # Windows will not delete an open log file
        h.close()
        codeview.log.removeHandler(h)
    shutil.rmtree(TMP, ignore_errors=True)


if __name__ == "__main__":
    unittest.main(verbosity=2)
