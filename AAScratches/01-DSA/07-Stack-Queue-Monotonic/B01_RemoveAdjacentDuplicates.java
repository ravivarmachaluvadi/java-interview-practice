/*
 * =====================================================================
 *  Remove All Adjacent Duplicates In String       LeetCode 1047 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a lowercase string, repeatedly delete any two adjacent equal characters until no
 *   such pair remains, and return the final string. Removing one pair can make a new pair
 *   adjacent, and that pair must be removed too. The answer is unique.
 *
 * EXAMPLE
 *   "abbaca"  ->  "ca"    remove "bb" -> "aaca", then "aa" -> "ca"
 *   "azxxzy"  ->  "ay"    remove "xx" -> "azzy", then "zz" -> "ay"  (cascade)
 *   "aaaa"    ->  ""      everything cancels
 *   ""        ->  ""
 *   "abc"     ->  "abc"   nothing adjacent is equal
 *
 * APPROACH  (Stack collapse on equal top)
 *   1. Walk the string once, keeping a stack of characters that have survived so far.
 *   2. If the current char equals the stack top, pop: the pair is destroyed.
 *   3. Otherwise push the current char.
 *   4. The stack, read BOTTOM to TOP, is the answer. Note: for-each over java.util.Stack
 *      iterates bottom to top, which is exactly the order we need here (see A01).
 *   Second method (removeDuplicatesWithBuilder): the same loop with a StringBuilder as the
 *   stack. Its last char is the top; deleteCharAt(len-1) is pop. No boxing, and the result
 *   is already a string.
 *
 * KEY INSIGHT
 *   One pass is enough because the stack top always holds the character that is CURRENTLY
 *   adjacent to the new one, even after earlier removals. That is the whole trick: the
 *   "cascade" the problem describes happens for free, because popping exposes the previous
 *   survivor as the new top. Every "compare with top, then push or pop" simulation in this
 *   folder (asteroids, stars, backspace compare) reuses this shape.
 *
 * COMPLEXITY
 *   Time  O(n)  each character is pushed at most once and popped at most once
 *   Space O(n)  the stack holds the whole input when nothing collapses
 *
 * INTERVIEW FOLLOW-UPS
 *   - Remove duplicates of length k (LeetCode 1209): stack of (char, count) pairs.
 *   - Remove stars from a string (LeetCode 2390): '*' pops instead of an equal char.
 *   - Do it in place with a write pointer over a char array: O(1) extra space.
 *   - Backspace string compare (LeetCode 844): same collapse with '#' as the pop trigger.
 *
 * RUN
 *   main() runs 5 cases (typical, cascade, all cancel, empty, no pairs) through both
 *   methods and prints actual vs expected.
 */
import java.util.Stack;

class RemoveAdjacentDuplicates {

    // Approach 1: explicit Stack of characters
    public static String removeDuplicates(String s) {
        Stack<Character> survivors = new Stack<>();

        for (char c : s.toCharArray()) {
            if (!survivors.isEmpty() && survivors.peek() == c) {
                survivors.pop();   // c and the top form an adjacent pair: destroy both
            } else {
                survivors.push(c);
            }
        }

        // for-each over Stack is bottom-to-top, which is the original left-to-right order
        StringBuilder result = new StringBuilder();
        for (char c : survivors) {
            result.append(c);
        }
        return result.toString();
    }

    // Approach 2: StringBuilder as the stack. Last char is the top; no Character boxing.
    public static String removeDuplicatesWithBuilder(String s) {
        StringBuilder survivors = new StringBuilder();
        for (char c : s.toCharArray()) {
            int top = survivors.length() - 1;
            if (top >= 0 && survivors.charAt(top) == c) {
                survivors.deleteCharAt(top);
            } else {
                survivors.append(c);
            }
        }
        return survivors.toString();
    }

    static void print(String label, String input, String expected) {
        System.out.println(label + " \"" + input + "\": stack=\"" + removeDuplicates(input)
                + "\" builder=\"" + removeDuplicatesWithBuilder(input)
                + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        print("case 1 typical   ", "abbaca", "ca");
        print("case 2 cascade   ", "azxxzy", "ay");
        print("case 3 all cancel", "aaaa", "");
        print("case 4 empty     ", "", "");
        print("case 5 no pairs  ", "abc", "abc");
    }
}
