package com.Zihmaho6.Tekloc.presentation.game

import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.BumperKind
import com.Zihmaho6.Tekloc.domain.model.DropStep
import com.Zihmaho6.Tekloc.domain.model.Placement
import com.Zihmaho6.Tekloc.domain.model.TrajectoryMode

enum class Phase { Loading, Placement, Dropping, Settled }

data class GameUiState(
    val phase: Phase = Phase.Loading,
    val layout: BoardLayout? = null,
    val placement: Placement = Placement(),
    val laneIndex: Int = 1,
    val selectedKind: BumperKind = BumperKind.DEFLECT,
    val previewPath: List<DropStep> = emptyList(),
    val previewZone: Int = -1,
    val trajectory: TrajectoryMode = TrajectoryMode.FULL,
    val tokenX: Int = -1,
    val tokenY: Int = -1,
    val trail: List<DropStep> = emptyList(),
    val litGates: Set<Int> = emptySet(),
    val barrierStep: Int = -1,
    val clockMillis: Long = 30000L,
    val clockTotal: Long = 30000L,
    val boardNumber: Int = 1,
    val stageNumber: Int = 1,
    val daily: Boolean = false,
    val won: Boolean = false,
    val landedZone: Int = -1,
    val replanUsed: Boolean = false,
    val paused: Boolean = false,
    val leaveConfirm: Boolean = false,
    val coreTint: Long = 0xFF00F5FF
) {
    val deflectLeft: Int
        get() = (layout?.deflectBudget ?: 0) - placement.countOf(BumperKind.DEFLECT)

    val boostLeft: Int
        get() = (layout?.boostBudget ?: 0) - placement.countOf(BumperKind.BOOST)

    val secondsLeft: Int
        get() = ((clockMillis + 999L) / 1000L).toInt()

    val clockFraction: Float
        get() = if (clockTotal <= 0L) 0f else (clockMillis.toFloat() / clockTotal.toFloat())

    val gateTotal: Int
        get() = layout?.gateCount ?: 0
}
