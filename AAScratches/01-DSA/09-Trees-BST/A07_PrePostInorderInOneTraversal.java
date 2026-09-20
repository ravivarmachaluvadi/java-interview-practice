/*
 * =====================================================================
 *  Preorder, Inorder and Postorder in One Traversal               GFG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, produce all three DFS orders (preorder, inorder,
 *   postorder) in a single pass over the tree, without recursion. Return them as
 *   [pre, in, post]. The tree may be empty.
 *
 * EXAMPLE
 *         1
 *        / \          pre  = [1, 2, 4, 5, 3]
 *       2   3         in   = [4, 2, 5, 1, 3]
 *      / \            post = [4, 5, 2, 3, 1]
 *     4   5
 *   empty tree                     ->  [[], [], []]
 *   1 -> 2 -> 3 (left-only chain)  ->  pre [1, 2, 3], in [3, 2, 1], post [3, 2, 1]
 *
 * APPROACH  (iterative stack with a per-node visit counter)
 *   1. Push (root, state 1). A node is popped up to three times; its state says which visit.
 *   2. State 1 = first visit: record in PRE, push node back as state 2, push left child as 1.
 *   3. State 2 = left done:   record in IN,  push node back as state 3, push right child as 1.
 *   4. State 3 = both done:   record in POST, drop the node for good.
 *   5. Loop until the stack is empty; the three lists are complete.
 *
 * KEY INSIGHT
 *   Recursion "remembers where it was" through the call stack; the state number is that memory
 *   made explicit. Pre/in/post are simply "which of the three visits do you record on". Any
 *   recursive DFS can be de-recursed this way, which matters when the depth could overflow the
 *   JVM stack.
 *   Fixed: the author returned [in, pre, post] while main() labelled index 0 as preorder, so the
 *   printed "preorder" was really the inorder. Now returns [pre, in, post] in every branch.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is pushed and popped exactly three times
 *   Space O(h)  the stack only ever holds the current root-to-node path (plus pending siblings)
 *
 * INTERVIEW FOLLOW-UPS
 *   - One order only with a plain stack (preorder: push right child first, then left).
 *   - Morris traversal: O(1) extra space using temporary threaded right pointers.
 *   - Why postorder is the awkward one iteratively (you must know you came back from the right).
 *
 * RUN
 *   main() runs 3 cases (typical, empty, left-only chain) and checks the iterative answer
 *   against a recursive reference, printing actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

class TreeNode {
    int data;
    TreeNode left, right;

    TreeNode(int val) {
        this.data = val;
    }
}

/** A node together with which of its three visits comes next (1 = pre, 2 = in, 3 = post). */
class NodeState {
    TreeNode node;
    int state;

    NodeState(TreeNode node, int state) {
        this.node = node;
        this.state = state;
    }
}

class PrePostInorderInOneTraversal {

    /** Returns [preorder, inorder, postorder], all built in one iterative pass. */
    public List<List<Integer>> treeTraversal(TreeNode root) {
        List<Integer> pre = new ArrayList<>();
        List<Integer> in = new ArrayList<>();
        List<Integer> post = new ArrayList<>();
        if (root == null) return Arrays.asList(pre, in, post);

        Deque<NodeState> stack = new ArrayDeque<>();
        stack.push(new NodeState(root, 1));

        while (!stack.isEmpty()) {
            NodeState top = stack.pop();
            TreeNode node = top.node;

            if (top.state == 1) {
                // first visit: preorder now, come back for inorder once the left subtree is done
                pre.add(node.data);
                stack.push(new NodeState(node, 2));
                if (node.left != null) stack.push(new NodeState(node.left, 1));
            } else if (top.state == 2) {
                // left subtree done: inorder now, come back for postorder after the right subtree
                in.add(node.data);
                stack.push(new NodeState(node, 3));
                if (node.right != null) stack.push(new NodeState(node.right, 1));
            } else {
                // both subtrees done: postorder, and this node never returns to the stack
                post.add(node.data);
            }
        }
        return Arrays.asList(pre, in, post);
    }

    /** Plain recursive version, used by main() as the reference answer. */
    static List<List<Integer>> recursiveTraversal(TreeNode root) {
        List<Integer> pre = new ArrayList<>();
        List<Integer> in = new ArrayList<>();
        List<Integer> post = new ArrayList<>();
        dfs(root, pre, in, post);
        return Arrays.asList(pre, in, post);
    }

    private static void dfs(TreeNode node, List<Integer> pre, List<Integer> in, List<Integer> post) {
        if (node == null) return;
        pre.add(node.data);
        dfs(node.left, pre, in, post);
        in.add(node.data);
        dfs(node.right, pre, in, post);
        post.add(node.data);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        PrePostInorderInOneTraversal sol = new PrePostInorderInOneTraversal();

        //       1
        //      / \
        //     2   3
        //    / \
        //   4   5
        TreeNode typical = new TreeNode(1);
        typical.left = new TreeNode(2);
        typical.right = new TreeNode(3);
        typical.left.left = new TreeNode(4);
        typical.left.right = new TreeNode(5);
        print("case 1 typical [pre, in, post]", sol.treeTraversal(typical),
                "[[1, 2, 4, 5, 3], [4, 2, 5, 1, 3], [4, 5, 2, 3, 1]]");
        print("case 1 matches recursive      ",
                sol.treeTraversal(typical).equals(recursiveTraversal(typical)), true);

        print("case 2 empty tree             ", sol.treeTraversal(null), "[[], [], []]");

        // 1 -> 2 -> 3, every node hangs on the left
        TreeNode chain = new TreeNode(1);
        chain.left = new TreeNode(2);
        chain.left.left = new TreeNode(3);
        print("case 3 left-only chain        ", sol.treeTraversal(chain),
                "[[1, 2, 3], [3, 2, 1], [3, 2, 1]]");
    }
}
