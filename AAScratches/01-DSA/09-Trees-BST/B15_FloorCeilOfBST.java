/*
 * =====================================================================
 *  Floor and Ceil in a BST                                   GFG | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a Binary Search Tree and an integer key, return two values:
 *   floor = the largest value in the tree that is <= key, and
 *   ceil  = the smallest value in the tree that is >= key.
 *   If no such value exists, return -1 for that slot (tree values are non-negative).
 *
 * EXAMPLE
 *   Tree: 8 (4 (2, 6), 12 (10, 14))
 *   key = 11    ->  [10, 12]   10 is the largest <= 11, 12 the smallest >= 11
 *   key = 6     ->  [6, 6]     exact hit: floor and ceil are the key itself
 *   key = 1     ->  [-1, 2]    nothing is <= 1
 *   key = 20    ->  [14, -1]   nothing is >= 20
 *   root = null ->  [-1, -1]
 *
 * APPROACH  (descent that remembers the best candidate so far)
 *   Floor pass:
 *   1. Start at the root with floor = -1.
 *   2. If node.data == key, that is the floor; stop.
 *   3. If node.data < key, the node is a valid floor: record it and go RIGHT to look for
 *      a bigger value that is still <= key.
 *   4. If node.data > key, the node is too big: go LEFT without recording anything.
 *   Ceil pass: the mirror image. Record when node.data > key and go LEFT; else go RIGHT.
 *
 * KEY INSIGHT
 *   You never visit the whole tree. At each node one comparison tells you whether the
 *   node is a candidate and which side could hold a better one. "Remember the best so
 *   far, then keep descending" is the same skeleton as in-order predecessor/successor
 *   without parent pointers, and as finding the insert position for a key.
 *
 * COMPLEXITY
 *   Time  O(h)   two root-to-leaf walks, h = tree height (log n if balanced)
 *   Space O(1)   iterative, no recursion, no extra structures
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in one pass: every turn right records a floor, every turn left records a
 *     ceil (see floorCeilOnePass below).
 *   - Return nodes instead of values so -1 is not overloaded as "not found".
 *   - In-order predecessor / successor of a given node: same descent, key = node value.
 *   - Why not in-order traversal? Correct but O(n); the descent is O(h).
 *
 * RUN
 *   main() runs 5 cases (typical, exact hit, below min, above max, empty tree) through
 *   both methods and prints actual vs expected.
 */
import java.util.Arrays;
import java.util.List;

class TreeNode {
    int data;
    TreeNode left, right;

    TreeNode(int val) {
        data = val;
    }
}

class FloorCeilOfBST {

    /** Author's approach: one descent finds the floor, a second descent finds the ceil. */
    public List<Integer> floorCeilOfBST(TreeNode root, int key) {
        int floor = -1;
        int ceil = -1;

        TreeNode current = root;
        while (current != null) {
            if (current.data == key) {
                floor = current.data;
                break;
            } else if (current.data < key) {
                floor = current.data;      // candidate: record it, then look right for a bigger one
                current = current.right;
            } else {
                current = current.left;    // too big to be a floor, record nothing
            }
        }

        current = root;                    // reset to the root before the second descent
        while (current != null) {
            if (current.data == key) {
                ceil = current.data;
                break;
            } else if (current.data > key) {
                ceil = current.data;       // candidate: record it, then look left for a smaller one
                current = current.left;
            } else {
                current = current.right;   // too small to be a ceil, record nothing
            }
        }
        return Arrays.asList(floor, ceil);
    }

    /**
     * Alternative: a single descent. The search path for key passes through both the floor
     * (last node where we turned right) and the ceil (last node where we turned left).
     */
    public List<Integer> floorCeilOnePass(TreeNode root, int key) {
        int floor = -1;
        int ceil = -1;

        TreeNode current = root;
        while (current != null) {
            if (current.data == key) {
                return Arrays.asList(key, key);
            }
            if (current.data < key) {
                floor = current.data;
                current = current.right;
            } else {
                ceil = current.data;
                current = current.left;
            }
        }
        return Arrays.asList(floor, ceil);
    }

    public static void main(String[] args) {
        /*
                  8
                /   \
               4     12
              / \   /  \
             2   6 10   14
        */
        TreeNode root = new TreeNode(8);
        root.left = new TreeNode(4);
        root.right = new TreeNode(12);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(6);
        root.right.left = new TreeNode(10);
        root.right.right = new TreeNode(14);

        check("case 1 key=11 (typical)  ", root, 11, "[10, 12]");
        check("case 2 key=6  (exact hit)", root, 6, "[6, 6]");
        check("case 3 key=1  (below min)", root, 1, "[-1, 2]");
        check("case 4 key=20 (above max)", root, 20, "[14, -1]");
        check("case 5 empty tree        ", null, 5, "[-1, -1]");
    }

    private static void check(String label, TreeNode root, int key, String expected) {
        FloorCeilOfBST sol = new FloorCeilOfBST();
        System.out.println(label + ": twoPass=" + sol.floorCeilOfBST(root, key)
                + " onePass=" + sol.floorCeilOnePass(root, key) + "   expected " + expected);
    }
}
