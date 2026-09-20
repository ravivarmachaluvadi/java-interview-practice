/*
 * =====================================================================
 *  Adapter Pattern (Wrapper)                  Design Pattern | Easy  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Adapter - Structural family. Also called Wrapper.
 *
 * INTENT
 *   Let two classes with incompatible interfaces work together. The adapter
 *   implements the interface the CLIENT expects and translates every call
 *   into the interface the existing (adaptee) class actually offers.
 *   Here: a legacy CSV source is made to look like a JSON source.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    - you must reuse a class you cannot (or must not) modify:
 *            third-party SDK, legacy service, a different data format.
 *   Use    - you want the translation isolated in one place, not smeared
 *            across every caller.
 *   Not    - you control both sides and can just change the interface.
 *   Not    - you want to ADD behaviour to the same interface -> Decorator.
 *   Not    - you want to hide a whole subsystem behind one call -> Facade.
 *
 * ROLES IN THIS CODE
 *   JsonDataProvider       Target    - the interface the client codes against.
 *   CsvDataProvider        Adaptee   - the existing, incompatible class.
 *   CsvToJsonDataProvider  Adapter   - implements Target, holds an Adaptee,
 *                                      translates CSV text into JSON text.
 *   AdapterPatternDemo     Client    - talks only to JsonDataProvider.
 *
 * KEY INSIGHT
 *   Object Adapter = "implement what the caller wants, hold what you have,
 *   translate in between". The client never learns that CSV exists. This one
 *   move (wrap + delegate) is the atom of Decorator, Proxy, Facade and Bridge;
 *   what separates those four is INTENT, not structure.
 *   Fixed: the original adapter called getDataInCsv() and then returned the
 *   hardcoded string "Json data", so it never actually adapted anything.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Object Adapter (composition, shown here) vs Class Adapter (inheritance)?
 *   - Adapter vs Facade: same interface count, different intent - explain.
 *   - Two-way adapter: can one class adapt A->B and B->A? When is that a smell?
 *   - Real examples: InputStreamReader, Arrays.asList, Spring HandlerAdapter.
 *
 * RUN
 *   main() runs 4 cases (typical, header-only, empty input, short row) and
 *   prints actual vs expected on the same line.
 */

/** Adaptee: an existing source that only speaks CSV, whose interface we cannot change. */
class CsvDataProvider {

    private final String csv;

    public CsvDataProvider() {
        this("name,age\nRavi,30\nVarma,28");
    }

    public CsvDataProvider(String csv) {
        this.csv = csv;
    }

    public String getDataInCsv() {
        return csv;
    }
}

/** Target: the only interface the client knows about. */
interface JsonDataProvider {
    String getDataInJson();
}

/** Adapter: looks like a JsonDataProvider, is really a CsvDataProvider underneath. */
class CsvToJsonDataProvider implements JsonDataProvider {

    private final CsvDataProvider csvDataProvider;

    public CsvToJsonDataProvider(CsvDataProvider csvDataProvider) {
        this.csvDataProvider = csvDataProvider;
    }

    @Override
    public String getDataInJson() {
        String dataInCsv = csvDataProvider.getDataInCsv(); // speak the adaptee's language
        return toJson(dataInCsv);                          // translate to the client's language
    }

    /** Minimal CSV -> JSON array translation. Row 0 is the header row. */
    private static String toJson(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return "[]";
        }
        String[] lines = csv.split("\n");
        String[] headers = lines[0].split(",");

        StringBuilder json = new StringBuilder("[");
        boolean firstRow = true;
        for (int row = 1; row < lines.length; row++) {
            if (lines[row].trim().isEmpty()) {
                continue; // tolerate blank lines rather than emitting an empty object
            }
            if (!firstRow) {
                json.append(",");
            }
            firstRow = false;

            String[] cells = lines[row].split(",");
            json.append("{");
            for (int col = 0; col < headers.length; col++) {
                if (col > 0) {
                    json.append(",");
                }
                String value = col < cells.length ? cells[col].trim() : ""; // short row -> empty
                json.append("\"").append(headers[col].trim()).append("\":\"").append(value).append("\"");
            }
            json.append("}");
        }
        return json.append("]").toString();
    }
}

class AdapterPatternDemo {

    public static void main(String[] args) {
        // Case 1: typical - two data rows.
        JsonDataProvider typical = new CsvToJsonDataProvider(new CsvDataProvider());
        print("case 1 typical      ", typical.getDataInJson(),
                "[{\"name\":\"Ravi\",\"age\":\"30\"},{\"name\":\"Varma\",\"age\":\"28\"}]");

        // Case 2: edge - header row only, no data rows.
        JsonDataProvider headerOnly = new CsvToJsonDataProvider(new CsvDataProvider("name,age"));
        print("case 2 header only  ", headerOnly.getDataInJson(), "[]");

        // Case 3: tricky - empty source, and a short row that is missing a cell.
        JsonDataProvider empty = new CsvToJsonDataProvider(new CsvDataProvider(""));
        print("case 3 empty source ", empty.getDataInJson(), "[]");

        JsonDataProvider shortRow = new CsvToJsonDataProvider(new CsvDataProvider("name,age\nRavi"));
        print("case 4 missing cell ", shortRow.getDataInJson(), "[{\"name\":\"Ravi\",\"age\":\"\"}]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " : " + actual + "   expected " + expected);
    }
}
