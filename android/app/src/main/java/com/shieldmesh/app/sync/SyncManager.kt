package com.shieldmesh.app.sync

import com.shieldmesh.app.data.repository.ThreatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val pendingCount: Int = 0,
    val lastSyncResult: String = ""
)

@Singleton
class SyncManager @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    private val threatRepository: ThreatRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun startObserving() {
        scope.launch {
            connectivityObserver.observe.collect { status ->
                when (status) {
                    ConnectivityStatus.AVAILABLE -> {
                        triggerSync()
                    }
                    ConnectivityStatus.LOST, ConnectivityStatus.UNAVAILABLE -> {
                        _syncState.value = _syncState.value.copy(
                            lastSyncResult = "Offline - changes queued locally"
                        )
                    }
                    ConnectivityStatus.LOSING -> {
                        // No action needed
                    }
                }
            }
        }
    }

    suspend fun triggerSync() {
        if (_syncState.value.isSyncing) return

        _syncState.value = _syncState.value.copy(isSyncing = true)

        try {
            val synced = threatRepository.syncPendingThreats()
            _syncState.value = SyncState(
                isSyncing = false,
                lastSyncTimestamp = System.currentTimeMillis(),
                pendingCount = 0,
                lastSyncResult = if (synced > 0) "Synced $synced threats to Solana" else "All up to date"
            )
        } catch (e: Exception) {
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncResult = "Sync failed: ${e.message}"
            )
        }
    }

    suspend fun retryFailed() {
        threatRepository.retryFailedSync()
        triggerSync()
    }
}
