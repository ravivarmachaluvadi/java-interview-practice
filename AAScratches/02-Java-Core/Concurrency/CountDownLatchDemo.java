import java.util.Comparator;
import java.util.concurrent.*;

class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        PriorityBlockingQueue<Integer> priorityBlockingQueue =
                new PriorityBlockingQueue<>(5, Comparator.reverseOrder());
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(() -> {
            priorityBlockingQueue.add(5);
            latch.countDown();
        });
        executor.execute(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            priorityBlockingQueue.add(10);
            latch.countDown();
        });
//        latch.await(1, TimeUnit.SECONDS);
        latch.await(2, TimeUnit.SECONDS);
        priorityBlockingQueue.stream().iterator().forEachRemaining(System.out::println);
        System.out.println("Hello world!");

    }
}