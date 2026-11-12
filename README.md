# 玉衡 · Yuheng

<p align="center"><sub>应用图标源码：<a href="manager/app/src/main/res/drawable/ic_launcher_foreground.xml">青龙主题标识（Android Vector Drawable）</a></sub></p>

<p align="center">
  <strong>以玉为度，简而有序。</strong><br />
  面向 Android 的 KernelSU 管理器衍生项目 · 东方未来科技美学
</p>

<p align="center">
  <a href="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml"><img src="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml/badge.svg?branch=develop" alt="Android Build" /></a>
  <a href="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml"><img src="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml/badge.svg?branch=develop" alt="LKM Build" /></a>
  <a href="https://github.com/yunyu614/Yuheng-KSU/commits/develop"><img src="https://img.shields.io/github/last-commit/yunyu614/Yuheng-KSU/develop" alt="Last commit" /></a>
  <img src="https://img.shields.io/badge/Platform-Android-3F806F" alt="Android" />
  <img src="https://img.shields.io/badge/Minimum_Android-12%2B-7894A3" alt="Minimum Android 12+" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-blue" alt="GPL-3.0" />
</p>

玉衡（Yuheng）基于 [KernelSU](https://github.com/tiann/KernelSU) 开源生态进行二次开发，围绕 Root 授权、模块管理与内核级集成构建。项目在保留底层管理能力的同时，重新梳理信息层级、页面布局和交互细节，探索更有辨识度、更易读且更精致的 Android Root 管理体验。

> **非官方项目声明**：玉衡是独立的第三方衍生项目，不代表 KernelSU 或其他上游项目的官方立场，也未获得其官方背书。

## 项目状态

- 当前主要开发分支：`develop`
- Android CI：在推送到 `develop`、面向 `develop` 的 Pull Request 和手动触发时构建 Debug 预览 APK，并运行单元测试；Lint 报告独立留存。
- LKM CI：当前工作流构建目标为 `android16-6.12`。
- 正式版：尚未发布 GitHub Release。签名 Release 工作流需要配置仓库 Actions Secrets，并在发布前完成设备验证。
- UI 截图：暂不提供占位图或设计稿；正式版完成后再补充真实设备截图。

**重要：** GitHub Actions 显示成功只代表对应工作流步骤通过，不等于所有设备功能均已完成真机验证，也不代表当前 Debug APK 可用于正式 Root 管理。

## 设计与主题

玉衡的视觉方向是**东方未来科技美学**：以青龙为首发主题识别，采用现代材质、清晰的信息排布和克制的光影表达。不是古画、水墨、文物拼贴，也不是普通 Material 配色替换。

| 主题 | 主色与识别方向 | 当前状态 | 视觉关键词 |
| --- | --- | --- | --- |
| **青龙 · 苍璧** | 苍青、翡翠绿 | 首发可用 | 玉质、云海、星宿 |
| **白虎 · 霜华** | 霜银、钛银 | 待开发 | 虎纹、冷雾、雪岭 |
| **玄武 · 玄甲** | 玄黄、琥珀金 | 待开发 | 龟甲、深水、北斗 |
| **朱雀 · 丹曜** | 朱砂红、暖金 | 待开发 | 凤羽、日轮、赤色云层 |
| **麒麟 · 紫宸** | 紫玉、香槟金 | 待开发 | 瑞鳞、星云、金色轨迹 |

主题选择界面保留五神兽完整阵列：**青龙 · 苍璧**为首发可用主题；其余四主题仅展示各自的配色与设计方向，并明确标注「待开发」，不可切换或误认为已交付。

首发视觉仍在打磨：当前以青龙主题色、文字和轻量氛围层建立识别；运行界面不再显示程序化 Canvas 神兽线稿或简化矢量轮廓冒充正式主视觉。五神兽高品质主体图与专属场景背景按当前决定暂缓；此前候选图片未通过视觉验收，不会直接打包进 APK。待统一制作并确认授权、清晰度、构图与手机端可读性后再接入。

## 功能范围

| 模块 | 当前说明 |
| --- | --- |
| Root / KernelSU 状态 | 基于上游管理器能力，需在匹配的设备与内核环境中验证 |
| 超级用户管理 | 保留上游授权管理链路；需真机回归 |
| 模块管理 | 保留上游模块列表、安装、启停和卸载能力；需真机回归 |
| 主题与昼夜模式 | 青龙·苍璧首发可用；其余四主题保留入口展示并标注待开发；浅色/深色配色已集成 |
| UI 实现 | Material / Miuix 两种 UI 模式均保留原有管理功能，并接入青龙主题；仍需真机回归 |
| 悬浮底栏与玻璃材质 | 两种 UI 模式均提供独立开关；玻璃透射效果和系统安全区仍需真机验收 |
| 自定义壁纸 | Material / Miuix 设置页通过 OpenDocument 持久化只读 URI；主题页会校验文件大小与图像尺寸后复制到应用私有目录；两条路径共用 settings 偏好键并支持恢复默认，仍需真机回归 |
| 三方签名 LKM | 仓库提供 `android16-6.12` 目标的构建工作流，具体设备兼容性取决于 KMI 和内核环境 |

## 支持范围与前置条件

- **最低 Android 版本：** Android 12（API 31），以 `manager/build.gradle.kts` 的 `minSdk` 配置为准。
- **APK 内置 Native ABI：** `arm64-v8a`、`x86_64`、`riscv64`。这仅表示应用构建时打包的 ABI，不代表这些架构上的 Root、KernelSU 或 LKM 均已验证可用。
- **Root 前置条件：** 设备必须运行与其内核环境匹配的 KernelSU 内核集成或经确认兼容的 LKM；仅安装 APK 不会获得 Root。
- **当前 LKM 构建范围：** 仅 `android16-6.12`。Android 版本相同不等于 KMI、架构或内核配置相同，不应将该产物视为通用模块。
- **设备兼容性：** 目前没有发布经过真机验收的设备清单；具体机型、内核、KMI、签名配对与安装结果均应视为待验证，直至完成设备验收。

## 获取构建产物

请从以下工作流进入具体运行记录，在页面底部的 **Artifacts** 区域下载产物：

- [Android Manager 构建记录](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml)
- [LKM 构建记录](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml)
- [签名 Release 构建工作流](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-release.yml)
- [全部 Actions 运行记录](https://github.com/yunyu614/Yuheng-KSU/actions)

Artifact 有保留期限，过期后需要从新的成功构建中重新下载。

### APK 包名与签名

当前 Android CI 使用以下参数构建 Debug 预览包：

- 应用名称：`玉衡预览`
- Application ID：`me.weishu.kernelsu.preview`
- 签名：Android Debug 签名
- Artifact：`yuheng-preview-debug-apk`

它用于预览界面、导航和基础交互，**不能替代正式管理器 APK，也不能据此认定与设备上的 LKM 签名授权匹配**。

正式签名工作流使用 `me.weishu.kernelsu`，并从仓库 Secrets 读取签名材料。配套 LKM 的三方签名摘要见 [LKM-README.md](LKM-README.md)。请勿公开或提交 keystore、密码、私钥或签名原始材料；正式构建前必须核对 APK v2 证书摘要与 LKM 配置一致。

## 本地构建

### 环境要求

CI 当前使用 JDK 21、Android SDK Platform 37、Build Tools 36.0.0 和 NDK 29.0.14206865。Gradle 版本以仓库 Wrapper 配置为准。

### 构建 Debug APK

```bash
cd manager
chmod +x ./gradlew
./gradlew --no-daemon :app:assembleDebug \
  -PKSU_NAME="玉衡预览" \
  -PKSU_PACKAGE_NAME="me.weishu.kernelsu.preview"
```

默认产物位于：

```text
manager/app/build/outputs/apk/debug/
```

### 构建签名 Release

仓库提供手动触发的 [android-release.yml](.github/workflows/android-release.yml)。运行前需在 GitHub 仓库 Actions Secrets 中配置：

- `YUHENG_KEYSTORE_BASE64`
- `YUHENG_KEYSTORE_PASSWORD`
- `YUHENG_KEY_ALIAS`
- `YUHENG_KEY_PASSWORD`

工作流会执行 Release 构建、APK 签名校验并上传 Artifact。配置 Secrets 和成功构建不等于已完成设备验收；首次正式分发前仍需验证安装升级、签名身份、Root 授权和配套 LKM。

### LKM

当前仓库工作流只声明构建 `android16-6.12` 目标。**Android 版本号相同不代表 KMI 一致**；刷入前必须核对设备内核 KMI、架构、内核配置和模块来源。详细参数、证书摘要与产物说明见 [LKM-README.md](LKM-README.md)。

## 安全与风险

Root 管理器和内核模块具有高权限，错误的 APK、签名或 LKM 可能导致授权失效、系统不稳定甚至无法启动。操作前请备份数据和启动镜像，并准备恢复方案；只使用可信来源、校验通过且与设备环境匹配的产物。

发现疑似安全漏洞，请优先按 [SECURITY.md](SECURITY.md) 的说明私下报告，不要在公开 Issue 中发布可利用细节、日志中的个人数据或密钥。

## 项目文档

- [视觉方向规范](docs/YUHENG_VISUAL_DIRECTION.md)
- [LKM 构建与签名说明](LKM-README.md)
- [贡献指南](CONTRIBUTING.md)
- [安全策略](SECURITY.md)
- [发布验收清单](docs/RELEASE_ACCEPTANCE.md)
- [Android Lint 技术债与 CI 处理原则](docs/LINT_DEBT.md)
- [变更记录](CHANGELOG.md)

## 上游项目与致谢

玉衡建立在开源社区的成果之上，感谢上游作者及贡献者：

- [KernelSU](https://github.com/tiann/KernelSU)
- [白饭 KSU LKM](https://github.com/NekoWannn/baifan-ksu-lkm)

上游源码、第三方依赖和资源继续遵循各自原有的版权声明与许可证。仓库根目录的 [LICENSE](LICENSE) 为 GNU GPL v3；`kernel/` 子目录保留 GNU GPL v2（见 `kernel/LICENSE`）。具体文件如附有单独许可或版权声明，应以该文件及其上游许可为准。

## English

**Yuheng** is an independent Android KernelSU manager derivative focused on a refined, modern Chinese-inspired interface. It preserves upstream root-management workflows and ships the Azure Dragon · Cangbi theme first. White Tiger, Black Tortoise, Vermilion Bird, and Qilin are future theme plans, not features exposed in the current release.

The project is under active development. CI preview builds are not production releases. The four planned themes remain visible as disabled palette previews and are not selectable. Device compatibility, signing, and LKM pairing must be verified for each target environment before installation. See the Chinese documentation above for build instructions, security notes, and contribution guidelines.

---

<p align="center">
  <strong>玉衡 · Yuheng</strong><br />
  <sub>以玉为度，简而有序。</sub>
</p>
