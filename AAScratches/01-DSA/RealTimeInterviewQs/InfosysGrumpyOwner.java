
class InfosysGrumpyOwner {
    /**
     * There is a bookstore owner that has a store open for n minutes. You are given an
     * <p>
     * integer array customers of length n where customers[i] is the number of the
     * <p>
     * customers that enter the store at the start of the ith minute and all those customers
     * <p>
     * leave after the end of that minute.
     * <p>
     * During certain minutes, the bookstore owner is grumpy. You are given a binary array grumpy
     * <p>
     * where grumpy[i] is 1 if the bookstore owner is grumpy during the ith minute, and is 0 otherwise.
     * <p>
     * When the bookstore owner is grumpy, the customers entering during that minute are not satisfied.
     * <p>
     * Otherwise, they are satisfied.
     * <p>
     * The bookstore owner knows a secret technique to remain not grumpy for minutes consecutive
     * <p>
     * minutes, but this technique can only be used once.
     * <p>
     * Return the maximum number of customers that can be satisfied throughout the day.
     * <p>
     * <p>
     * Example 1:
     * <p>
     * Input: customers = [1,0,1,2,1,1,7,5], grumpy = [0,1,0,1,0,1,0,1], minutes = 3
     * <p>
     * {3,2,5,10,20,5} , {1,1,1,0,0,1} , k=3
     * Output: 16
     * 10
     * 45
     * =35
     * Explanation:
     * <p>
     * The bookstore owner keeps themselves not grumpy for the last 3 minutes.
     * <p>
     * The maximum number of customers that can be satisfied = 1 + 1 + 1 + 1 + 7 + 5 = 16.
     * <p>
     * Example 2:
     * <p>
     * Input: customers = [1], grumpy = [0], minutes = 1
     * <p>
     * Output: 1
     * <p>
     * <p>
     * Constraints:
     * <p>
     * n == customers.length == grumpy.length
     * 1 <= minutes <= n <= 2 * 104
     * 0 <= customers[i] <= 1000
     * grumpy[i] is either 0 or 1.
     */
    public static void main(String[] args) {
        int[] customers = {3, 2, 5, 10, 20, 5};
        int[] grumpyTimings = {1, 1, 1, 0, 0, 1};

//        int[] customers = {1, 0, 1, 2, 1, 1, 7, 5};
//        int[] grumpyTimings = {0, 1, 0, 1, 0, 1, 0, 1};
        int k = 3;
        int n = customers.length;
        int maxSum = 0;
        int left = 0, right = k - 1;

        int windows = 0;

        while (windows <= n - k) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                if (grumpyTimings[j] == 0 || (j >= left && j <= right)) {
                    sum += customers[j];
                }
            }
            maxSum = Math.max(maxSum, sum);
            left++;
            right++;
            windows++;
        }
        System.out.println(maxSum);
    }
}
