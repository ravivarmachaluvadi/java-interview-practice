/*
 * =====================================================================
 *  Deep Clone                                  JavaScript | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Write deepClone(value) without structuredClone or JSON: it returns a copy that shares
 *   no object with the original. It must handle plain objects, arrays, Date, Map, Set,
 *   nested mixes of them, and cycles (an object that refers back to itself). Primitives and
 *   functions are returned as they are.
 *
 * EXAMPLE
 *   { a: [1, { b: 2 }] }          ->  equal content; changing the copy leaves the original alone
 *   o = { name: "x" }; o.self = o ->  copy.self === copy, and copy !== o
 *   { when: new Date(0) }         ->  copy.when is a Date with the same time, not the same object
 *
 * APPROACH  (recursion plus a "seen" map)
 *   1. A primitive or a function: return it.
 *   2. If this object was already copied (seen.get), return that copy: this ends cycles.
 *   3. Make the empty copy of the right kind, record it in seen BEFORE filling it, then
 *      fill it by cloning each entry (Map keys and values, Set values, own keys).
 *
 * KEY INSIGHT
 *   Record the copy before cloning the children: a cycle then finds the half-built copy
 *   instead of recursing forever, and two paths to one shared object give one shared copy.
 *
 * COMPLEXITY
 *   Time  O(n)  every object and value visited once
 *   Space O(n)  the seen map plus the recursion
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why JSON.parse(JSON.stringify(x)) is not enough: loses Date, Map, Set, undefined,
 *     functions, and throws on a cycle.
 *   - structuredClone: built in since Node 17; what it refuses (functions, DOM nodes).
 *   - Class instances: keep the prototype with Object.create(Object.getPrototypeOf(v)).
 *   - Symbol keys and property descriptors (getters): Reflect.ownKeys and
 *     Object.getOwnPropertyDescriptors.
 *
 * RUN
 *   main() prints actual vs expected.
 */
function deepClone(value, seen = new Map()) {
  if (value === null || typeof value !== 'object') return value;
  if (seen.has(value)) return seen.get(value);
  if (value instanceof Date) return new Date(value.getTime());
  let copy;
  if (value instanceof Map) {
    copy = new Map();
    seen.set(value, copy);
    value.forEach((v, k) => copy.set(deepClone(k, seen), deepClone(v, seen)));
    return copy;
  }
  if (value instanceof Set) {
    copy = new Set();
    seen.set(value, copy);
    value.forEach((v) => copy.add(deepClone(v, seen)));
    return copy;
  }
  copy = Array.isArray(value) ? [] : {};
  seen.set(value, copy);
  for (const key of Object.keys(value)) copy[key] = deepClone(value[key], seen);
  return copy;
}

function check(label, actual, expected) {
  const show = (v) => JSON.stringify(v);
  console.log(`${label}: ${show(actual)}   expected ${show(expected)}`);
}

function main() {
  const original = { a: [1, { b: 2 }], c: 'text' };
  const copy = deepClone(original);
  copy.a[1].b = 99;
  check('case 1 nested, apart   ', [original.a[1].b, copy.a[1].b], [2, 99]);
  const o = { name: 'x' };
  o.self = o;
  const oc = deepClone(o);
  check('case 2 a cycle         ', [oc.self === oc, oc !== o, oc.name], [true, true, 'x']);
  const d = { when: new Date(0) };
  const dc = deepClone(d);
  const date = [dc.when instanceof Date, dc.when.getTime(), dc.when !== d.when];
  check('case 3 a Date          ', date, [true, 0, true]);
  const shared = { n: 1 };
  const m = { map: new Map([['k', shared]]), set: new Set([shared]) };
  const mc = deepClone(m);
  const fromMap = mc.map.get('k'), fromSet = [...mc.set][0];
  const one = [fromMap === fromSet, fromMap !== shared, fromMap.n];
  check('case 4 Map, Set, shared', one, [true, true, 1]);
  check('case 5 primitives      ', [deepClone(5), deepClone('s'), deepClone(null)], [5, 's', null]);
}

main();
