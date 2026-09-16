/**
 * Problem: Demonstrates thread‑safe increment of shared static and instance fields
 * using a fixed thread pool.  Two million tasks are submitted, each task
 * increments both the static field {@code val} and the instance field
 * {@code nonStaticVal}.  Synchronization is performed on {@link StaticValChat.class}
 * to ensure atomic updates.
 *
 * Approach: Create a cached ExecutorService with 10 threads. Submit two types of
 * tasks in a loop: one that calls the static method {@code staticInc} and one
 * that calls the instance method {@code nonStaticInc}. Both methods synchronize on
 * the class object, incrementing the shared fields.
 *
 * Complexity:
 *   Time   O(n) where n = 2 000 000 tasks (constant work per task).
 *   Space  O(1) additional memory beyond the thread pool and task queue,
 *          which is bounded by the fixed pool size of 10 threads.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class StaticValChat {
    public static int val = 0;
    public int nonStaticVal = 0;
    static ExecutorService executorService = Executors.newFixedThreadPool(10);
    static StaticValChat staticVal = new StaticValChat();
    static Object lock = new Object();

    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            executorService.execute(StaticValChat::staticInc);
            executorService.execute(staticVal::nonStaticInc);
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
        System.out.println("static val : " + val);
        System.out.println("non-static val : " + staticVal.nonStaticVal);
    }

    private static void staticInc() {
//        synchronized ( StaticValChat.class )
        synchronized (StaticValChat.class) {
            val++;
            staticVal.nonStaticVal++;
        }
    }

    private void nonStaticInc() {
//        synchronized ( lock )
        synchronized (StaticValChat.class) {
            val++;
            staticVal.nonStaticVal++;
        }
    }
}

