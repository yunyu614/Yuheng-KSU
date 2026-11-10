package me.weishu.kernelsu.ui.screen.settings

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.Rule
import androidx.compose.material.icons.rounded.Adb
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LayersClear
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.SystemUpdateAlt
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.component.KsuIsValid
import me.weishu.kernelsu.ui.component.yuhengCardGradient
import me.weishu.kernelsu.ui.component.dialog.rememberLoadingDialog
import me.weishu.kernelsu.ui.component.miuix.SendLogDialog
import me.weishu.kernelsu.ui.component.uninstalldialog.UninstallDialog
import me.weishu.kernelsu.ui.theme.LocalEnableBlur
import me.weishu.kernelsu.ui.util.BlurredBar
import me.weishu.kernelsu.ui.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * @author weishu
 * @date 2023/1/1.
 */
@Composable
fun SettingPagerMiuix(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val backdrop = rememberBlurBackdrop(enableBlur)
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else colorScheme.surface
    val loadingDialog = rememberLoadingDialog()
    val showUninstallDialog = rememberSaveable { mutableStateOf(false) }
    val showSendLogDialog = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val wallpaperPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val hasCustomWallpaper = rememberSaveable {
        mutableStateOf(
            wallpaperPreferences.getString("yuheng_wallpaper_uri", null) != null ||
                wallpaperPreferences.getString("yuheng_wallpaper_file", null) != null
        )
    }
    DisposableEffect(wallpaperPreferences) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == null || key == "yuheng_wallpaper_uri" || key == "yuheng_wallpaper_file") {
                hasCustomWallpaper.value =
                    wallpaperPreferences.getString("yuheng_wallpaper_uri", null) != null ||
                        wallpaperPreferences.getString("yuheng_wallpaper_file", null) != null
            }
        }
        wallpaperPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { wallpaperPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    val wallpaperPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            val persistedPermission = runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            if (persistedPermission.isFailure) {
                Toast.makeText(context, "无法持久访问这张图片，请更换图片来源后重试", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            val previousUri = wallpaperPreferences.getString("yuheng_wallpaper_uri", null)
            if (previousUri != null && previousUri != uri.toString()) {
                runCatching {
                    context.contentResolver.releasePersistableUriPermission(
                        android.net.Uri.parse(previousUri),
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                }
            }
            wallpaperPreferences.edit()
                .remove("yuheng_wallpaper_file")
                .putString("yuheng_wallpaper_uri", uri.toString())
                .apply()
            hasCustomWallpaper.value = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            BlurredBar(backdrop) {
                TopAppBar(
                    color = barColor,
                    title = stringResource(R.string.settings),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = innerPadding,
                overscrollEffect = null,
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .yuhengCardGradient(),
                        colors = CardDefaults.defaultColors(color = Color.Transparent),
                    ) {
                        OverlayDropdownPreference(
                            title = stringResource(id = R.string.settings_ui_mode),
                            summary = stringResource(id = R.string.settings_ui_mode_summary),
                            items = UiMode.entries.map { it.name },
                            startAction = {
                                Icon(
                                    Icons.Rounded.DisplaySettings,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_ui_mode),
                                    tint = colorScheme.onBackground
                                )
                            },
                            selectedIndex = if (uiState.uiMode == UiMode.Material.value) 1 else 0,
                            onSelectedIndexChange = actions.onSetUiModeIndex
                        )
                        ArrowPreference(
                            title = stringResource(id = R.string.settings_theme),
                            summary = stringResource(id = R.string.settings_theme_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Palette,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_theme),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenTheme
                        )
                        ArrowPreference(
                            title = "自定义壁纸",
                            summary = if (hasCustomWallpaper.value) "已设置个人壁纸 · 点击更换" else "从相册选择图片作为玉衡界面背景",
                            startAction = {
                                Icon(
                                    Icons.Rounded.DisplaySettings,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = "自定义壁纸",
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = { wallpaperPicker.launch(arrayOf("image/*")) }
                        )
                        if (hasCustomWallpaper.value) {
                            ArrowPreference(
                                title = "恢复默认背景",
                                summary = "移除自定义壁纸，恢复当前神兽主题背景",
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = "恢复默认背景",
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = {
                                    wallpaperPreferences.getString("yuheng_wallpaper_uri", null)?.let { savedUri ->
                                        runCatching {
                                            context.contentResolver.releasePersistableUriPermission(
                                                android.net.Uri.parse(savedUri),
                                                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                                            )
                                        }
                                    }
                                    wallpaperPreferences.edit()
                                        .remove("yuheng_wallpaper_uri")
                                        .remove("yuheng_wallpaper_file")
                                        .apply()
                                    hasCustomWallpaper.value = false
                                }
                            )
                        }
                    }

                    KsuIsValid {
                        Card(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .yuhengCardGradient(),
                            colors = CardDefaults.defaultColors(color = Color.Transparent),
                        ) {
                            val profileTemplate = stringResource(id = R.string.settings_profile_template)
                            ArrowPreference(
                                title = profileTemplate,
                                summary = stringResource(id = R.string.settings_profile_template_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Description,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = profileTemplate,
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = actions.onOpenProfileTemplate
                            )
                        }
                    }

                    KsuIsValid {
                        Card(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .yuhengCardGradient(),
                            colors = CardDefaults.defaultColors(color = Color.Transparent),
                        ) {
                            val suCompatModeItems = listOf(
                                stringResource(id = R.string.settings_mode_enable_by_default),
                                stringResource(id = R.string.settings_mode_disable_until_reboot),
                                stringResource(id = R.string.settings_mode_disable_always),
                            )

                            val suSummary = when (uiState.suCompatStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_sucompat_summary)
                            }
                            OverlayDropdownPreference(
                                title = stringResource(id = R.string.settings_sucompat),
                                summary = suSummary,
                                items = suCompatModeItems,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.AdminPanelSettings,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_sucompat),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.suCompatStatus == "supported",
                                selectedIndex = uiState.suCompatMode,
                                onSelectedIndexChange = actions.onSetSuCompatMode
                            )

                            val umountSummary = when (uiState.kernelUmountStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_kernel_umount_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_kernel_umount),
                                summary = umountSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.LayersClear,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_kernel_umount),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.kernelUmountStatus == "supported",
                                checked = uiState.isKernelUmountEnabled,
                                onCheckedChange = actions.onSetKernelUmountEnabled
                            )

                            val selinuxHideSummary = when (uiState.selinuxHideStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_selinux_hide_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_selinux_hide),
                                summary = selinuxHideSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Security,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_selinux_hide),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.selinuxHideStatus == "supported",
                                checked = uiState.isSelinuxHideEnabled,
                                onCheckedChange = actions.onSetSelinuxHideEnabled
                            )

                            val sulogSummary = when (uiState.sulogStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_sulog_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_sulog),
                                summary = sulogSummary,
                                startAction = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.Article,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_sulog),
                                        tint = if (uiState.sulogStatus == "supported") colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
                                    )
                                },
                                enabled = uiState.sulogStatus == "supported",
                                checked = uiState.isSulogEnabled,
                                onCheckedChange = actions.onSetSulogEnabled
                            )

                            val adbRootSummary = when (uiState.adbRootStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_adb_root_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_adb_root),
                                summary = adbRootSummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Adb,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_adb_root),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                enabled = uiState.adbRootStatus == "supported",
                                checked = uiState.isAdbRootEnabled,
                                onCheckedChange = actions.onSetAdbRootEnabled
                            )

                            val shellSuAdbOnlySummary = when (uiState.shellSuAdbOnlyStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_shell_su_adb_only_summary)
                            }
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_shell_su_adb_only),
                                summary = shellSuAdbOnlySummary,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.VerifiedUser,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_shell_su_adb_only),
                                        tint = if (uiState.shellSuAdbOnlyStatus == "supported") colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
                                    )
                                },
                                enabled = uiState.shellSuAdbOnlyStatus == "supported",
                                checked = uiState.isShellSuAdbOnlyEnabled,
                                onCheckedChange = actions.onSetShellSuAdbOnlyEnabled
                            )
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_soft_reboot),
                                summary = stringResource(id = R.string.settings_soft_reboot_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.RestartAlt,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_soft_reboot),
                                        tint = if (uiState.isLateLoadMode) colorScheme.disabledOnSecondaryVariant else colorScheme.onBackground
                                    )
                                },
                                enabled = !uiState.isLateLoadMode,
                                checked = uiState.isLateLoadMode || uiState.useSoftReboot,
                                onCheckedChange = actions.onSetUseSoftReboot
                            )
                        }

                        Card(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .yuhengCardGradient(),
                            colors = CardDefaults.defaultColors(color = Color.Transparent),
                        ) {
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_umount_modules_default),
                                summary = stringResource(id = R.string.settings_umount_modules_default_summary),
                                startAction = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.Rule,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_umount_modules_default),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.isDefaultUmountModules,
                                onCheckedChange = actions.onSetDefaultUmountModules
                            )

                            SwitchPreference(
                                title = stringResource(id = R.string.enable_web_debugging),
                                summary = stringResource(id = R.string.enable_web_debugging_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.DeveloperMode,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.enable_web_debugging),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.enableWebDebugging,
                                onCheckedChange = actions.onSetEnableWebDebugging
                            )
                            SwitchPreference(
                                title = stringResource(id = R.string.settings_auto_jailbreak),
                                summary = stringResource(id = R.string.settings_auto_jailbreak_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.FlashOn,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_auto_jailbreak),
                                        tint = if (uiState.isLateLoadMode) colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
                                    )
                                },
                                enabled = uiState.isLateLoadMode,
                                checked = uiState.autoJailbreak,
                                onCheckedChange = actions.onSetAutoJailbreak
                            )
                        }
                    }

                    if (uiState.isLkmMode) {
                        Card(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .yuhengCardGradient(),
                            colors = CardDefaults.defaultColors(color = Color.Transparent),
                        ) {
                            val uninstall = stringResource(id = R.string.settings_uninstall)
                            ArrowPreference(
                                title = uninstall,
                                enabled = !uiState.isLateLoadMode,
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = uninstall,
                                        tint = Color(0xFFB76B62),
                                    )
                                },
                                onClick = { showUninstallDialog.value = true },
                            )
                            UninstallDialog(
                                show = showUninstallDialog.value,
                                onDismissRequest = { showUninstallDialog.value = false }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                            .yuhengCardGradient(),
                        colors = CardDefaults.defaultColors(color = Color.Transparent),
                    ) {
                        ArrowPreference(
                            title = stringResource(id = R.string.send_log),
                            startAction = {
                                Icon(
                                    Icons.Rounded.BugReport,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.send_log),
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = { showSendLogDialog.value = true },
                        )
                        SendLogDialog(
                            show = showSendLogDialog.value,
                            onDismissRequest = { showSendLogDialog.value = false },
                            loadingDialog = loadingDialog
                        )
                        val about = stringResource(id = R.string.about)
                        ArrowPreference(
                            title = about,
                            startAction = {
                                Icon(
                                    Icons.Rounded.Info,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = about,
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenAbout,
                        )
                    }
                    Spacer(Modifier.height(bottomInnerPadding))
                }
            }
        }
    }
}
