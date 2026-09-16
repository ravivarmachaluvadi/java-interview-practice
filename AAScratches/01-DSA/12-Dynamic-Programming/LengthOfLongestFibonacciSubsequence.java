import java.util.*;

// https://leetcode.com/problems/length-of-longest-fibonacci-subsequence/description/
// 873. Length of Longest Fibonacci Subsequence
class LengthOfLongestFibonacciSubsequence {
    // Approach-1: Greedy Simulation
    public int lenLongestFibSubseqGreedy(int[] arr) {
        int n = arr.length;
        Set<Integer> values = new HashSet<>();
        for (int num : arr) values.add(num);

        int longest = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int a = arr[i];
                int b = arr[j];
                int fibLen = 2;
                while (values.contains(a + b)) {
                    int sum = a + b;
                    a = b;
                    b = sum;
                    fibLen++;
                }
                if (fibLen > 2) longest = Math.max(longest, fibLen);
            }
        }
        return longest;
    }

    // Approach-2: Binary Search
    private int fibChainLength(int[] arr, int a, int b, int n) {
        int fibIdx = Arrays.binarySearch(arr, a + b);
        if (fibIdx >= 0 && fibIdx < n) {
            return 1 + fibChainLength(arr, b, a + b, n);
        }
        return 0;
    }

    public int lenLongestFibSubseqBinarySearch(int[] arr) {
        int n = arr.length;
        int longest = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int a = arr[i];
                int b = arr[j];
                int fibLen = fibChainLength(arr, a, b, n);
                if (fibLen > 0) longest = Math.max(longest, 2 + fibLen);
            }
        }
        return longest;
    }

    // Approach-3: Dynamic Programming
    public int lenLongestFibSubseqDP(int[] arr) {
        int n = arr.length;
        int[][] dp = new int[n][n];
        int longest = 0;

        for (int sum = 2; sum < n; sum++) {
            int a = 0;
            int b = sum - 1;
            while (a < b) {
                if (arr[a] + arr[b] < arr[sum]) {
                    a++;
                } else if (arr[a] + arr[b] > arr[sum]) {
                    b--;
                } else {
                    dp[b][sum] = 1 + dp[a][b];
                    longest = Math.max(longest, dp[b][sum]);
                    a++;
                    b--;
                }
            }
        }
        return longest == 0 ? 0 : 2 + longest;
    }
}