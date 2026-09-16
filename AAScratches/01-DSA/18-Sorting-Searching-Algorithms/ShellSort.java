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

