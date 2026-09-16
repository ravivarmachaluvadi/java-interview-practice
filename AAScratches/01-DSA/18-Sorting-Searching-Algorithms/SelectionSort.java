import java.util.Arrays;
/**
 * select min or max place it in at begining or at end of array
 * update index accordingly
 */
class SelectionSort {

    public static void main(String[] args) {
        int[] arr = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            // select maxVal and place at last index
            int maxIndex = getMaxVal(arr, length - 1 - i);
            swapVals(arr, maxIndex, length - 1 - i);
        }
        System.out.println(Arrays.toString(arr));
    }

    private static void swapVals(int[] arr, int maxIndex, int lastIndex) {
        int temp = arr[maxIndex];
        arr[maxIndex] = arr[lastIndex];
        arr[lastIndex] = temp;
    }

    private static int getMaxVal(int[] arr, int lastIndex) {
        int maxIndex = 0;
        int maxVal = arr[maxIndex];
        for (int j = 1; j <= lastIndex; j++) {
            if (arr[j] > maxVal) {
                maxIndex = j;
            }
        }
        return maxIndex;
    }
}
