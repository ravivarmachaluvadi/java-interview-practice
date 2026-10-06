/*
 * =====================================================================
 *  Abstract Factory - regional checkout kit            Creational | Medium
 * =====================================================================
 *
 * PROBLEM
 *   The store sells in India and the US. Each region needs its own payment
 *   gateway, tax rule and invoice currency, and the three must match: a GST
 *   invoice charged through Stripe in dollars is a compliance bug. Checkout
 *   must work for any region without naming one.
 *
 * KEY INSIGHT
 *   One factory makes the whole family, so choosing the region once (from
 *   config, at startup) guarantees every part matches. Checkout depends only
 *   on the interfaces. The cost: adding a new product to the family, say
 *   ShippingRates, means editing every kit.
 *
 * ROLES IN THIS CODE
 *   RegionKit                         AbstractFactory
 *   IndiaKit, UsKit                   ConcreteFactory (one per region)
 *   PaymentGateway, TaxCalculator,
 *   InvoiceFormatter                  AbstractProduct
 *   RazorpayGateway, GstCalculator..  ConcreteProduct
 *   Checkout                          Client
 *
 * INTERVIEW FOLLOW-UPS
 *   - Factory vs Abstract Factory: A01_FactoryDesignPattern makes one
 *     product from a key; this makes a family that must not be mixed.
 *   - Spring: one @Configuration per region, picked by @Profile("in").
 *   - Seen in: JDBC (a Driver gives you a matching Connection, Statement,
 *     ResultSet), javax.xml DocumentBuilderFactory.
 *
 * RUN
 *   4 cases: India order, US order, unknown region, kit parts all match.
 */

import java.util.Locale;

// ---------- the products every region must supply ----------

interface PaymentGateway {
    /** Gateways take the amount in the smallest unit (paise, cents). */
    String charge(long minorUnits);
}

interface TaxCalculator {
    long taxOn(long netMinor);

    String label();
}

interface InvoiceFormatter {
    String money(long minorUnits);
}

// ---------- India family ----------

class RazorpayGateway implements PaymentGateway {
    public String charge(long paise) {
        return "Razorpay charged " + paise + " paise";
    }
}

class GstCalculator implements TaxCalculator {
    public long taxOn(long netPaise) {
        return netPaise * 18 / 100;
    }

    public String label() {
        return "GST 18%";
    }
}

class RupeeFormatter implements InvoiceFormatter {
    public String money(long paise) {
        return String.format(Locale.US, "Rs %,d.%02d", paise / 100, paise % 100);
    }
}

// ---------- US family ----------

class StripeGateway implements PaymentGateway {
    public String charge(long cents) {
        return "Stripe charged " + cents + " cents";
    }
}

class UsSalesTax implements TaxCalculator {
    public long taxOn(long netCents) {
        return netCents * 825 / 10_000; // 8.25%
    }

    public String label() {
        return "Sales tax 8.25%";
    }
}

class DollarFormatter implements InvoiceFormatter {
    public String money(long cents) {
        return String.format(Locale.US, "$%,d.%02d", cents / 100, cents % 100);
    }
}

// ---------- the abstract factory and its two regions ----------

interface RegionKit {
    PaymentGateway gateway();

    TaxCalculator tax();

    InvoiceFormatter invoice();

    static RegionKit forRegion(String code) {
        return switch (code) {
            case "IN" -> new IndiaKit();
            case "US" -> new UsKit();
            default -> throw new IllegalArgumentException("No region kit for " + code);
        };
    }
}

class IndiaKit implements RegionKit {
    public PaymentGateway gateway() { return new RazorpayGateway(); }
    public TaxCalculator tax() { return new GstCalculator(); }
    public InvoiceFormatter invoice() { return new RupeeFormatter(); }
}

class UsKit implements RegionKit {
    public PaymentGateway gateway() { return new StripeGateway(); }
    public TaxCalculator tax() { return new UsSalesTax(); }
    public InvoiceFormatter invoice() { return new DollarFormatter(); }
}

/** Client: never mentions India, the US, Razorpay or Stripe. */
class Checkout {
    private final RegionKit kit;

    Checkout(RegionKit kit) {
        this.kit = kit;
    }

    String placeOrder(long netMinor) {
        TaxCalculator tax = kit.tax();
        InvoiceFormatter fmt = kit.invoice();
        long taxAmount = tax.taxOn(netMinor);
        long total = netMinor + taxAmount;
        return fmt.money(netMinor) + " + " + tax.label() + " " + fmt.money(taxAmount)
                + " = " + fmt.money(total) + " | " + kit.gateway().charge(total);
    }
}

class AbstractFactoryPattern {

    public static void main(String[] args) {
        // Case 1-2: same Checkout code, same net amount (1000.00), two regions.
        print("case 1 India ", new Checkout(RegionKit.forRegion("IN")).placeOrder(100_000),
                "Rs 1,000.00 + GST 18% Rs 180.00 = Rs 1,180.00 | Razorpay charged 118000 paise");
        print("case 2 US    ", new Checkout(RegionKit.forRegion("US")).placeOrder(100_000),
                "$1,000.00 + Sales tax 8.25% $82.50 = $1,082.50 | Stripe charged 108250 cents");

        // Case 3: edge. A region we do not sell in fails loudly, never defaults.
        String outcome;
        try {
            RegionKit.forRegion("BR");
            outcome = "no exception";
        } catch (IllegalArgumentException e) {
            outcome = e.getMessage();
        }
        print("case 3 BR    ", outcome, "No region kit for BR");

        // Case 4: the point of the pattern - one choice, a matching set of parts.
        RegionKit kit = RegionKit.forRegion("IN");
        print("case 4 family", kit.gateway().getClass().getSimpleName() + " + "
                        + kit.tax().getClass().getSimpleName() + " + "
                        + kit.invoice().getClass().getSimpleName(),
                "RazorpayGateway + GstCalculator + RupeeFormatter");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
