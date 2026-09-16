/**
 * Problem: Sort an integer array in ascending order using the Bubble Sort algorithm.
 *
 * Approach: Repeatedly iterate through the array, swapping adjacent elements that are out of order.
 * Each outer loop iteration guarantees that the largest unsorted element moves to its correct
 * position at the end of the array. The inner loop runs only over the unsorted portion,
 * reducing comparisons as sorting progresses.
 *
 * Time Complexity: O(n²) in worst and average cases; best case (already sorted) can be optimized to O(n)
 * with a flag, but this implementation does not use that optimization.
 *
 * Space Complexity: O(1) auxiliary space – the sort is performed in place using only a few temporary variables.
 */
import java.util.Arrays;

class BubbleSort {
    public static void main(String[] args) {
        int[] arr = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        bubbleSort(arr);
        System.out.println(Arrays.toString(arr));
    }
    private static void bubbleSort(int[] arr) {
        int n = arr.length - 1;
        for (int i = 0; i <= n; i++) {
            // for every entire iteration of array move largest
            // val to end of array and exclude the part in next
            for (int j = 1; j <= n - i; j++) {
                if (arr[j] < arr[j - 1]) {
                    // swap
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                }
            }
        }
    }
}
