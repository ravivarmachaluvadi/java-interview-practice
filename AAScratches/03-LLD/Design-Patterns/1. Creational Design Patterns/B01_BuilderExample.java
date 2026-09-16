/**
 * Builder Pattern is a creational design pattern
 * <p>
 * used to construct complex objects step by step.
 * <p>
 * It allows you to create different representations
 * <p>
 * of the same object using the same construction process.
 * <p>
 * 🧱 Key Advantages
 * <p>
 * ✅ Improves code readability
 * <p>
 * ✅ Avoids constructor telescoping(declaring multiple constructors with different number of parameters)
 * <p>
 * ✅ Makes object immutable once built
 * <p>
 * ✅ Easy to add new optional fields in future
 * <p>
 * ✅ Promotes method chaining
 */
class User {
    // required parameters
    private final String firstName;
    private final String lastName;

    //optional parameters
    private final String mail;
    private final int age;
    private final String phone;
    private final String address;

    private User(Builder builder) {
        this.mail = builder.mail;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    // static inner Builder class
    public static class Builder {
        private String mail;
        private String firstName;
        private String lastName;
        private int age;
        private String phone;
        private String address;

        // optional : create Builder constructor with mandatory attributes
        public Builder withMail(String mail) {
            this.mail = mail;
            return this;
        }

        // setter with kind of returning same object this
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
        User user = new User.Builder()
                .withAddress("Bangalore")
                .withAge(34)
                .withLastName("Doe")
                .withFirstName("Jane")
                .withMail("jane.doe@example.com")
                .withPhone("5550100200")
                .build();

        System.out.println(user);

    }
}
