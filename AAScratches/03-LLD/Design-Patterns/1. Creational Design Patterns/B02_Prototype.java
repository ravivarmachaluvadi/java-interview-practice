/*
 * =====================================================================
 *  Prototype Pattern (clone instead of construct)      Creational | Easy
 * =====================================================================
 *
 * PATTERN
 *   Prototype - Creational family. A Prototype interface declares clone();
 *   an existing, already-configured instance is the template for new ones.
 *
 * INTENT
 *   Create a new object by copying a live one rather than running its
 *   constructor again. Useful when building from scratch is expensive
 *   (remote lookup, parsing, heavy defaults) or when the caller only has
 *   the object, not the recipe that produced it.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when instances are costly to build but cheap to copy, or when a
 *     "template" object is configured once and stamped out many times
 *     (document templates, game entities, pre-warmed request configs).
 *   Use when the concrete class is unknown to the caller - it holds only a
 *     Prototype reference and still gets the right subtype back.
 *   Do NOT use when the object graph is deep and mutable; the copy rules
 *     get harder to maintain than a builder or a copy constructor.
 *   Do NOT reach for Cloneable by reflex - it is a widely criticised API
 *     (see follow-ups). A copy constructor is usually clearer.
 *
 * ROLES IN THIS CODE
 *   Prototype            Prototype - declares clone(), extends Cloneable so
 *                        Object.clone() will not throw
 *   Address              ConcretePrototype - all fields are final Strings,
 *                        so the default shallow copy is already correct
 *   Profile              ConcretePrototype with a MUTABLE field, added to
 *                        show where shallow copying breaks
 *   Profile.deepClone()  the fix: copy the mutable field too
 *   PrototypeExample     Client
 *
 * KEY INSIGHT
 *   super.clone() copies the object's fields bit for bit. For a primitive
 *   or an immutable field (String) that is a real copy; for a reference to
 *   a mutable object it copies the ARROW, not the thing - original and
 *   clone now share one list. Address looks trivially safe only because
 *   every field is a String. The moment one field is a collection or a
 *   custom object, "is this copy shallow or deep?" becomes the whole
 *   question, and that is exactly what the interviewer will ask.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Shallow vs deep copy - demonstrate both (cases 3 and 4 below).
 *   - Why is Cloneable called a broken interface? It declares no clone()
 *     method; it is a marker that merely tells Object.clone() not to throw
 *     CloneNotSupportedException, and clone() bypasses constructors.
 *   - What would you use instead? A copy constructor
 *     (new Address(other)) or a static Address.copyOf(other) - explicit,
 *     final-field friendly, and no checked exception.
 *   - Deep copy without writing it by hand? Serialize and deserialize, or
 *     map through a library - correct but far slower.
 *   - Prototype vs Builder? Builder assembles from parts; Prototype starts
 *     from a finished object.
 *
 * RUN
 *   main() runs 5 cases: clone equality and identity for the immutable
 *   Address, then the shallow-copy trap and the deep-copy fix on Profile.
 */

import java.util.ArrayList;
import java.util.List;

interface Prototype extends Cloneable {
    Prototype clone();
}

/**
 * Every field is a final String, so Object.clone()'s field-by-field copy is
 * already a correct copy - nothing mutable is shared.
 */
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
            // super.clone() allocates a new object and copies fields -
            // it does NOT run this class's constructor.
            return (Address) super.clone();
        } catch (CloneNotSupportedException e) {
            // Unreachable: Prototype extends Cloneable.
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

/**
 * Same pattern, one mutable field. This is where the interesting half of
 * the interview lives.
 */
class Profile implements Prototype {

    private final String name;
    private final List<String> tags;

    public Profile(String name, List<String> tags) {
        this.name = name;
        this.tags = tags;
    }

    /** Shallow: the new Profile points at the SAME list. */
    @Override
    public Prototype clone() {
        try {
            return (Profile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deep: copy the mutable field so the two profiles are independent. */
    public Profile deepClone() {
        Profile shallow = (Profile) clone();
        return new Profile(shallow.name, new ArrayList<>(shallow.tags));
    }

    public List<String> tags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Profile{name='" + name + "', tags=" + tags + '}';
    }
}

class PrototypeExample {

    public static void main(String[] args) {
        // Case 1-2: immutable prototype. Same contents, different object.
        Address address = new Address("field1", "field2", "field3", "field4");
        Prototype copy = address.clone();
        print("case 1 same contents  ", copy.toString().equals(address.toString()), true);
        print("case 2 different object", address == copy, false);

        // Case 3: the trap. A shallow clone shares the mutable list, so
        // mutating the clone's tags is visible through the original.
        Profile original = new Profile("ann", new ArrayList<>(List.of("java")));
        Profile shallow = (Profile) original.clone();
        shallow.tags().add("sql");
        print("case 3 shallow leaks  ", original.tags(), "[java, sql]");

        // Case 4-5: the fix. deepClone() copies the list, so the two are
        // independent again.
        Profile base = new Profile("bob", new ArrayList<>(List.of("java")));
        Profile deep = base.deepClone();
        deep.tags().add("sql");
        print("case 4 deep isolated  ", base.tags(), "[java]");
        print("case 5 deep copy       ", deep.tags(), "[java, sql]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
