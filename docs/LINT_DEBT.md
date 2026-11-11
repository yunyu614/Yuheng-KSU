# Android Lint 技术债记录

## 当前实测基线（2026-11-11，提交 `ed7400e2`）

使用 Android Lint 9.4.1 对 Debug 变体执行完整检查。提交 `ed7400e25319bd5deb9b58146c7753378285f473` 的原始 SARIF 包含 **389 条发现：334 errors / 55 warnings**；文本报告汇总与 SARIF 数量一致。

| 类别 | 数量 | 严重级别 |
| --- | ---: | --- |
| `MissingTranslation` | 334 | Error |
| `NewerVersionAvailable` | 16 | Warning |
| `UseKtx` | 13 | Warning |
| `UnusedResources` | 9 | Warning |
| `MissingOnRenderProcessGone` | 4 | Warning |
| `GradleDependency` | 3 | Warning |
| `Untranslatable` | 3 | Warning |
| `AndroidGradlePluginVersion` | 2 | Warning |
| `ModifierParameter` | 2 | Warning |
| `PluralsCandidate` | 1 | Warning |
| `ObsoleteSdkInt` | 1 | Warning |
| `TypographyEllipsis` | 1 | Warning |
| **合计** | **389** | **334 errors / 55 warnings** |

本轮相较 `cf9896a2` 的 401 条发现减少 12 条：移除旧启动图标位图与重复图标资源，并修正部分资源目录和翻译标记。没有关闭 Lint 规则，也没有通过隐藏报告实现数字下降。

### 已确认的问题与处理方向

- `MissingTranslation`：334 条，多语言资源覆盖仍不完整。Android 会回退到默认资源；不能粗暴删除语言支持或未经校对地批量填入英文。
- `UseKtx`：13 条，集中在壁纸管理相关代码中的 `SharedPreferences.edit` 与 `Uri.parse`。可使用 AndroidX Core KTX 扩展整理，但必须保持 URI 权限释放、偏好写入时序与失败处理行为不变。
- `UnusedResources`：9 项，主要为旧首页/设置字符串和历史 About 文案。删除前仍需检查所有资源目录与动态引用，避免造成 `ExtraTranslation` 或运行时资源缺失。
- `MissingOnRenderProcessGone`：两个 WebView 调用点各重复报告两次。代码已通过 AndroidX `WebViewClientCompat` 覆盖 renderer 退出回调并清理 WebView；Lint 仍未识别兼容类继承关系，不会全局关闭该规则。
- `Untranslatable`：当前有 3 条 `profile` 资源告警，分别位于越南语、乌克兰语和土耳其语资源。土耳其语条目的错误属性已修正；其余两条仍需追查产生冲突的资源定义，不能直接删除有效翻译。
- `ModifierParameter`：2 条，位于 `YuhengTheme.kt` 的 Composable 参数顺序；调整时需同步检查调用点，避免破坏现有调用。
- `ObsoleteSdkInt`：1 条，指向 `values-v31` 目录。当前 minSdk 为 31，但合并前需检查其中资源与主题启动行为。
- `PluralsCandidate`：1 条，提示默认字符串中的应用数量格式可能应改为复数资源；需检查调用处是否能正确传递数量。
- `TypographyEllipsis`：1 条，越南语文本仍使用三个句点。
- `NewerVersionAvailable` / 依赖版本告警：共 21 条。升级 Gradle、AGP、Kotlin、Compose 或 Native 工具链前，需独立验证兼容性，不为了清告警盲目升级。

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
- 提交 `cf9896a2`：修正部分资源翻译属性、清理越南语和印尼语的重复 ADB Root 术语，并补齐简体中文旧 About 字符串格式参数；Android 单元测试、Debug APK 构建及 LKM 构建成功；Lint 334 errors / 67 warnings。
- 提交 `ed7400e2`：清理旧启动图标位图与重复资源；Android 单元测试、Debug APK 构建及 `android16-6.12` LKM 构建成功；Lint 334 errors / 55 warnings。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。正式签名工作流还必须核对 APK v2 证书 SHA-256 与 LKM 信任配置一致。
