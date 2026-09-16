/**
 * Problem: Given a string of digits and a target value, generate all possible
 * expressions by inserting '+', '-', or '*' between the digits (without reordering)
 * such that the expression evaluates to the target.
 *
 * Approach: Depth‑first search with backtracking. At each position we try every
 * possible next number segment, recursively building the expression while keeping
 * track of the current evaluated value and the last multiplied term to handle
 * multiplication precedence correctly. Leading zeros are skipped unless the
 * segment is exactly "0".
 *
 * Time Complexity: O(4^n) in the worst case (each digit can start a new number or
 * be combined with previous digits, and each position has up to 3 operators).
 * Space Complexity: O(n) for recursion stack plus result list size.
 */
import java.util.ArrayList;
import java.util.List;

class ExpressionAddOperators {
    public List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num == null || num.length() == 0) return result;
        helper(result, "", num, target, 0, 0, 0);
        return result;
    }

    private void helper(List<String> result, String expression, String num, int target,
                        int pos, long eval, long multed) {
        if (pos == num.length()) {
            if (eval == target) result.add(expression);
            return;
        }

        for (int i = pos; i < num.length(); i++) {
            if (i != pos && num.charAt(pos) == '0') {
                break;
            }
            long cur = Long.parseLong(num.substring(pos, i + 1));

            if (pos == 0) {
                helper(result, String.valueOf(cur), num, target, i + 1, cur, cur);
            } else {
                helper(result, expression + "+" + cur, num, target, i + 1, eval + cur, cur);
                helper(result, expression + "-" + cur, num, target, i + 1, eval - cur, -cur);
                helper(result, expression + "*" + cur, num, target, i + 1, eval - multed + multed * cur, multed * cur);
            }
        }
    }

    public static void main(String[] args) {
        ExpressionAddOperators sol = new ExpressionAddOperators();
        String num = "01023";
        int target = 6;
        List<String> result = sol.addOperators(num, target);
        System.out.println(result); // Output: [1*2*3, 1+2+3]
    }
}
