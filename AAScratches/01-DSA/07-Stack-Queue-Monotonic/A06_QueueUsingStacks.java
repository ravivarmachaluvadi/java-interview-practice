/**
 * Implements a FIFO queue using two LIFO stacks.
 *
 * Problem solved:
 *   Provides enqueue, dequeue, peek, isEmpty, and size operations for a queue
 *   while only utilizing stack data structures.
 *
 * Approach:
 *   Elements are pushed onto stack1 during enqueue. For dequeue or peek,
 *   if stack2 is empty, all elements from stack1 are transferred to stack2,
 *   reversing their order so the oldest element is on top of stack2.
 *   Then pop/peek from stack2. This ensures FIFO behavior with amortized O(1)
 *   operations.
 *
 * Time Complexity:
 *   enqueue: O(1) average
 *   dequeue, peek: O(1) amortized (O(n) worst-case when transferring)
 *   isEmpty, size: O(1)
 *
 * Space Complexity:
 *   O(n), where n is the number of elements in the queue.
 */
import java.util.Stack;

class QueueUsingStacks {
    private final Stack<Integer> stack1;
    private final Stack<Integer> stack2;

    public QueueUsingStacks() {
        stack1 = new Stack<>();
        stack2 = new Stack<>();
    }

    public void enqueue(int value) {
        stack1.push(value);
    }

    public int dequeue() {
        if (stack2.isEmpty()) {
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }
        }
        if (stack2.isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        return stack2.pop();
    }

    public int peek() {
        if (stack2.isEmpty()) {
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }
        }
        if (stack2.isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        return stack2.peek();
    }

    public boolean isEmpty() {
        return stack1.isEmpty() && stack2.isEmpty();
    }

    public int size() {
        return stack1.size() + stack2.size();
    }

    public static void main(String[] args) {
        QueueUsingStacks queue = new QueueUsingStacks();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        System.out.println("Dequeued: " + queue.dequeue());
        System.out.println("Front element: " + queue.peek());
        System.out.println("Queue size: " + queue.size());
        System.out.println("Dequeued: " + queue.dequeue());
        System.out.println("Dequeued: " + queue.dequeue());

        System.out.println("Is queue empty? " + queue.isEmpty());
    }

}
