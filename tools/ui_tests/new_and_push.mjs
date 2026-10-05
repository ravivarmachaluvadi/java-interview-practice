// The + form makes a numbered problem file that opens ready to fill in; ⇡ Push lists what
// changed, commits the ticked files and pushes them; a clash with GitHub keeps the commit here.
// Everything happens in a scratch folder that is its own git repo, with its "GitHub" (a bare
// repo, .remote.git) inside it, so nothing reaches the real repo or GitHub. The test server
// also refuses to push the real repo (CODEVIEW_NO_PUSH, set by test_ui.py).
import { execFileSync } from 'node:child_process';
import { existsSync, readFileSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { suite } from './harness.mjs';

const B11 = 'class FindCorruptPair {\n    static int f() {\n        return 1;\n    }\n}\n';

await suite(async t => {
  const { ev, waitFor, press, type, check, sleep, ED } = t;
  const q = sel => `document.querySelector(${JSON.stringify(sel)})`;
  const ws = await t.scratchFolder({
    '01-Arrays/A01_First.java': 'class First {\n    public static void main(String[] a) { }\n}\n',
    '01-Arrays/B11_FindCorruptPair.java': B11,
    'README.md': '# Fixture\n',
  });
  const run = (dir, ...a) => execFileSync('git', ['-C', dir, ...a], { encoding: 'utf8' }).trim();
  const git = (...a) => run(ws.dir, ...a);
  const remote = join(ws.dir, '.remote.git'), other = join(ws.dir, '.other');
  git('init', '-q', '-b', 'main');
  for (const [k, v] of [['user.name', 'UI test'], ['user.email', 'ui@example.com'], ['core.autocrlf', 'false']]) git('config', k, v);
  writeFileSync(join(ws.dir, '.git', 'info', 'exclude'), '.remote.git/\n.other/\n');
  git('add', '-A'); git('commit', '-q', '-m', 'start');
  execFileSync('git', ['init', '-q', '--bare', '-b', 'main', remote]);
  git('remote', 'add', 'origin', remote); git('push', '-q', '-u', 'origin', 'main');

  await ws.open('01-Arrays/B11_FindCorruptPair.java');
  const inScratch = async () => {
    if (!(await ev('location.hash')).startsWith(`#/${ws.id}/`)) throw new Error('STOP: the scratch folder is not the open one; nothing created');
  };

  // ---- ⇡ appears for a folder in git, with no number while nothing changed
  check('⇡ shows for a folder in git', await waitFor(`!${q('#pushBtn')}.hidden`, 8000));
  check('… with no number while nothing changed', (await ev(`${q('#pushCnt')}.textContent`)) === '');

  // ---- + opens the problem form on the open file's folder and level
  await ev(`${q('#newBtn')}.click()`); await sleep(300);
  check('+ opens the New problem form', await ev(`!${q('#newPanel')}.hidden && !${q('#npForm')}.hidden`));
  const start = await ev(`[${q('#npFolder')}.value, ${q('#npLevel button.on')}.dataset.l, ${q('#npDiff')}.value].join()`);
  check("it starts on the open file's folder, level and a matching difficulty", start === '01-Arrays,B,Easy', start);
  const preview = () => ev(`${q('#npPreview')}.textContent`);
  await type('Two Sum');
  check('the preview names the next free B number', (await preview()) === '→ 01-Arrays/B12_TwoSum.java', await preview());
  const level = l => ev(`[...document.querySelectorAll('#npLevel button')].find(b => b.dataset.l === '${l}').click()`);
  await level('C');
  check('level C: C01 and Medium', (await preview()) === '→ 01-Arrays/C01_TwoSum.java' && (await ev(`${q('#npDiff')}.value`)) === 'Medium', await preview());
  await level('');
  check('no level: just the class name', (await preview()) === '→ 01-Arrays/TwoSum.java', await preview());
  await level('B');
  await ev(`${q('#npName')}.value = ''`); await type('3Sum');
  check('a name starting with a digit is refused before Create', (await ev(`${q('#npPreview')}.classList.contains('bad') && ${q('#npCreate')}.disabled`)), await preview());
  await ev(`${q('#npFolder')}.value = '02-New'; ${q('#npFolder')}.dispatchEvent(new Event('input'))`);
  await ev(`${q('#npName')}.value = ''; ${q('#npName')}.focus()`); await type('Two Sum');
  check('a folder not in the list says so', (await preview()) === '→ 02-New/B01_TwoSum.java   (new folder)', await preview());
  await ev(`${q('#npFolder')}.value = '01-Arrays'; ${q('#npFolder')}.dispatchEvent(new Event('input')); ${q('#npSource')}.focus()`);
  await type('1');
  await ev(`${q('#npName')}.focus()`);
  await inScratch();
  await press('Enter');                                              // Enter creates

  // ---- the file opens in Edit mode with PROBLEM's ... selected
  check('Create opens the new file', await waitFor(`location.hash.endsWith('/01-Arrays/B12_TwoSum.java') && !!${ED}.getModel() && ${ED}.getModel().getValue().includes('LeetCode 1 | Easy')`, 10000));
  await sleep(500);
  const disk = join(ws.dir, '01-Arrays', 'B12_TwoSum.java');
  const text = existsSync(disk) ? readFileSync(disk, 'utf8') : '';
  check('it is on disk with the header and LF line endings', text.startsWith('/*') && text.includes('class TwoSum {') && !text.includes('\r'));
  const sel = await ev(`(() => { const e = ${ED}, s = e.getSelection(), m = e.getModel(); return [s.startLineNumber, m.getLineContent(s.startLineNumber - 1), m.getValueInRange(s)]; })()`);
  check("PROBLEM's ... is selected, to type over", sel[1] === ' * PROBLEM' && sel[2] === '...', sel);
  check('…in Edit mode', (await ev(`${q('#badge')}.textContent`)) === 'Editing', await ev(`${q('#badge')}.textContent`));
  check('the toast says where it went', (await ev(`${q('#toast')}.textContent`)).startsWith('Created 01-Arrays/B12_TwoSum.java'), await ev(`${q('#toast')}.textContent`));
  check('⇡ now counts 1 changed file', await waitFor(`${q('#pushCnt')}.textContent === '1'`, 8000), await ev(`${q('#pushCnt')}.textContent`));

  // ---- an edit kept only in this browser is named in the panel, not pushed
  await ev(`localStorage.setItem('cv:draft:${ws.id}/01-Arrays/A01_First.java', JSON.stringify('class First { /* unsaved */ }'))`);

  // ---- ⇡ lists the new file, ticked, with a message made from its name
  await ev(`${q('#pushBtn')}.click()`);
  await waitFor(`document.querySelectorAll('#pushList .push-row').length === 1`, 8000);
  const rows = await ev(`[...document.querySelectorAll('#pushList .push-row')].map(r => r.querySelector('input').checked + ' ' + r.textContent)`);
  check('the panel lists the new file, ticked', rows.length === 1 && rows[0] === 'true newB12_TwoSum.java01-Arrays', rows);
  check('the message is made from its name', (await ev(`${q('#pushMsg')}.value`)) === 'Add B12_TwoSum', await ev(`${q('#pushMsg')}.value`));
  check('Push says how many files', (await ev(`${q('#pushGo')}.textContent`)) === 'Push 1 file' && !(await ev(`${q('#pushGo')}.disabled`)));
  check('the branch and where it goes', (await ev(`${q('#pushWhere')}.textContent`)) === 'main → origin/main', await ev(`${q('#pushWhere')}.textContent`));
  const note = await ev(`${q('#pushNote')}.textContent`);
  check('an unsaved edit is named as not included', note.includes('Not saved yet') && note.includes('A01_First.java'), note);
  await ev(`${q('#pushList input')}.click()`);
  check('unticking it: nothing to push, no message', (await ev(`${q('#pushGo')}.disabled`)) && (await ev(`${q('#pushMsg')}.value`)) === '');
  await ev(`${q('#pushAll')}.click()`);
  check('All files ticks it again', (await ev(`${q('#pushList input')}.checked`)) && (await ev(`${q('#pushMsg')}.value`)) === 'Add B12_TwoSum');

  await ev(`${q('#pushGo')}.click()`);
  check('Push closes the panel', await waitFor(`${q('#pushPanel')}.hidden`, 30000), await ev(`${q('#pushErr')}.textContent`));
  check('…and says it went', (await ev(`${q('#toast')}.textContent`)).startsWith('Pushed 1 file to GitHub ('), await ev(`${q('#toast')}.textContent`));
  check('"GitHub" has the commit with that message', run(remote, 'log', '-1', '--format=%s', 'main') === 'Add B12_TwoSum');
  check('…holding only the new file', run(remote, 'show', '--name-only', '--format=', 'main') === '01-Arrays/B12_TwoSum.java');
  check('nothing is left to commit', git('status', '--porcelain') === '', git('status', '--porcelain'));
  check('⇡ has no number again', await waitFor(`${q('#pushCnt')}.textContent === ''`, 8000));

  // ---- a clash: "GitHub" has a newer change to the same line
  execFileSync('git', ['clone', '-q', remote, other]);
  for (const [k, v] of [['user.name', 'Elsewhere'], ['user.email', 'e@example.com'], ['core.autocrlf', 'false']]) run(other, 'config', k, v);
  writeFileSync(join(other, '01-Arrays', 'B11_FindCorruptPair.java'), B11.replace('return 1;', 'return 2;'));
  run(other, 'commit', '-q', '-am', 'theirs'); run(other, 'push', '-q');
  writeFileSync(join(ws.dir, '01-Arrays', 'B11_FindCorruptPair.java'), B11.replace('return 1;', 'return 3;'));
  await ev(`${q('#pushBtn')}.click()`);
  await waitFor(`document.querySelectorAll('#pushList .push-row').length === 1`, 8000);
  check('the changed file is listed as changed', (await ev(`${q('#pushList .push-row .st')}.textContent`)) === 'changed');
  await ev(`${q('#pushGo')}.click()`);
  check('a clash shows an error in the panel', await waitFor(`!${q('#pushErr')}.hidden`, 30000));
  const err = await ev(`${q('#pushErr')}.textContent`);
  check('…saying why and that the commit is kept', err.includes('same lines') && err.includes('saved on this PC'), err);
  check('…and offers to push that commit later', (await ev(`${q('#pushGo')}.textContent`)) === 'Push 1 commit'
    && (await ev(`${q('#pushNote')}.textContent`)).includes('1 earlier commit not on GitHub yet goes too'), await ev(`${q('#pushNote')}.textContent`));
  check('"GitHub" still has only their change', run(remote, 'log', '-1', '--format=%s', 'main') === 'theirs');
  check('your commit and your file are kept here', git('log', '-1', '--format=%s') === 'Update B11_FindCorruptPair'
    && readFileSync(join(ws.dir, '01-Arrays', 'B11_FindCorruptPair.java'), 'utf8').includes('return 3;'));
  check('no rebase is left half done', !existsSync(join(ws.dir, '.git', 'rebase-merge')) && !existsSync(join(ws.dir, '.git', 'rebase-apply')));
  await ev(`${q('#pushMsg')}.focus()`);
  await press('Escape');
  check('Esc closes the panel', await ev(`${q('#pushPanel')}.hidden`));
});
