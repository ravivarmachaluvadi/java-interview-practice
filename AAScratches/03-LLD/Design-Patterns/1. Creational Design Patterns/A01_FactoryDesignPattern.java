/**
 *
 * It provides a way to create objects without exposing the
 * <p>
 * creation logic to the client and instead uses a common
 * <p>
 * interface to refer to the newly created object.
 * <p>
 * ✅ Advantages
 * <p>
 * Loose coupling between client and implementation.
 * <p>
 * Centralized object creation logic.
 * <p>
 * Makes adding new product types easier.
 * <p>
 * ⚠️ Disadvantages
 * <p>
 * Can increase code complexity if overused.
 * <p>
 * May lead to too many small classes (each factory or product).
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


class ShapeFactory {
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
        Shape circle = shapeFactory.getShape("Circle");
        Shape square = shapeFactory.getShape("Square");
        Shape triangle = shapeFactory.getShape("Triangle");

        circle.draw();
        square.draw();
        triangle.draw();

    }
}
