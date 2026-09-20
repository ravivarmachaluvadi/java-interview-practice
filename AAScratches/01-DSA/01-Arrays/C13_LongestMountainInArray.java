/*
 * =====================================================================
 *  Longest Mountain in Array                          LeetCode 845 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, return the length of the longest subarray that is a
 *   "mountain": strictly increasing up to a peak, then strictly decreasing, with at
 *   least one element on each side of the peak (so length >= 3). Return 0 if none.
 *
 * EXAMPLE
 *   arr = [2, 1, 4, 7, 3, 2, 5]           ->  5   mountain [1, 4, 7, 3, 2]
 *   arr = [875, 884, 239, 731, 723, 685]  ->  4   two peaks; [239, 731, 723, 685] wins
 *   arr = [1, 3, 3, 1]                    ->  0   plateau at the top breaks strictness
 *   arr = [1, 2, 3, 4, 5]                 ->  0   ascent with no descent
 *   arr = []                              ->  0   fewer than 3 elements
 *
 * APPROACH  (find peak, expand outward)
 *   1. Scan i over 1 .. n-2. Index 0 and n-1 can never be a peak (no neighbour on one side).
 *   2. i is a peak when arr[i-1] < arr[i] > arr[i+1].
 *   3. From a peak, walk left while the slope keeps strictly rising, and walk right
 *      while it keeps strictly falling.
 *   4. The mountain is [left, right]; its length is right - left + 1. Keep the maximum.
 *   5. A peak found this way already has both slopes, so the span is >= 3 automatically.
 *
 * KEY INSIGHT
 *   Anchor on the peak, not on the left edge. Every strict up-then-down run has exactly
 *   one peak, so each mountain is measured exactly once and the inner while-loops never
 *   re-scan a slope for a different peak. Pattern to recognise: when a shape has a
 *   unique "centre", find the centre and expand (same trick as expand-around-centre
 *   for palindromes). Compare C12_LongestBitonicSubarrayProblem, which scans the same
 *   shape left to right instead.
 *
 * COMPLEXITY
 *   Time  O(n)  each element is crossed by at most two expansions (one per adjacent peak)
 *   Space O(1)  three ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Non-strict slopes (plateaus allowed): change < to <= and decide how a flat peak counts.
 *   - Return the mountain's indices, not just its length: record left/right at the max.
 *   - Single-pass alternative: track up-run and down-run lengths from the left, the way
 *     C12_LongestBitonicSubarrayProblem does.
 *   - Count all mountains instead of the longest: increment a counter per peak.
 *
 * RUN
 *   main() runs 5 cases (typical, two peaks, plateau, one-sided slope, empty/null)
 *   and prints actual vs expected.
 */

class LongestMountainInArray {

    public static int longestMountain(int[] arr) {
        if (arr == null || arr.length < 3) {
            return 0;
        }

        int max = 0;

        // Skip index 0 and n-1: a peak needs a neighbour on both sides.
        for (int i = 1; i < arr.length - 1; i++) {
            if (!isPeak(arr, i)) {
                continue;
            }

            int left = i - 1;
            int right = i + 1;

            // Walk down the ascending slope while it stays strictly increasing.
            while (left > 0 && arr[left - 1] < arr[left]) {
                left--;
            }
            // Walk down the descending slope while it stays strictly decreasing.
            while (right < arr.length - 1 && arr[right] > arr[right + 1]) {
                right++;
            }

            // Span is already >= 3 (peak plus one neighbour each side), no extra check needed.
            max = Math.max(max, right - left + 1);
        }

        return max;
    }

    /** A peak is strictly greater than both neighbours. */
    private static boolean isPeak(int[] arr, int i) {
        return arr[i - 1] < arr[i] && arr[i] > arr[i + 1];
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Typical: LeetCode example 1, mountain [1, 4, 7, 3, 2].
        print("case 1 typical   ", longestMountain(new int[]{2, 1, 4, 7, 3, 2, 5}), 5);

        // Tricky: two candidate peaks, the second one is longer.
        print("case 2 two peaks ", longestMountain(new int[]{875, 884, 239, 731, 723, 685}), 4);

        // Tricky: plateau at the peak breaks strict monotonicity.
        print("case 3 plateau   ", longestMountain(new int[]{1, 3, 3, 1}), 0);

        // Edge: ascent with no descent, and descent with no ascent.
        print("case 4 one-sided ", longestMountain(new int[]{1, 2, 3, 4, 5})
                + longestMountain(new int[]{5, 4, 3, 2, 1}), 0);

        // Edge: below the minimum length, empty and null.
        print("case 5 too short ", longestMountain(new int[]{1, 2})
                + longestMountain(new int[]{}) + longestMountain(null), 0);
    }
}
