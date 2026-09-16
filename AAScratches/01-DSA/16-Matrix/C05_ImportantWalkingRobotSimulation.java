import java.util.*;
/**
 * -1: Turn right 90 degrees
 * <p>
 * -2: Turn left 90 degrees
 */
class ImportantWalkingRobotSimulation {

    public int robotSim(int[] commands, int[][] obstacles) {
        // Directions represent [north, east, south, west] in clockwise order
        // x, y -> 0,1 is change in x and y direction means no
        // change in x and positive increament in y by unit 1
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // N, E, S, W
        int dir = 0;  // Initially facing north
        int x = 0, y = 0;  // Starting point
        int maxDistance = 0;

        // Store obstacles in a set for quick lookup
        Set<String> obstacleSet = new HashSet<>();
        for (int[] obstacle : obstacles)
            obstacleSet.add(obstacle[0] + "," + obstacle[1]);

        // Process each command
        for (int command : commands) {
            if (command == -1) {
                // Turn right
                dir = (dir + 1) % 4;
            } else if (command == -2) {
                // Turn left
                dir = (dir + 3) % 4;  // Equivalent to turning left
            } else {
                // Move forward
                for (int step = 0; step < command; step++) {
                    int nextX = x + directions[dir][0];
                    int nextY = y + directions[dir][1];
                    // Stop moving if there's an obstacle
                    if (obstacleSet.contains(nextX + "," + nextY))
                        break;
                    x = nextX;
                    y = nextY;
                    maxDistance = Math.max(maxDistance, x * x + y * y);
                }
            }
        }
        return maxDistance;
    }

    public static void main(String[] args) {
        ImportantWalkingRobotSimulation robot = new ImportantWalkingRobotSimulation();

        // Example 1
        int[] commands1 = {4, -1, 3};
        int[][] obstacles1 = {};
        System.out.println(robot.robotSim(commands1, obstacles1));  // Output: 25

        // Example 2
        int[] commands2 = {4, -1, 4, -2, 4};
        int[][] obstacles2 = {{2, 4}};
        System.out.println(robot.robotSim(commands2, obstacles2));  // Output: 65
    }
}
