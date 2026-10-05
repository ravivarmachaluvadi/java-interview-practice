// The 5 Oct changes: Java files open at the code (Practice, Next, a kept place), `re` ranks
// `return` first, `Map<` closes itself, Compare's calmer colours in both themes, the header
// timer (count down with beeps, count up, pause, reload, reset) and the practice limit's beep.
// Chrome may play sound without a click here; timer_sound.mjs covers the case where it may not.
// Beeps are recorded by wrapping AudioContext before the page loads: no test hooks in the page.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, press, type, check, send, sleep, ED } = t;
  await send('Page.addScriptToEvaluateOnNewDocument', { source: `
    window.__tones = [];
    const AC = window.AudioContext;
    window.AudioContext = class extends AC {
      createOscillator() { const o = super.createOscillator(), st = o.start.bind(o), ctx = this;
        o.start = when => { window.__tones.push({ in: (when || 0) - ctx.currentTime }); return st(when); }; return o; }
    };` });
  const firstVisible = t.firstVisible, codeStart = t.codeStart;
  await t.fresh(C06);

  // ---- open at the code
  const cs = await codeStart();
  check('a Java file opens at its code, not the description', (await firstVisible()) === cs, [await firstVisible(), cs]);
  check('...with the cursor there', (await t.pos())[0] === cs);
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 10000);
  await sleep(600);
  const pcs = await codeStart();
  check('Practice opens at the code', (await firstVisible()) === pcs && pcs > 10, [await firstVisible(), pcs]);
  await ev(`${ED}.focus()`);
  await press('Alt+j');                                           // Next
  await waitFor(`!location.hash.endsWith('C06_DailyTemperatures.java')`, 8000);
  await sleep(1200);
  const ncs = await codeStart();
  check('Next opens at the code', (await firstVisible()) === ncs, [await ev('location.hash'), await firstVisible(), ncs]);
  await ev(`document.querySelector('#practiceBtn').click()`);      // back to the solution view of that file
  await sleep(500);
  await t.open(C06);
  await ev(`${ED}.revealLineNearTop(70)`); await sleep(500);
  const scrolled = await firstVisible();
  await ev(`history.back()`); await sleep(1500);
  await ev(`history.forward()`); await sleep(1500);
  check('a file you scrolled keeps its place', Math.abs((await firstVisible()) - scrolled) <= 1, [scrolled, await firstVisible()]);

  // ---- `re` ranks return first; Map<> closes itself (Edit mode)
  await t.open(C06);
  if ((await ev(`document.querySelector('#badge').textContent`)).startsWith('Practice')) { await ev(`document.querySelector('#practiceBtn').click()`); await sleep(500); }
  await ev(`document.querySelector('#editBtn').click()`);
  const freshLine = async () => {
    await press('Escape');
    await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('return result;'));
      e.setPosition({ lineNumber: n, column: m.getLineMaxColumn(n) }); e.focus(); })()`);
    await press('Enter');
  };
  await freshLine();
  await type('re');
  let ok = await waitFor(`(() => { const r = document.querySelector('.suggest-widget.visible .monaco-list-row'); return !!r && (r.getAttribute('aria-label') || '').startsWith('return'); })()`, 10000);
  check('`re` at a statement start: return is first', ok, await ev(`[...document.querySelectorAll('.suggest-widget.visible .monaco-list-row')].slice(0, 3).map(r => r.getAttribute('aria-label'))`));
  await press('Escape');
  await ev(`(() => { const e = ${ED}, p = e.getPosition(); e.executeEdits('t', [{ range: new monaco.Range(p.lineNumber, 1, p.lineNumber, p.column), text: '' }]); })()`);
  await type('Map<');
  await sleep(300);
  check('`Map<` becomes `Map<>`, cursor between', (await t.curLine()).trim() === 'Map<>'
        && (await ev(`${ED}.getModel().getLineContent(${ED}.getPosition().lineNumber).charAt(${ED}.getPosition().column - 1)`)) === '>', await t.curLine());
  await press('Escape');
  await type('String, Integer');
  await press('Escape');
  await press('>');
  check('typing > steps over it: Map<String, Integer>', (await t.curLine()).trim() === 'Map<String, Integer>', await t.curLine());
  await freshLine();
  await type('boolean less = n < 3;');
  check('`n < 3` is left as typed', (await t.curLine()).trim() === 'boolean less = n < 3;', await t.curLine());
  await freshLine();
  await type('List<');
  await sleep(300);
  await press('Backspace');
  check('Backspace after `List<` removes both', (await t.curLine()).trim() === 'List', await t.curLine());
  await freshLine();
  await type('String s = "Map<');
  await sleep(300);
  check('inside a string `<` is not closed', !(await t.curLine()).includes('<>'), await t.curLine());

  // ---- Compare's colours (an unsaved edit next to the file)
  const colours = () => ev(`(() => { const get = c => { const el = document.querySelector('#diff .' + c); return el ? getComputedStyle(el).backgroundColor : null; };
      return { lineInsert: get('line-insert'), lineDelete: get('line-delete'), charInsert: get('char-insert'), charDelete: get('char-delete') }; })()`);
  await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('result[colderDay] = today - colderDay;')) + 1;
      e.executeEdits('t', [{ range: new monaco.Range(n, 1, n, m.getLineMaxColumn(n)), text: '                result[colderDay] = today - colderDay + 0;' }]); })()`);
  await sleep(400);
  await ev(`document.querySelector('#compareBtn').click()`);
  await waitFor(`!!document.querySelector('#diff .char-insert')`, 8000);
  await sleep(600);
  const want = { dark: { lineInsert: 'rgb(26, 53, 33)', lineDelete: 'rgb(37, 40, 43)', charInsert: 'rgb(31, 71, 42)', charDelete: 'rgb(48, 54, 59)' },
                 light: { lineInsert: 'rgb(220, 242, 226)', lineDelete: 'rgb(236, 237, 239)', charInsert: 'rgb(180, 230, 194)', charDelete: 'rgb(216, 219, 222)' } };
  const theme1 = (await ev(`getComputedStyle(document.body).backgroundColor`)) === 'rgb(255, 255, 255)' ? 'light' : 'dark';
  let got = await colours();
  check(`Compare (${theme1}): the calm colours`, JSON.stringify(got) === JSON.stringify(want[theme1]), got);
  await ev(`document.querySelector('#themeBtn').click()`);
  await sleep(800);
  const theme2 = theme1 === 'light' ? 'dark' : 'light';
  got = await colours();
  check(`Compare (${theme2}): the calm colours`, JSON.stringify(got) === JSON.stringify(want[theme2]), got);
  await ev(`document.querySelector('#compareBtn').click()`);
  await sleep(300);

  // ---- the header timer
  await ev(`window.__tones.length = 0`);
  await ev(`document.querySelector('#clockBtn').click()`);
  check('the timer panel opens', await ev(`!document.querySelector('#clockMenu').hidden`));
  await ev(`(() => { const i = document.querySelector('#clockOther'); i.value = '0.05'; i.dispatchEvent(new Event('change', { bubbles: true })); })()`);
  await ev(`document.querySelector('#clockGo').click()`);
  await sleep(300);
  const tones = await ev(`window.__tones`);
  check('Start schedules 3 beeps for the end (3 s away)', tones.length === 3 && Math.abs(tones[0].in - 3) < 0.6, tones);
  check('the header shows ⏳ counting down', /^⏳ 00:0[23]$/.test(await ev(`document.querySelector('#clockBtn').textContent`)), await ev(`document.querySelector('#clockBtn').textContent`));
  ok = await waitFor(`document.querySelector('#clockBtn').classList.contains('ring')`, 6000);
  check('at zero it flashes, says so, and marks the tab', ok && /Time is up/.test(await ev(`document.querySelector('#toast').textContent`)) && (await ev('document.title')).startsWith('⏰'),
        [await ev(`document.querySelector('#toast').textContent`), await ev('document.title')]);
  await ev(`document.querySelector('#clockBtn').click()`);
  check('a click stops the flashing', !(await ev(`document.querySelector('#clockBtn').classList.contains('ring')`)) && !(await ev('document.title')).startsWith('⏰'));
  await ev(`document.querySelector('#clockBtn').click()`);              // open the panel again
  await ev(`document.querySelector('.clock .modes button[data-mode=up]').click()`);
  await ev(`document.querySelector('#clockGo').click()`);
  await sleep(2300);
  check('count up shows 00:02 after 2 s', /^⏱ 00:02$/.test(await ev(`document.querySelector('#clockBtn').textContent`)), await ev(`document.querySelector('#clockBtn').textContent`));
  await ev(`document.querySelector('#clockGo').click()`);              // Pause
  const paused = await ev(`document.querySelector('#clockBtn').textContent`);
  await sleep(1500);
  check('Pause stops it', (await ev(`document.querySelector('#clockBtn').textContent`)) === paused, paused);
  await ev(`document.querySelector('#clockGo').click()`);              // Resume
  await sleep(1200);
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED}`, 30000);
  await sleep(1500);
  check('a reload keeps it running', /^⏱ 00:0[3-9]$/.test(await ev(`document.querySelector('#clockBtn').textContent`)) && await ev(`document.querySelector('#clockBtn').classList.contains('on')`),
        await ev(`document.querySelector('#clockBtn').textContent`));
  await ev(`document.querySelector('#clockBtn').click()`);
  await ev(`document.querySelector('#clockReset').click()`);
  check('Reset clears it', (await ev(`document.querySelector('#clockBtn').textContent`)) === '⏱');

  // ---- the practice time limit beeps
  const k = `${t.rid}/${C06}`;
  await ev(`localStorage.setItem('cv:timerLimit', '15'); localStorage.setItem('cv:mode:${k}', '"practice"');
            localStorage.setItem('cv:timer:${k}', JSON.stringify({ ms: 15 * 60000 - 1500, paused: false, solved: false }))`);
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && document.querySelector('#badge').textContent.startsWith('Practice')`, 30000);
  ok = await waitFor(`/Time is up: 15 minutes/.test(document.querySelector('#toast').textContent)`, 8000);
  check('the practice limit beeps when it runs out', ok && (await ev(`window.__tones.length`)) >= 3, await ev(`window.__tones.length`));
}, { chromeFlags: ['--autoplay-policy=no-user-gesture-required'] });
