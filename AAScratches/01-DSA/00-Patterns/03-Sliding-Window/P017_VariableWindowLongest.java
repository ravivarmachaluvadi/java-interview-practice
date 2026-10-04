/*
 * =====================================================================
 *  P017 Variable Sliding Window: Longest Valid   Canonical LC 3 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 3, Longest Substring Without Repeating Characters)
 *   Return the length of the longest substring of s in which no character repeats.
 *
 * EXAMPLE
 *   "abcabcbb"  ->  3     "abc"
 *   "bbbbb"     ->  1
 *   "pwwkew"    ->  3     "wke" (a substring, not a subsequence)
 *   "abba"      ->  2     the trap for the "jump left" version
 *
 * RECOGNIZE WHEN
 *   - "longest / maximum length substring or subarray such that <condition>".
 *   - The condition is MONOTONIC: if a window is valid, every smaller window inside it is
 *     valid too (at most k distinct, at most k zeros, no repeats, sum <= k with positives).
 *   - Budget words: "at most k changes / flips / deletions / distinct".
 *   Not this if: values can be negative and the condition is a sum -> P002_PrefixSumHashMap;
 *   you want the SHORTEST valid window -> P018_VariableWindowShortest; you must COUNT
 *   windows -> P019_CountSubarraysAtMost.
 *
 * TEMPLATE
 *   left = 0
 *   for right in 0..n-1:
 *       add a[right] to the window state
 *       while window is invalid:              // shrink until valid again
 *           remove a[left] from the state; left++
 *       best = max(best, right - left + 1)    // every valid window is a candidate
 *
 * APPROACH
 *   1. The window state is a count per character.
 *   2. Adding s[right] may create a repeat; shrink from the left until that count is 1.
 *   3. The window [left, right] is now repeat-free; record its length.
 *
 * KEY INSIGHT
 *   Because validity is monotonic, once [left, right] is invalid, every window that starts
 *   at left and ends later is invalid too, so left can move forward for good. Both
 *   pointers only move right, so the whole scan is O(n) even with the inner while loop.
 *
 * COMPLEXITY
 *   Time O(n) (each index enters and leaves once), space O(alphabet) for the counts.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 340  At Most K Distinct       invalid while distinct > k
 *   [coded] LC 904  Fruit Into Baskets       LC 340 with k = 2, on an int array
 *   [coded] LC 1004 Max Consecutive Ones III invalid while zeros > k
 *   [coded] LC 424  Char Replacement         invalid while (len - maxFreq) > k; maxFreq
 *                                            never needs to decrease
 *           LC 1493 Longest After Deleting One  LC 1004 with k = 1, answer - 1
 *           LC 2024 Maximize Confusion       LC 1004 run twice (flip T's, flip F's)
 *           LC 1208 Equal Substrings Budget  invalid while cost sum > maxCost
 *           LC 1838 Most Frequent Element    sort; invalid while target * len - sum > k
 *           LC 2958 At Most K Frequency      invalid while count[a[right]] > k
 *           LC 1695 Max Erasure Value        LC 3 on an int array, track the window sum
 *
 * PITFALLS
 *   - Record the answer AFTER the shrink loop, when the window is valid again.
 *   - "Jump left" version with a last-index map: left = max(left, last + 1), or "abba"
 *     moves left backwards and answers 3.
 *   - Sliding window needs monotonic validity; with negative numbers it silently breaks.
 *
 * DEEP DIVE
 *   C03_LongestSubStringWithoutRepeatingCharacter, C04_MaxConsecutiveOnesIII,
 *   C06_FruitIntoBaskets, C07_CharacterReplacement (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.HashMap;
import java.util.Map;

class VariableWindowLongest {

    // Canonical LC 3.
    static int lengthOfLongestSubstring(String s) {
        int[] count = new int[128];
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            count[c]++;
            while (count[c] > 1) {               // invalid: c now repeats
                count[s.charAt(left)]--;
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    // LC 340: at most k distinct characters.
    static int longestAtMostKDistinct(String s, int k) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            count.merge(s.charAt(right), 1, Integer::sum);
            while (count.size() > k) {
                char out = s.charAt(left++);
                if (count.merge(out, -1, Integer::sum) == 0) {
                    count.remove(out);
                }
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    // LC 904: two baskets = at most 2 distinct fruit types.
    static int totalFruit(int[] fruits) {
        Map<Integer, Integer> count = new HashMap<>();
        int left = 0;
        int best = 0;
        for (int right = 0; right < fruits.length; right++) {
            count.merge(fruits[right], 1, Integer::sum);
            while (count.size() > 2) {
                int out = fruits[left++];
                if (count.merge(out, -1, Integer::sum) == 0) {
                    count.remove(out);
                }
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    // LC 1004: at most k zeros may be flipped.
    static int longestOnes(int[] nums, int k) {
        int zeros = 0;
        int left = 0;
        int best = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeros++;
            }
            while (zeros > k) {
                if (nums[left++] == 0) {
                    zeros--;
                }
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    // LC 424: replace at most k characters to make the window one letter.
    static int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int maxFreq = 0;                          // best count seen in any window so far
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            maxFreq = Math.max(maxFreq, ++count[s.charAt(right) - 'A']);
            while (right - left + 1 - maxFreq > k) {
                count[s.charAt(left++) - 'A']--;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 3 abcabcbb", lengthOfLongestSubstring("abcabcbb"), 3);
        check("LC 3 bbbbb", lengthOfLongestSubstring("bbbbb"), 1);
        check("LC 3 pwwkew", lengthOfLongestSubstring("pwwkew"), 3);
        check("LC 3 abba", lengthOfLongestSubstring("abba"), 2);
        check("LC 3 empty", lengthOfLongestSubstring(""), 0);

        check("LC 340 eceba k=2", longestAtMostKDistinct("eceba", 2), 3);
        check("LC 340 aa k=1", longestAtMostKDistinct("aa", 1), 2);
        check("LC 340 abc k=0", longestAtMostKDistinct("abc", 0), 0);

        check("LC 904 [1,2,1]", totalFruit(new int[]{1, 2, 1}), 3);
        check("LC 904 [0,1,2,2]", totalFruit(new int[]{0, 1, 2, 2}), 3);
        check("LC 904 [1,2,3,2,2]", totalFruit(new int[]{1, 2, 3, 2, 2}), 4);
        check("LC 904 [3,3,3,1,2,1,1,2,3,3,4]",
                totalFruit(new int[]{3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4}), 5);

        check("LC 1004 k=2", longestOnes(new int[]{1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0}, 2), 6);
        check("LC 1004 k=3",
                longestOnes(new int[]{0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1}, 3),
                10);
        check("LC 1004 k=0 all zeros", longestOnes(new int[]{0, 0}, 0), 0);

        check("LC 424 ABAB k=2", characterReplacement("ABAB", 2), 4);
        check("LC 424 AABABBA k=1", characterReplacement("AABABBA", 1), 4);
    }
}
