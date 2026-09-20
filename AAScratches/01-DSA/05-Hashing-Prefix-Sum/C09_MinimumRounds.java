/*
 * =====================================================================
 *  Minimum Rounds to Complete All Tasks            LeetCode 2244 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   tasks[i] is the difficulty level of a task. In one round you may complete either
 *   2 or 3 tasks of the SAME level. Return the minimum number of rounds to finish every
 *   task, or -1 if it is impossible (some level appears exactly once).
 *
 * EXAMPLE
 *   tasks = [2,2,3,3,2,4,4,4,4,4]  ->  4    counts 3,2,5 -> 1 + 1 + 2 rounds
 *   tasks = [2,3,3]                ->  -1   level 2 appears once, no valid round
 *   tasks = [1,1,1,1]              ->  2    count 4 -> two rounds of 2 (not 3 + leftover 1)
 *   tasks = [1,1]                  ->  1    single pair
 *
 * APPROACH  (counting map + greedy ceil(count / 3))
 *   1. Count how many times each level appears (HashMap level -> count).
 *   2. For each count c:
 *        c == 1       -> return -1 (cannot form a round of 2 or 3)
 *        c % 3 == 0   -> c / 3 rounds, all triples
 *        c % 3 == 1   -> c / 3 + 1 rounds: swap one triple for two pairs (3+1 = 2+2)
 *        c % 3 == 2   -> c / 3 + 1 rounds: triples plus one pair
 *      All three collapse to ceil(c / 3) = (c + 2) / 3.
 *   3. Sum over all levels.
 *   Alternative shown as minimumRoundsSorted(): sort, walk runs of equal values, apply the
 *   same formula per run. O(n log n) time and O(n) extra space, because the input is cloned
 *   so the caller's array is not mutated - O(log n) stack only if you may sort in place.
 *
 * KEY INSIGHT
 *   The hash map only sets up the real question: "given c identical items, how few groups
 *   of 2 or 3?" Using as many 3s as possible is optimal, and any leftover of 1 can be
 *   fixed by borrowing from one triple (3+1 -> 2+2), so the answer is always ceil(c/3).
 *   Pattern: count, then apply a per-bucket math argument.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to count, one pass over distinct levels
 *   Space O(k)  k distinct levels in the map
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove ceil(c/3) is optimal (each round removes at most 3, and c%3==1 forces two pairs).
 *   - Rounds must be exactly 2 tasks -> answer is c/2 per level, -1 if any count is odd.
 *   - Rounds of size 2 or 3 but across different levels allowed -> just ceil(n/3) overall.
 *   - Space-constrained input: group runs after sorting (minimumRoundsSorted); drop its
 *     defensive clone and sort the caller's array in place to get down to O(log n) stack.
 *
 * RUN
 *   main() runs 4 cases (typical, impossible, count%3==1, single pair) with both methods
 *   and prints actual vs expected.
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class MinimumRounds {

    /** Primary approach: frequency map, then ceil(count/3) per level. */
    public static int minimumRounds(int[] tasks) {
        Map<Integer, Integer> countByLevel = new HashMap<>();
        for (int task : tasks) {
            countByLevel.merge(task, 1, Integer::sum);
        }

        int totalRounds = 0;
        for (int count : countByLevel.values()) {
            int rounds = roundsForCount(count);
            if (rounds == -1) return -1;
            totalRounds += rounds;
        }
        return totalRounds;
    }

    /** Alternative: sort so equal levels are adjacent, then measure each run. O(n) extra: the
     *  clone below. Sorting the caller's array in place instead would cost only O(log n) stack. */
    public static int minimumRoundsSorted(int[] tasks) {
        int[] sorted = tasks.clone(); // do not mutate the caller's array
        Arrays.sort(sorted);

        int n = sorted.length;
        int totalRounds = 0;
        int runStart = 0;
        while (runStart < n) {
            int runEnd = runStart;
            while (runEnd < n && sorted[runEnd] == sorted[runStart]) runEnd++;

            int rounds = roundsForCount(runEnd - runStart);
            if (rounds == -1) return -1;
            totalRounds += rounds;
            runStart = runEnd;
        }
        return totalRounds;
    }

    /** Minimum groups of size 2 or 3 that cover exactly count items, or -1 if count == 1. */
    private static int roundsForCount(int count) {
        if (count == 1) return -1;
        // ceil(count / 3): a remainder of 1 is absorbed by turning one triple into two pairs
        return (count + 2) / 3;
    }

    private static void print(String label, int[] tasks, int expected) {
        System.out.println(label + ": map=" + minimumRounds(tasks)
                + " sorted=" + minimumRoundsSorted(tasks)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical      ", new int[]{2, 2, 3, 3, 2, 4, 4, 4, 4, 4}, 4);
        print("case 2 impossible   ", new int[]{2, 3, 3}, -1);
        print("case 3 count%3==1   ", new int[]{1, 1, 1, 1}, 2);
        print("case 4 single pair  ", new int[]{1, 1}, 1);
    }
}
