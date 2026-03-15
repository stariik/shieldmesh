package com.shieldmesh.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.DarkBackground
import com.shieldmesh.app.ui.theme.GlassBorder
import com.shieldmesh.app.ui.theme.GreenAccent
import com.shieldmesh.app.ui.theme.NavBarBackground
import com.shieldmesh.app.ui.theme.NavBarUnselected
import com.shieldmesh.app.ui.theme.SurfaceDark
import com.shieldmesh.app.ui.theme.TextMuted

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    Column {
        // Top border glow line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GreenAccent.copy(alpha = 0.3f),
                            GreenAccent.copy(alpha = 0.5f),
                            GreenAccent.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NavBarBackground.copy(alpha = 0.95f),
                            NavBarBackground
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavScreens.forEach { screen ->
                val selected = currentRoute == screen.route

                val iconColor by animateColorAsState(
                    targetValue = if (selected) GreenAccent else NavBarUnselected,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "navColor"
                )

                val indicatorSize by animateDpAsState(
                    targetValue = if (selected) 4.dp else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "indicator"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(screen) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .then(
                                if (selected) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GreenAccent.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                } else {
                                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                }
                            )
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = screen.title,
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = iconColor,
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Animated dot indicator
                    Box(
                        modifier = Modifier
                            .size(indicatorSize)
                            .clip(CircleShape)
                            .background(GreenAccent)
                    )
                }
            }
        }
    }
}
