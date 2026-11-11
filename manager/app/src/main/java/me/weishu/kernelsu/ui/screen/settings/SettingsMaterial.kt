package me.weishu.kernelsu.ui.screen.settings

import android.content.Context
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.theme.LocalYuhengThemePreset
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.component.KsuIsValid
import me.weishu.kernelsu.ui.component.material.ExpressiveScaffold
import me.weishu.kernelsu.ui.component.material.SegmentedColumn
import me.weishu.kernelsu.ui.component.material.SegmentedDropdownItem
import me.weishu.kernelsu.ui.component.material.SegmentedListItem
import me.weishu.kernelsu.ui.component.material.SegmentedSwitchItem
import me.weishu.kernelsu.ui.component.material.SendLogBottomSheet
import me.weishu.kernelsu.ui.component.material.SnackBarHost
import me.weishu.kernelsu.ui.component.material.expressiveTopAppBarColors
import me.weishu.kernelsu.ui.component.uninstalldialog.UninstallDialog

/**
 * @author weishu
 * @date 2023/1/1.
 */
@Composable
fun SettingPagerMaterial(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val snackBarHost = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val showUninstallDialog = rememberSaveable { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val wallpaperPreferences = remember(context) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    var hasCustomWallpaper by remember(wallpaperPreferences) {
        mutableStateOf(
            wallpaperPreferences.getString("yuheng_wallpaper_uri", null) != null ||
                wallpaperPreferences.getString("yuheng_wallpaper_file", null) != null
        )
    }
    DisposableEffect(wallpaperPreferences) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == null || key == "yuheng_wallpaper_uri" || key == "yuheng_wallpaper_file") {
                hasCustomWallpaper =
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
                coroutineScope.launch {
                    snackBarHost.showSnackbar("无法持久访问这张图片，请更换图片来源后重试")
                }
                return@rememberLauncherForActivityResult
            }

            val previousUri = wallpaperPreferences.getString("yuheng_wallpaper_uri", null)
            if (previousUri != null && previousUri != uri.toString()) {
                runCatching {
                    context.contentResolver.releasePersistableUriPermission(
                        previousUri.toUri(),
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                }
            }
            wallpaperPreferences.edit {
                remove("yuheng_wallpaper_file")
                putString("yuheng_wallpaper_uri", uri.toString())
            }
            hasCustomWallpaper = true
        }
    }

    UninstallDialog(
        show = showUninstallDialog.value,
        onDismissRequest = { showUninstallDialog.value = false }
    )

    ExpressiveScaffold(
        topBar = {
            TopBar(scrollBehavior = scrollBehavior)
        },
        snackbarHost = { SnackBarHost(hostState = snackBarHost, modifier = Modifier.padding(bottom = bottomInnerPadding)) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsAppearanceIntro()
            SettingsSectionTitle("外观与个性化")
            SegmentedColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                    gradient = true,
                content = buildList {
                    add {
                        SegmentedListItem(
                            onClick = actions.onOpenTheme,
                            headlineContent = { Text(stringResource(id = R.string.settings_theme)) },
                            supportingContent = { Text(stringResource(id = R.string.settings_theme_summary)) },
                            leadingContent = { Icon(Icons.Filled.Palette, stringResource(id = R.string.settings_theme)) },
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    null
                                )
                            }
                        )
                    }
                    add {
                        SegmentedSwitchItem(
                            icon = Icons.Filled.DisplaySettings,
                            title = "悬浮底栏",
                            summary = "使用独立悬浮的四项导航栏",
                            checked = uiState.enableFloatingBottomBar,
                            onCheckedChange = actions.onSetFloatingBottomBar
                        )
                    }
                    add {
                        SegmentedSwitchItem(
                            icon = Icons.Filled.Palette,
                            title = "玻璃材质",
                            summary = "为悬浮底栏启用背景模糊与通透质感",
                            enabled = uiState.enableFloatingBottomBar,
                            checked = uiState.enableFloatingBottomBarBlur,
                            onCheckedChange = actions.onSetFloatingBottomBarBlur
                        )
                    }
                    add {
                        SegmentedListItem(
                            onClick = { wallpaperPicker.launch(arrayOf("image/*")) },
                            headlineContent = { Text("自定义壁纸") },
                            supportingContent = {
                                Text(if (hasCustomWallpaper) "已设置个人壁纸 · 点击更换" else "从相册选择图片作为玉衡界面背景")
                            },
                            leadingContent = { Icon(Icons.Filled.DisplaySettings, "自定义壁纸") },
                            trailingContent = {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                            }
                        )
                    }
                    if (hasCustomWallpaper) {
                        add {
                            SegmentedListItem(
                                onClick = {
                                    wallpaperPreferences.getString("yuheng_wallpaper_uri", null)?.let { savedUri ->
                                        runCatching {
                                            context.contentResolver.releasePersistableUriPermission(
                                                savedUri.toUri(),
                                                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                                            )
                                        }
                                    }
                                    wallpaperPreferences.edit {
                                        remove("yuheng_wallpaper_uri")
                                        remove("yuheng_wallpaper_file")
                                    }
                                    hasCustomWallpaper = false
                                },
                                headlineContent = { Text("恢复默认背景") },
                                supportingContent = { Text("移除自定义壁纸，恢复当前神兽主题背景") },
                                leadingContent = { Icon(Icons.Filled.Delete, "恢复默认背景") },
                            )
                        }
                    }
                }
            )

            SettingsSectionTitle("配置模板")
            val profileTemplate = stringResource(id = R.string.settings_profile_template)
            KsuIsValid {
                SegmentedColumn(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                    gradient = true,
                    content = listOf {
                        SegmentedListItem(
                            onClick = actions.onOpenProfileTemplate,
                            headlineContent = { Text(profileTemplate) },
                            supportingContent = { Text(stringResource(id = R.string.settings_profile_template_summary)) },
                            leadingContent = { Icon(Icons.Filled.Description, profileTemplate) },
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    null
                                )
                            }
                        )
                    }
                )
            }

            SettingsSectionTitle("系统行为与安全")
            KsuIsValid {
                val suCompatModeItems = listOf(
                    stringResource(id = R.string.settings_mode_enable_by_default),
                    stringResource(id = R.string.settings_mode_disable_until_reboot),
                    stringResource(id = R.string.settings_mode_disable_always),
                )

                SegmentedColumn(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                    gradient = true,
                    content = listOf(
                        {
                            val suSummary = when (uiState.suCompatStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_sucompat_summary)
                            }
                            SegmentedDropdownItem(
                                icon = Icons.Filled.AdminPanelSettings,
                                title = stringResource(id = R.string.settings_sucompat),
                                summary = suSummary,
                                items = suCompatModeItems,
                                enabled = uiState.suCompatStatus == "supported",
                                selectedIndex = uiState.suCompatMode,
                                onItemSelected = actions.onSetSuCompatMode
                            )
                        },
                        {
                            val umountSummary = when (uiState.kernelUmountStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_kernel_umount_summary)
                            }
                            SegmentedSwitchItem(
                                icon = Icons.Filled.LayersClear,
                                title = stringResource(id = R.string.settings_kernel_umount),
                                summary = umountSummary,
                                enabled = uiState.kernelUmountStatus == "supported",
                                checked = uiState.isKernelUmountEnabled,
                                onCheckedChange = actions.onSetKernelUmountEnabled
                            )
                        },
                        {
                            val selinuxHideSummary = when (uiState.selinuxHideStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_selinux_hide_summary)
                            }
                            SegmentedSwitchItem(
                                icon = Icons.Filled.Security,
                                title = stringResource(id = R.string.settings_selinux_hide),
                                summary = selinuxHideSummary,
                                enabled = uiState.selinuxHideStatus == "supported",
                                checked = uiState.isSelinuxHideEnabled,
                                onCheckedChange = actions.onSetSelinuxHideEnabled
                            )
                        },
                        {
                            val sulogSummary = when (uiState.sulogStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_sulog_summary)
                            }
                            SegmentedSwitchItem(
                                icon = Icons.AutoMirrored.Filled.Article,
                                title = stringResource(id = R.string.settings_sulog),
                                summary = sulogSummary,
                                enabled = uiState.sulogStatus == "supported",
                                checked = uiState.isSulogEnabled,
                                onCheckedChange = actions.onSetSulogEnabled
                            )
                        },
                        {
                            val adbRootSummary = when (uiState.adbRootStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_adb_root_summary)
                            }
                            SegmentedSwitchItem(
                                icon = Icons.Filled.Adb,
                                title = stringResource(id = R.string.settings_adb_root),
                                summary = adbRootSummary,
                                enabled = uiState.adbRootStatus == "supported",
                                checked = uiState.isAdbRootEnabled,
                                onCheckedChange = actions.onSetAdbRootEnabled
                            )
                        },
                        {
                            val shellSuAdbOnlySummary = when (uiState.shellSuAdbOnlyStatus) {
                                "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                                "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                                "forced" -> stringResource(id = R.string.feature_status_forced_summary)
                                else -> stringResource(id = R.string.settings_shell_su_adb_only_summary)
                            }
                            SegmentedSwitchItem(
                                icon = Icons.Filled.VerifiedUser,
                                title = stringResource(id = R.string.settings_shell_su_adb_only),
                                summary = shellSuAdbOnlySummary,
                                enabled = uiState.shellSuAdbOnlyStatus == "supported",
                                checked = uiState.isShellSuAdbOnlyEnabled,
                                onCheckedChange = actions.onSetShellSuAdbOnlyEnabled
                            )
                        },
                        {
                            SegmentedSwitchItem(
                                icon = Icons.Filled.RestartAlt,
                                title = stringResource(id = R.string.settings_soft_reboot),
                                summary = stringResource(id = R.string.settings_soft_reboot_summary),
                                enabled = !uiState.isLateLoadMode,
                                checked = uiState.isLateLoadMode || uiState.useSoftReboot,
                                onCheckedChange = actions.onSetUseSoftReboot
                            )
                        },
                    )
                )

                SettingsSectionTitle("高级选项")
                SegmentedColumn(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                    gradient = true,
                    content = listOf(
                        {
                            SegmentedSwitchItem(
                                icon = Icons.AutoMirrored.Filled.Rule,
                                title = stringResource(id = R.string.settings_umount_modules_default),
                                summary = stringResource(id = R.string.settings_umount_modules_default_summary),
                                checked = uiState.isDefaultUmountModules,
                                onCheckedChange = actions.onSetDefaultUmountModules
                            )
                        },
                        {
                            SegmentedSwitchItem(
                                icon = Icons.Filled.DeveloperMode,
                                title = stringResource(id = R.string.enable_web_debugging),
                                summary = stringResource(id = R.string.enable_web_debugging_summary),
                                checked = uiState.enableWebDebugging,
                                onCheckedChange = actions.onSetEnableWebDebugging
                            )
                        },
                        {
                            SegmentedSwitchItem(
                                icon = Icons.Filled.FlashOn,
                                title = stringResource(id = R.string.settings_auto_jailbreak),
                                summary = stringResource(id = R.string.settings_auto_jailbreak_summary),
                                enabled = uiState.isLateLoadMode,
                                checked = uiState.autoJailbreak,
                                onCheckedChange = actions.onSetAutoJailbreak
                            )
                        }
                    )
                )
            }

            if (uiState.isLkmMode) {
                SettingsSectionTitle("维护")
                SegmentedColumn(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                    gradient = true,
                    content = listOf(
                        {
                            val uninstall = stringResource(id = R.string.settings_uninstall)
                            SegmentedListItem(
                                onClick = { showUninstallDialog.value = true },
                                enabled = !uiState.isLateLoadMode,
                                headlineContent = { Text(uninstall) },
                                leadingContent = { Icon(Icons.Filled.Delete, uninstall, tint = Color(0xFFB76B62)) }
                            )
                        }
                    )
                )
            }

            SettingsSectionTitle("关于玉衡")
            SegmentedColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                    gradient = true,
                content = listOf(
                    {
                        SegmentedListItem(
                            onClick = { showBottomSheet = true },
                            headlineContent = { Text(stringResource(id = R.string.send_log)) },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.BugReport,
                                    stringResource(id = R.string.send_log)
                                )
                            },
                        )
                    },
                    {
                        SegmentedListItem(
                            onClick = actions.onOpenAbout,
                            headlineContent = { Text(stringResource(id = R.string.about)) },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.Info,
                                    stringResource(id = R.string.about)
                                )
                            },
                        )
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (showBottomSheet) {
                SendLogBottomSheet(
                    onDismiss = { showBottomSheet = false },
                    snackbarHostState = snackBarHost,
                )
            }
            Spacer(modifier = Modifier.height(bottomInnerPadding))
        }
    }
}

@Composable
private fun SettingsAppearanceIntro() {
    val preset = LocalYuhengThemePreset.current
    val dark = isInDarkTheme()
    val modeLabel = if (dark) "夜间雅色" else "日间雅色"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(preset.surface(dark).copy(alpha = 0.90f))
            .border(1.dp, preset.accent.copy(alpha = 0.24f), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp, vertical = 17.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "YU HENG  /  PERSONALIZATION",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = preset.accent,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = modeLabel,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(preset.accent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = preset.accent,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "让玉衡，呈现你的东方气韵",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            color = preset.text(dark),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "当前主题 · ${preset.creature}",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = preset.muted(dark),
        )
        Spacer(Modifier.height(13.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(preset.accent.copy(alpha = 0.28f))
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "外观个性化  /  系统行为  /  安全与维护",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = preset.muted(dark),
        )
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Row(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 13.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(LocalYuhengThemePreset.current.accent)
        )
        Text(
            text = title,
            modifier = Modifier.padding(start = 9.dp),
            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        colors = expressiveTopAppBarColors(),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}
