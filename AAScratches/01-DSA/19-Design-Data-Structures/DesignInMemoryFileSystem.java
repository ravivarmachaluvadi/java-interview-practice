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
