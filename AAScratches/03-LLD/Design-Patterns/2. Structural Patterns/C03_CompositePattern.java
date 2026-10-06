/*
 * =====================================================================
 *  Composite - product bundles inside an order          Structural | Medium
 * =====================================================================
 *
 * PROBLEM
 *   An order holds single products and bundles ("Work Combo" = laptop +
 *   headset + mouse), and a bundle can contain another bundle. Totals,
 *   invoices and display must work at any depth without "if (isBundle)".
 *
 * KEY INSIGHT
 *   Product (leaf) and ProductBundle (composite) implement the same
 *   OrderItem, so a bundle can hold bundles. getPrice() on a bundle sums its
 *   children and the recursion stops at the leaves - no depth parameter, no
 *   casts. Summing with identity 0.0 keeps an empty bundle from crashing.
 *
 * ROLES IN THIS CODE
 *   OrderItem         Component - getPrice() + showDetails()
 *   Product           Leaf
 *   ProductBundle     Composite - holds List<OrderItem>
 *   CompositePattern  Client - calls only OrderItem
 *
 * INTERVIEW FOLLOW-UPS
 *   - Safe vs transparent: add()/remove() only on the composite (this file)
 *     or on OrderItem so leaves must throw?
 *   - Add a bundle discount: one field on ProductBundle, nothing else moves.
 *   - Seen in: category trees, file systems, UI component trees, Swing
 *     Container.
 *
 * RUN
 *   4 cases: nested order total, single leaf, empty bundle, total after a
 *   remove; then prints the whole tree.
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
