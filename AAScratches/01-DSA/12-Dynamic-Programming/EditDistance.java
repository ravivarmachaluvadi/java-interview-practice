class EditDistance {
    public static int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Initialize the base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Deleting all characters of word1
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Inserting all characters of word2
        }
        // Fill the dp table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No change needed
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1], // Replace
                            Math.min(dp[i - 1][j], // Delete
                                    dp[i][j - 1]) // Insert
                    );
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        String word1 = "horse";
        String word2 = "ros";
        System.out.println("Minimum edit distance: " + minDistance(word1, word2)); // Output: 3

        String word3 = "intention";
        String word4 = "execution";
        System.out.println("Minimum edit distance: " + minDistance(word3, word4)); // Output: 5
    }
}
