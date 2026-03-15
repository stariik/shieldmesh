package com.shieldmesh.app.ui.screens.bounties

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.data.local.entity.BountyEntity
import com.shieldmesh.app.ui.components.GlassCard
import com.shieldmesh.app.ui.components.GradientDivider
import com.shieldmesh.app.ui.components.SectionHeader
import com.shieldmesh.app.ui.components.StatCardPremium
import com.shieldmesh.app.ui.components.StatusBadge
import com.shieldmesh.app.ui.theme.CardBackground
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.CriticalRed
import com.shieldmesh.app.ui.theme.CyanAccent
import com.shieldmesh.app.ui.theme.DarkBackground
import com.shieldmesh.app.ui.theme.GradientGoldEnd
import com.shieldmesh.app.ui.theme.GradientGoldStart
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
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Bounties & Staking",
                subtitle = "Earn rewards for threat validation",
                icon = Icons.Default.MonetizationOn,
                accentColor = MediumYellow
            )
        }

        // Pool Stats - row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCardPremium(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalance,
                    value = String.format("%.2f", totalPool),
                    label = "Bounty Pool (SOL)",
                    accentColor = MediumYellow
                )
                StatCardPremium(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    value = String.format("%.2f", totalStaked),
                    label = "Total Staked (SOL)",
                    accentColor = GreenAccent
                )
            }
        }

        // Validators stat
        item {
            StatCardPremium(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.People,
                value = stakerCount.toString(),
                label = "Active Validators",
                accentColor = CyanAccent
            )
        }

        // Stake/Unstake panel
        item {
            GlassCard(
                glowColor = GreenAccent,
                borderColor = GreenAccent.copy(alpha = 0.15f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreenAccent.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = GreenAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Stake SOL",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Become a validator and earn rewards",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = stakeAmount,
                        onValueChange = { stakeAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "0.00",
                                color = TextMuted,
                                fontFamily = MonospaceFamily
                            )
                        },
                        suffix = {
                            Text(
                                "SOL",
                                color = TextMuted,
                                fontFamily = MonospaceFamily,
                                fontSize = 12.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenAccent.copy(alpha = 0.5f),
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = GreenAccent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = MonospaceFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stake button with gradient feel
                        Button(
                            onClick = {
                                val amount = stakeAmount.toDoubleOrNull() ?: return@Button
                                viewModel.stake(amount)
                                stakeAmount = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = stakeAmount.toDoubleOrNull() != null && (stakeAmount.toDoubleOrNull() ?: 0.0) > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenAccent,
                                contentColor = DarkBackground,
                                disabledContainerColor = CardBorder,
                                disabledContentColor = TextMuted
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Stake", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val amount = stakeAmount.toDoubleOrNull() ?: return@OutlinedButton
                                viewModel.unstake(amount)
                                stakeAmount = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = stakeAmount.toDoubleOrNull() != null && (stakeAmount.toDoubleOrNull() ?: 0.0) > 0,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                CriticalRed.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Unstake",
                                color = CriticalRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Bounties
        item {
            GradientDivider(modifier = Modifier.padding(vertical = 2.dp))
            Text(
                text = "Active Bounties",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary,
                letterSpacing = (-0.2).sp
            )
        }

        if (bounties.isEmpty()) {
            item {
                GlassCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No active bounties",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Report threats to create bounties",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
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
private fun BountyCard(bounty: BountyEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(bounty.timestamp))
    val statusColor = if (bounty.claimed) TextMuted else MediumYellow

    GlassCard(
        glowColor = if (!bounty.claimed) MediumYellow else Color.Transparent,
        borderColor = if (bounty.claimed) CardBorder else MediumYellow.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (bounty.claimed) "Claimed" else "Active",
                        color = statusColor,
                        fontFamily = MonospaceFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Threat: ${bounty.threatId.take(12)}...",
                    fontFamily = MonospaceFamily,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = dateStr, color = TextMuted, fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.3f", bounty.amount),
                    fontFamily = MonospaceFamily,
                    fontWeight = FontWeight.Black,
                    color = if (bounty.claimed) TextMuted else GreenAccent,
                    fontSize = 18.sp
                )
                Text(
                    text = "SOL",
                    color = TextMuted,
                    fontFamily = MonospaceFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
