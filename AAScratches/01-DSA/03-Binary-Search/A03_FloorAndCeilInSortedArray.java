/**
 * Problem:
 *   Given a sorted array of integers and a target value x, find the floor (greatest element ≤ x)
 *   and ceil (smallest element ≥ x) in the array.
 *
 * Approach:
 *   Use binary search twice: one to locate the largest element not exceeding x,
 *   another to locate the smallest element not less than x. Each search updates an
 *   answer variable when a candidate is found and narrows the search bounds accordingly.
 *
 * Complexity:
 *   Time:  O(log n) for each of floor and ceil, total O(log n).
 *   Space: O(1) auxiliary space (in-place operations only).
 */
class FloorAndCeilInSortedArray {
    private int findFloor(int[] nums, int n, int x) {
        int low = 0, high = n - 1, ans = -1;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (nums[mid] <= x) {
                ans = nums[mid];
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return ans;
    }

    private int findCeil(int[] nums, int n, int x) {
        int low = 0, high = n - 1;
        int ans = -1;
        while (low <= high) {
            int mid = (low + high) / 2;

            if (nums[mid] >= x) {
                ans = nums[mid];
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans;
    }

    public int[] getFloorAndCeil(int[] nums, int x) {
        int n = nums.length;

        int floor = findFloor(nums, n, x);
        int ceil = findCeil(nums, n, x);
        return new int[]{floor, ceil};
    }

    public static void main(String[] args) {
        FloorAndCeilInSortedArray obj = new FloorAndCeilInSortedArray();
        int[] nums = {1, 2, 8, 10, 10, 12, 19};
        int x = 5;

        int[] result = obj.getFloorAndCeil(nums, x);
        System.out.println("Floor: " + result[0]); // 2
        System.out.println("Ceil: " + result[1]); // 8
    }
}
