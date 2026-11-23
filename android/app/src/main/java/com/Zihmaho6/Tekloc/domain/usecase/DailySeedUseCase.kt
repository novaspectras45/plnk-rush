package com.Zihmaho6.Tekloc.domain.usecase

import com.Zihmaho6.Tekloc.core.config.GameConfig

class DailySeedUseCase {

    fun epochDay(nowMillis: Long = System.currentTimeMillis()): Long = nowMillis / DAY_MS

    fun seedFor(day: Long): Long = day * GameConfig.SEED_A xor GameConfig.SEED_B

    private companion object {
        const val DAY_MS = 86400000L
    }
}
