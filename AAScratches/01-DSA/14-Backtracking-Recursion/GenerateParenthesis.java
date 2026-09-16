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
