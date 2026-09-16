import java.util.Arrays;

class MergeSort {
    public int[] sortArray(int[] nums) {
        return mergeSort(0, nums.length - 1, nums);
    }

    private int[] mergeSort(int left, int right, int[] nums) {
        if (left >= right) {
            // Base case, single element array either left or right
            return new int[]{nums[right]};
        }

        int mid = (left + right) / 2;
        int[] leftPart = mergeSort(left, mid, nums);
        int[] rightPart = mergeSort(mid + 1, right, nums);

        return merge(leftPart, rightPart);
    }

    private int[] merge(int[] arr1, int[] arr2) {
        int m = arr1.length;
        int n = arr2.length;
        int[] result = new int[m + n];
        int i = 0, j = 0, k = 0;

        // Merge both arrays
        while (i < m && j < n) {
            if (arr1[i] <= arr2[j]) {
                result[k++] = arr1[i++];
            } else {
                result[k++] = arr2[j++];
            }
        }

        // Add remaining elements from arr1
        while (i < m) {
            result[k++] = arr1[i++];
        }

        // Add remaining elements from arr2
        while (j < n) {
            result[k++] = arr2[j++];
        }

        return result;
    }

    public static void main(String[] args) {
        MergeSort solution = new MergeSort();

        // Example input
        int[] nums = {5, 2, 9, 1, 5, 6};

        // Call sortArray method and store result
        int[] sortedArray = solution.sortArray(nums);

        // Print the sorted array
        System.out.println("Original array: " + Arrays.toString(nums));
        System.out.println("Sorted array: " + Arrays.toString(sortedArray));
    }
}

