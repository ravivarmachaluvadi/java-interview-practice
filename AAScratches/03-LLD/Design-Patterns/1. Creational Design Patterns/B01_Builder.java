/*
 * =====================================================================
 *  Builder Pattern (static nested builder)   Creational | Easy   MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Builder - Creational family. Java's idiomatic form: a private
 *   constructor on the product plus a public static nested Builder class
 *   whose setters return `this` so calls chain.
 *
 * INTENT
 *   Build an object that has many optional fields, one named step at a
 *   time, and hand back an immutable instance at the end. Replaces
 *   telescoping constructors - User(a), User(a,b), User(a,b,c), ... - which
 *   are unreadable at the call site and impossible to extend safely.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when a type has roughly 4+ fields, several of them optional, or
 *     several same-typed fields that are easy to swap by accident
 *     (new User("Jane", "Doe") vs new User("Doe", "Jane")).
 *   Use when you want the finished object immutable but built in stages.
 *   Do NOT use for 2-3 mandatory fields - a constructor or a record is
 *     shorter and needs no extra class.
 *   Do NOT share one Builder across threads; the builder itself is mutable.
 *
 * ROLES IN THIS CODE
 *   User            Product - all fields final, no public constructor
 *   User.Builder    Builder - mutable scratch space, one wither per field
 *   Builder.build() the factory step that freezes the scratch space
 *   BuilderExample  Client/Director - decides which fields to set
 *
 * KEY INSIGHT
 *   The builder is the *only* mutable thing in the design, and it is
 *   short-lived. `private User(Builder b)` is what enforces that: the
 *   product can never be constructed half-filled, and it can never be
 *   changed afterwards. Remember the exact combination - private
 *   constructor + static nested class + `return this` + build() - because
 *   Singleton (D01) reuses the same static-nested-class trick for a
 *   completely different reason.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you enforce required fields? Take them in the Builder's
 *     constructor (new Builder(firstName, lastName)) or validate in
 *     build(). This file does neither - case 2 builds an empty User.
 *   - Why must the nested class be static? A non-static inner class needs
 *     an existing User instance, and the whole point is that none exists.
 *   - Builder vs Factory? Factory picks *which* class; Builder configures
 *     *one* class step by step. They compose: a factory can return a builder.
 *   - Lombok @Builder, or records with wither methods - when is hand-rolling
 *     still worth it? When build() must validate or normalise.
 *   - Is the built object thread-safe? Yes, final fields are safely
 *     published; the Builder is not.
 *
 * RUN
 *   main() runs 3 cases: a fully populated build, an empty build (edge -
 *   defaults show through), and builder reuse (tricky - proves the first
 *   product is unaffected by later builder mutation).
 */
class User {

    // Conceptually required. NOTE: nothing in this file enforces that -
    // see INTERVIEW FOLLOW-UPS and case 2.
    private final String firstName;
    private final String lastName;

    // Optional parameters.
    private final String mail;
    private final int age;
    private final String phone;
    private final String address;

    // Private: the only way in is through Builder.build().
    private User(Builder builder) {
        this.mail = builder.mail;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    /** Static nested Builder: usable before any User exists. */
    public static class Builder {
        private String mail;
        private String firstName;
        private String lastName;
        private int age;
        private String phone;
        private String address;

        // Each wither returns `this`, which is what makes the calls chain.
        public Builder withMail(String mail) {
            this.mail = mail;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withAge(int age) {
            this.age = age;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        /** Snapshots the builder's current state into an immutable User. */
        public User build() {
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "mail='" + mail + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

class BuilderExample {

    public static void main(String[] args) {
        // Case 1: typical. Field order at the call site does not matter,
        // and every value is labelled by its method name.
        User full = new User.Builder()
                .withAddress("Bangalore")
                .withAge(34)
                .withLastName("Doe")
                .withFirstName("Jane")
                .withMail("jane.doe@example.com")
                .withPhone("5550100200")
                .build();
        String expectedFull = "User{mail='jane.doe@example.com', firstName='Jane', lastName='Doe'"
                + ", age=34, phone='5550100200', address='Bangalore'}";
        print("case 1 full build ", full, expectedFull);

        // Case 2: edge. Nothing is set, so the object defaults through -
        // proof that this builder does NOT enforce the "required" fields.
        User empty = new User.Builder().build();
        String expectedEmpty = "User{mail='null', firstName='null', lastName='null'"
                + ", age=0, phone='null', address='null'}";
        print("case 2 empty build", empty, expectedEmpty);

        // Case 3: tricky. Reusing a builder is legal; each build() takes a
        // fresh snapshot, so the earlier product is untouched.
        User.Builder reused = new User.Builder().withFirstName("Ann").withAge(30);
        User first = reused.build();
        User second = reused.withAge(31).build();
        print("case 3a first age ", ageOf(first), 30);
        print("case 3b second age", ageOf(second), 31);
        print("case 3c distinct  ", first != second, true);
    }

    /** Pulls the age back out of toString() so the test needs no getters. */
    private static int ageOf(User user) {
        String text = user.toString();
        int start = text.indexOf("age=") + "age=".length();
        return Integer.parseInt(text.substring(start, text.indexOf(',', start)));
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
