class HeapSort {
    public void heapSort(int[] arr) {
        int n = arr.length;
        // Build the max heap
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);

        // Extract elements from the heap one by one
        for (int i = n - 1; i >= 0; i--) {
            // Move the current root (maximum) to the end
            int max = arr[0];
            arr[0] = arr[i];
            arr[i] = max;
            // Heapify the reduced heap with i as limit in size
            heapify(arr, i, 0);
        }
    }

    // To heapify a subtree rooted at index i
    void heapify(int[] arr, int n, int i) {
        int largest = i;       // Initialize largest as root
        int left = 2 * i + 1;  // left = 2*i + 1
        int right = 2 * i + 2; // right = 2*i + 2

        // If the left child is larger than the root
        if (left < n && arr[left] > arr[largest])
            largest = left;

        // If the right child is larger than the largest so far
        if (right < n && arr[right] > arr[largest])
            largest = right;

        // If the largest is not root, swap and continue heapifying
        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            // Recursively heapify the affected subtree
            heapify(arr, n, largest);
        }
    }

    // Utility to print array
    static void printArray(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; ++i) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    // Main method to test the program
    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7};
        HeapSort sorter = new HeapSort();
        sorter.heapSort(arr);

        System.out.println("Sorted array is:");
        printArray(arr);
    }
}

