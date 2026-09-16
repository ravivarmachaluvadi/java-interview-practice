/**
 * The Adapter Pattern (also known as Wrapper Pattern) is
 * <p>
 * a structural design pattern that allows two incompatible
 * <p>
 * interfaces to work together. It acts as a bridge between an existing class
 * <p>
 * The Adapter Pattern converts the interface of a class
 * <p>
 * into another interface that the client expects.
 * <p>
 * 🧠 When to Use
 * <p>
 * You want to use an existing class, but its interface does not match what your code expects.
 * <p>
 * You want to reuse existing code without modifying it.
 * <p>
 * You want to integrate a third-party library or legacy code with your system.
 */

class CsvDataProvider {
    public String getDataInCsv() {
        return "name,age\nRavi,30\nVarma,28";
    }
}

interface JsonDataProvider {
    String getDataInJson();
}

class CsvToJsonDataProvider implements JsonDataProvider {

    private final CsvDataProvider csvDataProvider;

    public CsvToJsonDataProvider(CsvDataProvider csvDataProvider) {
        this.csvDataProvider = csvDataProvider;
    }

    @Override
    public String getDataInJson() {
        String dataInCsv = csvDataProvider.getDataInCsv();
        System.out.println("data in csv converted to json");
        return "Json data";
    }
}

public class AdapterPatternDemo {
    public static void main(String[] args) {
        CsvDataProvider csvDataProvider = new CsvDataProvider();
        JsonDataProvider jsonDataProvider = new CsvToJsonDataProvider(csvDataProvider);
        String dataInJson = jsonDataProvider.getDataInJson();

        System.out.println(dataInJson);

    }
}
