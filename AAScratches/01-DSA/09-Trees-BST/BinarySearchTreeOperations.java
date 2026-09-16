/**
 * Implements a basic Binary Search Tree (BST) that supports insertion,
 * search, and in-order traversal of integer values.
 *
 * The BST stores each element in a Node with left/right child pointers
 * such that for any node, all values in the left subtree are smaller
 * and all values in the right subtree are larger. Duplicate values are ignored.
 *
 * Approach:
 * - Insertion (put) uses recursion to find the correct leaf position,
 *   creating new nodes as needed.
 * - Search (contains) recursively compares the target value with the current node's value,
 *   traversing left or right accordingly until found or a null child is reached.
 * - In-order traversal visits left subtree, prints the node value, then visits
 *   the right subtree, yielding sorted output.
 *
 * Time Complexity:
 * - Average-case insertion/search: O(log n)
 * - Worst-case (degenerate tree): O(n)
 * Space Complexity:
 * - Recursion stack depth equals tree height: average O(log n), worst O(n).
 */
class BinarySearchTree {
    Node root;

    public BinarySearchTree() {
        root = null;
    }

    public void put(int value) {
        root = putRecursive(root, value);
    }

    private Node putRecursive(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }
        if (value < current.value) {
            current.left = putRecursive(current.left, value);
        } else if (value > current.value) {
            current.right = putRecursive(current.right, value);
        }
        return current;
    }

    public boolean contains(int value) {
        return containsRecursive(root, value);
    }

    private boolean containsRecursive(Node current, int value) {
        if (current == null) {
            return false;
        }
        if (value == current.value) {
            return true;
        }
        return value < current.value
                ? containsRecursive(current.left, value)
                : containsRecursive(current.right, value);
    }

    public void inOrderTraversal() {
        inOrderRecursive(root);
    }

    private void inOrderRecursive(Node node) {
        if (node != null) {
            inOrderRecursive(node.left);
            System.out.print(node.value + " ");
            inOrderRecursive(node.right);
        }
    }
}

class BinarySearchTreeOperations {
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        bst.put(50);
        bst.put(30);
        bst.put(70);
        bst.put(20);
        bst.put(40);
        bst.put(60);
        bst.put(80);

        System.out.println("In-order traversal:");
        bst.inOrderTraversal(); // Expected output: 20 30 40 50 60 70 80

        System.out.println("\nContains 40: " + bst.contains(40)); // true
        System.out.println("Contains 90: " + bst.contains(90));   // false
    }
}

class Node {
    int value;
    Node left, right;

    public Node(int value) {
        this.value = value;
        left = right = null;
    }
}
