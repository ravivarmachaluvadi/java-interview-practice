public static int[] findThreeLargest(int[] nums) {
//    Three largest numbers: [9, 6, 5]
    int first = Integer.MIN_VALUE;
    int second = Integer.MIN_VALUE;
    int third = Integer.MIN_VALUE;

    for (int num : nums) {
        // so far you are first now someone is first then
        // you will become second
        if (num > first) {
            third = second;
            second = first;
            first = num;
        } else if (num > second) {
            third = second;
            second = num;
        } else if (num > third) {
            third = num;
        }
    }
    // [9, 6, 5]
    return new int[]{first, second, third};
}

void main() {
    int[] nums = {3, 1, 4, 1, 5, 9, 2, 6, 5};
    int[] largest = findThreeLargest(nums);
    IO.println("Three largest numbers: " + Arrays.toString(largest));
}
