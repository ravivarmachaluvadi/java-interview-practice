// Mermaid diagrams and an Outline for .md files (8 Oct, Ravi: "mermaid diagrams and content index
// etc not there via code viewer"). Before this, 53 diagrams in 31 notes showed as their source
// text, there was no contents panel, and 14 in-page links broke: a heading with "&" got the id
// arrays-hashing where GitHub (and the notes' own contents lists) say arrays--hashing.
// Everything happens in a scratch folder; nothing is saved.
import { suite } from './harness.mjs';

const JAVA = 'class A01_First {\n    public static void main(String[] a) {\n        System.out.println(1 + "   expected 1");\n    }\n}\n';
const filler = n => Array.from({ length: n }, (_, i) => `Filler line ${i + 1}, long enough to wrap a little when the page is narrow.`).join('\n\n');
const DIAG = [
  '# Diagrams', '',
  '- [Arrays & Hashing](#arrays--hashing)',
  '- [The second Notes](#notes-1)', '',
  '```bash', '# not a heading', '```', '',
  filler(20), '',
  '## Arrays & Hashing', '',
  '```mermaid', 'flowchart TD', '    A[Start] --> B[End]', '```', '',
  filler(20), '',
  '## Broken', '',
  '```mermaid', 'flowchart TD', '    A[Start --> ', '```', '',
  filler(20), '',
  '## Notes', '', 'first notes', '',
  filler(20), '',
  '## Notes', '', 'second notes', '',
  filler(30), '',
].join('\n');
const lineOf = text => DIAG.split('\n').indexOf(text) + 1;

await suite(async t => {
  const { ev, waitFor, press, type, check, sleep, send, ED } = t;
  const q = sel => `document.querySelector(${JSON.stringify(sel)})`;
  const s = await t.scratchFolder({ 'README.md': '# Plain\n\nno diagrams here\n', 'diagrams.md': DIAG, 'A01_First.java': JAVA });
  const go = async (name, ready) => {
    await ev(`location.hash = '#/${s.id}/${name}'`);
    await waitFor(`location.hash.endsWith('/${name}') && (${ready})`, 15000);
    await sleep(400);
  };
  /* how far heading `id` is below the top of the rendered page's visible part (null: no such id).
     The page prefixes heading ids with mdh-, so a note's "## Output" is not the Run pane's #output. */
  const below = id => ev(`(() => { const h = document.getElementById(${JSON.stringify('mdh-' + id)}), md = ${q('#md')};
    return h && md.contains(h) ? Math.round(h.getBoundingClientRect().top - md.getBoundingClientRect().top) : null; })()`);
  const atTop = async id => { for (let i = 0; i < 30; i++) { const d = await below(id); if (d !== null && Math.abs(d) < 40) return true; await sleep(100); } return false; };
  const items = () => ev(`[...document.querySelectorAll('#outline .ol-item')].map(b => b.textContent.trim())`);
  const clickItem = async i => { await ev(`document.querySelectorAll('#outline .ol-item')[${i}].click()`); await sleep(400); };
  const current = () => ev(`[...document.querySelectorAll('#outline .ol-item')].findIndex(b => b.classList.contains('on'))`);
  const box = sel => ev(`(() => { const r = ${q(sel)}.getBoundingClientRect(); return [Math.round(r.left), Math.round(r.right)]; })()`);

  await s.open('A01_First.java');

  // ---- a page without a diagram does not pay for the library
  await go('README.md', `!${q('#md')}.hidden && ${q('#md')}.innerText.includes('no diagrams here')`);
  check('a .md without diagrams does not load the diagram library', await ev(`typeof window.mermaid === 'undefined'`));

  // ---- diagrams are drawn
  await go('diagrams.md', `!${q('#md')}.hidden && ${q('#md')}.innerText.includes('Filler line 1,')`);
  check('the diagram is drawn as a picture', await waitFor(`document.querySelectorAll('#md .mmd svg').length === 1`, 30000),
    await ev(`document.querySelectorAll('#md .mmd').length + ' boxes, ' + document.querySelectorAll('#md .mmd svg').length + ' svg'`));
  check('…with its labels in it', await ev(`(${q('#md .mmd svg')} || {}).textContent?.includes('Start') && ${q('#md .mmd svg')}.textContent.includes('End')`));
  check('the broken one shows the reason instead', await waitFor(`!!${q('#md .mmd-err')}`, 10000), await ev(`document.querySelectorAll('#md .mmd').length`));
  check('…and its source', await ev(`(${q('#md .mmd-err')} || {}).textContent?.includes('A[Start -->')`), await ev(`(${q('#md .mmd-err')} || {}).textContent`));
  check('nothing is left behind outside the page', await ev(`[...document.body.children].every(e => e.tagName.toLowerCase() !== 'svg' && !/cvm|mermaid/i.test(e.id || ''))`),
    await ev(`[...document.body.children].map(e => e.tagName + '#' + e.id).join(' ')`));
  check('other code blocks stay code', await waitFor(`!!${q('#md pre code')} && ${q('#md pre code')}.textContent.replace(/\u00a0/g, ' ').includes('# not a heading')`, 3000),
    await ev(`(${q('#md pre code')} || {}).textContent`));

  // ---- the theme button redraws it
  const before = await ev(`${q('#md .mmd svg')}.outerHTML`);
  await ev(`${q('#themeBtn')}.click()`);
  check('the theme button redraws the diagram in the other theme',
    await waitFor(`!!${q('#md .mmd svg')} && ${q('#md .mmd svg')}.outerHTML !== ${JSON.stringify(before)}`, 10000));
  await ev(`${q('#themeBtn')}.click()`);
  await waitFor(`!!${q('#md .mmd svg')}`, 10000);

  // ---- heading ids as GitHub makes them
  await ev(`${q('#md a[href="#arrays--hashing"]')}.click()`);
  check('"Arrays & Hashing" gets GitHub\'s id (two hyphens) and its link goes there', await atTop('arrays--hashing'), await below('arrays--hashing'));
  await ev(`${q('#md a[href="#notes-1"]')}.click()`);
  check('a repeated heading gets -1, and its link goes to the second one',
    await atTop('notes-1') && await ev(`document.getElementById('mdh-notes-1').nextElementSibling.textContent === 'second notes'`), await below('notes-1'));

  // ---- the outline
  check('Outline is on the bar for a .md', await ev(`!!${q('#outlineBtn')} && !${q('#outlineBtn')}.hidden`));
  check('…and closed until clicked', await ev(`!${q('#outline')} || ${q('#outline')}.hidden`));
  await ev(`${q('#outlineBtn')}.click()`); await sleep(400);
  check('a click opens it', await ev(`!!${q('#outline')} && !${q('#outline')}.hidden`));
  const it = await items();
  check('it lists the headings, not a # line inside a code block', JSON.stringify(it) === '["Diagrams","Arrays & Hashing","Broken","Notes","Notes"]', it);
  check('…a ## further in than the #', await ev(`(() => { const b = document.querySelectorAll('#outline .ol-item');
    return parseFloat(getComputedStyle(b[1]).paddingLeft) > parseFloat(getComputedStyle(b[0]).paddingLeft); })()`));
  let [mdL, mdR] = await box('#md'), [olL] = await box('#outline');
  check('it sits beside the page, not over it', mdR <= olL + 1, { mdR, olL });
  await clickItem(2);
  check('clicking an entry scrolls the page to that heading', await atTop('broken'), await below('broken'));
  check('…and marks it as where you are', await waitFor(`document.querySelectorAll('#outline .ol-item')[2].classList.contains('on')`, 2000), await current());
  await ev(`${q('#md')}.scrollTop = ${q('#md')}.scrollHeight`);
  check('scrolled to the end: the last section is marked', await waitFor(`[...document.querySelectorAll('#outline .ol-item')].at(-1).classList.contains('on')`, 2000), await current());

  // ---- the outline with the source
  await ev(`${q('#srcBtn')}.click()`);
  await waitFor(`!${q('#editor')}.hidden && !!${ED}.getModel() && ${q('.stage')}.classList.contains('split')`, 8000); await sleep(500);
  check('Source: the outline stays open', await ev(`!${q('#outline')}.hidden`));
  let [edL, edR] = await box('#editor'); [mdL, mdR] = await box('#md'); [olL] = await box('#outline');
  check('…source, page and outline side by side, none over another', edR <= mdL + 1 && mdR <= olL + 1, { edR, mdL, mdR, olL });
  await clickItem(2);
  const BROKEN = lineOf('## Broken');
  check('clicking an entry puts the caret on that heading\'s line', (await t.pos())[0] === BROKEN, [await t.pos(), BROKEN]);
  const fv = await t.firstVisible();
  check('…near the top of the editor', fv <= BROKEN && BROKEN - fv <= 4, [fv, BROKEN]);
  check('…and the page beside it goes there too', await atTop('broken'), await below('broken'));
  await ev(`${q('#previewBtn')}.click()`); await sleep(400);
  [edL, edR] = await box('#editor'); [olL] = await box('#outline');
  check('Preview off: the editor ends where the outline starts', !(await ev(`${q('.stage')}.classList.contains('split')`)) && edR <= olL + 1, { edR, olL });
  await clickItem(4);
  const NOTES2 = DIAG.split('\n').lastIndexOf('## Notes') + 1;
  check('the second "Notes" goes to the second one\'s line', (await t.pos())[0] === NOTES2, [await t.pos(), NOTES2]);
  check('…and is marked as where you are', await waitFor(`document.querySelectorAll('#outline .ol-item')[4].classList.contains('on')`, 2000), await current());
  await ev(`${q('#previewBtn')}.click()`); await sleep(400);

  // ---- typing in a diagram, side by side
  await ev(`${q('#editBtn')}.click()`);
  await waitFor(`${q('#badge')}.textContent === 'Editing' && ${q('.stage')}.classList.contains('split')`, 5000);
  await s.onlyIn('diagrams.md', '# Diagrams');
  await clickItem(1);                                        // the diagram's heading at the top of both sides
  await waitFor(`!!${q('#md .mmd svg')}`, 10000); await sleep(300);
  const AB = lineOf('    A[Start] --> B[End]');
  await ev(`(() => { const e = ${ED}; e.setPosition({ lineNumber: ${AB}, column: e.getModel().getLineMaxColumn(${AB}) }); e.focus(); })()`);
  const top0 = await ev(`${q('#md')}.scrollTop`);
  await ev(`(() => { window.__bare = 0; window.__iv = setInterval(() => { const b = document.querySelector('#md .mmd'); if (!b || !b.querySelector('svg')) window.__bare++; }, 20); })()`);
  await press('Enter');
  await type('B --> C[Added]');
  check('the picture follows the typing', await waitFor(`!!${q('#md .mmd svg')} && ${q('#md .mmd svg')}.textContent.includes('Added')`, 10000),
    await ev(`(${q('#md .mmd')} || {}).innerText?.slice(0, 200)`));
  const bare = await ev('(clearInterval(window.__iv), window.__bare)');
  check('…without flashing back to source text while half-typed', bare === 0, bare);
  const top1 = await ev(`${q('#md')}.scrollTop`);
  check('…and the page beside it does not jump', top0 > 0 && Math.abs(top1 - top0) < 60, { top0, top1 });

  // ---- remembered; Alt+O; only for .md
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && location.hash.endsWith('/diagrams.md')`, 30000); await sleep(1200);
  check('the outline is still open after a reload', await waitFor(`!!${q('#outline')} && !${q('#outline')}.hidden`, 5000));
  await ev(`document.activeElement && document.activeElement.blur && document.activeElement.blur()`);
  await press('Alt+o');
  check('Alt+O closes it', await waitFor(`${q('#outline')}.hidden`, 2000));
  const [stL, stR] = await box('.stage'); [mdL, mdR] = await box('#md');
  check('…and the page takes the room back', Math.abs(mdR - stR) <= 1, { mdR, stR });
  await press('Alt+o');
  check('Alt+O opens it again', await waitFor(`!${q('#outline')}.hidden`, 2000));
  await go('A01_First.java', `!${q('#editor')}.hidden`);
  check('a Java file has no Outline button and no outline', await ev(`${q('#outlineBtn')}.hidden && ${q('#outline')}.hidden`));
  [edL, edR] = await box('#editor');
  check('…and its editor has the whole width', Math.abs(edR - stR) <= 1, { edR, stR });
});
