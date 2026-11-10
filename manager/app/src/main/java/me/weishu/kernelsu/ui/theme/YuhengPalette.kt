package me.weishu.kernelsu.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import me.weishu.kernelsu.R

enum class YuhengThemePreset(
    val title: String,
    val creature: String,
    val iconRes: Int,
    val subtitle: String,
    val seedColor: Int,
    val lightBackground: Int,
    val lightSurface: Int,
    val darkBackground: Int,
    val darkSurface: Int,
    val lightText: Int,
    val darkText: Int,
    val lightMuted: Int,
    val darkMuted: Int,
    val ornament: Int,
) {
    QINGLONG("青龙", "苍龙行云", R.drawable.ic_theme_qinglong, "苍青 · 云纹 · 星宿", 0xFF347B68.toInt(), 0xFFEAF1EB.toInt(), 0xFFF8FBF6.toInt(), 0xFF111D19.toInt(), 0xFF1B2B24.toInt(), 0xFF203B31.toInt(), 0xFFE5EFE8.toInt(), 0xFF687D70.toInt(), 0xFFB0C2B7.toInt(), 0xFFC8AD75.toInt()),
    BAIHU("白虎", "白虎镇岳", R.drawable.ic_theme_baihu, "霜银 · 虎纹 · 山岳", 0xFF9AA8B2.toInt(), 0xFFEDF0F2.toInt(), 0xFFFAFBFC.toInt(), 0xFF171D22.toInt(), 0xFF252E35.toInt(), 0xFF29343B.toInt(), 0xFFE9EEF1.toInt(), 0xFF707E87.toInt(), 0xFFB7C1C8.toInt(), 0xFFD5DEE3.toInt()),
    XUANWU("玄武", "玄武负山", R.drawable.ic_theme_xuanwu, "玄黄 · 龟甲 · 北斗", 0xFFD6A82E.toInt(), 0xFFF3EEDC.toInt(), 0xFFFFFCF1.toInt(), 0xFF211D12.toInt(), 0xFF302817.toInt(), 0xFF443718.toInt(), 0xFFF3E9C9.toInt(), 0xFF897849.toInt(), 0xFFC9B985.toInt(), 0xFFE8C86F.toInt()),
    ZHUQUE("朱雀", "朱雀衔火", R.drawable.ic_theme_zhuque, "朱砂 · 凤羽 · 日轮", 0xFFB9423A.toInt(), 0xFFF5EAE6.toInt(), 0xFFFFF9F6.toInt(), 0xFF211615.toInt(), 0xFF332321.toInt(), 0xFF4A2824.toInt(), 0xFFF5E7E2.toInt(), 0xFF91645C.toInt(), 0xFFD1AAA1.toInt(), 0xFFE6A17C.toInt()),
    QILIN("麒麟", "麒麟呈瑞", R.drawable.ic_theme_qilin, "紫金 · 祥云 · 瑞鳞", 0xFF9570B5.toInt(), 0xFFF0EBF5.toInt(), 0xFFFCF9FF.toInt(), 0xFF1D1824.toInt(), 0xFF2D2537.toInt(), 0xFF3D2D50.toInt(), 0xFFF0E8F7.toInt(), 0xFF78658C.toInt(), 0xFFC2B2D2.toInt(), 0xFFD6B77A.toInt());

    companion object {
        fun fromSeedColor(seedColor: Int): YuhengThemePreset =
            entries.firstOrNull { it.seedColor == seedColor } ?: QINGLONG
    }
    fun background(dark: Boolean) = Color(if (dark) darkBackground else lightBackground)
    fun surface(dark: Boolean) = Color(if (dark) darkSurface else lightSurface)
    fun text(dark: Boolean) = Color(if (dark) darkText else lightText)
    fun muted(dark: Boolean) = Color(if (dark) darkMuted else lightMuted)
    val accent: Color get() = Color(seedColor)
    val decorative: Color get() = Color(ornament)
}

val LocalYuhengThemePreset = staticCompositionLocalOf { YuhengThemePreset.QINGLONG }
