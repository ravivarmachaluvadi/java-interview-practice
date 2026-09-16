// LeetCode Problem: https://leetcode.com/problems/longest-palindromic-subsequence/
class LongestPalindromicSubsequence {
    public static int findLongestPalindromicSubsequence(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        return findLPS(str, 0, str.length() - 1);
    }

    private static int findLPS(String str, int start, int end) {
        if (start > end)
            return 0;

        if (start == end)
            return 1;

        if (str.charAt(start) == str.charAt(end)) {
            return 2 + findLPS(str, start + 1, end - 1);
        } else {
            return Math.max(findLPS(str, start + 1, end), findLPS(str, start, end - 1));
        }
    }

    public static void main(String[] args) {
        String example = "bbbab";
        int result = findLongestPalindromicSubsequence(example);
        System.out.println("Longest Palindromic Subsequence Length: " + result);
        // Expected output: 4 ("bbbb" is the longest palindromic subsequence)
    }
}
