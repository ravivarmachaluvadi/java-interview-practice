/*
 * =====================================================================
 *  Fruit Into Baskets                                 LeetCode 904 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   fruits[i] is the type of the tree at position i. You walk left to right, picking one
 *   fruit from every tree you pass, but you carry only two baskets and each basket holds one
 *   type. Return the most fruit you can collect from one contiguous run of trees.
 *   In other words: longest subarray with at most 2 distinct values.
 *
 * EXAMPLE
 *   [1, 2, 1]                            ->  3   whole array has only two types
 *   [0, 1, 2, 2]                         ->  3   [1, 2, 2]
 *   [1, 2, 3, 2, 2]                      ->  4   [2, 3, 2, 2]
 *   [3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4]    ->  5   [1, 2, 1, 1, 2]
 *   []                                   ->  0   no trees
 *   [5]                                  ->  1   single tree
 *
 * APPROACH  (at most two distinct, count map)
 *   1. Keep a map type -> how many of that type are inside the window [left, right].
 *   2. Grow right by one: bump the count of fruits[right].
 *   3. While the map has more than 2 keys the window is invalid: decrement fruits[left],
 *      remove the key when its count hits 0, move left forward.
 *   4. The window is now valid, so right - left + 1 is a candidate for the answer.
 *
 * KEY INSIGHT
 *   This is the "at most K violations" window where the thing being counted is
 *   DISTINCTNESS, so a single counter is not enough: you need a frequency map whose
 *   size() tells you how many distinct types are present. Replace 2 with K and the same
 *   code solves "longest subarray with at most K distinct" (LeetCode 340 / 992).
 *
 * COMPLEXITY
 *   Time  O(n)  each index enters the window once and leaves once
 *   Space O(1)  the map never holds more than 3 keys (at most K + 1 in general)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Generalise to at most K distinct (same code, replace the 2).
 *   - Count subarrays with EXACTLY K distinct: atMost(K) - atMost(K - 1).
 *   - Can you do it without a map? Yes for K = 2 by tracking the last two types and the
 *     start of the most recent run, but the map version generalises and is easier to defend.
 *
 * RUN
 *   main() runs 6 cases (typical, edge, tricky) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class FruitIntoBaskets {

    /** Longest window of fruits containing at most two distinct types. */
    public static int totalFruit(int[] fruits) {
        Map<Integer, Integer> countByType = new HashMap<>();
        int maxFruits = 0;
        int left = 0;

        for (int right = 0; right < fruits.length; right++) {
            // merge: insert 1 if absent, otherwise add 1 to the existing count
            countByType.merge(fruits[right], 1, Integer::sum);

            // more than two types in the window -> shrink from the left until valid
            while (countByType.size() > 2) {
                int leftFruit = fruits[left];
                countByType.put(leftFruit, countByType.get(leftFruit) - 1);
                if (countByType.get(leftFruit) == 0) {
                    countByType.remove(leftFruit); // key must go so size() reflects distinct types
                }
                left++;
            }

            maxFruits = Math.max(maxFruits, right - left + 1);
        }
        return maxFruits;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [1,2,1]", totalFruit(new int[]{1, 2, 1}), 3);
        print("case 2 [0,1,2,2]", totalFruit(new int[]{0, 1, 2, 2}), 3);
        print("case 3 [1,2,3,2,2]", totalFruit(new int[]{1, 2, 3, 2, 2}), 4);
        print("case 4 [3,3,3,1,2,1,1,2,3,3,4]",
                totalFruit(new int[]{3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4}), 5);
        print("case 5 empty", totalFruit(new int[]{}), 0);
        print("case 6 single [5]", totalFruit(new int[]{5}), 1);
    }
}
