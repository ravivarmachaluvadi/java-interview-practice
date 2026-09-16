import java.util.*;

/**
 * Function to find the maximum cherries
 * <p>
 * that can be collected recursively
 * <p>
 * Given two robots that can collect cherries, one is
 * <p>
 * located at the top-leftmost (0, 0) cell
 * <p>
 * and the other at the top-rightmost (0, m-1) cell.
 * <p>
 * Return the maximum number of cherries that can be picked
 * <p>
 * by the two robots in total, following these rules
 */
class CherryPickII {
    private int func(int i, int j1, int j2, int n, int m, int[][] matrix) {
        // Base cases
        if (j1 < 0 || j1 >= m || j2 < 0 || j2 >= m)
            return (int) (-1e9);

        if (i == n - 1) {
            if (j1 == j2)
                return matrix[i][j1];
            else
                return matrix[i][j1] + matrix[i][j2];
        }

        int maxi = Integer.MIN_VALUE;

        // Try all possible moves for both positions (j1, j2)
        // 8 directions
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ans;

                if (j1 == j2)
                    ans = matrix[i][j1] + func(i + 1, j1 + di, j2 + dj, n, m, matrix);
                else
                    ans = matrix[i][j1] + matrix[i][j2] + func(i + 1, j1 + di, j2 + dj, n, m, matrix);

                maxi = Math.max(maxi, ans);
            }
        }
        return maxi;
    }

    // Function to find maximum cherries that can be collected
    public int cherryPickup(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;

        // Return the maximum cherries collected
        return func(0, 0, m - 1, n, m, matrix);
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {2, 3, 1, 2},
                {3, 4, 2, 2},
                {5, 6, 3, 5}
        };

        // Create an instance of Solution class
        CherryPickII sol = new CherryPickII();

        // Call the function and print the result
        System.out.println(sol.cherryPickup(matrix));
    }
}
