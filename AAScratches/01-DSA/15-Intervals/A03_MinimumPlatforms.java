/*
 * =====================================================================
 *  Minimum Number of Platforms                    GFG classic | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the arrival and departure times of n trains at one station, find the
 *   smallest number of platforms needed so that no train ever waits. A platform
 *   holds one train from its arrival until its departure, and a train arriving at
 *   exactly the moment another departs still needs its own platform.
 *
 * EXAMPLE
 *   arr = [900, 945, 955, 1100, 1500, 1800]
 *   dep = [920, 1200, 1130, 1150, 1900, 2000]  ->  3   (945, 955, 1100 all overlap)
 *   arr = [900], dep = [910]                   ->  1   single train
 *   arr = [100, 200, 300], dep = [900, 900, 900] -> 3  nothing leaves before all arrive
 *
 * APPROACH  (decouple the endpoints, then sweep time forward)
 *   1. Sort the arrival array and the departure array INDEPENDENTLY. Which train
 *      owns which time stops mattering; only the chronology of events does.
 *   2. Walk both arrays with two pointers, always handling the earlier event next.
 *   3. Arrival earlier than or equal to the next departure -> a train occupies a
 *      platform, so increment the live count and record the running maximum.
 *   4. Otherwise the next event is a departure -> decrement the live count.
 *   5. Stop when the arrivals run out; the peak can only occur on an arrival.
 *
 * KEY INSIGHT
 *   The answer is the maximum number of intervals alive at the same instant, and
 *   that peak is found without ever pairing a start with its own end. Breaking
 *   intervals into a sorted stream of +1 and -1 events is the third interval
 *   primitive, and it solves Meeting Rooms II, Divide Intervals Into Minimum
 *   Groups, Car Pooling and Maximum Population Year with the same loop.
 *   Using <= rather than < on the arrival branch encodes "arrival at the exact
 *   departure minute still needs a platform"; flip it to < if the station allows
 *   an instant handover.
 *
 * COMPLEXITY
 *   Time  O(n log n)  two sorts; the sweep afterwards is a single O(n) pass
 *   Space O(1) extra  both sorts are in place, the sweep keeps two counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same problem under the interview name Meeting Rooms II (C03).
 *   - Report the busiest time window, not just the count.
 *   - Each train carries a different number of coaches: weight the deltas
 *     (Car Pooling, C05).
 *   - Times are unbounded or arrive as a stream: use a TreeMap difference map.
 *
 * RUN
 *   main() runs 4 cases (typical, single train, total overlap, empty) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class MinimumPlatforms {

    private static int findPlatform(int[] arr, int[] dep, int n) {
        // Sorted independently on purpose: the pairing of an arrival with its own
        // departure is irrelevant, only the order of events in time matters.
        Arrays.sort(arr);
        Arrays.sort(dep);

        int minPlatforms = 0; // the answer: peak concurrency seen so far
        int platforms = 0;    // platforms occupied right now

        int i = 0; // next arrival
        int j = 0; // next departure
        while (i < n && j < n) {
            if (arr[i] <= dep[j]) {
                // A train arrives before (or exactly as) the next one leaves.
                platforms++;
                i++;
                minPlatforms = Math.max(minPlatforms, platforms);
            } else {
                // The next event in time is a departure, freeing a platform.
                platforms--;
                j++;
            }
        }

        // No tail loop is needed: once the arrivals are exhausted the count can
        // only fall, so the maximum has already been recorded.
        return minPlatforms;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: 945, 955 and 1100 are all on platforms at 1100
        int[] arr1 = {900, 945, 955, 1100, 1500, 1800};
        int[] dep1 = {920, 1200, 1130, 1150, 1900, 2000};
        print("case 1 six trains", findPlatform(arr1, dep1, arr1.length), 3);

        // edge: one train needs one platform
        int[] arr2 = {900};
        int[] dep2 = {910};
        print("case 2 single train", findPlatform(arr2, dep2, arr2.length), 1);

        // tricky: nothing departs until every train has arrived
        int[] arr3 = {100, 200, 300};
        int[] dep3 = {900, 900, 900};
        print("case 3 all overlap", findPlatform(arr3, dep3, arr3.length), 3);

        // edge: no trains at all
        print("case 4 empty", findPlatform(new int[]{}, new int[]{}, 0), 0);

        // tricky: back-to-back times still clash under the <= rule
        int[] arr5 = {900, 920};
        int[] dep5 = {920, 940};
        print("case 5 handover at 920", findPlatform(arr5, dep5, arr5.length), 2);
    }
}
