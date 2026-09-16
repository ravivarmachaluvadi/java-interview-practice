/**
 * Problem:
 * Implement a data structure that supports inserting nodes into a complete binary tree while maintaining its completeness.
 *
 * Approach:
 * Maintain a queue of candidate parent nodes (those missing at least one child). On insertion, peek the front node:
 * - If left child is null, insert there and return parent value.
 * - Else if right child is null, insert there, return parent value, and remove that node from the queue.
 * After inserting, enqueue the new node as a potential future parent.
 *
 * Complexity:
 * Time: O(1) amortized per insertion (each node processed at most once).
 * Space: O(n) for storing the queue of nodes in the tree. 
 */
import java.util.LinkedList;
import java.util.Queue;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

// 919. Complete Binary Tree Inserter
//  Complete Binary Tree
//    1. All levels except possibly the last are completely filled.
//    2. The last level is filled from left to right without gaps
class CBTInserter {

    TreeNode root;
    Queue<TreeNode> q;

    public CBTInserter(TreeNode root) {
        this.root = root;
        q = new LinkedList<>();
        q.add(root);
    }

    public int insert(int val) {
        TreeNode v = new TreeNode(val);
        while (!q.isEmpty()) {
            TreeNode peekNode = q.peek();

            if (peekNode.left == null) {
                // new node part of tree and inserted , no need
                // bother about Q insertion
                peekNode.left = v;
                return peekNode.val;
            } else if (peekNode.right == null) {
                peekNode.right = v;
                return peekNode.val;
            }
            peekNode = q.poll();
            q.add(peekNode.left);
            q.add(peekNode.right);
        }
        return -1;
    }

    public TreeNode get_root() {
        return root;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1, new TreeNode(2), new TreeNode(3));
        CBTInserter cbtInserter = new CBTInserter(root);
        System.out.println(cbtInserter.insert(4)); // Output: 2
        System.out.println(cbtInserter.insert(5)); // Output: 2
        System.out.println(cbtInserter.insert(6)); // Output: 3
        TreeNode updatedRoot = cbtInserter.get_root();
        // The updated tree structure can be verified by traversing updatedRoot
    }
}
