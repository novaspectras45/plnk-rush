package com.Zihmaho6.Tekloc.domain.usecase

import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.domain.model.Barrier
import com.Zihmaho6.Tekloc.domain.model.BoardDef
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.Cell
import com.Zihmaho6.Tekloc.domain.model.GateColor
import com.Zihmaho6.Tekloc.domain.model.PegPolarity
import kotlin.random.Random

class GenerateLayoutUseCase(
    private val solve: SolveLayoutUseCase,
    private val fallbacks: List<BoardLayout>
) {

    operator fun invoke(
        def: BoardDef,
        stageIndex: Int,
        attempt: Int,
        seedOverride: Long? = null
    ): BoardLayout {
        for (pass in 0 until GameConfig.MAX_GEN_ATTEMPTS) {
            val tries = attempt + pass
            val seed = seedOverride?.let { it xor (tries * GameConfig.SEED_B) }
                ?: (stageIndex * GameConfig.SEED_A xor tries * GameConfig.SEED_B)
            val candidate = build(def, stageIndex, tries, seed)
            if (solve.solvableWithoutBumpers(candidate)) continue
            val solution = solve(candidate) ?: continue
            return candidate.copy(par = solution.par)
        }
        val fallback = fallbacks[def.index.coerceIn(0, fallbacks.size - 1)]
        return fallback.copy(stageIndex = stageIndex, attempt = attempt)
    }

    private fun build(def: BoardDef, stageIndex: Int, attempt: Int, seed: Long): BoardLayout {
        val cols = GameConfig.COLS
        val rows = GameConfig.ROWS
        val random = Random(seed)
        val cells = MutableList<Cell>(cols * rows) { Cell.Empty }

        val barrierRows = BARRIER_ROWS.shuffled(random).take(def.barrierCount).sorted()
        val barriers = barrierRows.map { row ->
            Barrier(homeX = 3 + random.nextInt(3), y = row, phase = random.nextInt(3))
        }

        for (y in 1 until rows - 1) {
            if (barrierRows.contains(y)) continue
            for (x in 0 until cols) {
                if ((x + y) % 2 != 0) continue
                if (random.nextInt(100) < PEG_CHANCE) {
                    val polarity = if (random.nextBoolean()) PegPolarity.LEFT else PegPolarity.RIGHT
                    cells[y * cols + x] = Cell.Peg(polarity)
                }
            }
        }

        val gateCandidates = ArrayList<Int>()
        for (y in 2 until rows - 1) {
            if (barrierRows.contains(y)) continue
            for (x in 1 until cols - 1) {
                if (cells[y * cols + x] == Cell.Empty) gateCandidates.add(y * cols + x)
            }
        }
        val gatePicks = gateCandidates.shuffled(random).take(def.gateCount)
        gatePicks.forEachIndexed { order, index ->
            val color = GATE_ORDER[order % GATE_ORDER.size]
            cells[index] = Cell.GateCell(order, color)
        }

        val slotCandidates = ArrayList<Int>()
        for (y in 1 until rows - 1) {
            if (barrierRows.contains(y)) continue
            for (x in 0 until cols) {
                if (cells[y * cols + x] == Cell.Empty) slotCandidates.add(y * cols + x)
            }
        }
        val slots = slotCandidates.shuffled(random).take(GameConfig.CANDIDATE_SLOTS).sorted()

        return BoardLayout(
            boardIndex = def.index,
            stageIndex = stageIndex,
            seed = seed,
            attempt = attempt,
            cells = cells.toList(),
            barriers = barriers,
            slots = slots,
            targetZone = random.nextInt(GameConfig.ZONES),
            deflectBudget = def.deflectBudget,
            boostBudget = def.boostBudget,
            gateCount = gatePicks.size,
            par = 1,
            cols = cols,
            rows = rows
        )
    }

    private companion object {
        const val PEG_CHANCE = 52
        val BARRIER_ROWS = listOf(3, 6, 9)
        val GATE_ORDER = listOf(GateColor.MAGENTA, GateColor.CYAN, GateColor.LIME)
    }
}
