class RecursiveArraySum {
    public int arraySum(int[] nums) {
        // Start from index 0
        // started with first element
        return sum(nums, 0);
    }

    private int sum(int[] nums, int ind) {
        if (ind >= nums.length) {
            return 0;
        }
        // Add current element and recurse to next element
        return nums[ind] + sum(nums, ind + 1);
    }

    public static void main(String[] args) {
        RecursiveArraySum solver = new RecursiveArraySum();
        int[] example = {3, 5, -2, 7};
        int result = solver.arraySum(example);
        System.out.println("Input array: " + java.util.Arrays.toString(example));
        System.out.println("Output sum: " + result);
    }
}
