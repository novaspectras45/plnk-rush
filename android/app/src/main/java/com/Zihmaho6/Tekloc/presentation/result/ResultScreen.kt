package com.Zihmaho6.Tekloc.presentation.result

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.core.di.ServiceLocator
import com.Zihmaho6.Tekloc.core.theme.AccentAmber
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.AccentLime
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.MonoLarge
import com.Zihmaho6.Tekloc.core.theme.SurfaceWell
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.HudFrame
import com.Zihmaho6.Tekloc.core.ui.PrimaryButton
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.SecondaryButton
import com.Zihmaho6.Tekloc.core.ui.StarRow
import com.Zihmaho6.Tekloc.core.ui.StatCard
import com.Zihmaho6.Tekloc.core.ui.formatCount
import com.Zihmaho6.Tekloc.domain.model.RoundSummary

@Composable
fun ResultScreen(
    summary: RoundSummary,
    onAgain: (Int) -> Unit,
    onReplan: () -> Unit,
    onBoardSelect: () -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: ResultViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val state by resultViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(summary) {
        resultViewModel.load(summary.stageIndex, summary.won)
    }

    ResultContent(
        summary = summary,
        state = state,
        onAgain = { onAgain(state.nextStage) },
        onReplan = onReplan,
        onBoardSelect = onBoardSelect,
        onMenu = onMenu
    )
}

@Composable
private fun ResultContent(
    summary: RoundSummary,
    state: ResultUiState,
    onAgain: () -> Unit,
    onReplan: () -> Unit,
    onBoardSelect: () -> Unit,
    onMenu: () -> Unit
) {
    val score by animateIntAsState(
        targetValue = summary.score,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "scoreCount"
    )
    val accent = if (summary.won) AccentCyan else AccentMagenta
    val showStats = state.totalStars > 0 && state.stageBest > 0
    val showReplan = !summary.won && !summary.replanUsed && !summary.daily

    ScreenBackdrop(
        overlay = listOf(
            SurfaceWell.copy(alpha = 0.90f),
            BackgroundDeep.copy(alpha = 0.95f),
            BackgroundDeep.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (summary.won) {
                    stringResource(R.string.banner_locked)
                } else {
                    stringResource(R.string.banner_miss)
                },
                fontSize = 34.sp,
                lineHeight = 40.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(
                    R.string.result_subtitle,
                    summary.landedZone + 1,
                    GameConfig.ZONES,
                    summary.gatesLit
                ),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 1.5.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))
            StarRow(
                earned = summary.stars,
                total = GameConfig.STARS_PER_STAGE,
                starSize = 36.dp,
                staggered = true,
                captions = listOf(
                    stringResource(R.string.star_target),
                    stringResource(R.string.star_all_gates),
                    stringResource(R.string.star_under_par)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))
            HudFrame(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.result_score),
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            letterSpacing = 2.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCount(score),
                            style = MonoLarge,
                            color = TextPrimary,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        BreakdownLine(
                            text = stringResource(R.string.result_gates, summary.gatesLit),
                            color = AccentLime
                        )
                        BreakdownLine(
                            text = stringResource(
                                R.string.result_combo,
                                comboLabel(summary.comboMultiplier)
                            ),
                            color = AccentMagenta
                        )
                        BreakdownLine(
                            text = stringResource(
                                R.string.result_bumpers,
                                summary.bumpersUsed,
                                summary.par
                            ),
                            color = AccentCyan
                        )
                        BreakdownLine(
                            text = stringResource(R.string.result_clock, summary.secondsLeft),
                            color = AccentAmber
                        )
                    }
                }
            }

            if (showStats) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_stars),
                        value = state.totalStars,
                        accent = AccentAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_best),
                        value = state.stageBest,
                        accent = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_streak),
                        value = state.streak,
                        accent = AccentMagenta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.cta_drop_again),
                onClick = onAgain,
                height = 60.dp
            )
            if (showReplan) {
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.cta_replan),
                    onClick = onReplan
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.nav_board_select),
                    onClick = onBoardSelect,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_menu),
                    onClick = onMenu,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BreakdownLine(text: String, color: Color) {
    Box(modifier = Modifier.height(18.dp), contentAlignment = Alignment.CenterEnd) {
        Text(
            text = text,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 1.sp,
            color = color,
            textAlign = TextAlign.End
        )
    }
}

private fun comboLabel(multiplier: Float): String {
    val scaled = (multiplier * 10f).toInt()
    val whole = scaled / 10
    val fraction = scaled % 10
    return "" + whole + "." + fraction
}
