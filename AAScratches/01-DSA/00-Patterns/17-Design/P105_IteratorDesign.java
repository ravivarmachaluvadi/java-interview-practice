/*
 * =====================================================================
 *  P105 Design: Lazy Iterators   Canonical LC 341 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 341, Flatten Nested List Iterator)
 *   Each element is an integer or a list whose elements are integers or lists. Implement
 *   next() and hasNext() that walk all integers in order. (LeetCode's element type is
 *   called NestedInteger; here it is NestedEntry.)
 *
 * EXAMPLE
 *   [[1,1],2,[1,1]]  ->  1, 1, 2, 1, 1
 *   [1,[4,[6]]]      ->  1, 4, 6
 *   [[]]             ->  (nothing)
 *
 * RECOGNIZE WHEN
 *   - "implement an iterator" over a structure: nested lists, a BST in order, a 2D vector,
 *     zigzag over several lists, run-length data.
 *   - next() / hasNext() should be cheap and memory should not hold the whole flattened
 *     result.
 *   Not this if: you are asked for the whole result at once -> a plain traversal is simpler
 *   (P051_IterativeTraversals for trees).
 *
 * TEMPLATE
 *   keep the traversal's state explicitly (a stack of positions / nodes)
 *   hasNext(): advance the state until the top is a real element (or nothing is left);
 *              do the work HERE, so hasNext() can be called any number of times
 *   next():    hasNext(); then pop / return the element at the top
 *   peeking:   cache one element ahead ("next" buffered); peek returns the cache
 *
 * APPROACH
 *   1. A stack of list iterators replaces the recursion of a depth-first walk.
 *   2. hasNext() opens lists until the next item is an integer (or every list is done).
 *   3. next() returns that integer.
 *
 * KEY INSIGHT
 *   An iterator is a recursive traversal paused between steps, so its state must live in
 *   fields: the explicit stack IS the paused call stack. Doing the "find the next element"
 *   work in hasNext() keeps empty lists and repeated hasNext() calls correct.
 *
 * COMPLEXITY
 *   O(1) amortised per next(); space O(depth) for nested lists, O(h) for a BST.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 284  Peeking Iterator         buffer one element ahead
 *   [coded] LC 173  BST Iterator             stack of the left spine; pop, then push the
 *                                            left spine of the right child
 *           LC 251  Flatten 2D Vector        (row, col) pointers; skip empty rows in hasNext
 *           LC 281  Zigzag Iterator          queue of iterators; rotate after each next
 *           LC 900  RLE Iterator             consume counts lazily
 *           LC 1586 BST Iterator II          keep a list of visited values for prev()
 *
 * PITFALLS
 *   - [[]] and [[], [3]]: empty lists must be skipped in hasNext(), not next().
 *   - Calling hasNext() twice must not consume anything.
 *   - Do not flatten everything in the constructor unless the interviewer allows O(n) memory.
 *
 * DEEP DIVE
 *   B01_NestedListWeightSum (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

class IteratorDesign {

    // LeetCode's NestedInteger: either one integer or a list of entries.
    static class NestedEntry {
        Integer value;
        List<NestedEntry> list;

        // Builds from plain Java values: an Integer, or a List of such values.
        static NestedEntry of(Object o) {
            NestedEntry e = new NestedEntry();
            if (o instanceof Integer i) {
                e.value = i;
            } else {
                e.list = new ArrayList<>();
                for (Object child : (List<?>) o) {
                    e.list.add(of(child));
                }
            }
            return e;
        }

        static List<NestedEntry> listOf(List<?> items) {
            return of(items).list;
        }
    }

    // Canonical LC 341.
    static class NestedIterator implements Iterator<Integer> {
        private final Deque<Iterator<NestedEntry>> stack = new ArrayDeque<>();
        private Integer nextValue;

        NestedIterator(List<NestedEntry> nestedList) {
            stack.push(nestedList.iterator());
        }

        @Override
        public boolean hasNext() {
            while (nextValue == null && !stack.isEmpty()) {
                if (!stack.peek().hasNext()) {
                    stack.pop();                   // this list is finished
                    continue;
                }
                NestedEntry e = stack.peek().next();
                if (e.value != null) {
                    nextValue = e.value;
                } else {
                    stack.push(e.list.iterator()); // descend into the inner list
                }
            }
            return nextValue != null;
        }

        @Override
        public Integer next() {
            hasNext();
            Integer v = nextValue;
            nextValue = null;
            return v;
        }
    }

    // LC 284.
    static class PeekingIterator implements Iterator<Integer> {
        private final Iterator<Integer> it;
        private Integer buffered;

        PeekingIterator(Iterator<Integer> iterator) {
            it = iterator;
            buffered = it.hasNext() ? it.next() : null;
        }

        Integer peek() {
            return buffered;
        }

        @Override
        public Integer next() {
            Integer v = buffered;
            buffered = it.hasNext() ? it.next() : null;
            return v;
        }

        @Override
        public boolean hasNext() {
            return buffered != null;
        }
    }

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        // Builds a tree from LeetCode's level-order list (null = missing child).
        static TreeNode of(Integer... v) {
            if (v.length == 0 || v[0] == null) {
                return null;
            }
            TreeNode root = new TreeNode(v[0]);
            Deque<TreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            int i = 1;
            while (i < v.length) {
                TreeNode n = queue.poll();
                if (v[i] != null) {
                    n.left = new TreeNode(v[i]);
                    queue.add(n.left);
                }
                i++;
                if (i < v.length && v[i] != null) {
                    n.right = new TreeNode(v[i]);
                    queue.add(n.right);
                }
                i++;
            }
            return root;
        }
    }

    // LC 173: in-order iterator over a BST.
    static class BSTIterator {
        private final Deque<TreeNode> stack = new ArrayDeque<>();

        BSTIterator(TreeNode root) {
            pushLeft(root);
        }

        int next() {
            TreeNode n = stack.pop();
            pushLeft(n.right);
            return n.val;
        }

        boolean hasNext() {
            return !stack.isEmpty();
        }

        private void pushLeft(TreeNode n) {
            while (n != null) {
                stack.push(n);
                n = n.left;
            }
        }
    }

    static List<Integer> drain(Iterator<Integer> it) {
        List<Integer> out = new ArrayList<>();
        while (it.hasNext()) {
            out.add(it.next());
        }
        return out;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        List<Object> a = List.of(List.of(1, 1), 2, List.of(1, 1));
        check("LC 341 [[1,1],2,[1,1]]",
                drain(new NestedIterator(NestedEntry.listOf(a))), "[1, 1, 2, 1, 1]");
        List<Object> b = List.of(1, List.of(4, List.of(6)));
        check("LC 341 [1,[4,[6]]]", drain(new NestedIterator(NestedEntry.listOf(b))), "[1, 4, 6]");
        List<Object> c = List.of(List.of(), List.of(List.of()), 3);
        check("LC 341 empty lists skipped",
                drain(new NestedIterator(NestedEntry.listOf(c))), "[3]");
        NestedIterator twice = new NestedIterator(NestedEntry.listOf(List.of(List.of(), 7)));
        check("LC 341 hasNext twice is safe",
                twice.hasNext() && twice.hasNext() && twice.next() == 7, true);

        PeekingIterator pk = new PeekingIterator(List.of(1, 2, 3).iterator());
        String trace = pk.next() + "," + pk.peek() + "," + pk.next() + ","
                + pk.next() + "," + pk.hasNext();
        check("LC 284 next peek next next hasNext", trace, "1,2,2,3,false");

        BSTIterator bst = new BSTIterator(TreeNode.of(7, 3, 15, null, null, 9, 20));
        StringBuilder sb = new StringBuilder();
        while (bst.hasNext()) {
            sb.append(bst.next()).append(' ');
        }
        check("LC 173 in order", sb.toString().trim(), "3 7 9 15 20");
        check("LC 173 empty tree", new BSTIterator(null).hasNext(), false);
    }
}
