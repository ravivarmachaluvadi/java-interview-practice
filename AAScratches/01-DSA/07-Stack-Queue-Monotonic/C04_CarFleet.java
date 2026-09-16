import java.util.Arrays;

/**
 * There are n cars at given miles away from the starting mile 0,
 * <p>
 * traveling to reach the mile target.
 * <p>
 * You are given two integer arrays position and speed, both of length n,
 * <p>
 * where position[i] is the starting mile of the ith car and speed[i] is
 * <p>
 * the speed of the ith car in miles per hour.
 * <p>
 * A car cannot pass another car, but it can catch up and then travel next
 * <p>
 * to it at the speed of the slower car.
 * <p>
 * A car fleet is a single car or a group of cars driving next to each other.
 * <p>
 * The speed of the car fleet is the minimum speed of any car in the fleet.
 * <p>
 * If a car catches up to a car fleet at the mile target, it will still be considered
 * <p>
 * as part of the car fleet.
 * <p>
 * Return the number of car fleets that will arrive at the destination.
 */
class CarFleet {

    public int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        // Create an array to store car positions and
        // their corresponding times to reach the target
        double[][] cars = new double[n][2];

        // Populate the array with position and time to reach the target
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            // remember
            cars[i][1] = (double) (target - position[i]) / speed[i];
        }
        // Sort the cars based on their position (farther from the target come first)
        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));

        int fleets = 0;
        double lastFleetTime = 0.0;

        // Iterate over the sorted cars
        for (int i = 0; i < n; i++) {
            double currentTime = cars[i][1];
            // If the current car takes longer to reach the target
            // than the last fleet, it forms a new fleet
            if (currentTime > lastFleetTime) {
                fleets++;
                lastFleetTime = currentTime; // Update the time of the last fleet
            }
        }
        return fleets;
    }

    public static void main(String[] args) {
        CarFleet cf = new CarFleet();
        int target = 12;
        int[] position = {10, 8, 0, 5, 3};
        int[] speed = {2, 4, 1, 1, 3};

        int result = cf.carFleet(target, position, speed);
        System.out.println("Number of fleets: " + result);  // Output: 3
    }
}

