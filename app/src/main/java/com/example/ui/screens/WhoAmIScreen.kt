package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.VideoStripPreviewCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LilacPrimaryContainer

@Composable
fun WhoAmIScreen(
    profile: UserProfile?,
    isPlayingVoice: Boolean,
    onSaveProfile: (UserProfile) -> Unit,
    onSpeakSample: (String) -> Unit
) {
    val currentProfile = profile ?: UserProfile()

    var channelName by remember(currentProfile) { mutableStateOf(currentProfile.channelName) }
    var toneStyle by remember(currentProfile) { mutableStateOf(currentProfile.toneStyle) }
    var voiceTone by remember(currentProfile) { mutableStateOf(currentProfile.voiceTone) }
    var stripPosition by remember(currentProfile) { mutableStateOf(currentProfile.stripPosition) }
    var stripOpacity by remember(currentProfile) { mutableStateOf(currentProfile.stripOpacity) }
    var stripTextColor by remember(currentProfile) { mutableStateOf(currentProfile.stripTextColor) }
    var pinCode by remember(currentProfile) { mutableStateOf(currentProfile.pinCode) }

    var saveSuccessMessage by remember { mutableStateOf(false) }

    val voiceOptions = listOf(
        "Erkek - Tok & Güçlü (Can)",
        "Kadın - Enerjik & Genç (Elif)",
        "Gizemli Anlatıcı (Kore)",
        "Hızlı & Dinamik (Murat)",
        "Belgesel Tarzı (Pelin)"
    )

    val colorOptions = listOf(
        "#FFEB3B" to "Sarı Vurgu",
        "#D4B2FF" to "Lila Mor",
        "#FFFFFF" to "Beyaz",
        "#4ADE80" to "Neon Yeşil"
    )

    fun save() {
        onSaveProfile(
            currentProfile.copy(
                channelName = channelName,
                toneStyle = toneStyle,
                voiceTone = voiceTone,
                stripPosition = stripPosition,
                stripOpacity = stripOpacity,
                stripTextColor = stripTextColor,
                pinCode = pinCode
            )
        )
        saveSuccessMessage = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("screen_who_am_i")
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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = LilacPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "2. BEN KİMİM",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LilacPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Yapay zeka sizi tanır, yazı ve ses tarzınızı buna göre oluşturur",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card 1: Kanal Kimliği & Konuşma Tarzı
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF43355C), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kanal Bilgileri & Anlatım Tarzı",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("Kanal İsminiz") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LilacPrimary,
                        unfocusedBorderColor = Color(0xFF43355C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = toneStyle,
                    onValueChange = { toneStyle = it },
                    label = { Text("Nasıl Konuşuyorsunuz? Yazı & Anlatım Tarzınız") },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LilacPrimary,
                        unfocusedBorderColor = Color(0xFF43355C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(
                            text = "Örn: Merak uyandırıcı, doğrudan soruyla başlayan, enerjik, şok edici detayları öne çıkaran",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: AI Ses Tonu Seçimi & Dinleme
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LilacPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yapay Zeka Ses Tonu (TTS)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            onSpeakSample("Merhaba! $channelName kanalınız için klonlanan videolar bu ses tonuyla seslendirilecektir.")
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LilacPrimaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Örnek Sesi Dinle",
                            tint = LilacPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    voiceOptions.forEach { voice ->
                        val isSelected = voiceTone == voice
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) LilacPrimaryContainer else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) LilacPrimary else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { voiceTone = voice }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = voice,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) LilacPrimary else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = LilacPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Siyah Şerit & Yazı Formatı
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Siyah Şerit & Başlık Ayarları",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Şerit Konumu:", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TOP" to "Üst Şerit", "BOTTOM" to "Alt Şerit", "BOTH" to "Üst & Alt").forEach { (pos, label) ->
                        val isSelected = stripPosition == pos
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LilacPrimary else DarkSurfaceVariant)
                                .clickable { stripPosition = pos }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = if (isSelected) Color(0xFF2E1065) else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Şerit Yazı Rengi:", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colorOptions.forEach { (hex, label) ->
                        val isSelected = stripTextColor == hex
                        val c = when (hex) {
                            "#FFEB3B" -> Color(0xFFFFEB3B)
                            "#D4B2FF" -> LilacPrimary
                            "#4ADE80" -> Color(0xFF4ADE80)
                            else -> Color.White
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .border(
                                    2.dp,
                                    if (isSelected) LilacPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { stripTextColor = hex }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) LilacPrimary else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Şerit Opaklığı: %${(stripOpacity * 100).toInt()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Slider(
                    value = stripOpacity,
                    onValueChange = { stripOpacity = it },
                    valueRange = 0.7f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = LilacPrimary,
                        activeTrackColor = LilacPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Preview of Strip
        Text(
            text = "CANLI VİDEO ŞERİT GÖRÜNÜMÜ",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = LilacPrimary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val previewColor = when (stripTextColor) {
            "#FFEB3B" -> Color(0xFFFFEB3B)
            "#D4B2FF" -> LilacPrimary
            "#4ADE80" -> Color(0xFF4ADE80)
            else -> Color.White
        }

        VideoStripPreviewCard(
            stripHeadline = "BU GERÇEĞİ KİMSE BİLMİYOR! İZLEYİN",
            youtubeTitle = "$channelName: Şok Eden Olay! 😱 #Shorts",
            channelName = channelName,
            stripPosition = stripPosition,
            stripOpacity = stripOpacity,
            stripTextColor = previewColor,
            isPlayingVoice = isPlayingVoice,
            onPlayVoiceClick = {
                onSpeakSample("Bu videodaki detay herkesi şaşırttı. $channelName ile keşfedin.")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
            onClick = { save() },
            colors = ButtonDefaults.buttonColors(containerColor = LilacPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_profile_button")
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color(0xFF2E1065)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tarzımı & Bilgilerimi Kaydet",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E1065),
                fontSize = 15.sp
            )
        }

        if (saveSuccessMessage) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✓ Bilgiler kaydedildi! Yapay zeka artık videolarınızı bu tarzda üretecek.",
                color = Color(0xFF4ADE80),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
