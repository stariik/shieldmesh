package com.shieldmesh.app.ui.screens.bounties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldmesh.app.data.local.entity.BountyEntity
import com.shieldmesh.app.data.local.dao.BountyDao
import com.shieldmesh.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BountyViewModel @Inject constructor(
    bountyDao: BountyDao,
    private val walletRepository: WalletRepository
) : ViewModel() {

    val bounties: StateFlow<List<BountyEntity>> = bountyDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPool: StateFlow<Double> = bountyDao.getTotalPool()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val stakerCount: StateFlow<Int> = walletRepository.getStakerCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStaked: StateFlow<Double> = walletRepository.getTotalStaked()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun stake(amount: Double) {
        viewModelScope.launch {
            try {
                walletRepository.stake(amount)
            } catch (_: Exception) {
                // Handle error in production
            }
        }
    }

    fun unstake(amount: Double) {
        viewModelScope.launch {
            try {
                walletRepository.unstake(amount)
            } catch (_: Exception) {
                // Handle error in production
            }
        }
    }
}
