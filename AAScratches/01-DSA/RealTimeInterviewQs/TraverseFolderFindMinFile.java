
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class TraverseFolderFindMinFile {

    static List<File> solve(File root, long maxSize) {
        List<File> result = new ArrayList<>();
        traverse(root, maxSize, result);
        return result;
    }

    static void traverse(File file, long maxSize, List<File> result) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isFile()) {
            if (file.length() < maxSize) {
                result.add(file);
            }
            return;
        }

        File[] files = file.listFiles();

        if (files == null) {
            return;
        }

        for (File child : files) {
            traverse(child, maxSize, result);
        }
    }

    public static void main(String[] args) {
        File root = new File("/tmp");
        long maxSize = 1024; // 1 KB

        List<File> result = solve(root, maxSize);

        for (File file : result) {
            System.out.println(
                file.getAbsolutePath()
                + " : " + file.length() + " bytes"
            );
        }
    }
}
