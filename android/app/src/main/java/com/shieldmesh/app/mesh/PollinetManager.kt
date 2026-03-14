package com.shieldmesh.app.mesh

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import xyz.pollinet.sdk.BleService
import xyz.pollinet.sdk.PolliNetSDK
import xyz.pollinet.sdk.SdkConfig
import javax.inject.Singleton

data class MeshMetrics(
    val peersConnected: Int = 0,
    val messagesRouted: Long = 0,
    val uptime: Long = 0
)

data class QueueSizes(
    val outbound: Int = 0,
    val received: Int = 0
)

@Singleton
class PollinetManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "PollinetManager"
        private const val TICK_INTERVAL_MS = 1000L
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var sdk: PolliNetSDK? = null
    private var tickJob: Job? = null
    private var isBleServiceBound = false

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _meshMetrics = MutableStateFlow(MeshMetrics())
    val meshMetrics: StateFlow<MeshMetrics> = _meshMetrics.asStateFlow()

    private val _outboundQueueSize = MutableStateFlow(0)
    val outboundQueueSize: StateFlow<Int> = _outboundQueueSize.asStateFlow()

    private val _receivedQueueSize = MutableStateFlow(0)
    val receivedQueueSize: StateFlow<Int> = _receivedQueueSize.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBleServiceBound = true
            Log.d(TAG, "BleService connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBleServiceBound = false
            Log.d(TAG, "BleService disconnected")
        }
    }

    /**
     * Initialize the PolliNet SDK and bind the BLE foreground service.
     * Must be called before any other operations.
     */
    fun initialize(): Result<Unit> {
        return try {
            val storageDir = context.filesDir.resolve("pollinet").also { it.mkdirs() }
            val config = SdkConfig(
                rpcUrl = "https://api.devnet.solana.com",
                enableLogging = true,
                storageDirectory = storageDir.absolutePath,
            )
            sdk = PolliNetSDK.initialize(config).getOrThrow()
            _isInitialized.value = true

            // Bind BLE foreground service
            val intent = Intent(context, BleService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

            // Start the tick coroutine
            startTick()

            Log.d(TAG, "PolliNet SDK initialized successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize PolliNet SDK", e)
            _isInitialized.value = false
            Result.failure(e)
        }
    }

    /**
     * Start the periodic tick coroutine that handles retries and timeouts.
     */
    private fun startTick() {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                sdk?.tick()?.onFailure { e ->
                    Log.w(TAG, "Tick failure (non-fatal): ${e.message}")
                }
                refreshMetrics()
                delay(TICK_INTERVAL_MS)
            }
        }
    }

    /**
     * Refresh all observable metrics from the SDK.
     */
    private suspend fun refreshMetrics() {
        val currentSdk = sdk ?: return

        currentSdk.metrics().onSuccess { metrics ->
            _meshMetrics.value = MeshMetrics(
                peersConnected = metrics.peersConnected,
                messagesRouted = metrics.messagesRouted,
                uptime = metrics.uptime
            )
        }.onFailure { e ->
            Log.w(TAG, "Failed to fetch metrics: ${e.message}")
        }

        currentSdk.getOutboundQueueSize().onSuccess { size ->
            _outboundQueueSize.value = size
        }

        currentSdk.getReceivedQueueSize().onSuccess { size ->
            _receivedQueueSize.value = size
        }
    }

    /**
     * Prepare offline transaction bundles while online.
     * These bundles allow transactions to be created later without internet.
     *
     * @param count Number of bundles to prepare
     * @param senderKeypairBytes The sender's wallet keypair as bytes
     */
    suspend fun prepareOfflineBundle(count: Int, senderKeypairBytes: ByteArray): Result<Any> {
        val currentSdk = sdk ?: return Result.failure(
            IllegalStateException("PolliNet SDK not initialized")
        )
        return try {
            val bundle = currentSdk.prepareOfflineBundle(
                count = count,
                senderKeypair = senderKeypairBytes
            ).getOrThrow()
            Log.d(TAG, "Prepared $count offline bundles")
            Result.success(bundle)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prepare offline bundle", e)
            Result.failure(e)
        }
    }

    /**
     * Queue a signed transaction for transmission over the BLE mesh network.
     *
     * @param signedTxBase64 Base64-encoded signed transaction
     * @return Transaction ID on success
     */
    suspend fun queueTransaction(signedTxBase64: String): Result<String> {
        val currentSdk = sdk ?: return Result.failure(
            IllegalStateException("PolliNet SDK not initialized")
        )
        return try {
            val txId = currentSdk.acceptAndQueueExternalTransaction(
                base64SignedTx = signedTxBase64
            ).getOrThrow()
            Log.d(TAG, "Transaction queued: $txId")
            Result.success(txId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to queue transaction", e)
            Result.failure(e)
        }
    }

    /**
     * Get current mesh network metrics.
     */
    suspend fun getMetrics(): Result<MeshMetrics> {
        val currentSdk = sdk ?: return Result.failure(
            IllegalStateException("PolliNet SDK not initialized")
        )
        return currentSdk.metrics().map { metrics ->
            MeshMetrics(
                peersConnected = metrics.peersConnected,
                messagesRouted = metrics.messagesRouted,
                uptime = metrics.uptime
            )
        }
    }

    /**
     * Get current queue sizes for outbound and received queues.
     */
    suspend fun getQueueSizes(): Result<QueueSizes> {
        val currentSdk = sdk ?: return Result.failure(
            IllegalStateException("PolliNet SDK not initialized")
        )
        return try {
            val outbound = currentSdk.getOutboundQueueSize().getOrThrow()
            val received = currentSdk.getReceivedQueueSize().getOrThrow()
            Result.success(QueueSizes(outbound = outbound, received = received))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get the number of received transactions waiting to be processed.
     */
    suspend fun getReceivedQueueSize(): Result<Int> {
        val currentSdk = sdk ?: return Result.failure(
            IllegalStateException("PolliNet SDK not initialized")
        )
        return currentSdk.getReceivedQueueSize()
    }

    /**
     * Shut down the SDK, cancel the tick job, and unbind the BLE service.
     */
    fun shutdown() {
        tickJob?.cancel()
        tickJob = null

        sdk?.shutdown()
        sdk = null
        _isInitialized.value = false

        if (isBleServiceBound) {
            try {
                context.unbindService(serviceConnection)
            } catch (e: Exception) {
                Log.w(TAG, "Error unbinding BleService", e)
            }
            isBleServiceBound = false
        }

        _meshMetrics.value = MeshMetrics()
        _outboundQueueSize.value = 0
        _receivedQueueSize.value = 0

        Log.d(TAG, "PolliNet SDK shut down")
    }
}
