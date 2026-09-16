import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition:
 * <p>
 * The Flyweight Pattern is a structural design pattern that
 * <p>
 * helps reduce memory usage by sharing objects that are similar
 * <p>
 * in state instead of creating new ones each time.
 * <p>
 * It’s used when a large number of objects are created that
 * <p>
 * have common, reusable data (intrinsic state) and unique, variable
 * <p>
 * data (extrinsic state).
 */
// ✅ Step 1: Flyweight Class (Shared data)
class TreeType {
    private final String name;
    private String color;
    private String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    public void draw(int x, int y) {
        System.out.println("Drawing " + name + " tree at (" + x + ", " + y + ")");
    }
}

// ✅ Step 2: Flyweight Factory (Manages shared instances)
class TreeFactory {
    private static final Map<String, TreeType> treeFactory = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + "-" + color + "-" + texture;
        TreeType type = treeFactory.computeIfAbsent(key, k -> new TreeType(name, color, texture));

        return type;
    }
}

// ✅ Step 3: Context Class (Holds extrinsic data)
class Tree {
    private TreeType type;
    int x, y;

    public Tree(TreeType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void plant() {
        type.draw(x, y);
    }
}

//✅ Step 4: Client Code (Uses Flyweight objects)
public class FlyweightExample {
    private List<Tree> list = new ArrayList<>();

    public void plantTree(String name, String color, String texture, int x, int y) {
        TreeType treeType = TreeFactory.getTreeType(name, color, texture);
        list.add(new Tree(treeType, x, y));
    }

    public void draw() {
        for (Tree tree : list) {
            tree.plant();

        }
    }

    public static void main(String[] args) {
        FlyweightExample flyweight = new FlyweightExample();
        flyweight.plantTree("Oak", "Green", "Rough", 10, 20);
        flyweight.plantTree("Pine", "Dark Green", "Smooth", 15, 25);
        // Reuses same Flyweight
        flyweight.plantTree("Oak", "Green", "Rough", 30, 35);
        flyweight.draw();
    }

}
