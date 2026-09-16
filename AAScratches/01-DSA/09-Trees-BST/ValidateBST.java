/**
 * Problem: Determine whether a binary tree satisfies the Binary Search Tree (BST)
 * property – every node's value must be strictly greater than all values in its
 * left subtree and strictly less than all values in its right subtree.
 *
 * Approach: Recursively traverse the tree while maintaining an allowed range
 * for each node. For a node, check that its data lies within (minVal, maxVal).
 * Recurse on the left child with updated maxVal = node.data and on the right
 * child with minVal = node.data.
 *
 * Time Complexity: O(n) – each node is visited once.
 * Space Complexity: O(h) – recursion stack depth equals tree height (worst‑case O(n)).
 */
class Solution {
    private boolean checkBST(Node node, long minVal, long maxVal) {
        if (node == null) return true;

        if (node.data <= minVal || node.data >= maxVal) return false;

        return checkBST(node.left, minVal, node.data)
                &&
                checkBST(node.right, node.data, maxVal);
    }

    public boolean isValidBST(Node root) {
        return checkBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

}


class ValidateBST {

    public static void main(String[] args) {
        Node root = new Node(7);
        root.left = new Node(5);
        root.left.left = new Node(3);
        root.left.right = new Node(6);
        root.right = new Node(10);
        root.right.left = new Node(9);
        root.right.right = new Node(15);
        Solution ob = new Solution();
        boolean ans = ob.isValidBST(root);
        if (ans == true) {
            System.out.print("Valid BST");
        } else {
            System.out.print("Invalid BST");
        }
    }
}

class Node {
    int data;
    Node left, right;

    Node(int data) {
        this.data = data;
        left = null;
        right = null;
    }
}
