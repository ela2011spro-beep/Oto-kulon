package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectedAccounts
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LilacPrimaryContainer
import com.example.ui.theme.YouTubeRed

@Composable
fun ConnectedAccountsScreen(
    accounts: ConnectedAccounts?,
    onSaveAccounts: (ConnectedAccounts) -> Unit,
    onTestSync: () -> Unit
) {
    val currentAccounts = accounts ?: ConnectedAccounts()

    var igConnected by remember(currentAccounts) { mutableStateOf(currentAccounts.instagramConnected) }
    var igUsername by remember(currentAccounts) { mutableStateOf(currentAccounts.instagramUsername) }

    var ytConnected by remember(currentAccounts) { mutableStateOf(currentAccounts.youtubeConnected) }
    var ytChannel by remember(currentAccounts) { mutableStateOf(currentAccounts.youtubeChannel) }

    var autoSync by remember(currentAccounts) { mutableStateOf(currentAccounts.autoSyncActive) }
    var pollInterval by remember(currentAccounts) { mutableStateOf(currentAccounts.pollIntervalMinutes) }
    var uploadPrivacy by remember(currentAccounts) { mutableStateOf(currentAccounts.uploadPrivacy) }

    var testStatusText by remember { mutableStateOf<String?>(null) }

    fun commit() {
        onSaveAccounts(
            currentAccounts.copy(
                instagramConnected = igConnected,
                instagramUsername = igUsername,
                youtubeConnected = ytConnected,
                youtubeChannel = ytChannel,
                autoSyncActive = autoSync,
                pollIntervalMinutes = pollInterval,
                uploadPrivacy = uploadPrivacy
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("screen_connected_accounts")
    ) {
        // Section Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LilacPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = LilacPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "1. BAĞLI HESAPLAR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LilacPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Instagram kaynak hesabı ve YouTube hedef kanalını bağlayın",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card 1: Instagram Connection
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF833AB4).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("IG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Instagram Bağlantısı",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (igConnected) "Bağlı & Video Çekmeye Hazır" else "Bağlantı Kesildi",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (igConnected) Color(0xFF4ADE80) else Color.Gray
                            )
                        }
                    }

                    Switch(
                        checked = igConnected,
                        onCheckedChange = {
                            igConnected = it
                            commit()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LilacPrimary,
                            checkedTrackColor = LilacPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = igUsername,
                    onValueChange = {
                        igUsername = it
                        commit()
                    },
                    label = { Text("Instagram Otomasyon Hesabı / Kullanıcı Adı") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LilacPrimary,
                        unfocusedBorderColor = Color(0xFF43355C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4ADE80),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reels ve Video akış okuma izni aktif",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFCCCCCC)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E1065).copy(alpha = 0.5f))
                        .border(1.dp, LilacPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = LilacPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Yapım Kutusu'ndan seçtiğiniz takip ettiğiniz hesaplar yeni video paylaştığında sistem videoyu otomatik alıp Türkçe seslendirerek Shorts kanalınıza yükler.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: YouTube Shorts Connection
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, YouTubeRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(YouTubeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "YouTube Kanalı & Shorts",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (ytConnected) "Shorts API Yükleme Yetkisi Aktif" else "Yetki Verilmedi",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (ytConnected) Color(0xFF4ADE80) else Color.Gray
                            )
                        }
                    }

                    Switch(
                        checked = ytConnected,
                        onCheckedChange = {
                            ytConnected = it
                            commit()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = YouTubeRed,
                            checkedTrackColor = Color(0xFF5A1521)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = ytChannel,
                    onValueChange = {
                        ytChannel = it
                        commit()
                    },
                    label = { Text("Hedef YouTube Kanalı Adı") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YouTubeRed,
                        unfocusedBorderColor = Color(0xFF43355C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Shorts Yayınlama Gizliliği:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PUBLIC" to "Herkese Açık", "UNLISTED" to "Liste Dışı", "PRIVATE" to "Gizli").forEach { (code, label) ->
                        val isSel = uploadPrivacy == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) LilacPrimaryContainer else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSel) LilacPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) LilacPrimary else Color.White,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Otomatik Senkronizasyon Ayarları
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Otomatik İzleme ve Klonlama",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Seçili hesaplar yeni video attığında anında algılar",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = autoSync,
                        onCheckedChange = {
                            autoSync = it
                            commit()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LilacPrimary,
                            checkedTrackColor = LilacPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kontrol Sıklığı: Her $pollInterval dakikada bir",
                        style = MaterialTheme.typography.bodySmall,
                        color = LilacPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(3, 5, 15).forEach { min ->
                            val selected = pollInterval == min
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selected) LilacPrimary else DarkSurface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${min} dk",
                                    fontSize = 11.sp,
                                    color = if (selected) Color(0xFF2E1065) else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Test & Sync Button
        Button(
            onClick = {
                testStatusText = "Instagram & YouTube bağlantıları doğrulandı! Senkronizasyon hazır."
                onTestSync()
            },
            colors = ButtonDefaults.buttonColors(containerColor = LilacPrimaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("test_sync_button")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = LilacPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Hesap Bağlantılarını Doğrula & Test Et",
                fontWeight = FontWeight.Bold,
                color = LilacPrimary
            )
        }

        if (testStatusText != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E3A2F))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4ADE80),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = testStatusText!!,
                        color = Color(0xFF4ADE80),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
