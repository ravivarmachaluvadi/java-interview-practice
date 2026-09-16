import java.util.Arrays;

class Tricky10 {
    public static void main(String[] args) {
        int[] arr = new int[2];
        int i = 0, j = 0;
        arr[0] = ++i;
        arr[1] = j++;
        System.out.println(Arrays.toString(arr)); // [1, 0]
    }
}
