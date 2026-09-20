/*
 * =====================================================================
 *  Template Method Design Pattern           LLD | Easy  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Template Method - Behavioral family (GoF), inheritance based.
 *
 * INTENT
 *   Put the fixed skeleton of an algorithm in a final method on a base class, and
 *   leave the steps that differ as abstract methods subclasses must fill, or as hooks
 *   they may optionally override. The order of the steps can never be changed by a
 *   subclass; only the content of individual steps can.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when: several flows share the same step sequence and differ in one or two
 *     steps - ETL readers, request lifecycles, test setUp/run/tearDown, framework
 *     callbacks. It removes copy-pasted orchestration code.
 *   Do not use when: the variation must be chosen at runtime or combined freely -
 *     that is Strategy. Deep template hierarchies get brittle fast; one level is plenty.
 *
 * ROLES IN THIS CODE
 *   DataProcessor         -> AbstractClass. processData() is the template method and is
 *                            final so no subclass can reorder or skip steps.
 *   readData, parseData   -> primitive operations (abstract, subclass MUST supply)
 *   validateData          -> hook (concrete default, subclass MAY override)
 *   needsValidation       -> boolean hook that lets a subclass switch a step off
 *   saveData              -> invariant step, private, not overridable at all
 *   CSVDataProcessor,
 *   XMLDataProcessor,
 *   JSONDataProcessor     -> ConcreteClass
 *
 * KEY INSIGHT
 *   Inverted control: the base class calls down into the subclass ("don't call us,
 *   we'll call you"), which is why every framework you use is built this way. Mark the
 *   template method final and keep the invariant steps private - that is what makes the
 *   guarantee real rather than a convention.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Template Method vs Strategy: one algorithm with fixed holes, chosen at compile
 *     time by subclassing, vs a whole algorithm object swapped at runtime.
 *   - What is a "hook" and how does it differ from an abstract step?
 *   - Why does the template method have to be final?
 *   - How would you get the same effect with composition and no inheritance?
 *     (pass the varying steps in as functions - effectively Strategy per step)
 *
 * RUN
 *   main() runs 3 cases: two standard processors, plus a JSON processor whose hook
 *   turns validation off. Each prints the executed step list vs the expected list.
 */

import java.util.ArrayList;
import java.util.List;

abstract class DataProcessor {

    /**
     * The template method: the step order is fixed here and cannot be changed,
     * because the method is final. Returns the trace of executed steps so that
     * main() can assert on it.
     */
    public final List<String> processData() {
        List<String> steps = new ArrayList<>();
        steps.add(readData());
        steps.add(parseData());
        if (needsValidation()) {           // boolean hook - a subclass can skip this step
            steps.add(validateData());
        }
        steps.add(saveData());
        return steps;
    }

    /** Invariant step: private, so no subclass can change how saving works. */
    private String saveData() {
        return "save to database";
    }

    /** Hook with a sensible default; subclasses may override it. */
    protected String validateData() {
        return "validate";
    }

    /** Hook that switches an optional step on or off. */
    protected boolean needsValidation() {
        return true;
    }

    /** Primitive operations: every subclass must supply these. */
    abstract String readData();

    abstract String parseData();
}

class CSVDataProcessor extends DataProcessor {

    @Override
    String readData() {
        return "read CSV file";
    }

    @Override
    String parseData() {
        return "parse CSV";
    }
}

class XMLDataProcessor extends DataProcessor {

    @Override
    String readData() {
        return "read XML file";
    }

    @Override
    String parseData() {
        return "parse XML";
    }

    /** Overriding the hook changes one step without touching the skeleton. */
    @Override
    protected String validateData() {
        return "validate against XSD";
    }
}

/** Edge case: a source that is already schema-checked upstream skips validation. */
class JSONDataProcessor extends DataProcessor {

    @Override
    String readData() {
        return "read JSON file";
    }

    @Override
    String parseData() {
        return "parse JSON";
    }

    @Override
    protected boolean needsValidation() {
        return false;
    }
}

class TemplateDesignPattern {

    public static void main(String[] args) {
        print("case 1 csv (default hook)", new CSVDataProcessor().processData(),
                "[read CSV file, parse CSV, validate, save to database]");

        print("case 2 xml (hook overridden)", new XMLDataProcessor().processData(),
                "[read XML file, parse XML, validate against XSD, save to database]");

        print("case 3 json (step skipped)", new JSONDataProcessor().processData(),
                "[read JSON file, parse JSON, save to database]");
    }

    private static void print(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + "   " + (ok ? "[OK]" : "[FAIL]"));
    }
}
