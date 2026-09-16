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
