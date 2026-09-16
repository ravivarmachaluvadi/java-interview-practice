/**
 * Constraints:
 * <p>
 * colour ranging from 1 to m -> m colours
 * <p>
 * nodes ranging from 0 to n-1 -> n number of nodes
 */
class MColoringProblem {

    // Function to check if it's safe to color vertex v with color c
    static boolean isSafe(int currentNode,
                          int[][] graph,
                          int[] color,
                          int currentColor,
                          int noOfNodes) {
        for (int i = 0; i < noOfNodes; i++) {
            // if there is an edge and the neighbor already has this color
            // graph[currentNode][i] == 1 means currNode and node i have edge
            // both of them adjacent to each other and also we are checking
            // color of node i means adjacent node color and currColor for currNode
            // are same then return false
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
        // with no two adjacent nodes didn't have same colour
        // Base case
        if (currentNode == noOfNodes) return true;

        // Try all colors for the current vertex
        for (int currentColor = 1; currentColor <= possibleColorsSet; currentColor++) {
            if (isSafe(currentNode, graph, color, currentColor, noOfNodes)) {
                color[currentNode] = currentColor;
                // colout current node with color , go for next node to color
                if (graphColoringUtil(graph, possibleColorsSet, color, currentNode + 1, noOfNodes))
                    return true;
                // resetting colurs
                color[currentNode] = 0;
            }
        }
        return false;
    }

    public static boolean graphColoring(int[][] graph, int possibleColors, int noOfNodes) {
        // Initialize all vertices with no color (color 0)
        int[] colors = new int[noOfNodes];

        // Start solving the problem using backtracking
        if (!graphColoringUtil(graph, possibleColors, colors, 0, noOfNodes)) {
            System.out.println("Solution does not exist");
            return false;
        }

        // Print the solution (color assignment)
        System.out.println("Solution Exists: Following are the assigned colors:");
        for (int i = 0; i < noOfNodes; i++) {
            System.out.print(colors[i] + " ");
        }
        System.out.println();
        return true;
    }

    public static void main(String[] args) {
        // Example: 4 vertices, 3 colors
        int noOfNodes = 4;
        int possibleColors = 3;

        // Graph represented by an adjacency matrix
        int[][] graph = {
                {0, 1, 1, 1},
                {1, 0, 1, 0},
                {1, 1, 0, 1},
                {1, 0, 1, 0}
        };

        graphColoring(graph, possibleColors, noOfNodes);
    }
}
