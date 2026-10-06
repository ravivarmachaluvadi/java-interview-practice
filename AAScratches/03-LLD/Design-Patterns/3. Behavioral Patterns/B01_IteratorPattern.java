/*
 * =====================================================================
 *  Iterator - paged orders API export                Behavioral | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A nightly job exports every order to the data warehouse. The orders
 *   service only returns pages (GET /orders?cursor=0&limit=3), and loading
 *   all 2 million orders into one list would exhaust memory. The export
 *   code should still read like a plain for-each loop.
 *
 * KEY INSIGHT
 *   The iterator hides HOW elements are fetched. hasNext() calls the API
 *   only when the current page is used up, so memory holds one page and a
 *   caller that stops early never pays for the pages it did not read.
 *   Implementing java.util.Iterator and returning it from an Iterable is
 *   what makes for-each work.
 *
 * ROLES IN THIS CODE
 *   Iterator<T> (java.util)    Iterator interface
 *   PagedIterator              ConcreteIterator - cursor + current page
 *   PageSource / OrdersApi     the aggregate, reachable only page by page
 *   allOrders()                Iterable, so for-each works
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why `while`, not `if`, in hasNext()? An API may return an empty page
 *     that still has a next cursor.
 *   - Seen in: AWS SDK paginators, Spring Data Slice, JDBC ResultSet,
 *     Kafka consumer poll loops.
 *
 * RUN
 *   5 cases: 7 orders in 3 calls, stop early after 1 call, exactly 2 full
 *   pages, empty API, next() past the end.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** One page of results; nextCursor is null on the last page. */
record Page<T>(List<T> items, Integer nextCursor) {
}

interface PageSource<T> {
    Page<T> fetch(int cursor, int limit);
}

/** Stand-in for the remote orders service; counts how often it is called. */
class OrdersApi implements PageSource<String> {
    private final List<String> rows = new ArrayList<>();
    int calls;

    OrdersApi(int orderCount) {
        for (int i = 1; i <= orderCount; i++) {
            rows.add("OD-" + i);
        }
    }

    public Page<String> fetch(int cursor, int limit) {
        calls++;
        int end = Math.min(cursor + limit, rows.size());
        return new Page<>(rows.subList(cursor, end), end < rows.size() ? end : null);
    }
}

class PagedIterator<T> implements Iterator<T> {
    private final PageSource<T> source;
    private final int pageSize;
    private Iterator<T> current = Collections.emptyIterator();
    private Integer nextCursor = 0; // null = no more pages

    PagedIterator(PageSource<T> source, int pageSize) {
        this.source = source;
        this.pageSize = pageSize;
    }

    @Override
    public boolean hasNext() {
        while (!current.hasNext() && nextCursor != null) { // fetch lazily, one page at a time
            Page<T> page = source.fetch(nextCursor, pageSize);
            current = page.items().iterator();
            nextCursor = page.nextCursor();
        }
        return current.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more orders");
        }
        return current.next();
    }
}

class IteratorPatternExample {

    static Iterable<String> allOrders(PageSource<String> api, int pageSize) {
        return () -> new PagedIterator<>(api, pageSize);
    }

    public static void main(String[] args) {
        // Case 1: typical. 7 orders, pages of 3 -> 3 API calls, plain for-each.
        OrdersApi api = new OrdersApi(7);
        List<String> exported = new ArrayList<>();
        for (String order : allOrders(api, 3)) {
            exported.add(order);
        }
        print("case 1a exported", exported, "[OD-1, OD-2, OD-3, OD-4, OD-5, OD-6, OD-7]");
        print("case 1b calls   ", api.calls, 3);

        // Case 2: lazy. Stop after 2 orders -> only the first page was fetched.
        OrdersApi preview = new OrdersApi(7);
        Iterator<String> it = allOrders(preview, 3).iterator();
        print("case 2a first 2 ", it.next() + " " + it.next(), "OD-1 OD-2");
        print("case 2b calls   ", preview.calls, 1);

        // Case 3: edge. 6 orders = 2 full pages; the null cursor avoids a 3rd call.
        OrdersApi six = new OrdersApi(6);
        allOrders(six, 3).forEach(order -> { });
        print("case 3 calls    ", six.calls, 2);

        // Case 4-5: edge. No orders at all, then reading past the end.
        Iterator<String> none = allOrders(new OrdersApi(0), 3).iterator();
        print("case 4 empty    ", none.hasNext(), false);
        String outcome;
        try {
            outcome = none.next();
        } catch (NoSuchElementException e) {
            outcome = e.getMessage();
        }
        print("case 5 past end ", outcome, "no more orders");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
