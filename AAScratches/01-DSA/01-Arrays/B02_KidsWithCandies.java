/*
 * =====================================================================
 *  Kids With the Greatest Number of Candies              LeetCode 1431 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   candies[i] is how many candies kid i has; extraCandies is a bonus that could
 *   be handed to any ONE kid. For each kid answer: if this kid alone received all
 *   the extra candies, would they have the greatest count in the class (ties count)?
 *   Return a boolean list of the same length. 1 <= n <= 100, candies[i] >= 1.
 *
 * EXAMPLE
 *   candies = [2, 3, 5, 1, 3], extra = 3  ->  [true, true, true, false, true]
 *       max is 5; kid 3 has 1 + 3 = 4 < 5, every other kid reaches >= 5
 *   candies = [4, 2, 1, 1, 2], extra = 1  ->  [true, false, false, false, false]
 *   candies = [7],             extra = 0  ->  [true]          single kid is the max
 *   candies = [3, 3, 3],       extra = 0  ->  [true, true, true]   ties count
 *
 * APPROACH  (find max, then compare pass)
 *   1. First pass: scan once to find maxCandies, the current class maximum.
 *   2. Second pass: for each kid, answer candies[i] + extraCandies >= maxCandies.
 *   3. The extra candies go to one kid at a time, so the other kids' counts never
 *      change and the maximum found in step 1 stays valid for every comparison.
 *
 * KEY INSIGHT
 *   The question for each index depends only on one global aggregate (the max).
 *   Compute the aggregate once, then answer every index in a second pass.
 *   Pattern to recognise: "for each element, compare against something about the
 *   whole array" = two passes, never a nested loop.
 *
 * COMPLEXITY
 *   Time  O(n)  two linear scans
 *   Space O(1)  extra, excluding the output list of n booleans
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not an O(n^2) compare-each-kid-to-every-other loop? The max is the only
 *     value that matters, so precomputing it removes the inner loop.
 *   - What if the extra candies were split among several kids? The others' counts
 *     would change and the single precomputed max would no longer be enough.
 *   - Return a boolean[] instead of List<Boolean> to avoid boxing; same logic.
 *
 * RUN
 *   main() runs 4 cases (typical, LeetCode example 2, single kid, all equal) and
 *   prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class KidsWithCandies {

    public static List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        // Pass 1: the current maximum. MIN_VALUE seed keeps this correct even if
        // counts could be zero or negative (LeetCode guarantees >= 1, but be safe).
        int maxCandies = Integer.MIN_VALUE;
        for (int candy : candies) {
            maxCandies = Math.max(maxCandies, candy);
        }

        // Pass 2: each kid is compared against that fixed maximum (ties count).
        List<Boolean> result = new ArrayList<>(candies.length);
        for (int candy : candies) {
            result.add(candy + extraCandies >= maxCandies);
        }
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", kidsWithCandies(new int[]{2, 3, 5, 1, 3}, 3),
                "[true, true, true, false, true]");
        print("case 2 one winner", kidsWithCandies(new int[]{4, 2, 1, 1, 2}, 1),
                "[true, false, false, false, false]");
        print("case 3 single kid", kidsWithCandies(new int[]{7}, 0),
                "[true]");
        print("case 4 all equal ", kidsWithCandies(new int[]{3, 3, 3}, 0),
                "[true, true, true]");
    }
}
