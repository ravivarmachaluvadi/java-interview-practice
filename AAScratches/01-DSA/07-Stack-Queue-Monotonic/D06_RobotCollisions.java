/*
 * =====================================================================
 *  Robot Collisions                                    LeetCode 2751 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Robot i sits at positions[i] (all distinct) with healths[i] and moves 'L' or 'R' at equal
 *   speed. When two collide, the lower-health one is removed and the survivor loses 1 health;
 *   equal health removes both. Return the healths of the survivors in ORIGINAL input order.
 *
 * EXAMPLE
 *   pos [5,4,3,2,1] hp [2,17,9,15,10] "RRRRR"  ->  [2, 17, 9, 15, 10]   same direction, no hits
 *   pos [3,5,2,6]   hp [10,10,15,12]  "RLRL"   ->  [14]  by position: 2R15 3R10 5L10 6L12
 *                                                        5L ties 3R (both die); 6L beats 2R -> 14
 *   pos [1,2,5,6]   hp [10,10,11,11]  "RLRL"   ->  []    two equal-health pairs, all die
 *
 * APPROACH  (collision simulation with state and reindexing)
 *   1. Wrap each robot as {originalIndex, position, health, direction}.
 *   2. Sort robots by position so "to the left" means "earlier in the loop".
 *   3. Scan left to right with a stack (ArrayList used as one):
 *        'R' robot -> push, it cannot hit anything already seen.
 *        'L' robot -> while top is 'R' and this robot is alive:
 *            equal health   -> pop top, this robot's health = 0
 *            top weaker     -> pop top, this robot loses 1
 *            top stronger   -> top loses 1, this robot's health = 0
 *          push this robot only if it is still alive.
 *   4. Sort survivors back by originalIndex and collect their healths.
 *
 * KEY INSIGHT
 *   This is Asteroid Collision with two extras: mutable state (health decrements instead of
 *   binary win/lose) and a transform step (sort by position first, restore original order at
 *   the end). Recognise "positions given out of order" as a cue to sort, then reuse the
 *   push-or-fight loop unchanged.
 *
 * COMPLEXITY
 *   Time  O(n log n)  two sorts; the collision loop itself is O(n) (each robot pushed/popped once)
 *   Space O(n)        robot objects plus the stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can a right-mover never collide with something already on the stack?
 *   - How would you return the survivors' final positions instead of healths?
 *   - Asteroid Collision (LC 735) is the same loop without health or reindexing
 *
 * RUN
 *   main() runs 3 cases (no collisions, chained fights, everyone dies) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// https://leetcode.com/problems/robot-collisions/description/
class RobotCollisions {

    static class Robot {
        final int index;      // original input index, used to restore output order
        final int position;
        int health;
        final char direction;

        Robot(int index, int position, int health, char direction) {
            this.index = index;
            this.position = position;
            this.health = health;
            this.direction = direction;
        }
    }

    public static List<Integer> survivedRobotsHealths(int[] positions, int[] healths,
                                                      String directions) {
        int n = positions.length;
        Robot[] robots = new Robot[n];
        for (int i = 0; i < n; i++) {
            robots[i] = new Robot(i, positions[i], healths[i], directions.charAt(i));
        }
        // Sort by position so the scan order matches the physical left-to-right order.
        Arrays.sort(robots, Comparator.comparingInt(r -> r.position));

        List<Robot> stack = new ArrayList<>();   // survivors so far; last element is the top
        for (Robot robot : robots) {
            if (robot.direction == 'R') {
                stack.add(robot);                // nothing to its left can ever catch it
                continue;
            }
            // Left-mover: fight every right-mover on top of the stack until dead or clear.
            while (!stack.isEmpty() && top(stack).direction == 'R' && robot.health > 0) {
                Robot rightRobot = top(stack);
                if (rightRobot.health == robot.health) {
                    stack.remove(stack.size() - 1);   // both die
                    robot.health = 0;
                } else if (rightRobot.health < robot.health) {
                    stack.remove(stack.size() - 1);   // right robot dies, this one pays 1 health
                    robot.health -= 1;
                } else {
                    rightRobot.health -= 1;           // right robot survives with 1 less health
                    robot.health = 0;
                }
            }
            if (robot.health > 0) {
                stack.add(robot);
            }
        }

        // Survivors are in position order; the answer wants original index order.
        stack.sort(Comparator.comparingInt(r -> r.index));
        List<Integer> result = new ArrayList<>();
        for (Robot r : stack) {
            result.add(r.health);
        }
        return result;
    }

    private static Robot top(List<Robot> stack) {
        return stack.get(stack.size() - 1);
    }

    private static void print(String label, int[] positions, int[] healths, String directions,
                              List<Integer> expected) {
        System.out.println(label + ": " + survivedRobotsHealths(positions, healths, directions)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 no collisions ", new int[]{5, 4, 3, 2, 1}, new int[]{2, 17, 9, 15, 10},
                "RRRRR", Arrays.asList(2, 17, 9, 15, 10));
        print("case 2 chained fights", new int[]{3, 5, 2, 6}, new int[]{10, 10, 15, 12}, "RLRL",
                Arrays.asList(14));
        print("case 3 everyone dies ", new int[]{1, 2, 5, 6}, new int[]{10, 10, 11, 11}, "RLRL",
                Arrays.asList());
    }
}
