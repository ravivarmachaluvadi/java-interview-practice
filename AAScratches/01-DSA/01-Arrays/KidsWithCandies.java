import java.util.ArrayList;
import java.util.List;

/**
 * <p>Solves <b>Kids With the Greatest Number of Candies</b> (LeetCode #1431).</p>
 *
 * <p>Given an array {@code candies} where {@code candies[i]} is the number of
 * candies the {@code i}-th kid has, and an integer {@code extraCandies}
 * representing extra candies one kid could receive, returns a boolean array
 * {@code result} of the same length where {@code result[i]} is {@code true}
 * if giving the {@code i}-th kid all the {@code extraCandies} would let them
 * have the greatest number of candies among all kids (ties count), or
 * {@code false} otherwise.</p>
 *
 * <p><b>Approach:</b></p>
 * <ul>
 *   <li>First pass: find {@code maxCandies}, the current maximum.</li>
 *   <li>Second pass: for each kid, check if {@code candy + extraCandies >= maxCandies}.</li>
 * </ul>
 *
 * <pre>
 * Input:  candies = [2, 3, 5, 1, 3], extraCandies = 3
 * Output: [true, true, true, false, true]
 * </pre>
 *
 * <p>Time complexity: {@code O(n)}. Space complexity: {@code O(1)} excluding the output list.</p>
 *
 * @see <a href="https://leetcode.com/problems/kids-with-the-greatest-number-of-candies/description">LeetCode 1431</a>
 */
class KidsWithCandies {

    /**
     * <p>Determines, for each kid, whether receiving all {@code extraCandies}
     * would give them the greatest candy count among all kids.</p>
     *
     * @param candies      array where {@code candies[i]} is the candies the {@code i}-th kid has
     * @param extraCandies number of extra candies available to give to one kid at a time
     * @return a list of booleans where element {@code i} is {@code true} if the
     * {@code i}-th kid would have the max (or tied max) after receiving {@code extraCandies}
     */
    public static List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        List<Boolean> result = new ArrayList<>();
        int maxCandies = 0;
        for (int candy : candies) {
            maxCandies = Math.max(maxCandies, candy);
        }
        for (int candy : candies) {
            result.add((candy + extraCandies) >= maxCandies);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] candies1 = {2, 3, 5, 1, 3};
        int extraCandies1 = 3;
        System.out.println("(Example 1): " + kidsWithCandies(candies1, extraCandies1));
        // Output: [true, true, true, false, true]

        int[] candies2 = {4, 2, 1, 1, 2};
        int extraCandies2 = 1;
        System.out.println("(Example 2): " + kidsWithCandies(candies2, extraCandies2));
        // Output: [true, false, false, false, false]

        int[] candies3 = {12, 1, 12};
        int extraCandies3 = 10;
        System.out.println("(Example 3): " + kidsWithCandies(candies3, extraCandies3));
        // Output: [true, false, true]
    }
}
