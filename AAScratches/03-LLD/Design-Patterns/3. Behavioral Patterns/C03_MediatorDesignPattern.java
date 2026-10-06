/*
 * =====================================================================
 *  Mediator - chat room                              Behavioral | Medium
 * =====================================================================
 *
 * PROBLEM
 *   In a group chat every member's message must reach everyone else. If
 *   each user held references to all other users, n users would need
 *   n*(n-1) links, and "mute" or "direct message" would touch every user.
 *
 * KEY INSIGHT
 *   No user references another user. Everyone talks only to the room
 *   (mediator), which owns the routing rule - here, everyone except the
 *   sender. Links drop from O(n^2) to O(n), and new rules change one class.
 *
 * ROLES IN THIS CODE
 *   ChatMediator        Mediator - addUser, removeUser, sendMessage
 *   ChatMediatorImpl    ConcreteMediator - the roster and the routing rule
 *   User, UserImpl      Colleague - knows the room, not the other users
 *
 * INTERVIEW FOLLOW-UPS
 *   - Mediator vs Observer: Observer is one-way broadcast from a subject;
 *     a mediator routes many-to-many and holds the rules.
 *   - Avoid a god object: one mediator per room or per concern.
 *   - At scale the room becomes a broker topic (Kafka, Redis pub/sub).
 *
 * RUN
 *   3 cases: broadcast in a 3-person room, single member, a member leaves.
 */

import java.util.ArrayList;
import java.util.List;

/** Mediator: the only thing colleagues are allowed to know. */
interface ChatMediator {
    void sendMessage(String message, User sender);

    void addUser(User user);

    void removeUser(User user);
}

/** ConcreteMediator: owns the roster and the routing rule. */
class ChatMediatorImpl implements ChatMediator {

    private final List<User> userList = new ArrayList<>();

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : userList) {
            // The routing rule lives here and nowhere else: do not echo
            // the message back to whoever sent it.
            if (user != sender) {
                user.receiveMessage(sender.getName() + ": " + message);
            }
        }
    }

    @Override
    public void addUser(User user) {
        userList.add(user);
    }

    @Override
    public void removeUser(User user) {
        userList.remove(user);
    }
}

/** Colleague: holds the mediator, never another colleague. */
abstract class User {
    protected final ChatMediator chatMediator;
    protected final String name;

    public User(ChatMediator chatMediator, String name) {
        this.chatMediator = chatMediator;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract void sendMessage(String message);

    abstract void receiveMessage(String message);
}

class UserImpl extends User {

    /** Kept so main can assert what actually arrived. */
    private final List<String> inbox = new ArrayList<>();

    public UserImpl(ChatMediator chatMediator, String name) {
        super(chatMediator, name);
    }

    @Override
    void sendMessage(String message) {
        System.out.println("  " + name + " sends: " + message);
        chatMediator.sendMessage(message, this); // hand off, do not fan out
    }

    @Override
    void receiveMessage(String message) {
        System.out.println("    " + name + " received -> " + message);
        inbox.add(message);
    }

    public List<String> getInbox() {
        return inbox;
    }
}

class MediatorDesignPatternExample {

    public static void main(String[] args) {
        // Case 1: typical - one sender, two receivers, no self-echo.
        System.out.println("case 1: broadcast in a 3-person room");
        ChatMediator room = new ChatMediatorImpl();
        UserImpl ravi = new UserImpl(room, "Ravi");
        UserImpl varma = new UserImpl(room, "Varma");
        UserImpl kiran = new UserImpl(room, "Kiran");
        room.addUser(ravi);
        room.addUser(varma);
        room.addUser(kiran);

        ravi.sendMessage("Hi all");
        print("  1a varma inbox", varma.getInbox(), "[Ravi: Hi all]");
        print("  1b kiran inbox", kiran.getInbox(), "[Ravi: Hi all]");
        print("  1c ravi  inbox", ravi.getInbox(), "[]");

        // Case 2: edge - the only member of a room talks to nobody, and
        // nothing blows up because the sender knows no receivers.
        System.out.println("case 2: single-member room");
        ChatMediator empty = new ChatMediatorImpl();
        UserImpl solo = new UserImpl(empty, "Solo");
        empty.addUser(solo);
        solo.sendMessage("anyone here?");
        print("  2a solo inbox ", solo.getInbox(), "[]");

        // Case 3: tricky - Kiran leaves. Only the mediator's roster changes;
        // Ravi's code is identical, which is the whole point of the pattern.
        System.out.println("case 3: a member leaves");
        room.removeUser(kiran);
        ravi.sendMessage("second message");
        print("  3a varma inbox", varma.getInbox(),
                "[Ravi: Hi all, Ravi: second message]");
        print("  3b kiran inbox", kiran.getInbox(), "[Ravi: Hi all]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
