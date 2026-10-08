// IntelliJ keys asked for on 6 Oct: Alt+Enter → Create method on a call to a method not written
// yet, Ctrl+D duplicates, Alt+J with a selection adds the next match as another cursor (no
// selection: Next file), Ctrl+Alt+Shift+J every match; and Ctrl+Alt+L never fails without a word.
// All in a scratch folder, nothing saved.
import { suite } from './harness.mjs';

const MAIN = ['class Main {', '    public static void main(String[] args) {', '        int[] nums = {2, 7, 11, 15};',
  '        int total = nums.length + nums[0];', '        System.out.println(total);', '    }', '}', ''].join('\n');

await suite(async t => {
  const { ev, waitFor, press, check, send, sleep, ED } = t;
  const ws = await t.scratchFolder({ 'A01_Main.java': MAIN, 'B01_Next.java': 'class Next {\n}\n', 'Notes.md': '# Notes\n' });
  const text = () => ev(`${ED}.getModel().getValue()`);
  const sels = () => ev(`${ED}.getSelections().map(s => ${ED}.getModel().getValueInRange(s))`);
  const cursorOn = (lineText, word, plus = 1) => ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(lineText)})) + 1;
    e.setPosition({ lineNumber: n, column: m.getLineContent(n).indexOf(${JSON.stringify(word)}) + 1 + ${plus} }); e.focus(); })()`);
  const altEnter = async () => {
    await send('Input.dispatchKeyEvent', { type: 'rawKeyDown', key: 'Enter', code: 'Enter', windowsVirtualKeyCode: 13, modifiers: 1 });
    await send('Input.dispatchKeyEvent', { type: 'keyUp', key: 'Enter', code: 'Enter', windowsVirtualKeyCode: 13, modifiers: 1 });
  };

  await ws.open('A01_Main.java');
  await ws.onlyIn('A01_Main.java', 'class Main');
  await ev(`document.querySelector('#editBtn').click()`);
  await sleep(300);

  // ---- Alt+Enter on a call to a method not written yet: Create method
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('int[] nums')) + 1;
    e.executeEdits('t', [{ range: new monaco.Range(n, m.getLineMaxColumn(n), n, m.getLineMaxColumn(n)), text: '\\n        int r = twoSum(nums, 9);' }]); })()`);
  let ok = await waitFor(`monaco.editor.getModelMarkers({ owner: 'javac' }).some(m => /symbol:\\s+method twoSum/.test(m.message))`, 15000);
  check('a call to a method not written yet is underlined red', ok);
  await cursorOn('int r = twoSum', 'twoSum', 2);
  await altEnter();
  const menu = await waitFor(`[...document.querySelectorAll('.action-widget, .context-view')].some(x => x.textContent.includes("Create method 'twoSum'"))`, 10000);
  check("Alt+Enter offers Create method 'twoSum'", menu);
  await press('Enter');
  ok = await waitFor(`${ED}.getModel().getValue().includes('private static int twoSum(int[] nums, int i) {')`, 5000);
  check('Enter creates it, its parameters and return type taken from the call', ok, (await text()).split('\n').slice(-8));
  check("…below main, with the body's return selected to type over", JSON.stringify(await sels()) === '["return 0;"]'
    && (await text()).indexOf('public static void main') < (await text()).indexOf('private static int twoSum'), await sels());
  check('…and not full screen', !(await ev(`document.body.classList.contains('full')`)));
  ok = await waitFor(`!monaco.editor.getModelMarkers({ owner: 'javac' }).some(m => /cannot find symbol/.test(m.message))`, 15000);
  check('the red underline goes away', ok);
  await press('Ctrl+z');
  check('one Ctrl+Z takes it back', !(await text()).includes('private static int twoSum'));

  // ---- Ctrl+D duplicates the line, or the selection
  await cursorOn('System.out.println(total);', 'println');
  const lines = (await text()).split('\n').length;
  await press('Ctrl+d');
  const after = (await text()).split('\n');
  check('Ctrl+D duplicates the line', after.length === lines + 1 && after.filter(l => l === '        System.out.println(total);').length === 2,
    after.length - lines);
  check('…the cursor goes to the copy', (await t.pos())[0] === after.indexOf('        System.out.println(total);') + 2, await t.pos());
  await press('Ctrl+z');
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('int total')) + 1;
    const c = m.getLineContent(n).indexOf('total') + 1; e.setSelection(new monaco.Range(n, c, n, c + 5)); e.focus(); })()`);
  await press('Ctrl+d');
  check('Ctrl+D on a selection duplicates just that text', (await text()).includes('int totaltotal = '), (await text()).split('\n')[3]);
  await press('Ctrl+z');

  // ---- several cursors: Alt+J adds the next match, Alt+Shift+J takes it back, Ctrl+Alt+Shift+J all
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('int[] nums')) + 1;
    const c = m.getLineContent(n).indexOf('nums') + 1; e.setSelection(new monaco.Range(n, c, n, c + 4)); e.focus(); })()`);
  await press('Alt+j');
  check('Alt+J with "nums" selected adds the next "nums"', JSON.stringify(await sels()) === '["nums","nums"]', await sels());
  check('…and stays on this file', (await ev('location.hash')).endsWith('A01_Main.java'));
  await press('Alt+Shift+j');
  check('Alt+Shift+J takes the last one back', (await sels()).length === 1, await sels());
  await press('Ctrl+Alt+Shift+j');
  const all = ((await text()).match(/\bnums\b/g) || []).length;
  check('Ctrl+Alt+Shift+J selects every "nums"', all > 2 && (await sels()).length === all && (await sels()).every(s => s === 'nums'), [all, await sels()]);
  await press('Escape');

  // ---- Ctrl+Alt+L always says something
  await ev(`document.querySelector('#moreBtn').click()`);
  check('⋯ has Format the file', await ev(`!!document.querySelector('#menu [data-act="format"]')`));
  await ev(`document.querySelector('#menu [data-act="format"]').click()`);
  ok = await waitFor(`/Already formatted|Formatted/.test(document.querySelector('#toast').textContent)`, 15000);
  check('…and it formats', ok, await ev(`document.querySelector('#toast').textContent`));
  await ws.open('Notes.md');
  await ev(`document.activeElement && document.activeElement.blur()`);
  await press('Ctrl+Alt+l');
  ok = await waitFor(`/formats Java, JavaScript and HTML files/.test(document.querySelector('#toast').textContent)`, 4000);
  check('Ctrl+Alt+L in a Markdown file says which files it formats', ok, await ev(`document.querySelector('#toast').textContent`));

  // ---- with nothing selected Alt+J is still Next file
  await ws.open('A01_Main.java');
  await ev(`${ED}.setPosition({ lineNumber: 1, column: 1 }); ${ED}.focus()`);
  await press('Alt+j');
  check('Alt+J with nothing selected goes to the next file', await waitFor(`location.hash.endsWith('B01_Next.java')`, 8000), await ev('location.hash'));
});
