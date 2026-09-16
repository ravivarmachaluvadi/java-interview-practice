import java.util.*;

/**
 * Function to count the number of
 * <p>
 * distinct subsequences of s2 in s1
 * <p>
 * String s1 = "babgbag";
 * <p>
 * String s2 = "bag";
 */
//The Count of Distinct Subsequences is 5
class CountOfDistinctSubsequences {
    private static final int prime = (int) (1e9 + 7);

    private int countUtil(String s1, String s2, int ind1, int ind2) {
        /* If s2 has been completely matched,
        return 1 (found a valid subsequence)*/
        if (ind2 < 0) return 1;

        if (ind1 < 0) return 0;

        int result = 0;

        if (s1.charAt(ind1) == s2.charAt(ind2)) {
            int leaveOne = countUtil(s1, s2, ind1 - 1, ind2 - 1);
            int stay = countUtil(s1, s2, ind1 - 1, ind2);
            result = (leaveOne + stay) % prime;
        } else {
            result = countUtil(s1, s2, ind1 - 1, ind2);
        }
        return result;
    }

    public int distinctSubsequences(String s, String t) {
        int lt = s.length();
        int ls = t.length();
        return countUtil(s, t, lt - 1, ls - 1);
    }

    public static void main(String[] args) {
        String s1 = "babgbag";
        String s2 = "bag";

        CountOfDistinctSubsequences sol = new CountOfDistinctSubsequences();
        System.out.println("The Count of Distinct Subsequences is " + sol.distinctSubsequences(s1, s2));
    }
}
