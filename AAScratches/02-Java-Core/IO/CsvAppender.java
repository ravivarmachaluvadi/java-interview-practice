import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class CsvAppender {

    private static final int THREAD_COUNT = 10;
    private static final int RECORD_COUNT = 2000000;
    private static final String CSV_FILE = "output.csv";
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < RECORD_COUNT; i++) {
            int recordNumber = i;
            executor.submit(() -> {
                String record = generateRecord(recordNumber);
                appendToCsv(CSV_FILE, record);
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / 1000;
        System.out.println("Finished appending records in " + timeTaken + " seconds.");
    }

    private static String generateRecord(int recordNumber) {
        // Assuming the CSV has three columns for simplicity: ID, Column1, Column2
        return recordNumber + ",value1_" + recordNumber + ",value2_" + recordNumber + "\n";
    }

    private static void appendToCsv(String fileName, String record) {
        lock.lock();
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(record);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
