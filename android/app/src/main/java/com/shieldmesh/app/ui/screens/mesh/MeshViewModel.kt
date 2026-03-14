package com.shieldmesh.app.ui.screens.mesh

import androidx.lifecycle.ViewModel
import com.shieldmesh.app.data.repository.MeshRepository
import com.shieldmesh.app.data.repository.MeshStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MeshViewModel @Inject constructor(
    private val meshRepository: MeshRepository
) : ViewModel() {

    val meshStatus: StateFlow<MeshStatus> = meshRepository.meshStatus
    val peerMessages: StateFlow<List<String>> = meshRepository.peerMessages

    fun startMesh() {
        meshRepository.start()
    }

    fun stopMesh() {
        meshRepository.stop()
    }

    fun simulatePeers() {
        val currentPeers = meshRepository.getStatus().peersConnected
        meshRepository.simulatePeerDiscovery(currentPeers + (1..5).random())
    }
}
