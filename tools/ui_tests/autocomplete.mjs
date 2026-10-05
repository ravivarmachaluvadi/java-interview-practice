// Autocomplete and edit safety in C06: Javadoc on hover in a read-only file, the dropdown after
// a dot, a class name bringing its import, sout + Tab, postfix .var, parameter hints, the live
// error underline, Compare for an unsaved edit, Try taking the edit over, and Practice's
// dropdown switch. Nothing is saved.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, press, type, check, send, sleep, ED } = t;
  const suggestRows = `[...document.querySelectorAll('.suggest-widget.visible .monaco-list-row')].map(r => r.getAttribute('aria-label') || r.textContent)`;
  /* a fresh line inside the while loop, after `result[colderDay] = ...` */
  async function freshLine() {
    await press('Escape');
    await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('result[colderDay] = today - colderDay;')) + 1;
                      e.setPosition({ lineNumber: n, column: m.getLineMaxColumn(n) }); e.focus(); return n; })()`);
    await press('Enter');
  }

  await t.fresh(C06);
  const original = await ev(`${ED}.getModel().getValue()`);

  // --- hover on a read-only file
  await ev(`(() => { const e = ${ED}, m = e.getModel(); e.revealLineInCenter(m.getLinesContent().findIndex(l => l.includes('waiting.peek()')) + 1); })()`);
  await sleep(1000);                                   // smooth scrolling: measure after it settles
  const at = await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('waiting.peek()')) + 1;
    const col = m.getLineContent(n).indexOf('peek') + 2;
    const p = e.getScrolledVisiblePosition({ lineNumber: n, column: col }), r = e.getDomNode().getBoundingClientRect();
    return { x: r.left + p.left + 3, y: r.top + p.top + p.height / 2 }; })()`);
  for (const dx of [0, 2]) { await send('Input.dispatchMouseEvent', { type: 'mouseMoved', x: at.x + dx, y: at.y }); await sleep(100); }
  let ok = await waitFor(`[...document.querySelectorAll('.monaco-hover')].some(h => h.textContent.includes('Looks at the object at the top of this stack'))`, 20000);
  check('hover on a read-only file shows Javadoc', ok);
  await send('Input.dispatchMouseEvent', { type: 'mouseMoved', x: 5, y: 5 });

  // --- Ctrl+P: parameter info inside a call's brackets (read-only too), file search elsewhere
  const cursorOn = (lineText, word, plus = 0) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(lineText)})) + 1;
    e.setPosition({ lineNumber: n, column: m.getLineContent(n).indexOf(${JSON.stringify(word)}) + 1 + ${plus} }); e.focus(); })()`);
  const hints = `[...document.querySelectorAll('.parameter-hints-widget')].filter(w => w.offsetParent).map(w => w.textContent).join(' / ')`;
  const activeParam = `(document.querySelector('.parameter-hints-widget .parameter.active') || {}).textContent`;
  const filterFocused = `document.activeElement === document.querySelector('#filter')`;
  const ctrlP = async () => { await press('Escape'); await press('Ctrl+p'); };
  await cursorOn('waiting.push(today);', 'today');
  await ctrlP();
  ok = await waitFor(`(${hints}).includes('push(Integer item)')`, 8000);
  check('Ctrl+P in `waiting.push(|today)` shows push(Integer item)', ok && !(await ev(filterFocused)), await ev(hints));
  await cursorOn('new int[]{1, 1, 4, 2, 1, 1, 0, 0});', 'new');
  await ctrlP();
  ok = await waitFor(`(${hints}).includes('print(String label, int[] actual, int[] expected)') && ${activeParam} === 'int[] expected'`, 8000);
  check('...across lines: the 3rd argument of print( two lines up, `expected` in bold', ok, [await ev(hints), await ev(activeParam)]);
  await cursorOn('while (!waiting.isEmpty()', '!waiting');
  await ctrlP();
  check('in a `while (` header Ctrl+P still finds a file', await waitFor(filterFocused, 3000));
  await cursorOn('int n = temperatures.length;', 'temperatures');
  await ctrlP();
  check('outside any call Ctrl+P still finds a file', await waitFor(filterFocused, 3000));
  await ev(`${ED}.focus()`);

  // --- Edit, then suggestions after a dot
  await ev(`document.querySelector('#editBtn').click()`);
  await freshLine();
  await type('waiting.');
  ok = await waitFor(`(${suggestRows}).some(t => /\\bpush\\b/.test(t))`, 25000);
  const rows = await ev(suggestRows);
  check('dropdown after "waiting." lists Stack methods', ok, rows.slice(0, 6).join(' | '));
  check("Stack's own methods come first, templates last", ['empty', 'peek', 'pop', 'push', 'search'].every((m, i) => (rows[i] || '').startsWith(m)),
        rows.slice(0, 5).join(' | '));
  check('push shows its parameter name', rows.some(t => t.includes('push') && t.includes('Integer item')), rows.find(t => t.includes('push')));
  await type('po');
  await waitFor(`(${suggestRows})[0] && (${suggestRows})[0].includes('pop')`, 5000);
  await press('Enter');
  await sleep(200);
  check('Enter inserts pop()', (await t.curLine()).trim() === 'waiting.pop()', await t.curLine());

  // --- class name brings its import
  await freshLine();
  await type('ArrayDe');
  ok = await waitFor(`(${suggestRows}).some(t => t.includes('ArrayDeque'))`, 8000);
  await waitFor(`(${suggestRows})[0] && (${suggestRows})[0].includes('ArrayDeque')`, 3000);
  await press('Enter');
  await sleep(300);
  check('ArrayDeque + Enter adds the import', ok && (await ev(`${ED}.getModel().getValue()`)).includes('import java.util.ArrayDeque;'),
        await ev(`${ED}.getModel().getLinesContent().filter(l => l.startsWith('import')).join(' ; ')`));

  // --- live template with Tab (list closed first, so the page's own Tab handler runs)
  await freshLine();
  await type('sout');
  await sleep(400);
  await press('Escape');
  await press('Tab');
  await sleep(200);
  check('sout + Tab expands', (await t.curLine()).trim() === 'System.out.println();', await t.curLine());
  await ev(`${ED}.trigger('t', 'leaveSnippet')`);

  // --- postfix .var
  await freshLine();
  await type('waiting.pop().var');
  ok = await waitFor(`(${suggestRows})[0] && (${suggestRows})[0].includes('var')`, 8000);
  await press('Enter');
  await sleep(200);
  check('.var becomes a declaration', /Integer pop = waiting\.pop\(\);/.test(await t.curLine()), await t.curLine());
  await ev(`${ED}.trigger('t', 'leaveSnippet')`);

  // --- parameter hints
  await freshLine();
  await type('waiting.push(');
  ok = await waitFor(`[...document.querySelectorAll('.parameter-hints-widget')].some(w => w.textContent.includes('push(Integer item)'))`, 8000);
  check('typing ( shows parameter hints', ok, await ev(`[...document.querySelectorAll('.parameter-hints-widget')].map(w => w.textContent.slice(0, 80)).join(' / ')`));
  await press('Escape');

  // --- live error underline: first clear the half-typed lines above, then add a type error
  await ev(`(() => { const e = ${ED}; e.setValue(${JSON.stringify(original)}); })()`);
  await freshLine();
  await type('int x = "a";');
  ok = await waitFor(`monaco.editor.getModelMarkers({ owner: 'javac' }).some(m => m.message.includes('incompatible types'))`, 10000);
  check('a type error is underlined while typing', ok, await ev(`monaco.editor.getModelMarkers({ owner: 'javac' }).map(m => m.startLineNumber + ': ' + m.message)`));
  for (let i = 0; i < 4; i++) await press('Backspace');      // int x = "a  -> int x =
  await type('1;');
  ok = await waitFor(`monaco.editor.getModelMarkers({ owner: 'javac' }).length === 0`, 10000);
  check('the underline goes once fixed', ok, await t.curLine());

  // --- Compare shows the file next to the unsaved edit
  ok = await ev(`!document.querySelector('#compareBtn').hidden`);
  await ev(`document.querySelector('#compareBtn').click()`);
  await sleep(500);
  check('Compare is offered for an unsaved edit', ok && await ev(`document.querySelector('#diffLeft').textContent === 'The file (on disk)' && document.querySelector('#diffRight').textContent === 'Your edit · not saved'`),
        await ev(`document.querySelector('#diffLeft').textContent + ' | ' + document.querySelector('#diffRight').textContent`));
  await ev(`document.querySelector('#compareBtn').click()`);
  await sleep(300);

  // --- Try moves the unsaved edit and puts the file back
  const edited = await ev(`${ED}.getModel().getValue()`);
  await ev(`document.querySelector('#tryBtn').click()`);
  await sleep(500);
  const tryText = await ev(`${ED}.getModel().getValue()`);
  const draftKeys = await ev(`Object.keys(localStorage).filter(k => k.startsWith('cv:draft:'))`);
  check('Try copy holds the edit', tryText === edited && tryText.includes('int x = 1;'));
  check('the file has no unsaved edit left', draftKeys.length === 0, draftKeys);
  await ev(`document.querySelector('#tryBtn').click()`);      // Discard copy
  await sleep(500);
  check('after Discard the file shows its original text', (await ev(`${ED}.getModel().getValue()`)) === original);
  check('and the badge says Read-only', (await ev(`document.querySelector('#badge').textContent`)) === 'Read-only', await ev(`document.querySelector('#badge').textContent`));

  // --- Practice: no dropdown by default, dropdown once switched on
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 10000);
  await sleep(500);
  async function practiceDot() {
    await press('Escape');
    await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('public static void main')) + 1;
                      e.setPosition({ lineNumber: n, column: m.getLineMaxColumn(n) }); e.focus(); })()`);
    await press('Enter');
    await type('args.');
    return waitFor(`(${suggestRows}).some(t => t.includes('length'))`, 6000);
  }
  await cursorOn('new int[]{1, 1, 4, 2, 1, 1, 0, 0});', 'new');
  await ctrlP();
  check('Practice with suggestions off: Ctrl+P in a call says how to turn it on',
        await waitFor(`/Parameter info is off in practice/.test(document.querySelector('#toast').textContent)`, 4000) && !(await ev(filterFocused)),
        await ev(`document.querySelector('#toast').textContent`));
  check('Practice has no dropdown by default', !(await practiceDot()));
  await ev(`document.querySelector('#moreBtn').click()`);
  await ev(`document.querySelector('[data-act=assistPractice]').click()`);
  check('the menu switch turns it on', await practiceDot(), await ev(`document.querySelector('#assistPracticeLabel').textContent`));
});
