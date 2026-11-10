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
import androidx.compose.ui.draw.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
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
    val accent = preset.accent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(136.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(yuhengHeroBrush()),
    ) {
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 8.dp)
        ) {
            drawBeastArtwork(preset, alpha = 0.92f)
            drawCircle(preset.decorative.copy(alpha = 0.18f), radius = size.minDimension * 0.48f, style = Stroke(1.dp.toPx()))
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
                text = "五方神兽  ·  东方精工",
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
                // A different low-contrast beast illustration is part of each preset's atmosphere.
                drawBeastArtwork(
                    preset = preset,
                    alpha = if (dark) 0.15f else 0.12f,
                    center = Offset(size.width * 0.78f, size.height * 0.29f),
                    scale = size.minDimension * 0.74f,
                )
                val glow = preset.accent.copy(alpha = if (dark) 0.12f else 0.10f)
                drawCircle(
                    brush = Brush.radialGradient(listOf(glow, Color.Transparent), center = Offset(size.width * 0.78f, size.height * 0.28f), radius = size.minDimension * 0.66f),
                    radius = size.minDimension * 0.66f,
                    center = Offset(size.width * 0.78f, size.height * 0.28f),
                )
                val ornament = preset.decorative.copy(alpha = if (dark) 0.11f else 0.14f)
                drawCircle(ornament, radius = size.minDimension * 0.16f, center = Offset(size.width * 0.12f, size.height * 0.78f), style = Stroke(1.dp.toPx()))
                drawCircle(ornament.copy(alpha = 0.55f), radius = size.minDimension * 0.11f, center = Offset(size.width * 0.12f, size.height * 0.78f), style = Stroke(0.8.dp.toPx()))
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

/**
 * A recognizable, lightweight line illustration for the Five Beasts.
 * Drawn once by Compose Canvas; no bitmap decoding or per-frame blur is involved.
 */
@Composable
fun YuhengBeastIllustration(
    preset: YuhengThemePreset = LocalYuhengThemePreset.current,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) { drawBeastArtwork(preset, alpha = 0.94f) }
}

private fun DrawScope.drawBeastArtwork(
    preset: YuhengThemePreset,
    alpha: Float,
    center: Offset = Offset(size.width * 0.5f, size.height * 0.5f),
    scale: Float = size.minDimension,
) {
    val ink = preset.accent.copy(alpha = alpha)
    val detail = preset.decorative.copy(alpha = alpha * 0.92f)
    val stroke = (scale * 0.018f).coerceAtLeast(1.2f)
    fun p(x: Float, y: Float) = Offset(center.x + (x - 0.5f) * scale, center.y + (y - 0.5f) * scale)
    fun line(points: List<Offset>, color: Color = ink, width: Float = stroke) {
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
        }
        drawPath(path, color, style = Stroke(width = width))
    }
    when (preset) {
        YuhengThemePreset.QINGLONG -> {
            val body = Path().apply {
                moveTo(p(0.72f, 0.18f).x, p(0.72f, 0.18f).y)
                cubicTo(p(0.94f, 0.28f).x, p(0.94f, 0.28f).y, p(0.30f, 0.36f).x, p(0.30f, 0.36f).y, p(0.36f, 0.55f).x, p(0.36f, 0.55f).y)
                cubicTo(p(0.42f, 0.75f).x, p(0.42f, 0.75f).y, p(0.88f, 0.64f).x, p(0.88f, 0.64f).y, p(0.68f, 0.86f).x, p(0.68f, 0.86f).y)
            }
            drawPath(body, ink, style = Stroke(stroke * 2.1f))
            drawOval(ink, topLeft = p(0.66f, 0.09f), size = androidx.compose.ui.geometry.Size(scale * 0.23f, scale * 0.17f))
            line(listOf(p(0.70f,0.10f),p(0.64f,0.00f),p(0.73f,0.07f)), detail)
            line(listOf(p(0.80f,0.12f),p(0.88f,0.02f),p(0.85f,0.14f)), detail)
            line(listOf(p(0.86f,0.18f),p(0.96f,0.20f)), detail)
            drawCircle(detail, scale * 0.012f, p(0.82f,0.14f))
            for (i in 0..5) {
                val x = 0.39f + i * 0.065f
                line(listOf(p(x,0.38f),p(x-0.02f,0.31f),p(x+0.035f,0.35f)), detail, stroke * 0.75f)
            }
        }
        YuhengThemePreset.BAIHU -> {
            drawOval(ink, topLeft = p(0.18f,0.37f), size = androidx.compose.ui.geometry.Size(scale*0.60f,scale*0.34f), style = Stroke(stroke*1.8f))
            drawCircle(ink, scale*0.15f, p(0.73f,0.39f), style = Stroke(stroke*1.8f))
            line(listOf(p(0.64f,0.28f),p(0.63f,0.15f),p(0.73f,0.25f)),ink,stroke*1.2f)
            line(listOf(p(0.76f,0.25f),p(0.86f,0.14f),p(0.84f,0.31f)),ink,stroke*1.2f)
            for(i in 0..3) {
                val x=0.29f+i*0.105f
                val stripe=Path().apply { moveTo(p(x,0.39f).x,p(x,0.39f).y); cubicTo(p(x-0.09f,0.48f).x,p(x-0.09f,0.48f).y,p(x+0.06f,0.55f).x,p(x+0.06f,0.55f).y,p(x-0.02f,0.67f).x,p(x-0.02f,0.67f).y) }
                drawPath(stripe,detail,style=Stroke(stroke*1.15f))
            }
            drawCircle(detail,stroke*0.8f,p(0.77f,0.37f))
            line(listOf(p(0.78f,0.46f),p(0.84f,0.48f),p(0.88f,0.44f)),detail,stroke*0.8f)
            line(listOf(p(0.28f,0.67f),p(0.24f,0.82f),p(0.34f,0.82f)),ink)
            line(listOf(p(0.67f,0.66f),p(0.72f,0.81f),p(0.81f,0.80f)),ink)
        }
        YuhengThemePreset.XUANWU -> {
            drawOval(ink,topLeft=p(0.23f,0.27f),size=androidx.compose.ui.geometry.Size(scale*0.52f,scale*0.48f),style=Stroke(stroke*1.8f))
            for(i in 0..3) {
                val x=0.31f+i*0.11f
                line(listOf(p(x,0.31f),p(x-0.04f,0.48f),p(x,0.68f)),detail,stroke*0.75f)
            }
            line(listOf(p(0.26f,0.43f),p(0.70f,0.43f)),detail,stroke*0.75f)
            line(listOf(p(0.25f,0.56f),p(0.70f,0.56f)),detail,stroke*0.75f)
            val snake=Path().apply { moveTo(p(0.69f,0.57f).x,p(0.69f,0.57f).y); cubicTo(p(0.92f,0.63f).x,p(0.92f,0.63f).y,p(0.88f,0.88f).x,p(0.88f,0.88f).y,p(0.67f,0.82f).x,p(0.67f,0.82f).y); cubicTo(p(0.54f,0.77f).x,p(0.54f,0.77f).y,p(0.79f,0.68f).x,p(0.79f,0.68f).y,p(0.83f,0.52f).x,p(0.83f,0.52f).y) }
            drawPath(snake,detail,style=Stroke(stroke*1.8f))
            drawCircle(detail,scale*0.055f,p(0.85f,0.48f))
            line(listOf(p(0.31f,0.69f),p(0.23f,0.80f),p(0.35f,0.82f)),ink)
            line(listOf(p(0.62f,0.69f),p(0.70f,0.81f),p(0.78f,0.77f)),ink)
        }
        YuhengThemePreset.ZHUQUE -> {
            drawOval(ink,topLeft=p(0.42f,0.42f),size=androidx.compose.ui.geometry.Size(scale*0.18f,scale*0.31f))
            val left=Path().apply { moveTo(p(0.48f,0.49f).x,p(0.48f,0.49f).y); cubicTo(p(0.18f,0.38f).x,p(0.18f,0.38f).y,p(0.11f,0.12f).x,p(0.11f,0.12f).y,p(0.39f,0.30f).x,p(0.39f,0.30f).y); cubicTo(p(0.26f,0.28f).x,p(0.26f,0.28f).y,p(0.25f,0.50f).x,p(0.25f,0.50f).y,p(0.48f,0.60f).x,p(0.48f,0.60f).y) }
            val right=Path().apply { moveTo(p(0.54f,0.49f).x,p(0.54f,0.49f).y); cubicTo(p(0.82f,0.35f).x,p(0.82f,0.35f).y,p(0.91f,0.12f).x,p(0.91f,0.12f).y,p(0.62f,0.30f).x,p(0.62f,0.30f).y); cubicTo(p(0.76f,0.29f).x,p(0.76f,0.29f).y,p(0.78f,0.52f).x,p(0.78f,0.52f).y,p(0.54f,0.60f).x,p(0.54f,0.60f).y) }
            drawPath(left,ink,style=Stroke(stroke*1.7f)); drawPath(right,ink,style=Stroke(stroke*1.7f))
            for(i in 0..3) {
                val y=0.35f+i*0.10f
                line(listOf(p(0.42f,y),p(0.30f,y-0.07f),p(0.23f,y-0.13f)),detail,stroke*0.85f)
                line(listOf(p(0.58f,y),p(0.70f,y-0.07f),p(0.77f,y-0.13f)),detail,stroke*0.85f)
            }
            line(listOf(p(0.50f,0.68f),p(0.40f,0.88f),p(0.50f,0.81f),p(0.60f,0.91f),p(0.56f,0.69f)),detail,stroke*1.2f)
            drawCircle(ink,scale*0.07f,p(0.51f,0.34f))
            line(listOf(p(0.51f,0.29f),p(0.48f,0.20f),p(0.53f,0.25f)),detail)
        }
        YuhengThemePreset.QILIN -> {
            drawOval(ink,topLeft=p(0.19f,0.43f),size=androidx.compose.ui.geometry.Size(scale*0.52f,scale*0.27f),style=Stroke(stroke*1.8f))
            line(listOf(p(0.62f,0.49f),p(0.69f,0.31f),p(0.78f,0.25f),p(0.86f,0.30f)),ink,stroke*1.7f)
            drawCircle(ink,scale*0.09f,p(0.82f,0.29f),style=Stroke(stroke*1.4f))
            line(listOf(p(0.79f,0.21f),p(0.82f,0.09f),p(0.85f,0.22f)),detail,stroke*1.1f)
            line(listOf(p(0.87f,0.23f),p(0.94f,0.12f),p(0.91f,0.27f)),detail,stroke*1.1f)
            line(listOf(p(0.69f,0.34f),p(0.62f,0.20f),p(0.70f,0.27f),p(0.73f,0.13f)),detail,stroke)
            line(listOf(p(0.30f,0.66f),p(0.27f,0.85f),p(0.36f,0.85f)),ink)
            line(listOf(p(0.61f,0.66f),p(0.66f,0.84f),p(0.74f,0.83f)),ink)
            line(listOf(p(0.19f,0.49f),p(0.08f,0.38f),p(0.12f,0.30f)),detail,stroke*1.1f)
        }
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
