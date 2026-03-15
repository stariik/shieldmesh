package com.shieldmesh.app.ui.screens.mesh

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.ui.components.GlassCard
import com.shieldmesh.app.ui.components.GradientDivider
import com.shieldmesh.app.ui.components.PulsingDot
import com.shieldmesh.app.ui.components.SectionHeader
import com.shieldmesh.app.ui.theme.CardBackground
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.CriticalRed
import com.shieldmesh.app.ui.theme.CyanAccent
import com.shieldmesh.app.ui.theme.DarkBackground
import com.shieldmesh.app.ui.theme.GreenAccent
import com.shieldmesh.app.ui.theme.MediumYellow
import com.shieldmesh.app.ui.theme.MonospaceFamily
import com.shieldmesh.app.ui.theme.TextMuted
import com.shieldmesh.app.ui.theme.TextPrimary
import com.shieldmesh.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeshScreen(
    viewModel: MeshViewModel = hiltViewModel()
) {
    val meshStatus by viewModel.meshStatus.collectAsState()
    val messages by viewModel.peerMessages.collectAsState()
    val meshMetrics by viewModel.meshMetrics.collectAsState()
    val outboundQueue by viewModel.outboundQueueSize.collectAsState()
    val receivedQueue by viewModel.receivedQueueSize.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Mesh Network",
                subtitle = "Pollinet P2P Threat Relay",
                icon = Icons.Default.Hub,
                accentColor = CyanAccent
            )
        }

        // Mesh status card
        item {
            val statusColor = if (meshStatus.isActive) GreenAccent else CriticalRed

            GlassCard(
                glowColor = statusColor,
                borderColor = statusColor.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Status header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PulsingDot(
                                color = statusColor,
                                size = 10.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (meshStatus.isActive) "ACTIVE" else "INACTIVE",
                                color = statusColor,
                                fontFamily = MonospaceFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.5.sp
                            )
                        }
                        if (meshStatus.networkId.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = meshStatus.networkId,
                                    fontFamily = MonospaceFamily,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats grid row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MeshStatItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.People,
                            value = meshMetrics.peersConnected.toString(),
                            label = "Peers",
                            color = CyanAccent
                        )
                        MeshStatItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Route,
                            value = meshMetrics.messagesRouted.toString(),
                            label = "Routed",
                            color = GreenAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stats grid row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MeshStatItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Outbox,
                            value = outboundQueue.toString(),
                            label = "Outbound",
                            color = MediumYellow
                        )
                        MeshStatItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inbox,
                            value = receivedQueue.toString(),
                            label = "Received",
                            color = CyanAccent
                        )
                    }

                    // Uptime & last sync
                    if (meshMetrics.uptime > 0 || meshStatus.lastSyncTimestamp > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        GradientDivider(color = statusColor.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (meshMetrics.uptime > 0) {
                                val uptimeMinutes = meshMetrics.uptime / 60
                                val uptimeSeconds = meshMetrics.uptime % 60
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${uptimeMinutes}m ${uptimeSeconds}s",
                                        color = TextMuted,
                                        fontFamily = MonospaceFamily,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            if (meshStatus.lastSyncTimestamp > 0) {
                                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                Text(
                                    text = "Sync: ${dateFormat.format(Date(meshStatus.lastSyncTimestamp))}",
                                    color = TextMuted,
                                    fontFamily = MonospaceFamily,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (meshStatus.isActive) {
                            OutlinedButton(
                                onClick = { viewModel.stopMesh() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    CriticalRed.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Stop Mesh",
                                    color = CriticalRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.startMesh() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenAccent,
                                    contentColor = DarkBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Start Mesh",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.refreshMetrics() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                CyanAccent.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Refresh",
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Connection Methods
        item {
            GradientDivider(modifier = Modifier.padding(vertical = 2.dp))
            Text(
                text = "Connection Methods",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary,
                letterSpacing = (-0.2).sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ConnectionMethodCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Bluetooth,
                    name = "BLE",
                    status = if (isInitialized) "Active" else "Ready",
                    statusColor = if (isInitialized) GreenAccent else MediumYellow,
                    accentColor = CyanAccent
                )
                ConnectionMethodCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Wifi,
                    name = "Wi-Fi Direct",
                    status = "Ready",
                    statusColor = MediumYellow,
                    accentColor = CyanAccent
                )
                ConnectionMethodCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CellTower,
                    name = "Internet",
                    status = "Fallback",
                    statusColor = TextMuted,
                    accentColor = TextSecondary
                )
            }
        }

        // Pollinet info card
        item {
            GlassCard(
                glowColor = CyanAccent,
                borderColor = CyanAccent.copy(alpha = 0.15f)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanAccent.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Pollinet Integration",
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "The mesh network uses Pollinet for peer-to-peer threat intelligence sharing. " +
                                "When online connectivity is unavailable, threats are shared via BLE and Wi-Fi Direct " +
                                "with nearby ShieldMesh nodes.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isInitialized) GreenAccent.copy(alpha = 0.06f)
                                else MediumYellow.copy(alpha = 0.06f)
                            )
                            .border(
                                1.dp,
                                if (isInitialized) GreenAccent.copy(alpha = 0.15f)
                                else MediumYellow.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isInitialized) "Pollinet SDK active -- BLE mesh operational."
                                   else "Pollinet SDK ready -- tap Start Mesh to connect.",
                            color = if (isInitialized) GreenAccent else MediumYellow,
                            fontFamily = MonospaceFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Activity Log
        if (messages.isNotEmpty()) {
            item {
                GradientDivider(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = "Activity Log",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    letterSpacing = (-0.2).sp
                )
            }

            item {
                GlassCard {
                    Column(modifier = Modifier.padding(14.dp)) {
                        messages.takeLast(10).reversed().forEachIndexed { index, message ->
                            if (index > 0) {
                                GradientDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = CardBorder.copy(alpha = 0.3f)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(CyanAccent.copy(alpha = 0.5f))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = message,
                                    color = TextSecondary,
                                    fontFamily = MonospaceFamily,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MeshStatItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.04f))
            .border(1.dp, color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ConnectionMethodCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    name: String,
    status: String,
    statusColor: Color,
    accentColor: Color
) {
    GlassCard(
        modifier = modifier,
        borderColor = statusColor.copy(alpha = 0.15f),
        cornerRadius = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = status,
                color = statusColor,
                fontFamily = MonospaceFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
