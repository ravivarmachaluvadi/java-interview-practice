/**
 * Implements an in‑memory file system that supports creating directories,
 * adding and reading files, and listing directory contents.
 *
 * The API mirrors typical shell commands:
 * - {@code ls(path)} returns a sorted list of names in the given directory
 *   or the single file name if the path points to a file.
 * - {@code mkdir(path)} creates all intermediate directories along the path.
 * - {@code addContentToFile(filePath, content)} appends text to an existing
 *   file or creates it if absent.
 * - {@code readContentFromFile(filePath)} returns the full contents of a file.
 *
 * Internally each directory is represented by a {@link Dir} object that holds
 * maps for subdirectories and files. Traversal follows the path components,
 * creating nodes as needed for {@code mkdir} and file creation.
 *
 * Time Complexity:
 * - ls, mkdir, addContentToFile, readContentFromFile: O(L + K log K)
 *   where L is the number of path segments and K is the number of entries
 *   in a directory (for sorting during ls).
 *
 * Space Complexity:
 * - O(N) total, where N is the number of distinct directories and files
 *   stored in memory. Each file's content contributes to this space.
 */
import java.util.*;

class Dir {
    HashMap<String, Dir> dirs = new HashMap<>();
    HashMap<String, String> files = new HashMap<>();
}

class DesignInMemoryFileSystem {
    Dir root;

    public DesignInMemoryFileSystem() {
        root = new Dir();
    }

    // List directory contents or single file name
    public List<String> ls(String path) {
        Dir t = root;
        List<String> result = new ArrayList<>();

        if (!path.equals("/")) {
            String[] parts = path.split("/");
            for (int i = 1; i < parts.length - 1; i++) {
                t = t.dirs.get(parts[i]);
            }

            // If the last part is a file
            // If path is a file path, returns a list that only contains this file's name.
            if (t.files.containsKey(parts[parts.length - 1])) {
                result.add(parts[parts.length - 1]);
                return result;
            } else {
                t = t.dirs.get(parts[parts.length - 1]);
            }
        }

        // If path is a directory path, returns the list of file and
        // directory names in this directory.
        // Add all directory and file names
        result.addAll(t.dirs.keySet());
        result.addAll(t.files.keySet());
        Collections.sort(result);
        return result;
    }

    // Create directories as needed
    public void mkdir(String path) {
        Dir t = root;
        String[] parts = path.split("/");
        for (int i = 1; i < parts.length; i++) {
            t.dirs.putIfAbsent(parts[i], new Dir());
            t = t.dirs.get(parts[i]);
        }
    }

    // Add content to a file (create file if not exists)
    public void addContentToFile(String filePath, String content) {
        Dir t = root;
        String[] parts = filePath.split("/");
        for (int i = 1; i < parts.length - 1; i++) {
            t = t.dirs.get(parts[i]);
        }
        t.files.put(parts[parts.length - 1], t.files.getOrDefault(parts[parts.length - 1], "") + content);
    }

    // Read file content
    public String readContentFromFile(String filePath) {
        Dir t = root;
        String[] parts = filePath.split("/");
        for (int i = 1; i < parts.length - 1; i++) {
            t = t.dirs.get(parts[i]);
        }
        return t.files.get(parts[parts.length - 1]);
    }

    // ---------- Example Demo ----------
    public static void main(String[] args) {
        DesignInMemoryFileSystem fs = new DesignInMemoryFileSystem();

        System.out.println("Create directories: /a/b/c");
        fs.mkdir("/a/b/c");

        System.out.println("Add file: /a/b/c/d.txt with content 'hello'");
        fs.addContentToFile("/a/b/c/d.txt", "hello");

        System.out.println("List / -> " + fs.ls("/"));
        System.out.println("List /a/b/c -> " + fs.ls("/a/b/c"));
        System.out.println("Read file /a/b/c/d.txt -> " + fs.readContentFromFile("/a/b/c/d.txt"));

        System.out.println("Append content ' world' to /a/b/c/d.txt");
        fs.addContentToFile("/a/b/c/d.txt", " world");

        System.out.println("Read file /a/b/c/d.txt -> " + fs.readContentFromFile("/a/b/c/d.txt"));
    }
}
