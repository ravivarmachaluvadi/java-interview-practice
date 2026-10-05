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
});
