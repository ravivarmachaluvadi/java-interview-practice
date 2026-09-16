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

