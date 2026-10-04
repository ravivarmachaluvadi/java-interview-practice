/*
 * =====================================================================
 *  P031 Fast and Slow Pointers (Floyd)   Canonical LC 141 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 141, Linked List Cycle)
 *   Return true if following next pointers from head ever revisits a node. O(1) memory.
 *
 * EXAMPLE
 *   3 -> 2 -> 0 -> -4 -> (back to 2)   ->  true
 *   1 -> 2 -> (back to 1)              ->  true
 *   1                                  ->  false
 *
 * RECOGNIZE WHEN
 *   - A linked list (or any "next" function: x -> f(x)) and the question is about a cycle,
 *     its start, or its length.
 *   - "middle of the list", "second half", "k-th from the middle" in one pass.
 *   - "find the duplicate in 1..n without modifying the array, O(1) space" (index -> value
 *     is a next pointer).
 *   Not this if: extra memory is fine -> a HashSet of visited nodes is simpler; you need the
 *   k-th node from the END -> P034_GapPointers.
 *
 * TEMPLATE
 *   slow = fast = head
 *   while fast and fast.next:
 *       slow = slow.next; fast = fast.next.next
 *       if slow == fast: cycle found                 // LC 141
 *   // cycle start (LC 142): reset one pointer to head; step both by 1; they meet at start
 *   // middle (LC 876): when fast stops, slow is the middle (second middle if even)
 *
 * APPROACH
 *   1. Move slow by one and fast by two.
 *   2. Without a cycle, fast falls off the end. With one, fast gains one step per round on
 *      slow inside the loop, so it must land on slow.
 *
 * KEY INSIGHT
 *   Relative speed 1 means the gap inside the cycle shrinks by exactly 1 each round, so
 *   the pointers cannot jump past each other. For the start: if the tail has length a, the
 *   meeting point is a steps (mod cycle length) before the start, which is why walking a
 *   steps from head and from the meeting point lands both on the start.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 142  Cycle Start              after meeting, restart one pointer at head
 *   [coded] LC 876  Middle of the List       stop when fast can no longer jump twice
 *   [coded] LC 202  Happy Number             next(x) = sum of squared digits; cycle at 1?
 *   [coded] LC 287  Find the Duplicate       next(i) = nums[i]; the cycle start is the dup
 *           Cycle length                     after meeting, walk one pointer round once
 *           LC 2095 Delete the Middle Node   stop slow one node earlier (start fast ahead)
 *           LC 457  Circular Array Loop      Floyd per start, with direction checks
 *           LC 234 / LC 143                  middle + reverse -> P032_InPlaceReversal
 *
 * PITFALLS
 *   - Check fast != null AND fast.next != null before fast.next.next.
 *   - For the FIRST middle of an even list, start fast at head.next.
 *   - LC 287: start both at index 0 (value 0 never appears, so 0 is outside the cycle).
 *
 * DEEP DIVE
 *   C07_LinkedListLoopDetection, C08_LinkedListLoopLength, A03_FindMiddleOfLinkedList
 *   (06-Linked-List), B16_HappyNumber (17-Math-Bit-Manipulation),
 *   C15_FindDuplicate (01-Arrays)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class FastSlowPointers {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }

        // Builds a list; if pos >= 0 the tail links back to the node at index pos.
        static ListNode withCycle(int pos, int... vals) {
            ListNode dummy = new ListNode(0);
            ListNode cur = dummy;
            ListNode loopTo = null;
            for (int i = 0; i < vals.length; i++) {
                cur.next = new ListNode(vals[i]);
                cur = cur.next;
                if (i == pos) {
                    loopTo = cur;
                }
            }
            cur.next = loopTo;
            return dummy.next;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (ListNode n = this; n != null; n = n.next) {
                sb.append(n.val).append(n.next == null ? "" : ", ");
            }
            return sb.append("]").toString();
        }
    }

    // Canonical LC 141.
    static boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    // LC 142: the node where the cycle begins, or null.
    static ListNode detectCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                ListNode p = head;
                while (p != slow) {
                    p = p.next;
                    slow = slow.next;
                }
                return p;
            }
        }
        return null;
    }

    // LC 876: second middle for even lengths.
    static ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // LC 202: the digit-square sequence either reaches 1 or cycles.
    static boolean isHappy(int n) {
        int slow = n;
        int fast = next(n);
        while (fast != 1 && slow != fast) {
            slow = next(slow);
            fast = next(next(fast));
        }
        return fast == 1;
    }

    private static int next(int n) {
        int sum = 0;
        while (n > 0) {
            int d = n % 10;
            sum += d * d;
            n /= 10;
        }
        return sum;
    }

    // LC 287: values in 1..n, n + 1 slots; i -> nums[i] is a list whose cycle starts at
    // the duplicated value.
    static int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[nums[0]];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }
        int p = 0;
        while (p != slow) {
            p = nums[p];
            slow = nums[slow];
        }
        return p;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 141 [3,2,0,-4] pos=1", hasCycle(ListNode.withCycle(1, 3, 2, 0, -4)), true);
        check("LC 141 [1,2] pos=0", hasCycle(ListNode.withCycle(0, 1, 2)), true);
        check("LC 141 [1] no cycle", hasCycle(ListNode.withCycle(-1, 1)), false);
        check("LC 141 empty list", hasCycle(null), false);

        check("LC 142 start value", detectCycle(ListNode.withCycle(1, 3, 2, 0, -4)).val, 2);
        check("LC 142 cycle at head", detectCycle(ListNode.withCycle(0, 1, 2)).val, 1);
        check("LC 142 no cycle", detectCycle(ListNode.withCycle(-1, 1)), null);

        check("LC 876 odd length", middleNode(ListNode.withCycle(-1, 1, 2, 3, 4, 5)), "[3, 4, 5]");
        check("LC 876 even length",
                middleNode(ListNode.withCycle(-1, 1, 2, 3, 4, 5, 6)), "[4, 5, 6]");

        check("LC 202 n=19", isHappy(19), true);
        check("LC 202 n=2", isHappy(2), false);
        check("LC 202 n=1", isHappy(1), true);

        check("LC 287 [1,3,4,2,2]", findDuplicate(new int[]{1, 3, 4, 2, 2}), 2);
        check("LC 287 [3,1,3,4,2]", findDuplicate(new int[]{3, 1, 3, 4, 2}), 3);
        check("LC 287 [3,3,3,3,3]", findDuplicate(new int[]{3, 3, 3, 3, 3}), 3);
    }
}
