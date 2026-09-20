/*
 * =====================================================================
 *  Maximum Level Sum of a Binary Tree                   LeetCode 1161 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, levels are numbered from 1 at the root. Return the
 *   SMALLEST level number whose node-value sum is the largest. Values may be negative.
 *
 * EXAMPLE
 *   [1,7,0,7,-8]         ->  2    level sums: 1, 7, -1  -> level 2 wins
 *   [-100,-200,-300]     ->  1    level sums: -100, -500 -> the least negative is level 1
 *   [5]                  ->  1    single node
 *   [1,2,-1,3,-2]        ->  1    level sums: 1, 1, 1  -> tie, the smallest level (1) wins
 *
 * APPROACH  (BFS with a per-level accumulator)
 *   1. Push the root; keep maxSum = MIN_VALUE, bestLevel = 0, level = 0.
 *   2. Each pass of the while loop handles ONE level: remember levelSize = queue.size(),
 *      bump level, then pop exactly levelSize nodes, adding each value to sum and
 *      enqueueing its children.
 *   3. After the level, if sum > maxSum (strictly greater) record maxSum and bestLevel.
 *      The strict comparison is what makes ties resolve to the smallest level.
 *   4. Return bestLevel.
 *
 * KEY INSIGHT
 *   The level-size snapshot idiom (int n = queue.size() before the inner loop) is what turns
 *   a plain BFS into a "per level" BFS. Every level-flavoured question (views, zigzag,
 *   averages, max sum) is this exact loop with a different line inside the for.
 *   Start maxSum at Integer.MIN_VALUE, not 0, or an all-negative tree returns the wrong level.
 *
 * COMPLEXITY
 *   Time  O(n)  every node enqueued and dequeued once
 *   Space O(w)  queue holds at most one level, w = maximum width of the tree
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the maximum sum instead of the level (same loop, return maxSum).
 *   - DFS version: carry depth down and keep a List<Integer> of sums indexed by depth.
 *   - Average of levels (LC 637) and largest value per level (LC 515): same template.
 *   - What if levels start at 0 instead of 1? (Bump level after recording, not before.)
 *
 * RUN
 *   main() runs 4 cases (typical, all negative, single node, tie) and prints
 *   actual vs expected.
 */
import java.util.LinkedList;
import java.util.Queue;

class MaxLevelSum {

    public int maxLevelSum(TreeNode root) {
        if (root == null) return 0;

        int maxSum = Integer.MIN_VALUE;   // MIN_VALUE, not 0: sums can all be negative
        int bestLevel = 0;
        int level = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();   // snapshot: only these nodes belong to this level
            int sum = 0;
            level++;
            for (int i = 0; i < levelSize; i++) {
                TreeNode cur = queue.poll();
                sum += cur.val;
                if (cur.left != null) queue.add(cur.left);
                if (cur.right != null) queue.add(cur.right);
            }
            if (sum > maxSum) {   // strict '>' keeps the earliest level on a tie
                maxSum = sum;
                bestLevel = level;
            }
        }
        return bestLevel;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MaxLevelSum solver = new MaxLevelSum();

        // case 1:       1          level sums: 1, 7, -1
        //              / \
        //             7   0
        //            / \
        //           7  -8
        TreeNode t1 = new TreeNode(1);
        t1.left = new TreeNode(7);
        t1.right = new TreeNode(0);
        t1.left.left = new TreeNode(7);
        t1.left.right = new TreeNode(-8);
        print("case 1 [1,7,0,7,-8]", solver.maxLevelSum(t1), 2);

        // case 2: all negative   -100        level sums: -100, -500
        //                        /   \
        //                     -200  -300
        TreeNode t2 = new TreeNode(-100);
        t2.left = new TreeNode(-200);
        t2.right = new TreeNode(-300);
        print("case 2 all negative", solver.maxLevelSum(t2), 1);

        // case 3: single node
        print("case 3 single node", solver.maxLevelSum(new TreeNode(5)), 1);

        // case 4: tie          1          level sums: 1, 1, 1 -> smallest level wins
        //                     / \
        //                    2  -1
        //                   / \
        //                  3  -2
        TreeNode t4 = new TreeNode(1);
        t4.left = new TreeNode(2);
        t4.right = new TreeNode(-1);
        t4.left.left = new TreeNode(3);
        t4.left.right = new TreeNode(-2);
        print("case 4 three-way tie", solver.maxLevelSum(t4), 1);
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}
