/*
 * =====================================================================
 *  Bucket Sort (floats in [0, 1])                          Medium
 * =====================================================================
 *
 * PROBLEM
 *   Sort a float array whose values are uniformly spread over [0, 1], in better
 *   than comparison-sort time. Duplicates are allowed; the array may be empty or
 *   hold a single value. Values outside [0, 1] are NOT supported - scale them
 *   into that range first.
 *
 * EXAMPLE
 *   [0.897, 0.565, 0.656, 0.123, 0.665, 0.343]
 *        ->  [0.123, 0.343, 0.565, 0.656, 0.665, 0.897]
 *   [0.11, 0.13, 0.12]  ->  [0.11, 0.12, 0.13]   (all land in bucket 0: worst case)
 *   [0.0, 1.0]          ->  [0.0, 1.0]           (1.0 is clamped into the last bucket)
 *
 * APPROACH  (distribute by key, sort each bucket, concatenate)
 *   1. Make n buckets for n elements, so the expected load per bucket is 1.
 *   2. Bucket index for a value v is (int)(n * v). That formula is the whole trick:
 *      it maps the key range onto bucket numbers directly, no comparisons.
 *   3. Because v = 1.0 would give index n, clamp the index to n - 1.
 *   4. Sort each bucket with Arrays.sort over just its filled prefix.
 *   5. Walk the buckets in index order and copy values back. Buckets are ordered
 *      by construction, so concatenation alone finishes the sort.
 *
 * KEY INSIGHT
 *   Comparison sorts cannot beat O(n log n), but this one is not comparing across
 *   the whole array - it is using the VALUE as an address, the way counting sort
 *   and radix sort do. The speed is bought with an assumption: keys are bounded
 *   and roughly uniform. Break uniformity and everything lands in one bucket and
 *   you are back to the inner sort's O(n log n) or worse. Remember the bucket-index
 *   formula - Maximum Gap (LeetCode 164) is this formula plus a pigeonhole argument.
 *
 * COMPLEXITY
 *   Time  O(n^2) as written: `new float[n][n]` allocates and zero-fills n arrays of
 *         n floats before a single element is placed, and that dominates the rest.
 *         The three real passes cost O(n + k log k) average, where k is the load of
 *         a bucket; with uniform keys k is about 1, so the List<Float>[] or two-pass
 *         version is the one that actually runs in O(n), degrading to O(n log n)
 *         when all n values fall into one bucket and Arrays.sort does all the work.
 *   Space O(n^2) as written, because each of the n buckets is preallocated to
 *         length n. ArrayList buckets or a counting pass would make it O(n).
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you sort arbitrary floats? (find min/max, index by (v-min)/(max-min))
 *   - Bucket vs counting vs radix sort: which assumption does each one make?
 *   - Is bucket sort stable? (Only if the per-bucket sort is; Arrays.sort on
 *     primitives is quicksort, so this version is not.)
 *   - Fix the O(n^2) space: use List<Float>[] or two passes (count, then place).
 *
 * RUN
 *   main() runs 4 cases (typical, empty, all in one bucket, 0.0/1.0 boundary) and
 *   prints actual vs expected.
 */
import java.util.Arrays;

class BucketSort {

    public static void bucketSort(float[] arr) {
        int n = arr.length;
        if (n == 0) {
            return;
        }

        // 1. n buckets, each sized for the worst case where everything lands in one.
        float[][] buckets = new float[n][n];
        int[] bucketSizes = new int[n];              // how many slots of each bucket are used

        // 2. Distribute: the value itself decides the bucket, no comparisons.
        for (float value : arr) {
            int bucketIndex = (int) (n * value);
            if (bucketIndex >= n) {
                bucketIndex = n - 1;                 // value == 1.0 would otherwise overflow
            }
            buckets[bucketIndex][bucketSizes[bucketIndex]++] = value;
        }

        // 3. Sort only the filled prefix of each bucket.
        for (int i = 0; i < n; i++) {
            if (bucketSizes[i] > 1) {                // 0 or 1 element is already sorted
                Arrays.sort(buckets[i], 0, bucketSizes[i]);
            }
        }

        // 4. Buckets are already in ascending key order, so concatenation sorts.
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < bucketSizes[i]; j++) {
                arr[index++] = buckets[i][j];
            }
        }
    }

    public static void main(String[] args) {
        float[] typical = {0.897f, 0.565f, 0.656f, 0.123f, 0.665f, 0.343f};
        bucketSort(typical);
        print("case 1 typical    ", typical, "[0.123, 0.343, 0.565, 0.656, 0.665, 0.897]");

        float[] empty = {};
        bucketSort(empty);
        print("case 2 empty      ", empty, "[]");

        // All three values map to bucket 0 - correct, but this is the worst case.
        float[] oneBucket = {0.11f, 0.13f, 0.12f};
        bucketSort(oneBucket);
        print("case 3 one bucket ", oneBucket, "[0.11, 0.12, 0.13]");

        float[] boundary = {1.0f, 0.0f};
        bucketSort(boundary);
        print("case 4 0.0 and 1.0", boundary, "[0.0, 1.0]");
    }

    private static void print(String label, float[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }
}
