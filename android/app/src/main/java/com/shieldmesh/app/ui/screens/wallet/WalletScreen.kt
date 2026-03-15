package com.shieldmesh.app.ui.screens.wallet

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.data.remote.TransactionRecord
import com.shieldmesh.app.ui.components.GlassCard
import com.shieldmesh.app.ui.components.GradientDivider
import com.shieldmesh.app.ui.components.SectionHeader
import com.shieldmesh.app.ui.theme.CardBackground
import com.shieldmesh.app.ui.theme.CardBorder
import com.shieldmesh.app.ui.theme.CriticalRed
import com.shieldmesh.app.ui.theme.CyanAccent
import com.shieldmesh.app.ui.theme.DarkBackground
import com.shieldmesh.app.ui.theme.GradientPurpleEnd
import com.shieldmesh.app.ui.theme.GradientPurpleStart
import com.shieldmesh.app.ui.theme.GreenAccent
import com.shieldmesh.app.ui.theme.MonospaceFamily
import com.shieldmesh.app.ui.theme.PurpleAccent
import com.shieldmesh.app.ui.theme.TextMuted
import com.shieldmesh.app.ui.theme.TextPrimary
import com.shieldmesh.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val pubkey by viewModel.walletPubkey.collectAsState()
    val balanceSol by viewModel.balanceSol.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val clipboardManager = LocalClipboardManager.current

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
                title = "Wallet",
                subtitle = "Manage your Solana wallet",
                icon = Icons.Default.Wallet,
                accentColor = PurpleAccent
            )
        }

        if (pubkey == null) {
            // Not connected - premium empty state
            item {
                GlassCard(
                    glowColor = PurpleAccent,
                    borderColor = PurpleAccent.copy(alpha = 0.2f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Glowing wallet icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            PurpleAccent.copy(alpha = 0.15f),
                                            CyanAccent.copy(alpha = 0.08f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = PurpleAccent,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "No Wallet Connected",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connect your Solana wallet to report threats, stake SOL, and earn bounty rewards.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(28.dp))

                        // Premium connect button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            GradientPurpleStart,
                                            GradientPurpleEnd
                                        )
                                    )
                                )
                        ) {
                            Button(
                                onClick = { viewModel.connectDemoWallet() },
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    Icons.Default.Wallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Connect Wallet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Connected - premium wallet card
            item {
                GlassCard(
                    glowColor = PurpleAccent,
                    borderColor = GreenAccent.copy(alpha = 0.2f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Status row
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
                                        .background(GreenAccent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Connected",
                                    color = GreenAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.disconnectWallet() },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    CriticalRed.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = ButtonDefaults.ContentPadding
                            ) {
                                Icon(
                                    Icons.Default.LinkOff,
                                    contentDescription = null,
                                    tint = CriticalRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Disconnect",
                                    color = CriticalRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Address
                        Text(
                            text = "ADDRESS",
                            color = TextMuted,
                            fontFamily = MonospaceFamily,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkBackground.copy(alpha = 0.5f))
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = pubkey!!,
                                fontFamily = MonospaceFamily,
                                color = CyanAccent,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f),
                                lineHeight = 16.sp
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(pubkey!!))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        GradientDivider(color = PurpleAccent.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Balance - large and centered
                        Text(
                            text = "BALANCE",
                            color = TextMuted,
                            fontFamily = MonospaceFamily,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = String.format("%.4f", balanceSol),
                                fontFamily = MonospaceFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 36.sp,
                                color = TextPrimary,
                                letterSpacing = (-1).sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SOL",
                                fontFamily = MonospaceFamily,
                                color = PurpleAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 5.dp),
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Refresh button
                        OutlinedButton(
                            onClick = { viewModel.refreshBalance() },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                CyanAccent.copy(alpha = 0.25f)
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
                                "Refresh Balance",
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Transaction history
            item {
                GradientDivider(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = "Transaction History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    letterSpacing = (-0.2).sp
                )
            }

            if (transactions.isEmpty()) {
                item {
                    GlassCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Wallet,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No transactions yet",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Stake SOL or report threats to see activity",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(transactions) { tx ->
                    TransactionCard(tx = tx)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TransactionCard(tx: TransactionRecord) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(tx.timestamp))
    val isReceive = tx.type == "receive"
    val txColor = if (isReceive) GreenAccent else CriticalRed
    val txIcon = if (isReceive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

    GlassCard(borderColor = txColor.copy(alpha = 0.1f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(txColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        txIcon,
                        contentDescription = null,
                        tint = txColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tx.signature.take(16) + "...",
                        fontFamily = MonospaceFamily,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(text = dateStr, color = TextMuted, fontSize = 10.sp)
                }
            }
            Text(
                text = "${if (isReceive) "+" else "-"}${String.format("%.4f", tx.amount)}",
                fontFamily = MonospaceFamily,
                fontWeight = FontWeight.Bold,
                color = txColor,
                fontSize = 15.sp
            )
        }
    }
}
