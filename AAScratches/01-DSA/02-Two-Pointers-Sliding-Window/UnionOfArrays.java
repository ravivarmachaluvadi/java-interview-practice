/**
 * Problem: Compute the union of two sorted integer arrays without duplicates.
 *
 * Approach: Use two pointers to traverse both arrays simultaneously, adding
 * elements to a result list only when they are not already present as the last
 * added element. After one array is exhausted, append remaining unique elements
 * from the other array.
 *
 * Time Complexity: O(n + m), where n and m are the lengths of the input arrays.
 * Space Complexity: O(k), where k is the size of the union (at most n + m).
 */
import java.util.*;

class UnionOfArrays {
    static ArrayList<Integer> findUnion(int[] arr1, int[] arr2, int n, int m) {
        int i = 0, j = 0; // pointers
        ArrayList<Integer> list = new ArrayList<>(); // Union vector
        while (i < n && j < m) {
            if (arr1[i] <= arr2[j]) // Case 1 and 2
            {
                if (list.isEmpty() || list.get(list.size() - 1) != arr1[i])
                    list.add(arr1[i]);
                i++;
            } else // case 3
            {
                if (list.isEmpty() || list.get(list.size() - 1) != arr2[j])
                    list.add(arr2[j]);
                j++;
            }
        }
        while (i < n) // IF any element left in arr1
        {
            if (list.get(list.size() - 1) != arr1[i])
                list.add(arr1[i]);
            i++;
        }
        while (j < m) // If any elements left in arr2
        {
            if (list.get(list.size() - 1) != arr2[j])
                list.add(arr2[j]);
            j++;
        }
        return list;
    }

    public static void main(String[] args) {

        int n = 10, m = 7;
        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] arr2 = {2, 3, 4, 4, 5, 11, 12};
        ArrayList<Integer> Union = findUnion(arr1, arr2, n, m);
        System.out.println("Union of arr1 and arr2 is ");
        for (int val : Union)
            System.out.print(val + " ");
    }
}
