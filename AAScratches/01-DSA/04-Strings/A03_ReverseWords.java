/*
 * =====================================================================
 *  Reverse Each Word, Keep Word Order               LeetCode 557 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a sentence, reverse the characters inside every word but keep the words in their
 *   original order. Words are separated by single spaces.
 *
 * EXAMPLE
 *   "Hello World from Java"  ->  "olleH dlroW morf avaJ"
 *   ""                       ->  ""
 *   "abc"                    ->  "cba"                 single word
 *   "a  b"                   ->  "a  b"                double space survives (empty token)
 *
 * APPROACH  (StringBuilder.reverse per token)
 *   1. split(" ") gives the words (an empty token for each repeated space).
 *   2. Reverse each word with new StringBuilder(word).reverse().
 *   3. Append word + " " to a result builder, then trim() the one trailing space.
 *      (trim() would also eat leading spaces, so this version does not preserve them.)
 *
 *   Second method, reverseWordsInPlace: the O(1)-extra-space answer interviewers ask for next.
 *   Work on a char[]; scan for the end of each word and reverse that segment with two
 *   pointers. No split, no extra strings.
 *
 * KEY INSIGHT
 *   "Reverse" is the third primitive after split and join. Once you can reverse a segment with
 *   two pointers, "reverse each word", "reverse word order" and "rotate" are all the same tool.
 *
 * COMPLEXITY
 *   Time  O(n)  each character is copied and reversed once
 *   Space O(n)  the word array and result (in-place version: O(n) for the char[] copy only)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reverse the ORDER of the words instead (LeetCode 151, see C01_ReverseWordsInString).
 *   - Do it in place on a char[] with no split (reverseWordsInPlace below).
 *   - Preserve all whitespace exactly, including leading/trailing: the in-place version does.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, single word, double space) and prints actual vs
 *   expected for both methods.
 */
class ReverseWords {

    /** Author's approach: split, reverse each token with StringBuilder, join with spaces. */
    public static String reverseWords(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            String reversedWord = new StringBuilder(word).reverse().toString();
            result.append(reversedWord).append(" ");
        }
        return result.toString().trim();               // drop the one trailing space we added
    }

    /** Follow-up: reverse each word segment in a char[] with two pointers, no split. */
    public static String reverseWordsInPlace(String str) {
        char[] chars = str.toCharArray();
        int wordStart = 0;
        for (int i = 0; i <= chars.length; i++) {
            if (i == chars.length || chars[i] == ' ') { // hit a boundary: reverse [wordStart, i-1]
                reverseRange(chars, wordStart, i - 1);
                wordStart = i + 1;
            }
        }
        return new String(chars);
    }

    private static void reverseRange(char[] chars, int left, int right) {
        while (left < right) {
            char tmp = chars[left];
            chars[left++] = chars[right];
            chars[right--] = tmp;
        }
    }

    private static void check(String label, String input, String expected) {
        System.out.println(label + " split   : \"" + reverseWords(input)
                + "\"   expected \"" + expected + "\"");
        System.out.println(label + " inPlace : \"" + reverseWordsInPlace(input)
                + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        check("case 1 (typical)     ", "Hello World from Java", "olleH dlroW morf avaJ");
        check("case 2 (empty)       ", "", "");
        check("case 3 (single word) ", "abc", "cba");
        check("case 4 (double space)", "a  b", "a  b");
    }
}
