/*
 * =====================================================================
 *  Sum of Product of All Pairs                            Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array A, return the sum of A[i] * A[j] over every unordered
 *   pair i < j. Each pair is counted once and an element is never paired with
 *   itself. Values may be negative; an array of size 0 or 1 has no pairs, so the
 *   answer is 0.
 *
 * EXAMPLE
 *   A = [1, 3, 4]      ->  19     because 1*3 + 1*4 + 3*4 = 3 + 4 + 12
 *   A = [5]            ->  0      no pair exists
 *   A = [-2, 3, -4]    ->  -10    because -6 + 8 - 12
 *
 * APPROACH 1  (brute force pair enumeration - the author's loop)
 *   1. Outer loop i over every index.
 *   2. Inner loop j from i+1 so each unordered pair is visited exactly once.
 *   3. Accumulate A[i] * A[j].
 *
 * APPROACH 2  (algebraic identity - one pass)
 *   1. Expand (a1 + a2 + ... + an)^2. It equals the sum of squares plus TWICE
 *      the sum of all distinct pair products.
 *   2. Rearrange:  pairSum = (total^2 - sumOfSquares) / 2.
 *   3. One pass collects total and sumOfSquares, so the answer is O(n).
 *
 * KEY INSIGHT
 *   Any "sum over all pairs" question should make you try squaring the total
 *   first. The square of a sum already contains every pair product twice, so
 *   subtracting the diagonal (the squares) and halving collapses an O(n^2)
 *   double loop into O(n). The same trick answers "sum of |differences|
 *   squared" and several array-pair variants.
 *
 * COMPLEXITY
 *   Time  O(n^2) brute force (every pair touched) vs O(n) with the identity
 *   Space O(1)   both - only running accumulators
 *
 * INTERVIEW FOLLOW-UPS
 *   - Where does this overflow, and why must the accumulator be long?
 *   - Sum of products of all pairs modulo 1e9+7 - does the /2 still work?
 *   - Sum over ordered pairs (i != j) instead: just drop the halving.
 *   - Sum of A[i] * A[j] only for pairs with j - i <= k.
 *
 * NOTE ON THE ORIGINAL
 *   Fixed: the accumulator was an int and silently overflowed on large inputs
 *   (1000 elements of value 1000 already needs 499,500,000,000). Widened to long.
 *
 * RUN
 *   main() runs 4 cases (typical, single element, negatives, an overflow-sized
 *   input) through both methods and prints actual vs expected.
 */
class SumOfProductOfPairs {

    public static void main(String[] args) {
        check("typical [1, 3, 4]", new int[]{1, 3, 4}, 19);
        check("single  [5]      ", new int[]{5}, 0);
        check("negative[-2,3,-4]", new int[]{-2, 3, -4}, -10);

        // Tricky: 1000 copies of 1000. The true answer needs 39 bits, so an int
        // accumulator would wrap around here.
        int[] big = new int[1000];
        java.util.Arrays.fill(big, 1000);
        check("overflow 1000x1000", big, 499500000000L);
    }

    /** Approach 1: touch every unordered pair once. O(n^2). */
    public static long sumOfProductPairs(int[] a) {
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            // j starts at i+1 so (x, y) and (y, x) are not both counted
            for (int j = i + 1; j < a.length; j++)
                sum += (long) a[i] * a[j];
        }
        return sum;
    }

    /** Approach 2: (total^2 - sumOfSquares) / 2. O(n). */
    public static long sumOfProductPairsFast(int[] a) {
        long total = 0;
        long sumOfSquares = 0;
        for (int value : a) {
            total += value;
            sumOfSquares += (long) value * value;
        }
        // total^2 counts every pair product twice and each square once
        return (total * total - sumOfSquares) / 2;
    }

    private static void check(String label, int[] a, long expected) {
        System.out.println(label + ": brute " + sumOfProductPairs(a)
                + ", identity " + sumOfProductPairsFast(a)
                + "   expected " + expected);
    }
}
