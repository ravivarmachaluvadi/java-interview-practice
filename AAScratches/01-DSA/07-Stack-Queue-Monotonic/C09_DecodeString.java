/*
 * =====================================================================
 *  Decode String                                     LeetCode 394 | Medium     MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Decode a string where k[encoded] means "repeat encoded k times". Brackets nest, k is a
 *   positive integer (may have several digits), and letters outside brackets are copied as-is.
 *   Input is guaranteed valid; length <= 30, k <= 300.
 *
 * EXAMPLE
 *   "3[a]2[bc]"       ->  "aaabcbc"
 *   "3[a2[c]]"        ->  "accaccacc"        inner 2[c] = "cc", then a+cc repeated 3 times
 *   "2[abc]3[cd]ef"   ->  "abcabccdcdcdef"
 *   "abc"             ->  "abc"              no brackets at all
 *   "10[a]"           ->  "aaaaaaaaaa"       multi-digit count
 *
 * APPROACH  (twin stacks for nested state)
 *   Keep two pieces of "current" state: count being read, and current = text built since the
 *   last '['. Two stacks save the enclosing context when a bracket opens.
 *   1. digit  -> count = count * 10 + digit  (build multi-digit numbers)
 *   2. '['    -> push count and current; reset both (start a fresh inner scope)
 *   3. ']'    -> pop times and the outer text; append current to the outer text `times` times;
 *                that outer text becomes the new current
 *   4. letter -> append to current
 *   5. At the end, current holds the fully decoded string.
 *
 * KEY INSIGHT
 *   '[' is "save and enter", ']' is "restore and merge". Whatever context the outer scope had
 *   (its partial text and its pending repeat count) goes on a stack and comes back exactly
 *   when the inner scope closes. This save/restore-on-bracket model is the same one used by
 *   Basic Calculator and by nested expression evaluators generally.
 *
 * COMPLEXITY
 *   Time  O(n + output length)  each character is read once; appends are proportional to output
 *   Space O(nesting depth + output length)  stacks hold one entry per open bracket
 *
 * INTERVIEW FOLLOW-UPS
 *   - Recursive version: parse(index) returns the decoded substring and the index after ']'
 *   - What if input can be invalid (unmatched brackets)? Where would you detect it?
 *   - Basic Calculator (LC 224) uses the same save/restore on '(' and ')'
 *
 * RUN
 *   main() runs 5 cases (simple, nested, trailing letters, no brackets, multi-digit) and
 *   prints actual vs expected.
 */
import java.util.Stack;

// https://leetcode.com/problems/decode-string/description/
class DecodeString {

    // Walking "abc3[de2[f]]": at each '[' the text so far ("abc") and the count (3) are saved.
    public static String decodeString(String s) {
        Stack<Integer> counts = new Stack<>();          // repeat count pending for each open '['
        Stack<StringBuilder> outerTexts = new Stack<>(); // text built before each open '['
        StringBuilder current = new StringBuilder();    // text built since the last '['
        int count = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                count = count * 10 + (c - '0');         // multi-digit counts like 10[a]
            } else if (c == '[') {
                counts.push(count);
                outerTexts.push(current);
                count = 0;
                current = new StringBuilder();          // fresh scope for the bracket body
            } else if (c == ']') {
                int times = counts.pop();
                StringBuilder body = current;
                current = outerTexts.pop();             // restore the enclosing text
                for (int i = 0; i < times; i++) {
                    current.append(body);
                }
            } else {
                current.append(c);
            }
        }
        return current.toString();
    }

    private static void print(String label, String input, String expected) {
        System.out.println(label + ": " + decodeString(input) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 simple       ", "3[a]2[bc]", "aaabcbc");
        print("case 2 nested       ", "3[a2[c]]", "accaccacc");
        print("case 3 trailing text", "2[abc]3[cd]ef", "abcabccdcdcdef");
        print("case 4 no brackets  ", "abc", "abc");
        print("case 5 multi-digit  ", "10[a]", "aaaaaaaaaa");
    }
}
