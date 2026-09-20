/*
 * =====================================================================
 *  Flyweight Pattern - forest of trees                    Structural | Medium
 * =====================================================================
 *
 * PATTERN
 *   Flyweight (Structural, object pattern). Also called "interning" or
 *   "object caching" when people meet it outside the GoF book.
 *
 * INTENT
 *   Support very large numbers of fine-grained objects cheaply by sharing the
 *   part of their state that is identical, instead of copying it per object.
 *
 * WHEN TO USE, WHEN NOT
 *   USE when you create millions of near-identical objects, most of their
 *     fields repeat across instances, and the rest can be passed in per call.
 *   USE when the shared part is immutable (glyphs, tile sprites, tree species,
 *     currency codes, Integer.valueOf's cache, String literal pool).
 *   NOT when the shared part is mutable - one writer corrupts every holder.
 *   NOT when object count is small; the factory and the lookup cost more than
 *     the memory you save, and the indirection hides the real design.
 *
 * ROLES IN THIS CODE
 *   TreeType        Flyweight. Holds only INTRINSIC state (name, color,
 *                   texture) - identical for every Oak, so stored once.
 *   TreeFactory     FlyweightFactory. Interns TreeType by a composite key so
 *                   equal descriptions return the same instance.
 *   Tree            Context. Holds the EXTRINSIC state (x, y) that differs per
 *                   tree, plus a reference to its shared TreeType.
 *   FlyweightExample Client. Plants trees and never constructs a TreeType
 *                   directly - it always goes through the factory.
 *
 * KEY INSIGHT
 *   Split the object's state in two: what repeats (intrinsic -> share it) and
 *   what varies (extrinsic -> pass it in). The factory is what enforces the
 *   sharing; without it every caller would just call new again. 1,000,000
 *   trees then cost 1,000,000 small Context objects plus 2 TreeType objects.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Flyweight vs Singleton: Singleton is one instance total; Flyweight is
 *     one instance per distinct intrinsic value (a small pool, not one).
 *   - Why must the flyweight be immutable, and what breaks if it is not?
 *   - Where does the JDK do this? Integer.valueOf (-128..127), String pool,
 *     Boolean.valueOf, Character cache.
 *   - How do you make the factory thread-safe without a global lock?
 *
 * RUN
 *   main() runs 3 cases: a typical plant-and-draw, an edge case where the same
 *   species is planted again, and a tricky identity check. Each prints
 *   actual vs expected.
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
