// The ? panel (shortcuts) fits the window: 6 Oct, on Ravi's laptop its key column took most of
// the width, pushing the descriptions off the right edge, and the panel ran ~5000px past the
// bottom with no scroll bar.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, check, sleep, send } = t;
  for (const [w, h] of [[1366, 768], [1600, 880], [1000, 700]]) {
    await send('Emulation.setDeviceMetricsOverride', { width: w, height: h, deviceScaleFactor: 1, mobile: false });
    if (w === 1366) await t.open(C06);
    await ev(`document.querySelector('#help').hidden = true; document.querySelector('#helpBtn').click()`); await sleep(300);
    const m = await ev(`(() => { const p = document.querySelector('#help'), r = p.getBoundingClientRect();
      const keys = [...p.querySelectorAll('td:first-child')].map(x => x.getBoundingClientRect().width);
      return { left: r.left, right: r.right, bottom: r.bottom, sw: p.scrollWidth, cw: p.clientWidth, w: r.width,
               key: Math.max(...keys), tall: p.scrollHeight > innerHeight - 60,
               scrolls: p.scrollHeight > p.clientHeight && getComputedStyle(p).overflowY === 'auto' }; })()`);
    check(`${w}x${h}: the panel is inside the window`, m.left >= 0 && m.right <= w && m.bottom <= h, m);
    check(`${w}x${h}: nothing runs off its right edge`, m.sw <= m.cw, m);
    check(`${w}x${h}: the key column leaves most of the width to the descriptions`, m.key <= m.w * 0.4, m);
    check(`${w}x${h}: content taller than the window scrolls inside the panel`, !m.tall || m.scrolls, m);
  }
  await send('Emulation.clearDeviceMetricsOverride');
});
