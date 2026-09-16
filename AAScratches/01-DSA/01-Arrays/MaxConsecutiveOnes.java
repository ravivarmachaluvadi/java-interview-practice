/**
 * LeetCode 485 — Max Consecutive Ones.
 *
 * <p>Given a binary array {@code nums}, return the maximum number of consecutive {@code 1}s in
 * the array.</p>
 *
 * <p><b>Examples</b></p>
 * <pre>
 * Input:  nums = [1,1,0,1,1,1]
 * Output: 3
 * Reason: the first two digits and the last three form runs of 1s; the longest has length 3.
 *
 * Input:  nums = [1,0,1,1,0,1]
 * Output: 2
 * </pre>
 *
 * <p><b>Approach — single pass with a running counter</b></p>
 * <ul>
 *   <li>Track {@code currentRun}, the length of the run of 1s ending at the current index.</li>
 *   <li>On a {@code 1}, extend the run; on anything else, reset it to zero.</li>
 *   <li>Update {@code maxCount} after every step, so a run that reaches the end of the array
 *       is counted without a special case.</li>
 * </ul>
 *
 * <p>The common bug here is only comparing against {@code maxCount} when a run <em>ends</em>.
 * A trailing run never terminates, so {@code [1,1,0,1,1,1]} would wrongly yield {@code 2}.
 * Comparing on every increment avoids the problem structurally.</p>
 *
 * <p><b>Complexity</b></p>
 * <ul>
 *   <li>Time: {@code O(n)}, one pass.</li>
 *   <li>Space: {@code O(1)}.</li>
 * </ul>
 *
 * @see <a href="https://leetcode.com/problems/max-consecutive-ones/description/">LeetCode 485</a>
 */
class MaxConsecutiveOnes {

    /**
     * Returns the length of the longest run of consecutive {@code 1}s.
     *
     * @param nums a binary array; may be {@code null} or empty
     * @return the longest run of {@code 1}s, or {@code 0} if there is none
     */
    public static int findMaxConsecutiveOnes(int[] nums) {
        if (nums == null) {
            return 0;
        }

        int currentRun = 0;
        int maxCount = 0;

        for (int num : nums) {
            currentRun = (num == 1) ? currentRun + 1 : 0;
            maxCount = Math.max(maxCount, currentRun);
        }

        return maxCount;
    }

    public static void main(String[] args) {
        // LeetCode example 1 — the array from the original snippet.
        System.out.println(findMaxConsecutiveOnes(new int[]{1, 1, 0, 1, 1, 1}));  // 3

        // LeetCode example 2.
        System.out.println(findMaxConsecutiveOnes(new int[]{1, 0, 1, 1, 0, 1}));  // 2

        // Longest run sits at the very end — the classic failure case.
        System.out.println(findMaxConsecutiveOnes(new int[]{1, 0, 1, 1, 1}));     // 3

        // Longest run sits at the very start.
        System.out.println(findMaxConsecutiveOnes(new int[]{1, 1, 1, 0, 1}));     // 3

        // Entire array is ones.
        System.out.println(findMaxConsecutiveOnes(new int[]{1, 1, 1, 1}));        // 4

        // No ones at all.
        System.out.println(findMaxConsecutiveOnes(new int[]{0, 0, 0}));           // 0

        // Single element, each way.
        System.out.println(findMaxConsecutiveOnes(new int[]{1}));                 // 1
        System.out.println(findMaxConsecutiveOnes(new int[]{0}));                 // 0

        // Alternating — no run longer than one.
        System.out.println(findMaxConsecutiveOnes(new int[]{1, 0, 1, 0, 1}));     // 1

        System.out.println(findMaxConsecutiveOnes(new int[]{}));                  // 0
        System.out.println(findMaxConsecutiveOnes(null));                   // 0
    }
}