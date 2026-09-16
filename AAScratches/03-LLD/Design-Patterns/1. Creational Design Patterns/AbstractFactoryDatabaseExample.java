import java.awt.*;
import java.util.Scanner;

/**
 * Abstract Factory Pattern is a creational design pattern
 * <p>
 * that lets you create families of related objects without
 * <p>
 * specifying their concrete classes.
 * <p>
 * | Component           | Description                                               |
 * | ------------------- | --------------------------------------------------------- |
 * | AbstractFactory     | Interface that declares creation methods for products     |
 * | ConcreteFactory     | Creates products specific to a variant (e.g. Windows/Mac) |
 * | AbstractProduct     | Interface for a product type (e.g. Button)                |
 * | ConcreteProduct     | Variant of product (e.g. WindowsButton, MacButton)        |
 * | Client              | Uses the factory to create products                       |
 */
interface Connection {
    void connect();

    void disconnect();
}

interface QueryExecutor {
    void execute(String query);
}

class MySqlConnection implements Connection {

    @Override
    public void connect() {
        System.out.println("Connecting to MySql Database");
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting to MySql Database");
    }
}

class MySqlQueryExecutor implements QueryExecutor {

    @Override
    public void execute(String query) {
        System.out.println("Executing MySql query");
    }
}

class PostgresConnection implements Connection {


    @Override
    public void connect() {
        System.out.println("Connecting to Postgres Database");
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting to Postgres Database");
    }
}

class PostgresExecutor implements QueryExecutor {

    @Override
    public void execute(String query) {
        System.out.println("Executing Postgres query");
    }
}

// 4️⃣ Abstract Factory
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
}

/**
 * 💡 Extension Idea
 * <p>
 * Add one more family: OracleFactory, OracleConnection,
 * <p>
 * OracleQueryExecutor — no need to change client code.
 * <p>
 * That’s the power of Abstract Factory.
 */
class AbstractFactoryDatabaseExample {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String dbType = sc.next();
        DatabaseFactory databaseFactory;

        if (dbType.equalsIgnoreCase("mysql")) {
            databaseFactory = new MySqlFactory();
        } else {
            databaseFactory = new PostgresFactory();
        }
        DatabaseClient databaseClient = new DatabaseClient(databaseFactory);
        databaseClient.run("Select * from Users");
    }

}
