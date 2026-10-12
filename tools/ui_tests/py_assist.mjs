// Python suggestions (12 Oct), from Jedi in the server's helper (tools/pyassist.py): "abc".
// lists str's methods, a module's names come from the module, parameter hints and hover for
// the file's own functions. Off in Practice. In a scratch folder, through a Try copy.
import { suite } from './harness.mjs';

const PY = `"""
PROBLEM
  Area of a rectangle.

KEY INSIGHT
  Width times height.
"""


def area(width, height=1):
    """The area of a width x height rectangle."""
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
  const hints = () => ev(`document.querySelector('.parameter-hints-widget')?.textContent || ''`);
  const undoLine = async () => { await press('Escape'); await ev(`${ED}.trigger('t', 'undo', {})`); await sleep(200); };
  const tryOn = async () => { await press('Alt+t'); return waitFor(`document.querySelector('#badge').textContent.startsWith('Try')`, 5000); };
  const discard = async () => { await press('Alt+t'); await sleep(600); };

  const ws = await t.scratchFolder({ 'A01_Area.py': PY });
  await ws.open('A01_Area.py');
  await ev('localStorage.clear()');
  await ws.open('A01_Area.py');
  await ws.onlyIn('A01_Area.py', 'def area');
  check('the server says Python suggestions are available', await ev(`fetch('/api/info').then(r => r.json()).then(i => i.pyassist === true)`));
  await ev(`${ED}.focus()`);
  await tryOn();

  await typeAndSuggest('def main():', '    "abc".up');
  check('"abc".up offers upper', await offers('upper', 20000), await rows());      // 20 s: the helper's first start
  await undoLine();
  await typeAndSuggest('def main():', '    import collections; collections.Coun');
  check("a module's names: collections.Coun offers Counter", await offers('Counter'), await rows());
  await undoLine();
  await typeAndSuggest('def main():', '    are');
  check("the file's own function: are offers area", await offers('area'), await rows());
  await undoLine();
  await typeAndSuggest('def main():', '    area(');
  await ev(`${ED}.trigger('t', 'editor.action.triggerParameterHints', {})`);
  let ok = await waitFor(`/height=1/.test(document.querySelector('.parameter-hints-widget')?.textContent || '')`, 8000);
  check('parameter hints for a function in the file', ok, await hints());
  await undoLine();
  await discard();

  // --- Practice: none of it
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 8000);
  await ev(`${ED}.focus()`);
  await typeAndSuggest('def main():', '    "abc".up');
  await sleep(2500);
  check('Practice: no suggestions', !(await rows()).includes('upper'), await rows());
  await undoLine();
  await ev(`document.querySelector('#practiceBtn').click()`);
  await sleep(600);
});
