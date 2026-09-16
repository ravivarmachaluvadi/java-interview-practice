import java.util.LinkedList;
import java.util.Queue;

// Definition for a binary tree node.
// Serialized Tree: 1,2,3,null,null,4,5,null,null,null,null,
// Deserialized Tree (Serialized Again): 1,2,3,null,null,4,5,null,null,null,null,
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

/*
    1
   / \
  2   3
     / \
    4   5
 */
// include null, also in serialization
class ImportantSerializeAndDeserialiseBinaryTree {

    // Encodes a tree to a single string.
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
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data.equals("null")) return null;
        String[] strings = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(strings[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int i = 1; // zero indicates root object ,
        // i=1 indicates left child of root
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (!strings[i].equals("null")) {
                node.left = new TreeNode(Integer.parseInt(strings[i]));
                queue.add(node.left);
            }
            i++;
            if (!strings[i].equals("null")) {
                node.right = new TreeNode(Integer.parseInt(strings[i]));
                queue.add(node.right);
            }
            // remember i++ after right assignment too
            i++;
        }
        return root;
    }

    public static void main(String[] args) {
        ImportantSerializeAndDeserialiseBinaryTree codec = new ImportantSerializeAndDeserialiseBinaryTree();

        // Example: Construct a binary tree
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(5);

        // Serialize the tree
        String serialized = codec.serialize(root);
        System.out.println("Serialized Tree: " + serialized);

        // Deserialize the string back to a tree
        TreeNode deserializedRoot = codec.deserialize(serialized);
        System.out.println("Deserialized Tree (Serialized Again): " + codec.serialize(deserializedRoot));

    }
}
