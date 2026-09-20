/*
 * =====================================================================
 *  Segment Tree: range sum + point update      hand-built data structure | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Support two operations on a fixed-length int array, both in O(log n):
 *     rangeSum(l, r)        - sum of nums[l..r], inclusive
 *     update(i, newValue)   - set nums[i] and keep every stored sum correct
 *   A plain array gives O(1) update but O(n) query; a prefix-sum array gives
 *   O(1) query but O(n) update. The segment tree balances both at O(log n).
 *
 * EXAMPLE
 *   nums = [1, 3, 5, 7, 9, 11] rangeSum(2, 5)          -> 32   (5 + 7 + 9 + 11)
 *   update(2, 7) then (2,5) -> 34   (7 + 7 + 9 + 11)
 *
 * DESIGN  (classes and why)
 *   SegmentTree - one class, no Node objects. The tree lives in a flat int[]
 *     laid out exactly like a binary heap: node at index k covers a segment,
 *     its children are at 2k+1 and 2k+2. Array layout means no pointers, no
 *     allocation per node, and cache-friendly traversal.
 *   Internally it also keeps `values`, its own copy of the input, so update()
 *     can work out the delta without the caller passing the array back in.
 *   buildSegmentTree / updateValue / getSum are the three recursions; each one
 *     walks the same segment split (start, mid, mid+1, end).
 *
 * KEY DECISIONS
 *   1. Store SUMS at internal nodes: st[k] = st[2k+1] + st[2k+2]. Swap that one
 *      combine step for Math.min / Math.max / gcd and the same code answers a
 *      different range query - that is the interview-worthy observation.
 *   2. Point update by DELTA: compute diff = newValue - old, then add diff to
 *      every node whose segment contains i. Only one root-to-leaf path changes.
 *   3. Query is a three-way case split, and this is the part to be able to say
 *      out loud: no overlap -> return 0 (the identity for sum); full overlap ->
 *      return the stored node; partial -> recurse both children and combine.
 *   4. Size the array at 4n. The tight bound is 2 * 2^ceil(log2 n) - 1, but
 *      computing it from Math.log invites floating-point edge cases (and blows
 *      up for n = 0), so 4n buys safety for one extra array.
 *   5. The tree is fixed length: no insert or delete. Use a Fenwick/BIT if you
 *      only need prefix sums, a balanced BST if the size changes.
 *
 * COMPLEXITY
 *   Build   Time O(n)      every node is written once.
 *   Query   Time O(log n)  the recursion fans out to at most 2 nodes per level.
 *   Update  Time O(log n)  one root-to-leaf path.
 *   Space   O(n)           4n ints for the tree plus n for the stored values.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Range UPDATE with lazy propagation: push a pending delta down on demand.
 *   - Fenwick tree (BIT): half the memory, shorter code, prefix sums only.
 *   - Why return 0 on no-overlap? (it is the identity of the combine operation;
 *     for min you must return +infinity instead)
 *   - Make it generic over the combine function, or build it iteratively.
 *
 * RUN
 *   main() builds three trees (typical, single element, negatives), runs
 *   queries before and after an update, and prints each answer next to the
 *   expected value plus a brute-force cross-check.
 *
 * Fixed: sizing the array from Math.log crashed for an empty input
 *        (NegativeArraySizeException); update() also forced the caller to hand
 *        back the original array, which silently broke if they passed another.
 */

import java.util.Arrays;

class SegmentTree {
    private final int[] st;     // the tree, heap-indexed: children of k are 2k+1, 2k+2
    private final int[] values; // current leaf values, so update() can compute the delta
    private final int n;

    public SegmentTree(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Segment tree needs at least one element");
        }
        this.n = nums.length;
        this.values = nums.clone(); // our own copy: the caller's array stays untouched
        this.st = new int[4 * n];   // safe upper bound, see KEY DECISIONS 4
        buildSegmentTree(values, 0, n - 1, 0);
    }

    // Fills st[sIdx] with the sum of nums[start..end] and returns it.
    private int buildSegmentTree(int[] nums, int start, int end, int sIdx) {
        if (start == end) { // leaf: one element
            st[sIdx] = nums[start];
            return nums[start];
        }
        int mid = start + (end - start) / 2;
        int leftSum = buildSegmentTree(nums, start, mid, 2 * sIdx + 1);
        int rightSum = buildSegmentTree(nums, mid + 1, end, 2 * sIdx + 2);
        st[sIdx] = leftSum + rightSum;
        return st[sIdx];
    }

    // Point update: set index updatingIdx to newValue.
    public void update(int updatingIdx, int newValue) {
        checkIndex(updatingIdx);
        int diff = newValue - values[updatingIdx];
        values[updatingIdx] = newValue;
        updateValue(0, n - 1, updatingIdx, diff, 0);
    }

    // Adds diff to every node whose segment contains updatingIdx.
    private void updateValue(int start, int end, int updatingIdx, int diff, int sIdx) {
        if (updatingIdx < start || updatingIdx > end) return; // this segment is unaffected

        st[sIdx] += diff;

        if (start != end) {
            int mid = start + (end - start) / 2;
            updateValue(start, mid, updatingIdx, diff, 2 * sIdx + 1);
            updateValue(mid + 1, end, updatingIdx, diff, 2 * sIdx + 2);
        }
    }

    // Sum of values[searchStartIdx..searchEndIdx], inclusive.
    public int rangeSum(int searchStartIdx, int searchEndIdx) {
        checkIndex(searchStartIdx);
        checkIndex(searchEndIdx);
        if (searchStartIdx > searchEndIdx) {
            throw new IllegalArgumentException("start index must not exceed end index");
        }
        return getSum(0, n - 1, searchStartIdx, searchEndIdx, 0);
    }

    private int getSum(int segStart, int segEnd, int searchStartIdx, int searchEndIdx, int sIdx) {
        // No overlap: contribute the identity for sum.
        if (segEnd < searchStartIdx || segStart > searchEndIdx) {
            return 0;
        }
        // Full overlap: this node's segment sits entirely inside the query range,
        // e.g. node covers [2, 5] while the query asks for [1, 6] - use the cached sum.
        if (searchStartIdx <= segStart && searchEndIdx >= segEnd) {
            return st[sIdx];
        }
        // Partial overlap: split and let the children decide.
        int mid = segStart + (segEnd - segStart) / 2;
        return getSum(segStart, mid, searchStartIdx, searchEndIdx, 2 * sIdx + 1)
                + getSum(mid + 1, segEnd, searchStartIdx, searchEndIdx, 2 * sIdx + 2);
    }

    private void checkIndex(int idx) {
        if (idx < 0 || idx >= n) {
            throw new IndexOutOfBoundsException("index " + idx + " outside 0.." + (n - 1));
        }
    }

    // Straightforward O(n) sum, used only to cross-check the tree in main().
    private static int bruteForceSum(int[] nums, int from, int to) {
        int sum = 0;
        for (int i = from; i <= to; i++) sum += nums[i];
        return sum;
    }

    private static void print(String label, int actual, int bruteForce, int expected) {
        System.out.println(label + ": " + actual + " (brute force " + bruteForce + ")"
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1: typical array, query then point update then the same query.
        int[] nums = {1, 3, 5, 7, 9, 11};
        SegmentTree segTree = new SegmentTree(nums);
        print("case 1a sum(2,5)", segTree.rangeSum(2, 5), bruteForceSum(nums, 2, 5), 32);

        int[] afterUpdate = {1, 3, 7, 7, 9, 11}; // index 2 changed from 5 to 7
        segTree.update(2, 7);
        print("case 1b sum(2,5) after update(2,7)",
                segTree.rangeSum(2, 5), bruteForceSum(afterUpdate, 2, 5), 34);
        print("case 1c sum(0,5) whole array",
                segTree.rangeSum(0, 5), bruteForceSum(afterUpdate, 0, 5), 38);
        System.out.println("case 1d caller's array untouched: " + Arrays.toString(nums)
                + "   expected [1, 3, 5, 7, 9, 11]");

        // Case 2: single element - build, query and update all hit the same leaf.
        SegmentTree single = new SegmentTree(new int[]{42});
        print("case 2a sum(0,0)", single.rangeSum(0, 0), 42, 42);
        single.update(0, -5);
        print("case 2b sum(0,0) after update(0,-5)", single.rangeSum(0, 0), -5, -5);

        // Case 3: negatives, and a rejected out-of-range query.
        int[] mixed = {-2, 5, -1, 4};
        SegmentTree negatives = new SegmentTree(mixed);
        print("case 3a sum(0,3)", negatives.rangeSum(0, 3), bruteForceSum(mixed, 0, 3), 6);
        print("case 3b sum(1,2)", negatives.rangeSum(1, 2), bruteForceSum(mixed, 1, 2), 4);
        String rejected;
        try {
            negatives.rangeSum(2, 9);
            rejected = "no exception";
        } catch (IndexOutOfBoundsException e) {
            rejected = "IndexOutOfBoundsException";
        }
        System.out.println("case 3c sum(2,9) out of range: " + rejected
                + "   expected IndexOutOfBoundsException");
    }
}
