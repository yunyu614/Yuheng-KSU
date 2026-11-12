package me.weishu.kernelsu.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class YuhengPaletteTest {

    @Test
    fun fivePresetsHaveStableUniqueIdentity() {
        val presets = YuhengThemePreset.entries

        assertEquals("玉衡应内置五方神兽主题", 5, presets.size)
        assertEquals("每套主题必须使用独立主色", presets.size, presets.map { it.seedColor }.toSet().size)
        assertTrue("每套主题都必须有可用图标资源", presets.all { it.iconRes != 0 })
        assertTrue("主题名称和描述不可为空", presets.all {
            it.title.isNotBlank() && it.creature.isNotBlank() && it.subtitle.isNotBlank()
        })
    }

    @Test
    fun presetAccentsMatchTheFiveBeastPalette() {
        assertEquals(0xFF347B68.toInt(), YuhengThemePreset.QINGLONG.seedColor)
        assertEquals(0xFF9AA8B2.toInt(), YuhengThemePreset.BAIHU.seedColor)
        assertEquals(0xFFD6A82E.toInt(), YuhengThemePreset.XUANWU.seedColor)
        assertEquals(0xFFB9423A.toInt(), YuhengThemePreset.ZHUQUE.seedColor)
        assertEquals(0xFF9570B5.toInt(), YuhengThemePreset.QILIN.seedColor)
    }

    @Test
    fun seedColorLookupReturnsTheMatchingPreset() {
        YuhengThemePreset.entries.forEach { preset ->
            assertEquals(preset, YuhengThemePreset.fromSeedColor(preset.seedColor))
        }
        assertEquals(YuhengThemePreset.QINGLONG, YuhengThemePreset.fromSeedColor(0x12345678))
    }

    @Test
    fun textColorsMeetNormalTextContrastForBothModes() {
        YuhengThemePreset.entries.forEach { preset ->
            assertTrue(
                "${preset.title} 日间背景文字对比度不足",
                contrastRatio(preset.lightText, preset.lightBackground) >= 4.5,
            )
            assertTrue(
                "${preset.title} 日间卡片文字对比度不足",
                contrastRatio(preset.lightText, preset.lightSurface) >= 4.5,
            )
            assertTrue(
                "${preset.title} 夜间背景文字对比度不足",
                contrastRatio(preset.darkText, preset.darkBackground) >= 4.5,
            )
            assertTrue(
                "${preset.title} 夜间卡片文字对比度不足",
                contrastRatio(preset.darkText, preset.darkSurface) >= 4.5,
            )
            assertTrue(
                "${preset.title} 日间辅助文字背景对比度不足",
                contrastRatio(preset.lightMuted, preset.lightBackground) >= 4.5,
            )
            assertTrue(
                "${preset.title} 日间辅助文字卡片对比度不足",
                contrastRatio(preset.lightMuted, preset.lightSurface) >= 4.5,
            )
            assertTrue(
                "${preset.title} 夜间辅助文字背景对比度不足",
                contrastRatio(preset.darkMuted, preset.darkBackground) >= 4.5,
            )
            assertTrue(
                "${preset.title} 夜间辅助文字卡片对比度不足",
                contrastRatio(preset.darkMuted, preset.darkSurface) >= 4.5,
            )
            assertTrue(
                "${preset.title} 日间强调文字背景对比度不足",
                contrastRatio(preset.lightAccentText, preset.lightBackground) >= 4.5,
            )
            assertTrue(
                "${preset.title} 日间强调文字卡片对比度不足",
                contrastRatio(preset.lightAccentText, preset.lightSurface) >= 4.5,
            )
            assertTrue(
                "${preset.title} 夜间强调文字背景对比度不足",
                contrastRatio(preset.darkAccentText, preset.darkBackground) >= 4.5,
            )
            assertTrue(
                "${preset.title} 夜间强调文字卡片对比度不足",
                contrastRatio(preset.darkAccentText, preset.darkSurface) >= 4.5,
            )
        }
    }

    private fun contrastRatio(foreground: Int, background: Int): Double {
        val foregroundLuminance = relativeLuminance(foreground)
        val backgroundLuminance = relativeLuminance(background)
        val lighter = maxOf(foregroundLuminance, backgroundLuminance)
        val darker = minOf(foregroundLuminance, backgroundLuminance)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun relativeLuminance(color: Int): Double {
        fun linear(channel: Int): Double {
            val normalized = channel / 255.0
            return if (normalized <= 0.04045) {
                normalized / 12.92
            } else {
                Math.pow((normalized + 0.055) / 1.055, 2.4)
            }
        }

        val red = linear((color shr 16) and 0xFF)
        val green = linear((color shr 8) and 0xFF)
        val blue = linear(color and 0xFF)
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue
    }
}
