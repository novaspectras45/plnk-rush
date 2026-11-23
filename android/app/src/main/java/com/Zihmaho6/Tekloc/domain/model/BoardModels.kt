package com.Zihmaho6.Tekloc.domain.model

enum class PegPolarity { LEFT, RIGHT }

enum class GateColor { MAGENTA, CYAN, LIME }

enum class BumperKind { DEFLECT, BOOST }

sealed interface Cell {
    data object Empty : Cell
    data class Peg(val polarity: PegPolarity) : Cell
    data class GateCell(val index: Int, val color: GateColor) : Cell
}

data class Barrier(val homeX: Int, val y: Int, val phase: Int)

data class Bumper(val x: Int, val y: Int, val kind: BumperKind, val rotation: Int)

data class BoardLayout(
    val boardIndex: Int,
    val stageIndex: Int,
    val seed: Long,
    val attempt: Int,
    val cells: List<Cell>,
    val barriers: List<Barrier>,
    val slots: List<Int>,
    val targetZone: Int,
    val deflectBudget: Int,
    val boostBudget: Int,
    val gateCount: Int,
    val par: Int,
    val cols: Int,
    val rows: Int
) {
    fun cellAt(x: Int, y: Int): Cell =
        if (x < 0 || y < 0 || x >= cols || y >= rows) Cell.Empty else cells[y * cols + x]

    fun isSlot(x: Int, y: Int): Boolean = slots.contains(y * cols + x)

    val budget: Int get() = deflectBudget + boostBudget
}

data class Placement(val bumpers: List<Bumper> = emptyList()) {
    fun at(x: Int, y: Int): Bumper? = bumpers.firstOrNull { it.x == x && it.y == y }
    fun countOf(kind: BumperKind): Int = bumpers.count { it.kind == kind }
    val size: Int get() = bumpers.size
}

data class DropStep(
    val x: Int,
    val y: Int,
    val litGate: Int,
    val kicked: Boolean,
    val skipped: Boolean
)

data class DropOutcome(
    val path: List<DropStep>,
    val zone: Int,
    val litGates: List<Int>,
    val maxChain: Int,
    val kicks: Int
) {
    val gatesLit: Int get() = litGates.size

    val comboMultiplier: Float
        get() = when {
            maxChain >= 4 -> 3.0f
            maxChain == 3 -> 2.0f
            maxChain == 2 -> 1.5f
            else -> 1.0f
        }
}

data class RoundSummary(
    val boardIndex: Int,
    val stageIndex: Int,
    val attempt: Int,
    val daily: Boolean,
    val won: Boolean,
    val landedZone: Int,
    val targetZone: Int,
    val gatesLit: Int,
    val gateTotal: Int,
    val comboMultiplier: Float,
    val bumpersUsed: Int,
    val par: Int,
    val secondsLeft: Int,
    val replanUsed: Boolean,
    val score: Int,
    val starTarget: Boolean,
    val starGates: Boolean,
    val starPar: Boolean
) {
    val stars: Int
        get() = (if (starTarget) 1 else 0) + (if (starGates) 1 else 0) + (if (starPar) 1 else 0)
}
