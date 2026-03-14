package com.shieldmesh.app.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldmesh.app.data.remote.TransactionRecord
import com.shieldmesh.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    val walletPubkey: StateFlow<String?> = walletRepository.walletPubkey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val balanceSol: StateFlow<Double> = walletRepository.balanceLamports
        .let { flow ->
            kotlinx.coroutines.flow.flow {
                flow.collect { lamports ->
                    emit(lamports / 1_000_000_000.0)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val transactions: StateFlow<List<TransactionRecord>> = walletRepository.transactionHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun connectDemoWallet() {
        viewModelScope.launch {
            // Demo: generate a fake pubkey for testing
            // In production, this would use Mobile Wallet Adapter
            val demoPubkey = "ShLD" + (1..40).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".random() }.joinToString("")
            walletRepository.connectWallet(demoPubkey)
        }
    }

    fun disconnectWallet() {
        viewModelScope.launch {
            walletRepository.disconnectWallet()
        }
    }

    fun refreshBalance() {
        viewModelScope.launch {
            walletRepository.refreshBalance()
        }
    }
}
