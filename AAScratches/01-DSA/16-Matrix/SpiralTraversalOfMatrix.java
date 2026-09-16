import java.util.ArrayList;
import java.util.List;

class SpiralTraversalOfMatrix {
    // 1 2 3 4 8 12 16 15 14 13 9 5 6 7 11 10
    public static List<Integer> printSpiral(int[][] mat) {

        // Define ans list to store the result.
        List<Integer> ans = new ArrayList<>();

        int n = mat.length; // no. of rows
        int m = mat[0].length; // no. of columns

        // Initialize the pointers required for traversal.
        int top = 0, left = 0, bottom = n - 1, right = m - 1;

        // Loop until all elements are not traversed.
        while (top <= bottom && left <= right) {

            // For moving left to right
            for (int column = left; column <= right; column++)
                ans.add(mat[top][column]);

            top++;

            // For moving top to bottom.
            for (int row = top; row <= bottom; row++)
                ans.add(mat[row][right]);

            right--;

            // For moving right to left to cover bottom tow
            if (top <= bottom) {
                for (int column = right; column >= left; column--)
                    ans.add(mat[bottom][column]);

                bottom--;
            }

            // For moving bottom to top to cover left column
            if (left <= right) {
                for (int row = bottom; row >= top; row--)
                    ans.add(mat[row][left]);

                left++;
            }
        }
        return ans;
    }

    public static void main(String[] args) {

        //Matrix initialization.
        int[][] mat = {{1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}};

        List<Integer> ans = printSpiral(mat);

        for (int i = 0; i < ans.size(); i++) {
            System.out.print(ans.get(i) + " ");
        }

        System.out.println();
    }
}
