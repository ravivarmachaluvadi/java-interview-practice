/*
 * =====================================================================
 *  String Compression                              LeetCode 443 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a char array, replace each run of repeated characters with the character followed
 *   by the run length (omit the count when the run length is 1). The result must be written
 *   into the same array using O(1) extra space; return the new length.
 *
 * EXAMPLE
 *   [a,a,b,b,c,c,c]                ->  6, array starts "a2b2c3"
 *   [a]                            ->  1, array starts "a"       single char, no count
 *   [a,b,b,b,b,b,b,b,b,b,b,b,b]    ->  4, array starts "ab12"    multi-digit count
 *   [a,b,c]                        ->  3, array starts "abc"     nothing to compress
 *
 * APPROACH  (read pointer scans a run, write pointer records it)
 *   1. read sits at the start of a run; remember the char and advance read while the char
 *      repeats, counting as you go.
 *   2. Write the char at write++.
 *   3. If count > 1, write each digit of String.valueOf(count) at write++.
 *   4. Repeat until read reaches the end; write is the new length.
 *
 * KEY INSIGHT
 *   write never overtakes read: a run of k chars occupies k cells but is encoded in at most
 *   1 + digits(k) <= k cells, so writing into the same array is safe. This read/write split
 *   is the in-place rewrite pattern (Remove Duplicates, Move Zeroes) applied to runs.
 *
 * COMPLEXITY
 *   Time  O(n)  every cell is read once and written at most once
 *   Space O(1)  two indices and a count; the count string is a few chars at most
 *
 * INTERVIEW FOLLOW-UPS
 *   - Decompress "a2b12" back to the original array (parse multi-digit numbers).
 *   - Why return a length rather than a new String? The in-place constraint is the whole
 *     point; be ready to argue why write <= read always holds.
 *   - Write the count digits without String.valueOf: emit them reversed, then reverse the slice.
 *
 * RUN
 *   main() runs 4 cases (typical, single char, 12-run, no repeats) and prints actual vs
 *   expected for both the returned length and the compressed prefix.
 */
class StringCompression {

    public static int compress(char[] chars) {
        int write = 0;
        int read = 0;

        while (read < chars.length) {
            char runChar = chars[read];
            int count = 0;
            while (read < chars.length && chars[read] == runChar) {
                read++;
                count++;
            }

            chars[write++] = runChar;
            if (count > 1) {
                for (char digit : String.valueOf(count).toCharArray()) {
                    chars[write++] = digit;
                }
            }
        }
        return write;
    }

    /** Runs compress and formats "<length> \"<compressed prefix>\"" so one line shows both. */
    private static String compressAndShow(char[] chars) {
        int length = compress(chars);
        return length + " \"" + String.valueOf(chars, 0, length) + "\"";
    }

    public static void main(String[] args) {
        char[][] inputs = {
                {'a', 'a', 'b', 'b', 'c', 'c', 'c'},
                {'a'},
                {'a', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'},
                {'a', 'b', 'c'},
        };
        String[] expected = {"6 \"a2b2c3\"", "1 \"a\"", "4 \"ab12\"", "3 \"abc\""};

        for (int i = 0; i < inputs.length; i++) {
            String label = "case " + (i + 1) + " " + new String(inputs[i]);
            print(label, compressAndShow(inputs[i]), expected[i]);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
