/*
 * =====================================================================
 *  P006 Boyer-Moore Voting   Canonical LC 169 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 169, Majority Element)
 *   Return the element that appears more than n / 2 times in nums. It is guaranteed to
 *   exist. Follow-up: O(n) time and O(1) space.
 *
 * EXAMPLE
 *   [3, 2, 3]              ->  3
 *   [2, 2, 1, 1, 1, 2, 2]  ->  2
 *
 * RECOGNIZE WHEN
 *   - "appears more than n / 2 (or n / 3, or n / k) times" with O(1) space demanded.
 *   - A streaming input you can read once, where a frequency map is too big.
 *   Not this if: you need the top-k most frequent in general -> P043_TopK; extra space is
 *   fine -> a HashMap count (P021_FrequencyCanonicalKey) is simpler and needs no verify.
 *
 * TEMPLATE
 *   candidate = none, count = 0
 *   for x in nums:
 *       if count == 0: candidate = x
 *       count += (x == candidate) ? 1 : -1
 *   // n/3 version: two candidates, two counts; then a second pass to verify both
 *
 * APPROACH
 *   1. Pair every occurrence of the candidate with a different value and cancel both.
 *   2. When the count hits 0 the prefix so far has no majority; start over at the next x.
 *   3. A true majority cannot be fully cancelled, so it is the survivor.
 *
 * KEY INSIGHT
 *   Removing two DIFFERENT values never changes which value is the majority of what is
 *   left. With k - 1 candidates the same cancelling finds every value above n / k, but
 *   then the survivors must be counted again because they are only candidates.
 *
 * COMPLEXITY
 *   Time O(n) (two passes for n / 3), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 229  Majority Element II      two candidates; decrement both on a third value;
 *                                            verify with a second pass
 *           LC 1150 Majority in Sorted Array binary search the first and last index instead
 *           LC 2404 Most Frequent Even       no majority guarantee -> plain HashMap count
 *
 * PITFALLS
 *   - Without the guarantee, verify the candidate ([1, 2, 3] leaves 3 but has no majority).
 *   - n / 3: check "x == c1" and "x == c2" BEFORE "count == 0", or one value fills both.
 *
 * DEEP DIVE
 *   B09_MajorityElement (01-Arrays)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BoyerMooreVoting {

    // Canonical LC 169.
    static int majorityElement(int[] nums) {
        int candidate = 0;
        int count = 0;
        for (int x : nums) {
            if (count == 0) {
                candidate = x;
            }
            count += x == candidate ? 1 : -1;
        }
        return candidate;
    }

    // LC 229: at most two values can exceed n / 3. Find two candidates, then verify.
    static List<Integer> majorityElementII(int[] nums) {
        int c1 = 0;
        int c2 = 1;                      // distinct dummies so they never start equal
        int n1 = 0;
        int n2 = 0;
        for (int x : nums) {
            if (x == c1) {
                n1++;
            } else if (x == c2) {
                n2++;
            } else if (n1 == 0) {
                c1 = x;
                n1 = 1;
            } else if (n2 == 0) {
                c2 = x;
                n2 = 1;
            } else {
                n1--;                    // a third value cancels one of each
                n2--;
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int c : new int[]{c1, c2}) {
            int occurrences = 0;
            for (int x : nums) {
                if (x == c) {
                    occurrences++;
                }
            }
            if (occurrences > nums.length / 3 && !result.contains(c)) {
                result.add(c);
            }
        }
        Collections.sort(result);
        return result;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 169 [3,2,3]", majorityElement(new int[]{3, 2, 3}), 3);
        check("LC 169 [2,2,1,1,1,2,2]", majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}), 2);
        check("LC 169 [7] single", majorityElement(new int[]{7}), 7);

        check("LC 229 [3,2,3]", majorityElementII(new int[]{3, 2, 3}), "[3]");
        check("LC 229 [1]", majorityElementII(new int[]{1}), "[1]");
        check("LC 229 [1,2]", majorityElementII(new int[]{1, 2}), "[1, 2]");
        check("LC 229 [1,2,3] none above n/3", majorityElementII(new int[]{1, 2, 3}), "[]");
        check("LC 229 [2,2,1,1,1,3,3,3,3]",
                majorityElementII(new int[]{2, 2, 1, 1, 1, 3, 3, 3, 3}), "[3]");
    }
}
