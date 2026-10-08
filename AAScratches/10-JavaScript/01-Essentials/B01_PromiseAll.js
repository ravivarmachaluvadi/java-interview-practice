/*
 * =====================================================================
 *  Promise.all from Scratch                    JavaScript | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Write promiseAll(items) that behaves like Promise.all: it resolves to an array of the
 *   results in the same order as items (whatever order they finish in), rejects with the
 *   first error, accepts plain values as well as promises, and resolves [] for [].
 *
 * EXAMPLE
 *   [after 30 ms "a", after 10 ms "b", "c"]   ->  ["a", "b", "c"]   input order, not finish order
 *   []                                        ->  []
 *   [after 50 ms "a", rejects in 10 ms]       ->  rejects with that error
 *
 * APPROACH  (one counter, results stored by index)
 *   1. Return a new Promise; make a results array as long as items and a `left` counter.
 *   2. Empty input: resolve [] at once.
 *   3. For each item i: Promise.resolve(item).then(value => results[i] = value, left--).
 *   4. When left reaches 0, resolve(results). The first rejection calls reject; later
 *      settlements are ignored, because a promise settles only once.
 *
 * KEY INSIGHT
 *   Store each value at its own index and count down, rather than pushing as they arrive:
 *   push order is finish order, and the caller asked for input order.
 *
 * COMPLEXITY
 *   Time  O(n) plus the time of the slowest promise
 *   Space O(n)  the results
 *
 * INTERVIEW FOLLOW-UPS
 *   - Promise.allSettled: never reject; store {status, value or reason} for each.
 *   - Promise.race and Promise.any: settle with the first to finish / to succeed.
 *   - Run at most k at a time (a task pool): start k, start the next as each one ends.
 *
 * RUN
 *   main() runs 3 cases with real timers and prints actual vs expected.
 */
function promiseAll(items) {
  return new Promise((resolve, reject) => {
    const results = new Array(items.length);
    let left = items.length;
    if (left === 0) {
      resolve(results);
      return;
    }
    items.forEach((item, i) => {
      Promise.resolve(item).then((value) => {
        results[i] = value;
        left -= 1;
        if (left === 0) resolve(results);
      }, reject);
    });
  });
}

const later = (value, ms) => new Promise((resolve) => setTimeout(() => resolve(value), ms));

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

async function main() {
  const mixed = [later('a', 30), later('b', 10), 'c'];      // b finishes first, c is not a promise
  check('case 1 input order', await promiseAll(mixed), ['a', 'b', 'c']);
  check('case 2 empty      ', await promiseAll([]), []);
  const failing = new Promise((_, reject) => setTimeout(() => reject(new Error('boom')), 10));
  let outcome;
  try {
    await promiseAll([later('a', 50), failing]);
    outcome = 'resolved';
  } catch (error) {
    outcome = `rejected ${error.message}`;
  }
  check('case 3 first error', outcome, 'rejected boom');
}

main();
