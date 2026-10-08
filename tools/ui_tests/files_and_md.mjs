// New folder, new .md, edit and save, side by side with the rendered page (8 Oct, Ravi: "what if
// i wanted to create new folder and .md files and edit and save?"). Before this, + only made Java
// problems, a folder could not be made on its own (and an empty one never showed), a new .md
// opened as a rendered page with no editor, and Edit took Source + Edit. Also right-click Rename
// (files and folders, unsaved edits move along) and a folder's Move to Recycle Bin.
// Everything happens in a scratch folder; the Recycle Bin call is recorded in the page, not sent.
import { existsSync, readFileSync, statSync } from 'node:fs';
import { join } from 'node:path';
import { suite } from './harness.mjs';

const JAVA = 'class A01_First {\n    static int f(int x) { return x + 1; }\n\n    public static void main(String[] a) {\n        System.out.println(f(1) + "   expected 2");\n    }\n}\n';

await suite(async t => {
  const { ev, waitFor, press, type, check, sleep, send, ED, BASE } = t;
  const q = sel => `document.querySelector(${JSON.stringify(sel)})`;
  const s = await t.scratchFolder({ 'README.md': '# Fixture\n\nread me\n', 'Topic/A01_First.java': JAVA, 'Topic/notes.md': '# Notes\n\nold text\n' });
  const disk = rel => { try { return readFileSync(join(s.dir, rel), 'utf8'); } catch { return null; } };
  const isDir = rel => { try { return statSync(join(s.dir, rel)).isDirectory(); } catch { return false; } };
  const diskIs = async (rel, text, ms = 4000) => { for (let t0 = Date.now(); Date.now() - t0 < ms; await sleep(100)) if (disk(rel) === text) return true; return false; };
  const inScratch = async () => {
    if (!(await ev('location.hash')).startsWith(`#/${s.id}/`)) throw new Error('STOP: the scratch folder is not the open one; nothing created');
  };
  const view = () => ev(`({ hash: location.hash, editor: !${q('#editor')}.hidden, md: !${q('#md')}.hidden, split: ${q('.stage')}.classList.contains('split'),
      badge: ${q('#badge')}.textContent, edit: !${q('#editBtn')}.hidden, save: !${q('#saveBtn')}.hidden, mdText: ${q('#md')}.innerText.slice(0, 200) })`);
  const kind = () => ev(`(${q('#npKind button.on')} || {}).dataset?.k`);
  const preview = () => ev(`${q('#npPreview')}.textContent`);
  const setVal = (sel, v) => ev(`(() => { const i = ${q(sel)}; i.value = ${JSON.stringify(v)}; i.dispatchEvent(new Event('input')); i.focus(); })()`);
  const rightClick = async sel => {
    const [x, y] = await ev(`(() => { const r = ${q(sel)}; r.scrollIntoView({ block: 'center' });
      const b = r.getBoundingClientRect(); return [b.left + 40, b.top + b.height / 2]; })()`);
    for (const type of ['mousePressed', 'mouseReleased'])
      await send('Input.dispatchMouseEvent', { type, x, y, button: 'right', clickCount: 1 });
    await sleep(250);
  };
  const items = () => ev(`[...document.querySelectorAll('#rowMenu button')].map(b => b.textContent.trim())`);
  const pick = async label => { await ev(`[...document.querySelectorAll('#rowMenu button')].find(b => b.textContent.trim() === ${JSON.stringify(label)}).click()`); await sleep(300); };

  await s.open('Topic/A01_First.java');

  // ---- + makes a problem, a file or a folder
  await ev(`${q('#newBtn')}.click()`); await sleep(300);
  const kinds = await ev(`[...document.querySelectorAll('#npKind button')].map(b => b.textContent.trim())`);
  check('+ offers Problem, File and Folder', JSON.stringify(kinds) === '["Problem","File","Folder"]', kinds);
  check('a fresh browser starts on Problem, the form as before', (await kind()) === 'problem' && await ev(`!${q('#npForm')}.hidden`));
  await ev(`${q('#npKind button[data-k="folder"]')}.click()`); await sleep(200);
  check('Folder: its name box has the focus, in the open file\'s folder',
    (await ev('document.activeElement.id')) === 'ndName' && (await ev(`${q('#npFolder')}.value`)) === 'Topic' && await ev(`${q('#npForm')}.hidden`),
    [await ev('document.activeElement.id'), await ev(`${q('#npFolder')}.value`)]);
  await setVal('#npFolder', '');
  await ev(`${q('#ndName')}.focus()`);
  await type('a:b');
  await press('Enter');
  await sleep(400);
  check('a name Windows refuses: the reason shows in the panel, which stays open',
    /does not allow :/.test(await preview()) && await ev(`${q('#npPreview')}.classList.contains('bad') && !${q('#newPanel')}.hidden`), await preview());
  await setVal('#ndName', 'Spring');
  check('the preview names the new folder', (await preview()) === '→ Spring/   (new folder)', await preview());
  await inScratch();
  await press('Enter');
  for (let i = 0; i < 40 && !isDir('Spring'); i++) await sleep(100);
  check('Create makes the folder on disk', isDir('Spring'));
  check('the list shows the empty folder', await waitFor(`!!${q('#tree .row.dir[data-dir="Spring"]')}`, 5000));
  check('its listing opens, offering New file here', await waitFor(`location.hash.endsWith('/Spring') && !!${q('#md .dirview [data-new="file"]')}`, 5000), await ev('location.hash'));

  // ---- New file here: the File tab, in that folder; no file type means .md
  await ev(`${q('#md .dirview [data-new="file"]')}.click()`); await sleep(300);
  check('New file here opens File, in Spring', (await kind()) === 'file' && (await ev(`${q('#npFolder')}.value`)) === 'Spring'
    && (await ev('document.activeElement.id')) === 'nfName', [await kind(), await ev(`${q('#npFolder')}.value`), await ev('document.activeElement.id')]);
  await type('beans');
  check('no file type typed: .md is added', (await preview()) === '→ Spring/beans.md', await preview());
  await inScratch();
  await press('Enter');
  check('the new .md opens in the editor', await waitFor(`location.hash.endsWith('/Spring/beans.md') && !${q('#editor')}.hidden && ${ED}.getModel().getValue() === '# beans\\n\\n'`, 8000), await view());
  await sleep(400);
  let v = await view();
  check('…in Edit mode, with the rendered page beside it', v.badge === 'Editing' && v.split && v.md && v.editor, v);
  check('…the caret under the heading, the editor focused', JSON.stringify(await t.pos()) === '[3,1]' && await ev(`${ED}.hasTextFocus()`), await t.pos());
  check('on disk: just the heading, LF', disk('Spring/beans.md') === '# beans\n\n', disk('Spring/beans.md'));

  // ---- typing shows beside it; Ctrl+S saves
  await s.onlyIn('Spring/beans.md', '# beans');
  await type('- singleton scope');
  check('the rendered side follows the typing', await waitFor(`${q('#md')}.innerText.includes('singleton scope')`, 1500), await view());
  await press('Ctrl+s');
  check('Ctrl+S writes it to the file', await diskIs('Spring/beans.md', '# beans\n\n- singleton scope'), disk('Spring/beans.md'));
  check('…and the badge is back to Editing', await waitFor(`${q('#badge')}.textContent === 'Editing'`, 2000), await view());

  // ---- the side-by-side switch is remembered
  await ev(`${q('#previewBtn')}.click()`); await sleep(300);
  v = await view();
  check('Preview off: the editor alone', !v.split && !v.md && v.editor, v);
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel() && !${q('#editor')}.hidden`, 30000); await sleep(900);
  v = await view();
  check('…still off after a reload (and still in source)', !v.split && v.editor && v.hash.endsWith('/Spring/beans.md'), v);
  await ev(`${q('#previewBtn')}.click()`); await sleep(300);
  check('Preview on again', (await view()).split);
  await ev(`${q('#editBtn')}.click()`); await sleep(200);      // a reload leaves files read-only, as everywhere
  check('Edit after the reload', (await view()).badge === 'Editing', await view());

  // ---- Rendered shows an unsaved edit, with Save
  await s.onlyIn('Spring/beans.md', 'singleton scope');
  await ev(`(() => { const e = ${ED}, m = e.getModel(), n = m.getLineCount(); e.executeEdits('t', [{ range: new monaco.Range(n, m.getLineMaxColumn(n), n, m.getLineMaxColumn(n)), text: ' and prototype' }]); })()`);
  await sleep(500);
  await ev(`${q('#srcBtn')}.click()`); await sleep(600);
  v = await view();
  check('Rendered shows the unsaved edit, not the disk text', v.md && !v.editor && v.mdText.includes('and prototype'), v);
  check('…says it is not saved, and offers Save', /not saved/.test(v.badge) && v.save, v);
  await ev(`${q('#saveBtn')}.click()`);
  check('Save from the rendered page writes it', await diskIs('Spring/beans.md', '# beans\n\n- singleton scope and prototype'), disk('Spring/beans.md'));

  // ---- a rendered .md: Edit is one click
  await ev(`location.hash = '#/${s.id}/README.md'`);
  await waitFor(`location.hash.endsWith('/README.md') && !${q('#md')}.hidden`, 5000); await sleep(500);
  v = await view();
  check('README opens rendered, with Edit on the bar', v.md && !v.editor && v.edit, v);
  await ev(`${q('#editBtn')}.click()`); await sleep(600);
  v = await view();
  check('one click on Edit: source, editable, with the rendered page beside it', v.editor && v.badge === 'Editing' && v.split, v);
  await ev(`location.hash = '#/${s.id}/Topic/notes.md'`);
  await waitFor(`location.hash.endsWith('/Topic/notes.md')`, 5000); await sleep(600);
  v = await view();
  check('another .md still opens rendered (Edit was for README only)', v.md && !v.editor && !v.split, v);

  // ---- right-click a folder: new here, rename, Recycle Bin
  await ev(`location.hash = '#/${s.id}/Spring/beans.md'`);
  await waitFor(`location.hash.endsWith('/Spring/beans.md') && !${q('#md')}.hidden`, 5000); await sleep(500);
  check('beans.md opens as last left: rendered', !(await view()).editor, await view());
  await ev(`${q('#editBtn')}.click()`);
  await waitFor(`!${q('#editor')}.hidden && ${q('#badge')}.textContent === 'Editing'`, 5000); await sleep(300);
  await s.onlyIn('Spring/beans.md', 'prototype');
  await ev(`(() => { const e = ${ED}, m = e.getModel(); e.executeEdits('t', [{ range: new monaco.Range(1, 1, 1, 1), text: 'UNSAVED ' }]); })()`);
  await sleep(500);
  const DIR = '#tree .row.dir[data-dir="Spring"]';
  await rightClick(DIR);
  let it = await items();
  check('a folder\'s menu starts with New file / New folder here', it[0] === 'New file here…' && it[1] === 'New folder here…', it);
  check('…and ends with Rename and Move to Recycle Bin', it.at(-2) === 'Rename…' && it.at(-1) === 'Move to Recycle Bin…', it);
  await pick('Rename…');
  check('Rename shows its box with the name selected', await ev(`!${q('#renamePanel')}.hidden && document.activeElement.id === 'rnName'
    && ${q('#rnName')}.value === 'Spring' && ${q('#rnName')}.selectionStart === 0 && ${q('#rnName')}.selectionEnd === 6`));
  await setVal('#rnName', 'SpringCore');
  await inScratch();
  await press('Enter');
  check('the folder is renamed on disk', await diskIs('SpringCore/beans.md', '# beans\n\n- singleton scope and prototype') && !existsSync(join(s.dir, 'Spring')));
  check('the list follows', await waitFor(`!!${q('#tree .row.dir[data-dir="SpringCore"]')} && !${q('#tree .row.dir[data-dir="Spring"]')}`, 5000));
  check('the open file follows, with its unsaved edit', await waitFor(`location.hash.endsWith('/SpringCore/beans.md') && ${ED}.getModel().getValue().startsWith('UNSAVED # beans')`, 5000), await view());
  check('…stored under the new name only', await ev(`localStorage.getItem('cv:draft:${s.id}/SpringCore/beans.md') !== null && localStorage.getItem('cv:draft:${s.id}/Spring/beans.md') === null`));

  // ---- right-click a file: rename with the name before the type selected
  await rightClick('#tree .row.file[data-path="SpringCore/beans.md"]');
  it = await items();
  check('a file\'s menu starts with New file in this folder and ends with Rename and Move to Recycle Bin',
    it[0] === 'New file in this folder…' && it.at(-2) === 'Rename…' && it.at(-1) === 'Move to Recycle Bin…', it);
  await pick('Rename…');
  check('…its box selects "beans", not ".md"', await ev(`${q('#rnName')}.value === 'beans.md' && ${q('#rnName')}.selectionStart === 0 && ${q('#rnName')}.selectionEnd === 5`));
  await type('scopes');
  await press('Enter');
  check('the file is renamed, its edit still unsaved', await waitFor(`location.hash.endsWith('/SpringCore/scopes.md') && ${ED}.getModel().getValue().startsWith('UNSAVED')`, 5000)
    && disk('SpringCore/scopes.md') === '# beans\n\n- singleton scope and prototype', await view());

  // ---- a folder to the Recycle Bin: a confirm bar first (the call is recorded, not sent)
  await ev(`(() => { window.__del = []; const real = window.fetch;
    window.fetch = (u, o) => /\\/api\\/delete$/.test(String(u)) ? (window.__del.push(JSON.parse(o.body)), Promise.resolve(new Response('{"ok":true}', { status: 200 }))) : real(u, o); })()`);
  await rightClick('#tree .row.dir[data-dir="SpringCore"]');
  await pick('Move to Recycle Bin…');
  const bar = await ev(`${q('#banner')}.hidden ? '' : ${q('#bannerText')}.textContent`);
  check('the bar names the folder, its file and the unsaved edit', /SpringCore/.test(bar) && /1 file\b/.test(bar) && /1 unsaved edit/.test(bar), bar);
  check('nothing is sent before the click', (await ev('window.__del.length')) === 0);
  await ev(`[...document.querySelectorAll('#bannerBtns button')].find(b => b.textContent === 'Move to Recycle Bin').click()`); await sleep(500);
  const del = await ev('window.__del');
  check('the click asks for that folder', del.length === 1 && del[0].path === 'SpringCore' && del[0].folder === true && del[0].root === s.id, del);

  // ---- right-click empty space in the list: new at the top level
  await ev(`(() => { const t = ${q('#tree')}, b = t.getBoundingClientRect();
    t.dispatchEvent(new MouseEvent('contextmenu', { bubbles: true, cancelable: true, clientX: b.left + 30, clientY: b.bottom - 8 })); })()`);
  await sleep(250);
  it = await items();
  check('empty space in the list: New file / New folder here', it[0] === 'New file here…' && it[1] === 'New folder here…' && !it.includes('Rename…'), it);
  await pick('New folder here…');
  check('…at the top level', (await kind()) === 'folder' && (await ev(`${q('#npFolder')}.value`)) === '', [await kind(), await ev(`${q('#npFolder')}.value`)]);
  await press('Escape');
  check('Escape closes the panel', await ev(`${q('#newPanel')}.hidden`));
});
