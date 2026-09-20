/*
 * =====================================================================
 *  Rabin-Karp: find all occurrences of a pattern   LeetCode 28 family | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a text txt of length m and a pattern pat of length n, return every
 *   start index i where txt.substring(i, i + n) equals pat. Occurrences may
 *   overlap. If pat is empty or longer than txt, return an empty list.
 *
 * EXAMPLE
 *   txt = "ababcababcabc", pat = "abc"  ->  [2, 7, 10]
 *   txt = "aaaa",          pat = "aa"   ->  [0, 1, 2]   overlapping matches
 *   txt = "ab",            pat = "abc"  ->  []          pattern longer than text
 *
 * APPROACH  (rolling hash, then verify)
 *   1. Map each character to a small number ('a' -> 1) and hash a window as a
 *      polynomial:  h = sum over k of  c[k] * p^k  (mod 101).
 *   2. Hash the pattern and the first window of the text with the same powers.
 *   3. Slide the window right by one. Instead of dividing the text hash by p
 *      (division is awkward under a modulus), this code multiplies the PATTERN
 *      hash by p. Window i has hash sum c[i+k] * p^(i+k), and the pattern hash
 *      after i shifts is (sum pat[k] * p^k) * p^i, so the two stay comparable.
 *   4. Rolling step: subtract the outgoing char times p^i (pLeft), add the
 *      incoming char times p^(i+n) (pRight), keep everything mod 101.
 *   5. Equal hashes only mean "maybe". Confirm with a real string compare
 *      before recording the index, so a hash collision costs time, not correctness.
 *
 * KEY INSIGHT
 *   A rolling hash turns "compare n characters at every position" into O(1) per
 *   position: leaving and entering characters are the only difference between
 *   neighbouring windows. The hash is a filter, not a proof - always verify.
 *   Recognise this pattern for repeated-substring, duplicate-document and
 *   longest-duplicate-substring (binary search + rolling hash) problems.
 *
 * COMPLEXITY
 *   Time  O(n + m) expected; O(n * m) worst case when every window collides
 *         and the verification compare runs at each position.
 *   Space O(1) besides the output list.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why verify after a hash match? What does an adversarial input do here?
 *   - Why a large prime modulus (1e9+7) and a random base in production?
 *   - How would you search many patterns at once? (hash all patterns of one
 *     length into a set - the Rabin-Karp multi-pattern trick)
 *   - Compare with KMP: guaranteed O(n + m) but no hashing (see D02).
 *
 * RUN
 *   main() runs 4 cases (typical, overlapping, no match, pattern longer than
 *   text) and prints actual vs expected.
 *
 * Fixed: a pattern longer than the text crashed with StringIndexOutOfBounds
 *        in the initial hashing loop; guarded up front.
 */

import java.util.ArrayList;
import java.util.List;

class RabinKarpAlgorithm {

    private static final int BASE = 7;    // polynomial base
    private static final int MOD = 101;   // small prime; verification covers collisions

    // Returns every start index in txt where pat occurs (overlaps included).
    public List<Integer> search(String pat, String txt) {
        List<Integer> ans = new ArrayList<>();
        int n = pat.length();
        int m = txt.length();
        if (n == 0 || n > m) return ans; // nothing to search for / cannot fit

        int hashPat = 0, hashText = 0;
        int pLeft = 1;   // p^i  - weight of the character leaving the window
        int pRight = 1;  // p^(i+n) - weight of the character entering the window

        // Hash the pattern and the first window with the same weights p^0..p^(n-1).
        for (int i = 0; i < n; i++) {
            hashPat = (hashPat + charValue(pat.charAt(i)) * pRight) % MOD;
            hashText = (hashText + charValue(txt.charAt(i)) * pRight) % MOD;
            pRight = (pRight * BASE) % MOD;
        }

        for (int i = 0; i <= m - n; i++) {
            // Equal hashes are only a hint - confirm the characters really match.
            if (hashPat == hashText && txt.startsWith(pat, i)) {
                ans.add(i);
            }

            if (i < m - n) {
                // Drop txt[i] (weight p^i), take in txt[i + n] (weight p^(i+n)).
                hashText = (hashText - (charValue(txt.charAt(i)) * pLeft) % MOD + MOD) % MOD;
                hashText = (hashText + charValue(txt.charAt(i + n)) * pRight) % MOD;

                // Shift the pattern hash up by one power instead of dividing the
                // text hash down by one - same comparison, no modular inverse.
                hashPat = (hashPat * BASE) % MOD;

                pLeft = (pLeft * BASE) % MOD;
                pRight = (pRight * BASE) % MOD;
            }
        }
        return ans;
    }

    // 'a' -> 1, 'b' -> 2, ... any char works; only consistency matters.
    private static int charValue(char c) {
        return c - 'a' + 1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        RabinKarpAlgorithm rk = new RabinKarpAlgorithm();

        print("case 1 (typical)   ", rk.search("abc", "ababcababcabc"), "[2, 7, 10]");
        print("case 2 (overlap)   ", rk.search("aa", "aaaa"), "[0, 1, 2]");
        print("case 3 (no match)  ", rk.search("xyz", "ababcababcabc"), "[]");
        print("case 4 (pat > txt) ", rk.search("abc", "ab"), "[]");
    }
}
