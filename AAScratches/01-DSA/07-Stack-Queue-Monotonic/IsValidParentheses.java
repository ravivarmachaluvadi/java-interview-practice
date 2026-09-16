/**
 * Problem: Determine whether a string consisting solely of '(' and ')' characters is a valid parentheses sequence.
 *
 * Approach: Iterate through the string, maintaining a counter that increments for '(' and decrements for ')'.
 * If the counter ever becomes negative, an unmatched closing parenthesis has been encountered, so return false.
 * After processing all characters, the counter must be zero to indicate every opening parenthesis was matched.
 *
 * Time Complexity: O(n), where n is the length of the input string (single pass).
 * Space Complexity: O(1) – only a single integer counter is used regardless of input size. */
class IsValidParentheses {
    public static void main(String[] args) {
        System.out.println(isValid("()()()()"));
    }

    private static boolean isValid(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            // balance is negative because we got
            // closed one with out opening one
            // opening one not a issue at start because
            // closed one may appear in  future
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }
}