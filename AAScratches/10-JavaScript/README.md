# JavaScript practice

JavaScript practice for Node.js, one runnable file each, with the same header as the Java files. `main()` prints each actual result next to the expected one.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [01-Essentials](01-Essentials/) | 4 | 3 | The classic JavaScript questions: timers (debounce, throttle), promises (Promise.all), events (an emitter class). |
| [02-Functions](02-Functions/) | 5 | 2 | Closures and `this`: memoize, curry, call/apply/bind from scratch, flatten, deep clone with cycles. |
| [03-Async](03-Async/) | 4 | 2 | Promises and the event loop: allSettled/race/any, retry with backoff, a concurrency limit, predict the print order. |
| **Total** | **13** | **7** | |

## 01-Essentials

The classic JavaScript questions: timers (debounce, throttle), promises (Promise.all), events (an emitter class).

**Do these first:** [A01_Debounce.js](01-Essentials/A01_Debounce.js), [A02_Throttle.js](01-Essentials/A02_Throttle.js), [B01_PromiseAll.js](01-Essentials/B01_PromiseAll.js)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Debounce.js](01-Essentials/A01_Debounce.js) * | Debounce | JavaScript / Easy | Cancelling the previous timer on every call is the whole trick: only the call that is not followed by another within `wait` ms survives long enough to fire. |
| [A02_Throttle.js](01-Essentials/A02_Throttle.js) * | Throttle | JavaScript / Easy | Throttle compares against the last time it fired, so a steady stream of calls fires every `wait` ms; debounce restarts a timer on every call, so the same stream never fires. |
| [B01_PromiseAll.js](01-Essentials/B01_PromiseAll.js) * | Promise.all from Scratch | JavaScript / Medium | Store each value at its own index and count down, rather than pushing as they arrive: push order is finish order, and the caller asked for input order. |
| [C01_EventEmitter.js](01-Essentials/C01_EventEmitter.js) | Event Emitter | JavaScript / Medium | emit loops over a copy: a once-listener removes itself while the loop runs, and splicing the array being walked would make the loop skip the listener after it. |

## 02-Functions

Closures and `this`: memoize, curry, call/apply/bind from scratch, flatten, deep clone with cycles.

**Do these first:** [B01_Curry.js](02-Functions/B01_Curry.js), [B02_BindPolyfill.js](02-Functions/B02_BindPolyfill.js)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Memoize.js](02-Functions/A01_Memoize.js) | Memoize | JavaScript / Easy | Use a Map, not a plain object: an object turns every key into a string, so the number 1 and the string "1" would share a slot, and a key like "__proto__" breaks it. |
| [A02_FlattenArray.js](02-Functions/A02_FlattenArray.js) | Flatten an Array | JavaScript / Easy | Pass the remaining depth down, one less per level: depth 0 means "push this array whole", which is exactly how flat(1) leaves the deeper arrays alone. |
| [B01_Curry.js](02-Functions/B01_Curry.js) * | Curry | JavaScript / Medium | fn.length is how many parameters fn declares, so curry knows when to stop collecting. |
| [B02_BindPolyfill.js](02-Functions/B02_BindPolyfill.js) * | call, apply and bind from Scratch | JavaScript / Medium | To choose `this`, make fn a method of thisArg for one call, because in obj.method() JavaScript sets `this` to obj. |
| [B03_DeepClone.js](02-Functions/B03_DeepClone.js) | Deep Clone | JavaScript / Medium | Record the copy before cloning the children: a cycle then finds the half-built copy instead of recursing forever, and two paths to one shared object give one shared copy. |

## 03-Async

Promises and the event loop: allSettled/race/any, retry with backoff, a concurrency limit, predict the print order.

**Do these first:** [B01_PromiseCombinators.js](03-Async/B01_PromiseCombinators.js), [B03_ConcurrencyLimit.js](03-Async/B03_ConcurrencyLimit.js)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [B01_PromiseCombinators.js](03-Async/B01_PromiseCombinators.js) * | allSettled, race and any from Scratch | JavaScript / Medium | A promise settles once: every later resolve or reject call is ignored. |
| [B02_RetryWithBackoff.js](03-Async/B02_RetryWithBackoff.js) | Retry with Exponential Backoff | JavaScript / Medium | async/await turns retrying into an ordinary loop with try/catch, the way it is written in Java. |
| [B03_ConcurrencyLimit.js](03-Async/B03_ConcurrencyLimit.js) * | Run Tasks with a Concurrency Limit | JavaScript / Medium | Think of `limit` workers, not of tasks: a worker takes the next task the moment it is free, so exactly `limit` run until the queue is empty. |
| [C01_EventLoopOrder.js](03-Async/C01_EventLoopOrder.js) | Event Loop: Predict the Order | JavaScript / Hard | Microtasks are drained completely after every task, so "a timer of 0 ms" always loses to every promise callback already queued, and a then-chain queues its next step only when the previous step has run. |

`*` = must-know. Open any file in Code Viewer (`tools/codeview`): Ctrl+Enter runs it with Node.js and ticks each expected line; Practice hides the solution. Or run `node <file>`.
