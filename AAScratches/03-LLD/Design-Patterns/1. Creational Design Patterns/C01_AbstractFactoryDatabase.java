/*
 * =====================================================================
 *  Abstract Factory - database driver families   Creational | Medium
 * =====================================================================
 *
 * PATTERN
 *   Abstract Factory - Creational family. One factory interface with
 *   several create* methods, so every concrete factory hands back a
 *   MATCHED SET of products instead of a single object.
 *
 * INTENT
 *   Let the client work with a whole family of related objects - here a
 *   Connection plus the QueryExecutor that speaks the same dialect -
 *   without naming any concrete class, and without any chance of pairing
 *   a MySQL connection with a Postgres executor.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when products come in variants that must not be mixed: DB drivers,
 *     UI widget sets (Windows/Mac button + checkbox), cloud SDK clients.
 *   Use when the variant is chosen once at startup (config, profile) and
 *     everything downstream must follow that choice.
 *   Do NOT use for a single product type - that is plain Factory (A01_FactoryDesignPattern) and
 *     the extra interface buys nothing.
 *   Do NOT use when new PRODUCT TYPES appear often: adding createMigrator()
 *     forces a change in every concrete factory. Abstract Factory makes
 *     adding a new FAMILY cheap and a new product type expensive.
 *
 * ROLES IN THIS CODE
 *   Connection, QueryExecutor                AbstractProduct - the two
 *                                            product types in the family
 *   MySqlConnection, MySqlQueryExecutor      ConcreteProduct, family MySQL
 *   PostgresConnection, PostgresExecutor     ConcreteProduct, family Postgres
 *   DatabaseFactory                          AbstractFactory
 *   MySqlFactory, PostgresFactory            ConcreteFactory - each one
 *                                            returns a self-consistent pair
 *   DatabaseClient                           Client - holds interfaces only
 *   factoryFor(String)                       the one place the variant name
 *                                            is turned into a factory
 *
 * KEY INSIGHT
 *   Abstract Factory is Factory applied to a family. The guarantee is not
 *   "I do not call new" - it is "the objects I get back belong together".
 *   That is why the test below asserts the PAIR of product classes, not
 *   just one of them. Recognise it whenever a config switch must ripple
 *   consistently through several collaborating objects.
 *
 *   Fixed: the original if/else fell through to Postgres for ANY unknown
 *   input, so a typo like "mysqll" silently connected to the wrong engine.
 *   factoryFor() now throws IllegalArgumentException instead.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Factory Method vs Abstract Factory? Factory Method = one product, the
 *     choice made by subclassing. Abstract Factory = a family, the choice
 *     made by which factory object you are handed. Abstract Factories are
 *     usually implemented using factory methods.
 *   - Add Oracle support: write OracleConnection, OracleQueryExecutor,
 *     OracleFactory, add one line to factoryFor(). No client code changes.
 *   - Add createMigrator() to the family: every concrete factory must be
 *     edited - the known cost of this pattern.
 *   - How would Spring do this? Inject DatabaseFactory as a bean chosen by
 *     @Profile or @ConditionalOnProperty; the DI container is the factory.
 *   - Real JDK example: DocumentBuilderFactory, and java.sql.Driver +
 *     Connection selected by URL.
 *
 * RUN
 *   main() runs 4 cases: the two families, an unknown key (edge - must
 *   throw, not fall through), and a scanner-driven selection that reads
 *   from an in-file sample instead of blocking on System.in.
 */

import java.util.Scanner;

interface Connection {
    void connect();

    void disconnect();
}

interface QueryExecutor {
    void execute(String query);
}

// ---------- family 1: MySQL ----------

class MySqlConnection implements Connection {

    @Override
    public void connect() {
        System.out.println("Connecting to MySql Database");
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting from MySql Database");
    }
}

class MySqlQueryExecutor implements QueryExecutor {

    @Override
    public void execute(String query) {
        System.out.println("Executing MySql query: " + query);
    }
}

// ---------- family 2: Postgres ----------

class PostgresConnection implements Connection {

    @Override
    public void connect() {
        System.out.println("Connecting to Postgres Database");
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting from Postgres Database");
    }
}

class PostgresExecutor implements QueryExecutor {

    @Override
    public void execute(String query) {
        System.out.println("Executing Postgres query: " + query);
    }
}

// ---------- the abstract factory and its concrete families ----------

/** AbstractFactory: one create* method per product type in the family. */
interface DatabaseFactory {
    Connection createConnection();

    QueryExecutor createQueryExecutor();
}

class MySqlFactory implements DatabaseFactory {

    @Override
    public Connection createConnection() {
        return new MySqlConnection();
    }

    @Override
    public QueryExecutor createQueryExecutor() {
        return new MySqlQueryExecutor();
    }
}

class PostgresFactory implements DatabaseFactory {

    @Override
    public Connection createConnection() {
        return new PostgresConnection();
    }

    @Override
    public QueryExecutor createQueryExecutor() {
        return new PostgresExecutor();
    }
}

/**
 * Client. It never mentions MySql or Postgres - it only knows that the two
 * products it received came from the same factory, so they match.
 */
class DatabaseClient {

    private final Connection connection;
    private final QueryExecutor queryExecutor;

    DatabaseClient(DatabaseFactory databaseFactory) {
        connection = databaseFactory.createConnection();
        queryExecutor = databaseFactory.createQueryExecutor();
    }

    public void run(String query) {
        connection.connect();
        queryExecutor.execute(query);
        connection.disconnect();
    }

    /** Exposes the product pair so a test can assert the family matched. */
    public String products() {
        return connection.getClass().getSimpleName()
                + " + " + queryExecutor.getClass().getSimpleName();
    }
}

class AbstractFactoryDatabaseExample {

    // Stand-in for keyboard input so the demo never blocks.
    // To read the console instead: new Scanner(System.in)
    private static final String SAMPLE_INPUT = "postgres\n";

    public static void main(String[] args) {
        // Case 1-2: each factory yields a self-consistent pair of products.
        print("case 1 mysql   ", new DatabaseClient(factoryFor("mysql")).products(),
                "MySqlConnection + MySqlQueryExecutor");
        print("case 2 postgres", new DatabaseClient(factoryFor("Postgres")).products(),
                "PostgresConnection + PostgresExecutor");

        // Case 3: edge. An unknown variant must fail loudly, not default.
        String outcome;
        try {
            factoryFor("mysqll");
            outcome = "no exception";
        } catch (IllegalArgumentException e) {
            outcome = e.getMessage();
        }
        print("case 3 typo    ", outcome, "Unknown database type: mysqll");

        // Case 4: the variant chosen at runtime from "user" input.
        try (Scanner scanner = new Scanner(SAMPLE_INPUT)) {
            DatabaseClient client = new DatabaseClient(factoryFor(scanner.next()));
            print("case 4 from input", client.products(),
                    "PostgresConnection + PostgresExecutor");
            client.run("select * from users");
        }
    }

    /** The only place a variant name is translated into a factory. */
    private static DatabaseFactory factoryFor(String dbType) {
        if ("mysql".equalsIgnoreCase(dbType)) {
            return new MySqlFactory();
        }
        if ("postgres".equalsIgnoreCase(dbType)) {
            return new PostgresFactory();
        }
        // Fixed: previously any unrecognised value silently became Postgres.
        throw new IllegalArgumentException("Unknown database type: " + dbType);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
