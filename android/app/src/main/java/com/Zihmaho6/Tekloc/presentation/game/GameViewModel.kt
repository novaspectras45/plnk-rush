package com.Zihmaho6.Tekloc.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.data.sample.SampleData
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.Bumper
import com.Zihmaho6.Tekloc.domain.model.BumperKind
import com.Zihmaho6.Tekloc.domain.model.DropStep
import com.Zihmaho6.Tekloc.domain.model.Placement
import com.Zihmaho6.Tekloc.domain.model.RoundSummary
import com.Zihmaho6.Tekloc.domain.model.TrajectoryMode
import com.Zihmaho6.Tekloc.domain.repository.ProgressRepository
import com.Zihmaho6.Tekloc.domain.repository.SettingsRepository
import com.Zihmaho6.Tekloc.domain.usecase.AwardStarsUseCase
import com.Zihmaho6.Tekloc.domain.usecase.DailySeedUseCase
import com.Zihmaho6.Tekloc.domain.usecase.GenerateLayoutUseCase
import com.Zihmaho6.Tekloc.domain.usecase.ScoreDropUseCase
import com.Zihmaho6.Tekloc.domain.usecase.SimulateDropUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val generateLayout: GenerateLayoutUseCase,
    private val simulateDrop: SimulateDropUseCase,
    private val scoreDrop: ScoreDropUseCase,
    private val awardStars: AwardStarsUseCase,
    private val dailySeed: DailySeedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val events = Channel<RoundSummary>(Channel.BUFFERED)
    val finished = events.receiveAsFlow()

    private var clockJob: Job? = null
    private var dropJob: Job? = null
    private var preparedKey: String = ""
    private var stepMillis: Long = 180L
    private var stageIndex: Int = 0
    private var attempt: Int = 0
    private var keptLayout: BoardLayout? = null
    private var keptPlacement: Placement = Placement()

    fun prepare(stage: Int, daily: Boolean, round: Int, replan: Boolean) {
        val key = "" + stage + "/" + daily + "/" + round + "/" + replan
        if (preparedKey == key) return
        preparedKey = key
        stageIndex = stage
        attempt = round
        clockJob?.cancel()
        dropJob?.cancel()
        viewModelScope.launch {
            val settings = settingsRepository.load()
            val progress = progressRepository.load()
            stepMillis = settings.dropSpeed.stepMs
            val reuse = keptLayout
            val layout = if (replan && reuse != null) {
                reuse
            } else {
                val boardIndex = (stage / GameConfig.STAGES_PER_BOARD)
                    .coerceIn(0, SampleData.boards.size - 1)
                val def = SampleData.boards[boardIndex]
                withContext(Dispatchers.Default) {
                    if (daily) {
                        generateLayout(def, stage, round, dailySeed.seedFor(dailySeed.epochDay()))
                    } else {
                        generateLayout(def, stage, round, null)
                    }
                }
            }
            keptLayout = layout
            val placement = if (replan) keptPlacement else Placement()
            val clockTotal = if (replan) GameConfig.REPLAN_CLOCK_MS else settings.clockMode.totalMs
            _state.update {
                GameUiState(
                    phase = Phase.Placement,
                    layout = layout,
                    placement = placement,
                    laneIndex = 1,
                    selectedKind = BumperKind.DEFLECT,
                    trajectory = settings.trajectory,
                    clockMillis = clockTotal,
                    clockTotal = clockTotal,
                    boardNumber = layout.boardIndex + 1,
                    stageNumber = stage % GameConfig.STAGES_PER_BOARD + 1,
                    daily = daily,
                    replanUsed = replan,
                    coreTint = tintOf(progress.selectedCoreId)
                )
            }
            recomputePreview()
            startClock()
        }
    }

    fun selectLane(index: Int) {
        if (_state.value.phase != Phase.Placement) return
        _state.update { it.copy(laneIndex = index) }
        recomputePreview()
    }

    fun selectKind(kind: BumperKind) {
        if (_state.value.phase != Phase.Placement) return
        _state.update { it.copy(selectedKind = kind) }
    }

    fun tapCell(x: Int, y: Int) {
        val current = _state.value
        val layout = current.layout ?: return
        if (current.phase != Phase.Placement) return
        val existing = current.placement.at(x, y)
        if (existing != null) {
            val rotated = existing.copy(rotation = (existing.rotation + 90) % 360)
            val bumpers = current.placement.bumpers.map { if (it === existing) rotated else it }
            _state.update { it.copy(placement = Placement(bumpers)) }
            recomputePreview()
            return
        }
        if (!layout.isSlot(x, y)) return
        val kind = current.selectedKind
        val budget = if (kind == BumperKind.DEFLECT) layout.deflectBudget else layout.boostBudget
        if (current.placement.countOf(kind) >= budget) return
        val bumpers = current.placement.bumpers + Bumper(x, y, kind, 0)
        _state.update { it.copy(placement = Placement(bumpers)) }
        recomputePreview()
    }

    fun rotateLast() {
        val current = _state.value
        if (current.phase != Phase.Placement) return
        val bumpers = current.placement.bumpers
        if (bumpers.isEmpty()) return
        val last = bumpers.last()
        val updated = bumpers.dropLast(1) + last.copy(rotation = (last.rotation + 90) % 360)
        _state.update { it.copy(placement = Placement(updated)) }
        recomputePreview()
    }

    fun clearBumpers() {
        if (_state.value.phase != Phase.Placement) return
        _state.update { it.copy(placement = Placement()) }
        recomputePreview()
    }

    fun drop() {
        val current = _state.value
        if (current.phase != Phase.Placement) return
        val layout = current.layout ?: return
        clockJob?.cancel()
        keptPlacement = current.placement
        val outcome = simulateDrop(layout, current.placement, current.laneIndex)
        _state.update {
            it.copy(
                phase = Phase.Dropping,
                previewPath = emptyList(),
                previewZone = -1,
                litGates = emptySet(),
                trail = emptyList(),
                tokenX = simulateDrop.laneColumn(it.laneIndex),
                tokenY = 0,
                barrierStep = 0
            )
        }
        dropJob?.cancel()
        dropJob = viewModelScope.launch {
            val lit = LinkedHashSet<Int>()
            val trail = ArrayList<DropStep>()
            outcome.path.forEach { step ->
                if (step.litGate >= 0) lit.add(step.litGate)
                _state.update {
                    it.copy(
                        tokenX = step.x,
                        tokenY = step.y,
                        trail = trail.toList(),
                        litGates = lit.toSet(),
                        barrierStep = step.y
                    )
                }
                trail.add(0, step)
                while (trail.size > 3) trail.removeAt(trail.size - 1)
                delay(stepMillis)
            }
            val won = outcome.zone == layout.targetZone
            _state.update {
                it.copy(
                    phase = Phase.Settled,
                    won = won,
                    landedZone = outcome.zone,
                    trail = emptyList(),
                    barrierStep = -1
                )
            }
            delay(GameConfig.SETTLE_BANNER_MS)
            finishRound(layout, outcome.zone, won, outcome.gatesLit, outcome.comboMultiplier)
        }
    }

    fun pause() {
        clockJob?.cancel()
        dropJob?.cancel()
        if (_state.value.phase == Phase.Dropping) {
            _state.update { it.copy(phase = Phase.Placement, tokenX = -1, tokenY = -1) }
            recomputePreview()
        }
        _state.update { it.copy(paused = true) }
    }

    fun resume() {
        _state.update { it.copy(paused = false) }
        if (_state.value.phase == Phase.Placement) startClock()
    }

    fun restart() {
        val current = _state.value
        _state.update { it.copy(paused = false) }
        preparedKey = ""
        keptPlacement = Placement()
        keptLayout = null
        prepare(stageIndex, current.daily, attempt + 1, false)
    }

    fun askLeave() {
        clockJob?.cancel()
        _state.update { it.copy(leaveConfirm = true, paused = true) }
    }

    fun dismissLeave() {
        _state.update { it.copy(leaveConfirm = false, paused = false) }
        if (_state.value.phase == Phase.Placement) startClock()
    }

    fun stop() {
        clockJob?.cancel()
        dropJob?.cancel()
    }

    private fun startClock() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (isActive) {
                delay(CLOCK_STEP_MS)
                val current = _state.value
                if (current.phase != Phase.Placement || current.paused) continue
                val left = current.clockMillis - CLOCK_STEP_MS
                if (left <= 0L) {
                    _state.update { it.copy(clockMillis = 0L) }
                    drop()
                    return@launch
                }
                _state.update { it.copy(clockMillis = left) }
            }
        }
    }

    private fun recomputePreview() {
        val current = _state.value
        val layout = current.layout ?: return
        if (current.trajectory == TrajectoryMode.OFF) {
            _state.update { it.copy(previewPath = emptyList(), previewZone = -1) }
            return
        }
        val outcome = simulateDrop(layout, current.placement, current.laneIndex)
        _state.update {
            it.copy(
                previewPath = if (it.trajectory == TrajectoryMode.FULL) outcome.path else emptyList(),
                previewZone = outcome.zone
            )
        }
    }

    private fun finishRound(
        layout: BoardLayout,
        zone: Int,
        won: Boolean,
        gatesLit: Int,
        combo: Float
    ) {
        val current = _state.value
        val used = current.placement.size
        val seconds = current.secondsLeft
        val score = scoreDrop(won, gatesLit, combo, layout.par, used, seconds)
        val stars = awardStars(won, gatesLit, layout.gateCount, used, layout.par, current.replanUsed)
        val summary = RoundSummary(
            boardIndex = layout.boardIndex,
            stageIndex = stageIndex,
            attempt = attempt,
            daily = current.daily,
            won = won,
            landedZone = zone,
            targetZone = layout.targetZone,
            gatesLit = gatesLit,
            gateTotal = layout.gateCount,
            comboMultiplier = combo,
            bumpersUsed = used,
            par = layout.par,
            secondsLeft = seconds,
            replanUsed = current.replanUsed,
            score = score,
            starTarget = stars.target,
            starGates = stars.allGates,
            starPar = stars.underPar
        )
        viewModelScope.launch {
            if (current.daily) {
                progressRepository.recordDaily(dailySeed.epochDay(), stars.count, score)
            } else {
                progressRepository.recordStage(stageIndex, stars.count, score, won)
            }
            preparedKey = ""
            events.send(summary)
        }
    }

    private fun tintOf(coreId: Int): Long =
        SampleData.cores.firstOrNull { it.id == coreId }?.tint ?: SampleData.cores[0].tint

    override fun onCleared() {
        clockJob?.cancel()
        dropJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val CLOCK_STEP_MS = 200L
    }
}
