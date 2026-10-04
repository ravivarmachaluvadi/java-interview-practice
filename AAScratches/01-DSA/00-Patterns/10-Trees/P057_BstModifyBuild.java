/*
 * =====================================================================
 *  P057 BST: Insert, Delete, Build   Canonical LC 450 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 450, Delete Node in a BST)
 *   Delete the node with value key (if present) and return the root of the still-valid
 *   BST.
 *
 * EXAMPLE
 *   [5,3,6,2,4,null,7], key 3  ->  [5,4,6,2,null,null,7]
 *   [5,3,6,2,4,null,7], key 0  ->  [5,3,6,2,4,null,7]     not found: unchanged
 *   [],                 key 0  ->  []
 *
 * RECOGNIZE WHEN
 *   - Change a BST and keep it a BST: insert, delete, trim to a range.
 *   - Build a BST from sorted data (balanced) or from a traversal (preorder).
 *   Not this if: you only read the BST -> P056_BstProperty; the tree is a general binary
 *   tree built from two traversals -> P059_BuildSerializeTree.
 *
 * TEMPLATE
 *   modify(node, key):                           // return the NEW root of this subtree
 *       if node is null: return (insert ? new Node(key) : null)
 *       if key < node.val: node.left  = modify(node.left, key)
 *       elif key > node.val: node.right = modify(node.right, key)
 *       else: handle this node (delete: 0, 1 or 2 children)
 *       return node
 *   build balanced: root = middle of the sorted range, recurse on both halves
 *
 * APPROACH
 *   1. Recurse toward the key, re-linking each child to the result of the recursive call.
 *   2. At the key: no child -> null; one child -> that child; two children -> copy the
 *      successor's value (min of the right subtree) and delete the successor there.
 *
 * KEY INSIGHT
 *   "Return the new subtree root and assign it to the parent's pointer" removes all
 *   parent-tracking: every case, including deleting the root, is handled the same way.
 *   For two children, the inorder successor is the only value that can replace the node
 *   without breaking the order on either side.
 *
 * COMPLEXITY
 *   Time O(h) for insert / delete (O(log n) balanced, O(n) skewed); build O(n). Space O(h).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 701  Insert into a BST        walk down; create the node where you fall off
 *   [coded] LC 108  Sorted Array to BST      middle element is the root; recurse
 *   [coded] LC 1008 BST from Preorder        recurse with an upper bound; one shared index
 *           LC 109  Sorted List to BST       middle via P031_FastSlowPointers, then as LC 108
 *           LC 669  Trim a BST               out-of-range node -> return the trimmed child
 *           LC 1382 Balance a BST            inorder to a list, then LC 108
 *
 * PITFALLS
 *   - Forgetting to assign the recursive result back (node.left = ...) loses the change.
 *   - Two-child delete: delete the successor from the RIGHT subtree, by value.
 *   - LC 1008: the bound must be passed down, or a value lands in the wrong subtree.
 *
 * DEEP DIVE
 *   C05_DeleteNodeInBST, B15_SortedArrayToBST,
 *   C06_ConstructBinarySearchTreefromPreorderTraversal, A05_BinarySearchTreeOperations
 *   (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

class BstModifyBuild {

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

        // Level-order list with trailing nulls trimmed, as LeetCode prints trees.
        static String levels(TreeNode root) {
            List<String> out = new ArrayList<>();
            LinkedList<TreeNode> queue = new LinkedList<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                TreeNode n = queue.poll();
                out.add(n == null ? "null" : String.valueOf(n.val));
                if (n != null) {
                    queue.add(n.left);
                    queue.add(n.right);
                }
            }
            while (!out.isEmpty() && out.get(out.size() - 1).equals("null")) {
                out.remove(out.size() - 1);
            }
            return "[" + String.join(",", out) + "]";
        }
    }

    // Canonical LC 450.
    static TreeNode deleteNode(TreeNode node, int key) {
        if (node == null) {
            return null;
        }
        if (key < node.val) {
            node.left = deleteNode(node.left, key);
        } else if (key > node.val) {
            node.right = deleteNode(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            TreeNode successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.val = successor.val;
            node.right = deleteNode(node.right, successor.val);
        }
        return node;
    }

    // LC 701.
    static TreeNode insertIntoBST(TreeNode node, int val) {
        if (node == null) {
            return new TreeNode(val);
        }
        if (val < node.val) {
            node.left = insertIntoBST(node.left, val);
        } else {
            node.right = insertIntoBST(node.right, val);
        }
        return node;
    }

    // LC 108: height-balanced BST from a sorted array.
    static TreeNode sortedArrayToBST(int[] nums) {
        return build(nums, 0, nums.length - 1);
    }

    private static TreeNode build(int[] nums, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        int mid = lo + (hi - lo) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = build(nums, lo, mid - 1);
        root.right = build(nums, mid + 1, hi);
        return root;
    }

    private static int next;

    // LC 1008: each subtree takes values below its upper bound, in preorder.
    static TreeNode bstFromPreorder(int[] preorder) {
        next = 0;
        return fromPreorder(preorder, Integer.MAX_VALUE);
    }

    private static TreeNode fromPreorder(int[] pre, int bound) {
        if (next == pre.length || pre[next] > bound) {
            return null;
        }
        TreeNode root = new TreeNode(pre[next++]);
        root.left = fromPreorder(pre, root.val);
        root.right = fromPreorder(pre, bound);
        return root;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Integer[] t = {5, 3, 6, 2, 4, null, 7};
        check("LC 450 delete 3 (two children)", TreeNode.levels(deleteNode(TreeNode.of(t), 3)),
                "[5,4,6,2,null,null,7]");
        check("LC 450 key missing",
                TreeNode.levels(deleteNode(TreeNode.of(t), 0)), "[5,3,6,2,4,null,7]");
        check("LC 450 empty", TreeNode.levels(deleteNode(TreeNode.of(), 0)), "[]");
        check("LC 450 delete the root",
                TreeNode.levels(deleteNode(TreeNode.of(t), 5)), "[6,3,7,2,4]");
        check("LC 450 delete a leaf",
                TreeNode.levels(deleteNode(TreeNode.of(t), 7)), "[5,3,6,2,4]");

        check("LC 701 insert 5", TreeNode.levels(insertIntoBST(TreeNode.of(4, 2, 7, 1, 3), 5)),
                "[4,2,7,1,3,5]");
        check("LC 701 into empty", TreeNode.levels(insertIntoBST(TreeNode.of(), 5)), "[5]");

        check("LC 108 [-10,-3,0,5,9]",
                TreeNode.levels(sortedArrayToBST(new int[]{-10, -3, 0, 5, 9})),
                "[0,-10,5,null,-3,null,9]");
        check("LC 108 [1,3]", TreeNode.levels(sortedArrayToBST(new int[]{1, 3})), "[1,null,3]");

        check("LC 1008 [8,5,1,7,10,12]",
                TreeNode.levels(bstFromPreorder(new int[]{8, 5, 1, 7, 10, 12})),
                "[8,5,10,1,7,null,12]");
        check("LC 1008 [1,3]", TreeNode.levels(bstFromPreorder(new int[]{1, 3})), "[1,null,3]");
    }
}
