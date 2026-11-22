package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.BerryPink
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.PurpleFun
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SunnyYellow
import com.example.viewmodel.MainViewModel

@Composable
fun WordBuilderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val builderWord by viewModel.builderWord.collectAsState()
    val bubbles by viewModel.builderBubbles.collectAsState()
    val spelledLetters by viewModel.spelledLetters.collectAsState()
    val isComplete by viewModel.isWordBuilt.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()

    val targetLength = builderWord.word.length

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.PICTURE_QUIZ) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "🖼️ चित्र क्विझ", fontFamily = BalooFontFamily, fontSize = 13.sp, color = Color(0xFF334155))
                }
                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.LISTEN_FIND) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "👂 ऐका आणि शोधा", fontFamily = BalooFontFamily, fontSize = 13.sp, color = Color(0xFF334155))
                }
                Button(
                    onClick = { /* already here */ },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleFun),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "🧩 शब्द जुळवा", fontFamily = BalooFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Word Target Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("word_builder_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "अक्षरांचे फुगे निवडून शब्द पूर्ण करा!",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3E5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = builderWord.emoji, fontSize = 48.sp)
                        }

                        Column {
                            Text(
                                text = builderWord.marathiMeaning,
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = PurpleFun
                            )
                            Text(
                                text = "उच्चार: ${builderWord.marathiPronounce}",
                                fontFamily = BalooFontFamily,
                                fontSize = 15.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.speakWord(builderWord.word) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE7F6))
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "ऐका",
                                tint = PurpleFun
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Letter Slots (Spelled letters row)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until targetLength) {
                            val spelledChar = spelledLetters.getOrNull(i)?.char

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (spelledChar != null) {
                                            if (isComplete) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
                                        } else {
                                            Color(0xFFF1F5F9)
                                        }
                                    )
                                    .border(
                                        2.dp,
                                        if (isComplete) CorrectGreen else if (spelledChar != null) SkyBluePrimary else Color(0xFFCBD5E1),
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = spelledChar?.toString() ?: "",
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = if (isComplete) CorrectGreen else SkyBluePrimary
                                )
                            }
                        }
                    }
                }
            }

            // Scrambled Letter Balloons to Tap
            Text(
                text = "खालील फुगे क्रमाने टॅप करा 👇",
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF64748B)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val balloonColors = listOf(
                        Brush.linearGradient(listOf(Color(0xFFFF8A80), Color(0xFFFF5252))),
                        Brush.linearGradient(listOf(Color(0xFF82B1FF), Color(0xFF448AFF))),
                        Brush.linearGradient(listOf(Color(0xFFB9F6CA), Color(0xFF69F0AE))),
                        Brush.linearGradient(listOf(Color(0xFFFFE57F), Color(0xFFFFD740))),
                        Brush.linearGradient(listOf(Color(0xFFEA80FC), Color(0xFFE040FB))),
                        Brush.linearGradient(listOf(Color(0xFFFFD180), Color(0xFFFFAB40)))
                    )

                    bubbles.forEachIndexed { index, bubble ->
                        val brush = balloonColors[index % balloonColors.size]
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (bubble.isUsed) Color(0xFFE2E8F0) else Color.Transparent)
                                .then(
                                    if (!bubble.isUsed) Modifier.background(brush) else Modifier
                                )
                                .clickable(enabled = !bubble.isUsed && !isComplete) {
                                    viewModel.tapBuilderLetter(bubble)
                                }
                                .testTag("bubble_${bubble.char}_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bubble.char.toString(),
                                fontFamily = FredokaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = if (bubble.isUsed) Color(0xFF94A3B8) else Color.White
                            )
                        }
                    }
                }
            }

            // Undo / Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.undoLastBuilderLetter() },
                    enabled = spelledLetters.isNotEmpty() && !isComplete,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "मागे (Undo)", fontFamily = BalooFontFamily, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.resetBuilderWord() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "पुन्हा (Reset)", fontFamily = BalooFontFamily, fontSize = 13.sp)
                }
            }

            // Success Card when Completed
            AnimatedVisibility(visible = isComplete) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StarRatingRow(earnedStars = 3, starSize = 36.dp)

                        Text(
                            text = "🎉 उत्कृष्ट! तुम्ही शब्द तयार केला!",
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = CorrectGreen
                        )

                        Text(
                            text = "${builderWord.word} = ${builderWord.marathiMeaning} (३ तारे मिळाले ⭐⭐⭐)",
                            fontFamily = BalooFontFamily,
                            fontSize = 15.sp,
                            color = Color(0xFF2E7D32)
                        )

                        Button(
                            onClick = { viewModel.nextBuilderWord() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_next_builder"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen)
                        ) {
                            Text(
                                text = "पुढचा शब्द जोडा 🚀",
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        ConfettiEffect(isTriggered = showConfetti)
    }
}
