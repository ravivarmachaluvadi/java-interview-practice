/*
 * =====================================================================
 *  P001 Prefix Sum: Range Query   Canonical LC 303 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 303, Range Sum Query - Immutable)
 *   Given an int array nums, answer many queries sumRange(left, right) = the sum of
 *   nums[left..right] inclusive. The array never changes between queries.
 *
 * EXAMPLE
 *   nums = [-2, 0, 3, -5, 2, -1]
 *   sumRange(0, 2) -> 1      sumRange(2, 5) -> -1      sumRange(0, 5) -> -3
 *
 * RECOGNIZE WHEN
 *   - Many "sum / count / xor of a range" questions on an array that does not change.
 *   - An answer per index needs "everything to my left" and "everything to my right".
 *   - Weighted random choice: map a random ticket onto cumulative weights.
 *   Not this if: values change between queries -> P109_FenwickAndMergeCounting;
 *   you must COUNT subarrays that hit a target -> P002_PrefixSumHashMap.
 *
 * TEMPLATE
 *   prefix = new long[n + 1];                    // prefix[i] = a[0] + ... + a[i - 1]
 *   for i in 0..n-1: prefix[i + 1] = prefix[i] + a[i]
 *   sum(l..r) = prefix[r + 1] - prefix[l]        // O(1) per query
 *   2D: P[r+1][c+1] = a[r][c] + P[r][c+1] + P[r+1][c] - P[r][c]
 *
 * APPROACH
 *   1. Build prefix of length n + 1 once, with prefix[0] = 0.
 *   2. Answer each query with one subtraction: prefix[right + 1] - prefix[left].
 *
 * KEY INSIGHT
 *   A range sum is the difference of two running totals, so one O(n) pass buys O(1) for
 *   every later query. The extra leading 0 removes the "left == 0" special case.
 *
 * COMPLEXITY
 *   Build O(n) time and space, each query O(1). 2D: build O(R * C), each query O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 724  Find Pivot Index          left == total - left - nums[i]; no array
 *   [coded] LC 304  Range Sum Query 2D        2D prefix + inclusion-exclusion
 *   [coded] LC 528  Random Pick with Weight   prefix of weights + first prefix >= ticket
 *           LC 1480 Running Sum of 1d Array   the prefix array itself is the answer
 *           LC 1991 Find the Middle Index     identical to LC 724
 *           LC 1310 XOR Queries of Subarray   prefix XOR: xor(l..r) = px[r + 1] ^ px[l]
 *           LC 2389 Longest Subsequence Sum   sort, prefix, binary search per query
 *           LC 238  Product Except Self       prefix x suffix -> P008_PrefixSuffixTwoPasses
 *
 * PITFALLS
 *   - Off by one: with the n + 1 layout, range l..r is prefix[r + 1] - prefix[l].
 *   - Overflow: use long once n * max|value| can pass 2^31.
 *   - LC 528: tickets run 1..total and you need the FIRST prefix >= ticket (lower bound).
 *
 * DEEP DIVE
 *   B04_PivotIndex (01-Arrays), C02_RandomPickWithWeight (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Random;

class PrefixSumRangeQuery {

    // Canonical LC 303. prefix[i] holds the sum of nums[0..i-1]; prefix[0] = 0.
    static class NumArray {
        private final long[] prefix;

        NumArray(int[] nums) {
            prefix = new long[nums.length + 1];
            for (int i = 0; i < nums.length; i++) {
                prefix[i + 1] = prefix[i] + nums[i];
            }
        }

        long sumRange(int left, int right) {
            return prefix[right + 1] - prefix[left];
        }
    }

    // LC 724: the running left sum IS the prefix; the right side is total - left - nums[i].
    static int pivotIndex(int[] nums) {
        long total = 0;
        for (int x : nums) {
            total += x;
        }
        long left = 0;
        for (int i = 0; i < nums.length; i++) {
            if (left == total - left - nums[i]) {
                return i;
            }
            left += nums[i];
        }
        return -1;
    }

    // LC 304: p[r + 1][c + 1] = sum of the rectangle from (0, 0) to (r, c).
    static class NumMatrix {
        private final long[][] p;

        NumMatrix(int[][] m) {
            int rows = m.length;
            int cols = rows == 0 ? 0 : m[0].length;
            p = new long[rows + 1][cols + 1];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    p[r + 1][c + 1] = m[r][c] + p[r][c + 1] + p[r + 1][c] - p[r][c];
                }
            }
        }

        long sumRegion(int r1, int c1, int r2, int c2) {
            return p[r2 + 1][c2 + 1] - p[r1][c2 + 1] - p[r2 + 1][c1] + p[r1][c1];
        }
    }

    // LC 528: weight w[i] owns w[i] consecutive tickets; a ticket lands on the first index
    // whose running total reaches it.
    static class WeightedPicker {
        private final int[] prefix;
        private final Random random = new Random();

        WeightedPicker(int[] w) {
            prefix = new int[w.length];
            int run = 0;
            for (int i = 0; i < w.length; i++) {
                run += w[i];
                prefix[i] = run;
            }
        }

        int pickIndex() {
            return indexForTicket(random.nextInt(prefix[prefix.length - 1]) + 1);
        }

        // Deterministic core so it can be tested: first i with prefix[i] >= ticket.
        int indexForTicket(int ticket) {
            int lo = 0;
            int hi = prefix.length - 1;
            while (lo < hi) {
                int mid = lo + (hi - lo) / 2;
                if (prefix[mid] >= ticket) {
                    hi = mid;
                } else {
                    lo = mid + 1;
                }
            }
            return lo;
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        NumArray na = new NumArray(new int[]{-2, 0, 3, -5, 2, -1});
        check("LC 303 sumRange(0,2)", na.sumRange(0, 2), 1);
        check("LC 303 sumRange(2,5)", na.sumRange(2, 5), -1);
        check("LC 303 sumRange(0,5) whole array", na.sumRange(0, 5), -3);
        check("LC 303 sumRange(3,3) single cell", na.sumRange(3, 3), -5);

        check("LC 724 [1,7,3,6,5,6]", pivotIndex(new int[]{1, 7, 3, 6, 5, 6}), 3);
        check("LC 724 [1,2,3] none", pivotIndex(new int[]{1, 2, 3}), -1);
        check("LC 724 [2,1,-1] pivot at 0", pivotIndex(new int[]{2, 1, -1}), 0);

        NumMatrix nm = new NumMatrix(new int[][]{
                {3, 0, 1, 4, 2}, {5, 6, 3, 2, 1}, {1, 2, 0, 1, 5},
                {4, 1, 0, 1, 7}, {1, 0, 3, 0, 5}});
        check("LC 304 region(2,1,4,3)", nm.sumRegion(2, 1, 4, 3), 8);
        check("LC 304 region(1,1,2,2)", nm.sumRegion(1, 1, 2, 2), 11);
        check("LC 304 region(1,2,2,4)", nm.sumRegion(1, 2, 2, 4), 12);
        check("LC 304 region(0,0,0,0) corner", nm.sumRegion(0, 0, 0, 0), 3);

        WeightedPicker wp = new WeightedPicker(new int[]{3, 1, 2});   // prefix [3, 4, 6]
        check("LC 528 ticket 1", wp.indexForTicket(1), 0);
        check("LC 528 ticket 3 last of index 0", wp.indexForTicket(3), 0);
        check("LC 528 ticket 4", wp.indexForTicket(4), 1);
        check("LC 528 ticket 6 last ticket", wp.indexForTicket(6), 2);
        int pick = wp.pickIndex();
        check("LC 528 random pick is a valid index", pick >= 0 && pick < 3, true);
    }
}
