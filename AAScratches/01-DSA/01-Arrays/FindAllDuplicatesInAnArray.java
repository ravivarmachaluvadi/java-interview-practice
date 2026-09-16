// https://leetcode.com/problems/find-all-duplicates-in-an-array/


public static List<Integer> findDuplicates(int[] nums) {
    List<Integer> duplicates = new ArrayList<>();
    int i = 0;
    while (i < nums.length) {
        int correctIndex = nums[i] - 1;
        if (nums[correctIndex] == nums[i])
            i++;
        else
            swap(nums, correctIndex, i);
    }
    for (int j = 0; j < nums.length; j++) {
        if (nums[j] != j + 1)
            duplicates.add(nums[j]);
    }
    return duplicates;
}

public static void swap(int[] nums, int i, int j) {
    int temp = nums[i];
    nums[i] = nums[j];
    nums[j] = temp;
}

void main() {
    IO.println(findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1}));
}
