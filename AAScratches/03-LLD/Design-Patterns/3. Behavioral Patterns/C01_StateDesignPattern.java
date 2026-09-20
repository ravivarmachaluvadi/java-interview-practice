/*
 * =====================================================================
 *  State Pattern - e-commerce order lifecycle        Behavioral  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   State (Behavioral, GoF). The "objects as a finite state machine" pattern.
 *
 * INTENT
 *   Let an object change its behaviour when its internal state changes, so
 *   that it appears to change its class. Every state becomes its own class;
 *   the operation you call is dispatched to whichever state is current.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    : any lifecycle with rules about what is legal next - order
 *            (NEW/PAID/SHIPPED/DELIVERED/CANCELLED), vending machine,
 *            elevator, ATM, document approval, TCP connection.
 *            The giveaway is a method full of "if (status == X) ... else if".
 *   Not    : two states and one flag. A boolean plus an if is clearer than
 *            five classes. Also not when transitions are driven by an
 *            external table you would rather configure than compile.
 *
 * ROLES IN THIS CODE
 *   OrderState     State          - the interface every state implements.
 *   NewState,      ConcreteState  - each owns its own behaviour AND decides
 *   PaidState,                      its own successor via order.setState().
 *   ShippedState,
 *   DeliveredState,
 *   CancelledState                  (DELIVERED and CANCELLED are terminal:
 *                                    they refuse every transition.)
 *   Order          Context        - holds the current state and forwards
 *                                   next()/cancel()/printStatus() to it.
 *   main           Client         - only ever talks to the Order.
 *
 * KEY INSIGHT
 *   The transition table lives INSIDE the states, not in the context. A
 *   state does two things: behave, and name its successor. Adding a
 *   RETURNED state means adding one class, not editing a switch in five
 *   places - that is Open/Closed applied to a state machine.
 *   Illegal moves are handled by a state simply not transitioning.
 *
 * INTERVIEW FOLLOW-UPS
 *   - State vs Strategy? Same class shape. Strategy is chosen by the client
 *     and never changes itself; State swaps itself and knows its neighbours.
 *   - Where do you persist this? Store the state NAME in the DB and rebuild
 *     the state object on load (a small enum/map factory).
 *   - States are stateless here - make them singletons/enum constants to
 *     stop allocating a new object on every transition.
 *   - How do you audit transitions? Wrap setState() in the context and log
 *     from/to; the states stay untouched.
 *
 * RUN
 *   main() runs 3 cases (full happy path, cancel from NEW, illegal cancel
 *   after shipping) and prints the resulting state actual vs expected.
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
