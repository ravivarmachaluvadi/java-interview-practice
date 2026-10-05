// Ctrl+Alt+V (IntelliJ's Introduce Variable) in C06, Edit mode, nothing saved: the list of
// enclosing expressions, rename after inserting, "this occurrence or all", one Ctrl+Z, and the
// refusal for a void call.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, press, check, send, ED } = t;
  const ctrlAltV = () => press('Ctrl+Alt+v');
  const enter = () => press('Enter', { char: false });            // a page menu's Enter, as the old suite sent it
  const lines = re => ev(`${ED}.getModel().getLinesContent().filter(l => ${re}.test(l)).map(l => l.trim())`);
  const rows = `[...document.querySelectorAll('.menu.chooser button')].map(b => b.textContent)`;
  const addAfter = (anchor, text) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(anchor)})) + 1;
      e.executeEdits('t', [{ range: new monaco.Range(n, m.getLineMaxColumn(n), n, m.getLineMaxColumn(n)), text: ${JSON.stringify(text)} }]); })()`);
  const cursorOn = (lineText, word, plus = 1) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(lineText)})) + 1;
      e.setPosition({ lineNumber: n, column: m.getLineContent(n).indexOf(${JSON.stringify(word)}) + 1 + ${plus} }); e.focus(); })()`);
  const select = (lineText, word) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(lineText)})) + 1;
      const c = m.getLineContent(n).indexOf(${JSON.stringify(word)}) + 1; e.setSelection(new monaco.Range(n, c, n, c + ${word.length})); e.focus(); })()`);

  await t.fresh(C06);
  await ev(`document.querySelector('#editBtn').click()`);

  // A. bare cursor: pick from the list, then rename the new variable
  await addAfter('int n = temperatures.length;', '\n        int top = Math.max(temperatures[0], temperatures[n - 1]) + 1;');
  await cursorOn('int top = Math.max', 'temperatures[n - 1]', 2);
  await ctrlAltV();
  let ok = await waitFor(`(${rows}).length === 3`, 8000);
  check('a bare cursor lists the 3 enclosing expressions', ok, await ev(rows));
  await press('ArrowDown');
  check('the focused one is highlighted in the code', await ev(`${ED}.getModel().getAllDecorations().some(d => d.options.className === 'extract-hl'
        && ${ED}.getModel().getValueInRange(d.range) === 'Math.max(temperatures[0], temperatures[n - 1])')`));
  await enter();
  ok = await waitFor(`!!document.querySelector('.rename-box input') && document.activeElement === document.querySelector('.rename-box input')`, 8000);
  check('it inserts the declaration and opens rename on the name', ok, await lines(`/\\bmax\\b|int top/`));
  await send('Input.insertText', { text: 'bigger' });
  await enter();
  await waitFor(`${ED}.getModel().getValue().includes('int top = bigger + 1;')`, 6000);
  check('typed name is used at both places', JSON.stringify(await lines(`/bigger/`)) ===
        JSON.stringify(['int bigger = Math.max(temperatures[0], temperatures[n - 1]);', 'int top = bigger + 1;']), await lines(`/bigger/`));

  // B. a selection that appears twice: replace all
  await addAfter('int top = bigger + 1;', '\n        int a = n * 2;\n        int b = n * 2 + 1;');
  await select('int a = n * 2;', 'n * 2');
  await ctrlAltV();
  ok = await waitFor(`(${rows}).length === 2`, 8000);
  check('it asks: this occurrence or all 2', ok, await ev(rows));
  await press('ArrowDown');
  await enter();
  await waitFor(`!!document.querySelector('.rename-box input')`, 8000);
  await press('Escape');
  const ab = await lines(`/^\\s*int (a|b|i) = /`);
  check('replace all: one declaration, both lines use it', JSON.stringify(ab) === JSON.stringify(['int i = n * 2;', 'int a = i;', 'int b = i + 1;']), ab);

  // C. one Ctrl+Z undoes the whole extraction of B (rename was Escaped)
  await ev(`${ED}.focus()`);
  await press('Ctrl+z');
  const undone = await lines(`/^\\s*int (a|b|i) = /`);
  check('one Ctrl+Z undoes it', JSON.stringify(undone) === JSON.stringify(['int a = n * 2;', 'int b = n * 2 + 1;']), undone);

  // D. void is refused with a reason
  await addAfter('int b = ', '\n        System.out.println(n);');
  await select('System.out.println(n);', 'System.out.println(n)');
  await ctrlAltV();
  ok = await waitFor(`/void/.test(document.querySelector('#toast').textContent) && document.querySelector('#toast').classList.contains('show')`, 6000);
  check('a void call is refused with a reason', ok, await ev(`document.querySelector('#toast').textContent`));
});
