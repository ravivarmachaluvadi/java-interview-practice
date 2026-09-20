/*
 * =====================================================================
 *  Proxy Pattern                           Design Pattern | Medium  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Proxy - Structural family. Flavours: protection (shown here), virtual /
 *   lazy-loading (also shown here), remote, caching, logging.
 *
 * INTENT
 *   Stand in for another object so you can control access to it. The client
 *   holds the proxy, believes it is the real thing, and never sees that a
 *   permission check ran or that the real object did not exist yet.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    - access control: authorise before the call reaches the real object.
 *   Use    - lazy init: the real object is expensive, build it on first use.
 *   Use    - cross-cutting work (logging, metrics, caching, retries, tx) that
 *            must not be written inside the real class.
 *   Use    - the real object lives elsewhere (remote proxy / RPC stub).
 *   Not    - you want to ADD features the client asked for -> Decorator.
 *   Not    - you want to change the interface -> Adapter.
 *   Not    - you want to hide a whole subsystem -> Facade.
 *
 * ROLES IN THIS CODE
 *   Bank                Subject      - the interface both sides implement.
 *   RealBank            RealSubject  - does the actual work; knows nothing
 *                                      about the proxy or about permissions.
 *   BankProxy           Proxy        - implements Bank, owns the RealBank,
 *                                      checks the allow-list, creates the real
 *                                      object lazily on the first allowed call.
 *   ProxyDesignPattern  Client       - holds a Bank reference, never a RealBank.
 *
 * KEY INSIGHT
 *   Structurally a proxy is identical to a decorator: same interface, holds one
 *   wrappee, delegates. The difference is intent and LIFECYCLE - a decorator is
 *   handed an already-built object to enrich; a proxy OWNS the real object,
 *   decides when it is created, and may decide the call never reaches it.
 *   That is exactly how Spring AOP proxies and Hibernate lazy loading work.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Proxy vs Decorator vs Adapter vs Facade: four wrappers, one table.
 *   - How does Spring build proxies? (JDK dynamic proxy for interfaces,
 *     CGLIB subclass for classes - and why self-invocation skips the proxy.)
 *   - Why does a Hibernate lazy collection throw LazyInitializationException?
 *   - Make BankProxy thread-safe: where does the double-checked lock go, and
 *     why must the field be volatile?
 *
 * RUN
 *   main() runs 5 cases: the unloaded state, an allowed withdrawal, a denied
 *   one, a second allowed one proving the RealBank is built once, and a null
 *   account id. Every case prints actual vs expected.
 */

import java.util.Arrays;
import java.util.List;

/** Subject: the only type the client programs against. */
interface Bank {
    String withdraw(String accountNumber);
}

/**
 * RealSubject. Pretend this is expensive to build (opens a connection pool),
 * which is why the proxy defers creating it.
 */
class RealBank implements Bank {

    static int instancesCreated = 0; // demo-only counter, to prove lazy init

    RealBank() {
        instancesCreated++;
        System.out.println("  [RealBank] expensive real object constructed");
    }

    @Override
    public String withdraw(String accountNumber) {
        System.out.println("  [RealBank] withdrawing money from account: " + accountNumber);
        return "WITHDRAWN:" + accountNumber;
    }
}

/**
 * Proxy: same interface as RealBank, so the client cannot tell them apart.
 * It adds two things the RealBank does not know about - an allow-list check
 * and lazy creation of the real object.
 */
class BankProxy implements Bank {

    private static final List<String> ALLOWED_ACCOUNTS = Arrays.asList("ACC123", "ACC456");

    private RealBank realBank; // stays null until the first authorised call

    @Override
    public String withdraw(String accountNumber) {
        if (!ALLOWED_ACCOUNTS.contains(accountNumber)) {
            System.out.println("  [Proxy] access denied for account: " + accountNumber);
            return "DENIED"; // the call never reaches the real object
        }
        System.out.println("  [Proxy] access granted for account: " + accountNumber);
        if (realBank == null) {
            realBank = new RealBank(); // lazy init: built once, on first real use
        }
        return realBank.withdraw(accountNumber);
    }

    /** Demo helper: has the real object been built yet? */
    boolean isRealSubjectLoaded() {
        return realBank != null;
    }
}

class ProxyDesignPattern {

    public static void main(String[] args) {
        BankProxy proxy = new BankProxy();
        Bank bank = proxy; // the client only ever sees the Bank interface

        // Case 1: edge - before any call, the expensive object does not exist.
        print("case 1 not loaded yet", proxy.isRealSubjectLoaded(), false);

        // Case 2: typical - allowed account, so the proxy delegates.
        print("case 2 allowed       ", bank.withdraw("ACC123"), "WITHDRAWN:ACC123");
        print("case 2 instances     ", RealBank.instancesCreated, 1);

        // Case 3: edge - denied account. The proxy answers alone.
        print("case 3 denied        ", bank.withdraw("ACC128"), "DENIED");

        // Case 4: tricky - a second allowed call REUSES the real object,
        // so the counter must still read 1, not 2.
        print("case 4 second allowed", bank.withdraw("ACC456"), "WITHDRAWN:ACC456");
        print("case 4 instances     ", RealBank.instancesCreated, 1);

        // Case 5: edge - a null account id is simply not on the allow-list.
        print("case 5 null account  ", bank.withdraw(null), "DENIED");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " : " + actual + "   expected " + expected);
    }
}
