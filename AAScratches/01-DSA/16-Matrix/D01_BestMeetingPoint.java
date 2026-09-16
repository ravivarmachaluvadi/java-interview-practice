import java.util.*;

class BestMeetingPoint {
    // Function to find the best meeting point
    public static int minTotalDistance(int[][] grid) {
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();

        // Step 1: Collect all row and column indices where there are people
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                    cols.add(j);
                }
            }
        }

        // Step 2: Sort rows and columns to get the median
        Collections.sort(rows);
        Collections.sort(cols);

        // Step 3: Find the median row and column for optimal meeting point
        int medianRow = rows.get(rows.size() / 2);
        int medianCol = cols.get(cols.size() / 2);

        // Step 4: Calculate the total distance from the optimal meeting point
        int totalDistance = 0;
        for (int i = 0; i < rows.size(); i++) {
            totalDistance += Math.abs(rows.get(i) - medianRow) + Math.abs(cols.get(i) - medianCol);
        }

        return totalDistance;
    }

    public static void main(String[] args) {
        // Example input:
        int[][] grid = {
                {1, 0, 0, 0, 1},
                {0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0},
        };

        // Expected output: 6
        // Explanation: The best meeting point is (1, 2), where the total distance is minimized.

        int result = minTotalDistance(grid);
        System.out.println("Minimum total distance: " + result);
    }
}
