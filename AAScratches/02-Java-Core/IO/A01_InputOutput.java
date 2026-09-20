/*
 * =====================================================================
 *  Read a number from standard input                 Java Core | IO | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   The smallest possible console-input program: wrap a Scanner around an input
 *   source, pull one int token out of it, print it back. Every other file in this
 *   folder assumes you can do this without thinking.
 *
 * WHAT YOU WILL SEE
 *   "42"    -> 42      a plain token
 *   " -7 "  -> -7      surrounding whitespace is skipped, the sign is not
 *   "abc"   -> -1      nextInt() would THROW here; the guarded reader returns a fallback
 *
 * HOW IT WORKS
 *   1. A Scanner splits its source into tokens on whitespace (spaces, tabs, newlines).
 *   2. nextInt() takes the next token and parses it as an int. It does NOT consume
 *      the newline that follows the token - that matters the moment you mix it with
 *      nextLine() (see A02_ScannerMain).
 *   3. hasNextInt() peeks: it reports whether the NEXT token would parse as an int,
 *      without consuming anything. That is the only safe way to handle junk input.
 *
 * KEY INSIGHT
 *   Scanner is a token stream, not a line reader. nextInt() means "give me the next
 *   whitespace-delimited token and parse it", so it happily reads across line breaks
 *   and leaves the line break behind. Check with hasNextInt() before you read, or be
 *   ready for InputMismatchException (bad token) and NoSuchElementException (no token).
 *
 * GOTCHAS
 *   - No input at all -> NoSuchElementException, not a friendly error.
 *   - Non-numeric token -> InputMismatchException, and the bad token stays in the
 *     buffer, so a naive retry loop spins forever unless you call next() to drop it.
 *   - Scanner is slow for large input; competitive/high-volume code uses
 *     BufferedReader + Integer.parseInt instead.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is BufferedReader faster than Scanner? (no regex tokenising per read)
 *   - How do you recover from InputMismatchException without an infinite loop?
 *   - What happens to System.in if you close the Scanner? (it closes the stream too)
 *   - Reading 10^6 integers: which reader, and why?
 *
 * RUN
 *   main() runs 3 cases (typical, negative, junk input) against a String sample so
 *   the file is reproducible; one commented line shows how to switch to System.in.
 */
import java.util.Scanner;

class InputOutput {

    /** The author's original read: assumes the next token really is an int. */
    static int readNumber(Scanner sc) {
        return sc.nextInt();
    }

    /** Same read, but peeks first so junk input returns a fallback instead of throwing. */
    static int readNumberOrDefault(Scanner sc, int fallback) {
        if (!sc.hasNextInt()) {
            if (sc.hasNext()) {
                sc.next(); // drop the offending token, else the next call sees it again
            }
            return fallback;
        }
        return sc.nextInt();
    }

    public static void main(String[] args) {
        // Live version: Scanner sc = new Scanner(System.in);
        //               System.out.println("Enter a number:");
        //               System.out.println(readNumber(sc));

        try (Scanner typical = new Scanner("42")) {
            System.out.println("case 1 typical : " + readNumber(typical) + "   expected 42");
        }

        try (Scanner negative = new Scanner("   -7   ")) {
            System.out.println("case 2 negative: " + readNumber(negative) + "   expected -7");
        }

        try (Scanner junk = new Scanner("abc")) {
            int value = readNumberOrDefault(junk, -1);
            System.out.println("case 3 junk    : " + value + "   expected -1");
        }
    }
}
