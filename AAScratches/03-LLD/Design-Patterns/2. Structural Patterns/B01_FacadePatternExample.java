/*
 * =====================================================================
 *  Facade Pattern                                  Design Pattern | Easy
 * =====================================================================
 *
 * PATTERN
 *   Facade - Structural family.
 *
 * INTENT
 *   Put one simple entry point in front of a messy subsystem. The client
 *   makes a single call ("process this order") instead of knowing the four
 *   services involved, their order, and how to handle each failure.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    - a subsystem has many classes and callers only need one workflow.
 *   Use    - you want to decouple callers from a library you may later swap.
 *   Use    - you are writing a service layer over repositories/clients: that
 *            is a Facade, whether or not anyone calls it one.
 *   Not    - callers genuinely need the fine-grained API; a facade then just
 *            hides power without removing complexity.
 *   Not    - you need to make an incompatible interface fit -> Adapter.
 *   Not    - you need to control access to ONE object -> Proxy.
 *   Note   - a facade does not forbid direct subsystem use; it is convenience,
 *            not encapsulation. If you must forbid it, use module boundaries.
 *
 * ROLES IN THIS CODE
 *   PaymentServiceFacade   Facade     - single processOrder() entry point.
 *   UserService            Subsystem  - validates the user.
 *   InventoryService       Subsystem  - checks stock.
 *   PaymentGateway         Subsystem  - takes the money.
 *   NotificationService    Subsystem  - tells the user.
 *   FacadePatternExample   Client     - knows only the facade.
 *
 * KEY INSIGHT
 *   Facade is the degenerate case of delegation: it aggregates several
 *   collaborators, and it does NOT share an interface with any of them.
 *   That is exactly what tells it apart from Adapter, Decorator and Proxy,
 *   which all implement the same interface as the thing they wrap.
 *   One facade call in, many subsystem calls out.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Facade vs Adapter: both delegate. What is the real difference? (Intent:
 *     simplify many vs convert one. Facade defines a NEW interface.)
 *   - Facade vs Mediator: who knows whom? (Subsystem is unaware of a facade;
 *     Mediator's colleagues know their mediator.)
 *   - How would you make this testable? (Inject the four services rather than
 *     newing them in the constructor - shown as the second constructor here.)
 *   - Is a Spring @Service over repositories a facade? Why or why not?
 *
 * RUN
 *   main() runs 4 cases: a successful order, an out-of-stock order, an
 *   unknown user (early exit) and a zero amount, printing actual vs expected.
 */

class UserService {
    /** Demo rule: any id starting with "user" is a known customer. */
    public boolean validateUser(String userId) {
        System.out.println("  validating user : " + userId);
        return userId != null && userId.startsWith("user");
    }
}

class InventoryService {
    /** Demo rule: the product id "OUT" is the one we never have in stock. */
    public boolean checkStock(String productId) {
        System.out.println("  checking inventory for productId : " + productId);
        return !"OUT".equals(productId);
    }
}

class PaymentGateway {
    /** Demo rule: a non-positive amount is rejected by the gateway. */
    public boolean makePayment(String userId, double amount) {
        System.out.println("  processing payment of Rs " + amount + " for userId " + userId);
        return amount > 0;
    }
}

class NotificationService {
    public void sendNotification(String userId, String message) {
        System.out.println("  notifying user " + userId + " : " + message);
    }
}

/**
 * The Facade. Clients call processOrder(); they never touch the four services,
 * do not know the required order of the steps, and do not repeat the checks.
 */
class PaymentServiceFacade {

    private final UserService userService;
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    public PaymentServiceFacade() {
        this(new UserService(), new InventoryService(), new PaymentGateway(), new NotificationService());
    }

    /** Injected form: same facade, but the subsystem can be stubbed in a test. */
    public PaymentServiceFacade(UserService userService,
                                InventoryService inventoryService,
                                PaymentGateway paymentGateway,
                                NotificationService notificationService) {
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    /**
     * One call the client cares about. Returns a status string instead of void
     * so the demo (and a test) can assert the outcome, not just read the log.
     */
    public String processOrder(String userId, double amount, String productId) {
        System.out.println("  starting payment process");

        if (!userService.validateUser(userId)) {
            return "INVALID_USER";
        }
        if (!inventoryService.checkStock(productId)) {
            return "OUT_OF_STOCK";
        }
        if (!paymentGateway.makePayment(userId, amount)) {
            return "PAYMENT_FAILED";
        }
        notificationService.sendNotification(userId, "Order processed successfully");
        return "SUCCESS";
    }
}

class FacadePatternExample {

    public static void main(String[] args) {
        PaymentServiceFacade facade = new PaymentServiceFacade();

        // Case 1: typical - everything succeeds, all four services are used.
        print("case 1 happy path  ", facade.processOrder("user123", 123.0, "P675"), "SUCCESS");

        // Case 2: edge - stock check fails, so payment is never attempted.
        print("case 2 out of stock", facade.processOrder("user123", 123.0, "OUT"), "OUT_OF_STOCK");

        // Case 3: edge - unknown user, the facade exits on the very first step.
        print("case 3 bad user    ", facade.processOrder("ghost9", 123.0, "P675"), "INVALID_USER");

        // Case 4: tricky - valid user and stock, but the gateway rejects Rs 0.
        print("case 4 zero amount ", facade.processOrder("user123", 0.0, "P675"), "PAYMENT_FAILED");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " : " + actual + "   expected " + expected);
        System.out.println();
    }
}
