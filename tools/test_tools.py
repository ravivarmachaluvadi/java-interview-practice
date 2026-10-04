"""
Tests for the repo's command-line checkers: runjava's main() detection and verify_all's
"expected" comparison.

    python tools/test_tools.py
"""
import pathlib, sys, unittest

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

import javasrc  # noqa: E402
import runjava  # noqa: E402
import verify_all  # noqa: E402


class MainDetectionTest(unittest.TestCase):
    def test_brace_char_literals_do_not_hide_main(self):
        src = ("class Brackets {\n"
               "    static boolean opener(char c) { return c == '(' || c == '[' || c == '{'; }\n"
               "    public static void main(String[] args) { }\n"
               "}\n")
        self.assertEqual(runjava.classes_with_main(src), ["Brackets"])

    def test_braces_in_strings_and_comments_do_not_hide_main(self):
        src = ("class Text {\n"
               "    static String s = \"}}}\";   // a stray { in a comment\n"
               "    /* and } in a block comment */\n"
               "    public static void main(String[] args) { }\n"
               "}\n")
        self.assertEqual(runjava.classes_with_main(src), ["Text"])

    def test_main_in_a_second_top_level_class_is_found(self):
        src = "class Helper { }\nclass Runner {\n    public static void main(String[] a) { }\n}\n"
        self.assertEqual(runjava.classes_with_main(src), ["Runner"])

    def test_pattern_file_prefix_picks_the_named_class(self):
        self.assertEqual(javasrc.pick_main(["Helper", "VariableWindowLongest"],
                                           "P017_VariableWindowLongest.java"), "VariableWindowLongest")
        self.assertEqual(javasrc.pick_main(["Helper", "TwoSum"], "A02_TwoSum.java"), "TwoSum")


class ExpectationCheckTest(unittest.TestCase):
    """Each line below is a real output style from the repo; none of them is a mismatch."""

    AGREEING = [
        "case 1 typical  : [0, 0, 3, 0, 2]   expected [0, 0, 3, 0, 2]   (input restored)",
        "case 6 NO majority      : 3   expected 3 (meaningless)",
        "case 1 typical   : index 2   expected 2 | distance 2   expected 2",
        "case 1 present          : binary true, staircase true   expected true (both)",
        "case 1 typical      : 2   expected 2   (1 + 1)",
        "case 1 meet at root : twoRunner=3 set=3 byDepth=3   expected 3 from all three",
        "preorder empty serialize : #,   expected #,",
        "case 1 path 1-2-3         -> DFS false, BFS false   expected false, false",
        "case 1 five vertices, src 0 : PQ [0, 8, 6, 5, 3], TreeSet [0, 8, 6, 5, 3]   expected [0, 8, 6, 5, 3] for both",
        "case 1 chain [[1],[2],[3],[]]: recursive=true iterative=true   expected true true",
        "case 1 (typical):   [-3, 1, 2, -2, 4]  ->  two-pointer 8, brute force 8, input unchanged true   expected 8, 8, true",
        "get(1) -> actual 1   expected 1   OK",
        "case 3a: iteration order [10, 7, 9, 1, 5]   expected [10, 7, 9, 1, 5] - raw heap array, NOT sorted",
        "case 2 paypal: 1500.00 paid using PayPal   expected 1500.00 paid using PayPal   [OK]",
        "case 5 overflowing int form   : -728379968   expected -728379968  (the bug this file fixes)",
        "case 2: [\"a  \"]   expected [\"a  \"]   match=true",
        "both=2 tie       max -> actual 'leet'   expected 'hello' or 'leet'   OK",
        "read missing file -> actual    expected    OK",            # empty string expected
    ]

    NOT_CHECKABLE = [
        "expected: Animal makes a sound",                                     # Tricky-MCQ answer
        "--- the buggy variant: expected value is its KNOWN WRONG output ---",  # a heading
        "case 2 plain int       : 82364   expected: almost never 0 - lost updates from the data race",
        "run 2 two monitors - static counter  : 99923   expected: usually < 100000 (lost updates)",
        "case 4 address count: 2   expected fewer than 6 (CAS-free read-modify-write drops updates)",
    ]

    # Falsifiers: real mismatches must still be reported.
    DISAGREEING = [
        "case 1: 3   expected 4",
        "case 2 typical: [1, 9, 2, 3]   expected [1, 2, 3]",
        "case 3: true   expected false (both)",
        "case 4 typical   : index 2   expected 2 | distance 3   expected 2",
        "case 5 -> DFS true, BFS false   expected false, false",
        "case 6 serialize : 1,2,   expected 1,3,",
        "case 7: answer=5   expected 6 for both",
        "tie max -> actual 'world'   expected 'hello' or 'leet'   OK",
    ]

    def test_agreeing_lines_pass(self):
        for line in self.AGREEING:
            self.assertEqual(verify_all.check_expectations(line), [], line)

    def test_descriptive_lines_are_skipped(self):
        for line in self.NOT_CHECKABLE:
            self.assertEqual(verify_all.check_expectations(line), [], line)

    def test_real_mismatches_are_still_reported(self):
        for line in self.DISAGREEING:
            self.assertNotEqual(verify_all.check_expectations(line), [], line)

    def test_every_line_of_a_multi_line_output_is_checked(self):
        out = "case 1: 3   expected 3\ncase 2: 5   expected 6\n"
        self.assertEqual(len(verify_all.check_expectations(out)), 1)


if __name__ == "__main__":
    unittest.main()
