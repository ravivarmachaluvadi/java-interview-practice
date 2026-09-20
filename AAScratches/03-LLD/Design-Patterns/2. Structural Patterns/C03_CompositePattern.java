/*
 * =====================================================================
 *  Composite Pattern                                Design Pattern | Medium
 * =====================================================================
 *
 * PATTERN
 *   Composite - Structural family. Part-whole tree.
 *
 * INTENT
 *   Let a client treat a single object (leaf) and a group of objects
 *   (composite) through the same interface, so code that totals an order
 *   does not care whether it was handed one product or a bundle of bundles.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    - the data really is a part-whole tree: files/folders, menu items,
 *            org charts, UI components, order bundles, nested discounts.
 *   Use    - clients should recurse without writing "if (isGroup)" anywhere.
 *   Not    - the structure is flat, or leaves and groups need genuinely
 *            different APIs - forcing one interface then makes it lie.
 *   Not    - you only ever wrap ONE object at a time -> Decorator.
 *   Watch  - cycles. A tree assumes no child is also its own ancestor.
 *
 * ROLES IN THIS CODE
 *   OrderItem       Component  - getPrice() + showDetails(), the shared view.
 *   Product         Leaf       - a single item; getPrice() returns its price.
 *   ProductBundle   Composite  - holds List<OrderItem> children; getPrice()
 *                                sums the children, so it recurses.
 *   CompositePattern Client    - builds the tree and calls only OrderItem.
 *
 * KEY INSIGHT
 *   A composite is the decorator chain generalised from a line to a tree: the
 *   node holds MANY children instead of one wrappee, and because a composite
 *   is itself a Component, bundles nest to any depth. The recursion ends at
 *   the leaves, which is why getPrice() needs no depth parameter and no cast.
 *   Fixed: getPrice() used reduce(Double::sum).get(), which threw
 *   NoSuchElementException on an empty bundle. It now sums with identity 0.0.
 *
 * COMPLEXITY
 *   Time  O(n) per getPrice()/showDetails() over the whole subtree, n = nodes.
 *   Space O(h) call-stack for depth h (plus O(n) for the tree itself).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Transparency vs safety: should add()/remove() live on the Component
 *     (uniform, but a Leaf must throw) or only on the Composite (type-safe,
 *     but the client must downcast)? This file uses the SAFE variant.
 *   - How would you cache a bundle total and invalidate it on add/remove?
 *   - Composite + Visitor: how do you add "apply a discount" without editing
 *     every node class?
 *   - Where does the JDK/Java world use it? (Swing Container, java.io.File,
 *     Spring Security's filter chain, nested Maven modules.)
 *
 * RUN
 *   main() runs 4 cases: a nested order total, a leaf on its own, an empty
 *   bundle (the old crash), and a bundle after removeItem(). Prints actual vs
 *   expected, then dumps the tree with showDetails().
 */

import java.util.ArrayList;
import java.util.List;

/** Component: the single type the client codes against, leaf or group. */
interface OrderItem {

    double getPrice();

    /** Print this node (and any children) with the given left indent. */
    void showDetails(String indent);

    /** Convenience entry point for the client. */
    default void showDetails() {
        showDetails("");
    }
}

/** Leaf: an individual product. It has no children, so the recursion stops here. */
class Product implements OrderItem {

    private final double price;
    private final String name;

    public Product(double price, String name) {
        this.price = price;
        this.name = name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "- " + name + ": Rs " + price);
    }
}

/**
 * Composite: a bundle of OrderItems. Because a bundle IS an OrderItem, a
 * bundle can contain bundles, which is what makes the structure a tree.
 */
class ProductBundle implements OrderItem {

    private final String name;
    private final List<OrderItem> items = new ArrayList<>();

    public ProductBundle(String name) {
        this.name = name;
    }

    // add/remove live only on the composite: the "safe" variant of the pattern.
    public void addItem(OrderItem orderItem) {
        items.add(orderItem);
    }

    public void removeItem(OrderItem orderItem) {
        items.remove(orderItem);
    }

    @Override
    public double getPrice() {
        // Identity 0.0 matters: an empty bundle must cost 0, not blow up.
        return items.stream().mapToDouble(OrderItem::getPrice).sum();
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "Bundle: " + name);
        for (OrderItem item : items) {
            item.showDetails(indent + "  "); // same call on leaves and bundles
        }
        System.out.println(indent + "= Total for " + name + ": Rs " + getPrice());
    }
}

class CompositePattern {

    public static void main(String[] args) {
        Product laptop = new Product(250_000.0, "MacBook Pro");
        Product headset = new Product(25_000.0, "Airpods Pro");
        Product mouse = new Product(10_000.0, "Magic Mouse");
        Product keyboard = new Product(10_000.0, "Magic Keyboard");

        ProductBundle workCombo = new ProductBundle("Work Combo");
        workCombo.addItem(laptop);
        workCombo.addItem(headset);
        workCombo.addItem(mouse);
        workCombo.addItem(keyboard);

        Product phone = new Product(240_000.0, "17 Pro Max");
        Product pouch = new Product(4_500.0, "Silicon Pouch");
        ProductBundle phoneCombo = new ProductBundle("Phone Combo");
        phoneCombo.addItem(phone);
        phoneCombo.addItem(pouch);

        // A bundle of bundles: two levels deep, and the client never notices.
        ProductBundle completeOrder = new ProductBundle("Complete Order");
        completeOrder.addItem(workCombo);
        completeOrder.addItem(phoneCombo);

        // Case 1: typical - the whole nested tree.
        print("case 1 nested order ", completeOrder.getPrice(), 539500.0);

        // Case 2: edge - a leaf answers the same call with no recursion.
        print("case 2 single leaf  ", laptop.getPrice(), 250000.0);

        // Case 3: edge - an empty bundle. This used to throw NoSuchElementException.
        ProductBundle emptyGiftBox = new ProductBundle("Empty Gift Box");
        print("case 3 empty bundle ", emptyGiftBox.getPrice(), 0.0);

        // Case 4: tricky - remove a child; the parent total updates by recursion.
        workCombo.removeItem(mouse);
        print("case 4 after remove ", completeOrder.getPrice(), 529500.0);

        System.out.println();
        completeOrder.showDetails(); // one call renders the entire tree
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " : " + actual + "   expected " + expected);
    }
}
