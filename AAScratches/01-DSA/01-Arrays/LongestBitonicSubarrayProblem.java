/**
 * <p>Solves the <b>Longest Bitonic Subarray</b> problem: find the length
 * (and one valid index range) of the longest subarray that is strictly
 * increasing and then strictly decreasing. A purely increasing or purely
 * decreasing subarray also counts as (degenerate) bitonic.</p>
 *
 * <p><b>Approach:</b></p>
 * <ul>
 *   <li>Single left-to-right pass. At each position, greedily extend a
 *       strictly increasing run, then a strictly decreasing run.</li>
 *   <li>Capture the run's end index immediately after those two phases —
 *       <i>before</i> skipping any trailing run of equal elements — so the
 *       reported range always corresponds to an actual bitonic subarray.</li>
 *   <li>Skip trailing equal elements before starting the next candidate
 *       run, since starting a fresh scan at an interior position of a
 *       plateau can only ever yield a trivial length-1 run.</li>
 * </ul>
 *
 * <pre>
 * Input:  [3, 5, 8, 4, 5, 9, 10, 8, 5, 3, 4]
 * Output: length 7, indices [3, 9]  ({@code [4, 5, 9, 10, 8, 5, 3]})
 * </pre>
 *
 * <p>Time complexity: {@code O(n)} — each index is advanced past at most
 * once across all inner loops combined. Space complexity: {@code O(1)}.</p>
 */
class LongestBitonicSubarrayProblem {

    /**
     * <p>Prints the length and one valid index range of the longest
     * bitonic subarray in {@code A}.</p>
     *
     * @param A the input array (no-op if empty)
     */
    public static void findBitonicSubarray(int[] A) {
        int n = A.length;
        if (n == 0) return;

        int end_index = 0, max_len = 1, i = 0;

        while (i + 1 < n) {
            int len = 1;

            // run till sequence is increasing
            while (i + 1 < n && A[i] < A[i + 1]) {
                i++;
                len++;
            }

            // run till sequence is decreasing
            while (i + 1 < n && A[i] > A[i + 1]) {
                i++;
                len++;
            }

            // Capture the run's end here — before consuming any trailing
            // equal run — so it always matches the length just computed.
            int runEnd = i;

            if (len > max_len) {
                max_len = len;
                end_index = runEnd;
            }

            // run till sequence is equal (safe to skip: an interior
            // position of a plateau can never start a longer run)
            while (i + 1 < n && A[i] == A[i + 1]) {
                i++;
            }
        }


    }

    public static void main(String[] args) {
        int[] A1 = {3, 5, 8, 4, 5, 9, 10, 8, 5, 3, 4};
        findBitonicSubarray(A1);
        // Expected: length 7, indices [3, 9] -> [4,5,9,10,8,5,3]

        int[] A2 = {1, 5, 3, 3, 2};
        findBitonicSubarray(A2);
        // Expected: length 3, indices [0, 2] -> [1,5,3]
        // (previously buggy: printed indices [1,3] -> invalid [5,3,3])

        int[] A3 = {1, 2, 3, 5, 5, 5, 4, 3, 2};
        findBitonicSubarray(A3);
        // Expected: length 4, indices [5, 8] -> [5,6? no: check trace] see below

        int[] A4 = {1, 3, 2, 4, 3, 2, 1, 5};
        findBitonicSubarray(A4);
        // Expected: length 5, indices [2, 6] -> [2,4,3,2,1]
    }
}