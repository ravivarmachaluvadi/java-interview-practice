import java.util.*;

class WideColumnStore {
    // Simulating a keyspace with multiple column families
    private Map<String, Map<String, Map<String, String>>> keyspace = new HashMap<>();

    // Add a column family to the keyspace
    public void addColumnFamily(String columnFamilyName) {
        keyspace.putIfAbsent(columnFamilyName, new HashMap<>());
    }

    // Add a row to a column family
    public void addRow(String columnFamilyName, String rowKey) {
        Map<String, Map<String, String>> columnFamily = keyspace.get(columnFamilyName);
        if (columnFamily != null) {
            columnFamily.putIfAbsent(rowKey, new HashMap<>());
        }
    }

    // Add a column to a row
    public void addColumn(String columnFamilyName, String rowKey, String columnName, String value) {
        Map<String, Map<String, String>> columnFamily = keyspace.get(columnFamilyName);
        if (columnFamily != null) {
            Map<String, String> row = columnFamily.get(rowKey);
            if (row != null) {
                row.put(columnName, value);
            }
        }
    }

    // Retrieve a column value
    public String getColumn(String columnFamilyName, String rowKey, String columnName) {
        return keyspace.getOrDefault(columnFamilyName, Collections.emptyMap())
                .getOrDefault(rowKey, Collections.emptyMap())
                .get(columnName);
    }

    // Print the store
    public void printStore() {
        for (String columnFamilyName : keyspace.keySet()) {
            System.out.println("Column Family: " + columnFamilyName);
            keyspace.get(columnFamilyName).forEach((rowKey, columns) -> {
                System.out.println("  Row Key: " + rowKey);
                columns.forEach((columnName, value) -> {
                    System.out.println("    " + columnName + " = " + value);
                });
            });
        }
    }

    public static void main(String[] args) {
        WideColumnStore store = new WideColumnStore();

        store.addColumnFamily("UserActivity");
        store.addRow("UserActivity", "user_1234");
        store.addColumn("UserActivity", "user_1234", "time-uuid1", "page=home, time=5s");
        store.addColumn("UserActivity", "user_1234", "time-uuid2", "page=profile, time=15s");

        store.addRow("UserActivity", "user_5678");
        store.addColumn("UserActivity", "user_5678", "time-uuid1", "page=search, time=10s");
        store.printStore();
    }
}
