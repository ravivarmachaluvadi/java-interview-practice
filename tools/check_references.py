import pathlib
"""Every <TIER><NN>_Name (or pattern P<NNN>_Name) reference inside a header must name a file that exists."""
import re, pathlib, collections
ROOT = pathlib.Path(__file__).resolve().parent.parent / "AAScratches"
REF = re.compile(r"\b([A-D]\d{2}|P\d{3})(?:_(\w+))?\b")
bad = []
selfref = []
bare = []
checked = collections.Counter()   # full-form references verified, by kind
for p in sorted(ROOT.rglob("*.java")):
    rel = p.relative_to(ROOT).as_posix()
    if rel.startswith("_"): continue
    text = p.read_text(encoding="utf-8", errors="replace")
    m = re.match(r"\s*/\*.*?\*/", text, re.S)
    if not m: continue
    header = m.group(0)
    siblings = {x.stem for x in p.parent.glob("*.java")}
    by_tier = {s.split("_")[0]: s for s in siblings}
    own = p.stem.split("_")[0]
    for ref in REF.finditer(header):
        tier, name = ref.group(1), ref.group(2)
        full = ref.group(0)
        if name:                       # full form like C15_FindDuplicate
            checked["pattern" if tier[0] == "P" else "tier"] += 1
            if full not in siblings and not any(ROOT.rglob(full + ".java")):
                bad.append((rel, full))   # not in this folder and nowhere else either
            elif full == p.stem:
                selfref.append((rel, full))
        elif tier != own:              # bare tier number like "C17"; a file may name its own
            bare.append((rel, tier, by_tier.get(tier, "(no such tier slot)")))
print(f"full-form references checked: {sum(checked.values())} "
      f"({checked['tier']} tier, {checked['pattern']} pattern)")
print(f"references to files that do NOT exist: {len(bad)}")
for r, f in bad: print("   ", r, "->", f)
print(f"self-references: {len(selfref)}")
for r, f in selfref: print("   ", r, "->", f)
print(f"bare tier numbers (fragile, should carry the full name): {len(bare)}")
for r, t, res in bare[:25]: print(f"    {r} -> {t}  (currently {res})")
