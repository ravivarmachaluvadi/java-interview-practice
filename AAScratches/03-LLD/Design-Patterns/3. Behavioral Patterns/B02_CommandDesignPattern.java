/**
 * 🧩 What is the Command Pattern?
 * <p>
 * Definition:
 * <p>
 * The Command Pattern turns a request into a standalone object
 * <p>
 * that contains all the information about the request.
 * <p>
 * This allows you to parameterize methods with different requests,
 * <p>
 * queue or log commands, and support undoable operations.
 * <p>
 * In simple terms:
 * <p>
 * You encapsulate an action (command) and its receiver
 * <p>
 * (the object that does the work) inside a class.
 * <p>
 * The caller doesn’t need to know how the command is executed — it just calls execute()
 */

// Command
interface Command {
    void execute();
}

//Command Receiver
class Television {

    public void turnOn() {
        System.out.println("TV turned on");
    }

    public void turnOff() {
        System.out.println("TV turned off");
    }

    public void increaseVolume() {
        System.out.println("TV volume increased");
    }

    public void decreaseVolume() {
        System.out.println("TV volume decreased");
    }
}

// 3️⃣ Concrete Commands
class TurnOnCommand implements Command {
    Television tv;

    public TurnOnCommand(Television tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.turnOn();
    }
}

class TurnOffCommand implements Command {
    Television tv;

    public TurnOffCommand(Television tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.turnOff();
    }
}

//4️⃣ Invoker
class Remote {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}

class CommandDesignPattern {
    public static void main(String[] args) {
        Television tv = new Television();
        Command turnOn = new TurnOnCommand(tv);
        Command turnOff = new TurnOffCommand(tv);

        Remote remote = new Remote();
        remote.setCommand(turnOn);
        remote.pressButton();

        remote.setCommand(turnOff);
        remote.pressButton();

    }
}