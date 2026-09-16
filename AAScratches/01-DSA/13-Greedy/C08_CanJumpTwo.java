/**
 * Problem: Given an array of non‑negative integers where each element represents the maximum jump length from that position,
 * determine the minimum number of jumps required to reach the last index.
 *
 * Approach: Greedy traversal keeping track of the farthest reachable index within the current jump range.
 * When the current index reaches the end of this range, increment the jump count and extend the range
 * to the farthest reachable index found so far. Stop once the range covers or exceeds the last index.
 *
 * Time Complexity: O(n) – each element is processed once.
 * Space Complexity: O(1) – only a few integer variables are used regardless of input size.
 */

class CanJumpTwo {

    public static int jump(int[] nums) {
        int jumps = 0;
        int currentEnd = 0;
        int maxJump = 0;

    /*
    The goal of the problem is to reach the last index,
    so once you are at or beyond the second-to-last index,
    no further jumps are needed. This is because the
    problem doesn't ask you to "jump from" the last index
    but rather to "reach" it.
     */

        // < nums.length - 1 to handle edge case
        for (int i = 0; i < nums.length - 1; i++) {
            // Track the farthest index that can be reached
            maxJump = Math.max(maxJump, i + nums[i]);
            // If we've reached the end of the range for the current jump
            if (i == currentEnd) {
                jumps++;
                currentEnd = maxJump;
                // If the current end is past or at the last index, return the number of jumps
                if (currentEnd >= nums.length - 1) {
                    break;
                }
            }
        }
        return jumps;
    }

    public static void main(String[] args) {
        System.out.println(jump(new int[]{0}));
        System.out.println(jump(new int[]{1, 0}));
    }
}
