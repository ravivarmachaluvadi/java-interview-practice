/*
 * =====================================================================
 *  Memento - text editor undo                        Behavioral | Easy
 * =====================================================================
 *
 * PROBLEM
 *   An editor (or a form, or a game) must go back to an earlier saved
 *   state on Ctrl+Z. The undo history has to keep those states without
 *   reaching into the editor's private fields.
 *
 * KEY INSIGHT
 *   The editor (Originator) makes a sealed snapshot of itself; the history
 *   (Caretaker) stores snapshots but never reads them. Only the editor can
 *   restore from one. A stack gives last-in-first-out undo for free.
 *
 * ROLES IN THIS CODE
 *   EditorMemento   Memento - immutable snapshot
 *   Editor          Originator - save() and restore()
 *   History         Caretaker - a Stack of mementos, never inspected
 *
 * INTERVIEW FOLLOW-UPS
 *   - Memento vs Command for undo: Memento stores WHAT the state was,
 *     Command stores HOW to reverse a change (B02_CommandDesignPattern).
 *   - Redo: push undone snapshots onto a second stack; clear it on a new
 *     edit. Bound memory by capping the stack.
 *
 * RUN
 *   3 cases: undo twice, undo on an empty history, restore one snapshot.
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
