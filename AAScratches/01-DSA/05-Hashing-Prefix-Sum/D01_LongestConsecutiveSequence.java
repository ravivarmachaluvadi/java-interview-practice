import java.util.HashSet;

// https://leetcode.com/problems/longest-consecutive-sequence
class LongestConsecutiveSequence {
    public int longestConsecutive(int[] nums) {
        HashSet<Integer> numSet = new HashSet<>();
        for (int num : nums) numSet.add(num);

        int longestStreak = 0;
        for (int num : numSet) {
            // number before not contains means series starting with currNum
            if (!numSet.contains(num - 1)) {
                int currentNum = num;
                int currentStreak = 1;
                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentStreak++;
                }
                longestStreak = Math.max(longestStreak, currentStreak);
            }
        }
        return longestStreak;
    }

    public static void main(String[] args) {
        LongestConsecutiveSequence lcs = new LongestConsecutiveSequence();
        int[] nums = {100, 4, 200, 1, 3, 2};

        int result = lcs.longestConsecutive(nums);
        System.out.println("The length of the longest consecutive sequence is: " + result);
        // 4
    }
}
