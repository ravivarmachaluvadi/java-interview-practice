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
