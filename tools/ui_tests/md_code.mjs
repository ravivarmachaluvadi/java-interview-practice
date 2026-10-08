// Code blocks and diagrams in a rendered .md (8 Oct, Ravi's screenshots of 14_Kubernetes_QA and
// 06_Spring_Boot_Web_QA in dark mode: "anything can be done for yaml, code snippets ... for
// flow charts and mermaid diagrams any control to resize?"). Before this, colorizeElement ran
// without a theme, so Monaco used its light "vs" colours on the dark page (and switched the
// editor's theme too), every block ended in a blank line, bash had no colours, and there was no
// Copy, no wrap, no way to run a snippet and no way to zoom a diagram.
// Everything happens in a scratch folder; nothing is saved.
import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import { suite } from './harness.mjs';

const JAVA = 'class A01_First {\n    public static void main(String[] a) {\n        System.out.println(1 + "   expected 1");\n    }\n}\n';
const LONG = 'x'.repeat(40) + ' ' + Array.from({ length: 40 }, (_, i) => 'word' + i).join(' ');
// a label before the result: outcheck only crosses a labelled line (an unlabelled one only gets ticks)
const STMTS = 'int[] a = {1, 3, 5};\nIO.println("sum: " + (a[0] + a[2]) + "   expected 6");';
const DOC = [
  '# Code', '',
  '```yaml', 'apiVersion: v1', 'kind: Pod', '```', '',
  '```bash', 'echo "hi" | grep h', '```', '',
  '```text', LONG, '```', '',
  '```java', STMTS, '```', '',
  '```java', 'class Hello {', '    public static void main(String[] args) {', '        System.out.println("hello   expected hello");', '    }', '}', '```', '',
  '```java', '@RestController', 'class Web {', '    @GetMapping("/x") String x() { return "x"; }', '}', '```', '',
  '```java', 'class Bag {', '    public static void main(String[] args) {', '        List<Integer> xs = new ArrayList<>(Map.of(1, 2).keySet());',
  '        System.out.println("size: " + xs.size() + "   expected 1");', '    }', '}', '```', '',
  '## Flow', '',
  '```mermaid', 'flowchart TD', '    A[One] --> B[Two] --> C[Three]', '    C --> D[Four] --> E[Five] --> F[Six]', '```', '',
].join('\n');

await suite(async t => {
  const { ev, waitFor, press, check, sleep, send, ED } = t;
  const q = sel => `document.querySelector(${JSON.stringify(sel)})`;
  const s = await t.scratchFolder({ 'code.md': DOC, 'A01_First.java': JAVA });
  const disk = () => readFileSync(join(s.dir, 'code.md'), 'utf8');
  const blocks = `[...document.querySelectorAll('#md .cb')]`;
  const blk = i => `${blocks}[${i}]`;
  const MINI = `monaco.editor.getEditors().find(e => e.getContainerDomNode().closest('#md'))`;
  const tryOut = () => ev(`(${q('#md .try-out')} || {}).innerText || ''`);

  // dark theme and a known Scratch pad, before the page loads
  await s.open('A01_First.java');
  await ev(`localStorage.setItem('cv:theme', '"dark"'); localStorage.setItem('cv:scratch', JSON.stringify('// MINE\\n'))`);
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel()`, 30000); await sleep(800);
  await ev(`location.hash = '#/${s.id}/code.md'`);
  await waitFor(`location.hash.endsWith('/code.md') && !${q('#md')}.hidden && ${q('#md')}.querySelectorAll('pre code span').length > 0`, 15000);
  await sleep(1200);

  // ---- colours: the editor's own theme, and the editor keeps it
  const kw = await ev(`(() => { const sp = [...document.querySelectorAll('#md pre code span')].find(x => x.textContent === 'public'); return sp ? getComputedStyle(sp).color : null; })()`);
  check('dark theme: a Java keyword has the dark theme\'s colour, not Monaco\'s light blue', kw === 'rgb(207, 142, 109)', kw);
  await ev(`${q('#srcBtn')}.click()`);
  await waitFor(`!${q('#editor')}.hidden && !!${ED}.getModel()`, 8000); await sleep(800);
  const bg = await ev(`getComputedStyle(${q('#editor .monaco-editor-background')}).backgroundColor`);
  check('…and the editor beside the page stays dark', bg === 'rgb(30, 31, 34)', bg);
  await ev(`${q('#srcBtn')}.click()`);
  await waitFor(`${q('#editor')}.hidden && !${q('#md')}.hidden && ${q('#md')}.querySelectorAll('pre code span').length > 0`, 8000); await sleep(800);

  // ---- no blank last line; bash has colours
  const lines = i => ev(`(() => { const p = ${blk(i)}?.querySelector('pre') || document.querySelectorAll('#md pre')[${i}], cs = getComputedStyle(p);
    return p.clientHeight - parseFloat(cs.paddingTop) - parseFloat(cs.paddingBottom); })()`);
  const h2 = await lines(0), h1 = await lines(1);
  check('no blank last line: a 2-line block is twice as tall as a 1-line one', Math.abs(h2 / h1 - 2) < 0.15, { h2, h1 });
  const bashColours = await ev(`(() => { const sp = [...document.querySelectorAll('#md pre')[1].querySelectorAll('code span')];
    const colour = re => { const x = sp.find(s => re.test(s.textContent) && !s.children.length); return x ? getComputedStyle(x).color : null; };
    return [colour(/^echo$/), colour(/"hi"/)]; })()`);
  check('bash is coloured: a command and a string differ', !!bashColours[0] && !!bashColours[1] && bashColours[0] !== bashColours[1], bashColours);

  // ---- the block's bar: language, Copy
  check('each code block has its bar', (await ev(`${blocks}.length`)) === 7, await ev(`${blocks}.length`));
  check('…naming its language', (await ev(`(${blk(0)}?.querySelector('.cb-lang') || {}).textContent`)) === 'yaml');
  const [bx, by] = await ev(`(() => { const r = ${blk(0)}.getBoundingClientRect(); return [r.left + 40, r.top + 20]; })()`);
  await ev(`(() => { const md = ${q('#md')}; md.scrollTop = 0; })()`);
  await send('Input.dispatchMouseEvent', { type: 'mouseMoved', x: bx, y: by }); await sleep(300);
  check('the bar shows when the pointer is on the block', (await ev(`getComputedStyle(${blk(0)}.querySelector('.cb-bar')).opacity`)) === '1');
  await ev(`(() => { window.__copied = null; navigator.clipboard.writeText = t => { window.__copied = t; return Promise.resolve(); }; })()`);
  await ev(`${blk(0)}.querySelector('.cb-copy').click()`); await sleep(200);
  check('Copy takes exactly the text', (await ev('window.__copied')) === 'apiVersion: v1\nkind: Pod', await ev('window.__copied'));

  // ---- Alt+Z wraps long lines on the page too
  const over = () => ev(`(() => { const p = ${blk(2)}.querySelector('pre'); return p.scrollWidth - p.clientWidth; })()`);
  check('a long line scrolls sideways, as before', (await over()) > 20, await over());
  await ev('document.activeElement && document.activeElement.blur && document.activeElement.blur()');
  await press('Alt+z'); await sleep(300);
  check('Alt+Z on the page wraps it', (await over()) <= 1, await over());
  await press('Alt+z'); await sleep(300);
  check('…and Alt+Z again unwraps it', (await over()) > 20, await over());

  // ---- Try in place
  const tries = await ev(`${blocks}.map(b => !!b.querySelector('.cb-try'))`);
  check('Try on the two Java blocks that can run, not on Spring code or other languages', JSON.stringify(tries) === '[false,false,false,true,true,false,true]', tries);
  await ev(`${blk(3)}.querySelector('.cb-try').click()`);
  check('Try turns the block into an editor where it is', await waitFor(`!!${MINI} && ${blk(3)}.classList.contains('trying')`, 8000));
  check('…holding the snippet', (await ev(`${MINI}.getModel().getValue()`)) === STMTS, await ev(`${MINI}.getModel().getValue()`));
  check('…with the focus in it', await ev(`${MINI}.hasTextFocus()`));
  await press('Ctrl+Enter');
  check('Ctrl+Enter runs it: the output shows under it, ticked', await waitFor(`/sum: 6   expected 6/.test((${q('#md .try-out')} || {}).innerText || '') && !!${q('#md .try-out .ln.pass')}`, 30000), await tryOut());
  check('…and says it ran', /Ran/.test(await tryOut()), await tryOut());
  await ev(`(() => { const e = ${MINI}, m = e.getModel(); e.executeEdits('t', [{ range: m.findMatches('a[2]', false, false, true, null, false)[0].range, text: 'a[1]' }]); e.focus(); })()`);
  await press('Ctrl+Enter');
  check('an edit runs too: 4, crossed', await waitFor(`/sum: 4   expected 6/.test((${q('#md .try-out')} || {}).innerText || '') && !!${q('#md .try-out .ln.mismatch')}`, 30000), await tryOut());
  await ev(`(() => { const e = ${MINI}, m = e.getModel(); e.executeEdits('t', [{ range: m.findMatches('println', false, false, true, null, false)[0].range, text: 'printn' }]); e.focus(); })()`);
  await press('Ctrl+Enter');
  check('a typo: did not compile', await waitFor(`/Did not compile/.test((${q('#md .try-out')} || {}).innerText || '')`, 30000), await tryOut());
  check('…with a red underline on it', await waitFor(`monaco.editor.getModelMarkers({ resource: ${MINI}.getModel().uri }).length > 0`, 3000));
  await press('Ctrl+s'); await sleep(500);
  check('Ctrl+S in it does not touch the note', disk() === DOC);
  check('…and says so', /never saved/.test(await ev(`${q('#toast')}.textContent`)), await ev(`${q('#toast')}.textContent`));
  await ev(`${q('#md .try-close')}.click()`); await sleep(300);
  check('Close puts the block back as it was', !(await ev(`${blk(3)}.classList.contains('trying')`)) && !(await ev(`!!${MINI}`))
    && (await ev(`${blk(3)}.querySelector('pre').innerText.replace(/\\u00a0/g, ' ').trim()`)) === STMTS);
  await ev(`${blk(3)}.querySelector('.cb-try').click()`);
  check('Try again picks up the edit', await waitFor(`!!${MINI} && ${MINI}.getModel().getValue().includes('printn')`, 8000));
  await ev(`${blk(4)}.querySelector('.cb-try').click()`);
  check('Try on another block: one at a time', await waitFor(`!${blk(3)}.classList.contains('trying') && ${blk(4)}.classList.contains('trying')`, 8000));
  await press('Ctrl+Enter');
  check('a class with main() runs as it is', await waitFor(`/hello   expected hello/.test((${q('#md .try-out')} || {}).innerText || '')`, 30000), await tryOut());
  // the notes leave imports out; only a void main() file gets java.util and the rest by itself
  await ev(`${blk(6)}.querySelector('.cb-try').click()`);
  await waitFor(`${blk(6)}.classList.contains('trying')`, 8000);
  await press('Ctrl+Enter');
  check('a class using List, ArrayList and Map runs without its imports', await waitFor(`/size: 1   expected 1/.test((${q('#md .try-out')} || {}).innerText || '')`, 30000), await tryOut());

  // ---- → Scratch pad, undoable
  await ev(`${blk(3)}.querySelector('.cb-try').click()`);
  await waitFor(`${blk(3)}.classList.contains('trying')`, 8000);
  await ev(`${q('#md .try-scratch')}.click()`);
  check('→ Scratch pad opens it there, ready to run', await waitFor(`location.hash === '#/scratch' && ${ED}.getModel().getValue().includes('printn') && ${ED}.getModel().getValue().includes('void main()')`, 8000),
    await ev(`${ED}.getModel().getValue()`));
  await ev(`${ED}.focus()`);
  await press('Ctrl+z');
  check('…and Ctrl+Z there brings back what it had', (await ev(`${ED}.getModel().getValue()`)) === '// MINE\n', await ev(`${ED}.getModel().getValue()`));

  // ---- diagram zoom and Expand
  await ev(`location.hash = '#/${s.id}/code.md'`);
  await waitFor(`location.hash.endsWith('/code.md') && !!${q('#md .mmd svg')}`, 30000); await sleep(500);
  const nat = await ev(`${q('#md .mmd svg')}.viewBox.baseVal.width`);
  const w = () => ev(`Math.round(${q('#md .mmd svg')}.getBoundingClientRect().width)`);
  await ev(`${q('#md .mmd [data-z="in"]')}.click()`); await sleep(200);
  check('+ makes the diagram 125% of its own size', Math.abs((await w()) - nat * 1.25) <= 2, { w: await w(), nat });
  check('…and says so', (await ev(`${q('#md .mmd .mmd-zl')}.textContent`)) === '125%');
  await ev(`${q('#md .mmd [data-z="fit"]')}.click()`); await sleep(200);
  check('Fit goes back', (await ev(`${q('#md .mmd .mmd-zl')}.textContent`)) === 'Fit' && (await ev(`${q('#md .mmd svg')}.style.width`)) === '');
  await ev(`${q('#md .mmd [data-z="expand"]')}.click()`); await sleep(400);
  check('⤢ opens it full window', await ev(`!!${q('#mmdView')} && !${q('#mmdView')}.hidden && !!${q('#mmdView svg')}`));
  check('…titled with the heading above it', (await ev(`${q('#mmdView .mv-title')}.textContent`)) === 'Flow');
  const tf = () => ev(`(() => { const m = ${q('#mmdView .mv-stage')}.style.transform.match(/translate\\(([-\\d.]+)px, ([-\\d.]+)px\\) scale\\(([\\d.]+)\\)/); return m ? m.slice(1).map(Number) : null; })()`);
  const t0 = await tf();
  const [cx, cy] = await ev(`(() => { const r = ${q('#mmdView .mv-canvas')}.getBoundingClientRect(); return [r.left + r.width / 2, r.top + r.height / 2]; })()`);
  await send('Input.dispatchMouseEvent', { type: 'mouseWheel', x: cx, y: cy, deltaX: 0, deltaY: -400 }); await sleep(300);
  const t1 = await tf();
  check('the wheel zooms in', !!t0 && !!t1 && t1[2] > t0[2] * 1.2, { t0, t1 });
  await send('Input.dispatchMouseEvent', { type: 'mousePressed', x: cx, y: cy, button: 'left', clickCount: 1 });
  await send('Input.dispatchMouseEvent', { type: 'mouseMoved', x: cx + 120, y: cy + 60, button: 'left', buttons: 1 });
  await send('Input.dispatchMouseEvent', { type: 'mouseReleased', x: cx + 120, y: cy + 60, button: 'left', clickCount: 1 });
  await sleep(200);
  const t2 = await tf();
  check('dragging moves it', !!t2 && Math.abs(t2[0] - t1[0] - 120) < 2 && Math.abs(t2[1] - t1[1] - 60) < 2, { t1, t2 });
  await press('Escape'); await sleep(300);
  check('Esc closes it', await ev(`${q('#mmdView')}.hidden`));
});
