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
