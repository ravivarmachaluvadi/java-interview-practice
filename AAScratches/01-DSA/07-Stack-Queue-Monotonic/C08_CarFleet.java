/*
 * =====================================================================
 *  Car Fleet                                     LeetCode 853 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   n cars drive along a one-lane road toward mile target. position[i] is where car i starts
 *   and speed[i] its speed; all start positions are distinct. A car cannot overtake: when it
 *   catches a slower car it slows down and they travel together as one fleet. Catching up
 *   exactly at the target still counts as joining. Return how many fleets reach the target.
 *
 * EXAMPLE
 *   target = 12, position = [10, 8, 0, 5, 3], speed = [2, 4, 1, 1, 3]  ->  3
 *     cars at 10 and 8 meet at 12; car at 0 is alone; cars at 5 and 3 meet at 6
 *   target = 10, position = [3], speed = [3]                            ->  1   single car
 *   target = 100, position = [0, 2, 4], speed = [4, 2, 1]               ->  1   all catch up
 *   target = 10, position = [6, 8], speed = [3, 2]                      ->  2   never meet
 *
 * APPROACH  (sort by position, then compare arrival times front to back)
 *   1. For each car compute time = (target - position) / speed, as a double.
 *   2. Sort cars by start position, closest to the target first. The front car can never be
 *      caught by anything ahead of it, so it is always the head of a fleet.
 *   3. Walk the sorted list keeping lastFleetTime, the arrival time of the fleet in front.
 *      If this car's time is LARGER, it can never catch that fleet: count a new fleet and
 *      remember its time.
 *      If its time is smaller or equal, it would reach the target no later than the fleet
 *      ahead, so it catches up and merges; it inherits the slower fleet's time (no update).
 *
 * KEY INSIGHT
 *   Position order plus arrival time turns a physics question into a monotonic scan. A car
 *   behind merges when its (unblocked) arrival time <= the fleet ahead; the fleet's time is the
 *   maximum so far, so only a strictly larger time starts a new fleet. Written with an explicit
 *   stack of times, this is a monotonic increasing stack whose final size is the answer.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the scan is O(n)
 *   Space O(n)        the position/time pairs
 *
 * INTERVIEW FOLLOW-UPS
 *   - Car Fleet II (LeetCode 1776): return WHEN each car collides, needs a real stack.
 *   - Why doubles? Integer division would merge cars that actually arrive at different times.
 *
 * RUN
 *   main() runs 4 cases (typical, single car, all merge, none merge) and prints actual vs expected.
 */

import java.util.Arrays;

class CarFleet {

    public int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        // cars[i] = { start position, time to reach target driving unblocked }
        double[][] cars = new double[n][2];
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = (double) (target - position[i]) / speed[i]; // double: avoid truncation
        }
        // Closest to the target first, so the fleet in front is processed before its followers.
        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));

        int fleets = 0;
        double lastFleetTime = 0.0;
        for (double[] car : cars) {
            double arrivalTime = car[1];
            if (arrivalTime > lastFleetTime) {
                // Slower than the fleet ahead: it can never catch up, so it leads a new fleet.
                fleets++;
                lastFleetTime = arrivalTime;
            }
            // else: it reaches the target no later than the fleet ahead, so it merges into it.
        }
        return fleets;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        CarFleet cf = new CarFleet();
        print("case 1 typical    ",
                cf.carFleet(12, new int[]{10, 8, 0, 5, 3}, new int[]{2, 4, 1, 1, 3}), 3);
        print("case 2 single car ", cf.carFleet(10, new int[]{3}, new int[]{3}), 1);
        print("case 3 all merge  ", cf.carFleet(100, new int[]{0, 2, 4}, new int[]{4, 2, 1}), 1);
        print("case 4 none merge ", cf.carFleet(10, new int[]{6, 8}, new int[]{3, 2}), 2);
    }
}
