/*
 * =====================================================================
 *  P065 Grid DFS / BFS: Flood Fill and Islands   Canonical LC 200 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 200, Number of Islands)
 *   grid holds '1' (land) and '0' (water). An island is land connected up, down, left or
 *   right. Return the number of islands.
 *
 * EXAMPLE
 *   11110 / 11010 / 11000 / 00000  ->  1
 *   11000 / 11000 / 00100 / 00011  ->  3
 *
 * RECOGNIZE WHEN
 *   - A 2D grid where cells connect to their 4 (or 8) neighbours, and you count, measure,
 *     recolour or capture CONNECTED REGIONS.
 *   - "touching the border" regions are special: start the fill FROM the border.
 *   Not this if: you need the SHORTEST distance -> BFS levels (P067_BfsShortestPath,
 *   P068_MultiSourceBfs); regions merge over time -> P071_UnionFind.
 *
 * TEMPLATE
 *   for every cell: if it is unvisited land: count++, fill(cell)
 *   fill(r, c):
 *       if out of bounds or not land or visited: return
 *       mark visited (or overwrite the cell)
 *       fill the 4 neighbours (r+1, c), (r-1, c), (r, c+1), (r, c-1)
 *   border trick: fill from every border cell first, then the untouched regions are enclosed
 *
 * APPROACH
 *   1. Scan the grid. Each land cell not yet visited starts a new island.
 *   2. Flood-fill it, sinking ('0') every connected land cell so it is never counted again.
 *
 * KEY INSIGHT
 *   A grid is a graph whose edges are implicit (neighbouring cells). Counting connected
 *   components is "for each unvisited node, start a traversal and count the starts". Marking
 *   cells IN the grid doubles as the visited set.
 *
 * COMPLEXITY
 *   Time O(R * C), space O(R * C) recursion in the worst case (use BFS / a stack for huge
 *   grids).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 695  Max Area of Island       fill returns the number of cells it covered
 *   [coded] LC 733  Flood Fill               recolour one region; stop if the colour is the same
 *   [coded] LC 130  Surrounded Regions       fill from border 'O's first (mark safe), then
 *                                            flip the rest
 *   [coded] LC 1020 Number of Enclaves       sink border land first, then count what is left
 *           LC 417  Pacific Atlantic Flow    fill uphill from each ocean's border; intersect
 *           LC 1905 Count Sub Islands        an island counts if all its cells are land in grid1
 *           LC 694  Distinct Islands         record the DFS move path as a shape signature
 *           LC 463  Island Perimeter         count land-to-water / land-to-border edges
 *
 * PITFALLS
 *   - Check bounds BEFORE reading grid[r][c].
 *   - LC 733: if the new colour equals the old one, return at once (or loop forever).
 *   - Recursion depth: a 300x300 all-land grid recurses 90,000 deep; BFS is safer.
 *
 * DEEP DIVE
 *   B02_NumberOfIslands, B01_FloodFill, B05_NumberOfEnclaves (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class GridFloodFill {

    // Canonical LC 200.
    static int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    sink(grid, r, c);
                }
            }
        }
        return count;
    }

    private static void sink(char[][] g, int r, int c) {
        if (r < 0 || c < 0 || r >= g.length || c >= g[0].length || g[r][c] != '1') {
            return;
        }
        g[r][c] = '0';
        sink(g, r + 1, c);
        sink(g, r - 1, c);
        sink(g, r, c + 1);
        sink(g, r, c - 1);
    }

    // LC 695.
    static int maxAreaOfIsland(int[][] grid) {
        int best = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                best = Math.max(best, area(grid, r, c));
            }
        }
        return best;
    }

    private static int area(int[][] g, int r, int c) {
        if (r < 0 || c < 0 || r >= g.length || c >= g[0].length || g[r][c] != 1) {
            return 0;
        }
        g[r][c] = 0;
        return 1 + area(g, r + 1, c) + area(g, r - 1, c) + area(g, r, c + 1) + area(g, r, c - 1);
    }

    // LC 733.
    static int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int old = image[sr][sc];
        if (old != color) {
            paint(image, sr, sc, old, color);
        }
        return image;
    }

    private static void paint(int[][] img, int r, int c, int old, int color) {
        if (r < 0 || c < 0 || r >= img.length || c >= img[0].length || img[r][c] != old) {
            return;
        }
        img[r][c] = color;
        paint(img, r + 1, c, old, color);
        paint(img, r - 1, c, old, color);
        paint(img, r, c + 1, old, color);
        paint(img, r, c - 1, old, color);
    }

    // LC 130: 'O' regions not connected to the border become 'X'.
    static char[][] solve(char[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            mark(board, r, 0);
            mark(board, r, cols - 1);
        }
        for (int c = 0; c < cols; c++) {
            mark(board, 0, c);
            mark(board, rows - 1, c);
        }
        for (char[] row : board) {
            for (int c = 0; c < cols; c++) {
                row[c] = row[c] == 'S' ? 'O' : 'X';   // S = safe (touches the border)
            }
        }
        return board;
    }

    private static void mark(char[][] b, int r, int c) {
        if (r < 0 || c < 0 || r >= b.length || c >= b[0].length || b[r][c] != 'O') {
            return;
        }
        b[r][c] = 'S';
        mark(b, r + 1, c);
        mark(b, r - 1, c);
        mark(b, r, c + 1);
        mark(b, r, c - 1);
    }

    // LC 1020: land cells from which you cannot walk off the grid.
    static int numEnclaves(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        for (int r = 0; r < rows; r++) {
            area(grid, r, 0);
            area(grid, r, cols - 1);
        }
        for (int c = 0; c < cols; c++) {
            area(grid, 0, c);
            area(grid, rows - 1, c);
        }
        int left = 0;
        for (int[] row : grid) {
            for (int v : row) {
                left += v;
            }
        }
        return left;
    }

    static char[][] chars(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            g[i] = rows[i].toCharArray();
        }
        return g;
    }

    static String show(char[][] g) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : g) {
            sb.append(sb.length() == 0 ? "" : "/").append(row);
        }
        return sb.toString();
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 200 one island", numIslands(chars("11110", "11010", "11000", "00000")), 1);
        check("LC 200 three islands", numIslands(chars("11000", "11000", "00100", "00011")), 3);
        check("LC 200 diagonal does not connect", numIslands(chars("10", "01")), 2);

        int[][] g = {
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0}};
        check("LC 695 8x13 grid", maxAreaOfIsland(g), 6);
        check("LC 695 all water", maxAreaOfIsland(new int[][]{{0, 0, 0, 0}}), 0);

        check("LC 733 recolour",
                Arrays.deepToString(floodFill(new int[][]{{1, 1, 1}, {1, 1, 0}, {1, 0, 1}},
                        1, 1, 2)),
                "[[2, 2, 2], [2, 2, 0], [2, 0, 1]]");
        check("LC 733 same colour no loop",
                Arrays.deepToString(floodFill(new int[][]{{0, 0, 0}, {0, 0, 0}}, 0, 0, 0)),
                "[[0, 0, 0], [0, 0, 0]]");

        check("LC 130 one captured region", show(solve(chars("XXXX", "XOOX", "XXOX", "XOXX"))),
                "XXXX/XXXX/XXXX/XOXX");
        check("LC 130 single X", show(solve(chars("X"))), "X");

        check("LC 1020 three enclosed",
                numEnclaves(new int[][]{{0, 0, 0, 0}, {1, 0, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}}),
                3);
        check("LC 1020 all reach the edge",
                numEnclaves(new int[][]{{0, 1, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 0, 0}}),
                0);
    }
}
