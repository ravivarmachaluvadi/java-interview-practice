/**
 * Computes the nth Fibonacci number using a simple recursive algorithm.
 *
 * The method returns the value of the Fibonacci sequence at position n,
 * where fib(0)=0 and fib(1)=1, by summing the two preceding values
 * until reaching the base cases.
 *
 * Approach:
 *  - Base case: if n is 0 or 1, return n directly.
 *  - Recursive step: fib(n) = fib(n-1) + fib(n-2).
 *
 * Time Complexity: O(2^n) – exponential due to repeated subproblem evaluations.
 * Space Complexity: O(n) – recursion stack depth in the worst case.
 */
class FibonacciNumber {

    public static void main(String[] args) {
        FibonacciNumber solution = new FibonacciNumber();
        int n = 5; // Example input
        System.out.println("Fibonacci number at position " + n + " is " + solution.fib(n));
    }

    private int fib(int n) {
        if (n <= 1) {
            return n; // 5
        }

        return fib(n - 1) + fib(n - 2);
    }
}
