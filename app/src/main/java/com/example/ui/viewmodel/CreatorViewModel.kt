package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CreatorDatabase
import com.example.data.model.DescriptionResult
import com.example.data.model.FavoriteItem
import com.example.data.model.GeneratorType
import com.example.data.model.HistoryItem
import com.example.data.model.Language
import com.example.data.model.Platform
import com.example.data.model.ThumbnailTextItem
import com.example.data.model.TitleItem
import com.example.data.model.VideoIdeaItem
import com.example.data.remote.GeminiAiService
import com.example.data.repository.CreatorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BottomTab {
    HOME, HISTORY, FAVORITES, SETTINGS
}

enum class ActiveGenerator {
    NONE, TITLE, DESCRIPTION, HASHTAGS, IDEA, THUMBNAIL
}

class CreatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CreatorRepository

    init {
        val db = CreatorDatabase.getDatabase(application)
        val aiService = GeminiAiService()
        repository = CreatorRepository(aiService, db.historyDao(), db.favoriteDao())
    }

    val isAiConfigured: Boolean
        get() = repository.isAiConfigured

    // Navigation State
    private val _currentTab = MutableStateFlow(BottomTab.HOME)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    private val _activeGenerator = MutableStateFlow(ActiveGenerator.NONE)
    val activeGenerator: StateFlow<ActiveGenerator> = _activeGenerator.asStateFlow()

    fun selectTab(tab: BottomTab) {
        _currentTab.value = tab
        _activeGenerator.value = ActiveGenerator.NONE
    }

    fun openGenerator(generator: ActiveGenerator) {
        _activeGenerator.value = generator
    }

    fun closeGenerator() {
        _activeGenerator.value = ActiveGenerator.NONE
    }

    // --- 1. TITLE GENERATOR STATE ---
    val titleTopic = MutableStateFlow("Hidden Underground Bunker Build")
    val titlePlatform = MutableStateFlow(Platform.YOUTUBE)
    val titleLanguage = MutableStateFlow(Language.ENGLISH)
    val titleStyle = MutableStateFlow("Viral")
    val titleResults = MutableStateFlow<List<TitleItem>>(emptyList())
    val titleIsLoading = MutableStateFlow(false)
    val titleError = MutableStateFlow<String?>(null)

    fun generateTitles() {
        val topic = titleTopic.value.trim()
        if (topic.isEmpty()) return

        viewModelScope.launch {
            titleIsLoading.value = true
            titleError.value = null
            try {
                val results = repository.generateTitles(
                    topic = topic,
                    platform = titlePlatform.value.displayName,
                    language = titleLanguage.value.displayName,
                    style = titleStyle.value
                )
                titleResults.value = results
            } catch (e: Exception) {
                titleError.value = "Something went wrong. Please try again."
            } finally {
                titleIsLoading.value = false
            }
        }
    }

    // --- 2. DESCRIPTION GENERATOR STATE ---
    val descTopic = MutableStateFlow("Hidden Underground Bunker Build")
    val descPlatform = MutableStateFlow(Platform.YOUTUBE)
    val descLanguage = MutableStateFlow(Language.ENGLISH)
    val descTone = MutableStateFlow("Viral")
    val descKeywords = MutableStateFlow("underground bunker, survival shelter, diy build")
    val descResult = MutableStateFlow<DescriptionResult?>(null)
    val descIsLoading = MutableStateFlow(false)
    val descError = MutableStateFlow<String?>(null)

    fun generateDescription() {
        val topic = descTopic.value.trim()
        if (topic.isEmpty()) return

        viewModelScope.launch {
            descIsLoading.value = true
            descError.value = null
            try {
                val result = repository.generateDescription(
                    topic = topic,
                    platform = descPlatform.value.displayName,
                    language = descLanguage.value.displayName,
                    tone = descTone.value,
                    keywords = descKeywords.value
                )
                descResult.value = result
            } catch (e: Exception) {
                descError.value = "Something went wrong. Please try again."
            } finally {
                descIsLoading.value = false
            }
        }
    }

    // --- 3. HASHTAG GENERATOR STATE ---
    val hashtagTopic = MutableStateFlow("AI Video Production")
    val hashtagPlatform = MutableStateFlow(Platform.TIKTOK)
    val hashtagLanguage = MutableStateFlow(Language.ENGLISH)
    val hashtagCount = MutableStateFlow(10)
    val hashtagResults = MutableStateFlow<List<String>>(emptyList())
    val hashtagIsLoading = MutableStateFlow(false)
    val hashtagError = MutableStateFlow<String?>(null)

    fun generateHashtags() {
        val topic = hashtagTopic.value.trim()
        if (topic.isEmpty()) return

        viewModelScope.launch {
            hashtagIsLoading.value = true
            hashtagError.value = null
            try {
                val tags = repository.generateHashtags(
                    topic = topic,
                    platform = hashtagPlatform.value.displayName,
                    language = hashtagLanguage.value.displayName,
                    count = hashtagCount.value
                )
                hashtagResults.value = tags
            } catch (e: Exception) {
                hashtagError.value = "Something went wrong. Please try again."
            } finally {
                hashtagIsLoading.value = false
            }
        }
    }

    fun removeHashtag(tag: String) {
        hashtagResults.value = hashtagResults.value.filter { it != tag }
    }

    // --- 4. VIDEO IDEA GENERATOR STATE ---
    val ideaNiche = MutableStateFlow("AI Videos")
    val ideaPlatform = MutableStateFlow(Platform.YOUTUBE)
    val ideaLanguage = MutableStateFlow(Language.ENGLISH)
    val ideaCount = MutableStateFlow(5)
    val ideaResults = MutableStateFlow<List<VideoIdeaItem>>(emptyList())
    val ideaIsLoading = MutableStateFlow(false)
    val ideaError = MutableStateFlow<String?>(null)

    fun generateVideoIdeas() {
        val niche = ideaNiche.value.trim()
        if (niche.isEmpty()) return

        viewModelScope.launch {
            ideaIsLoading.value = true
            ideaError.value = null
            try {
                val ideas = repository.generateVideoIdeas(
                    niche = niche,
                    platform = ideaPlatform.value.displayName,
                    language = ideaLanguage.value.displayName,
                    count = ideaCount.value
                )
                ideaResults.value = ideas
            } catch (e: Exception) {
                ideaError.value = "Something went wrong. Please try again."
            } finally {
                ideaIsLoading.value = false
            }
        }
    }

    // --- 5. THUMBNAIL TEXT GENERATOR STATE ---
    val thumbTopic = MutableStateFlow("Secret Underground Room Under Tree")
    val thumbStyle = MutableStateFlow("Mystery")
    val thumbLanguage = MutableStateFlow(Language.ENGLISH)
    val thumbResults = MutableStateFlow<List<ThumbnailTextItem>>(emptyList())
    val thumbIsLoading = MutableStateFlow(false)
    val thumbError = MutableStateFlow<String?>(null)

    fun generateThumbnailText() {
        val topic = thumbTopic.value.trim()
        if (topic.isEmpty()) return

        viewModelScope.launch {
            thumbIsLoading.value = true
            thumbError.value = null
            try {
                val phrases = repository.generateThumbnailText(
                    topic = topic,
                    style = thumbStyle.value,
                    language = thumbLanguage.value.displayName
                )
                thumbResults.value = phrases
            } catch (e: Exception) {
                thumbError.value = "Something went wrong. Please try again."
            } finally {
                thumbIsLoading.value = false
            }
        }
    }

    // --- HISTORY STATE ---
    val historyList: StateFlow<List<HistoryItem>> = repository.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
            showToast("Item removed from history")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            showToast("History cleared")
        }
    }

    // --- FAVORITES STATE ---
    val selectedFavoriteCategory = MutableStateFlow("All")
    val allFavorites: StateFlow<List<FavoriteItem>> = repository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(
        type: GeneratorType,
        title: String,
        content: String,
        platform: String = "All",
        language: String = "English"
    ) {
        viewModelScope.launch {
            val existing = allFavorites.value.firstOrNull { it.content == content }
            if (existing != null) {
                repository.removeFavorite(existing.id)
                showToast("Removed from favorites")
            } else {
                repository.addFavorite(
                    type = type.name,
                    title = title,
                    content = content,
                    platform = platform,
                    language = language
                )
                showToast("Saved to favorites")
            }
        }
    }

    fun removeFavorite(id: Long) {
        viewModelScope.launch {
            repository.removeFavorite(id)
            showToast("Removed from favorites")
        }
    }

    // --- SETTINGS STATE ---
    val darkModeEnabled = MutableStateFlow(true)
    val defaultPlatform = MutableStateFlow(Platform.YOUTUBE)
    val defaultLanguage = MutableStateFlow(Language.ENGLISH)
    val notificationsEnabled = MutableStateFlow(true)

    // --- UTILITIES ---
    fun copyToClipboard(text: String, label: String = "Creator Toolkit") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        showToast("Copied to clipboard!")
    }

    fun shareContent(text: String, title: String = "Creator Toolkit Content") {
        val context = getApplication<Application>()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, title).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }
}
