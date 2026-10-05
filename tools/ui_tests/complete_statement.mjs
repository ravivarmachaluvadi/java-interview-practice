// Ctrl+Shift+Enter (IntelliJ's Complete Current Statement) and IntelliJ's ★ expected type after
// `new`, in C06 (Edit mode, nothing saved). Also prints the helper's warm timings.
import { suite, C06 } from './harness.mjs';

await suite(async t => {
  const { ev, waitFor, press, type, check, sleep, ED } = t;
  const complete = () => press('Ctrl+Shift+Enter');
  /* exact text at the caret: typing would auto-close brackets and quotes */
  const put = text => ev(`(() => { const e = ${ED}, p = e.getPosition();
    e.executeEdits('t', [{ range: new monaco.Range(p.lineNumber, p.column, p.lineNumber, p.column), text: ${JSON.stringify(text)} }]);
    e.setPosition({ lineNumber: p.lineNumber, column: p.column + ${text.length} }); })()`);
  const lineAt = d => ev(`(() => { const e = ${ED}; return e.getModel().getLineContent(e.getPosition().lineNumber + (${d})); })()`);
  const caretAtEnd = () => ev(`(() => { const e = ${ED}, p = e.getPosition(); return p.column === e.getModel().getLineMaxColumn(p.lineNumber); })()`);
  async function freshLine() {
    await press('Escape');
    await ev(`(() => { const e = ${ED}, m = e.getModel(); const n = m.getLinesContent().findIndex(l => l.includes('result[colderDay] = today - colderDay;')) + 1;
                      e.setPosition({ lineNumber: n, column: m.getLineMaxColumn(n) }); e.focus(); })()`);
    await press('Enter');
  }

  await t.fresh(C06);
  await ev(`document.querySelector('#editBtn').click()`);

  await freshLine();
  await type('int m = temperatures.length');
  await press('Escape');
  await complete();
  check('adds ; and keeps the caret at the end', (await lineAt(0)).trim() === 'int m = temperatures.length;' && await caretAtEnd(), await lineAt(0));
  const before = await ev(`${ED}.getModel().getLineCount()`);
  await complete();
  check('again on a finished line: a new line below', (await ev(`${ED}.getModel().getLineCount()`)) === before + 1 && (await lineAt(0)).trim() === ''
        && (await lineAt(-1)).trim() === 'int m = temperatures.length;', [await lineAt(-1), await lineAt(0)]);

  await freshLine();
  await put('System.out.println("a(b"');
  await press('Escape');
  await complete();
  check('closes the call, ignoring a bracket inside a string', (await lineAt(0)).trim() === 'System.out.println("a(b");', await lineAt(0));

  await freshLine();
  await put('int k = Math.max(1, 2 // note');
  await press('Escape');
  await complete();
  check('puts ; before a trailing comment', (await lineAt(0)).trim() === 'int k = Math.max(1, 2); // note', await lineAt(0));

  await freshLine();
  await put('if (m > 0');
  await press('Escape');
  await complete();
  const ifLine = await lineAt(-1), inner = await lineAt(0), close = await lineAt(1);
  check('if header gets braces, caret inside the block',
        ifLine.trim() === 'if (m > 0) {' && inner.trim() === '' && close.trim() === '}' && inner.length > ifLine.indexOf('if') && close.indexOf('}') === ifLine.indexOf('if'),
        [ifLine, inner, close]);

  await freshLine();
  await put('waiting.push(today');
  await press('Escape');
  await complete();
  check('call inside a loop closes and ends', (await lineAt(0)).trim() === 'waiting.push(today);', await lineAt(0));

  await freshLine();
  await type('System.out.println("x');            // as typed: the editor has already closed ") for you
  await press('Escape');
  await complete();
  check('typed with auto-close: just the ;', (await lineAt(0)).trim() === 'System.out.println("x");', await lineAt(0));

  await freshLine();
  await put('if (m > 0) m--');
  await press('Escape');
  await complete();
  check('`if (x) stmt` is a statement, not a header', (await lineAt(0)).trim() === 'if (m > 0) m--;', await lineAt(0));

  // --- IntelliJ's ★ after `new`
  const rows = `[...document.querySelectorAll('.suggest-widget.visible .monaco-list-row')].map(r => r.getAttribute('aria-label') || r.textContent)`;
  await freshLine();
  await type('int[] arr = new i');
  let ok = await waitFor(`(${rows})[0] && (${rows})[0].startsWith('int[]')`, 8000);
  check('`int[] arr = new i`: ★ int[] is first', ok, (await ev(rows)).slice(0, 3));
  await press('Enter');
  await sleep(200);
  const caret = await ev(`(() => { const e = ${ED}, p = e.getPosition(), l = e.getModel().getLineContent(p.lineNumber); return [l.trim(), l.slice(p.column - 2, p.column)]; })()`);
  check('Enter gives new int[] with the caret inside the brackets', caret[0] === 'int[] arr = new int[]' && caret[1] === '[]', caret);
  await ev(`${ED}.trigger('t', 'leaveSnippet')`);

  await freshLine();
  await type('java.util.List<Integer> list = new ');
  await ev(`${ED}.trigger('t', 'editor.action.triggerSuggest', {})`);
  ok = await waitFor(`(${rows})[0] && (${rows})[0].startsWith('ArrayList')`, 8000);
  check('`List<Integer> list = new ` + Ctrl+Space: ★ ArrayList first', ok, (await ev(rows)).slice(0, 3));
  await press('Enter');
  await sleep(300);
  check('and Enter adds its import', (await ev(`${ED}.getModel().getValue()`)).includes('import java.util.ArrayList;'));

  // the helper's own time per completion, warm (printed, not checked)
  for (const body of ['int[] arr = new i', 'java.util.List<Integer> list = new A']) {
    const code = `class E {\n    void f() {\n        ${body}\n    }\n}\n`;
    const ms = [];
    for (let i = 0; i < 10; i++) {
      const r = await (await fetch(t.BASE + '/api/assist', { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-CodeView': '1' },
        body: JSON.stringify({ op: 'complete', root: t.rid, path: null, code, offset: code.indexOf(body) + body.length }) })).json();
      ms.push(r.ms);
    }
    console.log(`  "${body}": helper ${ms.sort((a, b) => a - b)[5]} ms median`);
  }
});
