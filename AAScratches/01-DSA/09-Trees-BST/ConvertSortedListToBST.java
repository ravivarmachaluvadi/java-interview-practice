import java.util.*;

class ConvertSortedListToBST {

    public static void main(String[] args) {
        // Example sorted array
        int[] sortedArray = {-10, -3, 0, 5, 9};

        // Build linked list from the sorted array
        ListNode head = buildLinkedList(sortedArray);

        // Convert sorted linked list to BST
        Solution solution = new Solution();
        TreeNode bstRoot = solution.sortedListToBST(head);

        // Print the BST in-order to verify
        System.out.println("In-order Traversal of the BST:");
        printInOrder(bstRoot);
    }

    private static ListNode buildLinkedList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode head = new ListNode(arr[0]);
        ListNode current = head;
        for (int i = 1; i < arr.length; i++) {
            current.next = new ListNode(arr[i]);
            current = current.next;
        }
        return head;
    }

    private static void printInOrder(TreeNode root) {
        if (root == null) return;
        printInOrder(root.left);
        System.out.print(root.val + " ");
        printInOrder(root.right);
    }
}

class Solution {
    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) return null;
        return buildBST(head, null);
    }

    private TreeNode buildBST(ListNode start, ListNode end) {
        // remember base case
        if (start == end) return null;

        // Find the middle element to be the root
        ListNode mid = findMiddle(start, end);

        // Create the root node of the subtree
        TreeNode root = new TreeNode(mid.val);

        // Recursively build the left and right subtrees
        root.left = buildBST(start, mid);
        root.right = buildBST(mid.next, end);

        return root;
    }


    private ListNode findMiddle(ListNode start, ListNode end) {
        ListNode slow = start;
        ListNode fast = start;

        // remember fast!=end not null here
        while (fast != end && fast.next != end) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}
