/*
 * =====================================================================
 *  Construct Binary Tree from Preorder and Inorder   LeetCode 105 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the preorder and the inorder traversal of a binary tree whose node values are
 *   all distinct, rebuild the tree and return its root. The distinctness is what makes
 *   the answer unique: with duplicate values the same two traversals fit several trees.
 *
 * EXAMPLE
 *   preorder = [3, 9, 20, 15, 7], inorder = [9, 3, 15, 20, 7]   ->        3
 *                                                                        / \
 *                                                                       9   20
 *                                                                          /  \
 *                                                                        15    7
 *   preorder = [1, 2, 3], inorder = [3, 2, 1]  ->  left-leaning chain 1 -> 2 -> 3
 *   preorder = [], inorder = []                ->  null
 *
 * APPROACH  (preorder names the root, inorder splits it, a value->index map makes it O(1))
 *   1. Walk inorder once and store value -> index in a HashMap.
 *   2. The first value of the current preorder slice is the root of this subtree.
 *   3. Find that root in the inorder slice: values to its left form the left subtree,
 *      values to its right form the right subtree.
 *   4. leftCount = rootIndex - inStart says how many preorder slots right after the root
 *      belong to the left subtree; everything after those belongs to the right subtree.
 *   5. Recurse on the two (preorder slice, inorder slice) pairs; an empty slice is null.
 *
 * KEY INSIGHT
 *   Preorder tells you WHO the root is, inorder tells you WHERE the split is. The only
 *   arithmetic to get right is the size of the left subtree, because that is what cuts
 *   the preorder range. Re-scanning inorder for the root at every call is the O(n^2)
 *   version; the value->index map is the optimisation interviewers are waiting for.
 *
 * COMPLEXITY
 *   Time  O(n)  one map build, then constant work per node.
 *   Space O(n)  the map, plus recursion depth O(h), which is O(n) for a skewed tree.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same problem from inorder + postorder (LC 106): take the root from the END instead.
 *   - From preorder + postorder (LC 889) the tree is not unique - why?
 *   - What breaks when values repeat, and how would you change the input format to fix it?
 *   - Can you drop the index arguments and use one moving preorder pointer plus an
 *     inorder stop-value instead?
 *
 * RUN
 *   main() rebuilds four trees (typical, single node, skewed chain, empty) and prints the
 *   preorder and inorder of each rebuilt tree against the traversals it was built from.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ConstructBinaryTreeFromINPre {

    public TreeNode buildTree(List<Integer> preorder, List<Integer> inorder) {
        Map<Integer, Integer> indexOfValue = new HashMap<>();
        for (int i = 0; i < inorder.size(); i++) {
            indexOfValue.put(inorder.get(i), i); // value -> its position in inorder
        }
        return build(preorder, 0, preorder.size() - 1, 0, inorder.size() - 1, indexOfValue);
    }

    private TreeNode build(List<Integer> preorder, int preStart, int preEnd,
                           int inStart, int inEnd, Map<Integer, Integer> indexOfValue) {

        if (preStart > preEnd || inStart > inEnd) {
            return null; // empty slice: nothing left to build here
        }

        TreeNode root = new TreeNode(preorder.get(preStart));
        int rootIndexInorder = indexOfValue.get(root.val);
        int leftCount = rootIndexInorder - inStart; // nodes sitting in the left subtree

        root.left = build(preorder,
                preStart + 1, preStart + leftCount,
                inStart, rootIndexInorder - 1, indexOfValue);

        root.right = build(preorder,
                preStart + leftCount + 1, preEnd,
                rootIndexInorder + 1, inEnd, indexOfValue);

        return root;
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

    private static List<Integer> inorderOf(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        collectInorder(root, out);
        return out;
    }

    private static void collectInorder(TreeNode node, List<Integer> out) {
        if (node == null) {
            return;
        }
        collectInorder(node.left, out);
        out.add(node.val);
        collectInorder(node.right, out);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    /** Rebuilds the tree, then checks that its own traversals match the inputs. */
    private static void roundTrip(String label, List<Integer> preorder, List<Integer> inorder) {
        TreeNode root = new ConstructBinaryTreeFromINPre().buildTree(preorder, inorder);
        print(label + " preorder", preorderOf(root), preorder);
        print(label + " inorder ", inorderOf(root), inorder);
    }

    public static void main(String[] args) {
        roundTrip("case 1 typical    ",
                Arrays.asList(3, 9, 20, 15, 7),
                Arrays.asList(9, 3, 15, 20, 7));

        roundTrip("case 2 single node",
                Arrays.asList(1),
                Arrays.asList(1));

        // Every node is a left child: inorder is the reverse of preorder.
        roundTrip("case 3 left chain ",
                Arrays.asList(1, 2, 3),
                Arrays.asList(3, 2, 1));

        roundTrip("case 4 empty      ",
                Collections.<Integer>emptyList(),
                Collections.<Integer>emptyList());
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}
