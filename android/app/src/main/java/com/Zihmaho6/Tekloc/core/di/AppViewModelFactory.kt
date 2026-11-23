package com.Zihmaho6.Tekloc.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import com.Zihmaho6.Tekloc.domain.repository.SettingsRepository
import com.Zihmaho6.Tekloc.domain.usecase.AwardStarsUseCase
import com.Zihmaho6.Tekloc.domain.usecase.DailySeedUseCase
import com.Zihmaho6.Tekloc.domain.usecase.GenerateLayoutUseCase
import com.Zihmaho6.Tekloc.domain.usecase.ScoreDropUseCase
import com.Zihmaho6.Tekloc.domain.usecase.SimulateDropUseCase
import com.Zihmaho6.Tekloc.presentation.app.AppViewModel
import com.Zihmaho6.Tekloc.presentation.boardselect.BoardSelectViewModel
import com.Zihmaho6.Tekloc.presentation.collection.CollectionViewModel
import com.Zihmaho6.Tekloc.presentation.daily.DailyChallengeViewModel
import com.Zihmaho6.Tekloc.presentation.game.GameViewModel
import com.Zihmaho6.Tekloc.presentation.menu.MenuViewModel
import com.Zihmaho6.Tekloc.presentation.result.ResultViewModel
import com.Zihmaho6.Tekloc.presentation.settings.SettingsViewModel
import com.Zihmaho6.Tekloc.presentation.splash.SplashViewModel
import com.Zihmaho6.Tekloc.presentation.tutorial.TutorialViewModel

class AppViewModelFactory(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val generateLayout: GenerateLayoutUseCase,
    private val simulateDrop: SimulateDropUseCase,
    private val scoreDrop: ScoreDropUseCase,
    private val awardStars: AwardStarsUseCase,
    private val dailySeed: DailySeedUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when (modelClass) {
            AppViewModel::class.java -> AppViewModel()
            SplashViewModel::class.java -> SplashViewModel()
            MenuViewModel::class.java -> MenuViewModel(progressRepository, dailySeed)
            BoardSelectViewModel::class.java -> BoardSelectViewModel(progressRepository)
            CollectionViewModel::class.java -> CollectionViewModel(progressRepository)
            TutorialViewModel::class.java -> TutorialViewModel(progressRepository)
            SettingsViewModel::class.java -> SettingsViewModel(settingsRepository, progressRepository)
            ResultViewModel::class.java -> ResultViewModel(progressRepository)
            DailyChallengeViewModel::class.java ->
                DailyChallengeViewModel(progressRepository, dailySeed, generateLayout)
            GameViewModel::class.java -> GameViewModel(
                progressRepository,
                settingsRepository,
                generateLayout,
                simulateDrop,
                scoreDrop,
                awardStars,
                dailySeed
            )
            else -> throw IllegalArgumentException(UNKNOWN + modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }

    private companion object {
        const val UNKNOWN = "Unknown ViewModel: "
    }
}
