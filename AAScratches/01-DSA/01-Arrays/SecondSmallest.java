import java.util.Arrays;

/**
 * Finds the second-smallest <strong>distinct</strong> value in an array of integers.
 *
 * <h2>Definition</h2>
 * <p>"Second smallest" here means the second smallest <em>distinct</em> value, not the
 * second element in sorted order. For example, {@code {1, 1, 2}} has second-smallest
 * value {@code 2}, not {@code 1} — the duplicate {@code 1} doesn't count twice.</p>
 *
 * <h2>Approach</h2>
 * <p>Single pass tracking the smallest and second-smallest distinct values seen so far:</p>
 * <ul>
 *   <li>If {@code num} is smaller than the current smallest, the old smallest demotes to
 *       second-smallest and {@code num} becomes the new smallest.</li>
 *   <li>Otherwise, if {@code num} is strictly greater than the current smallest (i.e. not
 *       a duplicate of it) and smaller than the current second-smallest, it becomes the
 *       new second-smallest.</li>
 * </ul>
 * <p>The {@code num > smallest} guard (not just {@code num < secondSmallest}) is what
 * enforces distinctness — without it, a duplicate of the smallest value would incorrectly
 * overwrite second-smallest.</p>
 *
 * <p>Time: {@code O(n)}. Space: {@code O(1)}.</p>
 *
 * <p>Throws {@link IllegalArgumentException} if the array has fewer than 2 elements, or
 * if it has fewer than 2 <em>distinct</em> values (e.g. {@code {5, 5, 5}}) — in either
 * case there is no well-defined second-smallest value.</p>
 */
class SecondSmallest {

    /**
     * Returns the second smallest distinct value in {@code nums}.
     *
     * @param nums the input array
     * @return the second smallest distinct value
     * @throws IllegalArgumentException if {@code nums} has fewer than 2 elements, or
     *                                  fewer than 2 distinct values
     */
    public static int secondSmallest(int[] nums) {
        if (nums.length < 2) {
            throw new IllegalArgumentException("Array must contain at least 2 elements");
        }

        long smallest = Long.MAX_VALUE;
        long secondSmallest = Long.MAX_VALUE;

        for (int num : nums) {
            if (num < smallest) {
                secondSmallest = smallest;
                smallest = num;
            } else if (num > smallest && num < secondSmallest) { // strictly > smallest enforces distinctness
                secondSmallest = num;
            }
        }

        if (secondSmallest == Long.MAX_VALUE) {
            throw new IllegalArgumentException("Array must contain at least 2 distinct values");
        }

        return (int) secondSmallest;
    }

    public static void main(String[] args) {
        int[] a = {0};              // too short -> throws
        int[] b = {0, 1};           // normal case -> 1
        int[] c = {1, 1, 2};        // duplicate of smallest ignored -> 2
        int[] d = {5, 5, 5};        // no second distinct value -> throws
        int[] e = {4, 2, 2, 1, 1};  // smallest=1, second distinct smallest=2 -> 2
        int[] f = {-3, -3, -1, 0};  // negatives -> -1

        for (int[] nums : new int[][]{a, b, c, d, e, f}) {
            try {
                System.out.println(Arrays.toString(nums) + " -> " + secondSmallest(nums));
            } catch (IllegalArgumentException ex) {
                System.out.println(Arrays.toString(nums) + " -> threw: " + ex.getMessage());
            }
        }
    }
}