// LeetCode Link: https://leetcode.com/problems/maximum-swap/
/**
 * You are given an integer num. You can swap two digits
 * <p>
 * at most once to get the maximum valued number.
 * <p>
 * Return the maximum valued number you can get.
 * <p>
 * Input: num = 2736
 * Output: 7236
 * Explanation: Swap the number 2 and the number 7.
 */
class MaximumSwap {

    // logic is scan from left to right, and for first index i where the
    // digit at i is less than the maximum digit to its right, swap with
    // that max digit's position and break
    public int maximumSwap(int num) {
        char[] digits = Integer.toString(num).toCharArray();
        int[] last = new int[10];

        // Store the last occurrence of each digit
        for (int i = 0; i < digits.length; i++) {
            last[digits[i] - '0'] = i;
        }
        // 1009 -> 9001
        // 4321 -> 4321
        for (int i = 0; i < digits.length; i++) {
            // key condition to swap (d > digits[i] - '0')
            for (int d = 9; d > digits[i] - '0'; d--) {
                // last index position with value d
                // right side of i
                if (last[d] > i) {
                    // Swap the digits
                    char smaller = digits[i];
                    digits[i] = digits[last[d]];
                    digits[last[d]] = smaller;
                    return Integer.parseInt(new String(digits));
                }
            }
        }
        return num;
    }

    public static void main(String[] args) {
        MaximumSwap solution = new MaximumSwap();
        int num = 2736;
        int result = solution.maximumSwap(num);
        System.out.println("Maximum Swap of " + num + " is: " + result); // Output should be 7236
    }
}
