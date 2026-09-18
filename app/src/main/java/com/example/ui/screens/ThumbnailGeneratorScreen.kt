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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratorType
import com.example.ui.components.ErrorStateView
import com.example.ui.components.GenerateButton
import com.example.ui.components.GlassCard
import com.example.ui.components.LanguageSelector
import com.example.ui.components.LoadingAnimationView
import com.example.ui.components.TopBarWithBack
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SunsetPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CreatorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThumbnailGeneratorScreen(
    viewModel: CreatorViewModel,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.thumbTopic.collectAsState()
    val style by viewModel.thumbStyle.collectAsState()
    val language by viewModel.thumbLanguage.collectAsState()
    val phrases by viewModel.thumbResults.collectAsState()
    val isLoading by viewModel.thumbIsLoading.collectAsState()
    val error by viewModel.thumbError.collectAsState()
    val favorites by viewModel.allFavorites.collectAsState()

    val availableStyles = listOf("Viral", "Mystery", "Luxury", "Dramatic", "Funny", "Professional")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopBarWithBack(
                title = "Thumbnail Text Generator",
                subtitle = "Generate punchy 2–6 word scroll-stoppers",
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
                    // Topic Input
                    Column {
                        Text(
                            text = "Video Topic / Scene",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { viewModel.thumbTopic.value = it },
                            placeholder = { Text("e.g. Secret Underground Room Under Tree", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("thumb_topic_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = EmeraldGreen,
                                unfocusedBorderColor = CardBorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    // Thumbnail Style Selector
                    Column {
                        Text(
                            text = "Thumbnail Vibe / Style",
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
                            availableStyles.forEach { s ->
                                val isSelected = s == style
                                val borderColor = if (isSelected) EmeraldGreen else CardBorderColor
                                val background = if (isSelected) EmeraldGreen.copy(alpha = 0.25f) else CardBackground

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.thumbStyle.value = s }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("thumb_style_${s.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = s,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Language Selector
                    LanguageSelector(
                        selectedLanguage = language,
                        onLanguageSelected = { viewModel.thumbLanguage.value = it }
                    )

                    // Generate Button
                    GenerateButton(
                        text = "Generate Thumbnail Text",
                        onClick = { viewModel.generateThumbnailText() },
                        isLoading = isLoading,
                        enabled = topic.isNotBlank(),
                        testTag = "generate_thumbnail_button"
                    )
                }
            }
        }

        if (isLoading) {
            item {
                LoadingAnimationView(message = "Designing high-CTR thumbnail hooks...")
            }
        }

        if (error != null) {
            item {
                ErrorStateView(
                    errorMessage = error!!,
                    onRetry = { viewModel.generateThumbnailText() }
                )
            }
        }

        if (phrases.isNotEmpty() && !isLoading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "High-CTR Thumbnail Phrases (${phrases.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(
                        onClick = { viewModel.generateThumbnailText() },
                        modifier = Modifier.testTag("regenerate_thumb_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = NeonCyan
                        )
                    }
                }
            }

            itemsIndexed(phrases) { index, item ->
                val isFav = favorites.any { it.content == item.text }

                ThumbnailPhraseCard(
                    index = index + 1,
                    text = item.text,
                    isFavorite = isFav,
                    onCopy = { viewModel.copyToClipboard(item.text) },
                    onFavorite = {
                        viewModel.toggleFavorite(
                            type = GeneratorType.THUMBNAIL,
                            title = item.text,
                            content = item.text,
                            language = language.displayName
                        )
                    },
                    onShare = { viewModel.shareContent(item.text) },
                    onRegenerate = { viewModel.generateThumbnailText() }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ThumbnailPhraseCard(
    index: Int,
    text: String,
    isFavorite: Boolean,
    onCopy: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit,
    onRegenerate: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        borderColor = CardBorderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Visual Preview Badge simulating bold thumbnail typography
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E1B4B)
                            )
                        )
                    )
                    .border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp, horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFDE047), // High visibility thumbnail yellow/gold
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${text.split(" ").size} Words • High Impact",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Row {
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                            .testTag("regenerate_thumb_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                            .testTag("share_thumb_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onFavorite,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                            .testTag("favorite_thumb_$index")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) SunsetPink else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen.copy(alpha = 0.2f))
                            .testTag("copy_thumb_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
