/*
 * =====================================================================
 *  Singleton - sealing every back door       Creational | Hard   MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Singleton - Creational family. Three implementations side by side:
 *   double-checked locking (Singleton), the initialization-on-demand
 *   holder idiom (RobustSingleton), and an enum (EnumSingleton).
 *
 * INTENT
 *   Guarantee that a class has exactly one instance for the lifetime of
 *   the classloader, and give everyone one way to reach it.
 *
 * WHEN TO USE, WHEN NOT
 *   Use for genuinely process-wide, stateless-or-carefully-shared things:
 *     a connection pool, a metrics registry, a config holder.
 *   Do NOT use as a global variable - it hides dependencies, makes tests
 *     order-dependent, and cannot be swapped for a fake.
 *   Do NOT assume "one instance ever": one per classloader, and in a
 *     cluster one PER JVM, which is not the same as one per system.
 *   In Spring, the container already gives you singleton scope - hand
 *     rolling this inside a Spring app is almost always a mistake.
 *
 * ROLES IN THIS CODE
 *   Singleton        Singleton via double-checked locking; the field is
 *                    volatile and the constructor/clone/readResolve are the
 *                    three guards
 *   RobustSingleton  Singleton via the holder idiom - lazy and thread-safe
 *                    with zero synchronization, because class init is
 *                    already serialized by the JVM
 *   EnumSingleton    Singleton via enum - the JVM itself blocks reflection
 *                    and deserialization, so no guards are needed
 *   Main             Client, and the attacker: it tries each back door
 *
 * KEY INSIGHT
 *   `instance = new Singleton()` is three steps: allocate, run the
 *   constructor, assign the reference. Without `volatile` the JVM may
 *   publish the reference before the constructor finishes, so a second
 *   thread sees a non-null but half-built object. volatile forbids that
 *   reordering and makes the write visible to other threads.
 *   Then remember the three ways a "singleton" still gets a second
 *   instance: reflection on the private constructor, deserialization, and
 *   clone(). Every guard below exists for exactly one of those. Enum
 *   closes all three for free - which is why it is the recommended form.
 *
 *   Fixed: readResolve() declared `protected Singleton readResolve()`.
 *   Java serialization looks for a method returning Object, so a covariant
 *   return type means the hook is NEVER called and deserialization quietly
 *   produced a second instance. Return type is now Object (case 4 proves
 *   it). serialVersionUID was also missing.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why volatile in double-checked locking, and why was DCL broken
 *     before Java 5? The pre-2004 memory model allowed the reordering.
 *   - Why is the holder idiom preferred over DCL? Same laziness, no
 *     synchronized, no volatile, far less code to get wrong.
 *   - Why is enum the cleanest? Effective Java Item 3: reflection- and
 *     serialization-safe by construction. Drawback: cannot extend a class,
 *     and instantiation happens on first class use.
 *   - Eager static final field - when is it fine? When construction is
 *     cheap and always needed; laziness is the only thing you lose.
 *   - How do you test code that calls MySingleton.getInstance()? You
 *     usually cannot - which is the argument for dependency injection.
 *
 * RUN
 *   main() runs 8 cases: identity for all three variants, then the
 *   reflection, clone and serialization attacks against each guard.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/** `final` prevents a subclass from handing out extra instances. */
final class Singleton implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // volatile: stops the JVM publishing a half-constructed object to a
    // second thread, and makes the write visible without a lock on reads.
    // Cannot be final - it is assigned lazily.
    private static volatile Singleton singleton;

    private Singleton() {
        // Guard 1 of 3: reflection. Once an instance exists, a reflective
        // call to this constructor blows up.
        // Still bypassable if reflection runs BEFORE the first getInstance().
        if (singleton != null) {
            throw new IllegalStateException("Instance already created");
        }
    }

    public static Singleton getInstance() {
        // First read is unsynchronized - the common, fast path.
        if (singleton == null) {
            synchronized (Singleton.class) {
                // Second read inside the lock: two threads can both pass the
                // first check, only one may construct.
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // Guard 2 of 3: clone(). Object.clone() would allocate a copy
        // without running any constructor, so refuse outright.
        throw new CloneNotSupportedException("Cannot clone a singleton");
    }

    /**
     * Guard 3 of 3: deserialization. readStream builds a fresh object and
     * then calls this hook; returning the live instance discards it.
     * The return type MUST be Object - serialization matches the method by
     * exact signature, so a covariant return silently disables the hook.
     */
    @Serial
    protected Object readResolve() {
        return getInstance();
    }
}

/**
 * Initialization-on-demand holder idiom: lazy, thread-safe, lock-free.
 * Holder is not loaded until getInstance() touches it, and the JVM
 * guarantees class initialization runs once.
 */
class RobustSingleton implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    private RobustSingleton() {
        // During Holder's own class init this read returns null (the JVM
        // allows re-entrant access from the initializing thread), so the
        // legitimate creation passes. A later reflective call does not.
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Instance already created");
        }
    }

    private static class Holder {
        private static final RobustSingleton INSTANCE = new RobustSingleton();
    }

    public static RobustSingleton getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone a singleton");
    }

    @Serial
    protected Object readResolve() {
        return getInstance();
    }
}

/**
 * The recommended form. No guards written by hand: the JVM refuses
 * reflective construction of enums and deserializes constants by name.
 */
enum EnumSingleton {
    INSTANCE;

    public String greet() {
        return "hello from enum singleton";
    }
}

class Main {

    public static void main(String[] args) throws Exception {
        // Case 1: every getInstance() call returns the same reference.
        Singleton a = Singleton.getInstance();
        Singleton b = Singleton.getInstance();
        Singleton c = Singleton.getInstance();
        print("case 1 DCL identity    ", a == b && b == c, true);

        // Case 2: back door 1 - reflection on the private constructor.
        print("case 2 DCL reflection  ", reflectionAttack(Singleton.class),
                "IllegalStateException: Instance already created");

        // Case 3: back door 2 - clone().
        String cloneResult;
        try {
            invokeClone(a);
            cloneResult = "cloned";
        } catch (CloneNotSupportedException e) {
            cloneResult = "CloneNotSupportedException: " + e.getMessage();
        }
        print("case 3 DCL clone       ", cloneResult,
                "CloneNotSupportedException: Cannot clone a singleton");

        // Case 4: back door 3 - serialize, then read back. readResolve()
        // must swap the fresh object for the live one.
        print("case 4 DCL serialize   ", roundTrip(a) == a, true);

        // Case 5-6: the holder idiom gives the same guarantees with no lock.
        RobustSingleton r1 = RobustSingleton.getInstance();
        print("case 5 holder identity ", r1 == RobustSingleton.getInstance(), true);
        print("case 6 holder reflection", reflectionAttack(RobustSingleton.class),
                "IllegalStateException: Instance already created");

        // Case 7-8: enum. Identity is free, and the JVM itself refuses the
        // reflective attack - no hand-written guard anywhere.
        print("case 7 enum identity   ",
                EnumSingleton.INSTANCE == EnumSingleton.valueOf("INSTANCE"), true);
        print("case 8 enum reflection ", enumReflectionAttack(),
                "IllegalArgumentException: Cannot reflectively create enum objects");

        System.out.println(EnumSingleton.INSTANCE.greet());
    }

    /** Tries to build a second instance through the private constructor. */
    private static String reflectionAttack(Class<?> type) throws Exception {
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            return "attack succeeded - a second instance exists";
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            return cause.getClass().getSimpleName() + ": " + cause.getMessage();
        }
    }

    /** Enums are rejected by Constructor.newInstance() itself. */
    private static String enumReflectionAttack() throws Exception {
        Constructor<?> constructor = EnumSingleton.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            constructor.newInstance("EVIL", 1);
            return "attack succeeded - a second constant exists";
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException: " + e.getMessage();
        }
    }

    /** clone() is protected, so reach it reflectively from the client. */
    private static Object invokeClone(Object target) throws Exception {
        java.lang.reflect.Method clone = target.getClass().getDeclaredMethod("clone");
        clone.setAccessible(true);
        try {
            return clone.invoke(target);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof CloneNotSupportedException cnse) {
                throw cnse;
            }
            throw e;
        }
    }

    /** Writes the object to bytes and reads it straight back. */
    private static Object roundTrip(Object target) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(target);
        }
        ByteArrayInputStream source = new ByteArrayInputStream(bytes.toByteArray());
        try (ObjectInputStream in = new ObjectInputStream(source)) {
            return in.readObject();
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
