import java.util.*;

// https://leetcode.com/problems/design-file-system/description/
// 1166. Design File System
class DesignFileSystem {
    private final Node root;

    private static class Node {
        Map<String, Node> children = new HashMap<>();
        int value = -1;
    }

    public DesignFileSystem() {
        root = new Node();
    }

    public boolean createPath(String path, int value) {
        if (path == null || path.equals("/") || !path.startsWith("/"))
            return false;  // invalid path

        String[] parts = path.split("/");
        // parts[0] will be empty because path starts with "/"
        Node curr = root;
        // traverse through parent segments (all but last)
        for (int i = 1; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!curr.children.containsKey(part))
                return false; // parent path doesn’t exist

            curr = curr.children.get(part);
        }
        String last = parts[parts.length - 1];
        if (curr.children.containsKey(last))
            return false; // path already exists

        Node newNode = new Node();
        newNode.value = value;
        curr.children.put(last, newNode);
        return true;
    }

    public int get(String path) {
        if (path == null || !path.startsWith("/"))
            return -1;

        String[] parts = path.split("/");
        Node curr = root;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!curr.children.containsKey(part))
                return -1; // path doesn’t exist

            curr = curr.children.get(part);
        }
        return curr.value;
    }

    // Example main to test
    public static void main(String[] args) {
        // String[] split = "/a/b".split("/"); // [ "", "a", "b" ]
        DesignFileSystem fs = new DesignFileSystem();
        System.out.println(fs.createPath("/a", 1));       // true
        System.out.println(fs.get("/a"));                // 1
        System.out.println(fs.createPath("/a/b", 2));    // true
        System.out.println(fs.get("/a/b"));              // 2
        System.out.println(fs.createPath("/c/d", 3));    // false — parent "/c" doesn’t exist
        System.out.println(fs.get("/c"));                // -1
        System.out.println(fs.createPath("/a/b", 4));    // false — "/a/b" already exists
    }
}
