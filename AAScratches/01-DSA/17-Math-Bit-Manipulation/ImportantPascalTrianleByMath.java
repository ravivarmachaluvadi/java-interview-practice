/**
 * Generates the first five rows of Pascal's Triangle using a mathematical
 * approach that computes each element from its predecessor.
 *
 * For a given row number n, the k-th element (0‑based) is calculated as:
 *   value = previousValue * (n - k) / k
 * starting with 1 for the first element. This avoids recomputing factorials.
 *
 * The program builds each row iteratively and prints all rows to standard output.
 *
 * Time Complexity: O(n²), where n is the number of rows (here 5). Each row i requires i operations.
 * Space Complexity: O(n²) for storing the triangle; each element is an Integer in a list.
 */
import java.util.ArrayList;
import java.util.List;

class ImportantPascalTrianleByMath {
    public static void main(String[] args) {
        List<List<Integer>> pascalTriangle = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            List<Integer> generatedRow = generateRow(i);
            pascalTriangle.add(generatedRow);
        }

        for (List<Integer> integerList : pascalTriangle) {
            System.out.println(integerList);
        }

    }

    //    5*4*3*2*1
    //    1*2*3*4*5 <=

    //     5th row [1, 4, 6, 4, 1]
    //     (row-col)/col-->5-1/1--> 2nd Element
    private static List<Integer> generateRow(int rowNumber) {
        List<Integer> pascalRow = new ArrayList<>();
        pascalRow.add(1);
        int ans = 1;
        for (int col = 1; col < rowNumber; col++) {

            // second element
            ans = ans * (rowNumber - col);
            ans = ans / col;

            pascalRow.add(ans);

        }
        return pascalRow;
    }
}