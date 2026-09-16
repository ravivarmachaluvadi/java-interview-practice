/**
 * Problem: Compute the sum of the product of every unordered pair of elements in an integer array.
 *
 * Approach: Use two nested loops to iterate over all pairs (i, j) with i < j and accumulate A[i] * A[j].
 *
 * Time Complexity: O(n²), where n is the length of the input array, due to the double loop.
 * Space Complexity: O(1), only a constant amount of extra space is used for variables.
 */
class SumOfProductOfPairs {
    public static void main(String[] args) {
        int[] A = {1, 3, 4};
        int result = sumOfProductPairs(A);
        System.out.println("Sum of product of all pairs: " + result);
    }

    public static int sumOfProductPairs(int[] A) {
        int sum = 0;
        int n = A.length;
        // Iterate through each pair of elements
        for (int i = 0; i < n; i++) {
            // j=i+1 -> so we excluding duplicate of products
            for (int j = i + 1; j < n; j++)
                sum += A[i] * A[j];
        }
        return sum;
    }
}
