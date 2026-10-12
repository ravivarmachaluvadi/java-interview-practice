/*
 * =====================================================================
 *  Flatten an Array                            JavaScript | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Write flatten(items, depth = Infinity) without Array.prototype.flat: it returns a new
 *   array where nested arrays are opened up to `depth` levels. Values that are not arrays
 *   are kept as they are; the input is not changed.
 *
 * EXAMPLE
 *   [1, [2, [3, [4]]], 5]              ->  [1, 2, 3, 4, 5]
 *   [1, [2, [3, [4]]], 5], depth 1     ->  [1, 2, [3, [4]], 5]
 *   []                                 ->  []
 *
 * APPROACH  (recursion with a depth budget)
 *   1. Walk the items. A non-array, or an array when no depth is left, is pushed as is.
 *   2. An array with depth left is flattened with depth - 1, and its items are pushed.
 *   3. Return the new array.
 *
 * KEY INSIGHT
 *   Pass the remaining depth down, one less per level: depth 0 means "push this array
 *   whole", which is exactly how flat(1) leaves the deeper arrays alone.
 *
 * COMPLEXITY
 *   Time  O(n)  n = all values at every level
 *   Space O(d)  recursion depth d (plus the output)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Without recursion: a stack of [array, index, depth] frames, for very deep input.
 *   - A generator version that yields values lazily (function* with yield*).
 *   - Why Array.isArray and not instanceof Array: arrays from another frame fail instanceof.
 *   - reduce + concat one-liner, and why it is O(n^2) for long arrays.
 *
 * RUN
 *   main() prints actual vs expected.
 */
function flatten(items, depth = Infinity) {
  const out = [];
  for (const item of items) {
    if (Array.isArray(item) && depth > 0) out.push(...flatten(item, depth - 1));
    else out.push(item);
  }
  return out;
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  const nested = [1, [2, [3, [4]]], 5];
  check('case 1 all levels  ', flatten(nested), [1, 2, 3, 4, 5]);
  check('case 2 depth 1     ', flatten(nested, 1), [1, 2, [3, [4]], 5]);
  check('case 3 depth 0     ', flatten(nested, 0), [1, [2, [3, [4]]], 5]);
  check('case 4 empty       ', flatten([[], [[]]]), []);
  check('case 5 input intact', nested, [1, [2, [3, [4]]], 5]);
  check('case 6 same as flat', flatten(nested, 2), nested.flat(2));
}

main();
