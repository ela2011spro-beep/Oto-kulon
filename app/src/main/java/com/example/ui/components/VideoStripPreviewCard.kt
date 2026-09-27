package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.LilacPrimaryContainer
import com.example.ui.theme.YouTubeRed

@Composable
fun VideoStripPreviewCard(
    stripHeadline: String,
    youtubeTitle: String,
    channelName: String,
    stripPosition: String = "TOP",
    stripOpacity: Float = 0.95f,
    stripTextColor: Color = Color(0xFFFFEB3B),
    isPlayingVoice: Boolean = false,
    onPlayVoiceClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_wave")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.5.dp, LilacPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(12.dp)
            .testTag("video_strip_preview_card")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Header bar of the preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(YouTubeRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YouTube Shorts Canlı Önizleme (9:16)",
                        style = MaterialTheme.typography.labelMedium,
                        color = LilacPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onPlayVoiceClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlayingVoice) LilacPrimaryContainer else Color(0xFF2E1C44)
                        )
                        .testTag("voice_preview_button")
                ) {
                    Icon(
                        imageVector = if (isPlayingVoice) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                        contentDescription = "AI Sesi Dinle",
                        tint = if (isPlayingVoice) LilacPrimary else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 9:16 Simulated Shorts Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(9f / 14f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1030),
                                Color(0xFF331D4F),
                                Color(0xFF130922)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF5A4870), RoundedCornerShape(14.dp))
            ) {
                // Background simulated video motion elements
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LilacPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Orijinal Video Katmanı",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                // SİYAH ŞERİT (Black Strip) - Top or Bottom as requested
                val stripColor = Color.Black.copy(alpha = stripOpacity)
                if (stripPosition == "TOP" || stripPosition == "BOTH") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .background(stripColor)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stripHeadline.ifBlank { "BİLİM DÜNYASI ŞOKTA! GÖRÜNEN ŞEY NE?" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = stripTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                if (stripPosition == "BOTTOM" || stripPosition == "BOTH") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 60.dp)
                            .background(stripColor)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stripHeadline.ifBlank { "BİLİM DÜNYASI ŞOKTA! GÖRÜNEN ŞEY NE?" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = stripTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Animated AI Voice Wave overlay when playing
                if (isPlayingVoice) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, LilacPrimary.copy(alpha = pulseAlpha), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = LilacPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Seslendiriyor...",
                                style = MaterialTheme.typography.labelSmall,
                                color = LilacPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // YouTube Shorts Right Action Rail Simulation (Like, Share)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Beğen",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Text("18B", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 10.sp)

                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Paylaş",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Paylaş", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp)
                }

                // YouTube Shorts Bottom Channel Info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.78f)
                        .padding(start = 10.dp, bottom = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(LilacPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = channelName.take(1).uppercase(),
                                color = Color(0xFF2E1065),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "@$channelName",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = youtubeTitle.ifBlank { "İnanılmaz Gizem Açıklandı! 😱 #Shorts" },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
