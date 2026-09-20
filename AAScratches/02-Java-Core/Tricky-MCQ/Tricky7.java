/*
 * =====================================================================
 *  Redeclaring a local variable in a nested block       Tricky MCQ | Easy
 * =====================================================================
 *
 *  ** THIS FILE DOES NOT COMPILE, ON PURPOSE. **
 *  The compile error IS the answer. Do not "fix" it - if it compiled, the
 *  puzzle would be gone. Expect javac to fail with exit code 1.
 *
 * QUESTION
 *   public static void main(String[] args) {
 *       int x = 9;
 *       if (x == 9) {
 *           int x = 10;          // <-- this line
 *           System.out.println(x);
 *       }
 *   }
 *
 *   What happens?
 *
 * OPTIONS
 *   A. Prints 10 - the inner x shadows the outer one
 *   B. Prints 9  - the inner declaration is ignored
 *   C. Compile error: variable x is already defined in method main(String[])
 *   D. Runtime error: duplicate local variable
 *
 * ---------------------------------------------------------------------
 *  ANSWER  ->  C.  Compile error:
 *                  "variable x is already defined in method main(String[])"
 * ---------------------------------------------------------------------
 *
 * WHY
 *   1. A local variable's SCOPE runs from its declaration to the end of the
 *      block that contains it. The outer x is declared in the method body, so
 *      it is in scope for everything inside that body - including the if block.
 *   2. Java forbids declaring a second local variable with the same name while
 *      the first is still in scope. There is no "inner beats outer" rule for
 *      locals, unlike C, C++, JavaScript or Python.
 *   3. This is a COMPILE-time rule, so the program never starts. There is no
 *      runtime error to catch.
 *
 * KEY INSIGHT
 *   Locals cannot shadow locals. Locals CAN shadow fields, and that is the
 *   distinction the question is really testing:
 *       class C {
 *           int x = 9;                      // field
 *           void m() { int x = 10; }        // legal: local shadows the field,
 *       }                                   // reach the field via this.x
 *   So "int x = 10;" is fine when the outer x is a field, and illegal when the
 *   outer x is another local still in scope. Ask "field or local?" first.
 *
 * GOTCHAS
 *   - Two SIBLING blocks may each declare their own x, because neither x is in
 *     scope when the other is declared:
 *         if (c) { int x = 1; }  else { int x = 2; }     // legal
 *   - A lambda body cannot redeclare an enclosing local either - a lambda does
 *     not open a new naming scope the way an anonymous class does.
 *   - Method parameters count as locals: "void m(int x) { int x = 1; }" fails.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same code but the outer x is an instance field. What prints, and how do
 *     you still read the field?
 *   - Why can a for-loop index i be reused in two consecutive loops?
 *   - What does "effectively final" require of a local captured by a lambda?
 *   - Does an anonymous inner class get to redeclare an enclosing local?
 *
 * RUN
 *   Nothing runs. Compilation is expected to fail on the "int x = 10;" line,
 *   and that failure is the expected result.
 */
public class Tricky7 {
    public static void main(String[] args) {
        int x = 9;                   // scope: from here to the end of main

        if (x == 9) {
            // Compile error on the next line - this is the point of the file.
            // "variable x is already defined in method main(String[])"
            int x = 10;
            System.out.println(x);
        }
    }
}
