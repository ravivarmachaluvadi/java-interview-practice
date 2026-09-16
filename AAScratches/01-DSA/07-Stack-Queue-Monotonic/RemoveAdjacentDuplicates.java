import java.util.Stack;

class RemoveAdjacentDuplicates {

    public static String removeDuplicates(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            // If the stack is not empty and the top element
            // is the same as the current character
            if (!stack.isEmpty() && stack.peek() == c) {
                stack.pop();  // Remove the top element
            } else {
                stack.push(c);  // Add the current character to the stack
            }
        }

        // Build the result string from the stack
        StringBuilder result = new StringBuilder();
        for (char c : stack) {
            result.append(c);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        // Example usage
        String input = "abbaca";
        String output = removeDuplicates(input);
        System.out.println("Output: " + output);
    }
}
