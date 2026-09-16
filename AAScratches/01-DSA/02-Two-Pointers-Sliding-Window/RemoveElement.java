class RemoveElement {
    public int removeElement(int[] nums, int val) {
        // Pointer for the next non-val position
        int i = 0;
        // Iterate through the array
        for (int j = 0; j < nums.length; j++) {
            // If the current element is not equal to val
            if (nums[j] != val) {
                nums[i] = nums[j]; // Place the element at index i
                i++; // Move the pointer forward
            }
        }
        return i;
    }

    public static void main(String[] args) {
        RemoveElement sol = new RemoveElement();
        int[] nums = {3, 2, 2, 3};
        int val = 3;
        int newLength = sol.removeElement(nums, val);
        System.out.println("New length: " + newLength); // Output: 2

        // Print the modified array
        for (int i = 0; i < newLength; i++)
            System.out.print(nums[i] + " ");
        // Output: 2 2
    }
}
