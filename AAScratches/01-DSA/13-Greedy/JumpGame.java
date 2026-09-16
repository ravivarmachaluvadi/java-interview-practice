class JumpGame {
    public static void main(String[] args) {

        int[] arr = {2, 4, 6, 1, 2, 1, 0, 0, 1};
        System.out.println(getMinJumps(arr));
        System.out.println(canJump(arr));
    }

    private static int getMinJumps(int[] arr) {
        int currEnd = 0;
        int farthest = 0;
        int jumps = 0;
        for (int i = 0; i < arr.length; i++) {
            farthest = Math.max(farthest, i + arr[i]);
            if (i == currEnd) {
                jumps++;
                currEnd = farthest;
            }
        }
        return jumps;
    }

    private static boolean canJump(int[] arr) {
        int farthest = 0;
        for (int i = 0; i < arr.length; i++) {
            // farthest is behind the currentIndex
            // means cant reach currIdx , then we can't reach last index
            if (farthest < i) return false;
            farthest = Math.max(farthest, i + arr[i]);
            if (farthest >= arr.length - 1) return true;

        }
        return false;
    }
}