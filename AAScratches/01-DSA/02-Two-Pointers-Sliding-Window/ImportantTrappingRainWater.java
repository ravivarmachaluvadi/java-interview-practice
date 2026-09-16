// Array, Two Pointers, Dynamic Programming, Stack, Monotonic Stack
// https://leetcode.com/problems/trapping-rain-water

// https://leetcode.com/problems/trapping-rain-water/description/
// 42. Trapping Rain Water
class ImportantTrappingRainWater {
    public static void main(String[] args) {

        int[] heights = {1, 5, 3, 2, 7, 8, 9, 0, 4, 2, 3};
        int length = heights.length;
        int left = 0, right = length - 1;
        int maxLeft = 0, maxRight = 0;
        int res = 0;

        while (left <= right) {
            if (heights[left] <= heights[right]) {
                if (heights[left] >= maxLeft) {
                    maxLeft = heights[left];
                } else {
                    res = res + maxLeft - heights[left];
                }
                left++;
            } else {
                if (heights[right] >= maxRight) {
                    maxRight = heights[right];
                } else res = res + maxRight - heights[right];

                right--;
            }
        }
        System.out.println(res);
    }
}
