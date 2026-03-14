package com.shieldmesh.app.ui.screens.threats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldmesh.app.data.local.entity.ThreatEntity
import com.shieldmesh.app.data.repository.ThreatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThreatFeedViewModel @Inject constructor(
    threatRepository: ThreatRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val filteredThreats: StateFlow<List<ThreatEntity>> = combine(
        threatRepository.getAllThreats(),
        _selectedFilter
    ) { threats, filter ->
        if (filter == "all") threats
        else threats.filter { it.severity.name == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }
}
