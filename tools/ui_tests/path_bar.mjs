// The path above the editor keeps the file name visible: 6 Oct, on Ravi's laptop with the file
// list open, it read "AAScratches / 01-DSA / 09-Trees-BST ..." - the bar is full of buttons and the
// path was cut from its end, so the file name went first. Now the folders give way first.
import { suite } from './harness.mjs';

const FILES = ['AAScratches/01-DSA/09-Trees-BST/B09_DiameterOfBinaryTree.java',
  'AAScratches/01-DSA/09-Trees-BST/A06_PrePostInorderInOneTraversal.java'];

await suite(async t => {
  const { ev, check, sleep, send } = t;
  // Ravi's window (1600 x 817 at 125%) and two narrower ones, file list open
  for (const [w, h] of [[1600, 817], [1366, 768], [1100, 700]]) {
    await send('Emulation.setDeviceMetricsOverride', { width: w, height: h, deviceScaleFactor: 1, mobile: false });
    for (const f of FILES) {
      await t.open(f);
      await ev(`document.body.classList.contains('side-collapsed') && document.querySelector('#sideBtn').click()`);
      await sleep(300);
      const m = await ev(`(() => { const c = document.querySelector('#crumbs'), b = c.querySelector('b');
        const cr = c.getBoundingClientRect(), br = b.getBoundingClientRect();
        const d = c.querySelector('.dirs'), dw = d ? d.getBoundingClientRect().width : 0;
        return { name: b.textContent, shown: br.right <= cr.right + 0.5 && br.left >= cr.left - 0.5 && b.scrollWidth <= b.clientWidth + 0.5,
                 starts: br.left >= cr.left - 0.5, dirs: Math.round(dw), crumbs: Math.round(cr.width), nameW: Math.round(br.width),
                 side: !document.body.classList.contains('side-collapsed') }; })()`);
      if (w >= 1366) check(`${w}x${h}, file list open: ${f.split('/').pop()} is shown whole`, m.side && m.shown, m);
      // too narrow for the whole name: the folders are gone and the name has all the room, from its start
      else check(`${w}x${h}, file list open: ${f.split('/').pop()} is shown whole, or gets all the room`,
        m.side && (m.shown || (m.dirs === 0 && m.starts && m.nameW >= m.crumbs - 1)), m);
    }
  }
  await send('Emulation.clearDeviceMetricsOverride');
});
