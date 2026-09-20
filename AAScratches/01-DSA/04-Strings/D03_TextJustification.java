/*
 * =====================================================================
 *  Text Justification                                  LeetCode 68 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given words and maxWidth, pack the words greedily into lines of exactly maxWidth
 *   characters. Spare spaces go between words, spread as evenly as possible, with any
 *   remainder given to the leftmost gaps. The last line, and any line holding a single word,
 *   is left-justified: single spaces between words and padding at the end. Every word fits.
 *
 * EXAMPLE
 *   ["This","is","an","example","of","text","justification."], 16
 *     -> "This    is    an"     3 words, 16 - 8 = 8 spaces over 2 gaps -> 4 and 4
 *        "example  of text"     3 words, 16 - 13 = 3 spaces over 2 gaps -> 2 and 1 (left +1)
 *        "justification.  "     last line: left-justified, padded
 *   ["a"], 3  ->  ["a  "]       single word, single line
 *
 * APPROACH  (greedy line packing, then per-line space distribution)
 *   1. Walk the words keeping currentLine and lettersInLine (sum of word lengths, no spaces).
 *      The next word fits if lettersInLine + word.length() + currentLine.size() <= maxWidth:
 *      currentLine.size() is the number of gaps once this word joins the line.
 *   2. When it does not fit, flush: justifyLine() spreads maxWidth - lettersInLine spaces over
 *      size - 1 gaps; each gap gets total / gaps and the first total % gaps gaps get one more.
 *   3. A flushed line with a single word has no gap to spread over, so pad it on the right.
 *   4. After the loop the leftover words form the last line: join with single spaces, pad.
 *
 * KEY INSIGHT
 *   Greedy packing is optimal here (deferring a word never lets more fit later), so all the
 *   difficulty is bookkeeping: gaps = words - 1, the quotient goes to every gap, the remainder
 *   goes to the leftmost gaps, and two exceptions are left-justified (single word, last line).
 *   Keep justifyLine() and leftJustifyLastLine() separate so each rule lives in one place.
 *
 * COMPLEXITY
 *   Time  O(total characters)  each word is visited once and each output char written once
 *   Space O(total characters)  the output; the current-line buffer is O(maxWidth)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is greedy right here but "minimise raggedness" (sum of squared slack) needs DP?
 *   - Right-justify or centre instead: which helper changes and how?
 *   - A word longer than maxWidth: hyphenate, or reject the input?
 *   - Streaming input: lines can be emitted as soon as they flush, without seeing all words.
 *
 * RUN
 *   main() runs 4 cases and prints actual vs expected line lists.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TextJustification {

    public static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        List<String> currentLine = new ArrayList<>();
        int lettersInLine = 0;      // sum of word lengths only; spaces are not counted here

        for (String word : words) {
            // currentLine.size() == number of single spaces needed once this word joins
            boolean fits = lettersInLine + word.length() + currentLine.size() <= maxWidth;
            if (!fits) {
                result.add(justifyLine(currentLine, lettersInLine, maxWidth));
                currentLine.clear();
                lettersInLine = 0;
            }
            currentLine.add(word);
            lettersInLine += word.length();
        }
        result.add(leftJustifyLastLine(currentLine, maxWidth));
        return result;
    }

    /** Spread all spare spaces over the gaps; the leftmost gaps take the remainder. */
    private static String justifyLine(List<String> line, int lettersInLine, int maxWidth) {
        int totalSpaces = maxWidth - lettersInLine;
        if (line.size() == 1) {
            return line.get(0) + " ".repeat(totalSpaces);   // no gap to spread over
        }
        int gaps = line.size() - 1;
        int spacesPerGap = totalSpaces / gaps;
        int extraSpaces = totalSpaces % gaps;                // first extraSpaces gaps get +1

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.size(); i++) {
            sb.append(line.get(i));
            if (i < gaps) {
                sb.append(" ".repeat(spacesPerGap + (i < extraSpaces ? 1 : 0)));
            }
        }
        return sb.toString();
    }

    /** Last line: single spaces between words, then pad to maxWidth on the right. */
    private static String leftJustifyLastLine(List<String> line, int maxWidth) {
        String joined = String.join(" ", line);
        return joined + " ".repeat(maxWidth - joined.length());
    }

    /** Prints each line in quotes so the padding is visible. */
    private static String quoted(List<String> lines) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append('"').append(lines.get(i)).append('"');
        }
        return sb.append(']').toString();
    }

    private static void check(String label, String[] words, int maxWidth, List<String> expected) {
        List<String> actual = fullJustify(words, maxWidth);
        System.out.println(label + ": " + quoted(actual) + "   expected " + quoted(expected)
                + "   match=" + actual.equals(expected));
    }

    public static void main(String[] args) {
        // typical: even split (4,4), uneven split (2,1), left-justified last line
        check("case 1",
                new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16,
                Arrays.asList("This    is    an", "example  of text", "justification.  "));

        // edge: a single word is both a one-word line and the last line
        check("case 2", new String[]{"a"}, 3, Arrays.asList("a  "));

        // tricky: a one-word line in the middle ("acknowledgment") must be padded, not spread
        check("case 3",
                new String[]{"What", "must", "be", "acknowledgment", "shall", "be."}, 16,
                Arrays.asList("What   must   be", "acknowledgment  ", "shall be.       "));

        // tricky: remainder over 3 gaps ("Science  is  what we" -> 2,2,1) and a padded last line
        check("case 4",
                new String[]{"Science", "is", "what", "we", "understand", "well", "enough", "to",
                        "explain", "to", "a", "computer.", "Art", "is", "everything", "else",
                        "we", "do"}, 20,
                Arrays.asList("Science  is  what we", "understand      well",
                        "enough to explain to", "a  computer.  Art is",
                        "everything  else  we", "do                  "));
    }
}
