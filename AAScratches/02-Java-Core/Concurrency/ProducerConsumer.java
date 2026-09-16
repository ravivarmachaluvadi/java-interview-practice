/**
 * Implements a simple producer–consumer problem using a shared queue.
 *
 * The producer thread generates sequential integers and enqueues them,
 * while the consumer thread dequeues and prints each value. Both threads
 * synchronize on the same monitor, using wait() when the queue is full or empty
 * and notifyAll() after producing or consuming to wake the other thread.
 *
 * Time Complexity: O(n) for n produced/consumed items (each operation is constant time).
 * Space Complexity: O(1) additional space beyond the single-element queue used as a buffer.
 */
import java.util.LinkedList;
import java.util.Queue;

class ProducerConsumer {

    static Queue<Integer> queue = new LinkedList<>();
    static int val = 0;

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer producerConsumer = new ProducerConsumer();

        Thread producer = new Thread(producerConsumer::producer);
        Thread consumer = new Thread(producerConsumer::consumer);
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

    }

    public synchronized void producer() {
        while (true) {
            if (queue.isEmpty()) {
                int e = val++;
                System.out.println("Produced : " + e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                queue.add(e);
                notifyAll();
            } else {
                try {
                    // wait also should be inside while loop
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public synchronized void consumer() {
        while (true) {
            if (!queue.isEmpty()) {
                System.out.println("Consumed : " + queue.poll());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                notifyAll();
            } else {
                try {
                    // wait also should be inside while loop
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
