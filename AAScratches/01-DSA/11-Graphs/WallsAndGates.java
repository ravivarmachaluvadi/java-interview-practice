import java.util.LinkedList;
import java.util.Queue;

// LeetCode Link: https://leetcode.com/problems/walls-and-gates/

/**
 * You are given an m x n grid rooms initialized
 * <p>
 * with these three possible values.
 * <p>
 * -1 A wall or an obstacle.
 * <p>
 * 0 A gate.
 * <p>
 * INF Infinity means an empty room.
 */
class WallsAndGates {

    /**
     * 3 -1 0 1
     * 2 2 1 -1
     * 1 -1 2 -1
     * 0 -1 3 4
     */
    public void wallsAndGates(int[][] rooms) {
        if (rooms == null || rooms.length == 0 || rooms[0].length == 0) return;

        int rows = rooms.length;
        int cols = rooms[0].length;

        Queue<int[]> queue = new LinkedList<>();

        // Step 1: Add all gates to the queue
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rooms[i][j] == 0) {
                    queue.offer(new int[]{i, j});
                }
            }
        }

        // Directions: right, down, left, up
        int[] directions = {0, 1, 0, -1, 0};

        // Step 2: BFS from each gate to update distances
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            for (int i = 0; i < 4; i++) {
                int nx = x + directions[i];
                int ny = y + directions[i + 1];

                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && rooms[nx][ny] == Integer.MAX_VALUE) {
                    // assume empty room just adjacent to gate will
                    // 1 step away so update by incrementing by 1
                    rooms[nx][ny] = rooms[x][y] + 1;
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
    }

    public static void main(String[] args) {
        WallsAndGates solution = new WallsAndGates();

        // Example grid
        int[][] rooms = {
                {Integer.MAX_VALUE, -1, 0, Integer.MAX_VALUE},
                {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, -1},
                {Integer.MAX_VALUE, -1, Integer.MAX_VALUE, -1},
                {0, -1, Integer.MAX_VALUE, Integer.MAX_VALUE}
        };

        solution.wallsAndGates(rooms);

        for (int[] row : rooms) {
            for (int room : row) {
                System.out.print(room + " ");
            }
            System.out.println();
        }
    }
}
