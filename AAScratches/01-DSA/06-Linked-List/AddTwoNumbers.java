// https://leetcode.com/problems/add-two-numbers
/**
 * Input: l1 = [2,4,3], l2 = [5,6,4]
 * <p>
 * Output: [7,0,8]
 * <p>
 * Explanation: 342 + 465 = 807.
 */
class AddTwoNumbers {
    public Node addTwoNumbers(Node l1, Node l2) {
        Node dummy = new Node();
        Node temp = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = 0;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            sum += carry;
            // remember / and % seems you may confuse
            carry = sum / 10;
            Node node = new Node(sum % 10);
            temp.next = node;
            temp = temp.next;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        // Example: l1 = [2,4,3], l2 = [5,6,4]
        Node l1 = new Node(2);
        l1.next = new Node(4);
        l1.next.next = new Node(3);

        Node l2 = new Node(5);
        l2.next = new Node(6);
        l2.next.next = new Node(4);

        AddTwoNumbers solver = new AddTwoNumbers();
        Node result = solver.addTwoNumbers(l1, l2);

        // Print result
        // Expected Output: 7 -> 0 -> 8
        printList(result);
    }

    static void printList(Node head) {
        while (head != null) {
            System.out.print(head.val);
            if (head.next != null) System.out.print(" -> ");
            head = head.next;
        }
        System.out.println();
    }
}

class Node {
    int val;
    Node next;

    Node() {
    }

    Node(int val) {
        this.val = val;
    }
}