// 3000. Maximum Area of Longest Diagonal Rectangle
// https://leetcode.com/problems/maximum-area-of-longest-diagonal-rectangle/description/
class MaximumAreaofLongestDiagonalRectangle {
    public int areaOfMaxDiagonal(int[][] dimensions) {
        int maxDiagonalSquared = 0;
        int maxArea = 0;

        for (int[] rect : dimensions) {
            int l = rect[0];
            int w = rect[1];
            int diagSq = l * l + w * w;
            int area = l * w;

            if (diagSq > maxDiagonalSquared) {
                maxDiagonalSquared = diagSq;
                maxArea = area;
            } else if (diagSq == maxDiagonalSquared) {
                if (area > maxArea) {
                    maxArea = area;
                }
            }
        }

        return maxArea;
    }

    public static void main(String[] args) {
        MaximumAreaofLongestDiagonalRectangle sol = new MaximumAreaofLongestDiagonalRectangle();

        // Example 1:
        int[][] dims1 = {{9, 3}, {8, 6}};
        System.out.println(sol.areaOfMaxDiagonal(dims1));  // expected output: 48

        // Example 2:
        int[][] dims2 = {{3, 4}, {4, 3}};
        System.out.println(sol.areaOfMaxDiagonal(dims2));  // expected output: 12

        // You can add more test cases below
    }
}
