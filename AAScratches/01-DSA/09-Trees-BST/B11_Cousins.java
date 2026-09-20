/*
 * =====================================================================
 *  Cousins in Binary Tree                                LeetCode 993 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree with unique values and two different values x and y,
 *   return true if the nodes holding x and y are cousins: same depth, different parents.
 *   The root is at depth 0. Siblings (same parent) are NOT cousins.
 *
 * EXAMPLE
 *   tree [1,2,3,4,null,5], x=4, y=5  ->  true    both at depth 2, parents 2 and 3
 *   tree [1,2,3,4,5],      x=4, y=5  ->  false   same depth but same parent (siblings)
 *   tree [1,2,3,4,5],      x=2, y=4  ->  false   different depths
 *   tree [],               x=1, y=2  ->  false   nothing to find
 *
 * APPROACH  (BFS level by level, siblings rejected at the parent)
 *   1. Standard level-order loop: pop exactly queue.size() nodes per level.
 *   2. While popping a node, note whether it IS x or y (found flags for this level).
 *   3. Also look at its two children: if they are exactly {x, y}, they are siblings -> false.
 *      This check must happen at the parent, because a child cannot see its own parent.
 *   4. After finishing a level, if both flags are set the two nodes share a depth and were
 *      not rejected as siblings -> true.
 *   5. If a level ends with only one flag set, they are on different depths -> false.
 *
 * SECOND METHOD  (DFS recording depth and parent)
 *   Walk the tree carrying (depth, parent). When a node matches x or y, remember its depth
 *   and parent. Answer: depths equal AND parents differ. Same O(n), shows the "two facts per
 *   node" idea explicitly instead of hiding it in the BFS loop.
 *
 * KEY INSIGHT
 *   "Cousin" needs two facts about each node at once: its depth and who its parent is.
 *   BFS gives depth for free (the level loop) and gets parent-ness by checking children from
 *   the parent. DFS gets both by passing them down as parameters. Either way, the parent
 *   relationship is only visible from ABOVE the node, never from the node itself.
 *
 * COMPLEXITY
 *   Time  O(n)  each node visited once (both methods)
 *   Space O(w)  BFS queue holds one level (w = max width); DFS uses O(h) stack instead
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if values are not unique? (Need node references instead of values.)
 *   - Generalise: are two nodes at the same depth at all? (Drop the parent check.)
 *   - Early exit: BFS stops at the first level where either value is found; can DFS?
 *   - Related: Sum of Left Leaves (B06) is another "property only the parent can see" case.
 *
 * RUN
 *   main() runs 4 cases (true cousins, siblings, different depth, empty) through both
 *   methods and prints actual vs expected.
 */
import java.util.LinkedList;
import java.util.Queue;

class Cousins {

    // ---- Method 1: BFS with per-level found flags and a sibling check at the parent ----
    public boolean isCousinsBfs(TreeNode root, int x, int y) {
        if (root == null) return false;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            boolean foundX = false;
            boolean foundY = false;

            for (int i = 0; i < levelSize; i++) {
                TreeNode cur = queue.poll();
                if (cur.val == x) foundX = true;
                if (cur.val == y) foundY = true;

                // Siblings share this parent -> not cousins, no matter what else we find
                if (cur.left != null && cur.right != null) {
                    boolean xyChildren = (cur.left.val == x && cur.right.val == y)
                                      || (cur.left.val == y && cur.right.val == x);
                    if (xyChildren) return false;
                }

                if (cur.left != null) queue.offer(cur.left);
                if (cur.right != null) queue.offer(cur.right);
            }

            // Level complete: both present (and not siblings) means cousins
            if (foundX && foundY) return true;
            // Only one present means different depths
            if (foundX || foundY) return false;
        }
        return false;
    }

    // ---- Method 2: DFS carrying depth and parent, then compare the two records ----
    private int depthX, depthY;
    private TreeNode parentX, parentY;

    public boolean isCousinsDfs(TreeNode root, int x, int y) {
        depthX = depthY = -1;
        parentX = parentY = null;
        record(root, null, 0, x, y);
        if (depthX == -1 || depthY == -1) return false;   // one of them is not in the tree
        return depthX == depthY && parentX != parentY;
    }

    private void record(TreeNode node, TreeNode parent, int depth, int x, int y) {
        if (node == null) return;
        if (node.val == x) { depthX = depth; parentX = parent; }
        if (node.val == y) { depthY = depth; parentY = parent; }
        record(node.left, node, depth + 1, x, y);
        record(node.right, node, depth + 1, x, y);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Cousins solver = new Cousins();

        // case 1: cousins        1
        //                       / \
        //                      2   3
        //                     /   /
        //                    4   5
        TreeNode t1 = new TreeNode(1);
        t1.left = new TreeNode(2);
        t1.right = new TreeNode(3);
        t1.left.left = new TreeNode(4);
        t1.right.left = new TreeNode(5);
        print("case 1 BFS cousins (4,5)", solver.isCousinsBfs(t1, 4, 5), true);
        print("case 1 DFS cousins (4,5)", solver.isCousinsDfs(t1, 4, 5), true);

        // case 2: siblings       1
        //                       / \
        //                      2   3
        //                     / \
        //                    4   5
        TreeNode t2 = new TreeNode(1);
        t2.left = new TreeNode(2);
        t2.right = new TreeNode(3);
        t2.left.left = new TreeNode(4);
        t2.left.right = new TreeNode(5);
        print("case 2 BFS siblings (4,5)", solver.isCousinsBfs(t2, 4, 5), false);
        print("case 2 DFS siblings (4,5)", solver.isCousinsDfs(t2, 4, 5), false);

        // case 3: different depths, same tree as case 2, values 2 and 4
        print("case 3 BFS different depth (2,4)", solver.isCousinsBfs(t2, 2, 4), false);
        print("case 3 DFS different depth (2,4)", solver.isCousinsDfs(t2, 2, 4), false);

        // case 4: empty tree
        print("case 4 BFS empty", solver.isCousinsBfs(null, 1, 2), false);
        print("case 4 DFS empty", solver.isCousinsDfs(null, 1, 2), false);
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}
