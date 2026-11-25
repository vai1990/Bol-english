package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.SkyBluePrimary

data class NavTab(
    val route: ScreenRoute,
    val titleMarathi: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)

val NavTabs = listOf(
    NavTab(ScreenRoute.HOME, "मुख्य", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    NavTab(ScreenRoute.AI_TEACHER, "AI टीचर", Icons.Filled.School, Icons.Outlined.School, "nav_ai_teacher"),
    NavTab(ScreenRoute.SPEAK_PRACTICE, "सराव", Icons.Filled.Mic, Icons.Outlined.Mic, "nav_speak"),
    NavTab(ScreenRoute.PICTURE_QUIZ, "खेळ", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports, "nav_games"),
    NavTab(ScreenRoute.TROPHIES, "बक्षिसे", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_trophies")
)

@Composable
fun AppBottomNav(
    currentScreen: ScreenRoute,
    onTabSelected: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        NavTabs.forEach { tab ->
            val isSelected = when (tab.route) {
                ScreenRoute.PICTURE_QUIZ -> currentScreen in listOf(ScreenRoute.PICTURE_QUIZ, ScreenRoute.LISTEN_FIND, ScreenRoute.WORD_BUILDER)
                else -> currentScreen == tab.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.titleMarathi
                    )
                },
                label = {
                    Text(
                        text = tab.titleMarathi,
                        fontFamily = BalooFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkyBluePrimary,
                    selectedTextColor = SkyBluePrimary,
                    indicatorColor = Color(0xFFE3F2FD),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
