// Full screen hides the header, and with it the file-list button (6 Oct, Ravi: "add left pane
// toggle in fullscreen"). The file's bar then carries its own copy, next to Leave full screen.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, check, sleep, press, ED } = t;
  const q = s => `document.querySelector(${JSON.stringify(s)})`;
  const sideShown = () => ev(`!document.body.classList.contains('side-collapsed')`);
  const visible = sel => ev(`(() => { const b = ${q(sel)}; return !!b && !b.hidden && b.getBoundingClientRect().width > 0; })()`);
  await t.fresh(C06);
  if (!(await sideShown())) await ev(`${q('#sideBtn')}.click()`);

  check('outside full screen the bar has no second file-list button', !(await visible('#fullSideBtn')));
  await ev(`${q('#fullBtn')}.click()`); await sleep(400);
  check('full screen: the header is gone', await ev(`document.body.classList.contains('full')`) && !(await visible('#sideBtn')));
  check('…and the bar has the file-list button', await visible('#fullSideBtn'));
  await ev(`${q('#fullSideBtn')}.click()`); await sleep(200);
  check('it hides the file list', !(await sideShown()) && (await visible('#fullSideBtn')));
  check('…its look says the list is hidden', !(await ev(`${q('#fullSideBtn')}.classList.contains('on')`)));
  await ev(`${q('#fullSideBtn')}.click()`); await sleep(200);
  check('and shows it again', (await sideShown()) && (await ev(`${q('#fullSideBtn')}.classList.contains('on')`)));
  await ev(`${ED}.focus()`);
  await press('Ctrl+b');
  check('Ctrl+B and the button agree', !(await sideShown()) && !(await ev(`${q('#fullSideBtn')}.classList.contains('on')`)));
  await press('Ctrl+b');
  await ev(`${q('#fullExitBtn')}.click()`); await sleep(400);
  check('leaving full screen takes it away again', !(await visible('#fullSideBtn')) && (await visible('#sideBtn')));
});
