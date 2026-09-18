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
fun DescriptionGeneratorScreen(
    viewModel: CreatorViewModel,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.descTopic.collectAsState()
    val platform by viewModel.descPlatform.collectAsState()
    val language by viewModel.descLanguage.collectAsState()
    val tone by viewModel.descTone.collectAsState()
    val keywords by viewModel.descKeywords.collectAsState()
    val result by viewModel.descResult.collectAsState()
    val isLoading by viewModel.descIsLoading.collectAsState()
    val error by viewModel.descError.collectAsState()
    val favorites by viewModel.allFavorites.collectAsState()

    val availableTones = listOf("Professional", "Viral", "Storytelling", "Simple")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopBarWithBack(
                title = "Description Generator",
                subtitle = "Write rich descriptions, hooks, and keywords",
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
                            onValueChange = { viewModel.descTopic.value = it },
                            placeholder = { Text("e.g. Hidden Underground Bunker Build", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("desc_topic_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            maxLines = 3
                        )
                    }

                    // Platform Selector
                    PlatformSelector(
                        selectedPlatform = platform,
                        onPlatformSelected = { viewModel.descPlatform.value = it }
                    )

                    // Language Selector
                    LanguageSelector(
                        selectedLanguage = language,
                        onLanguageSelected = { viewModel.descLanguage.value = it }
                    )

                    // Tone Selector
                    Column {
                        Text(
                            text = "Tone",
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
                            availableTones.forEach { t ->
                                val isSelected = t == tone
                                val borderColor = if (isSelected) NeonCyan else CardBorderColor
                                val background = if (isSelected) NeonCyan.copy(alpha = 0.25f) else CardBackground

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.descTone.value = t }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("tone_${t.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = t,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Optional Keywords
                    Column {
                        Text(
                            text = "Optional Keywords (comma separated)",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = keywords,
                            onValueChange = { viewModel.descKeywords.value = it },
                            placeholder = { Text("e.g. bunker, survival, offgrid, diy", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("desc_keywords_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    // Generate Button
                    GenerateButton(
                        text = "Generate Description",
                        onClick = { viewModel.generateDescription() },
                        isLoading = isLoading,
                        enabled = topic.isNotBlank(),
                        testTag = "generate_description_button"
                    )
                }
            }
        }

        if (isLoading) {
            item {
                LoadingAnimationView(message = "Writing SEO-optimized description for $platform...")
            }
        }

        if (error != null) {
            item {
                ErrorStateView(
                    errorMessage = error!!,
                    onRetry = { viewModel.generateDescription() }
                )
            }
        }

        if (result != null && !isLoading) {
            val fullContent = buildString {
                appendLine(result!!.mainDescription)
                if (result!!.cta.isNotBlank()) {
                    appendLine()
                    appendLine("Call to Action:")
                    appendLine(result!!.cta)
                }
                if (result!!.keywords.isNotEmpty()) {
                    appendLine()
                    appendLine("Keywords: " + result!!.keywords.joinToString(", "))
                }
                if (result!!.hashtags.isNotEmpty()) {
                    appendLine()
                    appendLine(result!!.hashtags.joinToString(" "))
                }
            }
            val isFav = favorites.any { it.content == fullContent }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row {
                        IconButton(
                            onClick = { viewModel.generateDescription() },
                            modifier = Modifier.testTag("regenerate_desc_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate",
                                tint = NeonCyan
                            )
                        }
                        IconButton(
                            onClick = { viewModel.shareContent(fullContent) },
                            modifier = Modifier.testTag("share_desc_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondary
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.toggleFavorite(
                                    type = GeneratorType.DESCRIPTION,
                                    title = topic,
                                    content = fullContent,
                                    platform = platform.displayName,
                                    language = language.displayName
                                )
                            },
                            modifier = Modifier.testTag("favorite_desc_button")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) SunsetPink else TextSecondary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.copyToClipboard(fullContent) },
                            modifier = Modifier.testTag("copy_desc_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy All",
                                tint = ElectricViolet
                            )
                        }
                    }
                }
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Main Description
                        Column {
                            Text(
                                text = "MAIN DESCRIPTION",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = result!!.mainDescription,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        }

                        // Call to Action
                        if (result!!.cta.isNotBlank()) {
                            Column {
                                Text(
                                    text = "CALL TO ACTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SunsetPink,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = result!!.cta,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Relevant Keywords
                        if (result!!.keywords.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "SEO KEYWORDS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricViolet,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    result!!.keywords.forEach { kw ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ElectricViolet.copy(alpha = 0.15f))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = kw,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ElectricViolet
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Hashtags
                        if (result!!.hashtags.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "SUGGESTED HASHTAGS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    result!!.hashtags.forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                                .clickable { viewModel.copyToClipboard(tag) }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                }
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
