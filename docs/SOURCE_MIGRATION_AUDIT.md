# 源码迁移审查记录

> 审查日期：2026-11-10  
> 目标项目：玉衡（Yuheng）  
> 上游 UI / Manager 参考：NekoWannn/baifan-ksu-lkm  
> 上游核心参考：tiann/KernelSU

## 当前结论

玉衡仓库已建立独立的 `develop` 开发分支。当前仓库仍以品牌与规划文档为主，尚未导入可编译的 Android 工程；本文件记录迁移前审查结果，避免在缺少完整源码和签名材料时进行不完整迁移。

对白饭项目的 `manager/` 目录进行递归清点，当前上游树包含 506 个条目。它不是单一 UI 工程：除 Kotlin / Compose 页面外，还包含 Android Manifest、Gradle 配置、AIDL、JNI / C++、预编译 native libraries、内置 LKM、WebView 资源和其他构建文件。

## 已确认的上游基线

- 上游仓库：<https://github.com/NekoWannn/baifan-ksu-lkm>
- 参考分支：`main`
- 上游 README 声明基础版本：KernelSU v3.3.1，Manager 32680 / UAPI 5
- Manager 包名：`me.weishu.kernelsu`
- Manager 采用 Kotlin、Jetpack Compose，并同时维护 Material 与 Miuix 两套 UI。
- Manager 构建入口：`manager/gradlew`，模块：`:app`
- 上游构建参数示例：`-PKSU_NAME="大肥鱼KSU"`

上述版本信息是该上游 README 的声明，不代表已在玉衡仓库完成构建或验证。迁移时仍需锁定实际提交 SHA，并检查与目标设备内核的兼容性。

## 必须保留并核验的兼容边界

1. **Root 核心逻辑**：授权、模块安装 / 卸载、安装镜像、LKM 加载和 UAPI 交互不应因品牌 UI 改造而被重写。
2. **包名与身份**：初期保留 `me.weishu.kernelsu`，避免影响已有 Manager 与系统端交互。
3. **签名与 LKM 信任**：上游 README 明确说明当前 Manager 使用自定义 v2-only 签名，配套 LKM 内嵌对应公钥摘要。不能直接换签名、替换 LKM 或混用官方 LKM。
4. **预编译依赖**：`jniLibs`、`assets/lkm`、JNI / CMake、AIDL、Gradle Version Catalog 等都属于工程完整性的一部分，不能只迁移 UI 源码。
5. **双 UI 实现**：Material 与 Miuix 页面应共用主题语义和色彩 token，分别适配组件实现，避免两套界面表现不一致。
6. **第三方素材与许可证**：保留上游版权声明、GPL 文本、修改说明及第三方素材归属。逐项核验图片、图标、字体、预编译二进制与 LKM 的许可和再分发条件。

## 玉衡第一阶段改造范围

- 将应用品牌与可见名称调整为「玉衡」，英文名 Yuheng。
- 删除 DeepSeek 专属主题内容、角色图片、语录与余额页及其 API Key 存储 / 请求逻辑。
- 建立统一主题数据层，第一批主题为「青瓷雾」和「墨玉」。
- 重构首页信息层级，但保留 Root 状态、设备信息、安装入口、更新提示等必要能力。
- 后续再统一授权管理、模块管理、设置页。
- 每个阶段以独立提交记录变更，先保证编译，再做真机 Root 与模块功能回归。

## 当前阻塞与下一步

GitHub 目录清单可以确认文件结构，但当前连接的文本文件读取能力不能可靠地将 506 个文件、二进制 LKM、预编译 native libraries 和所有构建资产完整搬运到新仓库。

因此，在导入前需要取得完整上游源码归档（建议上传从白饭仓库指定提交导出的 ZIP，或由维护者提供完整源码包及其签名 / 二进制材料）。收到归档后再做完整性核验、导入和构建；不得用缺失二进制文件的“半成品工程”冒充可编译版本。

## 当前验证状态

- [x] 玉衡 GitHub 仓库存在且具备写入权限
- [x] 建立 `develop` 分支
- [x] 完成白饭 Manager 目录递归清点
- [x] 识别关键签名 / LKM 耦合风险
- [ ] 完整 Android 工程导入
- [ ] Gradle 构建通过
- [ ] 真机 Root 授权与模块操作回归
- [ ] Material / Miuix 双 UI 主题一致性验证
