import java.util.*;

class MissingNumber {
    public int missingNumber(int[] nums) {
        int N = nums.length;
        int sum1 = (N * (N + 1)) / 2;
        int sum2 = 0;
        for (int num : nums) {
            sum2 += num;
        }
        int missingNum = sum1 - sum2;
        return missingNum;
    }

    public static void main(String[] args) {
        int[] nums = {0, 1, 2, 4};
        MissingNumber solution = new MissingNumber();
        /* Call the missingNumber method
        to find the missing number*/
        int ans = solution.missingNumber(nums);

        System.out.println("The missing number is: " + ans);
    }

    public int missingNumber2(int[] nums) {
        int xor1 = 0, xor2 = 0;
        for (int i = 0; i < nums.length; i++) {
            xor1 = xor1 ^ (i + 1);
            xor2 = xor2 ^ nums[i];
        }
        return (xor1 ^ xor2);
    }

}
