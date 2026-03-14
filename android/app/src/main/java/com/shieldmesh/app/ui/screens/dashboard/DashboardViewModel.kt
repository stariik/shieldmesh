package com.shieldmesh.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldmesh.app.data.local.entity.ThreatEntity
import com.shieldmesh.app.data.repository.MeshRepository
import com.shieldmesh.app.data.repository.ThreatRepository
import com.shieldmesh.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    threatRepository: ThreatRepository,
    walletRepository: WalletRepository,
    meshRepository: MeshRepository
) : ViewModel() {

    val totalThreats: StateFlow<Int> = threatRepository.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val verifiedThreats: StateFlow<Int> = threatRepository.getVerifiedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val poolBalance: StateFlow<Double> = walletRepository.getTotalStaked()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val meshPeers: StateFlow<Int> = meshRepository.meshStatus
        .map { it.peersConnected }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentThreats: StateFlow<List<ThreatEntity>> = threatRepository.getAllThreats()
        .map { it.take(10) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
