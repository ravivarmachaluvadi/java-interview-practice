/*
 * =====================================================================
 *  Count Fibonacci Numbers In A Range                  no LeetCode id | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an inclusive range [low, high], count how many DISTINCT Fibonacci numbers fall
 *   inside it. The sequence used here is 0, 1, 2, 3, 5, 8, 13, ... - F(0) = 0 counts, and
 *   the value 1 counts once even though it appears twice in the sequence (F(1) and F(2)).
 *   The bounds are ints and may be negative; no Fibonacci number is negative.
 *
 * EXAMPLE
 *   low = 5,  high = 21   ->  4    5, 8, 13, 21
 *   low = 0,  high = 0    ->  1    only 0
 *   low = 1,  high = 1    ->  1    the value 1, counted once, not twice
 *   low = 22, high = 33   ->  0    21 is below, 34 is above
 *   low = 0,  high = 2147483647 -> 46   every Fibonacci value that fits in an int
 *
 * APPROACH  (iterative generate-and-test)
 *   1. Return 0 immediately if the range is empty (high < low).
 *   2. Count the special value 0 first, when 0 lies inside the range.
 *   3. Seed the generator with previous = 1, current = 1. Starting from the SECOND 1 is what
 *      makes the walk emit 1, 2, 3, 5, 8, ... with no repeat.
 *   4. While current <= high, count it if current >= low, then step:
 *      next = previous + current; previous = current; current = next.
 *
 * KEY INSIGHT
 *   Fibonacci numbers grow exponentially - roughly phi^n, phi = 1.618 - so only 47 of them
 *   fit in a signed 32-bit int (F(46) = 1836311903 is the last). That is why you never need a
 *   sieve, a set or a closed form here: generating the whole usable sequence is about 46 steps
 *   of addition, so "generate until you pass the upper bound" is already optimal. The two
 *   traps are the repeated 1 at the head of the sequence and the silent int overflow one step
 *   past F(46), which wraps negative and makes a naive loop count nonsense values.
 *
 *   Fixed (three bugs in the original):
 *     1. 1 was counted twice, because the walk started at previous = 0, current = 1.
 *     2. The accumulators were ints, so with high = Integer.MAX_VALUE the sum F(46) + F(45)
 *        wrapped negative, the loop kept going and counted wrapped values. They are longs now.
 *     3. low was clamped up to 1, so 0 - which is F(0) - could never be counted.
 *
 * COMPLEXITY
 *   Time  O(log_phi(high))  one addition per Fibonacci number up to high; at most 47 for ints
 *   Space O(1)              two accumulators and a counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - Test whether a single n is Fibonacci: 5n^2 + 4 or 5n^2 - 4 is a perfect square.
 *   - Return the numbers themselves instead of the count, or the sum of them.
 *   - For huge bounds use BigInteger, or fast doubling / matrix exponentiation to jump to
 *     F(n) in O(log n) instead of walking.
 *   - Many callers repeat this query - precompute the 47 int-sized values once and binary
 *     search the bounds, making each query O(log 47).
 *
 * RUN
 *   main() runs 6 cases: typical, both single-value edges, an empty slice between terms,
 *   a negative lower bound, and the full int range.
 */

class FibonacciCounter {

    /** Count the distinct Fibonacci numbers in the inclusive range [low, high]. */
    public static int countFibonacciInRange(int low, int high) {
        if (high < low) return 0;                 // empty range

        int count = 0;
        if (low <= 0 && high >= 0) count++;       // F(0) = 0, handled once and on its own

        // Seeding with (1, 1) instead of (0, 1) skips the duplicate 1 at the head of the
        // sequence: current now walks 1, 2, 3, 5, 8, ... with every value visited once.
        // longs are required - the step after F(46) = 1836311903 overflows an int.
        long previous = 1;
        long current = 1;

        while (current <= high) {
            if (current >= low) count++;
            long next = previous + current;
            previous = current;
            current = next;
        }
        return count;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> actual " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1: [5, 21]      5, 8, 13, 21", countFibonacciInRange(5, 21), 4);
        print("case 2: [0, 0]       just 0      ", countFibonacciInRange(0, 0), 1);
        print("case 3: [1, 1]       1 counts once", countFibonacciInRange(1, 1), 1);
        print("case 4: [22, 33]     between terms", countFibonacciInRange(22, 33), 0);
        print("case 5: [-10, 1]     0 and 1     ", countFibonacciInRange(-10, 1), 2);
        print("case 6: [0, MAX_INT] whole int range",
                countFibonacciInRange(0, Integer.MAX_VALUE), 46);
    }
}
