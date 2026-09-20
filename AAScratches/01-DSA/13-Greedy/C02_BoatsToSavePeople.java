/*
 * =====================================================================
 *  Boats to Save People                         LeetCode 881 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   people[i] is the weight of the i-th person. Every boat carries at most 2 people and
 *   at most `limit` total weight. Each person's weight is guaranteed to be <= limit, so a
 *   solution always exists. Return the minimum number of boats needed to carry everyone.
 *
 * EXAMPLE
 *   people = [1, 2],       limit = 3  ->  1   both fit in one boat (1 + 2 = 3)
 *   people = [3, 2, 2, 1], limit = 3  ->  3   (3), (2 + 1), (2)
 *   people = [3, 5, 3, 4], limit = 5  ->  4   no pair fits, everyone rides alone
 *   people = [5],          limit = 5  ->  1   single person, exact limit
 *
 * APPROACH  (sorted two-pointer pairing from both ends)
 *   1. Sort the weights ascending. Put `left` at the lightest, `right` at the heaviest.
 *   2. The heaviest remaining person must board some boat now, so a boat is always used
 *      on this iteration - increment boats and decrement right unconditionally.
 *   3. If the lightest also fits alongside them (people[left] + people[right] <= limit),
 *      seat them together and advance left too. Otherwise the heaviest sails alone.
 *   4. Repeat while left <= right. The left == right case seats one person in that boat.
 *
 * KEY INSIGHT
 *   The heaviest person has the fewest possible partners, so decide their seat first. If
 *   anyone can share with them it is the lightest person; pairing with anyone heavier only
 *   wastes a more constrained partner, so the lightest choice is never worse (exchange
 *   argument). Pattern to recognise: with a capacity of exactly 2, the answer is always
 *   "match the most constrained element against the least constrained, or send it alone".
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the two-pointer sweep is a single O(n) pass
 *   Space O(1)        sorting in place plus three counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if a boat can hold 3 people? (The greedy breaks - it becomes bin packing, NP-hard.)
 *   - What if some weights exceed the limit? Detect and reject instead of looping forever.
 *   - Return the actual boat assignments, not just the count.
 *   - With weights bounded by limit, can you drop the sort? (Yes - counting sort, O(n + limit).)
 *
 * RUN
 *   main() runs 4 cases: an exact-fit pair, a mixed queue, a queue where no pair fits, and
 *   the single-person edge case. Each prints actual vs expected.
 */

import java.util.Arrays;

class BoatsToSavePeople {

    public static int numRescueBoats(int[] people, int limit) {
        Arrays.sort(people);
        int left = 0;                      // lightest person not yet seated
        int right = people.length - 1;     // heaviest person not yet seated
        int boats = 0;

        while (left <= right) {
            // The lightest person rides along only if the pair fits under the limit.
            if (people[left] + people[right] <= limit) {
                left++;
            }
            // Paired or alone, the heaviest person takes this boat.
            right--;
            boats++;
        }
        return boats;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 exact fit pair", numRescueBoats(new int[]{1, 2}, 3), 1);
        print("case 2 mixed queue", numRescueBoats(new int[]{3, 2, 2, 1}, 3), 3);
        print("case 3 no pair fits", numRescueBoats(new int[]{3, 5, 3, 4}, 5), 4);
        print("case 4 single person", numRescueBoats(new int[]{5}, 5), 1);
    }
}
