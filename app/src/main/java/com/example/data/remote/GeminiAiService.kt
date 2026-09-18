package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.DescriptionResult
import com.example.data.model.TitleItem
import com.example.data.model.ThumbnailTextItem
import com.example.data.model.VideoIdeaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    val isConfigured: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    private suspend fun callGeminiApi(prompt: String): String = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            throw IllegalStateException("API_KEY_NOT_CONFIGURED")
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val requestBodyJson = JSONObject().apply {
            val contents = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    }
                    put("parts", parts)
                }
                put(contentObj)
            }
            put("contents", contents)

            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
            }
            put("generationConfig", generationConfig)
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                Log.e("GeminiAiService", "API error code ${response.code}: $errorBody")
                throw IOException("API returned error code ${response.code}: $errorBody")
            }

            val responseString = response.body?.string() ?: throw IOException("Empty response from AI")
            parseCandidateText(responseString)
        }
    }

    private fun parseCandidateText(jsonString: String): String {
        val root = JSONObject(jsonString)
        val candidates = root.optJSONArray("candidates") ?: throw IOException("No candidates in response")
        if (candidates.length() == 0) throw IOException("Empty candidates list")

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content") ?: throw IOException("No content in candidate")
        val parts = content.optJSONArray("parts") ?: throw IOException("No parts in content")
        if (parts.length() == 0) throw IOException("Empty parts list")

        val text = parts.getJSONObject(0).optString("text")
        if (text.isBlank()) throw IOException("Empty text in response part")
        return text.trim()
    }

    suspend fun generateTitles(
        topic: String,
        platform: String,
        language: String,
        style: String
    ): List<TitleItem> {
        val prompt = """
            You are a viral YouTube, TikTok, Facebook, and Instagram content strategist.
            Generate 6 high-CTR, engaging video titles for the topic: "$topic".
            Platform: $platform
            Target Audience Language: $language (If Bengali/বাংলা, generate in natural Bengali script).
            Title Style: $style
            Safety & Quality Rules:
            - Avoid spammy phrasing, fake clickbait promises, or harmful/copyrighted terms.
            - Ensure titles fit the video format and character guidelines for $platform.
            Output format:
            Output exactly 6 titles, one per line. Do not prefix with numbers, bullets, or asterisks.
        """.trimIndent()

        val raw = callGeminiApi(prompt)
        return raw.lines()
            .map { it.trim().trimStart { c -> c.isDigit() || c == '.' || c == '-' || c == '*' || c == ' ' } }
            .filter { it.isNotBlank() }
            .map { TitleItem(title = it, style = style) }
    }

    suspend fun generateDescription(
        topic: String,
        platform: String,
        language: String,
        tone: String,
        keywords: String
    ): DescriptionResult {
        val prompt = """
            You are a professional video SEO and description writer for $platform creators.
            Create an engaging, high-retention video description for the topic: "$topic".
            Platform: $platform
            Language: $language
            Tone: $tone
            Keywords to include: ${keywords.ifBlank { "auto-relevant niche keywords" }}
            Safety & Quality Rules:
            - No fake claims or misleading information.
            - Clean formatting.
            Structure your response strictly using these four section headers:
            [MAIN DESCRIPTION]
            (Write 2-3 engaging, well-structured paragraphs summarizing the video concept and value)
            [CALL TO ACTION]
            (Write a clear, friendly call to action to like, subscribe/follow, and comment)
            [KEYWORDS]
            (List 6-8 comma-separated SEO keywords)
            [HASHTAGS]
            (List 6-8 relevant hashtags separated by spaces, starting with #)
        """.trimIndent()

        val raw = callGeminiApi(prompt)
        var mainDesc = ""
        var cta = ""
        val keywordList = mutableListOf<String>()
        val hashtagList = mutableListOf<String>()

        var currentSection = ""
        val lines = raw.lines()
        val descBuilder = StringBuilder()
        val ctaBuilder = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.equals("[MAIN DESCRIPTION]", ignoreCase = true) -> currentSection = "DESC"
                trimmed.equals("[CALL TO ACTION]", ignoreCase = true) -> currentSection = "CTA"
                trimmed.equals("[KEYWORDS]", ignoreCase = true) -> currentSection = "KEYWORDS"
                trimmed.equals("[HASHTAGS]", ignoreCase = true) -> currentSection = "HASHTAGS"
                else -> {
                    when (currentSection) {
                        "DESC" -> if (trimmed.isNotEmpty()) descBuilder.appendLine(trimmed)
                        "CTA" -> if (trimmed.isNotEmpty()) ctaBuilder.appendLine(trimmed)
                        "KEYWORDS" -> {
                            trimmed.split(",").forEach { kw ->
                                val cleaned = kw.trim().trimStart('-', '*', ' ')
                                if (cleaned.isNotEmpty()) keywordList.add(cleaned)
                            }
                        }
                        "HASHTAGS" -> {
                            trimmed.split(Regex("\\s+")).forEach { tag ->
                                val cleaned = tag.trim()
                                if (cleaned.startsWith("#") && cleaned.length > 1) {
                                    hashtagList.add(cleaned)
                                } else if (cleaned.isNotEmpty() && !cleaned.startsWith("[")) {
                                    hashtagList.add("#$cleaned")
                                }
                            }
                        }
                    }
                }
            }
        }

        mainDesc = descBuilder.toString().trim()
        cta = ctaBuilder.toString().trim()
        if (mainDesc.isEmpty()) {
            mainDesc = raw
        }

        return DescriptionResult(
            mainDescription = mainDesc,
            cta = cta,
            keywords = keywordList.distinct(),
            hashtags = hashtagList.distinct()
        )
    }

    suspend fun generateHashtags(
        topic: String,
        platform: String,
        language: String,
        count: Int
    ): List<String> {
        val prompt = """
            You are a social media hashtag optimization specialist for $platform.
            Generate exactly $count high-reach, relevant, trending hashtags for the topic: "$topic".
            Platform: $platform
            Language: $language
            Rules:
            - Only output hashtags beginning with '#'.
            - One hashtag per word or item, separated by spaces or newlines.
            - Do not include numbering, punctuation, or extra explanations.
        """.trimIndent()

        val raw = callGeminiApi(prompt)
        val tags = mutableListOf<String>()
        raw.split(Regex("\\s+")).forEach { part ->
            val cleaned = part.trim().trim(',', ';', '.', '!', '?', '"', '\'')
            if (cleaned.startsWith("#") && cleaned.length > 1) {
                tags.add(cleaned)
            } else if (cleaned.isNotBlank() && !cleaned.startsWith("[")) {
                tags.add("#$cleaned")
            }
        }
        return tags.distinct().take(count)
    }

    suspend fun generateVideoIdeas(
        niche: String,
        platform: String,
        language: String,
        count: Int
    ): List<VideoIdeaItem> {
        val prompt = """
            You are a master creative director and viral content producer for $platform creators.
            Generate exactly $count creative, high-performing video concepts for the category/niche: "$niche".
            Platform: $platform
            Language: $language
            Format strictly for each idea as:
            IDEA [number]: [Catchy Video Title or Idea]
            Concept: [Short engaging description of what the video is about]
            Hook: [Exact first 3 seconds verbal or visual hook to prevent viewers from scrolling away]
            Format: [Video format style, e.g. Tutorial, Talking Head, Documentary, Cinematic, Vlog]
            Thumbnail: [Visual thumbnail idea]
            ---
        """.trimIndent()

        val raw = callGeminiApi(prompt)
        val ideas = mutableListOf<VideoIdeaItem>()

        val ideaBlocks = raw.split("---")
        for (block in ideaBlocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
            var title = ""
            var concept = ""
            var hook = ""
            var format = ""
            var thumbnail = ""

            for (line in lines) {
                when {
                    line.startsWith("IDEA", ignoreCase = true) -> {
                        title = line.substringAfter(":").trim().ifEmpty { line }
                    }
                    line.startsWith("Concept:", ignoreCase = true) -> {
                        concept = line.substringAfter(":").trim()
                    }
                    line.startsWith("Hook:", ignoreCase = true) -> {
                        hook = line.substringAfter(":").trim()
                    }
                    line.startsWith("Format:", ignoreCase = true) -> {
                        format = line.substringAfter(":").trim()
                    }
                    line.startsWith("Thumbnail:", ignoreCase = true) -> {
                        thumbnail = line.substringAfter(":").trim()
                    }
                }
            }

            if (title.isNotBlank() || concept.isNotBlank()) {
                ideas.add(
                    VideoIdeaItem(
                        title = title.ifBlank { "Creative Video Concept" },
                        concept = concept.ifBlank { "Exciting content exploring $niche." },
                        hook = hook.ifBlank { "You won't believe what happens next..." },
                        format = format.ifBlank { "High-Paced Video" },
                        thumbnailConcept = thumbnail.ifBlank { "High contrast bold text with dynamic visual action." }
                    )
                )
            }
        }

        return if (ideas.isNotEmpty()) ideas.take(count) else listOf(
            VideoIdeaItem(
                title = "Ultimate Guide to $niche",
                concept = "A breakdown of top secrets in the $niche space.",
                hook = "Nobody tells you this about $niche...",
                format = "Deep Dive",
                thumbnailConcept = "Shocked reaction with bold question mark."
            )
        )
    }

    suspend fun generateThumbnailText(
        topic: String,
        style: String,
        language: String
    ): List<ThumbnailTextItem> {
        val prompt = """
            You are a world-class graphic designer specializing in YouTube, TikTok, and Facebook thumbnails.
            Generate 8 short, explosive thumbnail text overlays for a video about: "$topic".
            Thumbnail Style: $style
            Language: $language
            CRITICAL RULE:
            - Thumbnail text MUST be 2 to 6 words maximum.
            - Short, punchy, curiosity-inducing, scroll-stopping.
            - Usually in ALL CAPS or bold phrasing.
            Examples of great thumbnail text:
            "SECRET ROOM FOUND!"
            "INSIDE THE BUNKER"
            "HE BUILT THIS!"
            "DON'T MAKE THIS MISTAKE"
            "YOU WON'T BELIEVE THIS!"
            "THE HIDDEN HOUSE"
            Output format:
            Output ONLY the phrases, one per line. No numbers, bullets, or extra text.
        """.trimIndent()

        val raw = callGeminiApi(prompt)
        return raw.lines()
            .map { it.trim().trimStart { c -> c.isDigit() || c == '.' || c == '-' || c == '*' || c == ' ' }.trim('"', '\'') }
            .filter { it.isNotBlank() && it.split(" ").size <= 8 }
            .map { ThumbnailTextItem(text = it, style = style) }
    }
}
