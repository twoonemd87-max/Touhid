package com.example.data.repository

import com.example.data.local.FavoriteDao
import com.example.data.local.HistoryDao
import com.example.data.model.DescriptionResult
import com.example.data.model.FavoriteItem
import com.example.data.model.GeneratorType
import com.example.data.model.HistoryItem
import com.example.data.model.ThumbnailTextItem
import com.example.data.model.TitleItem
import com.example.data.model.VideoIdeaItem
import com.example.data.remote.GeminiAiService
import kotlinx.coroutines.flow.Flow

class CreatorRepository(
    private val aiService: GeminiAiService,
    private val historyDao: HistoryDao,
    private val favoriteDao: FavoriteDao
) {
    val isAiConfigured: Boolean
        get() = aiService.isConfigured

    suspend fun generateTitles(
        topic: String,
        platform: String,
        language: String,
        style: String
    ): List<TitleItem> {
        val results = aiService.generateTitles(topic, platform, language, style)
        if (results.isNotEmpty()) {
            val contentSummary = results.joinToString("\n") { "• ${it.title}" }
            historyDao.insertHistory(
                HistoryItem(
                    type = GeneratorType.TITLE.name,
                    topic = topic,
                    platform = platform,
                    language = language,
                    content = contentSummary,
                    previewSummary = results.firstOrNull()?.title ?: topic
                )
            )
        }
        return results
    }

    suspend fun generateDescription(
        topic: String,
        platform: String,
        language: String,
        tone: String,
        keywords: String
    ): DescriptionResult {
        val result = aiService.generateDescription(topic, platform, language, tone, keywords)
        val fullContent = buildString {
            appendLine(result.mainDescription)
            if (result.cta.isNotBlank()) {
                appendLine()
                appendLine("Call to Action:")
                appendLine(result.cta)
            }
            if (result.keywords.isNotEmpty()) {
                appendLine()
                appendLine("Keywords: " + result.keywords.joinToString(", "))
            }
            if (result.hashtags.isNotEmpty()) {
                appendLine()
                appendLine(result.hashtags.joinToString(" "))
            }
        }

        historyDao.insertHistory(
            HistoryItem(
                type = GeneratorType.DESCRIPTION.name,
                topic = topic,
                platform = platform,
                language = language,
                content = fullContent,
                previewSummary = result.mainDescription.take(90) + if (result.mainDescription.length > 90) "..." else ""
            )
        )
        return result
    }

    suspend fun generateHashtags(
        topic: String,
        platform: String,
        language: String,
        count: Int
    ): List<String> {
        val tags = aiService.generateHashtags(topic, platform, language, count)
        if (tags.isNotEmpty()) {
            historyDao.insertHistory(
                HistoryItem(
                    type = GeneratorType.HASHTAGS.name,
                    topic = topic,
                    platform = platform,
                    language = language,
                    content = tags.joinToString(" "),
                    previewSummary = tags.take(5).joinToString(" ")
                )
            )
        }
        return tags
    }

    suspend fun generateVideoIdeas(
        niche: String,
        platform: String,
        language: String,
        count: Int
    ): List<VideoIdeaItem> {
        val ideas = aiService.generateVideoIdeas(niche, platform, language, count)
        if (ideas.isNotEmpty()) {
            val content = ideas.joinToString("\n\n") {
                "Title: ${it.title}\nConcept: ${it.concept}\nHook: ${it.hook}\nFormat: ${it.format}\nThumbnail: ${it.thumbnailConcept}"
            }
            historyDao.insertHistory(
                HistoryItem(
                    type = GeneratorType.IDEA.name,
                    topic = niche,
                    platform = platform,
                    language = language,
                    content = content,
                    previewSummary = ideas.firstOrNull()?.title ?: niche
                )
            )
        }
        return ideas
    }

    suspend fun generateThumbnailText(
        topic: String,
        style: String,
        language: String
    ): List<ThumbnailTextItem> {
        val phrases = aiService.generateThumbnailText(topic, style, language)
        if (phrases.isNotEmpty()) {
            val content = phrases.joinToString("\n") { it.text }
            historyDao.insertHistory(
                HistoryItem(
                    type = GeneratorType.THUMBNAIL.name,
                    topic = topic,
                    platform = "All",
                    language = language,
                    content = content,
                    previewSummary = phrases.firstOrNull()?.text ?: topic
                )
            )
        }
        return phrases
    }

    // History flows
    fun getAllHistory(): Flow<List<HistoryItem>> = historyDao.getAllHistory()
    suspend fun deleteHistory(id: Long) = historyDao.deleteHistoryById(id)
    suspend fun clearAllHistory() = historyDao.clearAllHistory()

    // Favorites flows
    fun getAllFavorites(): Flow<List<FavoriteItem>> = favoriteDao.getAllFavorites()
    fun getFavoritesByType(type: String): Flow<List<FavoriteItem>> = favoriteDao.getFavoritesByType(type)
    suspend fun addFavorite(type: String, title: String, content: String, platform: String, language: String) {
        favoriteDao.insertFavorite(
            FavoriteItem(
                type = type,
                title = title,
                content = content,
                platform = platform,
                language = language
            )
        )
    }
    suspend fun removeFavorite(id: Long) = favoriteDao.deleteFavoriteById(id)
    suspend fun removeFavoriteByContent(content: String) = favoriteDao.deleteFavoriteByContent(content)
    fun isFavorite(content: String): Flow<Boolean> = favoriteDao.isFavorite(content)
}
