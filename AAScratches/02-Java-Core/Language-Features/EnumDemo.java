/**
 * Demonstrates how to use Java enums with per-constant method implementations.
 *
 * The program iterates over all enum constants of {@code MyEnum} and invokes
 * each constant's overridden {@code m1()} method, printing a message that
 * identifies the constant.
 *
 * Approach:
 * 1. Define an abstract enum {@code MyEnum} with a concrete field and an abstract method {@code m1()}.
 * 2. Provide per-constant class bodies to override {@code m1()} for each enum value.
 * 3. In {@code main}, loop through {@code MyEnum.values()} and call {@code m1()} on each instance.
 *
 * Time Complexity: O(n) where n is the number of enum constants (here 2).
 * Space Complexity: O(1), aside from the fixed-size enum array created by the JVM.
 */
class EnumDemo {
    public static void main(String[] args) {
        for (MyEnum value : MyEnum.values()) {
            value.m1();
        }
    }
}

enum MyEnum {

    SUNDAY(0) {
        @Override
        void m1() {
            System.out.println("Override by " + MyEnum.SUNDAY + " ");
        }
    },
    MONDAY(1) {
        @Override
        void m1() {
            System.out.println("Override by " + MyEnum.MONDAY);
        }
    };

    final int value;

    abstract void m1();

    MyEnum(int i) {
        this.value = i;
    }
}

