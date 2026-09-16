import java.io.Serial;
import java.io.Serializable;

// ✅ 'final' prevents subclassing (good)
final public class Singleton implements Cloneable, Serializable {
    // ✅ Implements Serializable (handled safely below)
    // ✅ Implements Cloneable but handled to prevent cloning

    // ✅ 'volatile' ensures visibility and prevents half-initialized
    // instance issues during double-checked locking
    // NOT FINAL
    private static volatile Singleton singleton;

    private Singleton() {
        // ✅ Private constructor prevents instantiation from outside the class
        // ✅ Protection against reflection when instance already exists
        // ⚠️ Still vulnerable if reflection is used *before* getInstance() (singleton is still null then)
        if (singleton != null) {
            throw new IllegalStateException("Instance already created");
        }
    }

    public static Singleton getInstance() {
        // ✅ Lazy initialization (instance created only when needed)
        if (singleton == null) {
            synchronized (Singleton.class) {
                // ✅ Double-checked locking ensures thread safety with minimal synchronization cost
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton; // ✅ Returns the same instance every time
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // ✅ Prevents cloning from creating a new instance
        throw new CloneNotSupportedException("Cannot clone a singleton");
    }

    @Serial
    protected Singleton readResolve() {
        // ✅ Ensures same instance is returned during deserialization
        // ⚠️ Technically, the return type should be 'Object' (not harmful, but slightly nonstandard)
        return getInstance();
    }

}

class Main {
    public static void main(String[] args) {

        // ✅ All instances retrieved from getInstance() should be identical
        Singleton instance0 = Singleton.getInstance();
        Singleton instance1 = Singleton.getInstance();
        Singleton instance2 = Singleton.getInstance();

        // ✅ Same reference printed (proof of singleton behavior)
        System.out.println(instance0);
        System.out.println(instance1);
        System.out.println(instance2);
    }
}


//✅ Lazy-loaded
//✅ Thread-safe
//✅ Reflection-safe
//✅ Serialization-safe
//✅ Clone-safe
//✅ High performance
class RobustSingleton implements Serializable, Cloneable {

    // Step 1: Private constructor — prevent outside instantiation
    private RobustSingleton() {
        // Step 2: Reflection guard — prevent creating instance using reflection
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Instance already created");
        }
    }

    // Step 3: Static inner class — ensures lazy initialization + thread safety
    private static class Holder {
        private static final RobustSingleton INSTANCE = new RobustSingleton();
    }

    // Step 4: Public global access point
    public static RobustSingleton getInstance() {
        return Holder.INSTANCE;
    }

    // Step 5: Prevent cloning
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone a singleton");
    }

    // Step 6: Prevent creating new instance during deserialization
    @Serial
    protected Object readResolve() {
        return getInstance();
    }

    // Optional: For testing/logging
    public void showMessage() {
        System.out.println("Hello from robust non-enum singleton!");
    }
}
