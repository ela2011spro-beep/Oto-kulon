package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LilacPrimaryContainer
import com.example.ui.theme.LilacSecondary
import com.example.ui.theme.YouTubeRed

enum class OtoKlonTab(
    val title: String,
    val shortTitle: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val testTag: String
) {
    INSTAGRAM_SOURCES(
        title = "Instagram Kaynak Yönetimi",
        shortTitle = "Kaynaklar",
        filledIcon = Icons.Filled.Hub,
        outlinedIcon = Icons.Outlined.Hub,
        testTag = "nav_tab_instagram_sources"
    ),
    WHO_AM_I(
        title = "Ben Kimim",
        shortTitle = "Ben Kimim",
        filledIcon = Icons.Filled.Person,
        outlinedIcon = Icons.Outlined.Person,
        testTag = "nav_tab_whoami"
    ),
    PRODUCTION_BOX(
        title = "Yapım Kutusu",
        shortTitle = "Yapım",
        filledIcon = Icons.Filled.VideoLibrary,
        outlinedIcon = Icons.Outlined.VideoLibrary,
        testTag = "nav_tab_production"
    ),
    NOTIFICATIONS(
        title = "Bildirimler",
        shortTitle = "Bildirim",
        filledIcon = Icons.Filled.Notifications,
        outlinedIcon = Icons.Outlined.Notifications,
        testTag = "nav_tab_notifications"
    ),
    CHANNEL_ANALYTICS(
        title = "Kanal Analizi",
        shortTitle = "Analiz",
        filledIcon = Icons.Filled.Analytics,
        outlinedIcon = Icons.Outlined.Analytics,
        testTag = "nav_tab_analytics"
    ),
    CHANNEL_STATUS(
        title = "Kanal Durumu",
        shortTitle = "Kanal",
        filledIcon = Icons.Filled.Search,
        outlinedIcon = Icons.Outlined.Search,
        testTag = "nav_tab_channel_status"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtoKlonTopBar(
    currentTab: OtoKlonTab,
    isSyncActive: Boolean,
    onLockClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = Color.White
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LilacPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.ExtraBold,
                        color = LilacPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "OTO KLON",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = LilacPrimary
                )
            }
        },
        actions = {
            // Live automation badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, Color(0xFF43355C), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isSyncActive) Color(0xFF4ADE80) else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isSyncActive) "OTO AKTİF" else "DURDURULDU",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSyncActive) Color(0xFF4ADE80) else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Privacy Lock button ("Sadece ben girebiliyorum")
            IconButton(
                onClick = onLockClick,
                modifier = Modifier.testTag("top_bar_lock_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Kilitle (Gizlilik)",
                    tint = LilacPrimary
                )
            }
        }
    )
}

@Composable
fun OtoKlonBottomNav(
    selectedTab: OtoKlonTab,
    unreadNotificationsCount: Int,
    onTabSelected: (OtoKlonTab) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        OtoKlonTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == OtoKlonTab.NOTIFICATIONS && unreadNotificationsCount > 0) {
                        BadgedBox(badge = {
                            Badge(containerColor = YouTubeRed) {
                                Text(
                                    text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }) {
                            Icon(
                                imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                                contentDescription = tab.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                            contentDescription = tab.title
                        )
                    }
                },
                label = {
                    Text(
                        text = tab.shortTitle,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2E1065),
                    selectedTextColor = LilacPrimary,
                    indicatorColor = LilacPrimary,
                    unselectedIconColor = LilacSecondary.copy(alpha = 0.6f),
                    unselectedTextColor = LilacSecondary.copy(alpha = 0.6f)
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
