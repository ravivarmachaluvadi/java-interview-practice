/**
 * Problem: Compute the nth Fibonacci number, F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2).
 *
 * Approaches (same inputs, same answers, very different cost):
 *   1. naiveRecursive      - plain recursion, O(2^n) time, O(n) stack. The "why DP exists" example.
 *   2. memoized            - top-down DP: same recursion + cache, O(n) time, O(n) space.
 *   3. iterative           - bottom-up DP with two rolling variables, O(n) time, O(1) space.
 *   4. matrixExponentiation - {{1,1},{1,0}}^(n-1) via binary exponentiation, O(log n) time.
 *                             The interview "can you do better than O(n)?" answer.
 */
class Fibonacci {

    // ---------- 1. Naive recursion: O(2^n) ----------
    // fib(5) calls fib(3) twice, fib(2) three times ... the call tree doubles each level.
    static long naiveRecursive(int n) {
        if (n <= 1)
            return n;
        return naiveRecursive(n - 1) + naiveRecursive(n - 2);
    }

    // ---------- 2. Top-down DP (memoization): O(n) ----------
    // Same recursion, but each subproblem is solved once and cached.
    static long memoized(int n) {
        return memoized(n, new long[n + 1]);
    }

    private static long memoized(int n, long[] memo) {
        if (n <= 1)
            return n;
        if (memo[n] != 0)           // 0 only for unsolved cells (F(n) > 0 for n >= 1)
            return memo[n];
        return memo[n] = memoized(n - 1, memo) + memoized(n - 2, memo);
    }

    // ---------- 3. Bottom-up DP, O(1) space ----------
    // Only the last two values are ever needed, so no table at all.
    static long iterative(int n) {
        if (n <= 1)
            return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    // ---------- 4. Matrix exponentiation: O(log n) ----------
    // Identity:  [F(n+1) F(n)  ]   =  [1 1]^n
    //            [F(n)   F(n-1)]      [1 0]
    // So M^(n-1)[0][0] = F(n). Raise M with binary exponentiation (square-and-multiply).
    static long matrixExponentiation(long n) {
        if (n == 0) return 0;
        if (n == 1) return 1;

        long[][] baseMatrix = {{1, 1}, {1, 0}};
        long[][] resultMatrix = matrixPower(baseMatrix, n - 1);

        // The nth Fibonacci number is stored in resultMatrix[0][0]
        return resultMatrix[0][0];
    }

    // Binary exponentiation on a 2x2 matrix (same loop shape as fast integer pow)
    static long[][] matrixPower(long[][] base, long exp) {
        long[][] result = {{1, 0}, {0, 1}}; // Identity matrix

        while (exp > 0) {
            if ((exp & 1) == 1)               // If exp is odd, multiply base into result
                result = multiplyMatrices(result, base);
            base = multiplyMatrices(base, base); // Square the base
            exp >>= 1;                           // Equivalent to exp = exp / 2
        }
        return result;
    }

    // Multiply two 2x2 matrices (row of A dot column of B)
    static long[][] multiplyMatrices(long[][] A, long[][] B) {
        long[][] result = new long[2][2];
        result[0][0] = A[0][0] * B[0][0] + A[0][1] * B[1][0]; // row 0 . col 0
        result[0][1] = A[0][0] * B[0][1] + A[0][1] * B[1][1]; // row 0 . col 1
        result[1][0] = A[1][0] * B[0][0] + A[1][1] * B[1][0]; // row 1 . col 0
        result[1][1] = A[1][0] * B[0][1] + A[1][1] * B[1][1]; // row 1 . col 1
        return result;
    }

    public static void main(String[] args) {
        int[] inputs = {0, 1, 4, 5, 10, 30};   // F = 0, 1, 3, 5, 55, 832040

        for (int n : inputs) {
            System.out.println("n = " + n);
            System.out.println("  naiveRecursive       = " + naiveRecursive(n));
            System.out.println("  memoized             = " + memoized(n));
            System.out.println("  iterative            = " + iterative(n));
            System.out.println("  matrixExponentiation = " + matrixExponentiation(n));
        }

        // Only the fast ones can go this far (naive recursion would take minutes at n = 50).
        int big = 90;                           // F(90) = 2880067194370816120, still fits in long
        System.out.println("n = " + big + " (fast approaches only)");
        System.out.println("  iterative            = " + iterative(big));
        System.out.println("  matrixExponentiation = " + matrixExponentiation(big));
    }
}
