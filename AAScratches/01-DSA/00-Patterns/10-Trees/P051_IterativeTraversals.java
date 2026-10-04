/*
 * =====================================================================
 *  P051 Tree Traversals: Recursive, Iterative, Morris   Canonical LC 94 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 94, Binary Tree Inorder Traversal)
 *   Return the inorder (left, node, right) traversal of a binary tree, iteratively.
 *
 * EXAMPLE
 *   [1,null,2,3]                         ->  [1,3,2]
 *   [1,2,3,4,5,null,8,null,null,6,7,9]   ->  [4,2,6,5,7,1,3,9,8]
 *   []                                   ->  []
 *
 * RECOGNIZE WHEN
 *   - "inorder / preorder / postorder", "without recursion", "O(1) extra space".
 *   - BST problems that need sorted order (inorder) or must stop early (k-th smallest).
 *   - Recursion depth is a risk (a 10^5-node skewed tree overflows the call stack).
 *   Not this if: you need nodes level by level -> P052_BfsByLevel.
 *
 * TEMPLATE
 *   inorder:   cur = root; stack = []
 *              while cur or stack:
 *                  while cur: stack.push(cur); cur = cur.left   // go left as far as possible
 *                  cur = stack.pop(); visit(cur); cur = cur.right
 *   preorder:  stack = [root]; pop, visit, push right THEN left
 *   postorder: preorder with left and right swapped (node, right, left), then reverse
 *   Morris:    thread each node's inorder predecessor back to it; O(1) space
 *
 * APPROACH
 *   1. Push the whole left spine; the top is the leftmost unvisited node.
 *   2. Pop it, visit it, then repeat on its right subtree.
 *
 * KEY INSIGHT
 *   Recursion keeps "where to come back to" on the call stack; an explicit stack holds
 *   the same thing. Inorder pauses each node until its left side is done, which is
 *   exactly "push the left spine, pop, go right".
 *
 * COMPLEXITY
 *   Time O(n). Space O(h) for the stack (h = height); Morris O(1) extra.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 144  Preorder Iterative       stack; push right before left
 *   [coded] LC 145  Postorder Iterative      root-right-left with a stack, then reverse
 *   [coded] Morris Inorder                   predecessor.right = cur as a temporary thread
 *           LC 173  BST Iterator             the inorder stack, one pop per next()
 *                                            -> P105_IteratorDesign
 *           LC 230  Kth Smallest in BST      inorder, stop at the k-th pop
 *           LC 589 / 590  N-ary Pre / Post   push children in reverse order
 *
 * PITFALLS
 *   - Preorder: push RIGHT first so LEFT is popped first.
 *   - Morris: always remove the thread on the second visit, or the tree stays modified.
 *   - The inorder loop condition is "cur != null OR stack not empty".
 *
 * DEEP DIVE
 *   A01_RecursivePostorder, A06_PrePostInorderInOneTraversal (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class IterativeTraversals {

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

    // Canonical LC 94.
    static List<Integer> inorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            out.add(cur.val);
            cur = cur.right;
        }
        return out;
    }

    // LC 144.
    static List<Integer> preorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) {
            return out;
        }
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode n = stack.pop();
            out.add(n.val);
            if (n.right != null) {
                stack.push(n.right);
            }
            if (n.left != null) {
                stack.push(n.left);
            }
        }
        return out;
    }

    // LC 145: node-right-left, reversed, is left-right-node.
    static List<Integer> postorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) {
            return out;
        }
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode n = stack.pop();
            out.add(n.val);
            if (n.left != null) {
                stack.push(n.left);
            }
            if (n.right != null) {
                stack.push(n.right);
            }
        }
        Collections.reverse(out);
        return out;
    }

    // Morris inorder: O(1) extra space, restores the tree.
    static List<Integer> morrisInorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                out.add(cur.val);
                cur = cur.right;
            } else {
                TreeNode pred = cur.left;
                while (pred.right != null && pred.right != cur) {
                    pred = pred.right;
                }
                if (pred.right == null) {
                    pred.right = cur;              // thread back, then go left
                    cur = cur.left;
                } else {
                    pred.right = null;             // second visit: remove the thread
                    out.add(cur.val);
                    cur = cur.right;
                }
            }
        }
        return out;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Integer[] small = {1, null, 2, 3};
        Integer[] big = {1, 2, 3, 4, 5, null, 8, null, null, 6, 7, 9};
        check("LC 94 [1,null,2,3]", inorder(TreeNode.of(small)), "[1, 3, 2]");
        check("LC 94 twelve nodes", inorder(TreeNode.of(big)), "[4, 2, 6, 5, 7, 1, 3, 9, 8]");
        check("LC 94 empty", inorder(TreeNode.of()), "[]");

        check("LC 144 [1,null,2,3]", preorder(TreeNode.of(small)), "[1, 2, 3]");
        check("LC 144 twelve nodes", preorder(TreeNode.of(big)), "[1, 2, 4, 5, 6, 7, 3, 8, 9]");

        check("LC 145 [1,null,2,3]", postorder(TreeNode.of(small)), "[3, 2, 1]");
        check("LC 145 twelve nodes", postorder(TreeNode.of(big)), "[4, 6, 7, 5, 2, 9, 8, 3, 1]");

        TreeNode t = TreeNode.of(big);
        check("Morris twelve nodes", morrisInorder(t), "[4, 2, 6, 5, 7, 1, 3, 9, 8]");
        check("Morris leaves the tree intact", inorder(t), "[4, 2, 6, 5, 7, 1, 3, 9, 8]");
    }
}
