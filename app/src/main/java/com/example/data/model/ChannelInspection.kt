package com.example.data.model

data class MostViewedVideo(
    val title: String,
    val viewCount: Long,
    val viewCountFormatted: String,
    val publishedDate: String,
    val duration: String,
    val likesFormatted: String,
    val commentsFormatted: String,
    val viralScore: String,
    val whyViralReason: String,
    val videoUrl: String = "https://youtube.com/watch?v=sample"
)

data class ShortsVideoInfo(
    val title: String,
    val viewsFormatted: String,
    val likesFormatted: String
)

data class ChannelStatusInfo(
    val channelName: String,
    val handle: String,
    val avatarInitial: String,
    val avatarColorHex: String,
    val isVerified: Boolean = true,
    val subscribersFormatted: String,
    val totalVideosFormatted: String,
    val totalViewsFormatted: String,
    val country: String,
    val joinedDate: String,
    val category: String,
    val businessEmail: String,
    val isGmail: Boolean = true,
    val instagramHandle: String? = null,
    val twitterHandle: String? = null,
    val websiteUrl: String? = null,
    val channelDescription: String,
    val mostViewedVideo: MostViewedVideo,
    val topShortsVideos: List<ShortsVideoInfo> = emptyList(),
    val cloneOpportunityScore: String = "%94 Yüksek Potansiyel",
    val cloneStrategyTip: String = ""
)
