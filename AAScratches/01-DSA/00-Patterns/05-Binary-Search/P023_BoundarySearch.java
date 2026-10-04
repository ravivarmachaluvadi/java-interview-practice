/*
 * =====================================================================
 *  P023 Binary Search: First True (Lower / Upper Bound)   Canonical LC 34 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 34, Find First and Last Position of Element in Sorted Array)
 *   nums is sorted ascending. Return [first index of target, last index of target], or
 *   [-1, -1] if target is absent. O(log n).
 *
 * EXAMPLE
 *   [5, 7, 7, 8, 8, 10], target 8  ->  [3, 4]
 *   [5, 7, 7, 8, 8, 10], target 6  ->  [-1, -1]
 *   [],                  target 0  ->  [-1, -1]
 *
 * RECOGNIZE WHEN
 *   - Sorted input (or a monotonic yes/no question over indices) and an O(log n) target.
 *   - "first / last position", "insert position", "smallest x such that ...", "count of
 *     values in [a, b]", "first bad version".
 *   Not this if: the search space is the ANSWER (speed, capacity, days) rather than an
 *   index -> P025_AnswerSpaceMinimize / P026_AnswerSpaceMaximize; the array is rotated ->
 *   P024_RotatedSortedArray.
 *
 * TEMPLATE
 *   // first index in [lo, hi) where pred is true; hi itself means "never true"
 *   lo = 0, hi = n
 *   while lo < hi:
 *       mid = lo + (hi - lo) / 2
 *       if pred(mid): hi = mid            // mid might be the answer; keep it
 *       else:         lo = mid + 1        // mid is definitely not
 *   return lo
 *   lowerBound(x) = first i with a[i] >= x;   upperBound(x) = first i with a[i] > x
 *
 * APPROACH
 *   1. first = lowerBound(target); if it is n or a[first] != target, the target is absent.
 *   2. last = upperBound(target) - 1.
 *
 * KEY INSIGHT
 *   Every boundary question is "find the first index where a monotonic predicate flips
 *   from false to true". Write that ONE loop, with the half-open [lo, hi) range, and every
 *   variant is just a different pred. It never loops forever and never misses the edge.
 *
 * COMPLEXITY
 *   Time O(log n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 35   Search Insert Position   lowerBound(target)
 *   [coded] LC 278  First Bad Version        pred(v) = isBad(v) over 1..n
 *   [coded] LC 69   Sqrt(x)                  last v with v * v <= x = firstTrue(v * v > x) - 1
 *           LC 744  Next Greatest Letter     upperBound(target), wrap to 0 at the end
 *           LC 2529 Max Count Pos / Neg      negatives = lowerBound(0); positives = n -
 *                                            upperBound(0)
 *           Count in [a, b]                  upperBound(b) - lowerBound(a)
 *           LC 1150 Majority in Sorted Array upperBound(x) - lowerBound(x) > n / 2
 *           LC 34 follow-ups: floor / ceil   ceil = lowerBound(x); floor = upperBound(x) - 1
 *
 * PITFALLS
 *   - mid = (lo + hi) / 2 overflows for big ints; use lo + (hi - lo) / 2.
 *   - Mixing styles (hi = n - 1 with lo <= hi, plus hi = mid) causes infinite loops; pick
 *     one style and keep it.
 *   - Check lowerBound's result against n BEFORE reading a[result].
 *   - Sqrt: mid * mid overflows int; compare in long.
 *
 * DEEP DIVE
 *   A01_LowerAndUpperBounds, A02_FirstAndLastOccurrence, A03_FloorAndCeilInSortedArray,
 *   A04_SquareRoot (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;
import java.util.function.LongPredicate;

class BoundarySearch {

    // The one loop: first value in [lo, hi) for which pred is true (hi if none).
    static long firstTrue(long lo, long hi, LongPredicate pred) {
        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;
            if (pred.test(mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    static int lowerBound(int[] a, int x) {
        return (int) firstTrue(0, a.length, i -> a[(int) i] >= x);
    }

    static int upperBound(int[] a, int x) {
        return (int) firstTrue(0, a.length, i -> a[(int) i] > x);
    }

    // Canonical LC 34.
    static int[] searchRange(int[] nums, int target) {
        int first = lowerBound(nums, target);
        if (first == nums.length || nums[first] != target) {
            return new int[]{-1, -1};
        }
        return new int[]{first, upperBound(nums, target) - 1};
    }

    // LC 35.
    static int searchInsert(int[] nums, int target) {
        return lowerBound(nums, target);
    }

    // LC 278: versions 1..n; every version from firstBad on is bad.
    static int firstBadVersion(int n, int firstBad) {
        return (int) firstTrue(1, n + 1L, v -> v >= firstBad);   // pred stands in for isBad
    }

    // LC 69: the largest v with v * v <= x.
    static int mySqrt(int x) {
        return (int) firstTrue(0, x + 1L, v -> v * v > x) - 1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] a = {5, 7, 7, 8, 8, 10};
        check("LC 34 target 8", Arrays.toString(searchRange(a, 8)), "[3, 4]");
        check("LC 34 target 6 absent", Arrays.toString(searchRange(a, 6)), "[-1, -1]");
        check("LC 34 empty array", Arrays.toString(searchRange(new int[]{}, 0)), "[-1, -1]");
        check("LC 34 target 11 past the end", Arrays.toString(searchRange(a, 11)), "[-1, -1]");
        check("LC 34 target 5 at the start", Arrays.toString(searchRange(a, 5)), "[0, 0]");

        int[] b = {1, 3, 5, 6};
        check("LC 35 target 5", searchInsert(b, 5), 2);
        check("LC 35 target 2", searchInsert(b, 2), 1);
        check("LC 35 target 7", searchInsert(b, 7), 4);
        check("LC 35 target 0", searchInsert(b, 0), 0);

        check("LC 278 n=5 bad=4", firstBadVersion(5, 4), 4);
        check("LC 278 n=1 bad=1", firstBadVersion(1, 1), 1);
        check("LC 278 near int max",
                firstBadVersion(Integer.MAX_VALUE, Integer.MAX_VALUE), Integer.MAX_VALUE);

        check("LC 69 x=4", mySqrt(4), 2);
        check("LC 69 x=8", mySqrt(8), 2);
        check("LC 69 x=0", mySqrt(0), 0);
        check("LC 69 x=2147395599", mySqrt(2147395599), 46339);
    }
}
