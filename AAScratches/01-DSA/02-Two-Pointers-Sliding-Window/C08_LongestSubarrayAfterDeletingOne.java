/*
 * =====================================================================
 *  Longest Subarray of 1's After Deleting One Element   LeetCode 1493 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a binary array nums, delete exactly one element and return the length
 *   of the longest subarray of only 1s in what remains. If no such subarray
 *   exists return 0. Deleting is mandatory: an all-ones array answers n - 1.
 *
 * EXAMPLE
 *   nums = [1,1,0,1,1,1,0,1]  ->  5   delete the first zero: 1,1 + 1,1,1
 *   nums = [1,1,1]            ->  2   must still delete one element
 *   nums = [0]                ->  0
 *   nums = [1,0,0,1]          ->  1   two zeros in a row cannot both be deleted
 *   nums = [0,0,0]            ->  0
 *
 * APPROACH  (author: run lengths around each zero)
 *   1. Walk the array counting the current run of 1s (currCount) and remembering
 *      the run just before the last zero (prevCount).
 *   2. At each zero: candidate = prevCount + currCount (delete this zero and join
 *      the two runs), then prevCount = currCount, currCount = 0. Two zeros in a
 *      row make prevCount 0, which is what stops runs from joining across them.
 *   3. After the loop take one last candidate prevCount + currCount.
 *   4. If no zero was ever seen the array is all ones: return n - 1.
 *
 * APPROACH  (alternative: window with at most one zero, minus one)
 *   1. Same "at most K violations" window as Max Consecutive Ones III with k = 1.
 *   2. Report right - left (window length minus one) instead of the length: the
 *      deleted element is either the single zero or, in an all-ones window, any
 *      one of the ones. This handles the all-ones case with no special branch.
 *
 * KEY INSIGHT
 *   This is the k = 1 case of "longest window with at most k zeros", with an
 *   off-by-one trap: the deletion is mandatory, so the answer is always the
 *   window length minus one, never the raw length. Whichever method you use,
 *   test the all-ones input first; that is where most solutions fail.
 *
 * COMPLEXITY
 *   Time  O(n)   single pass in both methods
 *   Space O(1)   a few integer counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Delete at most one element instead of exactly one: all-ones returns n.
 *   - Flip up to k zeros instead of deleting (LC 1004): same window, report length.
 *   - Delete one element to maximise the longest run of a given value v.
 *   - Return which index to delete, not just the resulting length.
 *
 * RUN
 *   main() runs 5 cases (typical, all ones, single zero, adjacent zeros, all zeros)
 *   through both methods and prints actual vs expected.
 */
class LongestSubarrayAfterDeletingOne {

    // Author's approach: track the run of 1s before and after the most recent zero.
    public static int longestSubarray(int[] nums) {
        boolean zeroFound = false;
        int prevCount = 0; // run of 1s just before the last zero
        int currCount = 0; // run of 1s since the last zero
        int best = 0;

        for (int num : nums) {
            if (num == 0) {
                zeroFound = true;
                best = Math.max(best, prevCount + currCount); // delete this zero, join the runs
                prevCount = currCount;
                currCount = 0;
            } else {
                currCount++;
            }
        }
        best = Math.max(best, prevCount + currCount); // the final run after the last zero

        if (!zeroFound) {
            return nums.length - 1; // all ones: one element must still be deleted
        }
        return best;
    }

    // Alternative: sliding window allowing at most one zero; answer = window length - 1.
    public static int longestSubarrayWindow(int[] nums) {
        int left = 0;
        int zeroCount = 0;
        int best = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroCount++;
            }
            while (zeroCount > 1) {
                if (nums[left] == 0) {
                    zeroCount--;
                }
                left++;
            }
            best = Math.max(best, right - left); // window length minus the deleted element
        }
        return best;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] inputs = {
            {1, 1, 0, 1, 1, 1, 0, 1},
            {1, 1, 1},
            {0},
            {1, 0, 0, 1},
            {0, 0, 0}
        };
        String[] names = {"typical       ", "all ones      ", "single zero   ",
                          "adjacent zeros", "all zeros     "};
        int[] expected = {5, 2, 0, 1, 0};
        for (int i = 0; i < inputs.length; i++) {
            String label = "case " + (i + 1) + " " + names[i];
            print(label + " run-length", longestSubarray(inputs[i]), expected[i]);
            print(label + " window    ", longestSubarrayWindow(inputs[i]), expected[i]);
        }
    }
}
