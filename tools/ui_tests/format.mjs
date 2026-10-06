// Ctrl+Alt+L (IntelliJ's Reformat Code) in the page, nothing saved: read-only files ask for
// Edit, C06 is already formatted, a messy file comes out as IntelliJ lays it out (the same
// MESSY / MESSY_FORMATTED pair tools/test_codeview.py checks on the server side), the cursor
// keeps its line, one Ctrl+Z undoes it, and broken code is refused with its line.
import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import { suite, C06, REPO } from './harness.mjs';

const pySrc = readFileSync(join(REPO, 'tools', 'test_codeview.py'), 'utf8');
const pyString = name => pySrc.match(new RegExp(`^${name} = '''([\\s\\S]*?)'''`, 'm'))[1];
const messy = pyString('MESSY'), expected = pyString('MESSY_FORMATTED');

await suite(async t => {
  const { ev, waitFor, press, check, ED } = t;
  const format = () => press('Ctrl+Alt+l');

  await t.fresh(C06);
  const original = await ev(`${ED}.getModel().getValue()`);

  // read-only: it says to press Edit, and changes nothing
  await ev(`${ED}.focus()`);
  await format();
  let ok = await waitFor(`/press Edit/.test(document.querySelector('#toast').textContent)`, 4000);
  check('read-only file: asks to press Edit first', ok && (await ev(`${ED}.getModel().getValue()`)) === original);

  // C06 as written: "Already formatted"
  await ev(`document.querySelector('#editBtn').click()`);
  await ev(`${ED}.focus()`);
  await format();
  ok = await waitFor(`/Already formatted/.test(document.querySelector('#toast').textContent)`, 8000);
  check('C06 as written: already formatted, nothing changes', ok && (await ev(`${ED}.getModel().getValue()`)) === original,
        await ev(`document.querySelector('#toast').textContent`));

  // messy code: comes out as IntelliJ formats it (plus the kept rules), cursor line kept
  await ev(`${ED}.getModel().setValue(${JSON.stringify(messy)}); ${ED}.setPosition({ lineNumber: 12, column: 3 }); ${ED}.focus()`);   // on "int n=temperatures.length;"
  await format();
  ok = await waitFor(`${ED}.getModel().getValue() === ${JSON.stringify(expected)}`, 8000);
  check("messy file: Ctrl+Alt+L gives IntelliJ's layout", ok, ok ? '' : (await ev(`${ED}.getModel().getValue()`)).split('\n').slice(0, 14));
  check('the cursor stays on its line of code', (await t.curLine()).trim() === 'int n = temperatures.length;', await t.curLine());
  await press('Ctrl+z');
  check('one Ctrl+Z undoes the formatting', (await ev(`${ED}.getModel().getValue()`)) === messy);

  // broken code: refused with the line
  await ev(`${ED}.getModel().setValue('class B {\\n    void f() {\\n        int x = ;\\n    }\\n}\\n'); ${ED}.focus()`);
  await format();
  ok = await waitFor(`/line 3/.test(document.querySelector('#toast').textContent)`, 8000);
  check('broken code: refused, naming the line', ok, await ev(`document.querySelector('#toast').textContent`));

  // Try mode (Alt+T, a throwaway copy): Ctrl+Alt+L formats the copy (6 Oct, Ravi asked)
  await t.fresh(C06);
  await ev(`document.querySelector('#tryBtn').click()`);
  ok = await waitFor(`/Try/.test(document.querySelector('#badge').textContent) || /try/i.test(document.querySelector('#badge').className)`, 8000);
  await ev(`${ED}.getModel().setValue(${JSON.stringify(messy)}); ${ED}.setPosition({ lineNumber: 12, column: 3 }); ${ED}.focus()`);
  await format();
  const formatted = await waitFor(`${ED}.getModel().getValue() === ${JSON.stringify(expected)}`, 8000);
  check('Try mode: Ctrl+Alt+L formats the copy', ok && formatted,
        [await ev(`document.querySelector('#badge').textContent`), await ev(`document.querySelector('#toast').textContent`)]);

  // right after a click on a button (Try, Edit...) the focus is on that button, not in the code
  await ev(`${ED}.getModel().setValue(${JSON.stringify(messy)}); document.querySelector('#tryBtn').focus()`);
  const focusOut = await ev(`!document.activeElement.closest('.monaco-editor')`);
  await format();
  ok = await waitFor(`${ED}.getModel().getValue() === ${JSON.stringify(expected)}`, 8000);
  check('Ctrl+Alt+L also works when the focus is on a button, not in the code', focusOut && ok, focusOut);

  // 6 Oct, Ravi's screenshot: the code changed while the formatter was answering, and the answer
  // was dropped. Now it formats the new text instead. The first answer is held back 1.5 s here
  // and a line is added meanwhile; the result must be formatted and keep that line.
  await ev(`${ED}.getModel().setValue(${JSON.stringify(messy)}); ${ED}.setPosition({ lineNumber: 12, column: 3 }); ${ED}.focus();
    if (!window.__realFetch) window.__realFetch = window.fetch;
    window.__held = false;
    window.fetch = async (u, o) => {
      if (!window.__held && String(u).includes('/api/assist') && o && String(o.body).includes('"op":"format"')) {
        window.__held = true; await new Promise(r => setTimeout(r, 1500));
      }
      return window.__realFetch(u, o);
    }`);
  await format();
  await ev(`${ED}.executeEdits('t', [{ range: new monaco.Range(1, 1, 1, 1), text: '// added while formatting' + String.fromCharCode(10) }])`);
  ok = await waitFor(`${ED}.getModel().getValue() === ${JSON.stringify('// added while formatting\n' + expected)}`, 10000);
  check('a change during formatting: it formats the new text, keeping the change', ok,
        [await ev(`document.querySelector('#toast').textContent`), (await ev(`${ED}.getModel().getValue()`)).split('\n').slice(0, 3)]);
  await ev(`window.fetch = window.__realFetch`);

  // every press is logged in this browser (cv:fmtlog): from the keys arriving to how it ended
  await ev(`localStorage.removeItem('cv:fmtlog'); ${ED}.getModel().setValue(${JSON.stringify(messy)}); ${ED}.focus()`);
  await format();
  await waitFor(`/Formatted/.test(localStorage.getItem('cv:fmtlog') || '')`, 8000);
  const log = JSON.parse(await ev(`localStorage.getItem('cv:fmtlog') || '[]'`));
  check('the log has the keys, who handled them, and the outcome', log.length === 3 && /keys seen, focus in the editor/.test(log[0])
    && /format started by the editor key/.test(log[1]) && /\[try\] Formatted: /.test(log[2]), log);
});
