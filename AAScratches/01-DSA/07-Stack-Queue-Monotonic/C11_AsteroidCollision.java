/*
 * =====================================================================
 *  Asteroid Collision                          LeetCode 735 | Medium     MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Each int is an asteroid: |value| is its size, sign is its direction (+ right, - left).
 *   All move at the same speed on one line. When two meet, the smaller explodes; equal sizes
 *   both explode. Return the asteroids left after every collision. No value is 0.
 *
 * EXAMPLE
 *   [5, 10, -5]      ->  [5, 10]          10 beats -5; 5 and 10 move the same way, never meet
 *   [8, -8]          ->  []               equal sizes, both explode
 *   [10, 2, -5]      ->  [10]             -5 kills 2, then loses to 10
 *   [-2, -1, 1, 2]   ->  [-2, -1, 1, 2]   lefts are already past, rights never turn back
 *
 * APPROACH  (stack collision simulation)
 *   1. Scan left to right; the stack holds survivors so far, top = the nearest one to the left.
 *   2. A right-mover can never hit anything already on the stack: push it.
 *   3. A left-mover fights while the top is a right-mover:
 *        top smaller  -> pop, keep fighting
 *        top equal    -> pop and mark the current one destroyed (set to 0)
 *        top larger   -> mark the current one destroyed
 *   4. Push the current asteroid only if it survived (non-zero).
 *   5. Drain the stack bottom-to-top into the answer array.
 *
 * KEY INSIGHT
 *   Only "right-mover on the stack, left-mover arriving" can collide, so one while loop with a
 *   three-way branch covers every case. Setting the current value to 0 is a cheap "destroyed"
 *   flag that also exits the loop (the condition needs current < 0). Pattern: stack simulation
 *   where each new element may cancel a run of earlier ones.
 *
 * COMPLEXITY
 *   Time  O(n)  every asteroid is pushed at most once and popped at most once
 *   Space O(n)  the stack in the worst case (all right-movers)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Robot Collisions (LC 2751): same loop but with health and unsorted positions
 *   - What if speeds differ? Then it is Car Fleet (LC 853), which needs arrival times
 *   - Return the number of collisions instead of the survivors (count the pops)
 *
 * RUN
 *   main() runs 4 cases (typical, equal sizes, chain kill, no collision) and prints
 *   actual vs expected.
 */
import java.util.Arrays;
import java.util.Stack;

// https://leetcode.com/problems/asteroid-collision/description/
class ImportantAsteroidCollision {

    public static int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> survivors = new Stack<>();

        for (int current : asteroids) {
            // A collision is only possible when the top moves right and current moves left.
            while (!survivors.isEmpty() && current < 0 && survivors.peek() > 0) {
                int top = survivors.peek();
                int currentSize = Math.abs(current);
                if (top < currentSize) {
                    survivors.pop();          // current wins, keep fighting the next one down
                } else if (top == currentSize) {
                    survivors.pop();
                    current = 0;              // both explode; 0 = destroyed, also exits the loop
                } else {
                    current = 0;              // top wins; current is destroyed
                }
            }
            // Input guarantees no 0, so 0 can only mean "destroyed".
            if (current != 0) survivors.push(current);
        }

        int[] result = new int[survivors.size()];
        for (int i = survivors.size() - 1; i >= 0; i--) {
            result[i] = survivors.pop();
        }
        return result;
    }

    private static void print(String label, int[] input, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(asteroidCollision(input))
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        print("case 1 typical      ", new int[]{5, 10, -5}, new int[]{5, 10});
        print("case 2 equal sizes  ", new int[]{8, -8}, new int[]{});
        print("case 3 chain kill   ", new int[]{10, 2, -5}, new int[]{10});
        print("case 4 no collision ", new int[]{-2, -1, 1, 2}, new int[]{-2, -1, 1, 2});
    }
}
