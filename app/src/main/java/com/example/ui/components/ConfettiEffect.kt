package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val targetX: Float,
    val targetY: Float,
    val color: Color,
    val radius: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    isTriggered: Boolean = true
) {
    if (!isTriggered) return

    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFF1744),
            Color(0xFFFFEA00),
            Color(0xFF00E676),
            Color(0xFF2979FF),
            Color(0xFFFF9100),
            Color(0xFFD500F9)
        )
        List(40) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val distance = Random.nextFloat() * 500f + 150f
            Particle(
                initialX = 0f,
                initialY = 0f,
                targetX = (Math.cos(angle.toDouble()) * distance).toFloat(),
                targetY = (Math.sin(angle.toDouble()) * distance).toFloat() + 100f,
                color = colors.random(),
                radius = Random.nextFloat() * 7f + 6f
            )
        }
    }

    LaunchedEffect(isTriggered) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val currentP = progress.value
        val alpha = (1f - currentP).coerceIn(0f, 1f)

        particles.forEach { p ->
            val currX = centerX + p.targetX * currentP
            val currY = centerY + p.targetY * currentP
            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius * (1f - currentP * 0.3f),
                center = Offset(currX, currY)
            )
        }
    }
}
