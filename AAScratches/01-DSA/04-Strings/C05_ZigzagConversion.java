/**
 * Problem:
 *   Convert a string into its zigzag representation across a given number of rows
 *   and read the result row by row.
 *
 * Approach:
 *   Simulate the zigzag traversal using an array of StringBuilders, one per row.
 *   Move a pointer downwards until the last row, then upwards, toggling direction at each end.
 *   Append characters to the current row, then concatenate all rows for the final string.
 *
 * Complexity:
 *   Time:  O(n) – each character is processed once.
 *   Space: O(n) – storage for the StringBuilders holding all characters.
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
        boolean goingDown = false;

        for (char c : s.toCharArray()) {
            rows[currentRow].append(c);
            if (currentRow == 0 || currentRow == numRows - 1) {
                goingDown = !goingDown; // Change direction
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
        // Example test cases
        String[] testStrings = {"PAYPALISHIRING", "HELLO", "ZIGZAGCONVERSION"};
        int[] numRows = {3, 2, 4};

        for (int i = 0; i < testStrings.length; i++) {
            String s = testStrings[i];
            int rows = numRows[i];
            System.out.println("Input: \"" + s + "\", numRows: " + rows);
            System.out.println("Zigzag Output: \"" + convert(s, rows) + "\"");
            System.out.println();
        }
    }
}
