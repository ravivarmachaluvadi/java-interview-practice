/*
 * =====================================================================
 *  Bottom View of a Binary Tree                       GfG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Stand underneath the tree and look up. For every vertical column of the drawing you see
 *   exactly one node: the lowest one in that column. Return those values left column to right
 *   column. When two nodes sit in the same column AND on the same level, the one visited later
 *   in level order (the right-hand one) is the one that is seen.
 *
 * EXAMPLE
 *             20            horizontal distance (hd): root = 0, left child = hd-1, right = hd+1
 *            /  \
 *           8    22         hd: 20->0  8->-1  22->1  5->-2  3->0  25->2  10->-1  14->1
 *          / \     \
 *         5   3     25   ->  [5, 10, 3, 14, 25]   columns -2, -1, 0, 1, 2
 *            / \
 *          10   14
 *
 *   single node 20                 ->  [20]   one column, one node
 *   null root                      ->  []     nothing to see
 *   1 with left 2, right 3,
 *   2.right = 4, 3.left = 5        ->  [2, 5, 3]   4 and 5 share column 0 and level 2;
 *                                                  5 is seen later in BFS, so 5 wins
 *
 * APPROACH  (horizontal-distance map filled by level-order BFS)
 *   1. Give every node a column number hd: root gets 0, a left child gets hd - 1, a right child
 *      gets hd + 1. Carry the pair (node, hd) through the queue.
 *   2. BFS the tree. On visiting a node, write map[hd] = node value, overwriting whatever was
 *      there. Because BFS moves strictly downward, the last write per column is the lowest node.
 *   3. Use a TreeMap so the columns come out sorted from leftmost to rightmost with no extra work.
 *
 * KEY INSIGHT
 *   Adding one coordinate - the horizontal distance - turns a tree question into a grouping
 *   question: "give me one value per column". Bottom view keeps the last write, top view keeps
 *   the first (putIfAbsent), vertical order keeps them all. Same traversal, one line different.
 *   The reason BFS needs no level comparison is that BFS already visits in depth order; a DFS
 *   version must store the level alongside the value and compare it, as shown below.
 *
 * COMPLEXITY
 *   Time  O(n log n)  every node is visited once, each TreeMap write costs O(log n) columns
 *   Space O(n)        the queue holds one level, the map holds one entry per column
 *
 * INTERVIEW FOLLOW-UPS
 *   - Top view: change map.put to map.putIfAbsent, keeping the first node seen in each column.
 *   - Vertical order traversal (LeetCode 987): collect every node per column, then break ties by
 *     level and then by value - this is where "same column, same level" ordering gets strict.
 *   - Why can a naive recursive DFS get this wrong? It can overwrite a deep node with a shallow
 *     one visited later; bottomViewDfs() below fixes that by storing the level too.
 *   - Left/right view are the same idea keyed by level instead of by column.
 *
 * RUN
 *   main() runs 4 cases (typical tree, single node, empty tree, same-column tie) against the
 *   BFS version and the DFS version, and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

class BottomViewBinaryTree {

    static class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }

    /** A node together with the column it sits in, so the queue can carry both. */
    static class Pair {
        Node node;
        int hd;

        Pair(Node node, int hd) {
            this.node = node;
            this.hd = hd;
        }
    }

    public static List<Integer> bottomView(Node root) {
        Map<Integer, Integer> columnToValue = new TreeMap<>(); // TreeMap keeps columns sorted
        if (root == null) return new ArrayList<>();

        Queue<Pair> queue = new ArrayDeque<>();
        queue.add(new Pair(root, 0));

        while (!queue.isEmpty()) {
            Pair current = queue.poll();
            Node node = current.node;
            int hd = current.hd;

            // Overwrite: BFS goes top down, so the final write for a column is its lowest node.
            columnToValue.put(hd, node.data);

            if (node.left != null) queue.add(new Pair(node.left, hd - 1));
            if (node.right != null) queue.add(new Pair(node.right, hd + 1));
        }
        return new ArrayList<>(columnToValue.values());
    }

    /**
     * DFS version. Without BFS's depth ordering we must remember the level of the node we stored
     * and only overwrite when the new node is at least as deep - that comparison IS the fix for
     * the classic bug where a shallow node visited later hides a deeper one.
     */
    public static List<Integer> bottomViewDfs(Node root) {
        Map<Integer, int[]> columnToLevelAndValue = new TreeMap<>(); // hd -> {level, value}
        dfs(root, 0, 0, columnToLevelAndValue);

        List<Integer> result = new ArrayList<>();
        for (int[] levelAndValue : columnToLevelAndValue.values()) {
            result.add(levelAndValue[1]);
        }
        return result;
    }

    private static void dfs(Node node, int hd, int level,
                            Map<Integer, int[]> columnToLevelAndValue) {
        if (node == null) return;

        int[] stored = columnToLevelAndValue.get(hd);
        // ">=" so that at equal depth the later (right-hand) node wins, matching the BFS version.
        if (stored == null || level >= stored[0]) {
            columnToLevelAndValue.put(hd, new int[]{level, node.data});
        }
        dfs(node.left, hd - 1, level + 1, columnToLevelAndValue);
        dfs(node.right, hd + 1, level + 1, columnToLevelAndValue);
    }

    public static void main(String[] args) {
        //           20
        //          /  \
        //        8     22
        //       / \      \
        //      5   3      25
        //         / \
        //       10   14
        Node root = new Node(20);
        root.left = new Node(8);
        root.right = new Node(22);
        root.left.left = new Node(5);
        root.left.right = new Node(3);
        root.right.right = new Node(25);
        root.left.right.left = new Node(10);
        root.left.right.right = new Node(14);

        //     1          4 and 5 both land in column 0 on level 2
        //    / \
        //   2   3
        //    \  /
        //     4 5
        Node tie = new Node(1);
        tie.left = new Node(2);
        tie.right = new Node(3);
        tie.left.right = new Node(4);
        tie.right.left = new Node(5);

        System.out.println("-- BFS version --");
        print("case 1 typical    ", bottomView(root), "[5, 10, 3, 14, 25]");
        print("case 2 single node", bottomView(new Node(20)), "[20]");
        print("case 3 empty tree ", bottomView(null), "[]");
        print("case 4 column tie ", bottomView(tie), "[2, 5, 3]");

        System.out.println("-- DFS version with level tracking (same answers) --");
        print("case 5 typical    ", bottomViewDfs(root), "[5, 10, 3, 14, 25]");
        print("case 6 single node", bottomViewDfs(new Node(20)), "[20]");
        print("case 7 empty tree ", bottomViewDfs(null), "[]");
        print("case 8 column tie ", bottomViewDfs(tie), "[2, 5, 3]");
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
