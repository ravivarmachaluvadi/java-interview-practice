/*
 * =====================================================================
 *  Cutting Ribbons                                  LeetCode 1891 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You have ribbons of integer lengths and need at least k pieces, all of the same
 *   integer length x. Leftover material is discarded. Return the largest possible x,
 *   or 0 if no positive x yields k pieces.
 *
 * EXAMPLE
 *   ribbons = [9, 7, 5],  k = 3  ->  5    9 -> 5+4, 7 -> 5+2, 5 -> 5
 *   ribbons = [7, 5, 9],  k = 4  ->  4    7 -> 4+3, 5 -> 4+1, 9 -> 4+4+1
 *   ribbons = [5, 7, 10], k = 5  ->  3    len 3 gives 1+2+3 = 6 pieces, len 4 only 4
 *   ribbons = [1, 2],     k = 5  ->  0    total material is 3, cannot make 5 pieces
 *
 * APPROACH  (maximize x with count(x) >= k)
 *   1. Answer range is [1, max(ribbons)]; longer than the longest ribbon gives 0 pieces.
 *   2. canCut(len) = sum of (r / len) over all ribbons >= k. This is monotone:
 *      a shorter length never produces fewer pieces.
 *   3. Binary search the range. When canCut(mid) is true, record mid and go right
 *      (try longer); otherwise go left. The last recorded true is the answer.
 *
 * KEY INSIGHT
 *   The feasibility predicate is monotone in x (true, true, ..., false, false), so
 *   binary search finds the boundary. This is the gentlest answer-space search:
 *   the check is a single summation with no simulation, the warm-up for Koko.
 *
 * COMPLEXITY
 *   Time  O(n log M)  M = max ribbon length; each probe scans n ribbons
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Koko Eating Bananas (LC 875): same shape, search for the MINIMUM feasible speed.
 *   - Why is ans initialised to 0? So an impossible k naturally returns 0.
 *   - Can the count overflow? Use long for the running count when n * max is large.
 *
 * RUN
 *   main() runs 4 cases (two typical, one where the tempting answer is wrong, one
 *   impossible) and prints actual vs expected.
 */

class CuttingRibbons {

    // True if cutting every ribbon into pieces of length len yields at least k pieces.
    private static boolean canCut(int[] ribbons, int k, int len) {
        if (len <= 0) return false;
        long count = 0;
        for (int r : ribbons) {
            count += r / len;
            if (count >= k) {
                return true;   // early exit, no need to scan the rest
            }
        }
        return false;
    }

    // Largest piece length that still yields k pieces, or 0 if impossible.
    public static int maxLength(int[] ribbons, int k) {
        int lo = 1;
        int hi = 0;
        for (int r : ribbons) {
            hi = Math.max(hi, r);   // no piece can be longer than the longest ribbon
        }
        int ans = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (canCut(ribbons, k, mid)) {
                ans = mid;          // feasible: remember it and try a longer piece
                lo = mid + 1;
            } else {
                hi = mid - 1;       // too long: shorten
            }
        }
        return ans;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [9,7,5] k=3", maxLength(new int[]{9, 7, 5}, 3), 5);
        print("case 2 [7,5,9] k=4", maxLength(new int[]{7, 5, 9}, 4), 4);
        print("case 3 [5,7,10] k=5", maxLength(new int[]{5, 7, 10}, 5), 3);
        print("case 4 [1,2] k=5 (impossible)", maxLength(new int[]{1, 2}, 5), 0);
    }
}
