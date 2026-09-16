// https://leetcode.com/problems/cutting-ribbons/

/**
 * Your task is to determine the maximum length of ribbon, x,
 * <p>
 * that allows you to cut at least k ribbons, each of length x.
 */
class CuttingRibbons {
    // Returns true if at least k pieces of length len can be produced.
    private static boolean canCut(int[] ribbons, int k, int len) {
        if (len <= 0) return false;
        long count = 0;
        for (int r : ribbons) {
            count += (r / len);
            if (count >= k) {
                return true;
            }
        }
        return false;
    }

    // Returns the maximum possible length of at least k equal pieces.
    public static int maxLength(int[] ribbons, int k) {
        int lo = 1;  // minimal length possible (if any)
        int hi = 0;
        for (int r : ribbons) {
            hi = Math.max(hi, r);
        }
        int ans = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (canCut(ribbons, k, mid)) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return ans;
    }


    public static void main(String[] args) {
        // Example 1
        int[] ribbons1 = {9, 7, 5};
        int k1 = 3;
        // You can cut into three pieces of length 5: from 9 -> 5 + 4, from 7 -> 5 + 2, from 5 -> 5
        System.out.println("Expected 5, got = " + maxLength(ribbons1, k1));

        // Example 2
        int[] ribbons2 = {7, 5, 9};
        int k2 = 4;
        // You can make 4 pieces of length 4: (7 -> 4+3), (5 -> 4+1), (9 -> 4+4+1) => 4 pieces of length 4
        System.out.println("Expected 4, got = " + maxLength(ribbons2, k2));

        // Example 3 (not possible)
        int[] ribbons3 = {5, 7, 10};
        int k3 = 5;
        // Even if you cut optimally, you can't produce 5 pieces of any positive integer length
        System.out.println("Expected 3, got = " + maxLength(ribbons3, k3));
    }
}
