package me.weishu.kernelsu.ui.component

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import java.io.File
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Path
import me.weishu.kernelsu.ui.theme.YuhengThemePreset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.theme.LocalYuhengThemePreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun yuhengHeroBrush(): Brush {
    val dark = isInDarkTheme()
    val preset = LocalYuhengThemePreset.current
    return Brush.linearGradient(listOf(preset.background(dark), preset.surface(dark), preset.background(dark)))
}

/**
 * Theme-specific filled scenery. These are environmental silhouettes, not enlarged beast glyphs:
 * each preset gets a distinct visual language while remaining lightweight to render.
 */
@Composable
fun YuhengThemeAtmosphere(
    preset: YuhengThemePreset = LocalYuhengThemePreset.current,
    modifier: Modifier = Modifier,
    alpha: Float = 0.16f,
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val ink = preset.accent.copy(alpha = alpha)
        val ornament = preset.decorative.copy(alpha = alpha * 0.82f)
        fun shape(color: Color, build: Path.() -> Unit) {
            drawPath(Path().apply(build), color)
        }
        when (preset) {
            YuhengThemePreset.QINGLONG -> {
                drawCircle(ornament, h * 0.28f, Offset(w * 0.82f, h * 0.22f))
                shape(ink) {
                    moveTo(0f, h * 0.82f); cubicTo(w * .18f, h * .58f, w * .30f, h * .76f, w * .46f, h * .62f)
                    cubicTo(w * .62f, h * .48f, w * .72f, h * .72f, w, h * .48f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
                shape(ornament) {
                    moveTo(0f, h * .91f); cubicTo(w * .22f, h * .72f, w * .32f, h * .92f, w * .53f, h * .78f)
                    cubicTo(w * .72f, h * .65f, w * .83f, h * .86f, w, h * .72f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
                drawCircle(ornament, h * .10f, Offset(w * .22f, h * .34f))
                drawCircle(ornament, h * .07f, Offset(w * .31f, h * .31f))
            }
            YuhengThemePreset.BAIHU -> {
                drawCircle(ornament, h * .25f, Offset(w * .80f, h * .24f))
                shape(ink) {
                    moveTo(0f, h); lineTo(w * .22f, h * .35f); lineTo(w * .37f, h * .66f)
                    lineTo(w * .56f, h * .20f); lineTo(w * .78f, h * .62f); lineTo(w, h * .30f)
                    lineTo(w, h); close()
                }
                shape(ornament) {
                    moveTo(0f, h); lineTo(w * .30f, h * .63f); lineTo(w * .46f, h * .82f)
                    lineTo(w * .70f, h * .48f); lineTo(w, h * .78f); lineTo(w, h); close()
                }
            }
            YuhengThemePreset.XUANWU -> {
                drawCircle(ornament, h * .31f, Offset(w * .76f, h * .28f))
                shape(ink) {
                    moveTo(0f, h * .72f); cubicTo(w * .22f, h * .52f, w * .35f, h * .88f, w * .56f, h * .68f)
                    cubicTo(w * .76f, h * .48f, w * .86f, h * .82f, w, h * .60f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
                shape(ornament) {
                    moveTo(0f, h * .88f); cubicTo(w * .20f, h * .68f, w * .36f, h, w * .57f, h * .82f)
                    cubicTo(w * .76f, h * .64f, w * .87f, h * .98f, w, h * .78f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
                drawCircle(ink, h * .13f, Offset(w * .24f, h * .36f))
            }
            YuhengThemePreset.ZHUQUE -> {
                drawCircle(ornament, h * .31f, Offset(w * .78f, h * .26f))
                shape(ink) {
                    moveTo(w * .48f, h); cubicTo(w * .18f, h * .68f, w * .42f, h * .51f, w * .28f, h * .20f)
                    cubicTo(w * .55f, h * .40f, w * .52f, h * .14f, w * .68f, 0f)
                    cubicTo(w * .67f, h * .38f, w * .94f, h * .48f, w * .72f, h)
                    close()
                }
                shape(ornament) {
                    moveTo(w * .60f, h); cubicTo(w * .42f, h * .73f, w * .66f, h * .62f, w * .60f, h * .39f)
                    cubicTo(w * .86f, h * .64f, w * .80f, h * .82f, w, h)
                    close()
                }
            }
            YuhengThemePreset.QILIN -> {
                drawCircle(ornament, h * .27f, Offset(w * .78f, h * .22f))
                drawCircle(ink, h * .13f, Offset(w * .18f, h * .54f))
                drawCircle(ink, h * .16f, Offset(w * .31f, h * .49f))
                drawCircle(ink, h * .12f, Offset(w * .44f, h * .55f))
                shape(ink) {
                    moveTo(0f, h * .82f); cubicTo(w * .20f, h * .64f, w * .34f, h * .84f, w * .52f, h * .68f)
                    cubicTo(w * .70f, h * .52f, w * .82f, h * .76f, w, h * .58f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
                shape(ornament) {
                    moveTo(0f, h * .94f); cubicTo(w * .24f, h * .76f, w * .40f, h * .98f, w * .60f, h * .82f)
                    cubicTo(w * .76f, h * .68f, w * .90f, h * .90f, w, h * .80f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
            }
        }
    }
}

@Composable
fun Modifier.yuhengCardGradient(shape: Shape = RoundedCornerShape(18.dp)): Modifier =
    clip(shape)
        .background(yuhengCardColor())
        .border(BorderStroke(1.dp, LocalYuhengThemePreset.current.accent.copy(alpha = 0.14f)), shape)

@Composable
fun YuhengHeroCard(modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
    val preset = LocalYuhengThemePreset.current
    val title = preset.text(dark)
    val muted = preset.muted(dark)
    val accent = preset.accentText(dark)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(136.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(yuhengHeroBrush()),
    ) {
        YuhengThemeAtmosphere(
            preset = preset,
            modifier = Modifier.fillMaxSize(),
            alpha = if (dark) 0.28f else 0.22f,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accent),
                )
                Spacer(Modifier.width(8.dp))
                BasicText(
                    text = "Y U H E N G   ·   玉 衡",
                    style = TextStyle(
                        color = accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.1.sp,
                    ),
                )
            }
            Spacer(Modifier.height(8.dp))
            BasicText(
                text = "玉衡 · ${preset.creature}",
                style = TextStyle(
                    color = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                ),
            )
            Spacer(Modifier.height(4.dp))
            BasicText(
                text = preset.subtitle,
                style = TextStyle(
                    color = muted,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp,
                ),
            )
            Spacer(Modifier.height(8.dp))
            BasicText(
                text = "${preset.title}  ·  东方精工",
                style = TextStyle(
                    color = accent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                ),
            )
        }
    }
}

@Composable
fun YuhengAppBackground(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dark = isInDarkTheme()
    val preset = LocalYuhengThemePreset.current
    val preferences = remember(context) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    var wallpaperFilePath by remember(preferences) {
        mutableStateOf(preferences.getString("yuheng_wallpaper_file", null))
    }
    var wallpaperUri by remember(preferences) {
        mutableStateOf(preferences.getString("yuheng_wallpaper_uri", null))
    }
    DisposableEffect(preferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == null || key == "yuheng_wallpaper_file" || key == "yuheng_wallpaper_uri") {
                wallpaperFilePath = preferences.getString("yuheng_wallpaper_file", null)
                wallpaperUri = preferences.getString("yuheng_wallpaper_uri", null)
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    var wallpaper by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(wallpaperFilePath, wallpaperUri) {
        wallpaper = withContext(Dispatchers.IO) {
            runCatching {
                val wallpaperContentUri = wallpaperUri?.toUri()
                val file = wallpaperFilePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L }
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                if (file != null) {
                    BitmapFactory.decodeFile(file.absolutePath, bounds)
                } else if (wallpaperContentUri != null) {
                    context.contentResolver.openInputStream(wallpaperContentUri)?.use {
                        BitmapFactory.decodeStream(it, null, bounds)
                    }
                }
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null
                var sampleSize = 1
                while (bounds.outWidth / sampleSize > 1800 || bounds.outHeight / sampleSize > 2800) {
                    sampleSize *= 2
                }
                val options = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                if (file != null) {
                    BitmapFactory.decodeFile(file.absolutePath, options)
                } else {
                    wallpaperContentUri?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it, null, options)
                        }
                    }
                }
            }.getOrNull()
        }
    }

    val baseBackground = preset.background(dark)
    val surfaceBackground = preset.surface(dark)
    val brush = Brush.verticalGradient(listOf(baseBackground, surfaceBackground, baseBackground))
    Box(Modifier.fillMaxSize().background(brush)) {
        if (wallpaper == null) {
            YuhengThemeAtmosphere(
                preset = preset,
                modifier = Modifier.fillMaxSize(),
                alpha = if (dark) 0.10f else 0.075f,
            )
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 56.dp, y = 78.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                preset.accent.copy(alpha = if (dark) 0.10f else 0.08f),
                                Color.Transparent,
                            )
                        ),
                        CircleShape,
                    )
            )
        }
        wallpaper?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        if (wallpaper != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(baseBackground.copy(alpha = if (dark) 0.46f else 0.40f))
            )
        }
        content()
    }
}

@Composable
fun yuhengCardColor(): Color = LocalYuhengThemePreset.current.surface(isInDarkTheme()).copy(alpha = 0.76f)

@Composable
fun YuhengDeviceHintCard(deviceModel: String, modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
    val preset = LocalYuhengThemePreset.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(yuhengCardColor())
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(preset.accent),
        )
        Spacer(Modifier.width(12.dp))
        Column {
            BasicText(
                text = "设备信息",
                style = TextStyle(
                    color = preset.muted(dark),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Spacer(Modifier.height(4.dp))
            BasicText(
                text = deviceModel,
                style = TextStyle(
                    color = preset.text(dark),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}
