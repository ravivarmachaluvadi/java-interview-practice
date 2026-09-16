class ThreadGroupManagement {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup clientGroup = new ThreadGroup("ClientConnectionGroup");
        // Start some dummy client threads
        for (int i = 1; i <= 3; i++)
            new Thread(clientGroup, new DummyClient(), "ClientThread-" + i).start();
        // Sleep to let the threads run for a while
        Thread.sleep(5000);

        // Display active threads
        System.out.println("Active threads in group: " + clientGroup.activeCount());
        // Interrupt all threads in the group
        System.out.println("Interrupting all threads in group...");
        clientGroup.interrupt();
    }
}

class DummyClient implements Runnable {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread() + " is running...");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread() + " was interrupted.");
        }
    }
}

