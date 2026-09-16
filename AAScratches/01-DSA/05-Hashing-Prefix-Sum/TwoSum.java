import java.util.HashMap;

// Array, Hash Table
// https://leetcode.com/problems/two-sum
class TwoSum {

    // for small arrays is best solution
    public int[] twoSumI(int[] nums, int target) {
        for (int i = 1; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                if (target == nums[j] + nums[j - i]) {
                    return new int[]{j, j - i};
                }
            }
        }
        return null;
    }

    public int[] twoSumII(int[] nums, int target) {
        // you can create a hashmap too to see if required number exists without searching and get the index of it through value of the element in key - value pair
        HashMap<Integer,Integer> map = new HashMap<>();
        for(int i = 0 ; i<nums.length ; i++){
            if(map.containsKey(target-nums[i])){
                return new int[] {i,map.get(target-nums[i])};
            }
            map.put(nums[i],i);
        }
        return new int[]{4,4};
    }
}

class PairSum {
    public static int countPairsWithSum(int[] arr, int sum) {
        // HashMap to store the frequency of each element in the array
        HashMap<Integer, Integer> map = new HashMap<>();
        int count = 0;

        // Traverse through the array
        for (int num : arr) {
            // Calculate the complement
            int remSum = sum - num;

            // If the complement exists in the map, then there are pairs
            if (map.containsKey(remSum)) {
                count += map.get(remSum);
            }

            // Add or update the frequency of the current number in the map
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        return count;
    }

    public static void main(String[] args) {
        int[] arr = {1, 5, 7, -1, 5};
        int sum = 6;
        int result = countPairsWithSum(arr, sum);
        System.out.println("Number of pairs with sum " + sum + ": " + result);
    }
}
