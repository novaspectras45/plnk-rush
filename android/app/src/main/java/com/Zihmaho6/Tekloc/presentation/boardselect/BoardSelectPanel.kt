package com.Zihmaho6.Tekloc.presentation.boardselect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.MonoSmall
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.SurfaceRaised
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.HudFrame
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.SectionHeader

@Composable
fun BoardSelectPanel(
    onOpenStage: (Int) -> Unit,
    onBack: () -> Unit
) {
    val boardViewModel: BoardSelectViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val state by boardViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        boardViewModel.refresh()
    }

    ScreenBackdrop(
        overlay = listOf(
            BackgroundDeep.copy(alpha = 0.90f),
            BackgroundDeep.copy(alpha = 0.95f),
            BackgroundDeep.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.nav_board_select),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
                starTotal = state.totalStars,
                starDescription = stringResource(R.string.cd_star)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.rows, key = { it.index }) { row ->
                    BoardCard(row = row, onOpenStage = onOpenStage)
                }
            }
        }
    }
}

@Composable
private fun BoardCard(row: BoardRow, onOpenStage: (Int) -> Unit) {
    HudFrame(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (row.unlocked) AccentCyan.copy(alpha = 0.3f) else BorderIdle,
        bracketColor = if (row.unlocked) AccentCyan else BorderIdle
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = boardName(row.index),
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (row.unlocked) TextPrimary else TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = boardDescriptor(row.index),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
            }
            if (row.unlocked) {
                Text(
                    text = if (row.earnedStars > 0) {
                        stringResource(R.string.board_star_count, row.earnedStars, row.maxStars)
                    } else {
                        stringResource(R.string.board_no_runs)
                    },
                    style = MonoSmall,
                    color = AccentAmber,
                    textAlign = TextAlign.End
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.cd_locked),
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.board_locked, row.starsToUnlock),
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            row.stageStars.forEachIndexed { offset, stars ->
                StageChip(
                    number = offset + 1,
                    stars = stars,
                    enabled = row.unlocked,
                    onClick = {
                        onOpenStage(row.index * GameConfig.STAGES_PER_BOARD + offset)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StageChip(
    number: Int,
    stars: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val base = modifier
        .height(52.dp)
        .clip(shape)
        .background(if (enabled) SurfaceNight.copy(alpha = 0.86f) else SurfaceRaised.copy(alpha = 0.4f))
        .border(1.dp, if (stars > 0) AccentAmber.copy(alpha = 0.5f) else BorderIdle, shape)
    Box(
        modifier = if (enabled) base.clickable(onClick = onClick) else base,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.stage_chip, number),
                style = MonoSmall,
                color = if (enabled) TextPrimary else TextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                for (index in 0 until GameConfig.STARS_PER_STAGE) {
                    Icon(
                        painter = painterResource(
                            if (index < stars) R.drawable.ic_star else R.drawable.ic_star_outline
                        ),
                        contentDescription = null,
                        tint = if (index < stars) AccentAmber else BorderIdle,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun boardName(index: Int): String = when (index) {
    0 -> stringResource(R.string.board_one)
    1 -> stringResource(R.string.board_two)
    2 -> stringResource(R.string.board_three)
    else -> stringResource(R.string.board_four)
}

@Composable
private fun boardDescriptor(index: Int): String = when (index) {
    0 -> stringResource(R.string.board_one_desc)
    1 -> stringResource(R.string.board_two_desc)
    2 -> stringResource(R.string.board_three_desc)
    else -> stringResource(R.string.board_four_desc)
}
