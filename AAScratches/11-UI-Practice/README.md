# UI practice (HTML, CSS, JavaScript)

Small screens to build in HTML, CSS and JavaScript, as frontend machine-coding rounds ask. Each page is one file: the header states the task, the page works, and a `<script type="test">` block at the bottom clicks and types through it and checks the result. Browsers skip that block, so opening the file shows the plain page.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [01-Components](01-Components/) | 3 | 2 | Machine-coding screens: a counter, a todo list, an autocomplete with debounce and arrow keys. |
| **Total** | **3** | **2** | |

## 01-Components

Machine-coding screens: a counter, a todo list, an autocomplete with debounce and arrow keys.

**Do these first:** [B01_TodoList.html](01-Components/B01_TodoList.html), [C01_Autocomplete.html](01-Components/C01_Autocomplete.html)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Counter.html](01-Components/A01_Counter.html) | Counter | Machine coding / Easy | Change the state, then redraw from it. |
| [B01_TodoList.html](01-Components/B01_TodoList.html) * | Todo List | Machine coding / Medium | Event delegation: rows come and go with every render, so listeners on each row would have to be added again each time. |
| [C01_Autocomplete.html](01-Components/C01_Autocomplete.html) * | Autocomplete | Machine coding / Hard | Debounce the expensive part (here the search; on a real site the request), not the typing, and keep the highlight as an index into the state rather than as a CSS class to hunt for: arrows, Enter and render then all agree |

`*` = must-know. Open any file in Code Viewer (`tools/codeview`): the page shows beside its code as you type, and Ctrl+Enter runs the test block at the bottom and ticks each check. Practice hides the page's functions.
