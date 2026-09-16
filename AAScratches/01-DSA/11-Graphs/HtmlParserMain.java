import java.util.*;
import java.util.concurrent.*;

// This is the interface provided by LeetCode – you do *not* implement it.
interface HtmlParser {
    // Returns all URLs from the webpage at the given url.
    List<String> getUrls(String url);
}

public class HtmlParserMain {

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        // Concurrent set of visited URLs
        Set<String> visited = ConcurrentHashMap.newKeySet();
        // Extract the hostname of startUrl
        String hostname = extractHostname(startUrl);

        // Thread pool (fixed size)
        int numThreads = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Use a thread‐safe queue (blocking) of URLs to process
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.add(startUrl);
        visited.add(startUrl);

        // Submit worker tasks
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        String url = queue.poll(1, TimeUnit.SECONDS);
                        if (url == null) {
                            // no URL within timeout → assume done
                            break;
                        }
                        List<String> nextUrls = htmlParser.getUrls(url);
                        for (String u : nextUrls) {
                            if (extractHostname(u).equals(hostname) && visited.add(u)) {
                                queue.add(u);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    // thread interrupted → exit
                }
            });
        }

        // Shutdown executor and wait for tasks to finish
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }

        // Return results
        return new ArrayList<>(visited);
    }

    private String extractHostname(String url) {
        // assume url starts with "http://"
        final String prefix = "http://";
        int start = 0;
        if (url.startsWith(prefix)) {
            start = prefix.length();
        }
        int slashIndex = url.indexOf('/', start);
        if (slashIndex == -1) {
            return url.substring(start);
        }
        return url.substring(start, slashIndex);
    }

    // Example main to illustrate how it would be used
    public static void main(String[] args) {
        // Example HtmlParser implementation for testing
        Map<String, List<String>> mockWeb = new HashMap<>();
        mockWeb.put("http://news.yahoo.com/news/topics/",
                Arrays.asList("http://news.yahoo.com","http://news.yahoo.com/news","http://news.yahoo.com/us","http://news.google.com"));
        mockWeb.put("http://news.yahoo.com",
                Arrays.asList("http://news.yahoo.com/news/topics/"));
        mockWeb.put("http://news.yahoo.com/news",
                Collections.emptyList());
        mockWeb.put("http://news.yahoo.com/us",
                Collections.emptyList());
        mockWeb.put("http://news.google.com",
                Collections.emptyList());

        HtmlParser parser = new HtmlParser() {
            @Override
            public List<String> getUrls(String url) {
                return mockWeb.getOrDefault(url, Collections.emptyList());
            }
        };

        HtmlParserMain sol = new HtmlParserMain();
        List<String> result = sol.crawl("http://news.yahoo.com/news/topics/", parser);
        System.out.println("Crawled URLs:");
        for (String u : result) {
            System.out.println(u);
        }
    }
}
