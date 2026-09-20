/*
 * =====================================================================
 *  Partition Labels                     LeetCode 763 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s of lowercase letters, cut it into as many parts as possible so
 *   that every letter appears in at most one part. Return the sizes of the parts, in
 *   order. The parts must concatenate back to s exactly, so this is a cut, not a
 *   selection: every character lands in exactly one part.
 *
 * EXAMPLE
 *   s = "ababcbacadefegdehijhklij"  ->  [9, 7, 8]
 *        parts are "ababcbaca", "defegde", "hijhklij"
 *   s = "a"                         ->  [1]        single character
 *   s = "abcdef"                    ->  [1,1,1,1,1,1]  all distinct, every cut is legal
 *   s = "aaaa"                      ->  [4]        one letter, one part
 *
 * APPROACH  (last index per character, then merge to a boundary)
 *   1. One pass over s filling lastIndex[c] = the final position where c occurs.
 *   2. Walk i left to right holding two numbers: start (first index of the open part)
 *      and end (the farthest last-index seen among characters taken so far).
 *   3. At each i, end = max(end, lastIndex[s[i]]). Adding a character can only push
 *      the boundary right, never left.
 *   4. When i == end, every character inside [start, end] has all of its occurrences
 *      inside that window. Cut here: record end - start + 1 and set start = i + 1.
 *
 * KEY INSIGHT
 *   The cut is legal exactly when the scan index catches up to the farthest obligation
 *   we have accumulated. Each character is an interval [firstIndex, lastIndex]; the
 *   answer is the merged intervals, and merging is free because we meet them in sorted
 *   start order while scanning. Recognise it as interval merging in disguise: the same
 *   "extend end, close when i == end" loop solves Jump Game II and meeting-room style
 *   chunking. Cutting at the first legal boundary is optimal because a later cut only
 *   ever produces fewer, larger parts.
 *
 * COMPLEXITY
 *   Time  O(n)   two linear passes; the alphabet table is a fixed 26 slots
 *   Space O(1)   the output list aside, only the 26-entry lastIndex array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the actual substrings instead of the sizes.
 *   - Arbitrary Unicode instead of 'a'..'z' (swap the array for a HashMap).
 *   - Maximise the number of parts subject to a maximum part size.
 *   - Related: Max Chunks To Make Sorted (LeetCode 769), same close-when-caught-up idea.
 *
 * RUN
 *   main() runs 4 cases: typical, single character, all distinct, all identical.
 */

import java.util.ArrayList;
import java.util.List;

class PartitionLabels {

    public List<Integer> partitionLabels(String s) {
        List<Integer> result = new ArrayList<>();

        // Step 1: last position of each letter. Later writes overwrite earlier ones,
        // so after the loop each slot holds the FINAL occurrence.
        int[] lastIndex = new int[26];
        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;   // '-a' maps 'a'..'z' onto 0..25
        }

        // Step 2: grow a window until the scan index reaches the farthest obligation.
        int start = 0;
        int end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
            if (i == end) {                     // nothing inside reaches past i: safe to cut
                result.add(end - start + 1);
                start = i + 1;
            }
        }
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        PartitionLabels pl = new PartitionLabels();

        String s = "ababcbacadefegdehijhklij";
        print("case 1 (typical)      ", pl.partitionLabels(s), "[9, 7, 8]");
        print("case 2 (single char)  ", pl.partitionLabels("a"), "[1]");
        print("case 3 (all distinct) ", pl.partitionLabels("abcdef"), "[1, 1, 1, 1, 1, 1]");
        print("case 4 (all identical)", pl.partitionLabels("aaaa"), "[4]");
    }
}
