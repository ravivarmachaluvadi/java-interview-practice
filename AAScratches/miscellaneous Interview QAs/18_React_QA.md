# React — Interview Q&A

**What this is:** the React questions asked most often in interviews — short answer
first, then an example where it helps. It ends with two **debugging scenarios** and a
**predict-what-happens** round.

**Learn the concepts first:** [16_React_Fundamentals.md](16_React_Fundamentals.md) (it
lists online playgrounds — paste any example below into https://react.new to run it).
JavaScript questions: [17_JavaScript_QA.md](17_JavaScript_QA.md).

**Versions:** answers describe React 19 (latest on npm: 19.3, 7 Oct 2026). Features
added in 19.x are marked; React 19.2 details and React Compiler 1.0 were confirmed on
react.dev.

## Weight legend

| Mark | What it means |
| --- | --- |
| ★★★ | Asked in almost every React round |
| ★★ | Asked often |
| ★ | Occasional |

## Contents

| # | Question | Weight | One-line answer |
| --- | --- | --- | --- |
| 1 | [What is React](#1-what-is-react-and-why-use-it) | ★★★ | A library for building UIs from components; UI = f(state) |
| 2 | [Virtual DOM, reconciliation](#2-virtual-dom-and-reconciliation) | ★★★ | Compare old and new render output; update only the differences |
| 3 | [JSX](#3-what-does-jsx-compile-to) | ★★ | Syntax that compiles to function calls creating elements |
| 4 | [Function vs class components](#4-function-vs-class-components) | ★★ | Functions + hooks are the standard now |
| 5 | [Props vs state](#5-props-vs-state) | ★★★ | Inputs from the parent vs data the component owns |
| 6 | [Keys](#6-why-do-lists-need-keys-and-why-not-the-index) | ★★★ | Identify items between renders; index breaks on reorder |
| 7 | [Controlled vs uncontrolled](#7-controlled-vs-uncontrolled-components) | ★★★ | Value in React state vs in the DOM |
| 8 | [State not updating immediately](#8-why-doesnt-state-update-immediately) | ★★★ | Updates are scheduled; use the updater function |
| 9 | [Immutability](#9-why-must-state-be-immutable) | ★★★ | React detects changes by reference |
| 10 | [useEffect](#10-how-does-useeffect-work) | ★★★ | Sync with external systems; deps decide when; cleanup undoes |
| 11 | [useEffect mistakes](#11-common-useeffect-mistakes) | ★★★ | Missing deps, loops, races, effects that shouldn't exist |
| 12 | [useEffect vs useLayoutEffect](#12-useeffect-vs-uselayouteffect) | ★★ | After paint vs before paint |
| 13 | [useRef](#13-what-is-useref-for) | ★★ | DOM access and values that don't re-render |
| 14 | [useMemo, useCallback, memo](#14-usememo-vs-usecallback-vs-reactmemo) | ★★★ | Cache a value, a function, a component's render |
| 15 | [React Compiler](#15-what-is-the-react-compiler) | ★★ | Automatic memoization at build time |
| 16 | [Context](#16-context-api-and-when-not-to-use-it) | ★★★ | Share data without props; not for fast-changing data |
| 17 | [Prop drilling](#17-prop-drilling-and-how-to-avoid-it) | ★★ | Passing props through layers that don't use them |
| 18 | [Lifting state up](#18-lifting-state-up) | ★★ | Move shared state to the closest common parent |
| 19 | [Custom hooks](#19-custom-hooks) | ★★★ | Reuse stateful logic, not state |
| 20 | [Rules of hooks](#20-rules-of-hooks) | ★★★ | Top level only, React functions only — order matters |
| 21 | [Lifecycle → hooks](#21-class-lifecycle-methods-vs-hooks) | ★★ | Mount/update/unmount all map to useEffect |
| 22 | [Error boundaries](#22-error-boundaries) | ★★ | Catch render errors; still class components |
| 23 | [lazy and Suspense](#23-code-splitting-with-lazy-and-suspense) | ★★ | Load components on demand with a fallback |
| 24 | [useTransition, useDeferredValue](#24-usetransition-and-usedeferredvalue) | ★★ | Mark updates as non-urgent |
| 25 | [Effects run twice](#25-why-do-my-effects-run-twice) | ★★★ | StrictMode in development, on purpose |
| 26 | [React 19](#26-whats-new-in-react-19) | ★★★ | Actions, useActionState, useOptimistic, use, ref prop |
| 27 | [Server vs Client Components](#27-server-components-vs-client-components) | ★★ | Render on the server with no JS shipped vs interactive |
| 28 | [CSR vs SSR vs SSG](#28-csr-vs-ssr-vs-ssg) | ★★★ | Where and when HTML is produced |
| 29 | [State management](#29-choosing-state-management) | ★★★ | Local → context → Zustand/Redux; server data → TanStack Query |
| 30 | [Redux Toolkit](#30-redux-and-redux-toolkit-basics) | ★★ | Store, slices, actions, selectors |
| 31 | [Performance](#31-optimising-react-performance) | ★★★ | Measure, then fix re-renders, bundle size and lists |
| 32 | [Long lists](#32-rendering-very-long-lists) | ★★ | Virtualise: render only what's visible |
| 33 | [Portals](#33-portals) | ★ | Render into another DOM node — modals |
| 34 | [Passing refs](#34-passing-refs-to-components) | ★★ | `ref` is a normal prop in React 19 |
| 35 | [HOCs, render props, hooks](#35-hocs-render-props-and-hooks) | ★★ | Three ways to share logic; hooks won |
| 36 | [Synthetic events](#36-synthetic-events) | ★ | React's cross-browser event wrapper |
| 37 | [Security](#37-security-in-react) | ★★ | React escapes output; beware `dangerouslySetInnerHTML` |
| 38 | [Testing](#38-testing-react-components) | ★★ | React Testing Library: test what users see |
| 39 | [Forms](#39-forms-in-react) | ★ | Controlled inputs, React Hook Form, form actions |
| 40 | [Too many re-renders](#40-scenario-a-component-re-renders-too-often) | ★★★ | Profile, then memo / move state / split context |
| 41 | [Stale state in an interval](#41-scenario-stale-state-in-an-interval) | ★★★ | Closure captured old state; use the updater |
| 42 | [Predict what happens](#42-predict-what-happens-quick-round) | ★★★ | Batching, `&&` with 0, keys resetting state |

---

## 1. What is React, and why use it?

**Weight:** ★★★

**Short answer:** React is a JavaScript **library for building user interfaces** out of
reusable **components**. You describe what the UI should look like for the current
state; when state changes, React updates the page efficiently.

**Why teams use it:**

- **Components** — small, reusable, testable pieces.
- **Declarative** — describe the result, not the DOM steps to get there.
- **One-way data flow** — data down via props, events up via callbacks — easy to trace.
- **Huge ecosystem** — routing, data fetching, UI kits, frameworks (Next.js), React
  Native for mobile.

It is a **library**, not a full framework: routing, data fetching and forms come from
other libraries or a framework built on React.

---

## 2. Virtual DOM and reconciliation

**Weight:** ★★★

- Your components return a **description** of the UI (React elements — plain objects),
  often called the **virtual DOM**.
- On a state change, React re-runs the affected components, **compares** the new tree
  with the previous one (**reconciliation**, or "diffing"), and applies **only the
  differences** to the real DOM, which is the slow part.

**How the comparison stays fast:**

- An element of a **different type** (`<div>` → `<span>`, `ComponentA` → `ComponentB`)
  is torn down and rebuilt, including its state.
- The **same type** is updated in place.
- In lists, **keys** match items between renders (Q6).

**A precise follow-up:** the virtual DOM is not "faster than the DOM" by magic — it
spares *you* from deciding which DOM operations to do, and batches the necessary ones.

---

## 3. What does JSX compile to?

**Weight:** ★★

JSX is syntax that a compiler (Babel, esbuild, SWC — used by Vite and Next.js) turns
into function calls:

```jsx
const el = <h1 className="title">Hi {name}</h1>;
// compiles (with the modern JSX transform) to roughly:
const el2 = jsx("h1", { className: "title", children: ["Hi ", name] });
```

The result is a plain object describing the element. So JSX can be stored in variables,
returned, and passed as props — it's just a value. Expressions go in `{ }`; statements
(`if`, `for`) don't.

---

## 4. Function vs class components

**Weight:** ★★

| | Function components | Class components |
| --- | --- | --- |
| Syntax | A function returning JSX | A class with `render()` |
| State and side effects | Hooks (`useState`, `useEffect`) | `this.state`, lifecycle methods |
| `this` | None | Needed everywhere (binding bugs) |
| Status | **The standard** for new code | Legacy; still needed for error boundaries (Q22) |

Hooks (React 16.8) made function components able to do everything — with less code and
logic reuse through custom hooks.

---

## 5. Props vs state

**Weight:** ★★★

| | Props | State |
| --- | --- | --- |
| Comes from | The parent | The component itself (`useState`) |
| Can the component change it? | **No** — read-only | Yes, through its setter |
| Change causes re-render | When the parent passes new props | Yes |
| Like | Function arguments | Local variables that survive between renders |

A value can be state in a parent and a prop in its child — that is how data flows down.

---

## 6. Why do lists need keys, and why not the index?

**Weight:** ★★★

**Keys tell React which list item is which between renders**, so it can keep, move or
remove the right element and its state.

**Why not the array index:** when items are inserted, removed or reordered, the index of
each item changes. React then matches the **wrong** elements — text typed into an input
or a ticked checkbox appears on a different row, and more DOM work is done.

```jsx
{todos.map(todo => <TodoItem key={todo.id} todo={todo} />)}      // ✅ stable id
{todos.map((todo, i) => <TodoItem key={i} todo={todo} />)}       // ❌ if order can change
```

The index is acceptable only for static lists that never reorder. Keys must be unique
among siblings, not globally.

---

## 7. Controlled vs uncontrolled components

**Weight:** ★★★

| | Controlled | Uncontrolled |
| --- | --- | --- |
| Where the value lives | React state | The DOM element |
| How you read it | From state, on every change | With a ref, when you need it |
| Code | `value={name} onChange={e => setName(e.target.value)}` | `defaultValue="x" ref={inputRef}` |
| Good for | Validation as you type, formatting, dependent fields | Simple forms, file inputs, integrating non-React code |

Controlled is the common default. Form libraries such as React Hook Form use
uncontrolled inputs under the hood for performance on large forms.

---

## 8. Why doesn't state update immediately?

**Weight:** ★★★

`setState` doesn't change the variable — it **schedules a re-render**. The current
render's variable keeps its old value.

```jsx
const [count, setCount] = useState(0);

function handleClick() {
  setCount(count + 1);
  console.log(count);          // still 0 — the new value arrives on the next render
}
```

**Batching:** since React 18, multiple state updates in the same event (and in promises
and timeouts) are combined into **one** re-render.

**Updating from the previous value:** use the **updater function**, which receives the
latest value:

```jsx
setCount(count + 1); setCount(count + 1);       // +1 — both read the same count
setCount(c => c + 1); setCount(c => c + 1);     // +2
```

---

## 9. Why must state be immutable?

**Weight:** ★★★

React decides whether something changed by comparing **references** (`Object.is`), not
by deep comparison. If you mutate an object and pass the same object back, React sees
"no change" and may skip the re-render; memoized children and effects also miss it.

```jsx
items.push(newItem); setItems(items);           // ❌ same array → may not re-render
setItems([...items, newItem]);                  // ✅ new array
setUser({ ...user, address: { ...user.address, city: "Pune" } });   // ✅ nested update
```

Immutability also makes undo/redo, time-travel debugging and cheap change detection
possible. Libraries like Immer let you write "mutating" code that produces new objects.

---

## 10. How does useEffect work?

**Weight:** ★★★

**Purpose:** synchronise a component with something **outside React** — network,
subscriptions, timers, browser APIs.

```jsx
useEffect(() => {
  const socket = connect(roomId);       // set up
  return () => socket.disconnect();     // clean up
}, [roomId]);                           // dependencies
```

| Dependencies | When the effect runs |
| --- | --- |
| `[a, b]` | After the first render, and after any render where `a` or `b` changed |
| `[]` | Once after the first render |
| Omitted | After every render |

**Order:** React renders, updates the DOM, the browser paints, **then** effects run.
**Cleanup** runs before the effect re-runs and when the component unmounts.

**Think of it as** "keep this in sync with these values" — not "run this on mount".

---

## 11. Common useEffect mistakes

**Weight:** ★★★

| Mistake | What goes wrong | Fix |
| --- | --- | --- |
| Missing dependencies | Effect uses stale values | Include every value it uses (the lint rule tells you) |
| Setting state that is also a dependency | Infinite render loop | Use an updater function, or rethink the dependency |
| An object or function created during render as a dependency | Effect runs every render (new reference each time) | Move it inside the effect, `useMemo`/`useCallback`, or the React Compiler |
| No cleanup for a subscription or timer | Leaks; duplicate handlers | Return a cleanup function |
| Fetching without cancelling | A slow old response overwrites a newer one (race) | `AbortController`, or an `ignore` flag in cleanup |
| An effect to compute data from props/state | Extra render, out-of-sync values | Calculate during render |
| An effect to respond to a click | Hard-to-follow logic | Put it in the event handler |

---

## 12. useEffect vs useLayoutEffect

**Weight:** ★★

- `useEffect` runs **after** the browser paints — doesn't block the screen. Use it for
  almost everything.
- `useLayoutEffect` runs **after the DOM update but before paint**, synchronously. Use it
  only when you must measure the DOM and change it before the user sees a flicker (e.g.
  positioning a tooltip). It blocks painting, so keep it short.

---

## 13. What is useRef for?

**Weight:** ★★

`useRef(initial)` returns an object `{ current }` that **persists across renders**, and
changing `current` **does not re-render**.

- **DOM access:** `<input ref={inputRef} />` → `inputRef.current.focus()`.
- **Mutable values not shown on screen:** timer ids, the previous value, an "is mounted"
  flag, the latest callback.

**State vs ref:** if changing the value should update the screen → state; otherwise →
ref. Don't read or write `ref.current` during rendering (except for lazy
initialisation).

---

## 14. useMemo vs useCallback vs React.memo

**Weight:** ★★★

| | Caches | Returns | Typical use |
| --- | --- | --- | --- |
| `useMemo(() => compute(a), [a])` | A **computed value** | The value | Expensive calculations; stable objects passed as props |
| `useCallback(fn, [deps])` | A **function** | The same function reference | Callbacks passed to memoized children or used as effect dependencies |
| `memo(Component)` | A component's **render result** | A memoized component | Skip re-rendering when props are unchanged (shallow comparison) |

**How they work together:** `memo(Child)` only helps if the props really stay the same —
so the parent wraps objects in `useMemo` and callbacks in `useCallback`.

**Don't overuse them:** each has a cost and adds complexity. Measure with the React
DevTools Profiler first. With the React Compiler enabled, most manual memoization is
unnecessary (Q15).

---

## 15. What is the React Compiler?

**Weight:** ★★

- A **build-time tool** that analyses components and hooks and adds memoization
  automatically — the equivalent of applying `memo`, `useMemo` and `useCallback` where
  they help.
- **React Compiler 1.0** was released on 7 Oct 2025 (confirmed: react.dev); Vite,
  Next.js and Expo can start new apps with it enabled.
- It relies on your code following the **rules of React** (pure rendering, rules of
  hooks, no state mutation); the ESLint plugin reports code it can't optimise.

---

## 16. Context API, and when not to use it

**Weight:** ★★★

**What it does:** passes a value to any component below a provider without threading it
through props.

```jsx
const AuthContext = createContext(null);

function App() {
  const [user, setUser] = useState(null);
  // React 19 syntax; earlier versions: <AuthContext.Provider value={…}>
  return (
    <AuthContext value={{ user, setUser }}>
      <Layout />
    </AuthContext>
  );
}

function Avatar() {
  const { user } = useContext(AuthContext);
  return <img src={user?.photo} alt="" />;
}
```

**Good for:** data needed widely that changes rarely — current user, theme, language,
feature flags.

**Not ideal for:**

- **Frequently changing data** — every consumer re-renders when the value changes.
- **Server data** — use TanStack Query (caching, refetching).
- **Large global state with many updates** — a store such as Zustand or Redux lets
  components subscribe to just the slice they need.

**If you do use it:** split contexts by concern, and memoize the value object so it isn't
a new object every render.

---

## 17. Prop drilling and how to avoid it

**Weight:** ★★

**Prop drilling** is passing props through several components that don't use them, just
to reach a deep child. Annoying, and every layer re-renders.

**Options, simplest first:**

1. **Composition** — pass components as `children` so the middle layers don't need the
   data at all.
2. **Context** — for widely needed, rarely changing data.
3. **A state library** (Zustand, Redux Toolkit) — for app-wide state with frequent
   updates.

---

## 18. Lifting state up

**Weight:** ★★

When two sibling components need the same data, **move the state to their closest common
parent**, pass the value down as props, and pass a callback down so children can request
changes. There is then one source of truth.

Example: a product list and a cart summary both need the cart → the cart state lives in
their parent page.

---

## 19. Custom hooks

**Weight:** ★★★

A **custom hook** is a function named `useSomething` that calls other hooks. It extracts
stateful logic so several components can reuse it.

```jsx
function useWindowWidth() {
  const [width, setWidth] = useState(window.innerWidth);
  useEffect(() => {
    const onResize = () => setWidth(window.innerWidth);
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, []);
  return width;
}

function Header() {
  const width = useWindowWidth();
  return width < 600 ? <MobileNav /> : <DesktopNav />;
}
```

**Key point:** hooks share **logic**, not **state** — each component calling
`useWindowWidth()` gets its own state.

**Common examples:** `useFetch`, `useDebounce`, `useLocalStorage`, `useAuth`,
`useMediaQuery`.

---

## 20. Rules of hooks

**Weight:** ★★★

1. **Call hooks only at the top level** — not inside conditions, loops, nested functions,
   or after an early `return`.
2. **Call hooks only from function components or custom hooks.**

**Why:** React stores hook state in a list per component and matches each call **by
order**. If one render skips a hook (inside an `if`), every later hook reads the wrong
slot.

```jsx
if (isLoggedIn) {
  const [cart, setCart] = useState([]);   // ❌ conditional hook
}
const [cart, setCart] = useState([]);     // ✅ always called; use the condition below
```

`eslint-plugin-react-hooks` enforces both rules. (React 19's `use()` is the exception — it
may be called conditionally.)

---

## 21. Class lifecycle methods vs hooks

**Weight:** ★★

| Class component | Function component |
| --- | --- |
| `constructor` / `this.state` | `useState` |
| `componentDidMount` | `useEffect(() => { … }, [])` |
| `componentDidUpdate` | `useEffect(() => { … }, [deps])` |
| `componentWillUnmount` | The cleanup function returned from `useEffect` |
| `shouldComponentUpdate` / `PureComponent` | `memo` |
| `getDerivedStateFromError` / `componentDidCatch` | No hook — still a class error boundary (Q22) |

Hooks group code **by concern** (all subscription code together) instead of splitting it
across lifecycle methods.

---

## 22. Error boundaries

**Weight:** ★★

An **error boundary** catches errors thrown **while rendering** its child components and
shows a fallback UI instead of a blank page.

```jsx
class ErrorBoundary extends React.Component {
  state = { hasError: false };
  static getDerivedStateFromError() { return { hasError: true }; }
  componentDidCatch(error, info) { logToService(error, info); }
  render() {
    return this.state.hasError ? <p>Something went wrong.</p> : this.props.children;
  }
}

<ErrorBoundary><Dashboard /></ErrorBoundary>
```

- Still written as a **class** — or use the `react-error-boundary` package.
- It does **not** catch errors in event handlers, async code (`setTimeout`, promises)
  or the boundary itself — handle those with `try`/`catch`.
- Place boundaries around independent sections so one broken widget doesn't take down
  the page.

---

## 23. Code splitting with lazy and Suspense

**Weight:** ★★

Load a component's code only when it is first needed, so the initial bundle is smaller:

```jsx
import { lazy, Suspense } from "react";

const Reports = lazy(() => import("./Reports"));    // separate JS file

function App() {
  return (
    <Suspense fallback={<p>Loading reports…</p>}>
      <Reports />
    </Suspense>
  );
}
```

**Typical split points:** routes (each page loads its own code), heavy components
(charts, editors) and rarely used dialogs. `Suspense` also shows fallbacks while data
loads in frameworks and with `use()`.

---

## 24. useTransition and useDeferredValue

**Weight:** ★★

Concurrent rendering (React 18+) lets React interrupt non-urgent rendering to keep the UI
responsive.

- **`useTransition`** — mark a **state update** as non-urgent:

```jsx
const [isPending, startTransition] = useTransition();

function onFilterChange(value) {
  setInput(value);                               // urgent: the input stays responsive
  startTransition(() => setFilter(value));       // non-urgent: re-filtering a big list
}
```

- **`useDeferredValue`** — get a **lagging copy of a value**, useful when you don't control
  the update: `const deferredQuery = useDeferredValue(query);` and render the expensive
  list from `deferredQuery`.

---

## 25. Why do my effects run twice?

**Weight:** ★★★

In **development** with `<StrictMode>` (on by default in new projects), React
deliberately:

- renders components **twice**, to expose impure rendering, and
- runs effects **mount → cleanup → mount**, to expose missing cleanup.

**Production runs everything once.** If the double run causes a bug (two subscriptions,
two analytics events), the effect is missing a cleanup or isn't idempotent — fix that
rather than removing StrictMode.

---

## 26. What's new in React 19?

**Weight:** ★★★

| Feature | What it does |
| --- | --- |
| **Actions** | Async functions passed to `<form action={…}>` or run in a transition; React handles pending state, errors and form reset |
| `useActionState` | Returns `[state, formAction, isPending]` for a form action |
| `useFormStatus` | A child (e.g. a submit button) reads the parent form's pending state |
| `useOptimistic` | Show the expected result immediately; revert if the action fails |
| `use(promise / context)` | Read a promise (suspends until ready) or context — even conditionally |
| `ref` as a prop | Function components receive `ref` directly — `forwardRef` not needed |
| `<Context>` as provider | `<ThemeContext value={…}>` |
| Document metadata | `<title>`, `<meta>`, `<link>` rendered anywhere are moved to `<head>` |
| Server Components and Server Functions | Stable, used through frameworks (Q27) |
| `<Activity>` (19.2) | Hide part of the UI while preserving its state; pre-render hidden UI |
| `useEffectEvent` (19.2) | Read the latest props/state from an effect without making them dependencies |

```jsx
const [state, formAction, isPending] = useActionState(saveProfile, { error: null });

<form action={formAction}>
  <input name="name" />
  <button disabled={isPending}>Save</button>
  {state.error && <p>{state.error}</p>}
</form>
```

---

## 27. Server Components vs Client Components

**Weight:** ★★

| | Server Components | Client Components |
| --- | --- | --- |
| Run | On the server only (at request or build time) | On the server for the first HTML, then in the browser |
| Can use | `async`/`await`, direct database or file access, secrets | State, effects, event handlers, browser APIs |
| JavaScript sent to the browser | **None** for the component itself | The component's code |
| Marked by | Default in frameworks that support them (e.g. Next.js App Router) | `"use client"` at the top of the file |

**Why they exist:** less JavaScript in the browser and data fetching next to the data,
while interactive parts stay Client Components. They need a framework (Next.js, React
Router's framework mode) — a plain Vite app is client-only.

---

## 28. CSR vs SSR vs SSG

**Weight:** ★★★

| | Client-side rendering (CSR) | Server-side rendering (SSR) | Static site generation (SSG) |
| --- | --- | --- | --- |
| HTML produced | In the browser, after JS loads | On the server, per request | At build time |
| First content visible | Slowest (blank until JS runs) | Fast | Fastest (served from a CDN) |
| SEO | Weaker | Good | Good |
| Data freshness | Live | Live | As of the last build (or periodic regeneration) |
| Server cost | Static hosting only | A server for every request | Static hosting |
| Example | Vite + React dashboard behind a login | Product pages with live prices | Blog, docs, marketing pages |

**Hydration:** with SSR/SSG, the browser receives ready HTML, then React "hydrates" it —
attaches event handlers so it becomes interactive.

**Choose by page:** frameworks such as Next.js mix these per route.

---

## 29. Choosing state management

**Weight:** ★★★

**First classify the state:**

| Kind of state | Example | Best tool |
| --- | --- | --- |
| Local UI state | Is the dropdown open? Input text | `useState`, `useReducer` |
| Shared by a few components | Selected filters on a page | Lift state up |
| App-wide, rarely changing | Current user, theme | Context |
| App-wide client state, frequent updates | Cart, editor state | **Zustand** or **Redux Toolkit** |
| **Server data** | Products, orders from the API | **TanStack Query** (or the framework's loaders) |
| URL state | Current page, search filters | The router (query parameters) |

**The senior insight:** most "global state" is really **cached server data**. Move it to
TanStack Query and very little global client state is left — often context or a small
Zustand store is enough.

```js
import { create } from "zustand";

export const useCart = create(set => ({
  items: [],
  add: item => set(state => ({ items: [...state.items, item] })),
  clear: () => set({ items: [] }),
}));

// in a component:
const items = useCart(state => state.items);   // re-renders only when items change
```

---

## 30. Redux and Redux Toolkit basics

**Weight:** ★★

- **Store** — one object holding app state.
- **Action** — an object describing what happened: `{ type: "cart/added", payload }`.
- **Reducer** — a pure function `(state, action) => newState`.
- **Dispatch** — send an action; **selectors** read parts of the state.
- **Redux Toolkit (RTK)** is the standard way to write Redux today — far less
  boilerplate, and Immer lets reducers "mutate" safely.

```js
import { configureStore, createSlice } from "@reduxjs/toolkit";

const cartSlice = createSlice({
  name: "cart",
  initialState: { items: [] },
  reducers: {
    added(state, action) { state.items.push(action.payload); },   // Immer makes this safe
    cleared(state) { state.items = []; },
  },
});

export const { added, cleared } = cartSlice.actions;
export const store = configureStore({ reducer: { cart: cartSlice.reducer } });

// component: const items = useSelector(s => s.cart.items);  dispatch(added(product));
```

**When Redux fits:** large apps with complex shared client state, many developers, and a
need for strict patterns and dev tools (time-travel debugging). **RTK Query** covers
server data inside Redux.

---

## 31. Optimising React performance

**Weight:** ★★★

**Measure first** with React DevTools → Profiler ("what rendered and why").

| Problem | Fixes |
| --- | --- |
| Too many re-renders | Move state down closer to where it's used; split components and contexts; `memo` + stable props; the React Compiler |
| Expensive calculations | `useMemo`, or move them out of render |
| Large bundle / slow first load | Code splitting (`lazy`, per-route), remove heavy dependencies, tree-shaking |
| Long lists | Virtualisation (Q32) |
| Slow typing in inputs | `useTransition` / `useDeferredValue`; debounce expensive work |
| Too much data fetching | Cache with TanStack Query; avoid request waterfalls (fetch in parallel) |
| Images | Lazy-load, correct sizes, modern formats |
| Slow first paint | SSR/SSG via a framework |

---

## 32. Rendering very long lists

**Weight:** ★★

Rendering 10,000 rows creates 10,000 DOM nodes — slow to render, scroll and update.

**Virtualisation (windowing):** render only the rows currently visible (plus a few
extra), and swap them as the user scrolls. Libraries: **TanStack Virtual**,
**react-window**. Alternatives: pagination or "load more".

---

## 33. Portals

**Weight:** ★

`createPortal(children, domNode)` renders children into a **different DOM node** (e.g.
`document.body`) while keeping them in the same React tree — props, context and event
bubbling still follow the React tree.

**Use for:** modals, tooltips, dropdowns and toasts that must escape a parent's
`overflow: hidden` or `z-index`.

---

## 34. Passing refs to components

**Weight:** ★★

- **React 19:** `ref` is a regular prop for function components:

```jsx
function TextInput({ ref, ...props }) {
  return <input ref={ref} {...props} />;
}

const inputRef = useRef(null);
<TextInput ref={inputRef} placeholder="Name" />;
```

- **Before React 19:** wrap the component in `forwardRef((props, ref) => …)`.
  `forwardRef` still works and is common in existing code.
- `useImperativeHandle` exposes a limited set of methods (e.g. only `focus()`) instead of
  the whole DOM node.

---

## 35. HOCs, render props and hooks

**Weight:** ★★

Three ways to share logic between components:

| Pattern | Example | Drawback |
| --- | --- | --- |
| Higher-order component (HOC) | `withAuth(Component)` returns a wrapped component | "Wrapper hell"; props can collide |
| Render props | `<Mouse render={pos => <Cursor pos={pos} />} />` | Nesting gets deep |
| **Custom hooks** | `const pos = useMousePosition();` | — the modern default |

HOCs still appear in older code and some libraries; new code uses hooks.

---

## 36. Synthetic events

**Weight:** ★

React passes handlers a **SyntheticEvent** — a wrapper around the browser event with the
same interface (`preventDefault`, `stopPropagation`, `target`) that behaves consistently
across browsers. Since React 17, React attaches its listeners to the app's root container
rather than `document`. The native event is available as `event.nativeEvent`.

---

## 37. Security in React

**Weight:** ★★

- **React escapes values in JSX** — `{userInput}` is rendered as text, so injected
  `<script>` tags don't run.
- **`dangerouslySetInnerHTML`** bypasses that. Only use it with sanitised HTML (e.g.
  DOMPurify).
- **URLs:** user-provided `href` values like `javascript:…` can run code — validate URLs.
- **Tokens:** don't keep auth tokens in `localStorage` (readable by any XSS); prefer
  `HttpOnly` cookies ([17 Q29](17_JavaScript_QA.md#29-localstorage-vs-sessionstorage-vs-cookies)).
- **Secrets:** anything in front-end code or build-time environment variables is
  public — keep API keys on the server.
- Keep dependencies updated (`npm audit`) — supply-chain attacks target front-end
  packages.

---

## 38. Testing React components

**Weight:** ★★

| Tool | Role |
| --- | --- |
| **Vitest** or Jest | Test runner and assertions |
| **React Testing Library** | Render components and query them the way users do (by role, label, text) |
| `@testing-library/user-event` | Realistic typing and clicking |
| MSW (Mock Service Worker) | Mock API calls at the network level |
| Playwright / Cypress | End-to-end tests in a real browser |

```jsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

test("adds a todo", async () => {
  render(<TodoApp />);
  await userEvent.type(screen.getByPlaceholderText("What needs doing?"), "Buy milk");
  await userEvent.click(screen.getByRole("button", { name: "Add" }));
  expect(screen.getByText("Buy milk")).toBeInTheDocument();
});
```

**Principle:** test **behaviour users see**, not implementation details (state variable
names, internal methods) — tests then survive refactoring.

---

## 39. Forms in React

**Weight:** ★

- **Small forms:** controlled inputs with `useState` (Q7).
- **Large forms:** **React Hook Form** (fewer re-renders, built-in validation), often with
  **Zod** schemas for validation shared with the back end.
- **React 19:** `<form action={fn}>` with `useActionState` and `useFormStatus` handles
  pending and error states with little code.

---

## 40. Scenario: a component re-renders too often

**Weight:** ★★★ — "The page is slow when typing. How do you investigate?"

1. **Measure:** React DevTools → Profiler; record while typing; turn on "Highlight updates
   when components render".
2. **Find the cause** — usually one of:

| Cause | Fix |
| --- | --- |
| State too high up — typing re-renders the whole page | Move the input's state into a smaller component |
| A context value that changes often, consumed widely | Split the context; memoize the value |
| New object or function props every render defeat `memo` | `useMemo` / `useCallback`, or the React Compiler |
| An expensive calculation on every keystroke | `useMemo`, `useDeferredValue`, or debounce |
| A huge list re-rendering | Virtualise; memoize rows |

3. **Verify** with the Profiler again — fewer and shorter commits.

---

## 41. Scenario: stale state in an interval

**Weight:** ★★★

```jsx
function Timer() {
  const [seconds, setSeconds] = useState(0);

  useEffect(() => {
    const id = setInterval(() => {
      setSeconds(seconds + 1);        // ❌ always 0 + 1 — the timer sticks at 1
    }, 1000);
    return () => clearInterval(id);
  }, []);

  return <p>{seconds}s</p>;
}
```

**Why:** the effect ran once, so its callback **closed over** `seconds` from the first
render (0) forever — a **stale closure**.

**Fix:** use the updater function, which always receives the latest value:

```jsx
setSeconds(s => s + 1);
```

(Adding `seconds` to the dependency array also works, but recreates the interval every
second.) For reading the latest props or state inside an effect without re-running it,
React 19.2 adds `useEffectEvent`.

---

## 42. Predict what happens: quick round

**Weight:** ★★★

| Code | What happens | Why |
| --- | --- | --- |
| `setCount(count + 1)` three times in one handler | Count goes up by **1** | All three read the same `count`; updates are batched |
| `setCount(c => c + 1)` three times | Count goes up by **3** | Each updater gets the latest value |
| `console.log(count)` right after `setCount(5)` | Logs the **old** value | The new value arrives on the next render |
| `{items.length && <List />}` with an empty array | Renders **`0`** | `0 && …` evaluates to `0`, and React renders numbers |
| A parent re-renders; child props unchanged | Child **re-renders anyway** | Unless the child is wrapped in `memo` (or the Compiler is on) |
| `<Profile key={userId} />` and `userId` changes | Profile **remounts** — its state resets | A new key means a new component instance |
| `useEffect(() => { … })` with no dependency array | Runs after **every** render | No array = no filter |
| `useEffect(() => { setX(x + 1); }, [x])` | **Infinite loop** | The effect changes its own dependency |
| `items.push(4); setItems(items);` | Screen may **not** update | Same array reference |
| `onClick={handleClick()}` | Runs **during render**, not on click | It's called, not passed |
