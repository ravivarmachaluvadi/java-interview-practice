/**
 * Bucket Sort for floating point numbers in the range [0,1).
 *
 * The algorithm distributes each element into one of n buckets based on its value,
 * sorts each bucket with Java's Arrays.sort (which uses Dual-Pivot Quicksort),
 * and then concatenates the buckets back into the original array.
 *
 * Time Complexity: O(n + k log k) where k is the average number of elements per bucket
 * (for uniform distribution this becomes O(n)). Worst case O(n²) if all elements fall in one bucket.
 * Space Complexity: O(n²) due to preallocating n buckets each of size n; can be reduced with dynamic lists.
 */
import java.util.Arrays;

class BucketSort {

    public static void bucketSort(float[] arr) {
        int n = arr.length;
        if (n == 0) return;

        // 1. Create empty buckets (each bucket is an array)
        float[][] buckets = new float[n][n];
        int[] bucketSizes = new int[n]; // Track the size of each bucket

        // 2. Distribute elements into buckets
        for (float value : arr) {
            int bucketIndex = (int) (n * value); // Calculate bucket index
            buckets[bucketIndex][bucketSizes[bucketIndex]++] = value; // Add element to bucket
        }

        // 3. Sort individual buckets
        for (int i = 0; i < n; i++) {
            if (bucketSizes[i] > 0) {
                // Sort only the filled portion of each bucket
                Arrays.sort(buckets[i], 0, bucketSizes[i]);
            }
        }

        // 4. Concatenate all buckets into arr
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < bucketSizes[i]; j++) {
                arr[index++] = buckets[i][j];
            }
        }
    }

    public static void main(String[] args) {
        float[] arr = {0.897f, 0.565f, 0.656f, 0.123f, 0.665f, 0.343f};

        System.out.println("Original array: " + Arrays.toString(arr));

        bucketSort(arr);

        System.out.println("\nSorted array: " + Arrays.toString(arr));

    }
}
