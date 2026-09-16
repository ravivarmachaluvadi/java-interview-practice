/**
 * Input: n = 19
 * <p>
 * Output: true
 * <p>
 * Explanation:
 * <p>
 * 12 + 92 = 82
 * <p>
 * 82 + 22 = 68
 * <p>
 * 62 + 82 = 100
 * <p>
 * 12 + 02 + 02 = 1
 */
// https://leetcode.com/problems/happy-number/description/
class HappyNumber {

    public boolean isHappy(int n) {
        int slow = n;
        int fast = getNext(n);
        while (fast != 1 && slow != fast) {
            slow = getNext(slow);
            fast = getNext(getNext(fast));
        }
        return fast == 1;
    }

    private int getNext(int n) {
        int totalSum = 0;
        while (n > 0) {
            int d = n % 10;
            n = n / 10;
            totalSum += d * d;
        }
        return totalSum;
    }

}
