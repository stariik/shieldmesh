package com.shieldmesh.app.ui.screens.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldmesh.app.ai.ThreatDetector
import com.shieldmesh.app.ai.ThreatResult
import com.shieldmesh.app.data.repository.ThreatRepository
import com.shieldmesh.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val threatDetector: ThreatDetector,
    private val threatRepository: ThreatRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<ThreatResult?>(null)
    val scanResult: StateFlow<ThreatResult?> = _scanResult.asStateFlow()

    private val _reported = MutableStateFlow(false)
    val reported: StateFlow<Boolean> = _reported.asStateFlow()

    fun onInputChange(value: String) {
        _input.value = value
        _scanResult.value = null
        _reported.value = false
    }

    fun scan() {
        val currentInput = _input.value.trim()
        if (currentInput.isEmpty()) return

        viewModelScope.launch {
            _isScanning.value = true
            _scanResult.value = null
            _reported.value = false

            // Simulate brief analysis delay for UX
            delay(800)

            val result = threatDetector.analyze(currentInput)
            _scanResult.value = result
            _isScanning.value = false
        }
    }

    fun reportThreat() {
        val result = _scanResult.value ?: return
        val currentInput = _input.value.trim()

        viewModelScope.launch {
            val pubkey = walletRepository.walletPubkey.first() ?: "anonymous"
            threatRepository.reportThreat(
                url = currentInput,
                description = result.description,
                severity = result.severity,
                aiScore = result.score,
                reporterPubkey = pubkey
            )
            _reported.value = true
        }
    }
}
