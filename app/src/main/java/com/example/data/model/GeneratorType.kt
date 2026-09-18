package com.example.data.model

enum class GeneratorType(
    val title: String,
    val iconEmoji: String,
    val description: String
) {
    TITLE(
        title = "Title Generator",
        iconEmoji = "🎬",
        description = "Create viral, catchy, high-CTR titles for your videos"
    ),
    DESCRIPTION(
        title = "Description Generator",
        iconEmoji = "📝",
        description = "Write engaging descriptions with hooks, keywords, and CTAs"
    ),
    HASHTAGS(
        title = "Hashtag Generator",
        iconEmoji = "#️⃣",
        description = "Discover trending and high-reach tags for rapid growth"
    ),
    IDEA(
        title = "Video Idea Generator",
        iconEmoji = "💡",
        description = "Brainstorm high-performing concepts, hooks, and formats"
    ),
    THUMBNAIL(
        title = "Thumbnail Text Generator",
        iconEmoji = "🖼️",
        description = "Generate bold 2-6 word punchlines that stop the scroll"
    );

    val categoryName: String
        get() = when (this) {
            TITLE -> "Titles"
            DESCRIPTION -> "Descriptions"
            HASHTAGS -> "Hashtags"
            IDEA -> "Ideas"
            THUMBNAIL -> "Thumbnail"
        }
}
