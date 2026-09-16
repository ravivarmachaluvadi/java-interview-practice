
// https://leetcode.com/problems/single-element-in-a-sorted-array/description/
// 540. Single Element in a Sorted Array
class SingleNonDuplicateElement {

    public static int singleNonDuplicate(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0];
        if (arr[0] != arr[1]) return arr[0];
        if (arr[n - 1] != arr[n - 2]) return arr[n - 1];

        int low = 0, high = n - 1;

        while (low <= high) {
            int mid = (low + high) >> 1;

            // Check if mid is the unique element
            if (arr[mid] != arr[mid - 1] && arr[mid] != arr[mid + 1]) {
                return arr[mid];
            }

            // Adjust search space
            boolean leftPair = (mid % 2 == 0 && arr[mid] == arr[mid - 1])
                    || (mid % 2 == 1 && arr[mid] == arr[mid + 1]);

            if (leftPair) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    // Main method to test
    public static void main(String[] args) {
        int[] arr = {1, 1, 2, 2, 3, 4, 4, 5, 5};
        System.out.println("Input Array:");
        printArray(arr);
        int result = singleNonDuplicate(arr);
        System.out.println("Single non-duplicate element is: " + result);
    }

    private static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}
