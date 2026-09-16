import java.util.ArrayList;
import java.util.List;

/**
 * The Observer Design Pattern is one of the
 * <p>
 * Behavioral Design Patterns in Java.
 * <p>
 * It defines a one-to-many dependency between objects so
 * <p>
 * that when one object (subject) changes its state,
 * <p>
 * all its dependents (observers) are automatically notified and updated.
 */
// Step 1: Create the Observer interface
interface Observer {
    void update(float temperature, float humidity);
}

// Step 2: Create the Subject interface
interface Subject {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}

// Step 3: Concrete Subject (WeatherStation)
class WeatherStation implements Subject {

    private float temperature;
    private float humidity;
    private List<Observer> observers = new ArrayList<>();


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
        for (Observer observer : observers) {
            observer.update(temperature, humidity);
        }
    }

    public void updateMeasurements(float temperature, float humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
        notifyObservers();
    }

}

// Step 4: Concrete Observers
class MobileDisplay implements Observer {

    @Override
    public void update(float temperature, float humidity) {
        System.out.println("Mobile display : temperature : " + temperature + " humidity : " + humidity);
    }
}

class WebDisplay implements Observer {

    @Override
    public void update(float temperature, float humidity) {
        System.out.println("Web display : temperature : " + temperature + " humidity : " + humidity);
    }
}

public class ObserverDesignPattern {
    public static void main(String[] args) {
        // we can't invoke updateMeasurements with Subject type
//        Subject weatherStation = new WeatherStation();
        WeatherStation weatherStation = new WeatherStation();

        Observer phoneDisplay = new MobileDisplay();
        Observer webDisplay = new WebDisplay();

        weatherStation.registerObserver(phoneDisplay);
        weatherStation.registerObserver(webDisplay);
        System.out.println("---- First Update ----");
        weatherStation.updateMeasurements(30.5f, 65f);

        System.out.println("---- Second Update ----");
        weatherStation.updateMeasurements(28.2f, 70f);
    }
}
