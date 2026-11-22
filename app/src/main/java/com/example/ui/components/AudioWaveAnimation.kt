package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BerryPink
import com.example.ui.theme.SkyBluePrimary

@Composable
fun PulsingMicButton(
    isListening: Boolean,
    rmsDb: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 88.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    // Dynamic wave size reacting to kid's voice volume
    val voiceScale = if (isListening) {
        1f + (rmsDb.coerceIn(0f, 10f) / 18f)
    } else {
        1f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size * 1.6f)
    ) {
        // Outer pulsing wave
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(size * 1.5f)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(BerryPink.copy(alpha = pulseAlpha))
            )
            // Intermediate wave reacting to voice
            Box(
                modifier = Modifier
                    .size(size * 1.25f)
                    .scale(voiceScale)
                    .clip(CircleShape)
                    .background(SkyBluePrimary.copy(alpha = 0.25f))
            )
        }

        // Core Mic Button
        val buttonBrush = if (isListening) {
            Brush.linearGradient(
                listOf(Color(0xFFFF1744), Color(0xFFFF5252))
            )
        } else {
            Brush.linearGradient(
                listOf(SkyBluePrimary, Color(0xFF42A5F5))
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(buttonBrush)
                .clickable(onClick = onClick)
                .testTag("mic_toggle_button")
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                contentDescription = if (isListening) "मायक्रोफोन चालू आहे" else "उच्चार करण्यासाठी बोला",
                tint = Color.White,
                modifier = Modifier.size(size * 0.52f)
            )
        }
    }
}
