// Python and JavaScript files (8 Oct): Run with ticks, traceback links, syntax underlines,
// Practice to Done, Try + Compare, the editor's JavaScript suggestions and formatter, the
// Scratch pad's language select, ▶ Try on a python block in a note, and the New problem form's
// Language row. Everything happens in a scratch folder; nothing in the repo is opened for writing.
import { suite } from './harness.mjs';

const PY = `"""
=====================================================================
 Two Sum                                        LeetCode 1 | Easy
=====================================================================

PROBLEM
  Find two indices whose numbers add up to target.

EXAMPLE
  [2, 7, 11, 15], 9  ->  [0, 1]

APPROACH  (hash map)
  1. Walk once, remembering each number's index.

KEY INSIGHT
  Look up the complement.

COMPLEXITY
  Time  O(n)

RUN
  main() prints actual vs expected.
"""


def two_sum(nums, target):
    seen = {}
    for i, x in enumerate(nums):
        if target - x in seen:
            return [seen[target - x], i]
        seen[x] = i
    return []


def check(label, actual, expected):
    print(f"{label}: {actual}   expected {expected}")


def main():
    check("case 1", two_sum([2, 7, 11, 15], 9), [0, 1])
    check("case 2", two_sum([3, 3], 6), [0, 1])


if __name__ == "__main__":
    main()
`;

const JS = `/*
 * PROBLEM
 *   Add two numbers.
 *
 * KEY INSIGHT
 *   Plus.
 */
function add(a, b) {
  return a + b;
}

function check(label, actual, expected) {
  console.log(\`\${label}: \${actual}   expected \${expected}\`);
}

function main() {
  check('case 1', add(1, 2), 3);
  check('case 2', add(-1, 1), 0);
}

main();
`;

const NOTE = '# Notes\n\n```python\nnums = [3, 1, 2]\nprint(sorted(nums), "  expected [1, 2, 3]")\n```\n';

await suite(async t => {
  const { ev, waitFor, press, check, sleep, ED } = t;
  const text = () => ev(`${ED}.getModel().getValue()`);
  const out = () => ev(`document.querySelector('#output').textContent`);
  const runAndWait = async () => {
    await ev(`document.querySelector('#runBtn').click()`);
    await sleep(300);
    return waitFor(`!document.querySelector('#runBtn').disabled && !/Running|Compiling/.test(document.querySelector('#status').textContent)`, 30000);
  };
  const lineOf = s => ev(`${ED}.getModel().getLinesContent().findIndex(l => l.includes(${JSON.stringify(s)})) + 1`);
  const putAfter = (s, add) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(s)})) + 1;
    e.executeEdits('t', [{ range: new monaco.Range(n, m.getLineMaxColumn(n), n, m.getLineMaxColumn(n)), text: ${JSON.stringify(add)} }]); })()`);

  const ws = await t.scratchFolder({ 'B01_TwoSum.py': PY, 'A01_Sum.js': JS, 'notes.md': NOTE });
  await ws.open('B01_TwoSum.py');
  await ev('localStorage.clear()');
  await ws.open('B01_TwoSum.py');
  await ws.onlyIn('B01_TwoSum.py', 'def two_sum');

  // --- Python: Run, ticks, Python's name in the meta line
  check('a .py file opens as Python, at the code below the docstring',
        (await ev(`${ED}.getModel().getLanguageId()`)) === 'python' && (await t.pos())[0] === await lineOf('def two_sum'),
        [await ev(`${ED}.getModel().getLanguageId()`), await t.pos()]);
  check('Run and Practice are offered', !(await ev(`document.querySelector('#runBtn').disabled`)) && !(await ev(`document.querySelector('#practiceBtn').hidden`)));
  await runAndWait();
  check('Run ticks both expected lines', (await ev(`document.querySelector('#verdict').textContent`)).includes('2/2 match')
        && (await ev(`document.querySelectorAll('#output .ln.pass').length`)) === 2, await out());
  check('the meta line names Python', /Python 3\./.test(await ev(`document.querySelector('#meta').textContent`)));

  // --- a runtime error: the traceback's line is a link to it
  await press('Alt+t');
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Try')`, 5000);
  await putAfter('seen = {}', '\n    raise ValueError("boom")');
  const boom = await lineOf('raise ValueError');
  await runAndWait();
  const link = await ev(`[...document.querySelectorAll('#output a')].map(a => a.textContent).join(' | ')`);
  check('a traceback line links to the file and line', link.includes(`B01_TwoSum.py", line ${boom}`), link);
  await ev(`[...document.querySelectorAll('#output a')].find(a => a.textContent.includes('line ${boom}')).click()`);
  await sleep(300);
  check('and clicking it puts the caret there', (await t.pos())[0] === boom, await t.pos());

  // --- a syntax error: red underline, from the run and live while typing
  await ev(`(() => { const e = ${ED}, m = e.getModel(), n = m.getLinesContent().findIndex(l => l.includes('raise ValueError')) + 1;
    e.executeEdits('t', [{ range: new monaco.Range(n, 1, n, m.getLineMaxColumn(n)), text: '    if x' }]); })()`);
  let ok = await waitFor(`monaco.editor.getModelMarkers({ owner: 'javac' }).some(m => m.startLineNumber === ${boom} && /SyntaxError/.test(m.message))`, 6000);
  check('a Python syntax error is underlined as you type', ok,
        await ev(`monaco.editor.getModelMarkers({ owner: 'javac' }).map(m => m.startLineNumber + ' ' + m.message)`));
  await runAndWait();
  check('Run says Syntax error', (await ev(`document.querySelector('#status').textContent`)).includes('Syntax error'), await out());
  check('and underlines the line', await ev(`monaco.editor.getModelMarkers({ owner: 'javac' }).some(m => m.startLineNumber === ${boom})`));
  await press('Alt+t');                                // Discard copy
  await sleep(500);

  // --- Practice: the solution body goes, the docstring's approach becomes hints; solving marks it done
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 8000);
  const skel = await text();
  check('Practice hides two_sum and the approach', skel.includes('# TODO: your solution') && !skel.includes('seen[x] = i')
        && !skel.includes('Look up the complement') && skel.includes('def check(label, actual, expected):'), skel.slice(0, 200));
  check('Hint is offered', !(await ev(`document.querySelector('#hintBtn').hidden`)));
  await ev(`${ED}.getModel().setValue(${JSON.stringify(PY)})`);
  await runAndWait();
  ok = await waitFor(`/Done/.test(document.querySelector('#progBtn').textContent)`, 8000);
  check('a practice run with every case matching marks it done', ok, await ev(`document.querySelector('#progBtn').textContent`));
  await ev(`document.querySelector('#practiceBtn').click()`);         // back to the file (a peek after solving)
  await sleep(500);

  // --- JavaScript: Run, ticks, Node in the meta line
  await ws.open('A01_Sum.js');
  await ws.onlyIn('A01_Sum.js', 'function add');
  check('a .js file opens as JavaScript', (await ev(`${ED}.getModel().getLanguageId()`)) === 'javascript');
  await runAndWait();
  check('Run ticks the JavaScript cases', (await ev(`document.querySelector('#verdict').textContent`)).includes('2/2 match'), await out());
  check('the meta line names Node', /Node v\d+/.test(await ev(`document.querySelector('#meta').textContent`)));

  // --- Try + Compare
  await press('Alt+t');
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Try')`, 5000);
  await ev(`(() => { const e = ${ED}, m = e.getModel(), n = m.getLinesContent().findIndex(l => l.includes('return a + b')) + 1;
    e.executeEdits('t', [{ range: new monaco.Range(n, 1, n, m.getLineMaxColumn(n)), text: '  return a - b;' }]); })()`);
  await runAndWait();
  check('a changed Try copy runs and gets crosses', (await ev(`document.querySelectorAll('#output .ln.mismatch').length`)) === 2, await out());
  await press('Alt+c');
  ok = await waitFor(`!document.querySelector('#diffWrap').hidden && monaco.editor.getDiffEditors()[0].getLineChanges() && monaco.editor.getDiffEditors()[0].getLineChanges().length === 1`, 8000);
  check('Compare shows the one changed line', ok);
  await press('Alt+c');
  await sleep(300);

  // --- the editor's own JavaScript help: suggestions after a dot, and Ctrl+Alt+L
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLineCount();
    e.executeEdits('t', [{ range: new monaco.Range(n, 1, n, 1), text: 'const arr = [1];\\narr.' }]); e.setPosition({ lineNumber: n + 1, column: 5 }); e.focus(); })()`);
  await ev(`${ED}.trigger('t', 'editor.action.triggerSuggest', {})`);
  ok = await waitFor(`[...document.querySelectorAll('.suggest-widget .monaco-list-row')].some(r => /\\bconcat\\b/.test(r.textContent))`, 15000);   // (map is further down, not drawn)
  check("JavaScript suggestions after a dot (the editor's own)", ok);
  await press('Escape');
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLineCount();
    e.executeEdits('t', [{ range: new monaco.Range(n - 1, 1, n, m.getLineMaxColumn(n)), text: 'function   f( ){return 1}' }]); })()`);
  await press('Ctrl+Alt+l');
  ok = await waitFor(`${ED}.getModel().getValue().includes('function f() { return 1 }')`, 10000);
  check('Ctrl+Alt+L formats JavaScript', ok, (await text()).slice(-60));
  await press('Alt+t');                                // Discard copy
  await sleep(500);

  // --- the Scratch pad's language select: each language keeps its own text
  await ev(`location.hash = '#/scratch'`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Scratch')`, 8000);
  check('the Scratch pad offers its languages', (await ev(`[...document.querySelector('#scratchLang').options].map(o => o.value).join()`)) === 'java,py,js,html'
        && !(await ev(`document.querySelector('#scratchLang').hidden`)));
  await ev(`(() => { const s = document.querySelector('#scratchLang'); s.value = 'py'; s.dispatchEvent(new Event('change')); })()`);
  ok = await waitFor(`${ED}.getModel().getLanguageId() === 'python'`, 5000);
  await runAndWait();
  check('Python in the Scratch pad runs and ticks', ok && (await ev(`document.querySelectorAll('#output .ln.pass').length`)) === 1, await out());
  await ev(`${ED}.getModel().setValue('print("mine")\\n')`);
  await sleep(400);
  await ev(`(() => { const s = document.querySelector('#scratchLang'); s.value = 'js'; s.dispatchEvent(new Event('change')); })()`);
  await waitFor(`${ED}.getModel().getLanguageId() === 'javascript'`, 5000);
  await runAndWait();
  check('JavaScript in the Scratch pad runs and ticks', (await ev(`document.querySelectorAll('#output .ln.pass').length`)) === 1, await out());
  await ev(`(() => { const s = document.querySelector('#scratchLang'); s.value = 'py'; s.dispatchEvent(new Event('change')); })()`);
  await waitFor(`${ED}.getModel().getLanguageId() === 'python'`, 5000);
  check('switching back finds the Python text as it was left', (await text()) === 'print("mine")\n', await text());
  await ev(`(() => { const s = document.querySelector('#scratchLang'); s.value = 'java'; s.dispatchEvent(new Event('change')); })()`);
  await waitFor(`${ED}.getModel().getLanguageId() === 'java'`, 5000);
  check("and Java's text is still its own", (await text()).includes('void main()'));

  // --- ▶ Try on a python block in a note
  await ws.open('notes.md');
  await waitFor(`!!document.querySelector('#md .cb-try')`, 8000);
  await ev(`document.querySelector('#md .cb-try').click()`);
  await sleep(500);
  await ev(`document.querySelector('#md .try-run').click()`);
  ok = await waitFor(`!!document.querySelector('#md .try-out .ln.pass')`, 20000);
  check('▶ Try runs a python block from a note', ok, await ev(`(document.querySelector('#md .try-out') || {}).textContent || ''`));

  // --- New problem: the Language row
  await ws.open('B01_TwoSum.py');
  await ev(`document.querySelector('#newBtn').click()`);
  await waitFor(`!document.querySelector('#newPanel').hidden`, 5000);
  check("the form starts in the folder's language (Python here)", await ev(`document.querySelector('#npLang button.on').dataset.g`) === 'py'
        && await ev(`document.querySelector('#npMethod').closest('label').hidden`));
  await ev(`document.querySelector('#npLang [data-g="js"]').click()`);
  await ev(`document.querySelector('#npName').value = 'Debounce'; document.querySelector('#npName').dispatchEvent(new Event('input'))`);
  check('the preview names a .js file', (await ev(`document.querySelector('#npPreview').textContent`)).includes('B02_Debounce.js'),
        await ev(`document.querySelector('#npPreview').textContent`));
  await ev(`document.querySelector('#npCreate').click()`);
  ok = await waitFor(`location.hash.endsWith('/B02_Debounce.js') && ${ED}.getModel() && ${ED}.getModel().getLanguageId() === 'javascript'`, 10000);
  check('Create makes and opens it', ok, await ev('location.hash'));
  await runAndWait();
  check("the new file's harness runs", /expected/.test(await out()), await out());
});
