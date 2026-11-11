# Android Lint 技术债记录

## 当前实测基线（2026-11-11，提交 `2aec48e6`）

使用 Android Lint 9.4.1 对 Debug 变体执行完整检查。提交 `2aec48e6` 的 SARIF 报告包含 **407 条发现：334 errors / 73 warnings**。

| 类别 | 数量 | 严重级别 |
| --- | ---: | --- |
| `MissingTranslation` | 334 | Error |
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
| `StringFormatCount` | 1 | Warning |
| `TypographyEllipsis` | 1 | Warning |
| `IconXmlAndPng` | 1 | Warning |
| **合计** | **407** | **334 errors / 73 warnings** |

相较提交 `c12402d` 的 538 条发现，之前已移除繁体中文资源目录中 30 个未使用的 DeepSeek / Token 字符串与数组，以及 3 个未使用颜色资源；`ExtraTranslation` 的 60 条 Fatal 已全部消失，未通过关闭规则或隐藏报告实现。

`MissingTranslation` 是多语言资源覆盖不完整导致，涉及多个既有语言目录；Android 仍会回退到默认资源。不能将其误判为 Kotlin 编译失败，也不能因此粗暴删除语言支持或用未经校对的英文覆盖翻译。

当前 `Untranslatable` 主要来自默认资源将 `profile` 标记为不可翻译、但多个语言目录已经提供本地化文本；另外 `settings_adb_root` 是固定技术术语，越南语和印尼语目录重复定义了同样的英文词条。下一轮会按资源真实语义修正这两类不一致，而不是删除有价值的翻译。

`MissingOnRenderProcessGone` 在两个 WebView 调用点各重复报告两次。两个调用点实际都通过 AndroidX `WebViewClientCompat` 覆盖了 renderer 退出回调并清理 WebView；需要进一步确认检测器为何未识别兼容类继承关系。不能把已有 override 当作告警已消失，也不应在未确认前全局关闭规则。

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
- 提交 `2aec48e6`：Android 单元测试、Debug APK 构建和 `android16-6.12` LKM 构建成功；Lint SARIF 报告 334 errors / 73 warnings。相较上一轮文档的估算数字，按完整 SARIF 重新统计并修正了分类总数。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。正式签名工作流还必须核对 APK v2 证书 SHA-256 与 LKM 信任配置一致。
