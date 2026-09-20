/*
 * =====================================================================
 *  Edit Distance (Levenshtein)        LeetCode 72 | Medium | MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given two strings word1 and word2, return the minimum number of single
 *   character operations needed to turn word1 into word2. The three legal
 *   operations are insert a character, delete a character, and replace a
 *   character. Either string may be empty.
 *
 * EXAMPLE
 *   "horse", "ros"          ->  3   horse -> rorse -> rose -> ros
 *   "intention", "execution"->  5
 *   "", "abc"               ->  3   three inserts (edge case main() runs)
 *   "same", "same"          ->  0   nothing to do
 *
 * APPROACH  (2D bottom-up DP on the LCS grid)
 *   1. dp[i][j] = edit distance between the first i chars of word1 and the
 *      first j chars of word2.
 *   2. Base row and column: dp[i][0] = i (delete every char of word1),
 *      dp[0][j] = j (insert every char of word2).
 *   3. If word1[i-1] == word2[j-1] the last characters already agree, so
 *      nothing is spent: dp[i][j] = dp[i-1][j-1].
 *   4. Otherwise pay 1 and take the cheapest of the three moves:
 *        replace -> dp[i-1][j-1],  delete -> dp[i-1][j],  insert -> dp[i][j-1].
 *   5. dp[m][n] is the answer. minDistanceRolling() does the same scan keeping
 *      only the previous row, which is the O(n) space version.
 *
 * KEY INSIGHT
 *   It is the LCS table with a different transition. Each cell asks one
 *   question: "what is the cheapest way to make these two prefixes equal?"
 *   The three neighbours are literally the three operations - diagonal is
 *   replace (both prefixes shrink), up is delete, left is insert. Recognise
 *   this grid for any two-string alignment problem.
 *
 * COMPLEXITY
 *   Time  O(m * n)  one constant-time decision per cell of the table
 *   Space O(m * n)  the full table; O(n) with the rolling-row version, because
 *                   a cell only ever reads the previous row and the cell left
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the actual edit script, not just the count (walk back from dp[m][n]).
 *   - One Edit Distance (LC 161): is the distance exactly 1? Answer in O(n).
 *   - Give insert / delete / replace different costs - only the transition changes.
 *   - Delete Operation for Two Strings (LC 583): no replace, so m + n - 2 * LCS.
 *
 * RUN
 *   main() runs 4 cases (typical, classic, empty string, identical strings) and
 *   prints the table and rolling answers against the expected value.
 */
class EditDistance {

    /** Classic 2D table. dp[i][j] = distance between word1[0..i) and word2[0..j). */
    public static int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // delete all i characters of word1
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // insert all j characters of word2
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // last chars agree, spend nothing
                } else {
                    int replace = dp[i - 1][j - 1];
                    int delete = dp[i - 1][j];
                    int insert = dp[i][j - 1];
                    dp[i][j] = 1 + Math.min(replace, Math.min(delete, insert));
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Same recurrence, one row at a time. A cell needs only the row above and
     * the cell to its left, so two rows of length n+1 are enough.
     */
    public static int minDistanceRolling(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++) {
            prev[j] = j; // row 0: word1 prefix is empty, so insert j characters
        }

        for (int i = 1; i <= m; i++) {
            curr[0] = i; // column 0: word2 prefix is empty, so delete i characters
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(prev[j - 1], Math.min(prev[j], curr[j - 1]));
                }
            }
            int[] swap = prev; // curr becomes the previous row for the next i
            prev = curr;
            curr = swap;
        }
        return prev[n];
    }

    private static void print(String label, String a, String b, int expected) {
        System.out.println(label + " \"" + a + "\" -> \"" + b + "\" = "
                + minDistance(a, b) + ", rolling " + minDistanceRolling(a, b)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical):  ", "horse", "ros", 3);
        print("case 2 (classic):  ", "intention", "execution", 5);
        print("case 3 (empty):    ", "", "abc", 3);
        print("case 4 (identical):", "same", "same", 0);
    }
}
