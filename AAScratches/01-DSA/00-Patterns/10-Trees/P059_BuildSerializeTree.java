/*
 * =====================================================================
 *  P059 Build a Tree from Traversals / Serialize   Canonical LC 105 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 105, Construct Binary Tree from Preorder and Inorder)
 *   Values are unique. Rebuild the tree from its preorder and inorder traversals.
 *
 * EXAMPLE
 *   preorder [3,9,20,15,7], inorder [9,3,15,20,7]  ->  [3,9,20,null,null,15,7]
 *   preorder [-1],          inorder [-1]           ->  [-1]
 *
 * RECOGNIZE WHEN
 *   - Two traversals given, rebuild the tree (pre + in, post + in, pre + post for full
 *     trees).
 *   - Turn a tree into a string and back (serialize / deserialize, codec design).
 *   Not this if: the tree is a BST and you have one traversal -> P057_BstModifyBuild (LC 1008).
 *
 * TEMPLATE
 *   index = {value: position in inorder}            // O(1) split lookups
 *   build(lo, hi):                                  // inorder range [lo, hi]
 *       if lo > hi: return null
 *       root = next value from preorder (left to right) / postorder (right to left)
 *       mid = index[root]
 *       preorder:  root.left = build(lo, mid - 1); root.right = build(mid + 1, hi)
 *       postorder: build RIGHT first, then left
 *   serialize: preorder with a marker for null; deserialize by reading tokens in order
 *
 * APPROACH
 *   1. The next preorder value is the root of the current subtree.
 *   2. Its position in inorder splits the range into the left and right subtrees.
 *   3. Recurse left then right, consuming preorder values in order.
 *
 * KEY INSIGHT
 *   Preorder (or postorder) tells you WHO is the root; inorder tells you WHAT goes on each
 *   side. A hash map from value to inorder index makes each split O(1), so the whole build
 *   is O(n). Serializing with explicit null markers makes ONE traversal enough.
 *
 * COMPLEXITY
 *   Time O(n), space O(n) for the map and recursion.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 106  From Inorder + Postorder walk postorder from the END; build right first
 *   [coded] LC 297  Serialize / Deserialize  preorder with "#" for null, comma separated
 *           LC 889  From Preorder + Postorder  full binary tree: pre[1] roots the left part
 *           LC 449  Serialize a BST          no null markers needed (bounds rebuild it)
 *           LC 1028 Recover from Preorder    dashes give the depth; stack of the path
 *           LC 536  Construct from String    "4(2(3)(1))(6(5))": recursive descent
 *
 * PITFALLS
 *   - Duplicate values make pre + in ambiguous; the problem must promise uniqueness.
 *   - Postorder build must do the RIGHT subtree first, since you read roots backwards.
 *   - Serialization without null markers cannot rebuild a general tree.
 *
 * DEEP DIVE
 *   C23_ConstructBinaryTreeFromINPre, D02_SerializeAndDeserialiseBinaryTree,
 *   C24_BinaryTreeFromString (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class BuildSerializeTree {

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

    private static int cursor;

    // Canonical LC 105.
    static TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            index.put(inorder[i], i);
        }
        cursor = 0;
        return fromPre(preorder, index, 0, inorder.length - 1);
    }

    private static TreeNode fromPre(int[] pre, Map<Integer, Integer> index, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        TreeNode root = new TreeNode(pre[cursor++]);
        int mid = index.get(root.val);
        root.left = fromPre(pre, index, lo, mid - 1);
        root.right = fromPre(pre, index, mid + 1, hi);
        return root;
    }

    // LC 106: postorder read backwards is root, right subtree, left subtree.
    static TreeNode buildTreeFromPost(int[] inorder, int[] postorder) {
        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            index.put(inorder[i], i);
        }
        cursor = postorder.length - 1;
        return fromPost(postorder, index, 0, inorder.length - 1);
    }

    private static TreeNode fromPost(int[] post, Map<Integer, Integer> index, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        TreeNode root = new TreeNode(post[cursor--]);
        int mid = index.get(root.val);
        root.right = fromPost(post, index, mid + 1, hi);
        root.left = fromPost(post, index, lo, mid - 1);
        return root;
    }

    // LC 297 serialize: preorder, "#" for null.
    static String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        writePre(root, sb);
        return sb.substring(0, sb.length() - 1);   // drop the trailing comma
    }

    private static void writePre(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("#,");
            return;
        }
        sb.append(node.val).append(',');
        writePre(node.left, sb);
        writePre(node.right, sb);
    }

    // LC 297 deserialize: read the tokens back in the same preorder.
    static TreeNode deserialize(String data) {
        return readPre(Arrays.asList(data.split(",")).iterator());
    }

    private static TreeNode readPre(Iterator<String> tokens) {
        String t = tokens.next();
        if (t.equals("#")) {
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(t));
        node.left = readPre(tokens);
        node.right = readPre(tokens);
        return node;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 105 five nodes",
                TreeNode.levels(buildTree(new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7})),
                "[3,9,20,null,null,15,7]");
        check("LC 105 single", TreeNode.levels(buildTree(new int[]{-1}, new int[]{-1})), "[-1]");
        check("LC 105 left chain",
                TreeNode.levels(buildTree(new int[]{1, 2, 3}, new int[]{3, 2, 1})),
                "[1,2,null,3]");

        int[] in = {9, 3, 15, 20, 7};
        int[] post = {9, 15, 7, 20, 3};
        check("LC 106 five nodes",
                TreeNode.levels(buildTreeFromPost(in, post)),
                "[3,9,20,null,null,15,7]");

        TreeNode t = TreeNode.of(1, 2, 3, null, null, 4, 5);
        String data = serialize(t);
        check("LC 297 serialized", data, "1,2,#,#,3,4,#,#,5,#,#");
        check("LC 297 round trip", TreeNode.levels(deserialize(data)), "[1,2,3,null,null,4,5]");
        check("LC 297 empty tree", serialize(null), "#");
        check("LC 297 empty round trip", TreeNode.levels(deserialize("#")), "[]");
    }
}
