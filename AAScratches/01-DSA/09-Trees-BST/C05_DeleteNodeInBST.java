/*
 * =====================================================================
 *  Delete Node in a BST                       LeetCode 450 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a BST and a key, remove the node holding that key and
 *   return the new root, keeping the BST ordering intact. If the key is not
 *   present the tree is returned unchanged. Values are unique.
 *
 * EXAMPLE
 *   tree [5,3,6,2,4,null,7], key = 3  ->  in-order 2 4 5 6 7
 *   tree [5,3,6,2,4,null,7], key = 9  ->  in-order 2 3 4 5 6 7 (key absent)
 *   tree [5], key = 5                 ->  empty tree (root becomes null)
 *
 * APPROACH A  (recursive, copy the in-order successor's value)
 *   1. Search like a normal BST lookup: key < node -> go left, key > node -> right.
 *   2. Reassign the child link from the recursive call (node.left = delete(...)).
 *      That is what actually removes the node - the parent forgets it.
 *   3. At the target node there are three cases:
 *      - no left child  -> return the right child (covers the leaf case too)
 *      - no right child -> return the left child
 *      - two children   -> copy the smallest value of the right subtree into
 *        this node, then delete that successor from the right subtree. The
 *        successor has no left child, so that second delete ends in one step.
 *
 * APPROACH B  (iterative, splice the left subtree under the successor)
 *   Walk down holding the parent, then replace the target with a rebuilt
 *   subtree: with two children, hang the whole left subtree as the left child
 *   of the leftmost node of the right subtree and promote the right subtree.
 *   No value copying and O(1) extra space, but the tree gets taller because
 *   the left subtree is pushed down by the height of the right subtree.
 *
 * KEY DECISIONS
 *   - Successor (min of right) vs predecessor (max of left): either is correct;
 *     always picking one side is what slowly unbalances a textbook BST.
 *   - A returns the replacement up the stack, so callers must write
 *     `root.left = delete(root.left, key)` and never a bare `delete(...)`.
 *   - Same in-order, different shapes: case 4 shows it by comparing heights.
 *
 * COMPLEXITY
 *   Time  O(h) for both - one root-to-node descent plus one successor descent
 *   Space A: O(h) recursion stack.  B: O(1), only a couple of pointers.
 *         h = height: O(log n) balanced, O(n) skewed.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do AVL / red-black trees keep deletion O(log n) in the worst case?
 *   - Delete every node in a given value range instead of a single key.
 *   - Why does always using the successor skew the tree over many deletes?
 *
 * RUN
 *   main() runs 4 groups of cases (five keys on a 6-node tree, single-node
 *   tree, empty tree, and a shape comparison) and prints actual vs expected.
 */
class DeleteNodeInBST {

    // ---------------------------------------------------------------
    // Approach A: recursive, replace with inorder successor's value
    // ---------------------------------------------------------------
    public TreeNode deleteRecursiveSuccessorCopy(TreeNode root, int key) {
        if (root == null) return null;

        if (key < root.val) {
            root.left = deleteRecursiveSuccessorCopy(root.left, key);
        } else if (key > root.val) {
            root.right = deleteRecursiveSuccessorCopy(root.right, key);
        } else {
            // Node with one child or none: the parent adopts the surviving child.
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // Two children: overwrite with the inorder successor's value ...
            root.val = minValue(root.right);
            // ... then remove that successor, which has at most a right child.
            root.right = deleteRecursiveSuccessorCopy(root.right, root.val);
        }
        return root;
    }

    /** Smallest value in the subtree: walk left until there is no left child. */
    private int minValue(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.val;
    }

    // ---------------------------------------------------------------
    // Approach B: iterative parent walk + splice left subtree under
    //             the leftmost node of the right subtree
    // ---------------------------------------------------------------

    /** Given the node being removed, return the subtree that replaces it. */
    private TreeNode connector(TreeNode root) {
        // Case 1: no left child -> the right subtree takes its place.
        if (root.left == null) return root.right;

        // Case 2: no right child -> the left subtree takes its place.
        if (root.right == null) return root.left;

        // Case 3: both children. Every value in the left subtree is smaller
        // than every value in the right subtree, so the left subtree can hang
        // off the leftmost (smallest) node of the right subtree without
        // breaking the ordering. Then the right subtree becomes the new root.
        TreeNode leftChild = root.left;
        TreeNode leftmostChildInRightSubtree = root.right;
        while (leftmostChildInRightSubtree.left != null) {
            leftmostChildInRightSubtree = leftmostChildInRightSubtree.left;
        }
        leftmostChildInRightSubtree.left = leftChild;

        return root.right;
    }

    public TreeNode deleteIterativeSplice(TreeNode root, int key) {
        if (root == null) return null;

        // The root has no parent to rewire, so handle it separately.
        if (root.val == key) {
            return connector(root);
        }

        // Descend while `node` stays the *parent* of the candidate, so that
        // node.left / node.right can be reassigned once the key is spotted.
        TreeNode node = root;
        while (node != null) {
            if (node.val > key) {
                if (node.left != null && node.left.val == key) {
                    node.left = connector(node.left);
                    break;
                }
                node = node.left;
            } else {
                if (node.right != null && node.right.val == key) {
                    node.right = connector(node.right);
                    break;
                }
                node = node.right;
            }
        }
        return root; // key absent: the loop simply ran off the bottom
    }

    // ---------------------------------------------------------------
    // Helpers for main
    // ---------------------------------------------------------------

    private static String inorder(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        inorder(root, sb);
        String result = sb.toString().trim();
        return result.isEmpty() ? "(empty)" : result;
    }

    private static void inorder(TreeNode root, StringBuilder sb) {
        if (root == null) return;
        inorder(root.left, sb);
        sb.append(root.val).append(' ');
        inorder(root.right, sb);
    }

    private static int height(TreeNode root) {
        return root == null ? 0 : 1 + Math.max(height(root.left), height(root.right));
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    // Both approaches mutate the tree, so every test builds a fresh copy.
    //        5
    //      /   \
    //     3     6
    //    / \     \
    //   2   4     7
    private static TreeNode buildTree() {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(7);
        return root;
    }

    // A deeper tree, used to show that the two approaches agree on in-order
    // but disagree on shape.
    //          5
    //        /   \
    //       3     8
    //      / \   / \
    //     2   4 7   9
    //    /
    //   1
    private static TreeNode buildDeepTree() {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.left.left = new TreeNode(2);
        root.left.left.left = new TreeNode(1);
        root.left.right = new TreeNode(4);
        root.right = new TreeNode(8);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);
        return root;
    }

    public static void main(String[] args) {
        DeleteNodeInBST tree = new DeleteNodeInBST();
        print("original BST (inorder)", inorder(buildTree()), "2 3 4 5 6 7");

        // Case 1 - every structural situation on the 6-node tree.
        // 3 -> two children, 6 -> one child, 2 -> leaf, 5 -> root, 9 -> absent
        int[] keys = {3, 6, 2, 5, 9};
        String[] expected = {"2 4 5 6 7", "2 3 4 5 7", "3 4 5 6 7", "2 3 4 6 7", "2 3 4 5 6 7"};
        for (int i = 0; i < keys.length; i++) {
            print("case 1 delete " + keys[i] + " recursive",
                    inorder(tree.deleteRecursiveSuccessorCopy(buildTree(), keys[i])), expected[i]);
            print("case 1 delete " + keys[i] + " splice   ",
                    inorder(tree.deleteIterativeSplice(buildTree(), keys[i])), expected[i]);
        }

        // Case 2 - edge: deleting the only node must leave a null root.
        print("case 2 single node recursive",
                inorder(tree.deleteRecursiveSuccessorCopy(new TreeNode(5), 5)), "(empty)");
        print("case 2 single node splice   ",
                inorder(tree.deleteIterativeSplice(new TreeNode(5), 5)), "(empty)");

        // Case 3 - edge: deleting from an empty tree must not throw.
        print("case 3 empty tree recursive",
                inorder(tree.deleteRecursiveSuccessorCopy(null, 1)), "(empty)");
        print("case 3 empty tree splice   ",
                inorder(tree.deleteIterativeSplice(null, 1)), "(empty)");

        // Case 4 - tricky: same in-order, different shape. The splice version
        // pushes the left subtree one level below the successor, so it is taller.
        TreeNode byCopy = tree.deleteRecursiveSuccessorCopy(buildDeepTree(), 5);
        TreeNode bySplice = tree.deleteIterativeSplice(buildDeepTree(), 5);
        print("case 4 deep tree recursive inorder", inorder(byCopy), "1 2 3 4 7 8 9");
        print("case 4 deep tree splice    inorder", inorder(bySplice), "1 2 3 4 7 8 9");
        print("case 4 height after recursive     ", height(byCopy), 4);
        print("case 4 height after splice        ", height(bySplice), 5);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}
