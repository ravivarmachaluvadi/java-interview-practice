/*
 * =====================================================================
 *  Adapter - courier partner APIs                    Structural | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Checkout asks every courier partner for a shipping quote and shows the
 *   cheapest. Our code speaks ShippingProvider: kg in, whole rupees out.
 *   FedEx's SDK wants pounds and returns "USD 10.61"; Delhivery's wants grams
 *   and an int pincode, returns paise, and throws for places it cannot
 *   reach. We cannot change either SDK.
 *
 * KEY INSIGHT
 *   An adapter implements the interface we want, holds the SDK we have, and
 *   translates in between: units, currency, parameter types, error style.
 *   Every vendor quirk lives in one class per vendor; checkout stays clean
 *   and a new courier is one new adapter.
 *
 * ROLES IN THIS CODE
 *   ShippingProvider                  Target (our interface)
 *   FedExSdk, DelhiveryClient         Adaptee (third-party, cannot change)
 *   FedExAdapter, DelhiveryAdapter    Adapter
 *   cheapest()                        Client
 *
 * INTERVIEW FOLLOW-UPS
 *   - Adapter vs Facade: convert ONE interface vs simplify MANY.
 *   - Object adapter (holds the SDK, as here) vs class adapter (extends it):
 *     holding it also works when the SDK class is final.
 *   - Seen in: InputStreamReader (bytes -> chars), Arrays.asList, Spring
 *     MVC's HandlerAdapter.
 *
 * RUN
 *   4 cases: Delhivery quote, FedEx quote, cheapest courier, a pincode
 *   Delhivery cannot serve.
 */

import java.util.List;
import java.util.Locale;

record Parcel(double weightKg, String pincode) {
}

/** Target: what checkout wants from every courier. */
interface ShippingProvider {
    String name();

    /** Quote in whole rupees, or -1 when this courier cannot deliver there. */
    int quoteRs(Parcel parcel);
}

/** Adaptee from a vendor jar: pounds in, a USD string out. */
class FedExSdk {
    String getRate(double weightLbs, String destinationZip) {
        return String.format(Locale.US, "USD %.2f", 4.0 + 1.5 * weightLbs);
    }
}

/** Adaptee from another vendor: grams and an int pincode in, paise out. */
class DelhiveryClient {
    long priceInPaise(int grams, int pincode) {
        if (pincode >= 900_000) {
            throw new IllegalArgumentException("pincode " + pincode + " not serviceable");
        }
        return 4_000 + 6L * grams; // Rs 40 base + Rs 6 per 100 g
    }
}

class FedExAdapter implements ShippingProvider {
    private static final double LBS_PER_KG = 2.20462;
    private static final double RS_PER_USD = 83.0;
    private final FedExSdk sdk = new FedExSdk();

    public String name() {
        return "FedEx";
    }

    public int quoteRs(Parcel parcel) {
        String usd = sdk.getRate(parcel.weightKg() * LBS_PER_KG, parcel.pincode()); // kg -> lbs
        double dollars = Double.parseDouble(usd.substring("USD ".length()));    // "USD 10.61"
        return (int) Math.round(dollars * RS_PER_USD);                          // USD -> Rs
    }
}

class DelhiveryAdapter implements ShippingProvider {
    private final DelhiveryClient client = new DelhiveryClient();

    public String name() {
        return "Delhivery";
    }

    public int quoteRs(Parcel parcel) {
        try {
            int grams = (int) Math.round(parcel.weightKg() * 1000);
            return (int) (client.priceInPaise(grams, Integer.parseInt(parcel.pincode())) / 100);
        } catch (IllegalArgumentException e) {
            return -1; // vendor's exception -> our "cannot deliver" contract
        }
    }
}

class AdapterPatternDemo {

    /** Client: only ever sees ShippingProvider. */
    static String cheapest(List<ShippingProvider> couriers, Parcel parcel) {
        ShippingProvider best = null;
        int bestQuote = Integer.MAX_VALUE;
        for (ShippingProvider courier : couriers) {
            int quote = courier.quoteRs(parcel);
            if (quote >= 0 && quote < bestQuote) {
                best = courier;
                bestQuote = quote;
            }
        }
        return best == null ? "no courier" : best.name() + " Rs " + bestQuote;
    }

    public static void main(String[] args) {
        List<ShippingProvider> couriers = List.of(new FedExAdapter(), new DelhiveryAdapter());
        Parcel shoes = new Parcel(2.0, "560001");

        // Case 1-2: each adapter turns its vendor's units into whole rupees.
        print("case 1 Delhivery", new DelhiveryAdapter().quoteRs(shoes), 160);
        print("case 2 FedEx    ", new FedExAdapter().quoteRs(shoes), 881);

        // Case 3: typical. Checkout compares couriers without knowing either SDK.
        print("case 3 cheapest ", cheapest(couriers, shoes), "Delhivery Rs 160");

        // Case 4: edge. Delhivery's exception became -1, so FedEx wins here.
        print("case 4 remote   ", cheapest(couriers, new Parcel(2.0, "999001")), "FedEx Rs 881");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
