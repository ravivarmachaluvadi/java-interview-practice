
/**
 * You are given an array representing a row of seats where seats[i] = 1 represents
 * a person sitting in the ith seat, and seats[i] = 0 represents that the ith seat
 * is empty (0-indexed).
 * <p>
 * There is at least one empty seat, and at least one person sitting.
 * <p>
 * Alex wants to sit in the seat such that the distance between him and the closest
 * person to him is maximized.
 * <p>
 * Return that maximum distance to the closest person.
 */
// The best seat is at index: 2
class BestSeat {
    public static void main(String[] args) {
        int[] seats = {1, 0, 0, 0, 1, 0, 1};
        int bestSeatIndex = findBestSeat(seats);
        System.out.println("The best seat is at index: " + bestSeatIndex);
    }

    public static int findBestSeat(int[] seats) {
        int n = seats.length;
        int[] leftDist = new int[n];
        int[] rightDist = new int[n];

        // Fill leftDist
        int lastOccupied = -1;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                lastOccupied = i;
                leftDist[i] = 0;
            } else {
                // distance with inclusive of current seat
                leftDist[i] = (lastOccupied == -1) ? Integer.MAX_VALUE : i - lastOccupied;
            }
        }

        // Fill rightDist
        lastOccupied = -1;
        for (int i = n - 1; i >= 0; i--) {
            if (seats[i] == 1) {
                lastOccupied = i;
                rightDist[i] = 0;
            } else {
                rightDist[i] = (lastOccupied == -1) ? Integer.MAX_VALUE : lastOccupied - i;
            }
        }
        // Find best seat
        int bestSeat = -1, maxDistance = -1;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 0) {
                int distance = Math.min(leftDist[i], rightDist[i]);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    bestSeat = i;
                }
            }
        }
        return bestSeat;
    }
}
