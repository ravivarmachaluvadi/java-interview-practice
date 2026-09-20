/*
 * =====================================================================
 *  Longest Substring Without Repeating Characters   LeetCode 3 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, return the length of the longest contiguous substring in
 *   which no character appears twice. s may be empty.
 *
 * EXAMPLE
 *   s = "abcddcba"  ->  4   "abcd"
 *   s = "abcabcbb"  ->  3   "abc"
 *   s = "abba"      ->  2   "ab" or "ba"; the trap case for the left pointer
 *   s = "pwwkew"    ->  3   "wke"
 *   s = ""          ->  0
 *
 * APPROACH  (last-seen index map, jump left in one hop)
 *   1. Keep a map char -> last index where it was seen, and a window [left, right].
 *   2. For each right, if s[right] was seen before at index p, the window must
 *      start after p: left = max(left, p + 1). The max() matters: p may be
 *      BEFORE the current left (stale entry), and left must never move backwards.
 *   3. Window [left, right] is now duplicate-free; record right - left + 1.
 *   4. Store / overwrite the map entry for s[right] with right.
 *
 * KEY INSIGHT
 *   Instead of shrinking the window one character at a time until the duplicate
 *   leaves (the Set version, also shown below), remember WHERE each character was
 *   last seen and jump left there in one hop. The guard left = max(left, p + 1)
 *   is the whole difficulty: without it "abba" answers 3 instead of 2.
 *   Pattern: variable window whose left edge is driven by a last-seen index.
 *
 * COMPLEXITY
 *   Time  O(n)   right moves once per char; left only ever jumps forward
 *   Space O(min(n, alphabet))  the map holds at most one entry per distinct char
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the substring itself, not just its length (track the best left).
 *   - At most K distinct chars (LC 340): frequency map + shrink while size > K.
 *   - Longest Repeating Character Replacement (LC 424): window - maxFreq <= K.
 *   - Use an int[128] array instead of a HashMap when the input is ASCII.
 *
 * RUN
 *   main() runs 5 cases through both methods (jump-left and shrink-one-step)
 *   and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ImportantLongestSubStringWithoutRepeatingCharacter {

    // Author's approach: last-seen index map, left jumps straight past the duplicate.
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen.containsKey(c)) {
                // Never move left backwards: the stored index may be from before the window.
                left = Math.max(left, lastSeen.get(c) + 1);
            }
            maxLength = Math.max(maxLength, right - left + 1);
            lastSeen.put(c, right); // always record the latest position
        }
        return maxLength;
    }

    // Alternative for contrast: shrink from the left one step at a time until the
    // duplicate leaves the window. Same O(n) bound, but more moves of left.
    public static int lengthOfLongestSubstringShrinkOneStep(String s) {
        Set<Character> inWindow = new HashSet<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            while (inWindow.contains(c)) {
                inWindow.remove(s.charAt(left));
                left++;
            }
            inWindow.add(c);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] inputs = {"abcddcba", "abcabcbb", "abba", "pwwkew", ""};
        int[] expected = {4, 3, 2, 3, 0};
        for (int i = 0; i < inputs.length; i++) {
            String label = "case " + (i + 1) + " \"" + inputs[i] + "\"";
            print(label + " jump-left ", lengthOfLongestSubstring(inputs[i]), expected[i]);
            print(label + " shrink-one", lengthOfLongestSubstringShrinkOneStep(inputs[i]),
                    expected[i]);
        }
    }
}
