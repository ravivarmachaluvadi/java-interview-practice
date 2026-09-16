public class AImportantLowerAndUpperBounds {
    public static int findLowerBound(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        int ans = arr.length;

        while (low <= high) {
            int mid = (low + high) / 2;
            // maybe an answer
            if (arr[mid] >= target) {
                ans = mid;
                //look for smaller index on the left
                high = mid - 1;
            } else {
                low = mid + 1; // look on the right
            }
        }
        return ans;
    }

    public static int findUpperBound(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        int ans = arr.length;

        while (low <= high) {
            int mid = (low + high) / 2;
            // maybe an answer
            if (arr[mid] > target) {
                ans = mid;
                //look for smaller index on the left
                high = mid - 1;
            } else {
                low = mid + 1; // look on the right
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 2, 2, 3, 4, 5};
        int target = 2;

        int lowerBound = findLowerBound(nums, target);
        int upperBound = findUpperBound(nums, target);

        if (lowerBound != -1 && upperBound != -1) {
            System.out.println("Lower Bound: " + lowerBound); // Lower Bound: 1
            System.out.println("Upper Bound: " + upperBound); // Upper Bound: 4
        } else {
            System.out.println("Target not found in the array.");
        }
    }
}
