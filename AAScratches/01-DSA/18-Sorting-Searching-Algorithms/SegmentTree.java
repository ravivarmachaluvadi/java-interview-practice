/**
 * A Segment Tree is a binary tree data structure that is used to store
 * information about intervals (segments) of an array.
 * <p>
 * It allows answering range queries (like sum, min, max, gcd) and performing updates
 * on the array efficiently.
 */
class SegmentTree {
    private final int[] st; // Segment Tree representation
    private final int n;

    public SegmentTree(int[] nums) {
        this.n = nums.length;
        int height = (int) Math.ceil(Math.log(n) / Math.log(2));
        int maxSize = 2 * (int) Math.pow(2, height) - 1;
        st = new int[maxSize];

        buildSegmentTree(nums, 0, n - 1, 0);
    }

    // Build the Segment Tree
    private int buildSegmentTree(int[] nums, int start, int end, int sIdx) {
        if (start == end) {
            st[sIdx] = nums[start];
            return nums[start];
        }

        int mid = start + (end - start) / 2;
        int leftSum = buildSegmentTree(nums, start, mid, 2 * sIdx + 1);
        int rightSum = buildSegmentTree(nums, mid + 1, end, 2 * sIdx + 2);
        st[sIdx] = leftSum + rightSum;

        return st[sIdx];
    }

    // Update the value at a specific index
    public void update(int[] nums, int updatingIdx, int newValue) {
        int diff = newValue - nums[updatingIdx];
        nums[updatingIdx] = newValue;
        updateValue(0, n - 1, updatingIdx, diff, 0);
    }

    private void updateValue(int start, int end, int updatingIdx, int diff, int sIdx) {
        // If the index to be updated is out of this node's range, do nothing
        if (updatingIdx < start || updatingIdx > end) {
            return;
        }
        // Update the current node
        st[sIdx] += diff;

        if (start != end) {
            int mid = start + (end - start) / 2;
            updateValue(start, mid, updatingIdx, diff, 2 * sIdx + 1);
            updateValue(mid + 1, end, updatingIdx, diff, 2 * sIdx + 2);
        }
    }

    // Range Sum Query
    public int rangeSum(int searchStartIdx, int searchEndIdx) {
        return getSum(0, n - 1, searchStartIdx, searchEndIdx, 0);
    }

    private int getSum(int segmentTreeStart, int segmentTreeEnd, int searchStartIdx, int searchEndIdx, int idx) {
        // No overlap
        if (segmentTreeEnd < searchStartIdx || segmentTreeStart > searchEndIdx) {
            return 0;
        }

        // fulloverlap treePair(2 , 5) , rangePair(1 , 6)
        // outer interval vs inner interval , outerstart<= innerstart && innerend<=outerend
        if (searchStartIdx <= segmentTreeStart && searchEndIdx >= segmentTreeEnd) {
            return st[idx];
        }

        // Partial overlap
        int mid = segmentTreeStart + (segmentTreeEnd - segmentTreeStart) / 2;
        return getSum(segmentTreeStart, mid, searchStartIdx, searchEndIdx, 2 * idx + 1) +
                getSum(mid + 1, segmentTreeEnd, searchStartIdx, searchEndIdx, 2 * idx + 2);
    }

    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 7, 9, 11};
        SegmentTree segTree = new SegmentTree(nums);

        // Range sum query
        int searchStartIdx = 2, searchEndIdx = 5;
        System.out.println("Range Sum (2, 5): " + segTree.rangeSum(searchStartIdx, searchEndIdx)); // Output: 32

        // Update value
        int newValue = 7;
        int updatingIdx = 2;
        segTree.update(nums, updatingIdx, newValue);

        // Range sum query after update
        System.out.println("Range Sum (2, 5) after update: " + segTree.rangeSum(searchStartIdx, searchEndIdx)); // Output: 34
    }
}
