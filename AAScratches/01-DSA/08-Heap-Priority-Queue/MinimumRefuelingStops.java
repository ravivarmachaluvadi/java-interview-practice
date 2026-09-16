import java.util.*;

// https://leetcode.com/problems/minimum-number-of-refueling-stops/description/
// 871. Minimum Number of Refueling Stops

/**
 * A car travels from a starting position to a destination which is target miles east of the starting position.
 * <p>
 * There are gas stations along the way. The gas stations are represented as an array stations where
 * stations[i] = [positioni, fueli] indicates that the ith gas station is positioni miles east of the
 * starting position and has fueli liters of gas.
 * <p>
 * Return the minimum number of refueling stops the car must make in order to reach its destination. If it cannot reach the destination, return -1.
 */
class MinimumRefuelingStops {

    public static int minRefuelStops(int target, int startFuel, int[][] stations) {
        // Max-heap to store the fuel available at stations that we can reach
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        int currentFuel = startFuel;
        int prevLocation = 0;
        int stops = 0;
        int n = stations.length;

        stations = Arrays.copyOf(stations, n + 1);
        stations[n] = new int[]{target, 0};

        for (int i = 0; i <= n; i++) {
            // Calculate the distance from the previous station
            int dist = stations[i][0] - prevLocation;
            // While we don't have enough fuel to reach the next station, refuel at the best
            // station we can reach
            while (!maxHeap.isEmpty() && currentFuel < dist) {
                currentFuel += maxHeap.poll();
                stops++;
            }
            // If after refueling, we can't reach the next station, return -1
            if (currentFuel < dist) {
                return -1;
            }
            // Add the fuel at the current station to the max-heap
            currentFuel -= dist;
            maxHeap.offer(stations[i][1]);
            prevLocation = stations[i][0];
        }
        return stops;
    }

    public static void main(String[] args) {
        int target = 1000;
        int startFuel = 200;
        int[][] stations = {
                {200, 200},
                {400, 200},
                {600, 200},
                {800, 200}
        };

        int result = minRefuelStops(target, startFuel, stations);
        System.out.println("Minimum number of refueling stops: " + result); // Expected output: 4
    }
}

