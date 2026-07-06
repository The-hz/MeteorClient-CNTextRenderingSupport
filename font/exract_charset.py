import json
import os

def gen_gb2312_chars():
    """按 GB2312 编码范围生成全部 6763 个汉字，零外部依赖"""
    chars = []
    # 一级汉字 16-55 区 (0xB0-0xD7)，二级汉字 56-87 区 (0xD8-0xF7)
    # 每区 94 位 (0xA1-0xFE)，55 区末尾 5 位为空位会自动被 try/except 跳过
    for high in range(0xB0, 0xF8):        # 0xB0 到 0xF7
        for low in range(0xA1, 0xFF):     # 0xA1 到 0xFE
            try:
                ch = bytes([high, low]).decode('gb2312')
                chars.append(ch)
            except (UnicodeDecodeError, ValueError):
                pass    # 空位或非汉字位，跳过
    return chars

# 1. 从 MC 和 Meteor 的语言文件提取字符
chars = set()
for path in ["minecraft_zh_cn.json", "meteor_zh_cn.json"]:
    if os.path.exists(path):
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
            for v in data.values():
                chars.update(v)
        print(f"从 {path} 提取，当前共 {len(chars)} 个字符")

# 2. 加 GB2312 兜底（代码生成，不需要外部文件）
chars.update(gen_gb2312_chars())
print(f"加 GB2312 后共 {len(chars)} 个字符")

# 3. 剔除 ASCII 与已打包的拉丁/希腊/西里尔段
keep = []
for c in chars:
    cp = ord(c)
    if cp <= 0x7F:          continue    # ASCII
    if 0xA0 <= cp <= 0x17F: continue    # Latin-1 Supplement
    if 0x180 <= cp <= 0x24F: continue   # Latin Extended-A/B
    if 0x370 <= cp <= 0x3FF: continue   # Greek
    if 0x400 <= cp <= 0x4FF: continue   # Cyrillic
    keep.append(cp)

keep = sorted(set(keep))
print(f"最终保留 {len(keep)} 个码点")

with open("charset.txt", "w", encoding="utf-8") as f:
    f.write("".join(chr(cp) for cp in keep))
print(f"已生成 charset.txt")