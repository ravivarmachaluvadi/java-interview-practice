import java.util.Arrays;

class ArrayCloneExample {
    public static void main(String[] args) {
        int[][] arr = {{8, 9}, {7, 8, 6}};

        for (int[] ints : arr)
            System.out.println(Arrays.toString(ints));

        int[][] cloned = arr.clone();

        for (int[] ints : cloned)
            System.out.println(Arrays.toString(ints));
    }
}