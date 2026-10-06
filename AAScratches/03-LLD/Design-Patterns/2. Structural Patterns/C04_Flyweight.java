/*
 * =====================================================================
 *  Flyweight - forest of trees in a game map               Structural | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A game map draws a million trees. Each Oak repeats the same name,
 *   colour and texture (the heavy part); only its x, y differ. One full
 *   object per tree runs out of memory.
 *
 * KEY INSIGHT
 *   Split the state. What repeats (intrinsic: species, colour, texture) is
 *   stored once in a shared, immutable TreeType; what varies (extrinsic:
 *   x, y) stays in a tiny Tree or is passed in per call. The factory
 *   enforces the sharing: a million trees cost a million small Trees plus
 *   one TreeType per species.
 *
 * ROLES IN THIS CODE
 *   TreeType          Flyweight - intrinsic state only, immutable
 *   TreeFactory       FlyweightFactory - one instance per distinct key
 *   Tree              Context - x, y plus a pointer to its TreeType
 *   FlyweightExample  Client - never calls new TreeType
 *
 * INTERVIEW FOLLOW-UPS
 *   - Flyweight vs Singleton: one instance per distinct value, not one total.
 *   - Why immutable? One writer would change every tree sharing it.
 *   - Seen in: Integer.valueOf cache (-128..127), the String pool, text
 *     editor glyphs, map markers in delivery apps.
 *
 * RUN
 *   3 cases: plant and draw, re-plant the same species 100 times, identity
 *   checks on the factory.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Flyweight: intrinsic (shared, immutable) state only. Never stores x/y. */
class TreeType {
    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    /** x and y arrive as arguments - they are extrinsic, so not fields here. */
    public void draw(int x, int y) {
        System.out.println("  drawing " + name + " (" + color + ", " + texture
                + ") at (" + x + ", " + y + ")");
    }

    @Override
    public String toString() {
        return name + "/" + color + "/" + texture;
    }
}

/** FlyweightFactory: the only place a TreeType is ever constructed. */
class TreeFactory {
    private static final Map<String, TreeType> CACHE = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        // The key must cover every intrinsic field, otherwise two different
        // species could collide onto one shared instance.
        String key = name + "-" + color + "-" + texture;
        return CACHE.computeIfAbsent(key, k -> new TreeType(name, color, texture));
    }

    /** How many distinct flyweights exist - the number the pattern minimises. */
    public static int distinctTypes() {
        return CACHE.size();
    }

    public static void clear() {
        CACHE.clear();
    }
}

/** Context: one per tree. Cheap, because it only holds x, y and a pointer. */
class Tree {
    private final TreeType type;
    private final int x;
    private final int y;

    public Tree(TreeType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    /** Extrinsic state is handed to the flyweight at call time. */
    public void plant() {
        type.draw(x, y);
    }

    public TreeType getType() {
        return type;
    }
}

/** Client. */
class FlyweightExample {
    private final List<Tree> forest = new ArrayList<>();

    public void plantTree(String name, String color, String texture, int x, int y) {
        TreeType treeType = TreeFactory.getTreeType(name, color, texture);
        forest.add(new Tree(treeType, x, y));
    }

    public void draw() {
        for (Tree tree : forest) {
            tree.plant();
        }
    }

    public int treeCount() {
        return forest.size();
    }

    public List<Tree> trees() {
        return forest;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // ---- case 1: typical - 3 trees, only 2 distinct species -------------
        TreeFactory.clear();
        FlyweightExample forest = new FlyweightExample();
        forest.plantTree("Oak", "Green", "Rough", 10, 20);
        forest.plantTree("Pine", "DarkGreen", "Smooth", 15, 25);
        forest.plantTree("Oak", "Green", "Rough", 30, 35); // reuses the Oak flyweight
        forest.draw();
        print("case 1 trees planted", forest.treeCount(), 3);
        print("case 1 distinct flyweights", TreeFactory.distinctTypes(), 2);

        // ---- case 2: edge - planting the same species 100 more times ---------
        for (int i = 0; i < 100; i++) {
            forest.plantTree("Oak", "Green", "Rough", i, i);
        }
        print("case 2 trees planted", forest.treeCount(), 103);
        print("case 2 distinct flyweights", TreeFactory.distinctTypes(), 2);

        // ---- case 3: tricky - identity, not equality, is what is shared ------
        TreeType a = TreeFactory.getTreeType("Oak", "Green", "Rough");
        TreeType b = TreeFactory.getTreeType("Oak", "Green", "Rough");
        TreeType c = TreeFactory.getTreeType("Oak", "Brown", "Rough"); // color differs
        print("case 3 same description -> same object", a == b, true);
        print("case 3 different color -> new object", a == c, false);
        print("case 3 distinct flyweights now", TreeFactory.distinctTypes(), 3);
        print("case 3 first tree shares the Oak flyweight",
                forest.trees().get(0).getType() == a, true);
    }
}
