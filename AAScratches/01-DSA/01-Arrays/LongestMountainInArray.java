// https://leetcode.com/problems/longest-mountain-in-array/description/

/**
 * LeetCode 845 — Longest Mountain in Array.
 *
 * <p>An array {@code arr} is a <b>mountain</b> if and only if:</p>
 * <ul>
 *   <li>{@code arr.length >= 3}, and</li>
 *   <li>there exists a peak index {@code i} with {@code 0 < i < arr.length - 1} such that
 *       {@code arr[0] < arr[1] < ... < arr[i]} and
 *       {@code arr[i] > arr[i + 1] > ... > arr[arr.length - 1]}.</li>
 * </ul>
 *
 * <p>Given an integer array {@code arr}, return the length of the longest subarray which is a
 * mountain, or {@code 0} if there is no mountain subarray. Note that both slopes must be
 * <em>strictly</em> monotonic, so a flat stretch cannot be part of a mountain.</p>
 *
 * <p><b>Examples</b></p>
 * <pre>
 * Input:  arr = [2,1,4,7,3,2,5]
 * Output: 5
 * Reason: the longest mountain is [1,4,7,3,2], of length 5.
 *
 * Input:  arr = [2,2,2]
 * Output: 0
 * Reason: no strictly increasing then strictly decreasing run exists.
 * </pre>
 *
 * <p><b>Approach — locate each peak, then expand outward</b></p>
 * <ul>
 *   <li>Scan {@code i} over {@code 1..arr.length - 2}. Index {@code i} is a peak when
 *       {@code arr[i - 1] < arr[i] && arr[i] > arr[i + 1]}.</li>
 *   <li>From a peak, walk {@code left} down the strictly increasing slope and {@code right}
 *       down the strictly decreasing slope.</li>
 *   <li>The mountain spans {@code [left, right]}, so its length is
 *       {@code right - left + 1}. Track the maximum.</li>
 * </ul>
 *
 * <p><b>Complexity</b></p>
 * <ul>
 *   <li>Time: {@code O(n)}. The nested loops look quadratic, but each strictly monotonic run
 *       contains exactly one peak, so every element is visited by at most two expansions.</li>
 *   <li>Space: {@code O(1)}.</li>
 * </ul>
 */
public static int longestMountain(int[] arr) {
    if (arr == null || arr.length < 3) {
        return 0;
    }

    int max = 0;

    // Skip index 0 and n-1: a peak needs a neighbour on both sides.
    for (int i = 1; i < arr.length - 1; i++) {

        // Peak: strictly greater than both neighbours.
        if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
            int left = i - 1;
            int right = i + 1;

            // Walk down the ascending slope.
            while (left > 0 && arr[left - 1] < arr[left]) {
                left--;
            }
            // Walk down the descending slope.
            while (right < arr.length - 1 && arr[right] > arr[right + 1]) {
                right++;
            }

            // Span is already >= 3, so no extra length check is needed.
            max = Math.max(max, right - left + 1);
        }
    }

    return max;
}

void main() {
    // LeetCode example 1 — mountain [1,4,7,3,2].
    IO.println(longestMountain(new int[]{2, 1, 4, 7, 3, 2, 5}));          // 5

    // LeetCode example 2 — flat array, no strict slopes.
    IO.println(longestMountain(new int[]{2, 2, 2}));                      // 0

    // The array from the original snippet — mountain [2,5,4,3,2,1].
    IO.println(longestMountain(new int[]{2, 1, 4, 7, 3, 2, 5, 4, 3, 2, 1})); // 6

    // Smallest possible mountain.
    IO.println(longestMountain(new int[]{0, 1, 0}));                      // 3

    // Plateau at the peak breaks strict monotonicity.
    IO.println(longestMountain(new int[]{1, 3, 3, 1}));                   // 0

    // Ascent with no descent.
    IO.println(longestMountain(new int[]{1, 2, 3, 4, 5}));                // 0

    // Descent with no ascent.
    IO.println(longestMountain(new int[]{5, 4, 3, 2, 1}));                // 0

    // Two candidate peaks; the second is longer.
    IO.println(longestMountain(new int[]{875, 884, 239, 731, 723, 685})); // 4

    // Below the minimum mountain length.
    IO.println(longestMountain(new int[]{1, 2}));                         // 0
    IO.println(longestMountain(new int[]{}));                             // 0
    IO.println(longestMountain(null));                                    // 0
}