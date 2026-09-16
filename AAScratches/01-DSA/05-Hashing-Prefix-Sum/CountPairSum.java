/**
 * Counts the number of unordered pairs in an integer array whose sum equals a given target.
 *
 * The algorithm iterates through the array once, using a hash map to store the frequency
 * of each element seen so far. For each element x, it looks up how many times (target - x)
 * has appeared earlier and adds that count to the total. Then it updates the frequency of x.
 *
 * Time Complexity: O(n), where n is the length of the array.
 * Space Complexity: O(n) for the hash map storing frequencies.
 */
import java.util.HashMap;

class CountPairSum {
    public static int countPairs(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int count = 0;

        for (int num : nums) {
            int remSum = target - num;
            if (map.containsKey(remSum)) {
                count += map.get(remSum);
            }

            // *** Add or update the frequency of the current number in the map
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        return count;
    }

    public static void main(String[] args) {
        int[] nums = {1, 5, 7, -1, 5};
        int target = 6;
        System.out.println("Number of pairs: " + countPairs(nums, target));
    }
}
