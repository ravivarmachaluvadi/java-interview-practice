/*
 * =====================================================================
 *  P016 Fixed Window + Frequency Match   Canonical LC 438 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 438, Find All Anagrams in a String)
 *   Return every start index i such that s[i .. i + |p| - 1] is an anagram of p.
 *   Both strings hold lowercase English letters.
 *
 * EXAMPLE
 *   s = "cbaebabacd", p = "abc"  ->  [0, 6]
 *   s = "abab",       p = "ab"   ->  [0, 1, 2]
 *
 * RECOGNIZE WHEN
 *   - "anagram / permutation of p appears in s": a window of length |p| whose letter
 *     COUNTS equal p's counts.
 *   - "k-length substrings with all distinct characters", "almost unique subarrays".
 *   Not this if: the window length is not fixed (e.g. "smallest window containing all of
 *   t") -> P018_VariableWindowShortest; you only compare whole strings -> sort or count once
 *   (P021_FrequencyCanonicalKey).
 *
 * TEMPLATE
 *   need[c] = count of c in p;  unbalanced = number of distinct letters in p
 *   for i in 0..n-1:
 *       need[s[i]]--                  // entering: if it lands on 0, unbalanced--;
 *                                     //           if it leaves 0, unbalanced++
 *       if i >= m: need[s[i - m]]++   // leaving: the same two adjustments
 *       if i >= m - 1 and unbalanced == 0: record i - m + 1
 *
 * APPROACH
 *   1. need[] starts as p's counts. Each character entering the window decrements its
 *      need, each one leaving increments it back.
 *   2. Track how many letters have need exactly 0; when all distinct letters of p are at 0
 *      the window is an anagram.
 *
 * KEY INSIGHT
 *   Comparing two count arrays costs O(26) per step; a single "letters still unbalanced"
 *   counter updated only when a count crosses zero makes each slide O(1). Every window
 *   has the same length, so the counts alone decide equality.
 *
 * COMPLEXITY
 *   Time O(n + m), space O(1) (26 counters).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 567  Permutation in String    LC 438 that stops at the first match
 *   [coded] LC 1876 Distinct Substrings of 3 window of 3 is good when no count exceeds 1
 *           LC 1100 K-Length No Repeats      LC 1876 with any k (track "dups in window")
 *           LC 2841 Almost Unique Subarray   fixed window + map size >= m + running sum
 *           LC 30   Concatenation of Words   window moves in word-sized steps; one pass
 *                                            per offset 0..wordLen-1
 *           LC 187  Repeated DNA Sequences   set of windows of length 10 -> P107_RollingHash
 *
 * PITFALLS
 *   - Count "unbalanced letters", not characters; a letter can be over-supplied.
 *   - Remove the leaving character only once the window is longer than m.
 *   - |p| > |s| means no answer; guard before indexing.
 *
 * DEEP DIVE
 *   A04_AnagramStrings (04-Strings), C04_RepeatedDnaSequences (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.List;

class FixedWindowFrequencyMatch {

    // Canonical LC 438.
    static List<Integer> findAnagrams(String s, String p) {
        List<Integer> out = new ArrayList<>();
        int m = p.length();
        if (m > s.length()) {
            return out;
        }
        int[] need = new int[26];
        int unbalanced = 0;                      // letters whose need is not exactly 0
        for (char c : p.toCharArray()) {
            if (need[c - 'a']++ == 0) {
                unbalanced++;
            }
        }
        for (int i = 0; i < s.length(); i++) {
            int in = s.charAt(i) - 'a';
            need[in]--;
            if (need[in] == 0) {
                unbalanced--;
            } else if (need[in] == -1) {
                unbalanced++;                    // went from balanced to over-supplied
            }
            if (i >= m) {
                int out1 = s.charAt(i - m) - 'a';
                need[out1]++;
                if (need[out1] == 0) {
                    unbalanced--;
                } else if (need[out1] == 1) {
                    unbalanced++;
                }
            }
            if (i >= m - 1 && unbalanced == 0) {
                out.add(i - m + 1);
            }
        }
        return out;
    }

    // LC 567: is some permutation of s1 a substring of s2?
    static boolean checkInclusion(String s1, String s2) {
        return !findAnagrams(s2, s1).isEmpty();
    }

    // LC 1876: count windows of length 3 with no repeated character.
    static int countGoodSubstrings(String s) {
        int[] count = new int[26];
        int repeated = 0;                        // letters with count >= 2 in the window
        int good = 0;
        for (int i = 0; i < s.length(); i++) {
            if (++count[s.charAt(i) - 'a'] == 2) {
                repeated++;
            }
            if (i >= 3 && --count[s.charAt(i - 3) - 'a'] == 1) {
                repeated--;
            }
            if (i >= 2 && repeated == 0) {
                good++;
            }
        }
        return good;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 438 cbaebabacd / abc", findAnagrams("cbaebabacd", "abc"), "[0, 6]");
        check("LC 438 abab / ab", findAnagrams("abab", "ab"), "[0, 1, 2]");
        check("LC 438 p longer than s", findAnagrams("a", "ab"), "[]");
        check("LC 438 repeated letters aab", findAnagrams("baabaa", "aab"), "[0, 1, 2, 3]");

        check("LC 567 ab in eidbaooo", checkInclusion("ab", "eidbaooo"), true);
        check("LC 567 ab in eidboaoo", checkInclusion("ab", "eidboaoo"), false);

        check("LC 1876 xyzzaz", countGoodSubstrings("xyzzaz"), 1);
        check("LC 1876 aababcabc", countGoodSubstrings("aababcabc"), 4);
        check("LC 1876 too short", countGoodSubstrings("ab"), 0);
    }
}
