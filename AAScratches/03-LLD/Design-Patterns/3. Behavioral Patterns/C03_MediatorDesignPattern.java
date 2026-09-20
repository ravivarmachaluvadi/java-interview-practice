/*
 * =====================================================================
 *  Mediator Pattern - chat room                              Behavioral
 * =====================================================================
 *
 * PATTERN
 *   Mediator (Behavioral, GoF). Also seen as "hub", "broker", "controller".
 *
 * INTENT
 *   Define one object that encapsulates how a set of objects interact.
 *   Colleagues stop referring to each other directly, so the N-to-N mesh of
 *   references collapses into N-to-1 spokes around a hub.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    : chat rooms, air-traffic control, a dialog where enabling one
 *            widget depends on three others, an order service coordinating
 *            payment + inventory + shipping.
 *   Not    : when the interaction is genuinely simple, or when the hub keeps
 *            growing - a mediator that knows every rule in the system is a
 *            god object, and you have moved the complexity, not removed it.
 *
 * ROLES IN THIS CODE
 *   ChatMediator      Mediator           - the contract: addUser, removeUser,
 *                                          sendMessage.
 *   ChatMediatorImpl  ConcreteMediator   - holds the roster and decides who
 *                                          receives what (here: everyone
 *                                          except the sender).
 *   User              Colleague          - abstract participant; knows the
 *                                          mediator, not the other users.
 *   UserImpl          ConcreteColleague  - sends through the mediator and
 *                                          records what it receives.
 *   main              Client             - builds the room and sends.
 *
 * KEY INSIGHT
 *   No colleague holds a reference to another colleague. Adding a fourth
 *   user changes nothing in the first three; the routing rule lives in one
 *   place, so "mute this user" or "direct message" is a mediator change
 *   only. Wiring goes from O(n^2) links to O(n).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Mediator vs Observer? Observer is one subject broadcasting one way to
 *     listeners; Mediator is many peers talking both ways through a hub.
 *     A mediator is often implemented using observers internally.
 *   - Mediator vs Facade? A facade simplifies a subsystem for outsiders and
 *     the subsystem does not know it exists; colleagues know their mediator.
 *   - How do you stop the god object? Split by concern (one mediator per
 *     room), or move rules into strategy objects the mediator consults.
 *   - How would you scale this to a real chat service? The mediator becomes
 *     a message broker topic; users subscribe instead of being in a List.
 *
 * RUN
 *   main() runs 3 cases (broadcast to a room, single-member room, a user
 *   who left) and prints each inbox actual vs expected.
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
