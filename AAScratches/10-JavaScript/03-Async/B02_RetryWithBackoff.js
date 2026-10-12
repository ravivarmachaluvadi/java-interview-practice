/*
 * =====================================================================
 *  Retry with Exponential Backoff              JavaScript | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Write retry(task, { retries, baseMs, shouldRetry, wait }): call the async task; if it
 *   throws, wait baseMs, then 2 * baseMs, 4 * baseMs ... and try again, at most `retries`
 *   more times. Resolve with the first success. Reject with the last error once retries
 *   run out, or at once when shouldRetry(error) says the error is not worth retrying
 *   (a 400 will not fix itself; a 503 might). wait(ms) defaults to a real timer.
 *
 * EXAMPLE
 *   fails twice, then "ok"; retries 3, baseMs 100    ->  "ok", waited [100, 200]
 *   always fails; retries 2, baseMs 100              ->  rejects the 3rd error, waited [100, 200]
 *   fails with 400; shouldRetry = e => e.status >= 500 ->  rejects at once, waited []
 *
 * APPROACH  (a loop around await)
 *   1. for attempt = 0, 1, ... : try { return await task(attempt); }
 *   2. catch: if attempt === retries or !shouldRetry(error), throw it.
 *   3. otherwise await wait(baseMs * 2 ** attempt) and go round again.
 *
 * KEY INSIGHT
 *   async/await turns retrying into an ordinary loop with try/catch, the way it is written
 *   in Java. Doubling the wait gives a struggling server room to recover instead of
 *   hammering it at a fixed rate.
 *
 * COMPLEXITY
 *   Time  up to retries + 1 calls; waits add up to baseMs * (2^retries - 1)
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Jitter: wait a random part of the delay, so many clients do not retry in lockstep.
 *   - A cap on one wait (maxMs), and a total deadline.
 *   - Honour a Retry-After header when the server sends one.
 *   - Only retry what is safe to repeat: a GET, or a POST with an idempotency key.
 *   - Spring's @Retryable with @Backoff(multiplier = 2) does the same in Java.
 *
 * RUN
 *   main() records the waits instead of sleeping, and prints actual vs expected.
 */
async function retry(task, options = {}) {
  const { retries = 3, baseMs = 100, shouldRetry = () => true, wait = sleep } = options;
  for (let attempt = 0; ; attempt++) {
    try {
      return await task(attempt);
    } catch (error) {
      if (attempt >= retries || !shouldRetry(error)) throw error;
      await wait(baseMs * 2 ** attempt);
    }
  }
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

/* test helper: a task that fails `times` times with these errors, then returns value */
function flaky(times, value, error = (n) => new Error(`fail ${n}`)) {
  let calls = 0;
  return async () => {
    calls += 1;
    if (calls <= times) throw error(calls);
    return value;
  };
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

async function main() {
  let waited = [];
  const wait = async (ms) => { waited.push(ms); };
  const value = await retry(flaky(2, 'ok'), { retries: 3, baseMs: 100, wait });
  check('case 1 recovers      ', [value, waited], ['ok', [100, 200]]);

  waited = [];
  let last = '';
  try {
    await retry(flaky(9, 'never'), { retries: 2, baseMs: 100, wait });
  } catch (e) {
    last = e.message;
  }
  check('case 2 gives up      ', [last, waited], ['fail 3', [100, 200]]);

  waited = [];
  const http400 = () => Object.assign(new Error('bad request'), { status: 400 });
  const serverErrorsOnly = (e) => e.status >= 500;
  try {
    await retry(flaky(1, 'never', http400), { baseMs: 100, wait, shouldRetry: serverErrorsOnly });
  } catch (e) {
    last = e.message;
  }
  check('case 3 no retry 400  ', [last, waited], ['bad request', []]);

  waited = [];
  check('case 4 first try ok  ', [await retry(flaky(0, 'fast'), { wait }), waited], ['fast', []]);

  const t0 = Date.now();
  await retry(flaky(1, 'real'), { retries: 1, baseMs: 30 });
  check('case 5 a real 30 ms wait', Date.now() - t0 >= 25, true);
}

main();
