/*
 * =====================================================================
 *  Memoize                                     JavaScript | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Write memoize(fn, keyOf): it returns a function that gives the same results as fn but
 *   computes each distinct input only once. keyOf turns the arguments into a cache key;
 *   by default the key is the first argument.
 *
 * EXAMPLE
 *   square = memoize(slowSquare); square(4), square(4), square(5)  ->  16, 16, 25; 2 real calls
 *   add = memoize((a, b) => a + b, (a, b) => `${a},${b}`)
 *   add(1, 2), add(2, 1)                                           ->  cached apart: 2 keys
 *
 * APPROACH  (a Map in a closure)
 *   1. Create a Map once, when memoize is called.
 *   2. Each call works out its key; if the Map has it, return the stored value.
 *   3. Otherwise call fn, store the result under the key, return it.
 *
 * KEY INSIGHT
 *   Use a Map, not a plain object: an object turns every key into a string, so the number 1
 *   and the string "1" would share a slot, and a key like "__proto__" breaks it. And test
 *   with has(), not get(): a function may legitimately return undefined or 0.
 *
 * COMPLEXITY
 *   Time  O(1) per cached call (plus keyOf)
 *   Space O(n)  one entry per distinct input, forever
 *
 * INTERVIEW FOLLOW-UPS
 *   - The cache never shrinks: cap it (LRU) or give entries a time to live.
 *   - Object arguments: key by identity with a WeakMap, so the cache does not keep them alive.
 *   - Memoizing a recursive function (fib): the recursion must call the memoized version.
 *   - Async functions: cache the promise, so two calls in flight share one request.
 *
 * RUN
 *   main() counts the real calls and prints actual vs expected.
 */
function memoize(fn, keyOf = (first) => first) {
  const cache = new Map();
  return function (...args) {
    const key = keyOf(...args);
    if (cache.has(key)) return cache.get(key);
    const value = fn.apply(this, args);
    cache.set(key, value);
    return value;
  };
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  let real = 0;
  const square = memoize((n) => { real += 1; return n * n; });
  check('case 1 results       ', [square(4), square(4), square(5)], [16, 16, 25]);
  check('case 2 real calls    ', real, 2);
  const add = memoize((a, b) => a + b, (a, b) => `${a},${b}`);
  check('case 3 own key       ', [add(1, 2), add(2, 1), add(1, 2)], [3, 3, 3]);
  let lookups = 0;
  const find = memoize(() => { lookups += 1; return undefined; });
  find('missing'); find('missing');
  check('case 4 undefined kept', lookups, 1);
  real = 0;
  square(1); square('1');
  check('case 5 1 and "1"     ', real, 2);
}

main();
