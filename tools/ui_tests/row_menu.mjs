// Right-click in the file list (7 Oct, Ravi's screenshots): Chrome's own link menu used to show.
// Now Code Viewer's menu shows, and "Open in browser" asks the server to open the file, or the
// folder's "Index of" page, as a file:/// tab (a page served from http may not open file:///).
// fetch, window.open and the clipboard are recorded in the page, so no browser, tab or Explorer
// window starts and the real clipboard is untouched. Runs on a scratch folder.
import { join } from 'node:path';
import { pathToFileURL } from 'node:url';
import { suite } from './harness.mjs';

const JAVA = 'class A01_First {\n    static int f(int x) { return x + 1; }\n\n    public static void main(String[] a) {\n        System.out.println(f(1) + "   expected 2");\n    }\n}\n';
// 8 Oct: new here at the top, Rename and Recycle Bin at the bottom (files_and_md.mjs tests those)
const FILE_ITEMS = ['New file in this folder…', 'Open in browser', 'Open its folder in browser', 'Open in a new Code Viewer tab', 'Show in File Explorer', 'Copy path', 'Rename…', 'Move to Recycle Bin…'];
const DIR_ITEMS = ['New file here…', 'New folder here…', 'Open in browser', 'Show in File Explorer', 'Copy path', 'Rename…', 'Move to Recycle Bin…'];

await suite(async t => {
  const { ev, check, sleep, send, press, BASE } = t;
  const s = await t.scratchFolder({ 'README.md': '# R\n', 'Topic/A01_First.java': JAVA, 'Topic/notes.md': '# Notes\n' });
  const rootPath = (await (await fetch(BASE + '/api/roots')).json()).find(r => r.id === s.id).path;

  const record = () => ev(`(() => {
    window.__posts = []; window.__opened = []; window.__copied = [];
    const real = window.fetch;
    window.fetch = (u, o) => /\\/api\\/(open-local|reveal)$/.test(String(u))
      ? (window.__posts.push([String(u), JSON.parse(o.body)]), Promise.resolve(new Response('{"ok":true,"url":"file:///x"}', { status: 200 })))
      : real(u, o);
    window.open = u => { window.__opened.push(String(u)); return null; };
    navigator.clipboard.writeText = x => { window.__copied.push(x); return Promise.resolve(); };
    return true; })()`);
  const rightClick = async (sel, shift = false) => {
    const [x, y] = await ev(`(() => { const r = document.querySelector(${JSON.stringify(sel)}); r.scrollIntoView({ block: 'center' });
      const b = r.getBoundingClientRect(); return [b.left + 40, b.top + b.height / 2]; })()`);
    for (const type of ['mousePressed', 'mouseReleased'])
      await send('Input.dispatchMouseEvent', { type, x, y, button: 'right', clickCount: 1, modifiers: shift ? 8 : 0 });
    await sleep(250);
  };
  const menuShown = () => ev(`!document.querySelector('#rowMenu').hidden`);
  const items = () => ev(`[...document.querySelectorAll('#rowMenu button')].map(b => b.textContent.trim())`);
  const pick = async label => { await ev(`[...document.querySelectorAll('#rowMenu button')].find(b => b.textContent.trim() === ${JSON.stringify(label)}).click()`); await sleep(250); };
  const posts = () => ev('window.__posts.splice(0)');

  await s.open('Topic/A01_First.java');
  await record();
  const FILE = `#tree .row.file[data-path="Topic/A01_First.java"]`, DIR = `#tree .row.dir[data-dir="Topic"]`;

  // a file row
  await rightClick(FILE);
  check('right-click on a file shows Code Viewer\'s menu', await menuShown());
  check('...with the file items in order', JSON.stringify(await items()) === JSON.stringify(FILE_ITEMS), await items());
  if (process.env.CV_SHOTS) {
    const shot = await send('Page.captureScreenshot', { format: 'png' });
    (await import('node:fs')).writeFileSync(`${process.env.CV_SHOTS}/row_menu.png`, Buffer.from(shot.result.data, 'base64'));
  }
  await pick('Open in browser');
  let p = await posts();
  check('Open in browser asks the server for the file itself',
    p.length === 1 && p[0][0].endsWith('/api/open-local') && p[0][1].root === s.id && p[0][1].path === 'Topic/A01_First.java' && p[0][1].browser === 'chrome', p);
  check('...and the menu closes', !(await menuShown()));
  await rightClick(FILE);
  await pick('Open its folder in browser');
  p = await posts();
  check('Open its folder in browser asks for the folder', p.length === 1 && p[0][1].path === 'Topic', p);
  await rightClick(FILE);
  await pick('Open in a new Code Viewer tab');
  const opened = await ev('window.__opened');
  check('a new Code Viewer tab on that file', opened.length === 1 && opened[0].endsWith(`#/${s.id}/Topic/A01_First.java`), opened);
  await rightClick(FILE);
  await pick('Show in File Explorer');
  p = await posts();
  check('Show in File Explorer uses reveal on that file', p.length === 1 && p[0][0].endsWith('/api/reveal') && p[0][1].path === 'Topic/A01_First.java', p);
  await rightClick(FILE);
  await pick('Copy path');
  const copied = await ev('window.__copied');
  check('Copy path copies the Windows path', copied[0] === rootPath + '\\Topic\\A01_First.java', [copied, rootPath]);

  // a folder row
  await rightClick(DIR);
  check('right-click on a folder shows its items', JSON.stringify(await items()) === JSON.stringify(DIR_ITEMS), await items());
  await pick('Open in browser');
  p = await posts();
  check('Open in browser on a folder asks for that folder', p.length === 1 && p[0][1].path === 'Topic', p);
  check('the folder did not fold or unfold', await ev(`document.querySelector(${JSON.stringify(DIR)}).classList.contains('open')`));

  // closing, and the browser's own menu
  await rightClick(FILE);
  await press('Escape');
  check('Escape closes it', !(await menuShown()));
  await rightClick(FILE);
  const [ex, ey] = await ev(`(() => { const b = document.querySelector('#editor').getBoundingClientRect(); return [b.left + b.width / 2, b.top + 80]; })()`);
  await t.click(ex, ey);
  check('a click elsewhere closes it', !(await menuShown()));
  await rightClick(FILE, true);
  check('Shift+right-click leaves the browser\'s own menu (no Code Viewer menu)', !(await menuShown()));
  await ev(`document.querySelector(${JSON.stringify(FILE)}).dispatchEvent(new MouseEvent('contextmenu', { bubbles: true, cancelable: true, clientX: innerWidth - 3, clientY: innerHeight - 3 }))`);
  const box = await ev(`(() => { const b = document.querySelector('#rowMenu').getBoundingClientRect(); return { l: b.left, t: b.top, r: b.right, b: b.bottom, w: innerWidth, h: innerHeight }; })()`);
  check('near the bottom-right corner the menu stays inside the window', box.l >= 0 && box.t >= 0 && box.r <= box.w && box.b <= box.h, box);
  await press('Escape');
  check('the Scratch pad row keeps the browser\'s menu', await ev(`(() => { const e = new MouseEvent('contextmenu', { bubbles: true, cancelable: true });
    document.querySelector('#tree > a.row.file:first-child').dispatchEvent(e); return !e.defaultPrevented; })()`));

  // the folder page's rows have it too
  await send('Page.navigate', { url: `${BASE}/?load=${Date.now()}#/${s.id}/Topic` });
  await t.waitFor(`!!document.querySelector('#md:not([hidden]) .dirview')`, 15000);
  await sleep(500);
  await record();
  await rightClick(`#md .dirview .row[href$="notes.md"]`);
  check('a file on the folder page has the same menu', JSON.stringify(await items()) === JSON.stringify(FILE_ITEMS), await items());
  await pick('Open in browser');
  p = await posts();
  check('...and opens that file', p.length === 1 && p[0][1].path === 'Topic/notes.md', p);

  // what Chrome itself shows at these addresses (the real thing, no server involved)
  const show = async (path, dir) => {
    await send('Page.navigate', { url: pathToFileURL(path).href + (dir ? '/' : '') });
    await sleep(900);
    return ev(`({ type: document.contentType, title: document.title, text: document.body ? document.body.innerText.slice(0, 200) : '' })`);
  };
  let d = await show(s.dir, true);
  check('Chrome shows a folder address as its "Index of" page', /^Index of/.test(d.title) && d.text.includes('Topic'), d);
  d = await show(join(s.dir, 'Topic', 'A01_First.java'));
  check('...a .java file as text, not a download', d.type === 'text/plain' && d.text.includes('class A01_First'), d);
  d = await show(join(s.dir, 'Topic', 'notes.md'));
  // measured: Chrome calls it text/markdown and shows it (a Markdown viewer extension renders it)
  check('...and a .md file as text', d.type.startsWith('text/') && d.text.includes('# Notes'), d);
});
