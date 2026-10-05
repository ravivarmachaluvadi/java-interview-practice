/*
 * Shared harness for the Code Viewer browser checks in this folder. Run them with
 *
 *     python tools/test_ui.py            (all suites)
 *     python tools/test_ui.py format     (the suites whose name contains "format")
 *
 * Each suite drives a headless Chrome over the DevTools protocol, the way you would use the
 * page: real key presses, clicks and typing. It talks to the throwaway server test_ui.py starts
 * on 127.0.0.1:8026 (CV_BASE overrides it) - never the tray's 8025, never its progress file.
 *
 * Safety rules, learned the hard way (5 Oct 2026):
 *   - A check that saves must use scratchFolder(), never a repo file. A missed open once left
 *     C06 showing and Ctrl+S wrote three blank lines into it; onlyIn() refuses to go on unless
 *     the scratch file is the one open.
 *   - finish() compares `git status` and `git diff` with how they were at the start, and fails
 *     the suite loudly if anything in the repo changed.
 *   - The Chrome profile and the scratch folders are deleted at the end, also after a failure
 *     (leftover profiles once came to 1.46 GB).
 */
import { spawn, execFileSync } from 'node:child_process';
import { existsSync, mkdtempSync, readFileSync, rmSync, statSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { basename, dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

export const REPO = resolve(dirname(fileURLToPath(import.meta.url)), '..', '..');
export const BASE = process.env.CV_BASE || 'http://127.0.0.1:8026';
export const ED = 'monaco.editor.getEditors()[0]';
export const C06 = 'AAScratches/01-DSA/07-Stack-Queue-Monotonic/C06_DailyTemperatures.java';
export const sleep = ms => new Promise(r => setTimeout(r, ms));

function chromePath() {
  const local = process.env.LOCALAPPDATA || '';
  const found = [process.env.CHROME,
    'C:/Program Files/Google/Chrome/Application/chrome.exe',
    'C:/Program Files (x86)/Google/Chrome/Application/chrome.exe',
    local && join(local, 'Google/Chrome/Application/chrome.exe'),
    '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
    '/usr/bin/google-chrome', '/usr/bin/chromium'].filter(Boolean).find(p => existsSync(p));
  if (!found) throw new Error('Chrome not found: set CHROME to chrome.exe');
  return found;
}

/* `git status`, the whole diff, and each untracked file's size and time: equal before and after
   means the repo was not touched. (Plain `git status` shows an untracked folder as one line, so
   a file written into it went unnoticed - found by a probe that had to fail, 6 Oct.) */
function gitState() {
  const git = args => execFileSync('git', ['-C', REPO, ...args], { maxBuffer: 256 << 20 }).toString();
  const untracked = git(['ls-files', '--others', '--exclude-standard', '-z']).split('\0').filter(Boolean).map(f => {
    try { const s = statSync(join(REPO, f)); return `${f} ${s.size} ${s.mtimeMs}`; } catch { return `${f} gone`; }
  });
  return [git(['status', '--porcelain', '--untracked-files=all']), git(['diff']), ...untracked].join('\n');
}

/* a POST the server accepts from a script: its own header, no foreign Origin */
async function post(path, body) {
  const r = await fetch(BASE + path, { method: 'POST', body: JSON.stringify(body),
    headers: { 'Content-Type': 'application/json', 'X-CodeView': '1' } });
  if (!r.ok) throw new Error(`POST ${path}: ${r.status}`);
  return r.json();
}

/* key name -> [code, keyCode, text typed, extra modifiers] */
const MODS = { Alt: 1, Ctrl: 2, Meta: 4, Shift: 8 };
const KEYS = {
  Enter: ['Enter', 13, '\r'], Tab: ['Tab', 9], Escape: ['Escape', 27], Backspace: ['Backspace', 8],
  Delete: ['Delete', 46], Home: ['Home', 36], End: ['End', 35], ' ': ['Space', 32, ' '],
  ArrowUp: ['ArrowUp', 38], ArrowDown: ['ArrowDown', 40], ArrowLeft: ['ArrowLeft', 37], ArrowRight: ['ArrowRight', 39],
  F2: ['F2', 113], F6: ['F6', 117], F12: ['F12', 123],
  '>': ['Period', 190, '>', MODS.Shift], '<': ['Comma', 188, '<', MODS.Shift], '}': ['BracketRight', 221, '}', MODS.Shift],
};

/**
 * Runs one suite: `await suite(async t => { ... })`. An exception counts as a failure; the
 * clean-up and the repo check run either way, then the process exits 0 only if all passed.
 * opts.chromeFlags: extra Chrome switches (e.g. to allow sound without a click).
 */
export async function suite(body, opts = {}) {
  const t = await start(opts);
  try { await body(t); }
  catch (e) { console.log('ERROR ' + (e && e.stack || e)); t.results.push(false); }
  finally { await t.finish(); }
}

async function start({ chromeFlags = [] } = {}) {
  const gitAtStart = gitState();
  const profile = mkdtempSync(join(tmpdir(), 'cvui-'));
  const chrome = spawn(chromePath(), ['--headless=new', '--remote-debugging-port=0', `--user-data-dir=${profile}`,
    '--window-size=1366,768', '--screen-info={1366x768}', '--no-first-run', '--no-default-browser-check',
    ...chromeFlags, 'about:blank'], { stdio: 'ignore' });
  const results = [], scratch = [];

  // Chrome picks a free port and writes it to DevToolsActivePort
  let port, target;
  for (let i = 0; i < 150 && !port; i++) {
    try { port = readFileSync(join(profile, 'DevToolsActivePort'), 'utf8').split('\n')[0].trim(); } catch { await sleep(100); }
  }
  for (let i = 0; i < 50 && !target; i++) {
    try { target = (await (await fetch(`http://127.0.0.1:${port}/json/list`)).json()).find(x => x.type === 'page'); } catch {}
    if (!target) await sleep(200);
  }
  if (!target) throw new Error('Chrome did not start');
  const ws = new WebSocket(target.webSocketDebuggerUrl);
  await new Promise((ok, bad) => { ws.addEventListener('open', ok); ws.addEventListener('error', bad); });
  let id = 0;
  const pending = new Map();
  ws.addEventListener('message', e => { const m = JSON.parse(e.data); if (m.id && pending.has(m.id)) { pending.get(m.id)(m); pending.delete(m.id); } });
  const send = (method, params = {}) => new Promise(res => { const i = ++id; pending.set(i, res); ws.send(JSON.stringify({ id: i, method, params })); });
  await send('Page.enable');

  const t = {
    results, send, sleep, ED, BASE,

    /* the value of a page expression; throws if the page threw */
    async ev(expr) {
      const r = await send('Runtime.evaluate', { expression: expr, awaitPromise: true, returnByValue: true });
      if (r.error) throw new Error(`${expr.slice(0, 90)} -> ${r.error.message}`);
      if (r.result.exceptionDetails) {
        const d = r.result.exceptionDetails;
        throw new Error(`${expr.slice(0, 90)} -> ${(d.exception && d.exception.description || d.text).split('\n')[0]}`);
      }
      return r.result.result.value;
    },
    /* true once `expr` is truthy, false after `ms` */
    async waitFor(expr, ms = 8000) {
      const t0 = Date.now();
      while (Date.now() - t0 < ms) { try { if (await t.ev(expr)) return true; } catch {} await sleep(100); }
      return false;
    },
    check(name, ok, info = '') {
      ok = !!ok;
      results.push(ok);
      console.log((ok ? 'PASS ' : 'FAIL ') + name + (info !== '' ? '  -- ' + JSON.stringify(info).slice(0, 300) : ''));
    },
    /* a key press as a person makes it: press('Enter'), press('Ctrl+Shift+Enter'), press('Alt+j'), press('>').
       { char: false } sends the key without the character it types (a page menu's Enter). */
    async press(chord, { char = true } = {}) {
      const parts = chord.length > 1 ? chord.split('+') : [chord];
      const name = parts.pop();
      let modifiers = parts.reduce((m, p) => m | MODS[p], 0), code, vk, text;
      if (KEYS[name]) { const k = KEYS[name]; [code, vk, text] = k; modifiers |= k[3] || 0; }
      else if (/^[a-z0-9]$/i.test(name)) { code = /\d/.test(name) ? 'Digit' + name : 'Key' + name.toUpperCase(); vk = name.toUpperCase().charCodeAt(0); text = name; }
      else throw new Error('press: unknown key ' + chord);
      if (!char || modifiers & (MODS.Ctrl | MODS.Alt | MODS.Meta)) text = undefined;   // a shortcut types nothing
      await send('Input.dispatchKeyEvent', { type: 'rawKeyDown', key: name, code, windowsVirtualKeyCode: vk, modifiers });
      if (text) await send('Input.dispatchKeyEvent', { type: 'char', key: name, text, windowsVirtualKeyCode: vk, modifiers });
      await send('Input.dispatchKeyEvent', { type: 'keyUp', key: name, code, windowsVirtualKeyCode: vk, modifiers });
      await sleep(150);
    },
    /* text as typed, one character at a time (no key events: auto-close still applies) */
    async type(text) { for (const ch of text) { await send('Input.insertText', { text: ch }); await sleep(35); } },
    /* a real mouse click at page coordinates: unlike element.click(), the browser counts it as the user's */
    async click(x, y) {
      for (const type of ['mousePressed', 'mouseReleased']) await send('Input.dispatchMouseEvent', { type, x, y, button: 'left', clickCount: 1 });
      await sleep(150);
    },
    pos: () => t.ev(`(() => { const p = ${ED}.getPosition(); return [p.lineNumber, p.column]; })()`),
    curLine: () => t.ev(`(() => { const e = ${ED}; return e.getModel().getLineContent(e.getPosition().lineNumber); })()`),
    firstVisible: () => t.ev(`${ED}.getVisibleRanges()[0].startLineNumber`),
    /* where the page should open a Java file: the line after the header comment */
    codeStart: () => t.ev(`(() => { const l = ${ED}.getModel().getLinesContent(); if (!/^\\s*\\/\\*/.test(l[0] || '')) return 1;
        let n = l.findIndex(x => x.includes('*/')) + 1; while (n < l.length && !l[n].trim()) n++; return n + 1; })()`),

    rid: (await (await fetch(BASE + '/api/roots')).json())[0].id,
    /* a repo file, by path from the repo root (read-only use only: never Save here) */
    async open(path) {
      await send('Page.navigate', { url: `${BASE}/#/${t.rid}/${path}` });
      await t.waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel() && location.hash.endsWith(${JSON.stringify(path.split('/').pop())})`, 30000);
      await sleep(600);
    },
    /* open a repo file in a clean browser: no stored drafts, modes or timers */
    async fresh(path = C06) {
      await t.open(path);
      await t.ev('localStorage.clear()');
      await send('Page.reload');
      await t.waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel()`, 30000);
      await sleep(800);
    },
    /**
     * A throwaway folder with `files` ({name: text}), added to the test server as a second folder.
     * Returns { id, dir, open(name), onlyIn(name, marker) }. Deleted by finish().
     */
    async scratchFolder(files) {
      const dir = mkdtempSync(join(tmpdir(), 'cvws-'));
      for (const [name, text] of Object.entries(files)) writeFileSync(join(dir, name), text);
      const r = await post('/api/roots', { path: dir });
      const root = r.roots.find(x => x.path.replace(/\\/g, '/').endsWith('/' + basename(dir)));
      if (!root) throw new Error('the server did not add ' + dir);
      const s = { id: root.id, dir };
      scratch.push(s);
      /* the page learns about a new folder only when it loads, hence a new address */
      s.open = async name => {
        await send('Page.navigate', { url: `${BASE}/?load=${Date.now()}#/${s.id}/${name}` });
        await t.waitFor(`typeof monaco !== 'undefined' && !!${ED} && !!${ED}.getModel() && location.hash === ${JSON.stringify(`#/${s.id}/${name}`)}`, 30000);
        await sleep(900);
      };
      /* stop here unless `name` from this folder is the open file (and holds `marker`) */
      s.onlyIn = async (name, marker) => {
        const ok = (await t.ev('location.hash')) === `#/${s.id}/${name}`
          && (await t.ev(`!!${ED}.getModel() && ${ED}.getModel().getValue().includes(${JSON.stringify(marker)})`));
        if (!ok) throw new Error(`STOP: ${name} from the scratch folder is not the open file (${await t.ev('location.hash')}); nothing edited or saved`);
      };
      return s;
    },

    async finish() {
      for (const s of scratch) {
        try { await post('/api/roots', { action: 'remove', id: s.id }); } catch {}
        rmSync(s.dir, { recursive: true, force: true, maxRetries: 5, retryDelay: 300 });
      }
      try { ws.close(); } catch {}
      chrome.kill();
      await sleep(1500);                                   // Chrome lets go of its profile files
      try { rmSync(profile, { recursive: true, force: true, maxRetries: 10, retryDelay: 300 }); }
      catch (e) { console.log('could not delete the Chrome profile ' + profile + ': ' + e.message); }
      if (gitState() !== gitAtStart) {
        console.log('!!! THE REPO CHANGED DURING THIS SUITE: look at `git status` and `git diff` now');
        results.push(false);
      }
      console.log(`\n${results.filter(Boolean).length} of ${results.length} passed`);
      process.exit(results.length && results.every(Boolean) ? 0 : 1);
    },
  };
  return t;
}
