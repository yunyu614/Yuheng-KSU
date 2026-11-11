package me.weishu.kernelsu.ui.component.bottombar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.LocalMainPagerState
import me.weishu.kernelsu.ui.theme.LocalEnableFloatingBottomBar
import me.weishu.kernelsu.ui.theme.LocalEnableFloatingBottomBarBlur
import me.weishu.kernelsu.ui.component.liquid.vibrancy
import top.yukonga.miuix.kmp.blur.Backdrop
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop

@Composable
fun BottomBarMaterial(
    navigationBadge: NavigationBadgeState,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
) {
    val mainPagerState = LocalMainPagerState.current
    val floating = LocalEnableFloatingBottomBar.current
    val glassEnabled = floating && LocalEnableFloatingBottomBarBlur.current
    val shape = RoundedCornerShape(if (floating) 28.dp else 0.dp)
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val items = listOf(
        Triple(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(R.string.nav_superuser, Icons.Filled.Shield, Icons.Outlined.Shield),
        Triple(R.string.nav_module, Icons.Filled.Extension, Icons.Outlined.Extension),
        Triple(R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (floating) 18.dp else 0.dp, vertical = if (floating) 7.dp else 0.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (glassEnabled) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { shape },
                            effects = {
                                blur(18.dp.toPx(), 18.dp.toPx())
                                vibrancy()
                            },
                            onDrawSurface = {
                                drawRect(surfaceColor.copy(alpha = 0.42f))
                            },
                        )
                    } else {
                        Modifier
                    }
                ),
            shape = shape,
            color = if (glassEnabled) Color.Transparent else surfaceColor.copy(alpha = if (floating) 0.96f else 1f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = if (floating) BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (glassEnabled) 0.58f else 0.38f),
            ) else null,
            shadowElevation = if (floating) 12.dp else 0.dp,
            tonalElevation = if (floating) 3.dp else 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (floating) 76.dp else 80.dp)
                    .padding(horizontal = if (floating) 7.dp else 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, (label, selectedIcon, unselectedIcon) ->
                    val selected = mainPagerState.selectedPage == index
                    NavigationBarItem(
                        modifier = Modifier.weight(1f),
                        selected = selected,
                        onClick = {
                            if (!selected) mainPagerState.animateToPage(index)
                        },
                        icon = {
                            NavigationIconWithBadge(
                                icon = if (selected) selectedIcon else unselectedIcon,
                                contentDescription = stringResource(label),
                                badge = badgeFor(index, navigationBadge),
                            )
                        },
                        label = {
                            Text(
                                stringResource(label),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.78f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
internal fun NavigationIconWithBadge(
    icon: ImageVector,
    contentDescription: String?,
    badge: NavBadge?,
) {
    if (badge != null) {
        BadgedBox(
            badge = {
                when (badge.tone) {
                    BadgeTone.Alert -> Badge { Text(badge.count.toString()) }
                    BadgeTone.Accent -> Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) { Text(badge.count.toString()) }
                }
            }
        ) {
            Icon(icon, contentDescription)
        }
    } else {
        Icon(icon, contentDescription)
    }
}
