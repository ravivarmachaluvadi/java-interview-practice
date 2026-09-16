/**
 * Determines whether a given integer array is sorted in non‑decreasing order.
 *
 * The method iterates through the array once, comparing each element with its
 * successor; if any element is greater than the next one, the array is not
 * sorted and false is returned immediately. If no such pair exists, true is
 * returned.
 *
 * Time Complexity: O(n) – a single pass over n elements.
 * Space Complexity: O(1) – only constant auxiliary space used.
 */
class ArraySortedOrNot {
    static boolean arraySortedOrNot(int[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6};
        IO.println(arraySortedOrNot(arr, arr.length));
    }
}