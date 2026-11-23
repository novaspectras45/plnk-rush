package com.Zihmaho6.Tekloc.domain.usecase

import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.Bumper
import com.Zihmaho6.Tekloc.domain.model.BumperKind
import com.Zihmaho6.Tekloc.domain.model.Placement

class SolveLayoutUseCase(private val simulate: SimulateDropUseCase) {

    data class Solution(val par: Int, val lane: Int, val bumpers: List<Bumper>)

    fun solvableWithoutBumpers(layout: BoardLayout): Boolean {
        for (lane in GameConfig.LANE_COLUMNS.indices) {
            if (simulate(layout, EMPTY_PLACEMENT, lane).zone == layout.targetZone) return true
        }
        return false
    }

    operator fun invoke(layout: BoardLayout): Solution? {
        val budget = layout.budget
        val counter = intArrayOf(0)
        for (size in 1..budget) {
            for (lane in GameConfig.LANE_COLUMNS.indices) {
                val chosen = IntArray(size)
                val found = combine(layout, size, 0, 0, chosen, lane, ArrayList(size), counter)
                if (found != null) return Solution(size, lane, found)
                if (counter[0] >= GameConfig.MAX_SOLVER_SIMULATIONS) return null
            }
        }
        return null
    }

    private fun combine(
        layout: BoardLayout,
        size: Int,
        depth: Int,
        start: Int,
        chosen: IntArray,
        lane: Int,
        acc: ArrayList<Bumper>,
        counter: IntArray
    ): List<Bumper>? {
        if (depth == size) return assign(layout, size, 0, chosen, lane, acc, counter)
        val slots = layout.slots
        for (i in start until slots.size) {
            chosen[depth] = slots[i]
            val result = combine(layout, size, depth + 1, i + 1, chosen, lane, acc, counter)
            if (result != null) return result
            if (counter[0] >= GameConfig.MAX_SOLVER_SIMULATIONS) return null
        }
        return null
    }

    private fun assign(
        layout: BoardLayout,
        size: Int,
        depth: Int,
        chosen: IntArray,
        lane: Int,
        acc: ArrayList<Bumper>,
        counter: IntArray
    ): List<Bumper>? {
        if (depth == size) {
            if (counter[0] >= GameConfig.MAX_SOLVER_SIMULATIONS) return null
            counter[0] += 1
            val outcome = simulate(layout, Placement(acc.toList()), lane)
            return if (outcome.zone == layout.targetZone) acc.toList() else null
        }
        val index = chosen[depth]
        val x = index % layout.cols
        val y = index / layout.cols
        for (option in OPTIONS) {
            val cap = if (option.kind == BumperKind.DEFLECT) layout.deflectBudget else layout.boostBudget
            if (acc.count { it.kind == option.kind } >= cap) continue
            acc.add(Bumper(x, y, option.kind, option.rotation))
            val result = assign(layout, size, depth + 1, chosen, lane, acc, counter)
            acc.removeAt(acc.size - 1)
            if (result != null) return result
            if (counter[0] >= GameConfig.MAX_SOLVER_SIMULATIONS) return null
        }
        return null
    }

    private data class Option(val kind: BumperKind, val rotation: Int)

    private companion object {
        val EMPTY_PLACEMENT = Placement()
        val OPTIONS = listOf(
            Option(BumperKind.DEFLECT, 0),
            Option(BumperKind.DEFLECT, 90),
            Option(BumperKind.DEFLECT, 180),
            Option(BumperKind.DEFLECT, 270),
            Option(BumperKind.BOOST, 0),
            Option(BumperKind.BOOST, 90),
            Option(BumperKind.BOOST, 270)
        )
    }
}
