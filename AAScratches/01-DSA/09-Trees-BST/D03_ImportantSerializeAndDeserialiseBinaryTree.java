/*
 * =====================================================================
 *  Serialize and Deserialize Binary Tree        LeetCode 297 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a codec: serialize(root) turns a binary tree into a String, and
 *   deserialize(str) rebuilds an identical tree from that String. The tree can
 *   be any shape (not a BST, not complete), can be empty, and values may repeat.
 *   Round-trip must be exact: deserialize(serialize(t)) is structurally equal to t.
 *
 * EXAMPLE
 *        1
 *       / \
 *      2   3            ->  "1,2,3,null,null,4,5,null,null,null,null,"  (level order)
 *         / \
 *        4   5
 *   root = null          ->  "null"
 *   single node 7        ->  "7,null,null,"
 *   left chain 1-2-3     ->  "1,2,null,3,null,null,null,"
 *
 * APPROACH  (BFS level order with explicit null markers)
 *   serialize:
 *   1. Push root into a queue. Poll one node at a time.
 *   2. If the node is null, append "null,". Otherwise append its value and push
 *      BOTH children, even when they are null. That is what keeps the shape.
 *   deserialize:
 *   3. Split on ",". strings[0] is the root; keep an index i = 1 for the next token.
 *   4. Poll a parent from the queue. Token i is its left child, token i+1 its right.
 *      Build a child only for non-"null" tokens, and push every built child.
 *   5. Advance i by two per parent. The queue and the token stream stay in step
 *      because both were produced in the same BFS order.
 *
 *   A second codec (preorder DFS with "#" for null) is included as
 *   serializePreorder / deserializePreorder; it is the version most people write
 *   on a whiteboard because it is a few lines shorter.
 *
 * KEY INSIGHT
 *   A single traversal (preorder OR level order) is enough to rebuild a tree as
 *   long as every null child is recorded. Without null markers you need two
 *   traversals (preorder + inorder) and unique values; with them you need neither.
 *   Pattern: "reconstruct from one sequence" = encode the nulls.
 *
 * COMPLEXITY
 *   Time  O(n)  each node and each null slot is visited once in both directions
 *   Space O(n)  the output string; queue holds at most one level (up to n/2 nodes)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it with preorder DFS instead of BFS (included here). Which is shorter?
 *   - How would you shrink the output? Drop trailing nulls, or for a BST skip
 *     null markers entirely and rebuild with min/max bounds (LeetCode 449).
 *   - Values can be negative or multi-digit, so why is "," + "null" a safe format?
 *   - N-ary tree: emit child count per node, or a closing marker after the children.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, single node, left chain) through both
 *   codecs and prints the round-tripped serialization vs expected.
 */
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

class ImportantSerializeAndDeserialiseBinaryTree {

    // ---------- Approach 1: level order (BFS) with "null" markers ----------

    public String serialize(TreeNode root) {
        if (root == null) return "null";
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                sb.append("null,");
            } else {
                sb.append(node.val).append(",");
                // push both children even when null: the nulls are what record the shape
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        return sb.toString();
    }

    public TreeNode deserialize(String data) {
        if (data.equals("null")) return null;
        String[] tokens = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(tokens[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int i = 1; // tokens[0] is the root, so the first child token is at index 1
        while (!queue.isEmpty()) {
            TreeNode parent = queue.poll();
            // token i is this parent's left child
            if (!tokens[i].equals("null")) {
                parent.left = new TreeNode(Integer.parseInt(tokens[i]));
                queue.add(parent.left);
            }
            i++;
            // token i is this parent's right child
            if (!tokens[i].equals("null")) {
                parent.right = new TreeNode(Integer.parseInt(tokens[i]));
                queue.add(parent.right);
            }
            i++; // advance past the right token as well, even when it was "null"
        }
        return root;
    }

    // ---------- Approach 2: preorder (DFS) with "#" markers ----------

    public String serializePreorder(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        preorderEncode(root, sb);
        return sb.toString();
    }

    private void preorderEncode(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("#,");
            return;
        }
        sb.append(node.val).append(",");
        preorderEncode(node.left, sb);
        preorderEncode(node.right, sb);
    }

    public TreeNode deserializePreorder(String data) {
        // a queue of tokens acts as the shared "next index" across recursive calls
        Queue<String> tokens = new ArrayDeque<>();
        for (String t : data.split(",")) tokens.add(t);
        return preorderDecode(tokens);
    }

    private TreeNode preorderDecode(Queue<String> tokens) {
        String token = tokens.poll();
        if (token.equals("#")) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = preorderDecode(tokens);  // preorder: left subtree comes next
        node.right = preorderDecode(tokens);
        return node;
    }

    // ---------- test harness ----------

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ImportantSerializeAndDeserialiseBinaryTree codec = new ImportantSerializeAndDeserialiseBinaryTree();

        //        1
        //       / \
        //      2   3
        //         / \
        //        4   5
        TreeNode typical = new TreeNode(1);
        typical.left = new TreeNode(2);
        typical.right = new TreeNode(3);
        typical.right.left = new TreeNode(4);
        typical.right.right = new TreeNode(5);

        TreeNode single = new TreeNode(7);

        // left chain 1 -> 2 -> 3, tests deep nulls on one side
        TreeNode leftChain = new TreeNode(1);
        leftChain.left = new TreeNode(2);
        leftChain.left.left = new TreeNode(3);

        TreeNode[] trees = {typical, null, single, leftChain};
        String[] names = {"typical", "empty", "single", "leftChain"};
        String[] expectedBfs = {
                "1,2,3,null,null,4,5,null,null,null,null,",
                "null",
                "7,null,null,",
                "1,2,null,3,null,null,null,"
        };
        String[] expectedPre = {
                "1,2,#,#,3,4,#,#,5,#,#,",
                "#,",
                "7,#,#,",
                "1,2,3,#,#,#,#,"
        };

        for (int c = 0; c < trees.length; c++) {
            String bfs = codec.serialize(trees[c]);
            String bfsRoundTrip = codec.serialize(codec.deserialize(bfs));
            check("BFS      " + names[c] + " serialize ", bfs, expectedBfs[c]);
            check("BFS      " + names[c] + " round-trip", bfsRoundTrip, expectedBfs[c]);

            String pre = codec.serializePreorder(trees[c]);
            String preRoundTrip = codec.serializePreorder(codec.deserializePreorder(pre));
            check("preorder " + names[c] + " serialize ", pre, expectedPre[c]);
            check("preorder " + names[c] + " round-trip", preRoundTrip, expectedPre[c]);
        }
    }
}
