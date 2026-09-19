import java.util.ArrayList;
import java.util.List;

/**
 * Composite Pattern is a structural design pattern that lets you
 * <p>
 * treat a group of objects (composite) and a single object (leaf) uniformly.
 * <p>
 * ⚙️ When to Use Composite Pattern
 * <p>
 * ✅ When you want to represent part-whole hierarchies (tree-like structures)
 * <p>
 * ✅ When you want clients to treat individual objects and groups uniformly
 * <p>
 * ✅ When adding new leaf or composite elements should not affect existing code
 */



// ✅ Step 4: Demo
class CompositePattern {
    public static void main(String[] args) {
        // Laptop combo
        Product laptop = new Product(250_000.0d, "MacBook Pro");
        Product headset = new Product(25000.0, "Airpods Pro");
        Product mouse = new Product(10000.0, "Magic Mouse");
        Product keyboard = new Product(10000.0, "Magic Keyboard");

        ProductBundle workCombo = new ProductBundle("Work Combo");
        workCombo.addItem(laptop);
        workCombo.addItem(headset);
        workCombo.addItem(mouse);
        workCombo.addItem(keyboard);

        // Phone Combo
        Product phone = new Product(240000, "17Pro Max");
        Product pouch = new Product(4500, "Silicon Pouch");
        ProductBundle phoneCombo = new ProductBundle("Phone Combo");
        phoneCombo.addItem(phone);
        phoneCombo.addItem(pouch);

        ProductBundle completeOrder = new ProductBundle("Complete Order");
        completeOrder.addItem(workCombo);
        completeOrder.addItem(phoneCombo);

        completeOrder.showDetails();
    }
}

// ✅ Step 1: Component Interface
interface OrderItem {
    double getPrice();

    void showDetails();
}

// ✅ Step 2: Leaf Class → Single Product
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
    public void showDetails() {
        System.out.println("- " + name + ": ₹" + price);
    }
}

// ✅ Step 3: Composite Class → Product Bundle
class ProductBundle implements OrderItem {

    private final String name;
    List<OrderItem> items = new ArrayList<>();

    public ProductBundle(String name) {
        this.name = name;
    }

    public void addItem(OrderItem orderItem) {
        items.add(orderItem);
    }

    public void removeItem(OrderItem orderItem) {
        items.remove(orderItem);
    }

    @Override
    public double getPrice() {
        return items.stream().map(OrderItem::getPrice).reduce(Double::sum).get();
    }

    @Override
    public void showDetails() {
        System.out.println("Displaying bundle : " + name);
        for (OrderItem item : items) {
            item.showDetails();
        }
        System.out.println("→ Total for " + name + ": ₹" + getPrice());
    }
}
