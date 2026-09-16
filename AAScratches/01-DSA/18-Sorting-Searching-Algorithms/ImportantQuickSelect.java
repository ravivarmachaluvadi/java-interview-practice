/**
 * Finds the k-th smallest element in an array using QuickSelect.
 *
 * The algorithm repeatedly partitions the array around a pivot (chosen as the last
 * element of the current subarray). After each partition, it recurses into the
 * side that must contain the desired order statistic until the pivot lands at
 * index k. The array is partially sorted such that elements left of the pivot
 * are smaller and those right are larger.
 *
 * Time Complexity: O(n) on average, O(n²) worst‑case (when pivots are poor).
 * Space Complexity: O(1) auxiliary space – in‑place partitioning with recursion
 * stack depth up to O(log n) on average, O(n) worst‑case.
 */
class ImportantQuickSelect {
    public static void main(String[] args) {
        int[] arr = {7, 1, 4, 2, 8, 5, 0, 9, 3};
        int quickSelect = quickSelect(0, arr.length - 1, 1, arr);
        System.out.println(quickSelect);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
        }
    }

    private static int quickSelect(int low, int high, int k, int[] arr) {
        int partitionIndex = partition(low, high, arr);
        if (partitionIndex == k) return arr[k];
        else if (partitionIndex < k)
            return quickSelect(partitionIndex + 1, high, k, arr);

        return quickSelect(low, partitionIndex - 1, k, arr);
    }

    private static int partition(int low, int high, int[] arr) {
        int pivotValue = arr[high];
        int partitionIndex = low;
        for (int j = partitionIndex; j < high; j++) {
            if (arr[j] < pivotValue) {
                int lessThanPivot = arr[j];
                arr[j] = arr[partitionIndex];
                arr[partitionIndex] = lessThanPivot;
                // values left side of partitionIndex
                // are smaller than pivotValue
                partitionIndex++;
            }
        }
        int temp = arr[partitionIndex];
        arr[partitionIndex] = arr[high];
        arr[high] = temp;
        return partitionIndex;
    }
}