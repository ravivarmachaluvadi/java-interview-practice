// Edit pressed instead of Try, and saved (7 Oct, Ravi): a bar beside the line numbers marks every
// line changed since the file was opened in this tab, saved or not, until a reload. Compare shows
// them, Reset puts the as-opened text back (unsaved until Ctrl+S), and Try offers to move them
// into the Try copy and put the file back. Everything that saves works in a scratch folder.
import { readFileSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { suite } from './harness.mjs';

const JAVA = name => `class ${name} {\n    static int f(int x) { return x + 1; }\n\n    public static void main(String[] a) {\n        System.out.println(f(1) + "   expected 2");\n    }\n}\n`;
const A1 = 'Topic/A01_First.java', A2 = 'Topic/A02_Second.java';

await suite(async t => {
  const { ev, check, sleep, send, waitFor, press, ED } = t;
  const s = await t.scratchFolder({ [A1]: JAVA('A01_First'), [A2]: JAVA('A02_Second') });
  const disk = p => readFileSync(join(s.dir, p), 'utf8');
  /* the change bars on the open model: [[line, 'chg-…'], …] */
  const marks = () => ev(`${ED}.getModel().getAllDecorations().filter(d => /^chg-/.test(d.options.linesDecorationsClassName || ''))
    .map(d => [d.range.startLineNumber, d.options.linesDecorationsClassName]).sort((a, b) => a[0] - b[0])`);
  const settle = () => sleep(450);                      // past the marks' and the draft's debounce
  const same = (a, b) => JSON.stringify(a) === JSON.stringify(b);
  const setLine = (n, text) => ev(`(() => { const e = ${ED}; e.executeEdits('t', [{ range: new monaco.Range(${n}, 1, ${n}, e.getModel().getLineMaxColumn(${n})), text: ${JSON.stringify(text)} }]); e.setPosition({ lineNumber: 1, column: 1 }); e.focus(); return true; })()`);
  const insertAt = (n, text) => ev(`(() => { const e = ${ED}; e.executeEdits('t', [{ range: new monaco.Range(${n}, 1, ${n}, 1), text: ${JSON.stringify(text)} }]); e.setPosition({ lineNumber: 1, column: 1 }); e.focus(); return true; })()`);
  const badge = () => ev(`document.querySelector('#badge').textContent`);
  const shown = id => ev(`!document.querySelector('#${id}').hidden`);
  const click = id => ev(`document.querySelector('#${id}').click()`);
  const bannerButtons = () => ev(`document.querySelector('#banner').hidden ? [] : [...document.querySelectorAll('#banner button')].map(b => b.textContent)`);
  const bannerClick = label => ev(`[...document.querySelectorAll('#banner button')].find(b => b.textContent === ${JSON.stringify(label)}).click()`);
  const save = async (name, marker) => {
    await s.onlyIn(name, marker);
    await ev(`${ED}.focus()`);
    await ev(`document.querySelector('#toast').textContent = ''`);
    await press('Ctrl+s');
    await waitFor(`/Saved/.test(document.querySelector('#toast').textContent)`, 8000);
    await sleep(300);
  };
  const editOn = async () => { if (!(await ev(`document.querySelector('#editBtn').classList.contains('on')`))) await click('editBtn'); await sleep(200); };
  const L2 = '    static int f(int x) { return x + 2; }';

  // ---- marks while editing, then after Save
  await s.open(A1);
  check('a file just opened has no marks', same(await marks(), []), await marks());
  check('...and no Reset', !(await shown('resetBtn')));
  await editOn();
  await setLine(2, L2); await settle();
  check('a changed line is marked as changed (not saved yet)', same(await marks(), [[2, 'chg-mod']]), await marks());
  await insertAt(3, '    // added\n'); await settle();
  check('a new line under it is marked as added', same(await marks(), [[2, 'chg-mod'], [3, 'chg-add']]), await marks());
  await save(A1, 'class A01_First');
  check('(Ctrl+S wrote the change)', disk(A1).includes('x + 2') && disk(A1).includes('// added'), disk(A1));
  check('after Save the marks stay', same(await marks(), [[2, 'chg-mod'], [3, 'chg-add']]), await marks());
  const shot = async name => {
    if (!process.env.CV_SHOTS) return;
    const r = await send('Page.captureScreenshot', { format: 'png' });
    writeFileSync(`${process.env.CV_SHOTS}/${name}.png`, Buffer.from(r.result.data, 'base64'));
  };
  await shot('saved_changes');
  await click('editBtn'); await settle();
  check('...and when Edit is locked again', same(await marks(), [[2, 'chg-mod'], [3, 'chg-add']]), await marks());
  await ev(`location.hash = '#/${s.id}/${A2}'`);
  await waitFor(`!!${ED}.getModel() && ${ED}.getModel().getValue().includes('class A02_Second')`, 8000);
  await ev(`location.hash = '#/${s.id}/${A1}'`);
  await waitFor(`!!${ED}.getModel() && ${ED}.getModel().getValue().includes('class A01_First')`, 8000); await settle();
  check('...and after another file and back', same(await marks(), [[2, 'chg-mod'], [3, 'chg-add']]), await marks());

  // ---- Compare after Save
  check('Compare is offered after Save', await shown('compareBtn'));
  await press('Alt+c'); await sleep(600);
  const labels = await ev(`document.querySelector('#diffLeft').textContent + ' | ' + document.querySelector('#diffRight').textContent`);
  check('Compare: the file as you opened it next to it now', labels === 'The file when you opened it | Your changes · saved', labels);
  check('...the left side is the text as opened', await ev(`monaco.editor.getModels().some(m => !m.isDisposed() && m.getValue() === ${JSON.stringify(JAVA('A01_First'))})`));
  await press('Alt+c'); await sleep(400);

  // ---- Reset after Save: the as-opened text, unsaved
  check('Reset is offered after Save', await shown('resetBtn'));
  await click('resetBtn'); await settle();
  check('Reset puts the text as opened in the editor', (await ev(`${ED}.getModel().getValue()`)) === JAVA('A01_First'));
  check('...as an unsaved edit', (await badge()).includes('not saved'), await badge());
  check('...the file on disk is not touched yet', disk(A1).includes('x + 2'));
  check('...and the marks are gone', same(await marks(), []), await marks());
  await ev(`${ED}.focus()`);
  await ev(`${ED}.trigger('t', 'undo')`); await settle();
  check('Ctrl+Z brings the change and its marks back', (await ev(`${ED}.getModel().getValue()`)).includes('x + 2') && same(await marks(), [[2, 'chg-mod'], [3, 'chg-add']]), await marks());
  await click('resetBtn'); await settle();
  await save(A1, 'class A01_First');
  check('Reset, then Ctrl+S: the file is exactly as it was', disk(A1) === JAVA('A01_First'), disk(A1));
  check('...with no marks', same(await marks(), []), await marks());

  // ---- Try after Save: move the changes into the copy
  await editOn();
  await setLine(2, L2); await settle();
  await save(A1, 'class A01_First');
  await press('Alt+t'); await sleep(600);
  check('Try after Save offers to move the saved changes', (await bannerButtons()).includes('Move them'), await bannerButtons());
  check('(the Try copy holds them)', (await ev(`${ED}.getModel().getValue()`)).includes('x + 2'));
  await bannerClick('Move them');
  await waitFor(`/back as you opened it/.test(document.querySelector('#toast').textContent)`, 8000);
  check('Move them: the file is exactly as it was', disk(A1) === JAVA('A01_First'), disk(A1));
  check('...and the Try copy still has the changes', (await ev(`${ED}.getModel().getValue()`)).includes('x + 2'));
  await press('Alt+t'); await sleep(600);           // Discard copy
  check('back in the file: as opened, no marks', (await ev(`${ED}.getModel().getValue()`)) === JAVA('A01_First') && same(await marks(), []), await marks());

  // ---- Try after Save, Keep them in the file; a removed line is marked
  await ev(`location.hash = '#/${s.id}/${A2}'`);
  await waitFor(`!!${ED}.getModel() && ${ED}.getModel().getValue().includes('class A02_Second')`, 8000); await sleep(400);
  await editOn();
  await ev(`${ED}.executeEdits('t', [{ range: new monaco.Range(3, 1, 4, 1), text: '' }])`); await settle();
  check('a removed line is marked where it was', same(await marks(), [[3, 'chg-del']]), await marks());
  await shot('saved_changes_removed');
  await save(A2, 'class A02_Second');
  await press('Alt+t'); await sleep(600);
  await bannerClick('Keep them in the file'); await sleep(400);
  check('Keep them in the file: the file keeps the saved change', disk(A2) !== JAVA('A02_Second') && !disk(A2).includes('\n\n'), disk(A2));
  await press('Alt+t'); await sleep(600);

  // ---- an unsaved edit only: Try moves it, as on 5 Oct, and asks nothing
  await s.open(A1);
  await editOn();
  await setLine(2, L2); await settle();
  await press('Alt+t'); await sleep(600);
  check('an unsaved edit only: Try moves it (5 Oct)', /moved into this Try copy/.test(await ev(`document.querySelector('#toast').textContent`)), await ev(`document.querySelector('#toast').textContent`));
  check('...and asks nothing about saved changes', !(await bannerButtons()).includes('Move them'), await bannerButtons());
  await press('Alt+t'); await sleep(600);

  // ---- changed by something else: that is the new starting point
  await s.open(A1);
  await editOn();
  await setLine(2, L2); await settle();
  await save(A1, 'class A01_First');
  await click('editBtn'); await settle();
  check('(marks before the outside change)', same(await marks(), [[2, 'chg-mod']]), await marks());
  writeFileSync(join(s.dir, A1), JAVA('A01_First').replace('return x + 1;', 'return x + 3;'));
  await ev(`window.dispatchEvent(new Event('focus'))`);
  await waitFor(`${ED}.getModel().getValue().includes('x + 3')`, 8000); await settle();
  check('a change made elsewhere shows, with no marks', same(await marks(), []) && !(await shown('resetBtn')), await marks());

  // ---- a reload forgets the as-opened text
  await editOn();
  await setLine(2, L2); await settle();
  await save(A1, 'class A01_First');
  check('(marks before the reload)', same(await marks(), [[2, 'chg-mod']]), await marks());
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel() && ${ED}.getModel().getValue().includes('x + 2')`, 30000); await settle();
  check('after a reload: no marks and no Reset', same(await marks(), []) && !(await shown('resetBtn')), await marks());
});
