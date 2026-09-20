/*
 * =====================================================================
 *  Web Crawler Multithreaded                        LeetCode 1242 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a startUrl and an HtmlParser that returns the links on a page, crawl every page
 *   reachable from startUrl that lives under the SAME hostname, and return those URLs in
 *   any order. getUrls() is slow (network), so the crawl must run on several threads.
 *   Every URL looks like http://host/path; never visit the same URL twice.
 *
 * EXAMPLE
 *   start = http://news.yahoo.com/news/topics/
 *   topics/ links to {news.yahoo.com, .../news, .../us, http://news.google.com}
 *   -> [http://news.yahoo.com/news/topics/, http://news.yahoo.com,
 *       http://news.yahoo.com/news, http://news.yahoo.com/us]   google.com is a different host
 *   Edge: a page with no links at all -> [startUrl]
 *   Tricky: a -> b -> c -> a (cycle, slow parser) -> the 3 pages, exactly once each
 *
 * APPROACH  (concurrent BFS: fixed thread pool + shared queue + shared visited set)
 *   1. visited = ConcurrentHashMap.newKeySet(). visited.add(url) is an atomic
 *      "claim": it returns true for exactly one thread, so a URL is queued once.
 *   2. A LinkedBlockingQueue holds URLs still to fetch; N worker tasks take() from it.
 *   3. A worker fetches one URL, keeps the links on the same host it manages to claim,
 *      and pushes them back on the queue.
 *   4. Termination: an AtomicInteger counts URLs handed out but not finished. A worker
 *      increments it once per child it enqueues, then decrements for the URL it just
 *      finished. Whoever drives the counter to 0 pushes one POISON marker per worker,
 *      and every worker exits on seeing one.
 *   5. crawlSequential() is the single-thread BFS baseline, run from main() so both
 *      answers can be compared.
 *
 * KEY INSIGHT
 *   The BFS is the easy half; the interview is really about when to STOP. An empty queue
 *   does NOT mean the crawl is done - a worker may still be inside getUrls() and about to
 *   add more URLs. Count outstanding work, not queue emptiness. And do the dedup with the
 *   return value of Set.add(), so "have I seen it" and "mark it seen" are one atomic step
 *   instead of a check-then-act race.
 *
 * COMPLEXITY
 *   Time  O(V + E)  every page is fetched once, every link inspected once; wall clock
 *                   divides by the number of threads because fetches overlap.
 *   Space O(V)      visited set plus the queue, both bounded by the number of pages.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not poll(1, SECONDS) and quit on timeout? It both quits early (a slow fetch is
 *     still running) and wastes a second at the end. Fixed: outstanding-work counter.
 *   - Why not visited.contains() then visited.add()? Two threads can both pass contains()
 *     and crawl the same page twice. Use add()'s boolean.
 *   - Scale it past one machine: shard by host, Redis/Bloom filter for visited, a durable
 *     frontier queue (Kafka/SQS) so a crashed worker's URLs are redelivered.
 *   - Politeness: per-host rate limit, robots.txt, retry with backoff, depth cap.
 *
 * RUN
 *   main() runs 3 cases (typical fan-out, single page with no links, slow cyclic web)
 *   and prints actual vs expected for both the concurrent and the sequential crawl.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// This is the interface LeetCode provides - you do NOT implement it.
interface HtmlParser {
    // Returns all URLs linked from the webpage at the given url.
    List<String> getUrls(String url);
}

class HtmlParserMain {

    // Marker pushed on the queue to tell one worker "the crawl is over, go home".
    private static final String POISON = "!!crawl-finished!!";

    /*
     * Fixed: the original stopped a worker when queue.poll(1, SECONDS) timed out. That is a
     * guess, not a rule - an empty queue only means no URL is waiting right now, while
     * another worker may still be inside a slow getUrls() about to enqueue more. The
     * outstanding counter below makes termination exact (and instant instead of 1s late).
     */
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        String hostname = extractHostname(startUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        // URLs handed out but not yet finished. Starts at 1 for startUrl itself.
        AtomicInteger outstanding = new AtomicInteger(1);

        visited.add(startUrl);
        queue.add(startUrl);

        int numThreads = Math.min(8, Runtime.getRuntime().availableProcessors() * 2);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() ->
                    worker(queue, visited, outstanding, hostname, htmlParser, numThreads));
        }

        executor.shutdown();
        try {
            // Generous bound: the poison pills end the workers as soon as the work is done.
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(visited);
    }

    private void worker(BlockingQueue<String> queue, Set<String> visited,
                        AtomicInteger outstanding, String hostname,
                        HtmlParser htmlParser, int numThreads) {
        try {
            while (true) {
                String url = queue.take();
                if (POISON.equals(url)) {
                    return;
                }
                for (String next : htmlParser.getUrls(url)) {
                    // visited.add() is the atomic claim: true for exactly one thread.
                    if (hostname.equals(extractHostname(next)) && visited.add(next)) {
                        outstanding.incrementAndGet();   // count the child BEFORE queueing it
                        queue.add(next);
                    }
                }
                if (outstanding.decrementAndGet() == 0) {
                    // Nothing queued and nothing in flight: wake every worker so it can exit.
                    for (int i = 0; i < numThreads; i++) {
                        queue.add(POISON);
                    }
                    return;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Single-threaded BFS baseline - same answer, used in main() as a cross-check. */
    public List<String> crawlSequential(String startUrl, HtmlParser htmlParser) {
        String hostname = extractHostname(startUrl);
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        visited.add(startUrl);
        queue.add(startUrl);
        while (!queue.isEmpty()) {
            String url = queue.poll();
            for (String next : htmlParser.getUrls(url)) {
                if (hostname.equals(extractHostname(next)) && visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return new ArrayList<>(visited);
    }

    /** http://news.yahoo.com/us -> news.yahoo.com (host is everything up to the next '/'). */
    private String extractHostname(String url) {
        final String prefix = "http://";
        int start = url.startsWith(prefix) ? prefix.length() : 0;
        int slash = url.indexOf('/', start);
        return slash == -1 ? url.substring(start) : url.substring(start, slash);
    }

    // ---------------------------------------------------------------- test harness

    /** Mock parser over a fixed map, with an optional per-fetch delay to mimic the network. */
    private static HtmlParser mockParser(Map<String, List<String>> web, long delayMillis) {
        return url -> {
            if (delayMillis > 0) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return web.getOrDefault(url, Collections.emptyList());
        };
    }

    /** Crawl order is nondeterministic, so compare sorted sets. */
    private static String sorted(List<String> urls) {
        return new TreeSet<>(urls).toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        HtmlParserMain sol = new HtmlParserMain();

        // Case 1 - typical: fan-out, one link on a different host, one link back (cycle).
        Map<String, List<String>> yahoo = new HashMap<>();
        yahoo.put("http://news.yahoo.com/news/topics/", Arrays.asList(
                "http://news.yahoo.com", "http://news.yahoo.com/news",
                "http://news.yahoo.com/us", "http://news.google.com"));
        yahoo.put("http://news.yahoo.com", Arrays.asList("http://news.yahoo.com/news/topics/"));
        HtmlParser p1 = mockParser(yahoo, 0);
        String expect1 = "[http://news.yahoo.com, http://news.yahoo.com/news, "
                + "http://news.yahoo.com/news/topics/, http://news.yahoo.com/us]";
        String start1 = "http://news.yahoo.com/news/topics/";
        print("case 1 concurrent", sorted(sol.crawl(start1, p1)), expect1);
        print("case 1 sequential", sorted(sol.crawlSequential(start1, p1)), expect1);

        // Case 2 - edge: a page with no links, plus a link to itself that must not loop.
        Map<String, List<String>> lonely = new HashMap<>();
        lonely.put("http://a.com/", Arrays.asList("http://a.com/"));
        HtmlParser p2 = mockParser(lonely, 0);
        print("case 2 concurrent", sorted(sol.crawl("http://a.com/", p2)), "[http://a.com/]");
        print("case 2 sequential",
                sorted(sol.crawlSequential("http://a.com/", p2)), "[http://a.com/]");

        // Case 3 - tricky: slow chain a -> b -> c -> a. Each fetch takes 150ms, so at times
        // the queue is empty while work is still in flight. A timeout-based stop would
        // return a partial answer here; the outstanding counter returns all three.
        Map<String, List<String>> chain = new HashMap<>();
        chain.put("http://x.com/a", Arrays.asList("http://x.com/b"));
        chain.put("http://x.com/b", Arrays.asList("http://x.com/c"));
        chain.put("http://x.com/c", Arrays.asList("http://x.com/a", "http://y.com/a"));
        HtmlParser p3 = mockParser(chain, 150);
        String expect3 = "[http://x.com/a, http://x.com/b, http://x.com/c]";
        print("case 3 concurrent", sorted(sol.crawl("http://x.com/a", p3)), expect3);
        print("case 3 sequential", sorted(sol.crawlSequential("http://x.com/a", p3)), expect3);
    }
}
