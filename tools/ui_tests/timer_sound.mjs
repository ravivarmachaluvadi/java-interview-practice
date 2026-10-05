// The header timer after a reload, in a Chrome that keeps pages silent until you click or type
// (the normal rule; timer_and_typing.mjs turns it off). A running count-down shows 🔇 and says
// to click; a real click lets it beep at zero and the 🔇 goes. Beeps are recorded by wrapping
// AudioContext before the page loads.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, check, send, sleep, ED } = t;
  await send('Page.addScriptToEvaluateOnNewDocument', { source: `
    window.__tones = [];
    const AC = window.AudioContext;
    window.AudioContext = class extends AC {
      constructor(...a) { super(...a); window.__ctx = this; }
      createOscillator() { const o = super.createOscillator(), st = o.start.bind(o), ctx = this;
        o.start = when => { window.__tones.push({ in: (when || 0) - ctx.currentTime }); return st(when); }; return o; }
    };` });
  const button = () => ev(`document.querySelector('#clockBtn').textContent`);
  await t.fresh(C06);

  // a count-down of 1 minute, running, then a reload: no click on this page yet
  await ev(`localStorage.setItem('cv:clock', JSON.stringify({ mode: 'down', minutes: 1, running: true, startedAt: Date.now(), banked: 0, rang: false }))`);
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel()`, 30000);
  await sleep(800);
  check('before any click: the timer shows 🔇', /^⏳ 00:[0-5]\d 🔇$|^⏳ 01:00 🔇$/.test(await button()), await button());
  check('...and says why, once', /Click anywhere on the page so it can beep/.test(await ev(`document.querySelector('#toast').textContent`)),
        await ev(`document.querySelector('#toast').textContent`));
  check('...its tooltip says it too', /Click anywhere/.test(await ev(`document.querySelector('#clockBtn').title`)));
  check('the browser really has sound off (nothing scheduled)', (await ev(`window.__ctx && window.__ctx.state`)) === 'suspended' && (await ev(`window.__tones.length`)) === 0,
        [await ev(`window.__ctx && window.__ctx.state`), await ev(`window.__tones.length`)]);

  // a real click somewhere in the editor
  const at = await ev(`(() => { const r = document.querySelector('#editor').getBoundingClientRect(); return { x: r.left + r.width / 2, y: r.top + r.height / 2 }; })()`);
  await t.click(at.x, at.y);
  const on = await waitFor(`window.__ctx.state === 'running' && !document.querySelector('#clockBtn').textContent.includes('🔇')`, 5000);
  check('after a click: sound is on and the 🔇 is gone', on, [await button(), await ev(`window.__ctx.state`)]);
  const tones = await ev(`window.__tones`);
  const left = await ev(`(() => { const c = JSON.parse(localStorage.getItem('cv:clock')); return (c.minutes * 60000 - (Date.now() - c.startedAt)) / 1000; })()`);
  check('...and the 3 beeps are set for zero', tones.length === 3 && Math.abs(tones[0].in - left) < 1.5, [tones, left]);

  // a count-up needs no sound: no 🔇
  await ev(`localStorage.setItem('cv:clock', JSON.stringify({ mode: 'up', minutes: 1, running: true, startedAt: Date.now(), banked: 0, rang: false }))`);
  await send('Page.reload');
  await waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel()`, 30000);
  await sleep(800);
  check('a count-up shows no 🔇', /^⏱ 00:0\d$/.test(await button()), await button());
}, { chromeFlags: ['--autoplay-policy=document-user-activation-required'] });
