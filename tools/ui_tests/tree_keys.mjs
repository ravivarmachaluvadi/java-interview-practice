// ↑ / ↓ in the file list (7 Oct, Ravi's screenshot): a click left the focus on the row and the
// arrows only scrolled the list. Now they move along the rows and the file landed on opens - once
// the key rests, so a held key opens only the last one. → / ← open and close a folder, and step
// into it or up to it; Enter on a folder opens or closes it. Runs on a scratch folder.
import { suite } from './harness.mjs';

await suite(async t => {
  const { ev, check, sleep, send, waitFor, press, ED, BASE } = t;
  const s = await t.scratchFolder({
    'README.md': '# Readme\n',
    'Topic/a.md': '# A\n', 'Topic/b.md': '# B\n', 'Topic/c.md': '# C\n',
    'Zed/d.md': '# D\n',
  });
  const opened = (path, head) => `location.hash === ${JSON.stringify(`#/${s.id}/${path}`)}
    && (document.querySelector('#md:not([hidden]) h1') || {}).textContent === ${JSON.stringify(head)}`;
  const hash = () => ev('location.hash');
  /* the focused row: a file's path, 'dir:<path>' for a folder, else the tag name */
  const focused = () => ev(`(() => { const a = document.activeElement;
    return !a ? null : a.dataset.path !== undefined ? a.dataset.path : a.dataset.dir !== undefined ? 'dir:' + a.dataset.dir : a.tagName; })()`);
  /* the row after (or before) a row, in the list as it shows now, in the same form */
  const beside = (sel, step) => ev(`(() => { const rows = [...document.querySelectorAll('#tree .row.file, #tree .row.dir')].filter(r => r.offsetParent !== null);
    const r = rows[rows.indexOf(document.querySelector(${JSON.stringify(sel)})) + ${step}];
    return !r ? null : r.dataset.path !== undefined ? r.dataset.path : 'dir:' + r.dataset.dir; })()`);
  const isOpen = dir => ev(`document.querySelector('#tree .row.dir[data-dir="${dir}"]').classList.contains('open')`);
  /* a key pressed and let go with no pause after it, as a held key repeats */
  const tap = async key => {
    for (const type of ['rawKeyDown', 'keyUp']) await send('Input.dispatchKeyEvent', { type, key, code: key, windowsVirtualKeyCode: { ArrowDown: 40, ArrowUp: 38 }[key] });
  };
  const A = '#tree .row.file[data-path="Topic/a.md"]', C = '#tree .row.file[data-path="Topic/c.md"]';

  await send('Page.navigate', { url: `${BASE}/?load=${Date.now()}#/${s.id}/Topic/a.md` });
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && ${opened('Topic/a.md', 'A')}`, 30000);
  await sleep(700);

  // Ravi's case: a click on a file, then ↓ and ↑
  const [x, y] = await ev(`(() => { const r = document.querySelector(${JSON.stringify(A)}); r.scrollIntoView({ block: 'center' });
    const b = r.getBoundingClientRect(); return [b.left + 60, b.top + b.height / 2]; })()`);
  await t.click(x, y);
  check('(a click on a.md leaves the focus on its row)', (await focused()) === 'Topic/a.md', await focused());
  await press('ArrowDown');
  check('↓ after a click opens the next file, b.md', await waitFor(opened('Topic/b.md', 'B'), 3000), await hash());
  check('...and the focus moves to its row', (await focused()) === 'Topic/b.md', await focused());
  check('...which is the highlighted one', await ev(`document.querySelector('#tree .row.file[data-path="Topic/b.md"]').classList.contains('active')`));
  await press('ArrowUp');
  check('↑ opens the file above, a.md again', await waitFor(opened('Topic/a.md', 'A'), 3000), await hash());

  // a held key: only the file it stops on opens
  await ev(`(() => { window.__files = []; const real = window.fetch;
    window.fetch = (u, o) => { if (/\\/api\\/file\\?/.test(String(u))) window.__files.push(decodeURIComponent(String(u))); return real(u, o); }; return true; })()`);
  await tap('ArrowDown'); await tap('ArrowDown');
  check('two quick ↓ open c.md', await waitFor(opened('Topic/c.md', 'C'), 3000), await hash());
  await sleep(400);
  const files = await ev('window.__files');
  check('...and b.md, passed over, was never loaded', !files.some(u => u.includes('path=Topic/b.md')), files);

  // onto a folder: it is only highlighted
  const next = await beside(C, 1);
  check('(the row after c.md is the Zed folder)', next === 'dir:Zed', next);
  await press('ArrowDown'); await sleep(400);
  check('↓ onto a folder puts the focus on it', (await focused()) === 'dir:Zed', await focused());
  check('...and opens nothing: c.md stays', (await hash()) === `#/${s.id}/Topic/c.md`, await hash());

  // → / ← and Enter on a folder
  await press('ArrowRight');
  check('→ on a closed folder opens it', await isOpen('Zed'));
  check('...and the focus stays on it', (await focused()) === 'dir:Zed', await focused());
  await press('ArrowRight');
  check('→ on an open folder goes to its first file, which opens', await waitFor(opened('Zed/d.md', 'D'), 3000), await hash());
  check('...with the focus on it', (await focused()) === 'Zed/d.md', await focused());
  await press('ArrowLeft'); await sleep(300);
  check('← on a file goes up to its folder', (await focused()) === 'dir:Zed', await focused());
  check('...and d.md stays open', (await hash()) === `#/${s.id}/Zed/d.md`, await hash());
  await press('ArrowLeft');
  check('← on an open folder closes it', !(await isOpen('Zed')));
  await press('Enter');
  check('Enter on a folder opens it', await isOpen('Zed'));

  // the list is drawn again (Alt+M, Show, files changed on disk): the keys go on working
  await ev(`(() => { const s = document.querySelector('#showSel'); s.dispatchEvent(new Event('change')); return true; })()`);
  await sleep(300);
  check('after the list is drawn again the focus is still on Zed', (await focused()) === 'dir:Zed', await focused());
  await press('ArrowUp');
  check('...and ↑ goes on: c.md opens', await waitFor(opened('Topic/c.md', 'C'), 3000), await hash());

  // in the editor the arrows are the editor's
  await ev(`location.hash = '#/scratch'`);
  await waitFor(`!document.querySelector('#editor').hidden && !!${ED}.getModel()`, 8000);
  await sleep(500);
  await ev(`${ED}.setPosition({ lineNumber: 1, column: 1 }); ${ED}.focus()`);
  await press('ArrowDown'); await sleep(500);
  check('in the editor ↓ moves the caret', (await t.pos())[0] === 2, await t.pos());
  check('...and opens no file', (await hash()) === '#/scratch', await hash());
});
