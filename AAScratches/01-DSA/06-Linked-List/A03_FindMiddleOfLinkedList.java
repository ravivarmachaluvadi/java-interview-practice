/*
 * =====================================================================
 *  Middle of the Linked List               LeetCode 876 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list, return the middle node in one
 *   pass without knowing the length. For an even number of nodes there are
 *   two middles; LeetCode 876 wants the SECOND one. Some interviewers want
 *   the first (needed when splitting a list for merge sort), so both are here.
 *
 * EXAMPLE
 *   1 -> 2 -> 3 -> 4 -> 5        ->  3          (odd: unique middle)
 *   1 -> 2 -> 3 -> 4 -> 5 -> 6   ->  4          (even: second middle)
 *   1 -> 2 -> 3 -> 4 -> 5 -> 6   ->  3          (even: first middle variant)
 *   7                            ->  7          (single node)
 *   (empty)                      ->  null
 *
 * APPROACH  (slow/fast pointers)
 *   1. slow = fast = head.
 *   2. While fast != null && fast.next != null: fast moves 2, slow moves 1.
 *   3. When fast runs out, slow has covered half the distance: the middle.
 *   4. For the FIRST middle, use the guard fast.next != null && fast.next.next
 *      != null so fast stops one step earlier on even lengths.
 *
 * KEY INSIGHT
 *   Two pointers moving at a 2:1 ratio meet the halfway point without
 *   counting. The exact loop guard decides which middle you get; memorise
 *   both guards. This primitive feeds cycle detection, palindrome check,
 *   reorder list and merge sort on lists.
 *
 * COMPLEXITY
 *   Time  O(n)  fast visits about n/2 nodes, slow about n/2
 *   Space O(1)  two pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Delete the middle node (LeetCode 2095): track prev of slow.
 *   - Split a list in two halves for merge sort: needs the FIRST middle.
 *   - Same idea with a gap of n instead of 2:1 -> remove nth from end.
 *
 * Fixed: the old header claimed the first middle is returned; the code
 *   (and LeetCode 876) return the second. Header corrected; a
 *   findFirstMiddle method was added to show the other guard.
 *
 * RUN
 *   main() runs 5 cases (odd, even, single, two nodes, empty) through both
 *   methods and prints actual vs expected.
 */
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class FindMiddleOfLinkedList {

    /** LeetCode 876 semantics: for even length returns the SECOND middle. */
    static Node findMiddle(Node head) {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    /** Variant: for even length returns the FIRST middle (used to split for merge sort). */
    static Node findFirstMiddle(Node head) {
        if (head == null) return null;
        Node slow = head;
        Node fast = head;
        // fast stops one step earlier, so slow lands on the first of two middles
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    static Node build(int... vals) {
        Node dummy = new Node(0);
        Node tail = dummy;
        for (int v : vals) {
            tail.next = new Node(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String valueOf(Node node) {
        return node == null ? "null" : String.valueOf(node.data);
    }

    static void print(String label, Node actual, String expected) {
        System.out.println(label + ": " + valueOf(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 odd    1..5 second", findMiddle(build(1, 2, 3, 4, 5)), "3");
        print("case 1 odd    1..5 first ", findFirstMiddle(build(1, 2, 3, 4, 5)), "3");
        print("case 2 even   1..6 second", findMiddle(build(1, 2, 3, 4, 5, 6)), "4");
        print("case 2 even   1..6 first ", findFirstMiddle(build(1, 2, 3, 4, 5, 6)), "3");
        print("case 3 single 7    second", findMiddle(build(7)), "7");
        print("case 4 two    1,2  second", findMiddle(build(1, 2)), "2");
        print("case 4 two    1,2  first ", findFirstMiddle(build(1, 2)), "1");
        print("case 5 empty       second", findMiddle(null), "null");
        print("case 5 empty       first ", findFirstMiddle(null), "null");
    }
}
