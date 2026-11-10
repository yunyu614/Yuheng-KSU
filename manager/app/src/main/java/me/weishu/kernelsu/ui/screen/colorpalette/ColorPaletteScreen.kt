package me.weishu.kernelsu.ui.screen.colorpalette

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import me.weishu.kernelsu.KernelSUApplication
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.navigation3.LocalNavigator
import me.weishu.kernelsu.ui.theme.ColorMode
import me.weishu.kernelsu.ui.theme.YuhengThemePreset
import me.weishu.kernelsu.ui.viewmodel.SettingsViewModel

@Composable
fun ColorPaletteScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val wallpaperPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val target = File(context.filesDir, "yuheng-wallpaper")
                val temp = File(context.filesDir, "yuheng-wallpaper.tmp")
                val copyResult = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            temp.outputStream().use { output -> input.copyTo(output) }
                        } ?: error("无法读取所选图片")
                        check(temp.isFile && temp.length() > 0L) { "图片文件为空" }
                        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeFile(temp.absolutePath, bounds)
                        check(bounds.outWidth > 0 && bounds.outHeight > 0) { "所选文件不是有效图片" }
                        if (target.exists()) check(target.delete()) { "无法替换旧壁纸" }
                        check(temp.renameTo(target)) { "无法保存壁纸文件" }
                        target
                    }
                }
                copyResult.onSuccess { copied ->
                    context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .edit()
                        .putString("yuheng_wallpaper_file", copied.absolutePath)
                        .remove("yuheng_wallpaper_uri")
                        .apply()
                    activity?.recreate()
                }.onFailure { error ->
                    temp.delete()
                    Toast.makeText(
                        context,
                        "壁纸导入失败：${error.localizedMessage ?: "无法读取图片"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    val viewModel = viewModel<SettingsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentPaletteStyle = try {
        PaletteStyle.valueOf(uiState.colorStyle)
    } catch (_: Exception) {
        PaletteStyle.TonalSpot
    }
    val currentColorSpec = try {
        ColorSpec.SpecVersion.valueOf(uiState.colorSpec)
    } catch (_: Exception) {
        ColorSpec.SpecVersion.SPEC_2025
    }
    val state = ColorPaletteUiState(
        uiState = uiState,
        currentColorMode = ColorMode.fromValue(uiState.themeMode),
        currentPaletteStyle = currentPaletteStyle,
        currentColorSpec = currentColorSpec,
    )
    val actions = ColorPaletteScreenActions(
        onBack = dropUnlessResumed { navigator.pop() },
        onPickWallpaper = { wallpaperPicker.launch(arrayOf("image/*")) },
        onClearWallpaper = {
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit()
                .remove("yuheng_wallpaper_uri")
                .remove("yuheng_wallpaper_file")
                .apply()
            File(context.filesDir, "yuheng-wallpaper").delete()
            activity?.recreate()
        },
        onSetThemeMode = viewModel::setThemeMode,
        onApplyYuhengPreset = { preset ->
            val selectedPreset = YuhengThemePreset.entries.getOrElse(preset) { YuhengThemePreset.QINGLONG }
            viewModel.setKeyColor(selectedPreset.seedColor)
            viewModel.setColorStyle(PaletteStyle.TonalSpot.name)
        },
        onSetMiuixMonet = viewModel::setMiuixMonet,
        onSetKeyColor = viewModel::setKeyColor,
        onSetColorMode = viewModel::setColorMode,
        onSetColorStyle = viewModel::setColorStyle,
        onSetColorSpec = viewModel::setColorSpec,
        onSetEnableBlur = viewModel::setEnableBlur,
        onSetEnableFloatingBottomBar = viewModel::setEnableFloatingBottomBar,
        onSetEnableFloatingBottomBarBlur = viewModel::setEnableFloatingBottomBarBlur,
        onSetEnableNavigationBadge = viewModel::setEnableNavigationBadge,
        onSetEnablePredictiveBack = {
            viewModel.setEnablePredictiveBack(it)
            KernelSUApplication.setEnableOnBackInvokedCallback(context.applicationInfo, it)
            activity?.recreate()
        },
        onSetEnableSwipeDismiss = viewModel::setEnableSwipeDismiss,
        onSetPagerInterceptionMode = viewModel::setPagerInterceptionMode,
        onSetPageScale = viewModel::setPageScale,
        onSetModuleDescriptionMaxLines = viewModel::setModuleDescriptionMaxLines,
    )

    when (LocalUiMode.current) {
        UiMode.Miuix -> ColorPaletteScreenMiuix(state, actions)
        UiMode.Material -> ColorPaletteScreenMaterial(state, actions)
    }
}
