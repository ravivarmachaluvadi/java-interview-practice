// Run through the page, F12 (in this file and into another file of the folder), Shift+F6
// rename, and Alt+Enter's import fix, in C06 (Edit mode, nothing saved). The two-file F12 case
// uses a scratch folder.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, press, check, send, sleep, ED } = t;
  const caret = t.pos;
  const lineOf = s => ev(`${ED}.getModel().getLinesContent().findIndex(l => l.includes(${JSON.stringify(s)})) + 1`);
  const cursorOn = (lineText, word, plus = 1) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(lineText)})) + 1;
    e.setPosition({ lineNumber: n, column: m.getLineContent(n).indexOf(${JSON.stringify(word)}) + 1 + ${plus} }); e.focus(); })()`);
  const altEnter = async () => {             // Alt+Enter as the old suite sent it: key down and up, nothing typed
    await send('Input.dispatchKeyEvent', { type: 'rawKeyDown', key: 'Enter', code: 'Enter', windowsVirtualKeyCode: 13, modifiers: 1 });
    await send('Input.dispatchKeyEvent', { type: 'keyUp', key: 'Enter', code: 'Enter', windowsVirtualKeyCode: 13, modifiers: 1 });
  };

  await t.fresh(C06);

  // --- Run through the page
  await ev(`document.querySelector('#runBtn').click()`);
  let ok = await waitFor(`/expected/.test(document.querySelector('#output').textContent)`, 30000);
  check('Run works through the page', ok, await ev(`document.querySelector('#meta').textContent`));

  // --- F12 on a use of colderDay goes to its declaration (read-only file)
  await cursorOn('result[colderDay] = today', 'colderDay');
  await press('F12');
  await sleep(800);
  const declLine = await lineOf('int colderDay = waiting.pop();');
  check('F12 jumps to the declaration in this file', (await caret())[0] === declLine, [await caret(), declLine]);

  // --- Shift+F6 rename (Edit mode)
  const before = await ev(`${ED}.getModel().getValue()`);
  const inComments = (before.match(/\bcolder\b/g) || []).length;      // "a colder temperature" in the header and a comment
  await ev(`document.querySelector('#editBtn').click()`);
  await cursorOn('int colderDay = waiting.pop();', 'colderDay');
  await press('Shift+F6');
  ok = await waitFor(`!!document.querySelector('.rename-box input') && document.activeElement === document.querySelector('.rename-box input')`, 8000);
  await send('Input.insertText', { text: 'colder' });       // the old name is selected, so typing replaces it
  await sleep(200);
  await press('Enter', { char: false });
  await waitFor(`!${ED}.getModel().getValue().includes('colderDay')`, 5000);
  const text = await ev(`${ED}.getModel().getValue()`);
  check('Shift+F6 renames every use, comments untouched', ok && !text.includes('colderDay') && (text.match(/\bcolder\b/g) || []).length === inComments + 3,
        [inComments, (text.match(/\bcolder\w*\b/g) || []).join(',')]);
  await cursorOn('waiting.peek()', 'peek');
  await press('Shift+F6');
  ok = await waitFor(`[...document.querySelectorAll('.monaco-editor-overlaymessage, .rename-box')].some(x => /declared in this file/.test(x.textContent))`, 6000);
  check('renaming a JDK method is refused with a reason', ok);
  await press('Escape');

  // --- Alt+Enter: quick fix adds the import
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('result[colder] = today')) + 1;
    e.executeEdits('t', [{ range: new monaco.Range(n, m.getLineMaxColumn(n), n, m.getLineMaxColumn(n)), text: '\\n                ArrayDeque<Integer> dq = new ArrayDeque<>();' }]); })()`);
  ok = await waitFor(`monaco.editor.getModelMarkers({ owner: 'javac' }).some(m => /cannot find symbol/.test(m.message))`, 10000);
  await cursorOn('ArrayDeque<Integer> dq', 'ArrayDeque', 2);
  await altEnter();
  const menu = await waitFor(`[...document.querySelectorAll('.action-widget, .context-view')].some(x => x.textContent.includes('Import java.util.ArrayDeque'))`, 8000);
  await press('Enter');
  await sleep(500);
  check('Alt+Enter on the red name offers "Import java.util.ArrayDeque"', ok && menu);
  check('and Enter adds the import', (await ev(`${ED}.getModel().getValue()`)).includes('import java.util.ArrayDeque;'));
  check('without going full screen', !(await ev(`document.body.classList.contains('full')`)));
  await cursorOn('int n = temperatures.length;', 'int', 0);
  await altEnter();
  await sleep(400);
  check('Alt+Enter elsewhere is still full screen', await ev(`document.body.classList.contains('full')`));
  await ev(`document.querySelector('#fullExitBtn').click()`);

  // --- F12 into a helper class in another file of the folder
  const ws = await t.scratchFolder({
    'Helper.java': 'class Helper {\n    static int twice(int x) {\n        return 2 * x;\n    }\n}\n',
    'B03_UsesHelper.java': 'class B03_UsesHelper {\n    public static void main(String[] a) {\n        System.out.println(Helper.twice(21));\n    }\n}\n',
  });
  await ws.open('B03_UsesHelper.java');
  await ws.onlyIn('B03_UsesHelper.java', 'Helper.twice(21)');
  await cursorOn('Helper.twice(21)', 'twice');
  await press('F12');
  ok = await waitFor(`location.hash.endsWith('/Helper.java') && ${ED}.getModel().getValue().startsWith('class Helper')`, 8000);
  await sleep(500);
  check('F12 opens the other file at the method', ok && (await caret())[0] === 2, [await ev('location.hash'), await caret()]);
});
