// Shortcuts in Compare (7 Oct, Ravi): Compare's right side is a second editor, and Ctrl+Shift+Enter
// typed there did not complete the statement. Each key now acts on the editor you type in -
// never on the hidden one behind Compare, which holds the same code. Runs on a scratch folder.
import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import { suite } from './harness.mjs';

const JAVA = 'class A01_First {\n    static int f(int x) { return x + 1; }\n\n    public static void main(String[] a) {\n        System.out.println(f(1) + "   expected 2");\n    }\n}\n';
const NAME = 'Topic/A01_First.java';

await suite(async t => {
  const { ev, check, sleep, waitFor, press, ED } = t;
  const s = await t.scratchFolder({ [NAME]: JAVA });
  /* the right side of Compare: the editor in #diff that holds the same model as the main one */
  const MOD = `monaco.editor.getEditors().find(e => e !== ${ED} && e.getDomNode() && e.getDomNode().closest('#diff') && e.getModel() === ${ED}.getModel())`;
  const text = () => ev(`${ED}.getModel().getValue()`);
  const lines = () => ev(`${ED}.getModel().getLinesContent()`);
  const comparing = () => ev(`!document.querySelector('#diffWrap').hidden`);
  /* exact text into the right side, then the caret at the end of `line` there */
  const putIn = (at, txt) => ev(`(() => { const e = ${MOD}; e.executeEdits('t', [{ range: new monaco.Range(${at}, 1, ${at}, 1), text: ${JSON.stringify(txt)} }]); return true; })()`);
  const caretEnd = n => ev(`(() => { const e = ${MOD}; e.setPosition({ lineNumber: ${n}, column: e.getModel().getLineMaxColumn(${n}) }); e.focus(); return true; })()`);

  await s.open(NAME);
  await ev(`${ED}.setPosition({ lineNumber: 1, column: 1 }); ${ED}.focus()`);   // the hidden editor's caret: line 1
  await press('Alt+t'); await sleep(600);
  await ev(`${ED}.setPosition({ lineNumber: 1, column: 1 }); ${ED}.focus()`);
  await press('Alt+c'); await sleep(700);
  check('(Compare is open on the Try copy)', await comparing() && await ev(`!!(${MOD})`));

  // ---- Ctrl+Shift+Enter
  await putIn(5, '        int b = f(2)\n');
  await caretEnd(5);
  await press('Ctrl+Shift+Enter'); await sleep(300);
  const after = await lines();
  check('Ctrl+Shift+Enter in Compare adds the ; on that line', after[4] === '        int b = f(2);', after.slice(3, 7));
  check('...and changes nothing else (not the hidden editor\'s line 1)',
    (await text()) === JAVA.replace('    public static void main(String[] a) {\n', '    public static void main(String[] a) {\n        int b = f(2);\n'), after);
  check('...and the caret stays in Compare, at the line\'s end', await ev(`(() => { const e = ${MOD}, p = e.getPosition(); return e.hasTextFocus() && p.lineNumber === 5 && p.column === e.getModel().getLineMaxColumn(5); })()`));

  // ---- Ctrl+D duplicates the line you are on
  await press('Ctrl+d'); await sleep(300);
  const dup = await lines();
  check('Ctrl+D in Compare duplicates that line', dup[4] === '        int b = f(2);' && dup[5] === '        int b = f(2);' && dup[0] === 'class A01_First {', dup.slice(0, 7));
  await ev(`${MOD}.trigger('t', 'undo')`); await sleep(200);           // two `int b` would not compile

  // ---- Ctrl+Alt+L formats (the Java helper may take a few seconds the first time)
  await putIn(7, '      int   c=1;\n');
  await caretEnd(7);
  await press('Ctrl+Alt+l');
  const formatted = await waitFor(`${ED}.getModel().getLineContent(7) === '        int c = 1;'`, 20000);
  check('Ctrl+Alt+L in Compare formats the code', formatted, (await lines()).slice(5, 9));

  // ---- Ctrl+Enter runs what is in Compare
  await caretEnd(5);
  await press('Ctrl+Enter');
  // its own output line, not javac's "';' expected"
  check('Ctrl+Enter in Compare runs it', await waitFor(`/2\\s+expected 2/.test(document.querySelector('#output').textContent)`, 30000),
    await ev(`document.querySelector('#output').textContent.slice(0, 200)`));

  // ---- Alt+C closes Compare
  await caretEnd(5);
  await press('Alt+c'); await sleep(500);
  check('Alt+C in Compare closes it', !(await comparing()));
  await press('Alt+t'); await sleep(600);                    // Discard the Try copy

  // ---- an unsaved edit in Compare: Ctrl+S saves it
  await ev(`document.querySelector('#editBtn').click()`); await sleep(200);
  await ev(`(() => { const e = ${ED}; e.executeEdits('t', [{ range: new monaco.Range(2, 1, 2, e.getModel().getLineMaxColumn(2)), text: '    static int f(int x) { return x + 5; }' }]); e.focus(); return true; })()`);
  await sleep(400);
  await press('Alt+c'); await sleep(700);
  check('(Compare is open on the unsaved edit)', await comparing());
  await s.onlyIn(NAME, 'class A01_First');
  await caretEnd(2);
  await press('Ctrl+s');
  await waitFor(`/Saved/.test(document.querySelector('#toast').textContent)`, 8000); await sleep(300);
  check('Ctrl+S in Compare saves the edit', readFileSync(join(s.dir, NAME), 'utf8').includes('return x + 5;'));
});
