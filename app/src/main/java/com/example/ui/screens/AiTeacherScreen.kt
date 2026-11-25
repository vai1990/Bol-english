package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ai.AiTeacherUiState
import com.example.ai.SpeechInputLanguage
import com.example.ai.TeacherChatMessage
import com.example.ai.TeacherTopic
import com.example.speech.SpeechState
import com.example.ui.components.PulsingMicButton
import com.example.ui.theme.BalooFontFamily
import com.example.ui.theme.BerryPink
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.KiwiGreen
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.SkyBluePrimary
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiTeacherScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.teacherMessages.collectAsState()
    val teacherUiState by viewModel.aiTeacherUiState.collectAsState()
    val isTeacherSpeaking by viewModel.isTeacherSpeaking.collectAsState()
    val autoSpeakEnabled by viewModel.teacherAutoSpeak.collectAsState()
    val slowVoiceEnabled by viewModel.teacherSlowVoice.collectAsState()
    val inputLanguage by viewModel.teacherInputLanguage.collectAsState()
    val speechState by viewModel.teacherSpeechState.collectAsState()
    val rmsDb by viewModel.rmsDb.collectAsState()
    val liveTranscript by viewModel.teacherLiveTranscript.collectAsState()
    val micError by viewModel.teacherMicError.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var typedText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted) {
            viewModel.toggleTeacherVoiceInput()
        }
    }

    LaunchedEffect(messages.size, teacherUiState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Meera AI Teacher Stage Header
        TeacherStageHeader(
            isSpeaking = isTeacherSpeaking,
            isListening = speechState == SpeechState.LISTENING,
            isThinking = teacherUiState is AiTeacherUiState.Thinking,
            autoSpeakEnabled = autoSpeakEnabled,
            slowVoiceEnabled = slowVoiceEnabled,
            onStopSpeaking = { viewModel.stopTeacherSpeaking() },
            onToggleAutoSpeak = { viewModel.toggleTeacherAutoSpeak() },
            onToggleSlowVoice = { viewModel.toggleTeacherSlowVoice() },
            onResetChat = { viewModel.resetTeacherConversation() }
        )

        // 2. Guided Topic Starter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TeacherTopic.values()) { topic ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .clickable(enabled = teacherUiState !is AiTeacherUiState.Thinking) {
                            viewModel.selectTeacherTopic(topic)
                        }
                        .testTag("teacher_topic_${topic.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = topic.emoji, fontSize = 15.sp)
                        Text(
                            text = topic.marathiTitle,
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }

        // 3. Chat Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(messages, key = { _, item -> item.id }) { index, msg ->
                val isLatestTeacherMessage = msg.isFromTeacher && index == messages.lastIndex
                if (msg.isFromTeacher) {
                    TeacherMessageBubble(
                        message = msg,
                        showQuickReplies = isLatestTeacherMessage && teacherUiState !is AiTeacherUiState.Thinking,
                        onSpeakNormal = { viewModel.replayTeacherMessage(msg, isSlow = false) },
                        onSpeakSlow = { viewModel.replayTeacherMessage(msg, isSlow = true) },
                        onPracticePhraseClick = { phrase -> viewModel.speakWord(phrase, isSlow = true) },
                        onQuickReplyClick = { reply -> viewModel.sendMessageToTeacher(reply) }
                    )
                } else {
                    ChildMessageBubble(
                        message = msg,
                        avatarEmoji = userProfile.selectedAvatar.emoji
                    )
                }
            }

            if (teacherUiState is AiTeacherUiState.Thinking) {
                item(key = "teacher_thinking") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp,
                            color = SkyBluePrimary
                        )
                        Text(
                            text = stringResource(R.string.teacher_thinking),
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = SkyBluePrimary
                        )
                    }
                }
            }

            val errState = teacherUiState as? AiTeacherUiState.Error
            if (errState != null) {
                item(key = "teacher_error") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_error_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFC62828)
                                )
                                Text(
                                    text = errState.message,
                                    fontFamily = BalooFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFFC62828)
                                )
                            }
                            TextButton(
                                onClick = { viewModel.retryLastTeacherMessage() },
                                modifier = Modifier.testTag("btn_retry_teacher")
                            ) {
                                Text(
                                    text = "पुन्हा प्रयत्न",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Partial Speech Preview or Mic Error Notice
        AnimatedVisibility(visible = liveTranscript.isNotBlank() || micError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (micError != null) Color(0xFFFFF3E0) else Color(0xFFE3F2FD))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = micError ?: "🎙️ ऐकत आहे: \"$liveTranscript\"",
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (micError != null) Color(0xFFE65100) else SkyBluePrimary
                )
            }
        }

        // 4. Bottom Interactive Voice & Text Dock
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Speech Input Language Toggle (English / Marathi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpeechInputLanguage.values().forEach { lang ->
                        val isSelected = lang == inputLanguage
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) SkyBluePrimary else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { viewModel.setTeacherInputLanguage(lang) }
                                .testTag("lang_chip_${lang.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = lang.emoji, fontSize = 14.sp)
                                Text(
                                    text = lang.labelMarathi,
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                // Voice Mic Button + Text Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingMicButton(
                        isListening = speechState == SpeechState.LISTENING,
                        rmsDb = rmsDb,
                        size = 54.dp,
                        onClick = {
                            if (!hasAudioPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                viewModel.toggleTeacherVoiceInput()
                            }
                        },
                        modifier = Modifier.testTag("teacher_voice_mic_btn")
                    )

                    OutlinedTextField(
                        value = typedText,
                        onValueChange = { typedText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("teacher_text_input"),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.teacher_input_hint),
                                fontFamily = BalooFontFamily,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkyBluePrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (typedText.isNotBlank() && teacherUiState !is AiTeacherUiState.Thinking) {
                                    viewModel.sendMessageToTeacher(typedText)
                                    typedText = ""
                                }
                            }
                        )
                    )

                    IconButton(
                        onClick = {
                            if (typedText.isNotBlank() && teacherUiState !is AiTeacherUiState.Thinking) {
                                viewModel.sendMessageToTeacher(typedText)
                                typedText = ""
                            }
                        },
                        enabled = typedText.isNotBlank() && teacherUiState !is AiTeacherUiState.Thinking,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (typedText.isNotBlank() && teacherUiState !is AiTeacherUiState.Thinking) {
                                    KiwiGreen
                                } else {
                                    Color(0xFFE2E8F0)
                                }
                            )
                            .testTag("teacher_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "संदेश पाठवा",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherStageHeader(
    isSpeaking: Boolean,
    isListening: Boolean,
    isThinking: Boolean,
    autoSpeakEnabled: Boolean,
    slowVoiceEnabled: Boolean,
    onStopSpeaking: () -> Unit,
    onToggleAutoSpeak: () -> Unit,
    onToggleSlowVoice: () -> Unit,
    onResetChat: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "teacher_glow")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "teacher_ring_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("teacher_stage_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFE3F2FD), Color(0xFFFFF8E1))
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSpeaking || isListening) {
                            Box(
                                modifier = Modifier
                                    .size(66.dp)
                                    .scale(ringScale)
                                    .clip(CircleShape)
                                    .background(
                                        if (isListening) BerryPink.copy(alpha = 0.28f)
                                        else KiwiGreen.copy(alpha = 0.28f)
                                    )
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.img_ai_teacher_1795577890226),
                            contentDescription = stringResource(R.string.ai_teacher_title),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.ai_teacher_name),
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = when {
                                isListening -> "🎙️ ऐकत आहे… बोला!"
                                isThinking -> "🤔 उत्तर तयार करत आहे…"
                                isSpeaking -> "🗣️ मीरा टीचर बोलत आहे…"
                                else -> "✨ इंग्लिश संवाद टीचर (AI)"
                            },
                            fontFamily = BalooFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = when {
                                isListening -> BerryPink
                                isSpeaking -> KiwiGreen
                                else -> SkyBluePrimary
                            }
                        )
                    }
                }

                // Voice Controls Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isSpeaking) {
                        IconButton(
                            onClick = onStopSpeaking,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE))
                                .testTag("btn_stop_teacher_voice")
                        ) {
                            Icon(
                                imageVector = Icons.Default.StopCircle,
                                contentDescription = "बोलणे थांबवा",
                                tint = Color(0xFFD32F2F)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onToggleAutoSpeak,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .testTag("btn_toggle_auto_speak")
                        ) {
                            Icon(
                                imageVector = if (autoSpeakEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = if (autoSpeakEnabled) "आवाज सुरू आहे" else "आवाज बंद आहे",
                                tint = if (autoSpeakEnabled) SkyBluePrimary else Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Slow voice turtle button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (slowVoiceEnabled) Color(0xFFFFF3E0) else Color.White)
                            .border(
                                width = if (slowVoiceEnabled) 1.5.dp else 0.dp,
                                color = if (slowVoiceEnabled) OrangeFun else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(onClick = onToggleSlowVoice)
                            .testTag("btn_toggle_slow_voice"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐢", fontSize = 18.sp)
                    }

                    IconButton(
                        onClick = onResetChat,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .testTag("btn_reset_teacher_chat")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "नवीन संवाद सुरू करा",
                            tint = Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeacherMessageBubble(
    message: TeacherChatMessage,
    showQuickReplies: Boolean,
    onSpeakNormal: () -> Unit,
    onSpeakSlow: () -> Unit,
    onPracticePhraseClick: (String) -> Unit,
    onQuickReplyClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_ai_teacher_1795577890226),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Card(
                modifier = Modifier
                    .widthIn(max = 310.dp)
                    .testTag("teacher_msg_${message.id}"),
                shape = RoundedCornerShape(
                    topStart = 6.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp,
                    bottomStart = 20.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.englishText,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A),
                        lineHeight = 22.sp
                    )

                    if (message.marathiText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF8E1))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = message.marathiText,
                                fontFamily = BalooFontFamily,
                                fontSize = 14.sp,
                                color = Color(0xFF5D4037),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    if (!message.practicePhrase.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .clickable { onPracticePhraseClick(message.practicePhrase) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "सराव वाक्य ऐका",
                                tint = KiwiGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "बोलून सराव करा (Say aloud):",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "\"${message.practicePhrase}\"",
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    // Audio Replay Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.clickable(onClick = onSpeakNormal)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "ऐका",
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "ऐका",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SkyBluePrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.clickable(onClick = onSpeakSlow)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "🐢 हळू ऐका",
                                    fontFamily = BalooFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OrangeFun
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Reply Chips
        if (showQuickReplies && message.quickReplies.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 42.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                message.quickReplies.forEachIndexed { idx, suggestion ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFE3F2FD),
                        border = BorderStroke(1.dp, SkyBluePrimary.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .clickable { onQuickReplyClick(suggestion) }
                            .testTag("quick_reply_$idx")
                    ) {
                        Text(
                            text = "💬 $suggestion",
                            fontFamily = FredokaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = SkyBluePrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildMessageBubble(
    message: TeacherChatMessage,
    avatarEmoji: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .testTag("child_msg_${message.id}"),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 6.dp,
                bottomEnd = 20.dp,
                bottomStart = 20.dp
            ),
            colors = CardDefaults.cardColors(containerColor = SkyBluePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = message.englishText,
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE082)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = avatarEmoji, fontSize = 18.sp)
        }
    }
}
