package com.shieldmesh.app.ui.screens.scan

import android.net.Uri
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

enum class ScanMode { TEXT, FILE }

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val threatDetector: ThreatDetector,
    private val threatRepository: ThreatRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    private val _scanMode = MutableStateFlow(ScanMode.TEXT)
    val scanMode: StateFlow<ScanMode> = _scanMode.asStateFlow()

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName.asStateFlow()

    private val _selectedFileSize = MutableStateFlow(0L)
    val selectedFileSize: StateFlow<Long> = _selectedFileSize.asStateFlow()

    private val _fileContent = MutableStateFlow<String?>(null)

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<ThreatResult?>(null)
    val scanResult: StateFlow<ThreatResult?> = _scanResult.asStateFlow()

    private val _reported = MutableStateFlow(false)
    val reported: StateFlow<Boolean> = _reported.asStateFlow()

    fun setScanMode(mode: ScanMode) {
        _scanMode.value = mode
        _scanResult.value = null
        _reported.value = false
    }

    fun onInputChange(value: String) {
        _input.value = value
        _scanResult.value = null
        _reported.value = false
    }

    fun onFileSelected(fileName: String, fileSize: Long, content: String) {
        _selectedFileName.value = fileName
        _selectedFileSize.value = fileSize
        _fileContent.value = content
        _scanResult.value = null
        _reported.value = false
    }

    fun clearFile() {
        _selectedFileName.value = null
        _selectedFileSize.value = 0L
        _fileContent.value = null
        _scanResult.value = null
        _reported.value = false
    }

    fun scan() {
        viewModelScope.launch {
            _isScanning.value = true
            _scanResult.value = null
            _reported.value = false

            delay(800)

            val result = when (_scanMode.value) {
                ScanMode.TEXT -> {
                    val currentInput = _input.value.trim()
                    if (currentInput.isEmpty()) {
                        _isScanning.value = false
                        return@launch
                    }
                    threatDetector.analyze(currentInput)
                }
                ScanMode.FILE -> {
                    val fileName = _selectedFileName.value
                    if (fileName == null) {
                        _isScanning.value = false
                        return@launch
                    }
                    threatDetector.analyzeFile(
                        content = _fileContent.value ?: "",
                        fileName = fileName,
                        fileSize = _selectedFileSize.value
                    )
                }
            }

            _scanResult.value = result
            _isScanning.value = false
        }
    }

    fun reportThreat() {
        val result = _scanResult.value ?: return
        val target = when (_scanMode.value) {
            ScanMode.TEXT -> _input.value.trim()
            ScanMode.FILE -> "file:${_selectedFileName.value ?: "unknown"}"
        }

        viewModelScope.launch {
            val pubkey = walletRepository.walletPubkey.first() ?: "anonymous"
            threatRepository.reportThreat(
                url = target,
                description = result.description,
                severity = result.severity,
                aiScore = result.score,
                reporterPubkey = pubkey
            )
            _reported.value = true
        }
    }
}
