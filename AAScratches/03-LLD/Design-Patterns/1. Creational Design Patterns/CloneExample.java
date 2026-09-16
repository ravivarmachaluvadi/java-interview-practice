/**
 * Demonstrates shallow cloning of an immutable object using the {@code Cloneable} interface.
 *
 * The program creates a {@link User} instance, prints it, clones it via {@code clone()},
 * and shows that the original and clone are distinct objects but share the same field values.
 *
 * Approach:
 * 1. Implement {@code Cloneable} in {@code User}.
 * 2. Override {@code clone()} to delegate to {@link Object#clone()}, casting the result.
 * 3. Use the cloned instance to verify reference inequality while maintaining identical state.
 *
 * Time Complexity: O(1) – cloning a fixed-size object involves constant work.
 * Space Complexity: O(1) – only a new {@code User} reference is created; no additional data structures are used.
 */
public class CloneExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        User user = new User("ravi", "mail1@com");

        System.out.println(user);
        User clone = user.clone();
        System.out.println(clone);
        System.out.println(user == clone);
    }
}

class User implements Cloneable {

    private final String name;
    private final String mail;

    public User(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    @Override
    protected User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
