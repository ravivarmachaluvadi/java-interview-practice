/*
 * =====================================================================
 *  Recursive Factorial                                  Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a non-negative integer n, return n! = n * (n-1) * ... * 2 * 1, with 0! = 1.
 *   The answer is returned as a long, so only n <= 20 fits; 21! overflows a 64-bit signed
 *   integer. A negative n has no factorial and is rejected.
 *
 * EXAMPLE
 *   n = 5   ->  120                    because 5 * 4 * 3 * 2 * 1
 *   n = 0   ->  1                      the base case, by definition
 *   n = 20  ->  2432902008176640000    the largest factorial that fits in a long
 *   n = -1  ->  IllegalArgumentException
 *
 * APPROACH  (base case + one self-call)
 *   1. Reject n < 0 up front, so the recursion can assume a valid input.
 *   2. Base case: if n <= 1 the answer is 1 and the recursion stops.
 *   3. Recursive case: return n * factorial(n - 1) - trust the call to solve the
 *      strictly smaller problem, then combine its answer with the current n.
 *
 * KEY INSIGHT
 *   Every recursion is two things and nothing more: a base case that returns without
 *   calling itself, and a recursive case that moves strictly closer to that base case.
 *   Here "closer" is n - 1, which is why the chain must terminate. Recognise this shape -
 *   every backtracking template in this folder is this same skeleton with a loop wrapped
 *   around the self-call.
 *
 * COMPLEXITY
 *   Time  O(n)  one multiplication per level, n levels deep.
 *   Space O(n)  n stack frames; this is NOT tail recursion, because the multiply happens
 *               after the call returns, and javac does not eliminate tail calls anyway.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rewrite it iteratively; which one would you ship and why? (iterative: O(1) stack)
 *   - What is the largest n before overflow, and how do you detect it? (n > 20; BigInteger)
 *   - Convert it to an accumulator (tail) form: helper(n, acc).
 *   - How deep can Java recurse before StackOverflowError, and what controls that?
 *
 * RUN
 *   main() runs 5 cases (typical, base case, boundary, overflow, invalid) and prints
 *   actual vs expected.
 */
class RecursiveFactorial {

    /**
     * Fixed: a negative n used to fall into the n <= 1 base case and silently return 1.
     * Factorial is undefined for negatives, so we fail loudly instead.
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("factorial is undefined for n < 0, got " + n);
        }
        // Base case: 0! and 1! are both 1, and the recursion stops here.
        if (n <= 1) return 1;
        // Recursive case: solve the smaller problem, then fold n into its answer.
        return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        RecursiveFactorial rf = new RecursiveFactorial();

        print("case 1 (typical)   5!", rf.factorial(5), 120L);
        print("case 2 (base case) 0!", rf.factorial(0), 1L);
        print("case 3 (boundary) 20!", rf.factorial(20), 2432902008176640000L);

        // Tricky: 21! = 51090942171709440000 does not fit in a long, so it silently wraps.
        // The point of the case is that recursion is correct but the TYPE is too small.
        print("case 4 (overflow) 21!", rf.factorial(21), -4249290049419214848L);

        String invalid;
        try {
            rf.factorial(-1);
            invalid = "no exception";
        } catch (IllegalArgumentException e) {
            invalid = "IllegalArgumentException";
        }
        print("case 5 (invalid)  -1!", invalid, "IllegalArgumentException");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
