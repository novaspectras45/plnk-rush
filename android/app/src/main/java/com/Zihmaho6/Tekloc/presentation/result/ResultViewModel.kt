package com.Zihmaho6.Tekloc.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(private val progressRepository: ProgressRepository) : ViewModel() {

    private val _state = MutableStateFlow(ResultUiState())
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    fun load(stageIndex: Int, won: Boolean) {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val next = if (won) {
                (stageIndex + 1).coerceAtMost(GameConfig.TOTAL_STAGES - 1)
            } else {
                stageIndex
            }
            _state.update {
                it.copy(
                    totalStars = progress.totalStars,
                    stageBest = progress.stageBest.getOrElse(stageIndex) { 0 },
                    streak = progress.streak,
                    nextStage = next
                )
            }
        }
    }
}
