package com.Zihmaho6.Tekloc.domain.usecase

import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.domain.model.Barrier
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.BumperKind
import com.Zihmaho6.Tekloc.domain.model.Cell
import com.Zihmaho6.Tekloc.domain.model.DropOutcome
import com.Zihmaho6.Tekloc.domain.model.DropStep
import com.Zihmaho6.Tekloc.domain.model.PegPolarity
import com.Zihmaho6.Tekloc.domain.model.Placement

class SimulateDropUseCase {

    operator fun invoke(layout: BoardLayout, placement: Placement, laneIndex: Int): DropOutcome {
        val cols = layout.cols
        val rows = layout.rows
        var x = laneColumn(laneIndex)
        var skipRow = false
        var chain = 0
        var maxChain = 0
        var lastOrdinal = -1
        var kicks = 0
        val lit = ArrayList<Int>()
        val path = ArrayList<DropStep>(rows)

        for (y in 0 until rows) {
            if (skipRow) {
                skipRow = false
                path.add(DropStep(x, y, -1, false, true))
                continue
            }
            var litGate = -1
            var kicked = false
            val bumper = placement.at(x, y)
            if (bumper != null) {
                when (bumper.kind) {
                    BumperKind.DEFLECT -> x += when (bumper.rotation) {
                        0 -> -1
                        90 -> 1
                        180 -> -2
                        else -> 2
                    }
                    BumperKind.BOOST -> {
                        skipRow = true
                        x += when (bumper.rotation) {
                            90 -> 1
                            270 -> -1
                            else -> 0
                        }
                    }
                }
            } else if (barrierOccupies(layout.barriers, x, y)) {
                kicked = true
                kicks += 1
                x += if (x < GameConfig.BOARD_CENTRE) -1 else 1
                chain = 0
                lastOrdinal = -1
            } else {
                when (val cell = layout.cellAt(x, y)) {
                    is Cell.Peg -> x += if (cell.polarity == PegPolarity.LEFT) -1 else 1
                    is Cell.GateCell -> {
                        litGate = cell.index
                        if (!lit.contains(cell.index)) lit.add(cell.index)
                        val ordinal = cell.color.ordinal
                        when {
                            chain == 0 -> {
                                chain = 1
                                lastOrdinal = ordinal
                            }
                            ordinal == (lastOrdinal + 1) % 3 -> {
                                chain += 1
                                lastOrdinal = ordinal
                            }
                            else -> {
                                chain = 0
                                lastOrdinal = -1
                            }
                        }
                        if (chain > maxChain) maxChain = chain
                    }
                    Cell.Empty -> Unit
                }
            }
            if (x < 0) x = 1
            if (x > cols - 1) x = cols - 2
            path.add(DropStep(x, y, litGate, kicked, false))
        }

        val zone = (x / 2).coerceIn(0, GameConfig.ZONES - 1)
        return DropOutcome(path, zone, lit, maxChain, kicks)
    }

    fun laneColumn(laneIndex: Int): Int {
        val lanes = GameConfig.LANE_COLUMNS
        return lanes[laneIndex.coerceIn(0, lanes.size - 1)]
    }

    fun barrierColumn(barrier: Barrier, step: Int): Int =
        barrier.homeX + (((step + barrier.phase) % 3) - 1)

    private fun barrierOccupies(barriers: List<Barrier>, x: Int, y: Int): Boolean {
        for (barrier in barriers) {
            if (barrier.y == y && barrierColumn(barrier, y) == x) return true
        }
        return false
    }
}
