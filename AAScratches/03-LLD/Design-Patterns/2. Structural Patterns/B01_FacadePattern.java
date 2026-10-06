/*
 * =====================================================================
 *  Facade - one placeOrder() over four services        Structural | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Placing an order touches four services in a fixed order: validate the
 *   user, check stock, take payment, notify. Every caller (web, mobile app,
 *   admin tool) repeating that sequence and its early exits is a bug factory.
 *
 * KEY INSIGHT
 *   The facade owns the workflow: one call in, many subsystem calls out. It
 *   does NOT share an interface with what it wraps - that is what separates
 *   it from Adapter, Decorator and Proxy. The services do not know it exists.
 *
 * ROLES IN THIS CODE
 *   PaymentServiceFacade            Facade - processOrder() entry point
 *   UserService, InventoryService,
 *   PaymentGateway, Notification..  Subsystem
 *   FacadePatternExample            Client - knows only the facade
 *
 * INTERVIEW FOLLOW-UPS
 *   - Facade vs Adapter: simplify many vs convert one interface.
 *   - What if payment fails after stock was reserved? The facade is where
 *     the compensating step (release stock) belongs.
 *   - A Spring @Service over several clients/repositories is a facade; the
 *     second constructor here is how you inject fakes in a test.
 *
 * RUN
 *   4 cases: success, out of stock, unknown user, gateway rejects Rs 0.
 */

class UserService {
    /**
     * Demo rule: any id starting with "user" is a known customer.
     */
    public boolean validateUser(String userId) {
        System.out.println("  validating user : " + userId);
        return userId != null && userId.startsWith("user");
    }
}

class InventoryService {
    /**
     * Demo rule: the product id "OUT" is the one we never have in stock.
     */
    public boolean checkStock(String productId) {
        System.out.println("  checking inventory for productId : " + productId);
        return !"OUT".equals(productId);
    }
}

class PaymentGateway {
    /**
     * Demo rule: a non-positive amount is rejected by the gateway.
     */
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
        this(
                new UserService(),
                new InventoryService(),
                new PaymentGateway(),
                new NotificationService()
        );
    }

    /**
     * Injected form: same facade, but the subsystem can be stubbed in a test.
     */
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
