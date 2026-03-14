package com.shieldmesh.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shieldmesh.app.data.local.entity.StakerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StakerDao {
    @Query("SELECT * FROM stakers ORDER BY reputation DESC")
    fun getAll(): Flow<List<StakerEntity>>

    @Query("SELECT * FROM stakers WHERE pubkey = :pubkey")
    suspend fun getByPubkey(pubkey: String): StakerEntity?

    @Query("SELECT SUM(amount) FROM stakers")
    fun getTotalStaked(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM stakers")
    fun getStakerCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(staker: StakerEntity)

    @Update
    suspend fun update(staker: StakerEntity)

    @Delete
    suspend fun delete(staker: StakerEntity)
}
