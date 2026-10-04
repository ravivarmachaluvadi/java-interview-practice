/*
 * =====================================================================
 *  P060 Tree Coordinates: Vertical Order and Views   Canonical LC 987 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 987, Vertical Order Traversal of a Binary Tree)
 *   The root is at (row 0, col 0); a left child is at (row + 1, col - 1), a right child at
 *   (row + 1, col + 1). Return the columns from left to right; inside a column, order by
 *   row, and break ties at the same (row, col) by value.
 *
 * EXAMPLE
 *   [3,9,20,null,null,15,7]  ->  [[9],[3,15],[20],[7]]
 *   [1,2,3,4,6,5,7]          ->  [[4],[2],[1,5,6],[3],[7]]    5 and 6 share (2, 0): by value
 *
 * RECOGNIZE WHEN
 *   - "vertical", "column", "top view", "bottom view", "seen from above / below".
 *   - Anything that needs a horizontal distance per node: tag each node with (row, col).
 *   Not this if: left / right side views -> per-level BFS (P052_BfsByLevel, LC 199).
 *
 * TEMPLATE
 *   traverse with coordinates: visit(node, row, col)
 *       record (col, row, node.val); recurse (row + 1, col - 1) and (row + 1, col + 1)
 *   group by col (TreeMap keeps columns sorted), order inside a group as required
 *   top view:    first node seen in each column in BFS order
 *   bottom view: last node seen in each column in BFS order
 *
 * APPROACH
 *   1. DFS records (col, row, value) for every node.
 *   2. Sort by col, then row, then value.
 *   3. Cut the sorted list into groups of equal col.
 *
 * KEY INSIGHT
 *   Give every node explicit coordinates, and "vertical" questions become sorting and
 *   grouping problems. BFS already visits rows in order, so for top / bottom views you only
 *   keep the first or last node per column.
 *
 * COMPLEXITY
 *   Time O(n log n) for the sort, space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Top View (GfG)                   BFS with columns; keep the FIRST per column
 *   [coded] Bottom View (GfG)                BFS with columns; keep the LAST per column
 *           LC 314  Vertical Order (BFS)     ties in a column keep BFS order, no value sort
 *           LC 199  Right Side View          rows instead of columns -> P052_BfsByLevel
 *           Diagonal traversal               group by (col - row) or count left turns
 *
 * PITFALLS
 *   - LC 987 ties: same row AND column -> sort by value; DFS order is not enough.
 *   - Top / bottom views need BFS (row order); DFS can see a deeper node first.
 *   - Use a TreeMap (or track min and max column) to output columns in order.
 *
 * DEEP DIVE
 *   C21_BottomViewBinaryTree (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class VerticalOrderViews {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        // Builds a tree from LeetCode's level-order list (null = missing child).
        static TreeNode of(Integer... v) {
            if (v.length == 0 || v[0] == null) {
                return null;
            }
            TreeNode root = new TreeNode(v[0]);
            Deque<TreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            int i = 1;
            while (i < v.length) {
                TreeNode n = queue.poll();
                if (v[i] != null) {
                    n.left = new TreeNode(v[i]);
                    queue.add(n.left);
                }
                i++;
                if (i < v.length && v[i] != null) {
                    n.right = new TreeNode(v[i]);
                    queue.add(n.right);
                }
                i++;
            }
            return root;
        }
    }

    // Canonical LC 987.
    static List<List<Integer>> verticalTraversal(TreeNode root) {
        List<int[]> cells = new ArrayList<>();                  // {col, row, val}
        collect(root, 0, 0, cells);
        cells.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0])
                : a[1] != b[1] ? Integer.compare(a[1], b[1]) : Integer.compare(a[2], b[2]));
        List<List<Integer>> out = new ArrayList<>();
        Integer currentCol = null;
        for (int[] c : cells) {
            if (currentCol == null || c[0] != currentCol) {
                out.add(new ArrayList<>());
                currentCol = c[0];
            }
            out.get(out.size() - 1).add(c[2]);
        }
        return out;
    }

    private static void collect(TreeNode node, int row, int col, List<int[]> cells) {
        if (node == null) {
            return;
        }
        cells.add(new int[]{col, row, node.val});
        collect(node.left, row + 1, col - 1, cells);
        collect(node.right, row + 1, col + 1, cells);
    }

    // Top view: the first node met in each column during BFS.
    static List<Integer> topView(TreeNode root) {
        return view(root, true);
    }

    // Bottom view: the last node met in each column during BFS.
    static List<Integer> bottomView(TreeNode root) {
        return view(root, false);
    }

    private static List<Integer> view(TreeNode root, boolean firstWins) {
        Map<Integer, Integer> byColumn = new TreeMap<>();
        if (root != null) {
            Deque<Object[]> queue = new ArrayDeque<>();          // {node, col}
            queue.add(new Object[]{root, 0});
            while (!queue.isEmpty()) {
                Object[] e = queue.poll();
                TreeNode n = (TreeNode) e[0];
                int col = (int) e[1];
                if (!firstWins || !byColumn.containsKey(col)) {
                    byColumn.put(col, n.val);
                }
                if (n.left != null) {
                    queue.add(new Object[]{n.left, col - 1});
                }
                if (n.right != null) {
                    queue.add(new Object[]{n.right, col + 1});
                }
            }
        }
        return new ArrayList<>(byColumn.values());
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 987 five nodes", verticalTraversal(TreeNode.of(3, 9, 20, null, null, 15, 7)),
                "[[9], [3, 15], [20], [7]]");
        check("LC 987 tie broken by value", verticalTraversal(TreeNode.of(1, 2, 3, 4, 6, 5, 7)),
                "[[4], [2], [1, 5, 6], [3], [7]]");
        check("LC 987 single", verticalTraversal(TreeNode.of(1)), "[[1]]");

        check("top view seven nodes", topView(TreeNode.of(1, 2, 3, 4, 5, 6, 7)), "[4, 2, 1, 3, 7]");
        check("top view hidden right child",
                topView(TreeNode.of(1, 2, 3, null, 4, null, null, null, 5, null, 6)),
                "[2, 1, 3, 6]");

        check("bottom view nine nodes",
                bottomView(TreeNode.of(20, 8, 22, 5, 3, null, 25, null, null, 10, 14)),
                "[5, 10, 3, 14, 25]");
        check("bottom view single", bottomView(TreeNode.of(1)), "[1]");
    }
}
