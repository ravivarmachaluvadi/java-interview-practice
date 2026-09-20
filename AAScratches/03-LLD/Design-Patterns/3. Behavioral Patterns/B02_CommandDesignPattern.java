/*
 * =====================================================================
 *  Command Design Pattern                   LLD | Easy  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Command - Behavioral family (GoF). Also called Action or Transaction.
 *
 * INTENT
 *   Wrap a request as an object that holds everything needed to perform it: the
 *   receiver, the method to call, and any arguments. The invoker then triggers work
 *   through one uniform execute() call without knowing what the work is.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when: requests must be stored, queued, logged, retried, scheduled or undone -
 *     remote controls, job queues, editor undo stacks, transactional outbox, CQRS
 *     command handlers, GUI menu items that share an action with a toolbar button.
 *   Do not use when: the caller can just call the method. One command class per method
 *     is real ceremony; pay it only for the queue/undo/log capability it unlocks.
 *
 * ROLES IN THIS CODE
 *   Command              -> Command interface (execute)
 *   TurnOnCommand, TurnOffCommand,
 *   VolumeUpCommand      -> ConcreteCommands: each binds one Television method
 *   Television           -> Receiver: the object that actually does the work
 *   Remote               -> Invoker: holds a Command, presses it, knows nothing else
 *   CommandDesignPattern.main -> Client: builds commands and loads them into the invoker
 *
 * KEY INSIGHT
 *   Command is Strategy with the receiver already bound in. Because the "what to do"
 *   and the "who to do it to" are captured inside one object, that object can be put
 *   in a list, sent over a wire, replayed, or paired with an undo() - which is exactly
 *   what a plain method call can never be. Recognise it whenever a requirement says
 *   undo, redo, macro, retry or audit log.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Add undo: give Command an undo() and have the invoker push executed commands on
 *     a stack (this is where Command meets Memento for non-invertible state).
 *   - Macro command: a ConcreteCommand holding a List<Command> - that is Composite.
 *   - Command vs Strategy: both inject behaviour; Command binds a receiver and is
 *     designed to be stored and replayed, Strategy is chosen and run immediately.
 *   - Where in the JDK/Spring? Runnable handed to an ExecutorService is Command;
 *     so is every @Transactional service method behind a CQRS command handler.
 *
 * RUN
 *   main() runs 3 cases: a single button press, the same remote re-loaded with two more
 *   commands, and the edge case of pressing a button with no command bound.
 */

import java.util.ArrayList;
import java.util.List;

interface Command {
    void execute();
}

/** Receiver: knows how to do the real work, knows nothing about commands. */
class Television {

    private final List<String> actionLog = new ArrayList<>();

    public void turnOn() {
        actionLog.add("TV turned on");
    }

    public void turnOff() {
        actionLog.add("TV turned off");
    }

    public void increaseVolume() {
        actionLog.add("TV volume increased");
    }

    public void decreaseVolume() {
        actionLog.add("TV volume decreased");
    }

    /** Exposed so main() can assert on what actually reached the receiver. */
    List<String> getActionLog() {
        return actionLog;
    }
}

class TurnOnCommand implements Command {

    private final Television tv;   // the receiver is bound into the command itself

    public TurnOnCommand(Television tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.turnOn();
    }
}

class TurnOffCommand implements Command {

    private final Television tv;

    public TurnOffCommand(Television tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.turnOff();
    }
}

class VolumeUpCommand implements Command {

    private final Television tv;

    public VolumeUpCommand(Television tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.increaseVolume();
    }
}

/** Invoker: triggers the request but never learns which receiver or method it hits. */
class Remote {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        if (command == null) {
            throw new IllegalStateException("No command bound to this button");
        }
        command.execute();
    }
}

class CommandDesignPattern {

    public static void main(String[] args) {
        Television tv = new Television();
        Remote remote = new Remote();

        // Case 1: one command loaded into the invoker and fired.
        remote.setCommand(new TurnOnCommand(tv));
        remote.pressButton();
        print("case 1 after turn on", tv.getActionLog(), "[TV turned on]");

        // Case 2: the same invoker driving different requests - only the command changes.
        remote.setCommand(new VolumeUpCommand(tv));
        remote.pressButton();
        remote.setCommand(new TurnOffCommand(tv));
        remote.pressButton();
        print("case 2 after volume+off", tv.getActionLog(),
                "[TV turned on, TV volume increased, TV turned off]");

        // Case 3 (edge): an unbound button must fail loudly instead of NPE-ing.
        String result;
        try {
            new Remote().pressButton();
            result = "no exception";
        } catch (IllegalStateException e) {
            result = "IllegalStateException: " + e.getMessage();
        }
        print("case 3 unbound button", result,
                "IllegalStateException: No command bound to this button");
    }

    private static void print(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + "   " + (ok ? "[OK]" : "[FAIL]"));
    }
}
