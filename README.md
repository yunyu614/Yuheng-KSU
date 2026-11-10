# 玉衡 · Yuheng

<p align="center">
  <strong>以玉为度，简而有序。</strong><br />
  一款以东方美学为设计语言的 Android Root 管理器
</p>

<p align="center">
  <a href="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml"><img src="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml/badge.svg?branch=develop" alt="Android Build" /></a>
  <a href="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml"><img src="https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml/badge.svg?branch=develop" alt="LKM Build" /></a>
  <a href="https://github.com/yunyu614/Yuheng-KSU/commits/develop"><img src="https://img.shields.io/github/last-commit/yunyu614/Yuheng-KSU/develop" alt="Last commit" /></a>
  <img src="https://img.shields.io/badge/Platform-Android-3F806F" alt="Android" />
  <img src="https://img.shields.io/badge/Architecture-ARM64-7894A3" alt="ARM64" />
</p>

玉衡（Yuheng）基于 KernelSU 开源生态进行二次开发，围绕 Root 授权、模块管理与内核级集成构建。项目希望在保留底层管理能力的同时，提供更清晰的操作层级、更细腻的视觉表达，以及更贴近日常使用习惯的交互体验。

> **项目声明**：玉衡是独立的非官方衍生项目，不代表 KernelSU 或其他上游项目的官方立场，也未获得其官方背书。

## 设计理念

- **东方审美**：以玉色、釉色、月色与山水意象构建品牌视觉。
- **克制而清晰**：避免大面积纯白和高饱和装饰，强调信息层级与留白。
- **明暗皆宜**：浅色模式温润通透，深色模式沉静柔和，不做简单反色。
- **功能优先**：主题与界面调整不应影响 Root 授权、模块操作和底层兼容能力。

## 主题体系

| 主题 | 色彩方向 | 风格 |
| --- | --- | --- |
| **青瓷雾** | 青釉、雾灰、柔和玉色 | 默认主题，温润清透 |
| **月白** | 月白、银青、淡蓝灰 | 清冷、简洁 |
| **黛青** | 深黛、松石、灰绿 | 沉静、低调 |
| **丹霞** | 暖陶、浅绯、柔和米色 | 温暖、有层次 |
| **琥珀** | 蜜金、米杏、棕灰 | 雅致、柔和 |

支持系统明暗模式与主题色搭配。另提供自定义壁纸入口，可从相册选择背景，也可随时恢复默认背景。壁纸作为独立背景层显示，并通过遮罩降低对文字可读性的影响。

## 功能与开发进度

| 功能 | 状态 |
| --- | --- |
| KernelSU Manager 工程与基础导航 | 已集成 |
| Root 授权管理 | 保留上游核心逻辑，持续进行设备回归 |
| 模块列表及安装 / 卸载 | 保留上游核心逻辑，持续进行设备回归 |
| LKM 三签名支持 | 已集成，CI 构建通过 |
| 玉衡首页状态卡片布局调整 | 已提交，待 APK 构建与真机验收 |
| 五套主题预设 | 已提交，待 APK 构建与真机验收 |
| 相册壁纸选择 / 恢复默认 | 已提交，待 APK 构建与真机验收 |
| 卸载入口视觉统一 | 已提交，待 APK 构建与真机验收 |
| 小屏、大字体与不同 UI 模式适配 | 待完整回归 |
| 正式版签名与发布流程 | 持续完善 |

开发状态以 `develop` 分支和 GitHub Actions 实际结果为准。界面代码提交不等于已完成真机验收；在构建和设备测试通过前，请勿将开发构建视为正式稳定版。

## 获取构建产物

项目通过 GitHub Actions 自动构建：

- [Android Manager 构建记录](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/android-build.yml)
- [LKM 构建记录](https://github.com/yunyu614/Yuheng-KSU/actions/workflows/build-yuheng-dual-lkm.yml)
- [全部 Actions 运行记录](https://github.com/yunyu614/Yuheng-KSU/actions)

进入对应构建任务，在页面底部的 **Artifacts** 区域下载产物。Artifact 有保留期限，过期后需要从新的成功构建中重新下载。

### APK 与 LKM 签名说明

Android CI 当前生成的是独立包名 `me.weishu.kernelsu.preview` 的 Debug 预览 APK，主要用于检查界面、导航和主题效果。它与正式管理器包名及正式签名身份区分开来，**不能仅凭成功安装就认定它能够与设备上的 LKM 完成签名授权**。

配套 LKM 编译时包含 KernelSU、白饭 KSU 与玉衡管理器对应的签名摘要。用于实际 Root 管理的 APK 必须使用与 LKM 配置相匹配的玉衡签名证书；发布前应核对证书摘要、包名、版本和设备兼容性。不要用 CI Debug 签名替代正式签名。

## 构建说明

Android Manager 的 CI 构建环境：

- JDK 21
- Android SDK Platform 37
- Android Build Tools 36.0.0
- Android NDK 29.0.14206865
- Gradle Wrapper（以仓库配置为准）

在本地构建 Debug APK：

```bash
cd manager
chmod +x ./gradlew
./gradlew --no-daemon :app:assembleDebug
```

构建产物位于：

```text
manager/app/build/outputs/apk/debug/
```

LKM 使用仓库中的 `.github/workflows/build-yuheng-dual-lkm.yml` 工作流构建。设备端使用前，请确认 Android 版本、内核 KMI、架构和 LKM 来源均匹配。

## 上游项目与致谢

玉衡建立在开源社区的成果之上，感谢上游作者及所有贡献者：

- [KernelSU](https://github.com/tiann/KernelSU)
- [白饭 KSU LKM](https://github.com/NekoWannn/baifan-ksu-lkm)

上游源码中的版权声明、许可证和 NOTICE 文件应继续保留。第三方依赖、资源及二进制组件分别遵循其原有许可证；玉衡自身的修改也应按仓库对应许可文件执行。

## 安全提示

Root、内核模块和系统级修改可能影响设备启动、系统稳定性及数据安全。刷入或更新前请：

1. 确认设备型号、Android 版本、内核版本及 KMI 匹配。
2. 备份重要数据，并准备可用的恢复方案。
3. 仅使用来源可信、签名匹配且校验通过的构建产物。
4. 遇到启动异常或授权异常时，优先恢复原始启动镜像并检查日志。

**请自行评估 Root 和内核修改带来的风险。**

---

<p align="center">
  玉衡 · Yuheng<br />
  <sub>以玉为度，简而有序。</sub>
</p>
