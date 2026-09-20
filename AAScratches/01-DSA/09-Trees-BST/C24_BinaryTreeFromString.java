/*
 * =====================================================================
 *  Construct Binary Tree from String            LeetCode 536 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Build a binary tree from a string of the form  value(leftSubtree)(rightSubtree),
 *   where each subtree is written the same way and either or both may be absent.
 *   Values can be negative. If only one pair of brackets follows a value, it is the
 *   LEFT child - a right child can never appear without a left one in the input.
 *
 * EXAMPLE
 *   "4(2(3)(1))(6(5))"  ->        4          preorder: 4 2 3 1 6 5
 *                                / \
 *                               2   6
 *                              / \  /
 *                             3   1 5
 *   "42"          ->  a single node 42
 *   "-4(2(-3))(6)"->  negative values parse the same way   preorder: -4 2 -3 6
 *   ""            ->  null
 *
 * APPROACH  (recursive descent parsing with one shared index)
 *   1. Carry the read position in an int[1] so every recursive call advances the SAME
 *      cursor - a plain int parameter would only move the local copy.
 *   2. Read an optional '-' then the digits, and make a node from that number.
 *   3. If the next character is '(', consume it, parse the LEFT child recursively,
 *      then consume the matching ')'.
 *   4. If another '(' follows, do the same for the RIGHT child.
 *   5. Return the node. A ')' where a value was expected means an empty pair, so
 *      return null rather than manufacturing a phantom node with value 0.
 *
 * KEY INSIGHT
 *   The grammar is self-similar, so the parser is just the grammar rule written as a
 *   method: read a token, then recurse for each bracketed group. The only state that
 *   must be shared across the whole parse is the cursor, and int[] (or a field) is how
 *   you share it in Java. The same shared-cursor device reappears in deserialize().
 *
 * COMPLEXITY
 *   Time  O(n)  the cursor only moves forward, so each character is read once.
 *   Space O(h)  recursion depth equals the tree height, O(n) for a degenerate chain.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Write the inverse: tree2str (LC 606), and explain when "()" must be kept.
 *   - Rewrite the parser iteratively with an explicit stack - where does the ')' pop?
 *   - How would you report a malformed string instead of silently building a wrong tree?
 *   - How does this compare with serialize/deserialize using null markers (LC 297)?
 *
 * RUN
 *   main() parses four strings (typical, single node, negatives, empty) and prints the
 *   preorder of each parsed tree against the expected list.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class BinaryTreeFromString {

    public static TreeNode str2tree(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return buildTree(s, new int[]{0}); // int[] = a cursor every call can advance
    }

    private static TreeNode buildTree(String s, int[] index) {
        if (index[0] >= s.length() || s.charAt(index[0]) == ')') {
            return null; // nothing here, or an empty "()" pair
        }

        int sign = 1;
        if (s.charAt(index[0]) == '-') {
            sign = -1;
            index[0]++;
        }
        int num = 0;
        while (index[0] < s.length() && Character.isDigit(s.charAt(index[0]))) {
            num = num * 10 + (s.charAt(index[0]) - '0');
            index[0]++;
        }

        TreeNode node = new TreeNode(num * sign);

        // The first bracketed group after the value is always the LEFT child.
        if (index[0] < s.length() && s.charAt(index[0]) == '(') {
            index[0]++;                        // skip '('
            node.left = buildTree(s, index);
            index[0]++;                        // skip the matching ')'
        }

        // A second bracketed group is the RIGHT child.
        if (index[0] < s.length() && s.charAt(index[0]) == '(') {
            index[0]++;
            node.right = buildTree(s, index);
            index[0]++;
        }

        return node;
    }

    // ---------- test helpers ----------

    private static List<Integer> preorderOf(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        collectPreorder(root, out);
        return out;
    }

    private static void collectPreorder(TreeNode node, List<Integer> out) {
        if (node == null) {
            return;
        }
        out.add(node.val);
        collectPreorder(node.left, out);
        collectPreorder(node.right, out);
    }

    private static void check(String label, String input, List<Integer> expectedPreorder) {
        List<Integer> actual = preorderOf(str2tree(input));
        System.out.println(label + " \"" + input + "\" -> " + actual
                + "   expected " + expectedPreorder);
    }

    public static void main(String[] args) {
        check("case 1 typical    ", "4(2(3)(1))(6(5))", Arrays.asList(4, 2, 3, 1, 6, 5));
        check("case 2 single node", "42", Arrays.asList(42));
        check("case 3 negatives  ", "-4(2(-3))(6)", Arrays.asList(-4, 2, -3, 6));
        check("case 4 empty      ", "", Collections.<Integer>emptyList());
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}
