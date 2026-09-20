/*
 * =====================================================================
 *  Static method call on a null reference          Java language | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Calling a static method through a reference variable that holds null does
 *   NOT throw NullPointerException. The compiler resolves static members from
 *   the DECLARED TYPE of the expression, not from the object it points at, so
 *   the reference is never dereferenced at run time.
 *
 * WHAT YOU WILL SEE
 *   case 1: static method through a null ref   -> runs normally, no NPE
 *   case 2: instance method through a null ref -> NullPointerException
 *   case 3: static field through a null ref    -> reads the field, no NPE
 *   case 4: the receiver expression still runs -> its side effect happens
 *
 * HOW IT WORKS
 *   1. javac sees ref.staticMethod() where ref is declared as StaticNullRef.
 *   2. Static members bind at compile time to the declared type, so the call
 *      is effectively rewritten to StaticNullRef.staticMethod().
 *   3. That compiles to the invokestatic bytecode, which never looks at the
 *      reference value. An instance call compiles to invokevirtual, which must
 *      dereference the receiver to find the method - that is where NPE lives.
 *   4. The expression before the dot is still EVALUATED; only its value is
 *      discarded. So makeNull().staticMethod() still runs makeNull().
 *
 * KEY INSIGHT
 *   Static = bound at compile time to the declared type. Instance = dispatched
 *   at run time on the actual object. Only the second kind can throw NPE.
 *
 * GOTCHAS
 *   - The same rule covers static fields, so ref.CONSTANT reads fine on null.
 *   - Static methods are hidden, not overridden: a subclass reference still
 *     picks the declared type's version, which surprises people in tests.
 *   - IDEs warn "static member accessed via instance reference" for a reason;
 *     always write ClassName.staticMethod() in real code.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does a static method have no `this`?
 *   - Can a static method be overridden? What is method hiding?
 *   - Which bytecode does each call site compile to, and why does it matter?
 *   - Does Objects.requireNonNull help here, and where would you put it?
 *
 * RUN
 *   main() runs 4 cases (static call, instance call, static field, receiver
 *   side effect) and prints actual vs expected.
 */
class StaticNullRefWithStaticMethhodCall {

    /** Set by makeNullWithSideEffect() so case 4 can observe that it ran. */
    private static String sideEffectMarker = "not run";

    public static void main(String[] args) {
        print("case 1: static method via null ref", staticCallThroughNull(), "staticMethod ran");
        print("case 2: instance method via null ref", instanceCallThroughNull(),
                "NullPointerException");
        print("case 3: static field via null ref", staticFieldThroughNull(), "42");
        print("case 4: receiver expression evaluated", receiverStillEvaluated(),
                "side effect ran");
    }

    /** invokestatic: the null value is never dereferenced, so no NPE. */
    private static String staticCallThroughNull() {
        StaticNullRef ref = null;
        return ref.staticMethod();  // javac turns this into StaticNullRef.staticMethod()
    }

    /** invokevirtual: the receiver IS dereferenced, so this throws. */
    private static String instanceCallThroughNull() {
        StaticNullRef ref = null;
        try {
            ref.instanceMethod();
            return "no exception (unexpected)";
        } catch (NullPointerException e) {
            return "NullPointerException";
        }
    }

    /** Static fields bind to the declared type too, so a null ref reads fine. */
    private static String staticFieldThroughNull() {
        StaticNullRef ref = null;
        return String.valueOf(ref.STATIC_VALUE);
    }

    /** The value of the receiver is discarded, but the expression still runs. */
    private static String receiverStillEvaluated() {
        sideEffectMarker = "not run";
        makeNullWithSideEffect().staticMethod();
        return sideEffectMarker;
    }

    private static StaticNullRef makeNullWithSideEffect() {
        sideEffectMarker = "side effect ran";
        return null;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class StaticNullRef {

    static final int STATIC_VALUE = 42;

    static String staticMethod() {
        return "staticMethod ran";
    }

    String instanceMethod() {
        return "instanceMethod ran";
    }
}
