import java.util.Stack;

class Pair {
    int val, minVal;

    Pair(int val, int minVal) {
        this.val = val;
        this.minVal = minVal;
    }
}

class MinStack {
    Stack<Pair> st;

    public MinStack() {
        st = new Stack<>();
    }

    public void push(int value) {
        int min;
        if (st.isEmpty()) {
            min = value;
        } else {
            min = Math.min(st.peek().minVal, value);
        }
        st.push(new Pair(value, min));
    }

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
        return st.peek().minVal;
    }

    public static void main(String[] args) {
        MinStack minStack = new MinStack();

        minStack.push(5);
        minStack.push(3);
        minStack.push(7);
        minStack.push(2);

        System.out.println("Top element: " + minStack.top());     // Output: 2
        System.out.println("Minimum element: " + minStack.getMin()); // Output: 2

        minStack.pop();  // remove 2
        System.out.println("Top element after pop: " + minStack.top()); // Output: 7
        System.out.println("Minimum element after pop: " + minStack.getMin()); // Output: 3

        minStack.pop();  // remove 7
        System.out.println("Top element: " + minStack.top()); // Output: 3
        System.out.println("Minimum element: " + minStack.getMin()); // Output: 3
    }
}
