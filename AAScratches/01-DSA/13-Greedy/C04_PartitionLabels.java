import java.util.*;
/**
 * Input: s = "ababcbacadefegdehijhklij"
 * <p>
 * Output: [9,7,8]
 * <p>
 * Explanation:
 * <p>
 * The partition is "ababcbaca", "defegde", "hijhklij".
 */
// https://leetcode.com/problems/partition-labels/description/
class PartitionLabels {
    public List<Integer> partitionLabels(String s) {
        List<Integer> result = new ArrayList<>();
        // stores the last occurrence of each character
        int[] lastIndexes = new int[26];

        // Step 1: Find the last occurrence of each character
        for (int i = 0; i < s.length(); i++)
            // -'a' not zero
            lastIndexes[s.charAt(i) - 'a'] = i;

        // Step 2: Traverse the string and partition it
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            // find the farthest boundary between end and lastIndex of currChar
            end = Math.max(end, lastIndexes[s.charAt(i) - 'a']);
            // When the current index matches the end boundary, create a partition
            if (i == end) {
                result.add(end - start + 1); // size of the partition
                start = i + 1; // move to the next partition
            }
        }
        return result;
    }

    public static void main(String[] args) {
        PartitionLabels pl = new PartitionLabels();
        String s = "ababcbacadefegdehijhklij";
        System.out.println(pl.partitionLabels(s)); // Output: [9, 7, 8]
    }
}
