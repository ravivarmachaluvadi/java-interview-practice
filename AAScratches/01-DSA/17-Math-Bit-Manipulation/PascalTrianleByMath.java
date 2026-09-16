/**
 * Generates the first five rows of Pascal's Triangle using a mathematical
 * binomial coefficient approach.
 *
 * For each row i (1‑based), it starts with 1 and iteratively computes the next
 * element by multiplying the current value by (i - col) and dividing by col,
 * which yields C(i-1, col). The resulting list of rows is printed to stdout.
 *
 * Time Complexity: O(n²) where n is the number of rows (here 5), due to nested
 * loops for row generation. Space Complexity: O(n²) for storing all rows in a
 * list of lists. 
 */
import java.util.ArrayList;
import java.util.List;

class PascalTrianleByMath {
    public static void main(String[] args) {

//    5*4*3*2*1
//    1*2*3*4*5 <=

//     5th row [1, 4, 6, 4, 1]
//      (row-col)/col-->5-1/1--> 2nd Element
        List<List<Integer>> pascalTriangle = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            List<Integer> generatedRow = generateRow(i);
            pascalTriangle.add(generatedRow);
        }

        for (List<Integer> integerList : pascalTriangle) {
            System.out.println(integerList);
        }
    }

    private static List<Integer> generateRow(int rowNumber) {
        List<Integer> pascalRow = new ArrayList<>();
        pascalRow.add(1);
        int ans = 1;
        for (int col = 1; col < rowNumber; col++) {
            ans *= (rowNumber - col);
            ans /= col;
            pascalRow.add(ans);
        }
        return pascalRow;
    }
}
