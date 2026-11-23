package com.Zihmaho6.Tekloc.core.navigation

import com.Zihmaho6.Tekloc.domain.model.RoundSummary

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object BoardSelect : Screen
    data object Collection : Screen
    data object Daily : Screen
    data object Tutorial : Screen
    data object Settings : Screen
    data class Game(
        val stageIndex: Int,
        val daily: Boolean,
        val attempt: Int,
        val replan: Boolean = false
    ) : Screen
    data class GameOver(val summary: RoundSummary) : Screen
}
