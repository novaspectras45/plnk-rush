package com.Zihmaho6.Tekloc.domain.model

enum class TrajectoryMode { FULL, ENDPOINT, OFF }

enum class DropSpeed(val stepMs: Long) { CALM(220L), STANDARD(180L), RUSH(120L) }

enum class ClockMode(val totalMs: Long) { ON(30000L), RELAXED(60000L) }

data class BoardDef(
    val index: Int,
    val starsToUnlock: Int,
    val deflectBudget: Int,
    val boostBudget: Int,
    val gateCount: Int,
    val barrierCount: Int
)

data class CoreSkin(
    val id: Int,
    val tint: Long,
    val starsToUnlock: Int
)

data class DailyState(
    val lastPlayedDay: Long,
    val lastStars: Int,
    val lastScore: Int
)

data class AppSettings(
    val trajectory: TrajectoryMode,
    val dropSpeed: DropSpeed,
    val clockMode: ClockMode
)

data class PlayerProgress(
    val stageStars: List<Int>,
    val stageBest: List<Int>,
    val totalStars: Int,
    val bonusStars: Int,
    val streak: Int,
    val selectedCoreId: Int,
    val tutorialSeen: Boolean,
    val daily: DailyState
) {
    fun starsOfBoard(boardIndex: Int, stagesPerBoard: Int): Int {
        val from = boardIndex * stagesPerBoard
        var sum = 0
        for (i in from until from + stagesPerBoard) {
            if (i < stageStars.size) sum += stageStars[i]
        }
        return sum
    }

    fun firstUnfinishedStage(boardIndex: Int, stagesPerBoard: Int): Int {
        val from = boardIndex * stagesPerBoard
        for (i in from until from + stagesPerBoard) {
            if (i < stageStars.size && stageStars[i] == 0) return i
        }
        return from
    }
}
