# 源码迁移审查记录

## 签名与发布状态更新（2026-11-12）

- 当前三重签名 LKM 同时信任 KernelSU 官方、大肥鱼与玉衡自有发布证书；玉衡证书 DER 长度为 892 bytes（`0x037c`），SHA-256 为 `35fd4e58e9401af81397dae9ae5c385f1ef54c72256e3a3014e631cfa6a918e9`。
- 上游白饭原始证书的私钥不在本仓库中；这不妨碍玉衡使用**自己的**发布证书。正式签名工作流通过 GitHub Actions Secrets 注入玉衡 keystore 与密码，仓库不保存私钥。
- Debug 预览包使用独立 Application ID `me.weishu.kernelsu.preview` 和 Debug 签名，不能替代正式管理器，也不能与配套 LKM 的玉衡发布证书校验等同。
- 截至本次审查，签名 Release 工作流尚未运行；Secrets 是否齐全、正式 APK 是否成功签名以及 APK v2 证书摘要是否匹配，仍须以实际 Release 工作流记录为准。


> 审查日期：2026-11-10  
> 目标项目：玉衡（Yuheng）  
> 上游 UI / Manager 参考：NekoWannn/baifan-ksu-lkm  
> 上游核心参考：tiann/KernelSU

## 当前结论

玉衡仓库已建立独立的 `develop` 开发分支，并已导入完整 Android Manager 工程。当前 GitHub Actions 成功产出的是独立包名的 Debug 界面预览版；玉衡自有签名证书及配套 LKM 信任配置已经建立，但签名 Release 工作流尚未执行，真机 Root、模块及 LKM 回归尚未完成。上游白饭原始签名私钥未提供；玉衡正式发布使用独立证书。本文件记录迁移边界、已完成工作与后续检查项。

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
3. **签名与 LKM 信任**：配套三重签名 LKM 同时信任 KernelSU 官方、白饭与玉衡自有发布证书。白饭证书 DER 长度为 `0x0339`、SHA-256 为 `bad08be1f8e813067892adca0e70452cd645ba31e5ed15bec35e60fa430f2f48`；玉衡证书 DER 长度为 `0x037c`、SHA-256 为 `35fd4e58e9401af81397dae9ae5c385f1ef54c72256e3a3014e631cfa6a918e9`。仓库不包含私钥；正式 Release 需通过 Actions Secrets 注入玉衡自有 keystore，并由工作流校验 APK v2 摘要。Debug 预览包使用独立 Application ID 与 Debug 签名，不能作为正式 Root 管理版使用。不得擅自替换签名或 LKM。
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
