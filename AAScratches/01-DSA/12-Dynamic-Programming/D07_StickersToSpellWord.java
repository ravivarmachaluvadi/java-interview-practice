/*
 * =====================================================================
 *  Stickers to Spell Word                         LeetCode 691 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   You are given an array of sticker words and a target word. Each sticker is an infinite
 *   supply: you may use any sticker any number of times, cutting out individual letters from
 *   copies of it. Return the minimum number of sticker copies needed to spell the target,
 *   or -1 if it cannot be spelled. Only lowercase letters appear.
 *
 * EXAMPLE
 *   stickers = ["with","example","science"], target = "thehat"  ->  3
 *       "with" gives t,h; "example" gives e,a; a second "with" gives the remaining t,h
 *   stickers = ["notice","possible"], target = "basicbasic"     -> -1  (no sticker has 'a')
 *   stickers = ["a","b"], target = "aab"                        ->  3  (letters repeat)
 *   stickers = ["a"], target = "ab"                             -> -1  (partial progress, dead end)
 *
 * APPROACH  (memoised search over the remaining-letter signature)
 *   1. Order inside the target never matters, only how many of each letter are still missing.
 *      So the state is an int[26] count of what is left to cover.
 *   2. Serialise that array with Arrays.toString and use it as a HashMap key. The map is the
 *      DP table: every distinct remaining-signature is solved once.
 *   3. Base case: the all-zero signature costs 0 stickers.
 *   4. Transition: try each sticker, subtract the letters it can actually cover, and recurse.
 *      Skip a sticker that covers nothing (it would loop forever on the same state).
 *      Answer for this state = 1 + min over all useful stickers; IMPOSSIBLE if none help.
 *
 * KEY INSIGHT
 *   When the "index" of a classic DP has no meaning, invent a state out of the multiset of
 *   what remains and hash it. The signature is the DP dimension. Every applied sticker strictly
 *   reduces the total remaining count, which is why the recursion terminates without a depth
 *   parameter.
 *
 * COMPLEXITY
 *   Time  O(states * S * L) where S is the sticker count and L the average sticker length.
 *         States are bounded by the product of (count(c) + 1) over the target letters.
 *   Space O(states * 26) for the memo keys, plus the recursion stack (depth <= target length).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Classic bitmask form: when the target has no repeated letters, the state is a subset of
 *     its positions, giving O(2^n * S * L) with an int key instead of a string.
 *   - Prune hard by always covering the FIRST still-missing letter: skip any sticker that does
 *     not contain it. Same answer, far fewer branches.
 *   - Return which stickers were used: store the chosen sticker per state and walk back.
 *   - Why is greedy (take the sticker covering the most letters) wrong? Show a counterexample.
 *
 * FIXED
 *   The original had no base case for the empty target. An all-zero state fell through the
 *   sticker loop and returned -1, which the caller then read as "cost -1", producing 0 for a
 *   state that really needed one more sticker. The sample above printed 2 instead of 3, and
 *   genuinely impossible targets such as ["a"] / "ab" printed 0. Impossible is now a distinct
 *   sentinel and is only turned into -1 at the top level.
 *
 * RUN
 *   main() runs 4 cases (typical, impossible, repeated letters, dead-end),
 *   printing actual vs expected.
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class StickersToSpellWord {

    /** Internal marker for "this state cannot be completed"; never returned to the caller. */
    private static final int IMPOSSIBLE = Integer.MAX_VALUE;

    // Minimum number of sticker copies needed to spell the target, or -1 if impossible.
    public static int minStickers(String[] stickers, String target) {
        int[] remaining = new int[26];
        for (char c : target.toCharArray()) {
            remaining[c - 'a']++;
        }

        Map<String, Integer> memo = new HashMap<>();
        int best = helper(stickers, remaining, memo);
        return best == IMPOSSIBLE ? -1 : best;
    }

    /** Fewest stickers that clear the letters still counted in remaining, or IMPOSSIBLE. */
    private static int helper(String[] stickers, int[] remaining, Map<String, Integer> memo) {
        if (isEmpty(remaining)) {
            return 0;                       // base case: nothing left to spell
        }

        String key = Arrays.toString(remaining);   // the signature IS the DP state
        Integer cached = memo.get(key);
        if (cached != null) {
            return cached;
        }

        int best = IMPOSSIBLE;
        for (String sticker : stickers) {
            int[] next = remaining.clone();
            boolean covered = false;

            // Take from this sticker only the letters we still need.
            for (char c : sticker.toCharArray()) {
                if (next[c - 'a'] > 0) {
                    next[c - 'a']--;
                    covered = true;
                }
            }

            // A sticker that covers nothing leaves the state unchanged: recursing would not end.
            if (!covered) {
                continue;
            }

            int sub = helper(stickers, next, memo);
            if (sub != IMPOSSIBLE) {
                best = Math.min(best, sub + 1);    // +1 for the copy we just used
            }
        }

        memo.put(key, best);
        return best;
    }

    private static boolean isEmpty(int[] counts) {
        for (int c : counts) {
            if (c > 0) return false;
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): "with" is needed twice, which is what makes the answer 3 and not 2.
        String[] stickers1 = {"with", "example", "science"};
        print("case 1 thehat       ", minStickers(stickers1, "thehat"), 3);

        // Case 2 (impossible): no sticker contains 'a'.
        String[] stickers2 = {"notice", "possible"};
        print("case 2 basicbasic   ", minStickers(stickers2, "basicbasic"), -1);

        // Case 3 (edge): single-letter stickers, so the answer is just the target length.
        String[] stickers3 = {"a", "b"};
        print("case 3 aab          ", minStickers(stickers3, "aab"), 3);

        // Case 4 (tricky): real progress is possible but the target never completes.
        String[] stickers4 = {"a"};
        print("case 4 dead end ab  ", minStickers(stickers4, "ab"), -1);
    }
}
