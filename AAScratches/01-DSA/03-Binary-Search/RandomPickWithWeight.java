import java.util.Random;

// https://leetcode.com/problems/random-pick-with-weight/description/
// 528. Random Pick with Weight

/**
 * You need to implement the function pickIndex(), which randomly
 * <p>
 * picks an index in the range [0, w.length - 1] (inclusive) and
 * <p>
 * returns it. The probability of picking an index i is w[i] / sum(w).
 */
class RandomPickWithWeight {
    private int[] prefixSum;
    private int totalSum;
    private Random random;

    public RandomPickWithWeight(int[] w) {
        prefixSum = new int[w.length];
        totalSum = 0;
        random = new Random();
        for (int i = 0; i < w.length; i++) {
            totalSum += w[i];
            prefixSum[i] = totalSum;
        }
    }

    public int pickIndex() {
        // Random value in range [1, totalSum]
        int target = random.nextInt(totalSum) + 1;

        // Binary search to find the index
        int left = 0, right = prefixSum.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefixSum[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        int[] weights = {1, 3, 4, 6}; // Example
        RandomPickWithWeight solution = new RandomPickWithWeight(weights);

        // Test with multiple calls to check randomness
        for (int i = 0; i < 10; i++) {
            System.out.println(solution.pickIndex());
        }
    }
}
