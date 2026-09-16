class LongestCommonSubsequence {

    /**
     * | i\j   | 0 | 1 (a) | 2 (c) | 3 (e) |
     * | ----- | - | ----- | ----- | ----- |
     * | 0     | 0 | 0     | 0     | 0     |
     * | 1 (a) | 0 | 1     | 1     | 1     |
     * | 2 (b) | 0 | 1     | 1     | 1     |
     * | 3 (c) | 0 | 1     | 2     | 2     |
     * | 4 (d) | 0 | 1     | 2     | 2     |
     * | 5 (e) | 0 | 1     | 2     | 3     |
     */
    public static int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // i-1 , j-1
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        String text1 = "abcde";
        String text2 = "ace";
        System.out.println("Length of Longest Common Subsequence: " + longestCommonSubsequence(text1, text2)); // Output: 3

        String text3 = "abc";
        String text4 = "abc";
        System.out.println("Length of Longest Common Subsequence: " + longestCommonSubsequence(text3, text4)); // Output: 3

        String text5 = "abc";
        String text6 = "def";
        System.out.println("Length of Longest Common Subsequence: " + longestCommonSubsequence(text5, text6)); // Output: 0
    }
}
