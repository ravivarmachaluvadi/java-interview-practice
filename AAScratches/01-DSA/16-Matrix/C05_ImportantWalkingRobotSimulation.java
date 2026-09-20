/*
 * =====================================================================
 *  Walking Robot Simulation               LeetCode 874 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A robot starts at (0, 0) facing north on an infinite grid. Commands are
 *   -2 (turn left 90), -1 (turn right 90), or 1..9 (walk that many unit steps
 *   forward, stopping short if the next cell holds an obstacle).
 *   Return the maximum of x*x + y*y over every cell the robot ever occupies.
 *
 * EXAMPLE
 *   commands = [4,-1,3], obstacles = []          -> 25   ends at (3,4)
 *   commands = [4,-1,4,-2,4], obstacles = [[2,4]] -> 65  blocked at (2,4),
 *                                                        furthest cell (1,8)
 *   commands = [5], obstacles = [[0,1]]          -> 0    blocked on step one
 *
 * APPROACH  (grid simulation with a direction table)
 *   1. Store the four headings clockwise from north as (dx, dy) pairs:
 *      N(0,1), E(1,0), S(0,-1), W(-1,0). "dir" indexes this table.
 *   2. Turn right = (dir + 1) % 4. Turn left = (dir + 3) % 4, which is the
 *      same as (dir - 1 + 4) % 4 but never goes negative.
 *   3. Put every obstacle in a hash set keyed by "x,y" so the blocked test is
 *      O(1) instead of scanning the obstacle array on each step.
 *   4. For a move command, take the steps ONE AT A TIME. Before each step,
 *      peek at the next cell: if it is an obstacle, stop this command there.
 *   5. Track the best squared distance after every accepted step, not only at
 *      the end of a command, because the furthest point can be mid-walk.
 *
 * KEY INSIGHT
 *   Two reusable pieces. First, the clockwise direction table turns "turn
 *   left / turn right" into modular arithmetic on an index, which is the
 *   standard encoding for any turning-agent problem. Second, an obstacle set
 *   converts an O(steps * obstacles) scan into O(steps): obstacles are sparse
 *   points on an unbounded grid, so hash them rather than allocating a grid.
 *   Also note the answer is the squared distance, so never take a square root.
 *
 * COMPLEXITY
 *   Time  O(k + sum of step counts)  k obstacles hashed once, then O(1) a step
 *   Space O(k)                       the obstacle set
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why "x,y" strings? Encode as x * 60001L + y (or a long) to avoid the
 *     string allocation per step; an interviewer often asks for this.
 *   - Robot Bounded In Circle (LC 1041): same direction table, decide whether
 *     the path is a cycle after one pass of the instructions.
 *   - Return the final position, or the number of distinct cells visited.
 *   - What changes if a step count could be 10^9? Jump ahead to the nearest
 *     obstacle on the current ray instead of stepping.
 *
 * RUN
 *   main() runs 4 cases (typical, blocked mid-walk, blocked immediately,
 *   turns only) and prints actual vs expected.
 */

import java.util.HashSet;
import java.util.Set;

class ImportantWalkingRobotSimulation {

    // Headings in clockwise order so that right = +1 and left = +3 (mod 4).
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // N, E, S, W

    public int robotSim(int[] commands, int[][] obstacles) {
        int dir = 0;       // index into DIRECTIONS; 0 = facing north
        int x = 0, y = 0;  // current position
        int maxDistance = 0;

        Set<String> obstacleSet = new HashSet<>();
        for (int[] obstacle : obstacles) {
            obstacleSet.add(key(obstacle[0], obstacle[1]));
        }

        for (int command : commands) {
            if (command == -1) {
                dir = (dir + 1) % 4;       // turn right
            } else if (command == -2) {
                dir = (dir + 3) % 4;       // turn left, kept non-negative
            } else {
                for (int step = 0; step < command; step++) {
                    int nextX = x + DIRECTIONS[dir][0];
                    int nextY = y + DIRECTIONS[dir][1];
                    // Peek before moving: an obstacle ends this command early.
                    if (obstacleSet.contains(key(nextX, nextY))) break;
                    x = nextX;
                    y = nextY;
                    // The furthest cell can be mid-walk, so check every step.
                    maxDistance = Math.max(maxDistance, x * x + y * y);
                }
            }
        }
        return maxDistance;
    }

    private static String key(int x, int y) {
        return x + "," + y;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ImportantWalkingRobotSimulation robot = new ImportantWalkingRobotSimulation();

        print("case 1 (no obstacles)",
                robot.robotSim(new int[]{4, -1, 3}, new int[][]{}), 25);

        print("case 2 (blocked mid-walk)",
                robot.robotSim(new int[]{4, -1, 4, -2, 4}, new int[][]{{2, 4}}), 65);

        print("case 3 (blocked on first step)",
                robot.robotSim(new int[]{5}, new int[][]{{0, 1}}), 0);

        print("case 4 (turns only, never moves)",
                robot.robotSim(new int[]{-1, -2, -1}, new int[][]{}), 0);
    }
}
