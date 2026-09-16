import java.util.HashMap;
import java.util.Map;

// 904. Fruit Into Baskets
// https://leetcode.com/problems/fruit-into-baskets/description/
class FruitIntoBaskets {
    // max run of contagious fruits length
    // two pointer or sliding window approach
    public static int totalFruit(int[] fruits) {
        Map<Integer, Integer> count = new HashMap<>();
        int maxFruits = 0;
        int left = 0;

        for (int right = 0; right < fruits.length; right++) {
            // Merge function explanation
            //It adds a key-value pair if the key is not already in the map.
            //If the key already exists, it uses a function
            //you give (like add, sum, concatenate, etc.)
            //to combine the old value with the new value.
            count.merge(fruits[right], 1, Integer::sum);

            while (count.size() > 2) {
                int leftFruit = fruits[left];
                count.put(leftFruit, count.get(leftFruit) - 1);
                if (count.get(leftFruit) == 0) {
                    count.remove(leftFruit);
                }
                left++;
            }
            // nothing but max length
            maxFruits = Math.max(maxFruits, right - left + 1);
        }

        return maxFruits;
    }

    public static void main(String[] args) {
        // Sample test cases
        int[][] testCases = {
                {1, 2, 1},
                {0, 1, 2, 2},
                {1, 2, 3, 2, 2},
                {3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4}
        };

        for (int[] fruits : testCases) {
            System.out.print("Input: ");
            System.out.print("[");
            for (int i = 0; i < fruits.length; i++) {
                System.out.print(fruits[i] + (i < fruits.length - 1 ? ", " : ""));
            }
            System.out.print("] -> Output: ");
            System.out.println(totalFruit(fruits));
        }
    }
}
