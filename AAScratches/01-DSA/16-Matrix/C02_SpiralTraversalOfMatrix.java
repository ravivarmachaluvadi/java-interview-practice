/*
 * =====================================================================
 *  Spiral Matrix                          LeetCode 54 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n matrix, return all of its elements in spiral order:
 *   left to right across the top, down the right side, right to left across
 *   the bottom, up the left side, then repeat on the shrunken rectangle.
 *   The matrix is not necessarily square; m, n >= 1.
 *
 * EXAMPLE
 *   [[1,2,3,4],[5,6,7,8],[9,10,11,12],[13,14,15,16]]
 *       -> [1,2,3,4,8,12,16,15,14,13,9,5,6,7,11,10]
 *   [[1,2,3,4],[5,6,7,8],[9,10,11,12]] -> [1,2,3,4,8,12,11,10,9,5,6,7]
 *   [[1,2,3]]   -> [1,2,3]     single row, no vertical legs at all
 *   [[1],[2],[3]] -> [1,2,3]   single column, no bottom leg at all
 *
 * APPROACH  (four shrinking boundary pointers)
 *   1. Keep top, bottom, left, right as the walls of the untouched rectangle.
 *   2. While top <= bottom and left <= right, walk the four legs in order:
 *        a. top row, left -> right, then top++
 *        b. right column, top -> bottom, then right--
 *        c. bottom row, right -> left, then bottom--
 *        d. left column, bottom -> top, then left++
 *   3. Guard legs (c) and (d): after top++ and right-- the rectangle may have
 *      collapsed, and walking it again would emit the same cells twice.
 *   4. Stop when the walls cross; every cell is visited exactly once.
 *
 * KEY INSIGHT
 *   A spiral is not a direction-and-turn simulation, it is four walls closing
 *   in. Each leg consumes a whole row or column and then retires that wall, so
 *   no visited[][] array is needed. The two guards are the entire difficulty:
 *   a leftover single row would be emitted by leg (a) and again by leg (c),
 *   and a leftover single column by leg (b) and again by leg (d). Recognise
 *   this idiom for Spiral Matrix II (fill instead of read) and for rotating or
 *   peeling a matrix ring by ring.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each cell is appended exactly once
 *   Space O(1)      besides the output list, only four int pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Spiral Matrix II: fill an n x n matrix with 1..n*n in spiral order.
 *   - Spiral Matrix III: start off-centre and spiral outward past the bounds.
 *   - Walk the spiral counter-clockwise, or start from the bottom-right.
 *   - Return only the k-th element of the spiral without building the list.
 *
 * RUN
 *   main() runs 4 cases (square, rectangular, single row, single column)
 *   and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class SpiralTraversalOfMatrix {

    public static List<Integer> printSpiral(int[][] mat) {
        List<Integer> ans = new ArrayList<>();
        if (mat == null || mat.length == 0 || mat[0].length == 0) return ans;

        int rows = mat.length;
        int cols = mat[0].length;

        // Walls of the rectangle that still has unvisited cells.
        int top = 0, left = 0, bottom = rows - 1, right = cols - 1;

        while (top <= bottom && left <= right) {

            // Leg 1: top row, left -> right.
            for (int column = left; column <= right; column++) {
                ans.add(mat[top][column]);
            }
            top++;

            // Leg 2: right column, top -> bottom.
            for (int row = top; row <= bottom; row++) {
                ans.add(mat[row][right]);
            }
            right--;

            // Leg 3: bottom row, right -> left.
            // Guard: after top++ the rectangle may be empty vertically, and
            // re-walking it would print the leftover single row twice.
            if (top <= bottom) {
                for (int column = right; column >= left; column--) {
                    ans.add(mat[bottom][column]);
                }
                bottom--;
            }

            // Leg 4: left column, bottom -> top.
            // Guard: after right-- the rectangle may be empty horizontally,
            // which would print the leftover single column twice.
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    ans.add(mat[row][left]);
                }
                left++;
            }
        }
        return ans;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] square = {{1, 2, 3, 4},
                          {5, 6, 7, 8},
                          {9, 10, 11, 12},
                          {13, 14, 15, 16}};
        print("case 1 (4x4)", printSpiral(square),
                "[1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10]");

        int[][] wide = {{1, 2, 3, 4},
                        {5, 6, 7, 8},
                        {9, 10, 11, 12}};
        print("case 2 (3x4)", printSpiral(wide),
                "[1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]");

        int[][] singleRow = {{1, 2, 3}};
        print("case 3 (1x3)", printSpiral(singleRow), "[1, 2, 3]");

        int[][] singleCol = {{1}, {2}, {3}};
        print("case 4 (3x1)", printSpiral(singleCol), "[1, 2, 3]");
    }
}
