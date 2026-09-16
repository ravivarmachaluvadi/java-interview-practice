/**
 * Problem:
 * Given a string containing words separated by spaces, return a new string with the order of the words reversed.
 *
 * Approach:
 * 1. Trim leading/trailing whitespace and split on one or more spaces to get an array of words.
 * 2. Iterate the array backwards, appending each word to a StringBuilder with single spaces between them.
 *
 * Complexity:
 * Time: O(n) where n is the length of the input string (splitting + building result).
 * Space: O(n) for the array of words and the resulting string.
 */
class ReverseWordsInString {

    public String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder reversed = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            reversed.append(words[i]);
            if (i > 0) {
                reversed.append(" ");
            }
        }
        return reversed.toString();
    }

    public static void main(String[] args) {
        ReverseWordsInString solution = new ReverseWordsInString();

        String example1 = "  the sky is blue  ";
        System.out.println(solution.reverseWords(example1));
        // Expected output: "blue is sky the"

        String example2 = "hello world";
        System.out.println(solution.reverseWords(example2));
        // Expected output: "world hello"

        String example3 = "  a good   example  ";
        System.out.println(solution.reverseWords(example3));
        // Expected output: "example good a"
    }

}
