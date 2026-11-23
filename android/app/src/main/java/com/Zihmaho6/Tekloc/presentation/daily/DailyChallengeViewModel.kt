package com.Zihmaho6.Tekloc.presentation.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import com.Zihmaho6.Tekloc.domain.usecase.DailySeedUseCase
import com.Zihmaho6.Tekloc.domain.usecase.GenerateLayoutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DailyUiState(
    val day: Long = 0L,
    val done: Boolean = false,
    val lastStars: Int = 0,
    val totalStars: Int = 0,
    val preview: BoardLayout? = null
)

class DailyChallengeViewModel(
    private val progressRepository: ProgressRepository,
    private val dailySeed: DailySeedUseCase,
    private val generateLayout: GenerateLayoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DailyUiState())
    val state: StateFlow<DailyUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = progressRepository.load()
            val day = dailySeed.epochDay()
            val def = SampleData.boards[1]
            val layout = withContext(Dispatchers.Default) {
                generateLayout(def, GameConfig.DAILY_STAGE_INDEX, 0, dailySeed.seedFor(day))
            }
            _state.update {
                it.copy(
                    day = day,
                    done = progress.daily.lastPlayedDay == day,
                    lastStars = progress.daily.lastStars,
                    totalStars = progress.totalStars,
                    preview = layout
                )
            }
        }
    }

}
