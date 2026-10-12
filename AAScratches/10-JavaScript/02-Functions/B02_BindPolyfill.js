/*
 * =====================================================================
 *  call, apply and bind from Scratch           JavaScript | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Without using call, apply or bind, write:
 *     callWith(fn, thisArg, ...args)   runs fn with `this` = thisArg
 *     applyWith(fn, thisArg, args)     the same, arguments as an array
 *     bindTo(fn, thisArg, ...preset)   returns a new function that always runs fn with
 *                                      `this` = thisArg and the preset arguments first;
 *                                      called with `new`, it ignores thisArg, as bind does.
 *   thisArg must look untouched afterwards.
 *
 * EXAMPLE
 *   introduce = function (greeting) { return `${greeting}, I am ${this.name}`; }
 *   callWith(introduce, { name: "Ravi" }, "Hi")        ->  "Hi, I am Ravi"
 *   bindTo(introduce, { name: "Ravi" }, "Hey")()       ->  "Hey, I am Ravi"
 *   new (bindTo(Point, ignored, 1))(2)                 ->  a Point { x: 1, y: 2 }
 *
 * APPROACH  (`this` is whatever is left of the dot)
 *   1. callWith: put fn on thisArg under a fresh Symbol key, call thisArg[key](...args),
 *      then delete the key (in finally, so a throw cleans up too).
 *   2. applyWith: callWith with the array spread.
 *   3. bindTo: return a function that, called normally, does callWith(fn, thisArg,
 *      ...preset, ...more); called with new (new.target is set), does new fn(...).
 *
 * KEY INSIGHT
 *   To choose `this`, make fn a method of thisArg for one call, because in obj.method()
 *   JavaScript sets `this` to obj. A Symbol key cannot clash with a real property, and
 *   deleting it leaves the object as it was.
 *
 * COMPLEXITY
 *   Time  O(n) for n arguments
 *   Space O(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Put them on Function.prototype (myCall, myBind) so they read fn.myBind(obj).
 *   - Binding twice: the first this wins, because the outer bound function never looks at
 *     its own `this`.
 *   - Arrow functions ignore all three: they have no `this` of their own.
 *   - thisArg null or a number: sloppy code gets globalThis or a wrapper object; strict code
 *     gets the value as is.
 *
 * RUN
 *   main() prints actual vs expected.
 */
function callWith(fn, thisArg, ...args) {
  const target = thisArg == null ? globalThis : Object(thisArg);
  const key = Symbol('fn');
  target[key] = fn;
  try {
    return target[key](...args);
  } finally {
    delete target[key];
  }
}

function applyWith(fn, thisArg, args = []) {
  return callWith(fn, thisArg, ...args);
}

function bindTo(fn, thisArg, ...preset) {
  return function bound(...more) {
    if (new.target) return new fn(...preset, ...more);
    return callWith(fn, thisArg, ...preset, ...more);
  };
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  const introduce = function (greeting, end = '') {
    return `${greeting}, I am ${this.name}${end}`;
  };
  const ravi = { name: 'Ravi' };
  check('case 1 call          ', callWith(introduce, ravi, 'Hi'), 'Hi, I am Ravi');
  check('case 2 apply         ', applyWith(introduce, ravi, ['Hello', '!']), 'Hello, I am Ravi!');
  const hey = bindTo(introduce, ravi, 'Hey');
  check('case 3 bind, later   ', [hey(), hey('.')], ['Hey, I am Ravi', 'Hey, I am Ravi.']);
  const symbols = Object.getOwnPropertySymbols(ravi).length;
  check('case 4 object intact ', [Object.keys(ravi), symbols], [['name'], 0]);
  const twice = bindTo(bindTo(introduce, ravi), { name: 'Other' });
  check('case 5 first bind won', twice('Yo'), 'Yo, I am Ravi');
  function Point(x, y) { this.x = x; this.y = y; }
  const AtX1 = bindTo(Point, { ignored: true }, 1);
  const p = new AtX1(2);
  check('case 6 new ignores it', [p.x, p.y, p instanceof Point], [1, 2, true]);
}

main();
