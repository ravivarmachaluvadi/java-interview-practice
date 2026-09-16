/**
 * Problem: Sort an integer array in ascending order using the Shell sort algorithm.
 *
 * Approach: Use a decreasing gap sequence (starting at N/2 and halving each iteration). For each gap, perform
 * a gapped insertion sort: shift elements that are 'gap' positions apart until the current element fits into its
 * correct position within the subarray defined by the gap.
 *
 * Time Complexity:
 *   Best case: O(n log n) with optimal gaps (though this implementation uses N/2, N/4,...).
 *   Average/Worst case: O(n^(3/2)) for typical gap sequences; worst-case can be O(n^2) if gaps are poor.
 *
 * Space Complexity: O(1) auxiliary space – sorting is performed in-place.
 */
import java.util.Arrays;

class ShellSort {

    static void shellSort(int[] arr) {
        int N = arr.length;
        for (int gap = N / 2; gap > 0; gap = gap / 2) {
            for (int j = gap; j < N; j += 1) {
                // save arr[j] in temp and make a hole at position j
                int temp = arr[j];
                // shift earlier gap-sorted elements up until the correct location for arr[j] is found
                int i;
                for (i = j; i >= gap && arr[i - gap] > temp; i = i - gap)
                    arr[i] = arr[i - gap];
                // put temp (the original arr[i]) in its correct location
                arr[i] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {21, 12, 14, 46, 7, 25, 10, 62, 19, 31, 1};
        shellSort(arr);
        System.out.println(Arrays.toString(arr));

    }
}

