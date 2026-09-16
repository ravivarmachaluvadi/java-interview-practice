import java.util.*;

class LongestCommonSubsequence {

    private int func(String s1, String s2, int ind1, int ind2) {
        // Base case
        if (ind1 < 0 || ind2 < 0)
            return 0;

        if (s1.charAt(ind1) == s2.charAt(ind2))
            return 1 + func(s1, s2, ind1 - 1, ind2 - 1);
        else
            return Math.max(func(s1, s2, ind1, ind2 - 1), func(s1, s2, ind1 - 1, ind2));
    }

    public int lcs(String str1, String str2) {
        int n = str1.length();
        int m = str2.length();

        return func(str1, str2, n - 1, m - 1);
    }

    public static void main(String[] args) {
        String s1 = "acd";
        String s2 = "ced";

        LongestCommonSubsequence sol = new LongestCommonSubsequence();
        System.out.println("The Length of Longest Common Subsequence is " + sol.lcs(s1, s2));
    }
}
