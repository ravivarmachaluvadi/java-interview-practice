// The UI practice pages in AAScratches/11-UI-Practice (12 Oct): each one, opened as committed,
// passes its own <script type="test"> block with Ctrl+Enter (4 checks or more, no error), and
// its Practice copy - the page's functions hidden - does NOT pass, which proves the checks test
// the hidden code. Read-only: the files are opened, never edited or saved.
import { readdirSync } from 'node:fs';
import { join } from 'node:path';
import { suite, REPO } from './harness.mjs';

const DIR = 'AAScratches/11-UI-Practice/01-Components';
const NEW = ['A02_StarRating.html', 'A03_Accordion.html', 'B02_Pagination.html', 'B03_OtpInput.html',
  'B04_NestedCheckboxes.html', 'B05_Carousel.html', 'C02_ProgressBarQueue.html', 'C03_FileExplorer.html'];

await suite(async t => {
  const { ev, waitFor, press, check, sleep } = t;
  const files = readdirSync(join(REPO, DIR)).filter(f => f.endsWith('.html')).sort();
  for (const name of NEW) check(`${name} is there`, files.includes(name), files);

  const runAndWait = async () => {
    await press('Ctrl+Enter');
    await sleep(300);
    await waitFor(`!document.querySelector('#runBtn').disabled && !/Running/.test(document.querySelector('#status').textContent)`, 30000);
    return ev(`({ verdict: document.querySelector('#verdict').hidden ? '' : document.querySelector('#verdict').textContent,
      status: document.querySelector('#status').textContent, out: document.querySelector('#output').textContent })`);
  };
  const practice = () => ev(`document.querySelector('#badge').textContent.startsWith('Practice')`);

  // B03_OtpInput focuses its first box as it loads: that used to take the keyboard from the
  // editor on every redraw, so typing and Ctrl+Enter went into the page (found 12 Oct)
  const inFrame = () => ev(`document.activeElement === document.querySelector('#uiFrame')`);
  for (const name of files) {
    await t.open(`${DIR}/${name}`);
    await waitFor(`!document.querySelector('#uiWrap').hidden`, 8000);
    if (await practice()) { await ev(`document.querySelector('#practiceBtn').click()`); await sleep(800); }
    await ev(`${t.ED}.focus()`);
    await ev(`document.querySelector('#uiReload').click()`);
    await sleep(1500);
    check(`${name}: the keyboard stays in the editor while the page loads`, !(await inFrame()));
    let r = await runAndWait();
    const m = /(\d+)\/(\d+) match/.exec(r.verdict);
    check(`${name}: its tests pass as committed`, m && m[1] === m[2] && +m[1] >= 4 && !/error/i.test(r.status), r);

    await ev(`document.querySelector('#practiceBtn').click()`);
    await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 8000);
    const skel = await ev(`${t.ED}.getModel().getValue()`);
    await ev(`${t.ED}.focus()`);
    r = await runAndWait();
    const ran = /Ran|error|timed out/.test(r.status);
    check(`${name}: its Practice copy runs and does not pass`,
      skel.includes('TODO: your solution') && ran && !(m && r.verdict.includes(`${m[1]}/${m[1]} match`)), r);
    await ev(`document.querySelector('#practiceBtn').click()`);
    await sleep(600);
  }
});
