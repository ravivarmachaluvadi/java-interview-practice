/*
 * =====================================================================
 *  Simplify Path                                        LeetCode 71 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an absolute Unix path, return its canonical form: starts with "/", single slashes
 *   between names, no trailing slash, "." (current dir) removed, ".." (parent dir) resolved.
 *   ".." at the root stays at the root. Any other run of dots (e.g. "...") is a normal name.
 *
 * EXAMPLE
 *   "/home/"                          ->  "/home"
 *   "/home//foo/"                     ->  "/home/foo"           empty segment ignored
 *   "/home/user/Documents/../Pictures" ->  "/home/user/Pictures"
 *   "/../"                            ->  "/"                   cannot go above root
 *   "/.../a/../b/c/../d/./"           ->  "/.../b/d"            "..." is a real name
 *
 * APPROACH  (token stack for path resolution)
 *   1. Split on "/" to get segments. Empty strings appear for "//" and the leading "/".
 *   2. For each segment:
 *        ".."         -> pop the stack if it is non-empty (root absorbs extra "..")
 *        "." or ""    -> skip
 *        anything else -> push (it is a directory name)
 *   3. Empty stack means root: return "/".
 *   4. Otherwise join the stack bottom-to-top with "/" in front of each name.
 *
 * KEY INSIGHT
 *   Work on tokens, not characters: split first, then the stack only ever sees whole names.
 *   The problem is really an edge-case checklist (empty segments, ".", ".." at root, "..." as a
 *   name), so state the rules aloud before coding. Note that iterating java.util.Stack goes
 *   bottom-to-top, which is exactly the order a path needs here.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to split, one pass over segments, one pass to join
 *   Space O(n)  the segment array and the stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Relative paths: resolve against a given current directory
 *   - Symlinks or "~": which segments need extra state?
 *   - Do it without split(): scan with two pointers between slashes to avoid the array
 *
 * RUN
 *   main() runs 5 cases (typical, double slash, "..", ".." at root, "..." name) and prints
 *   actual vs expected.
 */
import java.util.Stack;

// https://leetcode.com/problems/simplify-path/
class SimplifyPath {

    public static String simplifyPath(String path) {
        String[] segments = path.split("/");   // "" for leading slash and for "//"
        Stack<String> dirs = new Stack<>();

        for (String segment : segments) {
            if (segment.equals("..")) {
                if (!dirs.isEmpty()) dirs.pop();     // at root ".." is a no-op
            } else if (!segment.equals(".") && !segment.isEmpty()) {
                dirs.push(segment);                  // a real directory name (includes "...")
            }
        }

        if (dirs.isEmpty()) return "/";

        StringBuilder canonical = new StringBuilder();
        for (String dir : dirs) {                    // Stack iterates bottom-to-top: root first
            canonical.append('/').append(dir);
        }
        return canonical.toString();
    }

    private static void print(String label, String path, String expected) {
        System.out.println(label + ": " + simplifyPath(path) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 trailing slash ", "/home/", "/home");
        print("case 2 double slash   ", "/home//foo/", "/home/foo");
        print("case 3 parent dir     ", "/home/user/Documents/../Pictures", "/home/user/Pictures");
        print("case 4 above root     ", "/../", "/");
        print("case 5 dots as a name ", "/.../a/../b/c/../d/./", "/.../b/d");
    }
}
