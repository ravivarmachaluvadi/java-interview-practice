/*
 * =====================================================================
 *  Number of Zero-Filled Subarrays                 LeetCode 2348 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, count the contiguous non-empty subarrays whose
 *   every element is 0. n can be 1e5, so an all-zero array yields n*(n+1)/2 ~ 5e9
 *   subarrays: the answer must be a long.
 *
 * EXAMPLE
 *   nums = [1, 3, 0, 0, 2, 0, 0, 4]  ->  6   four [0] + two [0,0]
 *   nums = [0, 0, 0, 2, 0, 0]        ->  9   run of 3 gives 6, run of 2 gives 3
 *   nums = [2, 10, 2019]             ->  0   no zeros at all
 *   nums = []                        ->  0   empty input
 *
 * APPROACH  (count subarrays ending at each index)
 *   1. Scan left to right, keeping streak = length of the zero run that ends at i.
 *   2. On a 0, streak++; on any other value, streak = 0.
 *   3. Add streak to the total at every step: exactly streak zero-filled subarrays
 *      end at index i (one of each length 1..streak).
 *
 * KEY INSIGHT
 *   Do not enumerate subarrays (O(n^2)). Ask "how many valid subarrays END at i?"
 *   and sum those counts. For a zero run of length L this sums 1+2+...+L =
 *   L*(L+1)/2, computed incrementally with one extra line on a streak counter.
 *   Pattern to recognise: "count subarrays with property P" -> count by right endpoint.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass Space O(1)  two counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count subarrays made of one repeated value (reset when nums[i] != nums[i-1]).
 *   - Count subarrays whose SUM is 0: a streak no longer works, use prefix sums + map.
 *   - Why long? Explain the n*(n+1)/2 bound for an all-zero array of length 1e5.
 *
 * RUN
 *   main() runs 4 cases (typical, all zeros, no zeros, empty) and prints actual vs expected.
 */
class NumberOfZeroFilledSubarrays {

    public static long zeroFilledSubarray(int[] nums) {
        long total = 0;
        long streak = 0; // length of the zero run ending at the current index
        for (int num : nums) {
            streak = (num == 0) ? streak + 1 : 0;
            total += streak; // 'streak' zero-filled subarrays end here
        }
        return total;
    }

    public static void main(String[] args) {
        print("case 1 typical  ", zeroFilledSubarray(new int[]{1, 3, 0, 0, 2, 0, 0, 4}), 6);
        print("case 2 runs 3+2 ", zeroFilledSubarray(new int[]{0, 0, 0, 2, 0, 0}), 9);
        print("case 3 no zeros ", zeroFilledSubarray(new int[]{2, 10, 2019}), 0);
        print("case 4 empty    ", zeroFilledSubarray(new int[]{}), 0);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
