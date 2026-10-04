/*
 * =====================================================================
 *  P026 Binary Search on the Answer: Maximise   Canonical LC 1552 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1552, Magnetic Force Between Two Balls)
 *   Baskets sit at the given positions. Place m balls in distinct baskets so that the
 *   SMALLEST distance between any two balls is as large as possible. Return it.
 *
 * EXAMPLE
 *   position [1, 2, 3, 4, 7],               m = 3  ->  3     balls at 1, 4, 7
 *   position [5, 4, 3, 2, 1, 1000000000],   m = 2  ->  999999999
 *
 * RECOGNIZE WHEN
 *   - "maximise the minimum" distance / piece / sweetness / tastiness.
 *   - Checking one candidate gap is a greedy pass; the best gap has no formula.
 *   - Feasibility is monotonic the OTHER way: if gap d works, every smaller gap works.
 *   Not this if: you minimise a maximum -> P025_AnswerSpaceMinimize.
 *
 * TEMPLATE
 *   lo = smallest answer (always feasible), hi = largest conceivable answer
 *   while lo < hi:
 *       mid = lo + (hi - lo + 1) / 2          // UPPER mid, or lo = mid loops forever
 *       if feasible(mid): lo = mid            // mid works; maybe something larger does
 *       else:             hi = mid - 1
 *   return lo
 *
 * APPROACH
 *   1. Sort positions. The answer lies in [1, (last - first)].
 *   2. feasible(d): put a ball in the first basket, then in each next basket that is at
 *      least d away from the previous ball; feasible if you place m balls.
 *   3. Binary search the LAST feasible d.
 *
 * KEY INSIGHT
 *   Same machine as P025_AnswerSpaceMinimize, flipped: the predicate is true on the LEFT
 *   and false on the right, so you look for the last true. The only code differences are
 *   "lo = mid" and the rounded-up mid that keeps the range shrinking.
 *
 * COMPLEXITY
 *   Time O(n log n + n log R), space O(1) besides the sort.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Aggressive Cows (SPOJ)           the same problem with stalls and cows
 *   [coded] LC 1231 Divide Chocolate         k cuts -> k + 1 pieces; feasible(s) = greedy
 *                                            pieces with sum >= s number at least k + 1
 *           LC 2517 Max Tastiness of Basket  sort prices, then exactly LC 1552
 *           LC 1760 Min Limit of Balls       minimise -> P025_AnswerSpaceMinimize
 *           LC 2064 Min Max Products         minimise -> P025_AnswerSpaceMinimize
 *
 * PITFALLS
 *   - With lo = mid you MUST round mid up; otherwise lo == mid forever when hi = lo + 1.
 *   - Greedy placement must start at the first sorted position.
 *   - LC 1231: you eat the piece with the SMALLEST sweetness; maximise that.
 *
 * DEEP DIVE
 *   C13_MagneticForceBetweenTwoBalls (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class AnswerSpaceMaximize {

    // Canonical LC 1552.
    static int maxDistance(int[] position, int m) {
        int[] p = position.clone();
        Arrays.sort(p);
        int lo = 1;
        int hi = p[p.length - 1] - p[0];
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (ballsPlaced(p, mid) >= m) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    private static int ballsPlaced(int[] sorted, int gap) {
        int count = 1;
        int last = sorted[0];
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] - last >= gap) {
                count++;
                last = sorted[i];
            }
        }
        return count;
    }

    // Aggressive cows: identical shape, different story.
    static int aggressiveCows(int[] stalls, int cows) {
        return maxDistance(stalls, cows);
    }

    // LC 1231: maximise the smallest piece after k cuts (k + 1 pieces).
    static int maximizeSweetness(int[] sweetness, int k) {
        int lo = Integer.MAX_VALUE;
        int hi = 0;
        for (int s : sweetness) {
            lo = Math.min(lo, s);
            hi += s;
        }
        hi /= (k + 1);                             // a piece cannot beat the average
        lo = Math.min(lo, hi);
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (piecesWithAtLeast(sweetness, mid) >= k + 1) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    private static int piecesWithAtLeast(int[] sweetness, int target) {
        int pieces = 0;
        int run = 0;
        for (int s : sweetness) {
            run += s;
            if (run >= target) {
                pieces++;
                run = 0;
            }
        }
        return pieces;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 1552 [1,2,3,4,7] m=3", maxDistance(new int[]{1, 2, 3, 4, 7}, 3), 3);
        check("LC 1552 far outlier m=2",
                maxDistance(new int[]{5, 4, 3, 2, 1, 1000000000}, 2), 999999999);
        check("LC 1552 m equals n", maxDistance(new int[]{1, 3, 6}, 3), 2);

        check("cows [1,2,4,8,9] c=3", aggressiveCows(new int[]{1, 2, 4, 8, 9}, 3), 3);
        check("cows [0,3,4,7,10,9] c=4", aggressiveCows(new int[]{0, 3, 4, 7, 10, 9}, 4), 3);

        check("LC 1231 1..9 k=5", maximizeSweetness(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, 5), 6);
        check("LC 1231 k=8 every piece",
                maximizeSweetness(new int[]{5, 6, 7, 8, 9, 1, 2, 3, 4}, 8), 1);
        check("LC 1231 k=2", maximizeSweetness(new int[]{1, 2, 2, 1, 2, 2, 1, 2, 2}, 2), 5);
    }
}
