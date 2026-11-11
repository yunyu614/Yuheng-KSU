# Android Lint 技术债记录

## 当前基线（2026-11-11）

最近一次完整 Debug 构建已完成 Kotlin 编译、单元测试和 APK 打包；随后 `:app:lintDebug` 因现存资源翻译问题失败，报告 **394 errors / 148 warnings**。日志中可见的首个问题为 `MissingTranslation`：默认资源中的字符串未覆盖 Burmese（`my`）和 Tagalog（`tl`）目录。

本记录不把这些问题认定为本轮新引入，也不将其静默关闭。

## CI 处理原则

- 单元测试和 APK 编译/打包是阻断门槛，失败即阻止产物上传。
- Lint 仍然执行，报告作为独立 artifact 留存；当前历史问题不阻断 APK 构建。
- 不设置全局 `abortOnError = false`，不禁用 `MissingTranslation`，不把已有问题伪装成通过。
- 后续应基于完整 SARIF 报告分类处理：优先修复新增代码问题、资源引用错误、可访问性和安全问题；翻译缺失按受支持语言逐步补齐。
- 当历史问题被清理后，将 Lint 恢复为阻断门槛。

## 本轮已知结果

- `:app:compileDebugKotlin`：通过。
- `:app:testDebugUnitTest`：任务执行完成。
- `:app:assembleDebug`：通过。
- `:app:lintDebug`：失败，394 errors / 148 warnings；首个可见类别为 MissingTranslation。
- LKM 构建：对应提交的 CI 成功。

**注意：** Debug CI 成功不代表正式签名、设备 Root 授权、模块生命周期或真机回归已经验收。
