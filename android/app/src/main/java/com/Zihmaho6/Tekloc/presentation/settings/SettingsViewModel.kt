package com.Zihmaho6.Tekloc.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.domain.model.AppSettings
import com.Zihmaho6.Tekloc.domain.model.ClockMode
import com.Zihmaho6.Tekloc.domain.model.DropSpeed
import com.Zihmaho6.Tekloc.domain.model.TrajectoryMode
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import com.Zihmaho6.Tekloc.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(
        TrajectoryMode.FULL,
        DropSpeed.STANDARD,
        ClockMode.ON
    ),
    val totalStars: Int = 0
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    settings = settingsRepository.load(),
                    totalStars = progressRepository.load().totalStars
                )
            }
        }
    }

    fun setTrajectory(index: Int) {
        val mode = TrajectoryMode.entries.getOrElse(index) { TrajectoryMode.FULL }
        viewModelScope.launch {
            settingsRepository.setTrajectory(mode)
            _state.update { it.copy(settings = it.settings.copy(trajectory = mode)) }
        }
    }

    fun setDropSpeed(index: Int) {
        val speed = DropSpeed.entries.getOrElse(index) { DropSpeed.STANDARD }
        viewModelScope.launch {
            settingsRepository.setDropSpeed(speed)
            _state.update { it.copy(settings = it.settings.copy(dropSpeed = speed)) }
        }
    }

    fun setClockMode(index: Int) {
        val mode = ClockMode.entries.getOrElse(index) { ClockMode.ON }
        viewModelScope.launch {
            settingsRepository.setClockMode(mode)
            _state.update { it.copy(settings = it.settings.copy(clockMode = mode)) }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            progressRepository.reset()
            _state.update {
                it.copy(settings = settingsRepository.load(), totalStars = 0)
            }
        }
    }
}
