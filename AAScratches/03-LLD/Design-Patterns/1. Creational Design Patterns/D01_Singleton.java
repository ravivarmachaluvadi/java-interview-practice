/*
 * =====================================================================
 *  Singleton - sealing every back door       Creational | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Some objects must exist once per JVM: the connection pool, the metrics
 *   registry, the app config. Two pools double the DB connections; two
 *   configs drift apart. Build it three ways (double-checked locking, holder
 *   idiom, enum) and attack each through its back doors.
 *
 * KEY INSIGHT
 *   `instance = new Singleton()` is allocate, construct, assign. Without
 *   `volatile` another thread may see the reference before the constructor
 *   finishes. Then three back doors still make a second instance:
 *   reflection, clone() and deserialization (readResolve must return
 *   Object). An enum closes all three for free - the recommended form.
 *
 * ROLES IN THIS CODE
 *   Singleton         double-checked locking + the three hand-written guards
 *   RobustSingleton   holder idiom: lazy and lock-free, JVM class init is once
 *   EnumSingleton     enum: the JVM blocks reflection and deserialization
 *   Main              client, and the attacker
 *
 * INTERVIEW FOLLOW-UPS
 *   - Holder vs DCL: same laziness, no lock, no volatile, less to get wrong.
 *   - Spring beans are already singleton-scoped; hand-rolling one there is
 *     usually a mistake, and getInstance() calls cannot be faked in tests.
 *   - "One instance" means one per classloader, so one per JVM, not per
 *     cluster.
 *
 * RUN
 *   8 cases: identity for each variant, then reflection, clone and
 *   serialization attacks against each guard.
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
