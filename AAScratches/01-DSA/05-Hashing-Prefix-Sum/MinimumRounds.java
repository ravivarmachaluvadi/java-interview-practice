/**
 * Problem: Given an array of task IDs, determine the minimum number of rounds required to complete all tasks.
 * Each round must consist of 2 or 3 identical tasks; a single task cannot form a round.
 *
 * Approach:
 * Count occurrences of each task using a hash map. For each count:
 * - If count == 1 → impossible, return -1.
 * - Otherwise compute rounds as ceil(count / 3), adjusting for remainders by replacing one group of 3 with two groups of 2 when needed.
 *
 * Time Complexity: O(n) – single pass to build the map and another over distinct tasks.
 * Space Complexity: O(k) – where k is the number of unique task IDs (hash map storage).
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class MinimumRounds {

    public static int minimumRoundsV1(int[] tasks) {
        Arrays.sort(tasks);
        int si = 0;
        int ei = 0;
        int n = tasks.length;
        int day = 0;
        while (ei < n) {
            // a a a b
            do ei++;
            while (ei < n && tasks[ei - 1] == tasks[ei]);

            int count = ei - si;
            si = ei;
            if (count == 1) return -1;
            day += count / 3;
            if (count % 3 != 0) day++;
        }
        return day;
    }

    public static int minimumRounds(int[] tasks) {
        // Count the frequency of each task
        Map<Integer, Integer> taskCount = new HashMap<>();
        for (int task : tasks) {
            taskCount.put(task, taskCount.getOrDefault(task, 0) + 1);
        }

        int totalRounds = 0;

        // Process each task count
        for (int count : taskCount.values()) {
            if (count == 1) {
                // If the task appears only once, we can't complete it in a valid round
                return -1;
            }
            // Add the number of rounds required for this task's count
            if (count % 3 == 0) {
                totalRounds += count / 3;
            } else if (count % 3 == 1 || count % 3 == 2) {
                // If the count modulo 3 is 1, take one batch of 3, then the rest as batches of 2
                totalRounds += (count / 3) + 1; // One batch of 3, then 2 batches of 2
            }
        }
        return totalRounds;
    }

    public static void main(String[] args) {
        int[] tasks1 = {1, 1, 2, 2, 2, 2, 3, 3, 3};
        System.out.println("Minimum rounds for tasks1: " + minimumRounds(tasks1)); // Output: 3

        int[] tasks2 = {1, 1, 1, 1};
        System.out.println("Minimum rounds for tasks2: " + minimumRounds(tasks2)); // Output: 2

        int[] tasks3 = {1};
        System.out.println("Minimum rounds for tasks3: " + minimumRounds(tasks3)); // Output: -1
    }
}
