/*
 * =====================================================================
 *  Visitor - tax, shipping, points over cart lines   Behavioral | Hard
 * =====================================================================
 *
 * PROBLEM
 *   A cart holds physical goods, e-books and gift cards. Tax, shipping and
 *   loyalty points each work differently per line type, and finance keeps
 *   asking for new calculations. Adding taxFor(), shippingFor(),
 *   pointsFor()... to every line class mixes unrelated rules into data.
 *
 * KEY INSIGHT
 *   Each calculation becomes one visitor class with a visit() per line
 *   type. A line's accept(v) calls v.visit(this), and because `this` has
 *   the line's real type, Java picks the right overload: double dispatch.
 *   New calculation = new visitor, line classes untouched. The price: a new
 *   LINE TYPE means editing every visitor.
 *
 * ROLES IN THIS CODE
 *   CartLine                              Element - accept(visitor)
 *   PhysicalItem, DigitalItem, GiftCard   ConcreteElement (records)
 *   LineVisitor                           Visitor
 *   TaxVisitor, ShippingVisitor, Points.. ConcreteVisitor
 *
 * INTERVIEW FOLLOW-UPS
 *   - Java 21: a sealed interface + switch with record patterns does the
 *     same job without accept() (case 4), and the compiler flags a missed
 *     type.
 *   - Seen in: javax.lang.model ElementVisitor, java.nio.file.FileVisitor,
 *     compiler ASTs.
 *
 * RUN
 *   5 cases: tax, shipping, a new points visitor, same tax via switch,
 *   empty cart. Tax rates here are illustrative, not legal advice.
 */

import java.util.List;

sealed interface CartLine permits PhysicalItem, DigitalItem, GiftCard {
    int accept(LineVisitor visitor);
}

record PhysicalItem(String name, int priceRs, double weightKg) implements CartLine {
    public int accept(LineVisitor visitor) {
        return visitor.visit(this);
    }
}

record DigitalItem(String name, int priceRs) implements CartLine {
    public int accept(LineVisitor visitor) {
        return visitor.visit(this);
    }
}

record GiftCard(int valueRs) implements CartLine {
    public int accept(LineVisitor visitor) {
        return visitor.visit(this);
    }
}

interface LineVisitor {
    int visit(PhysicalItem item);

    int visit(DigitalItem item);

    int visit(GiftCard card);
}

class TaxVisitor implements LineVisitor {
    public int visit(PhysicalItem item) {
        return item.priceRs() * 12 / 100;
    }

    public int visit(DigitalItem item) {
        return item.priceRs() * 18 / 100;
    }

    public int visit(GiftCard card) {
        return 0; // taxed when it is spent, not when it is bought
    }
}

class ShippingVisitor implements LineVisitor {
    public int visit(PhysicalItem item) {
        return 40 * (int) Math.ceil(item.weightKg()); // Rs 40 per started kg
    }

    public int visit(DigitalItem item) {
        return 0;
    }

    public int visit(GiftCard card) {
        return 0; // emailed
    }
}

/** Added later by marketing - not one line of the cart classes changed. */
class PointsVisitor implements LineVisitor {
    public int visit(PhysicalItem item) {
        return item.priceRs() / 100;
    }

    public int visit(DigitalItem item) {
        return item.priceRs() / 100;
    }

    public int visit(GiftCard card) {
        return 0;
    }
}

class VisitorDesignPatternDemo {

    static int total(List<CartLine> cart, LineVisitor visitor) {
        return cart.stream().mapToInt(line -> line.accept(visitor)).sum();
    }

    /** The Java 21 alternative: no accept(), the switch must cover every type. */
    static int taxBySwitch(CartLine line) {
        return switch (line) {
            case PhysicalItem p -> p.priceRs() * 12 / 100;
            case DigitalItem d -> d.priceRs() * 18 / 100;
            case GiftCard g -> 0;
        };
    }

    public static void main(String[] args) {
        List<CartLine> cart = List.of(
                new PhysicalItem("Shoes", 3000, 1.2),
                new PhysicalItem("Novel", 400, 0.3),
                new DigitalItem("E-book", 200),
                new GiftCard(1000));

        // 360 + 48 + 36 + 0
        print("case 1 tax       ", total(cart, new TaxVisitor()), 444);
        // 2 kg x 40 + 1 kg x 40
        print("case 2 shipping  ", total(cart, new ShippingVisitor()), 120);
        // 30 + 4 + 2 + 0
        print("case 3 points    ", total(cart, new PointsVisitor()), 36);
        print("case 4 tax switch", cart.stream().mapToInt(VisitorDesignPatternDemo::taxBySwitch)
                .sum(), 444);
        print("case 5 empty cart", total(List.of(), new TaxVisitor()), 0);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
