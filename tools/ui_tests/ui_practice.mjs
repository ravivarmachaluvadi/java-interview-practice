// UI pages (.html, 8 Oct): the live preview beside the code, Ctrl+Enter running the file's
// <script type="test"> block with ticks, an error's line as a link, the loop guard, the locked
// frame (no storage, no server), alert() not blocking, the timeout, Run with the preview hidden,
// Practice to Done, and a new UI problem from the form. All in a scratch folder.
import { existsSync } from 'node:fs';
import { join } from 'node:path';
import { suite } from './harness.mjs';

const PAGE = `<!DOCTYPE html>
<!--
PROBLEM
  A counter: + adds one.

APPROACH
  1. Keep the count, render it.

KEY INSIGHT
  State first, then render.
-->
<html>
<head>
  <style> button { padding: 4px; } </style>
</head>
<body>
  <span id="count">0</span>
  <button id="inc">+</button>
  <script>
    let count = 0;
    function render() {
      document.getElementById('count').textContent = count;
    }
    function increment() {
      count += 1;
      render();
    }
    document.getElementById('inc').onclick = increment;
    console.log('ready');
  </script>
  <script type="test">
    const $ = (s) => document.querySelector(s);
    function check(label, actual, expected) {
      console.log(\`\${label}: \${actual}   expected \${expected}\`);
    }
    $('#inc').click();
    check('case 1 one click', $('#count').textContent, '1');
    await new Promise((r) => setTimeout(r, 50));
    $('#inc').click();
    check('case 2 two clicks', $('#count').textContent, '2');
  </script>
</body>
</html>
`;

await suite(async t => {
  const { ev, waitFor, press, check, sleep, ED } = t;
  const out = () => ev(`document.querySelector('#output').textContent`);
  const foot = () => ev(`document.querySelector('#uiStatus').textContent`);
  const runAndWait = async (ms = 30000) => {
    await press('Ctrl+Enter');
    await sleep(300);
    return waitFor(`!document.querySelector('#runBtn').disabled && !/Running/.test(document.querySelector('#status').textContent)`, ms);
  };
  const lineOf = s => ev(`${ED}.getModel().getLinesContent().findIndex(l => l.includes(${JSON.stringify(s)})) + 1`);
  const replaceLine = (s, text) => ev(`(() => { const e = ${ED}, m = e.getModel(), n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(s)})) + 1;
    e.executeEdits('t', [{ range: new monaco.Range(n, 1, n, m.getLineMaxColumn(n)), text: ${JSON.stringify(text)} }]); return n; })()`);
  const tryOn = async () => { await press('Alt+t'); return waitFor(`document.querySelector('#badge').textContent.startsWith('Try')`, 5000); };
  const discard = async () => { await press('Alt+t'); await sleep(600); };

  const ws = await t.scratchFolder({ 'A01_Counter.html': PAGE });
  await ws.open('A01_Counter.html');
  await ev('localStorage.clear()');
  await ws.open('A01_Counter.html');
  await ws.onlyIn('A01_Counter.html', "getElementById('inc')");
  await ev(`${ED}.focus()`);

  // --- the preview: beside the code, in a locked frame, redrawn as you type
  let ok = await waitFor(`!document.querySelector('#uiWrap').hidden && document.querySelector('.stage').classList.contains('uisplit')`, 5000);
  check('a UI page shows beside its code', ok);
  check('in a frame locked away from this page', (await ev(`document.querySelector('#uiFrame').getAttribute('sandbox')`)) === 'allow-scripts allow-forms');
  ok = await waitFor(`/1 console line/.test(document.querySelector('#uiStatus').textContent)`, 5000);
  check('the page ran: its footer counts what it logged', ok, await foot());
  await tryOn();
  const readyLine = await replaceLine("console.log('ready');", "    console.log('ready'); console.log('again');");
  ok = await waitFor(`/2 console lines/.test(document.querySelector('#uiStatus').textContent)`, 5000);
  check('typing redraws it', ok, await foot());

  // --- an error: counted in the footer, and in the Output as a link to its line
  await replaceLine('count += 1;', '      count += 1; null.boom;');
  const errLine = await lineOf('null.boom');
  await waitFor(`/2 console lines$/.test(document.querySelector('#uiStatus').textContent)`, 3000);    // redrawn, nothing clicked yet
  await runAndWait();
  const o = await out();
  check('Run shows the error with its file and line', o.includes(`(A01_Counter.html:${errLine})`), o);
  check('and the checks it broke get crosses', (await ev(`document.querySelectorAll('#output .ln.mismatch').length`)) === 2, o);
  await ev(`[...document.querySelectorAll('#output a')].find(a => a.textContent === 'A01_Counter.html:${errLine}').click()`);
  await sleep(300);
  check('clicking the line link puts the caret on it', (await t.pos())[0] === errLine, [await t.pos(), errLine]);
  check('the footer says the tests ran there', /After the tests/.test(await foot()) && /error/.test(await foot()), await foot());
  await discard();

  // --- Run: the test block's checks ticked
  await runAndWait();
  check('Run ticks the test block\'s checks', (await ev(`document.querySelector('#verdict').textContent`)).includes('2/2 match')
        && (await out()).includes('ready'), await out());
  check('the meta line says it ran in this browser', /in this browser/.test(await ev(`document.querySelector('#meta').textContent`)));

  // --- a loop that never ends is stopped, and the tab stays usable
  await tryOn();
  const loopLine = await replaceLine("console.log('ready');", '    let k = 0; while (k < 1) { }');
  ok = await waitFor(`/loop ran for 2 s/.test(document.querySelector('#uiStatus').textContent)`, 12000);
  check('an endless loop is stopped after 2 s and named in the footer, with its line', ok && (await foot()).includes(`(line ${loopLine})`), await foot());
  const t0 = Date.now();
  await ev('1 + 1');
  check('the tab answers right away', Date.now() - t0 < 1500, Date.now() - t0);

  // --- the frame cannot reach this page's storage or the server; alert() only logs
  const probe = `    let mine = 'readable'; try { localStorage.length; } catch (e) { mine = 'blocked'; }
    console.log('storage ' + mine);
    for (const headers of [{ 'X-CodeView': '1', 'Content-Type': 'application/json' }, {}]) {
      try { await fetch(location.ancestorOrigins[0] + '/api/mkdir', { method: 'POST', headers, body: JSON.stringify({ root: ${JSON.stringify(ws.id)}, path: 'pwned' }) }); } catch (e) {}
      try { await fetch('${t.BASE}/api/mkdir', { method: 'POST', mode: 'no-cors', body: JSON.stringify({ root: ${JSON.stringify(ws.id)}, path: 'pwned' }) }); } catch (e) {}
    }
    alert('hello');
    console.log('after the alert');`;
  await replaceLine('let k = 0; while', "    console.log('ready');");
  await replaceLine("check('case 2 two clicks'", probe);
  await runAndWait();
  const o2 = await out();
  check('the page cannot read Code Viewer\'s storage', o2.includes('storage blocked'), o2);
  check('alert() is logged and the test goes on', o2.includes('[alert] hello') && o2.includes('after the alert'), o2);
  check('a POST from the page made nothing on disk', !existsSync(join(ws.dir, 'pwned')));
  await discard();

  // --- the timeout: a test that never finishes
  await tryOn();
  await replaceLine("check('case 2 two clicks'", '    await new Promise(() => {});');
  await ev(`document.querySelector('#timeoutSel').value = '5'`);
  await runAndWait(20000);
  check('a test that never ends stops at the timeout', (await ev(`document.querySelector('#status').textContent`)).includes('timed out'), await out());
  await ev(`document.querySelector('#timeoutSel').value = '10'`);
  await discard();

  // --- Run with the preview off: an off-screen page, the same result
  await ev(`document.querySelector('#previewBtn').click()`);
  ok = await waitFor(`document.querySelector('#uiWrap').hidden && !document.querySelector('.stage').classList.contains('uisplit')`, 3000);
  await runAndWait();
  check('with the preview hidden, Run still ticks the checks', ok && (await ev(`document.querySelector('#verdict').textContent`)).includes('2/2 match'), await out());
  check('and leaves no frame behind', (await ev(`document.querySelectorAll('iframe.ui-offscreen').length`)) === 0);
  await ev(`document.querySelector('#previewBtn').click()`);
  await waitFor(`!document.querySelector('#uiWrap').hidden`, 3000);

  // --- Practice: the script's function bodies hidden, markup and tests kept; solving marks it done
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 8000);
  const skel = await ev(`${ED}.getModel().getValue()`);
  check('Practice hides the page\'s functions, not its markup or tests', skel.includes('// TODO: your solution') && !skel.includes('count += 1')
        && skel.includes('<span id="count">0</span>') && skel.includes('<script type="test">') && !skel.includes('State first'), skel.slice(-700));
  await ev(`${ED}.getModel().setValue(${JSON.stringify(PAGE)})`);
  await runAndWait();
  ok = await waitFor(`/Done/.test(document.querySelector('#progBtn').textContent)`, 8000);
  check('a practice run with every check passing marks it done', ok, await ev(`document.querySelector('#progBtn').textContent`));
  await ev(`document.querySelector('#practiceBtn').click()`);
  await sleep(500);

  // --- a new UI problem from the form: its own tests pass as made
  await ev(`document.querySelector('#newBtn').click()`);
  await waitFor(`!document.querySelector('#newPanel').hidden`, 5000);
  check('the form starts as a UI problem in this folder', await ev(`document.querySelector('#npLang button.on').dataset.g`) === 'html');
  await ev(`document.querySelector('#npName').value = 'Star Rating'; document.querySelector('#npName').dispatchEvent(new Event('input'))`);
  await ev(`document.querySelector('#npCreate').click()`);
  ok = await waitFor(`location.hash.endsWith('/A02_StarRating.html') && ${ED}.getModel() && ${ED}.getModel().getLanguageId() === 'html'`, 10000);
  check('Create makes A02_StarRating.html and opens it', ok, await ev('location.hash'));
  await runAndWait();
  check("the new page's tests pass as made", (await ev(`document.querySelector('#verdict').textContent`)).includes('2/2 match'), await out());

  // --- a long output line scrolls inside the Output pane; it used to widen the whole column and
  //     push Run off a 1100px window (Java's C06 too, before 8 Oct)
  await t.send('Emulation.setDeviceMetricsOverride', { width: 1100, height: 700, deviceScaleFactor: 1, mobile: false });
  await tryOn();
  await replaceLine("check('case 2 two clicks'", "    console.log('long ' + 'x'.repeat(400));");
  await runAndWait();
  const fit = await ev(`({ page: document.documentElement.scrollWidth, inner: innerWidth, run: document.querySelector('#runBtn').getBoundingClientRect().right })`);
  check('a long output line leaves Run on screen at 1100px', fit.page <= fit.inner && fit.run <= fit.inner, fit);
  await discard();
});
