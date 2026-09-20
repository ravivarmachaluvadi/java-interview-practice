/*
 * =====================================================================
 *  Minimum Number of Refueling Stops                    LeetCode 871 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   A car starts at position 0 with startFuel litres and must reach position
 *   target. stations[i] = [position, fuel] lists gas stations sorted by position,
 *   all strictly before target; stopping at one adds all of its fuel. One litre
 *   moves the car one mile. Return the minimum number of stops needed to reach
 *   target, or -1 if it is impossible.
 *
 * EXAMPLE
 *   target = 1000, startFuel = 200,
 *   stations = [[200,200],[400,200],[600,200],[800,200]]
 *     -> 4   every hop is exactly 200 miles, so every station must be used
 *   target = 100, startFuel = 10, stations = [[10,60],[20,30],[30,30],[60,40]]
 *     -> 2   stop at [10,60] (fuel 60) and [60,40] (fuel 50), skip the rest
 *   target = 1, startFuel = 1, stations = []            ->  0   no stops needed
 *   target = 100, startFuel = 1, stations = [[10,100]]  -> -1   cannot reach the first station
 *
 * APPROACH  (retroactive greedy with a max-heap)
 *   1. Append a sentinel station [target, 0] so the destination is just one more
 *      hop and the loop needs no special end case.
 *   2. Walk the stations in order. For each, compute dist from the previous
 *      position and check whether the tank covers it.
 *   3. If not, pop the largest fuel amount banked in the heap (a station we
 *      already drove past) and count that as a stop. Repeat until the hop is
 *      covered or the heap runs dry; an empty heap with fuel still short means -1.
 *   4. Drive the hop (fuel -= dist) and bank this station's fuel in the heap
 *      without deciding yet whether we will ever "use" it.
 *
 * KEY INSIGHT
 *   You do not have to decide at a station whether to stop there. Bank every
 *   station you pass, and only when you actually run short do you retroactively
 *   "stop" at the biggest banked one. Because fuel is additive and order of
 *   refuelling does not matter, choosing the largest banked station is always
 *   optimal. Recognise this as the "regret / deferred decision" heap pattern,
 *   the same shape as Furthest Building You Can Reach.
 *
 * COMPLEXITY
 *   Time  O(n log n)  each station is offered once and polled at most once
 *   Space O(n)        the heap plus the copied stations array with the sentinel
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the exchange argument: why is "largest banked fuel first" never worse?
 *   - DP alternative: dp[s] = farthest reachable with s stops, O(n^2) time, O(n) space.
 *   - What breaks if stations are not sorted by position? (sort first, then same loop)
 *   - Return the actual stations used, not just the count.
 *
 * RUN
 *   main() runs 4 cases (every station needed, skip some, no stations, unreachable)
 *   and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

class MinimumRefuelingStops {

    public static int minRefuelStops(int target, int startFuel, int[][] stations) {
        // Fuel amounts of every station already driven past, largest on top.
        PriorityQueue<Integer> bankedFuel = new PriorityQueue<>(Collections.reverseOrder());
        int fuel = startFuel;
        int prevPosition = 0;
        int stops = 0;

        // Sentinel: treat the target as a final station with no fuel.
        int n = stations.length;
        int[][] route = Arrays.copyOf(stations, n + 1);
        route[n] = new int[]{target, 0};

        for (int[] station : route) {
            int dist = station[0] - prevPosition;

            // Short for this hop? Retroactively refuel at the best station we passed.
            while (fuel < dist && !bankedFuel.isEmpty()) {
                fuel += bankedFuel.poll();
                stops++;
            }
            if (fuel < dist) {
                return -1;                    // heap is empty and still short
            }

            fuel -= dist;
            bankedFuel.offer(station[1]);     // bank it; decide later whether it was a stop
            prevPosition = station[0];
        }
        return stops;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 every station needed",
                minRefuelStops(1000, 200,
                        new int[][]{{200, 200}, {400, 200}, {600, 200}, {800, 200}}),
                4);

        print("case 2 skip some stations",
                minRefuelStops(100, 10, new int[][]{{10, 60}, {20, 30}, {30, 30}, {60, 40}}),
                2);

        print("case 3 no stations, enough fuel",
                minRefuelStops(1, 1, new int[][]{}),
                0);

        print("case 4 unreachable",
                minRefuelStops(100, 1, new int[][]{{10, 100}}),
                -1);
    }
}
