package com.Zihmaho6.Tekloc.presentation.boardselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BoardRow(
    val index: Int,
    val unlocked: Boolean,
    val starsToUnlock: Int,
    val earnedStars: Int,
    val maxStars: Int,
    val stageStars: List<Int>
)

data class BoardSelectUiState(
    val totalStars: Int = 0,
    val rows: List<BoardRow> = emptyList()
)

class BoardSelectViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BoardSelectUiState())
    val state: StateFlow<BoardSelectUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val rows = SampleData.boards.map { def ->
                val from = def.index * GameConfig.STAGES_PER_BOARD
                val stages = (0 until GameConfig.STAGES_PER_BOARD).map { offset ->
                    progress.stageStars.getOrElse(from + offset) { 0 }
                }
                BoardRow(
                    index = def.index,
                    unlocked = progress.totalStars >= def.starsToUnlock,
                    starsToUnlock = def.starsToUnlock,
                    earnedStars = stages.sum(),
                    maxStars = GameConfig.STAGES_PER_BOARD * GameConfig.STARS_PER_STAGE,
                    stageStars = stages
                )
            }
            _state.update { it.copy(totalStars = progress.totalStars, rows = rows) }
        }
    }
}
