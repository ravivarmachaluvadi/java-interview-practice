import java.util.Arrays;
import java.util.Stack;
/**
 * Input: asteroids = [5,10,-5]
 * <p>
 * Output: [5,10]
 * <p>
 * Explanation: The 10 and -5 collide resulting in 10. The 5 and 10 never collide.
 * <p>
 * Find out the state of the asteroids after all collisions.
 * If two asteroids meet,
 * <p>
 * the smaller one will explode.
 * <p>
 * If both are the same size, both will explode.
 * <p>
 * Two asteroids moving in the same direction will never meet.
 * <p>
 * Constraints:
 * <p>
 * asteroids[i] != 0
 */
// https://leetcode.com/problems/asteroid-collision/description/
class ImportantAsteroidCollision {

    public static int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();

        for (int currentAsteroid : asteroids) {
            // only loop when current asteroid is negative
            while (!stack.isEmpty() && currentAsteroid < 0 && stack.peek() > 0) {
                if (stack.peek() < Math.abs(currentAsteroid)) {
                    stack.pop();
                } else if (stack.peek() == Math.abs(currentAsteroid)) {
                    stack.pop();
                    currentAsteroid = 0;
                } else {
                    currentAsteroid = 0;
                }
            }
            // Only push to stack if current asteroid hasn't been destroyed
            // remember constraint : asteroids[i] != 0
            if (currentAsteroid != 0) stack.push(currentAsteroid);
        }

        int[] result = new int[stack.size()];

        for (int i = stack.size() - 1; i >= 0; i--) {
            result[i] = stack.pop();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(asteroidCollision(new int[]{5, 10, -5})));
    }
}
