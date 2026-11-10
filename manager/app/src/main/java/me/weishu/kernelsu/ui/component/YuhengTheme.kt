package me.weishu.kernelsu.ui.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.ui.theme.isInDarkTheme

private val JadeInk = Color(0xFF29463C)
private val JadeText = Color(0xFFE4EEE8)
private val JadeMuted = Color(0xFF647B70)

@Composable
fun yuhengHeroBrush(): Brush = if (isInDarkTheme()) {
    Brush.linearGradient(listOf(Color(0xFF171F1D), Color(0xFF263A32), Color(0xFF30483D)))
} else {
    Brush.linearGradient(listOf(Color(0xFFE8EFEB), Color(0xFFD5E3DC), Color(0xFFC5D8CE)))
}

@Composable
fun Modifier.yuhengCardGradient(shape: Shape = RoundedCornerShape(18.dp)): Modifier =
    clip(shape).background(yuhengHeroBrush())

@Composable
fun YuhengHeroCard(modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
    val title = if (dark) JadeText else JadeInk
    val muted = if (dark) Color(0xFFB5C9BE) else JadeMuted
    val accent = if (dark) Color(0xFFA7C6B5) else Color(0xFF55796B)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(184.dp)
            .clip(RoundedCornerShape(24.dp))
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
                .padding(horizontal = 22.dp, vertical = 20.dp),
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
            Spacer(Modifier.height(12.dp))
            BasicText(
                text = "让 Root 管理，也有东方美学",
                style = TextStyle(
                    color = title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                ),
            )
            Spacer(Modifier.height(7.dp))
            BasicText(
                text = "以玉为度，简而有序",
                style = TextStyle(
                    color = muted,
                    fontSize = 13.sp,
                    letterSpacing = 0.4.sp,
                ),
            )
            Spacer(Modifier.height(13.dp))
            BasicText(
                text = "青瓷雾  ·  墨玉",
                style = TextStyle(
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                ),
            )
        }
    }
}

@Composable
fun YuhengAppBackground(content: @Composable () -> Unit) {
    val dark = isInDarkTheme()
    val brush = if (dark) {
        Brush.verticalGradient(listOf(Color(0xFF171F1D), Color(0xFF1B2723), Color(0xFF171F1D)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE8EFEB), Color(0xFFE2EBE6), Color(0xFFDCE7E1)))
    }
    Box(Modifier.fillMaxSize().background(brush)) {
        content()
    }
}

@Composable
fun yuhengCardColor(): Color = if (isInDarkTheme()) {
    Color(0xFF24312D)
} else {
    Color(0xFFDDE8E1)
}

@Composable
fun YuhengDeviceHintCard(deviceModel: String, modifier: Modifier = Modifier) {
    val dark = isInDarkTheme()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(yuhengCardColor())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(40.dp)
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
