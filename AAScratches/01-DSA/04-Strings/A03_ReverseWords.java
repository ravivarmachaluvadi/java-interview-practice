/**
 * Problem: Reverse each word in a given sentence while preserving the original word order.
 *
 * Approach: Split the input string by spaces, iterate over each token,
 * reverse it using StringBuilder.reverse(), and append to a result builder
 * with a trailing space. Finally trim any excess whitespace.
 *
 * Time Complexity: O(n) where n is the total number of characters in the input,
 * because each character is processed once during splitting and reversing.
 *
 * Space Complexity: O(n) for storing the split words array and the resulting string.
 */
class ReverseWords {

    public static String reverseWords(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String reversedWord = new StringBuilder(word).reverse().toString();
            result.append(reversedWord).append(" ");
        }
        return result.toString().trim();
    }

    public static void main(String[] args) {
        String str = "Hello World from Java"; // olleH dlroW morf avaJ

        String reversedWords = reverseWords(str);
        System.out.println(reversedWords);
    }
}
