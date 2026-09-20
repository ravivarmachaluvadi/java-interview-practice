/*
 * =====================================================================
 *  Happy Number                          LeetCode 202 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Repeatedly replace a positive integer n by the sum of the squares of its
 *   digits. n is "happy" if this process eventually reaches 1; it is unhappy
 *   if it falls into a cycle that never contains 1. Return true if happy.
 *
 * EXAMPLE
 *   n = 19  ->  true    19 -> 82 -> 68 -> 100 -> 1
 *   n = 2   ->  false   2 -> 4 -> 16 -> 37 -> 58 -> 89 -> 145 -> 42 -> 20 -> 4 (cycle)
 *   n = 1   ->  true    already at 1, the edge case
 *   n = 7   ->  true    7 -> 49 -> 97 -> 130 -> 10 -> 1
 *
 * APPROACH  (Floyd cycle detection, tortoise and hare)
 *   1. getNext(n) peels digits with n % 10 and n / 10 and sums their squares.
 *   2. slow takes one step per round, fast takes two.
 *   3. Stop when fast reaches 1 (happy) or when slow == fast (they met, so we
 *      are inside a cycle that does not contain 1).
 *   4. Return fast == 1.
 *
 * KEY INSIGHT
 *   The sequence ALWAYS ends in a cycle, so "does it loop?" is not the
 *   question - the question is which cycle. For any n below 1000 the next
 *   value is at most 9^2 * 3 = 243, so the sequence is trapped in a small
 *   finite range and must repeat. Once you see "a function applied over and
 *   over on a finite set", you are on a functional graph, and Floyd's two
 *   pointers detect the loop in O(1) space - the same trick as Linked List
 *   Cycle and Find the Duplicate Number.
 *
 * COMPLEXITY
 *   Time  O(log n)  to reduce n into the small range, then O(1) extra because
 *                   the cycle length below 243 is bounded by a constant
 *   Space O(1)      two ints; no HashSet of seen values needed
 *
 * INTERVIEW FOLLOW-UPS
 *   - Solve it with a HashSet instead; compare space (O(log n) vs O(1)).
 *   - Prove the sequence cannot grow without bound (the 3-digit argument above).
 *   - Name the one unhappy cycle: 4, 16, 37, 58, 89, 145, 42, 20, back to 4.
 *   - Generalise to sums of cubes of digits - do the same arguments hold?
 *
 * RUN
 *   main() runs 4 cases (happy, unhappy cycle, the n = 1 edge, one more
 *   happy number) and prints actual vs expected.
 */

class HappyNumber {

    public boolean isHappy(int n) {
        int slow = n;
        int fast = getNext(n);

        // Stop on success (fast hit 1) or when the pointers meet inside a cycle.
        while (fast != 1 && slow != fast) {
            slow = getNext(slow);
            fast = getNext(getNext(fast));
        }
        return fast == 1;
    }

    /** Sum of the squares of the decimal digits of n. */
    private int getNext(int n) {
        int totalSum = 0;
        while (n > 0) {
            int d = n % 10;   // last digit
            n = n / 10;       // drop it
            totalSum += d * d;
        }
        return totalSum;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        HappyNumber hn = new HappyNumber();

        print("case 1  n = 19 (happy)", hn.isHappy(19), true);
        print("case 2  n = 2  (unhappy cycle)", hn.isHappy(2), false);
        print("case 3  n = 1  (edge, already 1)", hn.isHappy(1), true);
        print("case 4  n = 7  (happy, longer path)", hn.isHappy(7), true);
    }
}
