package com.Zihmaho6.Tekloc.core.di

import android.content.Context
import com.Zihmaho6.Tekloc.data.local.PrefsStore
import com.Zihmaho6.Tekloc.data.repository.ProgressRepositoryImpl
import com.Zihmaho6.Tekloc.data.repository.SettingsRepositoryImpl
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.usecase.AwardStarsUseCase
import com.Zihmaho6.Tekloc.domain.usecase.DailySeedUseCase
import com.Zihmaho6.Tekloc.domain.usecase.GenerateLayoutUseCase
import com.Zihmaho6.Tekloc.domain.usecase.ScoreDropUseCase
import com.Zihmaho6.Tekloc.domain.usecase.SimulateDropUseCase
import com.Zihmaho6.Tekloc.domain.usecase.SolveLayoutUseCase

object ServiceLocator {

    private var factory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (factory != null) return
        val store = PrefsStore(context)
        val simulate = SimulateDropUseCase()
        val solve = SolveLayoutUseCase(simulate)
        factory = AppViewModelFactory(
            progressRepository = ProgressRepositoryImpl(store),
            settingsRepository = SettingsRepositoryImpl(store),
            generateLayout = GenerateLayoutUseCase(solve, SampleData.fallbackLayouts),
            simulateDrop = simulate,
            scoreDrop = ScoreDropUseCase(),
            awardStars = AwardStarsUseCase(),
            dailySeed = DailySeedUseCase()
        )
    }

    fun viewModelFactory(): AppViewModelFactory =
        checkNotNull(factory) { NOT_READY }

    private const val NOT_READY = "ServiceLocator.init was not called"
}
