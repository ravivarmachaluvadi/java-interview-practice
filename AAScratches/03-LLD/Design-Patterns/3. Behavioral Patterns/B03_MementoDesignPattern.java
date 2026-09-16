import java.util.Stack;

/**
 * ✅ Advantages
 * <p>
 * Preserves encapsulation of object state.
 * <p>
 * Supports undo/redo operations easily.
 * <p>
 * Easy to implement and extend.
 * <p>
 * ⚠️ Disadvantages
 * <p>
 * Can consume a lot of memory if states are large or saved frequently.
 * <p>
 * Managing many mementos can become complex.
 */
//Step 1️⃣ — Memento Class
class EditorMemento {
    private final String content;

    public EditorMemento(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

// Step 2️⃣ — Originator Class
class Editor {
    private String content;

    public void type(String newtext) {
        this.content = newtext;
    }

    public String getContent() {
        return content;
    }

    public EditorMemento save() {
        return new EditorMemento(content);
    }

    public void restore(EditorMemento editorMemento) {
        this.content = editorMemento.getContent();
    }
}

// Step 3️⃣ — Caretaker Class
class History {
    private Stack<EditorMemento> history = new Stack<>();

    public void save(Editor editor) {
        history.push(editor.save());
    }

    public void undo(Editor editor) {
        if (!history.isEmpty()) {
            editor.restore(history.pop());
        }
    }
}

class MementoDesignPattern {
    public static void main(String[] args) {
        Editor editor = new Editor();
        History history = new History();

        editor.type("Version 1");
        history.save(editor);

        editor.type("Version 2");
        history.save(editor);

        editor.type("Version 3");

        System.out.println("Current content : " + editor.getContent());

        history.undo(editor);
        System.out.println("After Undo: " + editor.getContent()); // Version 2

        history.undo(editor);
        System.out.println("After Undo: " + editor.getContent()); // Version 1
    }
}
