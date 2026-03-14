package com.shieldmesh.app.data.repository

import com.shieldmesh.app.mesh.PollinetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mesh status data backed by real Pollinet SDK metrics.
 */
data class MeshStatus(
    val peersConnected: Int = 0,
    val threatsRelayed: Int = 0,
    val isActive: Boolean = false,
    val networkId: String = "",
    val lastSyncTimestamp: Long = 0L,
    val outboundQueueSize: Int = 0,
    val receivedQueueSize: Int = 0,
    val messagesRouted: Long = 0
)

interface MeshNetworkProvider {
    fun start()
    fun stop()
    fun broadcastThreat(threatId: String, threatHash: String)
    fun getStatus(): MeshStatus
}

@Singleton
class MeshRepository @Inject constructor(
    private val pollinetManager: PollinetManager
) : MeshNetworkProvider {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _meshStatus = MutableStateFlow(MeshStatus())
    val meshStatus: StateFlow<MeshStatus> = _meshStatus.asStateFlow()

    private val _peerMessages = MutableStateFlow<List<String>>(emptyList())
    val peerMessages: StateFlow<List<String>> = _peerMessages.asStateFlow()

    init {
        // Observe PollinetManager state flows and combine them into MeshStatus
        scope.launch {
            combine(
                pollinetManager.isInitialized,
                pollinetManager.meshMetrics,
                pollinetManager.outboundQueueSize,
                pollinetManager.receivedQueueSize
            ) { initialized, metrics, outbound, received ->
                MeshStatus(
                    peersConnected = metrics.peersConnected,
                    threatsRelayed = outbound, // outbound items are threats queued for relay
                    isActive = initialized,
                    networkId = if (initialized) "shieldmesh-mainnet-v1" else "",
                    lastSyncTimestamp = if (initialized) System.currentTimeMillis() else 0L,
                    outboundQueueSize = outbound,
                    receivedQueueSize = received,
                    messagesRouted = metrics.messagesRouted
                )
            }.collect { status ->
                _meshStatus.value = status
            }
        }
    }

    override fun start() {
        pollinetManager.initialize().onFailure { e ->
            _peerMessages.value = _peerMessages.value + "Failed to start mesh: ${e.message}"
        }.onSuccess {
            _peerMessages.value = _peerMessages.value + "Mesh network started"
        }
    }

    override fun stop() {
        pollinetManager.shutdown()
        _peerMessages.value = _peerMessages.value + "Mesh network stopped"
    }

    override fun broadcastThreat(threatId: String, threatHash: String) {
        scope.launch {
            // Queue the threat hash as a signed transaction placeholder for BLE relay.
            // In production, this would be a properly signed Solana transaction.
            pollinetManager.queueTransaction(threatHash).onSuccess { txId ->
                _peerMessages.value = _peerMessages.value +
                    "Threat $threatId queued for mesh relay (tx: $txId)"
            }.onFailure { e ->
                _peerMessages.value = _peerMessages.value +
                    "Failed to broadcast threat $threatId: ${e.message}"
            }
        }
    }

    override fun getStatus(): MeshStatus = _meshStatus.value

    /**
     * Queue a fully signed transaction for BLE mesh transmission.
     */
    suspend fun queueSignedTransaction(signedTxBase64: String): Result<String> {
        return pollinetManager.queueTransaction(signedTxBase64)
    }

    /**
     * Prepare offline bundles while internet is available.
     */
    suspend fun prepareOfflineBundles(count: Int, senderKeypairBytes: ByteArray): Result<Any> {
        return pollinetManager.prepareOfflineBundle(count, senderKeypairBytes)
    }
}
