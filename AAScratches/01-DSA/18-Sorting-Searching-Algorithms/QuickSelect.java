// select kth smallest or largest in un-sorted array
class QuickSelect {

    public static void main(String[] args) {
        int[] arr = {7, 1, 4, 2, 8, 5, 0, 9, 3};
        int quickSelect = quickSelect(0, arr.length - 1, arr.length - 1, arr);
        System.out.println(quickSelect);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
        }
    }

    private static int quickSelect(int low, int high, int k, int[] arr) {
        int partitionIndex = partition(low, high, arr);
        // if else if ladder
        if (partitionIndex == k) return arr[k];
        else if (partitionIndex < k) {
            return quickSelect(partitionIndex + 1, high, k, arr);
        }
        return quickSelect(low, partitionIndex - 1, k, arr);
    }

    private static int partition(int low, int high, int[] arr) {
        int pivotValue = arr[high];
        int left = low;
// scannig arr from low to high-1 except pivot
        for (int right = left; right < high; right++) {
            if (arr[right] < pivotValue) {
// swap currentVal which is smaller than pivotVal with value at left(partitionIndex)
                int lesserValThanPivot = arr[right];
                arr[right] = arr[left];
                arr[left] = lesserValThanPivot;
                left++;
            }
        }
        int temp = arr[left];
        arr[left] = pivotValue;
        arr[high] = temp;
        return left;
    }
}
