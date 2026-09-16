// https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters/description/
// 395. Longest Substring with At Least K Repeating Characters
class LongestSubstringWithKRepeatingChars {

    /**
     * Given a string s and an integer k, return the length of the longest substring of s
     * <br><b>such that the frequency of each character in this substring is greater than or equal to k.<b/>
     * <p>
     * if no such substring exists, return 0.
     * <p>
     * Example 1:
     * <p>
     * Input: s = "aaabb", k = 3
     * Output: 3
     * Explanation: The longest substring is "aaa", as 'a' is repeated 3 times.
     * Example 2:
     * <p>
     * Input: s = "ababbc", k = 2
     * Output: 5
     * Explanation: The longest substring is "ababb", as 'a' is repeated 2 times and 'b' is repeated 3 times.
     */
    public static int longestSubstring(String s, int k) {
        if (s == null || s.isEmpty() || k > s.length())
            return 0;

        return helper(s, k, 0, s.length());
    }

    private static int helper(String s, int k, int start, int end) {
        if (end - start < k) return 0;

        // to find and find frequency of each character
        int[] freq = new int[26];
        for (int i = start; i < end; i++) freq[s.charAt(i) - 'a']++;

        for (int i = start; i < end; i++) {
            if (freq[s.charAt(i) - 'a'] < k) {
                // Split at this character
                int next = i + 1;
                while (next < end && freq[s.charAt(next) - 'a'] < k)
                    next++;

                return Math.max(helper(s, k, start, i), helper(s, k, next, end));
            }
        }
        return end - start;
    }

    public static void main(String[] args) {
        // Example input
        String s = "aaabb";
        int k = 3;

        System.out.println("String: " + s);
        System.out.println("K: " + k);
        System.out.println("Longest Substring Length: " + longestSubstring(s, k));

        // Additional example
        String s2 = "ababbc";
        int k2 = 2;
        System.out.println("\nString: " + s2);
        System.out.println("K: " + k2);
        System.out.println("Longest Substring Length: " + longestSubstring(s2, k2));
    }
}
