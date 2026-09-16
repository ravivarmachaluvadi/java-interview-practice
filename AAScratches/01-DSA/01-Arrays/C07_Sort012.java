import java.util.Arrays;

/**
 * LeetCode 75 — Sort Colors (Dutch National Flag problem).
 *
 * <p>Given an array {@code nums} with {@code n} objects colored red ({@code 0}),
 * white ({@code 1}), or blue ({@code 2}), sort them in place so that objects of the
 * same color are adjacent, in the order red, white, blue — without using a library
 * sort.</p>
 *
 * <h2>Approach — Dutch National Flag (three-way partition)</h2>
 * <p>Maintain three pointers that partition the array into four regions as the scan
 * proceeds:</p>
 * <ul>
 *   <li>{@code nums[0 .. low-1]} — known {@code 0}s</li>
 *   <li>{@code nums[low .. mid-1]} — known {@code 1}s</li>
 *   <li>{@code nums[mid .. high]} — unexamined</li>
 *   <li>{@code nums[high+1 .. n-1]} — known {@code 2}s</li>
 * </ul>
 * <p>At each step, {@code nums[mid]} is classified:</p>
 * <ul>
 *   <li>{@code 0}: swap into the {@code low} region; both {@code low} and
 *       {@code mid} advance.</li>
 *   <li>{@code 1}: already in the correct region; only {@code mid} advances.</li>
 *   <li>{@code 2}: swap into the {@code high} region; only {@code high} retreats.
 *       {@code mid} deliberately does <strong>not</strong> advance, since the element
 *       just swapped in from {@code high} is unexamined and must be classified on
 *       the next iteration.</li>
 * </ul>
 *
 * <p>Time: {@code O(n)}. Space: {@code O(1)}, sorted in place.</p>
 *
 * <p>Uses the arrow-form {@code switch} statement (Java 14+, JEP 361): each case
 * body runs with no fall-through and no {@code break}, which removes the classic
 * "forgot a break" bug class entirely. This is a {@code switch} <em>statement</em>,
 * not an <em>expression</em> — each branch executes multiple side-effecting
 * statements ({@code swap} plus pointer updates) rather than producing a value, so
 * a {@code yield}-based switch expression isn't the right fit here.</p>
 */
class Sort012 {

    /**
     * Sorts {@code nums} in place so that all {@code 0}s come before all {@code 1}s,
     * which come before all {@code 2}s.
     *
     * @param nums array containing only the values {@code 0}, {@code 1}, and {@code 2}
     */
    public void sortColors(int[] nums) {
        int low = 0;
        int mid = 0;
        int high = nums.length - 1;

        while (mid <= high) {
            switch (nums[mid]) {
                case 0 -> { // 0 -> swap into the low region, both pointers advance
                    swap(nums, low, mid);
                    low++;
                    mid++;
                }
                case 1 -> mid++; // 1 -> already correctly placed, just advance mid
                case 2 -> { // 2 -> swap into the high region; mid stays to recheck the swapped-in value
                    swap(nums, mid, high);
                    high--;
                }
                default -> throw new IllegalArgumentException(
                        "Unexpected value: " + nums[mid] + " (expected 0, 1, or 2)");
            }
        }
    }

    /**
     * Swaps the elements at indices {@code i} and {@code j} in {@code nums}.
     *
     * @param nums the array containing the elements to swap
     * @param i    index of the first element
     * @param j    index of the second element
     */
    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        Sort012 solution = new Sort012();

        int[] nums1 = {2, 0, 2, 1, 1, 0};
        solution.sortColors(nums1);
        System.out.println(Arrays.toString(nums1)); // [0, 0, 1, 1, 2, 2]

        int[] nums2 = {2, 0, 1};
        solution.sortColors(nums2);
        System.out.println(Arrays.toString(nums2)); // [0, 1, 2]

        int[] nums3 = {0}; // single element
        solution.sortColors(nums3);
        System.out.println(Arrays.toString(nums3)); // [0]

        int[] nums4 = {1, 1, 1, 1}; // all same color
        solution.sortColors(nums4);
        System.out.println(Arrays.toString(nums4)); // [1, 1, 1, 1]

        int[] nums5 = {2, 2, 1, 0, 0}; // reverse-ish order
        solution.sortColors(nums5);
        System.out.println(Arrays.toString(nums5)); // [0, 0, 1, 2, 2]

        int[] nums6 = {}; // empty array
        solution.sortColors(nums6);
        System.out.println(Arrays.toString(nums6)); // []
    }
}