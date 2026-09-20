/*
 * =====================================================================
 *  Magnetic Force Between Two Balls                 LeetCode 1552 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   position[i] is the coordinate of the i-th basket (unsorted, distinct). Place m balls
 *   (2 <= m <= n) into baskets so that the smallest distance between any two balls is as
 *   large as possible. Return that largest possible minimum distance.
 *
 * EXAMPLE
 *   position = [1, 2, 3, 4, 7], m = 3           ->  3          baskets 1, 4, 7; gaps 3, 3
 *   position = [5, 4, 3, 2, 1, 1000000000], m = 2 ->  999999999  the two extremes
 *   position = [1, 2], m = 2                    ->  1          only one way to place them
 *   position = [1, 2, 3, 4, 5], m = 5           ->  1          every basket used, gap 1
 *
 * APPROACH  (binary search on the answer: maximum feasible gap)
 *   1. Sort positions so a greedy left-to-right scan is possible.
 *   2. Bounds: gap is in [1, max - min]; the largest gap is the two extreme baskets.
 *   3. Predicate canPlaceBalls(gap): put the first ball in the leftmost basket, then walk
 *      right and drop a ball in the first basket at least 'gap' away from the previous ball.
 *      True if we manage to place m balls.
 *   4. The predicate is monotone in the opposite direction from Koko: a smaller gap is always
 *      easier, so the answer space is T T T F F F and we want the LAST true.
 *   5. If canPlaceBalls(mid): remember mid as best and search right (left = mid + 1);
 *      else search left (right = mid - 1).
 *
 * KEY INSIGHT
 *   "Maximize the minimum" is the mirror of "minimize the maximum". The template is identical
 *   (bounds, predicate, binary search) but the feasible region is T T T F F F, so the branch
 *   that records the answer is the TRUE branch moving right. Getting this direction wrong is
 *   the most common bug in the family; say out loud which side the trues are on.
 *
 * COMPLEXITY
 *   Time  O(n log n + n log D)  sorting, then log D probes (D = max - min) each scanning n
 *   Space O(1)                  beyond the in-place sort
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is placing the first ball at position[0] always safe? Shifting the first ball right
 *     only reduces room for the others; leftmost is never worse.
 *   - Aggressive Cows (classic) is the same problem; Minimize Max Distance to Gas Station
 *     (LC 774) is the dual on real numbers.
 *   - What if positions are not distinct? Duplicates give gap 0, so canPlaceBalls should
 *     still work; the lower bound of 1 assumes distinct baskets.
 *
 * RUN
 *   main() runs 4 cases (LeetCode examples, two baskets, every basket filled) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class MagneticForceBetweenTwoBalls {

    public static int maxDistance(int[] position, int m) {
        Arrays.sort(position);       // greedy placement needs left-to-right order
        int n = position.length;
        int left = 1;
        int right = position[n - 1] - position[0];
        int best = 0;

        // Answer space is T T T F F F; keep the last T.
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canPlaceBalls(position, m, mid)) {
                best = mid;          // feasible, try a larger gap
                left = mid + 1;
            } else {
                right = mid - 1;     // too ambitious, shrink the gap
            }
        }
        return best;
    }

    /** Greedy: first ball leftmost, then each next ball at the first basket >= minDist away. */
    private static boolean canPlaceBalls(int[] position, int m, int minDist) {
        int count = 1;
        int lastPos = position[0];

        for (int i = 1; i < position.length; i++) {
            if (position[i] - lastPos >= minDist) {
                count++;
                lastPos = position[i];
                if (count >= m) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical",           maxDistance(new int[]{1, 2, 3, 4, 7}, 3), 3);
        int[] farApart = {5, 4, 3, 2, 1, 1000000000};
        print("case 2 two extremes",      maxDistance(farApart, 2), 999999999);
        print("case 3 two baskets",       maxDistance(new int[]{1, 2}, 2), 1);
        print("case 4 every basket used", maxDistance(new int[]{1, 2, 3, 4, 5}, 5), 1);
    }
}
