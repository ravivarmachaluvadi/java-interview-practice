/*
 * =====================================================================
 *  Partition Array into Disjoint Intervals          LeetCode 915 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums (length >= 2), split it into two contiguous
 *   non-empty parts, left and right, such that every element of left is <= every
 *   element of right. Return the length of the smallest such left part. A valid
 *   split is guaranteed to exist.
 *
 * EXAMPLE
 *   nums = [5, 0, 3, 8, 6]       ->  3   left = [5, 0, 3], right = [8, 6]
 *   nums = [1, 1, 1, 0, 6, 12]   ->  4   left = [1, 1, 1, 0], right = [6, 12]
 *   nums = [1, 2, 3, 4, 5]       ->  1   already sorted, left = [1]
 *   nums = [1, 1]                ->  1   equal values may sit on either side
 *
 * APPROACH  (left max versus running max)
 *   1. leftMax = max of the current left part (starts nums[0]); leftLen = 1.
 *   2. runningMax = max of every element scanned so far, including the right part.
 *   3. For i from 1: if nums[i] < leftMax, the current split is broken, because a
 *      right element is smaller than a left one. The left part must grow to
 *      include i: leftLen = i + 1 and leftMax = runningMax (everything up to i is
 *      now on the left, and runningMax is exactly its max).
 *   4. Otherwise nums[i] >= leftMax, the split still holds; just fold nums[i]
 *      into runningMax.
 *
 * KEY INSIGHT
 *   The answer only moves when the invariant "all of right >= leftMax" breaks, and
 *   when it breaks at i the new left part is the whole prefix [0..i], whose max we
 *   were already carrying as runningMax. So one pass with two maxima replaces the
 *   O(n)-space prefixMax[i] <= suffixMin[i+1] approach.
 *   Pattern: carry a deferred aggregate (runningMax) that becomes the committed one
 *   (leftMax) the moment the boundary is forced to move.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass
 *   Space O(1)  three ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Derive the two-array version first (prefix max, suffix min), then collapse it.
 *   - What if left must be strictly less than right? (compare with <= instead of <).
 *   - Return the actual left and right sub-arrays, not just the length.
 *
 * RUN
 *   main() runs 4 cases (typical, ties, sorted, two elements) and prints actual vs expected.
 */
class PartitionArrayintoDisjointIntervals {

    public static int partitionDisjoint(int[] nums) {
        int leftMax = nums[0];    // max of the committed left part
        int runningMax = nums[0]; // max of everything seen so far
        int leftLen = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < leftMax) {
                // nums[i] cannot sit on the right, so the left part grows to include it
                leftLen = i + 1;
                leftMax = runningMax;
            } else {
                runningMax = Math.max(runningMax, nums[i]);
            }
        }
        return leftLen;
    }

    public static void main(String[] args) {
        print("case 1 typical      ", partitionDisjoint(new int[]{5, 0, 3, 8, 6}), 3);
        print("case 2 ties         ", partitionDisjoint(new int[]{1, 1, 1, 0, 6, 12}), 4);
        print("case 3 sorted       ", partitionDisjoint(new int[]{1, 2, 3, 4, 5}), 1);
        print("case 4 two equal    ", partitionDisjoint(new int[]{1, 1}), 1);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
