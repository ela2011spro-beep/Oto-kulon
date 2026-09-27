package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.OtoKlonBottomNav
import com.example.ui.components.OtoKlonTab
import com.example.ui.components.OtoKlonTopBar
import com.example.ui.screens.ChannelAnalyticsScreen
import com.example.ui.screens.ChannelStatusScreen
import com.example.ui.screens.ConnectedAccountsScreen
import com.example.ui.screens.InstagramSourceManagementScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProductionBoxScreen
import com.example.ui.screens.WhoAmIScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.OtoKlonViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: OtoKlonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Request Notification permission on Android 13+
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                OtoKlonApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OtoKlonApp(viewModel: OtoKlonViewModel) {
    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val connectedAccounts by viewModel.connectedAccounts.collectAsStateWithLifecycle()
    val targets by viewModel.targets.collectAsStateWithLifecycle()
    val cloneTasks by viewModel.cloneTasks.collectAsStateWithLifecycle()
    val topCloneTasks by viewModel.topCloneTasks.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val pipelineState by viewModel.pipelineState.collectAsStateWithLifecycle()
    val isPlayingVoice by viewModel.ttsPlaying.collectAsStateWithLifecycle()
    val channelSearchQuery by viewModel.channelSearchQuery.collectAsStateWithLifecycle()
    val isSearchingChannel by viewModel.isSearchingChannel.collectAsStateWithLifecycle()
    val channelInspectionResult by viewModel.channelInspectionResult.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(OtoKlonTab.INSTAGRAM_SOURCES) }

    AnimatedContent(
        targetState = isLocked,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "lock_transition"
    ) { locked ->
        if (locked) {
            LockScreen(
                correctPinHint = userProfile?.pinCode ?: "1234",
                onUnlockSuccess = { viewModel.unlockBiometric() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    OtoKlonTopBar(
                        currentTab = selectedTab,
                        isSyncActive = connectedAccounts?.autoSyncActive ?: true,
                        onLockClick = { viewModel.lockApp() }
                    )
                },
                bottomBar = {
                    OtoKlonBottomNav(
                        selectedTab = selectedTab,
                        unreadNotificationsCount = notifications.size,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground)
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        OtoKlonTab.INSTAGRAM_SOURCES -> {
                            InstagramSourceManagementScreen(
                                accounts = connectedAccounts,
                                targets = targets,
                                onSaveAccounts = { viewModel.saveAccounts(it) },
                                onAddTarget = { username, displayName ->
                                    viewModel.addTarget(username, displayName)
                                },
                                onAddMultipleTargets = {
                                    viewModel.addMultipleTargets(it)
                                },
                                onToggleMonitoring = { viewModel.toggleTargetMonitoring(it) },
                                onToggleAllTargets = { viewModel.toggleAllTargets(it) },
                                onDeleteTarget = { viewModel.removeTarget(it) },
                                onScanSingleTarget = { viewModel.scanSingleTarget(it) },
                                onScanAllTargets = { viewModel.scanAllTargetsNow() },
                                onTriggerClone = { target, caption ->
                                    viewModel.triggerAutoClone(target, caption)
                                },
                                onTestNotification = { viewModel.sendTestNotification() }
                            )
                        }
                        OtoKlonTab.WHO_AM_I -> {
                            WhoAmIScreen(
                                profile = userProfile,
                                isPlayingVoice = isPlayingVoice,
                                onSaveProfile = { viewModel.saveProfile(it) },
                                onSpeakSample = { viewModel.speakText(it) }
                            )
                        }
                        OtoKlonTab.PRODUCTION_BOX -> {
                            ProductionBoxScreen(
                                targets = targets,
                                cloneTasks = cloneTasks,
                                pipelineState = pipelineState,
                                autoSyncActive = connectedAccounts?.autoSyncActive ?: true,
                                pollIntervalMinutes = connectedAccounts?.pollIntervalMinutes ?: 5,
                                onAddTarget = { username, displayName ->
                                    viewModel.addTarget(username, displayName)
                                },
                                onAddMultipleTargets = {
                                    viewModel.addMultipleTargets(it)
                                },
                                onToggleMonitoring = { viewModel.toggleTargetMonitoring(it) },
                                onDeleteTarget = { viewModel.removeTarget(it) },
                                onTriggerClone = { target, caption ->
                                    viewModel.triggerAutoClone(target, caption)
                                }
                            )
                        }
                        OtoKlonTab.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onSendTestNotification = { viewModel.sendTestNotification() },
                                onClearAll = { viewModel.clearAllNotifications() }
                            )
                        }
                        OtoKlonTab.CHANNEL_ANALYTICS -> {
                            ChannelAnalyticsScreen(
                                channelName = userProfile?.channelName ?: "OtoKlon Shorts",
                                topCloneTasks = topCloneTasks
                            )
                        }
                        OtoKlonTab.CHANNEL_STATUS -> {
                            ChannelStatusScreen(
                                searchQuery = channelSearchQuery,
                                isSearching = isSearchingChannel,
                                channelResult = channelInspectionResult,
                                recentSearches = recentSearches,
                                onQueryChange = { viewModel.updateChannelSearchQuery(it) },
                                onSearch = { viewModel.searchChannel(it) },
                                onApplyToWhoAmI = { viewModel.applyChannelToWhoAmI(it) },
                                onAddToTargets = { viewModel.addChannelToProductionTargets(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
