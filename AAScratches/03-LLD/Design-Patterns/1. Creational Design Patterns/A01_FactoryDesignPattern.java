/*
 * =====================================================================
 *  Factory (Simple Factory / Factory Method)   Creational | Easy   MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Factory - Creational family. Shown here in its "simple factory" form:
 *   one factory class with a switch over a key that returns a product.
 *
 * INTENT
 *   Move the "which concrete class do I instantiate?" decision out of the
 *   client and into one place. The client asks for a Shape by name and gets
 *   back something that implements Shape; it never writes `new Circle()`.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when the concrete type depends on a runtime value (config, request
 *     payload, DB column) and new types get added over time.
 *   Use when construction is more than a constructor call (pooling, caching,
 *     validation) and you do not want that logic duplicated at call sites.
 *   Do NOT use when there is exactly one implementation - a constructor is
 *     clearer than a factory that can only ever return one thing.
 *   Do NOT use it as a dumping ground; a factory with 30 branches is a sign
 *     the key should map to a registry (see follow-ups).
 *
 * ROLES IN THIS CODE
 *   Shape                      Product - the interface the client depends on
 *   Circle / Square / Triangle ConcreteProduct - the classes the client
 *                              must NOT name
 *   ShapeFactory               Factory (Creator) - owns the new() calls
 *   main()                     Client - holds only Shape references
 *
 * KEY INSIGHT
 *   The win is not "fewer new keywords", it is the direction of the
 *   dependency: the client compiles against Shape only, so adding a
 *   Rectangle touches the factory and nothing else. If your "factory" hands
 *   back a concrete type, you have gained nothing - the return type must be
 *   the abstraction.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Simple Factory vs Factory Method vs Abstract Factory? Simple Factory
 *     is one class with a switch; Factory Method puts the choice in a
 *     subclass override; Abstract Factory returns a matched family (see
 *     C01_AbstractFactoryDatabase).
 *   - Unknown key: return null, throw, or Optional? Returning null (as here)
 *     pushes an NPE onto the caller; throwing IllegalArgumentException fails
 *     at the real fault line.
 *   - How do you kill the if/else chain? Register suppliers in a
 *     Map<String, Supplier<Shape>>, or discover them with ServiceLoader.
 *   - Where does Spring do this? BeanFactory / FactoryBean are this pattern.
 *
 * RUN
 *   main() runs 4 cases (three known keys, one unknown key) and prints
 *   actual vs expected, then draws each shape through the Shape interface.
 */

interface Shape {
    void draw();
}

class Circle implements Shape {

    @Override
    public void draw() {
        System.out.println("drawing circle");
    }
}

class Triangle implements Shape {

    @Override
    public void draw() {
        System.out.println("drawing triangle");
    }
}

class Square implements Shape {

    @Override
    public void draw() {
        System.out.println("drawing square");
    }
}

/**
 * The Creator. Every `new` for a Shape lives here and nowhere else, so the
 * client never mentions Circle, Square or Triangle by name.
 */
class ShapeFactory {

    /**
     * @param shape the product key, e.g. "Circle"
     * @return a Shape, or null when the key is unknown (see INTERVIEW
     * FOLLOW-UPS - throwing is usually the better contract)
     */
    public Shape getShape(String shape) {
        if ("Circle".equals(shape)) {
            return new Circle();
        } else if ("Square".equals(shape)) {
            return new Square();
        } else if ("Triangle".equals(shape)) {
            return new Triangle();
        }
        return null;
    }
}

class FactoryDesignPattern {

    public static void main(String[] args) {
        ShapeFactory shapeFactory = new ShapeFactory();

        // Case 1-3: typical. The declared type is Shape, never the concrete class.
        Shape circle = shapeFactory.getShape("Circle");
        Shape square = shapeFactory.getShape("Square");
        Shape triangle = shapeFactory.getShape("Triangle");

        print("case 1 key=Circle  ", typeOf(circle), "Circle");
        print("case 2 key=Square  ", typeOf(square), "Square");
        print("case 3 key=Triangle", typeOf(triangle), "Triangle");

        // Case 4: edge. Unknown key - this factory returns null, so the caller
        // gets an NPE at the *next* line instead of an error here.
        print("case 4 key=Hexagon ", typeOf(shapeFactory.getShape("Hexagon")), "null");

        // The payoff: identical client code for every concrete product.
        for (Shape shape : new Shape[]{circle, square, triangle}) {
            shape.draw();
        }
    }

    /** Names the runtime class without the client ever importing it. */
    private static String typeOf(Shape shape) {
        return shape == null ? "null" : shape.getClass().getSimpleName();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
