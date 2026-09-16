import java.util.Stack;
/**
 * Example 1:
 * <p>
 * Input: s = "3[a]2[bc]"
 * <p>
 * Output: "aaabcbc"
 * <p>
 * <p>
 * Example 2:
 * <p>
 * Input: s = "3[a2[c]]"
 * <p>
 * Output: "accaccacc"
 * <p>
 * Constraints:
 * <p>
 * 1 <= s.length <= 30
 * <p>
 * s consists of lowercase English letters, digits, and square brackets '[]'.
 * <p>
 * s is guaranteed to be a valid input.
 * <p>
 * All the integers in s are in the range [1, 300].
 */

// https://leetcode.com/problems/decode-string/description/
//string in between brackets and number before [ and before number string
class DecodeString {
    //abc(prevHalfString)32[def(currCompletedString)]
    public static String decodeString(String s) {
        Stack<Integer> stackNums = new Stack<>();
        Stack<StringBuilder> stackSB = new Stack<>();
        StringBuilder prevHalfString = new StringBuilder();
        int n = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                n = n * 10 + (c - '0');
            } else if (c == '[') {
                stackNums.push(n);
                n = 0;
                stackSB.push(prevHalfString);
                prevHalfString = new StringBuilder();
            } else if (c == ']') {
                int times = stackNums.pop();
                StringBuilder currCompletedString = prevHalfString;
                prevHalfString = stackSB.pop();
                while (times > 0) {
                    prevHalfString.append(currCompletedString);
                    times--;
                }
            } else {
                prevHalfString.append(c);
            }
        }
        return prevHalfString.toString();
    }

    public static void main(String[] args) {
        System.out.println(decodeString("3[a2[c]]"));
    }
}
