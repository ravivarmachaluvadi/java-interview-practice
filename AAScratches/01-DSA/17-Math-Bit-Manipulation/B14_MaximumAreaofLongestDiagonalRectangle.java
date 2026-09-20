/*
 * =====================================================================
 *  Maximum Area of Longest Diagonal Rectangle      LeetCode 3000 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   dimensions[i] = [length, width] describes one rectangle. Find the rectangle
 *   with the longest diagonal and return its area. If several rectangles tie on
 *   diagonal length, return the largest area among them. Sides are at least 1
 *   and at most 100, so every diagonal-squared value fits comfortably in an int.
 *
 * EXAMPLE
 *   [[9, 3], [8, 6]]  ->  48   diagonals^2 are 90 and 100; 100 wins, area 8*6
 *   [[3, 4], [4, 3]]  ->  12   same diagonal, same area
 *   [[1, 7], [5, 5]]  ->  25   both diagonals^2 are 50; the tie goes to area 25
 *
 * APPROACH  (single pass, max with a secondary tie-break key)
 *   1. Track two running values: the best diagonal-squared and the area that
 *      goes with it.
 *   2. For each rectangle compute diagSq = l*l + w*w and area = l*w.
 *   3. If diagSq beats the best, adopt this rectangle outright - its area
 *      replaces the stored area even if it is smaller.
 *   4. If diagSq only ties the best, keep the larger of the two areas.
 *   5. Return the stored area after the pass.
 *
 * KEY INSIGHT
 *   Never call Math.sqrt here. The diagonal is sqrt(l^2 + w^2) and sqrt is
 *   monotonic, so comparing the squares orders the rectangles identically while
 *   staying in exact integer arithmetic - no floating-point ties that are really
 *   equal but compare unequal. The second half of the pattern is the ordered
 *   comparison: primary key strictly greater means take over, primary key equal
 *   means fall through to the secondary key.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over the rectangles, constant work each
 *   Space O(1)  two running integers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the index of the winning rectangle instead of its area.
 *   - If sides could reach 1e9, where does l*l + w*w overflow, and what type
 *     fixes it?
 *   - Sort-based version: what comparator expresses the same two keys?
 *   - Extend to boxes: longest space diagonal l^2 + w^2 + h^2, tie-break volume.
 *
 * RUN
 *   main() runs 4 cases (typical, exact tie, single rectangle, tie broken by the
 *   secondary key) and prints actual vs expected.
 */
class MaximumAreaofLongestDiagonalRectangle {

    public int areaOfMaxDiagonal(int[][] dimensions) {
        int maxDiagonalSquared = 0;
        int maxArea = 0;

        for (int[] rect : dimensions) {
            int length = rect[0];
            int width = rect[1];
            int diagSq = length * length + width * width;   // compare squares, never sqrt
            int area = length * width;

            if (diagSq > maxDiagonalSquared) {
                maxDiagonalSquared = diagSq;
                maxArea = area;                             // longer diagonal wins outright
            } else if (diagSq == maxDiagonalSquared && area > maxArea) {
                maxArea = area;                             // tie on diagonal -> bigger area
            }
        }

        return maxArea;
    }

    public static void main(String[] args) {
        MaximumAreaofLongestDiagonalRectangle sol = new MaximumAreaofLongestDiagonalRectangle();

        print("case 1 [[9,3],[8,6]]", sol.areaOfMaxDiagonal(new int[][]{{9, 3}, {8, 6}}), 48);
        print("case 2 [[3,4],[4,3]]", sol.areaOfMaxDiagonal(new int[][]{{3, 4}, {4, 3}}), 12);
        print("case 3 [[2,3]]      ", sol.areaOfMaxDiagonal(new int[][]{{2, 3}}), 6);

        // Tricky: equal diagonals (1+49 = 25+25 = 50), so the area tie-break decides,
        // and the winner is the SECOND rectangle.
        print("case 4 [[1,7],[5,5]]", sol.areaOfMaxDiagonal(new int[][]{{1, 7}, {5, 5}}), 25);
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
