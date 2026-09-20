/*
 * =====================================================================
 *  Memento Pattern - text editor undo                        Behavioral
 * =====================================================================
 *
 * PATTERN
 *   Memento (Behavioral, GoF). Also called Snapshot / Token.
 *
 * INTENT
 *   Capture an object's internal state so it can be restored later,
 *   WITHOUT exposing the fields that make up that state. The snapshot is
 *   meant to be opaque: whoever stores it hands it back without reading it.
 *   In this demo that opacity is a convention, not a compiler guarantee -
 *   EditorMemento.getContent() is public for brevity, so History could read
 *   every snapshot if it wanted to.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    : undo/redo, transactional rollback, checkpoints in a wizard,
 *            game save points, "restore previous version" in an editor.
 *   Not    : when the state is huge or saved on every keystroke - each
 *            memento is a full copy, so memory grows linearly.
 *            Prefer a command log (store the delta, replay in reverse)
 *            when the state is big and the edits are small.
 *
 * ROLES IN THIS CODE
 *   EditorMemento  Memento   - immutable snapshot; by convention only the
 *                              Originator reads its content.
 *   Editor         Originator- creates snapshots (save) and restores
 *                              itself from one (restore).
 *   History        Caretaker - owns a Stack of mementos. It stores and
 *                              returns them but never inspects them.
 *   main           Client    - drives the three roles.
 *
 * KEY INSIGHT
 *   The Caretaker holds the state but does not read it; the Originator
 *   reads it but does not hold it. That split is the whole pattern - it is
 *   how undo works without leaking the Originator's fields to the world.
 *   To enforce the split for real, nest the memento as a private inner
 *   class of the Originator, or expose its content only through a narrow
 *   package-private accessor, instead of the public getter used here.
 *   A Stack gives LIFO undo for free; add a second stack for redo.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Memento vs Command for undo? Memento stores WHAT the state was;
 *     Command stores HOW to reverse it. Real editors use both.
 *   - How do you add redo? Push popped mementos onto a redo stack and
 *     clear it on any fresh edit.
 *   - How do you bound memory? Cap the stack size, or snapshot only every
 *     N edits and replay commands forward from the nearest snapshot.
 *
 * RUN
 *   main() runs 3 cases (undo chain, undo on empty history, snapshot is
 *   immune to later edits) and prints actual vs expected.
 */

import java.util.Stack;

/** Memento: an immutable snapshot of the Editor's content. The getter is
 *  public only to keep this demo short; see KEY INSIGHT for the real fix. */
class EditorMemento {
    private final String content;

    public EditorMemento(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

/** Originator: the object whose state we snapshot and restore. */
class Editor {
    private String content = "";

    public void type(String newText) {
        this.content = newText;
    }

    public String getContent() {
        return content;
    }

    /** Wraps the current state in a memento the caretaker can park. */
    public EditorMemento save() {
        return new EditorMemento(content);
    }

    /** Only the originator understands how to read a memento back. */
    public void restore(EditorMemento editorMemento) {
        this.content = editorMemento.getContent();
    }
}

/** Caretaker: stores mementos and, by convention, never looks inside them. */
class History {
    private final Stack<EditorMemento> history = new Stack<>();

    public void save(Editor editor) {
        history.push(editor.save());
    }

    /** Returns false when there is nothing left to undo, so main can assert. */
    public boolean undo(Editor editor) {
        if (history.isEmpty()) {
            return false;
        }
        editor.restore(history.pop());
        return true;
    }

    public int size() {
        return history.size();
    }
}

class MementoDesignPattern {

    public static void main(String[] args) {
        // Case 1: typical undo chain - save after each committed edit.
        Editor editor = new Editor();
        History history = new History();

        editor.type("Version 1");
        history.save(editor);
        editor.type("Version 2");
        history.save(editor);
        editor.type("Version 3"); // not saved: this is the "unsaved" edit

        print("case 1a current  ", editor.getContent(), "Version 3");
        history.undo(editor);
        print("case 1b undo once", editor.getContent(), "Version 2");
        history.undo(editor);
        print("case 1c undo twice", editor.getContent(), "Version 1");

        // Case 2: edge - undo with an empty history must be a safe no-op.
        print("case 2a stack size", history.size(), 0);
        print("case 2b undo empty", history.undo(editor), false);
        print("case 2c unchanged ", editor.getContent(), "Version 1");

        // Case 3: tricky - a memento is a value, later typing cannot mutate it.
        Editor draft = new Editor();
        draft.type("snapshot me");
        EditorMemento snapshot = draft.save();
        draft.type("overwritten");
        print("case 3a after edit", draft.getContent(), "overwritten");
        draft.restore(snapshot);
        print("case 3b restored  ", draft.getContent(), "snapshot me");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
