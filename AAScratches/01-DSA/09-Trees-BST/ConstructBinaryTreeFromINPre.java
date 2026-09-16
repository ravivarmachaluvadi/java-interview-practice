import java.util.*;

class ConstructBinaryTreeFromINPre {

    public TreeNode buildTree(List<Integer> preorder, List<Integer> inorder) {
        Map<Integer, Integer> inMap = new HashMap<>();
        for (int i = 0; i < inorder.size(); i++)
            // element , index
            inMap.put(inorder.get(i), i);
        return buildTree(preorder, 0, preorder.size() - 1, 0, inorder.size() - 1, inMap);
    }

    private TreeNode buildTree(List<Integer> preorder, int preStart, int preEnd,
                               int inStart, int inEnd, Map<Integer, Integer> inMap) {

        if (preStart > preEnd || inStart > inEnd)
            return null;

        TreeNode root = new TreeNode(preorder.get(preStart));
        int inRootIndex = inMap.get(root.val);
        int numsLeftInOrder = inRootIndex - inStart;

        root.left = buildTree(preorder,
                preStart + 1, preStart + numsLeftInOrder,
                inStart, inRootIndex - 1, inMap);

        root.right = buildTree(preorder,
                preStart + numsLeftInOrder + 1, preEnd,
                inRootIndex + 1, inEnd, inMap);

        return root;
    }

    private void printInorder(TreeNode root) {
        if (root != null) {
            printInorder(root.left);
            System.out.print(root.val + " ");
            printInorder(root.right);
        }
    }

    private void printVector(List<Integer> vec) {
        for (int i = 0; i < vec.size(); i++)
            System.out.print(vec.get(i) + " ");

        System.out.println();
    }

    public static void main(String[] args) {
        List<Integer> inorder = Arrays.asList(9, 3, 15, 20, 7);
        List<Integer> preorder = Arrays.asList(3, 9, 20, 15, 7);

        System.out.print("Inorder Vector: ");
        new ConstructBinaryTreeFromINPre().printVector(inorder);

        System.out.print("Preorder Vector: ");
        new ConstructBinaryTreeFromINPre().printVector(preorder);

        ConstructBinaryTreeFromINPre sol = new ConstructBinaryTreeFromINPre();
        TreeNode root = sol.buildTree(preorder, inorder);

        System.out.println("Inorder of Unique Binary Tree Created:");
        sol.printInorder(root);
        System.out.println();
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    public TreeNode(int x) {
        val = x;
        left = null;
        right = null;
    }
}
