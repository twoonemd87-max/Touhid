package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratorType
import com.example.data.model.VideoIdeaItem
import com.example.ui.components.ErrorStateView
import com.example.ui.components.GenerateButton
import com.example.ui.components.GlassCard
import com.example.ui.components.LanguageSelector
import com.example.ui.components.LoadingAnimationView
import com.example.ui.components.PlatformSelector
import com.example.ui.components.TopBarWithBack
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SunsetPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CreatorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IdeaGeneratorScreen(
    viewModel: CreatorViewModel,
    modifier: Modifier = Modifier
) {
    val niche by viewModel.ideaNiche.collectAsState()
    val platform by viewModel.ideaPlatform.collectAsState()
    val language by viewModel.ideaLanguage.collectAsState()
    val count by viewModel.ideaCount.collectAsState()
    val ideas by viewModel.ideaResults.collectAsState()
    val isLoading by viewModel.ideaIsLoading.collectAsState()
    val error by viewModel.ideaError.collectAsState()
    val favorites by viewModel.allFavorites.collectAsState()

    val availableNiches = listOf(
        "AI Videos", "Construction", "Technology", "Gaming",
        "Education", "Motivation", "Facts", "DIY", "Travel", "Other"
    )
    val availableCounts = listOf(5, 10, 20)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopBarWithBack(
                title = "Video Idea Generator",
                subtitle = "Brainstorm high-performing concepts, hooks & formats",
                onBackClick = { viewModel.closeGenerator() }
            )
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Niche / Category Selector
                    Column {
                        Text(
                            text = "Niche / Category",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableNiches.forEach { n ->
                                val isSelected = n == niche
                                val borderColor = if (isSelected) Color(0xFFF59E0B) else CardBorderColor
                                val background = if (isSelected) Color(0xFFF59E0B).copy(alpha = 0.25f) else CardBackground

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.ideaNiche.value = n }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("niche_${n.lowercase().replace(" ", "_")}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = n,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Platform Selector
                    PlatformSelector(
                        selectedPlatform = platform,
                        onPlatformSelected = { viewModel.ideaPlatform.value = it }
                    )

                    // Language Selector
                    LanguageSelector(
                        selectedLanguage = language,
                        onLanguageSelected = { viewModel.ideaLanguage.value = it }
                    )

                    // Number of Ideas
                    Column {
                        Text(
                            text = "Number of Ideas",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            availableCounts.forEach { c ->
                                val isSelected = c == count
                                val borderColor = if (isSelected) Color(0xFFF59E0B) else CardBorderColor
                                val background = if (isSelected) Color(0xFFF59E0B).copy(alpha = 0.25f) else CardBackground

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.ideaCount.value = c }
                                        .padding(vertical = 10.dp)
                                        .testTag("idea_count_$c"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$c",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Generate Button
                    GenerateButton(
                        text = "Generate Ideas",
                        onClick = { viewModel.generateVideoIdeas() },
                        isLoading = isLoading,
                        enabled = niche.isNotBlank(),
                        testTag = "generate_ideas_button"
                    )
                }
            }
        }

        if (isLoading) {
            item {
                LoadingAnimationView(message = "Brainstorming viral video concepts for $niche...")
            }
        }

        if (error != null) {
            item {
                ErrorStateView(
                    errorMessage = error!!,
                    onRetry = { viewModel.generateVideoIdeas() }
                )
            }
        }

        if (ideas.isNotEmpty() && !isLoading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Video Concepts (${ideas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(
                        onClick = { viewModel.generateVideoIdeas() },
                        modifier = Modifier.testTag("regenerate_ideas_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = NeonCyan
                        )
                    }
                }
            }

            itemsIndexed(ideas) { index, idea ->
                val fullText = buildString {
                    appendLine("Title: ${idea.title}")
                    appendLine("Concept: ${idea.concept}")
                    appendLine("Hook: \"${idea.hook}\"")
                    appendLine("Format: ${idea.format}")
                    if (idea.thumbnailConcept.isNotBlank()) {
                        appendLine("Thumbnail: ${idea.thumbnailConcept}")
                    }
                }
                val isFav = favorites.any { it.content == fullText }

                VideoIdeaCard(
                    index = index + 1,
                    idea = idea,
                    isFavorite = isFav,
                    onCopy = { viewModel.copyToClipboard(fullText) },
                    onFavorite = {
                        viewModel.toggleFavorite(
                            type = GeneratorType.IDEA,
                            title = idea.title,
                            content = fullText,
                            platform = platform.displayName,
                            language = language.displayName
                        )
                    },
                    onShare = { viewModel.shareContent(fullText) },
                    onRegenerate = { viewModel.generateVideoIdeas() }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun VideoIdeaCard(
    index: Int,
    idea: VideoIdeaItem,
    isFavorite: Boolean,
    onCopy: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit,
    onRegenerate: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        borderColor = CardBorderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with number and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Concept
            Column {
                Text(
                    text = "CONCEPT",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = idea.concept,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            }

            // Hook
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SunsetPink.copy(alpha = 0.12f))
                    .border(1.dp, SunsetPink.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "HOOK (FIRST 3 SECONDS)",
                        style = MaterialTheme.typography.labelSmall,
                        color = SunsetPink,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${idea.hook}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // Format & Thumbnail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorderColor, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "FORMAT",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricViolet,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = idea.format,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }

                if (idea.thumbnailConcept.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorderColor, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "THUMBNAIL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = idea.thumbnailConcept,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardBackground)
                        .testTag("regenerate_idea_$index")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardBackground)
                        .testTag("share_idea_$index")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardBackground)
                        .testTag("favorite_idea_$index")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) SunsetPink else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                        .testTag("copy_idea_$index")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
