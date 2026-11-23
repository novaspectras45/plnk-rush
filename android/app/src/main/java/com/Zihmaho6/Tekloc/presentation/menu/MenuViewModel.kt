package com.Zihmaho6.Tekloc.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.model.PlayerProgress
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import com.Zihmaho6.Tekloc.domain.usecase.DailySeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val dailySeed: DailySeedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val today = dailySeed.epochDay()
            _state.update {
                it.copy(
                    totalStars = progress.totalStars,
                    bestScore = progress.stageBest.maxOrNull() ?: 0,
                    streak = progress.streak,
                    currentStage = currentStage(progress),
                    tutorialSeen = progress.tutorialSeen,
                    dailyDone = progress.daily.lastPlayedDay == today,
                    dailyStars = progress.daily.lastStars,
                    coreTint = coreTint(progress.selectedCoreId)
                )
            }
        }
    }

    private fun coreTint(id: Int): Long =
        SampleData.cores.firstOrNull { it.id == id }?.tint ?: SampleData.cores[0].tint

    private fun currentStage(progress: PlayerProgress): Int {
        var board = 0
        SampleData.boards.forEach { def ->
            if (progress.totalStars >= def.starsToUnlock) board = def.index
        }
        return progress.firstUnfinishedStage(board, GameConfig.STAGES_PER_BOARD)
    }
}
