import java.util.*;

// 1381. Design a Stack With Increment Operation
// https://leetcode.com/problems/design-a-stack-with-increment-operation/description/
class DesignAStackWithIncrementOperation {
    private int maxSize;
    private int[] stack;
    // next free index (size of stack)
    private int top;
    // inc[i] holds extra increment to apply for stack position i
    private int[] inc;

    public DesignAStackWithIncrementOperation(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new int[maxSize];
        this.inc = new int[maxSize];
        this.top = 0;
    }

    public void push(int x) {
        if (top < maxSize) {
            stack[top] = x;
            inc[top] = 0;
            top++;
        }
    }

    public int pop() {
        if (top == 0) {
            return -1;
        }
        top--;
        int res = stack[top] + inc[top];
        if (top > 0) {
            // propagate the increment down to the next element
            inc[top - 1] += inc[top];
        }
        inc[top] = 0;
        return res;
    }

    public void increment(int k, int val) {
        int idx = Math.min(k, top) - 1;
        if (idx >= 0) {
            inc[idx] += val;
        }
    }

    // For testing / demonstration purposes
    public static void main(String[] args) {
        DesignAStackWithIncrementOperation cs = new DesignAStackWithIncrementOperation(3);  // maxSize = 3
        cs.push(1);           // stack = [1]
        cs.push(2);           // stack = [1,2]
        System.out.println(cs.pop());     // returns 2 → prints 2
        cs.push(2);           // stack = [1,2]
        cs.push(3);           // stack = [1,2,3]
        cs.push(4);           // stack size = 3 (max), push 4 is ignored
        cs.increment(5, 100); // increment bottom 5 (but size=3) → effectively increment all 3 by 100
        cs.increment(2, 100); // increment bottom 2 → so positions 0 and 1 get +100
        System.out.println(cs.pop());     // returns 103 → prints 103 (3 + 100)
        System.out.println(cs.pop());     // returns 202 → prints 202 (2 + 100 + 100)
        System.out.println(cs.pop());     // returns 101 → prints 101 (1 + 100)
    }
}
