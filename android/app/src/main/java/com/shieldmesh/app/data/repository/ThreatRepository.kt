package com.shieldmesh.app.data.repository

import com.shieldmesh.app.data.local.dao.ThreatDao
import com.shieldmesh.app.data.local.entity.Severity
import com.shieldmesh.app.data.local.entity.SyncStatus
import com.shieldmesh.app.data.local.entity.ThreatEntity
import com.shieldmesh.app.data.remote.SolanaRpcClient
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreatRepository @Inject constructor(
    private val threatDao: ThreatDao,
    private val solanaRpcClient: SolanaRpcClient
) {
    fun getAllThreats(): Flow<List<ThreatEntity>> = threatDao.getAll()

    fun getThreatsByStatus(status: String): Flow<List<ThreatEntity>> = threatDao.getByStatus(status)

    fun getThreatsBySeverity(severity: String): Flow<List<ThreatEntity>> = threatDao.getBySeverity(severity)

    fun getTotalCount(): Flow<Int> = threatDao.getTotalCount()

    fun getVerifiedCount(): Flow<Int> = threatDao.getVerifiedCount()

    fun getPendingSyncCount(): Flow<Int> = threatDao.getPendingSyncCount()

    suspend fun getThreatById(id: String): ThreatEntity? = threatDao.getById(id)

    suspend fun reportThreat(
        url: String,
        description: String,
        severity: Severity,
        aiScore: Int,
        reporterPubkey: String
    ): ThreatEntity {
        val id = UUID.randomUUID().toString()
        val hash = sha256(url + description + System.currentTimeMillis())

        val threat = ThreatEntity(
            id = id,
            hash = hash,
            severity = severity,
            aiScore = aiScore,
            reporterPubkey = reporterPubkey,
            validatorCount = 0,
            status = "pending",
            timestamp = System.currentTimeMillis(),
            description = description,
            url = url,
            syncStatus = SyncStatus.PENDING
        )

        // Offline-first: save to Room immediately
        threatDao.insert(threat)

        return threat
    }

    suspend fun updateThreat(threat: ThreatEntity) {
        threatDao.update(threat)
    }

    /**
     * Sync pending threats to Solana blockchain.
     * Called by SyncManager when connectivity is restored.
     */
    suspend fun syncPendingThreats(): Int {
        val pending = threatDao.getBySyncStatus(SyncStatus.PENDING)
        var synced = 0

        for (threat in pending) {
            try {
                // In a full implementation, this would:
                // 1. Build a Solana transaction to the ShieldMesh program
                // 2. Sign it via Mobile Wallet Adapter
                // 3. Send it via RPC
                // For now, we mark as synced after attempting RPC connectivity check
                val blockhash = solanaRpcClient.getLatestBlockhash()
                if (blockhash.isNotEmpty()) {
                    threatDao.update(threat.copy(syncStatus = SyncStatus.SYNCED))
                    synced++
                }
            } catch (e: Exception) {
                threatDao.update(threat.copy(syncStatus = SyncStatus.FAILED))
            }
        }

        return synced
    }

    suspend fun retryFailedSync() {
        val failed = threatDao.getBySyncStatus(SyncStatus.FAILED)
        for (threat in failed) {
            threatDao.update(threat.copy(syncStatus = SyncStatus.PENDING))
        }
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
