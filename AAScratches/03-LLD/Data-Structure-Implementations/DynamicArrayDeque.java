
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
}
