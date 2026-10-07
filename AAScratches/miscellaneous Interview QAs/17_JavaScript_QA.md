# JavaScript — Interview Q&A

**What this is:** the JavaScript questions asked most often in interviews, with a short
answer first and an example where it helps. It ends with the **coding tasks**
interviewers ask you to write (debounce, `Promise.all`, flatten, curry) and a quick
**predict-the-output** round.

**Learn the concepts first:** [15_JavaScript_Fundamentals.md](15_JavaScript_Fundamentals.md)
(it also lists online playgrounds — paste any example below into the browser console or
https://playcode.io to run it). React questions: [18_React_QA.md](18_React_QA.md).

## Weight legend

| Mark | What it means |
| --- | --- |
| ★★★ | Asked in almost every JavaScript round |
| ★★ | Asked often |
| ★ | Occasional |

## Contents

| # | Question | Weight | One-line answer |
| --- | --- | --- | --- |
| 1 | [var vs let vs const](#1-var-vs-let-vs-const) | ★★★ | Function-scoped vs block-scoped; `const` can't be reassigned |
| 2 | [Hoisting, TDZ](#2-hoisting-and-the-temporal-dead-zone) | ★★★ | Declarations move up; `let`/`const` unusable before their line |
| 3 | [== vs ===](#3--vs-) | ★★★ | `==` converts types; always use `===` |
| 4 | [Data types](#4-data-types-primitives-vs-references) | ★★★ | 7 primitives by value; objects by reference |
| 5 | [null vs undefined](#5-null-vs-undefined) | ★★ | Not set vs intentionally empty |
| 6 | [Truthy and falsy](#6-truthy-and-falsy-values) | ★★ | 8 falsy values; everything else truthy |
| 7 | [Closures](#7-what-is-a-closure) | ★★★ | A function remembers variables from where it was created |
| 8 | [setTimeout in a loop](#8-the-settimeout-in-a-loop-question) | ★★★ | `var` → 3 3 3; `let` → 0 1 2 |
| 9 | [this](#9-how-is-this-determined) | ★★★ | Decided by how the function is called |
| 10 | [Arrow vs regular functions](#10-arrow-functions-vs-regular-functions) | ★★★ | Arrows have no own `this`, `arguments` or `new` |
| 11 | [call, apply, bind](#11-call-apply-and-bind) | ★★ | Set `this` now (call/apply) or later (bind) |
| 12 | [Prototypes](#12-prototypes-and-prototypal-inheritance) | ★★★ | Objects inherit from other objects via a chain |
| 13 | [Classes](#13-classes-in-javascript) | ★★ | Syntax over prototypes; `#private` fields |
| 14 | [Event loop](#14-the-event-loop) | ★★★ | One thread; queues of callbacks run when the stack is empty |
| 15 | [Microtasks vs macrotasks](#15-microtasks-vs-macrotasks-predict-the-output) | ★★★ | Promises run before timers |
| 16 | [Callbacks → promises → async](#16-callbacks-promises-and-asyncawait) | ★★ | Same async work, increasingly readable |
| 17 | [Promise combinators](#17-promiseall-vs-allsettled-vs-race-vs-any) | ★★★ | all, allSettled, race, any |
| 18 | [Async error handling](#18-error-handling-in-async-code) | ★★ | `try`/`catch` around `await`; `.catch()` on chains |
| 19 | [Spread vs rest](#19-spread-vs-rest) | ★★ | Same `...`: expands vs collects |
| 20 | [Shallow vs deep copy](#20-shallow-vs-deep-copy) | ★★★ | Spread is shallow; `structuredClone` is deep |
| 21 | [map vs forEach; reduce](#21-map-vs-foreach-and-mapfilterreduce) | ★★★ | `map` returns a new array; `forEach` returns nothing |
| 22 | [Mutating array methods](#22-which-array-methods-mutate) | ★★ | sort, splice, push… mutate; toSorted doesn't |
| 23 | [Pure functions, immutability](#23-pure-functions-and-immutability) | ★★ | Same input → same output, no side effects |
| 24 | [Higher-order functions, currying](#24-higher-order-functions-and-currying) | ★★ | Functions taking/returning functions |
| 25 | [Debounce vs throttle](#25-debounce-vs-throttle) | ★★★ | Wait for quiet vs at most once per interval |
| 26 | [Memoization](#26-memoization) | ★★ | Cache results by input |
| 27 | [Bubbling, delegation](#27-event-bubbling-capturing-and-delegation) | ★★★ | Events travel up; one listener on the parent |
| 28 | [preventDefault vs stopPropagation](#28-preventdefault-vs-stoppropagation) | ★★ | Cancel the browser action vs stop the bubbling |
| 29 | [Storage vs cookies](#29-localstorage-vs-sessionstorage-vs-cookies) | ★★★ | Only cookies go to the server; tokens in HttpOnly cookies |
| 30 | [ES modules vs CommonJS](#30-es-modules-vs-commonjs) | ★★ | `import`/`export` vs `require` |
| 31 | [Strict mode](#31-strict-mode) | ★ | Turns silent mistakes into errors |
| 32 | [0.1 + 0.2, NaN](#32-why-is-01--02-not-03-and-what-is-nan) | ★★ | Floating point; NaN ≠ NaN |
| 33 | [Map, Set, WeakMap](#33-map-vs-object-set-and-weakmap) | ★★ | Any-type keys, unique values, GC-friendly keys |
| 34 | [Object.freeze vs const](#34-objectfreeze-vs-const) | ★★ | Freeze the object vs fix the variable |
| 35 | [Generators](#35-generators-and-iterators) | ★ | Functions that pause and resume |
| 36 | [Memory leaks](#36-memory-leaks-in-javascript) | ★★ | Listeners, timers, closures, detached DOM |
| 37 | [JS vs TypeScript](#37-javascript-vs-typescript) | ★★ | TypeScript adds compile-time types |
| 38 | [Implement Promise.all](#38-coding-implement-promiseall) | ★★★ | Collect in order, reject on first failure |
| 39 | [Implement helpers](#39-coding-flatten-curry-once-deep-clone) | ★★★ | flatten, curry, once, deep clone |
| 40 | [Output round](#40-predict-the-output-quick-round) | ★★★ | Coercion, sorting, ASI, `parseInt` |

---

## 1. var vs let vs const

**Weight:** ★★★

| | `var` | `let` | `const` |
| --- | --- | --- | --- |
| Scope | Function | Block `{ }` | Block `{ }` |
| Reassign | Yes | Yes | **No** |
| Redeclare in same scope | Yes | No | No |
| Before its line | `undefined` (hoisted) | ReferenceError (TDZ) | ReferenceError (TDZ) |

**Use `const` by default, `let` when you must reassign, never `var`.** `const` stops
reassignment of the variable — the object it points to can still change.

---

## 2. Hoisting and the temporal dead zone

**Weight:** ★★★

**Hoisting:** before code runs, JavaScript registers all declarations in their scope.

- `var` declarations are hoisted and set to `undefined`.
- **Function declarations** are hoisted completely — callable before their line.
- `let`, `const` and `class` are hoisted but **not initialised**. Using them before the
  declaration line throws a ReferenceError — that zone is the **temporal dead zone
  (TDZ)**.

```js
console.log(a);   // undefined
var a = 1;
console.log(b);   // ReferenceError: Cannot access 'b' before initialization
let b = 2;
greet();          // works
function greet() {}
```

---

## 3. == vs ===

**Weight:** ★★★

- `===` (strict) compares value **and** type, no conversion.
- `==` (loose) converts types first, with surprising rules: `0 == ""` → `true`,
  `"1" == 1` → `true`, `null == undefined` → `true`, `[] == false` → `true`.

**Always use `===`.** The one accepted exception: `x == null` checks for both `null` and
`undefined`.

---

## 4. Data types: primitives vs references

**Weight:** ★★★

- **Primitives (7):** `string`, `number`, `bigint`, `boolean`, `undefined`, `null`,
  `symbol`. Immutable, **copied by value**.
- **Objects:** everything else — plain objects, arrays, functions, dates, maps.
  **Copied by reference** — two variables can point to the same object.

```js
const a = { n: 1 };
const b = a;
b.n = 2;
a.n;            // 2 — same object
```

**Checking types:** `typeof` (but `typeof null` is `"object"` — a historical bug, and
arrays are `"object"` too — use `Array.isArray`), `instanceof` for class instances.

---

## 5. null vs undefined

**Weight:** ★★

- **`undefined`** — "no value assigned yet": an unset variable, a missing property or
  argument, a function with no `return`.
- **`null`** — "intentionally empty": you set it.
- `typeof undefined` is `"undefined"`; `typeof null` is `"object"`.
- `null == undefined` is `true`; `null === undefined` is `false`.
- `value ?? fallback` treats both as missing.

---

## 6. Truthy and falsy values

**Weight:** ★★

**Falsy (8):** `false`, `0`, `-0`, `0n`, `""`, `null`, `undefined`, `NaN`.
**Everything else is truthy** — including `"0"`, `"false"`, `[]`, `{}` and functions.

**Practical trap:** `count || 10` replaces a valid `0`; use `count ?? 10`.

---

## 7. What is a closure?

**Weight:** ★★★

**Short answer:** a closure is a function together with the variables from the scope
where it was **created**. It keeps access to them even after that outer function has
returned.

```js
function makeCounter() {
  let count = 0;                   // private state
  return {
    increment: () => ++count,
    get: () => count,
  };
}
const c = makeCounter();
c.increment();  c.increment();
c.get();        // 2 — count lives on inside the closure
```

**Where you use closures:** private state (module pattern), callbacks and event
handlers, `debounce`/`throttle`/`once` helpers, partial application, React hooks.

**Watch out:** a closure keeps everything it references alive — capturing a large object
in a long-lived callback is a classic memory leak.

---

## 8. The setTimeout-in-a-loop question

**Weight:** ★★★

```js
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log(i), 100);
}
// 3 3 3
```

**Why:** `var` is function-scoped, so there is **one** `i` shared by all three
callbacks. By the time they run (after the loop ends), `i` is 3.

**Fixes:**

```js
// 0 1 2 — let creates a new i for each iteration
for (let i = 0; i < 3; i++) setTimeout(() => console.log(i), 100);

for (var i = 0; i < 3; i++) {
  ((j) => setTimeout(() => console.log(j), 100))(i);   // old-style: capture with an IIFE
}
```

---

## 9. How is this determined?

**Weight:** ★★★

**By how the function is called** (arrow functions excepted):

| Call | `this` |
| --- | --- |
| `obj.method()` | `obj` |
| `fn()` | `undefined` in strict mode / modules; the global object otherwise |
| `new Fn()` | The newly created object |
| `fn.call(x)` / `fn.apply(x)` / `fn.bind(x)()` | `x` |
| Arrow function | `this` of the surrounding code where it was written |
| DOM listener with `function` | The element the listener is attached to |

**Classic bug:** passing a method as a callback loses `this`:

```js
const counter = { n: 0, inc() { this.n++; } };
setTimeout(counter.inc, 0);              // this is not counter → error or wrong object
setTimeout(() => counter.inc(), 0);      // ✅
setTimeout(counter.inc.bind(counter), 0); // ✅
```

---

## 10. Arrow functions vs regular functions

**Weight:** ★★★

| | Regular function | Arrow function |
| --- | --- | --- |
| `this` | Depends on the call | Inherited from the surrounding scope |
| `arguments` object | Yes | No — use `...args` |
| Can be used with `new` | Yes | No |
| Hoisted (declarations) | Yes | No (it's a variable) |
| Syntax | `function f(a) { return a; }` | `const f = a => a;` |

**When to use which:** arrows for callbacks and short functions (no `this` surprises);
regular functions or class methods for object methods that need their own `this`.

---

## 11. call, apply and bind

**Weight:** ★★

All three set `this` explicitly:

```js
function introduce(greeting, punctuation) {
  return `${greeting}, I'm ${this.name}${punctuation}`;
}
const asha = { name: "Asha" };

introduce.call(asha, "Hi", "!");        // arguments one by one   → "Hi, I'm Asha!"
introduce.apply(asha, ["Hello", "."]);  // arguments as an array  → "Hello, I'm Asha."
const hiAsha = introduce.bind(asha, "Hey");   // returns a NEW function for later
hiAsha("?");                            // "Hey, I'm Asha?"
```

**Memory aid:** **c**all = **c**ommas, **a**pply = **a**rray, **bind** = later.

---

## 12. Prototypes and prototypal inheritance

**Weight:** ★★★

**Short answer:** every object has a hidden link to another object, its **prototype**.
When a property isn't found on an object, JavaScript looks at its prototype, then the
prototype's prototype, up to `Object.prototype` — the **prototype chain**.

```js
const animal = { speak() { return `${this.name} makes a sound`; } };
const dog = Object.create(animal);    // dog → animal → Object.prototype → null
dog.name = "Rex";
dog.speak();                          // found on animal
Object.getPrototypeOf(dog) === animal; // true
```

**With classes:** methods are stored once on `ClassName.prototype` and shared by every
instance — memory-efficient. `class Dog extends Animal` links
`Dog.prototype → Animal.prototype`.

> **Java comparison:** Java inherits from **classes** (blueprints); JavaScript inherits
> from **objects** at run time.

---

## 13. Classes in JavaScript

**Weight:** ★★

- `class` is syntax over prototypes: `constructor`, methods, `extends`, `super`,
  `static`, getters/setters.
- **Private fields** with `#`: `#balance` — truly inaccessible from outside (ES2022).
- No interfaces, abstract classes, overloading or `protected` — TypeScript adds
  interfaces and access modifiers at compile time.
- Class bodies run in strict mode; classes aren't usable before their declaration (TDZ).

---

## 14. The event loop

**Weight:** ★★★

**Short answer:** JavaScript runs on **one thread** with one **call stack**. Slow
operations (timers, network, file I/O) are handed to the browser or Node; their
callbacks wait in **queues**. The **event loop** repeatedly: runs the current code until
the stack is empty → runs **all microtasks** → runs **one task** from the task queue →
lets the browser render → repeats.

| Queue | Holds | Priority |
| --- | --- | --- |
| Microtask queue | Promise callbacks (`then`, `catch`, code after `await`), `queueMicrotask` | Emptied completely before the next task |
| Task (macrotask) queue | `setTimeout`, `setInterval`, I/O, UI events | One per loop turn |

**Consequences:**

- `setTimeout(fn, 0)` runs **after** all current code and all pending promise callbacks.
- A long synchronous loop blocks everything — clicks, rendering, timers. Split heavy work
  or move it to a Web Worker.

See it live: https://www.jsv9000.app

---

## 15. Microtasks vs macrotasks: predict the output

**Weight:** ★★★ — appears in most interviews.

```js
console.log("A");
setTimeout(() => console.log("B"), 0);
Promise.resolve().then(() => console.log("C"));
(async () => {
  console.log("D");
  await null;
  console.log("E");
})();
console.log("F");
```

**Answer: A, D, F, C, E, B.**

1. Synchronous code first: `A`, then the async function runs **synchronously until its
   first `await`** → `D`, then `F`.
2. Microtasks, in the order they were queued: `C` (queued first), then `E` (the code
   after `await`).
3. Finally the timer task: `B`.

---

## 16. Callbacks, promises and async/await

**Weight:** ★★

| Style | Example | Problem it solved |
| --- | --- | --- |
| Callbacks | `getUser(id, (err, user) => { … })` | — but nesting leads to "callback hell" and scattered error handling |
| Promises | `getUser(id).then(…).catch(…)` | Flat chains; one `catch` for the chain |
| `async`/`await` | `const user = await getUser(id);` | Reads like synchronous code; normal `try`/`catch` |

`async` functions always return a Promise; `await` pauses that function (not the thread)
until the promise settles.

**Promise states:** *pending* → *fulfilled* or *rejected* (together: *settled*). A
promise settles once and never changes after that.

---

## 17. Promise.all vs allSettled vs race vs any

**Weight:** ★★★

| Method | Resolves | Rejects | Use for |
| --- | --- | --- | --- |
| `Promise.all` | When **all** succeed — array of results in input order | As soon as **any** rejects | Parallel calls that all must succeed |
| `Promise.allSettled` | When all finish — `{status, value/reason}` for each | Never | Run all, report each outcome |
| `Promise.race` | First to **settle**, success or failure | If the first to settle rejects | Timeouts |
| `Promise.any` | First to **succeed** | Only if all reject (`AggregateError`) | Fastest of several mirrors |

```js
const timeout = ms =>
  new Promise((_, reject) => setTimeout(() => reject(new Error("Timeout")), ms));

const user = await Promise.race([fetchUser(1), timeout(3000)]);
```

**Sequential vs parallel:** `await a(); await b();` runs one after the other;
`await Promise.all([a(), b()])` runs both at once.

---

## 18. Error handling in async code

**Weight:** ★★

```js
async function load() {
  try {
    const res = await fetch("/api/orders");
    if (!res.ok) throw new Error(`HTTP ${res.status}`);  // no throw on 4xx/5xx
    return await res.json();
  } catch (err) {
    log(err);
    throw err;            // rethrow unless you can really handle it
  }
}
```

**Traps:**

- `try`/`catch` only catches a rejection if you **`await`** inside the `try` —
  `return fetch(…)` without `await` escapes it.
- A promise rejection with no handler becomes an "unhandled rejection" (Node can exit
  on it). Always end chains with `.catch()` or `await` inside `try`.
- Errors thrown inside a `setTimeout` callback can't be caught by a `try` around the
  `setTimeout` call.

---

## 19. Spread vs rest

**Weight:** ★★

Same `...` syntax, opposite jobs:

```js
// Spread — EXPANDS an array/object into elements
const all = [...listA, ...listB];
const copy = { ...user, age: 32 };
Math.max(...[3, 7, 2]);                  // 7

// Rest — COLLECTS the remaining elements into an array/object
function sum(...nums) { return nums.reduce((a, b) => a + b, 0); }
const [first, ...others] = [1, 2, 3];   // others = [2, 3]
const { password, ...safeUser } = user; // remove a field
```

---

## 20. Shallow vs deep copy

**Weight:** ★★★

```js
const original = { name: "Asha", address: { city: "Pune" } };

const shallow = { ...original };       // also Object.assign, [...arr], arr.slice()
shallow.address.city = "Delhi";        // ❌ changes original too — nested object is shared

const deep = structuredClone(original);
deep.address.city = "Mumbai";          // ✅ original untouched
```

- **Shallow copy:** new top-level object; nested objects are still shared.
- **Deep copy:** everything copied. `structuredClone` handles nested objects, arrays,
  `Date`, `Map`, `Set` and circular references — but not functions or class methods.
- `JSON.parse(JSON.stringify(x))` — old trick; loses `Date` (becomes a string),
  `undefined`, functions, `Map`, `Set`.

---

## 21. map vs forEach, and map/filter/reduce

**Weight:** ★★★

- `forEach` runs a function for each item and returns **`undefined`** — for side effects.
- `map` returns a **new array** of transformed items — for transformations.
- Neither can `break` early — use `for…of`, `some` or `find` if you need to stop.

```js
const orders = [
  { total: 250, paid: true },
  { total: 90, paid: false },
  { total: 400, paid: true },
];

const paidTotal = orders
  .filter(o => o.paid)                 // keep matching items
  .map(o => o.total)                   // transform each
  .reduce((sum, t) => sum + t, 0);     // combine into one value → 650
```

**`reduce` explained:** it carries an *accumulator* through the array; the second
argument is its starting value. Always pass it — without one, `reduce` on an empty array
throws.

---

## 22. Which array methods mutate?

**Weight:** ★★

| Mutate the original | Return a new array |
| --- | --- |
| `push`, `pop`, `shift`, `unshift` | `map`, `filter`, `slice`, `concat`, `flat` |
| `splice`, `sort`, `reverse`, `fill` | `toSorted`, `toReversed`, `toSpliced`, `with` (ES2023) |

**Why it matters:** in React and Redux, mutating state arrays causes missed re-renders.
`arr.sort()` also sorts **as strings** by default — `[10, 9, 1].sort()` gives
`[1, 10, 9]`; pass a comparator `(a, b) => a - b`.

---

## 23. Pure functions and immutability

**Weight:** ★★

- **Pure function:** same inputs always give the same output, and it changes nothing
  outside itself (no mutation, no I/O). Easy to test, cache and reason about.
- **Immutability:** instead of changing data, create a new copy with the change
  (`{ ...user, age: 32 }`, `arr.map(…)`).
- React state, Redux reducers and memoization all rely on these: a change is detected
  by a **new reference**.

---

## 24. Higher-order functions and currying

**Weight:** ★★

- **Higher-order function:** takes a function as an argument or returns one — `map`,
  `filter`, `setTimeout`, `debounce`.
- **Currying:** turning `f(a, b, c)` into `f(a)(b)(c)`. Useful for building specialised
  functions from general ones:

```js
const discount = rate => price => price * (1 - rate);
const festive = discount(0.2);
festive(1000);     // 800
```

A general `curry` helper is in Q39.

---

## 25. Debounce vs throttle

**Weight:** ★★★

| | Debounce | Throttle |
| --- | --- | --- |
| Runs | Once, **after** events stop for N ms | At most once **every** N ms while events continue |
| Use for | Search-as-you-type, auto-save, window resize end | Scroll handlers, mouse move, rate-limited buttons |

```js
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);                              // restart the wait on every call
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

function throttle(fn, interval) {
  let last = 0;
  return function (...args) {
    const now = Date.now();
    if (now - last >= interval) {
      last = now;
      fn.apply(this, args);
    }
  };
}

const search = debounce(q => console.log("search", q), 300);
```

Both rely on **closures** to remember `timer` / `last` between calls.

---

## 26. Memoization

**Weight:** ★★

Cache a function's results by input so repeated calls are instant. Only for **pure**
functions.

```js
function memoize(fn) {
  const cache = new Map();
  return function (arg) {
    if (!cache.has(arg)) cache.set(arg, fn.call(this, arg));
    return cache.get(arg);
  };
}

const slowSquare = n => { for (let i = 0; i < 1e8; i++); return n * n; };
const fastSquare = memoize(slowSquare);
fastSquare(9);   // slow the first time
fastSquare(9);   // instant
```

Watch memory: an unbounded cache grows forever — limit its size or use a `WeakMap` for
object keys.

---

## 27. Event bubbling, capturing and delegation

**Weight:** ★★★

**Event propagation has three phases:** *capturing* (from `window` down to the target),
*target*, then *bubbling* (from the target back up). Listeners run in the bubbling phase
by default (`addEventListener(type, fn, { capture: true })` for capturing).

**Event delegation:** instead of a listener on every child, put **one** on the parent and
check `event.target`. It works for children added later and uses less memory.

```js
document.querySelector("#list").addEventListener("click", (event) => {
  const item = event.target.closest("li");     // the clicked <li>, if any
  if (item) console.log("Clicked", item.dataset.id);
});
```

`event.target` is the element actually clicked; `event.currentTarget` is the element the
listener is attached to.

---

## 28. preventDefault vs stopPropagation

**Weight:** ★★

- `event.preventDefault()` — cancels the **browser's default action**: a form
  submitting, a link navigating, a checkbox toggling. The event still bubbles.
- `event.stopPropagation()` — stops the event **travelling to parent elements**. The
  default action still happens.
- They are independent; use either or both.

---

## 29. localStorage vs sessionStorage vs cookies

**Weight:** ★★★

| | `localStorage` | `sessionStorage` | Cookies |
| --- | --- | --- | --- |
| Lifetime | Until cleared | Until the tab closes | Until the expiry date (or session) |
| Size | ~5 MB per origin (browser-dependent) | ~5 MB | ~4 KB each |
| Sent to the server | No | No | **Yes, with every request** to that domain |
| Readable by JavaScript | Yes | Yes | Yes — **unless `HttpOnly`** |

**The security point interviewers want:** anything in `localStorage` can be read by any
script on the page, so an XSS bug steals it. Store **auth tokens in `HttpOnly`, `Secure`,
`SameSite` cookies**, which JavaScript can't read. Use `localStorage` for non-sensitive
preferences (theme, drafts).

---

## 30. ES modules vs CommonJS

**Weight:** ★★

| | ES modules (ESM) | CommonJS (CJS) |
| --- | --- | --- |
| Syntax | `import x from "./x.js"`, `export …` | `const x = require("./x")`, `module.exports = …` |
| Loading | Static — analysed before running | Dynamic — `require` runs when reached |
| Tree-shaking (removing unused code) | Yes | Hard |
| Where | Browsers, modern Node, all bundlers | Older Node code and packages |
| Top-level `await` | Yes | No |

New code should use ESM. In Node, `"type": "module"` in `package.json` or `.mjs` files
switch it on.

---

## 31. Strict mode

**Weight:** ★

`"use strict"` at the top of a file or function makes JavaScript stricter: assigning to
an undeclared variable throws (instead of creating a global), `this` is `undefined` in
plain function calls, and writing to read-only properties throws. **ES modules and
classes are always strict** — so modern code already is.

---

## 32. Why is 0.1 + 0.2 not 0.3, and what is NaN?

**Weight:** ★★

- All numbers are 64-bit floating point (like Java's `double`). `0.1` and `0.2` can't be
  represented exactly, so `0.1 + 0.2` is `0.30000000000000004`. Compare with a tolerance
  (`Math.abs(a - b) < Number.EPSILON`) and store money as integer paise/cents.
- **`NaN`** ("Not a Number") is the result of an invalid number operation:
  `Number("abc")`, `0 / 0`. Its type is `"number"`, and it is **not equal to itself**
  (`NaN === NaN` is `false`). Test with `Number.isNaN(x)`.
- Integers are exact only up to `Number.MAX_SAFE_INTEGER` (2⁵³ − 1); beyond that use
  `BigInt` (`123n`).

---

## 33. Map vs Object, Set and WeakMap

**Weight:** ★★

| | Object | `Map` |
| --- | --- | --- |
| Keys | Strings and symbols only | Any value, including objects |
| Order | Insertion order, except integer-like keys come first | Always insertion order |
| Size | `Object.keys(o).length` | `map.size` |
| Iteration | `Object.entries(o)` | `for (const [k, v] of map)` directly |
| Best for | Fixed-shape records, JSON | Dictionaries with frequent adds and removes |

- **`Set`** — unique values: `[...new Set(arr)]` removes duplicates; ES2025 adds `union`,
  `intersection`, `difference`.
- **`WeakMap` / `WeakSet`** — keys must be objects and are held **weakly**: if nothing
  else references the key object, it can be garbage-collected along with its entry. Use
  them to attach data to objects (DOM nodes, instances) without leaking memory. Not
  iterable.

---

## 34. Object.freeze vs const

**Weight:** ★★

- `const` — the **variable** can't be reassigned; the object can still change.
- `Object.freeze(obj)` — the **object's** properties can't be added, removed or changed.
  Freezing is **shallow**: nested objects are still mutable.

```js
const config = Object.freeze({ port: 8080, db: { host: "x" } });
config.port = 9090;       // ignored (TypeError in strict mode)
config.db.host = "y";     // works — nested object not frozen
```

---

## 35. Generators and iterators

**Weight:** ★

- An **iterator** is an object with a `next()` method returning `{ value, done }`.
  Anything iterable works with `for…of` and spread.
- A **generator** (`function*`) pauses at each `yield` and resumes on the next `next()`
  call — an easy way to make lazy or infinite sequences.

```js
function* ids() {
  let id = 1;
  while (true) yield id++;
}
const gen = ids();
gen.next().value;   // 1
gen.next().value;   // 2
```

Async generators (`async function*` with `for await…of`) read streams page by page.

---

## 36. Memory leaks in JavaScript

**Weight:** ★★

JavaScript frees memory automatically once nothing references it. Leaks happen when
something **still references** data you no longer need:

| Cause | Fix |
| --- | --- |
| Event listeners never removed | `removeEventListener`, or the `signal` option with an `AbortController` |
| `setInterval` never cleared | `clearInterval` (in React: effect cleanup) |
| Closures capturing large objects | Capture only what's needed |
| Detached DOM nodes still referenced from JS | Drop the references |
| Global variables and unbounded caches | Scope them; limit cache size; `WeakMap` |

**Finding them:** Chrome DevTools → Memory → compare heap snapshots before and after an
action.

---

## 37. JavaScript vs TypeScript

**Weight:** ★★

- **TypeScript = JavaScript + static types**, checked at compile time and erased in the
  JavaScript it produces.
- **Benefits:** catches typos and wrong arguments before running, autocompletion and safe
  refactoring, types as documentation — the more code and people, the bigger the gain.
- **Costs:** a build step and a learning curve; types can be bypassed (`any`), and there
  is **no runtime checking** — validate external data (API responses) with a library
  such as Zod.
- Most professional React and Node codebases use TypeScript; the current major version
  is 7 (npm, Oct 2026).

---

## 38. Coding: implement Promise.all

**Weight:** ★★★

```js
function promiseAll(iterable) {
  return new Promise((resolve, reject) => {
    const items = Array.from(iterable);
    const results = new Array(items.length);
    let settled = 0;

    if (items.length === 0) return resolve(results);

    items.forEach((item, index) => {
      Promise.resolve(item)                 // accept plain values too
        .then(value => {
          results[index] = value;           // keep the input order
          settled += 1;
          if (settled === items.length) resolve(results);
        })
        .catch(reject);                     // first failure rejects the whole thing
    });
  });
}

promiseAll([1, Promise.resolve(2), fetchUser(3)]).then(console.log);
```

**Points the interviewer checks:** results in **input order** (not completion order),
plain values accepted, empty input resolves immediately with `[]`, the first rejection
wins.

---

## 39. Coding: flatten, curry, once, deep clone

**Weight:** ★★★

**Flatten a nested array:**

```js
const flatten = arr =>
  arr.reduce((acc, x) => acc.concat(Array.isArray(x) ? flatten(x) : x), []);

flatten([1, [2, [3, [4]]]]);   // [1, 2, 3, 4]
// built-in: [1, [2, [3, [4]]]].flat(Infinity)
```

**Curry:**

```js
function curry(fn) {
  return function curried(...args) {
    return args.length >= fn.length
      ? fn.apply(this, args)
      : (...more) => curried.apply(this, [...args, ...more]);
  };
}
const add3 = curry((a, b, c) => a + b + c);
add3(1)(2)(3);   // 6
add3(1, 2)(3);   // 6
```

**Once** (run a function only the first time):

```js
function once(fn) {
  let called = false;
  let result;
  return function (...args) {
    if (!called) {
      called = true;
      result = fn.apply(this, args);
    }
    return result;
  };
}
const init = once(() => console.log("initialised"));
init(); init();   // logs once
```

**Deep clone** (objects and arrays; in real code use `structuredClone`):

```js
function deepClone(value) {
  if (value === null || typeof value !== "object") return value;   // primitives
  if (Array.isArray(value)) return value.map(deepClone);
  const copy = {};
  for (const [key, val] of Object.entries(value)) copy[key] = deepClone(val);
  return copy;
}
```

Follow-up the interviewer may ask: this version doesn't handle `Date`, `Map`, `Set` or
circular references — `structuredClone` does.

---

## 40. Predict the output: quick round

**Weight:** ★★★ — cover the answer column and test yourself.

| Code | Output | Why |
| --- | --- | --- |
| `typeof NaN` | `"number"` | NaN is a number value |
| `typeof typeof 1` | `"string"` | `typeof` returns a string |
| `[] + []` | `""` | Both become empty strings |
| `[] + {}` | `"[object Object]"` | Object converted to a string |
| `"5" - 2` / `"5" + 2` | `3` / `"52"` | `-` converts to numbers; `+` concatenates strings |
| `true + 1` | `2` | `true` becomes 1 |
| `1 < 2 < 3` / `3 > 2 > 1` | `true` / `false` | `3 > 2` is `true`, and `true > 1` is `1 > 1` |
| `[1, 2, 3] == "1,2,3"` | `true` | The array becomes the string `"1,2,3"` |
| `Math.max()` | `-Infinity` | The identity value for max |
| `[10, 1, 5].sort()` | `[1, 10, 5]` | Default sort compares strings |
| `0 \|\| "a"` / `0 ?? "a"` | `"a"` / `0` | `\|\|` skips falsy; `??` only null/undefined |
| `!!"false"` / `!!""` | `true` / `false` | Non-empty strings are truthy |
| `[1, 2, 3].map(parseInt)` | `[1, NaN, NaN]` | `map` passes the index as `parseInt`'s radix |
| `0.1 + 0.2 === 0.3` | `false` | Floating-point rounding |
| `let x = 1; { let x = 2; } x` | `1` | The inner `x` is block-scoped |

**Automatic semicolon insertion trap:**

```js
function getConfig() {
  return
  {
    debug: true
  };
}
getConfig();   // undefined — a semicolon is inserted after `return`
```

Keep the `{` on the same line as `return`.
