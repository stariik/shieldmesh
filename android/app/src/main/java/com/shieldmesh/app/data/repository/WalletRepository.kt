package com.shieldmesh.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shieldmesh.app.data.local.dao.StakerDao
import com.shieldmesh.app.data.local.entity.StakerEntity
import com.shieldmesh.app.data.remote.SolanaRpcClient
import com.shieldmesh.app.data.remote.TransactionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val solanaRpcClient: SolanaRpcClient,
    private val stakerDao: StakerDao,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_WALLET_PUBKEY = stringPreferencesKey("wallet_pubkey")
    }

    private val _balanceLamports = MutableStateFlow(0L)
    val balanceLamports: StateFlow<Long> = _balanceLamports.asStateFlow()

    private val _transactionHistory = MutableStateFlow<List<TransactionRecord>>(emptyList())
    val transactionHistory: StateFlow<List<TransactionRecord>> = _transactionHistory.asStateFlow()

    val walletPubkey: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_WALLET_PUBKEY]
    }

    suspend fun connectWallet(pubkey: String) {
        dataStore.edit { prefs ->
            prefs[KEY_WALLET_PUBKEY] = pubkey
        }
        refreshBalance()
    }

    suspend fun disconnectWallet() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_WALLET_PUBKEY)
        }
        _balanceLamports.value = 0L
        _transactionHistory.value = emptyList()
    }

    suspend fun getConnectedPubkey(): String? {
        return dataStore.data.first()[KEY_WALLET_PUBKEY]
    }

    suspend fun refreshBalance() {
        val pubkey = getConnectedPubkey() ?: return
        try {
            val balance = solanaRpcClient.getBalance(pubkey)
            _balanceLamports.value = balance
        } catch (e: Exception) {
            // Keep last known balance on error
        }
    }

    fun getBalanceSol(): Double = solanaRpcClient.lamportsToSol(_balanceLamports.value)

    suspend fun stake(amount: Double) {
        val pubkey = getConnectedPubkey() ?: throw IllegalStateException("Wallet not connected")

        // Update local staker record
        val existing = stakerDao.getByPubkey(pubkey)
        if (existing != null) {
            stakerDao.update(existing.copy(amount = existing.amount + amount))
        } else {
            stakerDao.insert(StakerEntity(pubkey = pubkey, amount = amount, reputation = 0))
        }

        // In full implementation: build & send stake transaction to Solana program
    }

    suspend fun unstake(amount: Double) {
        val pubkey = getConnectedPubkey() ?: throw IllegalStateException("Wallet not connected")

        val existing = stakerDao.getByPubkey(pubkey) ?: throw IllegalStateException("Not a staker")
        val newAmount = (existing.amount - amount).coerceAtLeast(0.0)
        if (newAmount > 0) {
            stakerDao.update(existing.copy(amount = newAmount))
        } else {
            stakerDao.delete(existing)
        }

        // In full implementation: build & send unstake transaction to Solana program
    }

    fun getAllStakers(): Flow<List<StakerEntity>> = stakerDao.getAll()

    fun getTotalStaked(): Flow<Double?> = stakerDao.getTotalStaked()

    fun getStakerCount(): Flow<Int> = stakerDao.getStakerCount()
}
