import java.util.HashSet;
import java.util.Set;

class SetMatrixZeroes {

    public void setZeroes(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Set<Integer> rowzero = new HashSet<>();
        Set<Integer> colzero = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    rowzero.add(i);
                    colzero.add(j);
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rowzero.contains(i) || colzero.contains(j)) {
                    matrix[i][j] = 0;
                }
            }
        }
    }
}
