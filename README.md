# 玉衡 · Yuheng

<p align="center">
  <strong>以玉为度，简而有序。</strong><br />
  面向 Android 的 KernelSU 管理器衍生项目 · 东方未来科技美学
</p>

<p align="center">
  <a href="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml"><img src="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml/badge.svg?branch=develop" alt="Android Build" /></a>
  <a href="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml"><img src="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml/badge.svg?branch=develop" alt="LKM Build" /></a>
  <a href="https://github.com/yunyu614/Yuheng-KSU/commits/develop"><img src="https://img.shields.io/github/last-commit/yunyu614/Yuheng-KSU/develop" alt="Last commit" /></a>
  <img src="https://img.shields.io/badge/Platform-Android-3F806F" alt="Android" />
  <img src="https://img.shields.io/badge/Architecture-ARM64-7894A3" alt="ARM64" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-blue" alt="GPL-3.0" />
</p>

玉衡（Yuheng）基于 [KernelSU](https://github.com/tiann/KernelSU) 开源生态进行二次开发，围绕 Root 授权、模块管理与内核级集成构建。项目致力于在保留底层管理能力的同时，重新梳理信息层级、页面布局和交互细节，打造更清晰、更精致的 Android Root 管理体验。

> **非官方项目声明**：玉衡是独立的第三方衍生项目，不代表 KernelSU 或其他上游项目的官方立场，也未获得其官方背书。

## 开发分支说明

- **当前活跃开发分支：`develop`**。最新 Android 工程、UI 改动、主题实现、文档和 CI 均以该分支为准。
- 默认分支 `main` 主要承担仓库首页入口；若要查看最新代码，请切换到 [develop](https://github.com/yunyu614/Yuheng-KSU/tree/develop)。
- [Android 构建记录](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml) 当前生成 Debug 预览 APK。
- [LKM 构建记录](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml) 当前目标为 `android16-6.12`。
- 正式版尚未发布。CI 成功不等于完成全部设备端验证。

## 品牌与五方主题

玉衡采用**东方未来科技美学**，以中国五方神兽作为主题识别体系。重点是现代材质、精细光影、清晰的信息排布和克制的装饰，不采用古画、水墨或文物拼贴作为产品主视觉。

| 主题 | 色彩方向 | 视觉关键词 |
| --- | --- | --- |
| **青龙**（默认） | 苍青、翡翠绿 | 玉质、云海、星宿 |
| **白虎** | 霜银、冷灰 | 银钛、冰晶、山岳 |
| **玄武** | 玄黄、古铜 | 龟甲、深水、北斗 |
| **朱雀** | 朱砂红 | 凤羽、日轮、赤焰 |
| **麒麟** | 紫玉、鎏金 | 瑞鳞、紫晶、星轨 |

五套主题色彩预设已进入 `develop`。高品质神兽主体与专属背景素材仍在设计阶段，暂不使用低质量占位图。真实设备预览截图将在正式版完成后补充。

## 功能方向

- Root / KernelSU 状态展示与超级用户授权管理；
- 模块列表、安装、启停、卸载等上游管理能力；
- 玉衡主题预设、浅色/深色配色；
- 自定义壁纸选择与恢复默认入口；
- 配套三方签名 LKM 的构建与校验流程。

具体完成度、限制和验证状态请查看 [develop 分支 README](https://github.com/yunyu614/Yuheng-KSU/blob/develop/README.md)。

## 构建与安装

请从 [develop 分支](https://github.com/yunyu614/Yuheng-KSU/tree/develop) 获取当前源码。CI Debug APK 使用独立包名 `me.weishu.kernelsu.preview` 和 Debug 签名，仅用于预览，不是正式 Root 管理包。

正式签名 APK 使用 `me.weishu.kernelsu`，必须与配套 LKM 的受信任证书匹配。不要把 Debug 包当作正式版，也不要在设备 KMI 不匹配时刷入 LKM。

- [Android Debug 构建](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml)
- [签名 Release 构建工作流](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-release.yml)
- [LKM 构建工作流](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml)
- [LKM 签名与兼容说明](https://github.com/yunyu614/Yuheng-KSU/blob/develop/LKM-README.md)

## 文档与协作

- [项目完整说明](https://github.com/yunyu614/Yuheng-KSU/blob/develop/README.md)
- [视觉方向规范](https://github.com/yunyu614/Yuheng-KSU/blob/develop/docs/YUHENG_VISUAL_DIRECTION.md)
- [发布验收清单](https://github.com/yunyu614/Yuheng-KSU/blob/develop/docs/RELEASE_ACCEPTANCE.md)
- [贡献指南](https://github.com/yunyu614/Yuheng-KSU/blob/develop/CONTRIBUTING.md)
- [安全策略](https://github.com/yunyu614/Yuheng-KSU/blob/develop/SECURITY.md)
- [变更记录](https://github.com/yunyu614/Yuheng-KSU/blob/develop/CHANGELOG.md)
- [当前 Android 启动图标源码](https://github.com/yunyu614/Yuheng-KSU/blob/develop/manager/app/src/main/res/drawable/ic_launcher_foreground.xml)

## 上游与许可证

感谢 [KernelSU](https://github.com/tiann/KernelSU) 与 [白饭 KSU LKM](https://github.com/NekoWannn/baifan-ksu-lkm) 等上游项目及开源社区贡献者。上游源码和第三方依赖继续遵循各自许可证与版权声明。仓库根目录提供 GNU GPL v3 许可证文本；具体文件如附有单独声明，应以对应文件及其来源为准。

## 安全提示

Root 和内核模块具有高权限，错误的 APK、签名或 LKM 可能导致授权失效、系统不稳定甚至无法启动。刷入前请核对设备架构、内核 KMI、包名、签名证书和构建来源，并备份重要数据及启动镜像。

---

<p align="center">
  <strong>玉衡 · Yuheng</strong><br />
  <sub>以玉为度，简而有序。</sub>
</p>
