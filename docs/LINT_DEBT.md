# Android Lint 技术债记录

## 当前实测基线（2026-11-11，提交 `ff865e54`）

使用 Android Lint 9.4.1 对 Debug 变体执行完整检查。提交 `ff865e54` 的 CI 报告共 **443 条发现：334 errors / 109 warnings**。

| 类别 | 数量 | 严重级别 |
| --- | ---: | --- |
| `MissingTranslation` | 334 | Error |
| `StringFormatCount` | 37 | Warning |
| `NewerVersionAvailable` | 16 | Warning |
| `UseKtx` | 13 | Warning |
| `Untranslatable` | 12 | Warning |
| `UnusedResources` | 10 | Warning |
| `IconLauncherShape` | 5 | Warning |
| `MissingOnRenderProcessGone` | 4 | Warning |
| `GradleDependency` | 3 | Warning |
| `AndroidGradlePluginVersion` | 2 | Warning |
| `ModifierParameter` | 2 | Warning |
| `ObsoleteSdkInt` | 2 | Warning |
| `PluralsCandidate` | 1 | Warning |
| `TypographyEllipsis` | 1 | Warning |
| `IconXmlAndPng` | 1 | Warning |
| **合计** | **443** | **334 errors / 109 warnings** |

相较提交 `c12402d` 的 538 条发现，之前已移除繁体中文资源目录中 30 个未使用的 DeepSeek / Token 字符串与数组，以及 3 个未使用颜色资源；`ExtraTranslation` 的 60 条 Fatal 已全部消失，未通过关闭规则或隐藏报告实现。

本轮 WebView 代码已实现 renderer 退出回调，但 Lint 仍报告 4 条 `MissingOnRenderProcessGone`（两个调用点各重复两条）；需要继续确认 AndroidX `WebViewClientCompat` 与 Lint 检测器之间的识别问题，不能把已有 override 当作告警已消失。默认 Locale 告警已从最新报告中消失。

`StringFormatCount` 目前集中在历史 `about_source_code` 资源的占位符数量差异；当前 About 页面不再使用该资源。本轮先让默认英文资源与现有本地化版本的两个格式参数数量一致，避免继续产生不一致告警。该资源仍被 Lint 标记为未使用，后续可在统一资源清理时删除所有语言目录中的对应键。

`MissingTranslation` 是多语言资源覆盖不完整导致，涉及多个既有语言目录；Android 仍会回退到默认资源。不能将其误判为 Kotlin 编译失败，也不能因此粗暴删除语言支持或用未经校对的英文覆盖翻译。

## CI 处理原则

- 单元测试和 APK 编译/打包是阻断门槛，失败即阻止产物上传。
- Lint 仍然执行，报告作为独立 Artifact 留存；当前历史问题不阻断 APK 构建。
- 不设置全局 `abortOnError = false`，不禁用 `MissingTranslation`，不把已有问题伪装成通过。
- 后续按报告逐项分类：优先处理运行时风险、资源引用错误、占位符不一致、可访问性和安全问题；翻译缺失按实际支持的语言逐步补齐。
- 当历史问题被清理后，将 Lint 恢复为阻断门槛。

## 最近工程验证

- 提交 `51a6dbd`：Kotlin 编译、单元测试、Debug APK 打包成功；Lint 报告 394 errors / 148 warnings。
- 提交 `c12402d`：增加 Markdown WebView 与模块 WebUI 的渲染进程退出清理；Kotlin 编译、单元测试、Debug APK 打包成功；Lint 报告 394 errors / 144 warnings。
- 提交 `47ee288`：清理 30 个 DeepSeek / Token 遗留资源及 3 个无引用颜色；Kotlin 编译、单元测试、Debug APK 打包成功；Lint 报告 334 errors / 111 warnings。
- 提交 `b4d1aa8`：统一 About 页面品牌名称；Kotlin 编译、单元测试、Debug APK 打包成功；Lint 报告仍为 334 errors / 111 warnings。
- 提交 `ff865e54`：壁纸导入增加文件大小、图像尺寸和总像素限制，并使用同目录原子替换；单元测试、Debug APK 构建及 `android16-6.12` LKM 构建成功；Lint 报告 334 errors / 109 warnings。
- 同一轮 LKM Artifact 的 SHA-256 校验成功。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。正式签名工作流还必须核对 APK v2 证书 SHA-256 与 LKM 信任配置一致。
