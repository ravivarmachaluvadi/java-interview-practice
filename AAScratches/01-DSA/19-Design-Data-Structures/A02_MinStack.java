/*
 * =====================================================================
 *  Min Stack                           LeetCode 155 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a stack supporting push, pop, top and getMin, where getMin returns the
 *   smallest value currently on the stack. Every one of the four operations must be
 *   O(1) - so getMin is not allowed to scan the stack.
 *
 * EXAMPLE
 *   push 5, push 3, push 7, push 2  ->  top = 2,  getMin = 2
 *   pop (drops 2)                   ->  top = 7,  getMin = 3
 *   pop (drops 7)                   ->  top = 3,  getMin = 3
 *   push 3, push 3, pop             ->  getMin = 3  (duplicate mins must survive one pop)
 *
 * APPROACH  (augment each entry with a running aggregate)
 *   1. Store a Pair(val, minVal) per entry instead of a bare int.
 *   2. On push, minVal = min(value, minVal of the entry below), or value if empty.
 *   3. pop just removes the top pair - the minimum of the shorter stack is already
 *      recorded in the new top pair, so nothing has to be recomputed.
 *   4. top returns peek().val; getMin returns peek().minVal.
 *
 * KEY INSIGHT
 *   The minimum "so far" is a prefix property of the stack, and a stack only ever
 *   grows or shrinks at one end - so caching the answer per entry is enough, and
 *   popping automatically restores the previous answer. Recognise this whenever a
 *   query must become O(1) on a structure that changes at one end only: store the
 *   aggregate alongside each element rather than recomputing it.
 *
 * COMPLEXITY
 *   Time  O(1)  push, pop, top, getMin - all are single stack operations
 *   Space O(n)  one extra int per element for the running minimum
 *
 * INTERVIEW FOLLOW-UPS
 *   - Halve the memory: a second stack holding a min only when a new min is pushed.
 *   - Single-stack O(1) extra space with encoded deltas (2*val - min) - overflow risk.
 *   - Make it a Min Queue -> two stacks, or a monotonic deque (LC 239 sliding window).
 *   - getMax as well? Store both aggregates per entry; the pattern does not change.
 *
 * RUN
 *   main() runs 3 cases (typical sequence, duplicate minimums, empty stack) and
 *   prints actual vs expected on each line.
 */

import java.util.Stack;

class Pair {
    final int val;
    final int minVal; // smallest value in the stack up to and including this entry

    Pair(int val, int minVal) {
        this.val = val;
        this.minVal = minVal;
    }
}

class MinStack {
    private final Stack<Pair> st;

    public MinStack() {
        st = new Stack<>();
    }

    public void push(int value) {
        // The new minimum is the smaller of this value and the minimum below it.
        int min = st.isEmpty() ? value : Math.min(st.peek().minVal, value);
        st.push(new Pair(value, min));
    }

    /** Popping an empty stack is a no-op here, matching the LeetCode contract. */
    public void pop() {
        if (!st.isEmpty()) {
            st.pop();
        }
    }

    public int top() {
        if (st.isEmpty()) throw new RuntimeException("Stack is empty");
        return st.peek().val;
    }

    public int getMin() {
        if (st.isEmpty()) throw new RuntimeException("Stack is empty");
        return st.peek().minVal; // already computed at push time
    }

    public boolean isEmpty() {
        return st.isEmpty();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): the minimum moves down as the smaller values are popped off.
        MinStack stack = new MinStack();
        stack.push(5);
        stack.push(3);
        stack.push(7);
        stack.push(2);
        print("case 1a: top", stack.top(), 2);
        print("case 1b: getMin", stack.getMin(), 2);

        stack.pop(); // removes 2
        print("case 1c: top after pop", stack.top(), 7);
        print("case 1d: getMin after pop", stack.getMin(), 3);

        stack.pop(); // removes 7
        print("case 1e: top", stack.top(), 3);
        print("case 1f: getMin", stack.getMin(), 3);

        // Case 2 (tricky): duplicate minimums - popping one copy must not lose the min.
        MinStack dupes = new MinStack();
        dupes.push(3);
        dupes.push(3);
        dupes.push(9);
        dupes.pop(); // removes 9
        print("case 2a: getMin with two 3s left", dupes.getMin(), 3);
        dupes.pop(); // removes one 3
        print("case 2b: getMin with one 3 left", dupes.getMin(), 3);

        // Case 3 (edge): empty stack - pop is a no-op, getMin throws.
        MinStack empty = new MinStack();
        empty.pop(); // no-op, must not throw
        String result;
        try {
            empty.getMin();
            result = "no exception";
        } catch (RuntimeException e) {
            result = e.getMessage();
        }
        print("case 3a: pop on empty then isEmpty", empty.isEmpty(), true);
        print("case 3b: getMin on empty", result, "Stack is empty");
    }
}
