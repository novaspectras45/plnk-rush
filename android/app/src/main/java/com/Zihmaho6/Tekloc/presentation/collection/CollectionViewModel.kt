package com.Zihmaho6.Tekloc.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CoreRow(
    val id: Int,
    val tint: Long,
    val unlocked: Boolean,
    val selected: Boolean,
    val starsToUnlock: Int
)

data class CollectionUiState(
    val totalStars: Int = 0,
    val cores: List<CoreRow> = emptyList()
)

class CollectionViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { rebuild() }
    }

    fun select(id: Int) {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val core = SampleData.cores.firstOrNull { it.id == id } ?: return@launch
            if (progress.totalStars < core.starsToUnlock) return@launch
            progressRepository.selectCore(id)
            rebuild()
        }
    }

    private fun rebuild() {
        val progress = progressRepository.load()
        val cores = SampleData.cores.map { core ->
            CoreRow(
                id = core.id,
                tint = core.tint,
                unlocked = progress.totalStars >= core.starsToUnlock,
                selected = progress.selectedCoreId == core.id,
                starsToUnlock = core.starsToUnlock
            )
        }
        _state.update { it.copy(totalStars = progress.totalStars, cores = cores) }
    }
}
