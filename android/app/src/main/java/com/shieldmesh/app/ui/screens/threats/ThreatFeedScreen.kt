package com.shieldmesh.app.ui.screens.threats

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.data.local.entity.Severity
import com.shieldmesh.app.data.local.entity.SyncStatus
import com.shieldmesh.app.data.local.entity.ThreatEntity
import com.shieldmesh.app.ui.components.GlassCard
import com.shieldmesh.app.ui.components.GradientDivider
import com.shieldmesh.app.ui.components.SectionHeader
import com.shieldmesh.app.ui.components.StatusBadge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreatFeedScreen(
    viewModel: ThreatFeedViewModel = hiltViewModel()
) {
    val threats by viewModel.filteredThreats.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Threat Feed",
                subtitle = "${threats.size} threats reported",
                icon = Icons.Default.BugReport,
                accentColor = CriticalRed
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Filter chips - scrollable row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChipPremium("All", selectedFilter == "all") { viewModel.setFilter("all") }
                }
                item {
                    FilterChipPremium("Critical", selectedFilter == "CRITICAL", CriticalRed) { viewModel.setFilter("CRITICAL") }
                }
                item {
                    FilterChipPremium("High", selectedFilter == "HIGH", HighOrange) { viewModel.setFilter("HIGH") }
                }
                item {
                    FilterChipPremium("Medium", selectedFilter == "MEDIUM", MediumYellow) { viewModel.setFilter("MEDIUM") }
                }
                item {
                    FilterChipPremium("Low", selectedFilter == "LOW", LowGreen) { viewModel.setFilter("LOW") }
                }
            }
        }

        if (threats.isEmpty()) {
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
                            text = "No threats match the current filter",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(threats, key = { it.id }) { threat ->
                ThreatCard(threat = threat)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun FilterChipPremium(
    label: String,
    selected: Boolean,
    accentColor: Color = CyanAccent,
    onClick: () -> Unit
) {
    val chipColor = if (selected) accentColor else Color.Transparent
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        },
        leadingIcon = if (selected && label != "All") {
            {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = CardBackground,
            selectedContainerColor = chipColor.copy(alpha = 0.12f),
            labelColor = TextSecondary,
            selectedLabelColor = accentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = CardBorder,
            selectedBorderColor = chipColor.copy(alpha = 0.35f),
            enabled = true,
            selected = selected
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun ThreatCard(threat: ThreatEntity) {
    val severityColor = when (threat.severity) {
        Severity.CRITICAL -> CriticalRed
        Severity.HIGH -> HighOrange
        Severity.MEDIUM -> MediumYellow
        Severity.LOW -> LowGreen
    }

    val syncColor = when (threat.syncStatus) {
        SyncStatus.SYNCED -> GreenAccent
        SyncStatus.PENDING -> MediumYellow
        SyncStatus.FAILED -> CriticalRed
    }

    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(threat.timestamp))

    GlassCard(
        glowColor = severityColor,
        borderColor = severityColor.copy(alpha = 0.2f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: severity + AI score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Severity indicator bar
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = 28.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(severityColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = threat.severity.name,
                            color = severityColor,
                            fontFamily = MonospaceFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = dateStr,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
                StatusBadge(
                    text = "AI: ${threat.aiScore}",
                    color = severityColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // URL
            Text(
                text = threat.url,
                fontFamily = MonospaceFamily,
                color = TextPrimary,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = threat.description,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            GradientDivider(color = severityColor.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = threat.hash.take(12) + "...",
                    fontFamily = MonospaceFamily,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${threat.validatorCount} validators",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(syncColor)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = threat.syncStatus.name,
                        color = syncColor,
                        fontFamily = MonospaceFamily,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
