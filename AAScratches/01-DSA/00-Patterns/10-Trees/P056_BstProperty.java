/*
 * =====================================================================
 *  P056 BST Property: Bounds and Sorted Inorder   Canonical LC 98 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 98, Validate Binary Search Tree)
 *   Return whether the tree is a valid BST: every node in a left subtree is strictly
 *   smaller, and every node in a right subtree strictly larger, than the node.
 *
 * EXAMPLE
 *   [2,1,3]                  ->  true
 *   [5,1,4,null,null,3,6]    ->  false
 *   [5,4,6,null,null,3,7]    ->  false   the trap: 3 is a valid child of 6 but sits in 5's
 *                                        right subtree
 *
 * RECOGNIZE WHEN
 *   - The tree is a BST and the question uses ORDER: validate, k-th smallest, closest
 *     value, range sum, min difference, LCA, two-sum, recover swapped nodes.
 *   - "inorder of a BST is sorted" turns tree questions into sorted-array questions.
 *   Not this if: it is a general binary tree -> the BST shortcuts are wrong; use
 *   P053_BottomUpDfs / P058_LowestCommonAncestor.
 *
 * TEMPLATE
 *   bounds:  valid(node, lo, hi): lo < node.val < hi, then
 *            valid(left, lo, node.val) and valid(right, node.val, hi)
 *   inorder: walk left-node-right keeping `prev`; values must strictly increase
 *   search:  go left if target < node.val, right if larger: O(h)
 *
 * APPROACH
 *   1. Each node must lie in an open interval (lo, hi) inherited from its ancestors.
 *   2. Going left tightens hi to the node's value; going right tightens lo.
 *
 * KEY INSIGHT
 *   The BST rule is about ALL ancestors, not just the parent, so a local parent-child check
 *   is wrong. Either carry the allowed interval down, or use the equivalent fact that an
 *   inorder walk must be strictly increasing.
 *
 * COMPLEXITY
 *   Time O(n) to validate, O(h) to search; space O(h).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 230  Kth Smallest in BST      iterative inorder, stop at the k-th pop
 *   [coded] LC 235  LCA of a BST             walk from the root; split point where p and q
 *                                            go to different sides
 *   [coded] LC 530  Min Absolute Difference  inorder with prev; min of neighbour gaps
 *           LC 700  Search in a BST          the search template
 *           LC 938  Range Sum of BST         prune subtrees outside [low, high]
 *           LC 99   Recover BST              inorder: the two out-of-order spots
 *           LC 653  Two Sum IV in BST        inorder to a list + P011_OppositeEndsSorted
 *           LC 501  Find Mode in BST         inorder runs of equal values
 *
 * PITFALLS
 *   - Bounds with int overflow: a node equal to Integer.MAX_VALUE breaks "< MAX"; use
 *     long or nullable bounds.
 *   - Duplicates: decide strict vs non-strict from the problem statement.
 *   - LC 235 is for BSTs only; on a general tree use P058_LowestCommonAncestor.
 *
 * DEEP DIVE
 *   C01_ValidateBST, C02_NthLargestInBST, C11_LowestCommonAncestorBST,
 *   B14_GetMinimumDifference, B12_RangeSumBST (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class BstProperty {

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

        // The node holding value v (BST search), for building test inputs.
        static TreeNode find(TreeNode root, int v) {
            while (root != null && root.val != v) {
                root = v < root.val ? root.left : root.right;
            }
            return root;
        }
    }

    // Canonical LC 98.
    static boolean isValidBST(TreeNode root) {
        return valid(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private static boolean valid(TreeNode node, long lo, long hi) {
        if (node == null) {
            return true;
        }
        if (node.val <= lo || node.val >= hi) {
            return false;
        }
        return valid(node.left, lo, node.val) && valid(node.right, node.val, hi);
    }

    // LC 230.
    static int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (true) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            if (--k == 0) {
                return cur.val;
            }
            cur = cur.right;
        }
    }

    // LC 235.
    static TreeNode lowestCommonAncestorBst(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode cur = root;
        while (cur != null) {
            if (p.val < cur.val && q.val < cur.val) {
                cur = cur.left;
            } else if (p.val > cur.val && q.val > cur.val) {
                cur = cur.right;
            } else {
                return cur;                        // they split here (or one IS cur)
            }
        }
        return null;
    }

    private static Integer prev;
    private static int minGap;

    // LC 530: neighbours in inorder are the only candidates for the minimum gap.
    static int getMinimumDifference(TreeNode root) {
        prev = null;
        minGap = Integer.MAX_VALUE;
        inorderGap(root);
        return minGap;
    }

    private static void inorderGap(TreeNode node) {
        if (node == null) {
            return;
        }
        inorderGap(node.left);
        if (prev != null) {
            minGap = Math.min(minGap, node.val - prev);
        }
        prev = node.val;
        inorderGap(node.right);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 98 [2,1,3]", isValidBST(TreeNode.of(2, 1, 3)), true);
        check("LC 98 [5,1,4,null,null,3,6]",
                isValidBST(TreeNode.of(5, 1, 4, null, null, 3, 6)), false);
        check("LC 98 grandchild trap", isValidBST(TreeNode.of(5, 4, 6, null, null, 3, 7)), false);
        check("LC 98 int max value", isValidBST(TreeNode.of(2147483647)), true);
        check("LC 98 duplicate is invalid", isValidBST(TreeNode.of(2, 2, 2)), false);

        check("LC 230 k=1", kthSmallest(TreeNode.of(3, 1, 4, null, 2), 1), 1);
        check("LC 230 k=3", kthSmallest(TreeNode.of(5, 3, 6, 2, 4, null, null, 1), 3), 3);

        TreeNode bst = TreeNode.of(6, 2, 8, 0, 4, 7, 9, null, null, 3, 5);
        check("LC 235 p=2 q=8",
                lowestCommonAncestorBst(bst, TreeNode.find(bst, 2), TreeNode.find(bst, 8)).val, 6);
        check("LC 235 p=2 q=4 ancestor of itself",
                lowestCommonAncestorBst(bst, TreeNode.find(bst, 2), TreeNode.find(bst, 4)).val, 2);
        check("LC 235 p=3 q=5",
                lowestCommonAncestorBst(bst, TreeNode.find(bst, 3), TreeNode.find(bst, 5)).val, 4);

        check("LC 530 [4,2,6,1,3]", getMinimumDifference(TreeNode.of(4, 2, 6, 1, 3)), 1);
        check("LC 530 [1,0,48,null,null,12,49]",
                getMinimumDifference(TreeNode.of(1, 0, 48, null, null, 12, 49)), 1);
    }
}
