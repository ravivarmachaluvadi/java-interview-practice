import java.util.ArrayList;
import java.util.List;

// 2 pointers solution and no duplicates in arrays
class IntersectionOfTwoArrays {
    public static List<Integer> findArrayIntersection(int[] A, int n, int[] B, int m) {
        int i = 0, j = 0;
        List<Integer> ans = new ArrayList<>();

        while (i < n && j < m) {
            if (A[i] < B[j]) {
                i++;
            } else if (B[j] < A[i]) {
                j++;
            } else {
                ans.add(A[i]);
                i++;
                j++;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] arr1 = {1, 2, 3, 4, 5};
        int[] arr2 = {2, 3, 5, 6};

        List<Integer> intersection = findArrayIntersection(arr1, arr1.length, arr2, arr2.length);

        System.out.println("Intersection of arr1 and arr2 is: " + intersection);
        // [2, 3, 5]
    }
}
