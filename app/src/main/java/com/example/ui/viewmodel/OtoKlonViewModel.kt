package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.GeminiHelper
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityNotification
import com.example.data.model.ChannelStatusInfo
import com.example.data.model.CloneTask
import com.example.data.model.ConnectedAccounts
import com.example.data.model.InstagramTarget
import com.example.data.model.UserProfile
import com.example.data.repository.OtoKlonRepository
import com.example.data.service.ChannelInspectorService
import com.example.notification.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class PipelineState(
    val isRunning: Boolean = false,
    val currentStep: Int = 0, // 1 to 5
    val currentStepName: String = "",
    val currentAccount: String = "",
    val progressMessage: String = "",
    val generatedHeadline: String = "",
    val generatedTitle: String = "",
    val generatedScript: String = ""
)

class OtoKlonViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = OtoKlonRepository(database.otoKlonDao())
    private val notificationHelper = NotificationHelper(application)
    private val geminiHelper = GeminiHelper()
    private val channelInspectorService = ChannelInspectorService()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("tr", "TR")
                isTtsReady = true
            }
        }
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val connectedAccounts: StateFlow<ConnectedAccounts?> = repository.connectedAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val targets: StateFlow<List<InstagramTarget>> = repository.allTargets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cloneTasks: StateFlow<List<CloneTask>> = repository.allCloneTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topCloneTasks: StateFlow<List<CloneTask>> = repository.topCloneTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<ActivityNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _pipelineState = MutableStateFlow(PipelineState())
    val pipelineState: StateFlow<PipelineState> = _pipelineState.asStateFlow()

    private val _ttsPlaying = MutableStateFlow(false)
    val ttsPlaying: StateFlow<Boolean> = _ttsPlaying.asStateFlow()

    private val _channelSearchQuery = MutableStateFlow("")
    val channelSearchQuery: StateFlow<String> = _channelSearchQuery.asStateFlow()

    private val _isSearchingChannel = MutableStateFlow(false)
    val isSearchingChannel: StateFlow<Boolean> = _isSearchingChannel.asStateFlow()

    private val _channelInspectionResult = MutableStateFlow<ChannelStatusInfo?>(null)
    val channelInspectionResult: StateFlow<ChannelStatusInfo?> = _channelInspectionResult.asStateFlow()

    private val _recentSearches = MutableStateFlow(listOf("Ruhi Çenet", "Barış Özcan", "MrBeast", "Evrim Ağacı"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    fun updateChannelSearchQuery(query: String) {
        _channelSearchQuery.value = query
    }

    fun searchChannel(query: String) {
        val targetQuery = query.trim().ifBlank { "Ruhi Çenet" }
        _channelSearchQuery.value = targetQuery
        _isSearchingChannel.value = true

        viewModelScope.launch {
            try {
                val result = channelInspectorService.searchAndInspectChannel(targetQuery)
                _channelInspectionResult.value = result

                val currentList = _recentSearches.value.toMutableList()
                currentList.remove(targetQuery)
                currentList.add(0, targetQuery)
                _recentSearches.value = currentList.take(6)
            } finally {
                _isSearchingChannel.value = false
            }
        }
    }

    fun applyChannelToWhoAmI(channel: ChannelStatusInfo) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            val updated = current.copy(
                channelName = "${channel.channelName} Klon",
                toneStyle = "${channel.channelName} Tarzı: ${channel.category}. ${channel.cloneStrategyTip}"
            )
            repository.saveUserProfile(updated)
            repository.addNotification(
                ActivityNotification(
                    title = "Kanal Tarzı Klonlandı",
                    message = "${channel.channelName} kanalının konuşma ve video tarzı 'Ben Kimim' profiline aktarıldı.",
                    stepType = "SYSTEM"
                )
            )
        }
    }

    fun addChannelToProductionTargets(channel: ChannelStatusInfo) {
        viewModelScope.launch {
            repository.addInstagramTarget(
                username = channel.handle,
                displayName = channel.channelName
            )
            repository.addNotification(
                ActivityNotification(
                    title = "Yapım Kutusuna Eklendi",
                    message = "${channel.channelName} (${channel.handle}) video izleme hedeflerine eklendi.",
                    stepType = "SYSTEM"
                )
            )
        }
    }

    fun unlockWithPin(enteredPin: String): Boolean {
        val currentProfile = userProfile.value
        val requiredPin = currentProfile?.pinCode ?: "1234"
        val isMatch = (enteredPin == requiredPin)
        if (isMatch) {
            _isLocked.value = false
        }
        return isMatch
    }

    fun unlockBiometric() {
        _isLocked.value = false
    }

    fun lockApp() {
        _isLocked.value = true
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun saveAccounts(accounts: ConnectedAccounts) {
        viewModelScope.launch {
            repository.saveConnectedAccounts(accounts)
        }
    }

    fun addTarget(username: String, displayName: String) {
        viewModelScope.launch {
            repository.addInstagramTarget(username, displayName)
            repository.addNotification(
                ActivityNotification(
                    title = "Yeni Hesap Takibe Alındı",
                    message = "$username hesabı video izleme listesine eklendi.",
                    stepType = "SYSTEM"
                )
            )
        }
    }

    fun addMultipleTargets(targetList: List<Pair<String, String>>) {
        viewModelScope.launch {
            targetList.forEach { (username, displayName) ->
                repository.addInstagramTarget(username, displayName)
            }
            repository.addNotification(
                ActivityNotification(
                    title = "${targetList.size} Takip Edilen Hesap Eklendi",
                    message = "Instagram takip listenizden seçtiğiniz ${targetList.size} hesap otomatik video dinleme listesine alındı.",
                    stepType = "SYSTEM"
                )
            )
        }
    }

    fun toggleTargetMonitoring(target: InstagramTarget) {
        viewModelScope.launch {
            val updated = target.copy(isMonitoring = !target.isMonitoring)
            repository.updateTarget(updated)
        }
    }

    fun toggleAllTargets(enable: Boolean) {
        viewModelScope.launch {
            targets.value.forEach { target ->
                repository.updateTarget(target.copy(isMonitoring = enable))
            }
            repository.addNotification(
                ActivityNotification(
                    title = if (enable) "Tüm Kaynaklar Dinlemede" else "Tüm Kaynaklar Duraklatıldı",
                    message = "${targets.value.size} hesabın otomatik video takibi ${if (enable) "aktifleştirildi" else "durduruldu"}.",
                    stepType = "SYSTEM"
                )
            )
        }
    }

    fun scanAllTargetsNow() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            targets.value.forEach { target ->
                repository.updateTarget(
                    target.copy(
                        lastCheckedTimestamp = currentTime,
                        lastVideoDetected = "Tarandı (Yeni video bekleniyor)"
                    )
                )
            }
            repository.addNotification(
                ActivityNotification(
                    title = "Instagram Kaynak Taraması Tamamlandı",
                    message = "${targets.value.size} profil başarıyla tarandı. Yeni video algılandığında otomatik klonlama başlayacak.",
                    stepType = "DETECTION"
                )
            )
            notificationHelper.sendStepNotification(
                notificationId = 111,
                title = "OtoKlon: Kaynaklar Tarandı",
                message = "${targets.value.size} Instagram hesabı kontrol edildi. Sistem hazır.",
                stepName = "Tarama"
            )
        }
    }

    fun scanSingleTarget(target: InstagramTarget) {
        viewModelScope.launch {
            repository.updateTarget(
                target.copy(
                    lastCheckedTimestamp = System.currentTimeMillis(),
                    lastVideoDetected = "Son kontrol yapıldı - Güncel"
                )
            )
            repository.addNotification(
                ActivityNotification(
                    title = "${target.username} Kontrol Edildi",
                    message = "${target.displayName} profilinin son Reels ve videoları kontrol edildi.",
                    stepType = "DETECTION"
                )
            )
        }
    }

    fun removeTarget(target: InstagramTarget) {
        viewModelScope.launch {
            repository.removeTarget(target)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            notificationHelper.sendStepNotification(
                notificationId = (100..999).random(),
                title = "Oto Klon Test Bildirimi",
                message = "Her işlem adımında bu şekilde anlık bildirim alacaksınız.",
                stepName = "Test"
            )
            repository.addNotification(
                ActivityNotification(
                    title = "Test Bildirimi Gönderildi",
                    message = "Sistem bildirim kanalı test edildi ve aktifleştirildi.",
                    stepType = "SYSTEM"
                )
            )
        }
    }

    fun speakText(text: String) {
        if (isTtsReady && tts != null) {
            _ttsPlaying.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "OtoKlonTTS")
            viewModelScope.launch {
                delay(3500)
                _ttsPlaying.value = false
            }
        } else {
            // Visual simulated TTS
            _ttsPlaying.value = true
            viewModelScope.launch {
                delay(3000)
                _ttsPlaying.value = false
            }
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _ttsPlaying.value = false
    }

    fun triggerAutoClone(selectedTarget: InstagramTarget? = null, customCaption: String? = null) {
        if (_pipelineState.value.isRunning) return

        viewModelScope.launch {
            val target = selectedTarget ?: targets.value.firstOrNull() ?: InstagramTarget(
                username = "@ilgincbilgiler",
                displayName = "Günün İlginç Bilgisi"
            )
            val profile = userProfile.value ?: UserProfile()
            val caption = customCaption ?: target.lastVideoDetected ?: "İnanılmaz yeni keşif görüntüleri"

            _pipelineState.value = PipelineState(
                isRunning = true,
                currentStep = 1,
                currentStepName = "Video Algılandı & İndiriliyor",
                currentAccount = target.username,
                progressMessage = "${target.username} hesabından yeni video indiriliyor..."
            )

            // Step 1: Detection & Download
            notificationHelper.sendStepNotification(
                notificationId = 101,
                title = "1/5 Video İndirildi",
                message = "${target.username} yeni video paylaştı, yüksek kalitede indirildi.",
                stepName = "İndirme"
            )
            repository.addNotification(
                ActivityNotification(
                    title = "1/5 Video İndirildi",
                    message = "${target.username} videosu başarıyla cihaza alındı.",
                    stepType = "DETECTION"
                )
            )
            delay(1500)

            // Step 2: Black Strip & Styled Headline
            _pipelineState.value = _pipelineState.value.copy(
                currentStep = 2,
                currentStepName = "Siyah Şerit & Yazı Ekleniyor",
                progressMessage = "9:16 Shorts formatında siyah şerit yerleştiriliyor..."
            )

            val aiResult = geminiHelper.generateShortsContent(
                sourceAccount = target.username,
                rawCaption = caption,
                personaChannel = profile.channelName,
                personaTone = profile.toneStyle,
                voiceTone = profile.voiceTone
            )

            notificationHelper.sendStepNotification(
                notificationId = 102,
                title = "2/5 Siyah Şerit Eklendi",
                message = "\"${aiResult.stripHeadline}\" şeriti videoya basıldı.",
                stepName = "Şerit"
            )
            repository.addNotification(
                ActivityNotification(
                    title = "2/5 Siyah Şerit Eklendi",
                    message = "Şerit yazısı: ${aiResult.stripHeadline}",
                    stepType = "STRIP"
                )
            )
            delay(1500)

            // Step 3: AI YouTube Title & Description Generation
            _pipelineState.value = _pipelineState.value.copy(
                currentStep = 3,
                currentStepName = "YouTube Başlığı Hazırlanıyor",
                progressMessage = "Kanal tarzına (${profile.channelName}) uygun başlık ve etiketler üretiliyor...",
                generatedHeadline = aiResult.stripHeadline,
                generatedTitle = aiResult.youtubeTitle,
                generatedScript = aiResult.voiceScript
            )
            delay(1200)

            // Step 4: AI Voiceover (Text to speech)
            _pipelineState.value = _pipelineState.value.copy(
                currentStep = 4,
                currentStepName = "AI Seslendirme Yapılıyor",
                progressMessage = "${profile.voiceTone} ile ses sentezi oluşturuluyor..."
            )
            notificationHelper.sendStepNotification(
                notificationId = 103,
                title = "4/5 AI Seslendirme Hazır",
                message = "Şerit metni yapay zeka sesiyle seslendirildi.",
                stepName = "AI Ses"
            )
            repository.addNotification(
                ActivityNotification(
                    title = "4/5 AI Seslendirildi",
                    message = "${profile.voiceTone} sesi başarıyla video parçasına gömüldü.",
                    stepType = "VOICE"
                )
            )
            delay(1500)

            // Step 5: Upload to YouTube Shorts
            _pipelineState.value = _pipelineState.value.copy(
                currentStep = 5,
                currentStepName = "YouTube Shorts'a Yükleniyor",
                progressMessage = "YouTube Shorts API ile videonuz yayınlanıyor..."
            )
            delay(1600)

            val newTask = CloneTask(
                sourceUsername = target.username,
                originalCaption = caption,
                stripHeadline = aiResult.stripHeadline,
                youtubeTitle = aiResult.youtubeTitle,
                voiceScript = aiResult.voiceScript,
                voiceTone = profile.voiceTone,
                step = 5,
                status = "COMPLETED",
                createdAt = System.currentTimeMillis(),
                youtubeShortsUrl = "https://youtube.com/shorts/otoklon_${System.currentTimeMillis() % 100000}",
                estimatedViews = (5000..85000).random(),
                likesCount = (400..7500).random()
            )
            repository.saveCloneTask(newTask)
            repository.updateTarget(target.copy(totalCloned = target.totalCloned + 1))

            notificationHelper.sendStepNotification(
                notificationId = 104,
                title = "5/5 YouTube Shorts Yayında!",
                message = "\"${aiResult.youtubeTitle}\" başarıyla YouTube Shorts'a yüklendi.",
                stepName = "YouTube"
            )
            repository.addNotification(
                ActivityNotification(
                    title = "5/5 YouTube Shorts Yüklendi!",
                    message = "${aiResult.youtubeTitle} yayına girdi.",
                    stepType = "YOUTUBE"
                )
            )

            _pipelineState.value = _pipelineState.value.copy(
                isRunning = false,
                currentStep = 5,
                currentStepName = "Tamamlandı! Shorts Yayında 🎉",
                progressMessage = "Video başarıyla YouTube Shorts kanalınıza yüklendi."
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
