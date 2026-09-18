package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DescriptionGeneratorScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HashtagGeneratorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IdeaGeneratorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThumbnailGeneratorScreen
import com.example.ui.screens.TitleGeneratorScreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CreatorToolkitTheme
import com.example.ui.theme.DeepSpaceBackground
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ActiveGenerator
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.CreatorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CreatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkMode by viewModel.darkModeEnabled.collectAsState()

            CreatorToolkitTheme(darkTheme = darkMode) {
                CreatorApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CreatorApp(viewModel: CreatorViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeGenerator by viewModel.activeGenerator.collectAsState()

    // Handle System Back Press
    BackHandler(enabled = activeGenerator != ActiveGenerator.NONE || currentTab != BottomTab.HOME) {
        if (activeGenerator != ActiveGenerator.NONE) {
            viewModel.closeGenerator()
        } else if (currentTab != BottomTab.HOME) {
            viewModel.selectTab(BottomTab.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground),
        containerColor = DeepSpaceBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (activeGenerator == ActiveGenerator.NONE) {
                CreatorBottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeGenerator,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "generator_screen_transition"
            ) { generator ->
                when (generator) {
                    ActiveGenerator.TITLE -> TitleGeneratorScreen(viewModel = viewModel)
                    ActiveGenerator.DESCRIPTION -> DescriptionGeneratorScreen(viewModel = viewModel)
                    ActiveGenerator.HASHTAGS -> HashtagGeneratorScreen(viewModel = viewModel)
                    ActiveGenerator.IDEA -> IdeaGeneratorScreen(viewModel = viewModel)
                    ActiveGenerator.THUMBNAIL -> ThumbnailGeneratorScreen(viewModel = viewModel)
                    ActiveGenerator.NONE -> {
                        Crossfade(
                            targetState = currentTab,
                            label = "bottom_tab_transition"
                        ) { tab ->
                            when (tab) {
                                BottomTab.HOME -> HomeScreen(viewModel = viewModel)
                                BottomTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                                BottomTab.FAVORITES -> FavoritesScreen(viewModel = viewModel)
                                BottomTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorBottomNavBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CardBackground.copy(alpha = 0.95f),
                            Color(0xFF0F111D)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CardBorderColor.copy(alpha = 0.8f),
                            CardBorderColor.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    selected = currentTab == BottomTab.HOME,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    onClick = { onTabSelected(BottomTab.HOME) },
                    testTag = "tab_home"
                )

                BottomNavItem(
                    label = "History",
                    selected = currentTab == BottomTab.HISTORY,
                    selectedIcon = Icons.Filled.History,
                    unselectedIcon = Icons.Outlined.History,
                    onClick = { onTabSelected(BottomTab.HISTORY) },
                    testTag = "tab_history"
                )

                BottomNavItem(
                    label = "Favorites",
                    selected = currentTab == BottomTab.FAVORITES,
                    selectedIcon = Icons.Filled.Favorite,
                    unselectedIcon = Icons.Filled.FavoriteBorder,
                    onClick = { onTabSelected(BottomTab.FAVORITES) },
                    testTag = "tab_favorites"
                )

                BottomNavItem(
                    label = "Settings",
                    selected = currentTab == BottomTab.SETTINGS,
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    onClick = { onTabSelected(BottomTab.SETTINGS) },
                    testTag = "tab_settings"
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = ElectricViolet
    val inactiveColor = TextSecondary

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .then(
                    if (selected) {
                        Modifier
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.15f))
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (selected) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) activeColor else inactiveColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}
