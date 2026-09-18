package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Language
import com.example.data.model.Platform
import com.example.ui.components.GlassCard
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CreatorViewModel

@Composable
fun SettingsScreen(
    viewModel: CreatorViewModel,
    modifier: Modifier = Modifier
) {
    val darkMode by viewModel.darkModeEnabled.collectAsState()
    val defaultPlatform by viewModel.defaultPlatform.collectAsState()
    val defaultLanguage by viewModel.defaultLanguage.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPlatformDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Preferences and application management",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // App Info Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Creator Toolkit",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Creator Toolkit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan
                        )
                        Text(
                            text = "AI Suite for Social Media Creators",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Appearance & Preferences
        item {
            Text(
                text = "PREFERENCES",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricViolet,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingToggleRow(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Sleek creator-friendly high contrast theme",
                        checked = darkMode,
                        onCheckedChange = { viewModel.darkModeEnabled.value = it },
                        testTag = "toggle_dark_mode"
                    )

                    SettingNavRow(
                        icon = Icons.Default.SmartDisplay,
                        title = "Default Platform",
                        value = defaultPlatform.displayName,
                        onClick = { showPlatformDialog = true },
                        testTag = "setting_default_platform"
                    )

                    SettingNavRow(
                        icon = Icons.Default.Language,
                        title = "Default Language",
                        value = defaultLanguage.displayName,
                        onClick = { showLanguageDialog = true },
                        testTag = "setting_default_language"
                    )

                    SettingToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Tips, trend alerts, and creator recommendations",
                        checked = notifications,
                        onCheckedChange = { viewModel.notificationsEnabled.value = it },
                        testTag = "toggle_notifications"
                    )
                }
            }
        }

        // Data & Privacy
        item {
            Text(
                text = "DATA & LEGAL",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricViolet,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingNavRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Clear Generation History",
                        value = "Erase all local logs",
                        onClick = { showClearHistoryDialog = true },
                        tintColor = Color(0xFFEF4444),
                        testTag = "setting_clear_history"
                    )

                    SettingNavRow(
                        icon = Icons.Default.Policy,
                        title = "Privacy Policy",
                        value = "View terms",
                        onClick = { showPrivacyDialog = true },
                        testTag = "setting_privacy_policy"
                    )

                    SettingNavRow(
                        icon = Icons.Default.VerifiedUser,
                        title = "Terms of Use",
                        value = "View license",
                        onClick = { showTermsDialog = true },
                        testTag = "setting_terms_of_use"
                    )

                    SettingNavRow(
                        icon = Icons.Default.Info,
                        title = "About Creator Toolkit",
                        value = "v1.0.0",
                        onClick = { showAboutDialog = true },
                        testTag = "setting_about"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Platform Selector Dialog
    if (showPlatformDialog) {
        AlertDialog(
            onDismissRequest = { showPlatformDialog = false },
            title = { Text("Choose Default Platform", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Platform.values().forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (p == defaultPlatform) ElectricViolet.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    viewModel.defaultPlatform.value = p
                                    viewModel.titlePlatform.value = p
                                    viewModel.descPlatform.value = p
                                    viewModel.hashtagPlatform.value = p
                                    viewModel.ideaPlatform.value = p
                                    showPlatformDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = p.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (p == defaultPlatform) ElectricViolet else TextPrimary,
                                fontWeight = if (p == defaultPlatform) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlatformDialog = false }) {
                    Text("Done", color = ElectricViolet)
                }
            },
            containerColor = CardBackground
        )
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Choose Default Language", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Language.values().forEach { l ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (l == defaultLanguage) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    viewModel.defaultLanguage.value = l
                                    viewModel.titleLanguage.value = l
                                    viewModel.descLanguage.value = l
                                    viewModel.hashtagLanguage.value = l
                                    viewModel.ideaLanguage.value = l
                                    viewModel.thumbLanguage.value = l
                                    showLanguageDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = l.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (l == defaultLanguage) NeonCyan else TextPrimary,
                                fontWeight = if (l == defaultLanguage) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Done", color = NeonCyan)
                }
            },
            containerColor = CardBackground
        )
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear All History?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("All saved generator outputs will be deleted from your local device database.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardBackground
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Creator Toolkit respects your privacy.\n\n" +
                                "• All your saved video titles, descriptions, and favorite ideas are stored strictly on your local device via SQLite/Room database.\n" +
                                "• Prompt inputs are sent securely to Google's Gemini API strictly to produce your requested video content.\n" +
                                "• No personal tracking data, biometric data, or unnecessary identifiers are collected or sold.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = ElectricViolet)
                }
            },
            containerColor = CardBackground
        )
    }

    // Terms of Use Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Use", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "By using Creator Toolkit:\n\n" +
                                "• You own full rights to publish the video titles, descriptions, hashtags, and concepts generated for your YouTube, Facebook, TikTok, and Instagram channels.\n" +
                                "• You agree not to generate hate speech, harmful instructions, sexual content, or fraudulent schemes.\n" +
                                "• Creator Toolkit provides high-quality creative suggestions, but final reach and algorithm performance depend on your production quality and audience engagement.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close", color = ElectricViolet)
                }
            },
            containerColor = CardBackground
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Creator Toolkit", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Creator Toolkit v1.0.0\n\n" +
                                "Created for content creators across YouTube, TikTok, Facebook, and Instagram.\n\n" +
                                "Features:\n" +
                                "• 🎬 Video Title Generator\n" +
                                "• 📝 Video Description Generator\n" +
                                "• #️⃣ Hashtag Generator with interactive chips\n" +
                                "• 💡 Video Idea & Hook Generator\n" +
                                "• 🖼️ High-CTR Thumbnail Text Generator\n\n" +
                                "Built with Kotlin, Jetpack Compose, Room Database, and Gemini 3.5 Flash.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Got It", color = ElectricViolet)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ElectricViolet.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricViolet,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ElectricViolet,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = CardBackground
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun SettingNavRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    tintColor: Color = ElectricViolet,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tintColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
