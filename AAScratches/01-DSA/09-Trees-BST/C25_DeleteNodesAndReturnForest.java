/*
 * =====================================================================
 *  Delete Nodes And Return Forest                LeetCode 1110 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a binary tree with distinct values and a list of values to delete, remove those
 *   nodes. Deleting a node disconnects its children, so the tree falls apart into a
 *   forest. Return the roots of all remaining trees, in any order.
 *
 *   Fixed: TreeNode and Solution were inner (non-static) classes, so the static main()
 *   could not instantiate them - the file did not compile. Both are now static.
 *
 * EXAMPLE
 *         1              delete [3, 5]      1        6     7
 *        / \             ------------>     /
 *       2   3                             2
 *      / \ / \                           /
 *     4  5 6  7                         4
 *   ->  forest preorders [[1, 2, 4], [6], [7]]
 *   tree 1(2, 3), delete [1]  ->  [[2], [3]]   (deleting the root promotes both children)
 *   tree 1(2, 3), delete []   ->  [[1, 2, 3]]  (nothing removed, one tree back)
 *
 * APPROACH  (one DFS carrying "am I currently a root?" downward)
 *   1. Put the values to delete in a HashSet for O(1) membership tests.
 *   2. dfs(node, isRoot) returns the node itself, or null if the node is deleted.
 *   3. A node joins the forest when it is a root AND is not deleted. A node is a root if
 *      it is the original root, or if its parent was deleted.
 *   4. Recurse with isRoot = "this node is being deleted", and ASSIGN the results back:
 *      node.left = dfs(node.left, ...). That assignment is what actually unlinks a
 *      deleted child from its surviving parent.
 *   5. Return null for a deleted node so the parent's assignment clears the link.
 *
 * KEY INSIGHT
 *   Two directions of information meet in one traversal: "is my parent gone?" flows DOWN
 *   as a parameter, and "did I survive?" flows UP as the return value. Re-assigning the
 *   child pointer from the recursive call is the idiom for deleting inside a tree; the
 *   classic bug is calling dfs(node.left, ...) and ignoring what it returns, which leaves
 *   deleted nodes still attached.
 *
 * COMPLEXITY
 *   Time  O(n + d)  each node is visited once, d values go into the set.
 *   Space O(n)      the set plus recursion depth O(h), O(n) for a skewed tree.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why must a deleted node's children still be visited before the node is dropped?
 *   - What changes if the values are NOT distinct?
 *   - Can you do it iteratively (BFS) while still keeping the parent-deleted flag?
 *   - Return the forest sorted by tree size - what does that cost on top?
 *
 * RUN
 *   main() runs four cases (typical, delete the root, delete nothing, delete everything)
 *   and prints the preorder of each returned forest against the expected lists.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// You will be given a binary tree with unique values and a list of values to delete.
// Deleting nodes breaks the tree into several subtrees - return the roots of all of them.
class DeleteNodesAndReturnForest {

    static class TreeNode {
        int val;
        TreeNode left, right;

        TreeNode(int v) {
            val = v;
        }
    }

    static class Solution {
        public List<TreeNode> delNodes(TreeNode root, int[] toDelete) {
            List<TreeNode> forest = new ArrayList<>();
            Set<Integer> deleteSet = new HashSet<>();
            for (int value : toDelete) {
                deleteSet.add(value);
            }
            dfs(root, true, deleteSet, forest); // the original root starts out as a root
            return forest;
        }

        /** Returns the node, or null when it is deleted so the parent link is cleared. */
        private TreeNode dfs(TreeNode node, boolean isRoot, Set<Integer> deleteSet,
                             List<TreeNode> forest) {
            if (node == null) {
                return null;
            }

            boolean isDeleted = deleteSet.contains(node.val);
            if (isRoot && !isDeleted) {
                forest.add(node); // survives and has no parent: a tree of the forest
            }

            // A child becomes a root exactly when this node is deleted. Assigning the
            // result back is what detaches deleted children.
            node.left = dfs(node.left, isDeleted, deleteSet, forest);
            node.right = dfs(node.right, isDeleted, deleteSet, forest);

            return isDeleted ? null : node;
        }
    }

    // ---------- test helpers ----------

    private static List<Integer> preorderOf(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        collectPreorder(root, out);
        return out;
    }

    private static void collectPreorder(TreeNode node, List<Integer> out) {
        if (node == null) {
            return;
        }
        out.add(node.val);
        collectPreorder(node.left, out);
        collectPreorder(node.right, out);
    }

    private static List<List<Integer>> forestPreorders(List<TreeNode> forest) {
        List<List<Integer>> out = new ArrayList<>();
        for (TreeNode tree : forest) {
            out.add(preorderOf(tree));
        }
        return out;
    }

    /**
     *       1
     *      / \
     *     2   3
     *    / \ / \
     *   4  5 6  7
     */
    private static TreeNode sampleTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        return root;
    }

    private static TreeNode smallTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        return root;
    }

    private static void run(String label, TreeNode root, int[] toDelete,
                            List<List<Integer>> expected) {
        List<TreeNode> forest = new Solution().delNodes(root, toDelete);
        System.out.println(label + " delete " + Arrays.toString(toDelete) + " -> "
                + forestPreorders(forest) + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical      ", sampleTree(), new int[]{3, 5},
                Arrays.asList(Arrays.asList(1, 2, 4), Arrays.asList(6), Arrays.asList(7)));

        run("case 2 delete root  ", smallTree(), new int[]{1},
                Arrays.asList(Arrays.asList(2), Arrays.asList(3)));

        run("case 3 delete none  ", smallTree(), new int[]{},
                Arrays.asList(Arrays.asList(1, 2, 3)));

        run("case 4 delete all   ", smallTree(), new int[]{1, 2, 3},
                Collections.<List<Integer>>emptyList());
    }
}
