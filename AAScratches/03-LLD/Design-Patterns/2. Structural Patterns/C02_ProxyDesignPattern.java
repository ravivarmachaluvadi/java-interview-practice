/*
 * =====================================================================
 *  Proxy - cached, admin-only product catalog          Structural | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Every product page reads a price from the catalog DB, so a sale spike
 *   floods the DB with the same few reads. Price changes must be limited to
 *   admins and must never leave an old price in a cache. Callers should
 *   not change: they still just call ProductCatalog.
 *
 * KEY INSIGHT
 *   The proxy implements the same interface as the real catalog and stands
 *   in front of it, deciding whether and when the real one is touched:
 *   serve reads from a cache, refuse non-admin writes, evict on write.
 *   Spring builds exactly this at runtime for @Cacheable, @Transactional
 *   and @PreAuthorize, using a JDK dynamic proxy (case 4).
 *
 * ROLES IN THIS CODE
 *   ProductCatalog    Subject
 *   DbCatalog         RealSubject (counts DB reads)
 *   CatalogProxy      Proxy: caching + protection + invalidation
 *   loggingProxy()    the same idea built at runtime by java.lang.reflect.Proxy
 *
 * INTERVIEW FOLLOW-UPS
 *   - Proxy vs Decorator: same shape; a proxy CONTROLS access to one object
 *     (often creating it), a decorator ADDS behaviour and is handed it.
 *   - Self-invocation: this.price() inside the real bean skips the Spring
 *     proxy, so @Cacheable on it silently does nothing.
 *   - Other kinds: lazy (Hibernate entities), remote (gRPC stubs).
 *
 * RUN
 *   4 cases: second read cached, non-admin write refused, admin write
 *   evicts the cache, dynamic proxy records every call.
 */

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

interface ProductCatalog {
    int price(String sku);

    void updatePrice(String user, String sku, int priceRs);
}

class DbCatalog implements ProductCatalog {
    private final Map<String, Integer> table = new HashMap<>(Map.of("PHONE-15", 79_900));
    int dbReads;

    public int price(String sku) {
        dbReads++;
        return table.get(sku);
    }

    public void updatePrice(String user, String sku, int priceRs) {
        table.put(sku, priceRs);
    }
}

class CatalogProxy implements ProductCatalog {
    private final ProductCatalog real;
    private final Set<String> admins;
    private final Map<String, Integer> cache = new HashMap<>();

    CatalogProxy(ProductCatalog real, Set<String> admins) {
        this.real = real;
        this.admins = admins;
    }

    public int price(String sku) {
        return cache.computeIfAbsent(sku, real::price); // DB only on a cache miss
    }

    public void updatePrice(String user, String sku, int priceRs) {
        if (!admins.contains(user)) {
            throw new SecurityException(user + " may not change prices");
        }
        real.updatePrice(user, sku, priceRs);
        cache.remove(sku); // never serve the old price after a write
    }
}

class ProxyDesignPattern {

    /** What Spring AOP does: a proxy class generated at runtime for an interface. */
    static ProductCatalog loggingProxy(ProductCatalog target, List<String> calls) {
        return (ProductCatalog) Proxy.newProxyInstance(
                ProductCatalog.class.getClassLoader(),
                new Class<?>[]{ProductCatalog.class},
                (proxy, method, args) -> {
                    calls.add(method.getName()); // "before advice"
                    return method.invoke(target, args);
                });
    }

    public static void main(String[] args) {
        DbCatalog db = new DbCatalog();
        ProductCatalog catalog = new CatalogProxy(db, Set.of("admin"));

        // Case 1: typical. Two reads, one DB hit.
        catalog.price("PHONE-15");
        print("case 1a price   ", catalog.price("PHONE-15"), 79900);
        print("case 1b db reads", db.dbReads, 1);

        // Case 2: protection. A non-admin write never reaches the DB.
        String outcome;
        try {
            catalog.updatePrice("guest", "PHONE-15", 1);
            outcome = "updated";
        } catch (SecurityException e) {
            outcome = e.getMessage();
        }
        print("case 2a guest   ", outcome, "guest may not change prices");
        print("case 2b price   ", catalog.price("PHONE-15"), 79900);

        // Case 3: tricky. The write evicts the cache, so the next read is fresh.
        catalog.updatePrice("admin", "PHONE-15", 74_900);
        print("case 3a new     ", catalog.price("PHONE-15"), 74900);
        print("case 3b db reads", db.dbReads, 2);

        // Case 4: a runtime-generated proxy in front of the hand-written one.
        List<String> calls = new ArrayList<>();
        ProductCatalog logged = loggingProxy(catalog, calls);
        logged.price("PHONE-15");
        logged.updatePrice("admin", "PHONE-15", 69_900);
        print("case 4a calls   ", calls, "[price, updatePrice]");
        print("case 4b class   ", logged.getClass().getSimpleName().startsWith("$Proxy"), true);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
