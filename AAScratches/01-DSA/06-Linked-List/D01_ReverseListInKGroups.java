/*
 * =====================================================================
 *  Reverse Nodes in k-Group                         LeetCode 25 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a linked list and an integer k, reverse every consecutive group of
 *   k nodes and return the new head. If the last group has fewer than k nodes it stays in
 *   its original order. Only links may be changed, not node values. k is 1..length.
 *
 * EXAMPLE
 *   5 -> 4 -> 3 -> 7 -> 9 -> 2, k = 4  ->  7 3 4 5 9 2     (trailing 9 2 is a partial group)
 *   1 -> 2 -> 3 -> 4 -> 5 -> 6, k = 2  ->  2 1 4 3 6 5     (every group full)
 *   1 -> 2 -> 3,             k = 1  ->  1 2 3             (k = 1 is a no-op)
 *   1 -> 2 -> 3,             k = 3  ->  3 2 1             (one group = whole list)
 *   1 -> 2,                  k = 3  ->  1 2               (shorter than k, unchanged)
 *
 * APPROACH  (windowed reversal with three boundaries per window)
 *   1. From the current group head, walk k-1 steps to find the k-th node. If it is null,
 *      the group is partial: reattach it unchanged to the previous group's tail and stop.
 *   2. Remember nextGroupHead = kth.next, then cut kth.next = null so the plain iterative
 *      reversal (A02) sees exactly this window and nothing after it.
 *   3. Reverse the window. After reversing, kth is the window's new head and the old group
 *      head is its new tail.
 *   4. Relink: if this was the first window, the overall head becomes kth; otherwise the
 *      previous window's tail points to kth. Record the old group head as the new
 *      previous tail and move on to nextGroupHead.
 *
 * KEY INSIGHT
 *   Per window you must hold exactly three references: the previous window's tail (to
 *   connect in), the window's k-th node (becomes its head), and the next window's head
 *   (to continue). Cutting the window off before reversing lets you reuse the standard
 *   reversal unchanged; the bookkeeping, not the reversal, is what the interview tests.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited twice: once by getKthNode, once by the reversal
 *   Space O(1)  in-place; a handful of pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reverse the partial tail too (Swap Nodes in Pairs generalised): drop the null check.
 *   - Reverse alternate groups only: skip every other window instead of reversing it.
 *   - Recursive version: reverse the first k, then head.next = reverse(rest, k). Elegant
 *     but O(n/k) stack depth; interviewers usually want the iterative one.
 *
 * RUN
 *   main() runs 5 cases (partial tail, all full, k=1, k=length, shorter than k) and prints
 *   actual vs expected.
 */
class ReverseListInKGroups {

    public static void main(String[] args) {
        print("case 1 partial tail", kReverse(fromArray(new int[]{5, 4, 3, 7, 9, 2}), 4),
                "7 3 4 5 9 2");
        print("case 2 all full    ", kReverse(fromArray(new int[]{1, 2, 3, 4, 5, 6}), 2),
                "2 1 4 3 6 5");
        print("case 3 k = 1       ", kReverse(fromArray(new int[]{1, 2, 3}), 1), "1 2 3");
        print("case 4 k = length  ", kReverse(fromArray(new int[]{1, 2, 3}), 3), "3 2 1");
        print("case 5 shorter     ", kReverse(fromArray(new int[]{1, 2}), 3), "1 2");
    }

    static Node kReverse(Node head, int k) {
        Node currentGroupHead = head;
        Node tailOfLastGroup = null;

        while (currentGroupHead != null) {
            Node kThNode = getKthNode(currentGroupHead, k);
            if (kThNode == null) {
                // partial last group: leave it as-is and hook it onto the previous group
                if (tailOfLastGroup != null) tailOfLastGroup.next = currentGroupHead;
                break;
            }

            // cut the window off so the reversal stops at kThNode
            Node nextGroupHead = kThNode.next;
            kThNode.next = null;
            reverseLinkedList(currentGroupHead);   // after this, kThNode is the window head

            if (currentGroupHead == head) {
                head = kThNode;                    // first window decides the overall head
            } else {
                tailOfLastGroup.next = kThNode;    // previous window's tail -> this window's head
            }

            // the old group head is now the window's tail
            tailOfLastGroup = currentGroupHead;
            currentGroupHead = nextGroupHead;
        }
        return head;
    }

    /** Returns the k-th node counting from currHead as 1, or null if fewer than k remain. */
    static Node getKthNode(Node currHead, int k) {
        k -= 1;
        while (currHead != null && k > 0) {
            k--;
            currHead = currHead.next;
        }
        return currHead;
    }

    /** Standard iterative reversal; returns the new head. */
    static Node reverseLinkedList(Node head) {
        Node curr = head;
        Node prev = null;
        while (curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    // ---- helpers for main ------------------------------------------------

    private static Node fromArray(int[] values) {
        Node dummy = new Node(0);
        Node current = dummy;
        for (int value : values) {
            current.next = new Node(value);
            current = current.next;
        }
        return dummy.next;
    }

    private static String toString(Node head) {
        StringBuilder sb = new StringBuilder();
        for (Node n = head; n != null; n = n.next) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(n.data);
        }
        return sb.toString();
    }

    private static void print(String label, Node actual, String expected) {
        System.out.println(label + ": " + toString(actual) + "   expected " + expected);
    }
}

class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
    }
}
