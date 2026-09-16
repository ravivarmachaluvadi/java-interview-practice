import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
        left = null;
        right = null;
    }
}

class AllNodesDistanceKInBinaryTree {

    void markParents(TreeNode root, HashMap<TreeNode, TreeNode> parentTrack) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode parent = queue.poll();

            TreeNode left = parent.left;
            if (left != null) {
                parentTrack.put(left, parent);
                queue.add(left);
            }

            TreeNode right = parent.right;
            if (right != null) {
                parentTrack.put(right, parent);
                queue.add(right);
            }
        }
    }

    List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        HashMap<TreeNode, TreeNode> parentTrack = new HashMap<>();
        markParents(root, parentTrack);

        HashMap<TreeNode, Boolean> visited = new HashMap<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(target);
        visited.put(target, true); // Mark the target as visited
        int currLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            if (currLevel == k) {
                break;
            }
            currLevel++;
            for (int i = 0; i < size; i++) {
                TreeNode current = queue.poll();

                // Process left child
                TreeNode left = current.left;
                if (left != null && !visited.containsKey(left)) {
                    queue.add(left);
                    visited.put(left, true);
                }

                // Process right child
                TreeNode right = current.right;
                if (right != null && !visited.containsKey(right)) {
                    queue.add(right);
                    visited.put(right, true);
                }

                // parentTrack.containsKey ( current ) condition for leafs as leafs won't be parents
                TreeNode parent = parentTrack.get(current);
                if (parentTrack.containsKey(current) && !visited.containsKey(parent)) {
                    queue.add(parent);
                    visited.put(parent, true);
                }
            }
        }

        // Collect the result
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            result.add(current.val);
        }

        return result;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        AllNodesDistanceKInBinaryTree sol = new AllNodesDistanceKInBinaryTree();
        TreeNode target = root.left;
        int k = 2;
        List<Integer> result = sol.distanceK(root, target, k);

        System.out.print("Nodes at distance " + k + " from target node " + target.val + ": ");
        for (int val : result) {
            System.out.print(val + " ");
        }
        System.out.println();
    }
}
