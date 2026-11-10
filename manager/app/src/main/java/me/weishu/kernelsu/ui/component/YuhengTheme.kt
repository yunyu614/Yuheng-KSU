package me.weishu.kernelsu.ui.component

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.theme.LocalYuhengThemePreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val JadeInk = Color(0xFF263B32)
private val JadeText = Color(0xFFE4EEE8)
private val JadeMuted = Color(0xFF687A70)

@Composable
fun yuhengHeroBrush(): Brush {
    val dark = isInDarkTheme()
    val preset = LocalYuhengThemePreset.current
    return Brush.linearGradient(listOf(preset.background(dark), preset.surface(dark), preset.background(dark)))
}

@Composable
fun Modifier.yuhengCardGradient(shape: Shape = RoundedCornerShape(18.dp)): Modifier =
    clip(shape).background(yuhengCardColor())

@Composable
fun YuhengHeroCard(modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
    val preset = LocalYuhengThemePreset.current
    val title = preset.text(dark)
    val muted = preset.muted(dark)
    val accent = preset.accent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(136.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(yuhengHeroBrush()),
    ) {
        val ornament = preset.decorative
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp)
        ) {
            val center = Offset(size.width * 0.52f, size.height * 0.5f)
            drawCircle(ornament.copy(alpha = 0.24f), radius = size.minDimension * 0.43f, center = center, style = Stroke(1.1.dp.toPx()))
            drawCircle(ornament.copy(alpha = 0.20f), radius = size.minDimension * 0.32f, center = center, style = Stroke(0.8.dp.toPx()))
            drawCircle(accent.copy(alpha = 0.30f), radius = size.minDimension * 0.13f, center = center, style = Stroke(1.dp.toPx()))
            drawLine(ornament.copy(alpha = 0.26f), Offset(center.x, center.y - size.minDimension * 0.48f), Offset(center.x, center.y + size.minDimension * 0.48f), 0.8.dp.toPx())
            drawLine(ornament.copy(alpha = 0.26f), Offset(center.x - size.minDimension * 0.48f, center.y), Offset(center.x + size.minDimension * 0.48f, center.y), 0.8.dp.toPx())
            drawCircle(ornament.copy(alpha = 0.65f), radius = 2.2.dp.toPx(), center = Offset(center.x, center.y - size.minDimension * 0.43f))
            drawCircle(ornament.copy(alpha = 0.65f), radius = 2.2.dp.toPx(), center = Offset(center.x + size.minDimension * 0.43f, center.y))
            drawCircle(ornament.copy(alpha = 0.65f), radius = 2.2.dp.toPx(), center = Offset(center.x, center.y + size.minDimension * 0.43f))
            drawCircle(ornament.copy(alpha = 0.65f), radius = 2.2.dp.toPx(), center = Offset(center.x - size.minDimension * 0.43f, center.y))
        }
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
                text = "以玉为度，令系统井然有序",
                style = TextStyle(
                    color = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                ),
            )
            Spacer(Modifier.height(4.dp))
            BasicText(
                text = "以玉为度，简而有序",
                style = TextStyle(
                    color = muted,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp,
                ),
            )
            Spacer(Modifier.height(8.dp))
            BasicText(
                text = "东方美学  ·  精致管理",
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
                val file = wallpaperFilePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L }
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                if (file != null) {
                    BitmapFactory.decodeFile(file.absolutePath, bounds)
                } else if (wallpaperUri != null) {
                    context.contentResolver.openInputStream(Uri.parse(wallpaperUri))?.use {
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
                    context.contentResolver.openInputStream(Uri.parse(wallpaperUri!!))?.use {
                        BitmapFactory.decodeStream(it, null, options)
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
            Canvas(Modifier.fillMaxSize()) {
                val ornament = preset.decorative.copy(alpha = if (dark) 0.10f else 0.13f)
                val radius = size.minDimension * 0.22f
                val center = Offset(size.width * 0.88f, size.height * 0.16f)
                drawCircle(ornament, radius = radius, center = center, style = Stroke(1.2.dp.toPx()))
                drawCircle(ornament.copy(alpha = 0.55f), radius = radius * 0.72f, center = center, style = Stroke(0.8.dp.toPx()))
                drawCircle(ornament.copy(alpha = 0.42f), radius = radius * 0.42f, center = center, style = Stroke(0.7.dp.toPx()))
                drawLine(ornament, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 0.7.dp.toPx())
                drawLine(ornament, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 0.7.dp.toPx())
                val lower = Offset(size.width * 0.08f, size.height * 0.82f)
                drawCircle(ornament.copy(alpha = 0.55f), radius = radius * 0.58f, center = lower, style = Stroke(0.9.dp.toPx()))
            }
        }
        wallpaper?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    baseBackground.copy(alpha = if (dark) 0.48f else 0.42f)
                )
        )
        content()
    }
}

@Composable
fun yuhengCardColor(): Color = LocalYuhengThemePreset.current.surface(isInDarkTheme())

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
