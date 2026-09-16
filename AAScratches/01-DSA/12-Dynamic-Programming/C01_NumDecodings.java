
// String, Dynamic Programming
// https://leetcode.com/problems/decode-ways

/**
 * Input: s = "12"
 * <p>
 * Output: 2
 * <p>
 * Explanation:
 * <p>
 * "12" could be decoded as "AB" (1 2) or "L" (12).
 */
class NumDecodings {

    public static int numDecodings(String s) {
        if (s == null || s.isEmpty()) return 0;

        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = s.charAt(0) == '0' ? 0 : 1;

        for (int i = 2; i <= n; i++) {
            // moving from current to back by one place
            int oneDigit = Integer.parseInt(s.substring(i - 1, i));
            // moving from current to back by two places
            int twoDigits = Integer.parseInt(s.substring(i - 2, i));

            if (oneDigit >= 1 && oneDigit <= 9)
                dp[i] += dp[i - 1];
            if (twoDigits >= 10 && twoDigits <= 26)
                dp[i] += dp[i - 2];
        }
        return dp[n];
    }

    public static void main(String[] args) {
        System.out.println(numDecodings("12")); // 2
    }
}
