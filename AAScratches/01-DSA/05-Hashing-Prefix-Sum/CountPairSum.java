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
