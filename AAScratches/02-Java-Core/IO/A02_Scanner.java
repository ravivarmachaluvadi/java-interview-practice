/*
 * =====================================================================
 *  Scanner token loop, and the next()/nextInt()/nextLine() trap   Java Core | IO | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Reading a fixed number of tokens with Scanner, the nextInt() variant of the same
 *   loop, and the single most common input bug in Java: calling nextLine() straight
 *   after nextInt() and getting an empty string back.
 *
 * WHAT YOU WILL SEE
 *   "alpha beta gamma delta epsilon" , 5 tokens  ->  [alpha, beta, gamma, delta, epsilon]
 *   "alpha beta\ngamma\n  delta epsilon"         ->  same 5 tokens (line breaks are
 *                                                    just whitespace to next())
 *   "1 2 3 4 5" , 5 ints                         ->  [1, 2, 3, 4, 5]
 *   "7\nhello world" : nextInt() then nextLine() ->  ""  (the bug)
 *                      nextInt(), nextLine(), nextLine() -> "hello world"  (the fix)
 *
 * HOW IT WORKS
 *   1. Scanner keeps a position in its input and a delimiter pattern (whitespace).
 *   2. next() / nextInt() skip leading whitespace, consume ONE token, and stop right
 *      after the token - the newline behind it is still unread.
 *   3. nextLine() ignores the delimiter pattern: it consumes everything up to the next
 *      line terminator and returns it. Straight after nextInt() that remainder is the
 *      empty tail of the line you were already on.
 *
 * KEY INSIGHT
 *   Token reads and line reads measure the input differently, so they cannot be mixed
 *   carelessly. next()/nextInt() leave the cursor mid-line; nextLine() starts from the
 *   cursor. The fix is one throwaway nextLine() to finish the current line before you
 *   start reading whole lines - or never mix the two styles in one program at all.
 *
 * GOTCHAS
 *   - The loop below reads exactly 5 tokens; fewer tokens in the input means
 *     NoSuchElementException, not a short result. Guard with hasNext() for real input.
 *   - nextInt() on "3.5" throws InputMismatchException and leaves the token in place.
 *   - Scanner's default delimiter is whitespace, so a CSV needs useDelimiter(",") or,
 *     better, a real line reader plus split (see C01_CsvReader).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does nextLine() return "" after nextInt(), and what exactly is in the buffer?
 *   - How would you read "n" then n whole lines correctly?
 *   - When would you use BufferedReader + StringTokenizer instead, and why is it faster?
 *   - What does scanner.useDelimiter(",\s*") change about next()?
 *
 * RUN
 *   main() runs 4 cases (tokens, tokens across lines, ints, and the nextLine trap shown
 *   broken and fixed side by side) against String samples, printing actual vs expected.
 *   One commented line shows how to switch to System.in.
 */
import java.util.Arrays;
import java.util.Scanner;

class ScannerMain {

    /** The author's original loop: read exactly `count` whitespace-delimited tokens. */
    static String[] readTokens(Scanner in, int count) {
        String[] tokens = new String[count];
        for (int i = 0; i < count; i++) {
            tokens[i] = in.next();
        }
        return tokens;
    }

    /** The nextInt() variant the author had commented out: same loop, parsed as ints. */
    static int[] readInts(Scanner in, int count) {
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = in.nextInt();
        }
        return values;
    }

    /** The bug: nextLine() right after nextInt() returns the empty tail of line 1. */
    static String readLineAfterIntBroken(Scanner in) {
        in.nextInt();
        return in.nextLine();
    }

    /** The fix: one throwaway nextLine() finishes the current line first. */
    static String readLineAfterIntFixed(Scanner in) {
        in.nextInt();
        in.nextLine();           // consume the rest of the line holding the int
        return in.nextLine();
    }

    public static void main(String[] args) {
        // Live version: Scanner in = new Scanner(System.in);
        //               System.out.println(Arrays.toString(readTokens(in, 5)));

        try (Scanner oneLine = new Scanner("alpha beta gamma delta epsilon")) {
            System.out.println("case 1 tokens      : " + Arrays.toString(readTokens(oneLine, 5))
                    + "   expected [alpha, beta, gamma, delta, epsilon]");
        }

        try (Scanner manyLines = new Scanner("alpha beta\ngamma\n  delta epsilon")) {
            System.out.println("case 2 across lines: " + Arrays.toString(readTokens(manyLines, 5))
                    + "   expected [alpha, beta, gamma, delta, epsilon]");
        }

        try (Scanner ints = new Scanner("1 2 3 4 5")) {
            System.out.println("case 3 ints        : " + Arrays.toString(readInts(ints, 5))
                    + "   expected [1, 2, 3, 4, 5]");
        }

        String sample = "7\nhello world";
        try (Scanner broken = new Scanner(sample); Scanner fixed = new Scanner(sample)) {
            System.out.println("case 4 trap broken : \"" + readLineAfterIntBroken(broken)
                    + "\"   expected \"\"");
            System.out.println("case 4 trap fixed  : \"" + readLineAfterIntFixed(fixed)
                    + "\"   expected \"hello world\"");
        }
    }
}
