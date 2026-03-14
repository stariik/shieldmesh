package com.shieldmesh.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stakers")
data class StakerEntity(
    @PrimaryKey
    val pubkey: String,
    val amount: Double = 0.0,
    val reputation: Int = 0
)
