/*
 * =====================================================================
 *  Capacity To Ship Packages Within D Days          LeetCode 1011 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Packages must be shipped in the given order, one ship, one trip per day. The ship has a
 *   fixed weight capacity; each day it loads consecutive packages until the next one would
 *   exceed the capacity. Return the least capacity that ships everything within 'days' days.
 *
 * EXAMPLE
 *   weights = [1..10], days = 5        ->  15   days: [1..5] [6,7] [8] [9] [10]
 *   weights = [3, 2, 2, 4, 1, 4], days = 3 ->  6    days: [3,2] [2,4] [1,4]
 *   weights = [1, 2, 3, 1, 1], days = 4    ->  3    days: [1] [2] [3] [1,1]
 *   weights = [5], days = 1                ->  5    single package: capacity = its weight
 *   weights = [4, 4, 4], days = 3          ->  4    one package per day, capacity = max
 *
 * APPROACH  (binary search on the answer, greedy feasibility check)
 *   1. Bounds: low = max(weights) because the heaviest package must fit on some day;
 *      high = sum(weights) because that ships everything in a single day.
 *   2. Predicate canShip(capacity): walk the packages in order, start a new day whenever the
 *      running load would exceed capacity, and report whether the day count stays <= days.
 *   3. The predicate is monotone (a bigger ship never needs more days), so the answer space
 *      is F F F T T T. If canShip(mid) then try smaller (high = mid - 1) else low = mid + 1.
 *   4. When the loop ends, low is the first feasible capacity.
 *
 * KEY INSIGHT
 *   Same template as Koko (minimum feasible answer), with two twists: the lower bound is
 *   max(weights) rather than 1, and the predicate is a greedy simulation instead of a formula.
 *   Greedy is safe here because packages keep their order, so "fill the day as full as you
 *   can" is never worse than stopping early.
 *
 * COMPLEXITY
 *   Time  O(n log S)  S = sum(weights) - max(weights); each probe walks all n packages
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not start low at 1? A capacity below the heaviest package can never ship it, and
 *     the greedy check would silently mis-count; starting at max removes a whole edge case.
 *   - Split Array Largest Sum (LC 410) is the identical problem with different words.
 *   - What changes if packages could be reordered? Then it becomes bin packing (NP-hard);
 *     the greedy check no longer gives an exact answer.
 *   - Why "low <= high" with high = mid - 1 here, but "left < right" with right = mid in Koko?
 *     Both find the first true; this version tracks the answer as the final low.
 *
 * RUN
 *   main() runs 5 cases (LeetCode examples, single package, all equal) and prints actual
 *   vs expected.
 */

class CapacityToShipPackagesWithinDDays {

    public int shipWithinDays(int[] weights, int days) {
        int low = 0;
        int high = 0;
        for (int w : weights) {
            low = Math.max(low, w);   // capacity must be at least the heaviest package
            high += w;                // at most, ship everything in one day
        }

        // Answer space is F F F T T T; low ends on the first T.
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (canShip(weights, days, mid)) {
                high = mid - 1;       // feasible, try a smaller capacity
            } else {
                low = mid + 1;        // not feasible, need a larger capacity
            }
        }
        return low;
    }

    /** Greedy: load each day as full as possible; true if that needs at most 'days' days. */
    private boolean canShip(int[] weights, int days, int capacity) {
        int dayCount = 1;
        int currLoad = 0;

        for (int w : weights) {
            if (currLoad + w > capacity) {
                dayCount++;           // this package starts a new day
                currLoad = w;
            } else {
                currLoad += w;
            }
            if (dayCount > days) {
                return false;         // already over budget, no need to keep walking
            }
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        CapacityToShipPackagesWithinDDays s = new CapacityToShipPackagesWithinDDays();
        int[] oneToTen = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        print("case 1 typical",        s.shipWithinDays(oneToTen, 5), 15);
        print("case 2 LC example 2",   s.shipWithinDays(new int[]{3, 2, 2, 4, 1, 4}, 3), 6);
        print("case 3 LC example 3",   s.shipWithinDays(new int[]{1, 2, 3, 1, 1}, 4), 3);
        print("case 4 single package", s.shipWithinDays(new int[]{5}, 1), 5);
        print("case 5 all equal",      s.shipWithinDays(new int[]{4, 4, 4}, 3), 4);
    }
}
