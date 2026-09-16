class MinimumHealthToBeatGame {
    /**
     * LeetCode Link: https://leetcode.com/problems/minimum-health-to-beat-game/
     */
    public static int minimumHealth(int[] damage, int armor) {
        int totalDamage = 0;
        int maxDamage = 0;
        for (int d : damage) {
            totalDamage += d;
            maxDamage = Math.max(maxDamage, d);
        }
// Reduce the largest damage by the armor value (or the damage itself if armor > damage)
        totalDamage -= Math.min(maxDamage, armor);
// Add 1 to ensure health is never 0 at any point
        return totalDamage + 1;
    }

    public static void main(String[] args) {
        // Example 1
        int[] damage1 = {2, 7, 4, 3};
        int armor1 = 4;
        System.out.println(minimumHealth(damage1, armor1));
        // Output: 14

        // Example 2
        int[] damage2 = {2, 5, 3, 1};
        int armor2 = 3;
        System.out.println(minimumHealth(damage2, armor2));
        // Output: 11

        // Example 3
        int[] damage3 = {5, 10, 7};
        int armor3 = 15;
        System.out.println(minimumHealth(damage3, armor3));
        // Output: 8
    }
}
