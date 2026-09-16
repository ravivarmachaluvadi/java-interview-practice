/**
 * Implements a bounded blocking queue that blocks on {@code offer} when the
 * capacity is reached instead of throwing an exception. This allows producers
 * to wait until space becomes available, ensuring back‑pressure in concurrent
 * producer–consumer scenarios.
 *
 * The class extends {@link java.util.concurrent.LinkedBlockingQueue} and
 * overrides {@code offer(E)} to call {@code put(e)}, which blocks until the
 * queue has room or the thread is interrupted. If interrupted, the method
 * restores the interrupt status and returns false.
 *
 * Time Complexity: O(1) per {@code offer} (amortized). Space Complexity:
 * O(capacity), where capacity is specified at construction time.
 */
package com.tgt.gom.federator.grouped_processor;

import java.util.concurrent.LinkedBlockingQueue;

public class LimitQueue<E> extends LinkedBlockingQueue<E> {

    public LimitQueue(int maxSize) {
        super(maxSize);
    }

    /**
     * Custom blocking Linked Queue
     * This queue wait for insertion if the queue is full rather than throwing an exception
     *
     * @param e payload to be pushed on queue
     * @return push status for the payload
     */
    @Override
    public boolean offer(E e) {
        //Turn offer() and add() into a blocking calls (unless interrupted)
        try {
            put(e);
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        // Create a LimitQueue with capacity 3
        com.tgt.gom.federator.grouped_processor.LimitQueue<Integer> q = new com.tgt.gom.federator.grouped_processor.LimitQueue<>(3);

        // Prepare input values
        Integer[] inputs = {1, 2, 3, 4};

        System.out.println("Input values: ");
        for (int v : inputs) {
            System.out.print(v + " ");
        }
        System.out.println();

        // Start a thread that will poll an element after a short delay
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(1000); // wait before freeing space
                Integer polled = q.poll();
                System.out.println("Consumer polled: " + polled);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        // Offer all inputs; the fourth offer will block until the consumer frees space
        for (int v : inputs) {
            boolean offered = q.offer(v);
            System.out.println("Offered " + v + ": " + offered);
        }

        // Wait for consumer to finish
        consumer.join();

        // Drain remaining queue contents
        System.out.print("Final queue contents: ");
        Integer item;
        while ((item = q.poll()) != null) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
