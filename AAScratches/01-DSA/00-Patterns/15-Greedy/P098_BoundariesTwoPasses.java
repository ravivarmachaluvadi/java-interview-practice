/*
 * =====================================================================
 *  P098 Greedy with Boundaries and Two Passes   Canonical LC 763 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 763, Partition Labels)
 *   Split s into as many parts as possible so each letter appears in at most one part.
 *   Return the part sizes in order.
 *
 * EXAMPLE
 *   "ababcbacadefegdehijhklij"  ->  [9, 7, 8]
 *   "eccbbbbdec"                ->  [10]
 *
 * RECOGNIZE WHEN
 *   - "each letter / value must stay inside one part", "largest number after one swap":
 *     precompute where each value LAST appears, then one greedy pass.
 *   - Constraints from BOTH neighbours (rating higher than the left and the right): satisfy
 *     the left side in one pass and the right side in a second pass.
 *   Not this if: you need the aggregate of everything left / right of i ->
 *   P008_PrefixSuffixTwoPasses; you must remove letters to get the smallest result ->
 *   P039_GreedyRemovalStack.
 *
 * TEMPLATE
 *   last[c] = last index of c                       // one pre-pass
 *   end = 0, start = 0
 *   for i, c in s:
 *       end = max(end, last[c])                     // this part must reach at least here
 *       if i == end: close the part [start, end]; start = i + 1
 *   two-sided: left-to-right pass for the left rule, right-to-left pass for the right rule,
 *              combine with max
 *
 * APPROACH
 *   1. Record each letter's last position.
 *   2. Walk the string; the current part must extend to the last occurrence of every letter
 *      seen in it. When i reaches that end, nothing inside appears later: cut.
 *
 * KEY INSIGHT
 *   One pre-pass turns a global constraint ("this letter appears again later") into a
 *   number you can max() as you go. With two-sided constraints, each pass satisfies one side
 *   without breaking the other, and taking the max keeps both.
 *
 * COMPLEXITY
 *   Time O(n), space O(alphabet) (O(n) for the candy array).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 670  Maximum Swap             last index of each digit; at the first digit
 *                                            with a larger digit later, swap with its last copy
 *   [coded] LC 135  Candy                    left pass: more than the left if rated higher;
 *                                            right pass: more than the right; take the max
 *           LC 2405 Optimal String Partition cut whenever a letter repeats inside the part
 *           LC 1525 Good Ways to Split       distinct counts from the left and from the right
 *           LC 2100 Good Days to Rob a Bank  non-increasing run lengths from both sides
 *
 * PITFALLS
 *   - Cut only when i == end, not when the current letter's last index is i.
 *   - LC 670: swap with the LAST occurrence of the bigger digit (9973 vs 9937).
 *   - LC 135: equal ratings impose nothing; only strictly higher neighbours do.
 *
 * DEEP DIVE
 *   C05_PartitionLabels, C04_MaximumSwap, D01_Candy (13-Greedy)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.List;

class BoundariesTwoPasses {

    // Canonical LC 763.
    static List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) {
            last[s.charAt(i) - 'a'] = i;
        }
        List<Integer> sizes = new ArrayList<>();
        int start = 0;
        int end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) {
                sizes.add(end - start + 1);
                start = i + 1;
            }
        }
        return sizes;
    }

    // LC 670: at most one swap of two digits to get the largest number.
    static int maximumSwap(int num) {
        char[] d = String.valueOf(num).toCharArray();
        int[] last = new int[10];
        for (int i = 0; i < d.length; i++) {
            last[d[i] - '0'] = i;
        }
        for (int i = 0; i < d.length; i++) {
            for (int digit = 9; digit > d[i] - '0'; digit--) {
                if (last[digit] > i) {
                    char t = d[i];
                    d[i] = d[last[digit]];
                    d[last[digit]] = t;
                    return Integer.parseInt(new String(d));
                }
            }
        }
        return num;
    }

    // LC 135: everyone gets at least 1; a higher rating than a neighbour means more candy.
    static int candy(int[] ratings) {
        int n = ratings.length;
        int[] c = new int[n];
        java.util.Arrays.fill(c, 1);
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                c[i] = c[i - 1] + 1;               // left rule
            }
        }
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                c[i] = Math.max(c[i], c[i + 1] + 1);   // right rule, keeping the left rule
            }
        }
        int total = 0;
        for (int x : c) {
            total += x;
        }
        return total;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 763 three parts", partitionLabels("ababcbacadefegdehijhklij"), "[9, 7, 8]");
        check("LC 763 one part", partitionLabels("eccbbbbdec"), "[10]");
        check("LC 763 all distinct", partitionLabels("abc"), "[1, 1, 1]");

        check("LC 670 2736", maximumSwap(2736), 7236);
        check("LC 670 9973 already max", maximumSwap(9973), 9973);
        check("LC 670 98368 last copy", maximumSwap(98368), 98863);
        check("LC 670 1993", maximumSwap(1993), 9913);

        check("LC 135 [1,0,2]", candy(new int[]{1, 0, 2}), 5);
        check("LC 135 [1,2,2]", candy(new int[]{1, 2, 2}), 4);
        check("LC 135 [1,3,2,2,1]", candy(new int[]{1, 3, 2, 2, 1}), 7);
    }
}
