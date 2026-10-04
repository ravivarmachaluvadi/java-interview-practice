/*
 * =====================================================================
 *  P003 Difference Array: Range Updates   Canonical LC 1109 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1109, Corporate Flight Bookings)
 *   There are n flights labelled 1..n. Each booking [first, last, seats] reserves `seats`
 *   on every flight from first to last inclusive. Return the total seats per flight.
 *
 * EXAMPLE
 *   bookings = [[1,2,10],[2,3,20],[2,5,25]], n = 5  ->  [10, 55, 45, 25, 25]
 *   bookings = [[1,2,10],[2,2,15]],          n = 2  ->  [10, 25]
 *
 * RECOGNIZE WHEN
 *   - Many "add v to every index in [l, r]" updates, and you read the values only at the end.
 *   - "How many intervals cover point x" / "maximum load at any moment" over a small,
 *     integer timeline (car pooling, population per year, overlapping bookings).
 *   Not this if: reads and updates interleave -> P110_SegmentTree (lazy) or a Fenwick tree;
 *   coordinates are huge or sparse -> sort the events instead (P050_SweepLineMinRooms).
 *
 * TEMPLATE
 *   diff = new int[n + 1]
 *   for (l, r, v) in updates: diff[l] += v; diff[r + 1] -= v    // O(1) per update
 *   running = 0
 *   for i in 0..n-1: running += diff[i]; value[i] = running      // one prefix pass
 *
 * APPROACH
 *   1. Mark where each booking starts (+seats) and where it stops (-seats at last + 1).
 *   2. A prefix sum over the marks turns "changes" back into "values".
 *
 * KEY INSIGHT
 *   The difference array is the inverse of the prefix sum: a range update touches only its
 *   two endpoints, and one final prefix pass rebuilds every value. k updates on n cells
 *   cost O(k + n) instead of O(k * n).
 *
 * COMPLEXITY
 *   Time O(k + n) for k updates over n cells; space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 370  Range Addition            0-indexed version of the canonical
 *   [coded] LC 1094 Car Pooling               +p at pick-up, -p at drop-off (not to + 1);
 *                                             fail as soon as the running load > capacity
 *           LC 1854 Max Population Year       +1 at birth, -1 at death year, argmax of run
 *           LC 2381 Shifting Letters II       diff of shifts, then shift each char mod 26
 *           LC 1589 Max Sum of Any Permutation  diff gives how often each index is hit;
 *                                             pair biggest counts with biggest numbers
 *           LC 2536 Increment Submatrices     2D diff: +v at (r1,c1), -v at (r1,c2+1),
 *                                             -v at (r2+1,c1), +v at (r2+1,c2+1)
 *
 * PITFALLS
 *   - Size the array n + 1 (or n + 2 for 1-indexed input) so r + 1 never overflows it.
 *   - Half-open vs closed: car pooling drops passengers AT `to`, so subtract at `to`.
 *   - Convert 1-indexed input once, in one place.
 *
 * DEEP DIVE
 *   C05_CarPooling (15-Intervals)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class DifferenceArray {

    // Canonical LC 1109. Flights are 1-indexed; diff[first - 1] += seats, diff[last] -= seats.
    static int[] corpFlightBookings(int[][] bookings, int n) {
        int[] diff = new int[n + 1];
        for (int[] b : bookings) {
            diff[b[0] - 1] += b[2];
            diff[b[1]] -= b[2];
        }
        int[] answer = new int[n];
        int running = 0;
        for (int i = 0; i < n; i++) {
            running += diff[i];
            answer[i] = running;
        }
        return answer;
    }

    // LC 370: the same thing, 0-indexed: [start, end, inc].
    static int[] getModifiedArray(int length, int[][] updates) {
        int[] diff = new int[length + 1];
        for (int[] u : updates) {
            diff[u[0]] += u[2];
            diff[u[1] + 1] -= u[2];
        }
        int[] answer = new int[length];
        int running = 0;
        for (int i = 0; i < length; i++) {
            running += diff[i];
            answer[i] = running;
        }
        return answer;
    }

    // LC 1094: trip = [passengers, from, to]; passengers leave AT `to`, so the -p goes there.
    static boolean carPooling(int[][] trips, int capacity) {
        int maxStop = 0;
        for (int[] t : trips) {
            maxStop = Math.max(maxStop, t[2]);
        }
        int[] diff = new int[maxStop + 1];
        for (int[] t : trips) {
            diff[t[1]] += t[0];
            diff[t[2]] -= t[0];
        }
        int load = 0;
        for (int stop = 0; stop <= maxStop; stop++) {
            load += diff[stop];
            if (load > capacity) {
                return false;
            }
        }
        return true;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] bookings = {{1, 2, 10}, {2, 3, 20}, {2, 5, 25}};
        check("LC 1109 three bookings n=5",
                Arrays.toString(corpFlightBookings(bookings, 5)), "[10, 55, 45, 25, 25]");
        check("LC 1109 two bookings n=2",
                Arrays.toString(corpFlightBookings(new int[][]{{1, 2, 10}, {2, 2, 15}}, 2)),
                "[10, 25]");
        check("LC 1109 booking to the last flight",
                Arrays.toString(corpFlightBookings(new int[][]{{3, 3, 7}}, 3)), "[0, 0, 7]");

        check("LC 370 length=5",
                Arrays.toString(getModifiedArray(5, new int[][]{{1, 3, 2}, {2, 4, 3}, {0, 2, -2}})),
                "[-2, 0, 3, 5, 3]");
        check("LC 370 no updates",
                Arrays.toString(getModifiedArray(3, new int[][]{})), "[0, 0, 0]");

        check("LC 1094 cap=4 overloaded", carPooling(new int[][]{{2, 1, 5}, {3, 3, 7}}, 4), false);
        check("LC 1094 cap=5 fits", carPooling(new int[][]{{2, 1, 5}, {3, 3, 7}}, 5), true);
        check("LC 1094 drop-off and pick-up at stop 5",
                carPooling(new int[][]{{2, 1, 5}, {3, 5, 7}}, 3), true);
    }
}
