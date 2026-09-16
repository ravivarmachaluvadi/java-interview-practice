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
