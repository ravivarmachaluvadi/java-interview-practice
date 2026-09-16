
// 915
// Every element in left is less than or equal to every element in right
/**
 * Input: nums = [5,0,3,8,6]
 * Output: 3
 * Explanation: left = [5,0,3], right = [8,6]
 */
class PartitionArrayintoDisjointIntervals {
    public int partitionDisjoint(int[] arr) {
        int leftMax = arr[0], max = arr[0], ans = 1;
        for (int i = 1; i < arr.length; i++) {
            // since current is less than
            // leftMax so far it should be part of
            // left part , ans can i+1
            if (arr[i] < leftMax) {
                ans = i + 1;
                // updating last max found
                leftMax = max;
            } else max = Math.max(arr[i], max);
        }
        return ans;
    }

    public static void main(String[] args) {
        // Sample test cases
        PartitionArrayintoDisjointIntervals solution = new PartitionArrayintoDisjointIntervals();
        int[][] testCases = {
                {5, 0, 3, 8, 6},
                {1, 1, 1, 0, 6, 12},
                {1, 2, 3, 4, 5}
        };

        for (int[] arr : testCases) {
            System.out.print("Input: ");
            System.out.print("[");
            for (int i = 0; i < arr.length; i++) {
                System.out.print(arr[i] + (i < arr.length - 1 ? ", " : ""));
            }
            System.out.print("] -> Output: ");
            System.out.println(solution.partitionDisjoint(arr));
        }
    }
}
