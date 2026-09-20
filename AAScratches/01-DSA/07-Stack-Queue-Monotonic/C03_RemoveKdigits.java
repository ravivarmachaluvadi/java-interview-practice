/*
 * =====================================================================
 *  Remove K Digits                               LeetCode 402 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a non-negative integer as a string num and an integer k, remove exactly k digits so
 *   the remaining number is the smallest possible. Digit order must be preserved. Return the
 *   result without leading zeros; if nothing is left, return "0". k <= num.length().
 *
 * EXAMPLE
 *   num = "1432219", k = 3  ->  "1219"   remove 4, 3, 2
 *   num = "10200",   k = 1  ->  "200"    remove the 1, then strip the leading 0
 *   num = "10",      k = 2  ->  "0"      everything removed
 *   num = "1234",    k = 1  ->  "123"    already increasing, so drop from the end
 *   num = "100",     k = 1  ->  "0"      "00" collapses to a single "0"
 *
 * APPROACH  (monotonic non-decreasing stack with a removal budget)
 *   1. Scan digits left to right, keeping a stack whose digits never decrease bottom to top.
 *   2. Before pushing digit d, while the top is larger than d and k > 0, pop it and spend one
 *      removal: replacing a bigger digit with a smaller one in a higher position is the best
 *      possible move.
 *   3. Push d.
 *   4. If budget remains, the stack is non-decreasing, so pop from the top (the biggest digits).
 *   5. Rebuild the string bottom to top and strip leading zeros; return "0" if empty.
 *
 * KEY INSIGHT
 *   Greedy: the leftmost "peak" (a digit followed by a smaller one) is always the right thing
 *   to remove first, because it lowers the most significant position that can be lowered.
 *   The monotonic stack finds every such peak in one pass. Same skeleton as Remove Duplicate
 *   Letters and Create Maximum Number, only the pop condition changes.
 *
 * COMPLEXITY
 *   Time  O(n)  each digit is pushed once and popped at most once
 *   Space O(n)  the stack and the result builder
 *
 * INTERVIEW FOLLOW-UPS
 *   - Largest number instead of smallest: pop while the top is SMALLER than the new digit.
 *   - Remove Duplicate Letters (LeetCode 316): same stack, pop only if the char appears later.
 *   - Why "0" and not ""? The problem asks for a number; "00" and "" both mean zero.
 *
 * RUN
 *   main() runs 5 cases (typical, leading zero, remove all, already increasing, all zeros)
 *   and prints actual vs expected.
 */

import java.util.Stack;

class RemoveKdigits {

    public static String removeKdigits(String num, int k) {
        if (num.length() == k) {
            return "0";
        }
        // Digits kept so far; never decreasing from bottom to top.
        Stack<Character> kept = new Stack<>();

        for (int i = 0; i < num.length(); i++) {
            char digit = num.charAt(i);
            // A bigger digit on top followed by a smaller one is a peak: pop it while budget lasts.
            while (k > 0 && !kept.isEmpty() && kept.peek() > digit) {
                kept.pop();
                k--;
            }
            kept.push(digit);
        }
        // Budget left over means the digits are non-decreasing; the largest ones are on top.
        while (k > 0) {
            kept.pop();
            k--;
        }

        StringBuilder result = new StringBuilder();
        while (!kept.isEmpty()) {
            result.append(kept.pop());
        }
        result.reverse(); // popped top-down, so flip back to original order

        while (result.length() > 1 && result.charAt(0) == '0') {
            result.deleteCharAt(0);
        }
        return result.toString();
    }

    private static void print(String label, String actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical             ", removeKdigits("1432219", 3), "1219");
        print("case 2 leading zero        ", removeKdigits("10200", 1), "200");
        print("case 3 remove everything   ", removeKdigits("10", 2), "0");
        print("case 4 already increasing  ", removeKdigits("1234", 1), "123");
        print("case 5 collapses to zero   ", removeKdigits("100", 1), "0");
    }
}
