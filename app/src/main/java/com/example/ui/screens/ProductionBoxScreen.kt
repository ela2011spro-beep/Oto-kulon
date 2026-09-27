package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloneTask
import com.example.data.model.InstagramTarget
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LilacPrimaryContainer
import com.example.ui.theme.YouTubeRed
import com.example.ui.viewmodel.PipelineState

data class FollowedAccountPreset(
    val username: String,
    val displayName: String,
    val category: String,
    val followerCount: String
)

val DEFAULT_FOLLOWED_PRESETS = listOf(
    FollowedAccountPreset("@tarih.arsivi", "Tarihin Gizemleri", "Tarih & Belgesel", "1.2M"),
    FollowedAccountPreset("@bilimveotesi", "Bilim ve Ötesi", "Bilim & Teknoloji", "840K"),
    FollowedAccountPreset("@ilgincbilgiler", "Günün İlginç Bilgisi", "Genel Kültür & Merak", "2.1M"),
    FollowedAccountPreset("@evrimagaci", "Evrim Ağacı", "Bilim & Evrim", "1.5M"),
    FollowedAccountPreset("@uzayvedunya", "Uzay ve Evren", "Astronomi & Uzay", "650K"),
    FollowedAccountPreset("@ruhicenet", "Ruhi Çenet", "Olaylar & Belgesel", "3.2M"),
    FollowedAccountPreset("@psikolojitr", "Psikoloji Gerçekleri", "Zihin & Davranış", "720K"),
    FollowedAccountPreset("@kisa.bilgiler", "Kısa Bilgiler", "Hızlı Bilgi & Shorts", "980K")
)

@Composable
fun ProductionBoxScreen(
    targets: List<InstagramTarget>,
    cloneTasks: List<CloneTask>,
    pipelineState: PipelineState,
    autoSyncActive: Boolean = true,
    pollIntervalMinutes: Int = 5,
    onAddTarget: (String, String) -> Unit,
    onAddMultipleTargets: (List<Pair<String, String>>) -> Unit = {},
    onToggleMonitoring: (InstagramTarget) -> Unit,
    onDeleteTarget: (InstagramTarget) -> Unit,
    onTriggerClone: (InstagramTarget?, String?) -> Unit
) {
    var showManualAddDialog by remember { mutableStateOf(false) }
    var showFollowingPickerDialog by remember { mutableStateOf(false) }
    var showSimulationDialog by remember { mutableStateOf(false) }
    var simulationSelectedTarget by remember { mutableStateOf<InstagramTarget?>(null) }
    var simulationVideoTopic by remember { mutableStateOf("") }

    var newUsername by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "rotate"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("screen_production_box")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(LilacPrimary, Color(0xFF6B21A8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "3. YAPIM KUTUSU",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = LilacPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Takip ettiklerinizden otomatik video çekme",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two primary action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showFollowingPickerDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LilacPrimary,
                        contentColor = Color(0xFF2E1065)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("pick_from_following_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Takip Ettiklerimden Seç",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Takip Ettiklerimden Seç",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = { showManualAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LilacPrimary),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(44.dp)
                        .testTag("add_custom_target_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Hesap Ekle",
                        tint = LilacPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Manuel Ekle",
                        color = LilacPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // REASSURANCE / HOW IT WORKS CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF43355C), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (autoSyncActive) Color(0xFF4ADE80) else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (autoSyncActive) "7/24 OTOMATİK DİNLEME AKTİF" else "OTOMATİK DİNLEME DURAKLATILDI",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (autoSyncActive) Color(0xFF4ADE80) else Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LilacPrimaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Her $pollIntervalMinutes dk kontrol",
                                fontSize = 10.sp,
                                color = LilacPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "⚡ Evet! Sistem tam olarak şöyle çalışır:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "1. Seçtiğiniz Instagram takip ettikleriniz arka planda sürekli dinlenir.\n" +
                                "2. Bu hesaplardan biri YENİ BİR VİDEO paylaştığı an sistem anında algılar ve videoyu çeker.\n" +
                                "3. Sizin için üst ve alta siyah şerit çeker, 'Ben Kimim'deki kanal tarzınızla Türkçe yapay zeka sesiyle seslendirir.\n" +
                                "4. Başlık ve altyazıyı ekleyip YouTube Shorts'a otomatik yükler ve telefonunuza bildirim gönderir.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Simulation Trigger Button
                    Button(
                        onClick = {
                            simulationSelectedTarget = targets.firstOrNull()
                            simulationVideoTopic = "Günün En Çok Konuşulan Gizemi"
                            showSimulationDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4C1D95),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("simulate_new_video_button"),
                        enabled = !pipelineState.isRunning && targets.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚡ Yeni Video Paylaşımını Şimdi Canlı Test Et",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Pipeline Processing Card (if running or step > 0)
            if (pipelineState.isRunning || pipelineState.currentStep > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            if (pipelineState.isRunning) LilacPrimary else Color(0xFF4ADE80),
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("active_pipeline_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (pipelineState.isRunning) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = LilacPrimary,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .rotate(rotationAngle)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pipelineState.currentStepName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pipelineState.isRunning) LilacPrimary else Color(0xFF4ADE80)
                                )
                            }

                            Text(
                                text = "Adım ${pipelineState.currentStep}/5",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { pipelineState.currentStep / 5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LilacPrimary,
                            trackColor = Color(0xFF32234A)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = pipelineState.progressMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        if (pipelineState.generatedHeadline.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Şerit: ${pipelineState.generatedHeadline}",
                                    color = Color(0xFFFFEB3B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section 1: Tracked Instagram Accounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DİNLENEN HESAPLARINIZ (${targets.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LilacPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Yeni video attıklarında otomatik klonlanır",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // List of Targets
        items(targets) { target ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, Color(0xFF3B2E52), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(LilacPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = target.username.drop(1).take(2).uppercase().ifBlank { "IG" },
                                    color = LilacPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = target.username,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (target.isMonitoring) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF065F46))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "DİNLENİYOR",
                                                color = Color(0xFF6EE7B7),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = target.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "Üretilen Shorts: ${target.totalCloned} adet",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LilacPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Single-target trigger clone test
                            IconButton(
                                onClick = {
                                    onTriggerClone(target, "${target.displayName} yeni video paylaşımı")
                                },
                                enabled = !pipelineState.isRunning,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(LilacPrimaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Yeni Video Testi",
                                    tint = LilacPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Switch(
                                checked = target.isMonitoring,
                                onCheckedChange = { onToggleMonitoring(target) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = LilacPrimary,
                                    checkedTrackColor = LilacPrimaryContainer
                                )
                            )

                            IconButton(
                                onClick = { onDeleteTarget(target) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Sil",
                                    tint = Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Recent Cloned Shorts History
            Text(
                text = "TAMAMLANAN KLONLAR (${cloneTasks.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = LilacPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(cloneTasks) { task ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(YouTubeRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SHORTS",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = task.sourceUsername,
                                style = MaterialTheme.typography.labelSmall,
                                color = LilacPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${task.estimatedViews} izlenme",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = task.youtubeTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Black strip preview box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Şerit: ${task.stripHeadline}",
                            color = Color(0xFFFFEB3B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "AI Ses: ${task.voiceTone}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )

                        Text(
                            text = "✓ YouTube'a Yüklendi",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4ADE80),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal 1: Followed Accounts Picker Dialog
    if (showFollowingPickerDialog) {
        val selectedFollowed = remember { mutableStateListOf<FollowedAccountPreset>() }
        var customAddUsername by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showFollowingPickerDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = LilacPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Instagram Takip Ettiklerimden Seç",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Aşağıdaki takip ettiğiniz hesaplardan istediklerinizi seçin. Bu hesaplar yeni video attığı an sistem videoyu alıp sizin için yapacaktır:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset accounts list
                    DEFAULT_FOLLOWED_PRESETS.forEach { preset ->
                        val isChecked = selectedFollowed.contains(preset)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isChecked) selectedFollowed.remove(preset)
                                    else selectedFollowed.add(preset)
                                }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { check ->
                                        if (check) selectedFollowed.add(preset)
                                        else selectedFollowed.remove(preset)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = LilacPrimary,
                                        checkmarkColor = Color(0xFF2E1065)
                                    )
                                )
                                Column {
                                    Text(
                                        text = preset.username,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${preset.displayName} • ${preset.category}",
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Text(
                                text = preset.followerCount,
                                fontSize = 11.sp,
                                color = LilacPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom add inside picker
                    OutlinedTextField(
                        value = customAddUsername,
                        onValueChange = { customAddUsername = it },
                        label = { Text("Listede olmayan başka takip ettiğiniz hesap") },
                        placeholder = { Text("@kullaniciadi") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacPrimary,
                            unfocusedBorderColor = Color(0xFF43355C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetsToAdd = selectedFollowed.map { it.username to it.displayName }.toMutableList()
                        if (customAddUsername.isNotBlank()) {
                            val cleanUser = if (customAddUsername.startsWith("@")) customAddUsername.trim() else "@${customAddUsername.trim()}"
                            targetsToAdd.add(cleanUser to cleanUser)
                        }

                        if (targetsToAdd.isNotEmpty()) {
                            onAddMultipleTargets(targetsToAdd)
                        }
                        showFollowingPickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text(
                        text = "Seçilenleri Takibe Al (${selectedFollowed.size + if (customAddUsername.isNotBlank()) 1 else 0})",
                        color = Color(0xFF2E1065),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showFollowingPickerDialog = false }) {
                    Text("Kapat", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }

    // Modal 2: Simulation / Test Dialog
    if (showSimulationDialog) {
        AlertDialog(
            onDismissRequest = { showSimulationDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Yeni Video Paylaşımını Test Et",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Seçtiğiniz takip ettiğiniz bir hesap yeni bir video paylaştığında sistemin nasıl çalıştığını hemen deneyin:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Hangi hesap yeni video paylaştı?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    targets.take(4).forEach { t ->
                        val isSelected = simulationSelectedTarget?.id == t.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LilacPrimaryContainer else DarkSurfaceVariant)
                                .clickable { simulationSelectedTarget = t }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${t.username} (${t.displayName})",
                                color = if (isSelected) LilacPrimary else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = simulationVideoTopic,
                        onValueChange = { simulationVideoTopic = it },
                        label = { Text("Yeni Video Konusu / Başlığı") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacPrimary,
                            unfocusedBorderColor = Color(0xFF43355C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = simulationSelectedTarget ?: targets.firstOrNull()
                        onTriggerClone(target, simulationVideoTopic.ifBlank { "Yeni Keşif Görüntüleri" })
                        showSimulationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text(
                        text = "Otomatik Klonlamayı Başlat",
                        color = Color(0xFF2E1065),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulationDialog = false }) {
                    Text("İptal", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }

    // Modal 3: Add Target Account Dialog
    if (showManualAddDialog) {
        AlertDialog(
            onDismissRequest = { showManualAddDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Yeni Instagram Hesabı Ekle",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Bu hesap yeni Reels veya video attığında uygulama otomatik algılayacak ve klonlayacaktır.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("Kullanıcı Adı (@hesap)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacPrimary,
                            unfocusedBorderColor = Color(0xFF43355C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        label = { Text("Hesap Tanımı / Konsept") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacPrimary,
                            unfocusedBorderColor = Color(0xFF43355C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUsername.isNotBlank()) {
                            val clean = if (newUsername.startsWith("@")) newUsername.trim() else "@${newUsername.trim()}"
                            onAddTarget(clean, newDisplayName.trim().ifBlank { clean })
                            newUsername = ""
                            newDisplayName = ""
                            showManualAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text("Takibe Al", color = Color(0xFF2E1065), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualAddDialog = false }) {
                    Text("İptal", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}
