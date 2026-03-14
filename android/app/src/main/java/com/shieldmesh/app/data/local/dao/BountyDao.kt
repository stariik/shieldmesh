package com.shieldmesh.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shieldmesh.app.data.local.entity.BountyEntity
import com.shieldmesh.app.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BountyDao {
    @Query("SELECT * FROM bounties ORDER BY timestamp DESC")
    fun getAll(): Flow<List<BountyEntity>>

    @Query("SELECT * FROM bounties WHERE id = :id")
    suspend fun getById(id: String): BountyEntity?

    @Query("SELECT * FROM bounties WHERE claimed = 0 ORDER BY amount DESC")
    fun getUnclaimed(): Flow<List<BountyEntity>>

    @Query("SELECT * FROM bounties WHERE syncStatus = :syncStatus")
    suspend fun getBySyncStatus(syncStatus: SyncStatus): List<BountyEntity>

    @Query("SELECT SUM(amount) FROM bounties WHERE claimed = 0")
    fun getTotalPool(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bounty: BountyEntity)

    @Update
    suspend fun update(bounty: BountyEntity)

    @Delete
    suspend fun delete(bounty: BountyEntity)
}
