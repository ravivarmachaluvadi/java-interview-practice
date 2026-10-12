/*
 * =====================================================================
 *  Run Tasks with a Concurrency Limit          JavaScript | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Write promisePool(tasks, limit): tasks is an array of functions that each return a
 *   promise. Start them in order, never more than `limit` running at once, and start the
 *   next one as soon as any running one finishes. Resolve with the results in input order;
 *   reject with the first error (tasks not yet started are then never started).
 *
 * EXAMPLE
 *   4 tasks of 200, 10, 10, 10 ms, limit 2   ->  at most 2 running at any moment;
 *                                                task c starts at 10 ms, when task b ends;
 *                                                done after ~200 ms, results in input order
 *   limit >= tasks.length                     ->  same as Promise.all over every task
 *
 * APPROACH  (`limit` workers pulling from one shared index)
 *   1. Keep next = 0, a results array and a failed flag.
 *   2. Start `limit` workers. Each one loops while nothing failed and tasks are left: take
 *      i = next++, results[i] = await tasks[i](). On an error, set failed and rethrow.
 *   3. Promise.all(workers), then return results.
 *
 * KEY INSIGHT
 *   Think of `limit` workers, not of tasks: a worker takes the next task the moment it is
 *   free, so exactly `limit` run until the queue is empty. JavaScript runs one line at a
 *   time, so next++ needs no lock: two workers can never take the same index.
 *
 * COMPLEXITY
 *   Time  about the longest worker's total; O(n) bookkeeping
 *   Space O(n) results + O(limit) workers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Upload 100 files, 5 at a time; crawl links with at most 10 requests in flight.
 *   - Keep going after an error and report every outcome (allSettled style).
 *   - A reusable limiter: limit(fn) that queues calls, like the p-limit package.
 *   - Java: an ExecutorService with a fixed thread pool, or a Semaphore(limit).
 *
 * RUN
 *   main() logs each start and finish with fake tasks and prints actual vs expected.
 */
async function promisePool(tasks, limit) {
  const results = new Array(tasks.length);
  let next = 0, failed = false;
  async function worker() {
    while (!failed && next < tasks.length) {
      const i = next++;
      try {
        results[i] = await tasks[i]();
      } catch (error) {
        failed = true;                 // the other workers finish their task, then stop
        throw error;
      }
    }
  }
  const workers = Array.from({ length: Math.min(limit, tasks.length) }, () => worker());
  await Promise.all(workers);
  return results;
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

async function main() {
  let running = 0, most = 0;
  const log = [];
  const task = (name, ms) => async () => {
    running += 1; most = Math.max(most, running); log.push(`start ${name}`);
    await sleep(ms);
    running -= 1; log.push(`end ${name}`);
    return name;
  };
  const tasks = [task('a', 200), task('b', 10), task('c', 10), task('d', 10)];
  const results = await promisePool(tasks, 2);
  check('case 1 input order  ', results, ['a', 'b', 'c', 'd']);
  check('case 2 never over 2 ', most, 2);
  check('case 3 order of work', log,
    ['start a', 'start b', 'end b', 'start c', 'end c', 'start d', 'end d', 'end a']);

  most = 0;
  await promisePool([task('x', 5), task('y', 5)], 10);
  check('case 4 big limit   ', most, 2);
  check('case 5 no tasks    ', await promisePool([], 3), []);

  let started = 0;
  const boom = async () => { started++; throw new Error('boom'); };
  const slow = async () => { started++; await sleep(5); };
  const failing = [boom, slow, slow, slow];
  let error = '';
  try { await promisePool(failing, 2); } catch (e) { error = e.message; }
  await sleep(30);                     // time for a wrongly started task to show up
  check('case 6 an error: no new starts', [error, started], ['boom', 2]);
}

main();
