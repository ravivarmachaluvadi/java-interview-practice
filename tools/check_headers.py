import pathlib
"""Mechanical hygiene check of practice files against the header template.
usage: python hygiene.py [--modified-only]
"""
import re, sys, pathlib, subprocess, json
ROOT = pathlib.Path(__file__).resolve().parent.parent / "AAScratches"
PACKAGED = ("orders-springboot-project", "WorkFlowExecutor")
SECTIONS_DSA = ["PROBLEM", "EXAMPLE", "APPROACH", "KEY INSIGHT", "COMPLEXITY", "INTERVIEW FOLLOW-UPS", "RUN"]

def files():
    if "--modified-only" in sys.argv:
        out = subprocess.run(["git", "status", "--short", "--", "."], capture_output=True, text=True, cwd=str(ROOT)).stdout
        for line in out.splitlines():
            if line[:2].strip() in ("M", "A", "AM", "MM") and line.endswith(".java"):
                yield ROOT / line[3:].strip().strip('"')
    else:
        yield from sorted(p for p in ROOT.rglob("*.java") if not str(p.relative_to(ROOT)).startswith("_"))

def parse_header(text):
    m = re.match(r"\s*/\*(.*?)\*/", text, re.S)
    if not m: return None
    body = m.group(1)
    lines = [re.sub(r"^\s*\*\s?", "", l) for l in body.splitlines()]
    title = meta = ""; must = False
    for i, l in enumerate(lines):
        if re.match(r"=+\s*$", l.strip()):
            for j in range(i + 1, min(i + 3, len(lines))):
                if lines[j].strip():
                    parts = re.split(r"\s{2,}", lines[j].strip())
                    title = parts[0]
                    rest = " ".join(parts[1:])
                    must = "MUST-KNOW" in rest
                    meta = rest.replace("MUST-KNOW", "").strip(" |")
                    break
            break
    sections = []
    for l in lines:
        s = re.match(r"^([A-Z][A-Z /,&-]+?)\s*(?:\(.*\))?\s*$", l)
        if s and len(s.group(1).strip()) >= 3 and not re.match(r"=+$", s.group(1).strip()):
            sections.append(s.group(1).strip())
    insight = ""
    for i, l in enumerate(lines):
        if l.strip().startswith("KEY INSIGHT") or l.strip().startswith("KEY DECISIONS"):
            buf = []
            for k in lines[i + 1:]:
                if not k.strip(): break
                buf.append(k.strip())
            insight = " ".join(buf)
            break
    return {"title": title, "meta": meta, "mustKnow": must, "sections": sections, "insight": insight}

def check(p):
    rel = p.relative_to(ROOT).as_posix()
    text = p.read_text(encoding="utf-8", errors="replace")
    issues = []
    packaged = any(k in rel for k in PACKAGED)
    if not re.match(r"\s*/\*", text): issues.append("header-not-first")
    h = parse_header(text)
    if not h: issues.append("no-header")
    else:
        if not h["title"]: issues.append("no-title-line")
        if not packaged and "Tricky-MCQ" not in rel:
            if not ({"KEY INSIGHT", "KEY DECISIONS"} & set(h["sections"])):
                issues.append("missing:KEY INSIGHT/DECISIONS")
    nonascii = [i + 1 for i, l in enumerate(text.splitlines()) if any(ord(c) > 127 for c in l)]
    if nonascii: issues.append(f"non-ascii-lines:{nonascii[:5]}")
    longl = [i + 1 for i, l in enumerate(text.splitlines()) if len(l) > 100]
    if longl: issues.append(f"lines>100:{longl[:5]}")
    if "IO.println" in text or "IO.print(" in text: issues.append("IO.println")
    if not packaged:
        # a public top-level type must live in a file of the same name
        for m in re.finditer(r"^\s*public\s+(?:final\s+|abstract\s+|sealed\s+)*(?:class|interface|enum|record)\s+(\w+)", text, re.M):
            if m.group(1) != p.stem:
                issues.append(f"public-type-name-mismatch:{m.group(1)}")
        if re.search(r"(class|interface|enum|record)\s+[A-D]\d{2}_", text):
            issues.append("class-name-carries-tier-prefix")
    if not packaged:
        if re.search(r"^\s*package\s", text, re.M): issues.append("package-line")
        if "Tricky-MCQ" not in rel and not re.search(r"public\s+static\s+void\s+main\s*\(", text): issues.append("no-public-main")
    return rel, h, issues

if __name__ == "__main__":
    rows = []; bad = 0
    for p in files():
        rel, h, issues = check(p)
        rows.append({"file": rel, "header": h, "issues": issues})
        if issues:
            bad += 1; print(f"{rel}: {', '.join(issues)}")
    print(f"\n{len(rows)} files checked, {bad} with issues")
    pathlib.Path(__file__).with_name("hygiene.json").write_text(json.dumps(rows, indent=1), encoding="utf-8")
