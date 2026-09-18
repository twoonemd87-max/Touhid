package com.example.data.model

data class TitleItem(
    val title: String,
    val style: String = ""
)

data class DescriptionResult(
    val mainDescription: String,
    val cta: String,
    val keywords: List<String>,
    val hashtags: List<String>
)

data class VideoIdeaItem(
    val title: String,
    val concept: String,
    val hook: String,
    val format: String,
    val thumbnailConcept: String
)

data class ThumbnailTextItem(
    val text: String,
    val style: String = ""
)
