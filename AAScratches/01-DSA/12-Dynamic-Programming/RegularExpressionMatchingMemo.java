import java.util.HashMap;
import java.util.Map;

// https://leetcode.com/problems/regular-expression-matching/description/
// 10. Regular Expression Matching
public class RegularExpressionMatchingMemo {

    public static boolean isMatch(String s, String p) {
        Map<String, Boolean> memo = new HashMap<>();
        return dp(0, 0, s, p, memo);
    }

    private static boolean dp(int i, int j, String s, String p, Map<String, Boolean> memo) {
        String key = i + "," + j;
        if (memo.containsKey(key)) return memo.get(key);

        // Base case: reached end of pattern
        if (j == p.length()) {
            boolean ans = (i == s.length());
            memo.put(key, ans);
            return ans;
        }

        // First match condition (checks if chars align)
        boolean firstMatch = (i < s.length() &&
                (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.'));

        boolean ans;
        // If next char in pattern is '*', handle zero or multiple occurrences
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            ans = dp(i, j + 2, s, p, memo) ||  // '*' means "0 occurrences"
                    (firstMatch && dp(i + 1, j, s, p, memo)); // or "1+ occurrences"
        } else {
            ans = firstMatch && dp(i + 1, j + 1, s, p, memo);
        }

        memo.put(key, ans);
        return ans;
    }

    public static void main(String[] args) {
        String s1 = "aab", p1 = "c*a*b";
        System.out.println("s = \"" + s1 + "\", p = \"" + p1 + "\" -> " + isMatch(s1, p1));
        // true

        String s2 = "mississippi", p2 = "mis*is*p*.";
        System.out.println("s = \"" + s2 + "\", p = \"" + p2 + "\" -> " + isMatch(s2, p2));
        // false

        String s3 = "ab", p3 = ".*";
        System.out.println("s = \"" + s3 + "\", p = \"" + p3 + "\" -> " + isMatch(s3, p3));
        // true

        String s4 = "aaa", p4 = "a*a";
        System.out.println("s = \"" + s4 + "\", p = \"" + p4 + "\" -> " + isMatch(s4, p4));
        // true
    }
}
