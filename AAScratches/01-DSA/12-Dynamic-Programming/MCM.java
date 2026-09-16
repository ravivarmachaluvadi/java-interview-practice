import java.util.*;

class MCM {
    // 38000
    private int func(int[] arr, int i, int j) {
        if (i == j) return 0;

        int mini = Integer.MAX_VALUE;

        for (int k = i; k < j; k++) {
            int currMultiplication = arr[i - 1] * arr[k] * arr[j];
            int ans = func(arr, i, k) + currMultiplication + func(arr, k + 1, j);
            mini = Math.min(mini, ans);
        }
        return mini;
    }

    public int matrixMultiplication(int[] nums) {
        int N = nums.length;

        // Starting index of the matrix chain
        int i = 1;

        // Ending index of the matrix chain
        int j = N - 1;

        // Call the recursive function
        return func(nums, i, j);
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 30, 40, 50};

        // Create an instance of Solution class
        MCM sol = new MCM();

        // Print the result
        System.out.println("The minimum number of operations is " + sol.matrixMultiplication(arr));
    }
}
