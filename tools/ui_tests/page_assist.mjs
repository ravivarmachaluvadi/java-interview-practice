// Suggestions in a UI page (12 Oct): the markup's own (Monaco's HTML service), real JavaScript
// ones inside <script> - the test block too - and CSS property names and only-valid values
// inside <style>. Off in Practice. In a scratch folder, through a Try copy.
import { suite } from './harness.mjs';

const PAGE = `<!DOCTYPE html>
<!--
PROBLEM
  A counter.

KEY INSIGHT
  State, then render.
-->
<html>
<head>
  <style>
    .box { color: red; }
  </style>
</head>
<body>
  <span id="count">0</span>
  <script>
    let count = 0;
    function renderCount(times) {
      document.getElementById('count').textContent = count * times;
    }
  </script>
  <script type="test">
    console.log('case 1: 1   expected 1');
  </script>
</body>
</html>
`;
const PY = `"""
PROBLEM
  Two sum.

KEY INSIGHT
  Complements.
"""


def area(width, height=1):
    return width * height


def main():
    print("case 1:", area(2), "  expected 2")


if __name__ == "__main__":
    main()
`;

await suite(async t => {
  const { ev, waitFor, press, check, sleep, ED } = t;
  const LABELS = `[...document.querySelectorAll('.suggest-widget.visible .monaco-list-row .label-name')]`;
  const rows = () => ev(`${LABELS}.map(r => r.textContent)`);
  /* put `text` on its own new line after the line holding `after`, caret at its end, then ask */
  async function typeAndSuggest(after, text) {
    await press('Escape');
    await ev(`(() => { const e = ${ED}, m = e.getModel(), n = m.getLinesContent().findIndex(l => l.includes(${JSON.stringify(after)})) + 1;
      const col = m.getLineMaxColumn(n);
      e.executeEdits('t', [{ range: new monaco.Range(n, col, n, col), text: '\\n' + ${JSON.stringify(text)} }]);
      e.setPosition({ lineNumber: n + 1, column: ${text.length + 1} }); e.focus(); })()`);
    await ev(`${ED}.trigger('t', 'editor.action.triggerSuggest', {})`);
  }
  const offers = (word, ms = 10000) => waitFor(`${LABELS}.some(r => r.textContent === ${JSON.stringify(word)})`, ms);
  const undoLine = async () => { await press('Escape'); await ev(`${ED}.trigger('t', 'undo', {})`); await sleep(200); };
  const tryOn = async () => { await press('Alt+t'); return waitFor(`document.querySelector('#badge').textContent.startsWith('Try')`, 5000); };
  const discard = async () => { await press('Alt+t'); await sleep(600); };

  const ws = await t.scratchFolder({ 'A01_Page.html': PAGE, 'A01_Area.py': PY });
  await ws.open('A01_Page.html');
  await ev('localStorage.clear()');
  await ws.open('A01_Page.html');
  await ws.onlyIn('A01_Page.html', 'renderCount');
  await ev(`${ED}.focus()`);
  await tryOn();

  // --- the markup: Monaco's own HTML service
  await typeAndSuggest('<span id="count">0</span>', '  <but');
  check('markup: <but offers button', await offers('button'), await rows());
  await undoLine();

  // --- JavaScript inside <script>
  await typeAndSuggest('let count = 0;', '    document.getElementByI');
  check('<script>: document.getElementByI offers getElementById', await offers('getElementById'), await rows());
  await undoLine();
  await typeAndSuggest("console.log('case 1", '    renderC');
  check('the test block: offers the page\'s own renderCount', await offers('renderCount'), await rows());
  await undoLine();
  await typeAndSuggest("console.log('case 1", '    renderCount(');
  await ev(`${ED}.trigger('t', 'editor.action.triggerParameterHints', {})`);
  let ok = await waitFor(`/times/.test(document.querySelector('.parameter-hints-widget')?.textContent || '')`, 8000);
  check('<script>: parameter hints for the page\'s function', ok, await ev(`document.querySelector('.parameter-hints-widget')?.textContent || ''`));
  await undoLine();
  await press('Escape');
  await ev(`(() => { const e = ${ED}, m = e.getModel(), n = m.getLinesContent().findIndex(l => l.includes('function renderCount')) + 1;
    e.setPosition({ lineNumber: n, column: m.getLineContent(n).indexOf('renderCount') + 3 }); e.focus();
    e.trigger('t', 'editor.action.showHover', {}); })()`);
  ok = await waitFor(`/renderCount\\(times/.test(document.querySelector('.monaco-hover')?.textContent || '')`, 8000);
  check('<script>: hover shows the function\'s signature', ok, await ev(`document.querySelector('.monaco-hover')?.textContent || ''`));
  await press('Escape');

  // --- CSS inside <style>
  await typeAndSuggest('.box { color: red; }', '    .row { disp');
  check('<style>: disp offers display', await offers('display'), await rows());
  await undoLine();
  await typeAndSuggest('.box { color: red; }', '    .row { display: gr');
  check('<style>: display: gr offers grid', await offers('grid'), await rows());
  await undoLine();
  await typeAndSuggest('.box { color: red; }', '    .row { display: b');
  ok = await offers('block');
  const shown = await rows();
  check('<style>: display: b offers block but not bold (not a display value)', ok && !shown.some(r => /\bbold\b/.test(r)), shown);
  await undoLine();
  await typeAndSuggest('.box { color: red; }', '    .row { font-weight: b');
  check('<style>: font-weight: b offers bold', await offers('bold'), await rows());
  await undoLine();
  await discard();

  // --- Practice: none of it
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 8000);
  await typeAndSuggest("console.log('case 1", '    document.getElementByI');
  await sleep(2500);
  check('Practice: no JavaScript suggestions in <script>', !(await rows()).some(r => /getElementById/.test(r)), await rows());
  await undoLine();
  await ev(`document.querySelector('#practiceBtn').click()`);
  await sleep(600);
});
