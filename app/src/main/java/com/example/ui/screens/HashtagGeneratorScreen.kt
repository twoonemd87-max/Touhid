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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun HashtagGeneratorScreen(
    viewModel: CreatorViewModel,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.hashtagTopic.collectAsState()
    val platform by viewModel.hashtagPlatform.collectAsState()
    val language by viewModel.hashtagLanguage.collectAsState()
    val count by viewModel.hashtagCount.collectAsState()
    val hashtags by viewModel.hashtagResults.collectAsState()
    val isLoading by viewModel.hashtagIsLoading.collectAsState()
    val error by viewModel.hashtagError.collectAsState()
    val favorites by viewModel.allFavorites.collectAsState()

    val availableCounts = listOf(5, 10, 20, 30)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopBarWithBack(
                title = "Hashtag Generator",
                subtitle = "Boost discoverability with targeted hashtag chips",
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
                            text = "Video Topic",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { viewModel.hashtagTopic.value = it },
                            placeholder = { Text("e.g. AI Video Production", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hashtag_topic_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = SunsetPink,
                                unfocusedBorderColor = CardBorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    // Platform Selector
                    PlatformSelector(
                        selectedPlatform = platform,
                        onPlatformSelected = { viewModel.hashtagPlatform.value = it }
                    )

                    // Language Selector
                    LanguageSelector(
                        selectedLanguage = language,
                        onLanguageSelected = { viewModel.hashtagLanguage.value = it }
                    )

                    // Number of Hashtags
                    Column {
                        Text(
                            text = "Number of Hashtags",
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
                                val borderColor = if (isSelected) SunsetPink else CardBorderColor
                                val background = if (isSelected) SunsetPink.copy(alpha = 0.25f) else CardBackground

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.hashtagCount.value = c }
                                        .padding(vertical = 10.dp)
                                        .testTag("count_$c"),
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
                        text = "Generate Hashtags",
                        onClick = { viewModel.generateHashtags() },
                        isLoading = isLoading,
                        enabled = topic.isNotBlank(),
                        testTag = "generate_hashtags_button"
                    )
                }
            }
        }

        if (isLoading) {
            item {
                LoadingAnimationView(message = "Finding trending hashtags for $platform...")
            }
        }

        if (error != null) {
            item {
                ErrorStateView(
                    errorMessage = error!!,
                    onRetry = { viewModel.generateHashtags() }
                )
            }
        }

        if (hashtags.isNotEmpty() && !isLoading) {
            val allTagsText = hashtags.joinToString(" ")
            val isFav = favorites.any { it.content == allTagsText }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Hashtags (${hashtags.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row {
                        IconButton(
                            onClick = { viewModel.generateHashtags() },
                            modifier = Modifier.testTag("regenerate_hashtags_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate",
                                tint = NeonCyan
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.toggleFavorite(
                                    type = GeneratorType.HASHTAGS,
                                    title = topic,
                                    content = allTagsText,
                                    platform = platform.displayName,
                                    language = language.displayName
                                )
                            },
                            modifier = Modifier.testTag("favorite_hashtags_button")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) SunsetPink else TextSecondary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.shareContent(allTagsText) },
                            modifier = Modifier.testTag("share_hashtags_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }

            // Quick Copy All Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.copyToClipboard(allTagsText) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("copy_all_hashtags_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetPink)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Copy All (${hashtags.size})", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Individual Rounded Chips
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tap a hashtag to copy, or tap '✕' to remove it.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            hashtags.forEachIndexed { index, tag ->
                                HashtagChip(
                                    tag = tag,
                                    onTap = { viewModel.copyToClipboard(tag) },
                                    onRemove = { viewModel.removeHashtag(tag) },
                                    testTag = "hashtag_chip_$index"
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HashtagChip(
    tag: String,
    onTap: () -> Unit,
    onRemove: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SunsetPink.copy(alpha = 0.15f))
            .border(1.dp, SunsetPink.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onTap)
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.bodyMedium,
            color = SunsetPink,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = TextPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
