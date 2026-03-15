package com.shieldmesh.app.ui.screens.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldmesh.app.ai.IndicatorCategory
import com.shieldmesh.app.ai.ThreatResult
import com.shieldmesh.app.data.local.entity.Severity
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
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel()
) {
    val input by viewModel.input.collectAsState()
    val scanMode by viewModel.scanMode.collectAsState()
    val selectedFileName by viewModel.selectedFileName.collectAsState()
    val selectedFileSize by viewModel.selectedFileSize.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val result by viewModel.scanResult.collectAsState()
    val reported by viewModel.reported.collectAsState()

    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            var fileName = "unknown"
            var fileSize = 0L
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = c.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (nameIndex >= 0) fileName = c.getString(nameIndex)
                    if (sizeIndex >= 0) fileSize = c.getLong(sizeIndex)
                }
            }

            // Read file content as text
            val content = try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BufferedReader(InputStreamReader(stream)).use { reader ->
                        val sb = StringBuilder()
                        var line: String?
                        var lines = 0
                        while (reader.readLine().also { l -> line = l } != null && lines < 5000) {
                            sb.appendLine(line)
                            lines++
                        }
                        sb.toString()
                    }
                } ?: ""
            } catch (_: Exception) {
                ""
            }

            viewModel.onFileSelected(fileName, fileSize, content)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Threat Scanner",
                subtitle = "Analyze URLs, messages, and files",
                icon = Icons.Default.Security,
                accentColor = CyanAccent
            )
        }

        // Mode tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Text/URL tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (scanMode == ScanMode.TEXT) GreenAccent.copy(alpha = 0.1f)
                            else CardBackground
                        )
                        .border(
                            1.dp,
                            if (scanMode == ScanMode.TEXT) GreenAccent.copy(alpha = 0.3f) else CardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setScanMode(ScanMode.TEXT) }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = if (scanMode == ScanMode.TEXT) GreenAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "URL / Text",
                            color = if (scanMode == ScanMode.TEXT) GreenAccent else TextSecondary,
                            fontWeight = if (scanMode == ScanMode.TEXT) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                // File tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (scanMode == ScanMode.FILE) CyanAccent.copy(alpha = 0.1f)
                            else CardBackground
                        )
                        .border(
                            1.dp,
                            if (scanMode == ScanMode.FILE) CyanAccent.copy(alpha = 0.3f) else CardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setScanMode(ScanMode.FILE) }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = if (scanMode == ScanMode.FILE) CyanAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "File Upload",
                            color = if (scanMode == ScanMode.FILE) CyanAccent else TextSecondary,
                            fontWeight = if (scanMode == ScanMode.FILE) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Input area based on mode
        if (scanMode == ScanMode.TEXT) {
            // Text input
            item {
                GlassCard(
                    borderColor = if (input.isNotBlank()) CyanAccent.copy(alpha = 0.3f) else CardBorder
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ENTER TARGET",
                            color = TextMuted,
                            fontFamily = MonospaceFamily,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = input,
                            onValueChange = viewModel::onInputChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "https://suspicious-site.xyz/login...",
                                    color = TextMuted,
                                    fontFamily = MonospaceFamily,
                                    fontSize = 13.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent.copy(alpha = 0.5f),
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = CyanAccent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = MonospaceFamily,
                                color = TextPrimary,
                                fontSize = 14.sp
                            ),
                            minLines = 3,
                            maxLines = 6,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Scan button
            item {
                ScanButton(
                    enabled = input.isNotBlank() && !isScanning,
                    isScanning = isScanning,
                    label = "Scan for Threats",
                    scanningLabel = "Analyzing...",
                    accentColor = CyanAccent,
                    onClick = viewModel::scan
                )
            }
        } else {
            // File upload area
            item {
                if (selectedFileName != null) {
                    // File selected - show info
                    GlassCard(
                        glowColor = CyanAccent,
                        borderColor = CyanAccent.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CyanAccent.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedFileName!!,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatFileSize(selectedFileSize),
                                        color = TextMuted,
                                        fontFamily = MonospaceFamily,
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.clearFile() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove file",
                                        tint = CriticalRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // File picker area
                    GlassCard(
                        borderColor = CardBorder
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filePickerLauncher.launch("*/*") }
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(CyanAccent.copy(alpha = 0.08f))
                                    .border(1.dp, CyanAccent.copy(alpha = 0.15f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.UploadFile,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tap to select a file",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Text files, scripts, documents, configs",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // File scan button
            if (selectedFileName != null) {
                item {
                    ScanButton(
                        enabled = !isScanning,
                        isScanning = isScanning,
                        label = "Scan File",
                        scanningLabel = "Scanning File...",
                        accentColor = CyanAccent,
                        onClick = viewModel::scan
                    )
                }
            }
        }

        // Result display
        if (result != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(500)) { it / 3 }
                ) {
                    ScanResultCard(
                        result = result!!,
                        reported = reported,
                        onReport = viewModel::reportThreat
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun ScanButton(
    enabled: Boolean,
    isScanning: Boolean,
    label: String,
    scanningLabel: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled) Brush.linearGradient(
                    colors = listOf(accentColor, accentColor.copy(alpha = 0.8f))
                )
                else Brush.linearGradient(
                    colors = listOf(CardBorder, CardBorder)
                )
            )
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = DarkBackground,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isScanning) {
                val infiniteTransition = rememberInfiniteTransition(label = "scan")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotation),
                    color = TextMuted,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(scanningLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            } else {
                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${String.format("%.1f", bytes / 1024.0)} KB"
        else -> "${String.format("%.1f", bytes / (1024.0 * 1024.0))} MB"
    }
}

@Composable
private fun ScanResultCard(result: ThreatResult, reported: Boolean, onReport: () -> Unit) {
    val severityColor = when (result.severity) {
        Severity.CRITICAL -> CriticalRed
        Severity.HIGH -> HighOrange
        Severity.MEDIUM -> MediumYellow
        Severity.LOW -> LowGreen
    }

    val statusIcon = if (result.safe) Icons.Default.CheckCircle else Icons.Default.Warning

    GlassCard(
        glowColor = severityColor,
        borderColor = severityColor.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(severityColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = result.severity.name,
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonospaceFamily,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(severityColor.copy(alpha = 0.1f))
                        .border(1.dp, severityColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${result.score}/100",
                        color = severityColor,
                        fontFamily = MonospaceFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            GradientDivider(color = severityColor)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = result.description,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            if (result.indicators.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "INDICATORS",
                        color = TextMuted,
                        fontFamily = MonospaceFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(text = "${result.indicators.size}", color = severityColor)
                }
                Spacer(modifier = Modifier.height(12.dp))

                result.indicators.forEach { indicator ->
                    val catColor = when (indicator.category) {
                        IndicatorCategory.PHISHING -> CriticalRed
                        IndicatorCategory.SOCIAL_ENGINEERING -> HighOrange
                        IndicatorCategory.CRYPTO_SCAM -> MediumYellow
                        IndicatorCategory.TECHNICAL -> CyanAccent
                        IndicatorCategory.URL_STRUCTURE -> TextSecondary
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(catColor.copy(alpha = 0.04f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(catColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = indicator.label,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+${indicator.weight}",
                            color = catColor,
                            fontFamily = MonospaceFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Report button
            if (!result.safe) {
                Spacer(modifier = Modifier.height(20.dp))
                if (reported) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GreenAccent.copy(alpha = 0.06f))
                            .border(1.dp, GreenAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Reported to ShieldMesh", color = GreenAccent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassCard(borderColor = CyanAccent.copy(alpha = 0.2f)) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Queued for Pollinet mesh relay", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Broadcasting to nearby peers via BLE", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = onReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CriticalRed.copy(alpha = 0.12f),
                            contentColor = CriticalRed
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Report, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Report to ShieldMesh Network", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
