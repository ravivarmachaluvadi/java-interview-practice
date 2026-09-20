/*
 * =====================================================================
 *  Diameter of Binary Tree                LeetCode 543 | Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return the length of the longest path between any
 *   two nodes. Length is counted in EDGES, not nodes, and the path does not have to
 *   pass through the root. An empty tree and a single node both have diameter 0.
 *   Note: the old comment on this file said "number of nodes"; the code always counted
 *   edges, which is what LeetCode 543 asks for. The header is now correct.
 *
 * EXAMPLE
 *          1
 *         / \
 *        2   3          ->  3   (4 -> 2 -> 1 -> 3, or 5 -> 2 -> 1 -> 3)
 *       / \
 *      4   5
 *   tree = []  ->  0          tree = [1]  ->  0
 *          1
 *         /
 *        2
 *       / \             ->  4   (5 -> 3 -> 2 -> 4 -> 6; it never touches the root)
 *      3   4
 *     /     \
 *    5       6
 *
 * APPROACH  (return height, track the global best)
 *   1. Postorder DFS. For each node get leftDepth and rightDepth, the heights of its
 *      two subtrees measured in nodes.
 *   2. The longest path that BENDS at this node has leftDepth + rightDepth edges.
 *      Compare it with the global maximum and keep the larger.
 *   3. RETURN 1 + max(leftDepth, rightDepth): the height of this subtree, which is
 *      what the parent needs to compute its own bend.
 *   4. The answer is the global maximum, not the value returned from the root.
 *
 * KEY INSIGHT
 *   What you return up is NOT what you are answering. The recursion returns height
 *   (one arm) so the parent can build its own path; the answer (two arms joined) is
 *   written to a side field. Once this split is automatic, Balanced Tree (B10) and
 *   Max Path Sum (D01) are the same skeleton with a different combine step, and the
 *   naive O(n^2) "height at every node" solution never tempts you again.
 *
 * COMPLEXITY
 *   Time  O(n)  each node visited once; height is not recomputed
 *   Space O(h)  recursion stack equals tree height (O(n) for a skewed tree)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Binary Tree Maximum Path Sum (LC 124): same shape, clamp negative arms to 0
 *   - Diameter of an N-ary tree (LC 1522): keep the two largest child heights
 *   - Return the actual path, not just its length: remember the node where the best
 *     bend happened, then walk down its deepest arms
 *   - Why does a global field beat returning a pair? Both work; the field is shorter,
 *     the pair {height, best} is purer and thread-safe
 *
 * RUN
 *   main() runs 4 cases (typical, empty, single node, path avoiding the root) and
 *   prints actual vs expected.
 */

class DiameterOfBinaryTree {
    private int maxDiameter;

    public int diameterOfBinaryTree(Node root) {
        maxDiameter = 0;   // reset so one solver can serve several trees
        height(root);
        return maxDiameter;
    }

    // Returns the height of this subtree in nodes; records the best bend as a side effect.
    private int height(Node root) {
        if (root == null) return 0;

        int leftDepth = height(root.left);
        int rightDepth = height(root.right);

        // Longest path that bends at this node: down the left arm plus down the right arm.
        maxDiameter = Math.max(maxDiameter, leftDepth + rightDepth);

        return 1 + Math.max(leftDepth, rightDepth);
    }

    public static void main(String[] args) {
        DiameterOfBinaryTree solver = new DiameterOfBinaryTree();

        // case 1: typical, longest path 4 -> 2 -> 1 -> 3
        //        1
        //       / \
        //      2   3
        //     / \
        //    4   5
        Node typical = new Node(1);
        typical.left = new Node(2);
        typical.right = new Node(3);
        typical.left.left = new Node(4);
        typical.left.right = new Node(5);
        print("case 1 typical      ", solver.diameterOfBinaryTree(typical), 3);

        // case 2: empty tree and single node both have no edges
        print("case 2 empty tree   ", solver.diameterOfBinaryTree(null), 0);
        print("case 3 single node  ", solver.diameterOfBinaryTree(new Node(1)), 0);

        // case 4: the longest path bends at node 2 and never touches the root
        //        1
        //       /
        //      2
        //     / \
        //    3   4
        //   /     \
        //  5       6
        Node offRoot = new Node(1);
        offRoot.left = new Node(2);
        offRoot.left.left = new Node(3);
        offRoot.left.right = new Node(4);
        offRoot.left.left.left = new Node(5);
        offRoot.left.right.right = new Node(6);
        print("case 4 avoids root  ", solver.diameterOfBinaryTree(offRoot), 4);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class Node {
    int data;
    Node left;
    Node right;

    Node(int val) {
        data = val;
    }
}
