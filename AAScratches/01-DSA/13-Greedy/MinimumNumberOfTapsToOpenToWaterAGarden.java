import java.util.Arrays;

// https://leetcode.com/problems/minimum-number-of-taps-to-open-to-water-a-garden/description/
// 1326. Minimum Number of Taps to Open to Water a Garden
class MinimumNumberOfTapsToOpenToWaterAGarden {
    /**
     * Returns the minimum number of taps that should be opened to water the garden from 0 to n.
     * If impossible, returns -1.
     */
    public int minTaps(int n, int[] ranges) {
        // For each starting point l (0 ≤ l ≤ n), we
        // want to compute the farthest right reach
        // of any tap whose left reach is exactly l.
        int[] maxReachFrom = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            int r = ranges[i];
            int left = Math.max(0, i - r);
            int right = Math.min(n, i + r);
            // From starting at position `left`, we can reach `right`
            maxReachFrom[left] = Math.max(maxReachFrom[left], right);
        }

        int tapsOpened = 0;
        // the end of the current coverage
        int currentEnd = 0;
        // the farthest we can reach so far
        int farthest = 0;

        // We only need to iterate positions from 0 to n-1 (if we can cover up to n)
        for (int i = 0; i < n; i++) {
            // Update the farthest reachable point from among all taps that start at or before i
            farthest = Math.max(farthest, maxReachFrom[i]);
            // If we cannot extend coverage beyond i, then impossible
            if (farthest <= i) {
                return -1;
            }
            // If we've reached the end of current coverage segment, we need to open a new tap
            if (i == currentEnd) {
                tapsOpened++;
                currentEnd = farthest;
            }
        }
        return tapsOpened;
    }

    public static void main(String[] args) {
        MinimumNumberOfTapsToOpenToWaterAGarden sol = new MinimumNumberOfTapsToOpenToWaterAGarden();

        // Example 1:
        int n1 = 5;
        int[] ranges1 = {3, 4, 1, 1, 0, 0};
        System.out.println("Example1: n = " + n1 + ", ranges = " + Arrays.toString(ranges1));
        int result1 = sol.minTaps(n1, ranges1);
        System.out.println("Minimum taps = " + result1);
        // Expected: 1 (because tap at position 1 covers [0,5])

        // Example 2:
        int n2 = 3;
        int[] ranges2 = {0, 0, 0, 0};
        System.out.println("Example2: n = " + n2 + ", ranges = " + Arrays.toString(ranges2));
        int result2 = sol.minTaps(n2, ranges2);
        System.out.println("Minimum taps = " + result2);
        // Expected: -1 (cannot cover all [0,3])

        // You can add more test cases as needed.
    }
}
