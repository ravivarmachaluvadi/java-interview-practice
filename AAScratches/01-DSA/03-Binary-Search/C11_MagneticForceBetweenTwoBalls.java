import java.util.Arrays;

// https://leetcode.com/problems/magnetic-force-between-two-balls/description/
// 1552. Magnetic Force Between Two Balls

/**
 * Input: position = [1,2,3,4,7], m = 3
 * <p>
 * Output: 3
 * <p>
 * Explanation: Distributing the 3 balls into baskets 1, 4 and 7 will make the magnetic force between
 * <p>
 * ball pairs [3, 3, 6]. The minimum magnetic force is 3. We cannot achieve a larger minimum magnetic force than 3.
 * <p>
 * Example 2:
 * <p>
 * Input: position = [5,4,3,2,1,1000000000], m = 2
 * <p>
 * Output: 999999999
 * <p>
 * Explanation: We can use baskets 1 and 1000000000.
 */
class MagneticForceBetweenTwoBalls {

    public static int maxDistance(int[] position, int m) {
        Arrays.sort(position);
        int n = position.length;
        int left = 1;
        int right = position[n - 1] - position[0];
        int best = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canPlaceBalls(position, m, mid)) {
                // mid is feasible, try for larger
                best = mid;
                left = mid + 1;
            } else {
                // mid not feasible, reduce distance
                right = mid - 1;
            }
        }
        return best;
    }

    private static boolean canPlaceBalls(int[] position, int m, int minDist) {
        // place first ball at position[0]
        int count = 1;
        int lastPos = position[0];

        for (int i = 1; i < position.length; i++) {
            if (position[i] - lastPos >= minDist) {
                count++;
                lastPos = position[i];
                if (count >= m) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // Example 1
        int[] pos1 = {1, 2, 3, 4, 7};
        int m1 = 3;
        System.out.println("Example 1 result: " + maxDistance(pos1, m1));
        // Expected output: 3

        // Example 2
        int[] pos2 = {5, 4, 3, 2, 1, 1000000000};
        int m2 = 2;
        System.out.println("Example 2 result: " + maxDistance(pos2, m2));
        // Expected output: 999999999
    }
}
