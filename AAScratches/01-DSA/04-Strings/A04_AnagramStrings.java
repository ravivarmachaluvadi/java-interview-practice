/*
 * =====================================================================
 *  Valid Anagram                                  LeetCode 242 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given two strings s and t of lowercase letters a-z, return true if t is a rearrangement
 *   of s (same letters, same multiplicities). Different lengths can never be anagrams.
 *
 * EXAMPLE
 *   "integer", "tegerni"  ->  true
 *   "rat", "car"          ->  false     same length, different letters
 *   "aacc", "ccac"        ->  false     same letters, different counts
 *   "", ""                ->  true      two empty strings
 *   "a", "ab"             ->  false     length check short-circuits
 *
 * APPROACH  (int[26] frequency counting)
 *   1. If lengths differ, return false immediately.
 *   2. One int[26] array: ++ for every char of s, -- for every char of t.
 *   3. If every slot is back at zero the multisets match, so the strings are anagrams.
 *
 *   Second method, isAnagramBySorting: sort both char arrays and compare. Simpler to say,
 *   O(n log n), and it is the canonical-form idea that "group anagrams" builds on.
 *
 * KEY INSIGHT
 *   Increment for one side, decrement for the other, assert all zero. One array does the job
 *   of two, and the same three-line loop reappears in permutation-in-string, minimum window,
 *   and every "same characters?" question. Recite it.
 *
 * COMPLEXITY
 *   anagramStrings     Time O(n)        one pass over each string plus a fixed 26-slot scan
 *                      Space O(1)       the array is 26 ints regardless of input size
 *   isAnagramBySorting Time O(n log n)  two sorts dominate
 *                      Space O(n)       the two char[] copies from toCharArray
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unicode input: replace int[26] with a HashMap<Character,Integer> (or int[128] for ASCII).
 *   - Group anagrams (LeetCode 49): sorted string or the count array as the map key.
 *   - Find all anagram start indices in a text (LeetCode 438): sliding window, same array.
 *   - Case/whitespace insensitivity: normalise before counting.
 *
 * RUN
 *   main() runs 5 cases (typical, different letters, different counts, empty, length mismatch)
 *   and prints actual vs expected for both methods.
 */
import java.util.Arrays;

class AnagramStrings {

    /** Author's approach: one frequency array, ++ for s, -- for t, all zero means anagram. */
    public boolean anagramStrings(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] count = new int[26];                      // assumes lowercase a-z only

        for (char c : s.toCharArray()) count[c - 'a']++;
        for (char c : t.toCharArray()) count[c - 'a']--;
        for (int i : count) {
            // t had more (i<0) or fewer (i>0) of a letter
            if (i != 0) return false;
        }
        return true;
    }

    /** Alternative: canonical form by sorting. O(n log n) but no alphabet assumption. */
    public boolean isAnagramBySorting(String s, String t) {
        if (s.length() != t.length()) return false;
        char[] a = s.toCharArray();
        char[] b = t.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    private static void check(AnagramStrings sol, String label, String s, String t,
            boolean expected) {
        System.out.println(label + " count : " + sol.anagramStrings(s, t)
                + "   expected " + expected);
        System.out.println(label + " sort  : " + sol.isAnagramBySorting(s, t)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        AnagramStrings sol = new AnagramStrings();
        check(sol, "case 1 (typical)           ", "integer", "tegerni", true);
        check(sol, "case 2 (different letters) ", "rat", "car", false);
        check(sol, "case 3 (different counts)  ", "aacc", "ccac", false);
        check(sol, "case 4 (both empty)        ", "", "", true);
        check(sol, "case 5 (length mismatch)   ", "a", "ab", false);
    }
}
