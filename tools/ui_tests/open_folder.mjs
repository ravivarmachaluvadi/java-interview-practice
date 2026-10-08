// A folder opened from File Explorer's right-click "Open in Code Viewer" (7 Oct): the server's
// --open turns the path into #/<folder id>/<path>, and for a folder the page shows a listing -
// its subfolders and files with their marks - with the tree jumped to it. A path the tree does
// not have (a .png) shows the folder it is in. Runs on a scratch folder; nothing is saved.
import { basename } from 'node:path';
import { suite } from './harness.mjs';

const JAVA = name => `class ${name} {\n    static int f(int x) { return x + 1; }\n\n    public static void main(String[] a) {\n        System.out.println(f(1) + "   expected 2");\n    }\n}\n`;

await suite(async t => {
  const { ev, check, sleep, send, waitFor, press, BASE } = t;
  const s = await t.scratchFolder({
    'README.md': '# Scratch\n',
    'Topic/A01_First.java': JAVA('A01_First'),
    'Topic/B02_Second.java': JAVA('B02_Second'),
    'Topic/notes.md': '# Notes\n',
    'Topic/pic.png': 'not a picture',
    'Topic/Sub/C03_Deep.java': JAVA('C03_Deep'),
  });
  const post = (path, body) => fetch(BASE + path, { method: 'POST', body: JSON.stringify(body),
    headers: { 'Content-Type': 'application/json', 'X-CodeView': '1' } }).then(r => r.json());
  await post('/api/progress', { root: s.id, path: 'Topic/A01_First.java', status: 'done' });

  const listing = `!!document.querySelector('#md:not([hidden]) .dirview')`;
  const load = async hash => {
    await send('Page.navigate', { url: `${BASE}/?load=${Date.now()}#${hash}` });
    await waitFor(`typeof monaco !== 'undefined' && !!${t.ED} && document.title.endsWith('Code Viewer') && ${listing}`, 15000);
    await sleep(500);
  };
  const rows = () => ev(`[...document.querySelectorAll('#md .dirview .row')].map(r => r.querySelector('.nm').textContent)`);
  const shown = sel => ev(`(() => { const e = document.querySelector(${JSON.stringify(sel)}); return !!e && !e.hidden && e.offsetParent !== null; })()`);

  await load(`/${s.id}/Topic`);
  check('a folder address shows its listing', await ev(listing));
  check('its title is the folder name', (await ev(`document.querySelector('#md .dirview h1').textContent`)) === 'Topic');
  check('subfolders first, then the files the tree has (no pic.png)',
    JSON.stringify(await rows()) === JSON.stringify(['..', 'Sub', 'First', 'Second', 'notes.md']), await rows());
  check('a done file carries its ✓', await ev(`!!document.querySelector('#md .dirview .row[href$="A01_First.java"] .st.done')`));
  check('the summary counts practice files done, subfolders included',
    /1 of 3 practice files done/.test(await ev(`document.querySelector('#md .dv-sum').textContent`)),
    await ev(`document.querySelector('#md .dv-sum').textContent`));
  check('the tree row of the folder is highlighted and open',
    await ev(`(() => { const r = document.querySelector('#tree .row.dir[data-dir="Topic"]'); return !!r && r.classList.contains('active') && r.classList.contains('open'); })()`));
  check('the bar says Folder', (await ev(`document.querySelector('#badge').textContent`)) === 'Folder');
  for (const id of ['editBtn', 'progBtn', 'copyBtn', 'practiceBtn', 'tryBtn', 'saveBtn'])
    check(`no ${id} on a folder`, !(await shown('#' + id)));
  check('the path bar names the folder', (await ev(`document.querySelector('#crumbs b').textContent`)) === 'Topic');

  // keys that act on a file do nothing to a folder
  await press('Alt+m');
  await press('Ctrl+e');
  const prog = await (await fetch(`${BASE}/api/progress?root=${s.id}`)).json();
  check('Alt+M marks nothing for a folder', !('Topic' in prog), prog);
  check('Ctrl+E leaves it a folder listing', (await ev(`document.querySelector('#badge').textContent`)) === 'Folder' && await ev(listing));

  // into a subfolder, then a file
  await ev(`document.querySelector('#md .dirview .row[href$="/Topic/Sub"]').click()`);
  await waitFor(`location.hash === '#/${s.id}/Topic/Sub' && document.querySelector('#md .dirview h1').textContent === 'Sub'`, 5000);
  check('a subfolder row opens its listing', (await ev('location.hash')) === `#/${s.id}/Topic/Sub`);
  check('.. goes up to the folder above', await ev(`!!document.querySelector('#md .dirview .row.up[href$="/Topic"]')`));
  await ev(`document.querySelector('#md .dirview .row[href$="C03_Deep.java"]').click()`);
  await waitFor(`location.hash.endsWith('C03_Deep.java') && !document.querySelector('#editor').hidden`, 8000);
  check('a file row opens the file', (await ev('location.hash')).endsWith('C03_Deep.java') && !(await ev(`document.querySelector('#editor').hidden`)));
  check('the bar is a file\'s again', (await ev(`document.querySelector('#badge').textContent`)) !== 'Folder' && await shown('#progBtn'));

  // the folder itself, with nothing after its id
  await load(`/${s.id}/`);
  check('#/<id>/ lists the whole folder', (await ev(`document.querySelector('#md .dirview h1').textContent`)) === basename(s.dir), await ev('location.hash'));
  check('...with no .. row at the top', JSON.stringify(await rows()) === JSON.stringify(['Topic', 'README.md']), await rows());

  // a file the tree does not have: its folder, and a word why
  await load(`/${s.id}/Topic/pic.png`);
  check('a .png shows the folder it is in', (await ev(`document.querySelector('#md .dirview h1').textContent`)) === 'Topic');
  check('...and says why', /pic\.png/.test(await ev(`document.querySelector('#toast') ? document.querySelector('#toast').textContent : ''`)),
    await ev(`document.querySelector('#toast') ? document.querySelector('#toast').textContent : ''`));

  // Next from a folder starts in that folder (tree order: its subfolders first)
  await load(`/${s.id}/Topic`);
  await press('Alt+j');
  await waitFor(`location.hash.endsWith('.java')`, 8000);
  check('Next from a folder opens its first not-done Java file', (await ev('location.hash')).endsWith('Topic/Sub/C03_Deep.java'), await ev('location.hash'));

  // fits Ravi's window (1600 x 817 at 125%) and a narrow one, file list open
  for (const [w, h] of [[1600, 817], [1100, 700]]) {
    await send('Emulation.setDeviceMetricsOverride', { width: w, height: h, deviceScaleFactor: 1, mobile: false });
    await load(`/${s.id}/Topic`);
    await ev(`document.body.classList.contains('side-collapsed') && document.querySelector('#sideBtn').click()`);
    await sleep(300);
    const m = await ev(`(() => { const md = document.querySelector('#md'), list = md.querySelector('.dv-list');
      return { fits: md.scrollWidth <= md.clientWidth + 1, listW: Math.round(list.getBoundingClientRect().width), mdW: md.clientWidth }; })()`);
    check(`${w}x${h}, file list open: the listing fits without a sideways scroll`, m.fits, m);
    if (process.env.CV_SHOTS) {
      const shot = await send('Page.captureScreenshot', { format: 'png' });
      (await import('node:fs')).writeFileSync(`${process.env.CV_SHOTS}/open_folder_${w}.png`, Buffer.from(shot.result.data, 'base64'));
    }
  }
  await send('Emulation.clearDeviceMetricsOverride');
});
