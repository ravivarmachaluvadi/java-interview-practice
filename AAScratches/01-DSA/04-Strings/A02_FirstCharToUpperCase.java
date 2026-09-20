/*
 * =====================================================================
 *  Capitalize the First Letter of Each Word              Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a sentence, upper-case the first character of every word and leave the rest of each
 *   word untouched. Words are separated by single spaces; the spacing must be preserved.
 *
 * EXAMPLE
 *   "ravi varma"     ->  "Ravi Varma"
 *   ""               ->  ""              empty input, must not throw
 *   "hello  world"   ->  "Hello  World"  double space produces an empty token; keep it
 *   "3d printing"    ->  "3d Printing"   a non-letter first char is left as-is by toUpperCase
 *
 * APPROACH  (split, map, join pipeline)
 *   1. split(" ") turns the sentence into words (empty tokens appear for repeated spaces).
 *   2. map each word: first char upper-cased + the rest unchanged. An empty token is passed
 *      through untouched, because substring(0, 1) on "" throws.
 *   3. Collectors.joining(" ") glues the words back with the same separator, so the
 *      original spacing survives.
 *
 * KEY INSIGHT
 *   Most per-word transforms are exactly this shape: split -> transform each token -> join.
 *   Recognise the shape, then spend your attention on the one thing that varies: the map step.
 *   Fixed: the original crashed on "" and on repeated spaces (substring(0,1) of an empty token).
 *
 * COMPLEXITY
 *   Time  O(n)  every character is visited a constant number of times
 *   Space O(n)  the word array and the joined result
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it with one StringBuilder pass and a "start of word" flag (no split, no streams).
 *   - Lower-case the remainder as well (title case) vs leaving it untouched.
 *   - Treat tabs/newlines as separators too: the flag approach handles any Character.isWhitespace.
 *   - Locale: toUpperCase() without a Locale is locale-sensitive (Turkish dotless i).
 *
 * RUN
 *   main() runs 4 cases (typical, empty, double space, non-letter start) and prints
 *   actual vs expected.
 */
import java.util.Arrays;
import java.util.stream.Collectors;

class FirstCharToUpperCase {

    static String capitalizeWords(String sentence) {
        return Arrays.stream(sentence.split(" "))
                .map(FirstCharToUpperCase::capitalizeFirst)
                .collect(Collectors.joining(" "));
    }

    private static String capitalizeFirst(String word) {
        if (word.isEmpty()) return word;             // empty token from "" or a double space
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static void check(String label, String input, String expected) {
        System.out.println(label + ": \"" + capitalizeWords(input)
                + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        check("case 1 (typical)       ", "ravi varma", "Ravi Varma");
        check("case 2 (empty)         ", "", "");
        check("case 3 (double space)  ", "hello  world", "Hello  World");
        check("case 4 (digit start)   ", "3d printing", "3d Printing");
    }
}
