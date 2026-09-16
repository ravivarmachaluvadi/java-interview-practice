import java.util.Stack;

class ValidParentheses {
    public static void main(String[] args) {
        String str = "[])";

        Stack<Character> stack = new Stack<>();

        int n = str.length();
        for (int i = 0; i < n; i++) {
            char charAt = str.charAt(i);
            if (charAt == '(' || charAt == '{' || charAt == '[') {
                stack.push(charAt);
            } else if (!stack.isEmpty() && ((charAt == ')' && stack.peek() == '(')
                    || (charAt == ']' && stack.peek() == '[')
                    || (charAt == '}' && stack.peek() == '{'))) {
                stack.pop();
            } else {
                // If it’s a closing bracket but no match or stack empty
                System.out.println(false);
                return;
            }
        }
        System.out.println(stack.isEmpty());
    }
}