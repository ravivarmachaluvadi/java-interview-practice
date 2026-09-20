/*
 * =====================================================================
 *  Minimum Number of Arrows to Burst Balloons          LeetCode 452 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Each balloon is a horizontal interval points[i] = [start, end]. An arrow shot straight up
 *   at coordinate x bursts every balloon whose interval contains x (touching counts: start <= x
 *   and x <= end). Return the minimum number of arrows needed to burst all balloons.
 *   Coordinates can be anywhere in the full int range, so never compare by subtraction.
 *
 * EXAMPLE
 *   [[10,16],[2,8],[1,6],[7,12]]  ->  2    shoot at x=6 and x=12
 *   [[1,2],[3,4],[5,6],[7,8]]     ->  4    nothing overlaps, one arrow each
 *   [[1,2],[2,3]]                 ->  1    they touch at x=2, still one arrow
 *   []                            ->  0    nothing to burst
 *
 * APPROACH  (greedy grouping by end time)
 *   1. Sort the balloons by their END coordinate (not start).
 *   2. Shoot the first arrow at the smallest end coordinate; remember it as `end`.
 *   3. Walk the rest in sorted order. If a balloon starts at or before `end` the current arrow
 *      already pierces it, so skip it.
 *   4. If a balloon starts strictly after `end`, the current arrow cannot reach it: shoot a new
 *      arrow, count it, and move `end` to this balloon's end coordinate.
 *   5. The arrow count is the answer.
 *
 * KEY INSIGHT
 *   Placing the arrow at the earliest end coordinate is never worse than placing it anywhere
 *   else: any balloon a later position could hit, the earliest end also hits, because every
 *   surviving balloon must still be open at that point. That is the classic activity-selection
 *   exchange argument. Recognise the pattern: "minimum number of stabbing points" and
 *   "maximum non-overlapping intervals" are the SAME sort-by-end greedy; LeetCode 435
 *   (C06_NonOverlappingIntervals) reports removals instead of groups, with one comparison
 *   flipped for touching intervals. Sort by start answers union questions (Merge
 *   Intervals); sort by end answers selection questions.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the sweep afterwards is a single O(n) pass
 *   Space O(n)        Arrays.sort on an object array is TimSort, which allocates a temp
 *                     buffer of up to n/2; the scan itself uses only two variables
 *
 * INTERVIEW FOLLOW-UPS
 *   - LeetCode 435 (C06_NonOverlappingIntervals): the same sort-by-end greedy, but touching
 *     intervals do NOT overlap there, so the test is start >= lastEnd instead of start > end,
 *     and the counts are NOT related by n - arrows.
 *   - Why sort by end and not by start? Give a counterexample where sort-by-start greedy fails.
 *   - Print the actual arrow coordinates, not just the count.
 *   - What if arrows had a horizontal width w, bursting [x, x + w]?
 *
 * RUN
 *   main() runs 5 cases (typical, disjoint, touching endpoints, single, empty) and prints
 *   actual vs expected.
 */

import java.util.Arrays;
import java.util.Comparator;

class MinimumArrowsToBurstBalloons {

    public static int findMinArrowShots(int[][] points) {
        if (points == null || points.length == 0) return 0;

        // Sort by END coordinate. comparingInt (not a[1] - b[1]) so huge coordinates cannot
        // overflow int and scramble the order.
        Arrays.sort(points, Comparator.comparingInt(a -> a[1]));

        int arrows = 1;             // the first balloon always needs an arrow
        int end = points[0][1];     // where that arrow is currently standing

        for (int i = 1; i < points.length; i++) {
            // points[i][0] <= end means the balloon is still open at `end`, so the arrow
            // already in the air bursts it and we spend nothing.
            if (points[i][0] > end) {
                arrows++;
                end = points[i][1];  // new arrow moves to this group's earliest end
            }
        }
        return arrows;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ",
                findMinArrowShots(new int[][]{{10, 16}, {2, 8}, {1, 6}, {7, 12}}), 2);
        print("case 2 disjoint  ",
                findMinArrowShots(new int[][]{{1, 2}, {3, 4}, {5, 6}, {7, 8}}), 4);
        print("case 3 touching  ", findMinArrowShots(new int[][]{{1, 2}, {2, 3}}), 1);
        print("case 4 single    ", findMinArrowShots(new int[][]{{5, 9}}), 1);
        print("case 5 empty     ", findMinArrowShots(new int[][]{}), 0);
    }
}
