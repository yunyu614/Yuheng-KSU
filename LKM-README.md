# 玉衡双签名 KSU LKM

配合玉衡管理器使用的自定义 `kernelsu.ko`。当前构建保留大肥鱼管理器证书，并额外信任玉衡自有发布证书。

## 支持范围

- `android15-6.6`
- `android16-6.12`
- `android17-6.18`\n\n玉衡当前首个验证目标为 `android16-6.12`，对应设备 KMI；其他 KMI 暂不由玉衡双签名工作流构建。

构建工作流：`.github/workflows/build-yuheng-dual-lkm.yml`（手动触发，仅构建 `android16-6.12`）  
KernelSU 源码：v3.3.1（`KSU_VERSION=32680`，UAPI 5）

## 为什么需要配套 LKM

管理器使用自定义 v2-only 签名。KernelSU 内核会根据管理器签名决定是否信任当前 APK：

- 官方 **bundled LKM** 只信任官方管理器签名
- 使用本仓库的玉衡双签名 LKM 后，内核会信任大肥鱼KSU 与玉衡管理器的签名

## 管理器签名

```text
APK signing: v2-only
Certificate DER: 825 bytes (0x0339)
Certificate SHA-256: bad08be1f8e813067892adca0e70452cd645ba31e5ed15bec35e60fa430f2f48
Manager package: me.weishu.kernelsu
```

## 构建参数

```make
KSU_GIT_VERSION=2680
KSU_VERSION=32680
KSU_EXPECTED_SIZE=0x0339
KSU_EXPECTED_HASH=bad08be1f8e813067892adca0e70452cd645ba31e5ed15bec35e60fa430f2f48
KSU_EXPECTED_SIZE2=0x037c\nKSU_EXPECTED_HASH2=35fd4e58e9401af81397dae9ae5c385f1ef54c72256e3a3014e631cfa6a918e9\nKSU_PACKAGE_NAME=me.weishu.kernelsu
```

## 产物

```text
kernelsu-android15-6.6-baifan.ko
kernelsu-android16-6.12-baifan.ko
kernelsu-android17-6.18-baifan.ko\n\n玉衡首个验证产物：`kernelsu-android16-6.12-yuheng-dual.ko`
```

对应 Release：

<https://github.com/NekoWannn/baifan-ksu-lkm/releases/tag/v3.3.1-baifan.13>
