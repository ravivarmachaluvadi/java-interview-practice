"""
pyassist - Code Viewer's Python autocomplete helper (12 Oct): suggestions, parameter hints and
hover text for .py files, from Jedi (https://github.com/davidhalter/jedi), which reads the code
the way Python would - "abc". lists str's methods, a module's names come from the module.

codeview.py starts it on the first Python request, the same way as the Java helper
(CvAssist), and stops it after 20 idle minutes. It also exits by itself when its stdin closes,
so it never outlives the server.

Protocol (CvAssist's): one request is a header line of tab-separated fields
    op  offset  folder  file name  flags  extra  body length
followed by the body (the editor's text, UTF-8). The answer is one line of JSON.

    complete   {"items": [{"name", "type", "detail"}]}   at most 200
    signature  {"signatures": [{"label", "params", "active"}]}
    hover      {"text": "..."}                             "" when there is nothing to say
"""
import json
import os
import sys

MAX_ITEMS = 200
HOVER_LINES = 25


def position(code, offset):
    """Monaco's character offset -> Jedi's (line from 1, column from 0)."""
    offset = max(0, min(offset, len(code)))
    line = code.count("\n", 0, offset) + 1
    return line, offset - (code.rfind("\n", 0, offset) + 1)


def word_before(code, offset):
    i = offset
    while i > 0 and (code[i - 1].isalnum() or code[i - 1] == "_"):
        i -= 1
    return code[i:offset]


def script(jedi, code, folder, fname):
    folder = folder if folder and os.path.isdir(folder) else None
    path = os.path.join(folder, fname) if folder else None
    project = jedi.Project(folder) if folder else None          # `import helper` beside the file
    return jedi.Script(code, path=path, project=project)


def complete(jedi, code, offset, folder, fname):
    s = script(jedi, code, folder, fname)
    typed = word_before(code, offset)
    items = []
    for c in s.complete(*position(code, offset)):
        if c.name.startswith("_") and not typed.startswith("_"):
            continue                                            # dunders only when asked for
        items.append({"name": c.name, "type": c.type, "detail": c.description})
        if len(items) >= MAX_ITEMS:
            break
    return {"items": items}


def signature(jedi, code, offset, folder, fname):
    out = []
    for sig in script(jedi, code, folder, fname).get_signatures(*position(code, offset)):
        out.append({"label": sig.to_string(), "params": [p.to_string() for p in sig.params],
                    "active": sig.index if sig.index is not None else -1})
    return {"signatures": out}


def hover(jedi, code, offset, folder, fname):
    s = script(jedi, code, folder, fname)
    line, col = position(code, offset)
    for name in s.help(line, col) or s.infer(line, col):
        text = name.docstring().strip()
        if not text and name.type == "instance":
            text = name.description
        if text:
            return {"text": "\n".join(text.splitlines()[:HOVER_LINES])}
    return {"text": ""}


OPS = {"complete": complete, "signature": signature, "hover": hover}
EMPTY = {"complete": {"items": []}, "signature": {"signatures": []}, "hover": {"text": ""}}


def main():
    import jedi
    stdin, stdout = sys.stdin.buffer, sys.stdout
    while True:
        header = stdin.readline()
        if not header:
            return                                              # the server went away
        fields = header.decode("utf-8").rstrip("\r\n").split("\t")
        op, offset, folder, fname, size = fields[0], int(fields[1] or 0), fields[2], fields[3], int(fields[6])
        code = stdin.read(size).decode("utf-8", errors="replace")
        try:
            res = OPS[op](jedi, code, offset, folder, fname) if op in OPS else {"error": f"unknown request: {op}"}
        except Exception as e:                                  # Jedi on half-typed code: no answer,
            res = {**EMPTY[op], "note": f"{type(e).__name__}: {e}"}   # not an error box on every key
        stdout.write(json.dumps(res) + "\n")
        stdout.flush()


if __name__ == "__main__":
    main()
