import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}
/*
       1
     /   \
    2     3
     \     \
      5     4

 */
class RightView {
    public static List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        rightView(root, result, 0);
        return result;
    }

    private static void rightView(TreeNode node, List<Integer> result, int depth) {
        if (node == null) {
            return;
        }

        // If this is the first node of this level, add it to the result
        if (depth == result.size()) {
            result.add(node.val);
        }

        // Traverse right first, then left (to ensure rightmost nodes are prioritized)
        rightView(node.right, result, depth + 1);
        rightView(node.left, result, depth + 1);
    }

    // Function to print right view
    public static List<Integer> rightView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int size = queue.size();

            // Traverse level by level
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                // Last node in the current level → right view
                if (i == size - 1) {
                    result.add(node.val);
                }

                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return result;
    }

    public static List<Integer> leftView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int size = queue.size();

            // Traverse level by level
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                // First node in the current level → left view
                if (i == 0) {
                    result.add(node.val);
                }

                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        // Construct the binary tree
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(4);

        // Create an instance of RightView and get the right side view of the tree
        RightView rightView = new RightView();
        List<Integer> rightSide = rightView.rightSideView(root);

        // Print the right side view
        System.out.println("Right side view of the binary tree: " + rightSide);
    }
}
