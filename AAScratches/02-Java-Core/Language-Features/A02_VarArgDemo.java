/*
 * =====================================================================
 *  Varargs and overload resolution              Java language | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   A varargs parameter (T... args) is just an array parameter (T[] args) with
 *   compiler sugar at the call site, and varargs is the LAST resort when the
 *   compiler picks between overloads.
 *
 * WHAT YOU WILL SEE
 *   varArgDemo(1, 2, 3)                -> Integer... with 3 args
 *   varArgDemo(new Integer[]{1,2,3})   -> Integer... with 3 args (array passed as-is)
 *   varArgDemo(new String[]{"a","b"})  -> String... with 2 args
 *   varArgDemo("")                     -> String... with 1 arg
 *   varArgDemo(list.toArray(new Integer[0])) -> Integer... with 2 args
 *   pick(7)                            -> fixed-arity Integer, not the varargs one
 *
 *   Fixed: the original file called varArgDemo(someList). A List<Integer> is
 *   not an Integer[], so that did not compile. The fix is to hand over an
 *   array: list.toArray(new Integer[0]).
 *
 * HOW IT WORKS
 *   1. At a call site javac wraps the loose arguments into a new array, so
 *      varArgDemo(1, 2, 3) becomes varArgDemo(new Integer[]{1, 2, 3}).
 *   2. If you already pass an array of the right type, no wrapping happens and
 *      that exact array instance is used.
 *   3. Overloads are chosen in three phases:
 *        phase 1 - exact match / widening, no boxing, no varargs
 *        phase 2 - boxing and unboxing allowed, still no varargs
 *        phase 3 - varargs considered
 *      The first phase that finds a match wins, which is why pick(7) picks the
 *      fixed-arity Integer overload.
 *
 * KEY INSIGHT
 *   Varargs is array sugar resolved at compile time. Two rules cover almost
 *   every trick question: a varargs method is never chosen while any
 *   fixed-arity method applies, and passing an array skips the wrapping.
 *
 * GOTCHAS
 *   - varArgDemo() with no arguments would not compile here: both Integer...
 *     and String... match an empty call and neither is more specific.
 *   - Passing a single null to T... is ambiguous between "the whole array is
 *     null" and "one null element"; cast it, e.g. (String) null.
 *   - A collection is not an array, so a List never satisfies T... directly.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What does varargs compile down to in the bytecode?
 *   - Why can varargs only be the last parameter?
 *   - Why does the compiler warn about generic varargs, and what is @SafeVarargs?
 *   - What does f(null) resolve to when overloads take String... and Object...?
 *
 * RUN
 *   main() runs 6 cases (loose args, an Integer[] passed directly, a String[]
 *   passed directly, a single literal, a list converted to an array, and
 *   fixed-arity vs varargs) and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class VarArgDemo {

    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(10);
        numbers.add(20);

        Integer[] boxed = {1, 2, 3};
        String[] words = {"a", "b"};

        print("case 1: three loose ints", varArgDemo(1, 2, 3), "Integer... with 3 args");
        print("case 2: Integer[] passed directly", varArgDemo(boxed), "Integer... with 3 args");
        print("case 3: String[] passed directly", varArgDemo(words), "String... with 2 args");
        print("case 4: single empty string", varArgDemo(""), "String... with 1 args");
        // A List is not an array, so it must be converted first - this was the original bug.
        print("case 5: list converted to array", varArgDemo(numbers.toArray(new Integer[0])),
                "Integer... with 2 args");
        print("case 6: fixed-arity beats varargs", pick(7), "fixed-arity Integer");
    }

    private static String varArgDemo(Integer... list) {
        return "Integer... with " + list.length + " args";
    }

    private static String varArgDemo(String... list) {
        return "String... with " + list.length + " args";
    }

    /** Phase 2 (boxing) finds this, so phase 3 (varargs) is never reached. */
    private static String pick(Integer value) {
        return "fixed-arity Integer";
    }

    private static String pick(Integer... values) {
        return "varargs Integer...";
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
