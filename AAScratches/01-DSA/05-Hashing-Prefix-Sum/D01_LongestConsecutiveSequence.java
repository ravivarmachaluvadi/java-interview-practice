/*
 * =====================================================================
 *  Longest Consecutive Sequence                LeetCode 128 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an unsorted int array, return the length of the longest run of consecutive
 *   integers (values, not positions). Must run in O(n): sorting is the obvious O(n log n)
 *   answer and the interviewer will ask you to beat it. Duplicates may appear.
 *
 * EXAMPLE
 *   nums = [100, 4, 200, 1, 3, 2]    ->  4    the run 1,2,3,4
 *   nums = []                        ->  0
 *   nums = [1, 2, 0, 1]              ->  3    duplicate 1 ignored, run 0,1,2
 *   nums = [-3, -2, -1, 5, 7, 6]     ->  3    both -3..-1 and 5..7 have length 3
 *
 * APPROACH  (HashSet + expand only from sequence heads)
 *   1. Put every value in a HashSet (dedups and gives O(1) contains).
 *   2. For each value num in the set: if num - 1 is ALSO in the set, skip it; it is not the
 *      start of a run and will be counted when its run's head is visited.
 *   3. If num - 1 is absent, num is a head: walk num+1, num+2, ... while present, counting.
 *   4. Track the maximum run length.
 *
 * KEY INSIGHT
 *   The nested loop looks O(n^2) but is amortized O(n): each value is walked over at most
 *   once, because only the head of each run starts a walk and the walks are disjoint.
 *   The "no predecessor -> this is a head" check is the whole trick. Recognise it whenever
 *   you need "chains" out of a set without sorting.
 *
 * COMPLEXITY
 *   Time  O(n)  n set inserts plus every element visited by exactly one inner walk
 *   Space O(n)  the HashSet
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not sort? O(n log n); fine as a baseline, but state the O(n) version too.
 *   - Return the actual sequence, not its length: remember the head of the best run.
 *   - Union-Find alternative: union num with num+1, answer is the largest component.
 *   - Streaming / very large n: a HashMap of run boundaries (num -> run length at the
 *     ends) updates in O(1) per insert without rescanning.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, duplicates, negatives with a tie) and prints
 *   actual vs expected.
 */
import java.util.HashSet;
import java.util.Set;

class LongestConsecutiveSequence {

    public static int longestConsecutive(int[] nums) {
        Set<Integer> values = new HashSet<>();
        for (int num : nums) values.add(num);

        int longest = 0;
        for (int num : values) {
            // only start a walk from the head of a run; non-heads are covered by their head
            if (values.contains(num - 1)) continue;

            int current = num;
            int length = 1;
            while (values.contains(current + 1)) {
                current++;
                length++;
            }
            longest = Math.max(longest, length);
        }
        return longest;
    }

    private static void print(String label, int[] nums, int expected) {
        System.out.println(label + ": " + longestConsecutive(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", new int[]{100, 4, 200, 1, 3, 2}, 4);
        print("case 2 empty     ", new int[]{}, 0);
        print("case 3 duplicates", new int[]{1, 2, 0, 1}, 3);
        print("case 4 negatives ", new int[]{-3, -2, -1, 5, 7, 6}, 3);
    }
}
