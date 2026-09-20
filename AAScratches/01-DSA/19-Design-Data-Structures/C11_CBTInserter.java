/*
 * =====================================================================
 *  Complete Binary Tree Inserter                  LeetCode 919 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a COMPLETE binary tree (every level full except possibly the last,
 *   and the last filled left to right with no gaps), support insert(val), which adds a
 *   node so the tree stays complete and returns the value of the new node's PARENT, and
 *   get_root(), which returns the root.
 *
 * EXAMPLE
 *   start [1, 2, 3] insert(4) -> 2    4 becomes the left child of 2
 *   insert(5) -> 2    5 becomes the right child of 2
 *   insert(6) -> 3    tree is now [1, 2, 3, 4, 5, 6]
 *   edge case: start [1]; insert(2) -> 1, insert(3) -> 1, insert(4) -> 2
 *
 * DESIGN
 *   TreeNode          plain binary node (val, left, right).
 *   root              kept so get_root() is O(1).
 *   q: Queue          candidate PARENTS in level order -- nodes that may still have a
 *                     free child slot. The front is always the leftmost node on the
 *                     deepest incomplete level, which is exactly where the next node goes.
 *   Constructor       seeds the queue with the root ONLY. Children are enqueued lazily,
 *                     the moment their parent is found full, so no up-front BFS is needed
 *                     and level order is still preserved.
 *
 * KEY DECISIONS
 *   - insert() loops rather than assuming the front is free: it pops full nodes (both
 *     children present), enqueuing their children, until the front has a free slot.
 *   - After filling a node's RIGHT child the node is deliberately left in the queue. It
 *     is full now, so the next insert() pops it on its first iteration and enqueues its
 *     children then. Deferring costs nothing and keeps one exit path per branch.
 *   - Children are enqueued left then right; that is what keeps the tree complete.
 *
 * KEY INSIGHT
 *   "Complete" means the next free slot is the leftmost one on the deepest level, and a
 *   BFS queue hands you that node for free. Only nodes with a free child slot need to be
 *   retained; a full node is discarded forever once its children are enqueued, which is
 *   why every node is touched at most once.
 *
 * COMPLEXITY
 *   Time  insert O(1) amortized -- each node is enqueued once and polled once overall;
 *         get_root O(1).
 *   Space O(n) worst case, though in steady state the queue only holds the deepest level.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it with no queue: the path to 1-based index i is i's binary representation
 *     (0 = left, 1 = right) -- O(log n) insert, O(1) extra space.
 *   - Support deleting the last node (the array-backed heap's siftDown problem).
 *   - How does this relate to a binary heap stored in an array?
 *   - Prove the front of the queue is always the correct parent.
 *
 * RUN
 *   main() runs 3 cases (typical 3-node start, single-node start, and a 4-node start that
 *   forces the queue to skip a full node) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class CBTInserter {

    TreeNode root;
    Queue<TreeNode> q;

    public CBTInserter(TreeNode root) {
        this.root = root;
        this.q = new LinkedList<>();
        q.add(root);    // children are enqueued lazily, only when a node turns out full
    }

    public int insert(int val) {
        TreeNode node = new TreeNode(val);
        while (!q.isEmpty()) {
            TreeNode candidate = q.peek();

            if (candidate.left == null) {
                candidate.left = node;      // still has a right slot, so keep it queued
                return candidate.val;
            }
            if (candidate.right == null) {
                candidate.right = node;     // now full; the NEXT insert will pop it
                return candidate.val;
            }
            // Full node: retire it and promote its children as future parents, in order.
            q.poll();
            q.add(candidate.left);
            q.add(candidate.right);
        }
        return -1;      // unreachable for a non-null root
    }

    public TreeNode get_root() {
        return root;
    }

    /** Level-order values, so main() can show the whole tree on one line. */
    private static List<Integer> levelOrder(TreeNode root) {
        List<Integer> values = new ArrayList<>();
        if (root == null) {
            return values;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            values.add(node.val);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
        return values;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // ---- case 1: typical -- start from [1, 2, 3] ----------------------
        CBTInserter typical = new CBTInserter(new TreeNode(1, new TreeNode(2), new TreeNode(3)));
        print("case 1 insert 4 parent", typical.insert(4), 2);
        print("case 1 insert 5 parent", typical.insert(5), 2);
        print("case 1 insert 6 parent", typical.insert(6), 3);
        print("case 1 tree", levelOrder(typical.get_root()), "[1, 2, 3, 4, 5, 6]");

        // ---- case 2: edge case -- start from a single node ----------------
        CBTInserter single = new CBTInserter(new TreeNode(1));
        print("case 2 insert 2 parent", single.insert(2), 1);
        print("case 2 insert 3 parent", single.insert(3), 1);
        print("case 2 insert 4 parent", single.insert(4), 2);
        print("case 2 tree", levelOrder(single.get_root()), "[1, 2, 3, 4]");

        // ---- case 3: tricky -- last level partly filled, so the queue must
        //      skip node 2's left slot and land on its right slot first -----
        TreeNode root = new TreeNode(1,
                new TreeNode(2, new TreeNode(4), null),
                new TreeNode(3));
        CBTInserter partial = new CBTInserter(root);
        print("case 3 insert 5 parent", partial.insert(5), 2);   // fills 2's right
        print("case 3 insert 6 parent", partial.insert(6), 3);   // 2 is full, move to 3
        print("case 3 insert 7 parent", partial.insert(7), 3);
        print("case 3 insert 8 parent", partial.insert(8), 4);   // new level starts at 4
        print("case 3 tree", levelOrder(partial.get_root()), "[1, 2, 3, 4, 5, 6, 7, 8]");
    }
}
