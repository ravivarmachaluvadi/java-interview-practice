/*
 * =====================================================================
 *  P025 Binary Search on the Answer: Minimise   Canonical LC 875 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 875, Koko Eating Bananas)
 *   piles[i] bananas in each pile. Each hour Koko picks one pile and eats up to k bananas
 *   from it. Return the minimum integer speed k that finishes all piles within h hours.
 *
 * EXAMPLE
 *   piles [3, 6, 7, 11],       h = 8  ->  4
 *   piles [30, 11, 23, 4, 20], h = 5  ->  30
 *   piles [30, 11, 23, 4, 20], h = 6  ->  23
 *
 * RECOGNIZE WHEN
 *   - "minimum speed / capacity / days / largest-allowed value such that it is possible".
 *   - "minimise the maximum" (split array, ship packages, painters, allocate books).
 *   - Checking ONE candidate answer is easy (a greedy pass), but the answer has no formula.
 *   - Feasibility is monotonic: if k works, every bigger k works too.
 *   Not this if: you maximise a minimum -> P026_AnswerSpaceMaximize (the mirror image);
 *   you search positions in a sorted array -> P023_BoundarySearch.
 *
 * TEMPLATE
 *   lo = smallest possible answer, hi = largest possible answer (always feasible)
 *   while lo < hi:
 *       mid = lo + (hi - lo) / 2
 *       if feasible(mid): hi = mid            // mid works; maybe something smaller does
 *       else:             lo = mid + 1
 *   return lo
 *   feasible(x) = one greedy O(n) pass that simulates the process with x
 *
 * APPROACH
 *   1. Speed is between 1 and max(piles) (eating faster than the biggest pile never helps).
 *   2. feasible(k): hours = sum of ceil(pile / k); feasible if hours <= h.
 *   3. Binary search the first feasible k.
 *
 * KEY INSIGHT
 *   You are not searching an array; you are searching the range of possible ANSWERS, and
 *   "is this answer good enough?" is a yes/no question that flips exactly once. That turns
 *   an optimisation problem into log(range) easy checks.
 *
 * COMPLEXITY
 *   Time O(n log R) where R is the answer range; space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1011 Ship Within D Days       lo = max weight, hi = total; feasible =
 *                                            greedily fill each day, count days <= D
 *   [coded] LC 410  Split Array Largest Sum  identical to LC 1011 (k parts = D days)
 *   [coded] LC 1482 Min Days for Bouquets    answer = a day; feasible = count runs of k
 *                                            bloomed flowers >= m; -1 if m * k > n
 *           LC 1283 Smallest Divisor         sum of ceil(x / d) <= threshold
 *           LC 2187 Min Time for Trips       sum of (t / time[i]) >= totalTrips
 *           LC 774  Min Max Gas Station Dist answer is a REAL number: loop 100 times or
 *                                            until hi - lo < 1e-6
 *           Painter's partition / Allocate Books  same as LC 410
 *
 * PITFALLS
 *   - hi must be feasible (or you return an impossible answer); lo must be a real bound.
 *   - Ceil division without doubles: (pile + k - 1) / k.
 *   - Totals overflow int (hours, sums of weights): use long.
 *
 * DEEP DIVE
 *   C11_KokoEatingBananas, C12_CapacityToShipPackagesWithinDDays, C10_CuttingRibbons
 *   (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class AnswerSpaceMinimize {

    // Canonical LC 875.
    static int minEatingSpeed(int[] piles, int h) {
        int lo = 1;
        int hi = 0;
        for (int p : piles) {
            hi = Math.max(hi, p);
        }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (hoursAt(piles, mid) <= h) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    private static long hoursAt(int[] piles, int k) {
        long hours = 0;
        for (int p : piles) {
            hours += (p + k - 1) / k;
        }
        return hours;
    }

    // LC 1011.
    static int shipWithinDays(int[] weights, int days) {
        int lo = 0;
        int hi = 0;
        for (int w : weights) {
            lo = Math.max(lo, w);                // a day must carry the heaviest package
            hi += w;                             // one day can carry everything
        }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (daysNeeded(weights, mid) <= days) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    // Greedy check shared by LC 1011 and LC 410: fill each group until the cap.
    private static int daysNeeded(int[] weights, int cap) {
        int groups = 1;
        int load = 0;
        for (int w : weights) {
            if (load + w > cap) {
                groups++;
                load = 0;
            }
            load += w;
        }
        return groups;
    }

    // LC 410: k non-empty parts, minimise the largest part sum.
    static int splitArray(int[] nums, int k) {
        return shipWithinDays(nums, k);
    }

    // LC 1482: m bouquets of k ADJACENT flowers; bloomDay[i] is when flower i opens.
    static int minDays(int[] bloomDay, int m, int k) {
        if ((long) m * k > bloomDay.length) {
            return -1;
        }
        int lo = Integer.MAX_VALUE;
        int hi = 0;
        for (int d : bloomDay) {
            lo = Math.min(lo, d);
            hi = Math.max(hi, d);
        }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (bouquetsBy(bloomDay, mid, k) >= m) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    private static int bouquetsBy(int[] bloomDay, int day, int k) {
        int bouquets = 0;
        int run = 0;
        for (int d : bloomDay) {
            run = d <= day ? run + 1 : 0;
            if (run == k) {
                bouquets++;
                run = 0;
            }
        }
        return bouquets;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 875 h=8", minEatingSpeed(new int[]{3, 6, 7, 11}, 8), 4);
        check("LC 875 h=5 one pile per hour", minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5), 30);
        check("LC 875 h=6", minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 6), 23);
        check("LC 875 huge pile, overflow trap",
                minEatingSpeed(new int[]{805306368, 805306368, 805306368}, 1000000000), 3);

        check("LC 1011 1..10 in 5 days",
                shipWithinDays(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5), 15);
        check("LC 1011 [3,2,2,4,1,4] in 3", shipWithinDays(new int[]{3, 2, 2, 4, 1, 4}, 3), 6);
        check("LC 1011 [1,2,3,1,1] in 4", shipWithinDays(new int[]{1, 2, 3, 1, 1}, 4), 3);

        check("LC 410 [7,2,5,10,8] k=2", splitArray(new int[]{7, 2, 5, 10, 8}, 2), 18);
        check("LC 410 [1,2,3,4,5] k=2", splitArray(new int[]{1, 2, 3, 4, 5}, 2), 9);
        check("LC 410 [1,4,4] k=3", splitArray(new int[]{1, 4, 4}, 3), 4);

        check("LC 1482 m=3 k=1", minDays(new int[]{1, 10, 3, 10, 2}, 3, 1), 3);
        check("LC 1482 m=3 k=2 impossible", minDays(new int[]{1, 10, 3, 10, 2}, 3, 2), -1);
        check("LC 1482 m=2 k=3", minDays(new int[]{7, 7, 7, 7, 12, 7, 7}, 2, 3), 12);
    }
}
