/**
 * Given a triangle array, return the minimum
 * <p>
 * path sum from top to bottom.
 * <p>
 * For each step, you may move to an adjacent number of
 * <p>
 * the row below. More formally, if you are on index i on
 * <p>
 * the current row, you may move to either
 * <p>
 * index i or index i + 1 on the next row.
 */
class Triangle {
    //14
    private int func(int[][] triangle, int n) {
        int[] bottom = new int[n];
        int[] cur = new int[n];

        for (int j = 0; j < n; j++)
            bottom[j] = triangle[n - 1][j];

        for (int i = n - 2; i >= 0; i--) {
            for (int j = i; j >= 0; j--) {
                // Calculate minimum path sum for current cell
                int currVal = triangle[i][j];
                int down = currVal + bottom[j];
                int diagonal = currVal + bottom[j + 1];

                cur[j] = Math.min(down, diagonal);
            }
            bottom = cur.clone();
        }
        return bottom[0];
    }

    public int minTriangleSum(int[][] triangle) {
        int n = triangle.length;
        return func(triangle, n);
    }

    public static void main(String[] args) {
        int[][] triangle = {
                {1},
                {2, 3},
                {3, 6, 7},
                {8, 9, 6, 10}
        };

        Triangle sol = new Triangle();

        System.out.println(sol.minTriangleSum(triangle));
    }
}
