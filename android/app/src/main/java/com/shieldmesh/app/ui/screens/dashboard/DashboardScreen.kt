package com.shieldmesh.app.ui.screens.dashboard

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.data.local.entity.Severity
import com.shieldmesh.app.data.local.entity.ThreatEntity
import com.shieldmesh.app.ui.theme.CardBackground
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.CriticalRed
import com.shieldmesh.app.ui.theme.CyanAccent
import com.shieldmesh.app.ui.theme.DarkBackground
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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = GreenAccent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ShieldMesh",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Decentralized Threat Intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Connectivity status banner
        if (connectivity != ConnectivityStatus.AVAILABLE) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CriticalRed.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, CriticalRed.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(CriticalRed)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Offline Mode",
                                color = CriticalRed,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Threats queued locally. Relaying via Pollinet mesh.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        if (pendingSyncCount > 0) {
                            Text(
                                text = "$pendingSyncCount queued",
                                color = CriticalRed,
                                fontFamily = MonospaceFamily,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CriticalRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        } else if (pendingSyncCount > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MediumYellow.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, MediumYellow.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MediumYellow)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Syncing $pendingSyncCount threat${if (pendingSyncCount > 1) "s" else ""} to the network...",
                            color = MediumYellow,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
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
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.BugReport,
                    value = totalThreats.toString(),
                    label = "Total Threats",
                    accentColor = CyanAccent
                )
                StatCard(
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
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Shield,
                    value = String.format("%.2f", poolBalance),
                    label = "Pool (SOL)",
                    accentColor = CyanAccent
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Hub,
                    value = meshPeers.toString(),
                    label = "Mesh Peers",
                    accentColor = GreenAccent
                )
            }
        }

        // Quick scan button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScan() },
                colors = CardDefaults.cardColors(containerColor = GreenAccent.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, GreenAccent.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = GreenAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Quick Scan",
                        color = GreenAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Recent threats header
        item {
            Text(
                text = "Recent Threats",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (recentThreats.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No threats reported yet. Use the scanner to analyze URLs and messages.",
                        color = TextMuted,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
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

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = threat.severity.name,
                        color = severityColor,
                        fontFamily = MonospaceFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "Score: ${threat.aiScore}",
                    fontFamily = MonospaceFamily,
                    color = CyanAccent,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = threat.url.take(60) + if (threat.url.length > 60) "..." else "",
                fontFamily = MonospaceFamily,
                color = TextPrimary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateStr,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = threat.syncStatus.name,
                    color = when (threat.syncStatus.name) {
                        "SYNCED" -> GreenAccent
                        "PENDING" -> MediumYellow
                        else -> CriticalRed
                    },
                    fontFamily = MonospaceFamily,
                    fontSize = 11.sp
                )
            }
        }
    }
}
