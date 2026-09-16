/**
 * LeetCode 169 — Majority Element.
 *
 * <p>Given an array {@code nums} of size {@code n}, return the majority element: the element
 * that appears more than {@code n / 2} times. You may assume that a majority element
 * <b>always exists</b> in the array.</p>
 *
 * <p><b>Examples</b></p>
 * <pre>
 * Input:  nums = [3,2,3]
 * Output: 3
 *
 * Input:  nums = [2,2,1,1,1,2,2]
 * Output: 2
 * </pre>
 *
 * <p><b>Approach — Boyer-Moore Voting</b></p>
 * <ul>
 *   <li>Hold a {@code candidate} and a running {@code count}.</li>
 *   <li>Whenever {@code count} falls to zero, adopt the current element as the new candidate.</li>
 *   <li>Increment {@code count} on a match, decrement it otherwise.</li>
 * </ul>
 *
 * <p>Intuition: every element that differs from the candidate cancels one of its occurrences.
 * A true majority element appears more often than all other elements combined, so it cannot be
 * fully cancelled — whatever survives the sweep must be it.</p>
 *
 * <p><b>Important:</b> the algorithm identifies the <em>only possible</em> majority element, not
 * a confirmed one. Given {@code [1,2,3]} it returns some element even though no majority exists.
 * When the guarantee is dropped, a verification pass is required — see
 * {@link #majorityElementVerified(int[])}.</p>
 *
 * <p><b>Complexity</b></p>
 * <ul>
 *   <li>Time: {@code O(n)}, a single pass.</li>
 *   <li>Space: {@code O(1)}.</li>
 * </ul>
 *
 * @see <a href="https://leetcode.com/problems/majority-element/description/">LeetCode 169</a>
 */
class MajorityElement {

    /**
     * Returns the majority element, assuming one is guaranteed to exist.
     *
     * @param nums the input array; assumed non-empty and to contain a majority element
     * @return the element occurring more than {@code nums.length / 2} times
     */
    public static int majorityElement(int[] nums) {
        int candidate = 0;
        int count = 0;

        for (int num : nums) {
            if (count == 0) {
                candidate = num;
            }
            count += (num == candidate) ? 1 : -1;
        }

        return candidate;
    }

    /**
     * Returns the majority element, or {@code -1} when the array has no majority.
     *
     * <p>Adds a verification pass over the candidate produced by
     * {@link #majorityElement(int[])}, which is what the interview follow-up usually asks for
     * once the "a majority always exists" guarantee is removed. Still {@code O(n)} time and
     * {@code O(1)} space — two passes rather than one.</p>
     *
     * @param nums the input array; may be {@code null} or empty
     * @return the majority element, or {@code -1} if none exists
     */
    public static int majorityElementVerified(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int candidate = majorityElement(nums);

        int occurrences = 0;
        for (int num : nums) {
            if (num == candidate) {
                occurrences++;
            }
        }

        return occurrences > nums.length / 2 ? candidate : -1;
    }

    public static void main(String[] args) {
        // LeetCode example 1.
        System.out.println(majorityElement(new int[]{3, 2, 3}));                  // 3

        // LeetCode example 2 — the array from the original snippet.
        System.out.println(majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}));      // 2

        // Single element is trivially its own majority.
        System.out.println(majorityElement(new int[]{1}));                        // 1

        // Majority sits at the end rather than the start.
        System.out.println(majorityElement(new int[]{6, 5, 5}));                  // 5

        // Majority is interrupted but never fully cancelled.
        System.out.println(majorityElement(new int[]{1, 1, 1, 2, 2}));            // 1

        // Negative values work the same way.
        System.out.println(majorityElement(new int[]{-1, -1, 2, -1}));            // -1

        // --- Where the guarantee matters ---

        // No majority exists. The plain version still returns a value, and it is meaningless.
        System.out.println(majorityElement(new int[]{1, 2, 3}));                  // 3 (not a majority)
        System.out.println(majorityElementVerified(new int[]{1, 2, 3}));          // -1

        // Exactly n/2 occurrences is NOT a majority — the bound is strict.
        System.out.println(majorityElementVerified(new int[]{1, 1, 2, 2}));       // -1

        // Verified version agrees with the plain one when a majority is present.
        System.out.println(majorityElementVerified(new int[]{2, 2, 1, 1, 1, 2, 2})); // 2

        System.out.println(majorityElementVerified(new int[]{}));                 // -1
        System.out.println(majorityElementVerified(null));                        // -1
    }
}