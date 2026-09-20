/*
 * =====================================================================
 *  Design File System                            LeetCode 1166 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Map unix-style paths to integer values. createPath(path, value) succeeds only if the
 *   path is well formed ("/a/b", never "/" or ""), does NOT already exist, and its PARENT
 *   path already exists; it returns whether it succeeded. get(path) returns the value at
 *   that path, or -1 if the path does not exist.
 *
 * EXAMPLE
 *   createPath("/a", 1)    -> true     get("/a")   -> 1
 *   createPath("/a/b", 2)  -> true     get("/a/b") -> 2
 *   createPath("/c/d", 3)  -> false    parent "/c" was never created
 *   createPath("/a/b", 4)  -> false    already exists; the stored 2 is untouched
 *   createPath("/", 9)     -> false    the root is not a creatable path
 *
 * DESIGN
 *   Node          a trie node: children (segment name -> Node) plus an int value. The
 *                 sentinel value -1 means "exists only as a parent", which is also what
 *                 get() must return for a missing path, so one sentinel covers both.
 *   root          an unnamed Node standing for "/"; never a result itself.
 *   Splitting     "/a/b".split("/") is ["", "a", "b"] -- index 0 is always the empty
 *                 string before the leading slash, so every walk starts at i = 1.
 *
 * KEY DECISIONS
 *   - One node per path SEGMENT, not one map entry per full path string. A flat
 *     HashMap<String, Integer> would pass these two operations, but the trie is what
 *     makes the parent-exists rule a single lookup and what ls / mkdir -p build on.
 *   - createPath walks only the PARENT segments (i = 1 .. parts.length - 2) and bails the
 *     moment one is missing; the last segment is handled apart, because it is the one
 *     that must NOT already exist.
 *   - A rejected createPath returns before touching any map, so a failed call leaves the
 *     tree unchanged -- no half-created directories.
 *
 * KEY INSIGHT
 *   A path is not a string, it is a sequence of segments, so store it the way a trie
 *   stores a word: one node per segment. "The parent must exist" then becomes "the walk
 *   must not fall off the trie before the last segment", which is the whole problem.
 *   Recognise this shape whenever the keys share long prefixes.
 *
 * COMPLEXITY
 *   For a path of m segments and total length L:
 *   Time  createPath and get are both O(m) hash lookups, O(L) counting the character
 *         work in split and hashing.
 *   Space O(number of distinct segments across all created paths).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Add remove(path): delete the subtree, and decide whether removing a non-empty
 *     directory is allowed.
 *   - Add ls(path) returning children sorted -- swap HashMap for TreeMap and it is free.
 *   - Trie vs HashMap<String, Integer> on the full path: compare the parent check, a
 *     future ls, and a subtree delete.
 *   - Thread safety: which node do you lock when two creates race on the same parent?
 *
 * RUN
 *   main() runs 3 groups of cases (happy path, the two rejection rules, and malformed or
 *   missing paths) and prints actual vs expected.
 */

import java.util.HashMap;
import java.util.Map;

class DesignFileSystem {

    /** One path segment. value == -1 means "exists as a parent, holds nothing". */
    private static class Node {
        Map<String, Node> children = new HashMap<>();
        int value = -1;
    }

    private final Node root;

    public DesignFileSystem() {
        root = new Node();
    }

    public boolean createPath(String path, int value) {
        // "/" and anything without a leading slash are not creatable paths.
        if (path == null || path.equals("/") || !path.startsWith("/")) {
            return false;
        }

        String[] parts = path.split("/");   // "/a/b" -> ["", "a", "b"], so start at 1
        Node curr = root;

        // Walk the PARENT segments only; every one of them must already exist.
        for (int i = 1; i < parts.length - 1; i++) {
            curr = curr.children.get(parts[i]);
            if (curr == null) {
                return false;               // parent path does not exist
            }
        }

        String last = parts[parts.length - 1];
        if (curr.children.containsKey(last)) {
            return false;                   // path already exists; leave its value alone
        }

        Node created = new Node();
        created.value = value;
        curr.children.put(last, created);
        return true;
    }

    public int get(String path) {
        if (path == null || !path.startsWith("/")) {
            return -1;
        }

        String[] parts = path.split("/");
        Node curr = root;
        for (int i = 1; i < parts.length; i++) {
            curr = curr.children.get(parts[i]);
            if (curr == null) {
                return -1;                  // fell off the trie: path does not exist
            }
        }
        return curr.value;                  // for "/" this is the root's -1
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        DesignFileSystem fs = new DesignFileSystem();

        // ---- case 1: typical -- create a path, then a child of it ---------
        print("case 1 create /a", fs.createPath("/a", 1), true);
        print("case 1 get /a", fs.get("/a"), 1);
        print("case 1 create /a/b", fs.createPath("/a/b", 2), true);
        print("case 1 get /a/b", fs.get("/a/b"), 2);

        // ---- case 2: the two rejection rules ------------------------------
        print("case 2 missing parent /c/d", fs.createPath("/c/d", 3), false);
        print("case 2 get /c", fs.get("/c"), -1);          // nothing was half-created
        print("case 2 duplicate /a/b", fs.createPath("/a/b", 4), false);
        print("case 2 /a/b unchanged", fs.get("/a/b"), 2); // the failed create kept 2

        // ---- case 3: edge cases -- malformed and missing paths ------------
        print("case 3 create root", fs.createPath("/", 9), false);
        print("case 3 get root", fs.get("/"), -1);
        print("case 3 no leading slash", fs.createPath("leetcode", 5), false);
        print("case 3 get deeper than tree", fs.get("/a/b/c"), -1);
        print("case 3 create /a/b/c", fs.createPath("/a/b/c", 6), true);
        print("case 3 get /a/b/c", fs.get("/a/b/c"), 6);
    }
}
