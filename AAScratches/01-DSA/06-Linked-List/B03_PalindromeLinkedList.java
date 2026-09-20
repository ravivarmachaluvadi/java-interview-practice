/*
 * =====================================================================
 *  Palindrome Linked List                       LeetCode 234 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list, return true if the values read the same
 *   forwards and backwards. The follow-up that matters: do it in O(n) time and O(1)
 *   extra space, and leave the list as you found it.
 *
 * EXAMPLE
 *   [1, 2, 2, 1]      ->  true     even length
 *   [1, 2, 3, 2, 1]   ->  true     odd length, the middle 3 pairs with itself
 *   [1, 2, 3]         ->  false    list must still read [1, 2, 3] afterwards
 *   [7]               ->  true     single node
 *   []                ->  true     empty
 *
 * APPROACH  (middle, reverse second half, compare, restore)
 *   1. slow/fast to the middle. With the guard fast != null && fast.next != null,
 *      slow stops on the first node of the second half (even n) or on the exact
 *      middle node (odd n). Either way, reversing from slow is safe.
 *   2. Reverse the second half in place; remember its new head for the restore step.
 *   3. Walk from head and from the reversed head together while the reversed side is
 *      not null. Any mismatch means "not a palindrome" - but do not return yet.
 *   4. Reverse the second half back so the caller's list is intact, then return.
 *
 * KEY INSIGHT
 *   Reversing the second half turns "compare index i with n-1-i" into a forward walk of
 *   two lists. For odd n the middle node ends up in the reversed half and is compared
 *   with itself, which is harmless, so no special case is needed. The senior signal is
 *   the O(1) space version WITH restoration; the stack/array version is the fallback.
 *
 * Fixed: the original returned false on the first mismatch before restoring the second
 *   half, so a non-palindrome input was handed back mutated. Now the result is recorded
 *   and the restore always runs.
 *
 * COMPLEXITY
 *   Time  O(n)  middle + reverse + compare + restore, each a single pass over half
 *   Space O(1)  pointers only
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(n) space version: copy values to an ArrayList and two-pointer compare.
 *   - Recursive version: recurse to the end and compare against a front pointer on unwind.
 *   - Why not modify the list? Explain how you restore it and what breaks if you skip it.
 *   - Same shape as C09_MaximumTwinSum and C10_ReorderList; name the shared primitive.
 *
 * RUN
 *   main() runs 5 cases (even, odd, non-palindrome, single, empty) and prints actual vs
 *   expected; the non-palindrome case also prints the list after the call to prove restore.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

class ImpPalindromeLinkedList {

    public static boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }

        // Step 1: find the middle (first node of the second half for even n)
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: reverse the second half in place
        ListNode reversedHead = reverse(slow);

        // Step 3: compare, but record the answer instead of returning early
        boolean palindrome = true;
        ListNode left = head;
        ListNode right = reversedHead;
        while (right != null) {
            if (left.val != right.val) {
                palindrome = false;
                break;
            }
            left = left.next;
            right = right.next;
        }

        // Step 4: always restore the list before returning
        reverse(reversedHead);
        return palindrome;
    }

    // Standard iterative reversal (A02); calling it twice on the same half undoes it.
    private static ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode nextNode = head.next;
            head.next = prev;
            prev = head;
            head = nextNode;
        }
        return prev;
    }

    // ---- test helpers ----

    static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    static String toList(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.val);
        }
        return sb.append("]").toString();
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 even palindrome [1,2,2,1]  ", isPalindrome(build(1, 2, 2, 1)), true);
        print("case 2 odd palindrome  [1,2,3,2,1]", isPalindrome(build(1, 2, 3, 2, 1)), true);

        ListNode notPal = build(1, 2, 3);
        print("case 3 not palindrome  [1,2,3]    ", isPalindrome(notPal), false);
        print("case 3 list after call            ", toList(notPal), "[1, 2, 3]");

        print("case 4 single node     [7]        ", isPalindrome(build(7)), true);
        print("case 5 empty           []         ", isPalindrome(null), true);
    }
}
