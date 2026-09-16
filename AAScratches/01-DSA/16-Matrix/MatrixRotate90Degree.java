import java.util.*;

/*
13 9 5 1
14 10 6 2
15 11 7 3
16 12 8 4
 */
// 90 Degree -> Transpose Matrix and reverse each row
// 180 Degree -> reverse each column and reverse each row
// 270 Degree -> Transpose Matrix and reverse each column
class MatrixRotate90Degree {

    static void rotate90(int[][] mat) {
        int n = mat.length;

        // Perform Transpose
        for (int i = 0; i < n; i++) {
            // remember -> int j = i + 1
            for (int j = i + 1; j < n; j++) {
                int temp = mat[i][j];
                mat[i][j] = mat[j][i];
                mat[j][i] = temp;
            }
        }

        // Reverse each row
        for (int i = 0; i < n; i++) {
            int start = 0, end = n - 1;
            while (start < end) {
                int temp = mat[i][start];
                mat[i][start] = mat[i][end];
                mat[i][end] = temp;
                start++;
                end--;
            }
        }
    }

    public static void main(String[] args) {
        int[][] mat = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        rotate90(mat);
        // Print the rotated matrix
        for (int[] row : mat) {
            for (int x : row) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }
}
