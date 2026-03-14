package com.shieldmesh.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shieldmesh.app.data.local.dao.BountyDao
import com.shieldmesh.app.data.local.dao.StakerDao
import com.shieldmesh.app.data.local.dao.ThreatDao
import com.shieldmesh.app.data.local.entity.BountyEntity
import com.shieldmesh.app.data.local.entity.StakerEntity
import com.shieldmesh.app.data.local.entity.ThreatEntity

@Database(
    entities = [ThreatEntity::class, BountyEntity::class, StakerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun threatDao(): ThreatDao
    abstract fun bountyDao(): BountyDao
    abstract fun stakerDao(): StakerDao
}
