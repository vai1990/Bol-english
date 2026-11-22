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
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
fun PictureQuizScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val quizWord by viewModel.quizWord.collectAsState()
    val options by viewModel.quizOptions.collectAsState()
    val selectedOption by viewModel.selectedQuizOption.collectAsState()
    val isCorrect by viewModel.isQuizAnswerCorrect.collectAsState()
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
            // Header Switcher to other games
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* already here */ },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BerryPink),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "🖼️ चित्र क्विझ", fontFamily = BalooFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                    onClick = { viewModel.navigateTo(ScreenRoute.WORD_BUILDER) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "🧩 शब्द जुळवा", fontFamily = BalooFontFamily, fontSize = 13.sp, color = Color(0xFF334155))
                }
            }

            // Question Prompt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_card"),
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
                    Text(
                        text = "हे चित्र कशाचे आहे? योग्य पर्याय निवडा:",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quizWord.emoji,
                            fontSize = 64.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "मराठी अर्थ: \"${quizWord.marathiMeaning}\"",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFE65100)
                    )
                }
            }

            // 4 Options (2x2 or 4 rows)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    QuizOptionItem(
                        option = option,
                        isSelected = selectedOption?.id == option.id,
                        isTarget = option.id == quizWord.id,
                        hasAnswered = selectedOption != null,
                        onClick = { viewModel.answerQuiz(option) }
                    )
                }
            }

            // Feedback Banner & Next Button
            AnimatedVisibility(visible = selectedOption != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect == true) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCorrect == true) CorrectGreen else Color(0xFFC62828),
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isCorrect == true) "🎉 अगदी बरोबर! +२ तारे" else "पुढच्या वेळी नक्की जमेल!",
                                        fontFamily = BalooFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isCorrect == true) CorrectGreen else Color(0xFFC62828)
                                    )
                                    Text(
                                        text = "${quizWord.word} (${quizWord.marathiPronounce}) म्हणजे ${quizWord.marathiMeaning}",
                                        fontFamily = BalooFontFamily,
                                        fontSize = 14.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.speakWord(quizWord.word) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "ऐका",
                                    tint = SkyBluePrimary
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.nextQuizQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                    ) {
                        Text(
                            text = "पुढचा प्रश्न (Next Question)",
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

@Composable
fun QuizOptionItem(
    option: WordItem,
    isSelected: Boolean,
    isTarget: Boolean,
    hasAnswered: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        !hasAnswered -> Color(0xFFE2E8F0)
        isTarget -> CorrectGreen
        isSelected && !isTarget -> Color(0xFFE53935)
        else -> Color(0xFFE2E8F0)
    }

    val bgColor = when {
        !hasAnswered -> Color.White
        isTarget -> Color(0xFFE8F5E9)
        isSelected && !isTarget -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !hasAnswered, onClick = onClick)
            .testTag("quiz_opt_${option.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = option.emoji,
                    fontSize = 28.sp
                )
                Column {
                    Text(
                        text = option.word,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = option.marathiPronounce,
                        fontFamily = BalooFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (hasAnswered) {
                if (isTarget) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CorrectGreen)
                } else if (isSelected) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFFE53935))
                }
            }
        }
    }
}
