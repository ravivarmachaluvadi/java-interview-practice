/**
 * Problem: Given two strings, find the smallest substring of the first string that contains all characters
 * (with multiplicity) from the second string.
 *
 * Approach: Use a sliding window with two pointers. Maintain frequency maps for the target string and the current window.
 * Expand the right pointer until all required characters are present, then contract the left pointer to shrink the window while still valid,
 * updating the best answer when a smaller valid window is found.
 *
 * Time Complexity: O(n + m) where n is the length of the source string and m is the length of the target string.
 * Space Complexity: O(k), with k being the number of distinct characters in the target string (for frequency maps).
 */
import java.util.*;

class MinimumWindowSubstring {
    public static String minWindow(String string, String sequence) {

        if (string == null || sequence == null || string.length() < sequence.length())
            return "";

        // (window length, left, right)
        int[] ans = {Integer.MAX_VALUE, 0, 0};

        // Frequency map for characters in t
        Map<Character, Integer> tFreq = new HashMap<>();
        for (char c : sequence.toCharArray())
            tFreq.put(c, tFreq.getOrDefault(c, 0) + 1);

        int required = tFreq.size();
        int formed = 0;

        Map<Character, Integer> windowCounts = new HashMap<>();


        int left = 0, right = 0;
        while (right < string.length()) {
            char c = string.charAt(right);
            windowCounts.put(c, windowCounts.getOrDefault(c, 0) + 1);

            if (tFreq.containsKey(c)
                    && windowCounts.get(c).intValue() == tFreq.get(c).intValue())
                formed++;

            // Try to contract the window until it's no longer valid (i.e., formed < required)
            while (left <= right && formed == required) {
                c = string.charAt(left);

                // Update our answer if this window is smaller
                if (right - left + 1 < ans[0]) {
                    ans[0] = right - left + 1;
                    ans[1] = left;
                    ans[2] = right;
                }

                // Before moving left forward, reduce count in window
                left++;
                windowCounts.put(c, windowCounts.get(c) - 1);
                if (tFreq.containsKey(c)
                        && windowCounts.get(c) < tFreq.get(c))
                    formed--;

            }
            // Expand to the right
            right++;
        }

        return (ans[0] == Integer.MAX_VALUE) ? "" : string.substring(ans[1], ans[2] + 1);
    }


    // A simple main to test the function
    public static void main(String[] args) {
        String string = "ADOBECODEBANC";
        String sequence = "ABC";
        String result = minWindow(string, sequence);
        System.out.println("Input: s = \"" + string + "\", t = \"" + sequence + "\"");
        System.out.println("Output: \"" + result + "\"");
        // Expected: "BANC"
    }
}
