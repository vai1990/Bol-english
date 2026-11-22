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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.model.WordItem
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.BerryPink
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.SkyBluePrimary
import com.example.viewmodel.MainViewModel

@Composable
fun ListenQuizScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val listenWord by viewModel.listenWord.collectAsState()
    val options by viewModel.listenOptions.collectAsState()
    val selectedOption by viewModel.selectedListenOption.collectAsState()
    val isCorrect by viewModel.isListenCorrect.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()

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
                    onClick = { /* already here */ },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "👂 ऐका आणि शोधा", fontFamily = BalooFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.WORD_BUILDER) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "🧩 शब्द जुळवा", fontFamily = BalooFontFamily, fontSize = 13.sp, color = Color(0xFF334155))
                }
            }

            // Big Listening Prompt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("listen_prompt_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "काळजीपूर्वक ऐका आणि योग्य चित्र शोधा!",
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sound Playback Circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD))
                            .clickable { viewModel.speakCurrentListenWord(false) }
                            .testTag("play_listen_word"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "आवाज ऐका",
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "आवाज पुन्हा ऐकण्यासाठी वर टॅप करा",
                        fontFamily = BalooFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.speakCurrentListenWord(true) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = "🐢 कासव गती (हळू ऐका)",
                            fontFamily = BalooFontFamily,
                            fontSize = 13.sp,
                            color = OrangeFun
                        )
                    }
                }
            }

            // 4 Grid Choices
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    val hasAnswered = selectedOption != null
                    val isTarget = option.id == listenWord.id
                    val isChosen = selectedOption?.id == option.id

                    val borderColor = when {
                        !hasAnswered -> Color(0xFFE2E8F0)
                        isTarget -> CorrectGreen
                        isChosen && !isTarget -> Color(0xFFE53935)
                        else -> Color(0xFFE2E8F0)
                    }

                    val bgColor = when {
                        !hasAnswered -> Color.White
                        isTarget -> Color(0xFFE8F5E9)
                        isChosen && !isTarget -> Color(0xFFFFEBEE)
                        else -> Color.White
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !hasAnswered) { viewModel.answerListenQuiz(option) }
                            .testTag("listen_opt_${option.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(text = option.emoji, fontSize = 34.sp)
                                Column {
                                    Text(
                                        text = option.marathiMeaning,
                                        fontFamily = BalooFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = if (hasAnswered) option.word else "???",
                                        fontFamily = FredokaFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (hasAnswered) SkyBluePrimary else Color(0xFF94A3B8)
                                    )
                                }
                            }

                            if (hasAnswered) {
                                if (isTarget) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CorrectGreen)
                                } else if (isChosen) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFFE53935))
                                }
                            }
                        }
                    }
                }
            }

            // Answer Result & Next Button
            AnimatedVisibility(visible = selectedOption != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isCorrect == true) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isCorrect == true) CorrectGreen else Color(0xFFC62828),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = if (isCorrect == true) "खूप छान कान आहेत तुमचे! 🌟 +२ तारे" else "हा शब्द '${listenWord.word}' असा होता!",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isCorrect == true) CorrectGreen else Color(0xFFC62828)
                                )
                                Text(
                                    text = "${listenWord.word} (${listenWord.marathiPronounce}) म्हणजे ${listenWord.marathiMeaning}",
                                    fontFamily = BalooFontFamily,
                                    fontSize = 14.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.nextListenQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_next_listen"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                    ) {
                        Text(
                            text = "पुढचा आवाज ऐका (Next Sound)",
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        ConfettiEffect(isTriggered = showConfetti)
    }
}
