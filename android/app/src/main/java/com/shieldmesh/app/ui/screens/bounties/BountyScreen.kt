package com.shieldmesh.app.ui.screens.bounties

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.data.local.entity.BountyEntity
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
fun BountyScreen(
    viewModel: BountyViewModel = hiltViewModel()
) {
    val bounties by viewModel.bounties.collectAsState()
    val totalPool by viewModel.totalPool.collectAsState()
    val stakerCount by viewModel.stakerCount.collectAsState()
    val totalStaked by viewModel.totalStaked.collectAsState()

    var stakeAmount by remember { mutableStateOf("") }

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
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MediumYellow, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Bounties & Staking", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Text("Earn rewards for threat validation", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        // Pool Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PoolStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalance,
                    value = String.format("%.2f", totalPool),
                    label = "Bounty Pool (SOL)",
                    color = MediumYellow
                )
                PoolStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    value = String.format("%.2f", totalStaked),
                    label = "Total Staked (SOL)",
                    color = GreenAccent
                )
            }
        }

        item {
            PoolStatCard(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.People,
                value = stakerCount.toString(),
                label = "Active Validators",
                color = CyanAccent
            )
        }

        // Stake/Unstake panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Stake SOL",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Stake to become a validator and earn bounty rewards",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = stakeAmount,
                        onValueChange = { stakeAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Amount (SOL)", color = TextMuted) },
                        placeholder = { Text("0.00", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = GreenAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val amount = stakeAmount.toDoubleOrNull() ?: return@Button
                                viewModel.stake(amount)
                                stakeAmount = ""
                            },
                            modifier = Modifier.weight(1f),
                            enabled = stakeAmount.toDoubleOrNull() != null && (stakeAmount.toDoubleOrNull() ?: 0.0) > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenAccent,
                                contentColor = DarkBackground,
                                disabledContainerColor = CardBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Stake", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val amount = stakeAmount.toDoubleOrNull() ?: return@OutlinedButton
                                viewModel.unstake(amount)
                                stakeAmount = ""
                            },
                            modifier = Modifier.weight(1f),
                            enabled = stakeAmount.toDoubleOrNull() != null && (stakeAmount.toDoubleOrNull() ?: 0.0) > 0,
                            border = BorderStroke(1.dp, CriticalRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Unstake", color = CriticalRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Bounties header
        item {
            Text(
                text = "Active Bounties",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (bounties.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No active bounties. Report threats to create bounties.",
                        color = TextMuted,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(bounties, key = { it.id }) { bounty ->
                BountyCard(bounty = bounty)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun PoolStatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = value,
                    fontFamily = MonospaceFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(text = label, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun BountyCard(bounty: BountyEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(bounty.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, if (bounty.claimed) CardBorder else MediumYellow.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (bounty.claimed) TextMuted else MediumYellow)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (bounty.claimed) "Claimed" else "Active",
                        color = if (bounty.claimed) TextMuted else MediumYellow,
                        fontFamily = MonospaceFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Threat: ${bounty.threatId.take(12)}...",
                    fontFamily = MonospaceFamily,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(text = dateStr, color = TextMuted, fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.3f", bounty.amount),
                    fontFamily = MonospaceFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (bounty.claimed) TextMuted else GreenAccent,
                    fontSize = 16.sp
                )
                Text(text = "SOL", color = TextMuted, fontFamily = MonospaceFamily, fontSize = 10.sp)
            }
        }
    }
}
