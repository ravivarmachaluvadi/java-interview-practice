/*
 * =====================================================================
 *  Sqrt(x)                                   LeetCode 69 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a non-negative int x, return the integer part of its square root, i.e. the largest
 *   int m with m * m <= x. Built-in Math.sqrt is not allowed. x can be up to 2^31 - 1, so
 *   mid * mid would overflow a 32-bit int.
 *
 * EXAMPLE
 *   x = 25  ->  5    exact square
 *   x = 8   ->  2    because 2*2 = 4 <= 8 < 9 = 3*3
 *   x = 0   ->  0    edge: the loop cannot start at 1 with x = 0
 *   x = 1   ->  1 x = 2147483647 (Integer.MAX_VALUE)  ->  46340    overflow-safe check needed
 *
 * APPROACH  (binary search on the answer range [1, x])
 *   1. Handle x == 0 up front; the search range below starts at 1 so division is safe.
 *   2. low = 1, high = x. Pick mid.
 *   3. Compare mid with x / mid (integer division) instead of mid * mid with x:
 *        mid == x / mid  ->  mid*mid <= x < (mid+1)*mid, so mid is the answer, return it
 *        mid >  x / mid  ->  mid*mid > x, answer is smaller: high = mid - 1
 *        mid <  x / mid  ->  mid*mid < x, answer may be larger: low = mid + 1
 *   4. If the loop exits without an exact return, high is the last mid whose square
 *      was <= x, which is the floor of the square root.
 *
 * KEY INSIGHT
 *   There is no array. The sorted thing is the predicate "m*m <= x" over m = 1..x, which is
 *   true...true, false...false. Binary search on that value range finds the last true.
 *   This is the first "binary search on the answer" problem; Koko, ship capacity and cutting
 *   ribbons all use the same shape with a more expensive predicate.
 *
 * COMPLEXITY
 *   Time  O(log x)  the candidate range halves each step
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why x / mid instead of mid * mid: mid * mid overflows int for mid > 46340. Using long
 *     is the other common fix.
 *   - Tighten the upper bound to x / 2 + 1 for x >= 2 (sqrt(x) <= x/2 when x >= 4).
 *   - Newton's method converges faster: r = (r + x / r) / 2 until r * r <= x.
 *   - Perfect square check (LC 367): same search, return true only on an exact hit.
 *
 * RUN
 *   main() runs 5 cases (exact square, non-square, 0, 1, Integer.MAX_VALUE) and prints
 *   actual vs expected.
 */
class SquareRoot {

    public static int getMySqrt(int x) {
        if (x == 0) return 0; // range below starts at 1, so 0 needs its own answer

        int left = 1;
        int right = x;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            // Compare against x / mid rather than mid * mid so large x cannot overflow.
            int quotient = x / mid;
            if (mid == quotient) {
                return mid;          // mid*mid <= x < (mid+1)*mid, so mid is the floor sqrt
            } else if (mid > quotient) {
                right = mid - 1;     // mid*mid > x, look for something smaller
            } else {
                left = mid + 1;      // mid*mid < x, a larger root may still fit
            }
        }
        // Loop ended with left > right: right is the last mid whose square did not exceed x.
        return right;
    }

    private static void print(String label, int x, int expected) {
        System.out.println(label + ": sqrt(" + x + ") = " + getMySqrt(x)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 exact square ", 25, 5);
        print("case 2 non-square   ", 8, 2);
        print("case 3 zero         ", 0, 0);
        print("case 4 one          ", 1, 1);
        print("case 5 int max      ", Integer.MAX_VALUE, 46340);
    }
}
