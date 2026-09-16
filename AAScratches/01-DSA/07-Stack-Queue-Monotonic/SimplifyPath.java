// LeetCode Problem: https://leetcode.com/problems/simplify-path/
import java.util.*;

/**
 * Input: path = "/home/user/Documents/../Pictures"
 * <p>
 * Output: "/home/user/Pictures"
 */
// solution using stack
class SimplifyPath {

    public static String simplifyPath(String path) {
        StringBuilder absolutePath = new StringBuilder();
        String[] directories = path.split("/");
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < directories.length; i++) {
            if (directories[i].equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!directories[i].equals(".") && !directories[i].isBlank()) {
                stack.push(directories[i]);
            }
        }

        if (stack.isEmpty()) {
            return "/";
        }

        for (String dir : stack) {
            absolutePath.append("/");
            absolutePath.append(dir);
        }
        return absolutePath.toString();
    }

    public static void main(String[] args) {
        String path = "/home//foo/";
        String result = simplifyPath(path);
        System.out.println("Simplified Path: " + result);
        // Expected output: "/home/foo"
    }
}
