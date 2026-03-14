package com.shieldmesh.app.ui.screens.mesh

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SendAndArchive
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Mesh Network", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Text("Pollinet P2P Threat Relay", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        // Mesh status card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(
                    1.dp,
                    if (meshStatus.isActive) GreenAccent.copy(alpha = 0.3f) else CardBorder
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
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
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (meshStatus.isActive) GreenAccent else CriticalRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (meshStatus.isActive) "ACTIVE" else "INACTIVE",
                                color = if (meshStatus.isActive) GreenAccent else CriticalRed,
                                fontFamily = MonospaceFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        if (meshStatus.networkId.isNotEmpty()) {
                            Text(
                                text = meshStatus.networkId,
                                fontFamily = MonospaceFamily,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MeshStatItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.People,
                            value = meshStatus.peersConnected.toString(),
                            label = "Peers",
                            color = CyanAccent
                        )
                        MeshStatItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.SendAndArchive,
                            value = meshStatus.threatsRelayed.toString(),
                            label = "Relayed",
                            color = GreenAccent
                        )
                    }

                    if (meshStatus.lastSyncTimestamp > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        Text(
                            text = "Last sync: ${dateFormat.format(Date(meshStatus.lastSyncTimestamp))}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (meshStatus.isActive) {
                            OutlinedButton(
                                onClick = { viewModel.stopMesh() },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, CriticalRed.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Stop Mesh", color = CriticalRed, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.startMesh() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenAccent,
                                    contentColor = DarkBackground
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Start Mesh", fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.simulatePeers() },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simulate", color = CyanAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Connection types info
        item {
            Text(
                text = "Connection Methods",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectionMethodCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Bluetooth,
                    name = "BLE",
                    status = "Ready",
                    statusColor = MediumYellow
                )
                ConnectionMethodCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Wifi,
                    name = "Wi-Fi Direct",
                    status = "Ready",
                    statusColor = MediumYellow
                )
                ConnectionMethodCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CellTower,
                    name = "Internet",
                    status = "Fallback",
                    statusColor = TextMuted
                )
            }
        }

        // Info card about Pollinet
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyanAccent.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pollinet Integration", color = CyanAccent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The mesh network uses Pollinet for peer-to-peer threat intelligence sharing. " +
                                "When online connectivity is unavailable, threats are shared via BLE and Wi-Fi Direct " +
                                "with nearby ShieldMesh nodes. This enables fully offline threat detection in the field.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pollinet SDK integration pending -- using stub implementation.",
                        color = MediumYellow,
                        fontFamily = MonospaceFamily,
                        fontSize = 10.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MeshStatItem(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary
            )
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun ConnectionMethodCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    status: String,
    statusColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(text = status, color = statusColor, fontFamily = MonospaceFamily, fontSize = 10.sp)
        }
    }
}
