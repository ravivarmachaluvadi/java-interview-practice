/**
 * Implements a double-ended queue (Deque) backed by a dynamic circular array.
 *
 * Problem: Provide O(1) amortized insertion and removal at both ends of a sequence,
 * while automatically resizing when capacity is exceeded.
 *
 * Approach: Use a circular buffer with front and rear indices. When the buffer
 * becomes full, double its size and copy elements contiguously to reset indices.
 * All operations adjust indices modulo current capacity.
 *
 * Time Complexity:
 *   - addFront / addRear / removeFront / removeRear / getFront / getRear: O(1) amortized
 *   - resize (when needed): O(n), but occurs infrequently, keeping overall amortized cost constant.
 *
 * Space Complexity: O(n) where n is the number of elements stored; extra space for array resizing.
 */

class DynamicArrayDeque<T> {
    private T[] arr;
    private int front;
    private int rear;
    private int size;

    @SuppressWarnings("unchecked")
    public DynamicArrayDeque() {
        arr = (T[]) new Object[10]; // Initial capacity
        front = 0;
        rear = 0;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Doubles the array size when it’s full
    private void resize() {
        int newCapacity = arr.length * 2;
        @SuppressWarnings("unchecked")
        T[] newArr = (T[]) new Object[newCapacity];

        // Copy elements to the new array
        for (int i = 0; i < size; i++) {
            newArr[i] = arr[(front + i) % arr.length];
        }

        arr = newArr;
        front = 0;
        rear = size;
    }

    // Adds an element to the front of the deque
    public void addFront(T element) {
        if (size == arr.length) {
            resize();
        }

        front = (front - 1 + arr.length) % arr.length;
        arr[front] = element;
        size++;
    }

    // Adds an element to the rear of the deque
    public void addRear(T element) {
        if (size == arr.length) {
            resize();
        }

        arr[rear] = element;
        rear = (rear + 1) % arr.length;
        size++;
    }

    // Removes and returns the front element of the deque
    public T removeFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        T element = arr[front];
        front = (front + 1) % arr.length;
        size--;
        return element;
    }

    // Removes and returns the rear element of the deque
    public T removeRear() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        rear = (rear - 1 + arr.length) % arr.length;
        T element = arr[rear];
        size--;
        return element;
    }

    // Gets the front element of the deque
    public T getFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return arr[front];
    }

    // Gets the rear element of the deque
    public T getRear() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return arr[(rear - 1 + arr.length) % arr.length];
    }

    public static void main(String[] args) {
        DynamicArrayDeque<Integer> deque = new DynamicArrayDeque<>();
        System.out.println("Initial state: isEmpty=" + deque.isEmpty());

        // Add elements to front and rear
        deque.addFront(10);
        deque.addRear(20);
        deque.addFront(5);
        deque.addRear(30);

        System.out.println("\nAfter additions:");
        System.out.println("Front element (getFront): " + deque.getFront());
        System.out.println("Rear element (getRear): " + deque.getRear());

        // Remove elements
        int removedFront = deque.removeFront();
        int removedRear = deque.removeRear();

        System.out.println("\nAfter removals:");
        System.out.println("Removed from front: " + removedFront);
        System.out.println("Removed from rear: " + removedRear);
        System.out.println("New front element: " + deque.getFront());
        System.out.println("New rear element: " + deque.getRear());

        // Final state
        System.out.println("\nFinal state:");
        System.out.println("Size (via isEmpty check): " + (!deque.isEmpty() ? "Not empty" : "Empty"));
    }
}
