/*
 * =====================================================================
 *  Event Loop: Predict the Order               JavaScript | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Three short programs below (basic, asyncAwait, timers) log words with setTimeout,
 *   promises, queueMicrotask and await. Without running them, write predict(): for each
 *   program, the words in the order they are logged. main() runs the programs for real and
 *   compares.
 *
 * EXAMPLE
 *   console.log("A");
 *   setTimeout(() => console.log("B"));
 *   Promise.resolve().then(() => console.log("C"));
 *   ->  A, C, B     the promise callback runs before the timer, though both wait "0 ms"
 *
 * APPROACH  (three places a callback can wait)
 *   1. Run all the synchronous code first, top to bottom. A Promise executor
 *      (new Promise(executor)) and an async function up to its first await are synchronous.
 *   2. Then empty the microtask queue: .then callbacks, queueMicrotask, and the code after
 *      an await. A microtask that queues another microtask still runs before any timer.
 *   3. Then take ONE task (a timer), run it, and empty the microtask queue again. Repeat.
 *
 * KEY INSIGHT
 *   Microtasks are drained completely after every task, so "a timer of 0 ms" always loses
 *   to every promise callback already queued, and a then-chain queues its next step only
 *   when the previous step has run.
 *
 * COMPLEXITY
 *   Not an algorithm: what is asked is the order.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Node's process.nextTick runs before promise microtasks; setImmediate after I/O.
 *   - Why an endless chain of microtasks freezes the page, but an endless chain of
 *     setTimeout does not.
 *   - await on a non-promise still waits one microtask.
 *   - requestAnimationFrame: before the next paint, after the tasks.
 *
 * RUN
 *   main() runs each program and prints the real order next to your prediction.
 */
function predict() {
  return {
    basic: ['sync start', 'sync end', 'then 1', 'microtask', 'then 2', 'timeout'],
    asyncAwait: ['script start', 'a start', 'executor', 'script end',
      'a after await', 'then', 'timeout'],
    timers: ['t1', 't1 micro', 't2', 't2 micro'],
  };
}

// demo only: program 1
function basic(log) {
  log('sync start');
  setTimeout(() => log('timeout'), 0);
  Promise.resolve().then(() => log('then 1')).then(() => log('then 2'));
  queueMicrotask(() => log('microtask'));
  log('sync end');
}

// demo only: program 2
function asyncAwait(log) {
  async function a() {
    log('a start');
    await null;
    log('a after await');
  }
  log('script start');
  setTimeout(() => log('timeout'), 0);
  a();
  new Promise((resolve) => { log('executor'); resolve(); }).then(() => log('then'));
  log('script end');
}

// demo only: program 3
function timers(log) {
  setTimeout(() => { log('t1'); Promise.resolve().then(() => log('t1 micro')); }, 0);
  setTimeout(() => { log('t2'); queueMicrotask(() => log('t2 micro')); }, 0);
}

// demo only: runs a program and collects what it logs once every timer has fired
async function realOrder(program) {
  const seen = [];
  program((word) => seen.push(word));
  await new Promise((resolve) => setTimeout(resolve, 30));
  return seen;
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

async function main() {
  const guess = predict();
  check('case 1 basic     ', await realOrder(basic), guess.basic);
  check('case 2 asyncAwait', await realOrder(asyncAwait), guess.asyncAwait);
  check('case 3 timers    ', await realOrder(timers), guess.timers);
}

main();
