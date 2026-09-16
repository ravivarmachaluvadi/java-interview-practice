// https://leetcode.com/problems/minimum-add-to-make-parentheses-valid/
class MinimumAddToMakeParenthesesValid {

    public static int minAddToMakeValid(String s) {
        int openCount = 0; // Count of unmatched '('
        int closeCount = 0; // Count of unmatched ')'

        for (char ch : s.toCharArray()) {
            if (ch == '(') {
                openCount++; // Increment for unmatched '('
            } else {
                // Pair a ')' with a previous '('
                if (openCount > 0) {
                    openCount--;
                } else {
                    // Unmatched ')', so increment closeCount
                    closeCount++;
                }
            }
        }
        // ))((
        return openCount + closeCount;
    }

    public static void main(String[] args) {
        String s1 = "()))((";
        String s2 = "())";
        String s3 = "(((";
        String s4 = "))((";

        System.out.println("Minimum additions for '()))((' : " + minAddToMakeValid(s1)); // Should return 4
        System.out.println("Minimum additions for '())' : " + minAddToMakeValid(s2)); // Should return 1
        System.out.println("Minimum additions for '(((' : " + minAddToMakeValid(s3)); // Should return 3
        System.out.println("Minimum additions for '()' : " + minAddToMakeValid(s4)); // Should return 0
    }
}
