package com.Zihmaho6.Tekloc.domain.usecase

import com.Zihmaho6.Tekloc.core.config.GameConfig
import kotlin.math.max
import kotlin.math.roundToInt

class ScoreDropUseCase {

    operator fun invoke(
        won: Boolean,
        gatesLit: Int,
        comboMultiplier: Float,
        par: Int,
        bumpersUsed: Int,
        secondsLeft: Int
    ): Int {
        if (!won) return gatesLit * GameConfig.CONSOLATION_PER_GATE
        val gateScore = gatesLit * GameConfig.GATE_SCORE * comboMultiplier
        val saved = max(0, par - bumpersUsed) * GameConfig.BUMPER_SAVE_SCORE
        val clock = max(0, secondsLeft) * GameConfig.SECOND_SCORE
        return (GameConfig.BASE_SCORE + gateScore + saved + clock).roundToInt()
    }
}
