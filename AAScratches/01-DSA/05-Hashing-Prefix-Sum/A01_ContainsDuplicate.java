/*
 * =====================================================================
 *  Contains Duplicate                                  LeetCode 217 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, return true if any value appears at least twice,
 *   and false if every element is distinct. The array may be empty.
 *
 * EXAMPLE
 *   nums = [1, 2, 3, 1]                    ->  true    because 1 appears twice
 *   nums = [1, 2, 3, 4]                    ->  false   all distinct
 *   nums = []                              ->  false   nothing to repeat
 *   nums = [1, 1, 1, 3, 3, 4, 3, 2, 4, 2]  ->  true
 *
 * APPROACH  (HashSet membership check)
 *   1. Walk the array once, keeping a HashSet of values seen so far.
 *   2. Before adding the current value, ask the set "have I seen this?".
 *      If yes, we found a duplicate: return true immediately.
 *   3. If the loop finishes without a hit, every value was unique: return false.
 *   A second method, containsDuplicateBySorting, shows the O(1)-extra-space
 *   alternative: sort, then any duplicate must sit next to its twin.
 *
 * KEY INSIGHT
 *   Trade O(n) memory for O(1) average lookup. "Have I seen this before?" is the
 *   most basic hashing reflex; every other file in this folder builds on it.
 *   Early return matters: you stop at the FIRST duplicate, not after scanning all.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, O(1) average per set operation
 *   Space O(n)  the set may hold every element when there are no duplicates
 *   (sorting variant: O(n log n) time, O(1) extra space if sort is in place)
 *
 * INTERVIEW FOLLOW-UPS
 *   - No extra memory allowed? Sort first, then compare neighbours (n log n).
 *   - Values bounded to [0, n)? Use a boolean[] or mark visited by negating in place.
 *   - Duplicate within distance k (LC 219)? Same set, but evict indices older than k.
 *   - Streaming input? The set approach works unchanged; sorting does not.
 *
 * RUN
 *   main() runs 4 cases (typical, all-distinct, empty, many repeats) through both
 *   methods and prints actual vs expected.
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class ContainsDuplicate {

    // Primary approach: HashSet of values seen so far.
    public static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (seen.contains(num)) {
                return true; // second sighting -> duplicate exists, stop early
            }
            seen.add(num);
        }
        return false;
    }

    // Alternative: sort, then any duplicate must be adjacent. O(n log n) time, O(1) extra
    // space (the copy here is only so the caller's array is not mutated).
    public static boolean containsDuplicateBySorting(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] == sorted[i - 1]) {
                return true;
            }
        }
        return false;
    }

    private static void check(String label, int[] nums, boolean expected) {
        System.out.println(label + " " + Arrays.toString(nums)
                + " -> set: " + containsDuplicate(nums)
                + ", sort: " + containsDuplicateBySorting(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 (typical):     ", new int[]{1, 2, 3, 1}, true);
        check("case 2 (all distinct):", new int[]{1, 2, 3, 4}, false);
        check("case 3 (empty):       ", new int[]{}, false);
        check("case 4 (many repeats):", new int[]{1, 1, 1, 3, 3, 4, 3, 2, 4, 2}, true);
    }
}
