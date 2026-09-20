/*
 * =====================================================================
 *  LimitQueue - blocking work queue            Packaged helper | header-only
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The work queue handed to every ThreadPoolExecutor that WorkFlowExecutor builds
 *   in init(). It is a plain LinkedBlockingQueue with exactly one method changed:
 *   offer(E) delegates to put(E), so a full queue makes the CALLING thread wait
 *   instead of refusing the item.
 *
 *   Why that one override matters: ThreadPoolExecutor.execute() offers the task to
 *   its queue, and when offer() returns false it hands the task to the rejection
 *   handler - by default AbortPolicy, which throws RejectedExecutionException.
 *   Because offer() can no longer return false on a full queue, tasks are never
 *   dropped; the producer is throttled instead. That is back-pressure.
 *
 * WHAT TO NOTICE
 *   - The blocking happens on the SUBMITTING thread, not a pool thread. Any caller
 *     of WorkFlowExecutor.submitTask() can be parked here, so this is only safe
 *     when the producer is allowed to slow down (a consumer loop, not a web thread
 *     with a response deadline).
 *
 *   - CallerRunsPolicy is the built-in alternative to this trick. It also slows the
 *     producer, but by running the task on the producer's thread - which would break
 *     the per-key ordering WorkFlowExecutor exists to provide. Blocking preserves it.
 *
 *   - Interruption is the one path that can still reject: put() throws
 *     InterruptedException, the catch restores the interrupt flag and returns false,
 *     and the executor then treats the task as rejected.
 *
 *   - Shutdown hazard: a thread parked in put() is released only by a consumer or by
 *     an interrupt. shutdown() does not unblock it, and shutdownNow() interrupts the
 *     pool's worker threads, not the producers waiting to enqueue.
 *
 *   - add() inherits AbstractQueue's implementation, which calls offer(), so add()
 *     blocks too. put() and the timed offer(e, timeout, unit) are untouched.
 *
 *   - Capacity is fixed at construction via super(maxSize); LinkedBlockingQueue
 *     cannot be resized afterwards.
 *
 *   - It extends a Serializable class without declaring serialVersionUID, which
 *     javac -Xlint:serial will warn about.
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
        com.tgt.gom.federator.grouped_processor.LimitQueue<Integer> q =
                new com.tgt.gom.federator.grouped_processor.LimitQueue<>(3);

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
