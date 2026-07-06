# 中文

# MeteorClient 中文渲染支持

为 [MeteorClient](https://github.com/MeteorDevelopment/meteor-client) 添加中文文本渲染支持的 Fabric Mod。
本插件是从[原仓库](https://github.com/The-hz/Meteor-Client-CNSupport)仓库搬来的，由于我懒得写，所以本插件是VibeCoding的产物。

## 功能

- 通过 **Mixin** 对原版 MeteorClient 进行修改，使其正确渲染中文字符
- 修复中文显示乱码/方块字的问题
- 与原版 MeteorClient 兼容，无需修改核心源码

## 建议搭配使用

为获得完整的中文体验，建议同时安装：

- **[Meteor I18n Support Plugin](https://github.com/dingzhen-vape/Meteor-I18n-Support-plugin)**  
  提供 MeteorClient 的界面汉化（GUI 翻译），与本 Mod 的中文渲染能力互补。

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/) 与对应版本的 Fabric API
2. 将本 Mod 放入 `mods` 文件夹
3. （可选但推荐）将 [Meteor I18n Support Plugin](https://github.com/dingzhen-vape/Meteor-I18n-Support-plugin) 一同放入 `mods` 文件夹
4. 启动游戏

## 依赖

- Minecraft `对应版本+Fabric`
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [MeteorClient](https://github.com/MeteorDevelopment/meteor-client)

## 开源协议

[MIT](LICENSE)

---

# English

# MeteorClient Chinese Rendering Support

A Fabric mod that adds Chinese text rendering support for [MeteorClient](https://github.com/MeteorDevelopment/meteor-client).
This plugin was ported from the Meteor-Client-CNSupport repository. I was too lazy to write it myself, so this plugin is pure vibe coding.

## Features

- Modifies the original MeteorClient via **Mixin** to render Chinese characters correctly
- Fixes garbled / tofu text issues for CJK characters
- Compatible with the original MeteorClient without modifying its core source code

## Recommended Companion Mod

For a complete Chinese localization experience, it is recommended to install alongside:

- **[Meteor I18n Support Plugin](https://github.com/dingzhen-vape/Meteor-I18n-Support-plugin)**  
  Provides GUI translation for MeteorClient, complementing this mod's Chinese rendering capability.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) and the corresponding Fabric API version
2. Place this mod into your `mods` folder
3. (Optional but recommended) Also place [Meteor I18n Support Plugin](https://github.com/dingzhen-vape/Meteor-I18n-Support-plugin) into the `mods` folder
4. Launch the game

## Dependencies

- Minecraft `TrueVersion+Fabric` (or corresponding version)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [MeteorClient](https://github.com/MeteorDevelopment/meteor-client)

## License

[MIT](LICENSE)
