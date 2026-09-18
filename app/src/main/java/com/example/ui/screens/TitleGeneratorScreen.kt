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
fun TitleGeneratorScreen(
    viewModel: CreatorViewModel,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.titleTopic.collectAsState()
    val platform by viewModel.titlePlatform.collectAsState()
    val language by viewModel.titleLanguage.collectAsState()
    val style by viewModel.titleStyle.collectAsState()
    val titles by viewModel.titleResults.collectAsState()
    val isLoading by viewModel.titleIsLoading.collectAsState()
    val error by viewModel.titleError.collectAsState()
    val favorites by viewModel.allFavorites.collectAsState()

    val availableStyles = listOf("Viral", "Professional", "Curiosity", "Emotional", "Short", "SEO Friendly")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopBarWithBack(
                title = "Title Generator",
                subtitle = "Generate catchy, high-CTR viral titles",
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
                            text = "Topic / Video Subject",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { viewModel.titleTopic.value = it },
                            placeholder = { Text("e.g. Hidden Underground Bunker Build", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("title_topic_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardBorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = false,
                            maxLines = 3
                        )
                    }

                    // Platform Selector
                    PlatformSelector(
                        selectedPlatform = platform,
                        onPlatformSelected = { viewModel.titlePlatform.value = it }
                    )

                    // Language Selector
                    LanguageSelector(
                        selectedLanguage = language,
                        onLanguageSelected = { viewModel.titleLanguage.value = it }
                    )

                    // Style Selector
                    Column {
                        Text(
                            text = "Title Style",
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
                                val borderColor = if (isSelected) SunsetPink else CardBorderColor
                                val background = if (isSelected) SunsetPink.copy(alpha = 0.25f) else CardBackground

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.titleStyle.value = s }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("style_${s.lowercase().replace(" ", "_")}"),
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

                    // Generate Button
                    GenerateButton(
                        text = "Generate Titles",
                        onClick = { viewModel.generateTitles() },
                        isLoading = isLoading,
                        enabled = topic.isNotBlank(),
                        testTag = "generate_titles_button"
                    )
                }
            }
        }

        if (isLoading) {
            item {
                LoadingAnimationView(message = "Generating high-CTR titles for $platform...")
            }
        }

        if (error != null) {
            item {
                ErrorStateView(
                    errorMessage = error!!,
                    onRetry = { viewModel.generateTitles() }
                )
            }
        }

        if (titles.isNotEmpty() && !isLoading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Titles (${titles.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = { viewModel.generateTitles() },
                        modifier = Modifier.testTag("regenerate_all_titles_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate All",
                            tint = NeonCyan
                        )
                    }
                }
            }

            itemsIndexed(titles) { index, item ->
                val isFav = favorites.any { it.content == item.title }
                TitleResultCard(
                    index = index + 1,
                    title = item.title,
                    isFavorite = isFav,
                    onCopy = { viewModel.copyToClipboard(item.title) },
                    onFavorite = {
                        viewModel.toggleFavorite(
                            type = GeneratorType.TITLE,
                            title = item.title,
                            content = item.title,
                            platform = platform.displayName,
                            language = language.displayName
                        )
                    },
                    onShare = { viewModel.shareContent(item.title) },
                    onRegenerate = { viewModel.generateTitles() }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TitleResultCard(
    index: Int,
    title: String,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(ElectricViolet.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricViolet,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                        .testTag("regenerate_title_$index")
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
                        .testTag("share_title_$index")
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
                        .testTag("favorite_title_$index")
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
                        .background(ElectricViolet.copy(alpha = 0.2f))
                        .testTag("copy_title_$index")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = ElectricViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
