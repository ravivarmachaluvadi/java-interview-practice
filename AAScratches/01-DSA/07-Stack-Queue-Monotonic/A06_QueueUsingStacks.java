/*
 * =====================================================================
 *  Implement Queue using Stacks                   LeetCode 232 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Build a FIFO queue (enqueue, dequeue, peek, isEmpty, size) using only stack operations
 *   (push, pop, peek, isEmpty, size). Every operation should be amortised O(1).
 *
 * EXAMPLE
 *   enqueue 1, 2, 3; dequeue -> 1; peek -> 2; size -> 2
 *   enqueue 1, 2; dequeue -> 1; enqueue 3; dequeue -> 2; dequeue -> 3; isEmpty -> true
 *   dequeue on an empty queue -> throws NoSuchElementException
 *
 * APPROACH  (Two-stack FIFO, lazy transfer)
 *   1. inbox: enqueue always pushes here. Newest element on top.
 *   2. outbox: dequeue and peek always read from here. Oldest element on top.
 *   3. When outbox is empty and a read is requested, pour ALL of inbox into outbox
 *      (pop from inbox, push to outbox). Popping reverses the order, so the element that
 *      went into inbox first ends up on top of outbox.
 *   4. Never transfer while outbox still has elements: they are older than anything in inbox,
 *      so they must come out first. This is what keeps the ordering correct after interleaving.
 *
 * KEY INSIGHT
 *   Two reversals make a forward order: a stack reverses on the way in, a second stack
 *   reverses again on the way out. The amortised argument, which you must say aloud: each
 *   element is pushed to inbox once, moved to outbox once, popped from outbox once, so a
 *   sequence of n operations does at most 3n stack pushes/pops. A single dequeue can cost
 *   O(n), but it "pays" for the elements it moved, which are then O(1) to dequeue.
 *
 * COMPLEXITY
 *   Time  enqueue O(1); dequeue and peek O(1) amortised, O(n) worst single call; isEmpty,
 *         size O(1)
 *   Space O(n)  every element lives in exactly one of the two stacks
 *
 * INTERVIEW FOLLOW-UPS
 *   - Implement a Stack using Queues (LeetCode 225): rotate the queue after each push.
 *   - Why not transfer back to inbox after every dequeue? That makes dequeue O(n) always.
 *   - Thread safety: two stacks change under one operation, so lock the whole operation.
 *   - Sliding-window maximum uses the same "two ends" thinking with a deque.
 *
 * RUN
 *   main() runs 3 cases (basic FIFO order, interleaved enqueue/dequeue, dequeue on empty)
 *   and prints actual vs expected.
 */
import java.util.NoSuchElementException;
import java.util.Stack;

class QueueUsingStacks {
    private final Stack<Integer> inbox = new Stack<>();   // enqueue pushes here
    private final Stack<Integer> outbox = new Stack<>();  // dequeue and peek read from here

    public void enqueue(int value) {
        inbox.push(value);
    }

    public int dequeue() {
        refillOutboxIfEmpty();
        return outbox.pop();
    }

    public int peek() {
        refillOutboxIfEmpty();
        return outbox.peek();
    }

    public boolean isEmpty() {
        return inbox.isEmpty() && outbox.isEmpty();
    }

    public int size() {
        return inbox.size() + outbox.size();
    }

    // Only when outbox is empty: pouring inbox reverses it, so the oldest element lands on top.
    private void refillOutboxIfEmpty() {
        if (outbox.isEmpty()) {
            while (!inbox.isEmpty()) {
                outbox.push(inbox.pop());
            }
        }
        if (outbox.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: basic FIFO order
        QueueUsingStacks q1 = new QueueUsingStacks();
        q1.enqueue(1);
        q1.enqueue(2);
        q1.enqueue(3);
        print("case 1 dequeue", q1.dequeue(), 1);
        print("case 1 peek   ", q1.peek(), 2);
        print("case 1 size   ", q1.size(), 2);

        // case 2: interleaved operations. 3 arrives while outbox still holds 2; 2 must come first.
        QueueUsingStacks q2 = new QueueUsingStacks();
        q2.enqueue(1);
        q2.enqueue(2);
        int first = q2.dequeue();
        q2.enqueue(3);
        int second = q2.dequeue();
        int third = q2.dequeue();
        print("case 2 order  ", first + "," + second + "," + third, "1,2,3");
        print("case 2 isEmpty", q2.isEmpty(), true);

        // case 3: dequeue on an empty queue throws
        QueueUsingStacks q3 = new QueueUsingStacks();
        String outcome;
        try {
            q3.dequeue();
            outcome = "returned";
        } catch (NoSuchElementException e) {
            outcome = "threw NoSuchElementException";
        }
        print("case 3 empty  ", outcome, "threw NoSuchElementException");
    }
}
