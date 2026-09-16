// 1351. Count Negative Numbers in a Sorted Matrix
// https://leetcode.com/problems/count-negative-numbers-in-a-sorted-matrix/description/
class CountNegativeNumbersInASortedMatrix {
    public static int countNegatives(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        // initializing point last row and first column
        // last row may contain all of them as negative
        // numbres count may decrease by going up
        int row = m - 1, col = 0;
        int count = 0;

        while (row >= 0 && col < n) {
            if (grid[row][col] < 0) {
// if last contains all of them -ve then adding size of row
                count += (n - col);
                row--;
            } else {
                col++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[][] grid = {{4, 3, 2, -1}, {3, 2, 1, -1}, {1, 1, -1, -2}};
        System.out.println(countNegatives(grid)); // 4
    }
}