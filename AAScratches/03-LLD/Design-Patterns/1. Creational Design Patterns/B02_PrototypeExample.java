/**
 * 🔹 What is the Prototype Pattern?
 * <p>
 * <p>
 * Prototype Pattern is a creational design pattern that
 * <p>
 * allows you to create new objects by copying (cloning)
 * <p>
 * existing ones instead of creating from scratch.
 * <p>
 * It’s useful when:
 * <p>
 * Object creation is expensive (e.g., takes time, resources).
 * <p>
 * You want to avoid subclassing and instead copy an existing prototype.
 */
interface Prototype extends Cloneable {
    Prototype clone();
}

class Address implements Prototype {

    private final String field1;
    private final String field2;
    private final String field3;
    private final String field4;

    public Address(String field1, String field2, String field3, String field4) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    @Override
    public Prototype clone() {
        try {
            return (Address) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Address{" +
                "field1='" + field1 + '\'' +
                ", field2='" + field2 + '\'' +
                ", field3='" + field3 + '\'' +
                ", field4='" + field4 + '\'' +
                '}';
    }
}

class PrototypeExample {
    public static void main(String[] args) {
        Address address = new Address("field1", "field2", "field3", "field4");

        System.out.println(address);
        Prototype clone = address.clone();
        System.out.println(clone);
        System.out.println(address == clone); // false
    }
}
