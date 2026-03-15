package com.shieldmesh.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shieldmesh.app.ui.theme.CardBackground
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.GlassBorder
import com.shieldmesh.app.ui.theme.GlassWhite
import com.shieldmesh.app.ui.theme.MonospaceFamily
import com.shieldmesh.app.ui.theme.TextPrimary
import com.shieldmesh.app.ui.theme.TextSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    borderColor: Color = CardBorder,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .then(
                if (glowColor != Color.Transparent) {
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = glowColor.copy(alpha = 0.08f),
                            cornerRadius = CornerRadius(cornerRadius.toPx()),
                            size = size
                        )
                    }
                } else Modifier
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CardBackground,
                        CardBackground.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.5f),
                        borderColor.copy(alpha = 0.15f)
                    )
                ),
                shape = shape
            )
    ) {
        // Glass overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GlassWhite,
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 200f
                    )
                )
        )
        content()
    }
}

@Composable
fun StatCardPremium(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    accentColor: Color
) {
    GlassCard(
        modifier = modifier,
        glowColor = accentColor,
        borderColor = accentColor.copy(alpha = 0.25f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = TextPrimary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
        }
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun PulsingDot(
    color: Color,
    size: Dp = 10.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(size * 1.8f)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha * 0.2f))
        )
        // Inner dot
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.8f + alpha * 0.2f))
        )
    }
}

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    color: Color = CardBorder
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontFamily = MonospaceFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}
