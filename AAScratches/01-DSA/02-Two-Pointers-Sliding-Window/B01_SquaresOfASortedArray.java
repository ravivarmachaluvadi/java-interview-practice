import java.util.Arrays;

// 977. Squares of a Sorted Array
// https://leetcode.com/problems/squares-of-a-sorted-array/description/
class SquaresOfASortedArray {
    public static void main(String[] args) {
        int[] arr = {-5, -1, 0, 1, 2, 10};
        int[] sortedSquares = sortedSquares(arr);
        System.out.println(Arrays.toString(sortedSquares));

    }

    // two pointer solution
    public static int[] sortedSquares(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        int left = 0;
        int right = n - 1;
        int index = n - 1;
        while (left <= right) {
            int leftSquare = arr[left] * arr[left];
            int rightSquare = arr[right] * arr[right];
            if (leftSquare > rightSquare) {
                result[index--] = leftSquare;
                left++;
            } else {
                result[index--] = rightSquare;
                right--;
            }
        }
        return result;
    }
}

