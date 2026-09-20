/*
 * =====================================================================
 *  Last Day Where You Can Still Cross                LeetCode 1970 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   A 1-based row x col grid starts as all land. On day i (1-based) the cell
 *   cells[i-1] turns to water. You cross by walking from any cell in the top row
 *   to any cell in the bottom row, moving in the 4 directions over land only.
 *   Return the last day on which a crossing still exists.
 *
 * EXAMPLE
 *   row=2, col=2, cells=[[1,1],[2,1],[1,2],[2,2]]  ->  2   column 2 is dry until day 3
 *   row=2, col=2, cells=[[1,1],[1,2],[2,1],[2,2]]  ->  1   top row is gone after day 2
 *   row=1, col=1, cells=[[1,1]]                    ->  0   the only cell floods on day 1
 *
 * APPROACH  (widest-path / max-min search with a max-heap)
 *   1. lastDryDay[r][c] = index i of cells[i], which is the last day that cell is
 *      still land (it floods on day i+1). Cells never listed stay dry forever
 *      (Integer.MAX_VALUE).
 *   2. A path is usable on day D only if every cell on it is dry on day D, so a
 *      path's value is the MINIMUM lastDryDay along it. We want the path whose
 *      minimum is as large as possible - a max-min, or "widest path", problem.
 *   3. Run Dijkstra with the roles flipped: a max-heap keyed on the best-known
 *      bottleneck, seeded with every top-row cell at its own lastDryDay. Popping
 *      a cell relaxes its neighbours to min(currentBottleneck, lastDryDay[nb]).
 *   4. The first time a bottom-row cell is popped, its bottleneck is the answer.
 *
 * KEY INSIGHT
 *   Swap "sum of weights" for "min of weights" and Dijkstra still works, because
 *   min is also monotone: extending a path can never raise its bottleneck. That
 *   is why marking a cell visited at PUSH time is safe here - the heap pops in
 *   non-increasing order, so the first value written to a cell is already its best.
 *   Recognise this shape whenever a question asks for the largest threshold that
 *   keeps something connected (max-min path, minimax path, bottleneck spanning tree).
 *
 * COMPLEXITY
 *   Time  O(row*col*log(row*col))   each cell is pushed once, 4 edges each
 *   Space O(row*col)                lastDryDay, visited, and the heap
 *
 * INTERVIEW FOLLOW-UPS
 *   - Solve it by binary searching the day and running a plain BFS per guess.
 *     Same O(row*col*log(row*col)); which is easier to get right under pressure?
 *   - Solve it with union-find by replaying the days backwards, adding land back
 *     and asking when top and bottom first connect.
 *   - What changes if diagonal moves are allowed? (8 directions, same algorithm)
 *   - Return the actual crossing path for that day, not just the day.
 *
 * RUN
 *   main() runs 4 cases (two LeetCode samples, a 3x3 grid, a 1x1 edge case)
 *   and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.PriorityQueue;

class LastDayToCrossPQ {

    private static final int[][] DIRS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public int latestDayToCross(int row, int col, int[][] cells) {
        // lastDryDay[r][c] = last day this cell is still land. cells[i] floods on
        // day i+1, so i itself is the last day it can be walked on.
        int[][] lastDryDay = new int[row + 1][col + 1]; // 1-based, row/col 0 unused
        for (int[] r : lastDryDay) {
            Arrays.fill(r, Integer.MAX_VALUE);
        }
        for (int i = 0; i < cells.length; i++) {
            lastDryDay[cells[i][0]][cells[i][1]] = i;
        }

        boolean[][] visited = new boolean[row + 1][col + 1];

        // Entries are {r, c, bottleneck}; the largest bottleneck comes out first.
        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(b[2], a[2]));

        for (int j = 1; j <= col; j++) {
            queue.add(new int[]{1, j, lastDryDay[1][j]});
            visited[1][j] = true;
        }

        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int r = top[0], c = top[1], bottleneck = top[2];

            if (r == row) {
                return bottleneck; // best max-min path from the top row reaches here
            }

            for (int[] dir : DIRS) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr <= 0 || nr > row || nc <= 0 || nc > col || visited[nr][nc]) {
                    continue;
                }
                // Safe to mark now: later pops have a bottleneck <= this one, so
                // no future path can improve on the value we are writing here.
                visited[nr][nc] = true;
                queue.add(new int[]{nr, nc, Math.min(bottleneck, lastDryDay[nr][nc])});
            }
        }

        return -1; // unreachable for valid input: day 0 always crosses
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        LastDayToCrossPQ solver = new LastDayToCrossPQ();

        int[][] sampleOne = {{1, 1}, {2, 1}, {1, 2}, {2, 2}};
        print("case 1 (2x2 sample)  ", solver.latestDayToCross(2, 2, sampleOne), 2);

        int[][] sampleTwo = {{1, 1}, {1, 2}, {2, 1}, {2, 2}};
        print("case 2 (2x2 sample)  ", solver.latestDayToCross(2, 2, sampleTwo), 1);

        int[][] threeByThree = {
                {1, 2}, {2, 1}, {3, 3},
                {2, 2}, {1, 1}, {1, 3},
                {2, 3}, {3, 2}, {3, 1}
        };
        print("case 3 (3x3 grid)    ", solver.latestDayToCross(3, 3, threeByThree), 3);

        // Edge case: one cell is both the top and the bottom row.
        int[][] singleCell = {{1, 1}};
        print("case 4 (1x1 edge)    ", solver.latestDayToCross(1, 1, singleCell), 0);
    }
}
