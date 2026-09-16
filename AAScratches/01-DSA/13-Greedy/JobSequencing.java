import java.util.Arrays;

class JobSequencing {
    public static void main(String[] args) {
        int[][] arr = {{1, 4, 50}, {2, 1, 10}, {3, 1, 40}, {4, 1, 30}};

        // Sort the array in descending order based on
        // the third element and then the second element
        Arrays.sort(arr, (a, b) -> {
            if (a[2] != b[2]) {
                // Sort by the third element in descending order
                return Integer.compare(b[2], a[2]);
            } else {
                // If the third elements are equal, sort
                // by the second element in ascending order
                return Integer.compare(a[1], b[1]);
            }
        });
    }
}
