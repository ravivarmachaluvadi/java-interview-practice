/*
 * =====================================================================
 *  Enums with constant-specific bodies        Java language | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   An enum can declare an abstract method and let every constant supply its
 *   own implementation in a class body. That turns the enum into a small,
 *   exhaustive strategy table: the behaviour lives next to the constant
 *   instead of inside a switch somewhere else.
 *
 * WHAT YOU WILL SEE
 *   SUNDAY.describe()          -> "SUNDAY is a weekend day (value 0)"
 *   MONDAY.describe()          -> "MONDAY starts the work week (value 1)"
 *   MyEnum.valueOf("MONDAY")   -> MONDAY, and .value is 1
 *   MyEnum.valueOf("FUNDAY")   -> IllegalArgumentException
 *   SUNDAY.getClass() == MyEnum.class          -> false (anonymous subclass)
 *   SUNDAY.getDeclaringClass() == MyEnum.class -> true
 *
 * HOW IT WORKS
 *   1. MyEnum declares a final int field, a private constructor and an
 *      abstract method describe().
 *   2. Each constant is followed by { ... }, which compiles to an ANONYMOUS
 *      SUBCLASS of MyEnum overriding describe(). That is why the enum can be
 *      abstract yet still have instances.
 *   3. values() returns a fresh array of the constants in declaration order;
 *      valueOf(String) does an exact-name lookup and throws if it misses.
 *   4. Calling describe() on a constant is ordinary virtual dispatch into that
 *      constant's own subclass body.
 *
 * KEY INSIGHT
 *   A constant-specific body is an anonymous subclass. Because of that,
 *   getClass() is NOT MyEnum - use getDeclaringClass() (or name() and
 *   ordinal()) whenever you need the enum type itself, for example as a map
 *   key or in reflection.
 *
 * GOTCHAS
 *   - values() copies the array on every call; cache it in a hot loop.
 *   - ordinal() shifts the moment someone reorders the constants, so never
 *     persist it; store name() instead.
 *   - Enum constructors are implicitly private and run before any static
 *     field of the enum is initialised.
 *   - Use EnumMap and EnumSet, not HashMap and HashSet, for enum keys.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is a single-constant enum the best singleton in Java?
 *   - Constant-specific bodies versus a switch on the enum: which and why?
 *   - Can an enum implement an interface? Can it extend a class?
 *   - How do enums behave across serialization, and why is that safe?
 *
 * RUN
 *   main() runs 3 cases (every constant, lookup by name including the bad
 *   name, and the anonymous-subclass check) and prints actual vs expected.
 */
class EnumDemo {

    public static void main(String[] args) {
        // Case 1: iterate the constants in declaration order.
        StringBuilder all = new StringBuilder();
        for (MyEnum constant : MyEnum.values()) {
            all.append("[").append(constant.describe()).append("]");
        }
        print("case 1: every constant", all,
                "[SUNDAY is a weekend day (value 0)][MONDAY starts the work week (value 1)]");
        print("case 1: how many constants", MyEnum.values().length, 2);

        // Case 2: lookup by name, including the failing edge case.
        print("case 2: valueOf(\"MONDAY\").value", MyEnum.valueOf("MONDAY").value, 1);
        print("case 2: valueOf(\"FUNDAY\")", lookupOrError("FUNDAY"), "IllegalArgumentException");

        // Case 3: a constant-specific body means the constant is an anonymous subclass.
        print("case 3: getClass() == MyEnum.class", MyEnum.SUNDAY.getClass() == MyEnum.class,
                false);
        print("case 3: getDeclaringClass() == MyEnum.class",
                MyEnum.SUNDAY.getDeclaringClass() == MyEnum.class, true);
        print("case 3: name and ordinal", MyEnum.MONDAY.name() + "/" + MyEnum.MONDAY.ordinal(),
                "MONDAY/1");
    }

    /** valueOf() is an exact-name lookup; anything else is an IllegalArgumentException. */
    private static String lookupOrError(String name) {
        try {
            return MyEnum.valueOf(name).name();
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException";
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

enum MyEnum {

    // Each { ... } body compiles to an anonymous subclass of MyEnum.
    SUNDAY(0) {
        @Override
        String describe() {
            return name() + " is a weekend day (value " + value + ")";
        }
    },
    MONDAY(1) {
        @Override
        String describe() {
            return name() + " starts the work week (value " + value + ")";
        }
    };

    final int value;

    MyEnum(int value) {
        this.value = value;
    }

    /** Abstract, so every constant is forced to supply its own behaviour. */
    abstract String describe();
}
