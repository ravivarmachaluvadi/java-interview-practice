/*
 * =====================================================================
 *  Nested List Weight Sum                          LeetCode 339 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are given a nested list. Each element is either a single integer or
 *   another nested list. The weight of an integer is its depth: elements at the
 *   outermost level have depth 1, elements inside one list have depth 2, and so
 *   on. Return the sum of every integer multiplied by its own depth.
 *
 * EXAMPLE
 *   [[1,1],2,[1,1]]  ->  10    because 1*2 + 1*2 + 2*1 + 1*2 + 1*2 = 10
 *   [1,[4,[6]]]      ->  27    because 1*1 + 4*2 + 6*3 = 27
 *   []               ->  0     nothing to add
 *
 * APPROACH  (recursion carrying a depth parameter)
 *   1. Walk the list with a helper that also receives the current depth.
 *   2. If the element is an integer, add value * depth to the running sum.
 *   3. If the element is a list, recurse into it with depth + 1.
 *   4. Return the sum for this level; the caller adds it to its own sum.
 *   A second method, depthSumBfs, does the same level by level with a queue --
 *   the interviewer's usual "can you do it iteratively?" follow-up.
 *
 * KEY INSIGHT
 *   This is the gentlest form of recursion: state flows only downward, so there
 *   is nothing to undo. Depth is not stored anywhere in the data -- it is simply
 *   the recursion level, so passing it as a parameter is free. Recognise this
 *   whenever a problem says "weight by how deeply nested it is".
 *
 * COMPLEXITY
 *   Time  O(n)  every integer and every list node is visited exactly once
 *   Space O(d)  recursion stack, where d is the maximum nesting depth
 *
 * INTERVIEW FOLLOW-UPS
 *   - Nested List Weight Sum II (LC 364): weight by inverse depth, deepest = 1.
 *   - Do it iteratively without recursion (see depthSumBfs below).
 *   - What if nesting can be thousands deep? Recursion risks StackOverflowError.
 *   - Flatten Nested List Iterator (LC 341): expose the same data as an Iterator.
 *
 * RUN
 *   main() runs 3 cases (typical, deep chain, empty) through both the recursive
 *   and the BFS method and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Interface representing NestedInteger. LeetCode supplies this; here we supply
 * a small implementation below so the file runs standalone.
 */
interface NestedInteger {
    boolean isInteger();

    Integer getInteger();

    List<NestedInteger> getList();
}

class NestedListWeightSum {

    // ---------- approach 1: recursion with a depth parameter ----------

    public static int depthSum(List<NestedInteger> nestedList) {
        return sumAtDepth(nestedList, 1);
    }

    private static int sumAtDepth(List<NestedInteger> list, int depth) {
        int sum = 0;
        for (NestedInteger item : list) {
            if (item.isInteger()) {
                sum += item.getInteger() * depth;
            } else {
                // one level deeper, so every integer inside is worth depth + 1
                sum += sumAtDepth(item.getList(), depth + 1);
            }
        }
        return sum;
    }

    // ---------- approach 2: same answer, level-order with a queue ----------

    public static int depthSumBfs(List<NestedInteger> nestedList) {
        int sum = 0;
        int depth = 1;
        Deque<NestedInteger> queue = new ArrayDeque<>(nestedList);
        while (!queue.isEmpty()) {
            // drain exactly the nodes of the current depth before going deeper
            for (int remaining = queue.size(); remaining > 0; remaining--) {
                NestedInteger item = queue.poll();
                if (item.isInteger()) {
                    sum += item.getInteger() * depth;
                } else {
                    queue.addAll(item.getList());
                }
            }
            depth++;
        }
        return sum;
    }

    // ---------- tiny NestedInteger implementation + builders ----------

    /** A leaf integer. */
    private static NestedInteger leaf(int value) {
        return new SimpleNestedInteger(value);
    }

    /** A nested list built from the given children. */
    private static NestedInteger nest(NestedInteger... children) {
        return new SimpleNestedInteger(new ArrayList<>(Arrays.asList(children)));
    }

    private static List<NestedInteger> listOf(NestedInteger... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    private static void print(String label, List<NestedInteger> input, int expected) {
        System.out.println(label + " recursive=" + depthSum(input)
                + "  bfs=" + depthSumBfs(input)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: [[1,1],2,[1,1]]
        print("case 1 [[1,1],2,[1,1]]:",
                listOf(nest(leaf(1), leaf(1)), leaf(2), nest(leaf(1), leaf(1))), 10);

        // case 2: [1,[4,[6]]] -- a deep chain, each level weighs more
        print("case 2 [1,[4,[6]]]:    ",
                listOf(leaf(1), nest(leaf(4), nest(leaf(6)))), 27);

        // case 3: edge -- empty input contributes nothing
        print("case 3 []:             ", listOf(), 0);
    }
}

class SimpleNestedInteger implements NestedInteger {
    private final Integer value;
    private final List<NestedInteger> list;

    SimpleNestedInteger(Integer value) {
        this.value = value;
        this.list = null;
    }

    SimpleNestedInteger(List<NestedInteger> list) {
        this.value = null;
        this.list = list;
    }

    @Override
    public boolean isInteger() {
        return value != null;
    }

    @Override
    public Integer getInteger() {
        return value;
    }

    @Override
    public List<NestedInteger> getList() {
        return list;
    }
}
