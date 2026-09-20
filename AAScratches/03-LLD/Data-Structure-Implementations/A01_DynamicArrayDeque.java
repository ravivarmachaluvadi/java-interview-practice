/*
 * =====================================================================
 *  Dynamic Array Deque (circular buffer)      LLD build | Medium    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Build a double-ended queue on top of a plain array: add and remove at BOTH
 *   ends in O(1) amortized time, with no fixed capacity limit.
 *   The array must never be shifted on a normal add/remove, and it must grow
 *   automatically once it is full.
 *
 * DESIGN  (classes and why)
 *   DynamicArrayDeque<T>  - the whole structure; one class is enough because a
 *                           deque is just an array plus three integers.
 *     T[] arr    the storage. Its length is CAPACITY, not the element count.
 *     front      index of the first element.
 *     rear       index of the slot AFTER the last element (a half-open range).
 *     size       how many elements are actually stored.
 *   The live elements are arr[front], arr[(front+1) % cap], ... , size of them,
 *   so the block may wrap past the end of the array and continue at index 0.
 *
 * KEY DECISIONS
 *   1. Circular indexing, not shifting. Moving the front pointer with
 *      (front - 1 + cap) % cap is O(1); memmove-ing every element is O(n).
 *   2. Track size explicitly. With only front and rear you cannot tell "empty"
 *      (front == rear) from "full" (front == rear), the classic ring-buffer trap.
 *   3. Grow by DOUBLING, not by +1. Doubling makes the copies cost O(1) each
 *      when averaged over the adds that paid for them (amortized analysis).
 *   4. resize() unrolls the wrap: it copies front-first into a fresh array and
 *      resets front = 0, rear = size, so the new buffer starts contiguous.
 *   5. Removing does not shrink the array. Shrinking at exactly half full makes
 *      an add/remove alternation thrash; production code shrinks at 1/4 full.
 *
 * COMPLEXITY
 *   Time  O(1) amortized for addFront/addRear/removeFront/removeRear/peeks.
 *         A single add that triggers resize is O(n); n adds cost O(n) total,
 *         because each doubling copies less than the adds since the last one.
 *   Space O(capacity); capacity stays under 2n after any growth.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why double instead of grow by a constant? (constant growth is O(n^2) total)
 *   - Make it thread-safe: one lock, or two locks with a gap, or lock-free CAS?
 *   - Shrink when it empties out - at what load factor, and why not at 1/2?
 *   - This is ArrayDeque. When would you pick LinkedList instead? (almost never:
 *     ArrayDeque has better cache locality and no per-node object overhead)
 *
 * RUN
 *   main() runs 3 cases (typical mix of ends, empty-deque errors, and a growth
 *   case where the block wraps before it doubles) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class DynamicArrayDeque<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private T[] arr;
    private int front;   // index of the first element
    private int rear;    // index one past the last element
    private int size;

    DynamicArrayDeque() {
        this(DEFAULT_CAPACITY);
    }

    /** Small capacities are useful in tests: they make resize() happen early. */
    @SuppressWarnings("unchecked")
    DynamicArrayDeque(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        this.arr = (T[]) new Object[initialCapacity];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    /** Exposed only so the demo can show that growth doubles the buffer. */
    public int capacity() {
        return arr.length;
    }

    /**
     * Doubles the buffer and copies the elements front-first, which also
     * "unwraps" them, so the new array is contiguous from index 0.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newArr = (T[]) new Object[arr.length * 2];
        for (int i = 0; i < size; i++) {
            newArr[i] = arr[(front + i) % arr.length];
        }
        arr = newArr;
        front = 0;
        rear = size;
    }

    public void addFront(T element) {
        if (size == arr.length) {
            resize();
        }
        // step BACK one slot, wrapping around index 0 to the array's tail
        front = (front - 1 + arr.length) % arr.length;
        arr[front] = element;
        size++;
    }

    public void addRear(T element) {
        if (size == arr.length) {
            resize();
        }
        arr[rear] = element;           // rear already points at a free slot
        rear = (rear + 1) % arr.length;
        size++;
    }

    public T removeFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        T element = arr[front];
        arr[front] = null;             // drop the reference so GC can reclaim it
        front = (front + 1) % arr.length;
        size--;
        return element;
    }

    public T removeRear() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        rear = (rear - 1 + arr.length) % arr.length;  // step back onto the last element
        T element = arr[rear];
        arr[rear] = null;
        size--;
        return element;
    }

    public T getFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return arr[front];
    }

    public T getRear() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return arr[(rear - 1 + arr.length) % arr.length];
    }

    /** Front-to-rear view, so a wrapped buffer still prints in logical order. */
    @Override
    public String toString() {
        List<T> view = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            view.add(arr[(front + i) % arr.length]);
        }
        return view.toString();
    }

    // ------------------------------------------------------------------ demo

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 - typical: interleave both ends, then drain from both ends.
        DynamicArrayDeque<Integer> deque = new DynamicArrayDeque<>();
        deque.addFront(10);
        deque.addRear(20);
        deque.addFront(5);
        deque.addRear(30);
        print("case 1 contents", deque, "[5, 10, 20, 30]");
        print("case 1 getFront", deque.getFront(), 5);
        print("case 1 getRear", deque.getRear(), 30);
        print("case 1 size", deque.size(), 4);
        print("case 1 removeFront", deque.removeFront(), 5);
        print("case 1 removeRear", deque.removeRear(), 30);
        print("case 1 after removals", deque, "[10, 20]");

        // Case 2 - edge: an empty deque reports empty and refuses every read.
        DynamicArrayDeque<String> empty = new DynamicArrayDeque<>();
        print("case 2 isEmpty", empty.isEmpty(), true);
        print("case 2 removeFront", errorOf(empty), "IllegalStateException: Deque is empty");
        empty.addRear("only");
        print("case 2 single element", empty, "[only]");
        print("case 2 front == rear", empty.getFront().equals(empty.getRear()), true);
        print("case 2 drained", empty.removeFront() + " -> " + empty.isEmpty(), "only -> true");

        // Case 3 - tricky: fill a capacity-4 buffer so the block WRAPS, then add
        // one more to force the doubling copy, and check the order survives.
        DynamicArrayDeque<Integer> small = new DynamicArrayDeque<>(4);
        small.addRear(1);
        small.addRear(2);
        small.addFront(0);    // front wraps to index 3
        small.addFront(-1);   // buffer is now full and wrapped
        print("case 3 full+wrapped", small, "[-1, 0, 1, 2]");
        print("case 3 capacity", small.capacity(), 4);
        small.addRear(3);     // triggers resize(): copy, unwrap, then insert
        print("case 3 after growth", small, "[-1, 0, 1, 2, 3]");
        print("case 3 capacity", small.capacity(), 8);
        print("case 3 getFront/getRear", small.getFront() + "/" + small.getRear(), "-1/3");
    }

    /** Runs removeFront() on an empty deque and renders the failure as text. */
    private static String errorOf(DynamicArrayDeque<?> deque) {
        try {
            deque.removeFront();
            return "no exception";
        } catch (IllegalStateException e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}
