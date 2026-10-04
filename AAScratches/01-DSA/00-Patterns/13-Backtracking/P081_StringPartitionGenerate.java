/*
 * =====================================================================
 *  P081 Backtracking on Strings: Partition and Generate   Canonical LC 131 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 131, Palindrome Partitioning)
 *   Split s into pieces so every piece is a palindrome. Return every such split.
 *
 * EXAMPLE
 *   "aab"  ->  [["a","a","b"], ["aa","b"]]
 *   "a"    ->  [["a"]]
 *
 * RECOGNIZE WHEN
 *   - "all ways to split / cut / segment a string" where each piece must be valid
 *     (palindrome, IP octet, dictionary word).
 *   - "generate all valid strings" of a given shape (balanced parentheses, phone keypad
 *     letters, expressions with operators).
 *   Not this if: you only COUNT the ways or need the minimum cuts -> DP (P083_StringPrefixDp,
 *   P090_PalindromeDp).
 *
 * TEMPLATE
 *   partition: backtrack(start, parts):
 *                  if start == n: record parts
 *                  for end in start+1..n: piece = s[start:end]
 *                      if valid(piece): parts.add(piece); backtrack(end); parts.removeLast()
 *   generate:  backtrack(built, counters):
 *                  if complete: record
 *                  for each allowed next char (decided by counters): append, recurse, remove
 *
 * APPROACH
 *   1. From `start`, try every end position.
 *   2. If s[start..end) is a palindrome, keep it and partition the rest.
 *   3. Remove it and try the next end.
 *
 * KEY INSIGHT
 *   A split is a sequence of cut positions, so backtracking over "where does the next piece
 *   end?" enumerates every split exactly once. Generation problems work the same way, with
 *   simple counters (open / close brackets used) deciding which characters are legal next,
 *   so invalid strings are never even built.
 *
 * COMPLEXITY
 *   LC 131: O(n * 2^n). LC 22: O(4^n / sqrt(n)) (the Catalan number of results).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 93   Restore IP Addresses     exactly 4 pieces of 1-3 digits, 0..255, no
 *                                            leading zero
 *   [coded] LC 17   Letter Combinations      one level per digit; branch over its letters
 *   [coded] LC 22   Generate Parentheses     add '(' while open < n; ')' while close < open
 *           LC 140  Word Break II            pieces must be dictionary words (memoise ends)
 *           LC 282  Expression Add Operators carry value and last operand for '*' precedence
 *           LC 301  Remove Invalid Parens    BFS / backtracking over removals, dedupe
 *           LC 1593 Max Unique Substrings    pieces must not repeat: a set of used pieces
 *
 * PITFALLS
 *   - Copy the parts list when recording.
 *   - LC 93: "0" is a valid octet, "01" is not; cap the piece length at 3.
 *   - LC 17: an empty input returns [], not [""].
 *
 * DEEP DIVE
 *   C15_PalindromePartitioning (12-Dynamic-Programming), C01_GenerateParenthesis,
 *   B02_LetterCombinations, D04_ExpressionAddOperators (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.List;

class StringPartitionGenerate {

    // Canonical LC 131.
    static List<List<String>> partition(String s) {
        List<List<String>> out = new ArrayList<>();
        cut(s, 0, new ArrayList<>(), out);
        return out;
    }

    private static void cut(String s, int start, List<String> parts, List<List<String>> out) {
        if (start == s.length()) {
            out.add(new ArrayList<>(parts));
            return;
        }
        for (int end = start + 1; end <= s.length(); end++) {
            if (isPalindrome(s, start, end - 1)) {
                parts.add(s.substring(start, end));
                cut(s, end, parts, out);
                parts.remove(parts.size() - 1);
            }
        }
    }

    private static boolean isPalindrome(String s, int i, int j) {
        while (i < j) {
            if (s.charAt(i++) != s.charAt(j--)) {
                return false;
            }
        }
        return true;
    }

    // LC 93.
    static List<String> restoreIpAddresses(String s) {
        List<String> out = new ArrayList<>();
        octets(s, 0, new ArrayList<>(), out);
        return out;
    }

    private static void octets(String s, int start, List<String> parts, List<String> out) {
        if (parts.size() == 4) {
            if (start == s.length()) {
                out.add(String.join(".", parts));
            }
            return;
        }
        for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
            String piece = s.substring(start, start + len);
            if ((piece.length() > 1 && piece.charAt(0) == '0') || Integer.parseInt(piece) > 255) {
                continue;
            }
            parts.add(piece);
            octets(s, start + len, parts, out);
            parts.remove(parts.size() - 1);
        }
    }

    private static final String[] KEYS =
            {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    // LC 17.
    static List<String> letterCombinations(String digits) {
        List<String> out = new ArrayList<>();
        if (!digits.isEmpty()) {
            spell(digits, 0, new StringBuilder(), out);
        }
        return out;
    }

    private static void spell(String digits, int i, StringBuilder sb, List<String> out) {
        if (i == digits.length()) {
            out.add(sb.toString());
            return;
        }
        for (char c : KEYS[digits.charAt(i) - '0'].toCharArray()) {
            sb.append(c);
            spell(digits, i + 1, sb, out);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    // LC 22.
    static List<String> generateParenthesis(int n) {
        List<String> out = new ArrayList<>();
        brackets(n, 0, 0, new StringBuilder(), out);
        return out;
    }

    private static void brackets(int n, int open, int close, StringBuilder sb, List<String> out) {
        if (sb.length() == 2 * n) {
            out.add(sb.toString());
            return;
        }
        if (open < n) {
            sb.append('(');
            brackets(n, open + 1, close, sb, out);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (close < open) {
            sb.append(')');
            brackets(n, open, close + 1, sb, out);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 131 aab", partition("aab"), "[[a, a, b], [aa, b]]");
        check("LC 131 a", partition("a"), "[[a]]");
        check("LC 131 abba", partition("abba"), "[[a, b, b, a], [a, bb, a], [abba]]");

        check("LC 93 25525511135",
                restoreIpAddresses("25525511135"), "[255.255.11.135, 255.255.111.35]");
        check("LC 93 0000", restoreIpAddresses("0000"), "[0.0.0.0]");
        check("LC 93 101023", restoreIpAddresses("101023"),
                "[1.0.10.23, 1.0.102.3, 10.1.0.23, 10.10.2.3, 101.0.2.3]");

        check("LC 17 23", letterCombinations("23"), "[ad, ae, af, bd, be, bf, cd, ce, cf]");
        check("LC 17 empty", letterCombinations(""), "[]");
        check("LC 17 2", letterCombinations("2"), "[a, b, c]");

        check("LC 22 n=3", generateParenthesis(3), "[((())), (()()), (())(), ()(()), ()()()]");
        check("LC 22 n=1", generateParenthesis(1), "[()]");
    }
}
