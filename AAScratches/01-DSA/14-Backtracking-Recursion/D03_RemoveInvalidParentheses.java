import java.util.*;

class RemoveInvalidParentheses {
    // if input is even then we have that only one Parentheses in in result list
    public static List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        if (s == null) {
            return result;
        }
        // Use a queue for BFS and a set to track visited strings
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // Add the initial string to the queue
        queue.add(s);
        visited.add(s);

        boolean foundValid = false;

        while (!queue.isEmpty()) {
            String current = queue.poll();
// If the current string is valid, add it to the result
            if (isValid(current)) {
                result.add(current);
                foundValid = true;
            }
// If a valid string has been found, do not generate further invalid strings
            if (foundValid) continue;

            // Try removing one parenthesis at each position
            for (int i = 0; i < current.length(); i++) {
                if (current.charAt(i) == '(' || current.charAt(i) == ')') {
                    // endIndex – the ending index, exclusive
                    String next = current.substring(0, i) + current.substring(i + 1);
                    // Add the new string to the queue if it hasn't been visited yet
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
        return result;
    }

    private static boolean isValid(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            // balance is negative because we got closed one with out opening one
            // opening one not a issue at start because closed one may appear in  future
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }

    public static void main(String[] args) {
        // Example input:
        String s = "()())()";
        // Expected output: ["()()()", "(())()"]

        List<String> validStrings = removeInvalidParentheses(s);
        System.out.println("Valid strings after removal: " + validStrings);
    }
}
