import java.util.*;

// https://leetcode.com/problems/rearranging-fruits/description/
// 2561. Rearranging Fruits
// Return the minimum cost to make both the baskets equal or -1 if impossible.
class SolutionRearrangingFruits {
    public static long minCost(int[] basket1, int[] basket2) {
        int n = basket1.length;
        Map<Integer, Integer> count = new HashMap<>();
        for (int v : basket1) {
            count.merge(v, 1, Integer::sum);
        }
        for (int v : basket2) {
            count.merge(v, -1, Integer::sum);
        }
        int minVal = Integer.MAX_VALUE;
        List<Integer> swapList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : count.entrySet()) {
            int val = e.getKey();
            int diff = e.getValue();
            if ((diff % 2) != 0) {
                return -1L;
            }
            if (diff != 0) {
                int moves = Math.abs(diff) / 2;
                for (int i = 0; i < moves; i++) {
                    swapList.add(val);
                }
            }
            minVal = Math.min(minVal, val);
        }
        Collections.sort(swapList);
        long cost = 0L;
        int m = swapList.size();
        /**
         Swap (25 ↔ 1): cost = 1
         2️⃣ Swap (1 ↔ 30): cost = 1
         1 25 25
         1 30 30
         */
        for (int i = 0; i < m / 2; i++) {
            int x = swapList.get(i);
            cost += Math.min(x, 2L * minVal);
        }
        return cost;
    }

    public static void main(String[] args) {
        // Example 1:
        int[] basket1a = {4, 2, 2, 2};
        int[] basket2a = {1, 4, 1, 2};
        System.out.println("Example1 → expected 1, got: " + minCost(basket1a, basket2a)); // 1

        // Example 2:
        int[] basket1b = {2, 3, 4, 1};
        int[] basket2b = {3, 2, 5, 1};
        System.out.println("Example2 → expected -1, got: " + minCost(basket1b, basket2b));// -1

        // You can add more tests as needed.
    }
}
