/*
 * =====================================================================
 *  Majority Element                       LeetCode 169 | Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array nums of size n, return the element that appears more than n / 2 times.
 *   LeetCode guarantees such an element exists. The follow-up drops that guarantee and asks
 *   you to return -1 when there is no majority. Both must run in O(n) time and O(1) space.
 *
 * EXAMPLE
 *   nums = [3, 2, 3]                ->  3
 *   nums = [2, 2, 1, 1, 1, 2, 2]    ->  2
 *   nums = [1]                      ->  1     (single element is its own majority)
 *   nums = [1, 2, 3]  (verified)    ->  -1    (plain version returns 3, which is meaningless)
 *   nums = [1, 1, 2, 2] (verified)  ->  -1    (exactly n / 2 is NOT a majority; bound is strict)
 *
 * APPROACH  (Boyer-Moore voting)
 *   1. Keep a candidate and a count, both starting empty (count = 0).
 *   2. For each num: if count == 0, adopt num as the candidate.
 *   3. Then count += 1 if num == candidate, else count -= 1.
 *   4. The candidate left at the end is the only value that CAN be the majority.
 *   5. Verified version: count the candidate's occurrences in a second pass; return it
 *      only if occurrences > n / 2, else -1.
 *
 * KEY INSIGHT
 *   Every mismatch cancels one occurrence of the candidate against one non-candidate.
 *   A true majority appears more often than all other values combined, so it can never be
 *   cancelled to zero and must survive the sweep. State this carefully in an interview:
 *   the algorithm finds the only POSSIBLE majority, not a confirmed one. Without the
 *   guarantee you must verify, or you will return garbage on inputs like [1, 2, 3].
 *
 * COMPLEXITY
 *   Time  O(n)  one pass (two passes for the verified version)
 *   Space O(1)  two scalars
 *
 * INTERVIEW FOLLOW-UPS
 *   - No guarantee that a majority exists? Add the verification pass (majorityElementVerified).
 *   - Elements appearing more than n / 3 times (LeetCode 229)? At most two can exist; run
 *     the same voting with two candidates and two counts, then verify both.
 *   - Alternatives and why they lose: HashMap O(n) space; sort and take nums[n / 2]
 *     O(n log n) time; bit counting per position O(32 n) but also O(1) space.
 *
 * RUN
 *   main() runs 6 plain cases and 5 verified cases and prints actual vs expected.
 */

class MajorityElement {

    /** Boyer-Moore sweep. Assumes a majority exists; otherwise the result is meaningless. */
    public static int majorityElement(int[] nums) {
        int candidate = 0;
        int count = 0;
        for (int num : nums) {
            if (count == 0) {
                candidate = num;       // previous candidate fully cancelled; start over
            }
            count += (num == candidate) ? 1 : -1;
        }
        return candidate;
    }

    /** Same sweep plus a verification pass. Returns -1 when no majority exists. */
    public static int majorityElementVerified(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        int candidate = majorityElement(nums);

        int occurrences = 0;
        for (int num : nums) {
            if (num == candidate) {
                occurrences++;
            }
        }
        return occurrences > nums.length / 2 ? candidate : -1;   // strict: n / 2 is not enough
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        System.out.println("-- plain (majority guaranteed) --");
        print("case 1 typical          ", majorityElement(new int[]{3, 2, 3}), 3);
        print("case 2 interleaved      ", majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}), 2);
        print("case 3 single           ", majorityElement(new int[]{1}), 1);
        print("case 4 majority at end  ", majorityElement(new int[]{6, 5, 5}), 5);
        print("case 5 negatives        ", majorityElement(new int[]{-1, -1, 2, -1}), -1);
        print("case 6 NO majority      ", majorityElement(new int[]{1, 2, 3}), "3 (meaningless)");

        System.out.println("-- verified (no guarantee) --");
        print("case 7 no majority      ", majorityElementVerified(new int[]{1, 2, 3}), -1);
        print("case 8 exactly n/2      ", majorityElementVerified(new int[]{1, 1, 2, 2}), -1);
        print("case 9 agrees with plain",
                majorityElementVerified(new int[]{2, 2, 1, 1, 1, 2, 2}), 2);
        print("case 10 empty           ", majorityElementVerified(new int[]{}), -1);
        print("case 11 null            ", majorityElementVerified(null), -1);
    }
}
