/*
 * =====================================================================
 *  Wide-Column Store (Cassandra / HBase data model)   HLD building block  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Model the storage engine behind Cassandra, HBase and DynamoDB in three nested maps,
 *   so the row-key / column-family / column vocabulary stops being hand-waving. A write
 *   is an upsert of one cell; a read is either one cell or a SORTED RANGE of cells inside
 *   a single row. There is no join, no secondary lookup, no cross-row scan.
 *
 * SHAPE
 *   keyspace -> column family -> row key -> column name -> value
 *   ("UserActivity" -> "user_1234" -> "2024-03-01T09:00" -> "page=home")
 *   Row key   = partition key: decides WHICH node stores the data. Hashed, so order
 *               across rows is meaningless.
 *   Column    = clustering key: decides the ORDER INSIDE that row. Sorted, so a range
 *               slice is a contiguous read off one disk region on one node.
 *
 * DESIGN (classes and why)
 *   WideColumnStore - the keyspace. One HashMap of column families.
 *   Rows inside a column family: LinkedHashMap. A real store spreads rows by token hash,
 *     so any cross-row order is fiction; insertion order is kept only so this demo prints
 *     the same thing every run.
 *   Columns inside a row: TreeMap. This is the load-bearing choice - sorted columns are
 *     what makes sliceColumns() a range scan instead of a full row read.
 *   sliceColumns(cf, row, from, to) - the query this model exists to serve, backed by
 *     TreeMap.subMap.
 *
 * KEY DECISIONS
 *   Fixed: columns lived in a HashMap, so a row's cells came back in arbitrary order and
 *     no range query was possible. Clustering columns must be sorted -> TreeMap.
 *   Fixed: addRow/addColumn silently did nothing when the row or column family was
 *     missing, so writes vanished. Writes now upsert the row (real wide-column stores
 *     have no "create row" step), and an unknown column family throws, like an unknown
 *     table would.
 *   Rows are wide and sparse: every row may hold a different set of columns, and an
 *     absent column costs nothing. That is the difference from a relational table, where
 *     every row pays for every declared column.
 *
 * COMPLEXITY
 *   Time  get/put O(log c) for c columns in the row (TreeMap); slice O(log c + k) for
 *         k cells returned - the reason time-series reads are cheap here.
 *   Space O(total cells). Nothing is stored for a column a row does not have.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can you not query by a non-key column? No global index; you would scan every
 *     partition. The fix is a second table written at the same time (query-first design).
 *   - What is a hot partition, and why does an unbounded row (all events for one user,
 *     forever) eventually kill a node? Bucket the partition key by day/month.
 *   - Cassandra vs HBase: Dynamo-style peer ring with tunable quorum, vs HDFS-backed
 *     region servers with a single writer per region (strong consistency).
 *   - Where does the LSM tree fit - memtable, SSTable flush, compaction, tombstones?
 *
 * RUN
 *   main() runs 4 cases (point read, sorted range slice, sparse/missing cells, unknown
 *   column family) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class WideColumnStore {

    /** columnFamily -> rowKey -> (columnName -> value). Columns are kept sorted. */
    private final Map<String, Map<String, TreeMap<String, String>>> keyspace = new HashMap<>();

    /** Declares a column family. Schema, not data: it must exist before any write. */
    public void addColumnFamily(String columnFamilyName) {
        keyspace.putIfAbsent(columnFamilyName, new LinkedHashMap<>());
    }

    /** Optional: creates an empty row. Writes create the row anyway, so this is a hint. */
    public void addRow(String columnFamilyName, String rowKey) {
        columnFamily(columnFamilyName).computeIfAbsent(rowKey, k -> new TreeMap<>());
    }

    /** Upsert of one cell. Creates the row on demand; overwrites an existing value. */
    public void addColumn(String columnFamilyName, String rowKey, String columnName, String value) {
        columnFamily(columnFamilyName)
                .computeIfAbsent(rowKey, k -> new TreeMap<>())
                .put(columnName, value);
    }

    /** Point read. Returns null for a row or column that was never written (sparse row). */
    public String getColumn(String columnFamilyName, String rowKey, String columnName) {
        TreeMap<String, String> row = columnFamily(columnFamilyName).get(rowKey);
        return row == null ? null : row.get(columnName);
    }

    /**
     * Range slice inside one row: every cell with from <= columnName <= to, in order.
     * This is the query the whole model is built around - one node, one contiguous read.
     */
    public List<String> sliceColumns(String columnFamilyName, String rowKey,
                                     String from, String to) {
        TreeMap<String, String> row = columnFamily(columnFamilyName).get(rowKey);
        if (row == null) return Collections.emptyList();

        List<String> slice = new ArrayList<>();
        // subMap is the TreeMap view that makes this O(log c + k) instead of a row scan.
        row.subMap(from, true, to, true)
                .forEach((column, value) -> slice.add(column + "=" + value));
        return slice;
    }

    /** All column names of a row, in clustering order. Empty list if the row is absent. */
    public List<String> columnNames(String columnFamilyName, String rowKey) {
        TreeMap<String, String> row = columnFamily(columnFamilyName).get(rowKey);
        return row == null ? Collections.emptyList() : new ArrayList<>(row.keySet());
    }

    public void printStore() {
        keyspace.forEach((columnFamilyName, rows) -> {
            System.out.println("Column Family: " + columnFamilyName);
            rows.forEach((rowKey, columns) -> {
                System.out.println("  Row Key: " + rowKey);
                columns.forEach((columnName, value) ->
                        System.out.println("    " + columnName + " = " + value));
            });
        });
    }

    /** An unknown column family is a schema error, not a silently dropped write. */
    private Map<String, TreeMap<String, String>> columnFamily(String columnFamilyName) {
        Map<String, TreeMap<String, String>> cf = keyspace.get(columnFamilyName);
        if (cf == null) {
            throw new IllegalArgumentException("No such column family: " + columnFamilyName);
        }
        return cf;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        WideColumnStore store = new WideColumnStore();
        store.addColumnFamily("UserActivity");

        // Columns are written deliberately out of order to prove the store sorts them.
        store.addColumn("UserActivity", "user_1234", "2024-03-01T11:00", "page=cart");
        store.addColumn("UserActivity", "user_1234", "2024-03-01T09:00", "page=home");
        store.addColumn("UserActivity", "user_1234", "2024-03-01T12:00", "page=checkout");
        store.addColumn("UserActivity", "user_1234", "2024-03-01T10:00", "page=profile");

        // A second row with a completely different column set: rows are sparse and wide.
        store.addColumn("UserActivity", "user_5678", "2024-03-01T10:30", "page=search");
        store.addColumn("UserActivity", "user_5678", "device", "ios");

        store.printStore();

        // Case 1 (typical): point read of one cell, and an overwrite of the same cell.
        print("case 1 point read", store.getColumn("UserActivity", "user_1234", "2024-03-01T09:00"),
                "page=home");
        store.addColumn("UserActivity", "user_1234", "2024-03-01T09:00", "page=home&ref=ad");
        print("case 1 after upsert",
                store.getColumn("UserActivity", "user_1234", "2024-03-01T09:00"),
                "page=home&ref=ad");

        // Case 2 (the point of the model): a sorted range slice inside one row.
        print("case 2 clustering order", store.columnNames("UserActivity", "user_1234"),
                "[2024-03-01T09:00, 2024-03-01T10:00, 2024-03-01T11:00, 2024-03-01T12:00]");
        print("case 2 slice 10:00-11:00",
                store.sliceColumns("UserActivity", "user_1234",
                        "2024-03-01T10:00", "2024-03-01T11:00"),
                "[2024-03-01T10:00=page=profile, 2024-03-01T11:00=page=cart]");

        // Case 3 (edge): sparse row and missing row both read as absent, never as an error.
        print("case 3 column not in row",
                store.getColumn("UserActivity", "user_5678", "2024-03-01T09:00"),
                "null");
        print("case 3 row never written", store.getColumn("UserActivity", "user_9999", "device"),
                "null");
        print("case 3 slice of missing row",
                store.sliceColumns("UserActivity", "user_9999", "a", "z"), "[]");
        store.addRow("UserActivity", "user_0001");  // row with no cells yet
        print("case 3 empty row", store.columnNames("UserActivity", "user_0001"), "[]");

        // Case 4 (edge): writing to an undeclared column family must fail loudly.
        String result;
        try {
            store.addColumn("Orders", "user_1234", "2024-03-01T09:00", "total=99");
            result = "write accepted";
        } catch (IllegalArgumentException e) {
            result = "IllegalArgumentException";
        }
        print("case 4 unknown column family", result, "IllegalArgumentException");
    }
}
