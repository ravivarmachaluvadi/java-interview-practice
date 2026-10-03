"""
Tests for codeview's server side: tray quit, the log, progress, practice hints and the
offline copy of the page's libraries.

    python tools/test_codeview.py

Everything runs against temporary files: the env vars below are set before codeview is
imported, so the real tools/codeview-state.json and codeview.log are never touched.
"""
import base64, hashlib, io, json, os, pathlib, re, sys, tarfile, tempfile, threading, unittest
import urllib.error, urllib.request
from collections import Counter

TMP = pathlib.Path(tempfile.mkdtemp(prefix="codeview_test_"))
os.environ["CODEVIEW_STATE"] = str(TMP / "state.json")
os.environ["CODEVIEW_LOG"] = str(TMP / "codeview.log")
os.environ["CODEVIEW_VENDOR"] = str(TMP / "vendor")
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

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

    def shutdown(self):
        self.shut = True


def log_text():
    for h in codeview.log.handlers:
        h.flush()
    p = pathlib.Path(os.environ["CODEVIEW_LOG"])
    return p.read_text(encoding="utf-8") if p.exists() else ""


class QuitTest(unittest.TestCase):
    """3 Oct: one stray click on the tray's Quit stopped the page."""

    def setUp(self):
        codeview.setup_log()

    def test_saying_no_keeps_it_running(self):
        icon, server = FakeIcon(), FakeServer()
        codeview.quit_from_tray(icon, server, ask=lambda: False)
        self.assertFalse(icon.stopped)
        self.assertFalse(server.shut)
        self.assertIn("quit cancelled", log_text())

    def test_saying_yes_stops_and_logs_why(self):
        icon, server = FakeIcon(), FakeServer()
        codeview.quit_from_tray(icon, server, ask=lambda: True)
        self.assertTrue(icon.stopped)
        self.assertTrue(server.shut)
        self.assertIn("stopped: Quit from the tray menu", log_text())


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


class HintsTest(unittest.TestCase):
    def test_gentlest_first(self):
        hints = javasrc.practice_hints(SAMPLE)
        self.assertEqual([h["title"] for h in hints],
                         ["KEY INSIGHT", "APPROACH (hash map)", "COMPLEXITY", "INTERVIEW FOLLOW-UPS"])
        self.assertEqual(hints[0]["text"], "Look up the complement.")
        self.assertEqual(hints[1]["text"], "1. Walk once.\n   indented detail")

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


if __name__ == "__main__":
    unittest.main(verbosity=2)
