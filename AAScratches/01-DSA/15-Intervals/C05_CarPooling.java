import java.util.*;

// https://leetcode.com/problems/car-pooling/description/
// 1094. Car Pooling
/**
 * You are given the integer capacity and an array trips where trips[i] = [numPassengersi, fromi, toi]
 * indicates that the ith trip has numPassengersi passengers and the locations to pick them up and drop
 * them off are fromi and toi respectively
 */
class CarPooling {

    public static boolean carPooling(int[][] trips, int capacity) {
        List<int[]> events = new ArrayList<>();

        // Add the events for all the trips
        for (int[] trip : trips) {
            // remember better extract and use rather direct usage
            int numPassengers = trip[0];
            int startLocation = trip[1];
            int endLocation = trip[2];

            // Add pick-up and drop-off events
            events.add(new int[]{startLocation, numPassengers});
            events.add(new int[]{endLocation, -numPassengers});
        }

        // Sort events by location
        events.sort((a, b) -> a[0] - b[0]);

        int currentPassengers = 0;

        for (int[] event : events) {
            currentPassengers += event[1];
            if (currentPassengers > capacity) {
                return false; // Exceeds capacity
            }
        }

        return true;
    }

    public static void main(String[] args) {
        // Example 1
        int[][] trips1 = {
                {2, 1, 5},
                {3, 3, 7}
        };
        int capacity1 = 4;
        System.out.println("Example 1: " + carPooling(trips1, capacity1)); // Expected output: false

        // Example 2
        int[][] trips2 = {
                {2, 1, 5},
                {3, 5, 7}
        };
        int capacity2 = 5;
        System.out.println("Example 2: " + carPooling(trips2, capacity2)); // Expected output: true
    }
}
