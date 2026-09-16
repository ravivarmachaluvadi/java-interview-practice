import java.util.HashSet;

/**
 * <h2>Check If An Array Is Consecutive</h2>
 *
 * <p>Given an integer array, determine whether its elements form a set of
 * consecutive integers — that is, whether the values can be rearranged into
 * an unbroken run where each element is exactly one greater than the previous.</p>
 *
 * <p>An array qualifies only if both conditions hold:</p>
 * <ul>
 *   <li>It contains no duplicate values.</li>
 *   <li>The span between the minimum and maximum value exactly matches the
 *       element count, i.e. {@code max - min + 1 == nums.length}.</li>
 * </ul>
 *
 * <p>Order does not matter — {@code [3, 2, 1, 4, 5]} is consecutive because it
 * is a permutation of {@code [1, 2, 3, 4, 5]}. Any gap in the range, or any
 * repeated value, disqualifies the array.</p>
 *
 * <pre>
 * Examples:
 *   Input: [3, 2, 1, 4, 5]  Output: true   (permutation of 1..5)
 *   Input: [1, 2, 4, 5]     Output: false  (3 is missing)
 *   Input: [1, 2, 2, 3]     Output: false  (2 is duplicated)
 * </pre>
 *
 * <p>Time: {@code O(n)} — single pass with constant-time set operations.<br>
 * Space: {@code O(n)} for the set of seen values.</p>
 */
class CheckIfAnArrayIsConsecutive {

    public boolean isConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;   // decide this explicitly rather than by overflow accident
        }

        int mn = Integer.MAX_VALUE, mx = Integer.MIN_VALUE;
        HashSet<Integer> seen = new HashSet<>();

        for (int num : nums) {
            if (!seen.add(num)) return false;   // add() returns false if already present
            mn = Math.min(mn, num);
            mx = Math.max(mx, num);
        }

        return (long) mx - mn + 1 == nums.length;   // long guards against int overflow
    }

    public static void main(String[] args) {
        CheckIfAnArrayIsConsecutive solution = new CheckIfAnArrayIsConsecutive();

        System.out.println(solution.isConsecutive(new int[]{3, 2, 1, 4, 5})); // true
        System.out.println(solution.isConsecutive(new int[]{1, 2, 4, 5}));    // false
        System.out.println(solution.isConsecutive(new int[]{1, 2, 2, 3}));    // false
    }
}