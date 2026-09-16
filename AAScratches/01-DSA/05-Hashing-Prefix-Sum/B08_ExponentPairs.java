/**
 * Problem: Given an array of integers, identify all unique pairs (a, b) such that a^b equals b^a.
 *
 * Approach: Iterate over all unordered index pairs using two nested loops. For each pair compute Math.pow(a,b)
 * and Math.pow(b,a); if they match, store the sorted pair as a string in a HashSet to avoid duplicates.
 * Finally print all found pairs or indicate none were found.
 *
 * Time Complexity: O(n²) – every pair of elements is examined once.
 * Space Complexity: O(k) where k is the number of unique valid pairs stored in the HashSet (worst‑case O(n²)).
 */
import java.util.HashSet;

class ExponentPairs {
    public static void findPairs(int[] arr) {
        HashSet<String> resultPairs = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            // remember j=i+1 , when we don't want to include elements
            // that are on the right side of i not on the left side of i
            for (int j = i + 1; j < arr.length; j++) {
                int a = arr[i];
                int b = arr[j];

                // Check if a^b == b^a
                if (Math.pow(a, b) == Math.pow(b, a)) {
                    // Sort the pair for unique representation
                    if (a < b) {
                        resultPairs.add(a + "," + b);
                    } else {
                        resultPairs.add(b + "," + a);
                    }
                }
            }
        }

        if (resultPairs.isEmpty()) {
            System.out.println("No pairs found where a^b = b^a.");
        } else {
            System.out.println("Pairs where a^b = b^a:");
            for (String pair : resultPairs) {
                System.out.println("(" + pair.replace(",", ", ") + ")");
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {2, 4, 16, 256, 3};
        findPairs(arr);
    }
}
