/**
 * Problem:
 * Demonstrates the execution order of static and instance initialization blocks
 * when a subclass is instantiated. The program prints "ACBD" by loading classes
 * A and B, executing their static blocks in inheritance order, then creating an
 * object of B which triggers its instance block followed by A's instance block.
 *
 * Approach:
 * 1. Instantiate B (via new B()) in main(String[] args).
 * 2. Class loader loads B; static blocks run: A's static ("A") then B's static ("C").
 * 3. Object creation runs instance blocks: B's instance ("D") then A's instance ("B").
 *
 * Time Complexity:
 * O(1) – fixed number of print statements.
 *
 * Space Complexity:
 * O(1) – only a few class and object references are created.
 */
class Tricky1 {

    public static void main(String[] args) {
        B b = new B(); // A C B D
    }

    public static void main(char[] args) {
    }
}

class A {
    static {
        System.out.print("A");
    }

    {
        System.out.print("B");
    }
}


class B extends A {
    static {
        System.out.print("C");
    }

    {
        System.out.print("D");
    }
}
