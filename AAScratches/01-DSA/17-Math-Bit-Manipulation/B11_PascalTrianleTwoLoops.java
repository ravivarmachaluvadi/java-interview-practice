/**
 * Problem: print the first N rows of Pascal's triangle (N = 5 here).
 *
 * Approaches:
 *   1. twoLoops - build each row from the previous row: cur[j] = prev[j-1] + prev[j].
 *                 O(N^2) time, O(N^2) space. Needs all earlier rows.
 *   2. byMath   - compute each row on its own from the binomial recurrence
 *                 C(n,k) = C(n,k-1) * (n-k+1) / k. No previous row needed,
 *                 so a single row (LeetCode 119 "Pascal's Triangle II") costs
 *                 only O(N) space. O(N^2) time for the whole triangle.
 */
import java.util.ArrayList;
import java.util.List;

class PascalTrianleTwoLoops {

    public static void main(String[] args) {
        int rows = 5;

        System.out.println("twoLoops (prev-row sums):");
        for (List<Integer> row : twoLoops(rows))
            System.out.println("  " + row);

        System.out.println("byMath (binomial recurrence):");
        for (List<Integer> row : byMath(rows))
            System.out.println("  " + row);
    }

    // ---------------------------------------------------------------
    // Approach 1: each row from the previous row
    //
    //        1
    //      1   1
    //    1   2   1      <- 2 = 1 + 1 from the row above
    //  1   3   3   1    <- 3 = 1 + 2, 3 = 2 + 1
    // ---------------------------------------------------------------
    static List<List<Integer>> twoLoops(int rows) {
        List<List<Integer>> triangle = new ArrayList<>();
        if (rows == 0) return triangle;

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

    // ---------------------------------------------------------------
    // Approach 2: each row directly, no previous row
    //
    // Row n (1-based) is C(n-1, 0), C(n-1, 1), ..., C(n-1, n-1).
    // Instead of factorials, walk left to right:
    //     next = prev * (n - col) / col
    //
    // 5th row -> [1, 4, 6, 4, 1]
    //   col 1: 1 * (5-1) / 1 = 4
    //   col 2: 4 * (5-2) / 2 = 6
    //   col 3: 6 * (5-3) / 3 = 4
    //   col 4: 4 * (5-4) / 4 = 1
    // Multiply BEFORE dividing: the product is always divisible, so no
    // integer-division loss. (Numerator 4*3*2*1 over denominator 1*2*3*4.)
    // ---------------------------------------------------------------
    static List<List<Integer>> byMath(int rows) {
        List<List<Integer>> triangle = new ArrayList<>();
        for (int i = 1; i <= rows; i++)
            triangle.add(generateRow(i));
        return triangle;
    }

    private static List<Integer> generateRow(int rowNumber) {
        List<Integer> pascalRow = new ArrayList<>();
        pascalRow.add(1);
        int ans = 1;
        for (int col = 1; col < rowNumber; col++) {
            ans = ans * (rowNumber - col) / col;
            pascalRow.add(ans);
        }
        return pascalRow;
    }
}
