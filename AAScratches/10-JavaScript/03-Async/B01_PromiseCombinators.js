/*
 * =====================================================================
 *  allSettled, race and any from Scratch       JavaScript | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Write three of Promise's combinators without using them (Promise.resolve is allowed):
 *     allSettled(items)  waits for all; resolves to [{status, value | reason}] in input order
 *     race(items)        settles like the first item to settle, fulfilled or rejected
 *     any(items)         resolves with the first fulfilled value; if every item rejects,
 *                        rejects with an AggregateError holding all reasons in input order
 *   Items may be plain values as well as promises.
 *
 * EXAMPLE
 *   allSettled([later("a", 20), fail("x", 10)])  ->  [{fulfilled, "a"}, {rejected, "x"}]
 *   race([later("slow", 30), fail("fast", 10)])  ->  rejects "fast"
 *   any([fail("x", 10), later("ok", 30)])        ->  "ok"   (a rejection does not end it)
 *   any([])                                      ->  rejects AggregateError, no reasons
 *
 * APPROACH  (one new Promise, a handler per item)
 *   allSettled: like Promise.all, store each outcome at its own index, count down; it
 *     never rejects, so both handlers store an outcome.
 *   race: hand every item resolve and reject; the first call wins, later calls do nothing.
 *   any: resolve on the first value; store each reason at its index and count down; when
 *     the count hits 0, reject with an AggregateError.
 *
 * KEY INSIGHT
 *   A promise settles once: every later resolve or reject call is ignored. That is why race
 *   needs no "done" flag, and why the three differ only in which outcomes they count.
 *
 * COMPLEXITY
 *   Time  O(n) handlers
 *   Space O(n) results or reasons
 *
 * INTERVIEW FOLLOW-UPS
 *   - race([]) never settles (by the spec); any([]) rejects at once.
 *   - A timeout with race: race([work, rejectAfter(ms)]), and why the work keeps running.
 *   - Promise.all vs allSettled for a dashboard that shows whatever loaded.
 *   - Why wrap each item in Promise.resolve: plain values and thenables both work.
 *
 * RUN
 *   main() prints actual vs expected.
 */
function allSettled(items) {
  return new Promise((resolve) => {
    const results = new Array(items.length);
    let left = items.length;
    if (left === 0) return resolve(results);
    items.forEach((item, i) => {
      const settle = (outcome) => {
        results[i] = outcome;
        left -= 1;
        if (left === 0) resolve(results);
      };
      Promise.resolve(item).then(
        (value) => settle({ status: 'fulfilled', value }),
        (reason) => settle({ status: 'rejected', reason }));
    });
  });
}

function race(items) {
  return new Promise((resolve, reject) => {
    for (const item of items) Promise.resolve(item).then(resolve, reject);
  });
}

function any(items) {
  return new Promise((resolve, reject) => {
    const reasons = new Array(items.length);
    let left = items.length;
    if (left === 0) return reject(new AggregateError([], 'All promises were rejected'));
    items.forEach((item, i) => {
      Promise.resolve(item).then(resolve, (reason) => {
        reasons[i] = reason;
        left -= 1;
        if (left === 0) reject(new AggregateError(reasons, 'All promises were rejected'));
      });
    });
  });
}

const later = (value, ms) => new Promise((resolve) => setTimeout(() => resolve(value), ms));
const fail = (reason, ms) => new Promise((_, reject) => setTimeout(() => reject(reason), ms));
const why = (e) => (e instanceof AggregateError
  ? `all failed ${JSON.stringify(e.errors)}`
  : `rejected ${e}`);
const outcome = (p) => p.then((v) => `value ${v}`, why);

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

async function main() {
  const settled = await allSettled([later('a', 20), fail('x', 10), 'c']);
  check('case 1 allSettled  ', settled, [{ status: 'fulfilled', value: 'a' },
    { status: 'rejected', reason: 'x' }, { status: 'fulfilled', value: 'c' }]);
  const raced = await outcome(race([later('slow', 80), fail('fast', 10)]));
  check('case 2 race, reject', raced, 'rejected fast');
  const quick = await outcome(race([later('slow', 80), later('quick', 5)]));
  check('case 3 race, value ', quick, 'value quick');
  check('case 4 any         ', await outcome(any([fail('x', 5), later('ok', 30)])), 'value ok');
  const none = await outcome(any([fail('x', 20), fail('y', 5)]));
  check('case 5 any, none   ', none, 'all failed ["x","y"]');
  check('case 6 any, empty  ', await outcome(any([])), 'all failed []');
}

main();
