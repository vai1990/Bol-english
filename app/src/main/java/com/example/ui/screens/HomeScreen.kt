package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VocabularyRepository
import com.example.model.ScreenRoute
import com.example.model.WordCategory
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.BerryPink
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.KiwiGreen
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.PurpleFun
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SunnyYellow
import com.example.update.UpdateUiState
import com.example.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val dailyWord = VocabularyRepository.allWords[1] // Elephant

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        val availableUpdate = updateState as? UpdateUiState.UpdateAvailable
        if (availableUpdate != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showUpdateDialog() }
                        .testTag("home_update_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(KiwiGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = "नवीन अपडेट",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "🚀 नवीन अपडेट उपलब्ध (${availableUpdate.release.displayVersion})",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "डाउनलोड करण्यासाठी येथे टॅप करा",
                                    fontFamily = BalooFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "अपडेट पहा",
                            tint = KiwiGreen
                        )
                    }
                }
            }
        }
        // Welcome Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(SkyBluePrimary, Color(0xFF42A5F5))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🔥 ${userProfile.streakDays} दिवस सातत्य!",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFFFEB3B)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "नमस्ते, ${userProfile.kidName}! 👋",
                                fontFamily = FredokaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "आज कोणता नवा शब्द उच्चारणार?",
                                fontFamily = BalooFontFamily,
                                fontSize = 15.sp,
                                color = Color(0xFFE3F2FD)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.selectedAvatar.emoji,
                                fontSize = 38.sp
                            )
                        }
                    }
                }
            }
        }

        // Daily Spotlight Word Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_word_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐ आजचा खास शब्द (Today's Word)",
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFE65100)
                        )
                        IconButton(
                            onClick = { viewModel.speakWord(dailyWord.word) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .testTag("speak_daily_word")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "उच्चार ऐका",
                                tint = OrangeFun
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = dailyWord.emoji,
                            fontSize = 44.sp
                        )
                        Column {
                            Text(
                                text = dailyWord.word,
                                fontFamily = FredokaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "उच्चार: ${dailyWord.marathiPronounce} • अर्थ: ${dailyWord.marathiMeaning}",
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.selectWord(dailyWord)
                            viewModel.navigateTo(ScreenRoute.SPEAK_PRACTICE)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("practice_daily_word_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeFun)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "हा शब्द बोलून बघा! (Practice Now)",
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Section Title: Learn Through Games
        item {
            Text(
                text = "🎮 खेळातून शब्दसंग्रह वाढवा",
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 4 Main Play Mode Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1: Voice Recognition Practice
                PlayModeCard(
                    titleMr = "१. बोल आणि शिका (Speak & Check)",
                    titleEn = "Voice Pronunciation Checker",
                    desc = "मायक्रोफोनमध्ये अचूक इंग्रजी बोला आणि तारे जिंका!",
                    emoji = "🎙️",
                    badgeText = "व्हॉइस रेकग्निशन",
                    badgeColor = KiwiGreen,
                    bgBrush = Brush.linearGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))),
                    onClick = { viewModel.navigateTo(ScreenRoute.SPEAK_PRACTICE) },
                    testTag = "mode_speak_practice"
                )

                // Card 2: Picture Quiz
                PlayModeCard(
                    titleMr = "२. चित्र क्विझ (Picture Match)",
                    titleEn = "Identify the Word from Picture",
                    desc = "चित्रावरून योग्य इंग्रजी शब्द ओळखा आणि गुण मिळवा!",
                    emoji = "🖼️",
                    badgeText = "मजेदार क्विझ",
                    badgeColor = BerryPink,
                    bgBrush = Brush.linearGradient(listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))),
                    onClick = { viewModel.navigateTo(ScreenRoute.PICTURE_QUIZ) },
                    testTag = "mode_picture_quiz"
                )

                // Card 3: Listen & Find
                PlayModeCard(
                    titleMr = "३. ऐका आणि शोधा (Listen & Find)",
                    titleEn = "Ear Training & Sound Match",
                    desc = "इंग्रजी उच्चार काळजीपूर्वक ऐका आणि योग्य चित्र निवडा!",
                    emoji = "👂",
                    badgeText = "श्रवण सराव",
                    badgeColor = SkyBluePrimary,
                    bgBrush = Brush.linearGradient(listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))),
                    onClick = { viewModel.navigateTo(ScreenRoute.LISTEN_FIND) },
                    testTag = "mode_listen_find"
                )

                // Card 4: Word Builder
                PlayModeCard(
                    titleMr = "४. शब्द जुळवा (Letter Balloon)",
                    titleEn = "Letter Builder & Spelling",
                    desc = "अक्षरांचे रंगीबेरंगी फुगे फोडून योग्य स्पेलिंग तयार करा!",
                    emoji = "🧩",
                    badgeText = "स्पेलिंग गेम",
                    badgeColor = PurpleFun,
                    bgBrush = Brush.linearGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))),
                    onClick = { viewModel.navigateTo(ScreenRoute.WORD_BUILDER) },
                    testTag = "mode_word_builder"
                )
            }
        }

        // Section Title: Categories
        item {
            Text(
                text = "📚 विषयानुसार शब्द (Categories)",
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Categories Horizontal Carousel
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(WordCategory.values()) { category ->
                    CategoryBubbleItem(
                        category = category,
                        onClick = {
                            viewModel.selectCategory(category)
                            viewModel.navigateTo(ScreenRoute.SPEAK_PRACTICE)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlayModeCard(
    titleMr: String,
    titleEn: String,
    desc: String,
    emoji: String,
    badgeText: String,
    badgeColor: Color,
    bgBrush: Brush,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgBrush)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 32.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontFamily = BalooFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = titleMr,
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = desc,
                        fontFamily = BalooFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "सुरू करा",
                        tint = badgeColor
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBubbleItem(
    category: WordCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick)
            .testTag("category_${category.name.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(category.themeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.iconEmoji, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.marathiName,
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = category.englishName,
                fontFamily = FredokaFontFamily,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
