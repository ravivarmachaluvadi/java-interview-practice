"""
Tests for codeview's server side: tray quit, the log, progress, practice hints and the
offline copy of the page's libraries.

    python tools/test_codeview.py

Everything runs against temporary files: the env vars below are set before codeview is
imported, so the real tools/codeview-state.json and codeview.log are never touched.
"""
import base64, hashlib, io, json, os, pathlib, re, shutil, subprocess, sys, tarfile, tempfile, threading, time, unittest
import urllib.error, urllib.request
from unittest import mock
from collections import Counter

TMP = pathlib.Path(tempfile.mkdtemp(prefix="codeview_test_"))
os.environ["CODEVIEW_STATE"] = str(TMP / "state.json")
os.environ["CODEVIEW_LOG"] = str(TMP / "codeview.log")
os.environ["CODEVIEW_VENDOR"] = str(TMP / "vendor")
os.environ["CODEVIEW_NO_PUSH"] = "1"
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

    def test_a_tray_started_by_a_right_click_does_not_reopen_that_path(self):
        steps = []
        codeview.restart_from_tray(FakeIcon(), FakeServer(steps),
                                   argv=["codeview.py", "--tray", "--open", r"C:\x\A.java", "--port=9000"],
                                   spawn=lambda args: steps.append(args))
        self.assertEqual(steps[-1][2:], ["--tray", "--port=9000", "--no-open"])
        steps.clear()
        codeview.restart_from_tray(FakeIcon(), FakeServer(steps), argv=["codeview.py", "--tray", r"--open=C:\x"],
                                   spawn=lambda args: steps.append(args))
        self.assertEqual(steps[-1][2:], ["--tray", "--no-open"])


class TemplateTest(unittest.TestCase):
    """5 Oct: a blank New-file template (DecodeWays.java) passed check_headers.py."""

    def test_header_check_flags_an_unfilled_new_file(self):
        found = check_headers.placeholders(codeview.java_template("C16_MyProblem.java"))
        self.assertIn("LeetCode ?", found)
        self.assertIn("O(?)", found)
        self.assertIn("...", found)

    def test_filled_headers_have_none(self):
        self.assertEqual(check_headers.placeholders(SAMPLE), [])


class NewProblemTest(unittest.TestCase):
    """6 Oct: Ravi asked for an easier way to add a DSA problem than typing its whole path into
    +. The form sends folder, level, name and source; the server picks the next free number."""

    def setUp(self):
        self.base = TMP / "new_root"
        (self.base / "01-Arrays").mkdir(parents=True, exist_ok=True)
        for n in ("A01_First.java", "B01_One.java", "B11_FindCorruptPair.java", "C02_Two.java", "README.md"):
            (self.base / "01-Arrays" / n).write_text("class X {}", encoding="utf-8")
        self.rid = codeview.STATE.add_root(str(self.base))["id"]

    def tearDown(self):
        codeview.STATE.remove_root(self.rid)
        shutil.rmtree(self.base, ignore_errors=True)

    def make(self, **kw):
        args = dict(folder="01-Arrays", level="B", name="Two Sum", source="1", difficulty="Easy", must=False)
        res = codeview.new_problem(self.rid, **(args | kw))
        return res["path"], (self.base / res["path"]).read_text(encoding="utf-8")

    def test_takes_the_next_free_number_of_its_level(self):
        self.assertEqual(self.make()[0], "01-Arrays/B12_TwoSum.java")
        self.assertEqual(self.make(level="C", name="Three Sum")[0], "01-Arrays/C03_ThreeSum.java")
        self.assertEqual(self.make(level="D", name="Hard One")[0], "01-Arrays/D01_HardOne.java")
        self.assertEqual(self.make(level="", name="Plain")[0], "01-Arrays/Plain.java")
        self.assertEqual(self.make(folder="02-New/Sub", level="A")[0], "02-New/Sub/A01_TwoSum.java")

    def test_names_become_class_names(self):
        for name, cls, title in (("LRU cache", "LRUCache", "LRU cache"), ("TwoSumII", "TwoSumII", "Two Sum II"),
                                 ("kadane's algorithm", "KadanesAlgorithm", "kadane's algorithm"),
                                 ("two-sum: sorted", "TwoSumSorted", "two-sum: sorted")):
            with self.subTest(name=name):
                path, text = self.make(name=name, level="")
                self.assertEqual(path, f"01-Arrays/{cls}.java")
                self.assertIn(f"\nclass {cls} {{", text)
                self.assertEqual(check_headers.parse_header(text)["title"], title)

    def test_header_has_every_section_and_only_the_blanks_left_to_fill(self):
        path, text = self.make(must=True)
        self.assertNotIn(b"\r", (self.base / path).read_bytes())         # LF, like the repo's files
        h = check_headers.parse_header(text)
        self.assertEqual((h["title"], h["meta"], h["mustKnow"]), ("Two Sum", "LeetCode 1 | Easy", True))
        for s in check_headers.SECTIONS_DSA:
            self.assertIn(s, h["sections"])
        with mock.patch.object(check_headers, "ROOT", self.base):
            _, _, issues = check_headers.check(self.base / path)
        self.assertEqual(issues, ["unfilled-template:['O(?)', '...']"])

    def test_a_long_title_still_parses(self):
        _, text = self.make(name="Longest Substring Without Repeating Characters Again", difficulty="Medium")
        h = check_headers.parse_header(text)
        self.assertEqual((h["title"], h["meta"]), ("Longest Substring Without Repeating Characters Again",
                                                   "LeetCode 1 | Medium"))

    def test_source_words_and_a_blank_source(self):
        self.assertEqual(check_headers.parse_header(self.make(source="GfG", difficulty="Medium")[1])["meta"],
                         "GfG | Medium")
        _, text = self.make(name="Other", source=" ")
        self.assertEqual(check_headers.parse_header(text)["meta"], "LeetCode ? | Easy")
        self.assertIn("LeetCode ?", check_headers.placeholders(text))

    def test_refuses_bad_input(self):
        for kw in (dict(level="E"), dict(name=""), dict(name="!!!"), dict(name="3Sum"), dict(folder="../out"),
                   dict(difficulty="Brutal")):
            with self.subTest(kw=kw), self.assertRaises(ValueError):
                self.make(**kw)
        self.make(level="", name="Plain")
        with self.assertRaises(ValueError):
            self.make(level="", name="Plain")
        for n in range(12, 100):
            (self.base / "01-Arrays" / f"B{n:02d}_N{n}.java").write_text("class N {}", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "B99"):
            self.make()

    def test_a_typed_path_cannot_reuse_a_number(self):
        with self.assertRaisesRegex(ValueError, "B11_FindCorruptPair.java.*B12"):
            codeview.new_file(self.rid, "01-Arrays/B11_Other.java")
        res = codeview.new_file(self.rid, "01-Arrays/B12_Other.java")
        self.assertEqual(res["path"], "01-Arrays/B12_Other.java")
        lines = (self.base / res["path"]).read_text(encoding="utf-8").splitlines()
        sel = res["select"]                                     # the page selects PROBLEM's ... to type over
        self.assertEqual(lines[sel["line"] - 2], " * PROBLEM")
        self.assertEqual(lines[sel["line"] - 1][sel["col"] - 1:sel["col"] - 1 + sel["len"]], "...")


class MethodStubTest(unittest.TestCase):
    """6 Oct: Ravi asked for the method to be made too. The form takes a signature, typed or pasted
    from LeetCode (even its whole `class Solution { ... }`), and the file gets that method with a
    default return and a main() that calls it. Every case below must compile and run."""

    SIGS = [
        "public int[] twoSum(int[] nums, int target) {",
        "class Solution {    public List<List<Integer>> threeSum(int[] nums) {            }}",
        "public void rotate(int[] nums, int k)",
        "boolean isAnagram(String s, String t)",
        "public int maxDepth(TreeNode root) {",
        "public ListNode reverseList(ListNode head) {",
        "public TreeNode invertTree(TreeNode root)",
        "public int[][] merge(int[][] intervals) {",
        "public String longestCommonPrefix(String[] strs) {",
        "public Map<String, Integer> count(List<String> words)",
        "public char findTheDifference(String s, String t)",
        "public double findMedianSortedArrays(int[] nums1, int[] nums2)",
        "static long total(long... values)",
        "public void moveZeroes(int nums[])",
        "public void setZeroes(int[][] matrix)",
        "public boolean seenAll(Set<Integer> seen, char[] cs, Deque<Integer> stack, Long big)",
        "public List<String> letterCombinations(String digits)",
        "public void hello()",
        "public int longestOne(Map<String, List<Integer>> graphByName, int[][] distances, Deque<Integer> stack, int k)",
    ]

    def setUp(self):
        self.base = TMP / "stub_root"
        (self.base / "01-Arrays").mkdir(parents=True, exist_ok=True)
        (self.base / "01-Arrays" / "B11_FindCorruptPair.java").write_text("class X {}", encoding="utf-8")
        self.rid = codeview.STATE.add_root(str(self.base))["id"]

    def tearDown(self):
        codeview.STATE.remove_root(self.rid)
        shutil.rmtree(self.base, ignore_errors=True)

    def make(self, method, name="", level=""):
        res = codeview.new_problem(self.rid, "01-Arrays", level, name, "1", "Easy", method=method)
        return res["path"], (self.base / res["path"]).read_text(encoding="utf-8")

    def test_reads_signatures(self):
        P = codeview.parse_signature
        self.assertEqual(P("public int[] twoSum(int[] nums, int target) {"),
                         ("int[]", "twoSum", [("int[]", "nums"), ("int", "target")]))
        self.assertEqual(P("class Solution { public Map<String,List<Integer>> group(String[] words, int k) { } }"),
                         ("Map<String, List<Integer>>", "group", [("String[]", "words"), ("int", "k")]))
        self.assertEqual(P("static long total(long... values)"), ("long", "total", [("long[]", "values")]))
        self.assertEqual(P("void moveZeroes(int nums[])"), ("void", "moveZeroes", [("int[]", "nums")]))
        self.assertEqual(P("int count(final @Deprecated int n)"), ("int", "count", [("int", "n")]))
        for bad in ("twoSum", "int twoSum", "int[] (int a)", "return x(y)", "int class(int a)", "int f(int)", ""):
            with self.subTest(bad=bad), self.assertRaises(ValueError):
                P(bad)

    def test_every_signature_compiles_and_runs(self):
        for i, sig in enumerate(self.SIGS):
            with self.subTest(sig=sig):
                path, text = self.make(sig, name=f"Case {chr(65 + i)}")
                method = codeview.parse_signature(sig)[1]
                self.assertIn(f" {method}(", text)
                res = codeview.run_code(text, self.rid, path, "", 30, None)
                self.assertTrue(res["ok"], (res.get("phase"), res.get("output", "")[:600], text[-1500:]))
                self.assertIn("case 1", res["output"])
                if codeview.parse_signature(sig)[2]:
                    self.assertIn("expected ?", res["output"])
                self.assertTrue(javasrc.practice_skeleton(text)[1], "Practice would find nothing to hide")
                with mock.patch.object(check_headers, "ROOT", self.base):
                    self.assertEqual(check_headers.check(self.base / path)[2], ["unfilled-template:['O(?)', '...']"])

    def test_main_calls_it_with_samples_and_prints_arrays(self):
        _, text = self.make("public int[] twoSum(int[] nums, int target) {", name="Two Sum")
        self.assertIn("    static int[] twoSum(int[] nums, int target) {\n        return new int[0];\n    }", text)
        self.assertIn('check("case 1", twoSum(new int[]{1, 2, 3}, 2), "?");', text)
        out = codeview.run_code(text, self.rid, "01-Arrays/TwoSum.java", "", 30, None)["output"]
        self.assertIn("case 1: []   expected ?", out)                   # an array prints as [..], not [I@1b6d

    def test_a_void_method_prints_what_it_changed(self):
        path, text = self.make("public void rotate(int[] nums, int k)", name="Rotate Array")
        self.assertIn("int[] nums1 = new int[]{1, 2, 3};\n        rotate(nums1, 2);\n        check(\"case 1\", nums1, \"?\");", text)
        out = codeview.run_code(text, self.rid, path, "", 30, None)["output"]
        self.assertIn("case 1: [1, 2, 3]   expected ?", out)

    def test_trees_and_lists_get_their_node_class(self):
        _, text = self.make("public TreeNode invertTree(TreeNode root)", name="Invert")
        self.assertIn("class TreeNode {\n    int val;\n    TreeNode left;\n    TreeNode right;", text)
        self.assertNotIn("class ListNode", text)
        path, text = self.make("public ListNode reverseList(ListNode head) {", name="Reverse")
        self.assertIn("class ListNode {\n    int val;\n    ListNode next;", text)
        res = codeview.run_code(text.replace("return null;", "return head;"), self.rid, path, "", 30, None)
        self.assertIn("case 1: [1, 2, 3]   expected ?", res["output"])       # a list prints its values

    def test_the_name_can_come_from_the_method(self):
        path, text = self.make("public int[] twoSum(int[] nums, int target) {", level="B")
        self.assertEqual(path, "01-Arrays/B12_TwoSum.java")
        self.assertEqual(check_headers.parse_header(text)["title"], "Two Sum")
        with self.assertRaises(ValueError):
            self.make("", name="")

    def test_no_method_keeps_the_old_template(self):
        _, text = self.make("", name="Plain")
        self.assertIn("    static int solve(int[] nums) {\n        return 0;\n    }", text)


CREATE_SRC = """import java.util.*;

class Main {
    int field = 1;

    static long sumUp(long a) {
        $$
    }

    int count(int[] xs) {
        %%
    }

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        @@
    }
}

class Helper {
    int base = 1;
}
"""


class CreateMethodTest(unittest.TestCase):
    """6 Oct: Ravi asked for IntelliJ's Create method: write a call to a method that does not
    exist yet, Alt+Enter on its red name, and the method appears below with parameters from the
    arguments and a return type from where the call stands. Each result must compile and run."""

    def create(self, line, at="@@", src=CREATE_SRC, word=None):
        src = src.replace(at, line)
        for k in ("@@", "$$", "%%"):
            src = src.replace(k, "return 0;" if k != "@@" else "")
        name = word or re.search(r"(\w+)\(", line.split("=")[-1] if "=" in line else line).group(1)
        off = src.index(name, src.index(line))
        res = codeview.ASSIST.ask("create", src, off, None, "Main.java", "", "")
        self.assertNotIn("error", res, res)
        return src, res

    def apply(self, src, res):
        out = src[:res["insertAt"]] + res["text"] + src[res["insertAt"]:]
        if res.get("imports"):
            out = out[:res["importAt"]] + "".join(res["imports"]) + out[res["importAt"]:]
        return out

    def made(self, line, at="@@", src=CREATE_SRC, word=None):
        """-> (the new method's first line, its selected text, the whole new source)"""
        src, res = self.create(line, at, src, word)
        self.assertIn("text", res, res)
        new = self.apply(src, res)
        rid = codeview.STATE.roots()[0]["id"]
        run = codeview.run_code(new, rid, None, "", 30, None)
        self.assertTrue(run["ok"], (run.get("output", "")[:800], new))
        lines = res["text"].split("\n")
        head = next(x for x in lines if x.strip())
        sel = lines[res["selLine"]][res["selCol"] - 1:res["selCol"] - 1 + res["selLen"]]
        return head.strip(), sel, new

    def test_javac_names_the_missing_method_and_its_argument_types(self):
        src = CREATE_SRC.replace("@@", "int r = twoSum(nums, 9);").replace("$$", "return 0;").replace("%%", "return 0;")
        msgs = [d["msg"] for d in codeview.ASSIST.ask("check", src, 0, None, "Main.java", "", "")["diags"]]
        self.assertTrue(any("cannot find symbol" in m and "symbol:   method twoSum(int[],int)" in m for m in msgs), msgs)

    def test_return_type_from_where_the_call_stands(self):
        cases = [("int r = twoSum(nums, 9);", "private static int twoSum(int[] nums, int i) {", "return 0;"),
                 ('if (isValid("ab")) { }', "private static boolean isValid(String s) {", "return false;"),
                 ("process(nums);", "private static void process(int[] nums) {", ""),
                 ("String s = label(nums.length);", "private static String label(int length) {", "return null;"),
                 ("double avg = 1 + mean(nums);", "private static double mean(int[] nums) {", "return 0;"),
                 ("boolean ok = !done(3) && nums.length > 0;", "private static boolean done(int i) {", "return false;"),
                 ("for (int x : evens(nums)) { }", "private static int[] evens(int[] nums) {", "return new int[0];"),
                 ("System.out.println(total(nums, 2L, 'c', 1.5)); // total",
                  "private static Object total(int[] nums, long l, char c, double d) {", "return null;"),
                 ("List<Integer> got = collect(nums, new ArrayList<>(List.of(1)));",
                  "private static List<Integer> collect(int[] nums, ArrayList<Integer> arrayList) {", "return null;"),
                 ("int same = pair(nums, nums);", "private static int pair(int[] nums, int[] nums1) {", "return 0;")]
        for line, head, sel in cases:
            with self.subTest(line=line):
                word = line.split("// ")[1] if "// " in line else None
                self.assertEqual(self.made(line, word=word)[:2], (head, sel))

    def test_a_return_takes_the_methods_type(self):
        self.assertEqual(self.made("return helper(a);", at="$$")[:2], ("private static long helper(long a) {", "return 0;"))

    def test_instance_code_makes_an_instance_method(self):
        self.assertEqual(self.made("return size(xs) + field;", at="%%")[:2], ("private int size(int[] xs) {", "return 0;"))

    def test_it_goes_after_the_method_that_calls_it(self):
        head, _, new = self.made("int r = twoSum(nums, 9);")
        self.assertLess(new.index("public static void main"), new.index(head))
        self.assertLess(new.index(head), new.index("class Helper"))
        self.assertIn("    }\n\n    private static int twoSum(int[] nums, int i) {\n        return 0;\n    }\n}", new)

    def test_a_call_on_another_class_of_this_file_creates_it_there(self):
        head, _, new = self.made("int v = new Helper().twice(3);", word="twice")
        self.assertEqual(head, "int twice(int i) {")
        self.assertLess(new.index("class Helper"), new.index(head))
        head, _, new = self.made("int k = Helper.make(2);", word="make")
        self.assertEqual(head, "static int make(int i) {")
        self.assertLess(new.index("class Helper"), new.index(head))

    def test_a_type_not_imported_yet_gets_its_import(self):
        src = "import java.util.Map;\n\nclass Main {\n    public static void main(String[] args) {\n        @@\n    }\n}\n"
        _, res = self.create('int n = size(Map.of("a", 1).keySet());', src=src, word="size")
        self.assertEqual(res["imports"], ["\nimport java.util.Set;"])
        self.assertIn("private static int size(Set<String> keySet) {", self.made('int n = size(Map.of("a", 1).keySet());', src=src, word="size")[2])

    def test_the_page_can_ask_for_it(self):
        """The first browser run found the server refusing 'create': only listed ops pass."""
        src = CREATE_SRC.replace("@@", "int r = twoSum(nums, 9);").replace("$$", "return 0;").replace("%%", "return 0;")
        res = codeview.assist_request({"op": "create", "root": codeview.STATE.roots()[0]["id"], "path": None,
                                       "offset": src.index("twoSum("), "code": src})
        self.assertIn("private static int twoSum(int[] nums, int i) {", res.get("text", ""), res)

    def test_nothing_to_create(self):
        for line, word in (("int r = nums.length;", "length"), ("long z = sumUp(2);", "sumUp")):
            with self.subTest(line=line):
                self.assertNotIn("text", self.create(line, word=word)[1])


def git(cwd, *args):
    return subprocess.run(["git", "-C", str(cwd), *args], capture_output=True, text=True, check=True,
                          encoding="utf-8").stdout.strip()


class GitPushTest(unittest.TestCase):
    """6 Oct: Ravi asked for one Push button. The panel lists what changed; Push commits the
    ticked files only and pushes them, rebasing onto anything pushed from elsewhere."""

    def setUp(self):
        self.tmp = pathlib.Path(tempfile.mkdtemp(dir=TMP, prefix="git_"))
        self.remote = self.tmp / "remote.git"
        git(self.tmp, "init", "-q", "--bare", "-b", "main", str(self.remote))
        self.work = self.clone("work")
        for n in ("a.java", "b.java", "README.md"):
            (self.work / n).write_bytes(f"// {n}\nline two\n".encode())
        git(self.work, "add", "-A")
        git(self.work, "commit", "-q", "-m", "start")
        git(self.work, "push", "-q", "-u", "origin", "main")
        self.rid = codeview.STATE.add_root(str(self.work))["id"]

    def tearDown(self):
        codeview.STATE.remove_root(self.rid)
        shutil.rmtree(self.tmp, ignore_errors=True)

    def clone(self, name):
        repo = self.tmp / name
        git(self.tmp, "clone", "-q", str(self.remote), str(repo))
        for k, v in (("user.name", "Test"), ("user.email", "t@example.com"), ("core.autocrlf", "false")):
            git(repo, "config", k, v)
        return repo

    def from_elsewhere(self, name, text, subject):
        other = self.clone("other_" + subject.replace(" ", "_"))
        (other / name).write_bytes(text.encode())
        git(other, "commit", "-q", "-am", subject)
        git(other, "push", "-q")

    def remote_files(self):
        return set(git(self.remote, "show", "--name-only", "--format=", "main").splitlines())

    def subjects(self, repo):
        return git(repo, "log", "--format=%s", "main").splitlines()

    def test_status_lists_each_change(self):
        (self.work / "a.java").write_bytes(b"changed\n")
        (self.work / "b.java").unlink()
        (self.work / "sub").mkdir()
        (self.work / "sub" / "c.java").write_bytes(b"new\n")
        st = codeview.git_status(self.rid)
        self.assertEqual({f["path"]: f["status"] for f in st["files"]},
                         {"a.java": "changed", "b.java": "deleted", "sub/c.java": "new"})
        self.assertEqual((st["repo"], st["branch"], st["upstream"], st["ahead"], st["behind"]),
                         (True, "main", "origin/main", 0, 0))

    def test_a_folder_outside_git(self):
        plain = self.tmp / "plain"
        plain.mkdir()
        rid = codeview.STATE.add_root(str(plain))["id"]
        try:
            with mock.patch.dict(os.environ, {"GIT_CEILING_DIRECTORIES": str(self.tmp)}):
                self.assertEqual(codeview.git_status(rid), {"repo": False})
        finally:
            codeview.STATE.remove_root(rid)

    def test_pushes_only_the_ticked_files(self):
        (self.work / "a.java").write_bytes(b"changed\n")
        (self.work / "c.java").write_bytes(b"new\n")
        (self.work / "d.java").write_bytes(b"staged, not ticked\n")
        (self.work / "README.md").write_bytes(b"not ticked\n")
        (self.work / "b.java").unlink()
        git(self.work, "add", "d.java")
        res = codeview.git_push(self.rid, ["a.java", "c.java", "b.java"], "Update a, add c")
        self.assertTrue(res["ok"], res)
        self.assertEqual(self.subjects(self.remote)[0], "Update a, add c")
        self.assertEqual(self.remote_files(), {"a.java", "b.java", "c.java"})
        self.assertNotIn("b.java", git(self.remote, "ls-tree", "--name-only", "main").splitlines())
        left = {f["path"]: f["status"] for f in codeview.git_status(self.rid)["files"]}
        self.assertEqual(left, {"d.java": "new", "README.md": "changed"})
        self.assertEqual(git(self.work, "diff", "--cached", "--name-only"), "d.java")   # still staged
        self.assertEqual(codeview.git_status(self.rid)["ahead"], 0)

    def test_rebases_onto_a_push_from_elsewhere(self):
        self.from_elsewhere("README.md", "theirs\n", "theirs")
        (self.work / "a.java").write_bytes(b"mine\n")
        (self.work / "b.java").write_bytes(b"not ticked\n")
        res = codeview.git_push(self.rid, ["a.java"], "mine")
        self.assertTrue(res["ok"], res)
        self.assertTrue(res["rebased"])
        self.assertEqual(self.subjects(self.remote)[:2], ["mine", "theirs"])
        self.assertEqual((self.work / "b.java").read_bytes(), b"not ticked\n")      # put back after the rebase

    def test_a_clash_keeps_the_commit_here_and_pushes_nothing(self):
        self.from_elsewhere("a.java", "theirs\n", "theirs")
        (self.work / "a.java").write_bytes(b"mine\n")
        res = codeview.git_push(self.rid, ["a.java"], "mine")
        self.assertFalse(res["ok"])
        self.assertTrue(res["committed"])
        self.assertIn("same lines", res["error"])
        self.assertEqual(self.subjects(self.remote)[0], "theirs")
        self.assertEqual(self.subjects(self.work)[0], "mine")
        self.assertEqual((self.work / "a.java").read_bytes(), b"mine\n")
        git_dir = self.work / ".git"
        self.assertFalse((git_dir / "rebase-merge").exists() or (git_dir / "rebase-apply").exists())

    def test_refuses_stale_or_empty_requests(self):
        with self.assertRaisesRegex(ValueError, "[Nn]othing to push"):
            codeview.git_push(self.rid, [], "x")
        (self.work / "a.java").write_bytes(b"changed\n")
        with self.assertRaisesRegex(ValueError, "nope.java"):
            codeview.git_push(self.rid, ["nope.java"], "x")
        with self.assertRaisesRegex(ValueError, "message"):
            codeview.git_push(self.rid, ["a.java"], "  ")
        self.assertEqual(self.subjects(self.work), ["start"])

    def test_pushes_commits_left_by_a_failed_push(self):
        (self.work / "a.java").write_bytes(b"changed\n")
        git(self.work, "commit", "-q", "-am", "made earlier")
        self.assertEqual(codeview.git_status(self.rid)["ahead"], 1)
        res = codeview.git_push(self.rid, [], "")
        self.assertTrue(res["ok"], res)
        self.assertEqual(self.subjects(self.remote)[0], "made earlier")

    def test_a_rename_goes_as_one_change(self):
        git(self.work, "mv", "a.java", "a2.java")
        st = codeview.git_status(self.rid)
        self.assertEqual([(f["path"], f["status"], f.get("from")) for f in st["files"]], [("a2.java", "renamed", "a.java")])
        self.assertTrue(codeview.git_push(self.rid, ["a2.java"], "rename")["ok"])
        self.assertEqual(set(git(self.remote, "ls-tree", "--name-only", "main").splitlines()),
                         {"a2.java", "b.java", "README.md"})

    def test_readme_lists_the_new_file(self):
        prob = self.work / "AAScratches" / "01-DSA" / "01-Arrays"
        prob.mkdir(parents=True)
        (prob / "A01_Foo.java").write_bytes(SAMPLE.replace("Two Sum", "Foo").encode())
        with mock.patch.object(codeview, "readme_root", lambda top: top / "AAScratches"):
            git(self.work, "add", "-A")
            codeview.refresh_readmes(self.work)
            git(self.work, "add", "-A")
            git(self.work, "commit", "-q", "-m", "problems")
            git(self.work, "push", "-q")
            readme = self.work / "AAScratches" / "01-DSA" / "README.md"
            before = readme.read_bytes()
            self.assertIn(b"A01_Foo.java", before)
            (prob / "B01_Bar.java").write_bytes(SAMPLE.replace("Two Sum", "Bar").encode())
            st = codeview.git_status(self.rid, readmes=True)
            self.assertEqual(st["readmes"], ["AAScratches/01-DSA/README.md"])
            self.assertEqual(readme.read_bytes(), before)                                # only a preview
            res = codeview.git_push(self.rid, ["AAScratches/01-DSA/01-Arrays/B01_Bar.java"], "Add Bar")
        self.assertTrue(res["ok"], res)
        self.assertEqual(self.remote_files(), {"AAScratches/01-DSA/01-Arrays/B01_Bar.java", "AAScratches/01-DSA/README.md"})
        self.assertIn("B01_Bar.java", git(self.remote, "show", "main:AAScratches/01-DSA/README.md"))

    def test_test_servers_never_push_this_repo(self):
        """test_ui.py's server clicks Push; CODEVIEW_NO_PUSH keeps it off the real repo."""
        rid = codeview.STATE.roots()[0]["id"]
        self.assertEqual(codeview.root_path(rid), codeview.REPO)
        calls = []
        with mock.patch.object(codeview, "NO_PUSH", True), \
                mock.patch.object(codeview, "git", lambda *a, **k: calls.append(a) or self.fail(a)):
            with self.assertRaisesRegex(ValueError, "switched off"):
                codeview.git_push(rid, ["x.java"], "x")
        self.assertEqual(calls, [])


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


class FilesTest(unittest.TestCase):
    """8 Oct: Ravi asked for an easy way to make a folder and a .md file in it, then edit and
    save it. + now makes a problem, a file or a folder; right-click renames, and a folder can
    go to the Recycle Bin. Names Windows or the list would choke on are refused up front."""

    def setUp(self):
        self.base = TMP / "files_root"
        for rel, text in (("Topic/A01_First.java", "class First {}"), ("Topic/B12_Two.java", "class Two {}"),
                          ("Topic/notes.md", "# Notes\n"), ("README.md", "# R\n")):
            (self.base / rel).parent.mkdir(parents=True, exist_ok=True)
            (self.base / rel).write_bytes(text.encode())
        (self.base / "img").mkdir(exist_ok=True)
        (self.base / "img" / "pic.png").write_bytes(b"\x89PNG")
        self.rid = codeview.STATE.add_root(str(self.base))["id"]
        self.trashed = []

    def tearDown(self):
        codeview.STATE.remove_root(self.rid)
        codeview.STATE.data["progress"].pop(str(self.base.resolve()), None)
        shutil.rmtree(self.base, ignore_errors=True)

    def trash(self, p):
        self.trashed.append(p)
        shutil.rmtree(p)

    def dirs(self, node=None, prefix=""):
        node = node or codeview.build_tree(self.rid)
        out = []
        for d in node["dirs"]:
            out.append(prefix + d["name"])
            out += self.dirs(d, prefix + d["name"] + "/")
        return out

    # ---- new folder
    def test_makes_a_folder_and_a_nested_one(self):
        self.assertEqual(codeview.new_folder(self.rid, "Spring"), {"path": "Spring"})
        self.assertEqual(codeview.new_folder(self.rid, "Spring\\Core/Beans/"), {"path": "Spring/Core/Beans"})
        self.assertTrue((self.base / "Spring" / "Core" / "Beans").is_dir())

    def test_an_empty_folder_shows_in_the_tree_but_an_images_only_one_does_not(self):
        codeview.new_folder(self.rid, "Spring/Core")
        codeview.new_folder(self.rid, "Empty")
        self.assertEqual(self.dirs(), ["Empty", "Spring", "Spring/Core", "Topic"])

    def test_refuses_names_windows_or_the_list_would_choke_on(self):
        for rel, why in (("", "name"), ("../out", "outside|\\.\\."), ("a:b", "does not allow :"),
                         ("x.", "dot or a space"), ("Topic /y", "dot or a space"), ("CON", "Windows keeps"),
                         ("Topic/nul", "Windows keeps"), ("com1.d", "Windows keeps"), (".hidden", "starts with a dot"),
                         ("build", "hides folders named build"), ("Topic/target", "hides folders named target"),
                         ("Topic", "already exists"), ("Topic/notes.md", "already exists")):
            with self.subTest(rel=rel), self.assertRaisesRegex(ValueError, why):
                codeview.new_folder(self.rid, rel)
        self.assertEqual(self.dirs(), ["Topic"])

    # ---- new file
    def test_a_new_md_starts_with_its_heading_and_the_caret_below(self):
        res = codeview.new_file(self.rid, "Topic/fresh.md")
        self.assertEqual((self.base / "Topic" / "fresh.md").read_bytes(), b"# fresh\n\n")
        self.assertEqual(res, {"path": "Topic/fresh.md", "select": {"line": 3, "col": 1, "len": 0}})

    def test_file_names_get_plain_messages(self):
        for rel, why in (("Topic/notes", "file type.*notes\\.md"), ("Topic/Sub/", "folder"),
                         ("Topic/a:b.md", "does not allow :"), ("Topic/CON.md", "Windows keeps"),
                         ("build/x.md", "hides folders named build"), ("Topic/.x.md", "starts with a dot")):
            with self.subTest(rel=rel), self.assertRaisesRegex(ValueError, why):
                codeview.new_file(self.rid, rel)

    # ---- rename
    def test_renames_a_file_and_its_progress_goes_with_it(self):
        codeview.STATE.set_progress(self.rid, "Topic/A01_First.java", "done")
        res = codeview.rename_path(self.rid, "Topic/A01_First.java", "A01_Renamed.java")
        self.assertEqual(res, {"path": "Topic/A01_Renamed.java", "dir": False})
        self.assertFalse((self.base / "Topic" / "A01_First.java").exists())
        self.assertEqual((self.base / "Topic" / "A01_Renamed.java").read_bytes(), b"class First {}")
        prog = codeview.STATE.progress(self.rid)
        self.assertEqual(prog["Topic/A01_Renamed.java"]["s"], "done")
        self.assertNotIn("Topic/A01_First.java", prog)

    def test_renames_a_folder_and_the_progress_inside_it(self):
        codeview.STATE.set_progress(self.rid, "Topic/A01_First.java", "done")
        codeview.STATE.set_progress(self.rid, "README.md", "revise")
        self.assertEqual(codeview.rename_path(self.rid, "Topic", "Arrays"), {"path": "Arrays", "dir": True})
        prog = codeview.STATE.progress(self.rid)
        self.assertEqual(sorted(prog), ["Arrays/A01_First.java", "README.md"])
        self.assertTrue((self.base / "Arrays" / "notes.md").is_file())

    def test_a_change_of_case_only(self):
        self.assertEqual(codeview.rename_path(self.rid, "Topic/notes.md", "Notes.md")["path"], "Topic/Notes.md")
        self.assertIn("Notes.md", os.listdir(self.base / "Topic"))

    def test_the_same_level_number_may_stay(self):
        self.assertEqual(codeview.rename_path(self.rid, "Topic/B12_Two.java", "B12_TwoSum.java")["path"],
                         "Topic/B12_TwoSum.java")

    def test_rename_refusals(self):
        for rel, name, why in (("", "x", "folder itself"), ("Topic/notes.md", "B12_Two.java", "already exists"),
                               ("Topic/notes.md", "pic.png", "would not show"), ("Topic/notes.md", "sub/x.md", "one name"),
                               ("Topic/notes.md", "a:b.md", "does not allow :"), ("Topic/notes.md", "notes.md", "already has"),
                               ("Topic/notes.md", "B12_Other.java", "B12 is already B12_Two.java"),
                               ("Topic", "build", "hides folders named build"), ("Topic/missing.md", "x.md", "not found"),
                               ("img/pic.png", "x.png", "not a file or folder"), ("../x", "y", "outside")):
            with self.subTest(rel=rel, name=name), self.assertRaisesRegex(ValueError, why):
                codeview.rename_path(self.rid, rel, name)
        self.assertTrue((self.base / "Topic" / "notes.md").is_file())

    def test_a_file_open_in_another_program(self):
        with mock.patch.object(codeview.os, "rename", side_effect=PermissionError(13, "in use")), \
                self.assertRaisesRegex(ValueError, "open in another program"):
            codeview.rename_path(self.rid, "Topic/notes.md", "other.md")

    # ---- delete a folder
    def test_a_folder_goes_to_the_bin(self):
        self.assertEqual(codeview.delete_folder(self.rid, "Topic", trash=self.trash), {"ok": True})
        self.assertEqual(self.trashed, [(self.base / "Topic").resolve()])
        self.assertFalse((self.base / "Topic").exists())

    def test_delete_folder_refusals(self):
        (self.base / ".hidden").mkdir()
        for rel, why in (("", "folder itself"), ("Topic/notes.md", "not a folder"), ("missing", "not a folder"),
                         ("../x", "outside"), (".hidden", "not a folder")):
            with self.subTest(rel=rel), self.assertRaisesRegex(ValueError, why):
                codeview.delete_folder(self.rid, rel, trash=self.trash)
        self.assertEqual(self.trashed, [])
        with self.assertRaises(ValueError):            # the file route never takes a folder
            codeview.delete_file(self.rid, "Topic", trash=self.trash)


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


class LocateTest(unittest.TestCase):
    """7 Oct: Explorer's right-click "Open in Code Viewer" hands over any file or folder.
    locate() says which folder in the list shows it and where. Runs on a fake repo."""

    def setUp(self):
        self.top = pathlib.Path(os.path.realpath(tempfile.mkdtemp(dir=TMP)))
        self.repo, self.away = self.top / "repo", self.top / "elsewhere"
        for p in (self.repo / "a/b/B01_X.java", self.repo / "a/pic.png", self.repo / "tools/t.py",
                  self.repo / ".idea/w.xml", self.repo / "a/target/T.java", self.away / "sub/N.md",
                  self.away / "loose.txt"):
            p.parent.mkdir(parents=True, exist_ok=True)
            p.write_bytes(b"x")
        codeview.STATE.data = {"roots": [], "progress": {}}
        patcher = mock.patch.object(codeview, "REPO", self.repo)
        patcher.start()
        self.addCleanup(patcher.stop)
        self.repo_id = codeview.STATE.roots()[0]["id"]

    def where(self, p):
        r = codeview.locate(str(p))
        return codeview.STATE.root(r["root"])["path"], r["path"], r["added"]

    def test_a_repo_file_or_folder_stays_in_the_repo(self):
        """Progress is kept per folder in the list, so the repo always wins."""
        self.assertEqual(self.where(self.repo / "a/b/B01_X.java"), (str(self.repo), "a/b/B01_X.java", False))
        self.assertEqual(self.where(self.repo / "a/b"), (str(self.repo), "a/b", False))
        self.assertEqual(self.where(self.repo), (str(self.repo), "", False))
        codeview.STATE.add_root(str(self.repo / "a"))          # even with a deeper folder listed
        self.assertEqual(self.where(self.repo / "a/b/B01_X.java"), (str(self.repo), "a/b/B01_X.java", False))

    def test_a_file_the_tree_leaves_out_keeps_its_path(self):
        """The page shows its folder instead; nothing is added to the list."""
        self.assertEqual(self.where(self.repo / "a/pic.png"), (str(self.repo), "a/pic.png", False))

    def test_folders_the_repo_tree_skips_get_their_own_entry(self):
        for rel, root, path in (("tools/t.py", "tools", "t.py"), (".idea/w.xml", ".idea", "w.xml"),
                                ("a/target/T.java", "a/target", "T.java"), ("tools", "tools", "")):
            with self.subTest(rel):
                got = self.where(self.repo / rel)
                self.assertEqual(got[:2], (str(self.repo / root), path))
        self.assertEqual(len(codeview.STATE.roots()), 4)        # the repo + 3; "tools" was not added twice

    def test_outside_every_folder_its_folder_joins_the_list(self):
        self.assertEqual(self.where(self.away / "sub"), (str(self.away / "sub"), "", True))
        self.assertEqual(self.where(self.away / "sub/N.md"), (str(self.away / "sub"), "N.md", False))
        self.assertEqual(self.where(self.away / "loose.txt"), (str(self.away), "loose.txt", True))

    def test_the_deepest_listed_folder_wins_outside_the_repo(self):
        codeview.STATE.add_root(str(self.away))
        codeview.STATE.add_root(str(self.away / "sub"))
        self.assertEqual(self.where(self.away / "sub/N.md"), (str(self.away / "sub"), "N.md", False))

    def test_refuses_what_cannot_be_shown(self):
        for bad in ("", "  ", str(self.top / "missing.java"), pathlib.Path(self.top.anchor)):
            with self.subTest(bad=bad), self.assertRaises(ValueError):
                codeview.locate(str(bad))
        self.assertEqual(len(codeview.STATE.roots()), 1)

    def test_quotes_around_a_pasted_path_are_ignored(self):
        self.assertEqual(codeview.locate(f'"{self.repo / "a"}"')["path"], "a")

    def test_page_url(self):
        self.assertEqual(codeview.page_url(8025), "http://127.0.0.1:8025/")
        self.assertEqual(codeview.page_url(8025, {"root": "r", "path": "a b/C#1.java"}),
                         "http://127.0.0.1:8025/#/r/a%20b/C%231.java")
        self.assertEqual(codeview.page_url(8025, {"root": "r", "path": ""}), "http://127.0.0.1:8025/#/r/")


class OpenLocalTest(unittest.TestCase):
    """7 Oct: right-click in the page's file list → "Open in browser" shows the file, or a
    folder's "Index of" page, as a file:/// tab. A page served from http may not open file:///
    itself, so the server starts the browser the page runs in. Nothing real is launched here."""

    CHROME = {"id": "chrome/Default", "label": "Chrome (Ravi)", "exe": r"C:\c\chrome.exe", "profile": "Default"}
    CHROME2 = {"id": "chrome/Profile 2", "label": "Chrome (Work)", "exe": r"C:\c\chrome.exe", "profile": "Profile 2"}
    EDGE = {"id": "edge/Default", "label": "Edge (Ravi)", "exe": r"C:\e\msedge.exe", "profile": "Default"}

    def setUp(self):
        self.base = pathlib.Path(os.path.realpath(tempfile.mkdtemp(dir=TMP))) / "my root"
        (self.base / "Sub Dir").mkdir(parents=True)
        (self.base / "Sub Dir" / "A01_X.java").write_bytes(b"class X {}")
        codeview.STATE.data = {"roots": [], "progress": {}}
        self.rid = codeview.STATE.add_root(str(self.base))["id"]
        self.launched = []

    def open(self, rel, family="chrome", found=None):
        return codeview.open_local(self.rid, rel, family, launch=self.launched.append,
                                   found=[self.CHROME, self.CHROME2, self.EDGE] if found is None else found)

    def test_a_folder_opens_as_its_index_page(self):
        url = self.open("Sub Dir")["url"]
        self.assertEqual(url, (self.base / "Sub Dir").as_uri() + "/")
        self.assertTrue(url.startswith("file:///") and "my%20root/Sub%20Dir/" in url, url)
        self.assertEqual(self.launched, [[self.CHROME["exe"], url]])

    def test_a_file_opens_itself_and_the_root_is_a_folder_too(self):
        self.assertEqual(self.open("Sub Dir/A01_X.java")["url"], (self.base / "Sub Dir" / "A01_X.java").as_uri())
        self.assertEqual(self.open("")["url"], self.base.as_uri() + "/")

    def test_the_tray_choice_of_that_browser_keeps_its_profile(self):
        codeview.STATE.set_browser("chrome/Profile 2")
        url = self.open("Sub Dir")["url"]
        self.assertEqual(self.launched, [[self.CHROME2["exe"], "--profile-directory=Profile 2", url]])

    def test_an_edge_page_opens_edge_even_when_the_tray_picked_chrome(self):
        codeview.STATE.set_browser("chrome/Default")
        url = self.open("Sub Dir", family="edge")["url"]
        self.assertEqual(self.launched, [[self.EDGE["exe"], url]])

    def test_without_that_browser_any_installed_one(self):
        url = self.open("Sub Dir", family="edge", found=[self.CHROME])["url"]
        self.assertEqual(self.launched, [[self.CHROME["exe"], url]])

    def test_an_unknown_page_browser_uses_the_tray_choice(self):
        codeview.STATE.set_browser("edge/Default")
        url = self.open("Sub Dir", family="")["url"]
        self.assertEqual(self.launched, [[self.EDGE["exe"], "--profile-directory=Default", url]])

    @unittest.skipUnless(codeview.IS_WIN, "Windows: the default-browser fallback would open Explorer")
    def test_no_chrome_or_edge_is_an_error(self):
        with self.assertRaisesRegex(ValueError, "Chrome or Edge"):
            self.open("Sub Dir", found=[])
        self.assertEqual(self.launched, [])

    def test_refuses_outside_or_missing(self):
        for rel in ("../outside", "Sub Dir/missing.java"):
            with self.subTest(rel), self.assertRaises(ValueError):
                self.open(rel)
        self.assertEqual(self.launched, [])


@unittest.skipUnless(codeview.IS_WIN, "the right-click menu is Windows-only")
class ContextMenuTest(unittest.TestCase):
    """The three Explorer right-click entries, written under a throwaway key so the real
    HKCU\\Software\\Classes is never touched."""

    BASE = r"Software\CodeViewerTest\Classes"

    def tearDown(self):
        import winreg

        def drop(path):
            try:
                with winreg.OpenKey(winreg.HKEY_CURRENT_USER, path) as k:
                    kids = [winreg.EnumKey(k, i) for i in range(winreg.QueryInfoKey(k)[0])]
            except FileNotFoundError:
                return
            for kid in kids:
                drop(path + "\\" + kid)
            winreg.DeleteKey(winreg.HKEY_CURRENT_USER, path)
        drop(r"Software\CodeViewerTest")

    def values(self, cls):
        import winreg
        key = rf"{self.BASE}\{cls}\shell\CodeViewer"
        with winreg.OpenKey(winreg.HKEY_CURRENT_USER, key) as k:
            out = {winreg.EnumValue(k, i)[0]: winreg.EnumValue(k, i)[1] for i in range(winreg.QueryInfoKey(k)[1])}
        with winreg.OpenKey(winreg.HKEY_CURRENT_USER, key + r"\command") as k:
            out["command"] = winreg.QueryValue(k, None)
        return out

    def test_on_adds_files_folders_and_folder_background(self):
        self.assertFalse(codeview.context_menu_on(self.BASE))
        codeview.set_context_menu(True, self.BASE)
        self.assertTrue(codeview.context_menu_on(self.BASE))
        pyw = str(pathlib.Path(sys.executable).with_name("pythonw.exe"))
        script = str(HERE / "codeview.py")
        for cls, arg in (("*", "%1"), ("Directory", "%1"), (r"Directory\Background", "%V")):
            with self.subTest(cls):
                v = self.values(cls)
                self.assertEqual(v[""], "Open in Code Viewer")
                self.assertEqual(v["MultiSelectModel"], "Single")     # 10 files selected: no 10 tabs
                self.assertTrue(v["Icon"].endswith("codeview.ico"))
                self.assertEqual(v["command"], f'"{pyw}" "{script}" --tray --open "{arg}"')

    def test_off_removes_them_and_twice_is_fine(self):
        import winreg
        codeview.set_context_menu(True, self.BASE)
        codeview.set_context_menu(True, self.BASE)
        codeview.set_context_menu(False, self.BASE)
        codeview.set_context_menu(False, self.BASE)
        self.assertFalse(codeview.context_menu_on(self.BASE))
        for cls in ("*", "Directory", r"Directory\Background"):
            with self.subTest(cls), self.assertRaises(FileNotFoundError):
                winreg.OpenKey(winreg.HKEY_CURRENT_USER, rf"{self.BASE}\{cls}\shell\CodeViewer")

    def test_tray_switch(self):
        import pystray
        with mock.patch.object(codeview, "context_menu_on", return_value=False), \
                mock.patch.object(codeview, "set_context_menu") as setter:
            menu = codeview.tray_menu(pystray, FakeServer(), "http://127.0.0.1:1/")
            item = menu_item(menu, "Right-click: Open in Code Viewer")
            self.assertFalse(item.checked)
            item(FakeIcon())
        setter.assert_called_once_with(True)


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

    def test_file_routes_are_wired_and_guarded(self):
        """mkdir, rename and a folder's delete reach their functions; /api/info tells the page
        they exist, so a tray started before 8 Oct shows none of them."""
        self.assertTrue(json.load(self.get("/api/info"))["files"])
        base = TMP / "http_files"
        base.mkdir(exist_ok=True)
        rid = codeview.STATE.add_root(str(base))["id"]
        try:
            self.assertEqual(json.load(self.post("/api/mkdir", {"root": rid, "path": "Sub"})), {"path": "Sub"})
            self.assertEqual(json.load(self.post("/api/rename", {"root": rid, "path": "Sub", "name": "Sub2"})),
                             {"path": "Sub2", "dir": True})
            with self.assertRaises(urllib.error.HTTPError) as cm:       # the root: refused, nothing trashed
                self.post("/api/delete", {"root": rid, "path": "", "folder": True})
            self.assertEqual(cm.exception.code, 400)
            self.assertIn("folder itself", json.load(cm.exception)["error"])
            for path, body in (("/api/mkdir", {"root": rid, "path": "X"}), ("/api/rename", {"root": rid, "path": "Sub2", "name": "Y"})):
                with self.assertRaises(urllib.error.HTTPError) as cm:
                    self.post(path, body, page_header=False)
                self.assertEqual(cm.exception.code, 403)
            self.assertEqual(sorted(os.listdir(base)), ["Sub2"])
        finally:
            codeview.STATE.remove_root(rid)
            shutil.rmtree(base, ignore_errors=True)

    def test_locate_is_wired_and_guarded(self):
        """A web page cannot use it to add folders: it needs the page's own header."""
        target = str(codeview.REPO / "AAScratches")
        want = {"root": codeview.STATE.roots()[0]["id"], "path": "AAScratches", "added": False}
        self.assertEqual(json.load(self.post("/api/locate", {"path": target})), want)
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/locate", {"path": target}, page_header=False)
        self.assertEqual(cm.exception.code, 403)
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/locate", {"path": str(codeview.REPO / "no_such_folder")})
        self.assertEqual(cm.exception.code, 400)

    def test_open_local_is_wired_and_guarded(self):
        """A missing path gets open-local's own refusal (400), so no browser ever starts here."""
        body = {"root": codeview.STATE.roots()[0]["id"], "path": "AAScratches/no_such_folder", "browser": "chrome"}
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/open-local", body)
        self.assertEqual(cm.exception.code, 400)
        self.assertIn("not found", json.load(cm.exception)["error"])
        with self.assertRaises(urllib.error.HTTPError) as cm:
            self.post("/api/open-local", body, page_header=False)
        self.assertEqual(cm.exception.code, 403)

    def test_second_launch_opens_the_place_the_running_copy_found(self):
        with mock.patch.object(codeview, "open_page") as opened:
            url = codeview.open_in_running(self.port, str(codeview.REPO / "AAScratches" / "README.md"))
        rid = codeview.STATE.roots()[0]["id"]
        self.assertEqual(url, f"http://127.0.0.1:{self.port}/#/{rid}/AAScratches/README.md")
        opened.assert_called_once_with(url)

    def test_an_older_running_copy_says_to_restart(self):
        old = urllib.error.HTTPError("http://x/api/locate", 404, "Not found", {}, io.BytesIO(b"Not found"))
        with mock.patch.object(urllib.request, "urlopen", side_effect=old), \
                self.assertRaisesRegex(ValueError, "Restart Code Viewer"):
            codeview.locate_running(self.port, "C:\\x")

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


class SweepTest(unittest.TestCase):
    """Start-up clean-up: old helper builds and stranded Run folders go, after an hour, and
    nothing else. Runs on scratch folders, never the real %TEMP% or cache."""

    def setUp(self):
        self.root = pathlib.Path(tempfile.mkdtemp(dir=TMP))
        self.assist, self.temp = self.root / "assist", self.root / "temp"
        self.assist.mkdir()
        self.temp.mkdir()

    def make(self, folder, name, hours_old):
        p = folder / name
        p.mkdir()
        (p / "x.bin").write_bytes(b"0" * 1000)
        t = time.time() - hours_old * 3600
        os.utime(p, (t, t))
        return p

    def test_old_builds_and_stranded_run_folders_go(self):
        self.make(self.assist, codeview.assist_build(), 5)      # the build in use: kept however old
        self.make(self.assist, "0123456789ab", 2)               # an older build: removed
        self.make(self.assist, "ba9876543210", 0)               # another server's, used just now: kept
        self.make(self.temp, "codeview_abc", 2)                 # a Run that never finished: removed
        self.make(self.temp, "codeview_def", 0)                 # a Run going on now: kept
        self.make(self.temp, "other_xyz", 5)                    # not ours: kept
        self.assertEqual(codeview.sweep_leftovers(self.assist, self.temp), (2, 2000))
        self.assertEqual({p.name for p in self.assist.iterdir()}, {codeview.assist_build(), "ba9876543210"})
        self.assertEqual({p.name for p in self.temp.iterdir()}, {"codeview_def", "other_xyz"})

    def test_missing_folders_are_fine(self):
        self.assertEqual(codeview.sweep_leftovers(self.root / "none", self.root / "none2"), (0, 0))


MESSY = '''/*
 * header comment stays as written
 */
import java.util.*;
import java.util.function.Function;
class Messy {
      private static final String DOCUMENT =
  "first part"
   + " second part";
   /** One-line doc stays one line. */
  static int[] dailyTemperatures(int[]temperatures){
int n=temperatures.length;
      int[] result=new int[n];
        Deque<Integer>waiting=new ArrayDeque< >();
   for(int today=0;today<n;today++){
        while(!waiting.isEmpty()&&temperatures[today]>temperatures[waiting.peek()]){
                int colder=waiting.pop();
             result[colder]=today-colder;
        }
          waiting.push(today);
      }
 return result;
  }
    static double cast(int a,int b){ return (double)(a-b)/b; }
  static String kind(int x){
      switch(x){
          case 1:
          return "one";
          default:
          return x>0?"many":"none";
      }
  }
  static int arrow(char c){
    return switch(c){
        case '+'->1;
        default->-1;
    };
  }
  static void collect(Map<String,List<Integer>> map, int remaining, List<Integer> path,
  List<List<Integer>> out){
      for(Map.Entry<String,List<Integer>> e:map.entrySet()){ out.add(e.getValue()); }
      map.forEach((k,v)->{
          path.add(v.size());
      });
      List<Integer> lengths=map.keySet().stream()
      .map(String::length)
      .toList();
  }
  public static void main(String[] args){
      check("case 1 typical",        dailyTemperatures(new int[]{73,74,75}), "[1, 1, 0]");
      check("nested", Arrays.toString(dailyTemperatures(new int[]{1,
      2, 3})), "[1, 1, 0]");
      print("wrapped", find(6,
      new int[][]{{1, 2}, {3, 4}}, 1),
      "[0, 1]");
      int[] arr={-1, +2, ~3};
      if(arr.length>0)System.out.println(arr[0]);
      else
      System.out.println("empty");
  }
  static void check(String label, Object actual, String expected){ }
  static void print(String label, Object actual, String expected){ }
  static int find(int n, int[][] edges, int k){ return n; }
}
'''

# IntelliJ's own formatter (2025.2, `format.bat -s` with "keep simple blocks / multiple
# statements in one line" and Javadoc formatting off) gives exactly this, except for the two
# rules Ravi chose to keep: `{ return n; }` keeps its inner spaces, and the extra spaces after
# "case 1 typical", stay (his test-case columns).
MESSY_FORMATTED = '''/*
 * header comment stays as written
 */

import java.util.*;
import java.util.function.Function;

class Messy {
    private static final String DOCUMENT =
            "first part"
                    + " second part";

    /** One-line doc stays one line. */
    static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> waiting = new ArrayDeque<>();
        for (int today = 0; today < n; today++) {
            while (!waiting.isEmpty() && temperatures[today] > temperatures[waiting.peek()]) {
                int colder = waiting.pop();
                result[colder] = today - colder;
            }
            waiting.push(today);
        }
        return result;
    }

    static double cast(int a, int b) { return (double) (a - b) / b; }

    static String kind(int x) {
        switch (x) {
            case 1:
                return "one";
            default:
                return x > 0 ? "many" : "none";
        }
    }

    static int arrow(char c) {
        return switch (c) {
            case '+' -> 1;
            default -> -1;
        };
    }

    static void collect(Map<String, List<Integer>> map, int remaining, List<Integer> path,
                        List<List<Integer>> out) {
        for (Map.Entry<String, List<Integer>> e : map.entrySet()) { out.add(e.getValue()); }
        map.forEach((k, v) -> {
            path.add(v.size());
        });
        List<Integer> lengths = map.keySet().stream()
                .map(String::length)
                .toList();
    }

    public static void main(String[] args) {
        check("case 1 typical",        dailyTemperatures(new int[]{73, 74, 75}), "[1, 1, 0]");
        check("nested", Arrays.toString(dailyTemperatures(new int[]{1,
                2, 3})), "[1, 1, 0]");
        print("wrapped", find(6,
                        new int[][]{{1, 2}, {3, 4}}, 1),
                "[0, 1]");
        int[] arr = {-1, +2, ~3};
        if (arr.length > 0) System.out.println(arr[0]);
        else
            System.out.println("empty");
    }

    static void check(String label, Object actual, String expected) {}

    static void print(String label, Object actual, String expected) {}

    static int find(int n, int[][] edges, int k) { return n; }
}
'''


@unittest.skipUnless(pathlib.Path(codeview.JAVAC).exists(), "no JDK")
class FormatTest(unittest.TestCase):
    """Ctrl+Alt+L. 5 Oct: measured against IntelliJ's formatter on all 692 AAScratches files,
    as written and with their whitespace scrambled: 686 identical both times; the other 6 only
    keep one-line blocks unsplit (Ravi's "keep my style")."""

    def fmt(self, text, fname="Messy.java"):
        res = codeview.ASSIST.ask("format", text, 0, None, fname, "", "")
        self.assertNotIn("error", res, res)
        return res

    def test_messy_code_comes_out_as_intellij_formats_it(self):
        self.assertEqual(self.fmt(MESSY)["text"], MESSY_FORMATTED)

    def test_every_practice_file_keeps_its_code_and_formats_once(self):
        """Only whitespace may change, and formatting a formatted file changes nothing."""
        files = sorted((codeview.REPO / "AAScratches").rglob("*.java"))
        self.assertGreater(len(files), 500)          # the check is not running on nothing
        changed_code, not_stable = [], []
        for p in files:
            text = p.read_bytes().decode("utf-8")
            once = self.fmt(text, p.name)["text"]
            if re.sub(r"\s+", "", once) != re.sub(r"\s+", "", text):
                changed_code.append(p.name)
            elif self.fmt(once, p.name)["text"] != once:
                not_stable.append(p.name)
        self.assertEqual((changed_code, not_stable), ([], []))

    def test_broken_code_is_refused_with_its_line(self):
        res = self.fmt("class B {\n    void f() {\n        int x = ;\n    }\n}\n", "B.java")
        self.assertIn("line 3", res["reject"])


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

    def test_return_comes_first_at_the_start_of_a_statement(self):
        """5 Oct: `re` at the end of a method listed `record` first (all keywords tied, so A-Z)."""
        res = self.complete(method=f"re{CUR}")
        ranked = sorted((it for it in res["items"] if it["label"].startswith("re")), key=lambda it: it["sort"])
        self.assertEqual(ranked[0]["label"], "return")
        labels = [it["label"] for it in ranked]
        self.assertLess(labels.index("return"), labels.index("record"))

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

    # --- Ctrl+Alt+V introduce variable
    def extract(self, src, fname="DailyTemperatures.java", flags=""):
        """[A] and [B] mark the selection; one ‸ is a bare cursor."""
        if "[A]" in src:
            a = src.index("[A]")
            clean = src.replace("[A]", "", 1)
            b = clean.index("[B]")
            clean = clean.replace("[B]", "", 1)
        else:
            a = b = src.index(CUR)
            clean = src.replace(CUR, "")
        res = codeview.ASSIST.ask("extract", clean, a, None, fname, flags, str(b))
        self.assertNotIn("error", res, res)
        return res, clean

    def daily(self, method="", main=""):
        return DAILY.replace("@@", method).replace("%%", main)

    def test_extract_a_call_into_a_variable_above_its_statement(self):
        res, src = self.extract(self.daily().replace("int colderDay = waiting.pop();", f"int colderDay = waiting.po{CUR}p();"))
        self.assertEqual((res["type"], res["name"], res["expr"]), ("Integer", "pop", "waiting.pop()"))
        line = src.index("                int colderDay")
        self.assertEqual((res["one"]["at"], res["one"]["indent"]), (line, " " * 16))
        self.assertNotIn("all", res)                       # it appears once

    def test_extract_offers_every_occurrence(self):
        src = ("class E {\n    void f(int[] nums) {\n        int a = nums.length * 2;\n"
               "        int b = [A]nums.length[B] + 1;\n    }\n}\n")
        res, clean = self.extract(src, fname="E.java")
        self.assertEqual((res["type"], res["name"]), ("int", "length"))
        self.assertEqual(sorted(map(tuple, res["all"]["spots"])), [(m.start(), m.end()) for m in re.finditer(r"nums\.length", clean)])
        self.assertEqual(res["all"]["at"], clean.index("        int a"))       # before the first one

    def test_extract_at_a_bare_cursor_lists_the_enclosing_expressions(self):
        res, _ = self.extract(self.daily(method=f"int top = Math.max(temperatures[today], temperatures[colder{CUR}Day]) + 1;"))
        texts = [c["text"] for c in res["candidates"]]
        self.assertEqual(texts, ["temperatures[colderDay]", "Math.max(temperatures[today], temperatures[colderDay])",
                                 "Math.max(temperatures[today], temperatures[colderDay]) + 1"])

    def test_extract_names_an_array_element_after_the_array(self):
        res, _ = self.extract(self.daily(method="int warm = [A]temperatures[colderDay][B];"))
        self.assertEqual((res["type"], res["name"]), ("int", "temperature"))

    def test_extract_refuses_a_loop_condition(self):
        """Above the loop it would be computed once, but the condition runs every time round."""
        res, _ = self.extract(self.daily().replace("temperatures[waiting.peek()]", "[A]temperatures[waiting.peek()][B]"))
        self.assertIn("every time round", res["reject"])
        res, _ = self.extract(self.daily().replace("today < n; today++", "today < [A]n + 0[B]; today++"))
        self.assertIn("every time round", res["reject"])

    def test_extract_avoids_a_name_already_taken(self):
        res, _ = self.extract(self.daily(method="int pop = 0;\n                int top = [A]waiting.pop()[B];"))
        self.assertEqual(res["name"], "pop1")

    def test_extract_a_whole_call_statement_becomes_the_declaration(self):
        """`waiting.push(today);` returns the item, so it becomes `Integer push = waiting.push(today);`."""
        res, src = self.extract(self.daily().replace("waiting.push(today);", "[A]waiting.push(today)[B];"))
        start = src.index("waiting.push(today);")
        self.assertEqual(res["statement"], [start, start + len("waiting.push(today);")])
        self.assertEqual((res["type"], res["name"]), ("Integer", "push"))

    def test_extract_refuses_what_cannot_be_a_variable(self):
        res, _ = self.extract(self.daily().replace('System.out.println(label + ": "', '[A]System.out.println(label + ": " + Arrays.toString(actual))[B]//'))
        self.assertIn("void", res["reject"])
        res, _ = self.extract(self.daily(method="for (int i = 0; i < n; i++) result[i] = [A]i * 2[B];"))
        self.assertIn("`i`", res["reject"])                     # i only exists inside the loop
        res, _ = self.extract(self.daily().replace("result[colderDay] = today", "[A]result[colderDay][B] = today"))
        self.assertIn("left side", res["reject"])

    def test_extract_imports_the_type(self):
        res, src = self.extract(f"class E {{\n    void f() {{\n        Object o = [A]java.util.List.of(1, 2)[B];\n    }}\n}}\n", fname="E.java")
        self.assertEqual((res["type"], res["name"]), ("List<Integer>", "of"))
        self.assertEqual(res["imports"], [{"text": "import java.util.List;\n\n", "at": 0}])
        res, _ = self.extract(f"void main() {{\n    Object o = [A]java.util.List.of(1, 2)[B];\n}}\n", fname="Scratch.java", flags="c")
        self.assertEqual(res["imports"], [])                    # a compact file imports java.base itself

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
