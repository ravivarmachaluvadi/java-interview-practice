/*
 * =====================================================================
 *  Design a Stack With Increment Operation        LeetCode 1381 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Build a stack with a fixed maxSize supporting push (ignored when full), pop
 *   (returns -1 when empty) and increment(k, val), which adds val to the bottom k
 *   elements - or to every element if the stack holds fewer than k.
 *   The goal is O(1) for all three, so increment must not touch k cells.
 *
 * EXAMPLE
 *   maxSize = 3: push 1, push 2 -> pop() = 2
 *   push 2, push 3, push 4 (ignored, stack is full: [1, 2, 3])
 *   increment(5, 100) -> all 3 get +100 ; increment(2, 100) -> bottom 2 get +100
 *   pops are then 103, 202, 201
 *
 * DESIGN  (lazy increment propagation)
 *   int[] stack   raw pushed values, never rewritten by increment.
 *   int[] inc     inc[i] is a debt owed to positions 0..i. increment(k, val) writes
 *                 ONE cell: inc[min(k, top) - 1] += val.
 *   int top       next free slot, so top is also the current size.
 *   pop           real value = stack[top-1] + inc[top-1]; before returning, push the
 *                 debt one step down (inc[top-2] += inc[top-1]) so the element that
 *                 becomes the new peek still owes what it owed, then clear the slot.
 *   The true value of position i is stack[i] + sum(inc[i..top-1]); the propagation on
 *   pop is what keeps that sum collapsed into a single cell at the top.
 *
 * KEY DECISIONS
 *   - Why not just loop over the bottom k cells? That is O(k) per increment and the
 *     whole point of the problem is to make it O(1). Deferring the work is the answer.
 *   - Why store the debt at the TOP of the affected range rather than the bottom?
 *     Because pop always removes from the top, so the top is the only place the stack
 *     is ever read - parking the debt there means exactly one element ever reads it.
 *   - Fixed arrays rather than an ArrayList: maxSize is given, and the parallel array
 *     inc has to be index-aligned with stack anyway.
 *   - Fixed: the author's trailing comment claimed the last pop prints 101. It is 201
 *     (1 + 100 from increment(5,100) + 100 from increment(2,100)). The code was right,
 *     the comment was wrong; main() now asserts 201.
 *
 * COMPLEXITY
 *   Time  O(1) for push, pop and increment - each touches a constant number of cells
 *   Space O(maxSize) for the two parallel arrays
 *
 * INTERVIEW FOLLOW-UPS
 *   - Increment the TOP k instead of the bottom k: park the debt at inc[top - k].
 *   - Support getMin as well: keep MinStack's per-entry running minimum alongside.
 *   - Unbounded stack: same idea over ArrayLists, amortised O(1).
 *   - Where else does this trick show up? Difference arrays and lazy segment trees are
 *     the same "record the delta once, settle it on read" idea.
 *
 * RUN
 *   main() runs 3 cases: the LeetCode example, an empty-stack case (pop returns -1 and
 *   increment is a no-op), and an overflow case where push past maxSize is ignored.
 *   Every line prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class DesignAStackWithIncrementOperation {

    private final int maxSize;
    private final int[] stack;

    /** inc[i] is an unpaid increment owed to every position 0..i. */
    private final int[] inc;

    /** Next free index, which is also the current size. */
    private int top;

    public DesignAStackWithIncrementOperation(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new int[maxSize];
        this.inc = new int[maxSize];
        this.top = 0;
    }

    /** Pushes x; silently ignored when the stack is already at maxSize. */
    public void push(int x) {
        if (top < maxSize) {
            stack[top] = x;
            inc[top] = 0;
            top++;
        }
    }

    /** Pops and returns the top value with its debt settled, or -1 if empty. */
    public int pop() {
        if (top == 0) {
            return -1;
        }
        top--;
        int result = stack[top] + inc[top];
        if (top > 0) {
            // Hand the debt down: the new top owes whatever this element owed.
            inc[top - 1] += inc[top];
        }
        inc[top] = 0; // slot is free again
        return result;
    }

    /** Adds val to the bottom min(k, size) elements in O(1). */
    public void increment(int k, int val) {
        int highestAffected = Math.min(k, top) - 1;
        if (highestAffected >= 0) {
            inc[highestAffected] += val;
        }
    }

    /**
     * Bottom-to-top view of the real values, for the demo only.
     * Value at i is stack[i] plus every debt parked at or above i, so walk downwards
     * accumulating. Read-only: nothing here mutates inc.
     */
    public List<Integer> contents() {
        int[] real = new int[top];
        int owed = 0;
        for (int i = top - 1; i >= 0; i--) {
            owed += inc[i];
            real[i] = stack[i] + owed;
        }
        List<Integer> out = new ArrayList<>();
        for (int v : real) {
            out.add(v);
        }
        return out;
    }

    public static void main(String[] args) {
        // ---- case 1: the LeetCode example ---------------------------------------
        DesignAStackWithIncrementOperation cs = new DesignAStackWithIncrementOperation(3);
        cs.push(1);
        cs.push(2);
        print("case 1 pop", cs.pop(), 2);
        cs.push(2);
        cs.push(3);
        cs.push(4);                       // ignored, stack is full
        print("case 1 after full push", cs.contents(), "[1, 2, 3]");
        cs.increment(5, 100);             // k > size, so all 3 elements
        cs.increment(2, 100);             // bottom 2 elements
        print("case 1 after increments", cs.contents(), "[201, 202, 103]");
        print("case 1 pop", cs.pop(), 103);
        print("case 1 pop", cs.pop(), 202);
        print("case 1 pop", cs.pop(), 201);

        // ---- case 2: edge - empty stack -----------------------------------------
        DesignAStackWithIncrementOperation empty = new DesignAStackWithIncrementOperation(2);
        print("case 2 pop on empty", empty.pop(), -1);
        empty.increment(3, 50);           // no-op: nothing to owe the debt to
        empty.push(7);                    // must NOT inherit the earlier increment
        print("case 2 pop after no-op increment", empty.pop(), 7);

        // ---- case 3: tricky - debt survives an ignored push ----------------------
        DesignAStackWithIncrementOperation full = new DesignAStackWithIncrementOperation(2);
        full.push(10);
        full.push(20);
        full.push(30);                    // ignored: maxSize is 2
        full.increment(1, 5);             // bottom element only
        print("case 3 contents", full.contents(), "[15, 20]");
        print("case 3 pop", full.pop(), 20);
        print("case 3 pop", full.pop(), 15);
        print("case 3 pop on drained stack", full.pop(), -1);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
