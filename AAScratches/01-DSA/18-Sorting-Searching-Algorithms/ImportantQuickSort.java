import java.util.Arrays;
class ImportantQuickSort {

    public static void main(String[] args) {
        int[] array = {12, 4, 8, 1, 3, 15, 7, 9};
        System.out.println("Original Array: " + Arrays.toString(array));

        ImportantQuickSort sorter = new ImportantQuickSort();
        sorter.quickSort(array, 0, array.length - 1);
        System.out.println("QuickSorted Array: " + Arrays.toString(array));
    }

    // Method to perform QuickSort
    public void quickSort(int[] array, int low, int high) {
        if (low < high) {
            // Get the partition index
            //P QQ <--> ms ms merge
            int partitionIndex = partition(array, low, high);

            // Recursively sort elements before and after partition
            quickSort(array, low, partitionIndex - 1);
            quickSort(array, partitionIndex + 1, high);
        }
    }

    // Partition method used in QuickSort
    private int partition(int[] array, int low, int high) {
        int pivot = array[high];  // Choose the last element as pivot
        int index = low - 1;  // Pointer for the smaller element

        for (int j = low; j < high; j++) {
            // If the current element is smaller than or equal to the pivot, swap it
            if (array[j] <= pivot) {
                index++;
                swap(array, index, j);
            }
        }
        // Swap the pivot element with the element at i+1 position
        swap(array, index + 1, high);

        return index + 1;  // Return the partition index
    }

    // Method to swap two elements in the array
    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
