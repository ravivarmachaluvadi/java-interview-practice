/**
 * Koko loves to eat bananas. There are n piles of bananas,
 * <p>
 * the ith pile has piles[i] bananas. The guards have gone and will come back in h hours.
 * <p>
 * Koko can decide her bananas-per-hour eating speed of k. Each hour,
 * <p>
 * she chooses some pile of bananas and eats k bananas from that pile.
 * <p>
 * If the pile has less than k bananas, she eats all of them instead and
 * <p>
 * will not eat any more bananas during this hour.
 * <p>
 * Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.
 * <p>
 * Return the minimum integer k such that she can eat all the bananas within h hours.
 * <p>
 * Example 1:
 * <p>
 * Input: piles = [3,6,7,11], h = 8
 * <p>
 * Output: 4
 */

class KokoEatingBananas {

    public static int minEatingSpeed(int[] piles, int h) {

        int left = 1;
        int right = 1;
        for (int pile : piles)
            right = Math.max(right, pile);

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canFinish(piles, mid, h))
                right = mid;
            else
                left = mid + 1;
        }
        return left;
    }

    private static boolean canFinish(int[] piles, int speed, int h) {
        int time = 0;
        for (int pile : piles) {
            // ceil(pile / speed) without using (pile + speed - 1) / speed
            int hoursForPile = pile / speed;
            if (pile % speed != 0) {
                hoursForPile++;
            }
            time += hoursForPile;
        }
        return time <= h;
    }

    public static void main(String[] args) {
        System.out.println(minEatingSpeed(new int[]{3, 6, 7, 11},8));// 4
    }
}
