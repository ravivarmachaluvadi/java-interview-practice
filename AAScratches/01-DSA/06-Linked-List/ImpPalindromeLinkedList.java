/**
 * Problem: Determine whether a singly linked list is a palindrome.
 *
 * Approach:
 * 1. Use fast and slow pointers to find the middle of the list.
 * 2. Reverse the second half in place.
 * 3. Compare nodes from the start and from the reversed second half.
 * 4. Restore the original list structure (optional).
 *
 * Time Complexity: O(n) – one pass to find middle, one to reverse, one to compare.
 * Space Complexity: O(1) – only a few pointers are used; no extra data structures.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

public class ImpPalindromeLinkedList {

    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null)
            return true;

        // Step 1: Find the middle of the linked list
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: Reverse the second half of the list
        ListNode secondHalfHead = reverse(slow);

        // Step 3: Compare the first half and the reversed second half
        ListNode firstHalfHead = head;
        ListNode tempSecondHalfHead = secondHalfHead; // Preserve for list restoration

        while (secondHalfHead != null) {
            if (firstHalfHead.val != secondHalfHead.val) {
                return false;
            }
            firstHalfHead = firstHalfHead.next;
            secondHalfHead = secondHalfHead.next;
        }

        // Step 4: Restore the list (optional)
        reverse(tempSecondHalfHead);

        return true;
    }

    // Helper function to reverse a linked list
    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode nextNode = head.next;
            head.next = prev;
            prev = head;
            head = nextNode;
        }
        return prev;
    }

    // Helper function to print the linked list
    private void printList(ListNode head) {
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public static void main(String[] args) {
        // Example: Creating a linked list [1, 2, 2, 1]
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(2);
        ListNode node4 = new ListNode(1);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;

        // Print original list
        ImpPalindromeLinkedList pll = new ImpPalindromeLinkedList();
        System.out.print("Original List: ");
        pll.printList(node1);

        // Check if the list is a palindrome
        boolean result = pll.isPalindrome(node1);
        System.out.println("Is the list a palindrome? " + result);

        // Print restored list
        System.out.print("Restored List: ");
        pll.printList(node1);
    }
}
