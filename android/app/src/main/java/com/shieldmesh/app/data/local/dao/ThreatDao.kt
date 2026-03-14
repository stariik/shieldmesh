package com.shieldmesh.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shieldmesh.app.data.local.entity.SyncStatus
import com.shieldmesh.app.data.local.entity.ThreatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatDao {
    @Query("SELECT * FROM threats ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ThreatEntity>>

    @Query("SELECT * FROM threats WHERE id = :id")
    suspend fun getById(id: String): ThreatEntity?

    @Query("SELECT * FROM threats WHERE status = :status ORDER BY timestamp DESC")
    fun getByStatus(status: String): Flow<List<ThreatEntity>>

    @Query("SELECT * FROM threats WHERE syncStatus = :syncStatus")
    suspend fun getBySyncStatus(syncStatus: SyncStatus): List<ThreatEntity>

    @Query("SELECT * FROM threats WHERE severity = :severity ORDER BY timestamp DESC")
    fun getBySeverity(severity: String): Flow<List<ThreatEntity>>

    @Query("SELECT COUNT(*) FROM threats")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM threats WHERE status = 'verified'")
    fun getVerifiedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(threat: ThreatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(threats: List<ThreatEntity>)

    @Update
    suspend fun update(threat: ThreatEntity)

    @Delete
    suspend fun delete(threat: ThreatEntity)

    @Query("DELETE FROM threats")
    suspend fun deleteAll()
}
