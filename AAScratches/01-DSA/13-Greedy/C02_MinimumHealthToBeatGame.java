/*
 * =====================================================================
 *  Minimum Health to Beat Game              LeetCode 2214 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You play n levels in any order you like; level i costs damage[i] health. Exactly once,
 *   on one level of your choice, you may use armor to absorb min(armor, damage[i]) of that
 *   level's damage. Health must stay strictly above 0 the whole way. Return the minimum
 *   starting health that beats the game. Sums can exceed int range, so the answer is long.
 *
 * EXAMPLE
 *   damage = [2, 7, 4, 3], armor = 4   ->  13   armor absorbs 4 of the 7; 16 - 4 + 1
 *   damage = [2, 5, 3, 1], armor = 3   ->  9    armor absorbs 3 of the 5; 11 - 3 + 1
 *   damage = [5, 10, 7],   armor = 15  ->  13   armor caps out at the 10; 22 - 10 + 1
 *   damage = [1],          armor = 0   ->  2    no armor, so just total + 1
 *
 * APPROACH  (total aggregate plus one global choice)
 *   1. Level order is irrelevant: you take every level's damage exactly once, so the total
 *      health spent is fixed no matter how you sequence them.
 *   2. Sum all damage and track the single largest level in the same pass.
 *   3. The armor saves min(armor, damage[i]) on whichever level i you pick, and that value
 *      is maximised by picking the largest level - so the best saving is min(armor, max).
 *   4. Answer = totalDamage - min(armor, maxDamage) + 1. The +1 keeps health above 0
 *      rather than exactly 0 after the final level.
 *
 * KEY INSIGHT
 *   Strip away the ordering: nothing here is a scheduling problem, because every level is
 *   paid for regardless of sequence. What remains is one question - where does a single
 *   unit of a resource buy the most? min(armor, d) is non-decreasing in d, so the biggest
 *   level wins. Pattern to recognise: when order cannot change the total, a greedy reduces
 *   to "aggregate everything, then optimise one independent choice".
 *
 * COMPLEXITY
 *   Time  O(n)  one pass computing the sum and the maximum together
 *   Space O(1)  two accumulators, no extra structures
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does level order genuinely not matter? Say the invariant out loud.
 *   - What if the armor may be used k times? (Apply it to the k largest levels.)
 *   - What if armor absorbs a percentage instead of a flat amount? (Still the largest level.)
 *   - Why long and not int? Work out the worst case from the constraints.
 *
 * RUN
 *   main() runs 5 cases: two typical inputs, armor larger than every level, a single level
 *   with no armor, and an input whose total overflows int. Each prints actual vs expected.
 *
 * Fixed: two bugs. (1) totalDamage was an int, but 10^5 levels of 10^5 damage sum to 10^10
 * and silently wrapped to a negative number; the accumulator and the return type are now
 * long. (2) The expected-output comments in main were all wrong (14, 11, 8); the correct
 * answers for those inputs are 13, 9 and 13.
 */

import java.util.Arrays;

class MinimumHealthToBeatGame {

    public static long minimumHealth(int[] damage, int armor) {
        long totalDamage = 0;   // long: 10^5 levels x 10^5 damage overflows int
        int maxDamage = 0;

        for (int d : damage) {
            totalDamage += d;
            maxDamage = Math.max(maxDamage, d);
        }

        // Spend the armor on the largest level: min(armor, d) grows with d,
        // so no other level saves more.
        totalDamage -= Math.min(maxDamage, armor);

        // +1 so health is strictly above 0 after the last level, never exactly 0.
        return totalDamage + 1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", minimumHealth(new int[]{2, 7, 4, 3}, 4), 13L);
        print("case 2 typical", minimumHealth(new int[]{2, 5, 3, 1}, 3), 9L);

        // Armor exceeds every level, so it can only absorb the largest level in full.
        print("case 3 armor > max", minimumHealth(new int[]{5, 10, 7}, 15), 13L);

        // Edge case: a single level and no armor.
        print("case 4 single, no armor", minimumHealth(new int[]{1}, 0), 2L);

        // 100000 levels of 100000 damage sum to 10^10, which does not fit in an int.
        int[] huge = new int[100_000];
        Arrays.fill(huge, 100_000);
        print("case 5 overflows int", minimumHealth(huge, 0), 10_000_000_001L);
    }
}
