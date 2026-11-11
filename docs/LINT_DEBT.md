# Android Lint 技术债记录

## 当前实测基线（2026-11-11，提交 `c12402d`）

使用 Android Lint 9.4.1 对 Debug 变体执行完整检查，报告共 **538 条发现**：

| 类别 | 数量 | 严重级别 |
| --- | ---: | --- |
| `MissingTranslation` | 334 | Error |
| `ExtraTranslation` | 60 | Fatal |
| `UnusedResources` | 43 | Warning |
| `StringFormatCount` | 38 | Warning |
| `NewerVersionAvailable` | 16 | Warning |
| `UseKtx` | 13 | Warning |
| `Untranslatable` | 12 | Warning |
| `MissingOnRenderProcessGone` | 4 | Warning |
| `IconLauncherShape` | 5 | Warning |
| 其他 Warning | 13 | Warning |
| **合计** | **538** | **394 errors / 144 warnings** |

`ExtraTranslation` 的 60 条发现来自繁体中文（香港、台湾）资源目录中重复存在的 30 个 DeepSeek / Token 主题字符串与数组；这些资源在当前 Yuheng 界面中未被引用。后续清理提交已移除这批遗留资源以及 3 个未使用的 DeepSeek 颜色，新的 Lint 报告将用于确认清理结果。

`MissingTranslation` 是多语言资源覆盖不完整导致，涉及多个既有语言目录；Android 仍会回退到默认资源。不能将其误判为 Kotlin 编译失败，也不能因此粗暴删除语言支持。

## CI 处理原则

- 单元测试和 APK 编译/打包是阻断门槛，失败即阻止产物上传。
- Lint 仍然执行，报告作为独立 Artifact 留存；当前历史问题不阻断 APK 构建。
- 不设置全局 `abortOnError = false`，不禁用 `MissingTranslation`，不把已有问题伪装成通过。
- 后续按报告逐项分类：优先处理运行时风险、资源引用错误、占位符不一致、可访问性和安全问题；翻译缺失按实际支持的语言逐步补齐。
- 当历史问题被清理后，将 Lint 恢复为阻断门槛。

## 最近工程验证

- 提交 `51a6dbd`：Kotlin 编译、单元测试、Debug APK 打包成功；Lint 报告 394 errors / 148 warnings。
- 提交 `c12402d`：增加 Markdown WebView 与模块 WebUI 的渲染进程退出清理；Kotlin 编译、单元测试、Debug APK 打包成功；Lint 报告 394 errors / 144 warnings。
- 同一轮的 `android16-6.12` LKM 构建成功，产物 SHA-256 与 Artifact 内校验清单一致。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。正式签名工作流还必须核对 APK v2 证书 SHA-256 与 LKM 信任配置一致。
