# Android Lint 技术债记录

## 当前实测基线（2026-11-12，提交 `56afbbd306bc02a7cc5c5392af65e0ea9c61233c`）

使用 Android Lint 9.4.1 对 Debug 变体执行完整检查。CI run `43138824054`（源码提交 `56afbbd306bc02a7cc5c5392af65e0ea9c61233c`）上传的 `yuheng-preview-lint-report` 原始 SARIF 共 **375 条发现：335 errors / 40 warnings**。按 SARIF 规则默认严重级别统计（SARIF result 未逐条重复写 level）。

| 类别 | 数量 | 严重级别 |
| --- | ---: | --- |
| `MissingTranslation` | 335 | Error |
| `NewerVersionAvailable` | 16 | Warning |
| `UnusedResources` | 9 | Warning |
| `MissingOnRenderProcessGone` | 4 | Warning |
| `GradleDependency` | 4 | Warning |
| `AndroidGradlePluginVersion` | 2 | Warning |
| `ModifierParameter` | 2 | Warning |
| `PluralsCandidate` | 1 | Warning |
| `ObsoleteSdkInt` | 1 | Warning |
| `TypographyEllipsis` | 1 | Warning |
| `UseKtx` | **0** | — |
| **合计** | **375** | **335 errors / 40 warnings** |

相较上一轮 `f05829f2` 基线（387 条：335 errors / 52 warnings），当前净减少 12 条发现。壁纸相关代码的 13 条 `UseKtx` 已全部通过 AndroidX Core KTX 改造清零；同时新增的 Core KTX 1.17.0 触发 1 条 `GradleDependency` 更新提示，因此警告净减少 12 条。没有关闭 Lint 规则，也没有通过隐藏报告实现数字下降。

上一轮 `f05829f2a4120eb8f7f104957225955f57ff6dd1` 的 SARIF 历史基线为 **387 条：335 errors / 52 warnings**，其中 `MissingTranslation` 为 335 条（不是 334 条）。此前 `ed7400e2` 的历史报告为 389 条：334 errors / 55 warnings。越南语、乌克兰语和土耳其语的 `profile` 翻译标记问题已不再触发 `Untranslatable`；`7300603` 当前报告中该规则仍为 0。

### 已确认的问题与处理方向

- `MissingTranslation`：335 条，多语言资源覆盖仍不完整。Android 会回退到默认资源；不能粗暴删除语言支持或未经校对地批量填入英文。
- `UseKtx`：当前 0 条。原 13 条集中在壁纸管理相关代码中的 `SharedPreferences.edit` 与 `Uri.parse`，已改用 AndroidX Core KTX 的 `edit { }` 与 `toUri()`。偏好写入仍保持异步 apply 语义，壁纸 URI 解析也已复用解析结果。
- `UnusedResources`：9 项，主要为旧首页/设置字符串和历史 About 文案。删除前仍需检查所有资源目录与动态引用，避免造成 `ExtraTranslation` 或运行时资源缺失。
- `MissingOnRenderProcessGone`：两个 WebView 调用点各重复报告两次。代码已通过 AndroidX `WebViewClientCompat` 覆盖 renderer 退出回调并清理 WebView；Lint 仍未识别兼容类继承关系，不会全局关闭该规则。
- `Untranslatable`：当前 SARIF 无此类告警（0 条）。越南语、乌克兰语和土耳其语的 `profile` 资源标记已不再触发该规则；后续需避免在翻译目录中误加 `translatable="false"`。
- `ModifierParameter`：2 条，位于 `YuhengTheme.kt` 的 Composable 参数顺序；调整时需同步检查调用点，避免破坏现有调用。
- `ObsoleteSdkInt`：1 条，指向 `values-v31` 目录。当前 minSdk 为 31，但合并前需检查其中资源与主题启动行为。
- `PluralsCandidate`：1 条，提示默认字符串中的应用数量格式可能应改为复数资源；需检查调用处是否能正确传递数量。
- `TypographyEllipsis`：1 条，越南语文本仍使用三个句点。
- `NewerVersionAvailable` / 依赖版本告警：当前共 22 条（`NewerVersionAvailable` 16、`GradleDependency` 4、`AndroidGradlePluginVersion` 2）。其中 Core KTX 1.17.0 的更新提示已由本轮引入；升级 Gradle、AGP、Kotlin、Compose 或 Native 工具链前，需独立验证兼容性，不为了清告警盲目追新。

### CI 处理原则

- 单元测试和 APK 编译/打包是阻断门槛，失败即阻止产物上传。
- Lint 仍然执行，报告作为独立 Artifact 留存；当前历史问题不阻断 APK 构建。`android-release.yml` 的 `lintRelease` 同样采用非阻断策略并保留报告，避免与 Debug CI 的处理口径不一致。
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
- 提交 `f05829f2`：修正土耳其语 `profile` 翻译标记；Android 单元测试、Debug APK 构建及 `android16-6.12` LKM 构建成功；Lint 335 errors / 52 warnings。
- 提交 `7300603`：壁纸偏好读写与 URI 解析改用 AndroidX Core KTX；Android 单元测试、Debug APK、Lint 与 `android16-6.12` 三重签名 LKM 构建均成功；Lint 335 errors / 40 warnings，`UseKtx` 从 13 条降为 0。

- 提交 `56afbbd`：模块列表读取失败时保留上次成功数据并明确展示错误/重试状态；CI run `43138824054` 的单元测试与 Debug APK 构建成功，Lint 仍为 335 errors / 40 warnings（非阻断报告）。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。正式签名工作流还必须核对 APK v2 证书 SHA-256 与 LKM 信任配置一致。
