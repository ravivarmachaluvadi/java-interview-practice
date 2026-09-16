/**
 * The Facade Pattern is a Structural Design Pattern that
 * <p>
 * provides a simplified interface to a complex
 * <p>
 * subsystem of classes, libraries, or frameworks.
 * <p>
 * Think of it like this:
 * <p>
 * You interact with a single, simple “front desk”
 * <p>
 * instead of talking to every department in a company.
 */
//Why Design Patterns Matter
//Improves code readability and team communication
//Encourages best practices and loose coupling
//Makes code more flexible, scalable, and testable
//Helps avoid reinventing the wheel
class UserService {
    public boolean validateUser(String userId) {
        System.out.println("Validating user : " + userId);
        return true;
    }
}

class InventoryService {
    public boolean checkStock(String productId) {
        System.out.println("Checking inventory for productId : " + productId);
        return true;
    }
}

class PaymentGateway {
    public boolean makePayment(String userId, double amount) {
        System.out.println("Processing payment of Rs : " + amount + " for userId " + userId);
        return true;
    }
}

class NotificationService {
    public void sendNotification(String userId, String message) {
        System.out.println("Sending notification for user " + userId + " : " + message);
    }
}

class PaymentServiceFacade {
    private final UserService userService;
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    public PaymentServiceFacade() {
        this.userService = new UserService();
        this.inventoryService = new InventoryService();
        this.paymentGateway = new PaymentGateway();
        this.notificationService = new NotificationService();
    }

    public void processOrder(String userId, double amount, String productId) {
        System.out.println("Starting Payment Process");

        if (!userService.validateUser(userId)) {
            System.out.println("User is not valid");
            return;
        }
        if (!inventoryService.checkStock(productId)) {
            System.out.println("Product out of stock ");
            return;
        }
        if (!paymentGateway.makePayment(userId, amount)) {
            System.out.println("Payment processing failed");
            return;
        }

        notificationService.sendNotification(userId, "Order processed successfully");
    }
}

class FacadePatternExample {

    public static void main(String[] args) {
        PaymentServiceFacade facade = new PaymentServiceFacade();
        facade.processOrder("user123", 123.0, "675");
    }

}
