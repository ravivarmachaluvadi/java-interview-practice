/*
 * =====================================================================
 *  Design In-Memory File System                       LeetCode 588 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Build a file system in memory with four shell-like operations. Paths are absolute,
 *   use "/" as the separator, and never have a trailing slash; "/" itself is the root.
 *     ls(path)                        if path is a directory, the sorted names directly
 *                                     inside it; if path is a file, a list with just that
 *                                     file's name.
 *     mkdir(path)                     create the directory and every missing directory
 *                                     along the way (like mkdir -p).
 *     addContentToFile(path, content) append to the file, creating it (and any missing
 *                                     parent directory) if it does not exist yet.
 *     readContentFromFile(path)       the file's full accumulated content.
 *
 * EXAMPLE
 *   mkdir("/a/b/c"); addContentToFile("/a/b/c/d.txt", "hello")
 *   ls("/")          -> [a]
 *   ls("/a/b/c")     -> [d.txt]
 *   ls("/a/b/c/d.txt") -> [d.txt]          a file path lists only itself
 *   addContentToFile("/a/b/c/d.txt", " world"); readContentFromFile(...) -> "hello world"
 *   ls("/") on a brand-new file system -> []   (the empty-root edge case)
 *
 * DESIGN  (trie of path segments)
 *   Dir   one node of the tree. Two maps, which is what keeps every case simple:
 *           dirs  : child directory name -> Dir
 *           files : file name            -> its content so far
 *         Keeping files separate from dirs means "is this path a file or a directory?"
 *         is two O(1) lookups, never a type check on a shared node.
 *   DesignInMemoryFileSystem holds only the root Dir. Every operation is the same walk
 *         down the segment list; the operations differ only in what they do at the end
 *         and whether the walk is allowed to create missing nodes.
 *
 * KEY DECISIONS
 *   1. Split once into segments and drop empty ones, so "/" and "/a/b" go through the
 *      same code. path.split("/") on "/a/b" yields ["", "a", "b"] -- the leading empty
 *      string is the classic off-by-one in this problem, so it is filtered out here.
 *   2. Two walk helpers instead of one flag: walkCreating for mkdir and writes,
 *      walkExisting (returns null on a missing segment) for ls and reads. Reads then
 *      degrade to [] or "" instead of throwing NullPointerException.
 *   3. ls sorts on demand. Sorting on write would cost more overall, because a directory
 *      is usually written many times and listed rarely.
 *   4. addContentToFile appends with getOrDefault(name, "") + content, so "create" and
 *      "append" are one line rather than two branches.
 *
 * COMPLEXITY
 *   Let L be the number of path segments and K the number of entries in a directory.
 *   Time  ls O(L + K log K) for the sort; mkdir, addContentToFile, readContentFromFile O(L)
 *         plus the cost of copying the content on append.
 *   Space O(N) for N directories and files, plus the stored content of every file.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Add rm / rmdir: delete the entry from the parent's map, and decide recursive or not.
 *   - Make appends cheap: store List<String> or StringBuilder per file instead of String,
 *     so appending is O(added) rather than O(total).
 *   - Support relative paths and "." / "..": resolve against a current-directory pointer.
 *   - Make it concurrent: lock per directory node, or use ConcurrentHashMap children.
 *   - Persist it: what changes if the tree must survive a restart? (a write-ahead log)
 *
 * RUN
 *   main() runs 3 cases (the LeetCode walkthrough, empty root and a file at root,
 *   auto-created parents plus missing paths) and prints actual vs expected per line.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** One node of the path trie: child directories by name, and files by name. */
class Dir {
    Map<String, Dir> dirs = new HashMap<>();
    Map<String, String> files = new HashMap<>();
}

class DesignInMemoryFileSystem {

    private final Dir root = new Dir();

    /** Directory listing, or the single file name when path points at a file. */
    public List<String> ls(String path) {
        String[] parts = segments(path);
        Dir cur = walkExisting(parts, parts.length - 1); // stop at the parent of the last segment
        if (cur == null) {
            return new ArrayList<>(); // some directory on the way does not exist
        }

        if (parts.length > 0) {
            String last = parts[parts.length - 1];
            if (cur.files.containsKey(last)) {
                return new ArrayList<>(Collections.singletonList(last)); // a file lists only itself
            }
            cur = cur.dirs.get(last);
            if (cur == null) {
                return new ArrayList<>(); // neither a file nor a directory
            }
        }

        List<String> result = new ArrayList<>(cur.dirs.keySet());
        result.addAll(cur.files.keySet());
        Collections.sort(result); // directories and files are listed together, sorted by name
        return result;
    }

    /** Create the directory and every missing directory above it (mkdir -p). */
    public void mkdir(String path) {
        String[] parts = segments(path);
        walkCreating(parts, parts.length);
    }

    /** Append to the file, creating the file and any missing parent directory. */
    public void addContentToFile(String filePath, String content) {
        String[] parts = segments(filePath);
        if (parts.length == 0) {
            return; // "/" is the root directory, not a file
        }
        Dir parent = walkCreating(parts, parts.length - 1);
        String name = parts[parts.length - 1];
        // getOrDefault turns "create" and "append" into the same single statement.
        parent.files.put(name, parent.files.getOrDefault(name, "") + content);
    }

    /** Full content of the file, or "" if no such file exists. */
    public String readContentFromFile(String filePath) {
        String[] parts = segments(filePath);
        if (parts.length == 0) {
            return "";
        }
        Dir parent = walkExisting(parts, parts.length - 1);
        if (parent == null) {
            return "";
        }
        return parent.files.getOrDefault(parts[parts.length - 1], "");
    }

    // ------------------------------------------------------------------
    // Path helpers
    // ------------------------------------------------------------------

    /**
     * "/" -> [], "/a/b/c" -> [a, b, c].
     * split("/") leaves a leading empty string, which is the usual off-by-one here,
     * so empty segments are dropped rather than skipped with an index trick.
     */
    private static String[] segments(String path) {
        List<String> parts = new ArrayList<>();
        for (String part : path.split("/")) {
            if (!part.isEmpty()) {
                parts.add(part);
            }
        }
        return parts.toArray(new String[0]);
    }

    /** Walk the first {@code depth} segments, creating any directory that is missing. */
    private Dir walkCreating(String[] parts, int depth) {
        Dir cur = root;
        for (int i = 0; i < depth; i++) {
            cur = cur.dirs.computeIfAbsent(parts[i], name -> new Dir());
        }
        return cur;
    }

    /** Walk the first {@code depth} segments, returning null if any is missing. */
    private Dir walkExisting(String[] parts, int depth) {
        Dir cur = root;
        for (int i = 0; i < depth; i++) {
            cur = cur.dirs.get(parts[i]);
            if (cur == null) {
                return null;
            }
        }
        return cur;
    }

    // ------------------------------------------------------------------
    // Demo
    // ------------------------------------------------------------------

    private static void check(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println("  " + label + " -> actual " + actual + "   expected " + expected
                + (ok ? "   OK" : "   FAIL"));
    }

    public static void main(String[] args) {
        System.out.println("case 1: the LeetCode walkthrough");
        DesignInMemoryFileSystem fs = new DesignInMemoryFileSystem();
        fs.mkdir("/a/b/c");
        check("ls(\"/\")", fs.ls("/"), Arrays.asList("a"));
        check("ls(\"/a/b/c\") empty dir", fs.ls("/a/b/c"), Collections.emptyList());
        fs.addContentToFile("/a/b/c/d.txt", "hello");
        check("ls(\"/a/b/c\")", fs.ls("/a/b/c"), Arrays.asList("d.txt"));
        check("ls file path", fs.ls("/a/b/c/d.txt"), Arrays.asList("d.txt"));
        check("read d.txt", fs.readContentFromFile("/a/b/c/d.txt"), "hello");
        fs.addContentToFile("/a/b/c/d.txt", " world"); // append, do not overwrite
        check("read after append", fs.readContentFromFile("/a/b/c/d.txt"), "hello world");

        System.out.println("case 2: empty root, a file directly at root, mkdir(\"/\")");
        DesignInMemoryFileSystem fs2 = new DesignInMemoryFileSystem();
        check("ls(\"/\") on new fs", fs2.ls("/"), Collections.emptyList());
        fs2.mkdir("/"); // no segments to create, must be a harmless no-op
        check("ls(\"/\") after mkdir(/)", fs2.ls("/"), Collections.emptyList());
        fs2.addContentToFile("/top.txt", "T");
        fs2.mkdir("/zdir");
        check("ls(\"/\") dirs and files", fs2.ls("/"), Arrays.asList("top.txt", "zdir"));
        check("ls(\"/top.txt\")", fs2.ls("/top.txt"), Arrays.asList("top.txt"));
        check("read /top.txt", fs2.readContentFromFile("/top.txt"), "T");

        System.out.println("case 3: parents created on write, and missing paths");
        DesignInMemoryFileSystem fs3 = new DesignInMemoryFileSystem();
        fs3.addContentToFile("/x/y/z.txt", "deep"); // /x and /x/y never had mkdir called
        check("ls(\"/\")", fs3.ls("/"), Arrays.asList("x"));
        check("ls(\"/x/y\")", fs3.ls("/x/y"), Arrays.asList("z.txt"));
        check("read z.txt", fs3.readContentFromFile("/x/y/z.txt"), "deep");
        fs3.mkdir("/x/y/sub");
        fs3.addContentToFile("/x/y/a.txt", "A");
        check("ls(\"/x/y\") sorted", fs3.ls("/x/y"), Arrays.asList("a.txt", "sub", "z.txt"));
        check("ls missing dir", fs3.ls("/nope/nope"), Collections.emptyList());
        check("read missing file", fs3.readContentFromFile("/x/y/missing.txt"), "");
    }
}
