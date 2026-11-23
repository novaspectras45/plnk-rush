package com.Zihmaho6.Tekloc.data.repository

import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.data.local.PrefsStore
import com.Zihmaho6.Tekloc.domain.model.DailyState
import com.Zihmaho6.Tekloc.domain.model.PlayerProgress
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import kotlin.math.max

class ProgressRepositoryImpl(private val store: PrefsStore) : ProgressRepository {

    override fun load(): PlayerProgress {
        val stars = readList(PrefsStore.KEY_STAGE_STARS)
        val best = readList(PrefsStore.KEY_STAGE_BEST)
        val bonus = store.readInt(PrefsStore.KEY_BONUS_STARS, 0)
        return PlayerProgress(
            stageStars = stars,
            stageBest = best,
            totalStars = stars.sum() + bonus,
            bonusStars = bonus,
            streak = store.readInt(PrefsStore.KEY_STREAK, 0),
            selectedCoreId = store.readInt(PrefsStore.KEY_CORE, 0),
            tutorialSeen = store.readBoolean(PrefsStore.KEY_TUTORIAL, false),
            daily = DailyState(
                lastPlayedDay = store.readLong(PrefsStore.KEY_DAILY_DAY, 0L),
                lastStars = store.readInt(PrefsStore.KEY_DAILY_STARS, 0),
                lastScore = store.readInt(PrefsStore.KEY_DAILY_SCORE, 0)
            )
        )
    }

    override fun recordStage(stageIndex: Int, stars: Int, score: Int, won: Boolean) {
        if (stageIndex !in 0 until GameConfig.TOTAL_STAGES) return
        val starList = readList(PrefsStore.KEY_STAGE_STARS).toMutableList()
        val bestList = readList(PrefsStore.KEY_STAGE_BEST).toMutableList()
        starList[stageIndex] = max(starList[stageIndex], stars)
        bestList[stageIndex] = max(bestList[stageIndex], score)
        writeList(PrefsStore.KEY_STAGE_STARS, starList)
        writeList(PrefsStore.KEY_STAGE_BEST, bestList)
        val streak = if (won) store.readInt(PrefsStore.KEY_STREAK, 0) + 1 else 0
        store.writeInt(PrefsStore.KEY_STREAK, streak)
    }

    override fun recordDaily(day: Long, stars: Int, score: Int) {
        store.writeLong(PrefsStore.KEY_DAILY_DAY, day)
        store.writeInt(PrefsStore.KEY_DAILY_STARS, stars)
        store.writeInt(PrefsStore.KEY_DAILY_SCORE, score)
        store.writeInt(PrefsStore.KEY_BONUS_STARS, store.readInt(PrefsStore.KEY_BONUS_STARS, 0) + stars)
    }

    override fun selectCore(id: Int) {
        store.writeInt(PrefsStore.KEY_CORE, id)
    }

    override fun markTutorialSeen() {
        store.writeBoolean(PrefsStore.KEY_TUTORIAL, true)
    }

    override fun reset() {
        store.clearAll()
    }

    private fun readList(key: String): List<Int> {
        val raw = store.readString(key, "")
        val result = MutableList(GameConfig.TOTAL_STAGES) { 0 }
        if (raw.isEmpty()) return result
        val parts = raw.split(SEPARATOR)
        for (i in 0 until GameConfig.TOTAL_STAGES) {
            result[i] = parts.getOrNull(i)?.toIntOrNull() ?: 0
        }
        return result
    }

    private fun writeList(key: String, values: List<Int>) {
        store.writeString(key, values.joinToString(SEPARATOR))
    }

    private companion object {
        const val SEPARATOR = ","
    }
}
