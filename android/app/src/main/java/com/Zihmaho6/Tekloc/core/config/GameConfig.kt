package com.Zihmaho6.Tekloc.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val COLS = 9
    const val ROWS = 11
    const val ZONES = 5
    const val BOARD_CENTRE = 4

    const val BOARDS = 4
    const val STAGES_PER_BOARD = 6
    const val TOTAL_STAGES = BOARDS * STAGES_PER_BOARD
    const val STARS_PER_STAGE = 3

    const val SEED_A = 7919L
    const val SEED_B = 104729L
    const val MAX_GEN_ATTEMPTS = 20
    const val MAX_SOLVER_SIMULATIONS = 320000
    const val CANDIDATE_SLOTS = 10

    const val SETTLE_BANNER_MS = 600L
    const val REPLAN_CLOCK_MS = 20000L
    const val CLOCK_TICK_MS = 1000L
    const val DAILY_PAR = 3
    const val DAILY_STAGE_INDEX = STAGES_PER_BOARD

    const val BASE_SCORE = 500
    const val GATE_SCORE = 150
    const val BUMPER_SAVE_SCORE = 100
    const val SECOND_SCORE = 5
    const val CONSOLATION_PER_GATE = 50

    val LANE_COLUMNS = intArrayOf(2, 4, 6)
}
