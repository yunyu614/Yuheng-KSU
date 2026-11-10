# 玉衡双签名 KSU LKM

配合玉衡管理器使用的自定义 `kernelsu.ko`。当前构建保留大肥鱼管理器证书，并额外信任玉衡自有发布证书。

## 目标设备

- 首个构建目标：`android16-6.12`
- 适用前提：设备内核 KMI 与构建目标一致；仅凭 Android 版本号不能判断兼容性。

构建工作流：`.github/workflows/build-yuheng-dual-lkm.yml`（手动触发，仅构建 `android16-6.12`）  
KernelSU 源码：v3.3.1（`KSU_VERSION=32680`，UAPI 5）

## 为什么需要配套 LKM

管理器使用自定义 v2-only 签名。KernelSU 内核会根据管理器签名决定是否信任当前 APK：

- 官方 **bundled LKM** 只信任官方管理器签名
- 使用本仓库的玉衡双签名 LKM 后，内核会信任大肥鱼KSU 与玉衡管理器的签名

## 管理器签名

两张证书均使用相同的管理器包名，内核会分别校验 APK v2 签名证书：

| 信任对象 | DER 大小 | SHA-256 |
|---|---:|---|
| 大肥鱼原管理器 | 825 bytes（`0x0339`） | `bad08be1f8e813067892adca0e70452cd645ba31e5ed15bec35e60fa430f2f48` |
| 玉衡自有发布证书 | 892 bytes（`0x037c`） | `35fd4e58e9401af81397dae9ae5c385f1ef54c72256e3a3014e631cfa6a918e9` |

管理器包名：`me.weishu.kernelsu`。APK 需包含 v2 签名；仅有 v3/v3.1 签名不满足当前内核解析器的校验方式。

## 构建参数

```make
KSU_GIT_VERSION=2680
KSU_VERSION=32680
KSU_EXPECTED_SIZE=0x0339
KSU_EXPECTED_HASH=bad08be1f8e813067892adca0e70452cd645ba31e5ed15bec35e60fa430f2f48
KSU_EXPECTED_SIZE2=0x037c
KSU_EXPECTED_HASH2=35fd4e58e9401af81397dae9ae5c385f1ef54c72256e3a3014e631cfa6a918e9
KSU_PACKAGE_NAME=me.weishu.kernelsu
```

## 产物

```text
kernelsu-android15-6.6-baifan.ko
kernelsu-android16-6.12-baifan.ko
kernelsu-android17-6.18-baifan.ko

玉衡首个验证产物：`kernelsu-android16-6.12-yuheng-dual.ko`
```

对应 Release：

<https://github.com/NekoWannn/baifan-ksu-lkm/releases/tag/v3.3.1-baifan.13>
