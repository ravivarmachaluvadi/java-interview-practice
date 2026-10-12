/*
 * =====================================================================
 *  Throttle                                    JavaScript | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Write throttle(fn, wait): it returns a function that calls fn at most once every
 *   `wait` ms. The first call goes through at once; calls inside the quiet window are
 *   dropped. A scroll or resize handler uses it so it runs a few times a second, not
 *   hundreds of times.
 *
 * EXAMPLE
 *   wait = 50: calls at 0, 10, 20 ms                ->  fn runs once, at 0 ms
 *   wait = 50: calls at 0 ms and 70 ms              ->  fn runs twice
 *   debounce would wait for the calls to stop; throttle answers the first one at once
 *
 * APPROACH  (remember when it last ran)
 *   1. Keep the time of the last call that went through in a closure (start: never).
 *   2. On each call, if `wait` ms have passed since then, call fn and record the time.
 *   3. Otherwise drop the call.
 *
 * KEY INSIGHT
 *   Throttle compares against the last time it fired, so a steady stream of calls fires
 *   every `wait` ms; debounce restarts a timer on every call, so the same stream never fires.
 *
 * COMPLEXITY
 *   Time  O(1) per call
 *   Space O(1)  one timestamp
 *
 * INTERVIEW FOLLOW-UPS
 *   - A trailing option: also run once at the end of the window with the last arguments,
 *     so the final scroll position is never lost.
 *   - Why Date.now() and not a setTimeout flag: no timer to clean up, and a clock passed in
 *     makes it testable.
 *   - requestAnimationFrame as a throttle for drawing: once per frame.
 *   - Throttle vs debounce: search box -> debounce; scroll position -> throttle.
 *
 * RUN
 *   main() drives throttle with a fake clock and prints actual vs expected.
 */
function throttle(fn, wait, now = Date.now) {
  let last = -Infinity;
  return function (...args) {
    const t = now();
    if (t - last < wait) return;
    last = t;
    fn.apply(this, args);
  };
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  let clock = 0;
  const calls = [];
  const onScroll = throttle((y) => calls.push(y), 50, () => clock);
  onScroll(0);
  clock = 10; onScroll(10);
  clock = 20; onScroll(20);
  check('case 1 a burst          ', calls, [0]);
  clock = 50; onScroll(50);                       // exactly `wait` later: allowed
  check('case 2 after the window ', calls, [0, 50]);
  clock = 99; onScroll(99);
  clock = 120; onScroll(120);
  check('case 3 a steady stream  ', calls, [0, 50, 120]);
  const box = { name: 'box', calls: [] };
  box.log = throttle(function () { this.calls.push(this.name); }, 50, () => clock);
  box.log();
  check('case 4 keeps `this`     ', box.calls, ['box']);
}

main();
