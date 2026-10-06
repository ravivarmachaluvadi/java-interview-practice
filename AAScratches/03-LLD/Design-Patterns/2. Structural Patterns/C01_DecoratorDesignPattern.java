/*
 * =====================================================================
 *  Decorator - retry, fallback, logging on an API call  Structural | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   The product page calls inventory-service for stock. During a deploy it
 *   returns 503 for a few calls. We want retries, a safe fallback (show
 *   "out of stock" instead of an error page) and a log of each attempt -
 *   switchable per call site, without touching the HTTP client class and
 *   without a RetryingLoggingFallbackClient subclass for every combination.
 *
 * KEY INSIGHT
 *   Each wrapper implements the SAME interface it wraps, so wrappers stack:
 *   new Fallback(new Retry(new Logging(remote))). Each adds one behaviour
 *   and delegates the rest. ORDER MATTERS: a fallback inside a retry
 *   swallows the error, so the retry never fires (case 4 vs case 3).
 *
 * ROLES IN THIS CODE
 *   InventoryClient           Component
 *   RemoteInventory           ConcreteComponent (the real, flaky call)
 *   InventoryDecorator        Decorator base: IS-A and HAS-A InventoryClient
 *   Retry, Fallback, Logging  ConcreteDecorator
 *
 * INTERVIEW FOLLOW-UPS
 *   - Decorator vs Proxy: same shape; a decorator ADDS behaviour and is
 *     handed its wrappee, a proxy CONTROLS access and usually owns it.
 *   - Seen in: Resilience4j Decorators.ofSupplier(..).withRetry(..),
 *     java.io (new BufferedReader(new FileReader(..))),
 *     Collections.unmodifiableList.
 *
 * RUN
 *   5 cases: bare call fails, retry recovers, fallback after retries run
 *   out, the wrong wrap order, logging every attempt.
 */

import java.util.ArrayList;
import java.util.List;

interface InventoryClient {
    int stock(String sku);
}

/** The real remote call. Fails the first N calls, like a service mid-deploy. */
class RemoteInventory implements InventoryClient {
    private int failuresLeft;
    int calls;

    RemoteInventory(int failures) {
        this.failuresLeft = failures;
    }

    public int stock(String sku) {
        calls++;
        if (failuresLeft-- > 0) {
            throw new IllegalStateException("503 from inventory-service");
        }
        return 42;
    }
}

/** IS an InventoryClient and HAS one - that double identity is the pattern. */
abstract class InventoryDecorator implements InventoryClient {
    protected final InventoryClient inner;

    InventoryDecorator(InventoryClient inner) {
        this.inner = inner;
    }
}

class Retry extends InventoryDecorator {
    private final int maxAttempts;

    Retry(int maxAttempts, InventoryClient inner) {
        super(inner);
        this.maxAttempts = maxAttempts;
    }

    public int stock(String sku) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return inner.stock(sku);
            } catch (RuntimeException e) {
                last = e;
            }
        }
        throw last;
    }
}

class Fallback extends InventoryDecorator {
    private final int fallbackStock;

    Fallback(int fallbackStock, InventoryClient inner) {
        super(inner);
        this.fallbackStock = fallbackStock;
    }

    public int stock(String sku) {
        try {
            return inner.stock(sku);
        } catch (RuntimeException e) {
            return fallbackStock; // degrade, do not fail the page
        }
    }
}

class Logging extends InventoryDecorator {
    final List<String> log = new ArrayList<>();

    Logging(InventoryClient inner) {
        super(inner);
    }

    public int stock(String sku) {
        try {
            int stock = inner.stock(sku);
            log.add("ok " + stock);
            return stock;
        } catch (RuntimeException e) {
            log.add("fail");
            throw e;
        }
    }
}

class DecoratorDesignPattern {

    public static void main(String[] args) {
        // Case 1: no decorators. The 503 reaches the product page.
        String outcome;
        try {
            outcome = "stock " + new RemoteInventory(2).stock("SKU-1");
        } catch (IllegalStateException e) {
            outcome = e.getMessage();
        }
        print("case 1 bare      ", outcome, "503 from inventory-service");

        // Case 2: typical. Two failures, then success on the 3rd attempt.
        RemoteInventory flaky = new RemoteInventory(2);
        print("case 2a retry    ", new Retry(3, flaky).stock("SKU-1"), 42);
        print("case 2b calls    ", flaky.calls, 3);

        // Case 3: still down after 3 attempts -> the fallback answers 0.
        RemoteInventory down = new RemoteInventory(5);
        print("case 3a fallback ", new Fallback(0, new Retry(3, down)).stock("SKU-1"), 0);
        print("case 3b calls    ", down.calls, 3);

        // Case 4: tricky. Same two wrappers, other order: the fallback swallows the
        // first error, so Retry sees a success and never tries again.
        RemoteInventory blip = new RemoteInventory(2);
        print("case 4a wrong way", new Retry(3, new Fallback(0, blip)).stock("SKU-1"), 0);
        print("case 4b calls    ", blip.calls, 1);

        // Case 5: logging INSIDE retry sees every attempt.
        Logging logged = new Logging(new RemoteInventory(2));
        new Retry(3, logged).stock("SKU-1");
        print("case 5 log       ", logged.log, "[fail, fail, ok 42]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
