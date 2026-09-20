/*
 * =====================================================================
 *  Pascal's Triangle                       LeetCode 118 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a row count n, return the first n rows of Pascal's triangle.
 *   Row i (1-based) has i entries; the edges are 1 and every inner entry is the
 *   sum of the two entries above it. n is small (LeetCode caps it at 30).
 *
 * EXAMPLE
 *   n = 5  ->  [[1], [1, 1], [1, 2, 1], [1, 3, 3, 1], [1, 4, 6, 4, 1]]
 *   n = 1  ->  [[1]]
 *   n = 0  ->  []            (edge case main() runs)
 *
 * APPROACH 1  (row from previous row - the "two loops" build)
 *   1. Seed the triangle with the single-element row [1].
 *   2. For each next row of length i, walk j from 0 to i-1.
 *   3. If j is the first or last slot, write 1 (edges never change).
 *   4. Otherwise write prevRow[j-1] + prevRow[j].
 *   5. Append the row and make it the new prevRow.
 *
 * APPROACH 2  (binomial recurrence - each row on its own)
 *   1. Row n is C(n-1, 0), C(n-1, 1), ..., C(n-1, n-1).
 *   2. Start at 1 and slide right with next = prev * (n - col) / col.
 *   3. Multiply BEFORE dividing: the running product is always divisible,
 *      so integer division never truncates.
 *
 * KEY INSIGHT
 *   The triangle is the binomial coefficients laid out in a grid, so you have a
 *   choice: reuse the row above (simple, needs O(n) memory of history) or derive
 *   a row from nothing with the ratio C(n,k) = C(n,k-1) * (n-k+1) / k. The ratio
 *   form is what makes "return only row k" (LeetCode 119) cost O(k) space.
 *
 * COMPLEXITY
 *   Time  O(n^2)  both approaches - the triangle itself has n(n+1)/2 entries
 *   Space O(n^2)  for the returned triangle; one row alone needs only O(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return only the k-th row using O(k) extra space (LeetCode 119).
 *   - Why does multiplying before dividing stay exact in integer math?
 *   - Where does int overflow first, and which row number causes it?
 *   - Compute a single C(n, k) modulo a prime without building the triangle.
 *
 * RUN
 *   main() runs 5 cases (typical, n = 0 and n = 1 edges, a large row) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class PascalTrianleTwoLoops {

    public static void main(String[] args) {
        print("twoLoops(5)", twoLoops(5),
              "[[1], [1, 1], [1, 2, 1], [1, 3, 3, 1], [1, 4, 6, 4, 1]]");
        print("byMath(5)  ", byMath(5),
              "[[1], [1, 1], [1, 2, 1], [1, 3, 3, 1], [1, 4, 6, 4, 1]]");

        print("twoLoops(0)", twoLoops(0), "[]");
        print("twoLoops(1)", twoLoops(1), "[[1]]");

        // Tricky: row 15 exercises the multiply-then-divide rule hardest.
        print("row 15     ", byMath(15).get(14),
              "[1, 14, 91, 364, 1001, 2002, 3003, 3432, 3003, 2002, 1001, 364, 91, 14, 1]");
    }

    /**
     * Approach 1: each row is built from the row above.
     *
     *        1
     *      1   1
     *    1   2   1      <- 2 = 1 + 1 from the row above
     *  1   3   3   1    <- 3 = 1 + 2, 3 = 2 + 1
     */
    static List<List<Integer>> twoLoops(int rows) {
        List<List<Integer>> triangle = new ArrayList<>();
        if (rows <= 0) return triangle;

        List<Integer> prevRow = List.of(1);
        triangle.add(prevRow);

        for (int i = 2; i <= rows; i++) {            // i = length of the row being built
            List<Integer> currentRow = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                if (j == 0 || j == i - 1)
                    currentRow.add(1);               // edges are always 1
                else
                    currentRow.add(prevRow.get(j - 1) + prevRow.get(j));
            }
            triangle.add(currentRow);
            prevRow = currentRow;
        }
        return triangle;
    }

    /** Approach 2: every row computed on its own, no history needed. */
    static List<List<Integer>> byMath(int rows) {
        List<List<Integer>> triangle = new ArrayList<>();
        for (int i = 1; i <= rows; i++)
            triangle.add(generateRow(i));
        return triangle;
    }

    /**
     * Row n (1-based) = C(n-1, 0) .. C(n-1, n-1), walked left to right with
     *     next = prev * (n - col) / col
     *
     * 5th row -> [1, 4, 6, 4, 1]
     *   col 1: 1 * (5-1) / 1 = 4
     *   col 2: 4 * (5-2) / 2 = 6
     *   col 3: 6 * (5-3) / 3 = 4
     *   col 4: 4 * (5-4) / 4 = 1
     */
    private static List<Integer> generateRow(int rowNumber) {
        List<Integer> pascalRow = new ArrayList<>();
        pascalRow.add(1);
        int value = 1;
        for (int col = 1; col < rowNumber; col++) {
            // multiply first: the numerator so far is always divisible by col
            value = value * (rowNumber - col) / col;
            pascalRow.add(value);
        }
        return pascalRow;
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
