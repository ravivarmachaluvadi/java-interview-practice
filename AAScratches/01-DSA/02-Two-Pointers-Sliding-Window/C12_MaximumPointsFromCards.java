// https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/description/
// 1423. Maximum Points You Can Obtain from Cards
/**
 * There are several cards arranged in a row, and each card has an associated number of points.
 * <p>
 * The points are given in the integer array cardPoints.
 * <p>
 * In one step, you can take one card from the beginning or from the end of the row.
 * <p>
 * You have to take exactly k cards.
 * <p>
 * Your score is the sum of the points of the cards you have taken.
 * <p>
 * Given the integer array cardPoints and the integer k, return the maximum score you can obtain.
 */
// variation of maxSum of window size k in cyclic manner
class MaximumPointsFromCards {
    /**
     * Input: cardPoints = [1,2,3,4,5,6,1], k = 3
     * Output: 12
     */
    public static int maxScore(int[] cardPoints, int k) {
        int n = cardPoints.length;

        // Start with the back k cards (window starting at n - k)
        int sum = 0;
        for (int i = n - k; i < n; i++) {
            sum += cardPoints[i];
        }
        int maxSum = sum;

        // Slide k times: covers windows starting at n-k+1 ... n-1, 0
        for (int i = n; i < n + k; i++) {
            sum += cardPoints[i % n] - cardPoints[i - k];
            maxSum = Math.max(maxSum, sum);
        }
        return maxSum;
    }

    // Main method to test the solution
    public static void main(String[] args) {
        // Example 1
        int[] cards1 = {1, 2, 3, 4, 5, 6, 1};
        int k1 = 3;
        System.out.println("Example 1: " + maxScore(cards1, k1));  // Expected output: 12

        // Example 2
        int[] cards2 = {2, 2, 2};
        int k2 = 2;
        System.out.println("Example 2: " + maxScore(cards2, k2));  // Expected output: 4

        // Example 3
        int[] cards3 = {9, 7, 7, 9, 7, 7, 9};
        int k3 = 2;
        System.out.println("Example 3: " + maxScore(cards3, k3));  // Expected output: 30
    }
    //Example 1: 12
    //Example 2: 4
    //Example 3: 18
}
