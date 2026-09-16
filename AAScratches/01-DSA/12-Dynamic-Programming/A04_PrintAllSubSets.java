/**
 * Problem: Print all subsets (the power set) of a given integer array.
 *
 * Approach: Recursively explore each element with two choices:
 *   - Include the current element in the subset and recurse to the next index.
 *   - Exclude the current element, backtrack by removing it from the list,
 *     then recurse to the next index. When the end of the array is reached,
 *     print the accumulated subset.
 *
 * Time Complexity: O(2^n) – each element has two choices, generating 2^n subsets.
 * Space Complexity: O(n) – recursion stack depth and the current subset list
 *   store at most n elements. The output itself requires O(2^n) space if stored,
 *   but printing uses constant additional space beyond the recursion overhead.
 */
import java.util.ArrayList;
import java.util.List;

class PrintAllSubSets {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3};
        ArrayList<Integer> list = new ArrayList<>();
        printAllSubSets(0, arr, list);
    }

    private static void printAllSubSets(int index,
                                        int[] arr,
                                        List<Integer> ansList) {
        if (index == arr.length) {
            System.out.println(ansList);
            return;
        }
        // pick Element and go for next element to pick
        ansList.add(arr[index]);
        printAllSubSets(index + 1, arr, ansList);// Include
        // not-pick prev Element and go for next element to pick
        ansList.remove(ansList.size() - 1);
        printAllSubSets(index + 1, arr, ansList);// Exclude
    }
}