/**
 * Problem: Concurrently append 2 million CSV records to a single file using multiple threads.
 *
 * Approach: A fixed thread pool submits tasks that generate a record string and
 * then acquire a ReentrantLock before writing the record with FileWriter in append mode.
 * The lock ensures only one thread writes at a time, preventing interleaved output.
 *
 * Time Complexity: O(N) where N is RECORD_COUNT (each record is generated and written once).
 * Space Complexity: O(1) auxiliary space; memory usage grows only for the record string per task.
 */
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
