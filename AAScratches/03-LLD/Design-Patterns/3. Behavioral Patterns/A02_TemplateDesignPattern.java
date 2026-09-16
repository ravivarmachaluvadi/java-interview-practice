/**
 * The Template Method Design Pattern is one of
 * <p>
 * the behavioral design patterns in Java
 * It defines the skeleton of an algorithm in a base class
 * <p>
 * (called a template), but allows subclasses to override
 * <p>
 * specific steps of the algorithm without changing its overall structure.
 * <p>
 * You have an algorithm with fixed steps, but some steps vary
 * <p>
 * depending on the context (type, business rule, etc.).
 * <p>
 * You want to avoid duplicating the same algorithm while allowing
 * <p>
 * flexibility for parts that can differ.
 */

abstract class DataProcessor {
    public final void processData() {
        readData();
        parseData();
        validateData();
        saveData();
    }

    private void saveData() {
        System.out.println("Saving data to database...");
    }

    protected void validateData() {
        System.out.println("Validating data...");

    }

    abstract void readData();

    abstract void parseData();

}


class CSVDataProcessor extends DataProcessor {

    @Override
    void readData() {
        System.out.println("Reading data from CSV file");

    }

    @Override
    void parseData() {
        System.out.println("Parsing CSV data");
    }
}

class XMLDataProcessor extends DataProcessor {

    @Override
    void readData() {
        System.out.println("Reading data from XML file");
    }

    @Override
    void parseData() {
        System.out.println("Parsing XML data");
    }
}

class TemplateDesignPattern {
    public static void main(String[] args) {
        DataProcessor csvProcessor = new CSVDataProcessor();
        csvProcessor.processData();

        System.out.println("----");

        DataProcessor xmlProcessor = new XMLDataProcessor();
        xmlProcessor.processData();
    }
}
