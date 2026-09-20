/*
 * =====================================================================
 *  Two main() methods in one class                        Tricky MCQ | Easy
 * =====================================================================
 *
 * QUESTION
 *   public class Tricky6 {
 *       public static void main(String[] args)      { print("String Main"); }
 *       public static void main(Character[] args)   { print("Character Main"); }
 *   }
 *
 *   Does this class compile, and what does "java Tricky6" print?
 *
 * OPTIONS
 *   A. Compile error: main() is already defined in Tricky6
 *   B. Prints "String Main"
 *   C. Prints "Character Main"
 *   D. Prints both lines, in declaration order
 *
 * ---------------------------------------------------------------------
 *  ANSWER  ->  B.  It compiles, and running it prints only "String Main".
 * ---------------------------------------------------------------------
 *
 * WHY
 *   1. A method signature is name + parameter types. String[] and Character[]
 *      are different parameter types, so these are two legal OVERLOADS, not a
 *      duplicate definition. The compiler is happy.
 *   2. main is not special to the compiler. It is special only to the JVM
 *      launcher, which looks for exactly one signature:
 *          public static void main(String[])
 *      That is the only one it will call as the entry point.
 *   3. main(Character[]) is therefore just an ordinary static method that
 *      happens to be named main. Nothing calls it unless your code does -
 *      which is exactly what case 2 below does.
 *
 * KEY INSIGHT
 *   "Entry point" is a launcher contract, not a language rule. Overloading
 *   main is legal; only the String[] overload is ever auto-invoked. The same
 *   reasoning explains why main can be overLOADED but never overRIDDEN - it
 *   is static, so subclasses hide it rather than override it.
 *
 * GOTCHAS
 *   - Change the parameter to String... (varargs) and it STILL works: varargs
 *     of String compile to String[], so the signature is unchanged.
 *   - Drop "static", rename args, or return something other than void, and the
 *     launcher fails at runtime with "main method not found in class ...".
 *   - The parameter name (args) is irrelevant; only the type matters.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Is "public static void main(String... args)" a valid entry point? Why?
 *   - Can main() be final, synchronized, or declared to throw Exception?
 *   - Can a subclass override main()? What actually happens instead?
 *   - What runs before main - static initializer blocks or the JVM launcher?
 *
 * RUN
 *   main() runs 2 cases: the JVM-chosen entry point, and an explicit call to
 *   the Character[] overload. Each prints actual vs expected.
 */
class Tricky6 {

    /** The one signature the JVM launcher recognises as the entry point. */
    public static void main(String[] args) {
        System.out.print("case 1 (JVM entry point):        ");
        System.out.println("String Main" + "        expected String Main");

        // Case 2 - proof that the other main is a normal method: nothing calls
        // it automatically, but we can call it by hand like any static method.
        System.out.print("case 2 (explicit overload call): ");
        main(new Character[]{'a', 'b'});
    }

    /** A legal overload, but invisible to the launcher. */
    public static void main(Character[] args) {
        System.out.println("Character Main" + "     expected Character Main");
    }
}
