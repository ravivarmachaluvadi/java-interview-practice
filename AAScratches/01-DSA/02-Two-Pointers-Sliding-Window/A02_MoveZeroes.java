class MoveZeroes {

    public void moveZeroes(int[] nums) {
        int pIndex = 0;
        // Shift all non-zero elements to the front
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[pIndex++] = nums[i];
            }
        }
        // Fill the remaining positions with zeroes
        for (int i = pIndex; i < nums.length; i++) {
            nums[i] = 0;
        }
    }

    public static void main(String[] args) {
        MoveZeroes mz = new MoveZeroes();
        int[] nums = {0, 1, 0, 3, 12};
        mz.moveZeroes(nums);
        // Output should be: [1, 3, 12, 0, 0]
        for (int num : nums) {
            System.out.print(num + " ");
        }
    }
}
