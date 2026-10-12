/*
 * =====================================================================
 *  Curry                                       JavaScript | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Write curry(fn): it returns a function that collects arguments over any number of
 *   calls, one or several at a time, and calls fn as soon as it has fn.length of them.
 *   Each partial call returns a new function; earlier partials can be reused.
 *
 * EXAMPLE
 *   add3 = curry((a, b, c) => a + b + c)
 *   add3(1)(2)(3), add3(1, 2)(3), add3(1)(2, 3), add3(1, 2, 3)   ->  6 each time
 *   const add1 = add3(1); add1(2)(3) and add1(10)(20)            ->  6 and 31 (add1 reused)
 *
 * APPROACH  (a closure over the arguments so far)
 *   1. curried(...args): if args.length >= fn.length, return fn(...args).
 *   2. Otherwise return a new function that takes more arguments and calls curried with
 *      args concatenated with them.
 *   3. Never push into a shared array: each partial keeps its own copy.
 *
 * KEY INSIGHT
 *   fn.length is how many parameters fn declares, so curry knows when to stop collecting.
 *   Build a new array on every step ([...args, ...more]); pushing into one shared array
 *   makes add1(2)(3) and add1(10)(20) see each other's arguments.
 *
 * COMPLEXITY
 *   Time  O(k^2) for k arguments one at a time (each step copies the array); k is tiny
 *   Space O(k)
 *
 * INTERVIEW FOLLOW-UPS
 *   - fn.length is 0 for (...args) => and stops at the first default value: pass arity.
 *   - Placeholders (lodash's _): curry(f)(_, 2)(1) fills the gap later.
 *   - An infinite sum: sum(1)(2)(3)() with an empty call to finish, or valueOf.
 *   - Curry vs partial application vs bind(null, 1).
 *
 * RUN
 *   main() prints actual vs expected.
 */
function curry(fn, arity = fn.length) {
  return function curried(...args) {
    if (args.length >= arity) return fn.apply(this, args);
    return (...more) => curried.apply(this, [...args, ...more]);
  };
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  const add3 = curry((a, b, c) => a + b + c);
  const splits = [add3(1)(2)(3), add3(1, 2)(3), add3(1)(2, 3), add3(1, 2, 3)];
  check('case 1 every split   ', splits, [6, 6, 6, 6]);
  const add1 = add3(1);
  check('case 2 reuse partial ', [add1(2)(3), add1(10)(20)], [6, 31]);
  const join = curry((...parts) => parts.join('-'), 3);
  check('case 3 rest params   ', join('a')('b')('c'), 'a-b-c');
  const greet = curry((greeting, name) => `${greeting}, ${name}`);
  check('case 4 strings       ', greet('Hi')('Ravi'), 'Hi, Ravi');
  check('case 5 extra args ok ', add3(1, 2, 3, 4), 6);
}

main();
