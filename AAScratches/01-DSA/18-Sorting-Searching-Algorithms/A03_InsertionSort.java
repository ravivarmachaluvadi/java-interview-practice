import java.util.Arrays;
/**
 * Best Case (Already Sorted):
 * <p>
 * Only 1 comparison per element (no shifting needed).
 * <p>
 * Complexity: O(n)
 */
class InsertionSort {

    public static void main(String[] args) {
        int[] arr = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        insertionSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    private static void insertionSort(int[] arr) {
        int n = arr.length;
        // remeber < n-1
        for (int i = 0; i < n - 1; i++) {
            // 0 to i is sorted and trying expand by one element
            // inserting into the sorted array comapring and swap
            // we are trying to insert new element j = i + 1 
            //into already sorted array of 0 to i
            // remember j>0
            for (int j = i + 1; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    int smallTemp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = smallTemp;
                } else break;
            }
        }
    }

}
