import java.util.concurrent.*;

class CustomFutureThreadPool {
    private final BlockingQueue<FutureTask<?>> taskQueue;
    private final WorkerThread[] threads;

    public CustomFutureThreadPool(int poolSize) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.threads = new WorkerThread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            threads[i] = new WorkerThread();
            threads[i].start();
        }
    }

    public <T> Future<T> submitTask(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        try {
            taskQueue.put(futureTask);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("submitTask");
        return futureTask;
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            while (true) {  // Keep the thread running to pick up new tasks
                try {
                    System.out.println("Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.");
                    FutureTask<?> task = taskQueue.take();
                    System.out.println("took");
                    task.run();  // Executes the task's call() method
                    System.out.println("run completed");
                } catch (InterruptedException e) {
                    break;  // Break the loop if the thread is interrupted
                }
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CustomFutureThreadPool threadPool = new CustomFutureThreadPool(3);
        Callable<String> task1 = () -> {
            Thread.sleep(1000);
            System.out.println("task1");
            return "Task 1 Completed Returned";
        };
        Callable<Integer> task2 = () -> {
            Thread.sleep(500);
            return 42;
        };

        Future<String> result1 = threadPool.submitTask(task1);
        System.out.println("result1 : " + result1);
        // Waits if necessary for the computation to complete, and then retrieves its result.
        System.out.println(result1.get());  // Outputs: Task 1 completed
        System.out.println("result1 : " + result1);
    }
}

