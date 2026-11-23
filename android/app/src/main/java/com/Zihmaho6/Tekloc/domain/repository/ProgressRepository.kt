package com.Zihmaho6.Tekloc.domain.repository

import com.Zihmaho6.Tekloc.domain.model.PlayerProgress

interface ProgressRepository {
    fun load(): PlayerProgress
    fun recordStage(stageIndex: Int, stars: Int, score: Int, won: Boolean)
    fun recordDaily(day: Long, stars: Int, score: Int)
    fun selectCore(id: Int)
    fun markTutorialSeen()
    fun reset()
}
