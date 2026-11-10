package me.weishu.kernelsu.ui.component

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val JadeInk = Color(0xFF263B32)
private val JadeText = Color(0xFFE4EEE8)
private val JadeMuted = Color(0xFF687A70)

@Composable
fun yuhengHeroBrush(): Brush = if (isInDarkTheme()) {
    Brush.linearGradient(listOf(Color(0xFF1B2420), Color(0xFF202D26), Color(0xFF26362E)))
} else {
    Brush.linearGradient(listOf(Color(0xFFE7EEE9), Color(0xFFDDE7E0), Color(0xFFD5E1D9)))
}

@Composable
fun Modifier.yuhengCardGradient(shape: Shape = RoundedCornerShape(18.dp)): Modifier =
    clip(shape).background(yuhengCardColor())

@Composable
fun YuhengHeroCard(modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
    val title = if (dark) JadeText else JadeInk
    val muted = if (dark) Color(0xFFB5C9BE) else JadeMuted
    val accent = if (dark) Color(0xFFA7C6B5) else Color(0xFF55796B)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(yuhengHeroBrush()),
    ) {
        Box(
            modifier = Modifier
                .size(168.dp)
                .offset(x = (-58).dp, y = (-82).dp)
                .background(accent.copy(alpha = if (dark) 0.12f else 0.16f), CircleShape),
        )
        Box(
            modifier = Modifier
                .size(112.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 34.dp, y = 42.dp)
                .background(accent.copy(alpha = 0.12f), CircleShape),
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
                text = "让 Root 管理，也有东方美学",
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
                text = "五款东方配色  ·  随心切换",
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

    val brush = if (dark) {
        Brush.verticalGradient(listOf(Color(0xFF151C18), Color(0xFF19221D), Color(0xFF151C18)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F3F0), Color(0xFFEBF0EC), Color(0xFFE7EDE8)))
    }
    Box(Modifier.fillMaxSize().background(brush)) {
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
                    if (dark) Color(0xFF101A15).copy(alpha = 0.34f)
                    else Color(0xFFF3F1E9).copy(alpha = 0.28f)
                )
        )
        content()
    }
}

@Composable
fun yuhengCardColor(): Color = if (isInDarkTheme()) {
    Color(0xFF1D2B24)
} else {
    Color(0xFFF8F7F0)
}

@Composable
fun YuhengDeviceHintCard(deviceModel: String, modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
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
                .background(if (dark) Color(0xFFA7C6B5) else Color(0xFF55796B)),
        )
        Spacer(Modifier.width(12.dp))
        Column {
            BasicText(
                text = "设备信息",
                style = TextStyle(
                    color = if (dark) Color(0xFFB5C9BE) else JadeMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Spacer(Modifier.height(4.dp))
            BasicText(
                text = deviceModel,
                style = TextStyle(
                    color = if (dark) JadeText else JadeInk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}
