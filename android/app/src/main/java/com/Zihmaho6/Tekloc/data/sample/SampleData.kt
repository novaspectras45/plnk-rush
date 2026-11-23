package com.Zihmaho6.Tekloc.data.sample

import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.domain.model.BoardDef
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.Cell
import com.Zihmaho6.Tekloc.domain.model.CoreSkin
import com.Zihmaho6.Tekloc.domain.model.GateColor
import com.Zihmaho6.Tekloc.domain.model.PegPolarity

object SampleData {

    private const val FALLBACK_SEED = 990001L

    private val GATE_ORDER = listOf(GateColor.MAGENTA, GateColor.CYAN, GateColor.LIME)

    val boards: List<BoardDef> = listOf(
        BoardDef(0, 0, 3, 1, 3, 0),
        BoardDef(1, 9, 2, 2, 4, 1),
        BoardDef(2, 20, 2, 1, 5, 2),
        BoardDef(3, 33, 1, 1, 5, 2)
    )

    val cores: List<CoreSkin> = listOf(
        CoreSkin(0, 0xFF00F5FF, 0),
        CoreSkin(1, 0xFFFF006E, 6),
        CoreSkin(2, 0xFFB6FF2E, 14),
        CoreSkin(3, 0xFFFFB020, 24),
        CoreSkin(4, 0xFF8B5CF6, 38),
        CoreSkin(5, 0xFFF2F3FF, 55)
    )

    val tutorialSeeds: List<Long> = listOf(10457L, 20357L, 30869L, 41231L)

    val fallbackLayouts: List<BoardLayout> = boards.map { buildFallback(it) }

    private fun buildFallback(def: BoardDef): BoardLayout {
        val cols = GameConfig.COLS
        val rows = GameConfig.ROWS
        val lane = GameConfig.LANE_COLUMNS[0]
        val cells = MutableList<Cell>(cols * rows) { Cell.Empty }

        for (y in 1 until rows - 1) {
            for (x in 0 until cols) {
                if (x == lane || x == lane - 1 || x == lane + 1) continue
                if ((x + y) % 2 != 0) continue
                val polarity = if (x % 3 == 0) PegPolarity.LEFT else PegPolarity.RIGHT
                cells[y * cols + x] = Cell.Peg(polarity)
            }
        }

        val gateRows = listOf(2, 4, 6, 8, 9).take(def.gateCount)
        gateRows.forEachIndexed { order, row ->
            cells[row * cols + lane] = Cell.GateCell(order, GATE_ORDER[order % GATE_ORDER.size])
        }

        val slots = ArrayList<Int>()
        for (y in 2 until rows - 1) {
            for (x in intArrayOf(lane - 1, lane + 1, lane + 2)) {
                if (x in 0 until cols && cells[y * cols + x] == Cell.Empty) slots.add(y * cols + x)
            }
        }

        return BoardLayout(
            boardIndex = def.index,
            stageIndex = def.index * GameConfig.STAGES_PER_BOARD,
            seed = FALLBACK_SEED + def.index,
            attempt = 0,
            cells = cells.toList(),
            barriers = emptyList(),
            slots = slots.take(GameConfig.CANDIDATE_SLOTS),
            targetZone = lane / 2,
            deflectBudget = def.deflectBudget,
            boostBudget = def.boostBudget,
            gateCount = gateRows.size,
            par = 1,
            cols = cols,
            rows = rows
        )
    }
}
