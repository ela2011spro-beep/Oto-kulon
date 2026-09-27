package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val channelName: String = "OtoKlon Shorts",
    val toneStyle: String = "Merak uyandırıcı, doğrudan soruyla başlayan, samimi ve akıcı bir dil. İzleyiciyi ilk 2 saniyede yakalayan kurgu.",
    val voiceTone: String = "Erkek - Tok & Güçlü (Can)", // e.g. "Kadın - Enerjik (Elif)", "Gizemli Anlatıcı (Kore)", etc.
    val stripPosition: String = "TOP", // TOP, BOTTOM, BOTH
    val stripOpacity: Float = 0.95f,
    val stripTextColor: String = "#FFEB3B", // Yellow highlight
    val pinCode: String = "1234", // Default PIN for "Sadece ben girebiliyorum"
    val isLockEnabled: Boolean = true,
    val isBiometricEnabled: Boolean = true
)

@Entity(tableName = "instagram_targets")
data class InstagramTarget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String, // e.g. "@bilgiler"
    val displayName: String,
    val isMonitoring: Boolean = true,
    val lastVideoDetected: String? = null,
    val lastCheckedTimestamp: Long = System.currentTimeMillis(),
    val totalCloned: Int = 0
)

@Entity(tableName = "clone_tasks")
data class CloneTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceUsername: String,
    val originalCaption: String,
    val stripHeadline: String,
    val youtubeTitle: String,
    val voiceScript: String,
    val voiceTone: String,
    val step: Int = 5, // 1: Algılandı, 2: Şerit Eklendi, 3: Başlık Üretildi, 4: AI Seslendirildi, 5: YouTube'a Yüklendi
    val status: String = "COMPLETED", // PENDING, PROCESSING, COMPLETED, FAILED
    val createdAt: Long = System.currentTimeMillis(),
    val youtubeShortsUrl: String? = "https://youtube.com/shorts/otoklon_sample",
    val estimatedViews: Int = 0,
    val likesCount: Int = 0
)

@Entity(tableName = "activity_notifications")
data class ActivityNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val stepType: String, // "DETECTION", "STRIP", "VOICE", "YOUTUBE", "SYSTEM"
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true
)

@Entity(tableName = "connected_accounts")
data class ConnectedAccounts(
    @PrimaryKey val id: Int = 1,
    val instagramConnected: Boolean = true,
    val instagramUsername: String = "@otoklon_bot",
    val youtubeConnected: Boolean = true,
    val youtubeChannel: String = "OtoKlon Shorts",
    val autoSyncActive: Boolean = true,
    val pollIntervalMinutes: Int = 5,
    val uploadPrivacy: String = "PUBLIC" // PUBLIC, UNLISTED, PRIVATE
)
