/**
 * Problem: Given a sorted integer array, remove duplicate values in-place so that each element appears only once.
 *
 * Approach: Use two pointers. The left pointer marks the position of the last unique element found.
 * Iterate with the right pointer; when a new value is encountered, increment left and copy the
 * right element to arr[left]. After traversal, return left + 1 as the length of the deduplicated prefix.
 *
 * Time Complexity: O(n), where n is the array length (single pass).
 * Space Complexity: O(1) – only a few integer variables are used regardless of input size.
 */
class RemoveDuplicates {

    public static void main(String[] args) {
        int[] arr = {1, 1, 2, 2, 2, 3, 3};
        int k = removeDuplicates(arr);
        System.out.println("The array after removing duplicate elements is ");
        for (int i = 0; i < k; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    // two pointer solution
    static int removeDuplicates(int[] arr) {
        int left = 0;
        for (int right = 1; right < arr.length; right++) {
            if (arr[left] != arr[right]) {
                left++;
                arr[left] = arr[right];
            }
        }
        return left + 1;
    }
}
