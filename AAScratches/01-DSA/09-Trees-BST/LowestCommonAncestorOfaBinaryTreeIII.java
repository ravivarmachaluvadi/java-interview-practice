// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;

    public Node(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}

// https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iii/
// 1650. Lowest Common Ancestor of a Binary Tree III
class LowestCommonAncestorOfaBinaryTreeIII {

    public Node lowestCommonAncestorIII(Node p, Node q) {
        Node a = p, b = q;
        while (a != b) {
            a = (a == null) ? q : a.parent;
            b = (b == null) ? p : b.parent;
        }
        return a;
    }

    public Node lowestCommonAncestorII(Node p, Node q) {
        Node a = p, b = q;
        while (a != b) {
            if (a == null) a = q;
            else a = a.parent;
            if (b == null) b = p;
            else b = b.parent;
        }
        return a;
    }

    public Node lowestCommonAncestorI(Node p, Node q) {
        java.util.Set<Node> visited = new java.util.HashSet<>();
        Node cur = p;
        while (cur != null) {
            visited.add(cur);
            cur = cur.parent;
        }
        cur = q;
        while (cur != null) {
            if (visited.contains(cur)) {
                return cur;
            }
            cur = cur.parent;
        }
        return null;  // If no common ancestor (shouldn't happen given valid input)
    }

    // Example usage
    public static void main(String[] args) {
        /*
        Build example tree:
               3
              / \
             5   1
            / \   \
           6   2   8
              / \
             7   4
        */
        Node root = new Node(3);
        Node n5 = new Node(5);
        Node n1 = new Node(1);
        Node n6 = new Node(6);
        Node n2 = new Node(2);
        Node n0 = new Node(0);
        Node n8 = new Node(8);
        Node n7 = new Node(7);
        Node n4 = new Node(4);

        root.left = n5;
        n5.parent = root;
        root.right = n1;
        n1.parent = root;

        n5.left = n6;
        n6.parent = n5;
        n5.right = n2;
        n2.parent = n5;

        n1.right = n8;
        n8.parent = n1;

        n2.left = n7;
        n7.parent = n2;
        n2.right = n4;
        n4.parent = n2;

        // Example 1: p = 5, q = 1 ⇒ LCA = 3
        Node p = n5;
        Node q = n1;
        LowestCommonAncestorOfaBinaryTreeIII sol = new LowestCommonAncestorOfaBinaryTreeIII();
        Node lca = sol.lowestCommonAncestorI(p, q);
        System.out.println("LCA of " + p.val + " and " + q.val + " is: " + (lca != null ? lca.val : "null"));
        // Should print 3

        // Example 2: p = 5, q = 4 ⇒ LCA = 5
        p = n5;
        q = n4;
        lca = sol.lowestCommonAncestorII(p, q);
        System.out.println("LCA of " + p.val + " and " + q.val + " is: " + (lca != null ? lca.val : "null"));
        // Should print 5
    }
}
