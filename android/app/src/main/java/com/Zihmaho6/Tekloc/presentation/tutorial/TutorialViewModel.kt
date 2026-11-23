package com.Zihmaho6.Tekloc.presentation.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TutorialUiState(
    val diagrams: List<BoardLayout> = emptyList(),
    val seen: Boolean = false
)

class TutorialViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TutorialUiState())
    val state: StateFlow<TutorialUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val progress = progressRepository.load()
            _state.update {
                it.copy(diagrams = SampleData.fallbackLayouts, seen = progress.tutorialSeen)
            }
        }
    }

    fun markSeen() {
        viewModelScope.launch {
            progressRepository.markTutorialSeen()
            _state.update { it.copy(seen = true) }
        }
    }
}
