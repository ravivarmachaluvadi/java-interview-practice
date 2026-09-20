/*
 * =====================================================================
 *  Logging an exception so the stack trace survives   Java Core | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   The single most common logging bug in Java services: log.error("boom: " + e)
 *   prints only the exception's toString (class name and message) and throws the
 *   stack trace away. Passing the exception as an extra TRAILING argument -
 *   log.error("boom {}", id, e) - prints the full trace with every frame.
 *
 *   The original file used Lombok's @Slf4j and a real SLF4J binding. Those are
 *   external jars, so this scratch file ships MiniLogger: about 30 lines that
 *   implement SLF4J's own MessageFormatter rule, letting the demo run with
 *   nothing on the classpath. The call sites are exactly what you would write
 *   against a real logger.
 *
 * WHAT YOU WILL SEE
 *   log.error("error : " + e)          -> one line, no frames          (BAD)
 *   log.error("error {}", "123", e)    -> "error 123" plus full trace  (GOOD)
 *   log.error("error {} {}", "123", e) -> "error 123 java.lang..."     (TRAP)
 *                                         two placeholders swallow the throwable
 *
 * HOW IT WORKS
 *   1. SLF4J never formats with String.format; it scans the message for "{}"
 *      and substitutes the arguments in order. That is why it is cheap: nothing
 *      is built when the level is disabled.
 *   2. After substitution, if the LAST argument is a Throwable and there were
 *      more arguments than placeholders, it is not a placeholder value - it is
 *      the exception, and the appender prints its stack trace.
 *   3. So the throwable must be left over. Add a placeholder for it and it gets
 *      formatted as text instead, and the trace is lost.
 *   4. Concatenation never leaves anything over, so it never prints a trace.
 *
 * KEY INSIGHT
 *   The exception argument is positional, not typed magic: it must be the last
 *   argument AND unmatched by any "{}". Remember it as "one more argument than
 *   you have braces". Same rule in Logback and Log4j2.
 *
 * GOTCHAS
 *   - log.error("failed", e.getMessage()) loses the trace too - a String is not
 *     a Throwable, so nothing triggers the trace printing.
 *   - Do not log and rethrow the same exception; the caller logs it again and
 *     you get the trace twice under two different timestamps.
 *   - e.printStackTrace() bypasses the logging framework entirely: no level,
 *     no appender, no correlation id.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why parameterized logging instead of concatenation, even without an
 *     exception? (no string is built when the level is off)
 *   - What does getCause()/initCause() give you here? (the "Caused by" chain)
 *   - How do you keep the trace when wrapping? (new ServiceException(msg, e))
 *   - Where does the MDC fit in? (per-thread context such as a request id)
 *
 * RUN
 *   main() runs 3 cases (concatenation, correct trailing throwable, and the
 *   too-many-placeholders trap) and prints actual vs expected.
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

class ExceptionLoggingDemo {

    private static final MiniLogger log = new MiniLogger(ExceptionLoggingDemo.class.getName());

    public static void main(String[] args) {
        m1();
    }

    private static void m1() {
        m2();
    }

    private static void m2() {
        longName();
    }

    /** Deliberately several frames deep so a real stack trace has something to show. */
    private static void longName() {
        try {
            throw new RuntimeException("some exception");
        } catch (RuntimeException e) {

            // BAD: the exception is flattened into the message, so only its
            // toString survives and every frame is lost.
            banner("1 concatenation      ", "log.error(\"error : \" + e)");
            report("1 concatenation      ", log.error("error : " + e), false);

            // GOOD: one "{}" consumes "123", the trailing throwable is left over
            // and is therefore logged as the exception, trace and all.
            banner("2 trailing throwable ", "log.error(\"error {}\", \"123\", e)");
            report("2 trailing throwable ", log.error("error {}", "123", e), true);

            // TRAP: two "{}" for two arguments, so the throwable fills the second
            // placeholder as text and nothing is left over to print a trace.
            banner("3 swallowed by {}    ", "log.error(\"error {} {}\", \"123\", e)");
            report("3 swallowed by {}    ", log.error("error {} {}", "123", e), false);
        }
    }

    private static void banner(String label, String callSite) {
        System.out.println("---- case " + label + ": " + callSite + " ----");
    }

    /** The record was already printed by the logger; this line is the assertion. */
    private static void report(String label, String record, boolean expectTrace) {
        System.out.println("case " + label + ": stackFrames=" + countFrames(record)
                + " hasTrace=" + (countFrames(record) > 0)
                + "   expected hasTrace=" + expectTrace);
        System.out.println();
    }

    /** A stack frame line is "\tat some.Class.method(File.java:NN)". */
    private static int countFrames(String record) {
        int frames = 0;
        for (String line : record.split("\\R")) {
            if (line.strip().startsWith("at ")) {
                frames++;
            }
        }
        return frames;
    }
}

/**
 * The smallest logger that reproduces SLF4J's argument rule. Real code uses
 * Lombok's @Slf4j to generate the field and a binding such as Logback behind it.
 */
class MiniLogger {

    private final String name;

    MiniLogger(String name) {
        this.name = name;
    }

    /** Prints the record and also returns it so tests can assert on what was logged. */
    String error(String format, Object... args) {
        String record = "ERROR " + name + " -- " + render(format, args);
        System.out.println(record);
        return record;
    }

    /**
     * SLF4J's rule: substitute "{}" left to right; if the last argument is a
     * Throwable AND there are more arguments than placeholders, it is the
     * exception rather than a placeholder value, so print its stack trace.
     */
    private String render(String format, Object... args) {
        int placeholders = countPlaceholders(format);
        Object[] messageArgs = args;
        Throwable thrown = null;

        if (args.length > placeholders && args.length > 0
                && args[args.length - 1] instanceof Throwable last) {
            thrown = last;
            messageArgs = Arrays.copyOf(args, args.length - 1);
        }

        StringBuilder out = new StringBuilder(substitute(format, messageArgs));
        if (thrown != null) {
            out.append(System.lineSeparator()).append(stackTraceOf(thrown));
        }
        return out.toString();
    }

    private static int countPlaceholders(String format) {
        int count = 0;
        for (int i = format.indexOf("{}"); i >= 0; i = format.indexOf("{}", i + 2)) {
            count++;
        }
        return count;
    }

    private static String substitute(String format, Object[] args) {
        StringBuilder out = new StringBuilder();
        int from = 0;
        for (Object arg : args) {
            int at = format.indexOf("{}", from);
            if (at < 0) {
                break;          // more arguments than placeholders: the rest are dropped
            }
            out.append(format, from, at).append(arg);
            from = at + 2;
        }
        return out.append(format.substring(from)).toString();
    }

    private static String stackTraceOf(Throwable t) {
        StringWriter buffer = new StringWriter();
        t.printStackTrace(new PrintWriter(buffer));
        return buffer.toString().stripTrailing();
    }
}
