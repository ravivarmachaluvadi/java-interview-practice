import java.util.*;

// https://leetcode.com/problems/merge-k-sorted-lists
// 23. Merge k Sorted Lists
class MergeKLists {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
        for (ListNode node : lists) {
            // add heads of all lists to minHeap
            if (node != null) minHeap.offer(node);
        }
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        while (!minHeap.isEmpty()) {
            ListNode node = minHeap.poll();
            current.next = node;
            current = current.next;
            // add if next node found means
            // not reached end of linked list
            if (node.next != null) minHeap.offer(node.next);
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        // build example linked lists
        ListNode l1 = new ListNode(1, new ListNode(4, new ListNode(5)));
        ListNode l2 = new ListNode(1, new ListNode(3, new ListNode(4)));
        ListNode l3 = new ListNode(2, new ListNode(6));
        ListNode[] lists = new ListNode[]{l1, l2, l3};

        // print input
        System.out.println("Input lists:");
        for (int i = 0; i < lists.length; i++) {
            System.out.print("List " + (i + 1) + ": ");
            ListNode cur = lists[i];
            while (cur != null) {
                System.out.print(cur.val);
                if (cur.next != null) System.out.print(" -> ");
                cur = cur.next;
            }
            System.out.println();
        }

        // merge
        MergeKLists solver = new MergeKLists();
        ListNode mergedHead = solver.mergeKLists(lists);

        // print output
        System.out.print("\nMerged list: ");
        ListNode cur = mergedHead;
        while (cur != null) {
            System.out.print(cur.val);
            if (cur.next != null) System.out.print(" -> ");
            cur = cur.next;
        }
        System.out.println();
    }
}


class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}