// Prev and Start over open at the code; blank lines you make keep their indent, so Up/Down
// keep the caret off column 1; Save strips those spaces again (only lines you changed, and in
// Markdown only blank lines). Everything that saves works in a scratch folder.
import { suite, C06 } from './harness.mjs';

// line 8 already ends in 2 spaces on disk: Save must leave a line you did not touch alone
const JAVA = '/*\n * A fixture: Save must leave no trailing spaces.\n */\n\nclass B01_Spaces {\n    static int twice(int x) {\n'
  + '        int y = x * 2;\n        int z = y;  \n\n        return y;\n    }\n}\n';
const MD = '# Notes\n\nfirst line  \nsecond line\n';          // two trailing spaces = a Markdown line break
// empty lines 4 (top level), 8 (between statements), 11 (between methods), 13 (just inside a
// method) and 17 (inside a block comment)
const INDENT = ['/*', ' * Typing on empty lines.', ' */', '', 'class B02_Indent {', '    static int f(int x) {', '        int y = x;', '',
  '        return y;', '    }', '', '    static void g() {', '', '    }', '    /*', '       a note', '', '     */', '}', ''].join('\n');

await suite(async t => {
  const { ev, waitFor, press, type, check, sleep, ED } = t;
  const { pos, firstVisible, codeStart } = t;
  await t.fresh(C06);

  // ---- Prev after reading the description opens at the code; a place in the code is kept
  await ev(`${ED}.setScrollTop(0); ${ED}.setPosition({ lineNumber: 1, column: 1 }); ${ED}.focus()`);   // reading the problem
  await sleep(300);
  await press('Alt+j');
  await waitFor(`!location.hash.endsWith('C06_DailyTemperatures.java')`, 8000); await sleep(1000);
  await ev(`${ED}.focus()`);
  await press('Alt+k');                                             // Prev, back to C06
  await waitFor(`location.hash.endsWith('C06_DailyTemperatures.java')`, 8000); await sleep(1000);
  check('Prev after reading the description: opens at the code', (await firstVisible()) === (await codeStart()), [await firstVisible(), await codeStart()]);
  await ev(`${ED}.revealLineNearTop(70); ${ED}.setPosition({ lineNumber: 72, column: 9 }); ${ED}.focus()`); await sleep(400);
  const keep = await firstVisible();
  await press('Alt+j');
  await waitFor(`!location.hash.endsWith('C06_DailyTemperatures.java')`, 8000); await sleep(1000);
  await ev(`${ED}.focus()`);
  await press('Alt+k');
  await waitFor(`location.hash.endsWith('C06_DailyTemperatures.java')`, 8000); await sleep(1000);
  check('a place further down in the code is kept', Math.abs((await firstVisible()) - keep) <= 1 && (await pos())[0] === 72, [keep, await firstVisible(), await pos()]);

  // ---- Start over in Practice opens at the code
  await ev(`document.querySelector('#practiceBtn').click()`);
  await waitFor(`document.querySelector('#badge').textContent.startsWith('Practice')`, 10000); await sleep(600);
  await ev(`(() => { const e = ${ED}, n = e.getPosition().lineNumber;
    e.executeEdits('t', [{ range: new monaco.Range(n, 1, n, 1), text: '// started\\n' }]); e.setScrollTop(0); e.setPosition({ lineNumber: 1, column: 1 }); })()`);
  await sleep(400);
  await ev(`document.querySelector('#resetBtn').click()`); await sleep(300);
  await ev(`[...document.querySelectorAll('#banner button')].find(b => b.textContent === 'Start over').click()`); await sleep(600);
  check('Start over opens at the code', (await firstVisible()) === (await codeStart()), [await firstVisible(), await codeStart(), await pos()]);
  await ev(`document.querySelector('#practiceBtn').click()`); await sleep(500);

  // ---- blank lines keep the caret's column (scratch file, Edit mode)
  const ws = await t.scratchFolder({ 'B01_Spaces.java': JAVA, 'Notes.md': MD, 'B02_Indent.java': INDENT });
  await ws.open('B01_Spaces.java');
  await ws.onlyIn('B01_Spaces.java', 'class B01_Spaces');
  await ev(`document.querySelector('#editBtn').click()`);
  const toLine = (n, col) => ev(`(() => { const e = ${ED}, m = e.getModel(); e.setPosition({ lineNumber: ${n}, column: ${col ?? 'm.getLineMaxColumn(' + n + ')'} }); e.focus(); })()`);
  const lines = (a, b) => ev(`${ED}.getModel().getLinesContent().slice(${a - 1}, ${b})`);
  await toLine(7);                                                   // end of `int y = x * 2;`
  for (let i = 0; i < 3; i++) await press('Enter');
  await type('int a = 1;');
  await press('Escape');
  const t0 = await pos();
  await press('ArrowUp'); const t1 = await pos();
  await press('ArrowUp'); const t2 = await pos();
  await press('ArrowUp'); const t3 = await pos();
  console.log('  Enter x3, typed, then Up x3:', JSON.stringify([t0, t1, t2, t3]), JSON.stringify(await lines(7, 11)));
  check('Up through the blank lines you made: at the indent (column 9), not column 1', t1[1] === 9 && t2[1] === 9, [t0, t1, t2]);
  check('...and the next line of code gets the column back', t3[0] === 7 && t3[1] === t0[1], [t0, t3]);

  // ---- Save strips the spaces you added (never the caret's line, as IntelliJ)
  await toLine(13, 1);                                               // caret off the blank lines
  await ws.onlyIn('B01_Spaces.java', 'class B01_Spaces');
  await press('Ctrl+s');
  await waitFor(`/Saved/.test(document.querySelector('#toast').textContent)`, 8000);
  await sleep(300);
  const { readFileSync } = await import('node:fs');
  const saved = readFileSync(`${ws.dir}/B01_Spaces.java`, 'utf8');
  const trailing = saved.split('\n').filter(l => /[ \t]+$/.test(l));
  check('Save strips the spaces you added (not a line you left alone)', saved !== JAVA && trailing.length === 1 && trailing[0] === '        int z = y;  ',
        [trailing, saved.split('\n').slice(6, 12)]);
  check('...and the editor matches the file (no "not saved" badge)', !(await ev(`document.querySelector('#badge').textContent`)).includes('not saved'),
        await ev(`document.querySelector('#badge').textContent`));

  // ---- Markdown: two trailing spaces are a line break, never stripped
  await ws.open('Notes.md');
  await ev(`document.querySelector('#srcBtn').click()`); await sleep(600);
  await ws.onlyIn('Notes.md', 'first line');
  await ev(`document.querySelector('#editBtn').click()`);
  await ev(`(() => { const e = ${ED}; e.executeEdits('t', [{ range: new monaco.Range(4, 12, 4, 12), text: ' edited  ' }]);
    e.setPosition({ lineNumber: 1, column: 1 }); })()`);
  await sleep(400);
  await press('Ctrl+s');
  await waitFor(`/Saved/.test(document.querySelector('#toast').textContent)`, 8000); await sleep(300);
  const md = readFileSync(`${ws.dir}/Notes.md`, 'utf8');
  check('Markdown keeps a two-space line break, even on a line you edited', md === '# Notes\n\nfirst line  \nsecond line edited  \n', md);

  // ---- typing on an empty line that was already in the file starts at the right indent (Edit, not saved)
  await ws.open('B02_Indent.java');
  await ws.onlyIn('B02_Indent.java', 'class B02_Indent');
  await ev(`document.querySelector('#editBtn').click()`);
  const line = n => ev(`${ED}.getModel().getLineContent(${n})`);
  const typeAt = async (n, key, col = 1) => { await toLine(n, col); await press(key); await press('Escape'); return line(n); };
  check('between statements: `i` lands at the method body\'s indent', (await typeAt(8, 'i')) === '        i', await line(8));
  check('between methods: at the class body\'s indent', (await typeAt(11, 'v')) === '    v', await line(11));
  check('at the top level, after the header comment: column 1', (await typeAt(4, 'c')) === 'c', await line(4));
  check('inside a block comment: nothing added', (await typeAt(17, 'x')) === 'x', await line(17));
  await toLine(13, 1);
  await press('Tab');
  check('Tab on an empty line jumps to its indent (the editor does this itself)', (await line(13)) === '        ' && (await pos())[1] === 9, [await line(13), await pos()]);
  await press('Tab');
  check('a second Tab is an ordinary Tab', (await line(13)) === '            ', await line(13));
  await ev(`${ED}.executeEdits('t', [{ range: new monaco.Range(13, 1, 13, ${ED}.getModel().getLineMaxColumn(13)), text: '' }])`);
  check('`}` on an empty line lines up with its `{` (the editor does this itself)', (await typeAt(13, '}')) === '    }', await line(13));
  await ev(`${ED}.executeEdits('t', [{ range: new monaco.Range(13, 1, 13, ${ED}.getModel().getLineMaxColumn(13)), text: '        ' }])`);
  check('caret at column 1 of an indented blank line: the text goes after the indent', (await typeAt(13, 'z', 1)) === '        z', await line(13));
});
