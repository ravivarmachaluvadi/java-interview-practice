/*
 * =====================================================================
 *  Minimum Window Substring                  LeetCode 76 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given strings s and t, return the shortest substring of s that contains every
 *   character of t, counting multiplicity (t = "AABC" needs two A's). Return "" if no
 *   such window exists. The answer is guaranteed unique when it exists.
 *
 * EXAMPLE
 *   s = "ADOBECODEBANC", t = "ABC"   ->  "BANC"
 *   s = "ADOBECODEBANC", t = "AABC"  ->  "ADOBECODEBA"   (only two A's exist, at 0 and 10)
 *   s = "a",  t = "a"                ->  "a"
 *   s = "a",  t = "aa"               ->  ""              (s shorter than t)
 *
 * APPROACH  (variable window with a formed / required counter)
 *   1. Count each char of t into need. required = number of distinct chars in t.
 *   2. Slide right over s, adding s[right] to windowCount. When windowCount[c] becomes
 *      exactly need[c], one more distinct char is fully satisfied: formed++.
 *   3. While formed == required the window is valid: record it if it is the shortest so
 *      far, then drop s[left] and left++. If dropping pushes windowCount[c] below need[c],
 *      formed-- and the inner loop stops.
 *   4. Return the shortest recorded window, or "" if none was ever valid.
 *
 * KEY INSIGHT
 *   Never compare the two maps to test validity; that costs O(alphabet) per move. Track a
 *   single integer, formed, that changes only at the exact moment a char's count crosses
 *   its requirement (== on the way up, < on the way down). Validity is then O(1) per step,
 *   and each index enters and leaves the window once. Pattern: "contains a multiset" is
 *   a counter-crossing problem, not a map-equality problem.
 *
 * COMPLEXITY
 *   Time  O(n + m)  n = |s| for the two-pointer pass, m = |t| to build need
 *   Space O(k)      k = distinct characters of t (two maps of that size)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Replace the maps with int[128] when the alphabet is ASCII; same logic, faster.
 *   - Filter s down to only indices whose char is in t first when |t| << |s|.
 *   - Return all minimum windows, or the count of windows containing t.
 *   - Longest substring with at most K distinct chars: same skeleton, opposite direction.
 *
 * RUN
 *   main() runs 4 cases (typical, multiplicity, single char, t longer than s) and prints
 *   actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class MinimumWindowSubstring {

    public static String minWindow(String s, String t) {
        if (s == null || t == null || t.isEmpty() || s.length() < t.length())
            return "";

        // How many of each char the window must hold.
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray())
            need.merge(c, 1, Integer::sum);

        int required = need.size(); // distinct chars that must be fully satisfied
        int formed = 0;             // distinct chars currently fully satisfied

        Map<Character, Integer> windowCount = new HashMap<>();
        int bestLen = Integer.MAX_VALUE, bestLeft = 0, bestRight = 0;

        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            char added = s.charAt(right);
            windowCount.merge(added, 1, Integer::sum);

            // Crossing the requirement exactly once means one more char is satisfied.
            if (need.containsKey(added) && windowCount.get(added).intValue() == need.get(added))
                formed++;

            // Window is valid: shrink from the left while it stays valid.
            while (formed == required) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestLeft = left;
                    bestRight = right;
                }

                char removed = s.charAt(left++);
                windowCount.merge(removed, -1, Integer::sum);
                // Dropping below the requirement breaks validity for this char.
                if (need.containsKey(removed) && windowCount.get(removed) < need.get(removed))
                    formed--;
            }
        }

        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestLeft, bestRight + 1);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        print("case 1 typical      ", minWindow("ADOBECODEBANC", "ABC"), "BANC");
        print("case 2 multiplicity ", minWindow("ADOBECODEBANC", "AABC"), "ADOBECODEBA");
        print("case 3 single char  ", minWindow("a", "a"), "a");
        print("case 4 t longer     ", minWindow("a", "aa"), "");
    }
}
