/*
 * =====================================================================
 *  Random Pick Index                              LeetCode 398 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Two related jobs live in this file. The building block: pick one index of an
 *   array uniformly at random. The actual LeetCode 398 problem: given nums (which
 *   may contain duplicates) and a target, return a random index where that target
 *   sits, each such index equally likely.
 *
 * EXAMPLE
 *   arr = [10, 20, 30, 40, 50, 60]      ->  pickRandomIndex gives some i in [0, 6)
 *   arr = [7]                           ->  pickRandomIndex always gives 0
 *   nums = [1, 2, 3, 3, 3], target = 3  ->  one of {2, 3, 4}, each with probability 1/3
 *   nums = [1, 2, 3, 3, 3], target = 1  ->  always 0 (only one match)
 *
 * APPROACH  (bounded Random, then reservoir sampling of size 1)
 *   1. pickRandomIndex: rand.nextInt(arr.length). The bound is EXCLUSIVE, so the
 *      result is already a legal index - no -1 and no modulo bias.
 *   2. pickIndexOfTarget: walk nums once, counting matches seen so far (count).
 *      On the k-th match, replace the answer with that index with probability 1/k.
 *   3. Because the replacement probability shrinks as k grows, every match ends up
 *      equally likely, and only one int of extra memory is ever held.
 *
 * KEY INSIGHT
 *   Reservoir sampling turns "choose uniformly from a stream of unknown length"
 *   into O(1) space: keep the k-th item with probability 1/k. Recognise it whenever
 *   you may not store or re-scan the candidates (huge arrays, streams, linked lists).
 *
 * COMPLEXITY
 *   pickRandomIndex   Time O(1)  one bounded random draw
 *   pickIndexOfTarget Time O(n)  one pass over nums;  Space O(1)  one saved index
 *
 * INTERVIEW FOLLOW-UPS
 *   - Many pick calls, few updates? Pre-build Map<value, List<index>> for O(1) picks.
 *   - Pick k distinct indices instead of one -> reservoir of size k.
 *   - Weighted pick (LC 528) -> prefix sums plus binary search, not reservoir.
 *   - Why not rand.nextInt() % n? It skews toward small values when n is not a power of 2.
 *
 * NOTE
 *   Fixed: the file linked LC 398 but only had the uniform-index primitive; the real
 *   reservoir-sampling solution is added as pickIndexOfTarget and run from main().
 *
 * RUN
 *   main() runs 4 cases with a fixed seed, so the printed results are reproducible.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

class RandomPickIndex {

    private final Random rand;

    public RandomPickIndex() {
        this(new Random());
    }

    /** Seeded constructor so tests and demos are reproducible. */
    public RandomPickIndex(long seed) {
        this(new Random(seed));
    }

    private RandomPickIndex(Random rand) {
        this.rand = rand;
    }

    /** Building block: a uniformly random legal index of arr. */
    public int pickRandomIndex(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("cannot pick an index from an empty array");
        }
        return rand.nextInt(arr.length); // bound is exclusive, so 0 .. arr.length - 1
    }

    /** LeetCode 398: a uniformly random index i with nums[i] == target. */
    public int pickIndexOfTarget(int[] nums, int target) {
        int chosen = -1;
        int matchesSeen = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != target) {
                continue;
            }
            matchesSeen++;
            // Keep the k-th match with probability 1/k; that leaves all matches equally likely.
            if (rand.nextInt(matchesSeen) == 0) {
                chosen = i;
            }
        }
        if (chosen < 0) {
            throw new IllegalArgumentException("target not present: " + target);
        }
        return chosen;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        RandomPickIndex picker = new RandomPickIndex(42L); // fixed seed -> reproducible output

        // Case 1 (typical): every draw is a legal index, and every index does get drawn.
        int[] arr = {10, 20, 30, 40, 50, 60};
        int[] hits = new int[arr.length];
        boolean allInRange = true;
        for (int draw = 0; draw < 6000; draw++) {
            int i = picker.pickRandomIndex(arr);
            if (i < 0 || i >= arr.length) {
                allInRange = false;
            } else {
                hits[i]++;
            }
        }
        int leastHit = Integer.MAX_VALUE;
        for (int h : hits) {
            leastHit = Math.min(leastHit, h);
        }
        print("case 1a: 6000 draws all in [0, 6)", allInRange, true);
        print("case 1b: rarest index hit > 800 times", leastHit > 800, true);

        // Case 2 (edge): a single-element array leaves no choice at all.
        int[] single = {7};
        print("case 2: pickRandomIndex([7])", picker.pickRandomIndex(single), 0);

        // Case 3 (typical LC 398): three copies of the target, so three reachable indices.
        int[] nums = {1, 2, 3, 3, 3};
        TreeSet<Integer> seen = new TreeSet<>();
        for (int draw = 0; draw < 3000; draw++) {
            seen.add(picker.pickIndexOfTarget(nums, 3));
        }
        List<Integer> sortedSeen = new ArrayList<>(seen);
        print("case 3: indices picked for target 3", sortedSeen, "[2, 3, 4]");

        // Case 4 (tricky): a target that occurs once must always give the same index.
        boolean alwaysZero = true;
        for (int draw = 0; draw < 100; draw++) {
            alwaysZero &= picker.pickIndexOfTarget(nums, 1) == 0;
        }
        print("case 4: single-occurrence target always index 0", alwaysZero, true);
    }
}
