package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StarGold

@Composable
fun StarRatingRow(
    earnedStars: Int,
    maxStars: Int = 3,
    modifier: Modifier = Modifier,
    starSize: Dp = 36.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isEarned = i <= earnedStars
            val targetScale = if (isEarned) 1.15f else 0.95f
            val scale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "star_scale_$i"
            )

            Icon(
                imageVector = if (isEarned) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = if (isEarned) "तारा मिळवला" else "तारा",
                tint = if (isEarned) StarGold else Color(0xFFCBD5E1),
                modifier = Modifier
                    .size(starSize)
                    .scale(scale)
            )
        }
    }
}
