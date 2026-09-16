/**
 * Generates all combinations of well‑formed parentheses for a given number n.
 *
 * The algorithm uses backtracking, building the string one character at a time.
 * It keeps track of how many left '(' and right ')' parentheses remain to be added,
 * ensuring that at any point the number of ')' never exceeds the number of '('.
 *
 * Time Complexity: O(Cn) where Cn is the nth Catalan number (≈ 4ⁿ / (n^(3/2))).
 * Space Complexity: O(n) for recursion stack plus O(Cn·n) for storing all results.
 */
import java.util.*;

class GenerateParenthesis {
    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        generateParenthesisHelper(n, n, "", result);
        return result;
        //[((())), (()()), (())(), ()(()), ()()()]
    }
    private static void generateParenthesisHelper(int left, int right, String expression, List<String> result) {
        if (left == 0 && right == 0) {
            result.add(expression);
            return;
        }

        if (left > 0) {
            generateParenthesisHelper(left - 1, right, expression + "(", result);
        }

        if (right > left) {
            generateParenthesisHelper(left, right - 1, expression + ")", result);
        }
    }

    public static void main(String[] args) {
        System.out.println(generateParenthesis(3));
    }
}
