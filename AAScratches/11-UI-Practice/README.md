# UI practice (HTML, CSS, JavaScript)

Small screens to build in HTML, CSS and JavaScript, as frontend machine-coding rounds ask. Each page is one file: the header states the task, the page works, and a `<script type="test">` block at the bottom clicks and types through it and checks the result. Browsers skip that block, so opening the file shows the plain page.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [01-Components](01-Components/) | 11 | 2 | Machine-coding screens: counter, star rating, accordion, todo list, pagination, OTP input, nested checkboxes, carousel, autocomplete, progress bars with a queue, file explorer. |
| **Total** | **11** | **2** | |

## 01-Components

Machine-coding screens: counter, star rating, accordion, todo list, pagination, OTP input, nested checkboxes, carousel, autocomplete, progress bars with a queue, file explorer.

**Do these first:** [B01_TodoList.html](01-Components/B01_TodoList.html), [C01_Autocomplete.html](01-Components/C01_Autocomplete.html)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Counter.html](01-Components/A01_Counter.html) | Counter | Machine coding / Easy | Change the state, then redraw from it. |
| [A02_StarRating.html](01-Components/A02_StarRating.html) | Star Rating | Machine coding / Easy | Keep the preview (hover) separate from the choice (rating), and light up to hover when it is set, else up to rating: leaving the stars is then just hover = 0, nothing to undo. |
| [A03_Accordion.html](01-Components/A03_Accordion.html) | Accordion | Machine coding / Easy | "Only one open" is free when the state is a single index instead of a true/false per item: there is no way to represent two open panels, so no code has to close the others. |
| [B01_TodoList.html](01-Components/B01_TodoList.html) * | Todo List | Machine coding / Medium | Event delegation: rows come and go with every render, so listeners on each row would have to be added again each time. |
| [B02_Pagination.html](01-Components/B02_Pagination.html) | Pagination | Machine coding / Medium | Compute which numbers to show as a small sorted set first, and only then decide about gaps. |
| [B03_OtpInput.html](01-Components/B03_OtpInput.html) | OTP Input | Machine coding / Medium | Handle paste yourself: with maxlength="1" the browser puts only the first pasted character in one box, and the rest of the user's SMS code is lost. |
| [B04_NestedCheckboxes.html](01-Components/B04_NestedCheckboxes.html) | Nested Checkboxes | Machine coding / Medium | Store only the leaves and compute every parent from them on each render: nothing can go out of sync, and the indeterminate state falls out of "some but not all". |
| [B05_Carousel.html](01-Components/B05_Carousel.html) | Image Carousel | Machine coding / Medium | "(i + count) % count" handles wrapping in both directions: -1 becomes count - 1, and count becomes 0. |
| [C01_Autocomplete.html](01-Components/C01_Autocomplete.html) * | Autocomplete | Machine coding / Hard | Debounce the expensive part (here the search; on a real site the request), not the typing, and keep the highlight as an index into the state rather than as a CSS class to hunt for: arrows, Enter and render then all agree |
| [C02_ProgressBarQueue.html](01-Components/C02_ProgressBarQueue.html) | Progress Bars with a Queue | Machine coding / Hard | Keep one pump() that fills free slots from the queue, and call it from both places a slot can appear: when a bar is added and when a bar finishes. |
| [C03_FileExplorer.html](01-Components/C03_FileExplorer.html) | File Explorer | Machine coding / Hard | Keep open / selected as references to the data nodes, not to DOM elements. |

`*` = must-know. Open any file in Code Viewer (`tools/codeview`): the page shows beside its code as you type, and Ctrl+Enter runs the test block at the bottom and ticks each check. Practice hides the page's functions.
