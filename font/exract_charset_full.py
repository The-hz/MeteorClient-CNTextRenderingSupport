import json
import os

chars = set()

# 1. 从 MC 和 Meteor 的语言文件提取字符
for path in ["minecraft_zh_cn.json", "meteor_zh_cn.json"]:
    if os.path.exists(path):
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
            for v in data.values():
                chars.update(v)
        print(f"从 {path} 提取，当前共 {len(chars)} 个字符")

# 2. 如果有 gb2312.txt，读取它（向后兼容）
if os.path.exists("gb2312.txt"):
    with open("gb2312.txt", encoding="utf-8") as f:
        chars.update(f.read())
    print(f"加 GB2312 后，当前共 {len(chars)} 个字符")

# 3. 如果提供了完整的 cjk.txt，读取它
if os.path.exists("cjk.txt"):
    with open("cjk.txt", encoding="utf-8") as f:
        chars.update(f.read())
    print(f"加 cjk.txt 后，当前共 {len(chars)} 个字符")
else:
    # 如果没提供 cjk.txt，代码自动生成完整的 CJK 统一表意文字 (U+4E00 到 U+9FFF，共 20902 字)
    print("未找到 cjk.txt，自动生成完整的 CJK 基本区字符 (U+4E00 - U+9FFF)...")
    for cp in range(0x4E00, 0xA000):
        chars.add(chr(cp))

# 4. 剔除 ASCII 与已打包的拉丁/希腊/西里尔段
keep = []
for c in chars:
    cp = ord(c)
    if cp <= 0x7F:          continue # ASCII
    if 0xA0 <= cp <= 0x17F: continue # Latin-1 Supplement
    if 0x180 <= cp <= 0x24F: continue # Latin Extended-A/B
    if 0x370 <= cp <= 0x3FF: continue # Greek
    if 0x400 <= cp <= 0x4FF: continue # Cyrillic
    keep.append(cp)

keep = sorted(set(keep))
print(f"最终保留 {len(keep)} 个码点（主要是 CJK）")

# 5. 写入 charset.txt
with open("charset_full.txt", "w", encoding="utf-8") as f:
    f.write("".join(chr(cp) for cp in keep))

print(f"已生成 charset_full.txt，放在 {os.path.abspath('charset_full.txt')}")
