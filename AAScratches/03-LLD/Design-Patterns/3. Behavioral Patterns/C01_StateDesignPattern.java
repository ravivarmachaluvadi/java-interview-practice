/*
 * =====================================================================
 *  State - e-commerce order lifecycle        Behavioral | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   An order moves NEW -> PAID -> SHIPPED -> DELIVERED and can be cancelled
 *   only before shipping (with a refund once paid). Written as
 *   "if (status == ...)" in every method, each new state means editing
 *   every branch.
 *
 * KEY INSIGHT
 *   Each state is a class that knows its own rules AND names its successor
 *   via order.setState(). The Order just forwards next()/cancel() to its
 *   current state. A RETURNED state is one new class; an illegal move is a
 *   state that simply does not transition.
 *
 * ROLES IN THIS CODE
 *   OrderState                      State
 *   NewState, PaidState, Shipped..  ConcreteState (Delivered and Cancelled
 *                                   are terminal)
 *   Order                           Context
 *
 * INTERVIEW FOLLOW-UPS
 *   - State vs Strategy: same shape; the client picks a strategy, a state
 *     picks its own successor.
 *   - Persist the state NAME in the DB and rebuild the object on load.
 *   - States hold no data here, so enum constants would avoid allocating
 *     one per transition.
 *
 * RUN
 *   3 cases: full happy path, cancel from NEW, cancel from PAID vs SHIPPED.
 */

/** State: every lifecycle step implements this. */
interface OrderState {
    /** Advance to the next legal state, or refuse if terminal. */
    void next(Order order);

    /** Cancel if this state allows it, or refuse. */
    void cancel(Order order);

    /** Short stable name, used for asserting and for persistence. */
    String name();

    default void printState() {
        System.out.println("  [status] order is " + name());
    }
}

class NewState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("  Payment received. Moving to PAID.");
        order.setState(new PaidState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Order cancelled before payment.");
        order.setState(new CancelledState());
    }

    @Override
    public String name() {
        return "NEW";
    }
}

class PaidState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("  Order shipped. Moving to SHIPPED.");
        order.setState(new ShippedState());
    }

    @Override
    public void cancel(Order order) {
        // Money already taken, so cancelling here has a side effect.
        System.out.println("  Refund initiated. Order cancelled.");
        order.setState(new CancelledState());
    }

    @Override
    public String name() {
        return "PAID";
    }
}

class ShippedState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("  Delivered to customer. Moving to DELIVERED.");
        order.setState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        // Refusing = doing nothing. No setState call, so the state stands.
        System.out.println("  Cannot cancel: parcel is already in transit.");
    }

    @Override
    public String name() {
        return "SHIPPED";
    }
}

/** Terminal state: refuses every transition. */
class DeliveredState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("  Already delivered. No next state.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Cannot cancel: order already delivered.");
    }

    @Override
    public String name() {
        return "DELIVERED";
    }
}

/** Terminal state: refuses every transition. */
class CancelledState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("  Order is cancelled. No further transitions.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Order is already cancelled.");
    }

    @Override
    public String name() {
        return "CANCELLED";
    }
}

/** Context: owns the current state and delegates every request to it. */
class Order {
    private OrderState orderState = new NewState();

    /** Called by the states themselves - this is where transitions land. */
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

    public String currentState() {
        return orderState.name();
    }
}

class StateDesignPatternExample {

    public static void main(String[] args) {
        // Case 1: typical - walk the whole happy path, then try to cancel.
        System.out.println("case 1: happy path");
        Order order = new Order();
        print("  1a start      ", order.currentState(), "NEW");
        order.next();
        print("  1b after pay  ", order.currentState(), "PAID");
        order.next();
        print("  1c after ship ", order.currentState(), "SHIPPED");
        order.next();
        print("  1d delivered  ", order.currentState(), "DELIVERED");
        order.cancel(); // refused
        print("  1e cancel late", order.currentState(), "DELIVERED");

        // Case 2: edge - cancel straight from NEW, then a terminal state
        // must ignore both next() and a second cancel().
        System.out.println("case 2: cancel before payment");
        Order early = new Order();
        early.cancel();
        print("  2a cancelled  ", early.currentState(), "CANCELLED");
        early.next();
        print("  2b next after ", early.currentState(), "CANCELLED");
        early.cancel();
        print("  2c cancel twice", early.currentState(), "CANCELLED");

        // Case 3: tricky - cancel IS allowed from PAID (refund) but not
        // from SHIPPED, so the same call has two different outcomes.
        System.out.println("case 3: cancel from PAID vs from SHIPPED");
        Order paid = new Order();
        paid.next();
        paid.cancel();
        print("  3a paid-cancel", paid.currentState(), "CANCELLED");

        Order shipped = new Order();
        shipped.next();
        shipped.next();
        shipped.cancel();
        shipped.printStatus();
        print("  3b ship-cancel", shipped.currentState(), "SHIPPED");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
