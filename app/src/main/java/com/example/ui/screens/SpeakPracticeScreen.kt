package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.SpeechMatchStatus
import com.example.model.WordCategory
import com.example.speech.SpeechState
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.PulsingMicButton
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.BerryPink
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.KiwiGreen
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.StarGold
import com.example.viewmodel.MainViewModel

@Composable
fun SpeakPracticeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentWord = viewModel.currentPracticeWord
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val speechState by viewModel.speechState.collectAsState()
    val rmsDb by viewModel.rmsDb.collectAsState()
    val lastResult by viewModel.lastSpeechResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()
    val wordIndex by viewModel.currentWordIndex.collectAsState()
    val totalWords = viewModel.categoryWords.collectAsState().value.size

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startListening()
        }
    }

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
            // Category Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(WordCategory.values()) { category ->
                    val isSelected = category == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) category.themeColor else Color.White,
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier
                            .clickable { viewModel.selectCategory(category) }
                            .testTag("filter_cat_${category.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = category.iconEmoji, fontSize = 16.sp)
                            Text(
                                text = category.marathiName,
                                fontFamily = BalooFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            // Word Index Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "शब्द ${wordIndex + 1} / $totalWords",
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(selectedCategory.themeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${selectedCategory.iconEmoji} ${selectedCategory.englishName}",
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = selectedCategory.themeColor
                    )
                }
            }

            // Central Main Flashcard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("practice_flashcard"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Cute Illustration / Emoji
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(selectedCategory.themeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentWord.emoji,
                            fontSize = 64.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // English Word in Fredoka
                    Text(
                        text = currentWord.word,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    // Phonetic Pronunciation
                    Text(
                        text = "/ ${currentWord.phonetic} /",
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = SkyBluePrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Marathi Guide Bubble
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "उच्चार:",
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = currentWord.marathiPronounce,
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFFBF360C)
                            )
                            Text(
                                text = "•",
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = currentWord.marathiMeaning,
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Example Sentence
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "\"${currentWord.exampleSentence}\"",
                            fontFamily = FredokaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentWord.marathiSentence,
                            fontFamily = BalooFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fun Fact
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "माहिती",
                            tint = KiwiGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = currentWord.funFact,
                            fontFamily = BalooFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF1B5E20),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Playback Buttons: Normal & Slow (Turtle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.speakCurrentWord(isSlow = false) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_listen_normal"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ऐका (Listen)",
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.speakCurrentWord(isSlow = true) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_listen_turtle"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeFun)
                        ) {
                            Text(
                                text = "🐢 हळू ऐका (Slow)",
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = OrangeFun
                            )
                        }
                    }
                }
            }

            // Voice Recognition Section: Big Interactive Microphone
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (speechState) {
                        SpeechState.LISTENING -> "🎙️ मी ऐकत आहे… स्पष्ट बोला!"
                        SpeechState.PROCESSING -> "⏳ उच्चार तपासत आहे…"
                        SpeechState.SUCCESS -> "✨ उच्चार तपासला गेला!"
                        else -> "बटण दाबा आणि '${currentWord.word}' बोला!"
                    },
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (speechState == SpeechState.LISTENING) BerryPink else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                // Large Animated Mic
                PulsingMicButton(
                    isListening = speechState == SpeechState.LISTENING,
                    rmsDb = rmsDb,
                    onClick = {
                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.toggleSpeechRecognition()
                        }
                    },
                    modifier = Modifier.testTag("main_mic_action")
                )

                Text(
                    text = if (speechState == SpeechState.LISTENING) "बोलणे झाल्यावर आपोआप थांबेल" else "मायक्रोफोन टॅप करा आणि बोला",
                    fontFamily = BalooFontFamily,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Real-Time Pronunciation Evaluation Result Card
            AnimatedVisibility(
                visible = lastResult != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                lastResult?.let { result ->
                    val isSuper = result.status == SpeechMatchStatus.EXACT || result.status == SpeechMatchStatus.VERY_CLOSE
                    val cardBg = if (isSuper) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                    val cardBorder = if (isSuper) CorrectGreen else OrangeFun

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, cardBorder, RoundedCornerShape(22.dp))
                            .testTag("pronunciation_result_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StarRatingRow(
                                earnedStars = result.starsEarned,
                                starSize = 38.dp
                            )

                            Text(
                                text = result.feedbackTitleMarathi,
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = if (isSuper) CorrectGreen else Color(0xFFE65100),
                                textAlign = TextAlign.Center
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "मुलाचा उच्चार: \"${result.recognizedWord}\"",
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSuper) CorrectGreen else OrangeFun)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${result.accuracyPercentage}% अचूक",
                                        fontFamily = BalooFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                text = result.feedbackDescription,
                                fontFamily = BalooFontFamily,
                                fontSize = 14.sp,
                                color = Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )

                            if (result.isSuccess) {
                                Button(
                                    onClick = { viewModel.nextPracticeWord() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("btn_next_after_success"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen)
                                ) {
                                    Text(
                                        text = "पुढचा शब्द शिका! 🚀",
                                        fontFamily = BalooFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error notice if speech had an issue
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { err ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F)
                            )
                            Text(
                                text = err,
                                fontFamily = BalooFontFamily,
                                fontSize = 14.sp,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            // Next / Previous Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.previousPracticeWord() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("btn_prev_word")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "मागे (Previous)",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = { viewModel.nextPracticeWord() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("btn_next_word")
                ) {
                    Text(
                        text = "पुढे (Next)",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Celebratory Confetti Burst
        ConfettiEffect(isTriggered = showConfetti)
    }
}
