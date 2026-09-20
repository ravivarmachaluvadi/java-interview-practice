/*
 * =====================================================================
 *  Zigzag Conversion                                        LeetCode 6 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Write the string down numRows rows in a zigzag: straight down the first column, then
 *   diagonally up to row 0, then down again. Return the characters read row by row.
 *   numRows >= 1.
 *
 * EXAMPLE
 *   "PAYPALISHIRING", 3   ->  "PAHNAPLSIIGYIR"
 *       P   A   H   N
 *       A P L S I I G
 *       Y   I   R
 *   "HELLO", 2            ->  "HLOEL"
 *   "AB", 1               ->  "AB"                 edge: one row means no zigzag
 *   "ABC", 5              ->  "ABC"                edge: fewer chars than rows
 *   "ZIGZAGCONVERSION", 4 ->  "ZCSIGORIGANEOZVN"
 *
 * APPROACH  (row simulation with a direction flip)
 *   1. Return s unchanged when numRows == 1 or the string is no longer than numRows
 *      (each char lands in its own row, in order, so row-by-row reading is the input).
 *   2. Keep one StringBuilder per row, a currentRow index and a goingDown flag.
 *   3. For each char: append to rows[currentRow]; if currentRow is 0 or the last row,
 *      flip the direction; then move currentRow one step in that direction.
 *   4. Concatenate the rows top to bottom.
 *
 * KEY INSIGHT
 *   Do not compute column positions; the zigzag is just a row index bouncing between 0 and
 *   numRows - 1. Flip direction at the two walls and the geometry takes care of itself.
 *   The flag starts as "not going down" so the very first char at row 0 flips it to down.
 *
 * COMPLEXITY
 *   Time  O(n)  each char is appended once and copied once into the result
 *   Space O(n)  the row builders hold every char
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(1) extra space: row r holds indices r, r + cycle, r + 2*cycle, ... with
 *     cycle = 2 * numRows - 2, plus a diagonal char at k * cycle + cycle - r for inner rows.
 *   - Decode: given the zigzag output and numRows, recover the original string.
 *   - Why is the early return for numRows >= length correct and not a hack? Walk step 1.
 *
 * RUN
 *   main() runs 5 cases (typical, two rows, one row, string shorter than rows, four rows)
 *   and prints actual vs expected.
 */
class ZigzagConversion {

    public static String convert(String s, int numRows) {
        if (numRows == 1 || s.length() <= numRows) {
            return s;
        }

        StringBuilder[] rows = new StringBuilder[numRows];
        for (int i = 0; i < numRows; i++) {
            rows[i] = new StringBuilder();
        }

        int currentRow = 0;
        boolean goingDown = false;              // first char at row 0 flips this to true
        for (char c : s.toCharArray()) {
            rows[currentRow].append(c);
            boolean atWall = currentRow == 0 || currentRow == numRows - 1;
            if (atWall) {
                goingDown = !goingDown;
            }
            currentRow += goingDown ? 1 : -1;
        }

        StringBuilder result = new StringBuilder();
        for (StringBuilder row : rows) {
            result.append(row);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String[] inputs = {"PAYPALISHIRING", "HELLO", "AB", "ABC", "ZIGZAGCONVERSION"};
        int[] numRows = {3, 2, 1, 5, 4};
        String[] expected = {"PAHNAPLSIIGYIR", "HLOEL", "AB", "ABC", "ZCSIGORIGANEOZVN"};

        for (int i = 0; i < inputs.length; i++) {
            String label = "case " + (i + 1) + " \"" + inputs[i] + "\", " + numRows[i];
            print(label, convert(inputs[i], numRows[i]), expected[i]);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
