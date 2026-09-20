/*
 * =====================================================================
 *  Combination Sum                          LeetCode 39 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of DISTINCT positive integers (candidates) and a target,
 *   return every unique combination whose numbers add up to target.
 *   The same candidate may be reused an unlimited number of times.
 *   Two combinations differ if some number is used a different number of times;
 *   order inside a combination does not matter, so [2,3,3] and [3,2,3] are one answer.
 *
 * EXAMPLE
 *   candidates = [2, 3, 5], target = 8  ->  [[2,2,2,2], [2,3,3], [3,5]]
 *   candidates = [2],       target = 1  ->  []          2 already overshoots 1
 *   candidates = [7],       target = 14 ->  [[7,7]]     reuse of one candidate
 *
 * APPROACH  (start-index backtracking with reuse)
 *   1. Keep a running list "current" and the remaining target.
 *   2. remaining == 0 -> current is a complete answer, copy it into the result.
 *   3. Otherwise loop i from "start" to the end of the array. This loop-from-start
 *      form is what stops permutations of the same multiset being generated twice.
 *   4. Skip candidate[i] if it is bigger than what is left (pruning; all values
 *      are positive so it could never come back down to zero).
 *   5. Add candidate[i], recurse with start = i (NOT i + 1) so the same value can
 *      be picked again, then remove it again - the add / recurse / remove undo.
 *
 * KEY INSIGHT
 *   The single character that decides "reuse allowed" vs "use once" is the index
 *   passed down: i means the current element may be chosen again, i + 1 means move on.
 *   The "start" parameter itself is the anti-duplicate device: a branch may only ever
 *   look forward, so each combination is generated in exactly one (sorted-by-position)
 *   order. Recognise this whenever a problem says "combinations", not "permutations".
 *
 * COMPLEXITY
 *   Time  O(n^(target/min))  the recursion tree is bounded by depth target/min-candidate
 *                            with n branches per level; copying a hit costs O(target/min).
 *   Space O(target/min)      recursion depth plus the current list (result not counted).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Candidates may repeat and each may be used once -> Combination Sum II (sort + skip).
 *   - Only k numbers, values 1..9 -> Combination Sum III (extra size constraint).
 *   - Count the combinations instead of listing them -> unbounded-knapsack DP, O(n*target).
 *   - Count ORDERED sequences (Combination Sum IV) -> the loop order flips: target outer.
 *
 * RUN
 *   main() runs 4 cases (typical, no-solution, single-reused candidate, exact hit)
 *   and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class CombinationSum {

    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        combinationSumHelper(candidates, target, 0, current, result);
        return result;
    }

    /**
     * start = the first index this branch is still allowed to pick, which is what
     * keeps [2,3] and [3,2] from both being produced.
     */
    private static void combinationSumHelper(int[] candidates,
                                             int remaining,
                                             int start,
                                             List<Integer> current,
                                             List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));   // copy: current keeps mutating
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            // all candidates are positive, so anything larger than what is left is dead
            if (candidates[i] > remaining) {
                continue;
            }
            current.add(candidates[i]);
            // pass i, not i + 1: candidates[i] stays available for reuse
            combinationSumHelper(candidates, remaining - candidates[i], i, current, result);
            current.remove(current.size() - 1);     // undo, so the next i starts clean
        }
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [2,3,5] t=8",
                combinationSum(new int[]{2, 3, 5}, 8),
                "[[2, 2, 2, 2], [2, 3, 3], [3, 5]]");

        print("case 2 [2] t=1 (edge: impossible)",
                combinationSum(new int[]{2}, 1),
                "[]");

        print("case 3 [7] t=14 (edge: reuse one value)",
                combinationSum(new int[]{7}, 14),
                "[[7, 7]]");

        print("case 4 [2,3,6,7] t=7 (tricky: exact single hit)",
                combinationSum(new int[]{2, 3, 6, 7}, 7),
                "[[2, 2, 3], [7]]");
    }
}
