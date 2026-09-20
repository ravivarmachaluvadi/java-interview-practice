/*
 * =====================================================================
 *  M-Coloring Problem                         Classic backtracking | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an undirected graph of n nodes (0 .. n-1) as an adjacency matrix and m
 *   available colours (1 .. m), decide whether every node can be coloured so that no
 *   two nodes joined by an edge share a colour. Return true/false; the assignment
 *   itself is a useful by-product. graph[i][j] == 1 means i and j are adjacent.
 *
 * EXAMPLE
 *   n=4, m=3, edges 0-1, 0-2, 0-3, 1-2, 2-3  ->  true, colours [1, 2, 3, 2]
 *   the same graph with m=2                  ->  false  (0,1,2 form a triangle)
 *   K4 (every pair joined), m=3              ->  false  (a clique of 4 needs 4 colours)
 *   n=1, m=1                                 ->  true, colours [1]   edge case
 *
 * APPROACH  (assign / check / undo, one node per recursion level)
 *   1. colour[] holds the colour of each node, 0 meaning "not coloured yet".
 *   2. Recurse on node index: level k decides the colour of node k, so the depth of
 *      the tree is n and the branching factor is m.
 *   3. Base case: currentNode == n means all nodes are coloured - a full solution.
 *   4. For each colour 1..m, isSafe scans this node's row of the matrix: if some
 *      neighbour already carries this colour the choice is rejected.
 *   5. Safe -> assign, recurse on the next node, and return true immediately if the
 *      rest of the graph could be finished.
 *   6. The recursion failed -> reset colour[node] to 0 (the undo) and try the next colour.
 *      All m colours exhausted -> return false and let the caller re-choose.
 *
 * KEY INSIGHT
 *   Nodes already coloured are the only constraint that matters, so isSafe never has to
 *   look ahead - it only checks the past. That is the general backtracking contract:
 *   a partial assignment is extended only while it stays feasible, so an impossible
 *   prefix is abandoned at the first conflict instead of at the leaf. Resetting to 0 is
 *   not cosmetic: without it a stale colour would make a later isSafe lie.
 *
 * COMPLEXITY
 *   Time  O(m^n * n)  m choices at each of n levels, and each isSafe scans a row of n.
 *   Space O(n)        the colour array plus recursion depth n.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Find the chromatic number: run this for m = 1, 2, 3, ... and take the first true.
 *   - 2-colouring is the special case "is the graph bipartite?" - BFS solves it in O(V+E).
 *   - Speed-ups: colour the highest-degree node first, or keep a per-node bitmask of
 *     colours already used by neighbours to make isSafe O(1).
 *   - N-Queens is this problem with a cleverer isSafe (row + two diagonal arrays).
 *
 * RUN
 *   main() runs 4 cases (solvable, same graph with too few colours, K4, single node)
 *   and prints actual vs expected.
 */

import java.util.Arrays;

class MColoringProblem {

    /** Can currentNode take currentColor without clashing with an adjacent node? */
    static boolean isSafe(int currentNode,
                          int[][] graph,
                          int[] color,
                          int currentColor,
                          int noOfNodes) {
        for (int i = 0; i < noOfNodes; i++) {
            // graph[currentNode][i] == 1 -> i is a neighbour; if it already wears this
            // colour the assignment is illegal. Uncoloured neighbours hold 0, never a
            // real colour, so they can never trigger this test.
            if (graph[currentNode][i] == 1 && color[i] == currentColor) {
                return false;
            }
        }
        return true;
    }

    static boolean graphColoringUtil(int[][] graph,
                                     int possibleColorsSet,
                                     int[] color,
                                     int currentNode,
                                     int noOfNodes) {
        // every node has a colour and no check ever failed -> done
        if (currentNode == noOfNodes) return true;

        for (int currentColor = 1; currentColor <= possibleColorsSet; currentColor++) {
            if (isSafe(currentNode, graph, color, currentColor, noOfNodes)) {
                color[currentNode] = currentColor;
                if (graphColoringUtil(graph, possibleColorsSet, color,
                        currentNode + 1, noOfNodes)) {
                    return true;    // one solution is enough, stop unwinding
                }
                color[currentNode] = 0;   // undo, or a stale colour would fool isSafe later
            }
        }
        return false;   // no colour works here; the caller must re-choose
    }

    /** @return the colour of each node, or null when the graph is not m-colourable. */
    public static int[] solveColoring(int[][] graph, int possibleColors, int noOfNodes) {
        int[] colors = new int[noOfNodes];   // 0 everywhere = nothing coloured yet
        return graphColoringUtil(graph, possibleColors, colors, 0, noOfNodes) ? colors : null;
    }

    public static boolean graphColoring(int[][] graph, int possibleColors, int noOfNodes) {
        return solveColoring(graph, possibleColors, noOfNodes) != null;
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    /** Complete graph on n nodes: every pair joined, so it needs exactly n colours. */
    private static int[][] completeGraph(int n) {
        int[][] graph = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) graph[i][j] = 1;
            }
        }
        return graph;
    }

    public static void main(String[] args) {
        int[][] graph = {
                {0, 1, 1, 1},
                {1, 0, 1, 0},
                {1, 1, 0, 1},
                {1, 0, 1, 0}
        };

        print("case 1 n=4 m=3 solvable?", graphColoring(graph, 3, 4), "true");
        print("case 1 colours", Arrays.toString(solveColoring(graph, 3, 4)), "[1, 2, 3, 2]");

        print("case 2 n=4 m=2 (edge: triangle 0-1-2 needs 3)",
                graphColoring(graph, 2, 4), "false");

        print("case 3 K4 with m=3 (tricky: clique of 4)",
                graphColoring(completeGraph(4), 3, 4), "false");
        print("case 3 K4 with m=4",
                Arrays.toString(solveColoring(completeGraph(4), 4, 4)), "[1, 2, 3, 4]");

        print("case 4 single node, m=1 (edge)",
                Arrays.toString(solveColoring(new int[][]{{0}}, 1, 1)), "[1]");
    }
}
