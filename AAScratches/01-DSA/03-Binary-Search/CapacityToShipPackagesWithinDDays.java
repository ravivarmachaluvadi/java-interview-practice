// https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/description/
// Minimum capacity to ship packages within D days (binary search on answer)

class CapacityToShipPackagesWithinDDays {
    public int shipWithinDays(int[] weights, int days) {
        int low = 0;
        int high = 0;
        for (int w : weights) {
            low = Math.max(low, w);   // capacity must be at least the heaviest package
            high += w;                // at most, ship everything in one day
        }

        // Binary search on capacity
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (canShip(weights, days, mid)) {
                // mid is feasible, try smaller capacity
                high = mid - 1;
            } else {
                // mid is not feasible, need larger capacity
                low = mid + 1;
            }
        }

        return low;
    }

    // Check if it's possible to ship all packages within 'days' days
    // using the given 'capacity'
    private boolean canShip(int[] weights, int days, int capacity) {
        int dayCount = 1;
        int currLoad = 0;

        for (int w : weights) {
            // Safety check (not strictly needed if low starts at max(weights))
            if (w > capacity) {
                return false;
            }

            if (currLoad + w > capacity) {
                // Need a new day
                dayCount++;
                currLoad = w; // start new day with current package
            } else {
                currLoad += w;
            }

            if (dayCount > days) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        CapacityToShipPackagesWithinDDays solution = new CapacityToShipPackagesWithinDDays();
        int[] weights = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int days = 5;
        int result = solution.shipWithinDays(weights, days);
        System.out.println("Minimum capacity to ship packages within " + days + " days: " + result);
        // Expected output: 15
    }
}