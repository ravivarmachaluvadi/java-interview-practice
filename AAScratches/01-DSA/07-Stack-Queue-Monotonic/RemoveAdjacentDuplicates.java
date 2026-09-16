/**
 * Problem:
 *   Given a string, repeatedly delete adjacent pairs of identical characters until no such pairs remain.
 *
 * Approach:
 *   Use a stack to keep track of the current processed characters.
 *   For each character in the input, if it matches the top of the stack, pop the stack (remove the pair);
 *   otherwise push the character onto the stack. After processing all characters, build the result from
 *   the remaining stack contents.
 *
 * Complexity:
 *   Time:  O(n) – each character is pushed and popped at most once.
 *   Space: O(n) – worst‑case stack holds all characters of the input string.
 */
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
