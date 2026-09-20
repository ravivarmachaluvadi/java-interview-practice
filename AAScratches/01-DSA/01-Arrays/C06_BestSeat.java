/*
 * =====================================================================
 *  Best Seat (Maximize Distance to Closest Person)   LeetCode 849 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   seats[i] is 1 (occupied) or 0 (empty); there is at least one of each. Alex wants
 *   the empty seat that maximises the distance to the nearest person. This file
 *   answers both forms: findBestSeat returns that seat's index (the author's version),
 *   maxDistToClosest returns the distance itself (what LeetCode 849 asks for).
 *
 * EXAMPLE
 *   seats = [1, 0, 0, 0, 1, 0, 1]  ->  index 2, distance 2
 *   seats = [1, 0, 0, 0]           ->  index 3, distance 3   nobody to the right
 *   seats = [0, 0, 1]              ->  index 0, distance 2   nobody to the left
 *   seats = [1, 0, 1, 0, 1]        ->  index 1, distance 1   tie: leftmost wins
 *
 * APPROACH  (left and right distance passes)
 *   1. leftDist[i]  = distance from i to the nearest person on the LEFT, scanning
 *      left to right; "infinite" (Integer.MAX_VALUE) while no person has been seen.
 *   2. rightDist[i] = the same scanning right to left.
 *   3. For every empty seat the closest person is min(leftDist[i], rightDist[i]).
 *      Pick the empty seat with the largest such minimum; the first one wins ties.
 *
 * KEY INSIGHT
 *   "Nearest on either side" is two one-directional questions, each answerable by a
 *   running "last seen" index in one pass. The whole difficulty is the edges: an
 *   empty seat with nobody on one side is bounded only by the other side, and a
 *   sentinel of MAX_VALUE makes min() pick the real side automatically.
 *   Pattern: prefix/suffix passes over distances, same skeleton as product except self.
 *
 * COMPLEXITY
 *   Time  O(n)  three passes
 *   Space O(n)  two distance arrays (a gap-scan variant does it in O(1))
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in O(1) space: scan gaps between consecutive 1s (best = gap/2) and
 *     treat the leading and trailing gaps as full length.
 *   - Return the seat index AND the distance in one pass.
 *   - What changes if several people arrive one by one (Exam Room, LeetCode 855)?
 *
 * RUN
 *   main() runs 4 cases (typical, right edge, left edge, tie) for both methods and
 *   prints actual vs expected.
 */
class BestSeat {

    /** Index of the empty seat farthest from anyone; leftmost on ties. */
    public static int findBestSeat(int[] seats) {
        int[] leftDist = distanceToNearestOnLeft(seats);
        int[] rightDist = distanceToNearestOnRight(seats);

        int bestSeat = -1, maxDistance = -1;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] == 0) {
                int distance = Math.min(leftDist[i], rightDist[i]);
                if (distance > maxDistance) {   // strict: keep the leftmost on ties
                    maxDistance = distance;
                    bestSeat = i;
                }
            }
        }
        return bestSeat;
    }

    /** LeetCode 849's answer: the distance from the best seat to the nearest person. */
    public static int maxDistToClosest(int[] seats) {
        int best = findBestSeat(seats);
        return Math.min(distanceToNearestOnLeft(seats)[best],
                distanceToNearestOnRight(seats)[best]);
    }

    private static int[] distanceToNearestOnLeft(int[] seats) {
        int n = seats.length;
        int[] dist = new int[n];
        int lastOccupied = -1;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                lastOccupied = i;
                dist[i] = 0;
            } else {
                // nobody on the left yet -> effectively infinite
                dist[i] = (lastOccupied == -1) ? Integer.MAX_VALUE : i - lastOccupied;
            }
        }
        return dist;
    }

    private static int[] distanceToNearestOnRight(int[] seats) {
        int n = seats.length;
        int[] dist = new int[n];
        int lastOccupied = -1;
        for (int i = n - 1; i >= 0; i--) {
            if (seats[i] == 1) {
                lastOccupied = i;
                dist[i] = 0;
            } else {
                dist[i] = (lastOccupied == -1) ? Integer.MAX_VALUE : lastOccupied - i;
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        run("case 1 typical   ", new int[]{1, 0, 0, 0, 1, 0, 1}, 2, 2);
        run("case 2 right edge", new int[]{1, 0, 0, 0}, 3, 3);
        run("case 3 left edge ", new int[]{0, 0, 1}, 0, 2);
        run("case 4 tie       ", new int[]{1, 0, 1, 0, 1}, 1, 1);
    }

    private static void run(String label, int[] seats, int expectedIndex, int expectedDistance) {
        System.out.println(label + ": index " + findBestSeat(seats)
                + "   expected " + expectedIndex
                + " | distance " + maxDistToClosest(seats)
                + "   expected " + expectedDistance);
    }
}
