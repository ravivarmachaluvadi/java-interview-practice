/**
 * Computes the Nth Fibonacci number using a naive recursive algorithm.
 *
 * The method returns the value of the Fibonacci sequence at position N,
 * where fibonacci(0) = 0 and fibonacci(1) = 1, by summing the two
 * preceding values recursively until reaching the base cases.
 *
 * Approach:
 *   - Base case: if N <= 1 return N.
 *   - Recursive step: compute fibonacci(N-1) and fibonacci(N-2),
 *     then sum them to obtain fibonacci(N).
 *
 * Time Complexity: O(2^N) – exponential due to repeated subproblem
 *                    evaluations (no memoization).
 * Space Complexity: O(N) – recursion depth proportional to N.
 */
class Fibonacci {
    static int fibonacci(int N) {
        if (N <= 1)
            return N;

        int last = fibonacci(N - 1);
        int slast = fibonacci(N - 2);

        return last + slast;
    }

    public static void main(String[] args) {

        // Here, let’s take the value of N to be 4.
        int N = 4;
        System.out.println(fibonacci(N)); // 3
    }
}
