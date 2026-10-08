/*
 * =====================================================================
 *  Debounce                                    JavaScript | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Write debounce(fn, wait): it returns a function that, called many times in a row,
 *   calls fn only once, `wait` ms after the last call, with that last call's arguments.
 *   A search box uses it so it asks the server when typing pauses, not on every key.
 *
 * EXAMPLE
 *   save("h"), save("he"), save("hello") within 50 ms   ->  fn("hello") once, 50 ms later
 *   save("a"), 80 ms pause, save("b")                    ->  fn("a") and fn("b")
 *
 * APPROACH  (one timer, restarted on every call)
 *   1. Keep a timer id in a closure.
 *   2. Each call clears the pending timer and starts a new one for `wait` ms.
 *   3. When a timer finally fires, call fn with the arguments and `this` of the last call.
 *
 * KEY INSIGHT
 *   Cancelling the previous timer on every call is the whole trick: only the call that is
 *   not followed by another within `wait` ms survives long enough to fire.
 *
 * COMPLEXITY
 *   Time  O(1) per call
 *   Space O(1)  one timer and the last arguments
 *
 * INTERVIEW FOLLOW-UPS
 *   - Throttle instead: at most one call per `wait` ms, the first one going through at once.
 *   - A leading option: call at the start of a burst, then stay quiet until it ends.
 *   - cancel() and flush() methods on the returned function.
 *   - Why `function (...args)` and not an arrow: an arrow would lose the caller's `this`.
 *
 * RUN
 *   main() runs 3 cases with real timers and prints actual vs expected.
 */
function debounce(fn, wait) {
  let timer = null;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), wait);
  };
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

async function main() {
  const calls = [];
  const save = debounce((text) => calls.push(text), 50);
  save('h');
  save('he');
  save('hello');                       // typed quickly: only the last one counts
  check('case 1 before the pause', calls, []);
  await sleep(80);
  check('case 2 after the pause ', calls, ['hello']);
  save('a');
  await sleep(80);
  save('b');
  await sleep(80);
  check('case 3 spaced-out calls', calls, ['hello', 'a', 'b']);
}

main();
