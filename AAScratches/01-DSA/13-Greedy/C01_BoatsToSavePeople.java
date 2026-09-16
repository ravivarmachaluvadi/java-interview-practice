import java.util.Arrays;

// 881. Boats to Save People
// https://leetcode.com/problems/boats-to-save-people/description/

/**
 * Constraints:
 * <p>
 * 1 <= people.length <= 5 * 104
 * <p>
 * 1 <= people[i] <= limit <= 3 * 104
 */
class BoatsToSavePeople {

    public static int numRescueBoats(int[] people, int limit) {
        Arrays.sort(people);
        int left = 0;
        int right = people.length - 1;
        int boats = 0;

        while (left <= right) {
            // Try to pair the lightest (left) with the heaviest (right)
            if (people[left] + people[right] <= limit) {
                left++;
            }
            // Whether paired or not, the heaviest
            // person at `right` uses a boat now
            right--;
            boats++;
        }
        return boats;
    }

    public static void main(String[] args) {
        // Example 1
        int[] people1 = {1, 2};
        int limit1 = 3;
        System.out.println("Example 1 result = " + numRescueBoats(people1, limit1));
        // Expected output: 1

        // Example 2
        int[] people2 = {3, 2, 2, 1};
        int limit2 = 3;
        System.out.println("Example 2 result = " + numRescueBoats(people2, limit2));
        // Expected output: 3

        // Example 3
        int[] people3 = {3, 5, 3, 4};
        int limit3 = 5;
        System.out.println("Example 3 result = " + numRescueBoats(people3, limit3));
        // Expected output: 4
    }
}
