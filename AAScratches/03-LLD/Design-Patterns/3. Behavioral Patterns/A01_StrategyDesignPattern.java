/*
 * =====================================================================
 *  Strategy - checkout promotions                   Behavioral | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Every sale runs a different promotion: Rs 200 off above Rs 999, 10% off
 *   capped at Rs 500, buy 2 get the cheapest free. Marketing adds a new one
 *   each month, and checkout should apply the best offer for the cart.
 *   An if/else per promo inside checkout grows forever.
 *
 * KEY INSIGHT
 *   The promotion is a field holding an object, not a branch. Each promo is
 *   one class (or one lambda) behind the same interface; checkout only calls
 *   discount(cart). Picking the best offer is just "try every strategy,
 *   keep the biggest".
 *
 * ROLES IN THIS CODE
 *   Promotion                          Strategy
 *   FlatOff, PercentOff, BuyTwoGetOne  ConcreteStrategy
 *   Checkout                           Context
 *
 * INTERVIEW FOLLOW-UPS
 *   - Strategy vs State: the client chooses a strategy; a state chooses
 *     its own successor (C01_StateDesignPattern).
 *   - A one-method strategy is a functional interface, so a lambda is a
 *     strategy (case 6).
 *   - Seen in: Comparator for sort, Spring's PasswordEncoder.
 *
 * RUN
 *   6 cases: flat, percent with cap, buy-2-get-1, minimum not met, best
 *   offer, a lambda promo.
 */

import java.util.ArrayList;
import java.util.List;

record CartItem(String name, int priceRs, int qty) {
}

interface Promotion {
    /** Discount in whole rupees; 0 when the promo does not apply. */
    int discount(List<CartItem> cart);

    static int subtotal(List<CartItem> cart) {
        return cart.stream().mapToInt(i -> i.priceRs() * i.qty()).sum();
    }
}

class FlatOff implements Promotion {
    private final int amount;
    private final int minCart;

    FlatOff(int amount, int minCart) {
        this.amount = amount;
        this.minCart = minCart;
    }

    public int discount(List<CartItem> cart) {
        return Promotion.subtotal(cart) >= minCart ? amount : 0;
    }
}

class PercentOff implements Promotion {
    private final int percent;
    private final int cap;

    PercentOff(int percent, int cap) {
        this.percent = percent;
        this.cap = cap;
    }

    public int discount(List<CartItem> cart) {
        return Math.min(Promotion.subtotal(cart) * percent / 100, cap);
    }
}

/** For every 3 units in the cart, the cheapest unit is free. */
class BuyTwoGetOne implements Promotion {
    public int discount(List<CartItem> cart) {
        List<Integer> unitPrices = new ArrayList<>();
        cart.forEach(i -> {
            for (int q = 0; q < i.qty(); q++) {
                unitPrices.add(i.priceRs());
            }
        });
        unitPrices.sort(null);
        return unitPrices.subList(0, unitPrices.size() / 3).stream().mapToInt(p -> p).sum();
    }
}

/** Context: never names a concrete promotion. */
class Checkout {
    static int total(List<CartItem> cart, Promotion promo) {
        return Promotion.subtotal(cart) - promo.discount(cart);
    }

    static Promotion bestOffer(List<CartItem> cart, List<Promotion> live) {
        Promotion best = c -> 0; // "no promo" is a strategy too
        for (Promotion promo : live) {
            if (promo.discount(cart) > best.discount(cart)) {
                best = promo;
            }
        }
        return best;
    }
}

class StrategyDesignPattern {

    public static void main(String[] args) {
        // Subtotal: 3000 + 2 x 300 + 800 = 4400.
        List<CartItem> cart = List.of(new CartItem("Shoes", 3000, 1),
                new CartItem("Socks", 300, 2), new CartItem("Tee", 800, 1));
        Promotion flat = new FlatOff(200, 999);
        Promotion percent = new PercentOff(10, 500);
        Promotion b2g1 = new BuyTwoGetOne();

        // Case 1-3: same checkout call, three algorithms.
        print("case 1 flat 200    ", Checkout.total(cart, flat), 4200);
        print("case 2 10% (440)   ", Checkout.total(cart, percent), 3960);
        print("case 3 buy 2 get 1 ", Checkout.total(cart, b2g1), 4100);

        // Case 4: edge. Rs 600 cart is under the Rs 999 minimum.
        List<CartItem> small = List.of(new CartItem("Socks", 300, 2));
        print("case 4 min not met ", Checkout.total(small, flat), 600);

        // Case 5: the best of the live offers, chosen at runtime.
        Promotion best = Checkout.bestOffer(cart, List.of(flat, percent, b2g1));
        print("case 5 best offer  ", best.getClass().getSimpleName(), "PercentOff");

        // Case 6: a first-order 15% promo, added as a lambda - no new class.
        print("case 6 lambda 15%  ", Checkout.total(cart, c -> Promotion.subtotal(c) * 15 / 100),
                3740);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
