package com.Zihmaho6.Tekloc.presentation.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.core.di.ServiceLocator
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.AccentLime
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.MonoSmall
import com.Zihmaho6.Tekloc.core.theme.ScrimDeep
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.SurfaceRaised
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.BOARD_FRAME
import com.Zihmaho6.Tekloc.core.ui.BoardCanvas
import com.Zihmaho6.Tekloc.core.ui.ChipRow
import com.Zihmaho6.Tekloc.core.ui.HudFrame
import com.Zihmaho6.Tekloc.core.ui.PrimaryButton
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.domain.model.BumperKind
import com.Zihmaho6.Tekloc.domain.model.RoundSummary
import com.Zihmaho6.Tekloc.presentation.dialog.ConfirmDialog
import com.Zihmaho6.Tekloc.presentation.dialog.PauseDialog
import kotlin.math.floor
import kotlin.math.min

@Composable
fun GameScreen(
    stageIndex: Int,
    daily: Boolean,
    attempt: Int,
    replan: Boolean,
    onRoundFinished: (RoundSummary) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(stageIndex, daily, attempt, replan) {
        gameViewModel.prepare(stageIndex, daily, attempt, replan)
    }

    LaunchedEffect(Unit) {
        gameViewModel.finished.collect { summary -> onRoundFinished(summary) }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.stop() }
    }

    GameContent(
        state = state,
        onLane = { gameViewModel.selectLane(it) },
        onKind = { gameViewModel.selectKind(it) },
        onCell = { x, y -> gameViewModel.tapCell(x, y) },
        onRotate = { gameViewModel.rotateLast() },
        onClear = { gameViewModel.clearBumpers() },
        onDrop = { gameViewModel.drop() },
        onPause = { gameViewModel.pause() },
        onResume = { gameViewModel.resume() },
        onRestart = { gameViewModel.restart() },
        onAskLeave = { gameViewModel.askLeave() },
        onDismissLeave = { gameViewModel.dismissLeave() },
        onMenu = onMenu
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onLane: (Int) -> Unit,
    onKind: (BumperKind) -> Unit,
    onCell: (Int, Int) -> Unit,
    onRotate: () -> Unit,
    onClear: () -> Unit,
    onDrop: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onAskLeave: () -> Unit,
    onDismissLeave: () -> Unit,
    onMenu: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "gameLoops")
    val ghost by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ghostPulse"
    )
    val barAlpha by animateFloatAsState(
        targetValue = if (state.phase == Phase.Placement) 1f else 0.45f,
        animationSpec = tween(220),
        label = "barAlpha"
    )
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val layout = state.layout

    ScreenBackdropGame {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HudIconButton(
                    resource = R.drawable.icon_home,
                    description = stringResource(R.string.cd_menu),
                    onClick = onAskLeave
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            R.string.hud_board_stage,
                            state.boardNumber,
                            state.stageNumber
                        ),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 2.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.hud_par, layout?.par ?: 0),
                        style = MonoSmall,
                        fontSize = 14.sp,
                        color = AccentCyan
                    )
                }
                HudIconButton(
                    resource = R.drawable.icon_pause,
                    description = stringResource(R.string.cd_pause),
                    onClick = onPause
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { state.clockFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                    color = if (state.clockFraction < 0.25f) AccentMagenta else AccentCyan,
                    trackColor = SurfaceRaised
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.hud_seconds, state.secondsLeft),
                    style = MonoSmall,
                    color = TextPrimary,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 230.dp + navBottom),
                contentAlignment = Alignment.Center
            ) {
                if (layout != null) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val cellDp = resolveCell(
                            availableWidth = maxWidth.value,
                            availableHeight = maxHeight.value,
                            cols = layout.cols,
                            rows = layout.rows
                        )
                        Box(contentAlignment = Alignment.Center) {
                            BoardCanvas(
                                layout = layout,
                                placement = state.placement,
                                cell = cellDp.dp,
                                laneIndex = state.laneIndex,
                                previewPath = state.previewPath,
                                previewZone = state.previewZone,
                                tokenX = state.tokenX,
                                tokenY = state.tokenY,
                                trail = state.trail,
                                litGates = state.litGates,
                                barrierStep = state.barrierStep,
                                tokenTint = Color(state.coreTint),
                                ghostAlpha = ghost,
                                showGhostSlots = state.phase == Phase.Placement,
                                landedZone = state.landedZone,
                                interactive = state.phase == Phase.Placement,
                                onSlotTap = onCell
                            )
                            if (state.phase == Phase.Settled) {
                                SettledBanner(won = state.won)
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp + navBottom),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.game_hint),
                fontSize = 10.sp,
                lineHeight = 13.sp,
                letterSpacing = 1.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .height(20.dp)
                    .alpha(if (state.phase == Phase.Placement) 1f else 0f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            HudFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(barAlpha),
                containerColor = SurfaceNight.copy(alpha = 0.90f),
                cornerRadius = 22.dp,
                contentPadding = PaddingValues(12.dp)
            ) {
                ChipRow(
                    options = listOf(
                        stringResource(R.string.lane_one),
                        stringResource(R.string.lane_two),
                        stringResource(R.string.lane_three)
                    ),
                    selectedIndex = state.laneIndex,
                    onSelect = onLane,
                    accent = AccentMagenta,
                    chipHeight = 44.dp,
                    selectedStateLabel = stringResource(R.string.state_selected)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrayButton(
                        label = stringResource(R.string.tray_deflect),
                        count = state.deflectLeft,
                        accent = AccentCyan,
                        selected = state.selectedKind == BumperKind.DEFLECT,
                        onClick = { onKind(BumperKind.DEFLECT) },
                        modifier = Modifier.weight(1f)
                    )
                    TrayButton(
                        label = stringResource(R.string.tray_boost),
                        count = state.boostLeft,
                        accent = AccentLime,
                        selected = state.selectedKind == BumperKind.BOOST,
                        onClick = { onKind(BumperKind.BOOST) },
                        modifier = Modifier.weight(1f)
                    )
                    ToolButton(
                        resource = R.drawable.ic_rotate,
                        description = stringResource(R.string.cd_rotate),
                        enabled = state.placement.size > 0 && state.phase == Phase.Placement,
                        onClick = onRotate
                    )
                    ToolButton(
                        resource = R.drawable.ic_clear,
                        description = stringResource(R.string.cd_clear),
                        enabled = state.placement.size > 0 && state.phase == Phase.Placement,
                        onClick = onClear
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                PrimaryButton(
                    text = if (state.phase == Phase.Dropping) {
                        stringResource(R.string.cta_dropping)
                    } else {
                        stringResource(R.string.cta_drop)
                    },
                    onClick = onDrop,
                    enabled = state.phase == Phase.Placement,
                    height = 60.dp
                )
            }
        }

        if (state.paused && !state.leaveConfirm) {
            PauseDialog(
                onResume = onResume,
                onRestart = onRestart,
                onMenu = onMenu
            )
        }
        if (state.leaveConfirm) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_leave_title),
                body = stringResource(R.string.dialog_leave_body),
                confirmText = stringResource(R.string.cta_leave),
                dismissText = stringResource(R.string.cta_stay),
                onConfirm = onMenu,
                onDismiss = onDismissLeave
            )
        }
    }
}

@Composable
private fun ScreenBackdropGame(content: @Composable BoxScope.() -> Unit) {
    ScreenBackdrop(
        overlay = listOf(
            BackgroundDeep.copy(alpha = 0.88f),
            Color(0xFF0D0D1A).copy(alpha = 0.94f),
            BackgroundDeep.copy(alpha = 0.98f)
        ),
        content = content
    )
}

@Composable
private fun HudIconButton(resource: Int, description: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceNight.copy(alpha = 0.8f))
            .border(1.dp, BorderIdle, RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun ToolButton(
    resource: Int,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceNight.copy(alpha = if (enabled) 0.8f else 0.38f))
            .border(1.dp, BorderIdle, RoundedCornerShape(12.dp))
    ) {
        Icon(
            painter = painterResource(resource),
            contentDescription = description,
            tint = if (enabled) AccentCyan else TextMuted,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun TrayButton(
    label: String,
    count: Int,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = count > 0
    val shape = RoundedCornerShape(12.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 8.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected && enabled) accent else BorderIdle
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) accent.copy(alpha = 0.14f) else SurfaceNight.copy(alpha = 0.8f),
            contentColor = TextPrimary,
            disabledContainerColor = SurfaceNight.copy(alpha = 0.38f),
            disabledContentColor = TextMuted
        ),
        modifier = modifier
            .height(48.dp)
            .semantics { stateDescription = label }
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(
                if (enabled) accent else TextMuted,
                BlendMode.SrcAtop
            ),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = count.toString(),
            style = MonoSmall,
            color = if (enabled) accent else TextMuted
        )
    }
}

@Composable
private fun SettledBanner(won: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(ScrimDeep)
            .border(
                1.dp,
                if (won) AccentCyan else AccentMagenta,
                RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (won) {
                stringResource(R.string.banner_locked)
            } else {
                stringResource(R.string.banner_miss)
            },
            fontSize = 26.sp,
            lineHeight = 30.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (won) AccentCyan else AccentMagenta,
            textAlign = TextAlign.Center
        )
    }
}

private fun resolveCell(
    availableWidth: Float,
    availableHeight: Float,
    cols: Int,
    rows: Int
): Float {
    val frame = BOARD_FRAME.value * 2f
    val widthBudget = min(availableWidth - 16f, 360f)
    val fromWidth = (widthBudget - frame) / cols
    val fromHeight = (availableHeight - frame) / (rows + GameConfig.ZONES * 0.26f)
    val chosen = min(min(fromWidth, fromHeight), 38f)
    return floor(chosen).coerceAtLeast(18f)
}
