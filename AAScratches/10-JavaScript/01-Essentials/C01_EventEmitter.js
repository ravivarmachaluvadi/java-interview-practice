/*
 * =====================================================================
 *  Event Emitter                               JavaScript | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Write an EventEmitter class: on(name, fn) adds a listener and returns a function that
 *   removes it; off(name, fn) removes one; once(name, fn) runs at most once; emit(name,
 *   ...args) calls the listeners in the order they were added and returns whether any ran.
 *
 * EXAMPLE
 *   on("save", a); once("save", b); emit("save", "x") -> a("x"), b("x"); emit again -> a only
 *   const stop = on("save", a); stop(); emit("save")  -> false, nothing runs
 *
 * APPROACH  (a Map from event name to an array of listeners)
 *   1. on: append fn to the name's array (make it first); return () => off(name, fn).
 *   2. off: find fn in the array and splice it out.
 *   3. once: register a wrapper that first removes itself, then calls fn.
 *   4. emit: call every listener of a COPY of the array; return whether it had any.
 *
 * KEY INSIGHT
 *   emit loops over a copy: a once-listener removes itself while the loop runs, and
 *   splicing the array being walked would make the loop skip the listener after it.
 *
 * COMPLEXITY
 *   Time  on O(1), off O(listeners of that name), emit O(listeners of that name)
 *   Space O(all listeners)
 *
 * INTERVIEW FOLLOW-UPS
 *   - off(name, fn) for a fn added with once: keep the original on the wrapper to find it.
 *   - A listener that throws: catch, keep calling the others, report the error afterwards.
 *   - A wildcard "*" listener, or async emit that awaits each listener in turn.
 *
 * RUN
 *   main() runs 3 cases and prints actual vs expected.
 */
class EventEmitter {
  constructor() {
    this.listeners = new Map();
  }

  on(name, fn) {
    if (!this.listeners.has(name)) this.listeners.set(name, []);
    this.listeners.get(name).push(fn);
    return () => this.off(name, fn);
  }

  off(name, fn) {
    const list = this.listeners.get(name) || [];
    const i = list.indexOf(fn);
    if (i >= 0) list.splice(i, 1);
  }

  once(name, fn) {
    const wrapper = (...args) => {
      this.off(name, wrapper);
      fn(...args);
    };
    return this.on(name, wrapper);
  }

  emit(name, ...args) {
    const list = [...(this.listeners.get(name) || [])];
    list.forEach((fn) => fn(...args));
    return list.length > 0;
  }
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  const bus = new EventEmitter();
  const seen = [];
  const stop = bus.on('save', (file) => seen.push(`saved ${file}`));
  bus.once('save', (file) => seen.push(`first ${file}`));
  bus.emit('save', 'a.txt');
  bus.emit('save', 'b.txt');
  check('case 1 on and once', seen, ['saved a.txt', 'first a.txt', 'saved b.txt']);
  stop();
  check('case 2 after stop ', bus.emit('save', 'c.txt'), false);
  check('case 3 no listener', bus.emit('other'), false);
}

main();
