/*
 * =====================================================================
 *  Add Two Numbers                                  LeetCode 2 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Two non-empty linked lists each represent a non-negative integer with the digits stored in
 *   REVERSE order (least significant digit first). Return their sum as a new list in the same
 *   reversed form. Lists may have different lengths; a final carry may add an extra digit.
 *
 * EXAMPLE
 *   l1 = [2,4,3], l2 = [5,6,4]           ->  [7,0,8]            because 342 + 465 = 807
 *   l1 = [0], l2 = [0]                   ->  [0]
 *   l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9] ->  [8,9,9,9,0,0,0,1]  9999999 + 9999 = 10009998
 *
 * APPROACH  (dummy head with carry propagation)
 *   1. Create a dummy sentinel node; tail points at it and grows the answer list.
 *   2. Loop while l1 != null || l2 != null || carry != 0.
 *   3. Each round: sum = carry + (digit of l1 if any) + (digit of l2 if any); advance whichever
 *      list contributed a digit.
 *   4. Append a node holding sum % 10; the new carry is sum / 10.
 *   5. Return dummy.next (the dummy itself is never part of the answer).
 *
 * KEY INSIGHT
 *   Put the carry INTO the loop condition. Then the trailing carry (9+1 -> 10), lists of unequal
 *   length, and the normal case are all handled by the same body with no special post-loop code.
 *   Pattern to recognise: "grade-school addition, one digit per iteration, carry as loop state".
 *
 * COMPLEXITY
 *   Time  O(max(m, n))  one pass over the longer list, plus at most one extra node for the carry
 *   Space O(max(m, n))  for the result list (O(1) extra beyond the output)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Add Two Numbers II (LeetCode 445): digits stored most-significant first -> use two stacks,
 *     or reverse both lists, add, reverse the result.
 *   - Can you do it in place, reusing the longer list's nodes? Yes, but the carry may still need
 *     one new node at the end.
 *   - What if digits could be larger than 9? sum / 10 and sum % 10 still work as long as you
 *     keep the carry as an int and do not assume carry is 0 or 1.
 *
 * RUN
 *   main() runs 3 cases (typical, both zero, long carry chain) and prints actual vs expected.
 */
class AddTwoNumbers {

    public Node addTwoNumbers(Node l1, Node l2) {
        Node dummy = new Node();
        Node tail = dummy;
        int carry = 0;
        // carry in the condition: the final "1" of 5 + 5 = 10 gets its own iteration
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            carry = sum / 10;          // what moves to the next digit
            tail.next = new Node(sum % 10); // what stays in this digit
            tail = tail.next;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        AddTwoNumbers solver = new AddTwoNumbers();

        print("case 1 (342 + 465)",
                solver.addTwoNumbers(build(2, 4, 3), build(5, 6, 4)), "[7, 0, 8]");
        print("case 2 (0 + 0)",
                solver.addTwoNumbers(build(0), build(0)), "[0]");
        print("case 3 (9999999 + 9999)",
                solver.addTwoNumbers(build(9, 9, 9, 9, 9, 9, 9), build(9, 9, 9, 9)),
                "[8, 9, 9, 9, 0, 0, 0, 1]");
    }

    static void print(String label, Node actual, String expected) {
        System.out.println(label + ": " + toStr(actual) + "   expected " + expected);
    }

    static Node build(int... vals) {
        Node dummy = new Node();
        Node tail = dummy;
        for (int v : vals) {
            tail.next = new Node(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String toStr(Node head) {
        StringBuilder sb = new StringBuilder("[");
        for (Node n = head; n != null; n = n.next) {
            if (n != head) sb.append(", ");
            sb.append(n.val);
        }
        return sb.append("]").toString();
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
