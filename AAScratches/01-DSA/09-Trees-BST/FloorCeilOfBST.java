import java.util.*;

class TreeNode {
    int data;
    TreeNode left, right;

    TreeNode(int val) {
        data = val;
        left = right = null;
    }
}

class FloorCeilOfBST {
    public List<Integer> floorCeilOfBST(TreeNode root, int key) {
        int floor = -1;
        int ceil = -1;

        TreeNode current = root;
        while (current != null) {
            // if else if ladder
            // update floor with current node data not with key
            if (current.data == key) {
                floor = current.data;
                break;
            } else if (current.data < key) {
                floor = current.data;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        // again reset pointer to root before going to find ceil
        current = root;
        while (current != null) {
            if (current.data == key) {
                ceil = current.data;
                break;
            } else if (current.data > key) {
                ceil = current.data;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return Arrays.asList(floor, ceil);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(8);
        root.left = new TreeNode(4);
        root.right = new TreeNode(12);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(6);
        root.right.left = new TreeNode(10);
        root.right.right = new TreeNode(14);

        FloorCeilOfBST sol = new FloorCeilOfBST();
        int key = 11;
        List<Integer> result = sol.floorCeilOfBST(root, key);
        System.out.println("Floor: " + result.get(0) + ", Ceil: " + result.get(1));
        // Floor: 10, Ceil: 12
    }
}
