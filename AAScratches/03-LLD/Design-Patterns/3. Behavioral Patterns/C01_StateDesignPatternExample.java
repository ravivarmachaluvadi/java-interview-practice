/**
 * Problem: Simulate an e‑commerce order lifecycle (NEW → PAID → SHIPPED → DELIVERED) with possible cancellation at any stage.
 *
 * Approach: Implement the State Design Pattern. Each state is a separate class implementing OrderState; transitions are handled by calling setState on the context Order object, avoiding large if/else chains.
 *
 * Complexity:
 *   Time – O(1) per operation (next, cancel, printStatus).
 *   Space – O(1) additional space; only one state instance is held at a time.
 */
interface OrderState {
    void next(Order order);

    void cancel(Order order);

    void printState();
}

/**
 * The State Design Pattern in Java is a behavioral design pattern
 * <p>
 * that allows an object to change its behavior when its internal
 * <p>
 * state changes. It’s like the object changes its class at
 * <p>
 * runtime — without using multiple if-else or switch statements
 * <p>
 * everywhere.
 * <p>
 * 💡 Intent
 * <p>
 * Allow an object to alter its behavior when its internal state
 * <p>
 * changes. The object will appear to change its class.
 */
class NewState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Payment done moving to paid state");
        order.setState(new PaidState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order cancelled successfully");
        order.setState(new CancelledState());
    }

    @Override
    public void printState() {
        System.out.println("Order is in NEW state");
    }
}

class PaidState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Order shipped. Moving to Shipped state.");
        order.setState(new ShippedState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Refund initiated. Order cancelled.");
        order.setState(new CancelledState());
    }

    @Override
    public void printState() {
        System.out.println("Order is PAID and ready for shipment.");
    }
}


class ShippedState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Order delivered successfully!");
        order.setState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Cannot cancel, order already shipped!");
    }

    @Override
    public void printState() {
        System.out.println("Order is SHIPPED.");
    }
}

class DeliveredState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Order already delivered. No next state.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Cannot cancel, order already delivered!");
    }

    @Override
    public void printState() {
        System.out.println("Order is DELIVERED.");
    }
}

class CancelledState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Order cancelled. No further transitions.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order already cancelled!");
    }

    @Override
    public void printState() {
        System.out.println("Order is CANCELLED.");
    }
}


class Order {
    OrderState orderState;

    public Order() {
        orderState = new NewState();
    }

    public void setState(OrderState state) {
        orderState = state;
    }

    public void next() {
        orderState.next(this);
    }

    public void cancel() {
        orderState.cancel(this);
    }

    public void printStatus() {
        orderState.printState();
    }
}

class StateDesignPatternExample {
    public static void main(String[] args) {
        Order order = new Order();

        order.printStatus();  // NEW
        order.next();         // Move to Paid
        order.printStatus();  // PAID
        order.next();         // Move to Shipped
        order.printStatus();  // SHIPPED
        order.next();         // Move to Delivered
        order.printStatus();  // DELIVERED

        // Try cancelling after delivery
        order.cancel();       // Not allowed
    }
}
