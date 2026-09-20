/*
 * =====================================================================
 *  Lemonade Change                                LeetCode 860 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Each lemonade costs $5. Customers queue up in the given order and each pays with a
 *   single $5, $10 or $20 bill. You start with no change and can only hand back bills you
 *   have already received. Return true if every customer can be given correct change.
 *
 * EXAMPLE
 *   [5, 5, 5, 10, 20]  ->  true   the $20 is settled with one $10 and one $5
 *   [5, 5, 5, 20]      ->  true   no $10 in the till, so pay the $15 change with three $5
 *   [5, 5, 10, 10, 20] ->  false  only one $5 left, and $15 needs either 10+5 or 5+5+5
 *   [10]               ->  false  the very first customer needs $5 change you do not have
 *
 * APPROACH  (spend the least flexible bill first)
 *   1. Track only two counters: how many $5 and how many $10 bills are in the till.
 *      A $20 is never useful as change, so it is not worth counting.
 *   2. A $5 customer needs no change - bank the bill.
 *   3. A $10 customer needs $5 back. Without a $5 in the till, return false.
 *   4. A $20 customer needs $15 back. Prefer one $10 plus one $5; fall back to three $5.
 *      If neither combination is available, return false.
 *   5. Surviving the whole queue means every customer was served.
 *
 * KEY INSIGHT
 *   When a $20 arrives, both change options cost one $5, but the 10+5 option spends a bill
 *   that can ONLY ever pay a $20, while 5+5+5 burns two extra $5 bills that could each
 *   have served a future $10 customer. Preferring 10+5 is therefore never worse - the
 *   exchange argument. Pattern to recognise: when resources differ in flexibility, spend
 *   the least flexible one first and hoard the most flexible.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over the queue, constant work per customer
 *   Space O(1)  two integer counters regardless of queue length
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the greedy: why can preferring three $5 over 10+5 never help?
 *   - What if you start the day with a float of k $5 bills?
 *   - Generalise to arbitrary denominations - does the same greedy still hold?
 *   - Return the first customer index that fails instead of a boolean.
 *
 * RUN
 *   main() runs 4 cases: the typical queue, a $20 paid with three $5, a failing queue,
 *   and the single-customer edge case. Each prints actual vs expected.
 */

import java.util.Arrays;
import java.util.List;

class LemonadeChange {

    public static boolean lemonadeChange(List<Integer> bills) {
        int fives = 0;   // $5 bills in the till - the flexible change
        int tens = 0;    // $10 bills in the till - only ever usable against a $20

        for (int bill : bills) {
            if (bill == 5) {
                fives++;
            } else if (bill == 10) {
                if (fives == 0) {
                    return false;   // owe $5 and have none
                }
                fives--;
                tens++;
            } else {
                // $20 customer: owe $15. Spend the $10 first, it has no other use.
                if (tens > 0 && fives > 0) {
                    tens--;
                    fives--;
                } else if (fives >= 3) {
                    fives -= 3;
                } else {
                    return false;   // cannot form $15
                }
            }
        }
        return true;
    }

    private static void print(String label, List<Integer> bills, boolean actual,
                              boolean expected) {
        System.out.println(label + " " + bills + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        List<Integer> typical = Arrays.asList(5, 5, 5, 10, 20);
        print("case 1 typical", typical, lemonadeChange(typical), true);

        // No $10 banked, so the $15 change must come from three $5 bills.
        List<Integer> threeFives = Arrays.asList(5, 5, 5, 20);
        print("case 2 pay 15 with 5s", threeFives, lemonadeChange(threeFives), true);

        // Two $10 customers eat both $5 bills, leaving the $20 customer unserved.
        List<Integer> failing = Arrays.asList(5, 5, 10, 10, 20);
        print("case 3 impossible", failing, lemonadeChange(failing), false);

        // Edge case: the first customer already needs change.
        List<Integer> single = Arrays.asList(10);
        print("case 4 first fails", single, lemonadeChange(single), false);
    }
}
