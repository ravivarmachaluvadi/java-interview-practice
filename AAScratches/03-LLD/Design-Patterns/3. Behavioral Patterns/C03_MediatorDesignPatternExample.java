import java.util.ArrayList;
import java.util.List;

/**
 * The Mediator Design Pattern in Java is a behavioral
 * <p>
 * design pattern that promotes loose coupling between
 * <p>
 * interacting objects by introducing a mediator object
 * <p>
 * that coordinates their communication.
 * <p>
 * 🎯 Benefits
 * <p>
 * Reduces coupling between classes.
 * <p>
 * Centralizes control of communication logic.
 * <p>
 * Makes the system easier to maintain and extend.
 * <p>
 * ⚠️ Drawbacks
 * <p>
 * Mediator can become too complex if it handles too many interactions.
 * <p>
 * Can turn into a “god object” if not designed carefully.
 */
interface ChatMediator {
    void sendMessage(String message, User sender);

    void addUser(User user);
}

class ChatMediatorImpl implements ChatMediator {

    private List<User> userList = new ArrayList<>();

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : userList) {
            if (user != sender) {
                user.receiveMessage(message);
            }
        }
    }

    @Override
    public void addUser(User user) {
        userList.add(user);
    }
}

abstract class User {
    protected ChatMediator chatMediator;
    protected String name;

    public User(ChatMediator chatMediator, String name) {
        this.chatMediator = chatMediator;
        this.name = name;
    }

    abstract void sendMessage(String message);

    abstract void receiveMessage(String message);
}

class UserImpl extends User {

    public UserImpl(ChatMediator chatMediator, String name) {
        super(chatMediator, name);
    }

    @Override
    void sendMessage(String message) {
        System.out.println(name + " Sending: " + message);
        System.out.println("============================================");
        chatMediator.sendMessage(message, this);

    }

    @Override
    void receiveMessage(String message) {
        System.out.println(name + " received message :" + message);
    }
}

class MediatorDesignPatternExample {
    public static void main(String[] args) {
        ChatMediator chatMediator = new ChatMediatorImpl();

        UserImpl ravi = new UserImpl(chatMediator, "Ravi");
        chatMediator.addUser(ravi);
        chatMediator.addUser(new UserImpl(chatMediator, "Varma"));
        chatMediator.addUser(new UserImpl(chatMediator, "Kiran"));

        ravi.sendMessage("Hi All, This is Ravi");
    }
}