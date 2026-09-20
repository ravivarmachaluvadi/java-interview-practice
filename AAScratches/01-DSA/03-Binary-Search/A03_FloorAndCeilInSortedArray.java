/*
 * =====================================================================
 *  Floor and Ceil in a Sorted Array          (GfG classic | Easy)
 * =====================================================================
 *
 * PROBLEM
 *   Given a sorted int array and a value x, return [floor, ceil] where
 *   floor = the largest element <= x and ceil = the smallest element >= x.
 *   Return -1 for either one that does not exist. If x is present, floor == ceil == x.
 *
 * EXAMPLE
 *   nums = [1, 2, 8, 10, 10, 12, 19], x = 5   ->  [2, 8]    x sits between 2 and 8
 *   nums = [1, 2, 8, 10, 10, 12, 19], x = 10  ->  [10, 10]  exact hit
 *   nums = [1, 2, 8, 10, 10, 12, 19], x = 0   ->  [-1, 1]   below everything: no floor
 *   nums = [1, 2, 8, 10, 10, 12, 19], x = 25  ->  [19, -1]  above everything: no ceil
 *   nums = [],                        x = 5   ->  [-1, -1]
 *
 * APPROACH  (predecessor and successor by binary search)
 *   1. findFloor: if nums[mid] <= x, it is a candidate floor; record its VALUE and search
 *      RIGHT for a larger one. Else search left.
 *   2. findCeil: if nums[mid] >= x, it is a candidate ceil; record its value and search
 *      LEFT for a smaller one. Else search right.
 *   3. Each search starts with ans = -1 so "no candidate found" falls out naturally.
 *
 * KEY INSIGHT
 *   Same lower/upper bound template as A01_LowerAndUpperBounds, but the answer stored is the
 *   element's value rather than its index. Ceil is lowerBound(x) read as a value; floor is the
 *   element just before upperBound(x). Recognise this whenever a problem asks for "the closest
 *   element not exceeding / not below" something in sorted data.
 *
 * COMPLEXITY
 *   Time  O(log n)  two binary searches
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return indices instead of values (then -1 still works as "absent").
 *   - Floor and ceil in a BST: same idea, walk down the tree recording the candidate.
 *   - Closest element to x: compare |floor - x| and |ceil - x|.
 *   - Distinguish "-1 means absent" from an actual value -1: use Integer/null or an index.
 *
 * RUN
 *   main() runs 5 cases (between, exact, below all, above all, empty) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class FloorAndCeilInSortedArray {

    /** Largest value <= x, or -1 if every element is greater than x. */
    private int findFloor(int[] nums, int x) {
        int low = 0, high = nums.length - 1, ans = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] <= x) {
                ans = nums[mid]; // candidate; a larger one may sit to the right
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return ans;
    }

    /** Smallest value >= x, or -1 if every element is smaller than x. */
    private int findCeil(int[] nums, int x) {
        int low = 0, high = nums.length - 1, ans = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] >= x) {
                ans = nums[mid]; // candidate; a smaller one may sit to the left
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans;
    }

    public int[] getFloorAndCeil(int[] nums, int x) {
        return new int[]{findFloor(nums, x), findCeil(nums, x)};
    }

    private static void print(String label, int[] nums, int x, String expected) {
        int[] actual = new FloorAndCeilInSortedArray().getFloorAndCeil(nums, x);
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 8, 10, 10, 12, 19};
        print("case 1 between   ", nums, 5, "[2, 8]");
        print("case 2 exact hit ", nums, 10, "[10, 10]");
        print("case 3 below all ", nums, 0, "[-1, 1]");
        print("case 4 above all ", nums, 25, "[19, -1]");
        print("case 5 empty     ", new int[]{}, 5, "[-1, -1]");
    }
}
