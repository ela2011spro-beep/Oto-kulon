package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectedAccounts
import com.example.data.model.InstagramTarget
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LilacPrimaryContainer
import com.example.ui.theme.LilacSecondary
import com.example.ui.theme.YouTubeRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val InstagramGradient = Brush.linearGradient(
    listOf(
        Color(0xFF833AB4),
        Color(0xFFFD1D1D),
        Color(0xFFFCB045)
    )
)

enum class SourceScreenSubTab(val title: String) {
    TARGET_PROFILES("Takip Edilen Profiller"),
    CONNECTED_ACCOUNTS("Bağlı Hesaplar"),
    SYNC_RULES("Otomasyon Kuralları")
}

@Composable
fun InstagramSourceManagementScreen(
    accounts: ConnectedAccounts?,
    targets: List<InstagramTarget>,
    onSaveAccounts: (ConnectedAccounts) -> Unit,
    onAddTarget: (String, String) -> Unit,
    onAddMultipleTargets: (List<Pair<String, String>>) -> Unit,
    onToggleMonitoring: (InstagramTarget) -> Unit,
    onToggleAllTargets: (Boolean) -> Unit,
    onDeleteTarget: (InstagramTarget) -> Unit,
    onScanSingleTarget: (InstagramTarget) -> Unit,
    onScanAllTargets: () -> Unit,
    onTriggerClone: (InstagramTarget?, String?) -> Unit,
    onTestNotification: () -> Unit
) {
    val currentAccounts = accounts ?: ConnectedAccounts()

    var activeSubTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("ALL") } // ALL, ACTIVE, PAUSED

    // Dialog state
    var showFollowingPickerDialog by remember { mutableStateOf(false) }
    var showManualAddDialog by remember { mutableStateOf(false) }
    var showConnectAccountDialog by remember { mutableStateOf(false) }
    var showSimulationDialog by remember { mutableStateOf(false) }
    var simulationTarget by remember { mutableStateOf<InstagramTarget?>(null) }

    val activeCount = targets.count { it.isMonitoring }
    val totalClonedCount = targets.sumOf { it.totalCloned }

    val filteredTargets = remember(targets, searchQuery, filterStatus) {
        targets.filter { target ->
            val matchesQuery = searchQuery.isBlank() ||
                    target.username.contains(searchQuery, ignoreCase = true) ||
                    target.displayName.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (filterStatus) {
                "ACTIVE" -> target.isMonitoring
                "PAUSED" -> !target.isMonitoring
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("screen_instagram_source_management")
    ) {
        // TOP BANNER & TITLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(InstagramGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INSTAGRAM KAYNAK YÖNETİMİ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = LilacPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Hesap bağlama & takip edilen profiller",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                // Global live status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentAccounts.autoSyncActive) Color(0xFF064E3B) else Color(0xFF374151))
                        .border(
                            1.dp,
                            if (currentAccounts.autoSyncActive) Color(0xFF10B981) else Color.Gray,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (currentAccounts.autoSyncActive) Color(0xFF34D399) else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (currentAccounts.autoSyncActive) "DİNLENİYOR" else "DURDURULDU",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentAccounts.autoSyncActive) Color(0xFF6EE7B7) else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SUB TABS
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = Color.Transparent,
                contentColor = LilacPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = LilacPrimary,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                SourceScreenSubTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = activeSubTab == index,
                        onClick = { activeSubTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (activeSubTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (activeSubTab == index) LilacPrimary else Color.White.copy(alpha = 0.6f)
                                )
                                if (index == 0 && targets.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (activeSubTab == 0) LilacPrimary else DarkSurfaceVariant)
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = targets.size.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (activeSubTab == 0) Color(0xFF2E1065) else Color.White
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("source_subtab_$index")
                    )
                }
            }
        }

        // TAB CONTENTS
        when (activeSubTab) {
            0 -> {
                // TAB 1: TAKİP EDİLEN PROFİLLER
                TrackedProfilesTabContent(
                    targets = filteredTargets,
                    totalTargetCount = targets.size,
                    activeCount = activeCount,
                    totalClonedCount = totalClonedCount,
                    searchQuery = searchQuery,
                    filterStatus = filterStatus,
                    onSearchChange = { searchQuery = it },
                    onFilterChange = { filterStatus = it },
                    onOpenFollowingPicker = { showFollowingPickerDialog = true },
                    onOpenManualAdd = { showManualAddDialog = true },
                    onScanAll = onScanAllTargets,
                    onToggleAll = onToggleAllTargets,
                    onToggleMonitoring = onToggleMonitoring,
                    onScanSingle = onScanSingleTarget,
                    onDeleteTarget = onDeleteTarget,
                    onQuickCloneTest = { target ->
                        simulationTarget = target
                        showSimulationDialog = true
                    }
                )
            }
            1 -> {
                // TAB 2: BAĞLI HESAPLAR & OAUTH
                ConnectedAccountsTabContent(
                    accounts = currentAccounts,
                    onSaveAccounts = onSaveAccounts,
                    onOpenConnectDialog = { showConnectAccountDialog = true },
                    onOpenFollowingPicker = { showFollowingPickerDialog = true },
                    onTestNotification = onTestNotification
                )
            }
            2 -> {
                // TAB 3: OTOMASYON & SENKRONİZASYON KURALLARI
                AutomationRulesTabContent(
                    accounts = currentAccounts,
                    targetCount = targets.size,
                    onSaveAccounts = onSaveAccounts,
                    onTestSync = onScanAllTargets
                )
            }
        }
    }

    // MODAL 1: Takip Ettiklerimden Seç (Batch Selection)
    if (showFollowingPickerDialog) {
        val selectedPresets = remember { mutableStateListOf<FollowedAccountPreset>() }
        var extraAccountInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showFollowingPickerDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(InstagramGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Instagram Takip Ettiklerim",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Video kaynağı olarak eklemek istediklerinizi seçin",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Seçilen hesaplar yeni bir video paylaştığında sistem videoyu alıp Türkçe seslendirerek Shorts kanalınıza yükleyecektir:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DEFAULT_FOLLOWED_PRESETS.forEach { preset ->
                        val isChecked = selectedPresets.contains(preset)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isChecked) LilacPrimaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                                .clickable {
                                    if (isChecked) selectedPresets.remove(preset)
                                    else selectedPresets.add(preset)
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
                                    onCheckedChange = { checked ->
                                        if (checked) selectedPresets.add(preset)
                                        else selectedPresets.remove(preset)
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
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = extraAccountInput,
                        onValueChange = { extraAccountInput = it },
                        label = { Text("Listede olmayan başka hesap (@kullanici)") },
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
                        val toAdd = selectedPresets.map { it.username to it.displayName }.toMutableList()
                        if (extraAccountInput.isNotBlank()) {
                            val clean = if (extraAccountInput.startsWith("@")) extraAccountInput.trim() else "@${extraAccountInput.trim()}"
                            toAdd.add(clean to clean)
                        }
                        if (toAdd.isNotEmpty()) {
                            onAddMultipleTargets(toAdd)
                        }
                        showFollowingPickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text(
                        text = "Seçilenleri Ekle (${selectedPresets.size + if (extraAccountInput.isNotBlank()) 1 else 0})",
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

    // MODAL 2: Manuel Profil Ekle
    if (showManualAddDialog) {
        var username by remember { mutableStateOf("") }
        var displayName by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Genel Bilgi") }

        val categoryOptions = listOf("Genel Bilgi", "Bilim & Evrim", "Tarih", "Teknoloji", "Gizem & Olaylar", "Psikoloji")

        AlertDialog(
            onDismissRequest = { showManualAddDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = LilacPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Yeni Profil Kaynağı Ekle",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Instagram'da takip ettiğiniz herhangi bir profilin kullanıcı adını girin:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Kullanıcı Adı (@hesap)") },
                        placeholder = { Text("@ornekhesap") },
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
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Hesap Tanımı / İsim") },
                        placeholder = { Text("Örn: İlginç Olaylar") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacPrimary,
                            unfocusedBorderColor = Color(0xFF43355C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kategori:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categoryOptions) { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LilacPrimaryContainer,
                                    selectedLabelColor = LilacPrimary,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            val cleanUser = if (username.startsWith("@")) username.trim() else "@${username.trim()}"
                            val cleanName = displayName.trim().ifBlank { cleanUser }
                            onAddTarget(cleanUser, "$cleanName ($selectedCategory)")
                            showManualAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text("Kaydet & Dinle", color = Color(0xFF2E1065), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualAddDialog = false }) {
                    Text("İptal", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }

    // MODAL 3: Instagram Hesabı Bağla / Değiştir
    if (showConnectAccountDialog) {
        var accountHandleInput by remember { mutableStateOf(currentAccounts.instagramUsername) }
        var sessionTokenInput by remember { mutableStateOf("IG_TOKEN_OAUTH_ACTIVE_7721") }

        AlertDialog(
            onDismissRequest = { showConnectAccountDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(InstagramGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Instagram Hesabı Bağla",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Kendi Instagram hesabınızı bağlayarak takip ettiğiniz tüm profillerin otomatik taranmasını sağlayın:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = accountHandleInput,
                        onValueChange = { accountHandleInput = it },
                        label = { Text("Instagram Kullanıcı Adınız") },
                        placeholder = { Text("@kullaniciadiniz") },
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
                        value = sessionTokenInput,
                        onValueChange = { sessionTokenInput = it },
                        label = { Text("Yetkilendirme Jetonu (OAuth Token)") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = LilacPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LilacPrimary,
                            unfocusedBorderColor = Color(0xFF43355C),
                            focusedTextColor = Color.LightGray,
                            unfocusedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "✓ Otomatik takipçi & takip edilenler senkronizasyonu aktif edilecek.",
                        fontSize = 11.sp,
                        color = Color(0xFF34D399)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = if (accountHandleInput.startsWith("@")) accountHandleInput.trim() else "@${accountHandleInput.trim()}"
                        onSaveAccounts(
                            currentAccounts.copy(
                                instagramConnected = true,
                                instagramUsername = clean
                            )
                        )
                        showConnectAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text("Bağla & Aktifleştir", color = Color(0xFF2E1065), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConnectAccountDialog = false }) {
                    Text("İptal", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }

    // MODAL 4: Hızlı Klonlama Simülasyonu
    if (showSimulationDialog && simulationTarget != null) {
        val target = simulationTarget!!
        var customVideoTitle by remember { mutableStateOf("${target.displayName} son paylaşılan Reels videosu") }

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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Yeni Video Testi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "${target.username} hesabının şu an yeni bir video paylaştığını simüle edin. Sistem videoyu anında yakalayacak ve yapım kutusunda 5 adımlı klonlama sürecini başlatacaktır.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customVideoTitle,
                        onValueChange = { customVideoTitle = it },
                        label = { Text("Video Başlığı / Konusu") },
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
                        onTriggerClone(target, customVideoTitle)
                        showSimulationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary)
                ) {
                    Text("Klonlamayı Başlat", color = Color(0xFF2E1065), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulationDialog = false }) {
                    Text("İptal", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: TAKİP EDİLEN PROFİLLER (LİSTE & YÖNETİM)
// -------------------------------------------------------------
@Composable
private fun TrackedProfilesTabContent(
    targets: List<InstagramTarget>,
    totalTargetCount: Int,
    activeCount: Int,
    totalClonedCount: Int,
    searchQuery: String,
    filterStatus: String,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onOpenFollowingPicker: () -> Unit,
    onOpenManualAdd: () -> Unit,
    onScanAll: () -> Unit,
    onToggleAll: (Boolean) -> Unit,
    onToggleMonitoring: (InstagramTarget) -> Unit,
    onScanSingle: (InstagramTarget) -> Unit,
    onDeleteTarget: (InstagramTarget) -> Unit,
    onQuickCloneTest: (InstagramTarget) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("tracked_profiles_list")
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            // STATS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Toplam Profil",
                    value = totalTargetCount.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = LilacPrimary
                )
                StatCard(
                    title = "Dinleniyor",
                    value = activeCount.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFF34D399)
                )
                StatCard(
                    title = "Klon Shorts",
                    value = totalClonedCount.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = YouTubeRed
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // QUICK ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenFollowingPicker,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LilacPrimary,
                        contentColor = Color(0xFF2E1065)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("btn_pick_following")
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
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
                    onClick = onOpenManualAdd,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LilacPrimary),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(44.dp)
                        .testTag("btn_manual_add")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
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

            Spacer(modifier = Modifier.height(12.dp))

            // SEARCH & QUICK FILTER BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Profil ara (@kullanici / isim)", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Temizle",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LilacPrimary,
                        unfocusedBorderColor = Color(0xFF43355C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("search_target_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Scan all button
                IconButton(
                    onClick = onScanAll,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LilacPrimaryContainer)
                        .testTag("btn_scan_all")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Tümünü Tara",
                        tint = LilacPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // FILTER CHIPS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = filterStatus == "ALL",
                    onClick = { onFilterChange("ALL") },
                    label = { Text("Tümü (${totalTargetCount})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LilacPrimaryContainer,
                        selectedLabelColor = LilacPrimary,
                        containerColor = DarkSurfaceVariant,
                        labelColor = Color.White
                    )
                )

                FilterChip(
                    selected = filterStatus == "ACTIVE",
                    onClick = { onFilterChange("ACTIVE") },
                    label = { Text("Aktif (${activeCount})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF065F46),
                        selectedLabelColor = Color(0xFF6EE7B7),
                        containerColor = DarkSurfaceVariant,
                        labelColor = Color.White
                    )
                )

                FilterChip(
                    selected = filterStatus == "PAUSED",
                    onClick = { onFilterChange("PAUSED") },
                    label = { Text("Duraklatılan (${totalTargetCount - activeCount})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DarkSurfaceVariant,
                        selectedLabelColor = Color.LightGray,
                        containerColor = DarkSurfaceVariant,
                        labelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION TITLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DİNLENEN HESAP LİSTESİ (${targets.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LilacPrimary,
                    letterSpacing = 1.sp
                )

                Row {
                    TextButton(onClick = { onToggleAll(true) }) {
                        Text("Tümünü Aç", fontSize = 11.sp, color = Color(0xFF34D399))
                    }
                    TextButton(onClick = { onToggleAll(false) }) {
                        Text("Durdur", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }

        // TARGET ITEMS
        if (targets.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .border(1.dp, Color(0xFF43355C), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Aramaya uygun profil bulunamadı" else "Henüz dinlenen bir profil yok",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yukarıdaki 'Takip Ettiklerimden Seç' butonuna dokunarak hesap ekleyin.",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        } else {
            items(targets, key = { it.id }) { target ->
                TargetProfileCard(
                    target = target,
                    onToggleMonitoring = { onToggleMonitoring(target) },
                    onScanSingle = { onScanSingle(target) },
                    onDelete = { onDeleteTarget(target) },
                    onQuickCloneTest = { onQuickCloneTest(target) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun TargetProfileCard(
    target: InstagramTarget,
    onToggleMonitoring: () -> Unit,
    onScanSingle: () -> Unit,
    onDelete: () -> Unit,
    onQuickCloneTest: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val lastCheckTime = remember(target.lastCheckedTimestamp) {
        dateFormat.format(Date(target.lastCheckedTimestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(
                1.dp,
                if (target.isMonitoring) Color(0xFF4C1D95) else Color(0xFF332A44),
                RoundedCornerShape(14.dp)
            )
            .testTag("target_card_${target.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar, Username, Live Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar ring
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(InstagramGradient)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = target.username.drop(1).take(2).uppercase().ifBlank { "IG" },
                                color = LilacPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = target.username,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (target.isMonitoring) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF065F46))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "CANLI DİNLENİYOR",
                                        color = Color(0xFF6EE7B7),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF374151))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "DURAKLATILDI",
                                        color = Color.LightGray,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = target.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.65f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Switch(
                    checked = target.isMonitoring,
                    onCheckedChange = { onToggleMonitoring() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LilacPrimary,
                        checkedTrackColor = LilacPrimaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info bar: Last detected video & stats
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceVariant)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Son Durum: ${target.lastVideoDetected ?: "Yeni video taranıyor"}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Son Kontrol: $lastCheckTime • Klonlanan: ${target.totalCloned} Shorts",
                            fontSize = 10.sp,
                            color = LilacPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Clone Test
                Button(
                    onClick = onQuickCloneTest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LilacPrimaryContainer,
                        contentColor = LilacPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Video Testi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Scan Single Target
                OutlinedButton(
                    onClick = onScanSingle,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF43355C)),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tara",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: BAĞLI HESAPLAR & OAUTH YÖNETİMİ
// -------------------------------------------------------------
@Composable
private fun ConnectedAccountsTabContent(
    accounts: ConnectedAccounts,
    onSaveAccounts: (ConnectedAccounts) -> Unit,
    onOpenConnectDialog: () -> Unit,
    onOpenFollowingPicker: () -> Unit,
    onTestNotification: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("connected_accounts_tab_content")
    ) {
        item {
            // Instagram Primary Connected Account Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF833AB4).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(InstagramGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "BAĞLI INSTAGRAM HESABINIZ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LilacPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = accounts.instagramUsername,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (accounts.instagramConnected) Color(0xFF065F46) else Color(0xFF991B1B))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (accounts.instagramConnected) "BAĞLI & AKTİF" else "BAĞLI DEĞİL",
                                color = if (accounts.instagramConnected) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Permissions list
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Yetkilendirilen API İzinleri:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PermissionRow(title = "Reels & Video Akışı Okuma", granted = true)
                        PermissionRow(title = "Takip Edilen Profiller Listesi", granted = true)
                        PermissionRow(title = "Yeni Video Webhook Bildirimleri", granted = accounts.autoSyncActive)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenFollowingPicker,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LilacPrimary,
                                contentColor = Color(0xFF2E1065)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Takip Listemi Çek", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onOpenConnectDialog,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF43355C)),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Hesabı Değiştir", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // YouTube Shorts Target Channel Card
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(YouTubeRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "HEDEF YOUTUBE SHORTS KANALI",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = YouTubeRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = accounts.youtubeChannel,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF065F46))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "YÜKLEMEYE HAZIR",
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Instagram kaynaklarından çekilen ve Türkçeleştirilen videolar doğrudan bu kanala Shorts olarak yüklenir.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security & Privacy Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = LilacPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Gizlilik Güvencesi: Şifreniz asla üçüncü taraf sunucularda saklanmaz. Yalnızca seçtiğiniz profillerin yeni Reels ve videoları dinlenir.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTestNotification,
                colors = ButtonDefaults.buttonColors(containerColor = LilacPrimaryContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = LilacPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bağlantı ve Bildirim Testi Yap",
                    color = LilacPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: OTOMASYON & SENKRONİZASYON KURALLARI
// -------------------------------------------------------------
@Composable
private fun AutomationRulesTabContent(
    accounts: ConnectedAccounts,
    targetCount: Int,
    onSaveAccounts: (ConnectedAccounts) -> Unit,
    onTestSync: () -> Unit
) {
    var autoSync by remember(accounts) { mutableStateOf(accounts.autoSyncActive) }
    var pollInterval by remember(accounts) { mutableIntStateOf(accounts.pollIntervalMinutes) }
    var privacy by remember(accounts) { mutableStateOf(accounts.uploadPrivacy) }

    fun commit(sync: Boolean = autoSync, interval: Int = pollInterval, priv: String = privacy) {
        onSaveAccounts(
            accounts.copy(
                autoSyncActive = sync,
                pollIntervalMinutes = interval,
                uploadPrivacy = priv
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("automation_rules_tab_content")
    ) {
        item {
            // Master Switch
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (autoSync) Color(0xFF10B981) else Color(0xFF43355C),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "7/24 Arka Planda Canlı Dinleme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Takip ettiğiniz profiller yeni video attığında otomatik klonlar",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Switch(
                            checked = autoSync,
                            onCheckedChange = {
                                autoSync = it
                                commit(sync = it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF34D399),
                                checkedTrackColor = Color(0xFF065F46)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Frequency Setting
            Text(
                text = "TARAMA SIKLIĞI",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = LilacPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 5, 15, 30).forEach { minutes ->
                    val isSelected = pollInterval == minutes
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            pollInterval = minutes
                            commit(interval = minutes)
                        },
                        label = {
                            Text(
                                text = "Her $minutes dk",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LilacPrimary,
                            selectedLabelColor = Color(0xFF2E1065),
                            containerColor = DarkSurfaceVariant,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // YouTube Upload Privacy
            Text(
                text = "YOUTUBE SHORTS GİZLİLİK AYARI",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = LilacPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "PUBLIC" to "Herkese Açık",
                    "UNLISTED" to "Liste Dışı",
                    "PRIVATE" to "Gizli"
                ).forEach { (key, label) ->
                    val isSelected = privacy == key
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            privacy = key
                            commit(priv = key)
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LilacPrimary,
                            selectedLabelColor = Color(0xFF2E1065),
                            containerColor = DarkSurfaceVariant,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trigger Manual Scan
            Button(
                onClick = onTestSync,
                colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = Color(0xFF2E1065),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$targetCount Hesabı Şimdi Senkronize Et",
                    color = Color(0xFF2E1065),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLES
// -------------------------------------------------------------
@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, Color(0xFF3B2E52), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PermissionRow(title: String, granted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Close,
            contentDescription = null,
            tint = if (granted) Color(0xFF34D399) else Color.Gray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = if (granted) Color.White.copy(alpha = 0.9f) else Color.Gray
        )
    }
}
