#!/usr/bin/env python3
"""
runjava - compile and run a single practice file, whatever it is called inside.

    runjava path/to/A01_ArraySortedOrNot.java
    runjava A01_ArraySortedOrNot.java arg1 arg2
    runjava --find TwoSum                 locate files by name, then run one
    runjava --list-mains path/to/File.java   show which classes have a main()

WHY THIS EXISTS
`java Foo.java` (the JDK single-file launcher) looks for a class named after the
FILE. These files are deliberately named `<TIER><NN>_<Name>.java` while the class
inside keeps its original name, so the launcher cannot find it and reports
"can't find class". Roughly 113 of 643 files hit that.

This compiles to a temp directory and then runs whichever class actually declares
main(), so the filename never matters. It also puts the file's own folder on the
sourcepath, so helper classes in sibling files resolve.

Uses the newest installed JDK, not JAVA_HOME - these files need Java 25
(java.lang.IO.println, and `static void main` without `public`).
"""
import os, re, subprocess, sys, tempfile, pathlib

for _s in (sys.stdout, sys.stderr):
    try:
        _s.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass


def find_jdk():
    """Newest installed JDK wins; JAVA_HOME often points at an older one."""
    roots = [r"C:\Program Files\Eclipse Adoptium", r"C:\Program Files\Java",
             r"C:\Program Files\Microsoft", os.path.expanduser("~/.jdks")]
    found = []
    for r in roots:
        p = pathlib.Path(r)
        if not p.is_dir():
            continue
        for d in p.iterdir():
            if (d / "bin" / "javac.exe").is_file():
                m = re.search(r"(\d+)", d.name)
                found.append((int(m.group(1)) if m else 0, d))
    if not found:
        return "javac", "java"
    found.sort(key=lambda x: x[0], reverse=True)
    home = found[0][1]
    return str(home / "bin" / "javac.exe"), str(home / "bin" / "java.exe")


JAVAC, JAVA = find_jdk()


def classes_with_main(text):
    """Top-level class names whose body declares main(), outermost-brace aware."""
    out = []
    for m in re.finditer(r"^\s*(?:public\s+|final\s+|abstract\s+|sealed\s+)*"
                         r"(?:class|enum|record)\s+(\w+)", text, re.M):
        name = m.group(1)
        start = text.find("{", m.end())
        if start == -1:
            continue
        depth = 0
        for i in range(start, len(text)):
            if text[i] == "{":
                depth += 1
            elif text[i] == "}":
                depth -= 1
                if depth == 0:
                    if re.search(r"static\s+void\s+main\s*\(", text[start:i]):
                        out.append(name)
                    break
    return out


def find_files(needle):
    roots = [pathlib.Path(os.path.expanduser(p)) for p in (
        r"~\OneDrive\Documents\github\java-interview-practice",
        r"~\AppData\Roaming\JetBrains\IdeaIC2025.2\scratches")]
    hits = []
    for r in roots:
        if r.is_dir():
            hits += [p for p in r.rglob("*.java") if needle.lower() in p.name.lower()]
    return hits


def main():
    argv = sys.argv[1:]
    if not argv or argv[0] in ("-h", "--help"):
        print(__doc__)
        return 0

    if argv[0] == "--find":
        if len(argv) < 2:
            sys.exit("--find needs a name fragment")
        hits = find_files(argv[1])
        if not hits:
            print("  no match")
            return 1
        for h in hits[:40]:
            print("  " + str(h))
        print(f"\n  {len(hits)} match(es). Run one with:  runjava \"<path>\"")
        return 0

    list_only = argv[0] == "--list-mains"
    if list_only:
        argv = argv[1:]

    src = pathlib.Path(argv[0])
    if not src.is_file():
        hits = find_files(argv[0])
        if len(hits) == 1:
            src = hits[0]
            print(f"  resolved -> {src}")
        elif len(hits) > 1:
            print(f"  '{argv[0]}' is ambiguous ({len(hits)} matches):")
            for h in hits[:15]:
                print("    " + str(h))
            return 1
        else:
            sys.exit(f"Not a file and no match found: {argv[0]}")

    text = src.read_text(encoding="utf-8", errors="replace")
    mains = classes_with_main(text)

    if list_only:
        print(f"  {src.name}")
        print(f"  classes with main(): {', '.join(mains) if mains else '(none)'}")
        return 0

    if not mains:
        sys.exit(f"No class in {src.name} declares a main() method - nothing to run.")

    with tempfile.TemporaryDirectory(prefix="runjava_") as out:
        c = subprocess.run([JAVAC, "-nowarn", "-encoding", "UTF-8", "-d", out,
                            "-sourcepath", str(src.parent), str(src)],
                           capture_output=True, text=True, errors="replace")
        if c.returncode != 0:
            sys.stderr.write(c.stderr or "")
            return c.returncode
        cls = mains[0]
        if len(mains) > 1:
            print(f"  ({len(mains)} classes have main; running '{cls}'. "
                  f"Others: {', '.join(mains[1:])})")
        r = subprocess.run([JAVA, "-cp", out, cls] + argv[1:])
        return r.returncode


if __name__ == "__main__":
    sys.exit(main())
