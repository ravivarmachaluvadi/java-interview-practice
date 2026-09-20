/*
 * =====================================================================
 *  Reverse Words in a String                    LeetCode 151 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of words separated by spaces, return the words in reverse order joined
 *   by single spaces. The input may have leading, trailing or repeated spaces; the output
 *   must have none of those.
 *
 * EXAMPLE
 *   "the sky is blue"        ->  "blue is sky the"
 *   "  hello world  "        ->  "world hello"          leading/trailing spaces dropped
 *   "  a good   example  "   ->  "example good a"       repeated inner spaces collapsed
 *   "   "                    ->  ""                     edge: only spaces
 *
 * APPROACH  (trim, split on whitespace, walk backwards)
 *   1. trim() removes the outer spaces; split("\\s+") splits on runs of whitespace so
 *      repeated inner spaces produce no empty tokens.
 *   2. Walk the token array from the last index down to 0, appending each word and one
 *      space (no space after the final word).
 *
 *   Second method, reverseWordsInPlace, is the O(1)-extra-space follow-up on a char[]:
 *   1. Squeeze out extra spaces with a read/write pointer pair.
 *   2. Reverse the whole array: words are now in the right order but spelled backwards.
 *   3. Reverse each word individually to fix the spelling.
 *
 * KEY INSIGHT
 *   "Reverse the whole thing, then reverse each piece" is the pattern: reversing twice at
 *   two different granularities moves the pieces without scrambling the letters. The same
 *   trick rotates an array in place (LeetCode 189).
 *
 * COMPLEXITY
 *   Time  O(n)  split, one backward pass and one join over n characters
 *   Space O(n)  the token array and the StringBuilder (O(1) extra for the in-place version)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in place on a char array with O(1) extra space (see reverseWordsInPlace).
 *   - Reverse each word but keep the word order (LeetCode 557): skip the whole-array reverse.
 *   - Words arrive as a stream: push onto a stack, pop to build the answer.
 *
 * RUN
 *   main() runs 4 cases (typical, padded, repeated inner spaces, all spaces) through both
 *   methods and prints actual vs expected.
 */
class ReverseWordsInString {

    /** Author's approach: split into tokens, walk them backwards. */
    public String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder reversed = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            reversed.append(words[i]);
            if (i > 0) {
                reversed.append(' ');
            }
        }
        return reversed.toString();
    }

    /** Follow-up: O(1) extra space on a char array. Returns the cleaned, reversed string. */
    public String reverseWordsInPlace(char[] chars) {
        int length = squeezeSpaces(chars);       // step 1: "  a  b " -> "a b"
        reverse(chars, 0, length - 1);           // step 2: whole array, "b a" spelled backwards

        int wordStart = 0;                       // step 3: fix the spelling of each word
        for (int i = 0; i <= length; i++) {
            boolean atWordEnd = i == length || chars[i] == ' ';
            if (atWordEnd) {
                reverse(chars, wordStart, i - 1);
                wordStart = i + 1;
            }
        }
        return new String(chars, 0, length);
    }

    /** Drops leading/trailing spaces and collapses inner runs to one; returns the new length. */
    private static int squeezeSpaces(char[] chars) {
        int write = 0;
        for (int read = 0; read < chars.length; read++) {
            boolean redundantSpace = chars[read] == ' ' && (write == 0 || chars[write - 1] == ' ');
            if (!redundantSpace) {
                chars[write++] = chars[read];
            }
        }
        if (write > 0 && chars[write - 1] == ' ') {
            write--;                             // one trailing space may have slipped through
        }
        return write;
    }

    private static void reverse(char[] chars, int lo, int hi) {
        while (lo < hi) {
            char tmp = chars[lo];
            chars[lo++] = chars[hi];
            chars[hi--] = tmp;
        }
    }

    public static void main(String[] args) {
        ReverseWordsInString solution = new ReverseWordsInString();
        String[] inputs = {"the sky is blue", "  hello world  ", "  a good   example  ", "   "};
        String[] expected = {"blue is sky the", "world hello", "example good a", ""};

        for (int i = 0; i < inputs.length; i++) {
            String label = "case " + (i + 1);
            print(label + " split   ", solution.reverseWords(inputs[i]), expected[i]);
            String inPlace = solution.reverseWordsInPlace(inputs[i].toCharArray());
            print(label + " in-place", inPlace, expected[i]);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }
}
