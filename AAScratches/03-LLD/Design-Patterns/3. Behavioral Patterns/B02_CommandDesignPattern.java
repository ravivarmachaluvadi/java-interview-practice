/*
 * =====================================================================
 *  Command - shopping cart undo / redo               Behavioral | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   The cart page has "Undo" after every action: add item, remove item,
 *   apply coupon. Users undo several steps and redo them; a fresh action
 *   after an undo must wipe the redo list. The undo button cannot contain
 *   a switch over every possible action.
 *
 * KEY INSIGHT
 *   Each action becomes an object that knows how to execute() AND undo()
 *   itself, remembering whatever it needs to reverse (the removed price,
 *   the old coupon). The history only pushes and pops commands; it never
 *   knows what they do. A new action is a new class, not a new branch.
 *
 * ROLES IN THIS CODE
 *   CartCommand                       Command
 *   AddItem, RemoveItem, ApplyCoupon  ConcreteCommand
 *   Cart                              Receiver
 *   CartHistory                       Invoker - undo and redo stacks
 *
 * INTERVIEW FOLLOW-UPS
 *   - Command vs Memento for undo: Command stores HOW to reverse a change,
 *     Memento stores a snapshot (B03_MementoDesignPattern).
 *   - Group several commands into one undo step with a MacroCommand.
 *   - Seen in: Runnable/Callable queued on an executor, job queues, Redux
 *     actions, database redo logs.
 *
 * RUN
 *   6 cases: three actions, undo twice, redo, new action clears redo,
 *   undo a remove, undo past the start.
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/** Receiver: the cart itself knows nothing about undo. */
class Cart {
    private final Map<String, Integer> lines = new LinkedHashMap<>(); // product -> price
    private int couponOff;

    void add(String product, int priceRs) {
        lines.put(product, priceRs);
    }

    Integer remove(String product) {
        return lines.remove(product);
    }

    int couponOff() {
        return couponOff;
    }

    void setCouponOff(int rs) {
        couponOff = rs;
    }

    @Override
    public String toString() {
        int total = lines.values().stream().mapToInt(Integer::intValue).sum() - couponOff;
        return lines.keySet() + " Rs " + total;
    }
}

interface CartCommand {
    void execute();

    void undo();
}

class AddItem implements CartCommand {
    private final Cart cart;
    private final String product;
    private final int priceRs;

    AddItem(Cart cart, String product, int priceRs) {
        this.cart = cart;
        this.product = product;
        this.priceRs = priceRs;
    }

    public void execute() {
        cart.add(product, priceRs);
    }

    public void undo() {
        cart.remove(product);
    }
}

class RemoveItem implements CartCommand {
    private final Cart cart;
    private final String product;
    private Integer removedPrice; // remembered so undo can put it back

    RemoveItem(Cart cart, String product) {
        this.cart = cart;
        this.product = product;
    }

    public void execute() {
        removedPrice = cart.remove(product);
    }

    public void undo() {
        if (removedPrice != null) {
            cart.add(product, removedPrice);
        }
    }
}

class ApplyCoupon implements CartCommand {
    private final Cart cart;
    private final int offRs;
    private int previousOff;

    ApplyCoupon(Cart cart, int offRs) {
        this.cart = cart;
        this.offRs = offRs;
    }

    public void execute() {
        previousOff = cart.couponOff();
        cart.setCouponOff(offRs);
    }

    public void undo() {
        cart.setCouponOff(previousOff);
    }
}

/** Invoker: pushes and pops commands, never looks inside them. */
class CartHistory {
    private final Deque<CartCommand> undoStack = new ArrayDeque<>();
    private final Deque<CartCommand> redoStack = new ArrayDeque<>();

    void run(CartCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // a new action invalidates the redo path
    }

    boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        CartCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }

    boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        CartCommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return true;
    }
}

class CommandDesignPattern {

    public static void main(String[] args) {
        Cart cart = new Cart();
        CartHistory history = new CartHistory();

        // Case 1: typical. Three actions.
        history.run(new AddItem(cart, "Shoes", 3000));
        history.run(new AddItem(cart, "Socks", 300));
        history.run(new ApplyCoupon(cart, 500));
        print("case 1 three actions", cart, "[Shoes, Socks] Rs 2800");

        // Case 2: undo twice - the coupon goes, then the socks.
        history.undo();
        history.undo();
        print("case 2 undo twice   ", cart, "[Shoes] Rs 3000");

        // Case 3: redo brings the socks back.
        history.redo();
        print("case 3 redo         ", cart, "[Shoes, Socks] Rs 3300");

        // Case 4: tricky. A new action after an undo clears the redo stack,
        // so the undone coupon can no longer be redone.
        history.run(new RemoveItem(cart, "Shoes"));
        print("case 4a remove shoes", cart, "[Socks] Rs 300");
        print("case 4b redo coupon ", history.redo(), false);

        // Case 5: undoing a remove restores the item with its price.
        history.undo();
        print("case 5 undo remove  ", cart, "[Socks, Shoes] Rs 3300");

        // Case 6: edge. Undo back to an empty cart, then one undo too many.
        print("case 6a undo x3     ", history.undo() + " " + history.undo() + " "
                + history.undo(), "true true false");
        print("case 6b empty cart  ", cart, "[] Rs 0");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
