import java.util.*;

// https://leetcode.com/problems/robot-collisions/description/
// 2751. Robot Collisions
class RobotCollisions {
    static class Robot {
        int index;
        int position;
        int health;
        char direction;

        Robot(int idx, int pos, int hlth, char dir) {
            this.index = idx;
            this.position = pos;
            this.health = hlth;
            this.direction = dir;
        }
    }

    public static List<Integer> survivedRobotsHealths(int[] positions, int[] healths, String directions) {
        int n = positions.length;
        Robot[] robots = new Robot[n];
        for (int i = 0; i < n; i++) {
            robots[i] = new Robot(i, positions[i], healths[i], directions.charAt(i));
        }
        // sort by position
        Arrays.sort(robots, Comparator.comparingInt(r -> r.position));
        List<Robot> stack = new ArrayList<>();

        for (Robot robot : robots) {
            if (robot.direction == 'R') {
                stack.add(robot);
            } else {
                // moving left → might collide with robots moving right in stack
                while (!stack.isEmpty() && stack.get(stack.size() - 1).direction == 'R' && robot.health > 0) {
                    Robot rightRobot = stack.get(stack.size() - 1);
                    if (rightRobot.health == robot.health) {
                        // both die
                        stack.remove(stack.size() - 1);
                        robot.health = 0;
                    } else if (rightRobot.health < robot.health) {
                        // right robot dies, left robot loses 1 health
                        stack.remove(stack.size() - 1);
                        robot.health -= 1;
                    } else {
                        // right robot survives (loses 1 health), left robot dies
                        rightRobot.health -= 1;
                        robot.health = 0;
                    }
                }
                if (robot.health > 0) {
                    stack.add(robot);
                }
            }
        }

        // survivors in stack; sort them back by original index
        stack.sort(Comparator.comparingInt(r -> r.index));
        List<Integer> result = new ArrayList<>();
        for (Robot r : stack) {
            result.add(r.health);
        }
        return result;
    }

    // A simple main method to run an example
    public static void main(String[] args) {
        int[] positions = {3, 5, 2, 6};
        int[] healths = {10, 10, 15, 12};
        String directions = "RLRL";

        List<Integer> survivors = survivedRobotsHealths(positions, healths, directions);
        System.out.println("Surviving healths: " + survivors);
        // Expected output: [14]  (only one robot survives, with health 14)
    }
}
