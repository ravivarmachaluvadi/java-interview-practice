# JavaScript practice

JavaScript practice for Node.js, one runnable file each, with the same header as the Java files. `main()` prints each actual result next to the expected one.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [01-Essentials](01-Essentials/) | 3 | 2 | The classic JavaScript questions: timers (debounce), promises (Promise.all), events (an emitter class). |
| **Total** | **3** | **2** | |

## 01-Essentials

The classic JavaScript questions: timers (debounce), promises (Promise.all), events (an emitter class).

**Do these first:** [A01_Debounce.js](01-Essentials/A01_Debounce.js), [B01_PromiseAll.js](01-Essentials/B01_PromiseAll.js)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Debounce.js](01-Essentials/A01_Debounce.js) * | Debounce | JavaScript / Easy | Cancelling the previous timer on every call is the whole trick: only the call that is not followed by another within `wait` ms survives long enough to fire. |
| [B01_PromiseAll.js](01-Essentials/B01_PromiseAll.js) * | Promise.all from Scratch | JavaScript / Medium | Store each value at its own index and count down, rather than pushing as they arrive: push order is finish order, and the caller asked for input order. |
| [C01_EventEmitter.js](01-Essentials/C01_EventEmitter.js) | Event Emitter | JavaScript / Medium | emit loops over a copy: a once-listener removes itself while the loop runs, and splicing the array being walked would make the loop skip the listener after it. |

`*` = must-know. Open any file in Code Viewer (`tools/codeview`): Ctrl+Enter runs it with Node.js and ticks each expected line; Practice hides the solution. Or run `node <file>`.
