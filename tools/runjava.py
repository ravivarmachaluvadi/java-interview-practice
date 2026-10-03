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
inside keeps its problem name, so the launcher cannot find it and reports
"can't find class" on almost all of them.

This compiles to a temp directory and then runs whichever class actually declares
main(), so the filename never matters. It also puts the file's own folder on the
sourcepath, so helper classes in sibling files resolve.

Uses the newest installed JDK rather than JAVA_HOME, which often points at an
older one. JDK 21 is enough: every standalone file compiles on it.
"""
import os, re, subprocess, sys, tempfile, pathlib

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import javasrc  # noqa: E402  shared main()/package detection, also used by codeview

for _s in (sys.stdout, sys.stderr):
    try:
        _s.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass


def find_jdk():
    """Newest installed JDK wins; JAVA_HOME often points at an older one.

    JDK 21 or newer is required. Anything older will reject switch patterns,
    records and text blocks that several files use.
    """
    exe = ".exe" if os.name == "nt" else ""
    roots = [r"C:\Program Files\Eclipse Adoptium", r"C:\Program Files\Java", r"C:\Program Files\Microsoft",
             "/Library/Java/JavaVirtualMachines", "~/Library/Java/JavaVirtualMachines",   # macOS
             "/opt/homebrew/opt", "/usr/local/opt",                                       # Homebrew
             "/usr/lib/jvm", "~/.sdkman/candidates/java", "~/.jdks"]                       # Linux, SDKMAN, IntelliJ
    homes = [pathlib.Path(os.environ["JAVA_HOME"])] if os.environ.get("JAVA_HOME") else []
    for r in roots:
        p = pathlib.Path(os.path.expanduser(r))
        if p.is_dir():
            for d in p.iterdir():
                # a macOS .jdk bundle keeps its JDK home two levels down; so does Homebrew's openjdk
                homes += [d, d / "Contents" / "Home", d / "libexec" / "openjdk.jdk" / "Contents" / "Home"]
    found, seen = [], set()
    for h in homes:
        if (h / "bin" / ("javac" + exe)).is_file() and h.resolve() not in seen:
            seen.add(h.resolve())
            found.append((jdk_major(h), h))
    if not found:
        return "javac", "java"
    found.sort(key=lambda x: x[0], reverse=True)
    home = found[0][1]
    return str(home / "bin" / ("javac" + exe)), str(home / "bin" / ("java" + exe))


def jdk_major(home):
    """25 for a JDK 25 home: from its `release` file, else the first number in its path."""
    try:
        m = re.search(r'JAVA_VERSION="(?:1\.)?(\d+)', (home / "release").read_text(errors="replace"))
        if m:
            return int(m.group(1))
    except OSError:
        pass
    m = re.search(r"(\d+)", str(home))
    return int(m.group(1)) if m else 0


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
    # The repo this script lives in, wherever it is cloned. (The IntelliJ scratches folder
    # used to be searched too, but it is a junction to this same repo, so every hit came
    # back twice and every name looked ambiguous.)
    root = pathlib.Path(__file__).resolve().parent.parent
    return [p for p in root.rglob("*.java") if needle.lower() in p.name.lower()]


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
    pkg, mains, public, compact = javasrc.analyse(text)
    if compact:                        # JDK 25 file that is just `void main()`
        mains = [src.stem]

    if list_only:
        print(f"  {src.name}")
        print(f"  classes with main(): {', '.join(mains) if mains else '(none)'}")
        return 0

    if not mains:
        sys.exit(f"No class in {src.name} declares a main() method - nothing to run.")

    # A file that declares `package a.b;` needs the package root on the sourcepath and
    # its fully qualified class name at run time - running the bare name fails.
    srcpath = src.parent
    for _ in (pkg.split(".") if pkg else []):
        srcpath = srcpath.parent
    with tempfile.TemporaryDirectory(prefix="runjava_") as out:
        compile_me = src
        if public and public + ".java" != src.name:   # javac wants a public type in <Name>.java
            compile_me = pathlib.Path(out) / "src" / (public + ".java")
            compile_me.parent.mkdir()
            compile_me.write_text(text, encoding="utf-8")
        c = subprocess.run([JAVAC, "-nowarn", "-encoding", "UTF-8", "-d", out,
                            "-sourcepath", str(srcpath), str(compile_me)],
                           capture_output=True, text=True, errors="replace")
        if c.returncode != 0:
            sys.stderr.write(c.stderr or "")
            return c.returncode
        cls = javasrc.pick_main(mains, src.name)
        if len(mains) > 1:
            print(f"  ({len(mains)} classes have main; running '{cls}'. "
                  f"Others: {', '.join(m for m in mains if m != cls)})")
        r = subprocess.run([JAVA, "-cp", out, (pkg + "." if pkg else "") + cls] + argv[1:])
        return r.returncode


if __name__ == "__main__":
    sys.exit(main())
