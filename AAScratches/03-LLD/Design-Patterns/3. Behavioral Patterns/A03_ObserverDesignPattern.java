/*
 * =====================================================================
 *  Observer Design Pattern                  LLD | Easy  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Observer - Behavioral family (GoF). Also called Publish/Subscribe (in-process).
 *
 * INTENT
 *   Define a one-to-many dependency: when one object (the subject) changes state, every
 *   registered dependent (observer) is notified automatically. The subject knows only
 *   the Observer interface, so listeners can be added or dropped without editing it.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when: one state change must fan out to an unknown number of reactions -
 *     price ticker to dashboards, cache invalidation, UI listeners, domain events.
 *   Do not use when: you need ordering, retries or durability across processes - that
 *     is a message broker, not an in-memory list. Also avoid when the "observers" are
 *     really one fixed collaborator; a direct call is clearer.
 *
 * ROLES IN THIS CODE
 *   Subject          -> Subject interface (register / remove / notify)
 *   WeatherStation   -> ConcreteSubject: holds temperature + humidity and the observer
 *                       list; updateMeasurements() is the state change that triggers notify
 *   Observer         -> Observer interface (update(temperature, humidity))
 *   MobileDisplay, WebDisplay       -> ConcreteObservers; each stores what it last received
 *   ObserverDesignPattern.main -> Client: wires observers to the subject
 *
 * KEY INSIGHT
 *   The subject broadcasts to an interface it does not own instances of. Adding a
 *   sixth display is a new class plus one registerObserver call - the subject never
 *   changes. This demo uses PUSH (state is passed into update); the PULL variant passes
 *   the subject itself and lets each observer read only the fields it cares about.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Push vs pull notification: which scales better when observers need different slices?
 *   - Concurrency: iterating the list while an observer unregisters throws
 *     ConcurrentModificationException - fix with CopyOnWriteArrayList or a snapshot copy.
 *   - Memory leak: a subject that lives forever pins every observer it holds. Weak
 *     references or an explicit removeObserver on shutdown.
 *   - Observer vs Mediator: one-to-many broadcast vs a hub that coordinates many-to-many.
 *   - Java's own java.util.Observer is deprecated since Java 9 - why? (no ordering, no
 *     type safety, awkward threading). Today: PropertyChangeListener, Spring events, Flow.
 *
 * RUN
 *   main() runs 3 cases: a broadcast to two observers, a broadcast after one observer
 *   unregisters, and the edge case of notifying with no observers registered.
 */

import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(float temperature, float humidity);
}

interface Subject {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}

class WeatherStation implements Subject {

    private float temperature;
    private float humidity;
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        // Push model: the new state travels with the call.
        for (Observer observer : observers) {
            observer.update(temperature, humidity);
        }
    }

    /** The state change that drives the fan-out. */
    public void updateMeasurements(float temperature, float humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
        notifyObservers();
    }

    int observerCount() {
        return observers.size();
    }
}

class MobileDisplay implements Observer {

    private String lastReading = "no reading yet";

    @Override
    public void update(float temperature, float humidity) {
        lastReading = "Mobile temp=" + temperature + " humidity=" + humidity;
    }

    String getLastReading() {
        return lastReading;
    }
}

class WebDisplay implements Observer {

    private String lastReading = "no reading yet";

    @Override
    public void update(float temperature, float humidity) {
        lastReading = "Web temp=" + temperature + " humidity=" + humidity;
    }

    String getLastReading() {
        return lastReading;
    }
}

class ObserverDesignPattern {

    public static void main(String[] args) {
        // Declared as WeatherStation, not Subject: updateMeasurements() is a concrete-subject
        // method, so the Subject interface alone cannot trigger a state change.
        WeatherStation weatherStation = new WeatherStation();

        MobileDisplay phoneDisplay = new MobileDisplay();
        WebDisplay webDisplay = new WebDisplay();
        weatherStation.registerObserver(phoneDisplay);
        weatherStation.registerObserver(webDisplay);

        // Case 1: one state change reaches both observers.
        weatherStation.updateMeasurements(30.5f, 65f);
        print("case 1 mobile", phoneDisplay.getLastReading(), "Mobile temp=30.5 humidity=65.0");
        print("case 1 web", webDisplay.getLastReading(), "Web temp=30.5 humidity=65.0");

        // Case 2: unregister the web display - it must keep its stale reading.
        weatherStation.removeObserver(webDisplay);
        weatherStation.updateMeasurements(28.2f, 70f);
        print("case 2 mobile (updated)", phoneDisplay.getLastReading(),
                "Mobile temp=28.2 humidity=70.0");
        print("case 2 web (removed)", webDisplay.getLastReading(),
                "Web temp=30.5 humidity=65.0");

        // Case 3 (edge): a subject with zero observers notifies nobody and must not fail.
        WeatherStation emptyStation = new WeatherStation();
        emptyStation.updateMeasurements(10f, 20f);
        print("case 3 no observers", emptyStation.observerCount() + " notified, no exception",
                "0 notified, no exception");
    }

    private static void print(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + "   " + (ok ? "[OK]" : "[FAIL]"));
    }
}
