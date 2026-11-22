package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenRoute
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListenQuizScreen
import com.example.ui.screens.PictureQuizScreen
import com.example.ui.screens.SpeakPracticeScreen
import com.example.ui.screens.TrophyScreen
import com.example.ui.screens.WordBuilderScreen
import com.example.ui.screens.WordVaultScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KidsEnglishApp()
            }
        }
    }
}

@Composable
fun KidsEnglishApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Handle Android system back press to return to Home screen
    BackHandler(enabled = currentScreen != ScreenRoute.HOME) {
        viewModel.navigateTo(ScreenRoute.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                userProfile = userProfile,
                onBackClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                onAvatarClick = { viewModel.navigateTo(ScreenRoute.TROPHIES) }
            )
        },
        bottomBar = {
            AppBottomNav(
                currentScreen = currentScreen,
                onTabSelected = { route -> viewModel.navigateTo(route) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                when (screen) {
                    ScreenRoute.HOME -> HomeScreen(viewModel = viewModel)
                    ScreenRoute.SPEAK_PRACTICE -> SpeakPracticeScreen(viewModel = viewModel)
                    ScreenRoute.PICTURE_QUIZ -> PictureQuizScreen(viewModel = viewModel)
                    ScreenRoute.LISTEN_FIND -> ListenQuizScreen(viewModel = viewModel)
                    ScreenRoute.WORD_BUILDER -> WordBuilderScreen(viewModel = viewModel)
                    ScreenRoute.WORD_VAULT -> WordVaultScreen(viewModel = viewModel)
                    ScreenRoute.TROPHIES -> TrophyScreen(viewModel = viewModel)
                }
            }
        }
    }
}
