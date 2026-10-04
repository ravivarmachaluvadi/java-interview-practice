/*
 * =====================================================================
 *  P109 Fenwick Tree and Merge-Sort Counting   Canonical LC 307 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 307, Range Sum Query - Mutable)
 *   Support update(index, val) and sumRange(left, right) on an array, both in O(log n).
 *
 * EXAMPLE
 *   [1, 3, 5]: sumRange(0, 2) = 9; update(1, 2); sumRange(0, 2) = 8
 *
 * RECOGNIZE WHEN
 *   - Prefix sums, but values CHANGE between queries (a static array is
 *     P001_PrefixSumRangeQuery).
 *   - "how many earlier / later elements are smaller / larger" (count by rank as you go).
 *   - Inversion-style counts: pairs i < j with a[i] > a[j] (or > 2 * a[j]).
 *   Not this if: the array never changes -> P001_PrefixSumRangeQuery; you need min / max or
 *   range updates -> P110_SegmentTree.
 *
 * TEMPLATE
 *   Fenwick (1-indexed tree[]):
 *       add(i, delta): for (; i <= n; i += i & -i) tree[i] += delta
 *       sum(i):        for (; i > 0;  i -= i & -i) s += tree[i]       // prefix 1..i
 *       range(l, r) = sum(r) - sum(l - 1)
 *   counting smaller-after-self: compress values to ranks, scan RIGHT to LEFT,
 *       answer[i] = sum(rank[i] - 1), then add(rank[i], 1)
 *   merge-sort counting: while merging two sorted halves, count cross pairs with two pointers
 *
 * APPROACH
 *   1. tree[i] stores the sum of a block of length (i & -i) ending at i.
 *   2. A prefix sum hops down by removing the lowest set bit; an update hops up by adding it.
 *   3. update(index, val) adds (val - old) at that index.
 *
 * KEY INSIGHT
 *   The lowest set bit of i splits 1..n into O(log n) blocks, so every prefix is the sum of
 *   at most log n stored blocks and every index belongs to at most log n blocks. Counting
 *   problems become prefix sums over RANKS: "how many seen values are smaller" is sum(rank - 1).
 *
 * COMPLEXITY
 *   Fenwick O(log n) per update / query, O(n) space. Merge-sort counting O(n log n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 315  Count Smaller After Self Fenwick over value ranks, scanning right to left
 *   [coded] LC 493  Reverse Pairs            merge sort; before merging, count a[i] > 2 * b[j]
 *   [coded] Count inversions (GfG)           merge sort; every time the right side wins,
 *                                            add (left elements remaining)
 *           LC 327  Count of Range Sum       merge sort over prefix sums with a window
 *           LC 1649 Sorted Array Instructions  Fenwick: min(smaller, greater) per insert
 *           LC 2179 Count Good Triplets      Fenwick from both sides
 *
 * PITFALLS
 *   - Fenwick is 1-indexed: shift array indices by one.
 *   - Compress values first when they are large or negative.
 *   - LC 493: 2 * a[j] overflows int; compare in long.
 *
 * DEEP DIVE
 *   D03_SegmentTree, A05_MergeSort (18-Sorting-Searching-Algorithms)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class FenwickAndMergeCounting {

    static class Fenwick {
        private final long[] tree;

        Fenwick(int n) {
            tree = new long[n + 1];
        }

        void add(int i, long delta) {              // i is 1-based
            for (; i < tree.length; i += i & -i) {
                tree[i] += delta;
            }
        }

        long sum(int i) {                          // prefix 1..i
            long s = 0;
            for (; i > 0; i -= i & -i) {
                s += tree[i];
            }
            return s;
        }
    }

    // Canonical LC 307.
    static class NumArray {
        private final int[] a;
        private final Fenwick bit;

        NumArray(int[] nums) {
            a = nums.clone();
            bit = new Fenwick(nums.length);
            for (int i = 0; i < nums.length; i++) {
                bit.add(i + 1, nums[i]);
            }
        }

        void update(int index, int val) {
            bit.add(index + 1, val - a[index]);
            a[index] = val;
        }

        long sumRange(int left, int right) {
            return bit.sum(right + 1) - bit.sum(left);
        }
    }

    // LC 315.
    static int[] countSmaller(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        Fenwick bit = new Fenwick(nums.length);
        int[] out = new int[nums.length];
        for (int i = nums.length - 1; i >= 0; i--) {
            int rank = Arrays.binarySearch(sorted, nums[i]);
            while (rank > 0 && sorted[rank - 1] == nums[i]) {
                rank--;                            // first position of this value
            }
            out[i] = (int) bit.sum(rank);          // values strictly smaller, already seen
            bit.add(rank + 1, 1);
        }
        return out;
    }

    // LC 493: pairs i < j with nums[i] > 2 * nums[j].
    static int reversePairs(int[] nums) {
        return (int) sortCount(nums.clone(), 0, nums.length - 1, true);
    }

    // Inversions: pairs i < j with a[i] > a[j].
    static long countInversions(int[] a) {
        return sortCount(a.clone(), 0, a.length - 1, false);
    }

    private static long sortCount(int[] a, int lo, int hi, boolean doubled) {
        if (lo >= hi) {
            return 0;
        }
        int mid = (lo + hi) >>> 1;
        long count = sortCount(a, lo, mid, doubled) + sortCount(a, mid + 1, hi, doubled);
        int j = mid + 1;
        for (int i = lo; i <= mid; i++) {          // both halves are sorted now
            while (j <= hi && (long) a[i] > (doubled ? 2L * a[j] : a[j])) {
                j++;
            }
            count += j - (mid + 1);
        }
        int[] merged = new int[hi - lo + 1];
        int p = lo;
        int q = mid + 1;
        int k = 0;
        while (p <= mid || q <= hi) {
            merged[k++] = (q > hi || (p <= mid && a[p] <= a[q])) ? a[p++] : a[q++];
        }
        System.arraycopy(merged, 0, a, lo, merged.length);
        return count;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        NumArray na = new NumArray(new int[]{1, 3, 5});
        long before = na.sumRange(0, 2);
        na.update(1, 2);
        check("LC 307 sum, update, sum", before + "," + na.sumRange(0, 2), "9,8");
        check("LC 307 single cell after update", na.sumRange(1, 1), 2L);

        check("LC 315 [5,2,6,1]",
                Arrays.toString(countSmaller(new int[]{5, 2, 6, 1})), "[2, 1, 1, 0]");
        check("LC 315 [-1]", Arrays.toString(countSmaller(new int[]{-1})), "[0]");
        check("LC 315 duplicates", Arrays.toString(countSmaller(new int[]{-1, -1})), "[0, 0]");

        check("LC 493 [1,3,2,3,1]", reversePairs(new int[]{1, 3, 2, 3, 1}), 2);
        check("LC 493 [2,4,3,5,1]", reversePairs(new int[]{2, 4, 3, 5, 1}), 3);
        check("LC 493 overflow trap",
                reversePairs(new int[]{2147483647, 2147483647, 2147483647}), 0);

        check("inversions [2,4,1,3,5]", countInversions(new int[]{2, 4, 1, 3, 5}), 3L);
        check("inversions descending", countInversions(new int[]{5, 4, 3, 2, 1}), 10L);
    }
}
