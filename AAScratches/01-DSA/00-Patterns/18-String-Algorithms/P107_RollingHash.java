/*
 * =====================================================================
 *  P107 Rolling Hash (Rabin-Karp)   Canonical LC 187 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 187, Repeated DNA Sequences)
 *   s holds only A, C, G, T. Return every 10-letter substring that occurs more than once.
 *
 * EXAMPLE
 *   "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"  ->  [AAAAACCCCC, CCCCCAAAAA]
 *   "AAAAAAAAAAAAA"                     ->  [AAAAAAAAAA]
 *
 * RECOGNIZE WHEN
 *   - Compare MANY substrings of the same length: duplicates, "does any length-L substring
 *     appear twice", pattern search where hashing is easier than KMP.
 *   - Combined with binary search over the length ("longest duplicate substring").
 *   Not this if: one pattern in one text -> P106_KmpPrefixFunction (no collisions to
 *   handle); the window is small and the alphabet tiny -> a fixed window of counts
 *   (P016_FixedWindowFrequencyMatch).
 *
 * TEMPLATE
 *   h(window) = sum of code(c_i) * B^(L - 1 - i)  (mod M)
 *   slide:  h = (h - code(out) * B^(L - 1)) * B + code(in)   (mod M, keep it non-negative)
 *   a hash match is only a CANDIDATE: confirm with an equality check (or use two moduli)
 *   tiny alphabets: pack exactly (2 bits per DNA letter) and there are no collisions at all
 *
 * APPROACH
 *   1. Encode A, C, G, T as 0..3; a 10-letter window is a 20-bit integer.
 *   2. Slide: shift left 2 bits, add the new letter, mask to 20 bits.
 *   3. A window seen for the second time is reported once.
 *
 * KEY INSIGHT
 *   Sliding a hash costs O(1) per step because the new hash is the old one minus the
 *   leaving character plus the entering one. Comparing hashes instead of strings turns
 *   O(n * L) substring work into O(n). For DNA the "hash" is exact, so no checking is needed.
 *
 * COMPLEXITY
 *   Time O(n) per window length (O(n log n) with the binary search), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1044 Longest Duplicate Substring  binary search the length; Rabin-Karp per
 *                                            length; confirm candidates by comparing strings
 *   [coded] LC 28 by Rabin-Karp              hash the needle, slide over the haystack
 *           LC 1316 Distinct Echo Substrings for each half length, compare hashes of halves
 *           LC 718  Longest Repeated Subarray binary search + rolling hash (or DP)
 *           LC 1923 Longest Common Subpath   binary search + hash sets per path
 *
 * PITFALLS
 *   - Negative values after subtraction: add M before taking % M.
 *   - Overflow: keep every product below 2^63 (base and modulus around 1e9 fit in long).
 *   - Collisions are rare but possible: verify, or use two independent hashes.
 *
 * DEEP DIVE
 *   C02_RabinKarpAlgorithm (18-Sorting-Searching-Algorithms),
 *   C04_RepeatedDnaSequences (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RollingHash {

    static final long MOD = 1_000_000_007L;
    static final long BASE = 131;

    // Canonical LC 187: exact 2-bit packing, so no collisions.
    static List<String> findRepeatedDnaSequences(String s) {
        Map<Character, Integer> code = Map.of('A', 0, 'C', 1, 'G', 2, 'T', 3);
        Set<Integer> seen = new HashSet<>();
        Set<String> repeated = new HashSet<>();
        int window = 0;
        int mask = (1 << 20) - 1;                  // 10 letters x 2 bits
        for (int i = 0; i < s.length(); i++) {
            window = ((window << 2) | code.get(s.charAt(i))) & mask;
            if (i >= 9 && !seen.add(window)) {
                repeated.add(s.substring(i - 9, i + 1));
            }
        }
        List<String> out = new ArrayList<>(repeated);
        Collections.sort(out);                     // any order is accepted
        return out;
    }

    // LC 1044: longest substring that occurs at least twice (may overlap).
    static String longestDupSubstring(String s) {
        int lo = 1;
        int hi = s.length() - 1;
        String best = "";
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            String found = duplicateOfLength(s, mid);
            if (found != null) {
                best = found;                      // length mid works; try longer
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return best;
    }

    // Some substring of length len that appears twice, or null.
    private static String duplicateOfLength(String s, int len) {
        long power = 1;
        for (int i = 1; i < len; i++) {
            power = power * BASE % MOD;
        }
        Map<Long, List<Integer>> starts = new HashMap<>();
        long h = 0;
        for (int i = 0; i < s.length(); i++) {
            if (i >= len) {
                h = (h - s.charAt(i - len) * power % MOD + MOD) % MOD;   // drop the leaving char
            }
            h = (h * BASE + s.charAt(i)) % MOD;
            if (i >= len - 1) {
                int start = i - len + 1;
                List<Integer> same = starts.computeIfAbsent(h, k -> new ArrayList<>());
                for (int other : same) {
                    if (s.regionMatches(other, s, start, len)) {
                        return s.substring(start, start + len);       // confirmed, not a collision
                    }
                }
                same.add(start);
            }
        }
        return null;
    }

    // LC 28 with Rabin-Karp.
    static int strStr(String text, String p) {
        int m = p.length();
        if (m == 0) {
            return 0;
        }
        if (m > text.length()) {
            return -1;
        }
        long power = 1;
        for (int i = 1; i < m; i++) {
            power = power * BASE % MOD;
        }
        long target = 0;
        for (int i = 0; i < m; i++) {
            target = (target * BASE + p.charAt(i)) % MOD;
        }
        long h = 0;
        for (int i = 0; i < text.length(); i++) {
            if (i >= m) {
                h = (h - text.charAt(i - m) * power % MOD + MOD) % MOD;
            }
            h = (h * BASE + text.charAt(i)) % MOD;
            if (i >= m - 1 && h == target && text.startsWith(p, i - m + 1)) {
                return i - m + 1;
            }
        }
        return -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 187 two repeats", findRepeatedDnaSequences("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"),
                "[AAAAACCCCC, CCCCCAAAAA]");
        check("LC 187 overlapping repeat",
                findRepeatedDnaSequences("AAAAAAAAAAAAA"), "[AAAAAAAAAA]");
        check("LC 187 too short", findRepeatedDnaSequences("ACGT"), "[]");

        check("LC 1044 banana", longestDupSubstring("banana"), "ana");
        check("LC 1044 abcd none", "[" + longestDupSubstring("abcd") + "]", "[]");
        check("LC 1044 aaaaa overlapping", longestDupSubstring("aaaaa"), "aaaa");

        check("LC 28 Rabin-Karp sadbutsad", strStr("sadbutsad", "sad"), 0);
        check("LC 28 Rabin-Karp leeto", strStr("leetcode", "leeto"), -1);
        check("LC 28 Rabin-Karp aaaaab", strStr("aaaaab", "aab"), 3);
    }
}
