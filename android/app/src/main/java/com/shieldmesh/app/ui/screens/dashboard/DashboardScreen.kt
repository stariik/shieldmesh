package com.shieldmesh.app.ui.screens.dashboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.data.local.entity.Severity
import com.shieldmesh.app.data.local.entity.ThreatEntity
import com.shieldmesh.app.ui.components.GlassCard
import com.shieldmesh.app.ui.components.GradientDivider
import com.shieldmesh.app.ui.components.PulsingDot
import com.shieldmesh.app.ui.components.StatCardPremium
import com.shieldmesh.app.ui.components.StatusBadge
import com.shieldmesh.app.ui.theme.CardBackground
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.CriticalRed
import com.shieldmesh.app.ui.theme.CyanAccent
import com.shieldmesh.app.ui.theme.DarkBackground
import com.shieldmesh.app.ui.theme.GlowGreen
import com.shieldmesh.app.ui.theme.GradientGreenEnd
import com.shieldmesh.app.ui.theme.GradientGreenStart
import com.shieldmesh.app.ui.theme.GreenAccent
import com.shieldmesh.app.ui.theme.HighOrange
import com.shieldmesh.app.ui.theme.LowGreen
import com.shieldmesh.app.ui.theme.MediumYellow
import com.shieldmesh.app.ui.theme.MonospaceFamily
import com.shieldmesh.app.ui.theme.TextMuted
import com.shieldmesh.app.ui.theme.TextPrimary
import com.shieldmesh.app.ui.theme.TextSecondary
import com.shieldmesh.app.sync.ConnectivityStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToScan: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val totalThreats by viewModel.totalThreats.collectAsState()
    val verifiedThreats by viewModel.verifiedThreats.collectAsState()
    val poolBalance by viewModel.poolBalance.collectAsState()
    val meshPeers by viewModel.meshPeers.collectAsState()
    val recentThreats by viewModel.recentThreats.collectAsState()
    val connectivity by viewModel.connectivityStatus.collectAsState()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero header with gradient
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                GreenAccent.copy(alpha = 0.08f),
                                CyanAccent.copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shield logo with glow
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        GreenAccent.copy(alpha = 0.12f),
                                        CyanAccent.copy(alpha = 0.06f)
                                    )
                                )
                            )
                            .drawBehind {
                                drawRoundRect(
                                    color = GreenAccent.copy(alpha = 0.15f),
                                    cornerRadius = CornerRadius(16.dp.toPx()),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 1.dp.toPx()
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Shield",
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Mesh",
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = GreenAccent,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Text(
                            text = "DECENTRALIZED THREAT INTELLIGENCE",
                            fontSize = 9.sp,
                            color = TextMuted,
                            letterSpacing = 2.sp,
                            fontFamily = MonospaceFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Connectivity status banner
        if (connectivity != ConnectivityStatus.AVAILABLE) {
            item {
                GlassCard(
                    glowColor = CriticalRed,
                    borderColor = CriticalRed.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PulsingDot(color = CriticalRed)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Offline Mode",
                                color = CriticalRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Relaying via Pollinet mesh",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        if (pendingSyncCount > 0) {
                            StatusBadge(
                                text = "$pendingSyncCount queued",
                                color = CriticalRed
                            )
                        }
                    }
                }
            }
        } else if (pendingSyncCount > 0) {
            item {
                GlassCard(
                    glowColor = MediumYellow,
                    borderColor = MediumYellow.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PulsingDot(color = MediumYellow)
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Syncing $pendingSyncCount threat${if (pendingSyncCount > 1) "s" else ""}...",
                            color = MediumYellow,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Stats cards row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCardPremium(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.BugReport,
                    value = totalThreats.toString(),
                    label = "Total Threats",
                    accentColor = CyanAccent
                )
                StatCardPremium(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.VerifiedUser,
                    value = verifiedThreats.toString(),
                    label = "Verified",
                    accentColor = GreenAccent
                )
            }
        }

        // Stats cards row 2
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCardPremium(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Shield,
                    value = String.format("%.2f", poolBalance),
                    label = "Pool (SOL)",
                    accentColor = CyanAccent
                )
                StatCardPremium(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Hub,
                    value = meshPeers.toString(),
                    label = "Mesh Peers",
                    accentColor = GreenAccent
                )
            }
        }

        // Quick scan CTA button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                GradientGreenStart.copy(alpha = 0.15f),
                                GradientGreenEnd.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    GradientGreenStart.copy(alpha = 0.4f),
                                    GradientGreenEnd.copy(alpha = 0.2f)
                                )
                            ),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 1.dp.toPx()
                            )
                        )
                    }
                    .clickable { onNavigateToScan() }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GreenAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Quick Scan",
                            color = GreenAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Analyze URLs & messages for threats",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Recent threats
        item {
            GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Recent Threats",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary,
                letterSpacing = (-0.2).sp
            )
        }

        if (recentThreats.isEmpty()) {
            item {
                GlassCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "All clear",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "No threats reported yet. Use the scanner to get started.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(recentThreats.take(10)) { threat ->
                ThreatListItem(threat = threat)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun ThreatListItem(threat: ThreatEntity) {
    val severityColor = when (threat.severity) {
        Severity.CRITICAL -> CriticalRed
        Severity.HIGH -> HighOrange
        Severity.MEDIUM -> MediumYellow
        Severity.LOW -> LowGreen
    }

    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(threat.timestamp))

    GlassCard(
        glowColor = severityColor,
        borderColor = severityColor.copy(alpha = 0.2f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(severityColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = threat.severity.name,
                        color = severityColor,
                        fontFamily = MonospaceFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
                StatusBadge(
                    text = "Score: ${threat.aiScore}",
                    color = CyanAccent
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = threat.url.take(60) + if (threat.url.length > 60) "..." else "",
                fontFamily = MonospaceFamily,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateStr,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                when (threat.syncStatus.name) {
                                    "SYNCED" -> GreenAccent
                                    "PENDING" -> MediumYellow
                                    else -> CriticalRed
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = threat.syncStatus.name,
                        color = when (threat.syncStatus.name) {
                            "SYNCED" -> GreenAccent
                            "PENDING" -> MediumYellow
                            else -> CriticalRed
                        },
                        fontFamily = MonospaceFamily,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
