class MergeTwoSortedArrays {
    public static void main(String[] args) {
        int[] arr1 = {1, 3, 5, 7, 9, 11};
        int[] arr2 = {0, 2, 4, 6, 8, 10};

        int m = arr1.length;
        int n = arr2.length;

        int length = m + n;
        // Calculate the initial gap
        int gap = (length / 2) + (length % 2);
        while (gap > 0) {
            int left = 0;
            int right = left + gap;
            while (right < length) {
                // Case 1: arr1 and arr2 comparison
                if (left < m && right >= m) {
                    swapIfGreater(left, right - m, arr1, arr2);
                }
                // Case 2: arr2 and arr2 comparison
                else if (left >= m) {
                    swapIfGreater(left - m, right - m, arr2, arr2);
                }
                // Case 3: arr1 and arr1 comparison
                else {
                    swapIfGreater(left, right, arr1, arr1);
                }
                left++;
                right++;
            }
            if (gap == 1)
                break; // When gap is 1, we can stop further halving

            // Reduce gap for the next iteration
            gap = (gap / 2) + (gap % 2);
        }

        // Output the merged arrays
        System.out.println("Array 1:");
        for (int i : arr1) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.println("Array 2:");
        for (int i : arr2) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    private static void swapIfGreater(int left, int right, int[] arr1, int[] arr2) {
        if (arr1[left] > arr2[right]) {
            int tempVal = arr1[left];
            arr1[left] = arr2[right];
            arr2[right] = tempVal;
        }
    }
}
