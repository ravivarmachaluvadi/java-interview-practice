/*
 * =====================================================================
 *  Car Pooling                                       LeetCode 1094 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A car with a fixed capacity drives one way along a number line, never
 *   turning back. trips[i] = [numPassengers, from, to] means that many people
 *   board at "from" and leave at "to". Return true if the car can complete
 *   every trip without ever carrying more than capacity passengers.
 *
 * EXAMPLE
 *   trips = [[2,1,5],[3,3,7]], capacity = 4  ->  false  (5 aboard on [3,5])
 *   trips = [[2,1,5],[3,5,7]], capacity = 5  ->  true   (2 leave exactly as 3 board)
 *   trips = [[9,0,1]],         capacity = 9  ->  true   (exactly full, allowed)
 *
 * APPROACH  (weighted event sweep / difference array on the line)
 *   1. Turn each trip into two events: (from, +numPassengers) and (to, -numPassengers).
 *   2. Sort the events by location. Where locations tie, apply the DROP-OFFS
 *      first - passengers leaving at x are already gone before anyone boards at x.
 *      That is the whole comparator: location ascending, then delta ascending.
 *   3. Sweep left to right adding each delta to a running occupancy count.
 *   4. If the running count ever exceeds capacity, return false; otherwise true.
 *
 * KEY INSIGHT
 *   This is the +1/-1 concurrency sweep from Minimum Platforms with weights
 *   attached: the "+1 per meeting" becomes "+k per trip". Peak value of the
 *   running sum is the peak occupancy. The part that actually bites in an
 *   interview is the tie-break - without ordering drop-offs before pick-ups at
 *   the same coordinate you invent passengers who overlap for zero distance.
 *
 *   Fixed: the original comparator sorted only by location, so at a shared
 *   coordinate the order depended on which trip happened to be listed first.
 *   For trips = [[3,5,7],[2,1,5]] with capacity 4 it reported false even though
 *   the 2 passengers leave at 5 exactly as the 3 board. Comparing the delta on a
 *   tie makes the result independent of input order.
 *
 * COMPLEXITY
 *   Time  O(n log n)  sorting 2n events; the sweep is O(n).
 *   Space O(n)        the event list.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Locations are bounded (0..1000 on LeetCode): drop the sort and use a
 *     difference array over the axis for O(n + range).
 *   - Return the maximum occupancy, or the location where the car is fullest.
 *   - What if the car could turn around, so trips are unordered in time?
 *   - Streaming trips that arrive one at a time: keep a TreeMap of deltas.
 *
 * RUN
 *   main() runs 4 cases (over capacity, exact handover, exactly full, and the
 *   reordered handover that used to fail) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class CarPooling {

    public static boolean carPooling(int[][] trips, int capacity) {
        List<int[]> events = new ArrayList<>();

        for (int[] trip : trips) {
            // Naming the columns beats indexing trip[0..2] all over the method.
            int numPassengers = trip[0];
            int startLocation = trip[1];
            int endLocation = trip[2];

            events.add(new int[]{startLocation, numPassengers});  // pick-up
            events.add(new int[]{endLocation, -numPassengers});   // drop-off
        }

        // Location ascending; on a tie the negative delta (drop-off) sorts first,
        // so the seats are freed before the next group boards at that stop.
        events.sort((a, b) -> a[0] != b[0]
                ? Integer.compare(a[0], b[0])
                : Integer.compare(a[1], b[1]));

        int currentPassengers = 0;
        for (int[] event : events) {
            currentPassengers += event[1];
            if (currentPassengers > capacity) {
                return false;
            }
        }
        return true;
    }

    private static void print(String label, boolean actual, boolean expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1: typical - 2 + 3 = 5 aboard between 3 and 5, over a capacity of 4.
        print("case 1 (over capacity)", carPooling(new int[][]{{2, 1, 5}, {3, 3, 7}}, 4), false);

        // Case 2: edge - drop-off and pick-up at the same stop; peak is 3, not 5.
        print("case 2 (handover at 5)", carPooling(new int[][]{{2, 1, 5}, {3, 5, 7}}, 5), true);

        // Case 3: edge - occupancy exactly equals capacity, which is allowed.
        print("case 3 (exactly full)", carPooling(new int[][]{{9, 0, 1}}, 9), true);

        // Case 4: tricky - same handover as case 2 but the trips are listed in the
        // other order. The old location-only comparator returned false here.
        print("case 4 (reordered handover)",
                carPooling(new int[][]{{3, 5, 7}, {2, 1, 5}}, 4), true);
    }
}
