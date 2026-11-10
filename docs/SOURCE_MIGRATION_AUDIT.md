# 源码迁移审查记录

> 审查日期：2026-11-10  
> 目标项目：玉衡（Yuheng）  
> 上游 UI / Manager 参考：NekoWannn/baifan-ksu-lkm  
> 上游核心参考：tiann/KernelSU

## 当前结论

玉衡仓库已建立独立的 `develop` 开发分支，并已导入完整 Android Manager 工程。当前 GitHub Actions 成功产出的是独立包名的 Debug 界面预览版；正式 Root 管理版仍受原始 APK 签名私钥缺失限制，真机 Root、模块及 LKM 回归尚未完成。本文件记录迁移边界、已完成工作与后续检查项。

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
2. **包名与身份**：正式管理器保留 `me.weishu.kernelsu`，避免影响已有 Manager 与系统端交互；CI 预览包使用 `me.weishu.kernelsu.preview`，避免误覆盖正式管理器。
3. **签名与 LKM 信任**：配套 LKM 的 `kernel/manager/apk_sign.c` 校验 APK v2 证书 DER 长度 `0x0339` 与 SHA-256 摘要 `bad08be1f8e813067892adca0e70452cd645ba31e5ed15bec35e60fa430f2f48`。当前仓库未包含对应私钥；CI Debug 签名不匹配该摘要，预览 APK 不能作为 Root 管理版使用。不得擅自替换签名或 LKM。
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

## 源码导入方式

已新增手动 GitHub Actions 工作流：`.github/workflows/import-baifan-source.yml`。

运行方式：
1. 打开玉衡仓库的 **Actions**。
2. 选择 **Import Baifan KernelSU source**。
3. 选择分支 `develop`，点击 **Run workflow**。
4. 上游 ref 默认是 `main`；如需固定到某个版本，可填入对应 tag 或 commit。
5. 工作流会将上游完整源码导入 `develop`，保留玉衡根目录 README、现有品牌文档和 `.gitignore`，把上游 README 另存为 `docs/UPSTREAM_BAIFAN_README.md`，并记录实际上游 commit。

为避免未经审查的上游 CI 自动执行，导入工作流刻意不复制上游 `.github/workflows`。导入后再单独审查并配置玉衡自己的构建工作流。

## 当前验证状态

- [x] 玉衡 GitHub 仓库存在且具备写入权限
- [x] 建立 `develop` 分支
- [x] 完成白饭 Manager 目录递归清点
- [x] 识别关键签名 / LKM 耦合风险
- [x] 添加手动上游源码导入工作流
- [x] 运行工作流并完成完整 Android 工程导入
- [x] Gradle Debug 界面预览包构建通过（2026-11-10，Actions run 42763393771；独立包名 `me.weishu.kernelsu.preview`）
- [ ] 获取原始签名私钥，或与上游确认可行的签名交付方式；校验证书 DER 长度及 SHA-256 与 LKM 常量一致后再构建正式管理器
- [ ] 真机 Root 授权与模块操作回归
- [ ] Material / Miuix 双 UI 真机主题表现与 Root 流程回归验证


## 玉衡 UI 第一轮改造（2026-11-10）

- [x] 首页 Hero 卡片替换为玉衡品牌文案与青瓷 / 墨玉视觉。
- [x] 首页背景、信息卡片改为青瓷雾浅色与墨玉深色配色。
- [x] Material 与 Miuix 首页移除白饭主题署名卡片。
- [x] 主色调整为玉衡黛青，并将首页主题组件文件与 API 统一命名为 Yuheng。
- [x] 移除底部导航中的白饭余额入口，主导航调整为总览、授权、模块、设置四项。
- [x] 默认应用显示名称改为「玉衡」，包名仍保留 `me.weishu.kernelsu`。
- [x] 新增 Android Debug APK 自动构建工作流。
- [x] 执行首轮 CI，Gradle Debug APK 构建及 artifact 上传成功。
- [x] 移除旧主题文案、Token 余额页、API 请求仓库、Token 插画与历史角色图片资源。
- [x] 在 Material / Miuix 主题页加入青瓷雾、墨玉预设入口，复用设置仓库持久化主题模式与种子色。
