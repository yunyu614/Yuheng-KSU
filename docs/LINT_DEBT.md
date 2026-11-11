# Android Lint 技术债记录

## 当前实测基线（2026-11-11，提交 `cf9896a2`）

使用 Android Lint 9.4.1 对 Debug 变体执行完整检查。提交 `cf9896a2` 的报告为 **401 条发现：334 errors / 67 warnings**（以 `lint-results-debug.txt` 的汇总为准；SARIF 中包含 401 个 issue 结果）。

| 类别 | 数量 | 严重级别 |
| --- | ---: | --- |
| `MissingTranslation` | 334 | Error |
| `NewerVersionAvailable` | 16 | Warning |
| `UseKtx` | 13 | Warning |
| `UnusedResources` | 10 | Warning |
| `Untranslatable` | 7 | Warning |
| `IconLauncherShape` | 5 | Warning |
| `MissingOnRenderProcessGone` | 4 | Warning |
| `GradleDependency` | 3 | Warning |
| `AndroidGradlePluginVersion` | 2 | Warning |
| `ModifierParameter` | 2 | Warning |
| `ObsoleteSdkInt` | 2 | Warning |
| `PluralsCandidate` | 1 | Warning |
| `TypographyEllipsis` | 1 | Warning |
| `IconXmlAndPng` | 1 | Warning |
| **合计** | **401** | **334 errors / 67 warnings** |

相较提交 `c12402d` 的 538 条发现，之前已移除繁体中文资源目录中 30 个未使用的 DeepSeek / Token 字符串与数组，以及 3 个未使用颜色资源；`ExtraTranslation` 的 60 条 Fatal 已全部消失，未通过关闭规则或隐藏报告实现。

### 已确认的问题与处理方向

- `MissingTranslation`：334 条，多语言资源覆盖不完整，主要集中在部分既有语言目录。Android 会回退到默认资源；不能粗暴删除语言支持或未经校对地批量填入英文。
- `Untranslatable`：当前报告中的 7 条均指向 `profile`。默认资源已允许翻译，但荷兰语、波兰语、巴西葡萄牙语和俄语文件中的该条目仍残留 `translatable="false"`。本轮将移除这些本地化条目的错误属性，保留已有翻译。
- `UnusedResources`：10 项包含历史 About 字符串、旧首页/设置字符串与旧图标资源。删除前需要确认所有语言目录与资源引用，避免造成 `ExtraTranslation` 或运行时资源缺失。
- `MissingOnRenderProcessGone`：两个 WebView 调用点各重复报告两次。代码已通过 AndroidX `WebViewClientCompat` 覆盖 renderer 退出回调并清理 WebView；仍需确认检测器为何未识别兼容类继承关系，不会全局关闭该规则。
- `IconLauncherShape` / `IconXmlAndPng`：目前指向旧密度 PNG 图标。新品牌图标尚未完成，不用临时图形替换；后续可在确认 minSdk 和资源解析后清理冗余位图。
- `ObsoleteSdkInt`：指出 `mipmap-anydpi-v26` 与 `values-v31` 目录对当前最低系统版本可能多余。合并主题资源前需确认启动主题行为，避免为了清 Warning 改坏 Splash Screen。

### CI 处理原则

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
- 提交 `2aec48e6`：资源占位符修正；Android 单元测试、Debug APK 构建及 `android16-6.12` LKM 构建成功。
- 提交 `cf9896a2`：修正 `profile` 默认资源翻译属性、清理越南语和印尼语的重复 ADB Root 术语，并补齐简体中文旧 About 字符串格式参数；Android 单元测试、Debug APK 构建及 LKM 构建成功；Lint 334 errors / 67 warnings。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。正式签名工作流还必须核对 APK v2 证书 SHA-256 与 LKM 信任配置一致。
