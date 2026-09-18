package com.example.data.model

enum class Platform(val displayName: String, val badge: String) {
    YOUTUBE("YouTube", "YouTube"),
    FACEBOOK("Facebook", "Facebook"),
    TIKTOK("TikTok", "TikTok"),
    INSTAGRAM("Instagram", "Instagram")
}

enum class Language(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    BENGALI("বাংলা", "bn")
}
