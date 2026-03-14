package com.shieldmesh.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SyncStatus {
    PENDING, SYNCED, FAILED
}

enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Entity(tableName = "threats")
data class ThreatEntity(
    @PrimaryKey
    val id: String,
    val hash: String,
    val severity: Severity,
    val aiScore: Int,
    val reporterPubkey: String,
    val validatorCount: Int = 0,
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis(),
    val description: String,
    val url: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
