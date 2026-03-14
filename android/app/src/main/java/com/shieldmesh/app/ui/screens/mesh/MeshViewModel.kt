package com.shieldmesh.app.ui.screens.mesh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldmesh.app.data.repository.MeshRepository
import com.shieldmesh.app.data.repository.MeshStatus
import com.shieldmesh.app.mesh.MeshMetrics
import com.shieldmesh.app.mesh.PollinetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeshViewModel @Inject constructor(
    private val meshRepository: MeshRepository,
    private val pollinetManager: PollinetManager
) : ViewModel() {

    val meshStatus: StateFlow<MeshStatus> = meshRepository.meshStatus
    val peerMessages: StateFlow<List<String>> = meshRepository.peerMessages

    val isInitialized: StateFlow<Boolean> = pollinetManager.isInitialized

    val outboundQueueSize: StateFlow<Int> = pollinetManager.outboundQueueSize
    val receivedQueueSize: StateFlow<Int> = pollinetManager.receivedQueueSize

    val meshMetrics: StateFlow<MeshMetrics> = pollinetManager.meshMetrics

    fun startMesh() {
        meshRepository.start()
    }

    fun stopMesh() {
        meshRepository.stop()
    }

    fun refreshMetrics() {
        viewModelScope.launch {
            pollinetManager.getQueueSizes()
            pollinetManager.getMetrics()
        }
    }
}
