package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // TITLE, DESCRIPTION, HASHTAGS, IDEA, THUMBNAIL
    val topic: String,
    val platform: String,
    val language: String,
    val content: String,
    val previewSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // TITLE, DESCRIPTION, HASHTAGS, IDEA, THUMBNAIL
    val title: String,
    val content: String,
    val platform: String,
    val language: String,
    val timestamp: Long = System.currentTimeMillis()
)
