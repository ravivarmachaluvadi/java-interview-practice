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
}
