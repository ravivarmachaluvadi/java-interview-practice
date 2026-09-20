/*
 * =====================================================================
 *  Capitalize First and Last Character of Each Word              classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a sentence of words separated by single spaces, upper-case the first and the
 *   last character of every word and leave everything else untouched. A one-letter word
 *   simply becomes upper-case. Spacing is preserved exactly.
 *
 * EXAMPLE
 *   "hello world from java"  ->  "HellO WorlD FroM JavA"
 *   "a"                      ->  "A"
 *   ""                       ->  ""
 *   "hi  there"              ->  "HI  TherE"    double space preserved
 *
 * APPROACH  (split, setCharAt on each word, join)
 *   1. split(" ") the sentence into words (empty tokens appear for repeated spaces).
 *   2. For each non-empty word, wrap it in a StringBuilder and setCharAt(0) and
 *      setCharAt(length - 1) to their upper-case forms; empty tokens pass through.
 *   3. String.join(" ", words) puts the spaces back.
 *
 * KEY INSIGHT
 *   Strings are immutable, so per-word edits need a StringBuilder; setCharAt is the
 *   in-place mutation that avoids rebuilding the word with substring concatenation.
 *   Index 0 and length - 1 coincide for a one-letter word, which is why no special case
 *   is needed there. The only real trap is the EMPTY token, which has no index 0 at all.
 *
 * Fixed: setCharAt(0) on an empty token threw StringIndexOutOfBoundsException for input ""
 *   and for any run of two or more spaces; empty tokens are now returned unchanged.
 *
 * COMPLEXITY
 *   Time  O(n)  split, per-word edit and join each touch every character once
 *   Space O(n)  the word array and the joined result
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in one pass over a char[] without split: a char is "first" if i == 0 or
 *     s[i-1] == ' ', and "last" if i == n-1 or s[i+1] == ' '.
 *   - Split on any whitespace (\\s+) and collapse spacing, or keep tabs and newlines intact.
 *   - Title Case only the first letter (see A02) or the whole word (toUpperCase).
 *
 * RUN
 *   main() runs 4 cases (typical, one-letter word, empty, double space) and prints actual
 *   vs expected.
 */
class CapitalizeFirstAndLastCharacterOfEachWord {

    static String capitalizeFirstLast(String sentence) {
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = capitalizeEnds(words[i]);
        }
        return String.join(" ", words);
    }

    /** Upper-cases index 0 and index length-1 of one word; both are the same for "a". */
    private static String capitalizeEnds(String word) {
        if (word.isEmpty()) return word; // empty token from "" or a double space
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.setCharAt(sb.length() - 1, Character.toUpperCase(sb.charAt(sb.length() - 1)));
        return sb.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"hello world from java\"", capitalizeFirstLast("hello world from java"),
                "HellO WorlD FroM JavA");
        print("case 2 \"a\"", capitalizeFirstLast("a"), "A");
        print("case 3 \"\"", capitalizeFirstLast(""), "");
        print("case 4 \"hi  there\"", capitalizeFirstLast("hi  there"), "HI  TherE");
    }
}
