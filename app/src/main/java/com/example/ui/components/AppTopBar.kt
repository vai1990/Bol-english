package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.model.UserProfile
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.StarGold

@Composable
fun AppTopBar(
    currentScreen: ScreenRoute,
    userProfile: UserProfile,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentScreen != ScreenRoute.HOME) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "मागे जा",
                            tint = SkyBluePrimary
                        )
                    }
                }

                // Avatar & Name
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFE082), Color(0xFFFFB300))
                            )
                        )
                        .clickable(onClick = onAvatarClick)
                        .testTag("avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.selectedAvatar.emoji,
                        fontSize = 24.sp
                    )
                }

                Text(
                    text = if (currentScreen == ScreenRoute.HOME) "बोल इंग्लिश" else when (currentScreen) {
                        ScreenRoute.AI_TEACHER -> "मीरा AI टीचर"
                        ScreenRoute.SPEAK_PRACTICE -> "उच्चार सराव"
                        ScreenRoute.PICTURE_QUIZ -> "चित्र क्विझ"
                        ScreenRoute.LISTEN_FIND -> "ऐका आणि शोधा"
                        ScreenRoute.WORD_BUILDER -> "शब्द जुळवा"
                        ScreenRoute.WORD_VAULT -> "शब्दसंग्रह"
                        ScreenRoute.TROPHIES -> "बक्षिसे"
                        else -> "बोल इंग्लिश"
                    },
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Star Counter Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFF8E1))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "तारे",
                    tint = StarGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${userProfile.totalStars}",
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}
