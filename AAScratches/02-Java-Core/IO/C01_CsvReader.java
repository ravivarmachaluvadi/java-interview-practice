/**
 * Reads a CSV file line by line and processes each record concurrently using a thread pool.
 *
 * The program opens "output.csv", submits each line to an ExecutorService for processing,
 * counts the number of processed lines, and reports the total count along with elapsed time.
 *
 * Approach:
 * 1. Use BufferedReader to stream the file.
 * 2. Submit each line as a task to a fixed thread pool (currently single-threaded).
 * 3. Increment an AtomicLong counter in each task; optionally process the record.
 * 4. Await termination and output statistics.
 *
 * Time Complexity: O(n) where n is the number of lines, since each line is read and processed once.
 * Space Complexity: O(1) auxiliary space (excluding input storage); the thread pool size is constant.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class CsvReader {
    private static final int THREAD_COUNT = 1;
    private static final String CSV_FILE = "output.csv";
    private static final AtomicLong linesRead = new AtomicLong(0);

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.currentTimeMillis();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String record = line;
                executor.submit(() -> {
//                    processRecord(record);
                    linesRead.incrementAndGet();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        long timeTaken = endTime - startTime;
        System.out.println("Finished reading " + linesRead.get() + " records in " + timeTaken + " milliseconds.");
    }

    private static void processRecord(String record) {
        // Implement your record processing logic here
        System.out.println(record);
    }
}
