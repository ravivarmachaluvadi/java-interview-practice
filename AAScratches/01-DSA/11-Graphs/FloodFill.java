
// https://leetcode.com/problems/flood-fill/description/

/**
 * Perform the same process for each pixel that is directly adjacent
 * <p>
 * (pixels that share a side with the original pixel, either horizontally
 * <p>
 * or vertically) and shares the same color as the starting pixel.
 * <p>
 * Example 1:
 * <p>
 * Input: image = [[1,1,1],
 * [1,1,0],
 * [1,0,1]],
 * sr = 1, sc = 1, color = 2
 * <p>
 * Output: [[2,2,2],
 * [2,2,0],
 * [2,0,1]]
 */
class FloodFill {
    // delRow -> del = delta
    private void dfs(int row, int col,
                     int[][] image,
                     int newColor, int[] delRow, int[] delCol,
                     int iniColor) {
        image[row][col] = newColor;
        int n = image.length, m = image[0].length;

        for (int i = 0; i < 4; i++) {
            int nrow = row + delRow[i];
            int ncol = col + delCol[i];
            if (nrow >= 0 && nrow < n && ncol >= 0 && ncol < m &&
                    image[nrow][ncol] == iniColor) {
                dfs(nrow, ncol, image, newColor, delRow, delCol, iniColor);
            }
        }
    }

    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        int iniColor = image[sr][sc];
        if (iniColor == newColor) return image; // avoid infinite loop
        int[] delRow = {-1, 0, 1, 0};
        int[] delCol = {0, 1, 0, -1};
        dfs(sr, sc, image, newColor, delRow, delCol, iniColor);
        return image;
    }

    public static void main(String[] args) {
        int[][] image = {
                {1, 1, 1},
                {1, 1, 0},
                {1, 0, 1}
        };

        // sr = 1, sc = 1, newColor = 2
        FloodFill obj = new FloodFill();
        int[][] ans = obj.floodFill(image, 1, 1, 2);
        for (int i = 0; i < ans.length; i++) {
            for (int j = 0; j < ans[i].length; j++)
                System.out.print(ans[i][j] + " ");
            System.out.println();
        }
    }

}
