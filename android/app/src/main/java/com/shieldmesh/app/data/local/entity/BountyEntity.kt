package com.shieldmesh.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bounties")
data class BountyEntity(
    @PrimaryKey
    val id: String,
    val threatId: String,
    val reporter: String,
    val amount: Double,
    val claimed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
