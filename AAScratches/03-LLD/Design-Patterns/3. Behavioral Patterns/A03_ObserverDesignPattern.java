/*
 * =====================================================================
 *  Observer - back-in-stock alerts                   Behavioral | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A product page offers "Notify me when available". When stock goes from
 *   0 to anything, everyone who asked gets one alert and is then removed;
 *   the wishlist badge updates every time. The product must not know about
 *   email, push or badges, and one broken listener must not stop the rest.
 *
 * KEY INSIGHT
 *   The subject keeps a list of listeners behind one interface and calls
 *   them on the event it owns - here the 0 -> positive transition, not
 *   every restock. Two production details: catch per listener so one
 *   failure is isolated, and use CopyOnWriteArrayList so a listener can
 *   unsubscribe itself while the list is being walked.
 *
 * ROLES IN THIS CODE
 *   StockListener    Observer
 *   ProductPage      Subject - subscribe, unsubscribe, notify on 0 -> n
 *   NotifyMeOnce     ConcreteObserver - one alert, then unsubscribes
 *   lambdas in main  ConcreteObserver - wishlist badge, a broken listener
 *
 * INTERVIEW FOLLOW-UPS
 *   - Push (send qty) vs pull (listener asks the subject): push here.
 *   - Slow listeners: hand delivery to an executor or a queue (Kafka) so
 *     restock() does not wait on email.
 *   - Seen in: Spring ApplicationEventPublisher + @EventListener, UI event
 *     listeners, PropertyChangeListener.
 *
 * RUN
 *   4 cases: first restock alerts all, second restock alerts none, one-shot
 *   listeners removed, a throwing listener does not block the badge.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

interface StockListener {
    void backInStock(String product, int qty);
}

class ProductPage {
    private final String name;
    private int stock;
    private final List<StockListener> listeners = new CopyOnWriteArrayList<>();
    int failedListeners;

    ProductPage(String name) {
        this.name = name;
    }

    void subscribe(StockListener listener) {
        listeners.add(listener);
    }

    void unsubscribe(StockListener listener) {
        listeners.remove(listener);
    }

    int listenerCount() {
        return listeners.size();
    }

    void sell(int qty) {
        stock -= qty;
    }

    void restock(int qty) {
        boolean wasEmpty = stock == 0;
        stock += qty;
        if (wasEmpty && stock > 0) { // the event is the transition, not every restock
            for (StockListener listener : listeners) {
                try {
                    listener.backInStock(name, stock);
                } catch (RuntimeException e) {
                    failedListeners++; // one broken listener must not starve the others
                }
            }
        }
    }
}

/** "Notify me" button: alert once, then leave. */
class NotifyMeOnce implements StockListener {
    private final ProductPage page;
    private final String email;
    private final List<String> outbox;

    NotifyMeOnce(ProductPage page, String email, List<String> outbox) {
        this.page = page;
        this.email = email;
        this.outbox = outbox;
    }

    public void backInStock(String product, int qty) {
        outbox.add("mail " + email);
        page.unsubscribe(this); // safe mid-notify thanks to CopyOnWriteArrayList
    }
}

class ObserverDesignPattern {

    public static void main(String[] args) {
        List<String> outbox = new ArrayList<>();
        ProductPage sneakers = new ProductPage("Sneakers");
        sneakers.subscribe(new NotifyMeOnce(sneakers, "a@example.com", outbox));
        sneakers.subscribe(new NotifyMeOnce(sneakers, "b@example.com", outbox));
        sneakers.subscribe((product, qty) -> outbox.add("badge " + product + " " + qty));

        // Case 1: typical. 0 -> 5 alerts everyone.
        sneakers.restock(5);
        print("case 1 first restock", outbox,
                "[mail a@example.com, mail b@example.com, badge Sneakers 5]");

        // Case 2: 5 -> 10 is not "back in stock", so nobody hears about it.
        sneakers.restock(5);
        print("case 2 second restock", outbox.size(), 3);

        // Case 3: the two one-shot listeners removed themselves; the badge stays.
        print("case 3 listeners left", sneakers.listenerCount(), 1);

        // Case 4: tricky. Sell out, add a broken listener, restock: the badge
        // (subscribed earlier) still gets its update.
        sneakers.sell(10);
        sneakers.subscribe((product, qty) -> {
            throw new IllegalStateException("SMS provider down");
        });
        sneakers.restock(2);
        print("case 4a badge again ", outbox.get(outbox.size() - 1), "badge Sneakers 2");
        print("case 4b failures    ", sneakers.failedListeners, 1);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
