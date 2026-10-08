"""
offline - a copy of the page's three libraries on this PC, so codeview works without internet.

    ensure(log)      download whatever is missing (codeview runs this in the background at start)
    url_for(lib)     the local /vendor/... URL once the copy is complete, else the cdnjs URL
    path_for(rel)    the file behind /vendor/<rel>, refusing anything outside a complete copy

The copies come from the npm registry, checked against the registry's sha512, and live
outside the repo (they are 17 MB and OneDrive does not need them):
    Windows  %LOCALAPPDATA%\\CodeViewer\\vendor     macOS  ~/Library/Caches/CodeViewer/vendor
    Linux    $XDG_CACHE_HOME/codeview/vendor (~/.cache)
CODEVIEW_VENDOR overrides the folder. Delete it to make codeview download again.
"""
import base64, collections, hashlib, io, json, os, pathlib, shutil, sys, tarfile, urllib.request

# npm package, version, the part of the package the page needs ("dir/" or a file), cdnjs URL.
Lib = collections.namedtuple("Lib", "npm version member cdn")
MONACO = Lib("monaco-editor", "0.52.2", "min/", "https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.52.2/min")
MARKED = Lib("marked", "18.0.14", "lib/marked.umd.js",
             "https://cdnjs.cloudflare.com/ajax/libs/marked/18.0.14/lib/marked.umd.min.js")
# 8 Oct: draws the ```mermaid blocks in .md pages. 11.15.0 is the newest that cdnjs has; only the
# one 3.5 MB bundle is kept from the 16 MB package. It sets globalThis.mermaid (not UMD), so the
# page can load it on demand even after Monaco's AMD loader is in place.
MERMAID = Lib("mermaid", "11.15.0", "dist/mermaid.min.js",
              "https://cdnjs.cloudflare.com/ajax/libs/mermaid/11.15.0/mermaid.min.js")
LIBS = (MONACO, MARKED, MERMAID)
DONE = ".complete"


def cache_dir():
    if os.environ.get("CODEVIEW_VENDOR"):
        return pathlib.Path(os.environ["CODEVIEW_VENDOR"])
    if os.name == "nt":
        return pathlib.Path(os.environ.get("LOCALAPPDATA") or pathlib.Path.home()) / "CodeViewer" / "vendor"
    if sys.platform == "darwin":
        return pathlib.Path.home() / "Library" / "Caches" / "CodeViewer" / "vendor"
    return pathlib.Path(os.environ.get("XDG_CACHE_HOME") or pathlib.Path.home() / ".cache") / "codeview" / "vendor"


def lib_dir(lib):
    return cache_dir() / f"{lib.npm}@{lib.version}"


def ready(lib):
    return (lib_dir(lib) / DONE).exists()


def url_for(lib):
    return f"/vendor/{lib.npm}@{lib.version}/{lib.member.rstrip('/')}" if ready(lib) else lib.cdn


def path_for(rel):
    """/vendor/<rel> -> a file inside a complete copy. ValueError for anything else."""
    parts = rel.replace("\\", "/").split("/")
    if not rel or ".." in parts or parts[0] in ("", "."):
        raise ValueError("bad vendor path")
    base = (cache_dir() / parts[0]).resolve()
    if not (base / DONE).exists():
        raise ValueError("not downloaded")
    p = (cache_dir() / rel).resolve()
    if base not in p.parents or not p.is_file():
        raise ValueError("no such vendor file")
    return p


def install(lib, tgz, integrity):
    """Check the tarball against the registry's sha512, then unpack only lib.member."""
    algo, _, want = integrity.partition("-")
    if algo != "sha512" or base64.b64encode(hashlib.sha512(tgz).digest()).decode() != want:
        raise ValueError(f"{lib.npm} {lib.version}: download does not match the registry checksum")
    final = lib_dir(lib)
    tmp = final.with_name(final.name + f".tmp-{os.getpid()}")
    shutil.rmtree(tmp, ignore_errors=True)
    tmp.mkdir(parents=True)
    root = tmp.resolve()
    with tarfile.open(fileobj=io.BytesIO(tgz), mode="r:gz") as t:
        for m in t.getmembers():
            name = m.name[len("package/"):] if m.name.startswith("package/") else None
            if not m.isfile() or not name or ".." in name.split("/"):
                continue
            if not (name == lib.member or (lib.member.endswith("/") and name.startswith(lib.member))):
                continue
            dest = (tmp / name).resolve()
            if root not in dest.parents:
                continue
            dest.parent.mkdir(parents=True, exist_ok=True)
            dest.write_bytes(t.extractfile(m).read())
    (tmp / DONE).write_text(f"{lib.npm} {lib.version}\n", encoding="utf-8")
    shutil.rmtree(final, ignore_errors=True)
    os.replace(tmp, final)


def fetch(lib, timeout=60):
    """-> (tarball bytes, registry integrity string)."""
    with urllib.request.urlopen(f"https://registry.npmjs.org/{lib.npm}/{lib.version}", timeout=timeout) as r:
        dist = json.load(r)["dist"]
    with urllib.request.urlopen(dist["tarball"], timeout=timeout) as r:
        return r.read(), dist["integrity"]


def ensure(log=None):
    """Download every library that is not here yet. Never raises: no internet just means
    the page keeps using cdnjs, and the next start tries again."""
    say = log or (lambda msg: None)
    for stale in cache_dir().glob("*.tmp-*") if cache_dir().is_dir() else []:
        shutil.rmtree(stale, ignore_errors=True)          # a download cut off last time
    for lib in LIBS:
        if ready(lib):
            continue
        try:
            install(lib, *fetch(lib))
            say(f"offline copy: downloaded {lib.npm} {lib.version} to {lib_dir(lib)}")
        except Exception as e:                            # noqa: BLE001 - any failure means "try next start"
            say(f"offline copy: could not download {lib.npm} {lib.version} ({e}); the page keeps using cdnjs")
