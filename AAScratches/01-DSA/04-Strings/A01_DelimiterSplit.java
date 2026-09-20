/*
 * =====================================================================
 *  Split a String on a Delimiter                      Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string and a single delimiter character, return the substrings between the
 *   delimiters as an array. This is the tokenising step every word-level string problem
 *   in this folder starts from, so it is worth knowing exactly what split() does at the edges.
 *
 * EXAMPLE
 *   "apple,banana,orange,grape", ','  ->  [apple, banana, orange, grape]
 *   "a,,b", ','                       ->  [a, , b]       empty token in the middle is kept
 *   "a,b,", ','                       ->  [a, b]         trailing empty tokens are dropped
 *   "", ','                           ->  []             split returns [""], length 1, so we
 *                                                        special-case the empty input
 *   "1.2.3", '.'                      ->  [1, 2, 3]      '.' is a regex, must be quoted
 *
 * APPROACH  (String.split on a delimiter)
 *   1. split(String regex) takes a REGEX, not a literal. Wrap the delimiter in
 *      Pattern.quote(...) so '.', '|', '*' and friends behave as plain characters.
 *   2. Java drops trailing empty strings from the result (limit = 0). Pass limit = -1
 *      if the caller needs them kept.
 *   3. splitManually() shows the same thing with a single scan: walk the chars, cut a
 *      token every time the delimiter appears, and add the final tail after the loop.
 *
 * KEY INSIGHT
 *   split() is regex-based and trims trailing empties. Those two facts explain almost every
 *   "why did split give me that?" bug. When the delimiter is user-supplied, quote it.
 *
 * COMPLEXITY
 *   Time  O(n)  each character is examined once
 *   Space O(n)  the tokens together hold every non-delimiter character
 *
 * INTERVIEW FOLLOW-UPS
 *   - Split on one-or-more whitespace: split("\\s+") after trim() (see C01_ReverseWordsInString).
 *   - Keep trailing empties: split(regex, -1).
 *   - Multi-character delimiter, or several delimiters at once: Pattern.quote / character class.
 *   - Streaming input too large to hold: StringTokenizer or a manual scan like splitManually().
 *
 * RUN
 *   main() runs 5 cases (typical, empty tokens, trailing delimiter, empty input, regex char)
 *   and prints actual vs expected for both methods.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class DelimiterSplit {

    /** Library approach: quote the delimiter so it is treated literally, not as a regex. */
    static String[] splitWithLibrary(String input, char delimiter) {
        if (input.isEmpty()) return new String[0];      // "".split(x) gives [""], not []
        return input.split(Pattern.quote(String.valueOf(delimiter)));
    }

    /**
     * Manual scan: cut a token at every delimiter; mirrors split() by dropping
     * trailing empties.
     */
    static String[] splitManually(String input, char delimiter) {
        List<String> tokens = new ArrayList<>();
        int tokenStart = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == delimiter) {
                tokens.add(input.substring(tokenStart, i));
                tokenStart = i + 1;
            }
        }
        tokens.add(input.substring(tokenStart));        // tail after the last delimiter
        // match String.split: remove trailing empty strings
        while (!tokens.isEmpty() && tokens.get(tokens.size() - 1).isEmpty()) {
            tokens.remove(tokens.size() - 1);
        }
        return tokens.toArray(new String[0]);
    }

    private static void check(String label, String input, char delimiter, String expected) {
        System.out.println(label + " library : "
                + Arrays.toString(splitWithLibrary(input, delimiter))
                + "   expected " + expected);
        System.out.println(label + " manual  : "
                + Arrays.toString(splitManually(input, delimiter))
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 (typical)        ", "apple,banana,orange,grape", ',',
                "[apple, banana, orange, grape]");
        check("case 2 (empty middle)   ", "a,,b", ',', "[a, , b]");
        check("case 3 (trailing delim) ", "a,b,", ',', "[a, b]");
        check("case 4 (empty input)    ", "", ',', "[]");
        check("case 5 (regex char '.') ", "1.2.3", '.', "[1, 2, 3]");
    }
}
