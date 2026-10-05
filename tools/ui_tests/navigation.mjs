// IntelliJ's navigation in C06: next / previous method (Alt+↓ / ↑), moving a line on
// Alt+Shift+↓ / ↑, and Back / Forward (Ctrl+Alt+← / →, the ← → buttons) after F12, Ctrl+click,
// a far click and another file - but not after arrow keys or Page Down. Nothing saved.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, press, check, send, sleep, ED } = t;
  const lineOf = s => ev(`${ED}.getModel().getLinesContent().findIndex(l => l.includes(${JSON.stringify(s)})) + 1`);
  const colOf = (s, word) => ev(`${ED}.getModel().getLinesContent().find(l => l.includes(${JSON.stringify(s)})).indexOf(${JSON.stringify(word)}) + 1`);
  const at = async (n, c) => { await ev(`${ED}.setPosition({ lineNumber: ${n}, column: ${c} }); ${ED}.revealLineInCenter(${n}); ${ED}.focus()`); await sleep(300); };
  const disabled = id => ev(`!!document.querySelector('#${id}') && document.querySelector('#${id}').disabled`);
  const back = () => press('Ctrl+Alt+ArrowLeft'), fwd = () => press('Ctrl+Alt+ArrowRight');
  /* page coordinates of a word on a line (revealed and settled first) */
  const spot = async (n, col) => {
    await ev(`${ED}.revealLineInCenter(${n})`); await sleep(700);
    return ev(`(() => { const e = ${ED}, p = e.getScrolledVisiblePosition({ lineNumber: ${n}, column: ${col} }), r = e.getDomNode().getBoundingClientRect();
      return { x: r.left + p.left + 4, y: r.top + p.top + p.height / 2 }; })()`);
  };
  await t.fresh(C06);
  check('a fresh page has nothing to go back to: ← and → are greyed out', await disabled('navBackBtn') && await disabled('navFwdBtn'));

  // ---- next / previous method
  const dt = await lineOf('int[] dailyTemperatures(int[] temperatures)'), pr = await lineOf('private static void print('), mn = await lineOf('public static void main(');
  await at(await t.codeStart(), 1);
  await press('Alt+ArrowDown');
  let p = await t.pos();
  check('Alt+↓ goes to the next method, onto its name', p[0] === dt && p[1] === await colOf('int[] dailyTemperatures(', 'dailyTemperatures'), [p, dt]);
  await press('Alt+ArrowDown'); p = await t.pos();
  check('...and to the next one', p[0] === pr, [p, pr]);
  await press('Alt+ArrowDown'); await press('Alt+ArrowDown'); p = await t.pos();
  check('...main is the last: it stays there', p[0] === mn, [p, mn]);
  await press('Alt+ArrowUp'); p = await t.pos();
  check('Alt+↑ goes to the previous method', p[0] === pr, [p, pr]);
  await at(await lineOf('result[colderDay] = today - colderDay;'), 17);
  await press('Alt+ArrowUp'); p = await t.pos();
  check('from inside a method, Alt+↑ goes to its own name first', p[0] === dt, [p, dt]);

  // ---- moving a line is now Alt+Shift+↓ / ↑ (Edit mode, put back afterwards)
  await ev(`document.querySelector('#editBtn').click()`);
  const before = await ev(`${ED}.getModel().getValue()`), nLine = await lineOf('int n = temperatures.length;');
  await at(nLine, 9);
  await press('Alt+ArrowDown');
  check('in Edit mode Alt+↓ jumps too: the text is unchanged', (await ev(`${ED}.getModel().getValue()`)) === before);
  await at(nLine, 9);
  await press('Alt+Shift+ArrowDown');
  check('Alt+Shift+↓ moves the line down', (await ev(`${ED}.getModel().getLineContent(${nLine + 1})`)).trim() === 'int n = temperatures.length;'
        && (await ev(`${ED}.getModel().getLineContent(${nLine})`)).trim() === 'int[] result = new int[n];');
  await press('Alt+Shift+ArrowUp');
  check('Alt+Shift+↑ moves it back', (await ev(`${ED}.getModel().getValue()`)) === before);
  await ev(`document.querySelector('#editBtn').click()`);

  // ---- Back / Forward in one file (a fresh page: an empty history)
  await t.fresh(C06);
  const use = await lineOf('waiting.push(today);'), decl = await lineOf('for (int today = 0;'), useCol = await colOf('waiting.push(today);', 'today') + 1;
  await at(use, useCol);
  await press('F12'); await sleep(800);
  p = await t.pos();
  check('F12 on `today` jumps 6 lines up to its declaration', p[0] === decl, [p, decl]);
  check('...and ← is no longer greyed out', await ev(`!!document.querySelector('#navBackBtn')`) && !(await disabled('navBackBtn')));
  await back(); p = await t.pos();
  check('Ctrl+Alt+← goes back to the use, line and column (even after a short jump)', p[0] === use && p[1] === useCol, [p, use, useCol]);
  check('...and → lights up', await ev(`!!document.querySelector('#navFwdBtn')`) && !(await disabled('navFwdBtn')));
  await fwd(); p = await t.pos();
  check('Ctrl+Alt+→ goes forward to the declaration again', p[0] === decl, [p, decl]);

  // Ctrl+click on `temperatures` in the while condition: to the parameter, 7 lines up
  const wl = await lineOf('while (!waiting.isEmpty() && temperatures[today]'), wc = await colOf('while (!waiting.isEmpty() && temperatures[today]', 'temperatures[today]') + 2;
  await at(wl, 1);
  const s = await spot(wl, wc);
  await send('Input.dispatchMouseEvent', { type: 'mouseMoved', x: s.x, y: s.y, modifiers: 2 }); await sleep(400);
  for (const type of ['mousePressed', 'mouseReleased']) await send('Input.dispatchMouseEvent', { type, x: s.x, y: s.y, button: 'left', clickCount: 1, modifiers: 2 });
  await sleep(1000);
  p = await t.pos();
  check('Ctrl+click on `temperatures` jumps to the parameter', p[0] === dt, [p, dt]);
  await back(); p = await t.pos();
  check('...and Ctrl+Alt+← comes back to the click', p[0] === wl, [p, wl]);

  // a click far down is a jump; arrow keys and Page Down are not
  const far = await lineOf('new int[]{1, 1, 4, 2, 1, 1, 0, 0});');
  await at(decl, 9);
  const c = await spot(far, 17);
  await t.click(c.x, c.y);
  p = await t.pos();
  check('(a click far down moved the caret there)', p[0] === far, [p, far]);
  await back(); p = await t.pos();
  check('Ctrl+Alt+← after a far click goes back', p[0] === decl, [p, decl]);
  await t.fresh(C06);
  await at(await t.codeStart(), 1);
  for (let i = 0; i < 3; i++) await press('PageDown');
  await press('ArrowDown'); await press('Ctrl+Home');
  const moved = await t.pos();
  await back();
  check('arrow keys, Page Down and Ctrl+Home leave nothing to go back to',
        /Nothing to go back to/.test(await ev(`document.querySelector('#toast').textContent`)) && JSON.stringify(await t.pos()) === JSON.stringify(moved),
        [await ev(`document.querySelector('#toast').textContent`), moved, await t.pos()]);

  // ---- another file, and the buttons
  const left = [await lineOf('while (!waiting.isEmpty()'), 13];
  await at(...left);
  await press('Alt+j');
  await waitFor(`!location.hash.endsWith('C06_DailyTemperatures.java')`, 8000); await sleep(1000);
  const other = await ev('location.hash');
  await ev(`${ED}.focus()`);
  await back();
  await waitFor(`location.hash.endsWith('C06_DailyTemperatures.java')`, 8000); await sleep(800);
  p = await t.pos();
  check('after Next, Ctrl+Alt+← reopens C06 at the same line and column', p[0] === left[0] && p[1] === left[1], [await ev('location.hash'), p, left]);
  await ev(`document.querySelector('#navFwdBtn').click()`);
  check('the → button goes forward to the other file again', await waitFor(`location.hash === ${JSON.stringify(other)}`, 8000), await ev('location.hash'));
  await sleep(800);
  await ev(`document.querySelector('#navBackBtn').click()`);
  check('...and the ← button back to C06', await waitFor(`location.hash.endsWith('C06_DailyTemperatures.java')`, 8000));
});
