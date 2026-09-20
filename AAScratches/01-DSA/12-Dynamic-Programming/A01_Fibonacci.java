/*
 * =====================================================================
 *  Fibonacci Number                      LeetCode 509 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Return F(n), where F(0) = 0, F(1) = 1 and F(n) = F(n-1) + F(n-2).
 *   n is a non-negative int. F(92) is the last value that fits in a long,
 *   so this file returns long and stops at n = 90.
 *
 * EXAMPLE
 *   n = 0   ->  0        (base case, main() runs this)
 *   n = 4   ->  3        0, 1, 1, 2, 3
 *   n = 10  ->  55 n = 90  ->  2880067194370816120   (only the fast methods can reach this)
 *
 * APPROACH  (four versions of the same recurrence, cheapest to most expensive)
 *   1. naiveRecursive: translate the recurrence literally. fib(5) computes
 *      fib(3) twice and fib(2) three times - the call tree nearly doubles per
 *      level, so the cost is exponential. This is the problem DP exists to fix.
 *   2. memoized: same recursion plus a long[] cache. Before recursing, check
 *      the cache; after computing, store. Each of the n+1 states is solved once.
 *   3. iterative: fill the table left to right. Only the previous two values
 *      are ever read, so keep two variables instead of an array.
 *   4. matrixExponentiation: use the identity below and binary exponentiation
 *      to raise the 2x2 matrix in O(log n) multiplications.
 *          [F(n+1) F(n)  ]     [1 1]^n [F(n)   F(n-1)]  =  [1 0]
 *
 * KEY INSIGHT
 *   The naive recursion is slow not because recursion is slow but because it
 *   recomputes the SAME state over and over: the subproblems overlap. Every DP
 *   answer is one of two ways to pay for each state once - cache the recursion
 *   (top-down) or order the states so each is ready when needed (bottom-up).
 *   Then notice that a state only depends on the last two rows, which is the
 *   rolling-variable trick that turns O(n) space into O(1). Recognise the
 *   pattern whenever a recurrence calls itself more than once on smaller n.
 *
 * COMPLEXITY
 *   naiveRecursive        Time O(2^n)     Space O(n)    call stack depth n
 *   memoized              Time O(n)       Space O(n)    cache + stack
 *   iterative             Time O(n)       Space O(1)    two rolling variables
 *   matrixExponentiation  Time O(log n)   Space O(1)    squaring halves the exponent
 *
 * INTERVIEW FOLLOW-UPS
 *   - Can you beat O(n)? Matrix power in O(log n); also Binet's closed form,
 *     which loses precision past about n = 70 and is not a safe answer.
 *   - Return F(n) mod 1e9+7 for huge n: same matrix power with modular multiply.
 *   - Why does the memo use 0 as "not computed"? Because F(n) > 0 for n >= 1;
 *     with a different recurrence you would need a separate seen[] flag.
 *   - Climbing Stairs (LC 70) and House Robber are this recurrence in disguise.
 *
 * RUN
 *   main() runs the base cases, a typical n, and n = 90 for the fast methods,
 *   printing actual vs expected for every approach.
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
        if (memo[n] != 0)           // 0 only marks unsolved cells (F(n) > 0 for n >= 1)
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
    // M^(n-1)[0][0] = F(n), with M = {{1,1},{1,0}}. Raise M by square-and-multiply.
    static long matrixExponentiation(long n) {
        if (n == 0) return 0;
        if (n == 1) return 1;

        long[][] baseMatrix = {{1, 1}, {1, 0}};
        long[][] resultMatrix = matrixPower(baseMatrix, n - 1);

        return resultMatrix[0][0];
    }

    // Binary exponentiation on a 2x2 matrix (same loop shape as fast integer pow)
    static long[][] matrixPower(long[][] base, long exp) {
        long[][] result = {{1, 0}, {0, 1}}; // identity matrix

        while (exp > 0) {
            if ((exp & 1) == 1)                  // odd bit set -> fold base into the answer
                result = multiplyMatrices(result, base);
            base = multiplyMatrices(base, base); // square the base
            exp >>= 1;                           // drop the bit we just used
        }
        return result;
    }

    // Multiply two 2x2 matrices (row of A dot column of B)
    static long[][] multiplyMatrices(long[][] a, long[][] b) {
        long[][] result = new long[2][2];
        result[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0]; // row 0 . col 0
        result[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1]; // row 0 . col 1
        result[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0]; // row 1 . col 0
        result[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1]; // row 1 . col 1
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] inputs = {0, 1, 4, 5, 10, 30};
        long[] expected = {0, 1, 3, 5, 55, 832040};

        for (int i = 0; i < inputs.length; i++) {
            int n = inputs[i];
            long want = expected[i];
            print("n = " + n + " naiveRecursive      ", naiveRecursive(n), want);
            print("n = " + n + " memoized            ", memoized(n), want);
            print("n = " + n + " iterative           ", iterative(n), want);
            print("n = " + n + " matrixExponentiation", matrixExponentiation(n), want);
        }

        // Only the fast ones can go this far (naive recursion would take minutes at n = 50).
        int big = 90;                     // F(90) = 2880067194370816120, still fits in a long
        long bigExpected = 2880067194370816120L;
        print("n = " + big + " iterative           ", iterative(big), bigExpected);
        print("n = " + big + " matrixExponentiation", matrixExponentiation(big), bigExpected);
    }
}
